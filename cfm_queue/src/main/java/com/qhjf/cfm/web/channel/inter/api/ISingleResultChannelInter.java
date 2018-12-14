package com.qhjf.cfm.web.channel.inter.api;

import com.jfinal.plugin.activerecord.Record;

/**
 * 多条处理的渠道接口
 * @author yunxw
 *
 */
public interface ISingleResultChannelInter extends IChannelInter{

	/**
	 * 解析银企接口返回的json
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public Record parseResult(String json)throws Exception;
}
