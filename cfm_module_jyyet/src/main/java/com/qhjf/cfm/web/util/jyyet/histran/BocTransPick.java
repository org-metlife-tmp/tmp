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
 * 中行历史交易导入
 * @author CHT
 *
 */
public class BocTransPick extends ATransColPickStrategy {
	
	private static final String BOC_DATE_FORMAT = "yyyyMMdd";

	@Override
	public String getPk() {
		return ATransColPickStrategy.TemplatePk.BOC.getPk();
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
		String accNo = TypeUtils.castToString(cellData.get("B2"));
		
		//校验头部数据
		log.debug("中行导入头部数据：{}", cellData);
		Map<String, Date> dateRange = getDateRange(cellData);
		
		List<Map<String, Object>> result = new ArrayList<>();
		if (data == null || data.size() == 0) {
			return result;
		}
		
		int payTotalNumDetail = 0, recvTotalNumDetail = 0;
		BigDecimal payAmountDetail = new BigDecimal(0), recvAmountDetail = new BigDecimal(0);
		int i=1;
		for (Map<String, Object> map : data) {
			Map<String, Object> bocMap = new HashMap<>();
			bocMap.put("acc_no", accNo);
			
			String payAccNo = getStr(map, "pay_acc_no");
			String recvAccNo = getStr(map, "recv_acc_no");
			if (accNo.equals(payAccNo)) {
				bocMap.put("opp_acc_no", getStr(map, "recv_acc_no"));
				bocMap.put("opp_acc_name", getStr(map, "recv_acc_name"));
				bocMap.put("opp_acc_bank", getStr(map, "recv_acc_bank"));
			}else if(accNo.equals(recvAccNo)) {
				bocMap.put("opp_acc_no", getStr(map, "pay_acc_no"));
				bocMap.put("opp_acc_name", getStr(map, "pay_acc_name"));
				bocMap.put("opp_acc_bank", getStr(map, "pay_acc_bank"));
			}
			
			Double amount = getDouble(map, "amount");
			if (amount != null && amount < 0) {
				payTotalNumDetail++;
				amount = -amount;
				payAmountDetail = payAmountDetail.add(new BigDecimal(amount.toString()));
				//1付2收
				bocMap.put("direction", 1);
			}else if(amount != null && amount > 0){
				recvTotalNumDetail++;
				recvAmountDetail = recvAmountDetail.add(new BigDecimal(amount.toString()));
				bocMap.put("direction", 2);
			}else if (amount != null && amount == 0) {
				bocMap.put("direction", 1);
			}
			bocMap.put("amount", amount > 0 ? amount : -amount);
			
			String transDate = getStr(map, "trans_date");
			Date parse = null;
			try {
				parse = new SimpleDateFormat(BOC_DATE_FORMAT).parse(transDate);
			} catch (ParseException e) {
				throw new ReqDataException(String.format("交易时间[%s]格式错误，应为yyyyMMdd", transDate));
			}
			if (parse.compareTo(dateRange.get("start")) < 0 || parse.compareTo(dateRange.get("end")) > 0) {
				throw new ReqDataException(String.format("第%s条交易日期不在查询时间范围内", i));
			}
			bocMap.put("trans_date", new SimpleDateFormat("yyyy-MM-dd").format(parse));
			bocMap.put("trans_time", getStr(map, "trans_time"));
			
			bocMap.put("summary", getStr(map, "summary"));
			bocMap.put("post_script", getStr(map, "post_script"));
			
			result.add(bocMap);
			i++;
		}
		
		validateHeader(cellData, data.size(), payTotalNumDetail, payAmountDetail, recvTotalNumDetail, recvAmountDetail);
		
		return result;
	}
	
	private Map<String, Date> getDateRange(Map<String, Object> cellData) throws ReqDataException{
		String qryDateRange = getData(cellData.get("B8"), String.class, "B8");
		if (qryDateRange.length() != 17) {
			throw new ReqDataException("查询时间范围格式错误");
		}
		Date start = null;
		Date end = null;
		try {
			start = new SimpleDateFormat("yyyyMMdd").parse(qryDateRange.substring(0, qryDateRange.indexOf("-")));
			end = new SimpleDateFormat("yyyyMMdd").parse(qryDateRange.substring(qryDateRange.indexOf("-") + 1));
		} catch (ParseException e1) {
			throw new ReqDataException("查询时间范围格式错误");
		}
		HashMap<String, Date> result = new HashMap<String, Date>();//{{put("start", start);put("start", end);}};
		result.put("start", start);
		result.put("end", end);
		return result;
	}
	
	private void validateHeader(Map<String, Object> cellData, int totalNumDetail, int payTotalNumDetail,
			BigDecimal payAmountDetail, int recvTotalNumDetail, BigDecimal recvAmountDetail)
			throws ReqDataException {
		Integer totalNum = getData(cellData.get("B3"), Integer.class, "B3");
		Integer payTotalNum = getData(cellData.get("B4"), Integer.class, "B4");
		BigDecimal payAmount = getData(cellData.get("B5"), BigDecimal.class, "B5");
		Integer recvTotalNum = getData(cellData.get("B6"), Integer.class, "B6");
		BigDecimal recvAmount = getData(cellData.get("B7"), BigDecimal.class, "B7");
		if (null == totalNum || totalNum != totalNumDetail) {
			throw new ReqDataException("总比数不一致");
		}
		if (payTotalNum != null && payTotalNum.intValue() != payTotalNumDetail) {
			throw new ReqDataException("借方发生总笔数不一致");
		}
		if (payAmount != null && payAmountDetail != null && payAmount.compareTo(payAmountDetail) != 0) {
			throw new ReqDataException("借方发生总额不等");
		}
		if (recvTotalNum != null && recvTotalNum.intValue() != recvTotalNumDetail) {
			throw new ReqDataException("收方发生总笔数不一致");
		}
		if (recvAmount != null && recvAmountDetail != null && recvAmount.compareTo(recvAmountDetail) != 0) {
			throw new ReqDataException("收方发生总额不等");
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T getData(Object obj, Class<T> type, String cell) throws ReqDataException{
		T t = null;
		if (null == obj) {
			throw new ReqDataException(String.format("单元格%s数据为空", cell));
		}
		
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
	
	public static void main(String[] args) {
		Map<String, Object> cellData = new HashMap<>();
		cellData.put("B8", "20190101-20190106");
		try {
			Map<String, Date> dateRange = new BocTransPick().getDateRange(cellData);
			System.out.println(dateRange);
		} catch (ReqDataException e) {
			e.printStackTrace();
		}
	}
}
