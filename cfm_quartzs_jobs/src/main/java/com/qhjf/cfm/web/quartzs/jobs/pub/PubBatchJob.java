package com.qhjf.cfm.web.quartzs.jobs.pub;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.web.channel.inter.api.IChannelBatchInter;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.config.CFMQuartzConfigSection;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 批量指令作业公共业务
 *
 */
public abstract class PubBatchJob implements Job {
	private static Logger log = LoggerFactory.getLogger(PubBatchJob.class);
	private ExecutorService executeService;
	private Future<String> future;
	private JobExecutionContext context;
	//支持批量指令的银行cnaps号前三位的数组
	protected String cnaps[];
	private static final String JOB_PARAM_KEY = "cnaps";
	/**
	 * 获取线程池内线程数量,默认10,可被子类重写
	 * @return
	 */
	protected int getThreadPool(){
		return 10;
	}

	/**
	 * 生成发送到队列的bean,默认用sourceRecord生成,可被子类重写
	 * @param inter
	 * @param sourceRecord
	 * @return
	 */
	public Map<String, Object> getParams(IChannelInter inter, Record sourceRecord) {
		return inter.genParamsMap(sourceRecord);
	}
	
	/**
	 * 获取任务的context,子类不能重写
	 * @return
	 */
	public final JobExecutionContext getContext(){
		return this.context;
	}
	
	/**
	 * 调用call()方法之前的业务处理,默认不做处理,可被子类重写
	 * @param record
	 */
	public void beforeProcess(List<Record> record){
	}
	
	/**
	 * 调用call()方法之后的业务处理,默认不做处理,可被子类重写
	 * @param record
	 */
	public void afterProcess(List<Record> record){
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
	 * 获取支持批量指令的银行cnaps号前三位的数组
	 * @throws JobExecutionException 
	 */
	public void getSupportChannel() throws JobExecutionException{
		JobDataMap jobDataMap = getContext().getMergedJobDataMap();
		String cnapsParam = jobDataMap.getString(JOB_PARAM_KEY);
		log.debug(String.format("[%s]param参数为：[%s]", getJobName(), cnapsParam));
		if (StringUtils.isBlank(cnapsParam)) {
			throw new JobExecutionException(String.format("[%s]job参数有误", getJobName()));
		}
		if (cnapsParam.indexOf(",")!=-1 || cnapsParam.indexOf("，")!=-1) {
			cnapsParam = cnapsParam.replaceAll("，", ",");
			this.cnaps = cnapsParam.split(",");
		}else if(cnapsParam.indexOf("|") != -1){
			this.cnaps = cnapsParam.split("\\|");
		}else if(cnapsParam.indexOf("#") != -1){
			this.cnaps = cnapsParam.split("#");
		}else if(cnapsParam.indexOf("$") != -1){
			this.cnaps = cnapsParam.split("\\$");
		}else if(cnapsParam.indexOf("%") != -1){
			this.cnaps = cnapsParam.split("%");
		}else if(cnapsParam.indexOf("-") != -1){
			this.cnaps = cnapsParam.split("-");
		}else if(cnapsParam.indexOf("+") != -1){
			this.cnaps = cnapsParam.split("\\+");
		}else{
			this.cnaps = cnapsParam.split(",");
		}
	}
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		getLog().debug(getJobName()+"批量任务开始");
		this.context = context;
		this.executeService = Executors.newFixedThreadPool(1);
		List<Record> records = getSourceDataList();
		if(records != null && records.size() > 0){
			log.debug(String.format("该次批量任务中指令条数:%s", records.size()));
			
			this.future = executeService.submit(new ExecuteThread(records));
			executeService.shutdown();
			while (true) {
				if (this.future.isDone()) {
					break;
				}
			}
		}
		getLog().debug(getJobName()+"批量任务结束");
	}

	
	public class ExecuteThread implements Callable<String> {
		
		private List<Record> recordList;

		public ExecuteThread(List<Record> records) {
			this.recordList = records;
		}

		@Override
		public String call() throws Exception {
			try {
				beforeProcess(recordList);
				
				//1.对recordList按照bank_cnaps_code前三位分组
				Map<String, List<Record>> sourceMap = new HashMap<>();
				for (String cnapsCode : cnaps) {
					if (StringUtils.isBlank(cnapsCode)) {
						log.error(String.format("cfm_quartz[%s]的param参数配置有误", getJobName()));
						continue;
					}
					List<Record> records;
					if (!sourceMap.containsKey(cnapsCode)) {
						records = new ArrayList<>();
						sourceMap.put(cnapsCode, records);
					}else {
						records = sourceMap.get(cnapsCode);
					}
					
					Iterator<Record> iter = recordList.iterator();
					while (iter.hasNext()) {
						Record next = iter.next();
						if (next.getStr("bank_cnaps_code").startsWith(cnapsCode)) {
							records.add(next);
							iter.remove();
						}
					}
				}
				
				//2.对分组后的银行账号分别生成指令放入队列
				Set<Entry<String,List<Record>>> entrySet = sourceMap.entrySet();
				for (Entry<String, List<Record>> entry : entrySet) {
					String bankCode = entry.getKey();
					List<Record> listRec = entry.getValue();
					if (null == listRec || listRec.size() == 0) {
						continue;
					}
					IChannelBatchInter channelInter = (IChannelBatchInter)ChannelManager.getInter(bankCode, getJobCode());
					int batchSize = channelInter.getBatchSize();
					if (batchSize <= 0) {//接口对批量条数没有限制
						sendQueue(channelInter, listRec, bankCode, 0);
					}else {//批量接口每次最大条数为batchSize条
						int times = listRec.size() % batchSize == 0 ? listRec.size() / batchSize
								: listRec.size() / batchSize + 1;
						for (int i = 0; i < times; i++) {
							if (i == times -1) {
								List<Record> subList = listRec.subList(i * batchSize, listRec.size());
								sendQueue(channelInter, subList, bankCode, i);
								continue;
							}
							List<Record> subList = listRec.subList(i * batchSize, (i + 1) * batchSize);
							sendQueue(channelInter, subList, bankCode, i);
						}
					}
				}
				
				afterProcess(recordList);
				return "success";
			} catch (Exception e) {
				e.printStackTrace();
				return "fail";
			}
		}

		/**
		 * 发送队列
		 * @param channelInter
		 * @param sourceRecord
		 * @param bankCode
		 * @param index
		 */
		private void sendQueue(IChannelInter channelInter, List<Record> sourceRecord, String bankCode, int index){
			ISysAtomicInterface sysInter = getSysInter();
			sysInter.setChannelInter(channelInter);
			
			Record source = new Record().set("list", sourceRecord);
			Map<String, Object> params = getParams(sysInter.getChannelInter(), source);
			
			Record instrRecord = new Record();
			instrRecord.set("acc_id", bankCode.concat(":").concat(String.valueOf(index)));
			Record instr = sysInter.genInstr(instrRecord);
			
			if(saveInstrTable(instr)){
				QueueBean bean = new QueueBean(sysInter, params, bankCode);
				ProductQueue productQueue = new ProductQueue(bean);
				new Thread(productQueue).start();
			}
		}
	}
	
	private boolean saveInstrTable(Record instr){
		try{
			if(Db.save(getInstrTableName(),instr)){
				return true;
			}else{
				return reTry(instr);
			}
		}catch(Exception e){
			return reTry(instr);
		}
		
	}
	
	/**
	 * 重试插入指令表
	 * @param instr

	 * @throws Exception
	 */
	private boolean reTry(Record instr){
		if(needReTrySaveInstr()){
			Record oldLockRecord = getOldInstr(instr);
			if(oldLockRecord != null){
				Date oldDate = oldLockRecord.getDate("pre_query_time");
				Date currDate = instr.getDate("pre_query_time");
				log.debug("oldDate="+oldDate);
				log.debug("currDate="+currDate);
				log.debug("oldDateTime="+oldDate.getTime());
				log.debug("currDateTime()="+currDate.getTime());
				log.debug("maxTime="+CFMQuartzConfigSection.getInstance().getMaxTime());
				if(currDate.getTime()-oldDate.getTime()> CFMQuartzConfigSection.getInstance().getMaxTime()*1000*60){
					Db.delete(getInstrTableName(), oldLockRecord);
					return Db.save(getInstrTableName(), instr);
					
				}
			}
			return false;
		}
		return false;
		
	}
}
