package com.qhjf.cfm.web.util.jyyet.histran;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.qhjf.cfm.excel.bean.ExcelResultBean;
import com.qhjf.cfm.exceptions.ReqDataException;
/**
 * 建行历史交易导入
 * @author CHT
 *
 */
public class CbcTransPick extends ATransColPickStrategy {
	
	protected static final String CCB_DATE_FORMAT = "yyyyMMdd HH:mm:ss";

	@Override
	public String getPk() {
		return ATransColPickStrategy.TemplatePk.CBC.getPk();
	}

	@Override
	public List<Map<String, Object>> colPick(ExcelResultBean bean) throws ReqDataException {
		List<Map<String, Object>> data = bean.getRowData();
		if (null == data || data.isEmpty()) {
			throw new ReqDataException(String.format("导入excel行列数据为空!"));
		}
		
		List<Map<String, Object>> result = new ArrayList<>();
		if (data == null || data.size() == 0) {
			return result;
		}
		
		for (Map<String, Object> map : data) {
			Map<String, Object> ccbMap = new HashMap<>();
			
			ccbMap.put("acc_no", getAccNo(map, "acc_no_ccb"));
			ccbMap.put("opp_acc_no", getStr(map, "opp_acc_no"));
			ccbMap.put("opp_acc_name", getStr(map, "opp_acc_name"));
			ccbMap.put("opp_acc_bank", getStr(map, "opp_acc_bank"));
			
			Double payAmount = getDouble(map, "pay_amount");
			if (payAmount != null && payAmount > 0) {
				//1付2收
				ccbMap.put("direction", 1);
				ccbMap.put("amount", payAmount);
			}else {
				ccbMap.put("direction", 2);
				ccbMap.put("amount", getDouble(map, "recv_amount"));
			}
			
			String transTime = getStr(map, "trans_time");
			Date parse = null;
			try {
				parse = new SimpleDateFormat(CCB_DATE_FORMAT).parse(transTime);
			} catch (ParseException e) {
				throw new ReqDataException(String.format("交易时间[%s]格式错误，应为yyyyMMdd HH:mm:ss", transTime));
			}
			ccbMap.put("trans_date", new SimpleDateFormat("yyyy-MM-dd").format(parse));
			ccbMap.put("trans_time", new SimpleDateFormat("HH:mm:ss").format(parse));
			
			ccbMap.put("summary", getStr(map, "summary"));
			ccbMap.put("post_script", getStr(map, "post_script"));
			
			result.add(ccbMap);
		}
		
		return result;
	}
	
	
}
