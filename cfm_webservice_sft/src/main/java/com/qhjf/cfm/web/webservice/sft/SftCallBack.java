package com.qhjf.cfm.web.webservice.sft;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.webservice.ebs.EbsCallback;
import com.qhjf.cfm.web.webservice.la.LaCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
/**
 * webservice回写LA/EBS
 * 
 * @author MXW
 * 
 */
public class SftCallBack {
	
	private static Logger log = LoggerFactory.getLogger(SftCallBack.class);
	/**
	 * 整个子批次回调
	 * @param sftOsSource 系统来源 0：LA，1：EBS
	 * @param originDatas LA/EBS原始数据列表
	 */
	public void callback(int sftOsSource,List<Record> originDatas){
		if(sftOsSource == WebConstant.SftOsSource.LA.getKey()){
			LaCallback callback = new LaCallback();
			callback.callBack(originDatas);
		}else if(sftOsSource == WebConstant.SftOsSource.EBS.getKey()){
			EbsCallback callback = new EbsCallback();
			callback.callBack(originDatas);
		}
	}
	/**
	 * 重复预警，单笔回调
	 * @param sftOsSource
	 * @param originData
	 */
    public void callback(int sftOsSource,Record originData){
		List<Record> originDatas = new ArrayList<Record>();
		originDatas.add(originData);
		callback(sftOsSource, originDatas);
	}
    /**
     * 重复预警，单笔回调
     * @param sftOsSource
     * @param originId
     */
    public void callback(int sftOsSource,Long originId){
    	Record originData = null;
    	if(sftOsSource == WebConstant.SftOsSource.LA.getKey()){
    		originData = Db.findById("la_origin_pay_data", "id", originId);
		}else if(sftOsSource == WebConstant.SftOsSource.EBS.getKey()){
			originData = Db.findById("ebs_origin_pay_data", "id", originId);
		}
    	if(originData == null){
    		log.error(sftOsSource+"袁术数据id不存在:"+originId);
    		return;
    	}
		List<Record> originDatas = new ArrayList<Record>();
		originDatas.add(originData);
		callback(sftOsSource,originDatas);
	}

}
