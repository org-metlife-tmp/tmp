package com.qhjf.cfm.web.controller;


import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.render.ByteArrayRender;
import com.qhjf.cfm.web.service.VoucherQueryService;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2019/5/31
 * @Description: 凭证查询
 */
public class VoucherQueryController extends CFMBaseController {


    private final static Log logger = LogbackLog.getLog(VoucherQueryController.class);
    private VoucherQueryService service = new VoucherQueryService();

    /**
     * 凭证查询列表
     */
    @Auth(hasForces = {"VoucherQuery"})
    public void list() {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        try {
            UodpInfo uodpInfo = getCurUodp();
            record.set("org_id", uodpInfo.getOrg_id());

            Page<Record> page = service.page(pageNum, pageSize, record);
            renderOkPage(page);
        } catch (BusinessException e) {
            logger.error("sunVoucherData list fail!");
            renderFail(e);
        }

    }

    public void listexport() {
        doExport();
    }

    /**
     * 凭证查询导出xml文件
     */
    @Auth(hasForces = {"VoucherQuery"})
    public void export() {
        Record record = getParamsToRecord();
        String xmlName = "SST_SALL_%s_%s";
        String seq = "exprotvoucherfileseqno:%s";
        String type = "SSAL";
        String suffixXml = ".xml";
        String fileName = "";

        List<Record> list = null;
        try {
            UodpInfo uodpInfo = getCurUodp();
            record.set("org_id", uodpInfo.getOrg_id());
            list = service.list(record);
            //获取当前日期
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
            String nowDate = format.format(new Date());
            //生成序列号
            final String serialNumber = RedisSericalnoGenTool.genVoucherFileSeqNo(seq);
            fileName = String.format(xmlName, nowDate, serialNumber) + suffixXml;
            String fileContent = createXML(list, fileName, type);
            byte[] byteArray = fileContent.getBytes();

            render(new ByteArrayRender(fileName, byteArray));
        } catch (BusinessException e) {
            renderFail(e);
        }
    }

    /**
     * @param list
     * @param fileName
     * @return 生成xml
     */
    private String createXML(List<Record> list, String fileName, String type) {
        StringBuilder builder = new StringBuilder();
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
        builder.append("\t\t\t<JournalType>").append(type).append("</JournalType>\n");
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

}
