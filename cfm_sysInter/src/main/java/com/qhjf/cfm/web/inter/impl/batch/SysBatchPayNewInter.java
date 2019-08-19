package com.qhjf.cfm.web.inter.impl.batch;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.bankinterface.api.exceptions.BankInterfaceException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.CommKit;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.channel.inter.api.IChannelBatchInter;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.manager.SysInterManager;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SysBatchPayNewInter implements ISysAtomicInterface {

	private static Logger log = LoggerFactory.getLogger(SysBatchPayNewInter.class);
	private IChannelBatchInter channelInter;
	
	private Record instr;
	
	/**
	 * 付款指令汇总信息表
	 */
	public static final String BATCH_PAY_INSTR_TOTLE_TALBE = "batch_pay_instr_queue_total";
	/**
	 * 付款指令明细信息表
	 */
	public static final String BATCH_PAY_INSTR_DETAIL_TALBE = "batch_pay_instr_queue_detail";
	/**
	 * LA原始付款数据表：批量支付完结时回写该表
	 */
	public static final String LA_ORIGIN = "la_origin_pay_data";
	/**
	 * EBS原始付款数据：批量支付完结时回写该表
	 */
	public static final String EBS_ORIGIN = "ebs_origin_pay_data";
	
	private static final String BILL_NOT_EXIST = "批量支付回写时，通过source_ref=[{}],bill_id=[{}]未找到单据！";
//	private static final String INSTR_TOTAL_NOT_EXIST = "批量支付回写时，通过bank_serial_number=[%s],detail_bank_service_number=[%s],package_seq=[%s]未找到相应的付款指令汇总信息！";
//	private static final String INSTR_DETAIL_NOT_EXIST = "批量支付回写时，通过bank_serial_number=[%s],detail_bank_service_number=[%s],package_seq=[%s]未找到相应的付款指令明细信息！";

	@Override
	public void setChannelInter(IChannelInter channelInter) {
		this.channelInter = (IChannelBatchInter) channelInter;
	}

	@Override
	public IChannelInter getChannelInter() {
		return this.channelInter;
	}
	/**
	 * 生成支付指令表数据
	 * 需要对返回结果判空处理
	 */
	@Override
	public Record genInstr(Record record) {

		@SuppressWarnings("unchecked")
		List<Record> batchList = (List<Record>) record.get("list");
		if (null == batchList || batchList.size() == 0) {
			log.error("批量支付的单据列表为空！");
			return null;
		}

		String payCnaps = batchList.get(0).getStr("pay_bank_cnaps");
		String payBankCode = payCnaps.substring(0, 3);
		
		//生成付款指令汇总信息
		Record total = genInstrTotal(batchList.get(0), batchList.size(), payBankCode);
		if (total == null) {
			return null;
		}
		BigDecimal amount = new BigDecimal(0);
		List<Record> detailList = new ArrayList<>();
		for (Record batch : batchList) {
			//汇总每笔金额
			BigDecimal batchAmount = new BigDecimal(batch.getDouble("amount") == null ? "0" : Double.toString(batch.getDouble("amount")));
			amount = amount.add(batchAmount);
			
			//生成付款指令明细信息
			Record detail = genInstrDetail(batch, total.getStr("bank_serial_number"), payBankCode);
			if (detail == null) {
				return null;
			}
			detailList.add(detail);
		}
		
		total.set("total_amount", amount);

		this.instr = new Record();
		//付款指令汇总信息
		this.instr.set("total", total);
		//付款指令明细信息
		this.instr.set("detail", detailList);
		return this.instr;
	}

	@Override
	public Record getInstr() {
		return this.instr;
	}

	public void setInnerInstr(Record record) {
		this.instr = record;
	}

	/**
	 * 银行处理 成功的回调
	 */
	@Override
	public void callBack(String jsonStr) throws Exception {
		CommKit.debugPrint(log,"批付交易回写开始...jsonStr={}",jsonStr);
		int resultCount = channelInter.getResultCount(jsonStr);
		if (resultCount == 0) {
			log.error("银行返回数据条数为0");
			return;
		}
		
		Record instrTotal = (Record) this.instr.get("total");
        Long instrTotalId = TypeUtils.castToLong(instrTotal.get("id"));
        
        Record instrTotalRecord = Db.findById(BATCH_PAY_INSTR_TOTLE_TALBE, "id", instrTotalId);
        if (instrTotalRecord == null) {
            throw new Exception("批付单据发送指令不存在！id=" + instrTotalId);
        }
        
        for (int i = 0; i < resultCount; i++) {
            try {
                final Record parseRecord = channelInter.parseResult(jsonStr, i);
                
                if (instrTotalRecord.getStr("pay_bank_cnaps").startsWith("308")) {
                	String oldReqnbr = instrTotalRecord.getStr("reqnbr");
                	if (oldReqnbr == null || "".equals(oldReqnbr.trim())) {
                		//招行该字段叫：流程实例号
                        String reqnbr = parseRecord.getStr("reqnbr");
                    	//更新指令汇总表的‘流程实例号’
                    	Record instrTotalSet = new Record();
                    	instrTotalSet.set("init_resp_time", new Date());
                    	instrTotalSet.set("reqnbr", reqnbr);
                    	
                    	Record instrTotalWhere = new Record();
                    	instrTotalWhere.set("id", instrTotalId);
                    	
                    	int updIntrTotal = CommonService.updateRows(BATCH_PAY_INSTR_TOTLE_TALBE
									                    			, instrTotalSet
									                    			, instrTotalWhere);
                    	if (updIntrTotal != 1) {
    						log.error("*****************************需要人工处理***************************");
    						log.error("******需要人工处理：批付指令银行响应的‘流程实例号’回写指令汇总表失败！reqnbr={}*****", reqnbr);
    						log.error("*****************************需要人工处理***************************");
    					}
					}else {
						log.error("批量付回调, 指令汇总表的reqnbr字段已经回写，又再次回写，请人工核查原因 ！");
					}
				}
                
            } catch (Exception e) {
                CommKit.errorPrint(log,"批量付回调,第【i={}】次循环交易回写失败，回调数据：【{}】",jsonStr);
                e.printStackTrace();
                continue;
            }
        }

		log.debug("批量支付交易回写结束");
	}

	@Override
	public void callBack(final Exception e) throws Exception {
		/**
         * 如果是底层抛出的BankInterfaceException ，明确为失败 如果是其他的异常，打印日志，不进行处理
         */
        if (e instanceof BankInterfaceException) {

            Record instrTotal = (Record) this.instr.get("total");
            final Long instrTotalId = TypeUtils.castToLong(instrTotal.get("id"));
            final String billTotalTb = instrTotal.getStr("source_ref");
            final Long billTotalId = instrTotal.getLong("bill_id");

            Record billTotalRecord = getBillTotalRecord(billTotalTb, billTotalId);
            if (null == billTotalRecord) {
                log.error(BILL_NOT_EXIST, billTotalTb, billTotalId);
                return;
            }
            
            Integer sourceSys = getSourceSys(billTotalTb, billTotalRecord);
            if (null == sourceSys) {
                return;
            }
            
            final String originTb = sourceSys == WebConstant.SftOsSource.LA.getKey() ? LA_ORIGIN : EBS_ORIGIN;;
            
            boolean flag = Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {

                    //1.更新batch_pay_instr_queue_total
                    if (CommonService.updateRows(BATCH_PAY_INSTR_TOTLE_TALBE
                            , new Record().set("status", 2).set("bank_err_msg", "发送失败").set("init_resp_time", new Date())
                            , new Record().set("id", instrTotalId)) != 1) {
                    	log.error("批付发送银行指令失败回写，更新指令汇总表失败");
                        return false;
                    }
                    
                    //2.更新batch_pay_instr_queue_detail
                    if (!CommonService.update(BATCH_PAY_INSTR_DETAIL_TALBE
                            , new Record().set("status", 2).set("bank_err_msg", "发送失败").set("bank_back_time", new Date())
                            , new Record().set("base_id", instrTotalId))) {
                    	log.error("批付发送银行指令失败回写，更新指令明细表失败");
                        return false;
                    }
                    
                    //3.更新pay_batch_detail：通过外键更新
                    if (!CommonService.update(SysInterManager.getDetailTableName(billTotalTb)
                            , new Record().set("status", 2)
                            , new Record().set("base_id", billTotalId))) {
                    	log.error("批付发送银行指令失败回写，更新子批次明细表失败");
                        return false;
                    }
                    
                    //4.更新pay_batch_total
                    if (Db.update(Db.getSql("batchpay.updBillTotalToFail"), billTotalId) != 1) {
                    	log.error("批付发送银行指令失败回写，更新子批次汇总表失败");
                        return false;
                    }
                    
                    //5.更新la_origin_pay_data
                    SqlPara updOriginFailLaSqlPara = null;
                    if (LA_ORIGIN.equals(originTb)) {
                        updOriginFailLaSqlPara = Db.getSqlPara("batchpay.updOriginFailLa");
                    }else {
                    	updOriginFailLaSqlPara = Db.getSqlPara("batchpay.updOriginFailEbs");
					}
                    int updOrigin = Db.update(updOriginFailLaSqlPara.getSql(), instrTotalId);
                    if (updOrigin <= 0) {
                    	log.error("批付发送银行指令失败回写，更新原始数据表失败，instrTotalId={}", instrTotalId);
                        return false;
                    }
                    log.debug("批付发送银行指令失败回写，更新原始数据表条数：{},instrTotalId={}", updOrigin, instrTotalId);
                    
                    return true;
                }
            });
            
            if (!flag) {
                log.error("批收发送银行指令失败回写，修改指令、单据、原始数据状态失败！");
            } else {
                try {
                	List<Record> originRecord = null;
    				if (sourceSys == WebConstant.SftOsSource.LA.getKey()) {
    					log.info("======回调LA");
    					originRecord = Db.find(Db.getSql("disk_backing.selectLaOriginData"), billTotalRecord.get("child_batchno"));
    				} else if (sourceSys == WebConstant.SftOsSource.EBS.getKey()) {
    					log.info("======回调EBS");
    					originRecord = Db.find(Db.getSql("disk_backing.selectEbsOriginData"), billTotalRecord.get("child_batchno"));
    				}
    				SftCallBack callback = new SftCallBack();
    				callback.callback(sourceSys, originRecord);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    log.error("===============批付发送银行指令失败回写，回调失败");
                }
            }
        } else {
        	log.error(e.getMessage());
        	log.error("*****************************需要人工处理***************************");
			log.error("******需要人工处理：批付银行指令发送异常，需要人工跟银行确认批收指令接收状态，已接受需要询问流程实例号，并手工插入指令汇总表*****");
			log.error("*****************************需要人工处理***************************");
        }
	}

	/**
	 * 保存付款指令
	 */
	public boolean saveIntr() {
		Record total = (Record)this.instr.get("total");
		//保存：付款指令汇总信息表
		boolean save = Db.save(BATCH_PAY_INSTR_TOTLE_TALBE, total);
		
		if (save) {
			//向付款指令明细信息表反写 上一步生成的汇总表主键
			@SuppressWarnings("unchecked")
			List<Record> detail = (List<Record>)this.instr.get("detail");
			for (Record record : detail) {
				record.set("base_id", total.get("id"));
			}
			
			int[] batchSave = Db.batchSave(BATCH_PAY_INSTR_DETAIL_TALBE, detail, 1000);
			return ArrayUtil.checkDbResult(batchSave);
		}
		return false;
	}
	/**
	 * 通过：bank_serial_number、detail_bank_service_number、package_seq查询付款指令汇总信息表
	 */
	/*public static Record findInstrTotal(String bankServiceNumber, String detailBankServiceNumber, String packageSeq) throws Exception{
		Record r = null;
		if (null != bankServiceNumber) {
			r = Db.findFirst(Db.getSql("batchpay.findInstrTotal"), bankServiceNumber);
		}else {
			Kv kv = Kv.create();
			if (null != detailBankServiceNumber) {
				kv.set("detailBankServiceNumber", detailBankServiceNumber);
			}
			if (null != packageSeq) {
				kv.set("packageSeq", packageSeq);
			}
			SqlPara sqlPara = Db.getSqlPara("batchpay.findInstrTotalByDetail", Kv.by("map", kv));
			r = Db.findFirst(sqlPara);
		}
		
		if (null == r) {
			throw new Exception(String.format(INSTR_TOTAL_NOT_EXIST, bankServiceNumber, detailBankServiceNumber, packageSeq));
		}
		return r;
	}*/
	/**
	 * 查询付款指令明细信息表
	 */
	/*public static Record findInstryDetail(String bankServiceNumber, String detailBankServiceNumber, String packageSeq) throws Exception{
		Kv kv = Kv.create();
		if (null != bankServiceNumber) {
			kv.set("bankServiceNumber", bankServiceNumber);
		}
		if (null != detailBankServiceNumber) {
			kv.set("detailBankServiceNumber", detailBankServiceNumber);
		}
		if (null != packageSeq) {
			kv.set("packageSeq", packageSeq);
		}
		SqlPara sqlPara = Db.getSqlPara("batchpay.findInstrDetail", Kv.by("map", kv));
		Record r = Db.findFirst(sqlPara);
		if (null == r) {
			throw new Exception(String.format(INSTR_DETAIL_NOT_EXIST, bankServiceNumber, detailBankServiceNumber, packageSeq));
		}
		return r;
	}*/
	/**
	 * 生成付款指令汇总信息表 batch_pay_instr_queue_total
	 * @param r
	 * @return
	 */
	private Record genInstrTotal(Record r, int size, String payBankCode){
		Record total = new Record();
		//生成批量指令的银行流水号
		String bankSerialNumber = null;
		try {
			bankSerialNumber =  ChannelManager.getSerianlNo(payBankCode);
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		total.set("bank_serial_number", bankSerialNumber);
		total.set("repeat_count", r.getInt("repeat_count") == null ? 1 : r.getInt("repeat_count"));
		String sourceRef = r.getStr("source_ref");
		if (sourceRef == null || sourceRef.trim().equals("")) {
			log.error("批付指令生成汇总表数据时，source_ref为空！");
			return null;
		}
		total.set("source_ref", sourceRef);
		total.set("bill_id", r.get("bill_id"));
		total.set("total_num", size);
		total.set("pay_account_no", r.get("pay_account_no"));
		total.set("pay_account_name", r.get("pay_account_name"));
		total.set("pay_account_cur", r.get("pay_account_cur"));
		total.set("pay_account_bank", r.get("pay_account_bank"));
		total.set("pay_bank_cnaps", r.get("pay_bank_cnaps"));
		total.set("pay_bank_prov", r.get("pay_bank_prov"));
		total.set("pay_bank_city", r.get("pay_bank_city"));
		total.set("pay_bank_type", r.get("pay_bank_type"));
		total.set("status", 1);
		total.set("trade_date", DateKit.toStr(new Date(), "yyyy-MM-dd"));
		//发送时间不精确，从指令发送队列取指令时的时间更精确
		total.set("init_send_time", new Date());
		return total;
	}
	/**
	 * 生成发送日期
	 * @param cnaps
	 * @return
	 */
	/*private String genTradeDate(String cnaps){
		String result = null;
		if ("308".equals(cnaps)) {
			int preDay = configSection.getPreDay();
			result = DateUtil.getSpecifiedDayAfter(new Date(), preDay, "yyyyMMdd");
		}
		return result == null ? DateKit.toStr(new Date(), "yyyy-MM-dd") : result;
	}*/
	/**
	 * 生成付款指令明细信息 ： batch_pay_instr_queue_detail
	 */
	private Record genInstrDetail(Record batch, String bankSerialNumber, String payBankCode){
		Record detail = new Record();
		
		//detail_id：用于回写子批次明细时，关联用
		detail.set("detail_id", batch.get("id"));
		detail.set("bank_serial_number", bankSerialNumber);
		
		String detailBankServiceNumber = null;
		try {
			detailBankServiceNumber =  ChannelManager.getSerianlNo(payBankCode);
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		detail.set("detail_bank_service_number", detailBankServiceNumber);
		detail.set("package_seq", batch.get("package_seq"));
		detail.set("amount", batch.get("amount"));
		
		detail.set("recv_account_no", batch.getStr("recv_account_no"));
		detail.set("recv_account_name", batch.getStr("recv_account_name"));
		detail.set("recv_account_cur", batch.getStr("recv_account_cur"));
		detail.set("recv_account_bank", batch.getStr("recv_account_bank"));
		detail.set("recv_bank_cnaps", batch.getStr("recv_bank_cnaps"));
		detail.set("recv_bank_prov", batch.getStr("recv_bank_prov"));
		detail.set("recv_bank_city", batch.getStr("recv_bank_city"));
		detail.set("recv_bank_type", batch.getStr("recv_bank_type"));
		
		int isCrossBank = 0;
		String cnaps = batch.getStr("recv_bank_cnaps");
		if (null != cnaps && cnaps.length() > 3 && !payBankCode.equals(cnaps.substring(0, 3))) {
			isCrossBank = 1;
		}
		detail.set("is_cross_bank", isCrossBank);
		detail.set("status", 3);
		return detail;
	}
	
	private Record getBillTotalRecord(String billTotalTb, Long billTotalId){
		Record billTotalRec = Db.findById(billTotalTb
				, SysInterManager.getSourceRefPrimaryKey(billTotalTb)
				, billTotalId);
		return billTotalRec;
	}

	private Integer getSourceSys(String billTotalTb, Record billTotalRec) {
		// 原始数据来源 0:LA;1:EBS
		final Integer sourceSys = billTotalRec.getInt("source_sys");
		if (null == sourceSys || (sourceSys != WebConstant.SftOsSource.LA.getKey()
				&& sourceSys != WebConstant.SftOsSource.EBS.getKey())) {
			log.error("批量支付回写时，单据汇总表[{}]的数据来源[source_sys]=[{}]为空或不合法", billTotalTb, sourceSys);
			return null;
		}
		return sourceSys;
	}
	/**
	 * 查询单据表对应的原始数据表
	 */
	/*public static String getOriginTb(String billTotalTb, Long billTotalId){
		Record billTotalRec = getBillTotalRecord(billTotalTb, billTotalId);
		if (null == billTotalRec) {
			log.error(BILL_NOT_EXIST, billTotalTb, billTotalId);
			return null;
		}
		Integer sourceSys = getSourceSys(billTotalTb, billTotalRec);
		if (null == sourceSys) {
			return null;
		}
		return sourceSys == WebConstant.SftOsSource.LA.getKey() ? LA_ORIGIN : EBS_ORIGIN;
	}*/
	
	/**
	 * 更新单据批次汇总表
	 */
	/*private boolean updBillTotal(Long instrTotalId, Long billTotalId, String sourceRef){
		List<Record> instrDetails = Db.find(Db.getSql("batchpay.findInstrDetailByBaseId"), instrTotalId);
		int succ=0, fail=0;
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
							  .set("back_on", new Date())
				, new Record().set("id", billTotalId)) == 1;
	}*/
	/**
	 * 更新batch_pay_instr_queue_total.init_resp_time
	 */
	/*private void updWriteBackTime(Record instrTotal){
		int upd = CommonService.updateRows(BATCH_PAY_INSTR_TOTLE_TALBE
				, new Record().set("init_resp_time", new Date())
				, new Record().set("id", instrTotal.get("id")));
		if(upd != 1){
			log.error("批量支付回写时，更新[{}.init_resp_time]失败, updateRows=[{}]", BATCH_PAY_INSTR_TOTLE_TALBE, upd);
		}
	}*/
}
