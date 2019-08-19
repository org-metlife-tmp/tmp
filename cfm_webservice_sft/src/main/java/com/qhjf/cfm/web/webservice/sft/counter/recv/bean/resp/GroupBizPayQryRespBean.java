package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp;

import java.util.List;

import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.list.GroupBizPayQryRespInfoListBean;

/**
 * 收款业务信息查询结果的解析bean
 * 
 * @author CHT
 *
 */
public class GroupBizPayQryRespBean {

	/**
	 * 错误码：SUCCESS/FAIL
	 */
	private String resultCode;
	/**
	 * 错误信息
	 */
	private String resultMsg;
	/**
	 * 对应查询到的信息集合， 有可能查询到多条记录
	 */
	private List<GroupBizPayQryRespInfoListBean> list;

	/**
	 * 
	 * @param resultCode
	 *            错误码：SUCCESS/FAIL
	 * @param resultMsg
	 *            错误信息
	 * @param list
	 *            对应查询到的信息集合， 有可能查询到多条记录
	 */
	public GroupBizPayQryRespBean(String resultCode, String resultMsg,
			List<GroupBizPayQryRespInfoListBean> list) {
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

	public List<GroupBizPayQryRespInfoListBean> getList() {
		return list;
	}

	@Override
	public String toString() {
		return String.format("{错误码=[%s],[错误信息=[%s],[list=[%s]}", resultCode, resultMsg, list);
	}
}
