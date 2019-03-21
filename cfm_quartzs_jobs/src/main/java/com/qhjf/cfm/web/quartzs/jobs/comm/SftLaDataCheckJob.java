package com.qhjf.cfm.web.quartzs.jobs.comm;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.exceptions.ReqValidateException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.quartzs.jobs.pub.PubJob;
import com.qhjf.cfm.web.quartzs.jobs.utils.DDHSafeUtil;
import com.qhjf.cfm.web.quartzs.jobs.utils.ValidateUtil;
import com.qhjf.cfm.web.utils.comm.file.tool.DataDoubtfulCache;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SftLaDataCheckJob implements Job{

    private static Logger log = LoggerFactory.getLogger(PubJob.class);
    private ExecutorService executeService = Executors.newFixedThreadPool(100);
    private List<Future<String>> resultList = new ArrayList<Future<String>>();

    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<Record> list = Db.find(Db.getSql("la_cfm.getLAUnCheckedOriginList"), WebConstant.YesOrNo.NO.getKey());
        if (list == null || list.size() == 0) {
            return;
        }
        if (list != null && list.size() > 0) {
            log.debug(list.size() + "");
            for (Record record : list) {
                Future<String> future = executeService.submit(new ExecuteThread(record));
                resultList.add(future);
            }
            executeService.shutdown(); // 关闭线程池,不在接收新的任务
            try{
                while(true){
                    if(executeService.isTerminated()){
                        log.info("LA执行完毕！");
                        break;
                    }else{
                        Thread.sleep(2000);
                    }
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        log.debug("LA数据校验任务结束");


    }

    public class ExecuteThread implements Callable<String> {

        private Record laOiriginData;

        public ExecuteThread(Record laOiriginData) {
            this.laOiriginData = laOiriginData;
        }

        @Override
        public String call() throws Exception {
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

                    /*boolean isChannelOnline = isChannelOnline(laOiriginData.getStr("channel_code"));
                    if(!isChannelOnline){
                    	int flag = Db.update(Db.getSql("la_cfm.updLaOriginStatus"),
                                WebConstant.YesOrNo.YES.getKey(),
                                WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_N.getKey(),
                                WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_N.getDesc(),
                                laOiriginData.getLong("id"),
                                laOiriginData.getInt("persist_version"));
                        if(flag == 1){
                            return true;
                        }else{
                            return false;
                        }
                    }*/
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
            if (flag) {
                return "success";
            } else {
                return "fail";
            }

        }

        private void validate(Record laOiriginData) throws ReqValidateException, EncryAndDecryException {
        	//银行账号校验：recv_acc_no
        	accNoValidate(laOiriginData);
        	
            Record org = Db.findFirst(
                    Db.getSql("la_cfm.getOrg"), laOiriginData.getStr("org_code"), laOiriginData.getStr("branch_code"));
            if (org == null) {
                throw new ReqValidateException("未匹配到机构");
            }
            laOiriginData.set("tmp_org_id", org.getLong("org_id"));
            laOiriginData.set("tmp_org_code", org.getStr("tmp_org_code"));
            String recvCertType = laOiriginData.getStr("recv_cert_type");
            if(recvCertType != null && recvCertType.length() > 0){
            	Record certType = Db.findFirst(Db.getSql("la_cfm.getCertType"), laOiriginData.getStr("recv_cert_type"));
                if (certType == null) {
                    throw new ReqValidateException("未匹配到证件类型");
                }
            }
            
            List<Record> channels = Db.find(Db.getSql("la_cfm.getChannel")
                    , 0
                    , org.getLong("org_id")
                    , laOiriginData.getStr("bank_key")
                    , 1);
            if (channels == null || channels.size() == 0) {
                throw new ReqValidateException("未匹配到通道");
            }
            if (channels.size() > 1) {
                throw new ReqValidateException("匹配到多个通道");
            }
            
            Record channel = channels.get(0);
            /*Integer bankkeyStatus = channel.getInt("bankkey_status");
            if (null == bankkeyStatus || bankkeyStatus != 1) {
            	throw new ReqValidateException("bankkey状态未启用");
			}
            Integer isCheckout = channel.getInt("is_checkout");
            if (null == isCheckout || isCheckout != 1) {
            	throw new ReqValidateException("通道状态未启用");
			}*/
            
            laOiriginData.set("channel_id", channel.getLong("channel_id"));
            laOiriginData.set("channel_code", channel.getStr("channel_code"));
            laOiriginData.set("recv_bank_type", channel.getStr("bank_type"));
            laOiriginData.set("recv_bank_name", channel.getStr("name"));
        }
        /**
         * 银行账号解密-》校验-》加密
         * @param r
         * @throws ReqValidateException
         * @throws EncryAndDecryException
         */
        private void accNoValidate(Record r) throws ReqValidateException, EncryAndDecryException{
        	String oldRecvAccNo = r.getStr("recv_acc_no");
        	//数据库解密
        	String recvAccNo = DDHSafeUtil.decrypt(oldRecvAccNo);
        	if (null == recvAccNo) {
        		throw new ReqValidateException("银行账号数据库解密失败");
			}
        	
        	//账号非法校验
        	log.debug("数据库解密[{}]=[{}]", oldRecvAccNo, SymmetricEncryptUtil.accNoAddMask(recvAccNo));
        	boolean accNoValidate = ValidateUtil.accNoValidate(recvAccNo);
        	if (!accNoValidate) {
        		throw new ReqValidateException("银行账号非法");
			}
        	
        	//对称加密
        	String newRecvAccNo = SymmetricEncryptUtil.getInstance().encrypt(recvAccNo);
        	log.debug("对称加密后的密文=[{}]", newRecvAccNo);
        	r.set("recv_acc_no", newRecvAccNo);
        }

        private WebConstant.YesOrNo checkDoubtful(Record originData) throws Exception {

            Record checkDoubtful = new Record();
            Record bankRecord = Db.findById("bankkey_setting",
                    "bankkey",
                    TypeUtils.castToString(originData.get("bank_key")));
            originData.set("recv_bank_type", TypeUtils.castToString(bankRecord.get("bank_type")));

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
            String identification = MD5Kit.string2MD5(originData.getStr("insure_bill_no")
                    + "_" +originData.getStr("recv_acc_name")
                    + "_" +originData.getStr("amount"));

            checkDoubtful.set("identification", identification);
            checkDoubtful.set("is_doubtful", 0);

            Db.save("la_check_doubtful", checkDoubtful);

            /**
             * 根据保单号，收款人，金额查询合法表中是否存在数据，如果存在视为可疑数据，将合法表中的数据删除，更新可疑表数据状态为可疑
             */
            List<Record> legalRecordList = Db.find(Db.getSql("la_cfm.getpaylegal"),originData.getStr("insure_bill_no"),originData.getStr("recv_acc_name"),
                    originData.getStr("amount"));
            if(legalRecordList!=null && legalRecordList.size()!=0){
                Record legalRecord = legalRecordList.get(0);
                //可疑数据
                CommonService.update("la_check_doubtful",
                        new Record().set("is_doubtful", 1),
                        new Record().set("id", checkDoubtful.getLong("id")));
                //删除合法表中数据和合法扩展表数据
                Db.deleteById("pay_legal_data","id",legalRecord.getLong("id"));
                Db.delete(Db.getSql("la_cfm.dellapaylegalext"),legalRecord.getLong("id"));
                CommonService.update("la_check_doubtful",
                        new Record().set("is_doubtful", 1),
                        new Record().set("origin_id", legalRecord.getLong("origin_id")));
                return WebConstant.YesOrNo.YES;

            }
            return WebConstant.YesOrNo.NO;
        }
        
        private boolean isChannelOnline(String channelCode){
        	Record record = Db.findFirst(Db.getSql("la_cfm.getChannelOnline"), channelCode);
        	if(record == null){
        		return false;
        	}
        	return true;
        }
    }
}
