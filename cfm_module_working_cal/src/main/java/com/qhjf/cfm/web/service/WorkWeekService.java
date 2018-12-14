package com.qhjf.cfm.web.service;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;

/**
 * 日历模块_工作周功能
 * @author pc_liweibing
 *
 */
public class WorkWeekService {

	private static final Logger logger = LoggerFactory.getLogger(WorkWeekService.class);
	
	/**
	 * 查询周列表
	 * @param paramsToRecord
	 * @return  所有周列表详情
	 * @throws ReqDataException 
	 */
	public List<Record> findWeekList(Record paramsToRecord) throws BusinessException {
			
		String sql = Db.getSql("workweek.findWeekList");	
		logger.info("工作周列表查询sql===" + sql );
		String StrYear = paramsToRecord.get("year");
		List<Record> records = Db.find(sql, Integer.valueOf(StrYear.trim())) ;	 
		if(null == records || records.size()==0){
			throw new ReqDataException("工作周列表详情未设置！");
		}
		return records ;		
	}

	/**
	 * 删除 之前year 的工作周设置
	 * @param year
	 */
	public void deleteWork(String year) {
		String sql = Db.getSql("workweek.deleteWeekList");	
		int num = Db.delete(sql, year);
		logger.info("删除"+year+"年工作周列表"+num+"条");
	}

	/**
	 * 工作周设置插入
	 * @param records
	 * @throws DbProcessException 
	 */
	public void insertWeek(final List<Record> records) throws DbProcessException {
		       boolean flag = Db.tx(new IAtom() {
	            @Override
	            public boolean run() throws SQLException {
	              return ArrayUtil.checkDbResult(Db.batchSave("working_week_setting", records, 1000));
	            }
	        });
	        if (!flag) {
	            throw new DbProcessException("初始化工作周列表失败_插入工作周！");
	        }
		
		logger.info("插入工作周列表结束了!");
	}

	/**
	 *   初始化时查询此年份是否已经激活
	 *  1. 无数据.插入数据 ,激活状态为 : 未激活  2. 有数据 ,激活状态为 : 未激活 , 什么都不做  3. 有数据 , 激活状态为 : 已激活 ,抛出错误
	 * @throws ParseException 
	 */
	public void init_is_active(String year) throws DbProcessException, ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Record> records = this.selectActive(year);
		if( null == records ||  records.size() == 0 ){
			Record record = new Record();
			record.set("cdate",sdf.parse(year+"-02-02"));
			record.set("is_active", 0);
			record.set("type","week");
			boolean flag = Db.save("working_year_setting", record);
		    if (!flag) {
		       throw new DbProcessException("工作周激活标识插入失败！!");
		      }			
		}else {
			short is_active = records.get(0).get("is_active");
			if(1 == is_active) {
				logger.info("is_active标志位======="+is_active);
				throw new DbProcessException("工作周已经是激活状态!！");
			}
		}
	}
	
	
	
	
	
	//查询是否激活
	public List<Record> selectActive(String year) {
		String sql = Db.getSql("workweek.isActive");
		logger.info("查询是否激活的sql语句========="+sql);
		List<Record> records = Db.find(sql, year);
		return  records ;
	}

	/**
	 * 获取周列表的时候,获取是否激活状态  与初始化获取到激活状态后的操作不一样
	 * @param paramsToRecord
	 * @throws ReqDataException 
	 */
	public short list_is_active(Record paramsToRecord) throws ReqDataException {
		short flag = 0;
		String StrYear = paramsToRecord.get("year");
		List<Record> selectActive = selectActive(StrYear.trim());
		if(null != selectActive && selectActive.size() > 0 ) {
			flag = selectActive.get(0).get("is_active");
		}else {
			throw new ReqDataException("工作周列表详情查询发现是否激活状态表未设置！");
		}
		return flag ;
	}

	/**
	 * 激活工作状态
	 * @param record
	 * @throws ReqDataException 
	 * @throws DbProcessException 
	 */
	public void updateActiveFlag(Record record) throws ReqDataException, DbProcessException {	
		final String year = record.get("year");
		List<Record> selectActive = this.selectActive(year.trim());
		if(null == selectActive || selectActive.size() == 0){
			throw new ReqDataException(year+"年工作周还未进行初始化设置,不允许进行激活操作！");
		}
		short active_flag = selectActive.get(0).get("is_active");
		if(1 == active_flag) {
			throw new ReqDataException(year+"年工作周已经激活！");
		}
		//进行数据更新操作
    	boolean flag = Db.tx(new IAtom() {
    		@Override
    		public boolean run() throws SQLException {
    			//清除原有关联信息
    			boolean y = Db.update(Db.getSql("workweek.upActiveFlag"),year) > 0;
    			return y;	
    		}
    	});  
    	if(!flag) {
    		throw new DbProcessException("激活"+year+"年工作周失败!!");
    	}
	}
}
