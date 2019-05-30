package com.qhjf.cfm.web.webservice.la;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.utils.XmlTool;
import com.qhjf.cfm.web.constant.WebConstant;
import org.dom4j.DocumentException;

import java.util.HashMap;
import java.util.Map;

public class LaCallbackBean {
	
	private Long id;
	private String company;
	private String branch;
	private String reqnno;
	private String stflag;
	private String txtline;
	
	public LaCallbackBean(Record origin) throws Exception{
		this.id = origin.getLong("id");
		this.company = origin.getStr("branch_code");
		this.branch = origin.getStr("org_code");
		this.reqnno = origin.getStr("pay_code");
		if(origin.getInt("tmp_status") == WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_S.getKey()){
			this.stflag = "Y";
		}else if(origin.getInt("tmp_status") == WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_F.getKey()){
			this.stflag = "N";
			this.txtline = origin.getStr("tmp_err_message");
		}else{
			throw new Exception(origin.getLong("id")+"LA原始数据状态有误,无法回写,状态为"+origin.getInt("tmp_status"));
		}
	}
	
	public Map<String,String> toMap(){
		Map<String,String> map = new HashMap<String,String>();
		map.put("id", String.valueOf(this.id));
		map.put("COMPANY", this.company);
		map.put("BRANCH", this.branch);
		map.put("REQNNO", this.reqnno);
		map.put("STFLAG", this.stflag);
		map.put("TXTLINE", this.txtline);
		return map;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company;
	}
	public String getBranch() {
		return branch;
	}
	public void setBranch(String branch) {
		this.branch = branch;
	}
	public String getReqnno() {
		return reqnno;
	}
	public void setReqnno(String reqnno) {
		this.reqnno = reqnno;
	}
	public String getStflag() {
		return stflag;
	}
	public void setStflag(String stflag) {
		this.stflag = stflag;
	}
	public String getTxtline() {
		return txtline;
	}
	public void setTxtline(String txtline) {
		this.txtline = txtline;
	}
	
	public static void main(String[] args) throws DocumentException{
		String xml = "<ProcessMessageResponse xmlns='http://eai.metlife.com/'><ESBEnvelopeResult xmlns='http://MetLifeEAI.EAISchema'><ESBHeader xmlns=''><ESBHdVer>20120608_1.1</ESBHdVer><SrvDate>2019-01-29</SrvDate><SrvTime>01:49:01.4910000+08:00</SrvTime><SenderID>LA</SenderID><ReceiverID>TMP</ReceiverID><SrvOpName>PMTService</SrvOpName><SrvOpVer>20120606_1.1</SrvOpVer><MsgID>afc5477c-19e4-4a74-9338-a10cf33d8292</MsgID><CorrID>e66fa4ad-6e23-4935-b9d6-8d6cf00de3c8</CorrID><ESBRspCode>0</ESBRspCode><ESBRspDec>Success</ESBRspDec><ResField1></ResField1><ResField2></ResField2><ResField3></ResField3><ResField4></ResField4><ResField5></ResField5></ESBHeader><MsgBody xmlns=''><b:PMTUPDO_REC xmlns:b='http://www.csc.smart/bo/schemas/PMTUPDO' xmlns:a='http://www.csc.smart/bo/schemas/PMTUPDI' xmlns:c='http://www.csc.smart/msp/schemas/MSPContext' xmlns:d='http://www.csc.smart/bo/schemas/Error'><STATUS>0</STATUS><MORE_IND>N</MORE_IND><ADDITIONAL_FIELDS><PMTOUT><STATUS>FL</STATUS><REQNNO>000946141</REQNNO><FLDNAM>SN514-STFLAG</FLDNAM><ERRCODE>EV03</ERRCODE><ERRMESS>留空此项</ERRMESS></PMTOUT><PMTOUT><STATUS>FL</STATUS><REQNNO>000946141</REQNNO><FLDNAM>SN514-STFLAG</FLDNAM><ERRCODE>EV03</ERRCODE><ERRMESS>留空此项</ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT><PMTOUT><STATUS></STATUS><REQNNO></REQNNO><FLDNAM></FLDNAM><ERRCODE></ERRCODE><ERRMESS></ERRMESS></PMTOUT></ADDITIONAL_FIELDS></b:PMTUPDO_REC></MsgBody></ESBEnvelopeResult></ProcessMessageResponse>";
		JSONObject json = XmlTool.documentToJSONObject(xml);
		//JSONObject header = json.getJSONArray("ESBEnvelopeResult");
		JSONArray array = json.getJSONArray("ESBEnvelopeResult").getJSONObject(0).getJSONArray("MsgBody").getJSONObject(0).getJSONArray("PMTUPDO_REC").getJSONObject(0).getJSONArray("ADDITIONAL_FIELDS").getJSONObject(0).getJSONArray("PMTOUT");
		JSONObject rec = json.getJSONArray("ESBEnvelopeResult").getJSONObject(0).getJSONArray("MsgBody").getJSONObject(0).getJSONArray("PMTUPDO_REC").getJSONObject(0);
		System.out.println("status="+rec.getString("STATUS"));
		for(int i = 0;i<array.size();i++){
			System.out.println("STATUS="+array.getJSONObject(i).getString("STATUS"));
			System.out.println("REQNNO="+array.getJSONObject(i).getString("REQNNO"));
			System.out.println("FLDNAM="+array.getJSONObject(i).getString("FLDNAM"));
			System.out.println("ERRCODE="+array.getJSONObject(i).getString("ERRCODE"));
			System.out.println("ERRMESS="+array.getJSONObject(i).getString("ERRMESS"));
		}
		int i = 1;
	}
	
	
}



