package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.midle.la;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.PersonBillCancelReqBean;

/**
 * 保单撤销中间bean
 * 
 * @author CHT
 *
 */
public class LaInsureBillCancelBean {

	private Record data;

	public LaInsureBillCancelBean(PersonBillCancelReqBean bill) {
		if (null == bill) {
			return;
		}

		Record r = new Record();
		r.set("RECEIPT", bill.getRecept());
		data = r;
	}

	public Record getData() {
		return data;
	}
}
