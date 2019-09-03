package com.qhjf.cfm.web.webservice.la.recv;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.config.DDHLARecvConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.webservice.la.queue.noResp.LaNoRespProductQueue;
import com.qhjf.cfm.web.webservice.la.queue.noResp.LaNoRespQueueBean;
import com.qhjf.cfm.web.webservice.la.queue.recv.LaRecvProductQueue;
import com.qhjf.cfm.web.webservice.la.queue.recv.LaRecvQueueBean;
import com.qhjf.cfm.web.webservice.la.util.LaRecvOMElementSkipEAIUtil;
import com.qhjf.cfm.web.webservice.la.util.LaRecvOMElementUtil;
import org.apache.axiom.om.OMElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * LA回调核心系统工具
 * 
 * @author CHT
 *
 */
public class LaRecvCallback {
	private static Logger log = LoggerFactory.getLogger(LaRecvCallback.class);
	private static DDHLARecvConfigSection config = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLaRecv);
//	private static final LaRecvOMElementOldUtil oldUtil= new LaRecvOMElementOldUtil();
	private static final LaRecvOMElementUtil util= new LaRecvOMElementUtil();
//	private static final LaRecvOMElementSkipEAIUtil util= new LaRecvOMElementSkipEAIUtil();

	/**
	 * 
	 * @param records
	 *            LA批收原始数据
	 */
	public void callBack(List<Record> records) {
		if (records == null || records.size() == 0) {
			log.debug("回调LA批收核心系统,LA批收原始数据为空,不进行回调。");
			return;
		}
		
		log.debug("回调LA批收核心系统,将回写数据写至队列开始。。。。。。。。。");
		int batchNum = config.getBatchNum();
		List<LaRecvCallbackBean> callbackBeans = new ArrayList<LaRecvCallbackBean>();
		for (Record origin : records) {
			try {
				LaRecvCallbackBean bean = createBean(origin);
				callbackBeans.add(bean);
				if (callbackBeans.size() == batchNum) {
					OMElement oMElement = util.createOMElement(callbackBeans);
					LaRecvQueueBean queueBean = new LaRecvQueueBean(callbackBeans, oMElement);
					LaRecvProductQueue productQueue = new LaRecvProductQueue(queueBean);
					new Thread(productQueue).start();
					callbackBeans = new ArrayList<LaRecvCallbackBean>();
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		if (callbackBeans.size() > 0) {
			OMElement oMElement;
			try {
				oMElement = util.createOMElement(callbackBeans);
				LaRecvQueueBean queueBean = new LaRecvQueueBean(callbackBeans, oMElement);
				LaRecvProductQueue productQueue = new LaRecvProductQueue(queueBean);
				new Thread(productQueue).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		log.debug("回调LA批收核心系统,将回写数据写至队列结束。。。。。。。。。");
	}
	/**
	 *
	 * @param record
	 *            LA批收单笔回调
	 */
	public void callBack(Record record) {
		if (record == null ) {
			log.debug("回调LA批收核心系统,LA批收原始数据为空,不进行回调。");
			return;
		}

		log.debug("回调LA批收核心系统,将回写数据写至队列开始。。。。。。。。。");
		List<LaRecvCallbackBean> callbackBeans = new ArrayList<LaRecvCallbackBean>();
		try {
			LaRecvCallbackBean bean = createBean(record);
			callbackBeans.add(bean);
			OMElement oMElement = util.createOMElement(callbackBeans);
			LaNoRespQueueBean queueBean = new LaNoRespQueueBean(callbackBeans, oMElement);
			LaNoRespProductQueue productQueue = new LaNoRespProductQueue(queueBean);
			new Thread(productQueue).start();
		} catch (Exception e) {
			e.printStackTrace();
		}
		log.debug("回调LA批收核心系统,将回写数据写至队列结束。。。。。。。。。");
	}

	/**
	 * 根据原始数据创建LA批收回调核心系统的Bean
	 * @param origin	LA批收原始数据
	 * @return
	 * @throws Exception
	 */
	private LaRecvCallbackBean createBean(Record origin) throws Exception {
		LaRecvCallbackBean bean = new LaRecvCallbackBean(origin);
		int updNum = Db.update(Db.getSql("webservice_la_recv_cfm.updateCallbackingStatus"),
				WebConstant.SftCallbackStatus.SFT_CALLBACKING.getKey(), 
				new Date(), 
				origin.getLong("id"),
				WebConstant.SftCallbackStatus.SFT_NO_CALLBACK.getKey(),
				WebConstant.SftCallbackStatus.SFT_CALLBACK_F.getKey(), 
				origin.getStr("persist_version"));
		if (updNum != 1) {
			throw new Exception(origin.getLong("id") + "：构造回调bean后，修改批收原始单据状态失败,不回写LA");
		}
		return bean;
	}

	/**
	 * 创建WS请求报文
	 * @param callbackBeans
	 * @return
	 * @throws Exception
	 *//*
	private OMElement createOMElement(List<LaRecvCallbackBean> callbackBeans) throws Exception{
		ServiceClient sc = new ServiceClient();
		Options opts = new Options();
		EndpointReference end = new EndpointReference(config.getUrl());
		opts.setTo(end);
		opts.setAction("http://eai.metlife.com/ESBWebEntry/ProcessMessage");
		sc.setOptions(opts);

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

	*//**
	 * 请求头部参数设置
	 * @param fac xml节点构造工厂
	 * @return
	 *//*
	private OMElement setHeaderNs(OMFactory fac) {
		//1.1.1 soapenv:Header
		OMElement headerNs = fac.createOMElement("soapenv:Header", null);

		//1.1.1.1 ESBHdVer
		OMElement esbhaherrNs = fac.createOMElement("ESBHdVer", null);
		esbhaherrNs.setText("20120608_1.1");

		//1.1.1.2 SrvDate
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		OMElement srvDateNs = fac.createOMElement("SrvDate", null);
		srvDateNs.setText(dateString);

		//1.1.1.3 SrvTime
		SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm:ss");
		Date currentTime1 = new Date();
		String dateString1 = formatter1.format(currentTime1);
		OMElement srvTimeNs = fac.createOMElement("SrvTime", null);
		srvTimeNs.setText(dateString1);

		//1.1.1.4 SenderID
		OMElement senderIdNs = fac.createOMElement("SenderID", null);
		senderIdNs.setText("TMP");

		//1.1.1.5 ReceiverID
		OMElement receiverNs = fac.createOMElement("ReceiverID", null);
		receiverNs.setText("LA");

		//1.1.1.6 SrvOpName
		//批收与批付不一样，需要修改
		OMElement SrvOpName = fac.createOMElement("SrvOpName", null);
		SrvOpName.setText(config.getSrvOpName());

		//1.1.1.7 SrvOpVer
		OMElement SrvOpVer = fac.createOMElement("SrvOpVer", null);
		SrvOpVer.setText("20120606_1.1");

		//1.1.1.8 MsgID
		OMElement MsgID = fac.createOMElement("MsgID", null);
		MsgID.setText(UUID.randomUUID().toString());
		
		//1.1.1.9 CorrID
		OMElement CorrID = fac.createOMElement("CorrID", null);
		CorrID.setText(UUID.randomUUID().toString());

		//1.1.1.10 ESBRspCode
		OMElement ESBRspCode = fac.createOMElement("ESBRspCode", null);
		ESBRspCode.setText("0");

		//1.1.1.11 ESBRspDec
		OMElement ESBRspDec = fac.createOMElement("ESBRspDec", null);
		ESBRspDec.setText("Success");

		//1.1.1.12~1.1.1.16 预留字段
		OMElement ResField1 = fac.createOMElement("ResField1", null);
		OMElement ResField2 = fac.createOMElement("ResField2", null);
		OMElement ResField3 = fac.createOMElement("ResField3", null);
		OMElement ResField4 = fac.createOMElement("ResField4", null);
		OMElement ResField5 = fac.createOMElement("ResField5", null);

		headerNs.addChild(srvDateNs);
		headerNs.addChild(srvTimeNs);
		headerNs.addChild(senderIdNs);
		headerNs.addChild(receiverNs);
		headerNs.addChild(SrvOpName);
		headerNs.addChild(SrvOpVer);
		headerNs.addChild(MsgID);
		headerNs.addChild(CorrID);
		headerNs.addChild(ESBRspCode);
		headerNs.addChild(ESBRspDec);

		headerNs.addChild(ResField1);
		headerNs.addChild(ResField2);
		headerNs.addChild(ResField3);
		headerNs.addChild(ResField4);
		headerNs.addChild(ResField5);
		return headerNs;
	}

	*//**
	 *  消息体参数设设置
	 * @param fac xml节点构造工厂
	 * @param callbackBeans	LA批收回调报文业务字段bean
	 * @return
	 *//*
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
	*//**
	 * 设置msp:MSPContext xml节点
	 * @param fac
	 * @param callbackBeans
	 * @return
	 *//*
	private OMElement setMSPContext(OMFactory fac, List<LaRecvCallbackBean> callbackBeans){
		OMNamespace mspNs = fac.createOMNamespace("http://www.csc.smart/msp/schemas/MSPContext", "msp");
		OMElement mspContextNs = fac.createOMElement("MSPContext", mspNs);

		//1.1.2.1.1.1 msp:UserId
		OMElement userId = fac.createOMElement("UserId", mspNs);
		userId.setText(config.getUserId());
		//1.1.2.1.1.2 msp:UserPassword
		OMElement userPassword = fac.createOMElement("UserPassword", mspNs);
		userPassword.setText(config.getUserPassword());

		//1.1.2.1.1.3 msp:RequestParameters
		OMElement requestParametersNs = fac.createOMElement("RequestParameters", mspNs);

		//1.1.2.1.1.3.1 name="BRANCH" ---> "org_code"
		OMElement branch = fac.createOMElement("RequestParameter", mspNs);
		branch.addAttribute("name", "BRANCH", null);
		branch.addAttribute("value", callbackBeans.get(0).getBranch(), null);
		//1.1.2.1.1.3.2 name="COMPANY" ---> "branch_code"
		OMElement company = fac.createOMElement("RequestParameter", mspNs);
		company.addAttribute("name", "COMPANY", null);
		company.addAttribute("value", callbackBeans.get(0).getCompany(), null);
		//1.1.2.1.1.3.3 name="LANGUAGE"
		OMElement language = fac.createOMElement("RequestParameter", mspNs);
		language.addAttribute("name", "LANGUAGE", null);
		language.addAttribute("value", "S", null);
		//1.1.2.1.1.3.4 name="COMMIT_FLAG"
		OMElement commitflag = fac.createOMElement("RequestParameter", mspNs);
		commitflag.addAttribute("name", "COMMIT_FLAG", null);
		commitflag.addAttribute("value", "Y", null);
		//1.1.2.1.1.3.5 name="IGNORE_DRIVER_HELD"
		OMElement ignoredriverheld = fac.createOMElement("RequestParameter", mspNs);
		ignoredriverheld.addAttribute("name", "IGNORE_DRIVER_HELD", null);
		ignoredriverheld.addAttribute("value", "Y", null);
		//1.1.2.1.1.3.5 name="SUPPRESS_RCLRSC"
		OMElement xsuppressrclrsc = fac.createOMElement("RequestParameter", mspNs);
		xsuppressrclrsc.addAttribute("name", "SUPPRESS_RCLRSC", null);
		xsuppressrclrsc.addAttribute("value", "N", null);

		requestParametersNs.addChild(branch);
		requestParametersNs.addChild(company);
		requestParametersNs.addChild(language);
		requestParametersNs.addChild(commitflag);
		requestParametersNs.addChild(ignoredriverheld);
		requestParametersNs.addChild(xsuppressrclrsc);

		mspContextNs.addChild(userId);
		mspContextNs.addChild(userPassword);
		mspContextNs.addChild(requestParametersNs);
		
		return mspContextNs;
	}
	*//**
	 * 设置 ADDITIONAL_FIELDS xml节点
	 * @param fac
	 * @param callbackBeans
	 * @return
	 *//*
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
	}*/
}
