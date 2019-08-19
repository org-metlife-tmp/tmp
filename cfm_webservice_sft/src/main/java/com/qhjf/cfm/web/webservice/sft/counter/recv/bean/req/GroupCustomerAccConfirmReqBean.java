package com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req;

import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.config.DDHEBSConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.webservice.ebs.counter.recv.bean.PublicBean;

import cn.metlife.ebs_sit.services.FundsEnterService.FundsEnterServiceProxy;

/**
 * 客户账户 类型的保单 确认收款 的请求bean
 * 
 * @author CHT
 *
 */
public class GroupCustomerAccConfirmReqBean extends PublicBean {
	private static DDHEBSConfigSection config = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHEBS);
	private static final String PAY_WAY_CONST = "234";
	/**
	 * 资金流水号，每笔业务一个流水号
	 */
	private String payNo;
	/**
	 * 资金类别 0-正常 1–反冲 ;当是1-反冲时， 只要PayNo，PayType，CPayNo 必传，其他都可以不传
	 */
	// private String payType = "0";
	/**
	 * 资金反冲流水号, 当是反冲时必传， 该号码就对应正常预收时的PayNo.
	 */
	// private String cPayNo = null;
	/**
	 * 缴费人客户号。 对应缴费公司的客户号码， ebs查询不到该客户号会报错。
	 */
	private String payCustomerNo;
	/**
	 * 缴费方式 2-现金缴款单 3-支票 4-转账汇款
	 */
	private String payWay;
	/**
	 * 交费金额 单位元，2位小数
	 */
	private String payMoney;
	/**
	 * 票据号码 3-支票时必传
	 */
	private String chequeNo;
	/**
	 * 支票日期,YYYY-MM-DD 3-支票时必传
	 */
	private String chequeDate;
	/**
	 * 客户付款银行 需要做Mapping,缴费方式 2和3时 必传
	 */
	private String bankCode;
	/**
	 * 客户付款银行账号;缴费方式 2和3时 必传
	 */
	private String bankAccNo;
	/**
	 * 客户付款银行户名;缴费方式 2和3时 必传
	 */
	private String bankAccName;
	/**
	 * 大都会收款银行编码 需要做Mapping
	 */
	private String inBankCode;
	/**
	 * 大都会收款银行账号
	 */
	private String inBankAccNo;
	/**
	 * @param payNo 资金流水号，每笔业务一个流水号
	 */
	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}
	/**
	 * 
	 * @param payCustomerNo 缴费人客户号。 对应缴费公司的客户号码， ebs查询不到该客户号会报错。
	 */
	public void setPayCustomerNo(String payCustomerNo) {
		this.payCustomerNo = payCustomerNo;
	}
	/**
	 * 
	 * @param payWay 缴费方式 2-现金缴款单 3-支票 4-转账汇款
	 */
	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}
	/**
	 * 
	 * @param payMoney 交费金额 单位元，2位小数
	 */
	public void setPayMoney(String payMoney) {
		this.payMoney = payMoney;
	}
	/**
	 * 
	 * @param chequeNo 票据号码 3-支票时必传
	 */
	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}
	/**
	 * 
	 * @param chequeDate 支票日期,YYYY-MM-DD 3-支票时必传
	 */
	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	}
	/**
	 * 
	 * @param bankCode 客户付款银行 需要做Mapping,缴费方式 2和3时 必传
	 */
	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}
	/**
	 * 
	 * @param bankAccNo 客户付款银行账号;缴费方式 2和3时 必传
	 */ 
	public void setBankAccNo(String bankAccNo) {
		this.bankAccNo = bankAccNo;
	}
	/**
	 * 
	 * @param bankAccName  客户付款银行户名;缴费方式 2和3时 必传
	 */
	public void setBankAccName(String bankAccName) {
		this.bankAccName = bankAccName;
	}
	/**
	 * 
	 * @param inBankCode 大都会收款银行编码 需要做Mapping
	 */
	public void setInBankCode(String inBankCode) {
		this.inBankCode = inBankCode;
	}
	/**
	 * 
	 * @param inBankAccNo 大都会收款银行账号
	 */
	public void setInBankAccNo(String inBankAccNo) {
		this.inBankAccNo = inBankAccNo;
	}

	@Override
	public Map<String, Object> toMap() throws ReqDataException {
		Map<String, Object> map = new HashMap<>();
		if (StringUtils.isBlank(payNo)) {
			throw new ReqDataException("资金流水号必传");
		}
		map.put("PayNo", payNo);
		map.put("PayType", "0");
		map.put("CPayNo", null);
		if (StringUtils.isBlank(payCustomerNo)) {
			throw new ReqDataException("缴费人客户号必传");
		}
		map.put("PayCustomerNo", payCustomerNo);
		if (payWay == null || payWay.length() != 1 || PAY_WAY_CONST.indexOf(payWay) == -1) {
			throw new ReqDataException("缴费方式非法");
		}
		map.put("PayWay", payWay);
		if (StringUtils.isBlank(payMoney)) {
			throw new ReqDataException("交费金额必传");
		}
		map.put("PayMoney", payMoney);
		if ("3".equals(payWay)) {
			if (StringUtils.isBlank(chequeNo)) {
				throw new ReqDataException("缴费方式为3-支票时，票据号码必传");
			}
			if (StringUtils.isBlank(chequeDate)) {
				throw new ReqDataException("缴费方式为3-支票时，支票日期必传");
			}
			try {
				new SimpleDateFormat("yyyy-MM-dd").parse(chequeDate);
			} catch (ParseException e) {
				throw new ReqDataException("支票日期格式错误，正确格式为：yyyy-MM-dd");
			}
		}
		map.put("ChequeNo", chequeNo);
		map.put("ChequeDate", chequeDate);
		if ("2".equals(payWay) || "3".equals(payWay)) {
			if (StringUtils.isBlank(bankCode)) {
				throw new ReqDataException("缴费方式为：2-现金缴款单/3-支票时，客户付款银行必传");
			}
			if (StringUtils.isBlank(bankAccNo)) {
				throw new ReqDataException("缴费方式为：2-现金缴款单/3-支票时，客户付款银行账号必传");
			}
			if (StringUtils.isBlank(bankAccName)) {
				throw new ReqDataException("缴费方式为：2-现金缴款单/3-支票时，客户付款银行户名必传");
			}
		}
		map.put("BankCode", bankCode);
		map.put("BankAccNo", bankAccNo);
		map.put("BankAccName", bankAccName);
		if (StringUtils.isBlank(inBankCode)) {
			throw new ReqDataException("收款银行编码必传");
		}
		map.put("InBankCode", inBankCode);
		if (StringUtils.isBlank(inBankAccNo)) {
			throw new ReqDataException("收款银行账号必传");
		}
		map.put("InBankAccNo", inBankAccNo);
		return map;
	}

	@Override
	public String getTemplate() {
		return "customerAccConfirm.vm";
	}

	@Override
	public String getUrl() {
		return config.getFundsEnterService();
	}
	@Override
	public String service(String xml) throws RemoteException {
		FundsEnterServiceProxy proxy = new FundsEnterServiceProxy(getUrl());
		return proxy.dealFunds(xml);
	}

}
