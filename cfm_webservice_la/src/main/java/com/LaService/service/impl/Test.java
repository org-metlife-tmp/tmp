package com.LaService.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;

public class Test {
		
	public void testCase() {
		try {
			ServiceClient sc = new ServiceClient();
			Options opts = new Options();   
			EndpointReference end = new EndpointReference("http://10.164.26.43/esbwebentry/ESBWebEntry.asmx?wsdl");   
			opts.setTo(end); 
			opts.setAction("http://eai.metlife.com/ESBWebEntry/ProcessMessage");
			sc.setOptions(opts);
			
			OMFactory fac = OMAbstractFactory.getOMFactory();

			OMNamespace soapenvNs = fac.createOMNamespace("http://MetLifeEAI.EAISchema","");     
     
//			OMNamespace headNs = fac.createOMNamespace("http://www.w3.org/2001/XMLSchema","");
			OMNamespace processNs = fac.createOMNamespace("http://eai.metlife.com/","");
			OMElement processMessage = fac.createOMElement("ProcessMessage",processNs);    
			
			OMElement envelopeNs = fac.createOMElement("ESBEnvelope",soapenvNs);
			
			OMElement headerNs = setHeaderNs(fac);
			OMElement msgBodyNs = setBodyNs(fac);
			envelopeNs.addChild(headerNs);
			envelopeNs.addChild(msgBodyNs);
			
			processMessage.addChild(envelopeNs);
			System.out.println("ome="+processMessage);
			System.out.println("str="+processMessage.toString());
			//发送请求LA服务
			OMElement res = sc.sendReceive(processMessage);
			System.out.println(res);
		} catch (AxisFault e) {
			e.printStackTrace();
		} 
		
	}
	
	//请求头部参数设置
	private OMElement setHeaderNs(OMFactory fac) {
		OMElement headerNs = fac.createOMElement("ESBHeader",null); 
        
        OMElement esbhaherrNs = fac.createOMElement("ESBHdVer",null); 
        esbhaherrNs.setText("20120608_1.1");
        
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(currentTime);
        OMElement srvDateNs = fac.createOMElement("SrvDate",null); 
        srvDateNs.setText(dateString);
        
        SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm:ss");
        Date currentTime1 = new Date();
        String dateString1 = formatter1.format(currentTime1);
        OMElement srvTimeNs = fac.createOMElement("SrvTime",null); 
        srvTimeNs.setText(dateString1);
        
        OMElement senderIdNs = fac.createOMElement("SenderID",null); 
        senderIdNs.setText("TMP");
        
        OMElement receiverNs = fac.createOMElement("ReceiverID",null); 
        receiverNs.setText("LA");
        
        OMElement SrvOpName = fac.createOMElement("SrvOpName",null); 
        SrvOpName.setText("PMTService");
        
        OMElement SrvOpVer = fac.createOMElement("SrvOpVer",null); 
        SrvOpVer.setText("20120606_1.1");
        
        OMElement MsgID = fac.createOMElement("MsgID",null); 
        MsgID.setText(UUID.randomUUID().toString());
        OMElement CorrID = fac.createOMElement("CorrID",null); 
        CorrID.setText(UUID.randomUUID().toString());
        
        OMElement ESBRspCode = fac.createOMElement("ESBRspCode",null); 
        ESBRspCode.setText("0");
        
        OMElement ESBRspDec = fac.createOMElement("ESBRspDec",null); 
        ESBRspDec.setText("Success");
        
        OMElement ResField1 = fac.createOMElement("ResField1",null); 
        OMElement ResField2 = fac.createOMElement("ResField2",null); 
        OMElement ResField3 = fac.createOMElement("ResField3",null); 
        OMElement ResField4 = fac.createOMElement("ResField4",null); 
        OMElement ResField5 = fac.createOMElement("ResField5",null);
        
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
	
	private OMElement setBodyNs(OMFactory fac) {
		OMElement msgBody = fac.createOMElement("MsgBody",null); 
        
	    OMNamespace prm = fac.createOMNamespace("http://www.csc.smart/bo/schemas/PMTUPDI", "prm");     
	    OMElement method = fac.createOMElement("PMTUPDI_REC",prm);  
	         
	    //额外参数
	    OMNamespace mspNs = fac.createOMNamespace("http://www.csc.smart/msp/schemas/MSPContext", "msp");     
	        
	    OMElement mspContextNs = fac.createOMElement("MSPContext", mspNs);
	    
	    OMElement userId=fac.createOMElement("UserId", mspNs);
//	    		  userId.setText("DXC170");
	    		  userId.setText("MSP");
	    OMElement userPassword=fac.createOMElement("UserPassword", mspNs);
//	    		  userPassword.setText("DXC170");
	    		  userPassword.setText("");
	    		  
	    OMElement requestParametersNs=fac.createOMElement("RequestParameters", mspNs);
	    
	    OMElement branch=fac.createOMElement("RequestParameter", mspNs);
	    		  branch.addAttribute("name", "BRANCH", null);
	    		  branch.addAttribute("value", "SU", null);
	    OMElement company=fac.createOMElement("RequestParameter", mspNs);
	    		  company.addAttribute("name", "COMPANY", null);
	    		  company.addAttribute("value","2", null);
	    OMElement language=fac.createOMElement("RequestParameter", mspNs);
				  language.addAttribute("name", "LANGUAGE", null);
				  language.addAttribute("value", "S", null);	 
	    OMElement commitflag=fac.createOMElement("RequestParameter", mspNs);
				  commitflag.addAttribute("name", "COMMIT_FLAG", null);
				  commitflag.addAttribute("value", "Y", null);
	    OMElement ignoredriverheld=fac.createOMElement("RequestParameter", mspNs);
				  ignoredriverheld.addAttribute("name", "IGNORE_DRIVER_HELD", null);
				  ignoredriverheld.addAttribute("value", "Y", null);
		OMElement xsuppressrclrsc=fac.createOMElement("RequestParameter", mspNs);
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
	    
	  //设置参数
	    OMElement polenq2i = fac.createOMElement("ADDITIONAL_FIELDS",null); 
	    
	    OMElement pmtin = fac.createOMElement("PMTIN",null);  
	    
	    OMElement company1 = fac.createOMElement("COMPANY", null);
	    	company1.setText("2");
	    OMElement branch1 = fac.createOMElement("BRANCH", null);
	    	branch1.setText("SU");
	    OMElement reqnno = fac.createOMElement("REQNNO", null);
	    	reqnno.setText("222333");
	    OMElement stflag = fac.createOMElement("STFLAG", null);
	    	stflag.setText("Y");
	    OMElement txtline = fac.createOMElement("TXTLINE", null);
	    	txtline.setText("没有原因");

	    	pmtin.addChild(company1);
	    	pmtin.addChild(branch1);
	    	pmtin.addChild(reqnno);
	    	pmtin.addChild(stflag);
	    	pmtin.addChild(txtline);

    	polenq2i.addChild(pmtin);
	    method.addChild(polenq2i);
	    
	    msgBody.addChild(method);
		return msgBody;
	}
	
	public static void main(String[] args) {
		Test test =new Test();
		test.testCase();
	}
}
