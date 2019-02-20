package com.qhjf.cfm.web.inter.impl.batch;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.IAtom;
import com.qhjf.bankinterface.api.exceptions.BankInterfaceException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.constant.WebConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.web.channel.inter.api.IChannelBatchInter;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.manager.SysInterManager;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;

public class SysBatchPayInter implements ISysAtomicInterface {

	private static Logger log = LoggerFactory.getLogger(SysBatchPayInter.class);
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
	private static final String INSTR_TOTAL_NOT_EXIST = "批量支付回写时，通过bank_serial_number=[%s],detail_bank_service_number=[%s],package_seq=[%s]未找到相应的付款指令汇总信息！";
	private static final String INSTR_DETAIL_NOT_EXIST = "批量支付回写时，通过bank_serial_number=[%s],detail_bank_service_number=[%s],package_seq=[%s]未找到相应的付款指令明细信息！";

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
		double amount = 0;
		List<Record> detailList = new ArrayList<>();
		for (Record batch : batchList) {
			//汇总每笔金额
			Double batchAmount = batch.getDouble("amount");
			amount +=  batchAmount==null ? 0 : batchAmount;
			
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
		log.debug("交易回写开始...");
		int resultCount = channelInter.getResultCount(jsonStr);
		if (resultCount == 0) { }
		
		//是否进行回写batch_pay_instr_queue_total，pay_batch_total，la_origin_pay_data|ebs_origin_pay_data
		//需要在回写时进行双重判断，明细是否已经全部为：成功/失败
		boolean isWriteBack = true;
		
		//处理中的单笔支付，回写时公用变量
		final Record setRecord = new Record();
		setRecord.set("init_resp_time", new Date());
		
		Record instrTotalRecord = null;
		String sourceRef = null;
		final String billTable[] = new String[1];
		final String billTablePrimaryKey[] = new String[1];
		
		for (int i = 0; i < resultCount; i++) {
			try {
				final Record parseRecord = channelInter.parseResult(jsonStr, i);
				final int status = parseRecord.getInt("status");
				
				String bankServiceNumber = parseRecord.getStr("bank_serial_number");
				String detailBankServiceNumber = parseRecord.getStr("detail_bank_service_number");
				String packageSeq = parseRecord.getStr("package_seq");
				if (null == bankServiceNumber && null == detailBankServiceNumber) {
					log.error("批量支付回写时，第[{}]笔即没有返回bank_serial_number，又没有返回detail_bank_service_number，跳过该笔回写！", i+1);
					continue;
				}
				
				if (null == instrTotalRecord) {
					//查询付款指令汇总信息表
					instrTotalRecord = findInstrTotal(bankServiceNumber, detailBankServiceNumber, packageSeq);
					sourceRef = instrTotalRecord.getStr("source_ref");
					billTable[0] = SysInterManager.getDetailTableName(sourceRef);
					billTablePrimaryKey[0] = SysInterManager.getSourceRefPrimaryKey(billTable[0]);
				}
				
				// 成功、失败处理逻辑
				if (status == WebConstant.PayStatus.SUCCESS.getKey()
						|| status == WebConstant.PayStatus.FAILD.getKey()) {
					//1.查询付款指令明细信息
					final Record instrDetailRecord = findInstryDetail(bankServiceNumber, detailBankServiceNumber, packageSeq);
					
					Db.tx(new IAtom() {
						@Override
						public boolean run() throws SQLException {
							Long detailId = TypeUtils.castToLong(instrDetailRecord.get("detail_id"));
							Record bill_setRecord = new Record();
							Record bill_whereRecord = new Record().set(billTablePrimaryKey[0], detailId);

							Record instr_setRecord = new Record();
							Record instr_whereRecord = new Record().set("id", instrDetailRecord.getLong("id"));

							if (status == WebConstant.PayStatus.SUCCESS.getKey()) {
								bill_setRecord.set( SysInterManager.getStatusFiled(billTable[0])
										, SysInterManager.getSuccessStatusEnum(billTable[0]));
								
								instr_setRecord.set("status", SysInterManager.getSuccessStatusEnum(BATCH_PAY_INSTR_DETAIL_TALBE));
							} else {
								bill_setRecord.set( SysInterManager.getStatusFiled(billTable[0])
										, SysInterManager.getFailStatusEnum(billTable[0]));
								
								instr_setRecord.set("status", SysInterManager.getFailStatusEnum(BATCH_PAY_INSTR_DETAIL_TALBE))
												.set("bank_err_code", parseRecord.getInt("code"))
												.set("bank_err_msg", parseRecord.getInt("message"));
							}

							// 1.更新单据状态；2.修改队列表状态
							if (CommonService.updateRows(billTable[0], bill_setRecord, bill_whereRecord) == 1) { // 修改单据状态
								boolean updDetail = CommonService.updateRows(BATCH_PAY_INSTR_DETAIL_TALBE, instr_setRecord, instr_whereRecord) == 1;
								if (!updDetail) {
									log.error("批量支付回写时，[{}]更新失败！instr_setRecord=【{}】，instr_whereRecord=【{}】", BATCH_PAY_INSTR_DETAIL_TALBE, instr_setRecord, instr_whereRecord);
								}
								return true;
							} else {
								log.error("批量支付回写时，[{}]更新失败！bill_setRecord=【{}】，bill_whereRecord=【{}】", billTable[0], bill_setRecord, bill_whereRecord);
								return false;
							}
						}
					});
				
				}else {
					//处理中
					isWriteBack = false;
				}
			} catch (Exception e) {
				isWriteBack = false;
				log.error("批量支付回调,第【i={}】次循环交易回写失败，回调数据：【{}】", i, jsonStr);
				e.printStackTrace();
				continue;
			}
		}
		
		if (isWriteBack) {
			//单笔回写完毕，检查是否具备回写汇总表条件，具备则回写
			writeBack(instrTotalRecord);
		}else {
			//更新回写时间
			updWriteBackTime(instrTotalRecord);
		}

		log.debug("批量支付交易回写结束");
	}

	@Override
	public void callBack(final Exception e) throws Exception {
		/**
		 * 如果是底层抛出的BankInterfaceException ，明确为失败 如果是其他的异常，打印日志，不进行处理
		 */
		if (e instanceof BankInterfaceException) {

			Record instrTotal = (Record)this.instr.get("total");
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
			final String originTb = sourceSys == WebConstant.SftOsSource.LA.getKey() ? LA_ORIGIN : EBS_ORIGIN;
			
			boolean flag = Db.tx(new IAtom() {
				@Override
				public boolean run() throws SQLException {
					
					//1.更新batch_pay_instr_queue_total
					if( CommonService.updateRows(BATCH_PAY_INSTR_TOTLE_TALBE
							, new Record().set("status", 2).set("bank_err_msg", "发送失败")
							, new Record().set("id", instrTotalId)) != 1){
						return false;
					}
					//2.更新batch_pay_instr_queue_detail
					if(!CommonService.update(BATCH_PAY_INSTR_DETAIL_TALBE
							, new Record().set("status", 2).set("bank_err_msg", "发送失败")
							, new Record().set("base_id", instrTotalId))){
						return false;
					}
					//3.更新pay_batch_detail
					if (!CommonService.update(SysInterManager.getDetailTableName(billTotalTb)
							, new Record().set("status", 2)
							, new Record().set("base_id", billTotalId))) {
						return false;
					}
					//4.更新pay_batch_total
					if (Db.update(Db.getSql("batchpay.updBillTotalToFail"), billTotalTb) != 1) {
						return false;
					}
					//5.更新la_origin_pay_data|ebs_origin_pay_data
					SqlPara updOriginFailSqlPara = Db.getSqlPara("batchpay.updOriginFail", Kv.by("tb", originTb));
					if (Db.update(updOriginFailSqlPara.getSql(), instrTotalId) <= 0) {
						return false;
					}
					return true;
				}
			});
			if (!flag) {
				log.error("批量支付回写时，修改单据状态失败！");
			}else {
				try {
					Record originRecord = null;
					if (sourceSys == WebConstant.SftOsSource.LA.getKey()) {
						log.info("======回调LA");
						originRecord = Db.findFirst(Db.getSql("disk_backing.selectLaOriginData"), billTotalRecord.get("child_batchno"));
					} else if (sourceSys == WebConstant.SftOsSource.EBS.getKey()) {
						log.info("======回调EBS");
						originRecord = Db.findFirst(Db.getSql("disk_backing.selectEbsOriginData"), billTotalRecord.get("child_batchno"));
					}
					SftCallBack callback = new SftCallBack();
					callback.callback(sourceSys, originRecord);
				} catch (Exception e1) {
					e1.printStackTrace();
					log.error("===============回调失败");
				}
			}
		} else {
			log.error(e.getMessage());
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
	public static Record findInstrTotal(String bankServiceNumber, String detailBankServiceNumber, String packageSeq) throws Exception{
		Record r = null;
		if (null != bankServiceNumber) {
			r = Db.findFirst(Db.getSql("batchpay.findInstrTotal"), bankServiceNumber);
		}else {
			Kv kv = Kv.create();
			if (null != detailBankServiceNumber) {
				kv.set("detail_bank_service_number", detailBankServiceNumber);
			}
			if (null != packageSeq) {
				kv.set("package_seq", packageSeq);
			}
			SqlPara sqlPara = Db.getSqlPara("batchpay.findInstrTotalByDetail", Kv.by("map", kv));
			r = Db.findFirst(sqlPara);
		}
		
		if (null == r) {
			throw new Exception(String.format(INSTR_TOTAL_NOT_EXIST, bankServiceNumber, detailBankServiceNumber, packageSeq));
		}
		return r;
	}
	/**
	 * 查询付款指令明细信息表
	 */
	public static Record findInstryDetail(String bankServiceNumber, String detailBankServiceNumber, String packageSeq) throws Exception{
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
	}
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
			bankSerialNumber =  ChannelManager.getChannelInfo(payBankCode).getSerialnoGenTool().next();
		} catch (Exception e1) {
			e1.printStackTrace();
			return null;
		}
		total.set("bank_serial_number", bankSerialNumber);
		total.set("repeat_count", r.getInt("repeat_count") == null ? 1 : r.getInt("repeat_count"));
		String sourceRef = r.getStr("source_ref");
		if (sourceRef == null || sourceRef.trim().equals("")) {
			return null;
		}
		total.set("source_ref", sourceRef);
		total.set("bill_id", r.get("bill_id"));
		total.set("total_num", size);
//		total.set("total_amount", );
		total.set("pay_account_no", r.get("pay_account_no"));
		total.set("pay_account_name", r.get("pay_account_name"));
		total.set("pay_account_cur", r.get("pay_account_cur"));
		total.set("pay_account_bank", r.get("pay_account_bank"));
		total.set("pay_bank_cnaps", r.get("pay_bank_cnaps"));
		total.set("pay_bank_prov", r.get("pay_bank_prov"));
		total.set("pay_bank_city", r.get("pay_bank_city"));
		total.set("pay_bank_type", r.get("pay_bank_type"));
		total.set("status", 3);
		total.set("trade_date", DateKit.toStr(new Date(), "yyyy-MM-dd"));
		//发送时间不精确，从指令发送队列取指令时的时间更精确
		total.set("init_send_time", new Date());
		return total;
	}
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
			detailBankServiceNumber =  ChannelManager.getChannelInfo(payBankCode).getSerialnoGenTool().next();
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
		detail.set("recv_bank_type", batch.getBigDecimal("recv_bank_type"));
		
		int isCrossBank = 0;
		String cnaps = batch.getStr("recv_bank_cnaps");
		if (null != cnaps && cnaps.length() > 3 && !payBankCode.equals(cnaps.substring(0, 3))) {
			isCrossBank = 1;
		}
		detail.set("is_cross_bank", isCrossBank);
		detail.set("status", 3);
		return detail;
	}
	/**
	 * 单笔回写完毕，检查是否具备回写汇总表条件，具备则回写
	 */
	private void writeBack(final Record instrTotal){
		final Long instrTotalId = TypeUtils.castToLong(instrTotal.get("id"));
		//1.检查是否满足回写条件
		Integer count = Db.queryInt(Db.getSql("batchpay.countInstrDetailInHandling"), instrTotalId);
		if (count > 0) {
			return;
		}
		
		//2.查询付款子批次汇总信息 对应的原始数据表名
		String billTotalTbName = instrTotal.getStr("source_ref");
		Record billTotalRecord = getBillTotalRecord(billTotalTbName, instrTotal.getLong("bill_id"));
		if (null == billTotalRecord) {
			log.error(BILL_NOT_EXIST, billTotalTbName, instrTotal.getLong("bill_id"));
			return;
		}
		Integer sourceSys = getSourceSys(billTotalTbName, billTotalRecord);
		if (null == sourceSys) {
			return;
		}
		final String originTb = sourceSys == WebConstant.SftOsSource.LA.getKey() ? LA_ORIGIN : EBS_ORIGIN;
		//3.更新汇总表与原始数据
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				//3.1.更新batch_pay_instr_queue_total：明细中有一条成功，汇总就更新为成功
				int updInstrTotal = Db.update(Db.getSql("batchpay.updInstrTotal"), instrTotalId, instrTotalId);
				if (updInstrTotal == 1) {
					//3.2.更新pay_batch_total
					if (updBillTotal(instrTotalId, TypeUtils.castToLong(instrTotal.get("bill_id")), instrTotal.getStr("source_ref"))) {
						//3.3.更新la_origin_pay_data|ebs_origin_pay_data
						SqlPara updOrginLaSqlPara = Db.getSqlPara("batchpay.updOrginLa", Kv.by("tb", originTb));
						int updOrigin = Db.update(updOrginLaSqlPara.getSql(), instrTotalId);
						if (updOrigin == instrTotal.getInt("total_num")) {
							return true;
						}else {
							log.error("批量支付回写时，付款指令汇总信息表batch_pay_instr_queue_total.total_num与更新的原始数据条数[{}]不一致, instrTotalId={}", updOrigin, instrTotalId);
							return false;
						}
					}else {
						log.error("批量支付回写时，更新{}失败", instrTotal.getStr("source_ref"));
						return false;
					}
				}
				log.error("批量支付回写时，更新batch_pay_instr_queue_total失败！");
				return false;
			}
		});
		
		
		if (flag) {
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
			} catch (Exception e) {
				e.printStackTrace();
				log.error("===============回调失败");
			}
		}
	}
	/**
	 * 查询单据表对应的原始数据表
	 */
	public static String getOriginTb(String billTotalTb, Long billTotalId){
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
	}
	public static Record getBillTotalRecord(String billTotalTb, Long billTotalId){
		Record billTotalRec = Db.findById(billTotalTb
				, SysInterManager.getSourceRefPrimaryKey(billTotalTb)
				, billTotalId);
		return billTotalRec;
	}

	public static Integer getSourceSys(String billTotalTb, Record billTotalRec) {
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
	 * 更新单据批次汇总表
	 */
	public static boolean updBillTotal(Long instrTotalId, Long billTotalId, String sourceRef){
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
				, new Record().set("success_num", succ).set("success_amount", succAmount).set("fail_num", fail).set("fail_amount", failAmount)
				, new Record().set("id", billTotalId)) == 1;
	}
	/**
	 * 更新batch_pay_instr_queue_total.init_resp_time
	 */
	private void updWriteBackTime(Record instrTotal){
		int upd = CommonService.updateRows(BATCH_PAY_INSTR_TOTLE_TALBE
				, new Record().set("init_resp_time", new Date())
				, new Record().set("id", instrTotal.get("id")));
		if(upd != 1){
			log.error("批量支付回写时，更新[{}.init_resp_time]失败, updateRows=[{}]", BATCH_PAY_INSTR_TOTLE_TALBE, upd);
		}
	}
}
