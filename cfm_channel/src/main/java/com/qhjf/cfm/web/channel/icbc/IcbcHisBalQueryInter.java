package com.qhjf.cfm.web.channel.icbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.icbc.IcbcConstant;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.channel.inter.api.ISingleResultChannelInter;
import com.qhjf.cfm.web.channel.util.AmountUtil;
import com.qhjf.cfm.web.channel.util.IcbcResultParseUtil;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 历史余额查询
 */
public class IcbcHisBalQueryInter implements ISingleResultChannelInter {

    @Override
    public Map<String, Object> genParamsMap(Record record) {
        Map<String, Object> result = new HashMap<>();
        AtomicInterfaceConfig inter = getInter();

        Map<String, Object> pub = new HashMap<>();
        pub.put("CIS", inter.getChannelConfig("CIS"));//集团CIS号
        
		// 指令包序列号
		String serianlNo = RedisSericalnoGenTool.genBankSeqNo();
		if (serianlNo == null) {
			return result;
		}
        pub.put("fSeqno", serianlNo);//指令包序列号

        List<Map<String, Object>> rds = new ArrayList<Map<String, Object>>();
        Map<String, Object> rd = new HashMap<>();
        rd.put("iSeqno", "1");//指令顺序号
        rd.put("AccNo", record.getStr("acc_no"));
        rds.add(rd);

        result.put("TotalNum", "1");//总笔数:需要查询的账号的个数，即提交包明细的笔数。
        result.put("pub", pub);
        result.put("rd", rds);
        return result;
    }

    @Override
    public Record parseResult(String jsonStr) throws Exception {
        Record record = new Record();
        JSONArray rdArray = IcbcResultParseUtil.parseResult(jsonStr).getJSONArray("rd");

        JSONObject rd0 = rdArray.getJSONObject(0);

        BigDecimal balance = AmountUtil.icbcAmountHandle(rd0.getBigDecimal("AccBalance"));
//		BigDecimal usableBalance = AmountUtil.icbcAmountHandle(rd0.getBigDecimal("UsableBalance"));

        record.set("bal", balance);
        record.set("available_bal", balance);
        return record;
    }

    @Override
    public AtomicInterfaceConfig getInter() {
        return IcbcConstant.QACCBAL;
    }
}
