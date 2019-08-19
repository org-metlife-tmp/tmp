package com.qhjf.cfm.web.channel.cmbc;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.cmbc.CmbcConstant;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;

import java.util.HashMap;
import java.util.Map;

public class CmbcElectronicImgQueryInter implements IMoreResultChannelInter{

	@Override
	public Map<String, Object> genParamsMap(Record record) {
		Map<String,Object> map = new HashMap<>();
		Map<String,Object> details = new HashMap<String,Object>();
        details.put("EACNBR", record.getStr("acc_no"));
        details.put("BEGDAT", record.get("beginDate"));
        details.put("ENDDAT", record.get("endDate"));
        details.put("RRCFLG", "1");

        map.put("CSRRCFDFY0", details);
        return map;
	}

	@Override
	public AtomicInterfaceConfig getInter() {
		return CmbcConstant.SDKCSFDFBRTIMG;
	}

	@Override
	public int getResultCount(String jsonStr) throws Exception {
		return 0;

		
	}

	@Override
	public Record parseResult(String json, int index) throws Exception {
		return null;
	}

}
