package com.qhjf.cfm.web.inter.api;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;

public interface ISysAtomicInterface {
	
	public Record genInstr(Record record);
	
	public Record getInstr();
	
    public void callBack(String jsonStr) throws Exception;
	
	public void callBack(Exception e) throws Exception;
	
	public void setChannelInter(IChannelInter channelInter);
	
	public IChannelInter getChannelInter();

}
