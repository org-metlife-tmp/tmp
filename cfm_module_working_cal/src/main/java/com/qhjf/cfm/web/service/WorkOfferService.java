package com.qhjf.cfm.web.service;

import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.util.CommonUtil;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WorkOfferService {
	
    /**
     * 报盘列表查询
     *
     * @param record
     * @return
     * @throws ReqDataException 
     */
    public Map<String,Object> findWorkOfferList(final Record record) throws BusinessException {
        SqlPara sqlPara = Db.getSqlPara("workoffer.list", Ret.by("map", record.getColumns()));
        Record rc = Db.findFirst(sqlPara);
        if(rc == null) {
        	throw new ReqDataException("暂未有银行设置报盘日!");
        }
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("year", record.getStr("year"));
        map.put("bank_type", Db.findFirst("select name from const_bank_type where code = ?", rc.getStr("bank_type")).getStr("name"));
        map.put("dates", rc.getStr("cdate").split(","));
        map.put("is_active", Db.findFirst(Db.getSql("workcal.findactive"),
        							rc.getStr("bank_type"),
        							record.getStr("year")).get("is_active"));
    	return map;
    }
    
    /**
     * 报盘新增，其实也是修改
     * 每次需要清除原来该银行设置的报盘日，在新增清除之前需要判断该银行的报盘日是否启动：
     * 	  1，启动，不能修改
     *    2，未启动，允许
     *
     * @param record
     * @return
     * @throws ReqDataException 
     */
    public void addWorkOfferList(final Record record) throws BusinessException {
    	
    	final ArrayList<Record> list = new ArrayList<Record>();
    	final List<String> dates = record.get("offer_date");
    	final String bank_type = record.getStr("bank_type");
    	final String year = record.getStr("year");
    	
    	boolean exist = Db.find(Db.getSql("workcal.findisactive"),bank_type,year).size()>0;
    	if(exist) {
    		throw new ReqDataException("当前银行报盘日已经启用！");
    	}  	
    	
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    	try {
	    	for(String s : dates) {
	    		Record map = new Record();
	    		map.set("bank_type", bank_type);
				map.set("offer_date", sdf.parse(s));
				list.add(map);
	    	}
    	} catch (ParseException e) {
    		throw new ReqDataException("请求日期参数格式不对!");
    	}
    	
    	if(CommonUtil.isNullHoliday(dates)) {
    		throw new ReqDataException("未有更新的数据!");
    	}
        //进行新增操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //批量新增
            	Kv cond = Kv.by("offer_date", year).set("bank_type", bank_type);
            	Db.update(Db.getSqlPara("workoffer.deloffer", cond));
            	int[] result = Db.batch(Db.getSql("workoffer.add"),"bank_type, offer_date",
            			list,dates.size());
            	boolean existBank = Db.find(Db.getSql("workcal.findactive"),bank_type,year).size()>0;
            	boolean saveBank = true;
            	if(!existBank) {
            		Record recordBank = new Record();
            		recordBank.set("cdate", CommonUtil.getCurrentYear(year));
            		recordBank.set("is_active", 0);
            		recordBank.set("type", bank_type);
            		saveBank = Db.save("working_year_setting", "cdate", recordBank);        	
            	}              	
            	return (result.length == dates.size()) && saveBank;
            }
        });
        if (!flag) {
            throw new DbProcessException("新增报盘日期失败！");
        }
    }

    /**
     * 启用停用
     *
     * @param record
     * @return
     * @throws  
     * @throws ReqDataException 
     */
    public Record updateWorkOfferActivity(final Record record) throws BusinessException {
    	final String year = record.getStr("year");
    	final String bankType = record.getStr("bank_type");
    	List<Record> liy  = Db.find(Db.getSql("workcal.findactive"),bankType,year);
    	if(liy.size() <=0) {
    		throw new ReqDataException("该银行未设置报盘日！");
    	}
    	
    	//进行数据更新操作
    	boolean flag = Db.tx(new IAtom() {
    		@Override
    		public boolean run() throws SQLException {
    			//清除原有关联信息
    			boolean y = Db.update(Db.getSql("workcal.upactive"),bankType,year) > 0;
    			return y;
    		}
    	});    	
    	Record rc = new Record();
    	if(!flag) {
    		throw new DbProcessException("更新银行报盘日失败！");
    	}else {
    		rc.set("is_active", 1);
    		return rc;
    	}
    }    
   
    
}
