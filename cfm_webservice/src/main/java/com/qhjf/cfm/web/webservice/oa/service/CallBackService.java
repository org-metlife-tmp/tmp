package com.qhjf.cfm.web.webservice.oa.service;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.config.DDHOAWSConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.webservice.tool.XmlTool;
import com.seeyon.AuthorityService;
import com.seeyon.AuthorityServicePortType;
import com.seeyon.MetlifeApplyPayService;
import com.seeyon.MetlifeApplyPayServicePortType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallBackService {
	
	private static Logger log = LoggerFactory.getLogger(CallBackService.class);

	private static DDHOAWSConfigSection ddhoawsConfigSection = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHOAWS);

	/**
	 * 推送
	 * OA单据付款状态
	 * @param originData
	 * @return
	 * @throws Exception
	 */
	public String callback(Record originData) throws Exception {
		AuthorityService factory = new AuthorityService(ddhoawsConfigSection.getAuthorityWsdl());
		factory.getAuthorityServiceHttpSoap11Endpoint();
		AuthorityServicePortType auth = factory.getAuthorityServiceHttpSoap11Endpoint();
		String token = auth.authenticate(ddhoawsConfigSection.getAuthorityUserName(), ddhoawsConfigSection.getAuthorityPassword()).getId().getValue();
		log.debug("token="+token);
		MetlifeApplyPayService factory2 = new MetlifeApplyPayService(ddhoawsConfigSection.getPushWsdl());
		MetlifeApplyPayServicePortType push = factory2.getMetlifeApplyPayServiceHttpSoap11Endpoint();
		String callBackMsg = genXml(originData);
		log.debug(originData.getStr("flow_id")+"回调信息="+callBackMsg);
		String result = push.pushStatus(token, callBackMsg);
		log.debug(originData.getStr("flow_id")+"回调结果="+result);
		return result;
	}
	
	public String genXml(Record originData){
		String interfaceFbCode = originData.getStr("interface_fb_code");
		if(interfaceFbCode == null){
			interfaceFbCode = "";
		}
		
		String interfaceFbMsg = originData.getStr("interface_fb_msg");
		if(interfaceFbMsg == null){
			interfaceFbMsg = "";
		}
		StringBuffer sb = new StringBuffer("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		sb.append("<oa>");
		sb.append("<flow_id>").append(originData.getStr("flow_id")).append("</flow_id>");
		sb.append("<send_count>").append(originData.getInt("send_count")).append("</send_count>");
		sb.append("<bill_status>").append(originData.getInt("interface_status")).append("</bill_status>");
		sb.append("<bill_err_code>").append(interfaceFbCode).append("</bill_err_code>");
		sb.append("<bill_err_message>").append(XmlTool.transferSpecialChar(interfaceFbMsg)).append("</bill_err_message>");
		sb.append("</oa>");
		return sb.toString();
	}
}
