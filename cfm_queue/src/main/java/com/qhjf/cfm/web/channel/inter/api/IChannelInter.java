package com.qhjf.cfm.web.channel.inter.api;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;

import java.util.Map;

/**
 * 渠道接口
 * @author yunxw
 *
 */
public interface IChannelInter {
	
	/**
	 * 生成要发送到银企接口的map
	 * @param record
	 * @return
	 */
	public Map<String,Object> genParamsMap(Record record);
	
	/**
	 * 获取引起接口
	 * @return
	 */
	public AtomicInterfaceConfig getInter();

}
