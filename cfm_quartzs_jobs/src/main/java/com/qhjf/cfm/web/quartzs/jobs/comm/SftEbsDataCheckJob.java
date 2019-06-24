package com.qhjf.cfm.web.quartzs.jobs.comm;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.exceptions.ReqValidateException;
import com.qhjf.cfm.utils.*;
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

public class SftEbsDataCheckJob implements Job{

    private static Logger log = LoggerFactory.getLogger(PubJob.class);
    private ExecutorService executeService = Executors.newFixedThreadPool(100);
    private List<Future<String>> resultList = new ArrayList<Future<String>>();

    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.debug("批量付EBS数据校验任务开始");
        List<Record> list = Db.find(Db.getSql("ebs_cfm.getEBSUnCheckedOriginList"), WebConstant.YesOrNo.NO.getKey());
        if (list == null || list.size() == 0) {
            return;
        }
        if (list != null && list.size() > 0) {
            log.debug(list.size() + "");
            for (Record record : list) {
                executeProcess(record);
            }
        }
        log.debug("批量付EBS数据校验任务结束");


    }

    public void executeProcess(final Record ebsOriginData){
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean returnFlag = false;
                try {
                    validate(ebsOriginData);
                } catch (ReqValidateException e) {
                    e.printStackTrace();
                    log.error("原始数据校验失败，开始回调EBS！");
                    int flag = Db.update(Db.getSql("ebs_cfm.updEbsOriginStatus"),
                            WebConstant.YesOrNo.YES.getKey(),
                            WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_F.getKey(),
                            e.getMessage(),
                            ebsOriginData.getLong("id"),
                            ebsOriginData.getInt("persist_version"));
                    if(flag == 1){
                        ebsOriginData.set("persist_version", ebsOriginData.getInt("persist_version") + 1);
                        SftCallBack callback = new SftCallBack();
                        callback.callback(WebConstant.SftOsSource.EBS.getKey(), ebsOriginData);
                        return true;
                    }else{
                        return false;
                    }
                } catch (EncryAndDecryException e) {
                    e.printStackTrace();
                    return false;
                }
                try{
                    if (checkDoubtful(ebsOriginData).equals(WebConstant.YesOrNo.NO)) {

                        Record payLegal = new Record();
                        payLegal.set("source_sys", "1");
                        payLegal.set("origin_id", ebsOriginData.getLong("id"));
                        payLegal.set("pay_code", ebsOriginData.getStr("pay_code"));
                        payLegal.set("channel_id", ebsOriginData.getLong("channel_id"));
                        payLegal.set("org_id", ebsOriginData.getLong("tmp_org_id"));
                        payLegal.set("org_code", ebsOriginData.getStr("tmp_org_code"));
                        payLegal.set("amount", ebsOriginData.getBigDecimal("amount"));
                        payLegal.set("recv_acc_name", ebsOriginData.getStr("recv_acc_name"));
                        payLegal.set("recv_cert_type", ebsOriginData.getStr("tmp_recv_cert_type"));
                        payLegal.set("recv_cert_code", ebsOriginData.getStr("recv_cert_code"));
                        payLegal.set("recv_bank_type", ebsOriginData.getStr("recv_bank_type"));
                        payLegal.set("recv_bank_name", ebsOriginData.getStr("recv_bank_name"));
                        payLegal.set("recv_acc_no", ebsOriginData.getStr("recv_acc_no"));
                        payLegal.set("create_time", ebsOriginData.getDate("create_time"));


                        String payMode = TypeUtils.castToString(ebsOriginData.get("pay_mode"));
                        if ("C".equalsIgnoreCase(payMode)) {
                        }else{
                            //柜面付
                            String company_name = ebsOriginData.getStr("company_name");
                            if(org.apache.commons.lang.StringUtils.isEmpty(company_name)){
                                payLegal.set("consumer_acc_name", ebsOriginData.getStr("recv_acc_name"));
                            }else{
                                payLegal.set("consumer_acc_name", company_name);
                            }
                        }

                        if (!Db.save("pay_legal_data", payLegal)) {
                            return false;
                        }

                        Record ebsPayLegalExt = new Record();
                        ebsPayLegalExt.set("legal_id", payLegal.getLong("id"));
                        ebsPayLegalExt.set("origin_id", ebsOriginData.getLong("id"));
                        ebsPayLegalExt.set("insure_type", ebsOriginData.getStr("insure_type"));
                        ebsPayLegalExt.set("org_code", ebsOriginData.getStr("org_code"));
                        ebsPayLegalExt.set("preinsure_bill_no", ebsOriginData.getStr("preinsure_bill_no"));
                        ebsPayLegalExt.set("insure_bill_no", ebsOriginData.getStr("insure_bill_no"));
                        ebsPayLegalExt.set("branch_bill_no", ebsOriginData.getStr("branch_bill_no"));
                        ebsPayLegalExt.set("biz_type", ebsOriginData.getStr("biz_type"));
                        ebsPayLegalExt.set("pay_mode", ebsOriginData.getStr("pay_mode"));
                        ebsPayLegalExt.set("pay_date", ebsOriginData.getStr("pay_date"));
                        ebsPayLegalExt.set("recv_cert_type", ebsOriginData.getStr("recv_cert_type"));
                        ebsPayLegalExt.set("company_name", ebsOriginData.getStr("company_name"));
                        ebsPayLegalExt.set("company_customer_no", ebsOriginData.getStr("company_customer_no"));
                        ebsPayLegalExt.set("biz_code", ebsOriginData.getStr("biz_code"));
                        ebsPayLegalExt.set("sale_code", ebsOriginData.getStr("sale_code"));
                        ebsPayLegalExt.set("sale_name", ebsOriginData.getStr("sale_name"));
                        ebsPayLegalExt.set("op_code", ebsOriginData.getStr("op_code"));
                        ebsPayLegalExt.set("op_name", ebsOriginData.getStr("op_name"));
                        ebsPayLegalExt.set("bank_key", ebsOriginData.getStr("bank_key"));
                        if (!Db.save("ebs_pay_legal_data_ext", ebsPayLegalExt)) {
                            return false;
                        }
                    }
                    int updRows = Db.update(Db.getSql("ebs_cfm.updEbsOriginProcess"),
                            WebConstant.YesOrNo.YES.getKey(),
                            ebsOriginData.getLong("id"),
                            ebsOriginData.getInt("persist_version"));
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
            log.info("批量付EBS数据【"+ebsOriginData.get("id")+"】执行失败！");
        }
    }

    private void validate(Record ebsOriginData) throws ReqValidateException, EncryAndDecryException {
        //银行账号校验：recv_acc_no
        accNoValidate(ebsOriginData);

        Record org = Db.findFirst(Db.getSql("ebs_cfm.getOrg"),
                TypeUtils.castToString(ebsOriginData.get("org_code")));
        if (org == null) {
            throw new ReqValidateException("TMPPJ:未匹配到机构");
        }
        ebsOriginData.set("tmp_org_id", org.getLong("org_id"));
        ebsOriginData.set("tmp_org_code", org.getStr("tmp_org_code"));
        String recvCertType = ebsOriginData.getStr("recv_cert_type");
        if(recvCertType != null && recvCertType.length() > 0){
            Record certType = Db.findFirst(Db.getSql("ebs_cfm.getCertType"),
                    TypeUtils.castToString(ebsOriginData.get("recv_cert_type")));
            if (certType == null) {
                throw new ReqValidateException("TMPPJ:未匹配到证件类型");
            }
        }

        /*//EBS银行大类匹配
        Record recvBank = Db.findFirst(Db.getSql("ebs_cfm.getRecvBank"), ebsOriginData.getStr("recv_bank_name"));
        if(recvBank == null){
            throw new ReqValidateException("未匹配到收款银行");
        }*/
        
        String payMode = TypeUtils.castToString(ebsOriginData.get("pay_mode"));
        if ("C".equalsIgnoreCase(payMode)) {
        	String bankKey = ebsOriginData.getStr("recv_bank_name");//recvBank.getStr("code");
            ebsOriginData.set("bank_key", bankKey);

            List<Record> channels = Db.find(Db.getSql("ebs_cfm.getChannel")
                    , 1
                    , org.getLong("org_id")
                    , bankKey);
            if (channels == null || channels.size() == 0) {
                throw new ReqValidateException("TMPPJ:未匹配到通道");
            }
            if (channels.size() > 1) {
                throw new ReqValidateException("TMPPJ:匹配到多个通道");
            }

            Record channel = channels.get(0);
           /* Integer bankkeyStatus = channel.getInt("bankkey_status");
            if (null == bankkeyStatus || bankkeyStatus != 1) {
                throw new ReqValidateException("bankkey状态未启用");
            }
            Integer isCheckout = channel.getInt("is_checkout");
            if (null == isCheckout || isCheckout != 1) {
                throw new ReqValidateException("通道状态未启用");
            }*/

            /*ebsOriginData.set("channel_id", channel.getLong("channel_id"));
            ebsOriginData.set("channel_code", channel.getStr("channel_code"));
            ebsOriginData.set("recv_bank_type", bankKey);
            ebsOriginData.set("recv_bank_name", recvBank.getStr("name"));*/

            String bankType = channel.getStr("bank_type");
            Map<String, Object> constBankType = TableDataCacheUtil.getInstance().getARowData("const_bank_type", "code", bankType);

            ebsOriginData.set("channel_id", channel.getLong("channel_id"));
            ebsOriginData.set("channel_code", channel.getStr("channel_code"));
            ebsOriginData.set("recv_bank_type", bankType);
            ebsOriginData.set("recv_bank_name", TypeUtils.castToString(constBankType.get("name")));
		}else {
            log.debug("批收原始数据校验，pay_mode不等于c，不校验通道！");
            ebsOriginData.set("channel_id", 0);
            ebsOriginData.set("channel_code", "");
            ebsOriginData.set("recv_bank_type", "");
            ebsOriginData.set("recv_bank_name", "");
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
        
        if ("C".equalsIgnoreCase(TypeUtils.castToString(r.get("pay_mode")))) {
        	//数据库解密
        	String recvAccNo = DDHSafeUtil.decrypt(oldRecvAccNo);
        	if (null == recvAccNo) {
        		throw new ReqValidateException("TMPPJ:银行账号数据库解密失败");
        	}
        	
        	//账号非法校验
        	log.debug("数据库解密[{}]=[{}]", oldRecvAccNo, SymmetricEncryptUtil.accNoAddMask(recvAccNo));
        	boolean accNoValidate = ValidateUtil.accNoValidate(recvAccNo);
        	if (!accNoValidate) {
        		throw new ReqValidateException("TMPPJ:银行账号非法");
        	}
        	
        	//对称加密
        	String newRecvAccNo = null;
        	newRecvAccNo = SymmetricEncryptUtil.getInstance().encrypt(recvAccNo);
        	log.debug("对称加密后的密文=[{}]", newRecvAccNo);
        	r.set("recv_acc_no", newRecvAccNo);
        }else {
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
        Record checkDoubtful = new Record();

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

        String payMode = TypeUtils.castToString(originData.get("pay_mode"));
        if ("C".equalsIgnoreCase(payMode)) {
        }else{
            //柜面付
            String company_name = originData.getStr("company_name");
            if(org.apache.commons.lang.StringUtils.isEmpty(company_name)){
                checkDoubtful.set("consumer_acc_name", originData.getStr("recv_acc_name"));
            }else{
                checkDoubtful.set("consumer_acc_name", company_name);
            }
        }

        //判断可疑表中是否存在可疑数据
        List<Record> checkRecordList = Db.find(Db.getSql("ebs_cfm.getpaycheck"),originData.getStr("insure_bill_no"),originData.getStr("recv_acc_name"),
                originData.getStr("amount"),createTime);
        if(checkRecordList!=null && checkRecordList.size()!=0){
            checkDoubtful.set("is_doubtful", 1);
            Db.save("ebs_check_doubtful", checkDoubtful);
            /**
             * 将合法表中数据迁移到可疑表中，并更新状态为未处理
             */
            List<Record> legalRecordList = Db.find(Db.getSql("ebs_cfm.getpaylegal"),originData.getStr("insure_bill_no"),originData.getStr("recv_acc_name"),
                    originData.getStr("amount"),createTime);
            if(legalRecordList!=null && legalRecordList.size()!=0){
                for(Record legalRecord : legalRecordList){
                    //删除合法表中数据和合法扩展表数据
                    Db.deleteById("pay_legal_data","id",legalRecord.getLong("id"));
                    Db.delete(Db.getSql("ebs_cfm.delebspaylegalext"),legalRecord.getLong("id"));
                    CommonService.update("ebs_check_doubtful",
                            new Record().set("is_doubtful", 1).set("status", 0),
                            new Record().set("pay_code", TypeUtils.castToString(legalRecord.get("pay_code"))));
                }
            }
            return WebConstant.YesOrNo.YES;
        }else{
            checkDoubtful.set("is_doubtful", 0);
        }

        Db.save("ebs_check_doubtful", checkDoubtful);

        /**
         * 根据保单号，收款人，金额查询合法表中是否存在数据，如果存在视为可疑数据，将合法表中的数据删除，更新可疑表数据状态为可疑
         */
        List<Record> legalRecordList = Db.find(Db.getSql("ebs_cfm.getpaylegal"),originData.getStr("insure_bill_no"),originData.getStr("recv_acc_name"),
                originData.getStr("amount"),createTime);
        if(legalRecordList!=null && legalRecordList.size()!=0){
            //可疑数据
            CommonService.update("ebs_check_doubtful",
                    new Record().set("is_doubtful", 1),
                    new Record().set("id", checkDoubtful.getLong("id")));
            for(Record legalRecord : legalRecordList){
                //删除合法表中数据和合法扩展表数据
                Db.deleteById("pay_legal_data","id",legalRecord.getLong("id"));
                Db.delete(Db.getSql("ebs_cfm.delebspaylegalext"),legalRecord.getLong("id"));
                CommonService.update("ebs_check_doubtful",
                        new Record().set("is_doubtful", 1),
                        new Record().set("pay_code", TypeUtils.castToString(legalRecord.get("pay_code"))));
            }
            return WebConstant.YesOrNo.YES;
        }
        return WebConstant.YesOrNo.NO;
    }

    /*private boolean isChannelOnline(String channelCode){
    	Record record = Db.findFirst(Db.getSql("ebs_cfm.getChannelOnline"), channelCode);
    	if(record == null){
    		return false;
    	}
    	return true;
    }*/
    
}

