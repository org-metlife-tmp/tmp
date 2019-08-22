package com.qhjf.cfm.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class DDHEBSConfigSection extends AbstractConfigSection{
	
	private static final Logger log = LoggerFactory.getLogger(DDHEBSConfigSection.class);

	private String url;
	private String fundsEnterService;
	private String customerAccountService;
	private String queryBussinessPayInfo;
	private String bussinessFundsEnterService;
	private String advanceReceiptStatusService;
	private int batchNum;
	private static final int defBatchNum = 1;

	@SuppressWarnings("static-access")
	protected DDHEBSConfigSection() {
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
			this.fundsEnterService = getAndValidateNoNullItem(pros,"fundsEnterService");
			this.customerAccountService = getAndValidateNoNullItem(pros,"customerAccountService");
			this.queryBussinessPayInfo = getAndValidateNoNullItem(pros,"queryBussinessPayInfo");
			this.bussinessFundsEnterService = getAndValidateNoNullItem(pros,"bussinessFundsEnterService");
			this.advanceReceiptStatusService = getAndValidateNoNullItem(pros,"advanceReceiptStatusService");

		} else {
			addErrMsg(SECTION_ERR_TEMP, getSectionName());
		}
	}

	@Override
	public String getSectionName() {
		return "DDHEBSConfigSection";
	}

	@Override
	public IConfigSectionType getSectionType() {
		return IConfigSectionType.DDHConfigSectionType.DDHEBS;
	}

	
	public String getUrl() {
		return url;
	}

	public int getBatchNum() {
		return batchNum;
	}

	public String getFundsEnterService() {
		return fundsEnterService;
	}

	public String getCustomerAccountService() {
		return customerAccountService;
	}

	public String getQueryBussinessPayInfo() {
		return queryBussinessPayInfo;
	}

	public String getBussinessFundsEnterService() {
		return bussinessFundsEnterService;
	}

	public String getAdvanceReceiptStatusService() {
		return advanceReceiptStatusService;
	}

}
