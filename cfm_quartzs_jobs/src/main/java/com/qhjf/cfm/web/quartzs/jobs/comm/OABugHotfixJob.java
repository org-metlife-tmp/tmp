package com.qhjf.cfm.web.quartzs.jobs.comm;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.qhjf.cfm.utils.CommKit;
import com.qhjf.cfm.web.constant.WebConstant;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.webservice.oa.callback.OaCallback;

/**
 * OA回调异常数据再次回调作业：
 * 	1.优先取定时任务param参数 ids（使用英文逗号分割的，oa原始数据id的字符串），job通过id查询oa_origin_data表数据，进行回调OA
 * 	2.如果ids不存在，查看param参数中是否存在start 和 end 参数，start和end参数必须同时存在，切符合yyyy-MM-dd 的日期格式，
 * 	job通过start 和 end查询oa_origin_data表数据，进行回调OA
 * 	2.如果定时任务param参数没有维护，或为空，则任务终止。
 * (风险点：OA是否支持重复回调)
 * @author CHT
 *
 */
public class OABugHotfixJob implements Job{
	
	private static Logger log = LoggerFactory.getLogger(OABugHotfixJob.class);

	/**
	 *  查询oa_origin_data 需要回调的数据集合（小光提供查询条件）
	 *	轮询列表，循环，new OaCallback().callback（item），
	 *	结束
	 */
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		log.debug("OA回调异常数据再次回调作业start...");
		String originIds = context.getMergedJobDataMap().getString("ids");
		String start = context.getMergedJobDataMap().getString("start");
		String end = context.getMergedJobDataMap().getString("end");
		if (null != originIds && !"".equals(originIds.trim())) {
			String[] split = splitIds(originIds);
			for (String orginId : split) {
				if (null != orginId && !"".equals(orginId.trim())) {
					try {
						Record oaOrigin = Db.findById("oa_origin_data", "id", TypeUtils.castToLong(orginId));
						if (null != oaOrigin) {
							log.debug("OA回调异常数据再次回调作业，开始回调[{}]", oaOrigin);
							new OaCallback().callback(oaOrigin);
						}
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}
			}
		}else {
			if(validateDateParam(start,end)){
				//1.查询oa_origin_data
				List<Record> originList = Db.find(Db.getSqlPara("oa_hotfix_cfm.getOaHotfixOriginData", Kv.by("start_date",start).set("end_date",end)));
				if (null == originList || originList.size() == 0) {
					log.debug("OA回调异常数据再次回调作业，未查询到OA原始数据end...");
					return;
				}
				//循环回调回调OA
				for (Record origin : originList) {
					log.debug("OA回调异常数据再次回调作业，开始回调[{}]", origin);
					try {
						new OaCallback().callback(origin);
					} catch (Exception e) {
						e.printStackTrace();
						continue;
					}
				}

			}else{
				log.debug("OA回调异常数据再次回调作业参数异常，不进行回调");
			}
		}
		log.debug("OA回调异常数据再次回调作业end...");
	}

	/**
	 * 判断日期参数，只有start，end都存在且都为yyyy-MM-dd格式是为true，其余都为false
	 * @param start
	 * @param end
	 * @return
	 */
	private boolean validateDateParam(String start, String end){
		if(CommKit.isNullOrEmpty(start)|| CommKit.isNullOrEmpty(end)){
			return false;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try{
			sdf.parse(start);
			sdf.parse(end);
			return true;
		}catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private String[] splitIds(String originIdStr){
		String[] result = null;
		if (originIdStr.indexOf(",") != -1) {
			result = originIdStr.split(",");
		}else if (originIdStr.indexOf("，") != -1) {
			result = originIdStr.split(",");
		}else if (originIdStr.indexOf("|") != -1) {
			result = originIdStr.split("\\|");
		}else {
			result = originIdStr.split(",");
		}
		return result;
	}
}
