package com.qhjf.cfm.web.inter.impl;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.impl.batch.SysBatchRecvInter;
import com.qhjf.cfm.web.inter.manager.SysInterManager;
import com.qhjf.cfm.web.webservice.sft.SftRecvCallBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: cht
 * @Date: 2019/2/26
 * @Description:
 */
public class SysTradeResultRecvBatchQueryInter implements ISysAtomicInterface {

    private static Logger log = LoggerFactory.getLogger(SysTradeResultRecvBatchQueryInter.class);
    private IMoreResultChannelInter channelInter;
    private Record instr;


    @Override
    public Record genInstr(Record record) {
        this.instr = new Record();
        Date date = new Date();

        instr.set("bank_serial_number", record.getStr("bank_serial_number"));
        instr.set("reqnbr", record.getStr("reqnbr"));
        instr.set("source_ref", record.getStr("source_ref"));
        instr.set("bill_id", record.getLong("bill_id"));
        instr.set("process_bank_type", record.getStr("recv_bank_type"));
        instr.set("trade_date", record.get("trade_date"));
        instr.set("pre_query_time", DateKit.toDate(DateKit.toStr(date, DateKit.timeStampPattern)));
        return this.instr;
    }

    @Override
    public Record getInstr() {
        return this.instr;
    }

    @Override
    public void callBack(String jsonStr) throws Exception {
        Db.delete("batch_recv_instr_queue_lock", instr);

        log.debug("批量收付查询历史交易状态指令回写开始。。。instr={}", instr);
        int resultCount = channelInter.getResultCount(jsonStr);
        if (resultCount <= 0) {
            return;
        }

        Record instrTotalRecord = findInstrTotal(instr.getStr("bank_serial_number"));
        if (null == instrTotalRecord) {
        	log.error("批量收付查询历史交易状态指令回写，通过bank_serial_number=[{}]未查询到指令！", instr.getStr("bank_serial_number"));
        	return;
		}
        
        final String billTable = instrTotalRecord.getStr("source_ref");//批次汇总表名
        final String billDetailTable = SysInterManager.getDetailTableName(billTable);//批次明细表名
        final String billTablePrimaryKey = SysInterManager.getSourceRefPrimaryKey(billDetailTable);//批次汇总表主键

        for (int i = 0; i < resultCount; i++) {
        	//channel返回的Record对象包括键：
            //	CMBC:通过付款账号+金额查询指令详情数据
            //		pay_account_no/amount/bank_err_msg/bank_err_code/status
            //	ICBC:通过指令汇总表的流水号+指令详情表指令序号查询指令详情数据
        	//		package_seq/bank_err_msg/bank_err_code/status
            Record channelRecord = channelInter.parseResult(jsonStr, i);
            channelRecord.set("id", instrTotalRecord.getLong("id"));
            channelRecord.set("recv_bank_cnaps", instrTotalRecord.getLong("recv_bank_cnaps"));
            
            //单条回写单据明细与指令明细
            singleResultWriteBack(channelRecord, billDetailTable, billTablePrimaryKey);
        }
        writeBack(instrTotalRecord);
        log.debug("批量收付状态查询回写结束");

    }

    @Override
    public void callBack(Exception e) throws Exception {
        Db.delete("batch_recv_instr_queue_lock", instr);
    }

    @Override
    public void setChannelInter(IChannelInter channelInter) {
        this.channelInter = (IMoreResultChannelInter) channelInter;
    }

    @Override
    public IChannelInter getChannelInter() {
        return this.channelInter;
    }

    /**
     * 单笔回写完毕，检查是否具备回写汇总表条件，具备则回写
     */
    private void writeBack(final Record instrTotal) {
        final Long instrTotalId = TypeUtils.castToLong(instrTotal.get("id"));
        //1.检查是否满足回写条件
        Integer count = Db.queryInt(Db.getSql("batchrecv.countInstrDetailInHandling"), instrTotalId);
        if (count > 0) {
            //更新回写时间
            int upd = CommonService.updateRows(SysBatchRecvInter.BATCH_RECV_INSTR_TOTLE_TALBE
                    , new Record().set("init_resp_time", new Date())
                    , new Record().set("id", instrTotal.get("id")));
            if (upd != 1) {
                log.error("批量收付查询历史交易状态指令原始数据回写，更新[{}.init_resp_time]失败, updateRows=[{}]", SysBatchRecvInter.BATCH_RECV_INSTR_TOTLE_TALBE, upd);
            }
            return;
        }

        //2.查询付款子批次汇总信息 对应的原始数据表名
        String billTotalTbName = instrTotal.getStr("source_ref");

        Record billTotalRecord = SysBatchRecvInter.getBillTotalRecord(billTotalTbName, instrTotal.getLong("bill_id"));
        if (null == billTotalRecord) {
            log.error("需人工处理,批量收付查询历史交易状态指令原始数据回写，通过source_ref=[{}],bill_id=[{}]未找到单据！", billTotalTbName, instrTotal.getLong("bill_id"));
            return;
        }
        
        final String originTb = SysBatchRecvInter.LA_ORIGIN;

        //3.更新汇总表与原始数据
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                int updInstrTotal = CommonService.updateRows("batch_recv_instr_queue_total"
                		, new Record().set("status", 2)
                		, new Record().set("id", instrTotalId));
                if (updInstrTotal == 1) {
                    log.debug("批量收付查询历史交易状态指令原始数据回写，更新指令汇总表成功！");
                    //更新recv_batch_total
                    if (SysBatchRecvInter.updBillTotal(instrTotalId, TypeUtils.castToLong(instrTotal.get("bill_id")), instrTotal.getStr("source_ref"))) {
                        
                        //更新la_origin_recv_data
                        int updOrigin = 0;
                        if (originTb.equals(SysBatchRecvInter.LA_ORIGIN)) {
                            log.debug("批量收付查询历史交易状态指令原始数据回写，回写LA原始数据");
                            updOrigin = Db.update(Db.getSql("batchrecv.updOrginSuccLa"), instrTotalId);
                        }
                        if (updOrigin == instrTotal.getInt("total_num")) {
                            return true;
                        } else {
                            log.error("批量收付查询历史交易状态指令原始数据回写，付款指令汇总信息表batch_recv_instr_queue_total.total_num与更新的原始数据条数[{}]不一致, instrTotalId={}", updOrigin, instrTotalId);
                            return false;
                        }
                    } else {
                        log.error("批量收付查询历史交易状态指令原始数据回写，更新{}失败", instrTotal.getStr("source_ref"));
                        return false;
                    }
                }
                log.error("需人工处理，批量收付查询历史交易状态指令原始数据回写，更新batch_recv_instr_queue_total失败，更新了多条数据！");
                return false;
            }
        });

        if (flag) {
        	log.info("批量收付查询历史交易状态指令原始数据回写，回写成功，开始回调LA。。。");
            try {
                List<Record> originRecord = null;
                originRecord = Db.find(Db.getSql("disk_backing.selectLaRecvOriginData"), billTotalRecord.get("child_batchno"));
                SftRecvCallBack callback = new SftRecvCallBack();
                callback.callback(WebConstant.SftOsSource.LA.getKey(), originRecord);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("===============回调失败");
            }
        }else {
        	log.info("批量收付查询历史交易状态指令原始数据回写，回写失败！");
		}
    }
    
    
    private Record findInstrTotal(String bankSerialNumber){
    	return Db.findFirst(Db.getSql("batchrecv.findInstrTotal"), bankSerialNumber);
    }
    
    /**
     * 银行返回数据进行校验;
     * @param bankRcord
     * @return
     */
    private Record handleBankData(Record bankRcord){
    	String recvBankCnaps = bankRcord.getStr("recv_bank_cnaps");
    	if (recvBankCnaps.startsWith("308")) {
    		//付方账号存在校验
    		String payAccountNo = bankRcord.getStr("pay_account_no");
    		if (null == payAccountNo || "".equals(payAccountNo.trim())) {
    			log.error("需人工排查,批量收付查询历史交易状态指令回写，【{}】没有返回pay_account_no，跳过该笔回写！", bankRcord);
    			return null;
    		}
    		
    		String encPayAccNo = null;
    		try {
    			encPayAccNo = SymmetricEncryptUtil.getInstance().encrypt(payAccountNo.trim());
    		} catch (EncryAndDecryException e1) {
    			log.error("需人工排查,批量收付查询历史交易状态指令回写，付款人账号[{}]加密失败，跳过该笔回写！", SymmetricEncryptUtil.accNoAddMask(payAccountNo.trim()));
    			e1.printStackTrace();
    			return null;
    		}
    		
    		bankRcord.set("pay_account_no", encPayAccNo);
		
    		//付款金额校验
    		try {
    			Double amount = TypeUtils.castToDouble(bankRcord.get("amount"));
    			bankRcord.set("amount", amount);
    		} catch (Exception e) {
    			log.error("需人工排查,批量收付查询历史交易状态指令回写，【{}】金额转换为double失败，跳过该笔回写！", bankRcord.get("amount"));
    			e.printStackTrace();
    			return null;
    		}
    	}
        
        //异常信息截取
        String bankErrMsg = bankRcord.getStr("bank_err_msg");
        bankRcord.set("bank_err_msg", bankErrMsg.length() > 200 ? bankErrMsg.substring(0, 200) : bankErrMsg);
        
        return bankRcord;
    }
    /**
     * 银行返回的交易明细，进行单条回写：1.回写单据明细表；2.回写指令明细表
     * @param channelRecord
     * @param billDetailTable
     * @param billTablePrimaryKey
     */
    private void singleResultWriteBack(Record channelRecord, final String billDetailTable, final String billTablePrimaryKey){
    	final int status = channelRecord.getInt("status");
    	//付款银行账号对称加密，金额检查，异常信息截取
    	final Record parseRecord = handleBankData(channelRecord);
        if (parseRecord == null) {
			return;
		}
        
        if (status == WebConstant.PayStatus.SUCCESS.getKey() || status == WebConstant.PayStatus.FAILD.getKey()) {
        	//查询收款指令明细信息
        	List<Record> instrDetailRecordList = getInstrDetailRecordList(parseRecord);
            
        	if (null == instrDetailRecordList || instrDetailRecordList.size() == 0) {
            	log.error("需人工排查,批量收付查询历史交易状态指令回写，通过【{}】未查询到单据详情！", parseRecord);
            	return;
			}
            if (instrDetailRecordList.size() > 1) {
            	log.error("需人工排查,批量收查询历史交易状态指令回写，指令汇总主键=【{}】通过银行账号=【{}】，金额=【{}】查询到多条数据！", parseRecord.get("id"), parseRecord.get("pay_account_no"), parseRecord.get("amount"));
            	return;
			}
            
            final Record instrDetailRecord = instrDetailRecordList.get(0);
            
            boolean flag = Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    Long detailId = TypeUtils.castToLong(instrDetailRecord.get("detail_id"));
                    Record bill_setRecord = new Record();
                    Record bill_whereRecord = new Record().set(billTablePrimaryKey, detailId);

                    Record instr_setRecord = new Record();
                    Record instr_whereRecord = new Record().set("id", instrDetailRecord.getLong("id"));

                    if (status == WebConstant.PayStatus.SUCCESS.getKey()) {
                        bill_setRecord.set(SysInterManager.getStatusFiled(billDetailTable), SysInterManager.getSuccessStatusEnum(billDetailTable))
                        			  .set("bank_err_code", "成功")
                        			  .set("bank_err_msg", TypeUtils.castToString(parseRecord.get("bank_err_msg")));

                        instr_setRecord.set("status", SysInterManager.getSuccessStatusEnum(SysBatchRecvInter.BATCH_RECV_INSTR_DETAIL_TALBE))
	                            	   .set("bank_err_msg", TypeUtils.castToString(parseRecord.get("bank_err_msg")))
	                                   .set("bank_back_time", new Date());
                    } else {
                        bill_setRecord.set(SysInterManager.getStatusFiled(billDetailTable), SysInterManager.getFailStatusEnum(billDetailTable))
                        			  .set("bank_err_msg", TypeUtils.castToString(parseRecord.get("bank_err_msg")))
                        			  .set("bank_err_code", TypeUtils.castToString(parseRecord.get("bank_err_code")));

                        instr_setRecord.set("status", SysInterManager.getFailStatusEnum(SysBatchRecvInter.BATCH_RECV_INSTR_DETAIL_TALBE))
                        			   .set("bank_err_msg", TypeUtils.castToString(parseRecord.get("bank_err_msg")))
                        			   .set("bank_err_code", TypeUtils.castToString(parseRecord.get("bank_err_code")))
                        			   .set("bank_back_time", new Date());
                    }

                    // 1.更新单据状态；2.修改指令表状态
                    if (CommonService.updateRows(billDetailTable, bill_setRecord, bill_whereRecord) == 1) { // 修改单据状态
                        boolean updDetail = CommonService.updateRows(SysBatchRecvInter.BATCH_RECV_INSTR_DETAIL_TALBE, instr_setRecord, instr_whereRecord) == 1;
                        if (!updDetail) {
                            log.error("批量收付状态查询回写时，[{}]更新失败！instr_setRecord=【{}】，instr_whereRecord=【{}】", SysBatchRecvInter.BATCH_RECV_INSTR_DETAIL_TALBE, instr_setRecord, instr_whereRecord);
                        }
                        return true;
                    } else {
                        log.error("批量收付状态查询回写时，[{}]更新失败！bill_setRecord=【{}】，bill_whereRecord=【{}】", billDetailTable, bill_setRecord, bill_whereRecord);
                        return false;
                    }
                }
            });
            if (flag) {
				log.error("批量收付状态查询回写时，回写失败，事务回滚！");
			}
        }else {
			log.debug("批量收付状态查询回写时，该笔处于处理中状态【{}】", parseRecord);
		}
    }
    /**
     * 查询收款指令明细信息
     * 1.CMBC通过 银行账号+金额+收款指令汇总表主键查询
     * 2.ICBC通过 指令汇总表的指令流水号+指令明细表的指令序号查询
     * @param r
     * @return
     */
    private List<Record> getInstrDetailRecordList(Record r){
    	String recvBankCnaps = r.getStr("recv_bank_cnaps");
    	if (recvBankCnaps.startsWith("308")) {
    		return Db.find(Db.getSql("batchrecv.selIntrDetailByAcc")
    				, r.getLong("id")
    				, TypeUtils.castToString(r.get("pay_account_no"))
    				, TypeUtils.castToDouble(r.get("amount")));
		}else if(recvBankCnaps.startsWith("102")) {
			return Db.find(Db.getSql("batchrecv.selInstrDetailByPackageSeq")
					, r.getLong("id")
					, TypeUtils.castToString(r.get("package_seq")));
		}
    	return null;
    }
    
}
