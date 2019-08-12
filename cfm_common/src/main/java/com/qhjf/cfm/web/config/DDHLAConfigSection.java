package com.qhjf.cfm.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class DDHLAConfigSection extends AbstractConfigSection {

	private static final Logger log = LoggerFactory.getLogger(DDHLAConfigSection.class);

	private String url;
	private int batchNum;
	private String srvOpName;
	private String userId;
	private String userPassword;
	private static final int defBatchNum = 50;
	private String secret;
	private String tranType;

	@SuppressWarnings("static-access")
	protected DDHLAConfigSection() {
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
			this.secret = pros.getProperty("secret");
			this.tranType = pros.getProperty("tranType");
		} else {
			addErrMsg(SECTION_ERR_TEMP, getSectionName());
		}
	}

	@Override
	public String getSectionName() {
		return "DDHLAConfigSection";
	}

	@Override
	public IConfigSectionType getSectionType() {
		return IConfigSectionType.DDHConfigSectionType.DDHLA;
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

	public String getSecret() {
		return secret;
	}

	public String getTranType() {
		return tranType;
	}
	
	

}
