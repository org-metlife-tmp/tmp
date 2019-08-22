package com.qhjf.cfm.web.inter.impl;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.inter.api.ISingleResultChannelInter;
import com.qhjf.cfm.web.channel.util.DateUtil;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

public class SysHisBalQueryInter implements ISysAtomicInterface{
	
	private static Logger log = LoggerFactory.getLogger(SysHisBalQueryInter.class);
	private ISingleResultChannelInter channelInter;
	private Record instr;

	@Override
	public Record genInstr(Record record) {
		this.instr = new Record();
		Date date = new Date();
		Date hisDate = DateUtil.getSpecifiedDayBeforeDate(date);
		instr.set("acc_id", record.getLong("acc_id"));
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
		if(jsonStr == null || jsonStr.length() == 0){
        	log.error("历史余额查询返回报文为空,不错处理");
        	return;
        }
		Long accId = instr.getLong("acc_id");
		String date = instr.getStr("query_date");
		Record accHisBal = Db.findFirst(Db.getSql("quartzs_job_cfm.get_account_his_balance"), accId,date);
		Record parseRecord = channelInter.parseResult(jsonStr);
		if(accHisBal == null){
			accHisBal = new Record();
			Record account = Db.findFirst(Db.getSql("quartzs_job_cfm.get_account"), accId);
			if(account == null){
				log.debug("账户id不存在:"+accId);
				return;
			}
			accHisBal.set("acc_id", accId);
			accHisBal.set("acc_no", account.get("acc_no"));
			accHisBal.set("acc_name", account.get("acc_name"));
			accHisBal.set("bank_type", account.getStr("bank_cnaps_code").substring(0,3));
			accHisBal.set("data_source", 1);
			accHisBal.set("bal_date", date);
			accHisBal.set("import_time",new Date());
			accHisBal.setColumns(parseRecord);
			Db.save("acc_his_balance", accHisBal);
		}else{
			accHisBal.set("import_time",new Date());
			accHisBal.setColumns(parseRecord);
			Db.update("acc_his_balance", accHisBal);
		}
		
	}
	
	@Override
	public IChannelInter getChannelInter() {
		return this.channelInter;
	}

	@Override
	public void setChannelInter(IChannelInter channelInter) {
		this.channelInter = (ISingleResultChannelInter) channelInter;
		
	}

	@Override
	public void callBack(Exception e) throws Exception {
		Db.delete("bal_query_instr_queue_lock", instr);
		
	}
}

