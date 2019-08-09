package com.qhjf.cfm.web.quartzs.jobs.pub;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.utils.OminiUtils;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.config.CFMQuartzConfigSection;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public abstract class PubJob implements Job{

	private static Logger log = LoggerFactory.getLogger(PubJob.class);
	private ExecutorService executeService;
	private Future<String> future = null;
	private JobExecutionContext context;
	
	/**
	 * 获取线程池内线程数量,默认10,可被子类重写
	 * @return
	 */
	protected int getThreadPool(){
		return 10;
	}
	
	/**
	 * 插入锁表失败后,是否需要重试,默认false,可被子类重写
	 * @return
	 */
	public boolean needReTrySaveInstr(){
		return false;
	}

	/**
	 * 如果needReTrySaveInstr()返回true,调用此方法查询是否有相同记录,违反唯一性约束,可被子类重写
	 * @param currInstr
	 * @return
	 */
	public Record getOldInstr(Record currInstr){
		return null;
	}
	
	/**
	 * 生成发送到队列的bean,默认用sourceRecord生成,可被子类重写
	 * @param inter
	 * @param instrRecord
	 * @param sourceRecord
	 * @return
	 */
	public  Map<String,Object> getParams(IChannelInter inter,Record instrRecord,Record sourceRecord){
		return inter.genParamsMap(sourceRecord);
	}
	
	/**
	 * 获取任务的ontext,子类不能重写
	 * @return
	 */
	public final JobExecutionContext getContext(){
		return this.context;
	}
	
	/**
	 * 调用call()方法之前的业务处理,默认不做处理,可被子类重写
	 * @param record
	 */
	public Boolean beforeProcess(Record record){
		//默认失败
		return true ;
	}
	
	/**
	 * 调用call()方法之后的业务处理,默认不做处理,可被子类重写
	 * @param record
	 */
	public void afterProcess(Record record){
	}
	
	/**
	 * 获取任务编号
	 * @return
	 */
	public abstract String getJobCode();
	
	/**
	 * 获取任务名称
	 * @return
	 */
	public abstract String getJobName();
	
	/**
	 * 获取任务的log
	 * @return
	 */
	public abstract Logger getLog();
	
	/**
	 * 获取任务的原数据
	 * @return
	 * @throws JobExecutionException
	 */
	public abstract List<Record> getSourceDataList() throws JobExecutionException;
	
	/**
	 * 获取锁表名称
	 * @return
	 */
	public abstract String getInstrTableName();
	
	/**
	 * 获取系统接口
	 * @return
	 */
	public abstract ISysAtomicInterface getSysInter();
	
	

	/**
	 * 执行任务
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		getLog().debug(getJobName()+"任务开始");
		this.context = context;
		this.executeService = Executors.newFixedThreadPool(getThreadPool());
		List<Record> records = getSourceDataList();
		if(records != null && records.size() > 0){
			log.debug(records.size()+"");
			for(Record record : records){
				this.future = executeService.submit(new ExecuteThread(record));
			}
			executeService.shutdown(); // 关闭线程池,不在接收新的任务
			while (true) {
				if (future.isDone()) {
					break;
				}
			}
		}
		getLog().debug(getJobName()+"任务结束");

	}


	public class ExecuteThread implements Callable<String> {

		private Record record;

		public ExecuteThread(Record record) {
			this.record = record;
		}

		@Override
		public String call() throws Exception {
			try {
				Boolean flag = beforeProcess(record);
				if(!flag){
					return "fail";
				}
				String cnaps = record.getStr("bank_cnaps_code");
				Record account = Db.findFirst(Db.getSql("batchrecv.findInstrTotal"),record.getStr("bank_serial_number"));
				Record channel = Db.findFirst(Db.getSql("batchrecv.qryChannel"),account.getStr("recv_account_no"));
				String bankCode = "";
				if(!OminiUtils.isNullOrEmpty(channel)){
					Record dircet = Db.findFirst(Db.getSql("batchrecv.qryChannelSet"),channel.getStr("channel_id"));
					cnaps = dircet.getStr("shortPayCnaps");
				}
				if(cnaps.startsWith("fingard")){
					bankCode = cnaps;
				} else {
					bankCode = cnaps.substring(0, 3);
				}
				IChannelInter channelInter = ChannelManager.getInter(bankCode, getJobCode());
				ISysAtomicInterface sysInter = getSysInter();
				sysInter.setChannelInter(channelInter);
				Record instr = sysInter.genInstr(record);
				try{
					if(Db.save(getInstrTableName(), instr)){
						if("102".equals(cnaps)){
							List<Record> bankList = Db.find(Db.getSql("batchrecvjob.getBankunPack"),record.getStr("bank_serial_number"));
							if(!OminiUtils.isNullOrEmpty(bankList) && bankList.size() > 0){
								for (Record re : bankList){
									if(!OminiUtils.isNullOrEmpty(re.getStr("bank_serial_number_unpack"))){
										record.set("bank_serial_number",re.getStr("bank_serial_number_unpack"));
										sendQueue(sysInter,record,bankCode);
									} else {
										sendQueue(sysInter,record,bankCode);
									}
								}
							} else {
								sendQueue(sysInter,record,bankCode);
							}
						} else {
							sendQueue(sysInter,record,bankCode);
						}
					}else{
						reTry(sysInter,bankCode);
					}
				}catch(Exception e){
					reTry(sysInter,bankCode);
				}
				afterProcess(record);
				return "success";
			} catch (Exception e) {
				e.printStackTrace();
				return "fail";
			}
		}
		
		/**
		 * 重试插入指令表
		 * @param sysInter

		 * @throws Exception
		 */
		private void reTry(ISysAtomicInterface sysInter,String bankCode) throws Exception{
			if(needReTrySaveInstr()){
				Record oldLockRecord = getOldInstr(sysInter.getInstr());
				if(oldLockRecord != null){
					Date oldDate = oldLockRecord.getDate("pre_query_time");
					Date currDate = sysInter.getInstr().getDate("pre_query_time");
					log.debug("oldDate="+oldDate);
					log.debug("currDate="+currDate);
					log.debug("oldDateTime="+oldDate.getTime());
					log.debug("currDateTime()="+currDate.getTime());
					log.debug("maxTime="+CFMQuartzConfigSection.getInstance().getMaxTime());
					if(currDate.getTime()-oldDate.getTime()> CFMQuartzConfigSection.getInstance().getMaxTime()*1000*60){
						Db.delete(getInstrTableName(), oldLockRecord);
						if(Db.save(getInstrTableName(), sysInter.getInstr())){
							sendQueue(sysInter,record,bankCode);
						}
						
					}
				}
				return;
			}
			throw new Exception("插入数据库失败");
			
		}
		
		/**
		 * 发送队列
		 * @param sysInter
		 * @param sourceRecord
		 * @param sourceRecord
		 */
		private void sendQueue(ISysAtomicInterface sysInter,Record sourceRecord,String bankCode){
			QueueBean bean = new QueueBean(sysInter,getParams(sysInter.getChannelInter(),sysInter.getInstr(),sourceRecord),bankCode);
			ProductQueue productQueue = new ProductQueue(bean);
			new Thread(productQueue).start();
		}
		

	}

}
