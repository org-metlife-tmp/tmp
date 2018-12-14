package com.qhjf.cfm.web.util.jyyet;

import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.redis.Redis;
import com.qhjf.cfm.excel.bean.ExcelResultBean;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.config.RedisCacheConfigSection;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.Map;

public class ExcelCacheUtil {

	private static final LogbackLog log = LogbackLog.getLog(ExcelCacheUtil.class);

	private  static final RedisCacheConfigSection config = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Redis);
	
	/**
	 * 从redis中取出当次上传的Excel中的数据，并转换为List《Record》
	 * 
	 * @return
	 * @throws BusinessException
	 */
	public static List<Map<String, Object>> getExcelDataObj(Record record) throws BusinessException {
		String objectId = record.getStr("object_id");
		if (StringUtils.isBlank(objectId)) {
			throw new ReqDataException("请求参数object_id为空");
		}

		// 从redis中获取excel导入的数据
		String redisDb = config.getCacheName();

		log.debug(String.format("请求参数：【%s】,Redis库：【%s】", record, redisDb));

		ExcelResultBean excelResultBean = (ExcelResultBean) Redis.use(redisDb).get(objectId);
		List<Map<String, Object>> rowData = excelResultBean.getRowData();
		if (null == rowData || rowData.isEmpty()) {
			throw new ReqDataException(String.format("导入数据为空!object_id=%s", objectId));
		}
		return rowData;
	}
}
