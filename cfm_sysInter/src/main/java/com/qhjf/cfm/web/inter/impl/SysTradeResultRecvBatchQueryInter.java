package com.qhjf.cfm.web.inter.impl;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
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
        final String billTable = instrTotalRecord.getStr("source_ref");
        final String billTablePrimaryKey = SysInterManager.getSourceRefPrimaryKey(billTable);

        for (int i = 0; i < resultCount; i++) {
            Record parseRecord = channelInter.parseResult(jsonStr, i);
            final int status = parseRecord.getInt("status");
            final Map<String, Object> bankData = handleBankData(parseRecord);
            if (bankData == null) {
				continue;
			}
            
            if (status == WebConstant.PayStatus.SUCCESS.getKey() || status == WebConstant.PayStatus.FAILD.getKey()) {
            	//查询收款指令明细信息：通过 银行账号+金额+收款指令汇总表主键查询
                final Record instrDetailRecord = Db.findFirst(Db.getSql("batchrecv.selIntrDetailByAcc")
                		, instrTotalRecord.getLong("id")
                		, TypeUtils.castToString(bankData.get("pay_account_no"))
                		, TypeUtils.castToDouble(bankData.get("amount")));
                if (null == instrDetailRecord) {
                	log.error("*****************下面error需人工排查************************");
                	bankData.put("id", instrTotalRecord.get("id"));
                	log.error("批量收付查询历史交易状态指令回写，通过【{}】未查询到单据详情！", bankData);
                	continue;
				}
                
                boolean flag = Db.tx(new IAtom() {
                    @Override
                    public boolean run() throws SQLException {
                        Long detailId = TypeUtils.castToLong(instrDetailRecord.get("detail_id"));
                        Record bill_setRecord = new Record();
                        Record bill_whereRecord = new Record().set(billTablePrimaryKey, detailId);

                        Record instr_setRecord = new Record();
                        Record instr_whereRecord = new Record().set("id", instrDetailRecord.getLong("id"));

                        if (status == WebConstant.PayStatus.SUCCESS.getKey()) {
                            bill_setRecord.set(SysInterManager.getStatusFiled(billTable)
                                    , SysInterManager.getSuccessStatusEnum(billTable));

                            instr_setRecord.set("status", SysInterManager.getSuccessStatusEnum(SysBatchRecvInter.BATCH_RECV_INSTR_DETAIL_TALBE))
                                    .set("bank_back_time", new Date());
                        } else {
                            bill_setRecord.set(SysInterManager.getStatusFiled(billTable), SysInterManager.getFailStatusEnum(billTable))
                            			  .set("bank_err_msg", TypeUtils.castToString(bankData.get("bank_err_msg")))
                            			  .set("bank_err_code", TypeUtils.castToString(bankData.get("bank_err_code")));

                            instr_setRecord.set("status", SysInterManager.getFailStatusEnum(SysBatchRecvInter.BATCH_RECV_INSTR_DETAIL_TALBE))
                            			   .set("bank_err_msg", TypeUtils.castToString(bankData.get("bank_err_msg")))
                            			   .set("bank_err_code", TypeUtils.castToString(bankData.get("bank_err_code")))
                            			   .set("bank_back_time", new Date());
                        }

                        // 1.更新单据状态；2.修改队列表状态
                        if (CommonService.updateRows(billTable, bill_setRecord, bill_whereRecord) == 1) { // 修改单据状态
                            boolean updDetail = CommonService.updateRows(SysBatchRecvInter.BATCH_RECV_INSTR_DETAIL_TALBE, instr_setRecord, instr_whereRecord) == 1;
                            if (!updDetail) {
                                log.error("批量收付状态查询回写时，[{}]更新失败！instr_setRecord=【{}】，instr_whereRecord=【{}】", SysBatchRecvInter.BATCH_RECV_INSTR_DETAIL_TALBE, instr_setRecord, instr_whereRecord);
                            }
                            return true;
                        } else {
                            log.error("批量收付状态查询回写时，[{}]更新失败！bill_setRecord=【{}】，bill_whereRecord=【{}】", billTable, bill_setRecord, bill_whereRecord);
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
        	log.error("*****************下面error需人工处理************************");
            log.error("批量收付查询历史交易状态指令原始数据回写，通过source_ref=[{}],bill_id=[{}]未找到单据！", billTotalTbName, instrTotal.getLong("bill_id"));
            return;
        }
        
        //批收没有EBS，不判断数据源
        /*Integer sourceSys = SysBatchRecvInter.getSourceSys(billTotalTbName, billTotalRecord);
        if (null == sourceSys) {
        	log.error("*****************上面error需人工处理************************");
            return;
        }*/
        final String originTb = SysBatchRecvInter.LA_ORIGIN;

        //3.更新汇总表与原始数据
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //更新batch_recv_instr_queue_total：明细中有一条成功，汇总就更新为成功
                int updInstrTotal = Db.update(Db.getSql("batchrecv.updInstrTotal"), instrTotalId, instrTotalId);
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
                log.error("*****************下面error需人工处理************************");
                log.error("批量收付查询历史交易状态指令原始数据回写，更新batch_recv_instr_queue_total失败，更新了多条数据！");
                return false;
            }
        });

        if (flag) {
        	log.info("批量收付查询历史交易状态指令原始数据回写，回写成功，开始回调LA。。。");
            try {
                List<Record> originRecord = null;
                /*if (sourceSys == WebConstant.SftOsSource.LA.getKey()) {
                    log.info("======回调LA");
                    originRecord = Db.find(Db.getSql("disk_backing.selectLaRecvOriginData"), billTotalRecord.get("child_batchno"));
                }*/
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
     * 银行返回数据进行校验
     * @param bankRcord
     * @return
     */
    private Map<String,Object> handleBankData(Record bankRcord){
    	Map<String, Object> result = new HashMap<>();
        
    	//付方账号存在校验
        String payAccountNo = bankRcord.getStr("pay_account_no");
        if (null == payAccountNo || "".equals(payAccountNo.trim())) {
        	log.error("*****************下面error需人工排查************************");
            log.error("批量收付查询历史交易状态指令回写，【{}】没有返回pay_account_no，跳过该笔回写！", bankRcord);
            return null;
        }
        
        String encPayAccNo = null;
		try {
			encPayAccNo = SymmetricEncryptUtil.getInstance().encrypt(payAccountNo.trim());
		} catch (EncryAndDecryException e1) {
			log.error("*****************下面error需人工排查************************");
            log.error("批量收付查询历史交易状态指令回写，付款人账号[{}]加密失败，跳过该笔回写！", SymmetricEncryptUtil.accNoAddMask(payAccountNo.trim()));
			e1.printStackTrace();
			return null;
		}
		
        result.put("pay_account_no", encPayAccNo);
        
        //付款金额校验
        try {
			Double amount = TypeUtils.castToDouble(bankRcord.get("amount"));
			result.put("amount", amount);
		} catch (Exception e) {
			log.error("*****************下面error需人工排查************************");
			log.error("批量收付查询历史交易状态指令回写，【{}】金额转换为double失败，跳过该笔回写！", bankRcord.get("amount"));
			e.printStackTrace();
			return null;
		}
        
        String bankErrMsg = bankRcord.getStr("bank_err_msg");
        result.put("bank_err_msg", bankErrMsg.length() > 200 ? bankErrMsg.substring(0, 200) : bankErrMsg);
        
        String bankErrCode = bankRcord.getStr("bank_err_code");
        result.put("bank_err_code", bankErrCode);
        return result;
    }
    
}
