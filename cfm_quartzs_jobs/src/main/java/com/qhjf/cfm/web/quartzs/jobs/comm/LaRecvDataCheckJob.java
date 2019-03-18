package com.qhjf.cfm.web.quartzs.jobs.comm;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.utils.CommonService;
/**
 * LA批收原始数据校验
 * 	规则：同一批次数据，父子表的总比数相同，总金额相同
 * @author CHT
 *
 */
public class LaRecvDataCheckJob implements Job{
	
	private static Logger log = LoggerFactory.getLogger(LaRecvDataCheckJob.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.debug("LA批收原始数据校验start。。。。。。。。。");
		//查询父表一条状态为0的数据
		List<Record> totalList = Db.find(Db.getSql("quartzs_job_cfm.selOneLaRecvTotal"));
		if (null == totalList || totalList.size() == 0) {
			log.debug("LA批收原始数据校验，头表没有为校验的数据！");
			return ;
		}
		
		for (Record total : totalList) {
			BigDecimal jobno = total.getBigDecimal("JOBNO");
			//数据库查询汇总：同一批次子表数据的笔数、金额
			Record details = Db.findFirst(Db.getSql("quartzs_job_cfm.selOneBatchLaRecvDetail"), jobno);
			if (null == details || details.getBigDecimal("sum_amount") == null) {
				log.debug("LA批收原始数据校验，明细表数据为空！jobno=", jobno);
				updZDDHPF(LaRecvTotalStatus.SIZE_ERR.getKey(), total.getLong("id"));
				continue;
			}
			checkSizeAndAmount(total, details);
		}
		
		log.debug("LA批收原始数据校验end。。。。。。。。。");
	}
	
	private void updZDDHPF(int status, long id){
		boolean update = CommonService.update("ZDDHPF"
				, new Record().set("STATUS", status).set("DATIME", new Date())
				, new Record().set("id", id));
		if (!update) {
			log.error("更新ZDDHPF失败！");
		}
		
		if (status == LaRecvTotalStatus.AMOUNT_ERR.getKey() 
				|| status == LaRecvTotalStatus.SIZE_ERR.getKey()) {
			//TODO:发送邮件给客户固定邮箱（邮箱放在配置文件中，不用环境不一样）
			
		}
	}
	
	private void checkSizeAndAmount(Record total, Record detail){
		Integer zntotrec = total.getInt("ZNTOTREC");
		BigDecimal zntotamt = total.getBigDecimal("ZNTOTAMT");
		
		Integer size = detail.getInt("size");
		BigDecimal sumAmount = detail.getBigDecimal("sum_amount");
		if (zntotrec != size) {
			log.error("LA批收数据校验，总笔数不一致id={}", total.getLong("id"));
			updZDDHPF(LaRecvTotalStatus.SIZE_ERR.getKey(), total.getLong("id"));
			return;
		}
		if (zntotamt.compareTo(sumAmount) != 0) {
			log.error("LA批收数据校验，总笔数不一致id={}", total.getLong("id"));
			updZDDHPF(LaRecvTotalStatus.AMOUNT_ERR.getKey(), total.getLong("id"));
			return;
		}
		
		updZDDHPF(LaRecvTotalStatus.NORMAL.getKey(), total.getLong("id"));
	}
	
	
	public enum LaRecvTotalStatus{
		RECIVE(0, "已接收"),
		NORMAL(1, "详情数据正常"),
		SIZE_ERR(2, "总比数不一致"),
		AMOUNT_ERR(3, "总金额不一致"),
		OVER(4, "详情处理完毕");
		
		public int key;
		public String desc;
		private LaRecvTotalStatus(int key,  String desc){
			this.key = key;
			this.desc = desc;
		}
		
		public int getKey(){
			return this.key;
		}
		public String getDesc(){
			return this.desc;
		}
		public String getDescBykey(int key){
			String result = null;
			LaRecvTotalStatus[] values = LaRecvTotalStatus.values();
			for (LaRecvTotalStatus s : values) {
				if (key == s.getKey()) {
					result = s.getDesc();
				}
			}
			
			return result;
		}
	}

}
