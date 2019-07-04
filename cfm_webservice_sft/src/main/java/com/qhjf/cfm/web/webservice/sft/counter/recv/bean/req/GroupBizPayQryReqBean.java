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

import cn.metlife.ebs_sit.services.QueryBussinessPayInfo.QueryBussinessPayInfoProxy;

/**
 * 业务（新单签发、保全收费、定期结算收费、续期收费、不定期收费） 信息查询请求bean 
 * 
 * @author CHT
 *
 */
public class GroupBizPayQryReqBean extends PublicBean {
	private static DDHEBSConfigSection config = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHEBS);
	/**
	 * 客户号 ebs 精准查询
	 */
	private String customerNo;
	/**
	 * 客户名称（模糊查询）
	 */
	private String customerName;
	/**
	 * 投保单号
	 */
	private String preinsureBillNo;
	/**
	 * 保单号
	 */
	private String insureBillNo;
	/**
	 * 业务号
	 */
	private String bussinessNo;
	/**
	 * 业务类型 ：1-新单签发收费,2-保全收费,3-定期结算收费,4-续期首付,5-不定期收费
	 */
	private String bussinessType;

	private static String CONST = "12345";

	/**
	 * 
	 * @param customerNo
	 *            客户号 ebs 精准查询
	 * @param customerName
	 *            客户名称（模糊查询）
	 * @param preinsureBillNo
	 *            投保单号
	 * @param insureBillNo
	 *            保单号
	 * @param bussinessNo
	 *            业务号
	 * @param bizType
	 *            业务类型 ：1-新单签发收费,2-保全收费,3-定期结算收费,4-续期首付,5-不定期收费
	 * @throws ReqDataException
	 */
	public GroupBizPayQryReqBean(String customerNo, String customerName, String preinsureBillNo,
			String insureBillNo, String bussinessNo, String bizType) throws ReqDataException {
		this.customerNo = StringUtils.isBlank(customerNo) ? null : customerNo;
		this.customerName = StringUtils.isBlank(customerName) ? null : customerName;
		this.preinsureBillNo = StringUtils.isBlank(preinsureBillNo) ? null : preinsureBillNo;
		this.insureBillNo = StringUtils.isBlank(insureBillNo) ? null : insureBillNo;
		this.bussinessNo = StringUtils.isBlank(bussinessNo) ? null : bussinessNo;

		if (StringUtils.isNotBlank(bizType)) {
			if (bizType.length() != 1 || CONST.indexOf(bizType) == -1) {
				throw new ReqDataException("业务类型错误");
			}
		}
		
		this.bussinessType = bizType;
	}

	public String getCustomerNo() {
		return customerNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getPreinsureBillNo() {
		return preinsureBillNo;
	}

	public String getInsureBillNo() {
		return insureBillNo;
	}

	public String getBussinessNo() {
		return bussinessNo;
	}

	public String getBussinessType() {
		return bussinessType;
	}

	public static String getCONST() {
		return CONST;
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> map = new HashMap<>();
		map.put("customerNo", this.customerNo);
		map.put("customerName", this.customerName);
		map.put("preinsureBillNo", this.preinsureBillNo);
		map.put("insureBillNo", this.insureBillNo);
		map.put("bussinessNo", this.bussinessNo);
		map.put("bussinessType", this.bussinessType);
		return map;
	}

	@Override
	public String getTemplate() {
		return "bizPayQry.vm";
	}

	@Override
	public String getUrl() {
		return config.getQueryBussinessPayInfo();
	}

	@Override
	public String service(String xml) throws RemoteException {
		QueryBussinessPayInfoProxy proxy = new QueryBussinessPayInfoProxy(getUrl());
		return proxy.queryInfo(xml);
	}

}
