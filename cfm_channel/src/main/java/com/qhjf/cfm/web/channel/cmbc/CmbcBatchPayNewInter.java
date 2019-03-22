package com.qhjf.cfm.web.channel.cmbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.cmbc.CmbcConstant;
import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.web.channel.inter.api.IChannelBatchInter;
import com.qhjf.cfm.web.channel.util.CmbcParamsUtil;
import com.qhjf.cfm.web.channel.util.CmbcParamsUtil.TranType;
import com.qhjf.cfm.web.config.DDHLAConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * 批量付：代付实现版本
 * @author CHT
 *
 */
public class CmbcBatchPayNewInter implements IChannelBatchInter {
	
	private static final Logger log = LoggerFactory.getLogger(CmbcBatchPayNewInter.class);
	public static final String ERROR_MSG = "批量支付回写，银行处理失败,失败原因:%s-%s";
	private JSONArray rsArray;
	private static DDHLAConfigSection section = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLA);

	@Override
	public Map<String, Object> genParamsMap(Record record) {
		TranType tranType = StringUtils.isBlank(section.getTranType()) ? 
				TranType.BYSA : TranType.getTranType(section.getTranType());
		
		Map<String, Object> map = new HashMap<>();
        Map<String, Object> totMap = new HashMap<>();
        List<Map<String, Object>> details = new ArrayList<Map<String, Object>>();

        Record totalRec = (Record) record.get("total");

        totMap.put("BUSCOD", tranType.getBusCod());
		totMap.put("BUSMOD", "00001");// 业务模式编号
		totMap.put("TRSTYP", tranType.getTrstyp());//交易代码
        totMap.put("DBTACC", totalRec.get("pay_account_no"));//转出账号/转入账号(代发为转出账号；代扣为转入账号)
        totMap.put("BBKNBR", totalRec.getStr("pay_bank_cnaps").substring(3, 7));//分行代码
        totMap.put("SUM", String.valueOf(totalRec.get("total_amount")));//总金额
        totMap.put("TOTAL", String.valueOf(totalRec.get("total_num")));//总笔数
        totMap.put("YURREF", totalRec.get("bank_serial_number"));//业务参考号
        totMap.put("MEMO", tranType.getCTrstyp());//用途

        @SuppressWarnings("unchecked")
        List<Record> detail = (List<Record>) record.get("detail");
        if (detail != null && detail.size() > 0) {
            log.debug("CMB批量收付：条数=【{}】；list=【{}】", detail.size(), detail);
            for (Record r : detail) {
                Map<String, Object> detailMap = new HashMap<String, Object>();
                detailMap.put("ACCNBR", decryptAccNo(r.getStr("recv_account_no")));//收款账号/被扣款账号
                detailMap.put("CLTNAM", r.get("recv_account_name"));//户名
                detailMap.put("TRSAMT", String.valueOf(r.get("amount")));//金额

                details.add(detailMap);
            }
        }

        map.put("SDKATSRQX", totMap);
        map.put("SDKATDRQX", details);
        return map;
	}
	//解密
	private String decryptAccNo(String accNo){
		String recvAccountNo = null;
		try {
			recvAccountNo = SymmetricEncryptUtil.getInstance().decryptToStr(accNo);
		} catch (EncryAndDecryException e) {
			e.printStackTrace();
		}
		return recvAccountNo;
	}

	@Override
	public AtomicInterfaceConfig getInter() {
		return CmbcConstant.AgentRequest;
	}

	@Override
	public int getResultCount(String jsonStr) throws Exception {
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONObject infoJson = json.getJSONArray("INFO").getJSONObject(0);
		String resultCode = infoJson.getString("RETCOD");
		
		if(!"0".equals(resultCode)){
			throw new Exception(String.format(ERROR_MSG, resultCode, infoJson.getString("ERRMSG")));
		}
		
		JSONArray jsonArray = json.getJSONArray("NTREQNBRY");
		if(jsonArray == null || jsonArray.size() == 0){
			throw new Exception("批量支付回写，银行返回数据明细条数为0或null");
		}
		
		this.rsArray = jsonArray;
		//招行批付接口返回一批
		return 1;
	}


	@Override
	public Record parseResult(String json, int index) throws Exception {
		Record record = new Record();
		JSONObject jo = this.rsArray.getJSONObject(index);
		record.set("reqnbr", jo.getString("REQNBR"));
		return record;
	}

	@Override
	public int getBatchSize() {
		return 1000;
	}

}
