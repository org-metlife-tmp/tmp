package com.qhjf.cfm.web.inter.impl.batch;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.channel.inter.api.IChannelBatchInter;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.util.DateUtil;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class SysHisBalBatchQueryInter implements ISysAtomicInterface{
	
	private static Logger log = LoggerFactory.getLogger(SysHisBalBatchQueryInter.class);
	private IChannelBatchInter channelInter;
	private Record instr;

	@Override
	public Record genInstr(Record record) {
		this.instr = new Record();
		Date date = new Date();
		Date hisDate = DateUtil.getSpecifiedDayBeforeDate(date);
		
		instr.set("acc_id", record.get("acc_id"));
		instr.set("query_date", DateKit.toStr(hisDate));
		instr.set("pre_query_time", DateKit.toDate(DateKit.toStr(date,DateKit.timeStampPattern)));
		return this.instr;
	}
	
	@Override
	public Record getInstr() {
		return this.instr;
	}
	
	@Override
	public void callBack(String jsonStr) throws Exception {
		Db.delete("bal_query_instr_queue_lock", instr);
		
		int resultCount = channelInter.getResultCount(jsonStr);
		for (int i = 0; i < resultCount; i++) {
			try{
				Record parseRecord = channelInter.parseResult(jsonStr, i);
				
				String accNo = parseRecord.getStr("acc_no");
				String date = instr.getStr("query_date");
				Record accCurBal = Db.findFirst(Db.getSql("quartzs_job_cfm.get_account_his_balance_byaccno"), accNo,date);
				
				if(accCurBal == null){
					accCurBal = new Record();
					Record account = Db.findFirst(Db.getSql("quartzs_job_cfm.get_account_byaccno"), accNo);
					if(account == null){
						log.debug("账户号不存在:"+accNo);
						continue;
					}
					accCurBal.set("acc_id", account.getLong("acc_id"));
//					accCurBal.set("acc_no", account.get("acc_no"));
					accCurBal.set("acc_name", account.get("acc_name"));
					accCurBal.set("bank_type", account.getStr("bank_cnaps_code").substring(0,3));
					accCurBal.set("data_source", 1);
					accCurBal.set("bal_date", date);
					accCurBal.set("import_time",new Date());
					accCurBal.setColumns(parseRecord);
					Db.save("acc_his_balance", accCurBal);
				}else{
					accCurBal.set("import_time",new Date());
					accCurBal.setColumns(parseRecord);
					Db.update("acc_his_balance", accCurBal);
				}
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
			
		}
	}
	
	@Override
	public IChannelInter getChannelInter() {
		return this.channelInter;
	}

	@Override
	public void setChannelInter(IChannelInter channelInter) {
		this.channelInter = (IChannelBatchInter) channelInter;
		
	}

	@Override
	public void callBack(Exception e) throws Exception {
		Db.delete("bal_query_instr_queue_lock", instr);
		
	}
}

