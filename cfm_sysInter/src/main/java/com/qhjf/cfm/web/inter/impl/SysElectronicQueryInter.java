package com.qhjf.cfm.web.inter.impl;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SysElectronicQueryInter implements ISysAtomicInterface{
	
	private static Logger log = LoggerFactory.getLogger(SysElectronicQueryInter.class);
	private IMoreResultChannelInter channelInter;
	private ExecutorService executeService = Executors.newFixedThreadPool(10);
	private Future<String> future = null;
	private Record instr;

	@Override
	public Record genInstr(Record record) {
		this.instr = new Record();
		Date date = new Date();
		instr.set("acc_id", record.getLong("acc_id"));
		instr.set("start_date", DateKit.toStr(date));
		instr.set("end_date", DateKit.toStr(date));
		instr.set("pre_query_time", DateKit.toDate(DateKit.toStr(date,DateKit.timeStampPattern)));
		return this.instr;
	}

	@Override
	public Record getInstr() {
		return this.instr;
	}

	@Override
	public void callBack(String jsonStr) throws Exception {
		Db.delete("electronic_bill_query_lock", instr);
		if(jsonStr == null || jsonStr.length() == 0){
        	log.error("电子回单查询返回报文为空,不错处理");
        	return;
        }
		int resultCount = channelInter.getResultCount(jsonStr);
		log.debug("resultCount"+resultCount);
		if(resultCount <= 0){
			return;
		}

		for(int i = 0;i < resultCount;i++){
			Record parseRecord = channelInter.parseResult(jsonStr, i);
			this.future = executeService.submit(new ExecuteThread(parseRecord));
		}
		executeService.shutdown(); // 关闭线程池,不在接收新的任务
		while (true) {
			if (future.isDone()) {
				break;
			}
		}
		
	}

	@Override
	public void callBack(Exception e) throws Exception {
		Db.delete("electronic_bill_query_lock", instr);
		
	}

	@Override
	public void setChannelInter(IChannelInter channelInter) {
		this.channelInter = (IMoreResultChannelInter) channelInter;
	}

	@Override
	public IChannelInter getChannelInter() {
		return this.channelInter;
	}

	public class ExecuteThread implements Callable<String> {

		private Record parseRecord;

		public ExecuteThread(Record parseRecord) {
			this.parseRecord = parseRecord;
		}

		@Override
		public String call() throws Exception {
			try {
				Record electronic = new Record();
				electronic.setColumns(parseRecord);
				StringBuffer inedtifierPlain = new StringBuffer();
				inedtifierPlain.append(electronic.getStr("field_1")).append("@");
				inedtifierPlain.append(electronic.getStr("field_2")).append("@");
				inedtifierPlain.append(electronic.getStr("field_3")).append("@");
				inedtifierPlain.append(electronic.getStr("field_4")).append("@");
				inedtifierPlain.append(electronic.getStr("field_5")).append("@");
				inedtifierPlain.append(electronic.getStr("field_6")).append("@");
				inedtifierPlain.append(electronic.getStr("field_7")).append("@");
				inedtifierPlain.append(electronic.getStr("field_8")).append("@");
				inedtifierPlain.append(electronic.getStr("field_9")).append("@");
				inedtifierPlain.append(electronic.getStr("field_10")).append("@");
				inedtifierPlain.append(electronic.getStr("field_11")).append("@");
				inedtifierPlain.append(electronic.getStr("field_12")).append("@");
				inedtifierPlain.append(electronic.getStr("field_13")).append("@");
				inedtifierPlain.append(electronic.getStr("field_14")).append("@");
				inedtifierPlain.append(electronic.getStr("field_15")).append("@");
				inedtifierPlain.append(electronic.getStr("field_16")).append("@");
				inedtifierPlain.append(electronic.getStr("field_17")).append("@");
				inedtifierPlain.append(electronic.getStr("field_18")).append("@");
				inedtifierPlain.append(electronic.getStr("field_19")).append("@");
				inedtifierPlain.append(electronic.getStr("field_20")).append("@");
				inedtifierPlain.append(electronic.getStr("field_21")).append("@");
				inedtifierPlain.append(electronic.getStr("field_22")).append("@");
				inedtifierPlain.append(electronic.getStr("field_23")).append("@");
				inedtifierPlain.append(electronic.getStr("field_24")).append("@");
				inedtifierPlain.append(electronic.getStr("field_25")).append("@");
				inedtifierPlain.append(electronic.getStr("field_26")).append("@");
				inedtifierPlain.append(electronic.getStr("field_27")).append("@");
				inedtifierPlain.append(electronic.getStr("field_28")).append("@");
				inedtifierPlain.append(electronic.getStr("field_29")).append("@");
				inedtifierPlain.append(electronic.getStr("field_30")).append("@");
				inedtifierPlain.append(electronic.getStr("field_31")).append("@");
				inedtifierPlain.append(electronic.getStr("field_32")).append("@");
				inedtifierPlain.append(electronic.getStr("field_33")).append("@");
				inedtifierPlain.append(electronic.getStr("field_34")).append("@");
				inedtifierPlain.append(electronic.getStr("field_35")).append("@");
				inedtifierPlain.append(electronic.getStr("field_36")).append("@");
				inedtifierPlain.append(electronic.getStr("field_37")).append("@");
				inedtifierPlain.append(electronic.getStr("field_38")).append("@");
				inedtifierPlain.append(electronic.getStr("field_39")).append("@");
				inedtifierPlain.append(electronic.getStr("field_40")).append("@");
				inedtifierPlain.append(electronic.getStr("field_41")).append("@");
				inedtifierPlain.append(electronic.getStr("field_42")).append("@");
				inedtifierPlain.append(electronic.getStr("field_43")).append("@");
				inedtifierPlain.append(electronic.getStr("field_44")).append("@");
				inedtifierPlain.append(electronic.getStr("field_45")).append("@");
				inedtifierPlain.append(electronic.getStr("field_46")).append("@");
				inedtifierPlain.append(electronic.getStr("field_47")).append("@");
				inedtifierPlain.append(electronic.getStr("field_48")).append("@");
				inedtifierPlain.append(electronic.getStr("field_49")).append("@");
				inedtifierPlain.append(electronic.getStr("field_50"));
				
				String identify = MD5Kit.string2MD5(inedtifierPlain.toString());
				electronic.set("identify", identify);
				Db.save("electronic_bill", electronic);
				return "success";
			} catch (Exception e) {
				e.printStackTrace();
				return "fail";
			}
		}
		

	}
}
