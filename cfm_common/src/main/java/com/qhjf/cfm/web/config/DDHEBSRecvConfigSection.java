package com.qhjf.cfm.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * EBS暂时没有批收，目前不用该配置类
 * @author CHT
 *
 */
public class DDHEBSRecvConfigSection extends AbstractConfigSection{
	
	private static final Logger log = LoggerFactory.getLogger(DDHEBSRecvConfigSection.class);

	private String url;
	private int batchNum;
	private static final int defBatchNum = 1;

	@SuppressWarnings("static-access")
	protected DDHEBSRecvConfigSection() {
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

		} else {
			addErrMsg(SECTION_ERR_TEMP, getSectionName());
		}
	}

	@Override
	public String getSectionName() {
		return "DDHEBSRecvConfigSection";
	}

	@Override
	public IConfigSectionType getSectionType() {
		return IConfigSectionType.DDHConfigSectionType.DDHEbsRecv;
	}

	
	public String getUrl() {
		return url;
	}

	public int getBatchNum() {
		return batchNum;
	}

}
