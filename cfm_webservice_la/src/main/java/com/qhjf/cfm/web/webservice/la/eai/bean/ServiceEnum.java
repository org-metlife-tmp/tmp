package com.qhjf.cfm.web.webservice.la.eai.bean;

import com.qhjf.cfm.exceptions.ReqDataException;

/**
 * LA核心系统服务接口常量定义
 * 
 * @author CHT
 *
 */
public enum ServiceEnum {
	/**
	 * DRNService：批量收款回调接口/柜面收个单确认回调接口
	 */
	DRNService("DRNService","DRNADDI_REC", "drn", "http://www.csc.smart/bo/schemas/DRNADDI", true, "DRNINRECS", "批量收款回调/柜面收确认"),
	/**
	 * POLService：柜面收款查询订单
	 */
	POLService("POLService","POLENQ2I_REC", "pol", "http://www.csc.smart/bo/schemas/POLENQ2I", false, null, "柜面收款查询订单"),
	/**
	 * DELService：柜面收款撤销订单
	 */
	DELService("DRNService","DRNDELI_REC", "drn", "http://www.csc.smart/bo/schemas/DRNDELI", false, null, "柜面收款撤销订单");
	/**
	 * 服务名字
	 */
	private String serviceName;
	/**
	 * MsgBody xml节点的子节点名称
	 */
	private String nodeName;
	/**
	 * MsgBody xml节点的子节点 命名空间名称
	 */
	private String nodeNameSpace;
	/**
	 * MsgBody xml节点的子节点 命名空间URL
	 */
	private String nodeNameSpaceUrl;
	/**
	 * ADDITIONAL_FIELDS直接子节点是否为数组
	 */
	private boolean additionalFieldsSonIsArray;
	/**
	 * ADDITIONAL_FIELDS直接子节点为数组，则该字段为子节点名字
	 */
	private String additionalFieldsSonTagName;
	/**
	 * 描述
	 */
	private String desc;

	ServiceEnum(String serviceName, String name, String ns, String nsUrl, boolean additionalFieldsSonIsArray,
			String additionalFieldsSonTagName, String desc) {
		this.serviceName = serviceName;
		nodeName = name;
		nodeNameSpace = ns;
		nodeNameSpaceUrl = nsUrl;
		this.additionalFieldsSonIsArray = additionalFieldsSonIsArray;
		this.additionalFieldsSonTagName = additionalFieldsSonTagName;
		this.desc = desc;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getNodeNameSpace() {
		return nodeNameSpace;
	}

	public String getNodeNameSpaceUrl() {
		return nodeNameSpaceUrl;
	}

	public String getDesc() {
		return desc;
	}
	
	public boolean isAdditionalFieldsSonIsArray() {
		return additionalFieldsSonIsArray;
	}

	public String getAdditionalFieldsSonTagName() {
		return additionalFieldsSonTagName;
	}

	public static ServiceEnum getEnum(String serviceName) throws ReqDataException {
		ServiceEnum[] values = ServiceEnum.values();
		for (ServiceEnum serviceEnum : values) {
			if (serviceEnum.name().equals(serviceName)) {
				return serviceEnum;
			}
		}
		throw new ReqDataException("请求的核心系统方法不存在！");
	}
}
