package com.qhjf.cfm.web.channel.cmbc;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.cmbc.CmbcConstant;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.web.channel.icbc.IcbcSinglePayInter;
import com.qhjf.cfm.web.channel.icbc.IcbcTradeResultQueryInter;
import com.qhjf.cfm.web.channel.inter.api.ISingleResultChannelInter;
import com.qhjf.cfm.web.channel.util.DateUtil;
import com.qhjf.cfm.web.config.CMBCTestConfigSection;
import com.qhjf.cfm.web.inter.impl.SysOaSinglePayInter;
import com.qhjf.cfm.web.inter.impl.SysSinglePayInter;

public class CmbcTradeResultQueryInter  implements ISingleResultChannelInter{

	private static CMBCTestConfigSection configSection = CMBCTestConfigSection.getInstance();
	private static final Logger log = LoggerFactory.getLogger(CmbcTradeResultQueryInter.class);

	@Override
	public Map<String, Object> genParamsMap(Record record) {
		int preDay = configSection.getPreDay();
		Map<String,Object> map = new HashMap<>();
        Map<String,Object> details = new HashMap<String,Object>();

        details.put("BUSCOD", "N02031");
        details.put("YURREF", record.getStr("bank_serial_number"));
        
        String BGNDAT = DateUtil.getSpecifiedDayBefore(record.getDate("trade_date"), preDay+2, "yyyyMMdd");
        String ENDDAT = DateUtil.getSpecifiedDayBefore(record.getDate("trade_date"), preDay-2, "yyyyMMdd");
        details.put("BGNDAT", BGNDAT);
        details.put("ENDDAT", ENDDAT);

        map.put("NTQRYSTYX1", details);
        return map;
	}

	@Override
	public Record parseResult(String jsonStr) {
		Record record = new Record();
		JSONObject json = JSONObject.parseObject(jsonStr);
		JSONObject infoJson = json.getJSONArray("INFO").getJSONObject(0);
		String resultCode = infoJson.getString("RETCOD");
		if(!"0".equals(resultCode)){
			record.set("status", 3);
			record.set("message", infoJson.getString("ERRMSG"));
			return record;
		}
		JSONArray jsonArray = json.getJSONArray("NTSTLLSTZ");
		if(jsonArray == null || jsonArray.size() == 0){
			reSend(json.getString("bank_serial_number"));
			record.set("status", 3);
			return record;
		}
		JSONObject detail = jsonArray.getJSONObject(0);
		String isFinish = detail.getString("REQSTS");
		if(!"FIN".equals(isFinish)){
			record.set("status", 3);
			return record;
		}
		String result = detail.getString("RTNFLG");
		if("S".equals(result)  || "B".equals(result)){
			record.set("status", 1);
			return record;
		}else{
			record.set("status", 2);
			record.set("message", detail.getString("RTNNAR"));
			return record;
		}
	}
	
	private void reSend(String bank_serial_number){
		log.error("没有符合条件的记录，讲交易重新加入队列");
		
		String sql = "select * from single_pay_instr_queue where bank_serial_number='%s' and status='3'";
		Record record = Db.findFirst(String.format(sql, bank_serial_number));
		if (record != null) {
			//把支付指令插入队列
			SysSinglePayInter sysInter = null;
			String sourceRef = record.getStr("source_ref");//单据表
			if ("oa_branch_payment_item".equals(sourceRef) || "oa_head_payment".equals(sourceRef)) {
				sysInter = new SysOaSinglePayInter();
			}else {
				sysInter = new SysSinglePayInter();
			}
			sysInter.setInnerInstr(record);
			sysInter.setChannelInter(new CmbcSinglePayInter());
			QueueBean bean = new QueueBean(sysInter, sysInter.getChannelInter().genParamsMap(record),"308");
			ProductQueue productQueue = new ProductQueue(bean);
			new Thread(productQueue).start();
		}
	}

	
	@Override
	public AtomicInterfaceConfig getInter() {
		return CmbcConstant.NTQRYSTY;
	}
}
