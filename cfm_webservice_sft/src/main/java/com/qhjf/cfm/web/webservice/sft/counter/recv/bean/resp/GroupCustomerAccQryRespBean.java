package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp;

import java.util.List;

import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.list.GroupCustomerAccRespInfoListBean;

/**
 * 客户账户查询解析结果bean
 * 
 * @author CHT
 *
 */
public class GroupCustomerAccQryRespBean {
	/**
	 * SUCCESS/FAIL
	 */
	private String resultCode;
	/**
	 * 返回的信息
	 */
	private String resultMsg;
	/**
	 * 对应查询到的信息集合， 有可能查询到多条记录
	 */
	private List<GroupCustomerAccRespInfoListBean> list;
	
	public GroupCustomerAccQryRespBean(String resultCode, String resultMsg, List<GroupCustomerAccRespInfoListBean> list){
		this.resultCode = resultCode;
		this.resultMsg = resultMsg;
		this.list = list;
	}

	public String getResultCode() {
		return resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public List<GroupCustomerAccRespInfoListBean> getList() {
		return list;
	}
	
	@Override
	public String toString() {
		return String.format("{错误码=[%s],错误信息=[%s],列表=[%s]}", resultCode, resultMsg, list);
	}
}
