package com.qhjf.cfm.web.webservice.la.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.Map.Entry;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;

import com.qhjf.cfm.web.config.DDHLARecvConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.webservice.la.recv.LaRecvCallbackBean;

/**
 * bobo给的报文格式
 * @author CHT
 *
 */
public class LaRecvOMElementUtil {
	private static DDHLARecvConfigSection config = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLaRecv);
	
	private static Map<String, String> headKv = new HashMap<>();
	static{
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		headKv.put("SrvDate", dateString);
		
		SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm:ss");
		String dateString1 = formatter1.format(currentTime);
		headKv.put("SrvTime", dateString1);
		
		headKv.put("SenderID", "TMP");
		headKv.put("ReceiverID", "LA");
		headKv.put("SrvOpName", config.getSrvOpName());
		headKv.put("SrvOpVer", "20120606_1.1");
		headKv.put("MsgID", UUID.randomUUID().toString());
		headKv.put("CorrID", UUID.randomUUID().toString());
		headKv.put("ESBRspCode", "0");
		headKv.put("ESBRspDec", "Success");
		headKv.put("ResField1", null);
		headKv.put("ResField2", null);
		headKv.put("ResField3", null);
		headKv.put("ResField4", null);
		headKv.put("ResField5", null);
	}
	/**
	 * 创建WS请求报文
	 * @param callbackBeans
	 * @return
	 * @throws Exception
	 */
	public OMElement createOMElement(List<LaRecvCallbackBean> callbackBeans) throws Exception{
		OMFactory fac = OMAbstractFactory.getOMFactory();

		//1 ProcessMessage
		OMNamespace processNs = fac.createOMNamespace("http://eai.metlife.com/", "");
		OMElement processMessage = fac.createOMElement("ProcessMessage", processNs);

		//1.1 Envelope
		OMElement envelopeNs = fac.createOMElement("Envelope", null);
		OMNamespace soapenvNs = fac.createOMNamespace("http://schemas.xmlsoap.org/soap/envelope/", "soapenv");
		OMNamespace drn = fac.createOMNamespace("http://www.csc.smart/bo/schemas/DRNADDI", "drn");
		OMNamespace msp = fac.createOMNamespace("http://www.csc.smart/msp/schemas/MSPContext", "msp");
		envelopeNs.setNamespace(msp);
		envelopeNs.setNamespace(drn);
		envelopeNs.setNamespace(soapenvNs);

		//1.1.1 soapenv:Header
		OMElement headerNs = setHeaderNs(fac);
		//1.1.2 soapenv:Body
		OMElement msgBodyNs = setBodyNs(fac,callbackBeans);
		envelopeNs.addChild(headerNs);
		envelopeNs.addChild(msgBodyNs);

		processMessage.addChild(envelopeNs);
		System.out.println(processMessage);
		return processMessage;
	}

	/**
	 * 请求头部参数设置
	 * @param fac xml节点构造工厂
	 * @return
	 */
	private OMElement setHeaderNs(OMFactory fac) {
		OMElement headerNs = fac.createOMElement("soapenv:Header", null);
		addChildList(fac, headerNs, headKv);
		return headerNs;
	}

	/**
	 *  消息体参数设设置
	 * @param fac xml节点构造工厂
	 * @param callbackBeans	LA批收回调报文业务字段bean
	 * @return
	 */
	private OMElement setBodyNs(OMFactory fac, List<LaRecvCallbackBean> callbackBeans) {
		//1.1.2 soapenv:Body
		OMElement msgBody = fac.createOMElement("soapenv:Body", null);
		
		//1.1.2.1 要调用的接口方法名称
		OMElement method = fac.createOMElement("drn:DRNADDI_REC", null);

		// 1.1.2.1.1 msp:MSPContext	第一个方法参数
		OMElement mspContext = setmspMSPContext(fac, callbackBeans);
		method.addChild(mspContext);

		// 1.1.2.1.2 ADDITIONAL_FIELDS	第二个方法参数
		OMElement additionalFields = setAdditionalFields(fac, callbackBeans);
		method.addChild(additionalFields);

		msgBody.addChild(method);
		return msgBody;
	}
	
	private OMElement setmspMSPContext(OMFactory fac, List<LaRecvCallbackBean> callbackBeans){
		OMElement mspContextNs = fac.createOMElement("msp:MSPContext", null);
		
		OMElement mspUserId = fac.createOMElement("msp:UserId", null);
		mspUserId.setText(config.getUserId());
		
		OMElement mspUserPassword = fac.createOMElement("msp:UserPassword", null);
		mspUserPassword.setText(config.getUserPassword());
		
		OMElement mspRequestParameters = fac.createOMElement("msp:RequestParameters", null);
		OMElement mspRequestParameter = fac.createOMElement("msp:RequestParameter", null);
		mspRequestParameter.addAttribute("name", "BRANCH", null);
		mspRequestParameter.addAttribute("value", callbackBeans.get(0).getBranch(), null);
		mspRequestParameters.addChild(mspRequestParameter);
		
		mspContextNs.addChild(mspUserId);
		mspContextNs.addChild(mspUserPassword);
		mspContextNs.addChild(mspRequestParameters);
		return mspContextNs;
	}
	/**
	 * 设置 ADDITIONAL_FIELDS xml节点
	 * @param fac
	 * @param callbackBeans
	 * @return
	 */
	private OMElement setAdditionalFields(OMFactory fac, List<LaRecvCallbackBean> callbackBeans){
		OMElement additionalFields = fac.createOMElement("ADDITIONAL_FIELDS", null);
		
		OMElement drninrecs = fac.createOMElement("DRNINRECS", null);
		
		for(LaRecvCallbackBean callbackBean : callbackBeans){
			Map<String, String> kvs = callbackBean.toMap();
			addChildList(fac, drninrecs, kvs);
		}
		
		additionalFields.addChild(drninrecs);
		return additionalFields;
	}
	
	private void addChildList(OMFactory fac, OMElement parent, Map<String, String> kvs){
		if (kvs.size() > 0) {
			Set<Entry<String,String>> entrySet = kvs.entrySet();
			for (Entry<String, String> kv : entrySet) {
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
