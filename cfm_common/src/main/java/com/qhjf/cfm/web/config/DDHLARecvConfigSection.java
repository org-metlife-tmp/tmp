package com.qhjf.cfm.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class DDHLARecvConfigSection extends AbstractConfigSection {

	private static final Logger log = LoggerFactory.getLogger(DDHLARecvConfigSection.class);

	private String url;
	private int batchNum;
	private String srvOpName;
	private String userId;
	private String userPassword;
	private String tranType;
	private String nbBillQryUrl;//回调nb的url地址
	private String nbIsOpen;//是否开启Nb回调
	private static final int defBatchNum = 50;
	private String protocolNo0;//协议编号0
	private String protocolNo1;//协议编号1
	private String protocolNo2;//协议编号2
	private String payType0;//缴费种类0
	private String payType1;//缴费种类1
	private String payType2;//缴费种类2
	private String enterpriseName;//企业名称
	private String enterpriseAccNo;//企业账号
	private String countSize; //拆包长度
	private String protocolSize; //协议拆包长度
	private String mixAmount;    //最小金额
	private String maxAmount;    //最大金额

	@SuppressWarnings("static-access")
	protected DDHLARecvConfigSection() {
		Properties pros = this.reader.getSection(getSectionName());
		if (pros != null && pros.size() > 0) {
			String batchNumStr = pros.getProperty("batchNum", String.valueOf(defBatchNum));
			try {
				this.batchNum = Integer.parseInt(batchNumStr);
			} catch (Exception e) {
				log.error("la批次数量配置有误，采用默认值" + defBatchNum);
				this.batchNum = defBatchNum;
			}
			this.url = getAndValidateNoNullItem(pros,"url");
			this.srvOpName = getAndValidateNoNullItem(pros,"srvOpName");
			this.userId = getAndValidateNoNullItem(pros,"userId");
			this.userPassword = pros.getProperty("userPassword");
			this.tranType = pros.getProperty("tranType");
			this.nbBillQryUrl = getAndValidateNoNullItem(pros,"nbBillQryUrl");
			String nbIsOpen = pros.getProperty("nbIsOpen");
			this.nbIsOpen = nbIsOpen == null || nbIsOpen.trim().equals("") ? "0" : nbIsOpen;

			this.protocolNo0 = pros.getProperty("protocolNo0");
			this.protocolNo1 = pros.getProperty("protocolNo1");
			this.protocolNo2 = pros.getProperty("protocolNo2");
			this.payType0 = pros.getProperty("payType0");
			this.payType1 = pros.getProperty("payType1");
			this.payType2 = pros.getProperty("payType2");
			this.enterpriseName = pros.getProperty("enterpriseName");
			this.enterpriseAccNo = pros.getProperty("enterpriseAccNo");
			this.countSize = pros.getProperty("countSize");
			this.protocolSize = pros.getProperty("protocolSize");
			this.mixAmount = pros.getProperty("mixamount");
			this.maxAmount = pros.getProperty("maxamount");
		} else {
			addErrMsg(SECTION_ERR_TEMP, getSectionName());
		}
	}

	@Override
	public String getSectionName() {
		return "DDHLARecvConfigSection";
	}

	@Override
	public IConfigSectionType getSectionType() {
		return IConfigSectionType.DDHConfigSectionType.DDHLaRecv;
	}

	
	public String getUrl() {
		return url;
	}

	public int getBatchNum() {
		return batchNum;
	}

	public String getSrvOpName() {
		return srvOpName;
	}

	public String getUserId() {
		return userId;
	}

	public String getUserPassword() {
		return userPassword;
	}

	public String getTranType() {
		return tranType;
	}

	public String getProtocolNo0() {
		return protocolNo0;
	}

	public String getProtocolNo1() {
		return protocolNo1;
	}

	public String getProtocolNo2() {
		return protocolNo2;
	}

	public String getCountSize(){
		return countSize;
	}

	public String getProtocolSize(){
		return protocolSize;
	}

	public String getMixAmount(){
		return mixAmount;
	}

	public String getMaxAmount(){
		return maxAmount;
	}

	public String getPayType0() {
		return payType0;
	}
	public String getPayType1() {
		return payType1;
	}
	public String getPayType2() {
		return payType2;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public String getEnterpriseAccNo() {
		return enterpriseAccNo;
	}
	
	public String getNbBillQryUrl(){
		return nbBillQryUrl;
	}

	public String getNbIsOpen() {
		return nbIsOpen;
	}

}
