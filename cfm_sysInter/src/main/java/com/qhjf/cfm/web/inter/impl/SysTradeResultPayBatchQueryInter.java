package com.qhjf.cfm.web.inter.impl;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.impl.batch.SysBatchRecvInter;
import com.qhjf.cfm.web.inter.manager.SysInterManager;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;

public class SysTradeResultPayBatchQueryInter implements ISysAtomicInterface {

	private static Logger log = LoggerFactory.getLogger(SysTradeResultPayBatchQueryInter.class);
	private static String INSTR_TOTAL = "batch_pay_instr_queue_total";
	private static String INSTR_DETAIL = "batch_pay_instr_queue_detail";
	private static String LA_ORIGIN = "la_origin_pay_data";
	private static String EBS_ORIGIN = "ebs_origin_pay_data";
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
        Db.delete("batch_pay_instr_queue_lock", instr);

        log.debug("批量付查询历史交易状态指令回写开始。。。");
        int resultCount = channelInter.getResultCount(jsonStr);
        if (resultCount <= 0) {
            return;
        }

        //查询指令汇总
        Record instrTotalRecord = findInstrTotal(instr.getStr("bank_serial_number"));
        if (null == instrTotalRecord) {
        	log.error("批量付查询历史交易状态指令回写，通过bank_serial_number=[{}]未查询到指令！", instr.getStr("bank_serial_number"));
        	return;
		}
        final String billTable = instrTotalRecord.getStr("source_ref");
        final String billDetailTable = SysInterManager.getDetailTableName(billTable);
        final String billDetailTablePrimaryKey = SysInterManager.getSourceRefPrimaryKey(billDetailTable);
        
        for (int i = 0; i < resultCount; i++) {
            Record parseRecord = channelInter.parseResult(jsonStr, i);
            final int status = parseRecord.getInt("status");
            
            //处理银行返回数据
            final Map<String, Object> bankData = handleBankData(parseRecord);
            if (bankData == null) {
				continue;
			}
            
            if (status == WebConstant.PayStatus.SUCCESS.getKey() || status == WebConstant.PayStatus.FAILD.getKey()) {
            	//查询收款指令明细信息：通过 银行账号+金额+收款指令汇总表主键查询
                final Record instrDetailRecord = Db.findFirst(Db.getSql("batchpay.selIntrDetailByAcc")
                		, instrTotalRecord.getLong("id")
                		, TypeUtils.castToString(bankData.get("pay_account_no"))
                		, TypeUtils.castToDouble(bankData.get("amount")));
                if (null == instrDetailRecord) {
                	log.error("*****************下面error需人工排查************************");
                	bankData.put("id", instrTotalRecord.get("id"));
                	log.error("批量付查询历史交易状态指令回写，通过【{}】未查询到单据详情！", bankData);
                	continue;
				}
                
                boolean flag = Db.tx(new IAtom() {
                    @Override
                    public boolean run() throws SQLException {
                        Long detailId = TypeUtils.castToLong(instrDetailRecord.get("detail_id"));
                        Record bill_setRecord = new Record();
                        Record bill_whereRecord = new Record().set(billDetailTablePrimaryKey, detailId);

                        Record instr_setRecord = new Record();
                        Record instr_whereRecord = new Record().set("id", instrDetailRecord.getLong("id"));

                        if (status == WebConstant.PayStatus.SUCCESS.getKey()) {
                            bill_setRecord.set(SysInterManager.getStatusFiled(billDetailTable)
                                    , SysInterManager.getSuccessStatusEnum(billDetailTable));

                            instr_setRecord.set("status", SysInterManager.getSuccessStatusEnum(INSTR_DETAIL))
                                    .set("bank_back_time", new Date());
                        } else {
                            bill_setRecord.set(SysInterManager.getStatusFiled(billDetailTable), SysInterManager.getFailStatusEnum(billDetailTable))
                            			  .set("bank_err_msg", TypeUtils.castToString(bankData.get("bank_err_msg")))
                            			  .set("bank_err_code", TypeUtils.castToString(bankData.get("bank_err_code")));

                            instr_setRecord.set("status", SysInterManager.getFailStatusEnum(INSTR_DETAIL))
                            			   .set("bank_err_msg", TypeUtils.castToString(bankData.get("bank_err_msg")))
                            			   .set("bank_err_code", TypeUtils.castToString(bankData.get("bank_err_code")))
                            			   .set("bank_back_time", new Date());
                        }

                        // 1.更新单据详情状态；2.修改指令明细状态
                        if (CommonService.updateRows(billDetailTable, bill_setRecord, bill_whereRecord) == 1) {
                            boolean updDetail = CommonService.updateRows(INSTR_DETAIL, instr_setRecord, instr_whereRecord) == 1;
                            if (!updDetail) {
                                log.error("批量付状态查询回写时，[{}]更新失败！instr_setRecord=【{}】，instr_whereRecord=【{}】", INSTR_DETAIL, instr_setRecord, instr_whereRecord);
                            }
                            return true;
                        } else {
                            log.error("批量付状态查询回写时，[{}]更新失败！bill_setRecord=【{}】，bill_whereRecord=【{}】", billDetailTable, bill_setRecord, bill_whereRecord);
                            return false;
                        }
                    }
                });
                if (flag) {
					log.error("批量付状态查询回写时，回写失败，事务回滚！");
				}
            }else {
				log.debug("批量付状态查询回写时，该笔处于处理中状态【{}】", parseRecord);
			}
        }
        writeBack(instrTotalRecord);
        log.debug("批量付状态查询回写结束");

    }

    @Override
    public void callBack(Exception e) throws Exception {
        Db.delete("batch_pay_instr_queue_lock", instr);
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
        Integer count = Db.queryInt(Db.getSql("batchpay.countInstrDetailInHandling"), instrTotalId);
        if (count > 0) {
            //更新回写时间
            int upd = CommonService.updateRows(INSTR_TOTAL
                    , new Record().set("init_resp_time", new Date())
                    , new Record().set("id", instrTotal.get("id")));
            if (upd != 1) {
                log.error("批量付查询历史交易状态指令原始数据回写，不满足回写状态，更新[{}.init_resp_time]失败, updateRows=[{}]", INSTR_TOTAL, upd);
            }
            return;
        }

        //2.查询付款子批次汇总信息
        String billTotalTbName = instrTotal.getStr("source_ref");
        String billTotalTbPrimary = SysInterManager.getSourceRefPrimaryKey(billTotalTbName);
        Record billTotalRecord = Db.findById(billTotalTbName, billTotalTbPrimary, instrTotal.getLong("bill_id"));
        
        if (null == billTotalRecord) {
        	log.error("*****************下面error需人工处理************************");
            log.error("批量付查询历史交易状态指令原始数据回写，通过source_ref=[{}],bill_id=[{}]未找到单据！", billTotalTbName, instrTotal.getLong("bill_id"));
            return;
        }
        
        Integer sourceSys = SysBatchRecvInter.getSourceSys(billTotalTbName, billTotalRecord);
        if (null == sourceSys) {
        	log.error("*****************上面error需人工处理************************");
            return;
        }
        final String originTb = sourceSys == WebConstant.SftOsSource.LA.getKey() ? LA_ORIGIN : EBS_ORIGIN;

        //3.更新汇总表与原始数据
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                
            	//更新batch_pay_instr_queue_total
//                int updInstrTotal = Db.update(Db.getSql("batchrecv.updInstrTotal"), instrTotalId, instrTotalId);
                int updInstrTotal = CommonService.updateRows(INSTR_TOTAL
                		, new Record().set("status", 2)
                		, new Record().set("id", instrTotalId));
                if (updInstrTotal == 1) {
                    log.debug("批量付查询历史交易状态指令原始数据回写，更新指令汇总表成功！");
                    
                    //更新pay_batch_total
                    if (updBillTotal(instrTotalId, TypeUtils.castToLong(instrTotal.get("bill_id")), instrTotal.getStr("source_ref"))) {
                        
                        //更新la_origin_pay_data
                        int updOrigin = 0;
                        if (originTb.equals(LA_ORIGIN)) {
                            log.debug("批量付查询历史交易状态指令原始数据回写，回写LA原始数据");
                            updOrigin = Db.update(Db.getSql("batchpay.updOrginSuccLa"), instrTotalId);
                        }else {
                        	Calendar c = Calendar.getInstance();
							String date = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
							String time = new SimpleDateFormat("HH:mm:ss").format(c.getTime());
							String payBankType = instrTotal.getStr("pay_bank_type");
							String payAccountNo = instrTotal.getStr("pay_account_no");
							String paybankcode = null;
							Map<String, Object> ebsBankMapping = TableDataCacheUtil.getInstance()
									.getARowData("ebs_bank_mapping", "tmp_bank_code", payBankType);
							if (ebsBankMapping != null) {
								paybankcode = TypeUtils.castToString(ebsBankMapping.get("ebs_bank_code"));
							}else {
								paybankcode = payBankType + "未匹配到ebs数据";
							}
							updOrigin = Db.update(Db.getSql("batchpay.updOrginSuccEbs")
									, date, time, paybankcode, payAccountNo, instrTotalId);
						}
                        
                        if (updOrigin == instrTotal.getInt("total_num")) {
                            return true;
                        } else {
                            log.error("批量付查询历史交易状态指令原始数据回写，付款指令汇总信息表batch_pay_instr_queue_total.total_num与更新的原始数据条数[{}]不一致, instrTotalId={}", updOrigin, instrTotalId);
                            return false;
                        }
                    } else {
                        log.error("批量收付查询历史交易状态指令原始数据回写，更新{}失败", instrTotal.getStr("source_ref"));
                        return false;
                    }
                }
                log.error("*****************下面error需人工处理************************");
                log.error("批量收付查询历史交易状态指令原始数据回写，更新batch_pay_instr_queue_total失败，更新了多条数据！");
                return false;
            }
        });

        if (flag) {
        	log.info("批量收付查询历史交易状态指令原始数据回写，回写成功，开始回调LA。。。");
            try {
                List<Record> originRecord = null;
                if (sourceSys == WebConstant.SftOsSource.LA.getKey()) {
                    log.info("======回调LA");
                    originRecord = Db.find(Db.getSql("disk_backing.selectLaOriginData"), billTotalRecord.get("child_batchno"));
                }else {
                	log.info("======回调EBS");
                    originRecord = Db.find(Db.getSql("disk_backing.selectEbsOriginData"), billTotalRecord.get("child_batchno"));
				}
                SftCallBack callback = new SftCallBack();
                callback.callback(WebConstant.SftOsSource.LA.getKey(), originRecord);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("===============回调失败");
            }
        }else {
        	log.info("批量付查询历史交易状态指令原始数据回写，回写失败！");
		}
    }
    
    private Record findInstrTotal(String bankSerialNumber){
    	return Db.findFirst(Db.getSql("batchpay.findInstrTotal"), bankSerialNumber);
    }
    
    private boolean updBillTotal(Long instrTotalId, Long billTotalId, String sourceRef) {
        List<Record> instrDetails = Db.find(Db.getSql("batchpay.findInstrDetailByBaseId"), instrTotalId);
        int succ = 0, fail = 0;
        double succAmount = 0, failAmount = 0;
        for (Record detail : instrDetails) {
            Integer status = detail.getInt("status");
            Double amount = detail.getDouble("amount");
            if (status == 1) {
                succ++;
                succAmount += amount == null ? 0 : amount;
            }
            if (status == 2) {
                fail++;
                failAmount += amount == null ? 0 : amount;
            }
        }
        return CommonService.updateRows(sourceRef
                , new Record().set("success_num", succ)
                			  .set("success_amount", succAmount)
                			  .set("fail_num", fail)
                			  .set("fail_amount", failAmount)
                			  .set("service_status", 5)
                , new Record().set("id", billTotalId)) == 1;
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
