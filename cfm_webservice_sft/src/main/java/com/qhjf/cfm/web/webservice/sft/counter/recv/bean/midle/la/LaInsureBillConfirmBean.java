package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.midle.la;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.PersonBillComfirmReqBean;

/**
 * La柜面收 ：保单确认bean
 * @author CHT
 *
 */
public class LaInsureBillConfirmBean {
	
	private List<Record> data;
	
	public LaInsureBillConfirmBean(List<PersonBillComfirmReqBean> bill){
		data = new ArrayList<>();
		if (null == bill || bill.size() == 0) {
			return;
		}
		
		for (PersonBillComfirmReqBean bean : bill) {
			Record r = new Record();
			r.set("BRANCH", bean.getBranch());
			r.set("DOCORIGAMT", bean.getDocorigamt());

			r.set("BANKACCKEY", bean.getBankacckey());
			
			r.set("COWNSEL", bean.getCownsel());
			r.set("PAYTYPE", bean.getPaytype());
			r.set("COMPANY", bean.getCompany());
			r.set("TCHQDATE", bean.getTchqdate());
			
			r.set("BANKACCDSC", bean.getBankaccdsc());
			
			r.set("ZNBNKKEY", bean.getBankkey());
			r.set("BANKCODE", bean.getBankcode());
			r.set("SACSCODE", bean.getSacscode());
			r.set("SACSTYPE", bean.getSacstype());
			
			
			r.set("BANKDESC", bean.getCownsel());
			r.set("JOBNO", "0");
			r.set("TRANCD", null);
			r.set("NEXTDATE", "0");
			r.set("ZNSTAT", null);
			r.set("TXTLINE", null);
			r.set("DDDEREF", "0");
			
			data.add(r);
		}
	}

	public List<Record> getData() {
		return data;
	}
}
