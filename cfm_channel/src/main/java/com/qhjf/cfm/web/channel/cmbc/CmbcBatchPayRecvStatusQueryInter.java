package com.qhjf.cfm.web.channel.cmbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.cmbc.CmbcConstant;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.channel.util.CmbcParamsUtil.TranType;
import com.qhjf.cfm.web.channel.util.DateUtil;
import com.qhjf.cfm.web.config.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * 批量收付状态查询接口：代付接口实现版本，查询批量收付的支付状态，继而通过状态查询批量收付明细
 * @author CHT
 *
 */
public class CmbcBatchPayRecvStatusQueryInter  implements IMoreResultChannelInter {
	private static Logger log = LoggerFactory.getLogger(CmbcBatchPayRecvStatusQueryInter.class);
	private static CMBCTestConfigSection configSection = CMBCTestConfigSection.getInstance();
	public static final String ERROR_MSG = "批量收付状态查询，银行处理失败,失败原因:%s-%s";
	private JSONArray rsArray;
	private static DDHLAConfigSection paySection = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLA);
	private static DDHLARecvConfigSection recvSection = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLaRecv);
	
	@Override
	public Map<String, Object> genParamsMap(Record record) {
		int preDay = configSection.getPreDay();
		
		Map<String,Object> map = new HashMap<>();
        Map<String,Object> details = new HashMap<String,Object>();
        
        Integer bizType = record.getInt("biz_type");
        if (null == bizType) {
			log.error("批收付指令状态查询，业务类型参数为空，构造指令参数Map失败！");
			return null;
		}
        
        //1付2收
        TranType tranType = null;
        if (bizType == 1) {
        	tranType = StringUtils.isBlank(paySection.getTranType()) ? 
    				TranType.BYBK : TranType.getTranType(paySection.getTranType());
		}else {
			tranType = StringUtils.isBlank(recvSection.getTranType()) ? 
    				TranType.AYBK : TranType.getTranType(recvSection.getTranType());
		}
        
        //N03010: 代发工资, N03020: 代发, N03030: 代扣
        details.put("BUSCOD", tranType.getBusCod());
        details.put("BUSMOD", "00001");
        //起始结束日期间隔不可超过一周
        details.put("BGNDAT", DateUtil.getSpecifiedDayBefore(record.getDate("begin_date"), preDay+2, "yyyyMMdd"));
        details.put("ENDDAT", DateUtil.getSpecifiedDayBefore(record.getDate("end_date"), preDay-2, "yyyyMMdd"));

        map.put("NTAGCINNY1", details);
		return map;
	}
	
	@Override
	public AtomicInterfaceConfig getInter() {
		return CmbcConstant.NTAGCINN;
	}

	@Override
	public int getResultCount(String jsonStr) throws Exception {
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONObject infoJson = json.getJSONArray("INFO").getJSONObject(0);
		String resultCode = infoJson.getString("RETCOD");

		if (!"0".equals(resultCode)) {
			throw new Exception(String.format(ERROR_MSG, resultCode, infoJson.getString("ERRMSG")));
		}

		JSONArray jsonArray = json.getJSONArray("NTAGCINQZ");
		if (jsonArray == null || jsonArray.size() == 0) {
			throw new Exception("批量收付状态查询，银行返回数据明细条数为0或null");
		}
		
		this.rsArray = jsonArray;
		return jsonArray.size();
	}

	@Override
	public Record parseResult(String json, int index) throws Exception {
		Record result = new Record();
		JSONObject jo = this.rsArray.getJSONObject(index);
		
		result.set("reqnbr", jo.getString("REQNBR"));
		result.set("reqsta", "FIN".equalsIgnoreCase(jo.getString("REQSTA")) ? 1 : 0);
		
		//0：处理中；1:交易失败；2:交易部分成功；9：未知
		Integer rtnflg = "S".equalsIgnoreCase(jo.getString("RTNFLG")) ? 
				2 : "F".equalsIgnoreCase(jo.getString("RTNFLG")) ? 1 : 9;
		
		result.set("rtnflg", rtnflg);
		result.set("bank_service_number", jo.getString("YURREF"));
		result.set("bank_err_msg", jo.getString("ERRDSP"));
		
		return result;
	}

}
