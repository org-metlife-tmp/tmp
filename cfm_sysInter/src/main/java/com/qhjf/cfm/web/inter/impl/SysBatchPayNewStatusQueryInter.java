package com.qhjf.cfm.web.inter.impl;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.config.PlfConfigAccnoSection;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.manager.SysInterManager;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;

/**
 * 批付交易状态查询
 * 
 * @author CHT
 *
 */
public class SysBatchPayNewStatusQueryInter implements ISysAtomicInterface {

	private static Logger log = LoggerFactory.getLogger(SysBatchPayNewStatusQueryInter.class);
	PlfConfigAccnoSection section = PlfConfigAccnoSection.getInstance();
	private static final String LA_ORIGIN = "la_origin_pay_data";
	private static final String EBS_ORIGIN = "ebs_origin_pay_data";
	private static final String BILL_NOT_EXIST = "批量支付回写时，通过source_ref=[{}],bill_id=[{}]未找到单据！";
	private IMoreResultChannelInter channelInter;
	private Record instr;

	@Override
	public Record genInstr(Record record) {
		this.instr = new Record();
		// 1:批付；2：批收
		instr.set("biz_type", record.get("biz_type"));
		instr.set("begin_date", record.getStr("begin_date"));
		instr.set("end_date", record.getStr("end_date"));
		instr.set("process_bank_type", record.getStr("process_bank_type"));
		instr.set("trade_date", record.get("trade_date"));
		instr.set("pre_query_time", DateKit.toDate(DateKit.toStr(new Date(), DateKit.timeStampPattern)));
		return this.instr;
	}

	@Override
	public Record getInstr() {
		return this.instr;
	}

	@Override
	public void callBack(String jsonStr) throws Exception {
		Db.delete("batch_pay_recv_status_queue_lock", instr);

		log.debug("批量付交易状态指令查询回写开始。。。");
		int resultCount = channelInter.getResultCount(jsonStr);
		if (resultCount <= 0) {
			return;
		}

		for (int i = 0; i < resultCount; i++) {
			final Record parseRecord = channelInter.parseResult(jsonStr, i);
			String reqnbr = parseRecord.getStr("reqnbr");
			Integer reqsta = parseRecord.getInt("reqsta");
			Integer rtnflg = parseRecord.getInt("rtnflg");
			String bankErrMsg = parseRecord.getStr("bank_err_msg");
			String bankServiceNumber = parseRecord.getStr("bank_service_number");
			log.debug("批量付交易状态指令查询回写，bankServiceNumber={}", bankServiceNumber);

			Record intrTotal = Db.findFirst(Db.getSql("batchpay.qryIntrTotalByBSN"), bankServiceNumber);
			if (null == intrTotal) {
				log.error("批量付交易状态指令查询回写，通过bank_service_number={}查询instrTotal为空", bankServiceNumber);
				continue;
			}
			Integer sta = TypeUtils.castToInt(intrTotal.get("reqsta"));
			if (sta != null && sta == 1) {
				log.debug("批量付交易状态指令查询回写,reqsta已回写，reqsta=1");
				continue;
			}
			
			Record setRecord = new Record();
			String oldReqnbr = intrTotal.getStr("reqnbr");
			if (StringUtils.isBlank(oldReqnbr)) {
				setRecord.set("reqnbr", reqnbr);
			} else if (!oldReqnbr.equals(reqnbr)) {
				throw new Exception("批量付交易状态指令查询回写，返回得reqnbr与批付指令发送时，银行返回得reqnbr不一样，请联系银行处理！");
			}
			
			setRecord.set("reqsta", reqsta);
			setRecord.set("rtnflg", rtnflg);
			setRecord.set("init_resp_time", new Date());
			setRecord.set("bank_err_msg", bankErrMsg);
			
			//交易失败，回写原始数据
			if (reqsta == 1 && rtnflg == 1) {
				if (writeBack(intrTotal, setRecord)) {
					continue;
				}
			}
			
			boolean update = CommonService.update("batch_pay_instr_queue_total"
					, setRecord
					, new Record().set("bank_serial_number", bankServiceNumber).set("status", 1));
			if (!update) {
				log.error("批量付交易状态指令查询回写，更新指令汇总表失败bank_serial_number=", bankServiceNumber);
			}
		}
		
		log.debug("批量付交易状态指令查询回写结束。。。");
	}
	
	/**
	 * 回写原始数据
	 */
	private boolean writeBack(Record instrTotal, final Record instrTotalSetRecord){
		log.debug("交易失败，直接回写原始数据！");
		if (!"308".equals(instrTotal.getStr("pay_bank_cnaps").substring(0,3))) {
			return false;
		}
		
		//1.查询付款子批次汇总信息 对应的原始数据表名
		final Long instrTotalId = TypeUtils.castToLong(instrTotal.get("id"));
		final String billTotalTb = instrTotal.getStr("source_ref");
		final Long billTotalId = instrTotal.getLong("bill_id");
		
		Record billTotalRecord = getBillTotalRecord(billTotalTb, billTotalId);
		if (null == billTotalRecord) {
			log.error(BILL_NOT_EXIST, billTotalTb, billTotalId);
			return false;
		}
		Integer sourceSys = getSourceSys(billTotalTb, billTotalRecord);
		if (null == sourceSys) {
			return false;
		}
		final String originTb = sourceSys == WebConstant.SftOsSource.LA.getKey() ? LA_ORIGIN : EBS_ORIGIN;
		
		//2.更新汇总表与原始数据
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				//1.更新batch_pay_instr_queue_total
				if( CommonService.updateRows("batch_pay_instr_queue_total"
						, new Record().setColumns(instrTotalSetRecord.getColumns()).set("status", 2)
						, new Record().set("id", instrTotalId)) != 1){
					return false;
				}
				//2.更新batch_pay_instr_queue_detail
				if(!CommonService.update("batch_pay_instr_queue_detail"
						, new Record().set("status", 2).set("bank_err_msg", "交易失败").set("bank_back_time", new Date())
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
				if (LA_ORIGIN.equals(originTb)) {
					SqlPara updOriginFailLaSqlPara = Db.getSqlPara("batchpay.updOriginFailLa");
					if (Db.update(updOriginFailLaSqlPara.getSql(), instrTotalId) <= 0) {
						return false;
					}
				}else {
					//需求变更：ebs加四个非空字段，回传EBS：paydate 支付日期|paytime 支付时间|paybankcode 大都会支付银行编码 （需要做mapping）|paybankaccno 大都会支付银行账号
					//paybankcode来源：拿到PlfConfigAccnoSection配置中配置的银行账号，查询account表，得到bankcode	
					SqlPara updOriginFailEbsSqlPara = Db.getSqlPara("batchpay.updOriginFailEbs");
					Calendar c = Calendar.getInstance();
					String date = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
					String time = new SimpleDateFormat("HH:mm:ss").format(c.getTime());
					
					String accno = section.getAccno();
					Map<String, Object> aRowData = TableDataCacheUtil.getInstance().getARowData("account", "acc_no", accno);
					String paybankcode = null;
					if (null != aRowData) {
						paybankcode = TypeUtils.castToString(aRowData.get("bankcode"));
					}else {
						paybankcode = String.format("银行账号：(%s)未维护到account表", accno);
					}
					
					if (Db.update(updOriginFailEbsSqlPara.getSql(), date, time, paybankcode, accno, instrTotalId) <= 0) {
						return false;
					}
				}
				return true;
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
			return true;
		}else {
			return false;
		}
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

	@Override
	public void callBack(Exception e) throws Exception {
		Db.delete("batch_pay_recv_status_queue_lock", instr);
	}

	@Override
	public void setChannelInter(IChannelInter channelInter) {
		this.channelInter = (IMoreResultChannelInter) channelInter;
	}

	@Override
	public IChannelInter getChannelInter() {
		return this.channelInter;
	}

}
