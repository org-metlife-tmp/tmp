package com.qhjf.cfm.web.webservice.la.eai.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.jfinal.plugin.activerecord.Record;

/**
 * xml节点对象：标签、值、属性数组（属性名：属性值）
 * @author CHT
 *
 */
public class RequestParametersBean {

	private String nodeName = "RequestParameter";
	private List<Map<String, String>> attributes;
	
	/**
	 * 
	 * @param orgCode 机构
	 * @param branchCode 分公司
	 */
	public RequestParametersBean(String orgCode, String branchCode){
		attributes = new ArrayList<>();
		createAttrs(orgCode, branchCode);
	}
	
	private void createAttrs(String orgCode, String branchCode){
		attributes.add(createMpa("BRANCH", orgCode));
		attributes.add(createMpa("COMPANY", branchCode));
		attributes.add(createMpa("LANGUAGE", "S"));
		attributes.add(createMpa("COMMIT_FLAG", "Y"));
		attributes.add(createMpa("IGNORE_DRIVER_HELD", "Y"));
		attributes.add(createMpa("SUPPRESS_RCLRSC", "N"));
	}
	
	
	private Map<String, String> createMpa(String key, String value){
		Map<String, String> map = new HashMap<>();
		map.put("name", key);
		map.put("value", value);
		return map;
	}

	public String getNodeName() {
		return nodeName;
	}

	public List<Map<String, String>> getAttributes() {
		return attributes;
	}
	
}
