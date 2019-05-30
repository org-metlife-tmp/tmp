package com.qhjf.cfm.web.util.jyyet.histran;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.util.TypeUtils;
import com.qhjf.cfm.excel.bean.ExcelResultBean;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.web.constant.WebConstant;

/**
 * 花旗银行历史交易导入
 * 
 * @author CHT
 *
 */
public class CitiTransPick extends ATransColPickStrategy {
	private static Logger log = LoggerFactory.getLogger(CitiTransPick.class);
	
	private static final String DYA_CUT = "报表日期";
	private static final String QCZMYE = "期初账面余额";
	private static final String YHCKH = "银行参考号";
	private static final String DJCS = "贷记次数";

	@Override
	public int getPk() {
		return WebConstant.HisTransImportBankList.CITI.getKey();
	}

	@Override
	public List<Map<String, Object>> colPick(ExcelResultBean bean) throws ReqDataException {
		// 行列
		List<Map<String, Object>> data = bean.getRowData();
		if (null == data || data.isEmpty()) {
			throw new ReqDataException("导入excel交易明细为空!");
		}

		// 单元格
		Map<String, Object> cellData = bean.getCellData();
		if (null == cellData || cellData.isEmpty()) {
			throw new ReqDataException("导入excel查询账号为空!");
		}
		String accNo = TypeUtils.castToString(cellData.get("B8"));
//		String accNo = getData(cellData.get("B8"), String.class, "B8");
		
		List<Map<String, Object>> result = new ArrayList<>();
		
		String reportDate = null;//报表日期
		String mainAccNo = null;//主账号
		//收款是 借记 ，付款是贷记
		int creditCount = 0; //贷记次数
		int debitCount = 0; //借方计数
		BigDecimal creditAmount = new BigDecimal(0);//贷记总金额
		BigDecimal debitAmount = new BigDecimal(0);//借记金额总计
		
		int size = data.size();
		for (int i = 0; i < size; i++) {
			log.debug("解析到第[{}]行", i);
			Map<String, Object> map = data.get(i);
			
			String aCol = getStr(map, "a");
			aCol = aCol != null ? aCol.trim() : aCol;
			
			//一天历史交易记录的开始
			if (DYA_CUT.equals(aCol)) {
				reportDate = getStrTrim(map, "b");//报表日期
				if (StringUtils.isBlank(reportDate)) {
					throw new ReqDataException(String.format("导入Excel第[%s]行，报表日期为空！", (i + 10)));
				}
				mainAccNo = getStrTrim(map, "d");//账户号码
				if (StringUtils.isBlank(mainAccNo)) {
					mainAccNo = accNo;
				}else {
					Map<String, Object> account = TableDataCacheUtil.getInstance().getARowData("account", "acc_no", mainAccNo);
					if (null == account) {
						throw new ReqDataException(String.format("导入Excel第[%s]行，账户号码在系统中没有维护！", (i + 10)));
					}
				}
				continue;
			}
			
			//解析余额
			if (QCZMYE.equals(aCol)) {
				if ((i + 1) < size) {
					Map<String, Object> ye = data.get(i + 1);
					BigDecimal beginCase = getBigDecimal(ye, "a");//期初账面余额
					BigDecimal endCase = getBigDecimal(ye, "b");//目前/期末账面余额
					BigDecimal beginUseableCase = getBigDecimal(ye, "c");//期初可动用余额
					BigDecimal endUseableCase = getBigDecimal(ye, "d");//目前/期末可用余额
					log.debug("期初账面余额[{}],目前/期末账面余额[{}],期初可动用余额[{}],目前/期末可用余额[{}]", beginCase, endCase, beginUseableCase, endUseableCase);
					log.debug("跳过余额数据行[{}]", i);
					continue;
				}
			}
			
			//解析交易明细
			if (YHCKH.equals(aCol)) {
				if ((i + 1) < size) {
					Map<String, Object> citiMap = new HashMap<>();
					citiMap.put("acc_no", mainAccNo);
					
					Map<String, Object> detail = data.get(i + 1);
					String oppAccNo = getStrTrim(detail, "a");//银行参考号
					if (DJCS.equals(oppAccNo)) {//当天没有交易明细
						continue;
					}
					String prodectType = getStrTrim(detail, "b");//产品类型
					String transInfo = getStrTrim(detail, "c");//交易说明
					String valueDate = getStrTrim(detail, "d");// 起息日 
					String amount = getStrTrim(detail, "e");//金额，如：-570000
					log.debug("银行参考号[{}],产品类型[{}],交易说明[{}],起息日 [{}],金额[{}]", oppAccNo, prodectType, transInfo,
							valueDate, amount);
					
					putOppAcc(citiMap, oppAccNo);
					putTransDate(citiMap, reportDate, valueDate, i);
					
					if (StringUtils.isNotBlank(amount)) {
						if (amount.indexOf(',') != 0) {
							amount = amount.replaceAll(",", "");
						}
						
						if (amount.indexOf("-") != -1) {
							amount = amount.replaceAll("-", "");
							citiMap.put("amount", Double.parseDouble(amount));
							citiMap.put("direction", 1);//1付2收
							
							debitCount = debitCount+1;
							debitAmount = debitAmount.add(new BigDecimal(amount));
						}else {
							citiMap.put("amount", Double.parseDouble(amount));
							citiMap.put("direction", 2);
							
							creditCount = creditCount+1;
							creditAmount = creditAmount.add(new BigDecimal(amount));
						}
					}else {
						citiMap.put("amount", 0);
						citiMap.put("direction", 2);
					}
					
					if ((i + 2) < size) {
						Map<String, Object> detail2 = data.get(i + 2);
						String clientNo = getStrTrim(detail2, "b");//客户参考号
						log.debug("客户参考号[{}]", clientNo);
					}
					
					result.add(citiMap);
				}
			}
			
			//核对 借贷金额是否一致：忽略支票
			if (DJCS.equals(aCol)) {
				if ((i + 1) < size) {
					Map<String, Object> detail = data.get(i + 1);
					BigDecimal ctTmp = getBigDecimal(detail, "a");
					int ct = ctTmp != null ? ctTmp.intValue() : 0;//贷记次数
					
					BigDecimal ca = getAbsBigDecimal(detail, "b");//贷记总金额
					
					BigDecimal dtTmp = getBigDecimal(detail, "c");
					int dt = dtTmp != null ? dtTmp.intValue() : 0;//借方计数
					
					BigDecimal da = getAbsBigDecimal(detail, "d");//借记金额总计
					
					log.debug("贷记次数[{}],贷记总金额[{}],借方计数[{}],借记金额总计[{}]", ct, ca, dt, da);
					
					if (ct != creditCount) {
						throw new ReqDataException(String.format("导入Excel第[%s]行，贷记次数不相等！", i + 10 + 1));
					}
					ca = ca == null ? new BigDecimal(0) : ca;
					if (ca.compareTo(creditAmount) != 0) {
						throw new ReqDataException(String.format("导入Excel第[%s]行，贷记总金额不相等！", i + 10 + 1));
					}
					if (dt != debitCount) {
						throw new ReqDataException(String.format("导入Excel第[%s]行，借方计数不相等！", i + 10 + 1));
					}
					da = da == null ? new BigDecimal(0) : da;
					if (da.compareTo(debitAmount) != 0) {
						throw new ReqDataException(String.format("导入Excel第[%s]行，借记金额总计不相等！", i + 10 + 1));
					}
				}
				
				creditCount = 0;
				creditAmount = new BigDecimal(0);
				debitCount = 0;
				debitAmount = new BigDecimal(0);
			}
			
		}
		
		return result;
	}
	/**
	 * 设置收款银行行号相关信息
	 * @param citiMap
	 * @param oppAccNo
	 */
	private void putOppAcc(Map<String, Object> citiMap, String oppAccNo) {
		citiMap.put("opp_acc_no", oppAccNo);

		Map<String, Object> account = TableDataCacheUtil.getInstance().getARowData("account", "acc_no", oppAccNo);
		if (null != account) {
			citiMap.put("opp_acc_name", account.get("acc_name"));
			Map<String, Object> bank = TableDataCacheUtil.getInstance().getARowData("all_bank_info", "cnaps_code",
					TypeUtils.castToString(account.get("bank_cnaps_code")));
			citiMap.put("opp_acc_bank", bank.get("name"));
		}
	}
	/**
	 * 设置 交易日期
	 * @param citiMap
	 * @param reportDate
	 * @param valueDate
	 * @param i
	 * @throws ReqDataException
	 */
	private void putTransDate(Map<String, Object> citiMap, String reportDate, String valueDate, int i) throws ReqDataException{
		if (!reportDate.equals(valueDate)) {
			throw new ReqDataException(String.format("导入Excel第[%s]行，起息日与报表日期不一致！", (i + 10 + 1)));
		}
		
		Date parse = null;
		try {
			parse = new SimpleDateFormat("MM/dd/yyyy").parse(valueDate);
		} catch (ParseException e) {
			throw new ReqDataException(String.format("导入Excel第[%s]行，交易时间[%s]格式错误，应为:MM/dd/yyyy！", (i + 10), valueDate));
		}
		citiMap.put("trans_date", new SimpleDateFormat("yyyy-MM-dd").format(parse));
	}

	@SuppressWarnings("unchecked")
	private <T> T getData(Object obj, Class<T> type, String cell) throws ReqDataException {
		T t = null;
		if (null == obj) {
			throw new ReqDataException(String.format("单元格%s数据为空", cell));
		}

		if (type == Integer.class) {
			t = (T) TypeUtils.castToInt(obj.toString().trim());
		} else if (type == BigDecimal.class) {
			String trim = obj.toString().trim();
			if (trim.indexOf(',') != 0) {
				trim = trim.replaceAll(",", "");
			}
			t = (T) TypeUtils.castToBigDecimal(trim);
		} else if (type == String.class) {
			t = (T) obj.toString().trim();
		}

		return t;
	}

	public static void main(String[] args) {
		String s = "5.0";
		System.out.println(TypeUtils.castToBigDecimal(s).intValue());
	}
}
