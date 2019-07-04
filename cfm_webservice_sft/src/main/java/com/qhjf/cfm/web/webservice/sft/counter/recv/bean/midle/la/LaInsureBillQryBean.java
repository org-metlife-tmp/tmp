package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.midle.la;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.jfinal.plugin.activerecord.Record;

public class LaInsureBillQryBean {
	private Record req;

	public LaInsureBillQryBean(String insureBillNo, String branch, String company){
		Record r = new Record();
		String today = new SimpleDateFormat("yyyyMMdd").format(new Date());
		r.set("CHDRCOY", company);
		r.set("CHDRNUM", insureBillNo);
		r.set("EFFDATE", today);
		req = r;
	}

	public Record getReq() {
		return req;
	}
}
