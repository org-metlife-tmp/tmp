package com.qhjf.cfm.web.webservice.la.util;

import com.qhjf.cfm.web.config.DDHLAConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.webservice.la.LaCallbackBean;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import java.util.*;
import java.util.Map.Entry;

/**
 * 批付跳过EAI回调核心 util
 * @author CHT
 *
 */
public class LaPayOMElementSkipEAIUtil2 {
	private static DDHLAConfigSection config = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLA);
	
	
	/**
	 * 创建WS请求报文
	 * @param callbackBeans
	 * @return
	 * @throws Exception
	 */
	public OMElement createOMElement(List<LaCallbackBean> callbackBeans) throws Exception{
		OMFactory fac = OMAbstractFactory.getOMFactory();
		OMElement msgBodyNs = setBodyNs(fac,callbackBeans);
		return msgBodyNs;
	}

	/**
	 *  消息体参数设设置
	 * @param fac xml节点构造工厂
	 * @param callbackBeans	LA批收回调报文业务字段bean
	 * @return
	 */
	private OMElement setBodyNs(OMFactory fac, List<LaCallbackBean> callbackBeans) {
		
		OMNamespace prm = fac.createOMNamespace("http://www.csc.smart/bo/schemas/PMTUPDI", "pmt");
		OMElement method = fac.createOMElement("PMTUPDI_REC", prm);

		OMElement mspContext = setmspMSPContext(fac, callbackBeans);
		method.addChild(mspContext);

		OMElement additionalFields = setAdditionalFields(fac, callbackBeans);
		method.addChild(additionalFields);

		return method;
	}
	
	private OMElement setmspMSPContext(OMFactory fac, List<LaCallbackBean> callbackBeans){
		OMNamespace msp = fac.createOMNamespace("http://www.csc.smart/msp/schemas/MSPContext", "msp");
		OMElement mspContextNs = fac.createOMElement("MSPContext", msp);

		OMElement userId = fac.createOMElement("UserId", msp);
		userId.setText(config.getUserId());
		OMElement userPassword = fac.createOMElement("UserPassword", msp);
		userPassword.setText("");
		
		
		OMElement requestparameters = fac.createOMElement("RequestParameters", msp);

		OMElement branch = fac.createOMElement("RequestParameter", msp);
		branch.addAttribute("name", "BRANCH", null);
		branch.addAttribute("value", callbackBeans.get(0).getBranch(), null);
		OMElement company = fac.createOMElement("RequestParameter", msp);
		company.addAttribute("name", "COMPANY", null);
		company.addAttribute("value", callbackBeans.get(0).getCompany(), null);
		OMElement language = fac.createOMElement("RequestParameter", msp);
		language.addAttribute("name", "LANGUAGE", null);
		language.addAttribute("value", "S", null);
		OMElement commitflag = fac.createOMElement("RequestParameter", msp);
		commitflag.addAttribute("name", "COMMIT_FLAG", null);
		commitflag.addAttribute("value", "Y", null);
		OMElement ignoredriverheld = fac.createOMElement("RequestParameter", msp);
		ignoredriverheld.addAttribute("name", "IGNORE_DRIVER_HELD", null);
		ignoredriverheld.addAttribute("value", "Y", null);
		OMElement xsuppressrclrsc = fac.createOMElement("RequestParameter", msp);
		xsuppressrclrsc.addAttribute("name", "SUPPRESS_RCLRSC", null);
		xsuppressrclrsc.addAttribute("value", "N", null);
		requestparameters.addChild(branch);
		requestparameters.addChild(company);
		requestparameters.addChild(language);
		requestparameters.addChild(commitflag);
		requestparameters.addChild(ignoredriverheld);
		requestparameters.addChild(xsuppressrclrsc);
		
		mspContextNs.addChild(userId);
		mspContextNs.addChild(userPassword);
		mspContextNs.addChild(requestparameters);
		return mspContextNs;
	}
	/**
	 * 设置 ADDITIONAL_FIELDS xml节点
	 * @param fac
	 * @param callbackBeans
	 * @return
	 */
	private OMElement setAdditionalFields(OMFactory fac, List<LaCallbackBean> callbackBeans) {
		OMElement additionalFields = fac.createOMElement("ADDITIONAL_FIELDS", null);

		for (LaCallbackBean callbackBean : callbackBeans) {
			OMElement drninrecs = fac.createOMElement("PMTIN", null);
			Map<String, String> kvs = callbackBean.toMap();
			addChildList(fac, drninrecs, kvs);
			additionalFields.addChild(drninrecs);
		}

		return additionalFields;
	}
	
	private void addChildList(OMFactory fac, OMElement parent, Map<String, String> kvs){
		if (kvs.size() > 0) {
			Set<Entry<String,String>> entrySet = kvs.entrySet();
			for (Entry<String, String> kv : entrySet) {
				if ("id".equals(kv.getKey())) {
					continue;
				}
				addChild(fac, parent, kv.getKey(), kv.getValue());
			}
		}
	}
	
	private void addChild(OMFactory fac, OMElement parent, String key, String value){
		OMElement omElement = fac.createOMElement(key, null);
		omElement.setText(value);
		parent.addChild(omElement);
	}
}
