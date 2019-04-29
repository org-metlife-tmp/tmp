package com.qhjf.cfm.web.util.jyyet.histran;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.util.TypeUtils;
import com.qhjf.cfm.excel.bean.ExcelResultBean;
import com.qhjf.cfm.exceptions.ReqDataException;
/**
 * 中信历史交易导入
 * @author CHT
 *
 */
public class CNCBTransPick extends ATransColPickStrategy {

	@Override
	public String getPk() {
		return ATransColPickStrategy.TemplatePk.CNCB.getPk();
	}

	@Override
	public List<Map<String, Object>> colPick(ExcelResultBean bean) throws ReqDataException {
		//行列
		List<Map<String, Object>> data = bean.getRowData();
		if (null == data || data.isEmpty()) {
			throw new ReqDataException("导入excel交易记录为空!");
		}
		
		//单元格
		Map<String, Object> cellData = bean.getCellData();
		if (null == cellData || cellData.isEmpty()) {
			throw new ReqDataException("导入excel查询账号为空!");
		}
		String qryAccNo = getData(cellData.get("C5"), String.class, "C5");
		String startDate = getData(cellData.get("C6"), String.class, "C6");
		String endDate = getData(cellData.get("C7"), String.class, "C7");
		BigDecimal min = getData(cellData.get("C8"), BigDecimal.class, "C8");
		BigDecimal max = getData(cellData.get("C9"), BigDecimal.class, "C9");
		
		//校验头部数据
		log.debug("中行导入头部数据：{}", cellData);
		validateHeader(qryAccNo, startDate, endDate, min, max);
		
		List<Map<String, Object>> result = new ArrayList<>();
		if (data == null || data.size() == 0) {
			throw new ReqDataException("导入Excel数据为空！");
		}
		
		BigDecimal zero = new BigDecimal(0);
		int i=1;
		for (Map<String, Object> map : data) {
			Map<String, Object> cncbMap = new HashMap<>();
			
			String accNo = TypeUtils.castToString(map.get("acc_no"));
			if (!qryAccNo.equals(accNo)) {
				throw new ReqDataException(String.format("第[%s]条数据，交易账号[%s]与查询账号[%s]不一致！", i, accNo, qryAccNo));
			}
			cncbMap.put("acc_no", qryAccNo);
			
			cncbMap.put("opp_acc_no", getStr(map, "opp_acc_no"));
			cncbMap.put("opp_acc_name", getStr(map, "opp_acc_name"));
			cncbMap.put("opp_acc_bank", getStr(map, "opp_bank_name"));
			
			
			BigDecimal payAmount = getBigDecimal(map, "pay_amount");
			BigDecimal recvAmount = getBigDecimal(map, "recv_amount");
			if (null != payAmount && null != recvAmount) {
				throw new ReqDataException(String.format("第[%s]条数据，贷方发生额与借方发生额都不为空！", i));
			}
			
			if (payAmount != null && payAmount.compareTo(zero) > 0) {
				//1付2收
				cncbMap.put("direction", 1);
				cncbMap.put("amount", payAmount);
			}else if(recvAmount != null && recvAmount.compareTo(zero) > 0){
				cncbMap.put("direction", 2);
				cncbMap.put("amount", recvAmount);
			}else {
				throw new ReqDataException(String.format("第[%s]条数据，交易金额非法！", i));
			}
			
			String transDate = getStr(map, "trans_date");
			if (transDate.compareTo(startDate) < 0 || transDate.compareTo(endDate) > 0) {
				throw new ReqDataException(String.format("第[%s]条数据，交易日期[%s]不在起始日期与结束日期之间！", i, transDate));
			}
			cncbMap.put("trans_date", getStr(map, "trans_date"));
			cncbMap.put("trans_time", null);
			
			cncbMap.put("summary", getStr(map, "summary"));
			cncbMap.put("post_script", getStr(map, "post_script"));
			
			result.add(cncbMap);
			i++;
		}
		
		return result;
	}
	
	private void validateHeader(String qryAccNo, String startDate, String endDate, BigDecimal min, BigDecimal max) throws ReqDataException{
		if (startDate != null && endDate != null) {
			if (startDate.compareTo(endDate) > 0) {
				throw new ReqDataException("截至日期必须在起始日期之后!");
			}
		}
		
		if (min != null && max != null) {
			if (min.compareTo(max) > 0) {
				throw new ReqDataException("最小金额 大于 最大金额!");
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getData(Object obj, Class<T> type, String cell) throws ReqDataException{
		T t = null;
		
		if (type == Integer.class) {
			t = (T) TypeUtils.castToInt(obj.toString().trim());
		}else if (type == BigDecimal.class) {
			String trim = obj.toString().trim();
			if (trim.indexOf(',') != 0) {
				trim = trim.replaceAll(",", "");
            }
			t = (T) TypeUtils.castToBigDecimal(trim);
		}else if (type == String.class) {
			t = (T) obj.toString().trim();
		}
		
		return t;
	}
	
}
