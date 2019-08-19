package com.qhjf.cfm.web.webservice.la.eai.bean;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ESBHeaderBean {

//	private String ESBHdVer;
	private String SrvDate;
	private String SrvTime;
	private String SenderID;
	private String ReceiverID;
	private String SrvOpName;
	private String SrvOpVer;
	private String MsgID;
	private String CorrID;
	private String ESBRspCode;
	private String ESBRspDec;
	private String ResField1;
	private String ResField2;
	private String ResField3;
	private String ResField4;
	private String ResField5;
	
	/**
	 * 
	 * @param srvOpName 核心接口方法名
	 * 	PMTService：批付
	 *  POLService：柜面收单据查询
	 *  DRNService：
	 */
	public ESBHeaderBean(String srvOpName){
//		ESBHdVer = "20120608_1.1";
		SrvDate = getTodayDate();
		SrvTime = getCurrentTime();
		SenderID = "TMP";//资金平台
		ReceiverID = "LA";
		SrvOpName = srvOpName;
		SrvOpVer = "20120606_1.1";
		MsgID = UUID.randomUUID().toString();
		CorrID = UUID.randomUUID().toString();
		ESBRspCode = "0";
		ESBRspDec = "Success";
	}
	
	public Map<String, Object> toMap(){
		Map<String,Object> map = new LinkedHashMap<String,Object>();
//		map.put("ESBHdVer", ESBHdVer);
		map.put("SrvDate", SrvDate);
		map.put("SrvTime", SrvTime);
		map.put("SenderID", SenderID);
		map.put("ReceiverID", ReceiverID);
		map.put("SrvOpName", SrvOpName);
		map.put("SrvOpVer", SrvOpVer);
		map.put("MsgID", MsgID);
		map.put("CorrID", CorrID);
		map.put("ESBRspCode", ESBRspCode);
		map.put("ESBRspDec", ESBRspDec);
		map.put("ResField1", ResField1);
		map.put("ResField2", ResField2);
		map.put("ResField3", ResField3);
		map.put("ResField4", ResField4);
		map.put("ResField5", ResField5);
		return map;
	}
	
	private String getTodayDate(){
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(now);
	}
	
	private String getCurrentTime(){
		Date now = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(now);
	}
	
//	public String getESBHdVer() {
//		return ESBHdVer;
//	}

	public String getSrvDate() {
		return SrvDate;
	}

	public String getSrvTime() {
		return SrvTime;
	}

	public String getSenderID() {
		return SenderID;
	}

	public String getReceiverID() {
		return ReceiverID;
	}

	public String getSrvOpName() {
		return SrvOpName;
	}

	public String getSrvOpVer() {
		return SrvOpVer;
	}

	public String getMsgID() {
		return MsgID;
	}

	public String getCorrID() {
		return CorrID;
	}

	public String getESBRspCode() {
		return ESBRspCode;
	}

	public String getESBRspDec() {
		return ESBRspDec;
	}

	public String getResField1() {
		return ResField1;
	}

	public String getResField2() {
		return ResField2;
	}

	public String getResField3() {
		return ResField3;
	}

	public String getResField4() {
		return ResField4;
	}

	public String getResField5() {
		return ResField5;
	}

}
