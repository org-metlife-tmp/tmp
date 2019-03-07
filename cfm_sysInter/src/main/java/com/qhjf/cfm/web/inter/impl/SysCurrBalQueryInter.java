package com.qhjf.cfm.web.inter.impl;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.inter.api.ISingleResultChannelInter;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class SysCurrBalQueryInter implements ISysAtomicInterface{
	
	private static Logger log = LoggerFactory.getLogger(SysCurrBalQueryInter.class);
	private ISingleResultChannelInter channelInter;
	private Record instr;
	
	@Override
	public Record genInstr(Record record) {
		this.instr = new Record();
		Date date = new Date();
		instr.set("acc_id", record.getLong("acc_id"));
		instr.set("query_date", DateKit.toStr(date));
		instr.set("pre_query_time", DateKit.toDate(DateKit.toStr(date,DateKit.timeStampPattern)));
		return instr;
	}
	
	@Override
	public Record getInstr() {
		return this.instr;
	}

	@Override
	public void callBack(String jsonStr) throws Exception {
		Db.delete("bal_query_instr_queue_lock", instr);
		if(jsonStr == null || jsonStr.length() == 0){
        	log.error("当日余额查询返回报文为空,不错处理");
        	return;
        }
		Long accId = instr.getLong("acc_id");
		String date = instr.getStr("query_date");
		Record accCurBal = Db.findFirst(Db.getSql("quartzs_job_cfm.get_account_cur_balance") ,accId,date);
		Record parseRecord = channelInter.parseResult(jsonStr);
		if(accCurBal == null){
			accCurBal = new Record();
			Record account = Db.findFirst(Db.getSql("quartzs_job_cfm.get_account"), accId);
			if(account == null){
				log.debug("账户id不存在:"+accId);
				return;
			}
			accCurBal.set("acc_id", accId);
			accCurBal.set("acc_no", account.get("acc_no"));
			accCurBal.set("acc_name", account.get("acc_name"));
			accCurBal.set("bank_type", account.getStr("bank_cnaps_code").substring(0,3));
			accCurBal.set("data_source", 1);
			accCurBal.set("bal_date", date);
			accCurBal.set("import_time",new Date());
			accCurBal.setColumns(parseRecord);
			Db.save("acc_cur_balance", accCurBal);
		}else{
			accCurBal.set("import_time",new Date());
			accCurBal.setColumns(parseRecord);
			Db.update("acc_cur_balance", accCurBal);
		}
		accCurBal.remove("id");
		Db.save("acc_cur_balance_wave", accCurBal);
		
	}

	@Override
	public void setChannelInter(IChannelInter channelInter) {
		this.channelInter = (ISingleResultChannelInter) channelInter;
		
	}

	@Override
	public IChannelInter getChannelInter() {
		return this.channelInter;
	}

	@Override
	public void callBack(Exception e) throws Exception {
		Db.delete("bal_query_instr_queue_lock", instr);
	}
}
