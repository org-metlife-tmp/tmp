package com.qhjf.cfm.web.inter.impl;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.manager.SysInterManager;
import com.qhjf.cfm.web.webservice.sft.SftRecvCallBack;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 批收交易状态查询
 * 
 * @author CHT
 *
 */
public class SysBatchRecvStatusQueryInter implements ISysAtomicInterface {

	private static Logger log = LoggerFactory.getLogger(SysBatchRecvStatusQueryInter.class);
	private static final String BILL_NOT_EXIST = "批量收回写时，通过source_ref=[{}],bill_id=[{}]未找到单据！";
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

		log.debug("批量收交易状态指令查询回写开始。。。");
		int resultCount = channelInter.getResultCount(jsonStr);
		log.debug("批量收交易状态指令查询回写，查回条数size = {}", resultCount);
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
			log.debug("批量收交易状态指令查询回写，回写第{}条，bankServiceNumber={}", i, bankServiceNumber);

			Record intrTotal = Db.findFirst(Db.getSql("batchrecv.qryIntrTotalByBSN"), bankServiceNumber);
			if (null == intrTotal) {
				log.debug("批量收交易状态指令查询回写，通过bank_service_number={}查询instrTotal为空", bankServiceNumber);
				continue;
			}
			Integer sta = TypeUtils.castToInt(intrTotal.get("reqsta"));
			if (sta != null && sta == 1) {
				log.debug("批量收交易状态指令查询回写,reqsta已回写，reqsta=1");
				continue;
			}
			
			Record setRecord = new Record();
			String oldReqnbr = intrTotal.getStr("reqnbr");
			if (StringUtils.isBlank(oldReqnbr)) {
				setRecord.set("reqnbr", reqnbr);
			} else if (!oldReqnbr.equals(reqnbr)) {
				throw new Exception("批量收交易状态指令查询回写，返回得reqnbr与批收指令发送时，银行返回得reqnbr不一样，请联系银行处理！");
			}
			
			setRecord.set("reqsta", reqsta);
			setRecord.set("rtnflg", rtnflg);
			setRecord.set("init_resp_time", new Date());
			setRecord.set("bank_err_msg", bankErrMsg);
			
			//交易失败，回写原始数据（概要信息不回写，查询明细的时候查询）
			/*if (reqsta == 1 && rtnflg == 1) {
				if (writeBack(intrTotal, setRecord)) {
					continue;
				}
			}*/
			
			boolean update = CommonService.update("batch_recv_instr_queue_total"
					, setRecord
					, new Record().set("bank_serial_number", bankServiceNumber).set("status", 1));
			if (!update) {
				log.error("批量收付交易状态指令查询回写，更新指令汇总表失败bank_serial_number=", bankServiceNumber);
			}
		}
		
		log.debug("批量收付交易状态指令查询回写结束。。。");
	}
	
	/**
	 * 回写原始数据
	 */
	private boolean writeBack(Record instrTotal, final Record instrTotalSetRecord){
		log.debug("交易失败，直接回写原始数据！");
		if (!"308".equals(instrTotal.getStr("recv_bank_cnaps").substring(0,3))) {
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
		
		//2.更新汇总表与原始数据
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				
				//1.更新batch_recv_instr_queue_total
				if( CommonService.updateRows("batch_recv_instr_queue_total"
						, new Record().setColumns(instrTotalSetRecord.getColumns()).set("status", 2)
						, new Record().set("id", instrTotalId)) != 1){
					log.error("批量收交易状态指令查询回写，交易失败回写，intrTotal更新失败！");
					return false;
				}
				
				//2.更新batch_recv_instr_queue_detail
				if(!CommonService.update("batch_recv_instr_queue_detail"
						, new Record().set("status", 2).set("bank_err_msg", "交易失败").set("bank_back_time", new Date())
						, new Record().set("base_id", instrTotalId))){
					log.error("批量收交易状态指令查询回写，交易失败回写，intrDetail更新失败！");
					return false;
				}
				
				//3.更新recv_batch_detail
				if (!CommonService.update(SysInterManager.getDetailTableName(billTotalTb)
						, new Record().set("status", 2).set("bank_err_msg", instrTotalSetRecord.getStr("bank_err_msg"))
						, new Record().set("base_id", billTotalId))) {
					log.error("批量收交易状态指令查询回写，交易失败回写，billDetail更新失败！");
					return false;
				}
				
				//4.更新recv_batch_total
				if (Db.update(Db.getSql("batchrecv.updBillTotalToFail"), billTotalId) != 1) {
					log.error("批量收交易状态指令查询回写，交易失败回写，billTotal更新失败！");
					return false;
				}
				
				//5.更新la_origin_recv_data
				SqlPara updOriginFailLaSqlPara = Db.getSqlPara("batchrecv.updOriginFailLa");
				if (Db.update(updOriginFailLaSqlPara.getSql(), instrTotalId) <= 0) {
					log.error("批量收交易状态指令查询回写，交易失败回写，laOrigin更新失败！");
					return false;
				}
				return true;
			}
		});
		
		
		if (flag) {
			try {
				List<Record> originRecord = null;
				log.info("======回调LA");
				originRecord = Db.find(Db.getSql("disk_backing.selectLaRecvOriginData"),
						billTotalRecord.get("child_batchno"));
				SftRecvCallBack callback = new SftRecvCallBack();
				callback.callback(WebConstant.SftOsSource.LA.getKey(), originRecord);
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
