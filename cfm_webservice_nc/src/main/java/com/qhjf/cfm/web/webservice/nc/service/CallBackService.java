package com.qhjf.cfm.web.webservice.nc.service;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.config.DDHNCWSConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.webservice.client.AuthorPortType;
import com.qhjf.cfm.web.webservice.client.AuthorService;
import com.qhjf.cfm.web.webservice.tool.XmlTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;

public class CallBackService {
	
	private static Logger log = LoggerFactory.getLogger(CallBackService.class);

	private static DDHNCWSConfigSection ddhNCWSConfigSection = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHNCWS);

	/**
	 * 推送
	 * OA单据付款状态
	 * @param originData
	 * @return
	 * @throws Exception
	 */
	public String callback(Record originData,Record tranRecord) throws Exception {
		AuthorService factory = new AuthorService(new URL(ddhNCWSConfigSection.getPushWsdl()));
		AuthorPortType push =  factory.getAuthorPortName();
		String callBackMsg = genXml(originData);
		log.debug(originData.getStr("flow_id")+"回调信息="+callBackMsg);
		String result = push.payData(callBackMsg);
		log.debug(originData.getStr("flow_id")+"回调结果="+result);
//		NCCallBackResp resp = null;
//		try{
//			resp = new NCCallBackResp(result);
//			String flowid=resp.getFlow_id();
//			if("1".equals(resp.getStatus())&& WebConstant.OaInterfaceStatus.OA_INTER_PROCESS_S.getKey()==originData.getInt("interface_status")
//					&& WebConstant.OaProcessStatus.OA_TRADE_SUCCESS.getKey()==originData.getInt("process_status")
//			&&tranRecord!=null){
//				try {
//					// 生成凭证信息
//					log.info("NC【"+flowid+"】的数据回调成功，生成凭证---begin");
//					CheckVoucherService.ncHeadCheckVoucher(originData,tranRecord);
//					log.info("NC【"+flowid+"】的数据回调成功，生成凭证---end");
//				} catch (BusinessException e) {
//					log.error("NC响应回写原始数据，NC接收成功，生成凭证失败！", e.getMessage());
//					e.printStackTrace();
//				}
//			}
//		}catch (Exception e){
//			e.printStackTrace();
//			log.debug(originData.getStr("flow_id")+"回调结果="+e.getMessage());
//		}
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
