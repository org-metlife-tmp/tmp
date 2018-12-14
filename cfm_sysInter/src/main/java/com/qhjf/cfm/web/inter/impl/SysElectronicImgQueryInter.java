package com.qhjf.cfm.web.inter.impl;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.channel.inter.api.ISingleResultChannelInter;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;

public class SysElectronicImgQueryInter implements ISysAtomicInterface{
	
	private IMoreResultChannelInter channelInter;

	@Override
	public Record genInstr(Record record) {
		return null;
	}

	@Override
	public Record getInstr() {
		return null;
	}

	@Override
	public void callBack(String jsonStr) throws Exception {
	}

	@Override
	public void callBack(Exception e) throws Exception {
	}

	@Override
	public void setChannelInter(IChannelInter channelInter) {
		this.channelInter = (IMoreResultChannelInter) channelInter;
		
	}

	@Override
	public IChannelInter getChannelInter() {
		return this.channelInter;
	}

}
