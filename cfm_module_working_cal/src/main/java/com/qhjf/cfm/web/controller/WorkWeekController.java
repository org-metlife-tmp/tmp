package com.qhjf.cfm.web.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.service.WorkWeekService;

/**
   *      日历管理模块/工作周设置
 * @author pc_liweibing
 *
 */
public class WorkWeekController extends CFMBaseController {

	private static final Logger logger = LoggerFactory.getLogger(WorkWeekController.class);
	
	private WorkWeekService workWeekService = new WorkWeekService();
	
	/**
	  *  工作周 列表查询
	 */
	@Auth(hasForces = {"SectionSet"})
	public void list() {
		logger.info("enter into 日历模块_工作周列表部分");	
		List<Record> records = null;
		Record innerRecord = new Record() ;
		try {
		  records = workWeekService.findWeekList(getParamsToRecord());
		  for (Record record : records) {
			 logger.info( "查询到的周列表条数为===" + records.size() ) ;
			 //开始组装返回报文,先排除掉year节点
			 record.remove("year");
		  }
		  //查询列表的时候,获取是否激活的状态
		  Short active_flag = workWeekService.list_is_active(getParamsToRecord());
		  innerRecord.set("weeks", records);
		  innerRecord.set("is_active", String.valueOf(active_flag));
		  innerRecord.set("year", getParamsToRecord().get("year"));
		  renderOk(innerRecord);
	  }catch(BusinessException e){
		  e.printStackTrace();
		  logger.error("查询工作周列表失败!", e);   
		  renderFail(e);
	}
		
  }
	
	/**
	  *  工作周 初始化
	 * @throws Exception 
	 */
	@Auth(hasForces = {"SectionSet"})
	public void set() throws Exception {
		
		logger.info("enter into 工作周初始化 ");
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");	
		String str = getParamsToRecord().getStr("endpoint");
		str = str.replace("[", "");
		str = str.replace("]", "");
		str = str.replace("\"", "");
		String[] endpoints = str.split(",");
		List<Record> records = new ArrayList<Record>();
		Record returnRecord = new Record();
		try {
			//获取年份,删除
			String year = endpoints[0].substring(0, 4);
			//判断此年份是否已经激活 1:已经激活 抛出错误
			workWeekService.init_is_active(year);
			workWeekService.deleteWork(year);
			//重新开始插入
			Arrays.sort(endpoints);
			ArrayList<String> asList =  new ArrayList<String>(Arrays.asList(endpoints)); 
			asList.remove(year+"-01-01");
			asList.remove(year+"-12-31");
			for (int i = 0; i <= asList.size(); i++) {
				Record record = new Record();
				record.set("year", Integer.valueOf(year));
				record.set("serial_no", i+1);
				record.set("end_date", asList.size() == i ? sdf.parse(year+"-12-31") :sdf.parse(asList.get(i)) );	
				if(i==0){
					record.set("start_date", sdf.parse(year+"-01-01"));
				}else {
					Calendar instance = Calendar.getInstance();
					instance.setTime(sdf.parse(asList.get(i-1)));
					instance.add(Calendar.DAY_OF_YEAR, 1);
					Date startDay = instance.getTime();
					record.set("start_date", startDay);
				}
				records.add(record);
			}
			//开始进行批量插入
			workWeekService.insertWeek(records);
			//将新的工作周返还给前端页面
			returnRecord.set("year", year);
			for (Record Record : records) {
				Record.remove("year");
				String start_date = sdf.format(Record.get("start_date"));
				String end_date = sdf.format(Record.get("end_date"));
				Record.set("start_date", start_date);
				Record.set("end_date", end_date);
			}
			returnRecord.set("is_active","0");
			returnRecord.set("weeks", records);
			renderOk(returnRecord);
		}catch(BusinessException e){
			e.printStackTrace();
			logger.error("工作周初始化失败!!");
			renderFail(e);
		}		
	}
	
	/**
	 * 工作周激活
	 * @throws DbProcessException 
	 * @throws ReqDataException 	
	 */
	 @Auth(hasForces = {"SectionSet"})
	 public void acvivity() throws ReqDataException, DbProcessException {
		 logger.info("Enter into 激活工作周接口");
		 Record record = new Record();
		 try {
		     workWeekService.updateActiveFlag(getParamsToRecord());
		     record.set("is_active", "1");
		     renderOk(record);
		 } catch(BusinessException e) {
			 e.printStackTrace();
			 logger.error("激活工作周失败!!");
			 renderFail(e);
		 }
		 
	 }
}
