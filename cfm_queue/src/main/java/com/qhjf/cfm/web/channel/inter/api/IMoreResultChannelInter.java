package com.qhjf.cfm.web.channel.inter.api;

import com.jfinal.plugin.activerecord.Record;

/**
 * 多条处理的渠道接口
 * @author yunxw
 *
 */
public interface IMoreResultChannelInter extends IChannelInter{
	
	/**
	 * 获取返回数据的条数
	 * @param json
	 * @return
	 * @throws Exception
	 */
	public int getResultCount(String json) throws Exception;

	/**
	 * 根据index从返回的json中解析相应数据
	 * @param json
	 * @param index
	 * @return
	 * @throws Exception
	 */
	public Record parseResult(String json,int index)throws Exception;
}
