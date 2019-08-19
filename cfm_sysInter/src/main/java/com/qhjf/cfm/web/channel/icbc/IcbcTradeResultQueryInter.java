package com.qhjf.cfm.web.channel.icbc;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.icbc.IcbcConstant;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.channel.inter.api.ISingleResultChannelInter;
import com.qhjf.cfm.web.channel.util.IcbcResultParseUtil;
import com.qhjf.cfm.web.inter.impl.SysOaSinglePayInter;
import com.qhjf.cfm.web.inter.impl.SysSftSinglePayInter;
import com.qhjf.cfm.web.inter.impl.SysSinglePayInter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class IcbcTradeResultQueryInter  implements ISingleResultChannelInter{
	private static final Logger log = LoggerFactory.getLogger(IcbcTradeResultQueryInter.class);
	@Override
	public Map<String, Object> genParamsMap(Record record) {
		Map<String,Object> result = new HashMap<>();
		
		AtomicInterfaceConfig inter = getInter();
		Map<String, Object> pub = new HashMap<>();

		pub.put("CIS", inter.getChannelConfig("CIS"));
		
		// 指令包序列号
		String serianlNo = RedisSericalnoGenTool.genBankSeqNo();
		if (serianlNo == null) {
			return result;
		}
		pub.put("fSeqno", serianlNo);
		
		result.put("pub", pub);
		//根据指令序列号查询
		result.put("QryfSeqno", record.getStr("bank_serial_number"));
//		result.put("QrySerialNo", record.getStr("bank_serial_number"));//银行流水号
        return result;
	}

	@Override
	public Record parseResult(String jsonStr) throws Exception {
		JSONObject out0 = null;
		try {
			out0 = IcbcResultParseUtil.parseResult(jsonStr);
		} catch (Exception e) {
			/*响应的错误信息：<RetCode> B0116 </RetCode> <RetMsg>没有符合条件的记录</RetMsg>
				如果为“没有符合条件的记录”，则bank_serial_number查询single_pay_instr_queue表
				，如果查询到数据，就把查询到的交易指令重新放回指令发送队列；*/
			JSONObject json = JSONObject.parseObject(jsonStr);
	        JSONArray ebArray = json.getJSONArray("eb");
	        JSONObject eb0 = ebArray.getJSONObject(0);
	        JSONArray pubArray = eb0.getJSONArray("pub");
	        JSONObject pub0 = pubArray.getJSONObject(0);
	        String retCode = pub0.getString("RetCode");
			if (null != retCode && "B0116".equals(retCode.trim())) {
				log.error("没有符合条件的记录，讲交易重新加入队列");
				JSONObject out = eb0.getJSONArray("out").getJSONObject(0);
				String qryfSeqno = out.getString("QryfSeqno");
				if (qryfSeqno == null || "".equals(qryfSeqno.trim())) {
					log.error("查询交易时没有符合条件的记录，但是银行返回报文中fSeqno为空，需要人工核查！");
				}else {
					String sql = "select * from single_pay_instr_queue where bank_serial_number='%s' and status='3'";
					Record record = Db.findFirst(String.format(sql, qryfSeqno));
					if (record != null) {
						//把支付指令插入队列
						SysSinglePayInter sysInter = null;
						String sourceRef = record.getStr("source_ref");//单据表
						if ("oa_branch_payment_item".equals(sourceRef) || "oa_head_payment".equals(sourceRef)) {
							sysInter = new SysOaSinglePayInter();
						}else if("gmf_bill".equals(sourceRef)){
							sysInter = new SysSftSinglePayInter();
			            }else {
							sysInter = new SysSinglePayInter();
						}
						sysInter.setInnerInstr(record);
						sysInter.setChannelInter(new IcbcSinglePayInter());
						QueueBean bean = new QueueBean(sysInter, sysInter.getChannelInter().genParamsMap(record),"102");
						ProductQueue productQueue = new ProductQueue(bean);
						new Thread(productQueue).start();
					}
				}
			}
			throw e;
		}
		JSONArray rdArray = out0.getJSONArray("rd");
		JSONObject rd = rdArray.getJSONObject(0);
		
		return IcbcSinglePayInter.parseRsult(rd);
	}
	
	@Override
	public AtomicInterfaceConfig getInter() {
		return IcbcConstant.QPAYENT;
	}
}
