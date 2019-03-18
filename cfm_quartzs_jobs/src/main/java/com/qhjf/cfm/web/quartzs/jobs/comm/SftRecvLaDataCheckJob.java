package com.qhjf.cfm.web.quartzs.jobs.comm;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.exceptions.ReqValidateException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.quartzs.jobs.pub.PubJob;
import com.qhjf.cfm.web.quartzs.jobs.utils.DDHSafeUtil;
import com.qhjf.cfm.web.quartzs.jobs.utils.ValidateUtil;
import com.qhjf.cfm.web.webservice.sft.SftRecvCallBack;
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

public class SftRecvLaDataCheckJob implements Job{

    private static Logger log = LoggerFactory.getLogger(PubJob.class);
    private ExecutorService executeService = Executors.newFixedThreadPool(100);
    private List<Future<String>> resultList = new ArrayList<Future<String>>();
    
    private static final String ORG_UNMATCH = "未匹配到机构";
    private static final String CERT_UNMATCH = "未匹配到证件类型";
    private static final String CHANNEL_UNMATCH = "未匹配到通道";
    private static final String CHANNEL_MULTY_MATCH = "匹配到多个通道";
//    private static final String BK_UNENABLE = "bankkey状态未启用";
//    private static final String CHANNEL_UNENABLE = "通道状态未启用";
    
    public void execute(JobExecutionContext context) throws JobExecutionException {
    	log.debug("LA批收原始数据校验任务开始");
        List<Record> list = Db.find(Db.getSql("la_recv_cfm.getLARecvUnCheckedOriginList"), WebConstant.YesOrNo.NO.getKey());
        if (list == null || list.size() == 0) {
        	if (Db.update(Db.getSql("la_recv_cfm.updLARecvTotle")) > 0) {
				log.debug("LA批收校验主子表数据，更新主表状态为4成功");
				list = Db.find(Db.getSql("la_recv_cfm.getLARecvUnCheckedOriginList"), WebConstant.YesOrNo.NO.getKey());
				if (list == null || list.size() == 0) {
					return;
				}
        	}else {
				log.debug("LA批收校验主子表数据，更新主表状态为4失败");
				return;
			}
        }
        
        log.debug("LA批收原始数据校验,待校验数据条数size = {}", list.size());
        for (Record record : list) {
            Future<String> future = executeService.submit(new ExecuteThread(record));
            resultList.add(future);
        }
        executeService.shutdown(); // 关闭线程池,不在接收新的任务
        try{
            while(true){
                if(executeService.isTerminated()){
                    log.info("LA批收原始数据校验执行完毕！");
                    break;
                }else{
                    Thread.sleep(2000);
                }
            }
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        
        log.debug("LA批收原始数据校验任务结束");


    }

    public class ExecuteThread implements Callable<String> {

        private Record laRecvOiriginData;

        public ExecuteThread(Record laOiriginData) {
            this.laRecvOiriginData = laOiriginData;
        }

        @Override
        public String call() throws Exception {
            boolean flag = Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    boolean returnFlag = false;
                    try {
                    	//1.合法性校验
                        validate(laRecvOiriginData);
                    } catch (ReqValidateException e) {
                        e.printStackTrace();
                        return failWriteBackAndPushCoreSys(e.getMessage());
                    } catch (EncryAndDecryException e) {
						e.printStackTrace();
						return false;
					}
                    
                    /*//2.通道是否已经启用
                    boolean isChannelOnline = isChannelOnline(laRecvOiriginData.getStr("channel_code"));
                    if(!isChannelOnline){
                    	log.debug("LA批收原始数据校验，原始数据所属通道未启用！record={}", laRecvOiriginData);
                    	int flag = 0;
                    	flag = Db.update(Db.getSql("la_recv_cfm.updLaOriginStatus"),
                                WebConstant.YesOrNo.YES.getKey(),
                                WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_N.getKey(),
                                WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_N.getDesc(),
                                laRecvOiriginData.getLong("id"),
                                laRecvOiriginData.getInt("persist_version"));
                        if(flag == 1){
                            return true;
                        }else{
                        	log.debug("LA批收原始数据校验，原始数据所属通道未启用！更新数据库失败！");
                            return false;
                        }
                    }*/
                    
                    //3.可疑校验
                    try{
                        if (checkDoubtful(laRecvOiriginData).equals(WebConstant.YesOrNo.NO)) {
                        	boolean genAndSaveLegalDataBool = genAndSaveLegalData();
                            if (!genAndSaveLegalDataBool) {
                                return false;
                            }
                        }

                        //更新原始数据为已处理
                        int updRows = Db.update(Db.getSql("la_recv_cfm.updLaRecvOriginProcess"),
                                WebConstant.YesOrNo.YES.getKey(),
                                laRecvOiriginData.getLong("id"),
                                laRecvOiriginData.getInt("persist_version"));
                        if (updRows != 1) {
                        	log.error("LA批收原始数据校验，更新原始数据失败！originId={}", laRecvOiriginData.getLong("id"));
                            return false;
                        }
                        
                        returnFlag = true;
                    }catch (Exception e){
                    	log.error("LA批收原始数据校验，可疑校验/save合法数据/更新原始数据 失败！");
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
        	//银行账号合法校验：pay_acc_no
        	accNoValidate(laOiriginData);
        	/*boolean accNoValidate = ValidateUtil.accNoValidate(laOiriginData.getStr("pay_acc_no"));
        	if (!accNoValidate) {
        		throw new ReqValidateException("银行账号非法");
			}*/
        	
            Record org = Db.findFirst(
                    Db.getSql("la_cfm.getOrg"), laOiriginData.getStr("org_code"), laOiriginData.getStr("branch_code"));
            if (org == null) {
                throw new ReqValidateException(ORG_UNMATCH);
            }
            laOiriginData.set("tmp_org_id", org.getLong("org_id"));
            laOiriginData.set("tmp_org_code", org.getStr("tmp_org_code"));
            String recvCertType = laOiriginData.getStr("pay_cert_type");
            if(recvCertType != null && recvCertType.length() > 0){
            	Record certType = Db.findFirst(Db.getSql("la_cfm.getCertType"), recvCertType);
                if (certType == null) {
                    throw new ReqValidateException(CERT_UNMATCH);
                }
            }
            
            List<Record> channels = Db.find(Db.getSql("la_cfm.getChannel")
            		, 0
            		, org.getLong("org_id")
            		, laOiriginData.getStr("bank_key")
            		, 0);
            if (channels == null || channels.size() == 0) {
                throw new ReqValidateException(CHANNEL_UNMATCH);
            }
            if (channels.size() > 1) {
                throw new ReqValidateException(CHANNEL_MULTY_MATCH);
            }
            
            Record channel = channels.get(0);
            /*Integer bankkeyStatus = channel.getInt("bankkey_status");
            if (null == bankkeyStatus || bankkeyStatus != 1) {
            	throw new ReqValidateException(BK_UNENABLE);
			}
            Integer isCheckout = channel.getInt("is_checkout");
            if (null == isCheckout || isCheckout != 1) {
            	throw new ReqValidateException(CHANNEL_UNENABLE);
			}*/
            
            //bankcode在回调LA批收时需要
            Db.update(Db.getSql("la_recv_cfm.updLaRecvOrigin")
            		, channel.getStr("bankcode")
            		, channel.getLong("id")
            		, channel.getInt("persist_version"));
            
            laOiriginData.set("channel_id", channel.getLong("channel_id"));
            laOiriginData.set("channel_code", channel.getStr("channel_code"));
            laOiriginData.set("pay_bank_type", channel.getStr("bank_type"));
            laOiriginData.set("pay_bank_name", channel.getStr("name"));
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
        /**
         * 合法校验失败，回写原始数据，并推送LA核心系统
         * @param errMsg
         * @return
         */
        private boolean failWriteBackAndPushCoreSys(String errMsg){
        	int flag = Db.update(Db.getSql("la_recv_cfm.updLaRecvOriginStatus"),
                    WebConstant.YesOrNo.YES.getKey(),
                    WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_F.getKey(),
                    errMsg,
                    laRecvOiriginData.getLong("id"),
                    laRecvOiriginData.getInt("persist_version"));
            if(flag == 1){
            	laRecvOiriginData.set("persist_version", laRecvOiriginData.getInt("persist_version") + 1);
            	laRecvOiriginData.set("tmp_err_message", errMsg);
            	laRecvOiriginData.set("tmp_status", WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_F.getKey());
            	SftRecvCallBack callback = new SftRecvCallBack();
    			callback.callback(WebConstant.SftOsSource.LA.getKey(), laRecvOiriginData);
                return true;
            }else{
            	log.error("LA批收原始数据校验未通过，更新原始数据时失败！record={}", laRecvOiriginData);
                return false;
            }
        }

        /**
         * 可疑数据校验
         * @param originData	原始数据
         * @return
         * @throws Exception
         */
        private WebConstant.YesOrNo checkDoubtful(Record originData) throws Exception {

            Record checkDoubtful = new Record();
            Record bankRecord = Db.findById("bankkey_setting", "bankkey", TypeUtils.castToString(originData.get("bank_key")));
            originData.set("pay_bank_type", TypeUtils.castToString(bankRecord.get("bank_type")));

            Map<String, Object> columns = originData.getColumns();
            for (Map.Entry<String, Object> entry : columns.entrySet()) {
                String key = entry.getKey();
                if(key.equals("id")){
                    checkDoubtful.set("origin_id", entry.getValue());
                    continue;
                }
                if (key.equals("persist_version") || key.equals("source_sys") || key.equals("channel_code")
                		||key.equals("trans_code") || key.equals("receipt") || key.equals("bankcode")
                		||key.equals("fee_mode") || key.equals("sacscode") || key.equals("sacstyp")
                		|| key.equals("job_no")) {
                    continue;
                }
                checkDoubtful.set(key, entry.getValue());
            }
            
            String identification = MD5Kit.string2MD5(originData.getStr("insure_bill_no")
                    + "_" + originData.getStr("pay_acc_name")
                    + "_" + originData.getStr("amount")
            		+ "_" + originData.getStr("recv_date"));

            checkDoubtful.set("identification", identification);
            checkDoubtful.set("is_doubtful", 0);

            Db.save("la_recv_check_doubtful", checkDoubtful);

            /**
             * 根据保单号，收款人，金额查询合法表中是否存在数据，如果存在视为可疑数据，将合法表中的数据删除，更新可疑表数据状态为可疑
             */
            List<Record> legalRecordList = Db.find(Db.getSql("la_recv_cfm.getrecvlegal"),originData.getStr("insure_bill_no"),originData.getStr("recv_acc_name"),
                    originData.getStr("amount"),originData.getStr("recv_date"));
            if(legalRecordList!=null && legalRecordList.size()!=0){
                Record legalRecord = legalRecordList.get(0);
                //可疑数据
                CommonService.update("la_recv_check_doubtful",
                        new Record().set("is_doubtful", 1),
                        new Record().set("id", checkDoubtful.getLong("id")));
                //删除合法表中数据和合法扩展表数据
                Db.deleteById("recv_legal_data","id",legalRecord.getLong("id"));
                Db.delete(Db.getSql("la_recv_cfm.dellarecvlegalext"),legalRecord.getLong("id"));

                CommonService.update("la_recv_check_doubtful",
                        new Record().set("is_doubtful", 1),
                        new Record().set("origin_id", legalRecord.getLong("origin_id")));
                return WebConstant.YesOrNo.YES;

            }
            return WebConstant.YesOrNo.NO;
        }
        
        private boolean genAndSaveLegalData(){
        	Record payLegal = new Record();
            payLegal.set("source_sys", "0");
            payLegal.set("origin_id", laRecvOiriginData.getLong("id"));
            payLegal.set("pay_code", laRecvOiriginData.getStr("pay_code"));
            payLegal.set("channel_id", laRecvOiriginData.getLong("channel_id"));
            payLegal.set("org_id", laRecvOiriginData.getLong("tmp_org_id"));
            payLegal.set("org_code", laRecvOiriginData.getStr("tmp_org_code"));
            payLegal.set("amount", laRecvOiriginData.getBigDecimal("amount"));
            payLegal.set("pay_acc_name", laRecvOiriginData.getStr("pay_acc_name"));
            payLegal.set("pay_cert_type", laRecvOiriginData.getStr("pay_cert_type"));
            payLegal.set("pay_cert_code", laRecvOiriginData.getStr("pay_cert_code"));
            payLegal.set("pay_bank_type", laRecvOiriginData.getStr("pay_bank_type"));
            payLegal.set("pay_bank_name", laRecvOiriginData.getStr("pay_bank_name"));
            payLegal.set("pay_acc_no", laRecvOiriginData.getStr("pay_acc_no"));
            
            if (Db.save("recv_legal_data", payLegal)) {
            	log.error("LA批收原始数据校验，recv_legal_data保存失败！payLegal={}", payLegal);
                return false;
            }
            
            Record laPayLegalExt = new Record();
            laPayLegalExt.set("legal_id", payLegal.getLong("id"));
            laPayLegalExt.set("origin_id", laRecvOiriginData.getLong("id"));
            laPayLegalExt.set("branch_code", laRecvOiriginData.getStr("branch_code"));
            laPayLegalExt.set("org_code", laRecvOiriginData.getStr("org_code"));
            laPayLegalExt.set("preinsure_bill_no", laRecvOiriginData.getStr("preinsure_bill_no"));
            laPayLegalExt.set("insure_bill_no", laRecvOiriginData.getStr("insure_bill_no"));
            laPayLegalExt.set("biz_type", laRecvOiriginData.getStr("biz_type"));
            laPayLegalExt.set("pay_mode", laRecvOiriginData.getStr("pay_mode"));
            laPayLegalExt.set("recv_date", laRecvOiriginData.getStr("recv_date"));
            laPayLegalExt.set("pay_cert_type", laRecvOiriginData.getStr("pay_cert_type"));
            laPayLegalExt.set("bank_key", laRecvOiriginData.getStr("bank_key"));
            laPayLegalExt.set("sale_code", laRecvOiriginData.getStr("sale_code"));
            laPayLegalExt.set("sale_name", laRecvOiriginData.getStr("sale_name"));
            laPayLegalExt.set("op_code", laRecvOiriginData.getStr("op_code"));
            laPayLegalExt.set("op_name", laRecvOiriginData.getStr("op_name"));
            if (!Db.save("la_recv_legal_data_ext", laPayLegalExt)) {
            	log.error("LA批收原始数据校验，la_recv_legal_data_ext保存失败！laPayLegalExt={}", laPayLegalExt);
                return false;
            }
            return true;
        }
        
        /**
         * 通过渠道号查询渠道
         * @param channelCode	渠道号
         * @return
         */
        private boolean isChannelOnline(String channelCode){
        	Record record = Db.findFirst(Db.getSql("la_cfm.getChannelOnline"), channelCode);
        	if(record == null){
        		return false;
        	}
        	return true;
        }
    }
}
