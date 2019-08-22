package com.qhjf.cfm.web;

import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

public class WorkingCalOptypeMgr extends  AbstractOptypeMgr {


    @Override
    public void registe() {
    	/** 工作日历设置 start **/
    	optypes.add(new Optype(Optype.Mode.NORMAL, "workcal_list")
    			.registerValidate(new RequiredParamsValidate(new String[]{"year"}))
    			.registKeepParams(new String[]{"year"}));
    	
    	optypes.add(new Optype(Optype.Mode.ADMIN, "workcal_init")
    			.registerValidate(new RequiredParamsValidate(new String[]{"year"}))
    			.registKeepParams(new String[]{"year"}));
    	
    	optypes.add(new Optype(Optype.Mode.ADMIN, "workcal_holiday")
    			.registerValidate(new RequiredParamsValidate(new String[]{"year"}))
    			.registKeepParams(new String[]{"year","holiday","workingday"}));
    	
    	optypes.add(new Optype(Optype.Mode.ADMIN, "workcal_activity")
    			.registerValidate(new RequiredParamsValidate(new String[]{"year"}))
    			.registKeepParams(new String[]{"year"}));
    	/** 工作日历设置 end **/
    	
    	/** 工作周设置 start **/
    	optypes.add(new Optype(Optype.Mode.NORMAL, "workweek_list")
                .registerValidate(new RequiredParamsValidate(new String[]{"year"}))
                .registKeepParams(new String[]{"year"}));
    	optypes.add(new Optype(Optype.Mode.NORMAL, "workweek_set")
                .registerValidate(new RequiredParamsValidate(new String[]{"endpoint"}))
                .registKeepParams(new String[]{"endpoint"}));
    	optypes.add(new Optype(Optype.Mode.NORMAL, "workweek_acvivity")
                .registerValidate(new RequiredParamsValidate(new String[]{"year"}))
                .registKeepParams(new String[]{"year"}));
    	/** 工作周设置 end **/
    	
    	/** 报盘工作日设置 start **/
    	optypes.add(new Optype(Optype.Mode.NORMAL, "workoffer_list")
                .registerValidate(new RequiredParamsValidate(new String[]{"year"}))
                .registKeepParams(new String[]{"year","bank_type"}));
    	
    	optypes.add(new Optype(Optype.Mode.NORMAL, "workoffer_add")
    			.registerValidate(new RequiredParamsValidate(new String[]{"year","bank_type","offer_date"}))
    			.registKeepParams(new String[]{"year","bank_type","offer_date"}));
    	
    	optypes.add(new Optype(Optype.Mode.NORMAL, "workoffer_activity")
    			.registerValidate(new RequiredParamsValidate(new String[]{"year","bank_type"}))
    			.registKeepParams(new String[]{"year","bank_type"}));
    	
    	optypes.add(new Optype(Optype.Mode.NORMAL, "workcal_checkoutset")
    			.registerValidate(new RequiredParamsValidate(new String[]{"year","dates"}))
    			.registKeepParams(new String[]{"year","dates"}));
    	
    	optypes.add(new Optype(Optype.Mode.NORMAL, "workcal_checkoutlist")
    			.registerValidate(new RequiredParamsValidate(new String[]{"year"}))
    			.registKeepParams(new String[]{"year"}));
    	
    	optypes.add(new Optype(Optype.Mode.NORMAL, "workcal_checkoutactivity")
    			.registerValidate(new RequiredParamsValidate(new String[]{"year"}))
    			.registKeepParams(new String[]{"year"}));
    	/** 报盘工作日设置 end **/
    }
}
