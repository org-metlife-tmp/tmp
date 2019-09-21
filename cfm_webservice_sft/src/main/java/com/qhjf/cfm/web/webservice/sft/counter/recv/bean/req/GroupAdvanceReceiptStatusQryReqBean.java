package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.config.DDHEBSConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.webservice.ebs.counter.recv.bean.PublicBean;

import cn.metlife.ebs_sit.services.AdvanceReceiptStatusService.AdvanceReceiptStatusServiceProxy;

/**
 * 预收ebs状态查询接口
 * 
 * @author CHT
 *
 */
public class GroupAdvanceReceiptStatusQryReqBean extends PublicBean {
	private static DDHEBSConfigSection config = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHEBS);
	/**
	 * 资金流水号，查询的资金号码
	 */
	private String PayNo;

	/**
	 * 
	 * @param PayNo
	 *            资金流水号，查询的资金号码
	 */
	public GroupAdvanceReceiptStatusQryReqBean(String PayNo) {
		this.PayNo = PayNo;
	}

	public String getPayNo() {
		return PayNo;
	}

	@Override
	public Map<String, Object> toMap() throws ReqDataException {
		Map<String, Object> map = new HashMap<>();
		map.put("PayNo", PayNo);
		return map;
	}

	@Override
	public String getTemplate() {
		return "advanceReceiptStatusQry.vm";
	}

	@Override
	public String getUrl() {
		return config.getAdvanceReceiptStatusService();
	}

	@Override
	public String service(String xml) throws RemoteException {
		AdvanceReceiptStatusServiceProxy proxy = new AdvanceReceiptStatusServiceProxy(getUrl());
		return proxy.getStatusInfo(xml);
	}

}
