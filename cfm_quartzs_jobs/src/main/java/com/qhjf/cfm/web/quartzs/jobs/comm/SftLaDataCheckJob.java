package com.qhjf.cfm.web.quartzs.jobs.comm;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.exceptions.ReqValidateException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.DateFormatThreadLocal;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.web.config.GmfConfigAccnoSection;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.quartzs.jobs.pub.PubJob;
import com.qhjf.cfm.web.quartzs.jobs.utils.DDHSafeUtil;
import com.qhjf.cfm.web.quartzs.jobs.utils.ValidateUtil;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;

import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SftLaDataCheckJob implements Job{

    private static Logger log = LoggerFactory.getLogger(PubJob.class);
    private ExecutorService executeService = Executors.newFixedThreadPool(100);
    private List<Future<String>> resultList = new ArrayList<Future<String>>();

    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.debug("批量付LA数据校验任务开始");
        List<Record> list = Db.find(Db.getSql("la_cfm.getLAUnCheckedOriginList"), WebConstant.YesOrNo.NO.getKey());
        if (list == null || list.size() == 0) {
            return;
        }
        if (list != null && list.size() > 0) {
            log.debug(list.size() + "");
            for (Record record : list) {
                executeProcess(record);
            }
        }
        log.debug("批量付LA数据校验任务结束");


    }

    public void executeProcess(final Record laOiriginData){
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean returnFlag = false;
                try {
                    validate(laOiriginData);
                } catch (ReqValidateException e) {
                    e.printStackTrace();
                    int flag = Db.update(Db.getSql("la_cfm.updLaOriginStatus"),
                            WebConstant.YesOrNo.YES.getKey(),
                            WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_F.getKey(),
                            e.getMessage(),
                            laOiriginData.getLong("id"),
                            laOiriginData.getInt("persist_version"));
                    if(flag == 1){
                        SftCallBack callback = new SftCallBack();
                        callback.callback(WebConstant.SftOsSource.LA.getKey(), laOiriginData);
                        return true;
                    }else{
                        return false;
                    }
                } catch (EncryAndDecryException e) {
                    e.printStackTrace();
                    return false;
                }
                try{
                    if (checkDoubtful(laOiriginData).equals(WebConstant.YesOrNo.NO)) {
                        Record payLegal = new Record();
                        payLegal.set("source_sys", "0");
                        payLegal.set("origin_id", laOiriginData.getLong("id"));
                        payLegal.set("pay_code", laOiriginData.getStr("pay_code"));
                        payLegal.set("channel_id", laOiriginData.getLong("channel_id"));
                        payLegal.set("org_id", laOiriginData.getLong("tmp_org_id"));
                        payLegal.set("org_code", laOiriginData.getStr("tmp_org_code"));
                        payLegal.set("amount", laOiriginData.getBigDecimal("amount"));
                        payLegal.set("recv_acc_name", laOiriginData.getStr("recv_acc_name"));
                        payLegal.set("recv_cert_type", laOiriginData.getStr("recv_cert_type"));
                        payLegal.set("recv_cert_code", laOiriginData.getStr("recv_cert_code"));
                        payLegal.set("recv_bank_type", laOiriginData.getStr("recv_bank_type"));
                        payLegal.set("recv_bank_name", laOiriginData.getStr("recv_bank_name"));
                        payLegal.set("recv_acc_no", laOiriginData.getStr("recv_acc_no"));
                        payLegal.set("consumer_acc_name", laOiriginData.get("recv_acc_name"));
                        payLegal.set("create_time", laOiriginData.getDate("create_time"));
                        if (!Db.save("pay_legal_data", payLegal)) {
                            return false;
                        }
                        Record laPayLegalExt = new Record();
                        laPayLegalExt.set("legal_id", payLegal.getLong("id"));
                        laPayLegalExt.set("origin_id", laOiriginData.getLong("id"));
                        laPayLegalExt.set("branch_code", laOiriginData.getStr("branch_code"));
                        laPayLegalExt.set("org_code", laOiriginData.getStr("org_code"));
                        laPayLegalExt.set("preinsure_bill_no", laOiriginData.getStr("preinsure_bill_no"));
                        laPayLegalExt.set("insure_bill_no", laOiriginData.getStr("insure_bill_no"));
                        laPayLegalExt.set("biz_type", laOiriginData.getStr("biz_type"));
                        laPayLegalExt.set("pay_mode", laOiriginData.getStr("pay_mode"));
                        laPayLegalExt.set("pay_date", laOiriginData.getStr("pay_date"));
                        laPayLegalExt.set("recv_cert_type", laOiriginData.getStr("recv_cert_type"));
                        laPayLegalExt.set("bank_key", laOiriginData.getStr("bank_key"));
                        laPayLegalExt.set("sale_code", laOiriginData.getStr("sale_code"));
                        laPayLegalExt.set("sale_name", laOiriginData.getStr("sale_name"));
                        laPayLegalExt.set("op_code", laOiriginData.getStr("op_code"));
                        laPayLegalExt.set("op_name", laOiriginData.getStr("op_name"));
                        if (!Db.save("la_pay_legal_data_ext", laPayLegalExt)) {
                            return false;
                        }
                    }

                    int updRows = Db.update(Db.getSql("la_cfm.updLaOriginProcess"),
                            WebConstant.YesOrNo.YES.getKey(),
                            laOiriginData.getLong("id"),
                            laOiriginData.getInt("persist_version"));
                    if (updRows != 1) {
                        return false;
                    }else{
                        returnFlag = true;
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                return returnFlag;
            }
        });
        if (!flag) {
            log.info("批量付LA数据【"+laOiriginData.get("id")+"】执行失败！");
        }
    }


    private void validate(Record laOiriginData) throws ReqValidateException, EncryAndDecryException {
        //银行账号校验：recv_acc_no
        accNoValidate(laOiriginData);

        Record org = Db.findFirst(
                Db.getSql("la_cfm.getOrg"), laOiriginData.getStr("org_code"), laOiriginData.getStr("branch_code"));
        if (org == null) {
            throw new ReqValidateException("TMPPJ:未匹配到机构");
        }
        laOiriginData.set("tmp_org_id", org.getLong("org_id"));
        laOiriginData.set("tmp_org_code", org.getStr("tmp_org_code"));
        String recvCertType = laOiriginData.getStr("recv_cert_type");
        if(recvCertType != null && recvCertType.length() > 0){
            Record certType = Db.findFirst(Db.getSql("la_cfm.getCertType"), laOiriginData.getStr("recv_cert_type"));
            if (certType == null) {
                throw new ReqValidateException("TMPPJ:未匹配到证件类型");
            }
        }

        String payMode = TypeUtils.castToString(laOiriginData.get("pay_mode"));
        if ("C".equalsIgnoreCase(payMode) || "H".equalsIgnoreCase(payMode)) {
            List<Record> channels = Db.find(Db.getSql("la_cfm.getChannel")
                    , 0
                    , org.getLong("org_id")
                    , laOiriginData.getStr("bank_key")
                    , 1);
            if (channels == null || channels.size() == 0) {
                throw new ReqValidateException("TMPPJ:未匹配到通道");
            }
            if (channels.size() > 1) {
                throw new ReqValidateException("TMPPJ:匹配到多个通道");
            }

            Record channel = channels.get(0);

            laOiriginData.set("channel_id", channel.getLong("channel_id"));
            laOiriginData.set("channel_code", channel.getStr("channel_code"));
            laOiriginData.set("recv_bank_type", channel.getStr("bank_type"));
            laOiriginData.set("recv_bank_name", channel.getStr("name"));
            if ("H".equalsIgnoreCase(payMode)) {
            	laOiriginData.set("pay_mode", "C");
			}
        }else {
            log.debug("批收原始数据校验，pay_mode不等于c，不校验通道！");
            laOiriginData.set("channel_id", 0);
            laOiriginData.set("channel_code", "");
            laOiriginData.set("recv_bank_type", "");
            laOiriginData.set("recv_bank_name", "");
        }
    }
    /**
     * 银行账号解密-》校验-》加密
     * @param r
     * @throws ReqValidateException
     * @throws EncryAndDecryException
     */
    private void accNoValidate(Record r) throws ReqValidateException, EncryAndDecryException{

        String oldRecvAccNo = r.getStr("recv_acc_no");
        String payMode = TypeUtils.castToString(r.get("pay_mode"));
        if ("C".equalsIgnoreCase(payMode) || "H".equalsIgnoreCase(payMode)) {
            // 数据库解密
            String recvAccNo = DDHSafeUtil.decrypt(oldRecvAccNo);
            if (null == recvAccNo) {
                throw new ReqValidateException("TMPPJ:银行账号数据库解密失败");
            }

            // 账号非法校验
            log.debug("数据库解密[{}]=[{}]", oldRecvAccNo, SymmetricEncryptUtil.accNoAddMask(recvAccNo));
            boolean accNoValidate = ValidateUtil.accNoValidate(recvAccNo);
            if (!accNoValidate) {
                throw new ReqValidateException("TMPPJ:银行账号非法");
            }

            // 对称加密
            String newRecvAccNo = SymmetricEncryptUtil.getInstance().encrypt(recvAccNo);
            log.debug("对称加密后的密文=[{}]", newRecvAccNo);
            r.set("recv_acc_no", newRecvAccNo);

        } else {//柜面付特俗处理
        	String recvAccNo = "";
            if (StringUtils.isBlank(oldRecvAccNo)) {
            	recvAccNo = " ";
            }else {
            	// 数据库解密
            	recvAccNo = DDHSafeUtil.decrypt(oldRecvAccNo);
            	if (null == recvAccNo) {
            		recvAccNo = " ";
            	}
			}

            // 对称加密
            String newRecvAccNo = SymmetricEncryptUtil.getInstance().encrypt(recvAccNo);
            log.debug("对称加密后的密文=[{}]", newRecvAccNo);
            r.set("recv_acc_no", newRecvAccNo);
        }


    }

    private WebConstant.YesOrNo checkDoubtful(Record originData) throws Exception {
        if (originData.get("recv_acc_no") == null) {
            originData.set("recv_acc_no", "");
        }

        Record checkDoubtful = new Record();

        String payMode = TypeUtils.castToString(originData.get("pay_mode"));
        if ("C".equalsIgnoreCase(payMode) || "H".equalsIgnoreCase(payMode)) {
            Record bankRecord = Db.findById("bankkey_setting",
                    "bankkey",
                    TypeUtils.castToString(originData.get("bank_key")));
            originData.set("recv_bank_type", TypeUtils.castToString(bankRecord.get("bank_type")));
        }/*else {
			String accno = GmfConfigAccnoSection.getInstance().getAccno();
			log.debug("批收原始数据校验，pay_mode不等于c，从all_bank_info获取[{}]的bank_type！", accno);
			Record findFirst = Db.findFirst(Db.getSql("la_cfm.qryBankTypeByAccNo"), accno);
			if (null == findFirst) {
				log.error("通过柜面付的付款账号{}，没有查询到银行信息", accno);
			}
			originData.set("recv_bank_type", TypeUtils.castToString(findFirst.get("bank_type")));
			originData.set("recv_bank_name", TypeUtils.castToString(findFirst.get("name")));
		}*/

        Map<String, Object> columns = originData.getColumns();
        for (Map.Entry<String, Object> entry : columns.entrySet()) {
            String key = entry.getKey();
            if(key.equals("id")){
                checkDoubtful.set("origin_id", entry.getValue());
                continue;
            }
            if (key.equals("persist_version") || key.equals("source_sys") || key.equals("channel_code")) {
                continue;
            }
            checkDoubtful.set(key, entry.getValue());
        }
        String createTime = DateFormatThreadLocal.format("yyyyMMdd",originData.getDate("create_time"));
        String identification = MD5Kit.string2MD5(originData.getStr("insure_bill_no")
                + "_" +originData.getStr("recv_acc_name")
                + "_" +originData.getStr("amount"))
                + "_" +createTime;

        checkDoubtful.set("identification", identification);
        checkDoubtful.set("consumer_acc_name", originData.get("recv_acc_name"));
        //判断可疑表中是否存在可疑数据
        List<Record> checkRecordList = Db.find(Db.getSql("la_cfm.getpaycheck"),originData.getStr("insure_bill_no"),originData.getStr("recv_acc_name"),
                originData.getStr("amount"),createTime);
        if(checkRecordList!=null && checkRecordList.size()!=0){
            checkDoubtful.set("is_doubtful", 1);
            Db.save("la_check_doubtful", checkDoubtful);
            /**
             * 将合法表中数据迁移到可疑表中，并更新状态为未处理
             */
            List<Record> legalRecordList = Db.find(Db.getSql("la_cfm.getpaylegal"),originData.getStr("insure_bill_no"),originData.getStr("recv_acc_name"),
                    originData.getStr("amount"),createTime);
            if(legalRecordList!=null && legalRecordList.size()!=0){
                for(Record legalRecord : legalRecordList){
                    //删除合法表中数据和合法扩展表数据
                    Db.deleteById("pay_legal_data","id",legalRecord.getLong("id"));
                    Db.delete(Db.getSql("la_cfm.dellapaylegalext"),legalRecord.getLong("id"));
                    CommonService.update("la_check_doubtful",
                            new Record().set("is_doubtful", 1).set("status", 0),
                            new Record().set("pay_code", TypeUtils.castToString(legalRecord.get("pay_code"))));
                }
            }
            return WebConstant.YesOrNo.YES;
        }else{
            checkDoubtful.set("is_doubtful", 0);
        }

        Db.save("la_check_doubtful", checkDoubtful);

        /**
         * 根据保单号，收款人，金额查询合法表中是否存在数据，如果存在视为可疑数据，将合法表中的数据删除，更新可疑表数据状态为可疑
         */
        List<Record> legalRecordList = Db.find(Db.getSql("la_cfm.getpaylegal"),originData.getStr("insure_bill_no"),originData.getStr("recv_acc_name"),
                originData.getStr("amount"),createTime);
        if(legalRecordList!=null && legalRecordList.size()!=0){
            //可疑数据
            CommonService.update("la_check_doubtful",
                    new Record().set("is_doubtful", 1),
                    new Record().set("id", checkDoubtful.getLong("id")));
            for(Record legalRecord : legalRecordList){
                //删除合法表中数据和合法扩展表数据
                Db.deleteById("pay_legal_data","id",legalRecord.getLong("id"));
                Db.delete(Db.getSql("la_cfm.dellapaylegalext"),legalRecord.getLong("id"));
                CommonService.update("la_check_doubtful",
                        new Record().set("is_doubtful", 1),
                        new Record().set("pay_code", TypeUtils.castToString(legalRecord.get("pay_code"))));
            }
            return WebConstant.YesOrNo.YES;
        }
        return WebConstant.YesOrNo.NO;
    }

    /*private boolean isChannelOnline(String channelCode){
        Record record = Db.findFirst(Db.getSql("la_cfm.getChannelOnline"), channelCode);
        if(record == null){
            return false;
        }
        return true;
    }*/
}
