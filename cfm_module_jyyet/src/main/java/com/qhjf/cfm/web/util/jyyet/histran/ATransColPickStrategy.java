package com.qhjf.cfm.web.util.jyyet.histran;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.util.TypeUtils;
import com.qhjf.cfm.excel.bean.ExcelResultBean;
import com.qhjf.cfm.exceptions.ReqDataException;

public abstract class ATransColPickStrategy {
	protected static final Logger log = LoggerFactory.getLogger(ATransColPickStrategy.class);
	
	public enum TemplatePk{
		CBC("10", "建设银行历史交易导入模板"),
		BOC("11", "中国银行历史交易导入模板"),
		CNCB("12", "中信银行历史交易导入模板");
		private String pk;
		private String desc;
		TemplatePk(String pk, String desc){
			this.pk = pk;
			this.desc = desc;
		}
		public String getPk(){
			return pk;
		}
		public String getDesc(){
			return desc;
		}
	}

	/**
	 * 获取银行历史交易模板PK
	 * @return
	 */
	public abstract String getPk();
	
	public abstract List<Map<String, Object>> colPick(ExcelResultBean data) throws ReqDataException;
	
	protected String getAccNo(Map<String, Object> map, String key) throws ReqDataException{
		String accNo = TypeUtils.castToString(map.get(key));
		return judgeBlank(accNo);
	}
	protected String judgeBlank(String data) throws ReqDataException{
		if (StringUtils.isBlank(data)) {
			throw new ReqDataException("上传模板与提交模板不一致");
		}
		return data;
	}
	protected String getStr(Map<String, Object> map, String key) {
		return TypeUtils.castToString(map.get(key));
	}
	protected Double getDouble(Map<String, Object> map, String key) {
		return TypeUtils.castToDouble(map.get(key));
	}
	protected static BigDecimal getBigDecimal(Map<String, Object> map, String key) {
		String v = TypeUtils.castToString(map.get(key));
		if (!StringUtil.isBlank(v)) {
			if (v.indexOf(",") != -1 || v.indexOf("，") != -1) {
				v = v.replaceAll("[,，]", "");
			}
			return new BigDecimal(v);
		}
		return null;
	}
}
