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
	private String protocolNo;//协议编号
	private String payType;//缴费种类
	private String enterpriseName;//企业名称
	private String enterpriseAccNo;//企业账号
	private String countSize;

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

			this.protocolNo = pros.getProperty("protocolNo");
			this.payType = pros.getProperty("payType");
			this.enterpriseName = pros.getProperty("enterpriseName");
			this.enterpriseAccNo = pros.getProperty("enterpriseAccNo");
			this.countSize = pros.getProperty("countSize");
			
		} else {
			addErrMsg(SECTION_ERR_TEMP, getSectionName());
		}
	}

	public String getCountSize() {
		return countSize;
	}

	public void setCountSize(String countSize) {
		this.countSize = countSize;
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

	public String getProtocolNo() {
		return protocolNo;
	}

	public String getPayType() {
		return payType;
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
