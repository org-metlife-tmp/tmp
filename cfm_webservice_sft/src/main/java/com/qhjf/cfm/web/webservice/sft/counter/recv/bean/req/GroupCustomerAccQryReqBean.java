package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.config.DDHEBSConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.webservice.ebs.counter.recv.bean.PublicBean;

import cn.metlife.ebs_sit.services.CustomerAccountService.CustomerAccountServiceProxy;

/**
 * 客户账户查询请求bean
 * 
 * @author CHT
 *
 */
public class GroupCustomerAccQryReqBean extends PublicBean {
	private static DDHEBSConfigSection config = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHEBS);
	/**
	 * 客户号 ebs 精准查询 , 传入参数，两者必须有一个
	 */
	private String clientNo;
	/**
	 * 客户名称（模糊查询）, 传入参数，两者必须有一个
	 */
	private String clientName;

	/**
	 * 
	 * @param clientNo
	 *            客户号 ebs（精准查询） , 传入参数，两者必须有一个
	 * @param clientName
	 *            客户名称（模糊查询）, 传入参数，两者必须有一个
	 * @throws ReqDataException
	 */
	public GroupCustomerAccQryReqBean(String clientNo, String clientName) throws ReqDataException {
		if (StringUtils.isBlank(clientNo) && StringUtils.isBlank(clientName)) {
			throw new ReqDataException("客户号与客户名称不能同时为空");
		}
		this.clientNo = StringUtils.isBlank(clientNo) ? null : clientNo;
		this.clientName = StringUtils.isBlank(clientName) ? null : clientName;
	}

	public String getClientNo() {
		return clientNo;
	}

	public String getClientName() {
		return clientName;
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("clientNo", this.clientNo);
		map.put("clientName", this.clientName);
		return map;
	}

	@Override
	public String getTemplate() {
		return "customerAccQry.vm";
	}

	@Override
	public String getUrl() {
		return config.getCustomerAccountService();
	}

	@Override
	public String service(String xml) throws RemoteException {
		CustomerAccountServiceProxy proxy = new CustomerAccountServiceProxy(getUrl());
		return proxy.getAccountInfo(xml);
	}

}
