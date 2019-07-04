package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.list;

import java.util.Map;

/**
 * 收款业务信息查询结果子列表
 * 
 * @author CHT
 *
 */
public class GroupBizPayQryRespInfoListBean {

	/**
	 * 投保单号
	 */
	private String preinsureBillNo;
	/**
	 * 保单号
	 */
	private String insureBillNo;
	/**
	 * 业务号
	 */
	private String bussinessNo;
	/**
	 * 业务类型 ：1-新单签发收费,2-保全收费,3-定期结算收费,4-续期首付,5-不定期收费
	 */
	private String bussinessType;
	/**
	 * 业务所需缴费金额：单位元，2位小数
	 */
	private String needPayMoney;
	/**
	 * 业务已经缴费金额。 比如一笔业务要收费1000元， 会存在先交一次400元。 后续600下次再缴。 则 这里havePayMoney就是400
	 * （单位元，2位小数）
	 */
	private String havePayMoney;
	/**
	 * 是否为中介业务:Y-是中介业务,N-不是中介业务
	 */
	private String agentCom;
	/**
	 * 保单所属机构 例如：北京分公司
	 */
	private String manageCom;
	/**
	 * 客户号
	 */
	private String customerNo;
	/**
	 * 客户名
	 */
	private String customerName;
	/**
	 * 该业务下的主客户号
	 */
	private String appntNo;
	/**
	 * 该业务下的主客户名
	 */
	private String appntName;

	/**
	 * 
	 * @param preinsureBillNo
	 *            投保单号
	 * @param insureBillNo
	 *            保单号
	 * @param bussinessNo
	 *            业务号
	 * @param bussinessType
	 *            业务类型 ：1-新单签发收费,2-保全收费,3-定期结算收费,4-续期首付,5-不定期收费
	 * @param needPayMoney
	 *            业务所需缴费金额：单位元，2位小数
	 * @param havePayMoney
	 *            业务已经缴费金额。 比如一笔业务要收费1000元， 会存在先交一次400元。 后续600下次再缴。 则
	 *            这里havePayMoney就是400（单位元，2位小数）
	 * @param agentCom
	 *            是否为中介业务:Y-是中介业务,N-不是中介业务
	 * @param manageCom
	 *            保单所属机构 例如：北京分公司
	 * @param customerNo
	 *            客户号
	 * @param customerName
	 *            客户名
	 * @param appntNo
	 *            该业务下的主客户号
	 * @param appntName
	 *            该业务下的主客户名
	 */
	public GroupBizPayQryRespInfoListBean(Map<String, String> datas) {
		if (datas != null) {
			this.preinsureBillNo = datas.get("preinsureBillNo");
			this.insureBillNo = datas.get("insureBillNo");
			this.bussinessNo = datas.get("bussinessNo");
			this.bussinessType = datas.get("bussinessType");
			this.needPayMoney = datas.get("needPayMoney");
			this.havePayMoney = datas.get("havePayMoney");

			this.agentCom = datas.get("agentCom");
			this.manageCom = datas.get("manageCom");
			this.customerNo = datas.get("customerNo");
			this.customerName = datas.get("customerName");
			this.appntNo = datas.get("appntNo");
			this.appntName = datas.get("appntName");

		}
	}

	public String getPreinsureBillNo() {
		return preinsureBillNo;
	}

	public String getInsureBillNo() {
		return insureBillNo;
	}

	public String getBussinessNo() {
		return bussinessNo;
	}

	public String getBussinessType() {
		return bussinessType;
	}

	public String getNeedPayMoney() {
		return needPayMoney;
	}

	public String getHavePayMoney() {
		return havePayMoney;
	}

	public String getAgentCom() {
		return agentCom;
	}

	public String getManageCom() {
		return manageCom;
	}

	public String getCustomerNo() {
		return customerNo;
	}

	public String getCustomerName() {
		return customerName;
	}

	public String getAppntNo() {
		return appntNo;
	}

	public String getAppntName() {
		return appntName;
	}

	@Override
	public String toString() {
		return String.format(
				"{投保单号=[%s],保单号=[%s],业务号=[%s],业务类型 =[%s],业务所需缴费金额=[%s],业务已经缴费金额=[%s],是否为中介业务=[%s],保单所属机构=[%s] ,客户号=[%s],客户名=[%s],该业务下的主客户号=[%s] ,该业务下的主客户名=[%s]}",
				preinsureBillNo, insureBillNo, bussinessNo, bussinessType, needPayMoney, havePayMoney, agentCom,
				manageCom, customerNo, customerName, appntNo, appntName);
	}
}
