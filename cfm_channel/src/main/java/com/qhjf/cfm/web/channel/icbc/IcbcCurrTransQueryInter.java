package com.qhjf.cfm.web.channel.icbc;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.druid.sql.ast.statement.SQLIfStatement.Else;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.icbc.IcbcChannelConfig;
import com.qhjf.bankinterface.icbc.IcbcConstant;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.channel.util.DateUtil;
import com.qhjf.cfm.web.channel.util.IcbcResultParseUtil;
import com.qhjf.cfm.web.config.CMBCTestConfigSection;
import com.qhjf.cfm.web.config.ICBCTestConfigSection;
import com.qhjf.cfm.web.constant.WebConstant;
/**
 * 当日交易查询
 */
public class IcbcCurrTransQueryInter  implements IMoreResultChannelInter{
	
	private static final Logger log = LoggerFactory.getLogger(IcbcCurrTransQueryInter.class);

	private static ICBCTestConfigSection configSection = ICBCTestConfigSection.getInstance();

	@Override
	public Map<String, Object> genParamsMap(Record record) {
		Map<String,Object> result = new HashMap<>();
		AtomicInterfaceConfig inter = getInter();
        Map<String,Object> pub = new HashMap<>();
        
        pub.put("CIS", inter.getChannelConfig("CIS"));
        pub.put("fSeqno", inter.getChannelInfo().getSerialnoGenTool().next());
        
        result.put("TotalNum", "1");
        result.put("AccNo", record.getStr("acc_no"));
        Date date = new Date();
		int preDay = configSection.getPreDay();
        String preDate = DateUtil.getSpecifiedDayBefore(date, preDay, "yyyyMMdd");
        result.put("BeginDate", preDate);//工行测试环境与当前时间相差7天，故需要做转换
        result.put("EndDate", preDate);
        result.put("MinAmt", "0");
        result.put("MaxAmt", String.valueOf(Integer.MAX_VALUE));
        result.put("pub", pub);
		return result;
	}

	@Override
	public Record parseResult(String jsonStr,int index) throws Exception {
		Record record = new Record();
		
		JSONObject out = IcbcResultParseUtil.parseResult(jsonStr);
		JSONArray rdArray = out.getJSONArray("rd");
		JSONObject rd = rdArray.getJSONObject(index);
		
		record.set("direction", parseDirection(rd));
		record.set("amount", parseAmount(rd));
		record.set("opp_acc_no", rd.getString("RecipAccNo"));
		record.set("opp_acc_name", rd.getString("RecipName"));
		record.set("opp_acc_bank", rd.getString("RecipBkName"));
		record.set("summary", rd.getString("Summary"));
		record.set("trans_date", parseTransDate(rd));
		record.set("trans_time", DateUtil.formatDate(parseTransTime(rd), "HH:mm:ss"));
		record.set("identifier", rd.getString("OnlySequence"));
		log.debug(String.format("渠道返回数据处理parseResult（）=[%s]", record.toJson()));
		return record;
	}
	
	@Override
	public int getResultCount(String jsonStr) throws Exception {
		JSONObject out = IcbcResultParseUtil.parseResult(jsonStr);
		JSONArray resp = out.getJSONArray("rd");
		if(resp == null){
			return 0;
		}
		return resp.size();
	}
	
	/**
	 * 收支方向处理：交易类型转收支方向--》借贷金额转收支方向--》
	 * @param rd
	 * @return
	 * @throws Exception 
	 */
	private int parseDirection(JSONObject rd) throws Exception{
		Integer result = null;
		/*String CASHF = rd.getString("CASHF");//交易类型
		if (null != CASHF) {
			if ("0".equals(CASHF) || "2".equals(CASHF) ||"4".equals(CASHF)) {
				result = WebConstant.PayOrRecv.PAYMENT.getKey();
			}else if ("1".equals(CASHF) || "3".equals(CASHF)) {
				result = WebConstant.PayOrRecv.RECEIPT.getKey();
			}else if ("5".equals(CASHF)) {
				log.warn("工商银行当日交易查询接口，银行返回的交易类型为5（其他）");
			}
		}*/
		String Drcrf = rd.getString("Drcrf");//借贷标志1-借；2-贷；
		if (null != Drcrf) {
			if ("1".equals(Drcrf)) {
				result = WebConstant.PayOrRecv.PAYMENT.getKey();
			}else if ("2".equals(Drcrf)) {
				result = WebConstant.PayOrRecv.RECEIPT.getKey();
			}
		}
		
		if(result == null) {
			BigDecimal debitAmount = rd.getBigDecimal("DebitAmount");//借方发生额
			BigDecimal creditAmount = rd.getBigDecimal("CreditAmount");//贷方发生额
			if (null != debitAmount) {
				result = WebConstant.PayOrRecv.PAYMENT.getKey();
			}else if (null != creditAmount) {
				result = WebConstant.PayOrRecv.RECEIPT.getKey();
			}
		}
		
		if (null == result) {
			throw new Exception("银行返回的交易数据收支方向为空！");
		}
		
		return result;
	}
	/**
	 * 交易金额处理
	 * @param rd
	 * @return
	 * @throws Exception
	 */
	private BigDecimal parseAmount(JSONObject rd)throws Exception{
		BigDecimal result = null;
		BigDecimal debitAmount = rd.getBigDecimal("DebitAmount");//借方发生额
		BigDecimal creditAmount = rd.getBigDecimal("CreditAmount");//贷方发生额
		if (null != debitAmount) {
			result = debitAmount;
		}else {
			result = creditAmount;
		}
		
		//以币种的最小单位为单位,所以除以100
		if (null != result) {
			result = result.divide(new BigDecimal(100));
		}
		return result;
	}
	/**
	 * 交易日期处理：优先取交易日志，其次取到账日期，最后返回指令查询条件中的：开始日期
	 * @param rd
	 * @return
	 * @throws ParseException 
	 */
	private String parseTransDate(JSONObject rd) throws ParseException{
		String result = null;
		String date = rd.getString("Date");//交易日期
		String BusiDate = rd.getString("BusiDate");//到账日期
		
		if (null != date) {
			result = date;
		}else if (null != BusiDate) {
			result = BusiDate;
		}else {
			int preDay = configSection.getPreDay();
			result = DateUtil.getSpecifiedDayBefore(new Date(), preDay, "yyyyMMdd");
		}
		return result;
	}
	/**
	 * 交易时间处理：优先取交易时间，其次取时间戳，再次取到账时间，最后返回当前时间；
	 * @param rd
	 * @return
	 */
	private String parseTransTime(JSONObject rd){
		String result = null;
		String tradeTime = rd.getString("TradeTime");//交易时间：HH.MM.SS
		String time = rd.getString("Time");//时间戳：2012-11-20-16.42.03.731573||HH.MM.SS||YYYY-MM-DD-HH.mm.ss
		String busiTime = rd.getString("BusiTime");//入账时间:HH.MM.SS
		
		if (null != tradeTime) {
			result = tradeTime.replaceAll("\\.", ":");
		}else if (null != time) {
			int length = time.trim().length();
			if (length == 8) {
				result = time.replaceAll("\\.", ":");
			}else if(length >= 19){
				result = time.trim().substring(11, 19).replaceAll("\\.", ":");
			}
		}else if(null != busiTime){
			result = busiTime.replaceAll("\\.", ":");
		}else {
			result = new SimpleDateFormat("HH:mm:ss").format(new Date());
		}
		
		return result;
	}
	
	@Override
	public AtomicInterfaceConfig getInter() {
		return IcbcConstant.QHISD;
	}

}
