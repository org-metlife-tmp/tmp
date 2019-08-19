package com.la.test;


import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.webservice.la.recv.LaRecvCallbackBean;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

public class TestRecv {
	private static Map<String, String> headKv = new LinkedHashMap<>();
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
		headKv.put("SrvOpName", "DRNService");//"DRNService"
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
	
	public List<LaRecvCallbackBean> getTestData(){
		List<LaRecvCallbackBean> callbackBeans = new ArrayList<>();
		Record origin = new Record();
		origin.set("id", 21345l);
		origin.set("branch_code", "1");
		origin.set("org_code", "SH");
		origin.set("fee_mode", "2");
		origin.set("recv_date", "2");
		origin.set("amount", "2");
		origin.set("pay_acc_no", "6222000200129579199           ");
		origin.set("pay_bank_name", "2");
		origin.set("insure_bill_no", "2");
		origin.set("pay_code", "2");
		origin.set("pay_acc_name", "张艳涛                        ");
		origin.set("insure_bill_no", "2");
		origin.set("job_no", "2");
		origin.set("trans_code", "2");
		origin.set("sacscode", "2");
		origin.set("sacstyp", "2");
		origin.set("tmp_status", 2);
		origin.set("tmp_err_message", "测试错误信息");
		origin.set("bankcode", "2");
		try {
			LaRecvCallbackBean bean = new LaRecvCallbackBean(origin);
			callbackBeans.add(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return callbackBeans;
	}
		
	public void testCase() {
		try {
			ServiceClient sc = new ServiceClient();
			Options opts = new Options();   
			EndpointReference end = new EndpointReference("http://10.164.26.43/esbwebentry/ESBWebEntry.asmx?wsdl");   
			opts.setTo(end); 
			opts.setAction("http://eai.metlife.com/ESBWebEntry/ProcessMessage");
			sc.setOptions(opts);
			
			OMFactory fac = OMAbstractFactory.getOMFactory();

			//1 ProcessMessage
			OMNamespace processNs = fac.createOMNamespace("http://eai.metlife.com/","");
			OMElement processMessage = fac.createOMElement("ProcessMessage", processNs);  

			//1.1 Envelope
			OMNamespace soapenvNs = fac.createOMNamespace("http://MetLifeEAI.EAISchema","");  
			OMElement envelopeNs = fac.createOMElement("ESBEnvelope",soapenvNs);
			
			//1.1.1 soapenv:Header
			OMElement headerNs = setHeaderNs(fac);
			//1.1.2 soapenv:Body
			OMElement msgBodyNs = setBodyNs(fac,getTestData());
			envelopeNs.addChild(headerNs);
			envelopeNs.addChild(msgBodyNs);

			processMessage.addChild(envelopeNs);
			System.out.println(processMessage);
			//发送请求LA服务
			OMElement res = sc.sendReceive(processMessage);
			System.out.println(res);
		} catch (AxisFault e) {
			e.printStackTrace();
		} 
		
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
		OMNamespace processNs = fac.createOMNamespace("http://eai.metlife.com/","");
		OMElement processMessage = fac.createOMElement("ProcessMessage", processNs);  

		//1.1 Envelope
		OMNamespace soapenvNs = fac.createOMNamespace("http://MetLifeEAI.EAISchema","");  
		OMElement envelopeNs = fac.createOMElement("ESBEnvelope",soapenvNs);
		
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
		OMElement headerNs = fac.createOMElement("ESBHeader", null);
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
		OMElement msgBody = fac.createOMElement("MsgBody", null);
		
		//1.1.2.1 要调用的接口方法名称
		OMNamespace drn = fac.createOMNamespace("http://www.csc.smart/bo/schemas/DRNADDI", "drn");
		OMElement drnaddiRec = fac.createOMElement("DRNADDI_REC", drn);

		// 1.1.2.1.1 msp:MSPContext	第一个方法参数
		OMElement mspContext = setmspMSPContext(fac, callbackBeans);
		drnaddiRec.addChild(mspContext);

		// 1.1.2.1.2 ADDITIONAL_FIELDS	第二个方法参数
		OMElement additionalFields = setAdditionalFields(fac, callbackBeans);
		drnaddiRec.addChild(additionalFields);

		msgBody.addChild(drnaddiRec);
		return msgBody;
	}
	
	private OMElement setmspMSPContext(OMFactory fac, List<LaRecvCallbackBean> callbackBeans){
		OMNamespace msp = fac.createOMNamespace("http://www.csc.smart/msp/schemas/MSPContext", "msp");
		OMElement mspContextNs = fac.createOMElement("MSPContext", msp);
		
		OMElement userid = fac.createOMElement("UserId", msp);
		userid.setText("MSP");//测试：MSP；生产：MSPNCLA
		OMElement userpassword = fac.createOMElement("UserPassword", msp);
		userpassword.setText("");
		
		
		
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
		
		mspContextNs.addChild(userid);
		mspContextNs.addChild(userpassword);
		mspContextNs.addChild(requestparameters);
		return mspContextNs;
	}
	/**
	 * 设置 ADDITIONAL_FIELDS xml节点
	 * @param fac
	 * @param callbackBeans
	 * @return
	 */
	private OMElement setAdditionalFields(OMFactory fac, List<LaRecvCallbackBean> callbackBeans) {
		OMElement additionalFields = fac.createOMElement("ADDITIONAL_FIELDS", null);

		for (LaRecvCallbackBean callbackBean : callbackBeans) {
			OMElement drninrecs = fac.createOMElement("DRNINRECS", null);
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
				addChild(fac, parent, kv.getKey(), kv.getValue());
			}
		}
	}
	
	private void addChild(OMFactory fac, OMElement parent, String key, String value){
		OMElement omElement = fac.createOMElement(key, null);
		omElement.setText(value);
		parent.addChild(omElement);
	}
	
	public static void main(String[] args) {
		TestRecv test =new TestRecv();
		test.testCase();
	}
}
