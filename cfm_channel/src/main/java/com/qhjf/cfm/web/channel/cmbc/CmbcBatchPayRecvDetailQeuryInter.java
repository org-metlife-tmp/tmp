package com.qhjf.cfm.web.channel.cmbc;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.cmbc.CmbcConstant;
import com.qhjf.cfm.web.channel.inter.api.IChannelBatchInter;

import java.util.Map;

/**
 * 批付明细查询：代发接口实现版本
 * @author CHT
 *
 */
public class CmbcBatchPayRecvDetailQeuryInter  implements IChannelBatchInter {

	@Override
	public Map<String, Object> genParamsMap(Record record) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AtomicInterfaceConfig getInter() {
		return CmbcConstant.GetAgentDetail;
	}

	@Override
	public int getResultCount(String json) throws Exception {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Record parseResult(String json, int index) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBatchSize() {
		// TODO Auto-generated method stub
		return 0;
	}

}
