package com.qhjf.cfm.web.webservice.la;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.utils.XmlTool;
import com.qhjf.cfm.web.config.DDHLAConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.webservice.la.queue.LaProductQueue;
import com.qhjf.cfm.web.webservice.la.queue.LaQueueBean;

public class LaCallback {

	private static Logger log = LoggerFactory.getLogger(LaCallback.class);
	private static DDHLAConfigSection config = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLA);

	public void callBack(List<Record> records) {
		if (records == null || records.size() == 0) {
			log.debug("回写LA数据为空,不进行回写");
			return;
		}
		log.debug("回写LA,将回写数据写至队列开始。。。。。。。。。");
		int batchNum = config.getBatchNum();
		List<LaCallbackBean> callbackBeans = new ArrayList<LaCallbackBean>();
		for (Record origin : records) {
			try {
				LaCallbackBean bean = createBean(origin);
				callbackBeans.add(bean);
				if (callbackBeans.size() == batchNum) {
					OMElement oMElement = createOMElement(callbackBeans);
					LaQueueBean queueBean = new LaQueueBean(callbackBeans, oMElement);
					LaProductQueue productQueue = new LaProductQueue(queueBean);
					new Thread(productQueue).start();
					callbackBeans = new ArrayList<LaCallbackBean>();
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		if (callbackBeans.size() > 0) {
			OMElement oMElement;
			try {
				oMElement = createOMElement(callbackBeans);
				LaQueueBean queueBean = new LaQueueBean(callbackBeans, oMElement);
				LaProductQueue productQueue = new LaProductQueue(queueBean);
				new Thread(productQueue).start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		log.debug("回写LA,将回写数据写至队列结束。。。。。。。。。");
	}

	private LaCallbackBean createBean(Record origin) throws Exception {
		LaCallbackBean bean = new LaCallbackBean(origin);
		int updNum = Db.update(Db.getSql("webservice_la_cfm.updateCallbackingStatus"),
				WebConstant.SftCallbackStatus.SFT_CALLBACKING.getKey(), new Date(), origin.getLong("id"),
				WebConstant.SftCallbackStatus.SFT_NO_CALLBACK.getKey(),
				WebConstant.SftCallbackStatus.SFT_CALLBACK_F.getKey(), origin.getStr("persist_version"));
		if (updNum != 1) {
			throw new Exception(origin.getLong("id") + "修改原始单据状态失败,不回写LA");
		}
		return bean;
	}

	private OMElement createOMElement(List<LaCallbackBean> callbackBeans) throws Exception{
		/*ServiceClient sc = new ServiceClient();
		Options opts = new Options();
		EndpointReference end = new EndpointReference(config.getUrl());
		opts.setTo(end);
		opts.setAction("http://eai.metlife.com/ESBWebEntry/ProcessMessage");
		sc.setOptions(opts);*/

		OMFactory fac = OMAbstractFactory.getOMFactory();

		OMNamespace soapenvNs = fac.createOMNamespace("http://MetLifeEAI.EAISchema", "");

		// OMNamespace headNs =
		// fac.createOMNamespace("http://www.w3.org/2001/XMLSchema","");
		OMNamespace processNs = fac.createOMNamespace("http://eai.metlife.com/", "");
		OMElement processMessage = fac.createOMElement("ProcessMessage", processNs);

		OMElement envelopeNs = fac.createOMElement("ESBEnvelope", soapenvNs);

		OMElement headerNs = setHeaderNs(fac);
		OMElement msgBodyNs = setBodyNs(fac,callbackBeans);
		envelopeNs.addChild(headerNs);
		envelopeNs.addChild(msgBodyNs);

		processMessage.addChild(envelopeNs);
		System.out.println(processMessage);
		return processMessage;
	}

	// 请求头部参数设置
	private OMElement setHeaderNs(OMFactory fac) {
		OMElement headerNs = fac.createOMElement("ESBHeader", null);

		OMElement esbhaherrNs = fac.createOMElement("ESBHdVer", null);
		esbhaherrNs.setText("20120608_1.1");

		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		OMElement srvDateNs = fac.createOMElement("SrvDate", null);
		srvDateNs.setText(dateString);

		SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm:ss");
		Date currentTime1 = new Date();
		String dateString1 = formatter1.format(currentTime1);
		OMElement srvTimeNs = fac.createOMElement("SrvTime", null);
		srvTimeNs.setText(dateString1);

		OMElement senderIdNs = fac.createOMElement("SenderID", null);
		senderIdNs.setText("TMP");

		OMElement receiverNs = fac.createOMElement("ReceiverID", null);
		receiverNs.setText("LA");

		OMElement SrvOpName = fac.createOMElement("SrvOpName", null);
		SrvOpName.setText("PMTService");

		OMElement SrvOpVer = fac.createOMElement("SrvOpVer", null);
		SrvOpVer.setText("20120606_1.1");

		OMElement MsgID = fac.createOMElement("MsgID", null);
		MsgID.setText(UUID.randomUUID().toString());
		OMElement CorrID = fac.createOMElement("CorrID", null);
		CorrID.setText(UUID.randomUUID().toString());

		OMElement ESBRspCode = fac.createOMElement("ESBRspCode", null);
		ESBRspCode.setText("0");

		OMElement ESBRspDec = fac.createOMElement("ESBRspDec", null);
		ESBRspDec.setText("Success");

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

	private OMElement setBodyNs(OMFactory fac,List<LaCallbackBean> callbackBeans) {
		OMElement msgBody = fac.createOMElement("MsgBody", null);

		OMNamespace prm = fac.createOMNamespace("http://www.csc.smart/bo/schemas/PMTUPDI", "prm");
		OMElement method = fac.createOMElement("PMTUPDI_REC", prm);

		// 额外参数
		OMNamespace mspNs = fac.createOMNamespace("http://www.csc.smart/msp/schemas/MSPContext", "msp");

		OMElement mspContextNs = fac.createOMElement("MSPContext", mspNs);

		OMElement userId = fac.createOMElement("UserId", mspNs);
		// userId.setText("DXC170");
		userId.setText(config.getUserId());
		OMElement userPassword = fac.createOMElement("UserPassword", mspNs);
		// userPassword.setText("DXC170");
		userPassword.setText(config.getUserPassword());

		OMElement requestParametersNs = fac.createOMElement("RequestParameters", mspNs);

		OMElement branch = fac.createOMElement("RequestParameter", mspNs);
		branch.addAttribute("name", "BRANCH", null);
		branch.addAttribute("value", callbackBeans.get(0).getBranch(), null);
		OMElement company = fac.createOMElement("RequestParameter", mspNs);
		company.addAttribute("name", "COMPANY", null);
		company.addAttribute("value", callbackBeans.get(0).getCompany(), null);
		OMElement language = fac.createOMElement("RequestParameter", mspNs);
		language.addAttribute("name", "LANGUAGE", null);
		language.addAttribute("value", "S", null);
		OMElement commitflag = fac.createOMElement("RequestParameter", mspNs);
		commitflag.addAttribute("name", "COMMIT_FLAG", null);
		commitflag.addAttribute("value", "Y", null);
		OMElement ignoredriverheld = fac.createOMElement("RequestParameter", mspNs);
		ignoredriverheld.addAttribute("name", "IGNORE_DRIVER_HELD", null);
		ignoredriverheld.addAttribute("value", "Y", null);
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

		method.addChild(mspContextNs);

		// 设置参数
		OMElement polenq2i = fac.createOMElement("ADDITIONAL_FIELDS", null);
		
		for(LaCallbackBean callbackBean : callbackBeans){
			OMElement pmtin = fac.createOMElement("PMTIN", null);

			OMElement company1 = fac.createOMElement("COMPANY", null);
			company1.setText(callbackBean.getCompany());
			OMElement branch1 = fac.createOMElement("BRANCH", null);
			branch1.setText(callbackBean.getBranch());
			OMElement reqnno = fac.createOMElement("REQNNO", null);
			reqnno.setText(callbackBean.getReqnno());
			OMElement stflag = fac.createOMElement("STFLAG", null);
			stflag.setText(callbackBean.getStflag());
			OMElement txtline = fac.createOMElement("TXTLINE", null);
			txtline.setText(callbackBean.getTxtline());

			pmtin.addChild(company1);
			pmtin.addChild(branch1);
			pmtin.addChild(reqnno);
			pmtin.addChild(stflag);
			pmtin.addChild(txtline);

			polenq2i.addChild(pmtin);
		}

		
		method.addChild(polenq2i);

		msgBody.addChild(method);
		return msgBody;
	}
	
	

}
