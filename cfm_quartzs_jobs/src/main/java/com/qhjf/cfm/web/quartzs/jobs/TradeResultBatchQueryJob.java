package com.qhjf.cfm.web.quartzs.jobs;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.impl.SysTradeResultBatchQueryInter;
import com.qhjf.cfm.web.quartzs.jobs.pub.PubJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Deprecated
public class TradeResultBatchQueryJob extends PubJob{
	
	private static Logger log = LoggerFactory.getLogger(TradeResultBatchQueryJob.class);
	@Override
	public String getJobCode() {
		return "TradeResultBatchQuery";
	}
	@Override
	public String getJobName() {
		return "批量交易状态查询";
	}
	@Override
	public Logger getLog() {
		return log;
	}
	@Override
	public String getInstrTableName() {
		//批量收付查询状态指令表
		return "trade_result_query_batch_instr_queue_lock";
	}
	@Override
	public Record getOldInstr(Record currInstr) {
		String cnaps = currInstr.getStr("pay_bank_cnaps");
		String packageSeq = currInstr.getStr("package_seq");
		String detailBankServiceNumber = currInstr.getStr("detail_bank_service_number");
		//工行是批量查询，
		if (cnaps.startsWith("102")) {
			detailBankServiceNumber = "0";
			packageSeq = "0";
		}
		return Db.findFirst(Db.getSql("batchpayjob.getOldBatchTrade")
				, currInstr.getStr("bank_serial_number")
				, detailBankServiceNumber
				, packageSeq);
	}
	/**
	 * 招行先单笔查询;
	 * 批量查询还未验证，验证通过在这里修改，通过修改CmbcTradeResultBatchQueryInter.genParamsMap
	 */
	@Override
	public List<Record> getSourceDataList() {
		List<Record> result = null;
		
		//Db.find的入参为查询多少个处理中的批次
		List<Record> sourceList = Db.find(Db.getSql("batchpayjob.getTradeResultBatchQrySourceList"), 10);
		
		if (null != sourceList && sourceList.size() > 0) {
			result = new ArrayList<>();
			
			Long instrTotalId = null;
			for (Record source : sourceList) {
				String cnaps = source.getStr("pay_bank_cnaps");
				//工行直接取instrTotal
				if (cnaps.startsWith("102")) {
					if (null != instrTotalId) {
						if (instrTotalId.equals(source.getLong("base_id"))) {
							continue;
						}
						instrTotalId = source.getLong("base_id");
						result.add(source);
					}else {
						instrTotalId = source.getLong("base_id");
						result.add(source);
					}
				}
				
				//招行取instrDetail
				if (cnaps.startsWith("308")) {
					result.add(source);
				}
			}
		}
		
		return result;
	}
	@Override
	public ISysAtomicInterface getSysInter() {
		return new SysTradeResultBatchQueryInter();
	}
	@Override
	public boolean needReTrySaveInstr(){
		return true;
	}
	
}