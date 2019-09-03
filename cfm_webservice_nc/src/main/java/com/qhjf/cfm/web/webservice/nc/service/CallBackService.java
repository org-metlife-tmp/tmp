package com.qhjf.cfm.web.webservice.nc.service;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.web.config.DDHNCWSConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.service.CheckVoucherService;
import com.qhjf.cfm.web.webservice.client.AuthorService;
import com.qhjf.cfm.web.webservice.client.AuthorServiceLocator;
import com.qhjf.cfm.web.webservice.client.AuthorServiceSoapBindingStub;
import com.qhjf.cfm.web.webservice.nc.server.request.NCReciveDateReq;
import com.qhjf.cfm.web.webservice.nc.server.response.NCReciveDataResp;
import com.qhjf.cfm.web.webservice.nc.server.response.parent.NCCallBackResp;
import com.qhjf.cfm.web.webservice.tool.XmlTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CallBackService {
	
	private static Logger log = LoggerFactory.getLogger(CallBackService.class);

	private static DDHNCWSConfigSection ddhNCWSConfigSection = new DDHNCWSConfigSection();

	/**
	 * 推送
	 * OA单据付款状态
	 * @param originData
	 * @return
	 * @throws Exception
	 */
	public String callback(Record originData,Record tranRecord) throws Exception {
		AuthorServiceLocator factory2 = new AuthorServiceLocator();
		AuthorServiceSoapBindingStub push = (AuthorServiceSoapBindingStub) factory2.getauthorPortName();
		String callBackMsg = genXml(originData);
		log.debug(originData.getStr("flow_id")+"回调信息="+callBackMsg);
		String result = push.payData(callBackMsg);
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
		sb.append("<newComp>");
		sb.append("<flow_id>").append(originData.getStr("flow_id")).append("</flow_id>");
		sb.append("<send_count>").append(originData.getInt("send_count")).append("</send_count>");
		sb.append("<bill_status>").append(originData.getInt("interface_status")).append("</bill_status>");
		sb.append("<bill_err_code>").append(interfaceFbCode).append("</bill_err_code>");
		sb.append("<bill_err_message>").append(XmlTool.transferSpecialChar(interfaceFbMsg)).append("</bill_err_message>");
		sb.append("</newComp>");
		return sb.toString();
	}
}
