package com.qhjf.cfm.web.webservice.sft;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.webservice.ebs.recv.EbsRecvCallback;
import com.qhjf.cfm.web.webservice.la.recv.LaRecvCallback;

/**
 * 资金系统回调核心系统LA/EBS
 * 
 * @author CHT
 *
 */
public class SftRecvCallBack {
	private static Logger log = LoggerFactory.getLogger(SftRecvCallBack.class);
	
	/**
	 * 整个子批次回调
	 * 
	 * @param sftOsSource
	 *            系统来源 0：LA，1：EBS
	 * @param originDatas
	 *            LA/EBS原始数据列表
	 */
	public void callback(int sftOsSource, List<Record> originDatas) {
		if (sftOsSource == WebConstant.SftOsSource.LA.getKey()) {
			LaRecvCallback callback = new LaRecvCallback();
			callback.callBack(originDatas);
		} else if (sftOsSource == WebConstant.SftOsSource.EBS.getKey()) {
			//TODO：目前没有EBS批收业务
			EbsRecvCallback callback = new EbsRecvCallback();
			callback.callBack(originDatas);
		}
	}

	/**
	 * 重复预警，单笔回调
	 * 
	 * @param sftOsSource
	 *            系统来源 0：LA，1：EBS
	 * @param originData
	 *            LA/EBS原始数据
	 */
	public void callback(int sftOsSource, Record originData) {
		List<Record> originDatas = new ArrayList<Record>();
		originDatas.add(originData);
		callback(sftOsSource, originDatas);
	}

	/**
	 * 重复预警，单笔回调
	 * 
	 * @param sftOsSource
	 *            系统来源 0：LA，1：EBS
	 * @param originId
	 *            LA/EBS原始数据主键
	 */
	public void callback(int sftOsSource, Long originId) {
		Record originData = null;
		if (sftOsSource == WebConstant.SftOsSource.LA.getKey()) {
			originData = Db.findById("la_origin_recv_data", "id", originId);
		} else if (sftOsSource == WebConstant.SftOsSource.EBS.getKey()) {
			originData = Db.findById("ebs_origin_recv_data", "id", originId);
		}
		if (originData == null) {
			log.error(sftOsSource + "袁术数据id不存在:" + originId);
			return;
		}
		List<Record> originDatas = new ArrayList<Record>();
		originDatas.add(originData);
		callback(sftOsSource, originDatas);
	}
}
