package com.qhjf.cfm.web.quartzs.jobs;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.config.DDHVoucherConfigSection;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/11/7
 * @Description: 自动生成凭证任务
 */
public class CheckVoucherJob implements Job {

    private static DDHVoucherConfigSection ddhVoucherConfigSection = DDHVoucherConfigSection.getInstance();

    private static Logger log = LoggerFactory.getLogger(CheckVoucherJob.class);
    private static StringBuilder builder = null;
    private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
    private static String FILE_ANME_XML = "SST_SSAL_%s_%s";
    private static String FILE_ANME_TXT = "SST_SSAL_%s_%s_%s";
    private static String FILE_PATH = ddhVoucherConfigSection.getPath();
    private static final String suffixXml = ".xml";
    private static final String suffixTxt = ".txt";
    private static Map<String, Record> map = null;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        log.debug("【自动生成凭证任务---begin】");

        checkVoucher("auto_check_cfm.sun_voucher_data_list");

        log.debug("【自动生成凭证任务---end】");
    }

    private void checkVoucher(String sql) {
        //查询凭证表(sun_voucher_data) 未生成凭证的数据(file_name 为 NULL)
        SqlPara sqlPara = Db.getSqlPara(sql);
        final List<Record> voucherList = Db.find(sqlPara);

        if (voucherList != null && voucherList.size() > 0) {
            //获取当前日期
            String nowDate = format.format(new Date());
            //生成序列号
            final String seq = RedisSericalnoGenTool.genVoucherFileSeqNo();
            final String fileNameXML = String.format(FILE_ANME_XML, nowDate, seq);
            final String fileNameTXT = String.format(FILE_ANME_TXT, nowDate, seq, "CONTROL");

            //生成xml文件
            String xml = createXML(voucherList, fileNameXML);
            //生成txt文件
            String txt = createControl(voucherList, fileNameXML);

            //存储文件

            Path filePathTempXml = Paths.get(FILE_PATH, fileNameXML + suffixXml);
            Path filePathTempTxt = Paths.get(FILE_PATH, fileNameTXT + suffixTxt);

            if (createFile(xml, filePathTempXml) && createFile(txt, filePathTempTxt)) {
                log.info("文件创建成功，path:" + FILE_PATH);
                log.info("xml文件,path:" + filePathTempXml);
                log.info("txt文件,path:" + filePathTempTxt);

                //修改凭证信息filename以及export_count +1
                boolean flag = Db.tx(new IAtom() {
                    @Override
                    public boolean run() throws SQLException {
                        List<Record> updateList = new ArrayList<>();

                        for (Record record : voucherList) {
                            int expCount = TypeUtils.castToInt(record.get("export_count"));

                            Record update = new Record();
                            update.set("file_name", fileNameXML + suffixXml)
                                    .set("export_count", (expCount + 1))
                                    .set("id", TypeUtils.castToLong(record.get("id")));

                            updateList.add(update);
                        }

                        int[] chgRes = Db.batchUpdate("sun_voucher_data", "id", updateList, 1000);
                        return ArrayUtil.checkDbResult(chgRes);
                    }
                });

                if (!flag) {
                    log.info(format.format(new Date()) + "生成凭证失败!");
                }
            }

        }

    }

    /**
     * @param list
     * @param fileName
     * @return 生成xml
     */
    private String createXML(List<Record> list, String fileName) {
        builder = new StringBuilder();
        builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        builder.append("<SSC>\n");
        builder.append("\t<ErrorContext>\n");
        builder.append("\t\t<CompatibilityMode>").append(0).append("</CompatibilityMode>\n");
        builder.append("\t\t<ErrorOutput>").append(1).append("</ErrorOutput>\n");
        builder.append("\t\t<ErrorThreshold>").append(1).append("</ErrorThreshold>\n");
        builder.append("\t</ErrorContext>\n");

        builder.append("\t<SunSystemsContext>\n");
        builder.append("\t\t<BusinessUnit>").append("NCA").append("</BusinessUnit>\n");
        builder.append("\t\t<BudgetCode>").append("A").append("</BudgetCode>\n");
        builder.append("\t</SunSystemsContext>\n");
        builder.append("\t<MethodContext>\n");
        builder.append("\t\t<LedgerPostingParameters>\n");
        builder.append("\t\t\t<AllowBalTran>").append("N").append("</AllowBalTran>\n");
        builder.append("\t\t\t<AllowOverBudget>").append("N").append("</AllowOverBudget>\n");
        builder.append("\t\t\t<AllowPostToSuspended>").append("N").append("</AllowPostToSuspended>\n");
        builder.append("\t\t\t<BalancingOptions>").append("T9").append("</BalancingOptions>\n");
        builder.append("\t\t\t<Description>").append(fileName).append("</Description>\n");
        builder.append("\t\t\t<JournalType>").append("SSAL").append("</JournalType>\n");
        builder.append("\t\t\t<LayoutCode />\n");
        builder.append("\t\t\t<LoadOnly>").append("N").append("</LoadOnly>\n");
        builder.append("\t\t\t<PostProvisional>").append("N").append("</PostProvisional>\n");
        builder.append("\t\t\t<PostToHold>").append("N").append("</PostToHold>\n");
        builder.append("\t\t\t<PostingType>").append(2).append("</PostingType>\n");
        builder.append("\t\t\t<Print>").append("N").append("</Print>\n");
        builder.append("\t\t\t<ReportErrorsOnly>").append("Y").append("</ReportErrorsOnly>\n");
        builder.append("\t\t\t<ReportingAccount>").append("9999999999").append("</ReportingAccount>\n");
        builder.append("\t\t\t<SuppressSubstitutedMessages>").append("Y").append("</SuppressSubstitutedMessages>\n");
        builder.append("\t\t\t<SuspenseAccount>").append("9999999999").append("</SuspenseAccount>\n");
        builder.append("\t\t\t<TransactionAmountAccount>").append("9999999999").append("</TransactionAmountAccount>\n");
        builder.append("\t\t</LedgerPostingParameters>\n");
        builder.append("\t</MethodContext>\n");

        builder.append("\t<Payload>\n");
        builder.append("\t\t<Ledger>\n");
        for (Record record : list) {
            builder.append("\t\t\t<Line>\n");
            builder.append("\t\t\t\t<AccountCode>").append(TypeUtils.castToString(record.get("account_code"))).append("</AccountCode>\n");
            builder.append("\t\t\t\t<AccountingPeriod>").append(TypeUtils.castToString(record.get("account_period"))).append("</AccountingPeriod>\n");
            builder.append("\t\t\t\t<AnalysisCode1>").append(TypeUtils.castToString(record.get("a_code1"))).append("</AnalysisCode1>\n");
            builder.append("\t\t\t\t<AnalysisCode2>").append(TypeUtils.castToString(record.get("a_code2"))).append("</AnalysisCode2>\n");
            builder.append("\t\t\t\t<AnalysisCode3>").append(TypeUtils.castToString(record.get("a_code3"))).append("</AnalysisCode3>\n");
            builder.append("\t\t\t\t<AnalysisCode5>").append(TypeUtils.castToString(record.get("a_code5"))).append("</AnalysisCode5>\n");
            builder.append("\t\t\t\t<AnalysisCode6>").append(TypeUtils.castToString(record.get("a_code6"))).append("</AnalysisCode6>\n");
            builder.append("\t\t\t\t<AnalysisCode7>").append(TypeUtils.castToString(record.get("a_code7"))).append("</AnalysisCode7>\n");
            builder.append("\t\t\t\t<AnalysisCode10>").append(TypeUtils.castToString(record.get("a_code10"))).append("</AnalysisCode10>\n");
            builder.append("\t\t\t\t<BaseAmount>").append(TypeUtils.castToBigDecimal(record.get("base_amount"))).append("</BaseAmount>\n");
            builder.append("\t\t\t\t<CurrencyCode>").append(TypeUtils.castToString(record.get("currency_code"))).append("</CurrencyCode>\n");
            builder.append("\t\t\t\t<DebitCredit>").append(TypeUtils.castToString(record.get("debit_credit"))).append("</DebitCredit>\n");
            builder.append("\t\t\t\t<Description>").append(TypeUtils.castToString(record.get("description"))).append("</Description>\n");
            builder.append("\t\t\t\t<JournalSource>").append(TypeUtils.castToString(record.get("journal_source"))).append("</JournalSource>\n");
            builder.append("\t\t\t\t<TransactionAmount>").append(TypeUtils.castToBigDecimal(record.get("transaction_amount"))).append("</TransactionAmount>\n");
            builder.append("\t\t\t\t<TransactionDate>").append(TypeUtils.castToString(record.get("transaction_date"))).append("</TransactionDate>\n");
            builder.append("\t\t\t\t<TransactionReference>").append(TypeUtils.castToString(record.get("transaction_reference"))).append("</TransactionReference>\n");
            builder.append("\t\t\t</Line>\n");
        }
        builder.append("\t\t</Ledger>\n");
        builder.append("\t</Payload>\n");

        builder.append("</SSC>\n");
        return builder.toString();
    }

    /**
     * @param list
     * @param fileName
     * @return 生成Control
     */
    private String createControl(List<Record> list, String fileName) {
        builder = new StringBuilder();

        //统计
        totalData(list);

        Record totalRec = map.get("total");
        //移除统计总数，只保留机构统计信息
        map.remove("total");

        builder.append("Control,")//校验字段
                .append(fileName).append(suffixXml).append(",")//接口XML文件名称
                .append(0).append(",")//校验字段值(比如涉及三个渠道则应该生成三行, AGY, BXS, TM…)
                .append(TypeUtils.castToInt(totalRec.get("totalDebitsCount"))).append(",")//借方行数
                .append(TypeUtils.castToInt(totalRec.get("totalCreditsCount"))).append(",")//贷方行数
                .append(TypeUtils.castToInt(totalRec.get("totalCount"))).append(",")//总行数
                .append(TypeUtils.castToBigDecimal(totalRec.get("totalDebitsAmount"))).append(",")//借方本位币/人民币合计
                .append(TypeUtils.castToBigDecimal(totalRec.get("totalCreditsAmount")))//贷方本位币/人民币合计
                .append("\n");


        builder.append("Channel,")//校验字段
                .append(fileName).append(suffixXml).append(",")//接口XML文件名称
                .append("CO").append(",")//校验字段值(比如涉及三个渠道则应该生成三行, AGY, BXS, TM…)
                .append(TypeUtils.castToInt(totalRec.get("totalDebitsCount"))).append(",")//借方行数
                .append(TypeUtils.castToInt(totalRec.get("totalCreditsCount"))).append(",")//贷方行数
                .append(TypeUtils.castToInt(totalRec.get("totalCount"))).append(",")//总行数
                .append(TypeUtils.castToBigDecimal(totalRec.get("totalDebitsAmount"))).append(",")//借方本位币/人民币合计
                .append(TypeUtils.castToBigDecimal(totalRec.get("totalCreditsAmount")))//贷方本位币/人民币合计
                .append("\n");

        builder.append("Fund,")//校验字段
                .append(fileName).append(suffixXml).append(",")//接口XML文件名称
                .append("G").append(",")//校验字段值(比如涉及三个渠道则应该生成三行, AGY, BXS, TM…)
                .append(TypeUtils.castToInt(totalRec.get("totalDebitsCount"))).append(",")//借方行数
                .append(TypeUtils.castToInt(totalRec.get("totalCreditsCount"))).append(",")//贷方行数
                .append(TypeUtils.castToInt(totalRec.get("totalCount"))).append(",")//总行数
                .append(TypeUtils.castToBigDecimal(totalRec.get("totalDebitsAmount"))).append(",")//借方本位币/人民币合计
                .append(TypeUtils.castToBigDecimal(totalRec.get("totalCreditsAmount")))//贷方本位币/人民币合计
                .append("\n");

        for (Map.Entry<String, Record> entry : map.entrySet()) {
            Record brRec = entry.getValue();

            builder.append("Branch,")//校验字段
                    .append(fileName).append(suffixXml).append(",")//接口XML文件名称
                    .append(entry.getKey()).append(",")//校验字段值(比如涉及三个渠道则应该生成三行, AGY, BXS, TM…)
                    .append(TypeUtils.castToInt(brRec.get("totalDebitsCount"))).append(",")//借方行数
                    .append(TypeUtils.castToInt(brRec.get("totalCreditsCount"))).append(",")//贷方行数
                    .append(TypeUtils.castToInt(brRec.get("totalCount"))).append(",")//总行数
                    .append(TypeUtils.castToBigDecimal(brRec.get("totalDebitsAmount"))).append(",")//借方本位币/人民币合计
                    .append(TypeUtils.castToBigDecimal(brRec.get("totalCreditsAmount")))//贷方本位币/人民币合计
                    .append("\n");
        }

        return builder.toString();
    }

    /**
     * @param list
     * @return 数据统计
     */
    private Map<String, Record> totalData(List<Record> list) {
        map = new HashMap<>();

        for (Record rec : list) {
            String key = TypeUtils.castToString(rec.get("a_code10"));//获取机构
            BigDecimal amount = TypeUtils.castToBigDecimal(rec.get("base_amount"));
            String debitCredit = TypeUtils.castToString(rec.get("debit_credit"));

            Record brRec = map.get(key);
            if (brRec == null) {
                brRec = initRecord();
            }
            //统计机构
            brRec.set("debit_credit", debitCredit);
            brRec = addRecord(brRec, amount);
            map.put(key, brRec);

            //总数统计 每次循环都统计
            Record totalRec = map.get("total");
            if (totalRec == null) {
                totalRec = initRecord();
            }
            totalRec.set("debit_credit", debitCredit);
            totalRec = addRecord(totalRec, amount);
            map.put("total", totalRec);
        }

        return map;
    }

    /**
     * @return 初始化统计Record
     */
    private Record initRecord() {
        return new Record()
                .set("totalCount", 0)
                .set("totalDebitsCount", 0)
                .set("totalCreditsCount", 0)
                .set("totalDebitsAmount", new BigDecimal(0))
                .set("totalCreditsAmount", new BigDecimal(0));
    }

    /**
     * @param record
     * @return Record统计处理
     */
    private Record addRecord(Record record, BigDecimal amount) {

        int totalCount = TypeUtils.castToInt(record.get("totalCount"));
        int totalDebitsCount = TypeUtils.castToInt(record.get("totalDebitsCount"));
        BigDecimal totalDebitsAmount = TypeUtils.castToBigDecimal(record.get("totalDebitsAmount"));

        int totalCreditsCount = TypeUtils.castToInt(record.get("totalCreditsCount"));
        BigDecimal totalCreditsAmount = TypeUtils.castToBigDecimal(record.get("totalCreditsAmount"));

        totalCount += 1;

        //判断借贷
        if ("C".equals(TypeUtils.castToString(record.get("debit_credit")))) {//贷
            //贷统计
            totalDebitsCount += 1;
            totalDebitsAmount = totalDebitsAmount.add(amount);
        } else {
            //借统计
            totalCreditsCount += 1;
            totalCreditsAmount = totalCreditsAmount.add(amount);
        }

        return new Record()
                .set("totalCount", totalCount)
                .set("totalDebitsCount", totalDebitsCount)
                .set("totalCreditsCount", totalCreditsCount)
                .set("totalDebitsAmount", totalDebitsAmount)
                .set("totalCreditsAmount", totalCreditsAmount);

    }

    /**
     * @param fileContent
     * @param path
     * @return 创建文件
     */
    private boolean createFile(String fileContent, Path path) {
        boolean flag = false;
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(Files.newOutputStream(path));
            pw.write(fileContent);
            flag = true;
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
        return flag;
    }

}
