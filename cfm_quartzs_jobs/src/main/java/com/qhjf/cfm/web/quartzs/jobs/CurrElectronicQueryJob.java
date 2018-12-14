package com.qhjf.cfm.web.quartzs.jobs;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.channel.util.DateUtil;
import com.qhjf.cfm.web.config.CMBCTestConfigSection;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.impl.SysElectronicImgQueryInter;
import com.qhjf.cfm.web.inter.impl.SysElectronicQueryInter;
import com.qhjf.cfm.web.quartzs.jobs.pub.PubJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

public class CurrElectronicQueryJob extends PubJob{
	
	private static Logger log = LoggerFactory.getLogger(HisElectronicQueryJob.class);

	private static CMBCTestConfigSection configSection =CMBCTestConfigSection.getInstance();

	@Override
	public String getJobCode() {
		return "CurrElectronicQuery";
	}
	@Override
	public String getJobName() {
		return "当日电子回单查询";
	}
	@Override
	public Logger getLog() {
		return log;
	}
	@Override
	public String getInstrTableName() {
		return "electronic_bill_query_lock";
	}
	@Override
	public Record getOldInstr(Record currInstr) {
		return Db.findFirst(Db.getSql("quartzs_job_cfm.get_electronic_lock"), currInstr.getLong("acc_id"),currInstr.getStr("start_date"),currInstr.getStr("end_date"));
	}
	@Override
	public List<Record> getSourceDataList() {
		return Db.find(Db.getSql("quartzs_job_cfm.get_account_list"));
	}
	@Override
	public ISysAtomicInterface getSysInter() {
		return new SysElectronicQueryInter();
	}
	@Override
	public boolean needReTrySaveInstr(){
		return true;
	}
	
	@Override
	public void afterProcess(Record record){
		SysElectronicImgQueryInter imgInter = new SysElectronicImgQueryInter();
		String cnaps = record.getStr("bank_cnaps_code");
		String bankCode = cnaps.substring(0, 3);
		IChannelInter channelInter = null;
		try {
			channelInter = ChannelManager.getInter(bankCode, "ElectronicImgQuery");
		} catch (Exception e) {
			e.printStackTrace();
			log.debug("发送查询电子回单图片请求失败");
		}
		imgInter.setChannelInter(channelInter);
		int preDay = configSection.getPreDay();
		String date = DateUtil.getSpecifiedDayBefore(new Date(), preDay, "yyyyMMdd");
		record.set("beginDate", date);
		record.set("endDate", date);
		QueueBean bean = new QueueBean(imgInter,channelInter.genParamsMap(record),bankCode);
		ProductQueue productQueue = new ProductQueue(bean);
		new Thread(productQueue).start();
	}
}

