package com.qhjf.cfm.web.channel.icbc;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;

import java.util.Map;

/**
 * 
 *工行不支持电子回单图片查询
 */
public class IcbcElectronicImgQueryInter implements IMoreResultChannelInter{

	@Override
	public Map<String, Object> genParamsMap(Record record) {
		return null;
	}

	@Override
	public AtomicInterfaceConfig getInter() {
		return null;
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
