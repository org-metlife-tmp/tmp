package com.qhjf.cfm.web.webservice.sft.counter.recv.util.resp;

import com.alibaba.fastjson.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.PersonBillQryRespBean;

import java.util.HashMap;

/**
 * LA系统保单查询结果解析工具
 * @author CHT
 *
 */
public class LaCounterRecvRespResolveTool implements ResponseResolveTool {
	private static final Logger log = LoggerFactory.getLogger(LaCounterRecvRespResolveTool.class);
	private LaCounterRecvRespResolveTool(){}
	public static LaCounterRecvRespResolveTool getInstance(){
		return LaCounterRecvRespResolveToolInner.INSTANCE;
	}
	private static class LaCounterRecvRespResolveToolInner{
		private static final LaCounterRecvRespResolveTool INSTANCE = new LaCounterRecvRespResolveTool();
	}
	
	@Override
	public Object parseResponse(JSONObject jo) {
		
		JSONObject jsonObject = null;
		try {
			jsonObject = jo.getJSONArray("ESBEnvelopeResult").getJSONObject(0)
					.getJSONArray("MsgBody").getJSONObject(0).getJSONArray("POLENQ2O_REC").getJSONObject(0);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		if ("0".equals(jsonObject.getString("STATUS"))) {
			JSONObject result = jsonObject.getJSONArray("PLCENQO").getJSONObject(0);
			
			if (result == null || result.isEmpty()) {
				return null;
			}
			
			String ERRCODE2 = result.getString("ERRCODE2");
			if (ERRCODE2 != null && !"".equals(ERRCODE2.trim())) {
				String PDESC2 = result.getString("PDESC2");
				log.debug("La保单查询错误：ERRCODE2=[{}], PDESC2=[{}]", ERRCODE2, PDESC2);
				return null;
			}
			
			String OWN_LSURNAME = result.getString("OWN_LSURNAME");//保单人
			String OWN_COWNNUM = result.getString("OWN_COWNNUM");//投保人客户号
			String OWN_SECUITYNO = result.getString("OWN_SECUITYNO");//投保险人证件号
			//TODO:LA的状态字典是否与NB状态字典一致，
			String CHDR_STATCODE = result.getString("CHDR_STATCODE");//保单状态

			//获取到分公司code和简写，branch和company，设置给个单查询返回值
			String CHDR_CHDRCOY = result.getString("CHDR_CHDRCOY");//分公司code
			String CHDR_CNTBRANCH = result.getString("CHDR_CNTBRANCH");//分公司code



			PersonBillQryRespBean bean = new PersonBillQryRespBean(OWN_LSURNAME, OWN_COWNNUM, OWN_SECUITYNO,CHDR_CHDRCOY, CHDR_CNTBRANCH);
			bean.setInsureStatus(CHDR_STATCODE);
			bean.setSourceSys("0");
			//TODO：1、保费标准

			//是否保费垫交中：la字段名改为LOANAPL
//			bean.setIsPadPayment(result.getString("IsAPL"));
			bean.setIsPadPayment(result.getString("LOAN_APL"));  //0915修改是否垫交中保单缴费LA返回的字段
   			//是否银行转账中的保单缴费
			bean.setIsTransAccount(result.getString("PREM_ONWAY"));
			//暂记余额
			bean.setSuspenseBalance(result.getString("AmtLPS"));
			//bankcode
			bean.setBankcode(result.getString("RENEW_BANKKEY"));

			//保费标准
			String CHDR_BILLFREQ = result.getString("CHDR_BILLFREQ");//趸交判断
			JSONArray covrs=(JSONArray)result.getJSONArray("COVRS");
			JSONObject covrObj = (JSONObject)covrs.get(0);
			JSONArray  covrArr= covrObj.getJSONArray("COVR");
			JSONObject OBJ = (JSONObject)covrArr.get(0);
			if(CHDR_BILLFREQ.equals("00")){
				bean.setPremiumStandard(OBJ.getString("COVR_SINGP"));//保单标准
			}else{
				bean.setPremiumStandard(OBJ.getString("COVR_INSTPREM"));//保单标准
			}

			//<COVR_SINGP>0</COVR_SINGP>
			return bean;
		}
		return null;
	}

}
