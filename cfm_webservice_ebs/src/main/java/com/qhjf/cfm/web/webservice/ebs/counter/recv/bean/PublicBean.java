package com.qhjf.cfm.web.webservice.ebs.counter.recv.bean;

import java.rmi.RemoteException;
import java.util.Map;
import com.qhjf.cfm.exceptions.ReqDataException;

public abstract class PublicBean {

	/**
	 * 获取请求参数集合
	 * @return
	 */
	public abstract Map<String,Object> toMap() throws ReqDataException;
	/**
	 * 获取请求xml模板
	 * @return
	 */
	public abstract String getTemplate();
	/**
	 * 获取请求URL
	 * @return
	 */
	public abstract String getUrl();
	/**
	 * 调用ebs接口的webservice方法
	 * @return
	 */
	public abstract String service(String xml) throws RemoteException;
}
