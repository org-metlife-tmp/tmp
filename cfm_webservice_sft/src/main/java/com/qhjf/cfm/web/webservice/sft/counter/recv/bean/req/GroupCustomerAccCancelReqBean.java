package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import com.qhjf.cfm.web.config.DDHEBSConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.webservice.ebs.counter.recv.bean.PublicBean;

import cn.metlife.ebs_sit.services.FundsEnterService.FundsEnterServiceProxy;

/**
 * 客户账户 类型的保单 撤销 的请求bean
 * 
 * @author CHT
 *
 */
public class GroupCustomerAccCancelReqBean extends PublicBean {
	private static DDHEBSConfigSection config = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHEBS);
	/**
	 * 资金流水号，每笔业务一个流水号
	 */
	private String payNo;
	/**
	 * 资金类别 0-正常 1–反冲 ;当是1-反冲时， 只要PayNo，PayType，CPayNo 必传，其他都可以不传
	 */
	// private String payType = "1";
	/**
	 * 资金反冲流水号, 当是反冲时必传， 该号码就对应正常预收时的PayNo.
	 */
	private String cPayNo;

	/**
	 * 
	 * @param payNo
	 *            资金流水号，每笔业务一个流水号
	 * @param cPayNo
	 *            资金反冲流水号, 当是反冲时必传， 该号码就对应正常预收时的PayNo.
	 */
	public GroupCustomerAccCancelReqBean(String payNo, String cPayNo) {
		this.payNo = payNo;
		this.cPayNo = cPayNo;
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("PayNo", payNo);
		map.put("PayType", "1");
		map.put("CPayNo", cPayNo);
		map.put("PayCustomerNo", null);
		map.put("PayWay", null);
		map.put("PayMoney", null);
		map.put("ChequeNo", null);
		map.put("ChequeDate", null);
		map.put("BankCode", null);
		map.put("BankAccNo", null);
		map.put("BankAccName", null);
		map.put("InBankCode", null);
		map.put("InBankAccNo", null);
		return map;
	}

	@Override
	public String getTemplate() {
		return "customerAccCancel.vm";
	}

	@Override
	public String getUrl() {
		return config.getFundsEnterService();
	}

	@Override
	public String service(String xml) throws RemoteException {
		FundsEnterServiceProxy proxy = new FundsEnterServiceProxy(getUrl());
		return proxy.dealFunds(xml);
	}

}
