package com.qhjf.cfm.web.service;


import java.util.List;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.plugins.log.LogbackLog;


/**
 * 柜面付
 * 
 * @author pc_liweibing
 *
 */
public class PayCounterService {

	private final static Log logger = LogbackLog.getLog(PayCounterService.class);

	/**
	 * 柜面付列表
	 * @param record
	 */
	public void list(Record record) {
		List<Integer> status = record.get("status");
		if(null == status) {
			record.remove("status");
		}
	}

	
	
	
}
