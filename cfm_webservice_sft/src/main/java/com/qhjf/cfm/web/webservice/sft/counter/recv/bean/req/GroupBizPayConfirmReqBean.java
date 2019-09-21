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

import cn.metlife.ebs_sit.services.BussinessFundsEnterService.BussinessFundsEnterServiceProxy;

public class GroupBizPayConfirmReqBean extends PublicBean {
	private static DDHEBSConfigSection config = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHEBS);
	private static final String BUSSINESSTYPE_CONST = "12345";
	private static final String PAYWAY_CONST = "234";
	/**
	 * 资金流水号，每笔业务一个流水号
	 */
	private String payNo;
	/**
	 * 资金类别 0-正常 具体业务收款没有反冲接口
	 */
	// private String payType = "0";
	/**
	 * 保单号
	 */
	private String insureBillNo;
	/**
	 * 投保单号
	 */
	private String preinsureBillNo;
	/**
	 * 业务号码， 要收费的业务号码， 必传
	 */
	private String bussinessNo;
	/**
	 * 业务类型 ： 必传 1-新单签发收费,2-保全收费,3-定期结算收费,4-续期首付,5-不定期收费
	 */
	private String bussinessType;
	/**
	 * 缴费人客户号。 对应缴费公司的客户号码，
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
	 * 客户付款银行 需要做Mapping缴费方式, 2和3时 必传
	 */
	private String bankCode;
	/**
	 * 客户付款银行账号,缴费方式 2和3时 必传
	 */
	private String bankAccNo;
	/**
	 * 客户付款银行户名,缴费方式 2和3时 必传
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

	public void setPayNo(String payNo) {
		this.payNo = payNo;
	}

	public void setInsureBillNo(String insureBillNo) {
		this.insureBillNo = insureBillNo;
	}

	public void setPreinsureBillNo(String preinsureBillNo) {
		this.preinsureBillNo = preinsureBillNo;
	}

	public void setBussinessNo(String bussinessNo) {
		this.bussinessNo = bussinessNo;
	}

	public void setBussinessType(String bussinessType) {
		this.bussinessType = bussinessType;
	}

	public void setPayCustomerNo(String payCustomerNo) {
		this.payCustomerNo = payCustomerNo;
	}

	public void setPayWay(String payWay) {
		this.payWay = payWay;
	}

	public void setPayMoney(String payMoney) {
		this.payMoney = payMoney;
	}

	public void setChequeNo(String chequeNo) {
		this.chequeNo = chequeNo;
	}

	public void setChequeDate(String chequeDate) {
		this.chequeDate = chequeDate;
	}

	public void setBankCode(String bankCode) {
		this.bankCode = bankCode;
	}

	public void setBankAccNo(String bankAccNo) {
		this.bankAccNo = bankAccNo;
	}

	public void setBankAccName(String bankAccName) {
		this.bankAccName = bankAccName;
	}

	public void setInBankCode(String inBankCode) {
		this.inBankCode = inBankCode;
	}

	public void setInBankAccNo(String inBankAccNo) {
		this.inBankAccNo = inBankAccNo;
	}

	/**
	 * 参数太多，参数校验放在该方法进行
	 * 
	 * @throws ReqDataException
	 */
	@Override
	public Map<String, Object> toMap() throws ReqDataException {
		Map<String, Object> map = new HashMap<>();
		if (StringUtils.isBlank(this.payNo)) {
			throw new ReqDataException("资金流水号不能为空");
		}
		map.put("PayNo", this.payNo);
		map.put("PayType", "0");
		map.put("GrpContNo", this.insureBillNo);
		map.put("PrtNo", this.preinsureBillNo);
		if (StringUtils.isBlank(this.bussinessNo)) {
			throw new ReqDataException("业务号码不能为空");
		}
		map.put("BussinessNo", this.bussinessNo);
		if (StringUtils.isBlank(this.bussinessNo)) {
			throw new ReqDataException("业务类型不能为空");
		}
		if (StringUtils.isBlank(this.bussinessType)) {
			throw new ReqDataException("业务类型不能为空");
		}
		if (this.bussinessType.length() != 1 || BUSSINESSTYPE_CONST.indexOf(this.bussinessType) == -1) {
			throw new ReqDataException("业务类型非法");
		}
		map.put("BussinessType", this.bussinessType);
		map.put("PayCustomerNo", this.payCustomerNo);
		if (StringUtils.isBlank(this.payWay)) {
			throw new ReqDataException("缴费方式不能为空");
		}
		if (this.payWay.length() != 1 || PAYWAY_CONST.indexOf(this.payWay) == -1) {
			throw new ReqDataException("缴费方式非法");
		}
		map.put("PayWay", this.payWay);
		if (StringUtils.isBlank(this.payMoney)) {
			throw new ReqDataException("交费金额不能为空");
		}
		map.put("PayMoney", this.payMoney);
		if ("3".equals(this.payWay)) {
			if (StringUtils.isBlank(this.chequeNo)) {
				throw new ReqDataException("票据号码不能为空");
			}
			if (StringUtils.isBlank(this.chequeDate)) {
				throw new ReqDataException("支票日期不能为空");
			}
			try {
				new SimpleDateFormat("yyyy-MM-dd").parse(chequeDate);
			} catch (ParseException e) {
				throw new ReqDataException("支票日期格式错误，正确格式为：yyyy-MM-dd");
			}
		}
		map.put("ChequeNo", this.chequeNo);
		map.put("ChequeDate", this.chequeDate);
		if ("2".equals(this.payWay) || "3".equals(this.payWay)) {
			if (StringUtils.isBlank(this.bankCode)) {
				throw new ReqDataException("客户付款银行不能为空");
			}
			if (StringUtils.isBlank(this.bankAccNo)) {
				throw new ReqDataException("客户付款银行账号不能为空");
			}
			if (StringUtils.isBlank(this.bankAccName)) {
				throw new ReqDataException("客户付款银行户名不能为空");
			}
		}
		map.put("BankCode", this.bankCode);
		map.put("BankAccNo", this.bankAccNo);
		map.put("BankAccName", this.bankAccName);
		if (StringUtils.isBlank(this.inBankCode)) {
			throw new ReqDataException("大都会收款银行编码不能为空");
		}
		if (StringUtils.isBlank(this.inBankAccNo)) {
			throw new ReqDataException("大都会收款银行账号不能为空");
		}
		map.put("InBankCode", this.inBankCode);
		map.put("InBankAccNo", this.inBankAccNo);
		return map;
	}

	@Override
	public String getTemplate() {
		return "bizPayConfirm.vm";
	}

	@Override
	public String getUrl() {
		return config.getBussinessFundsEnterService();
	}

	@Override
	public String service(String xml) throws RemoteException {
		BussinessFundsEnterServiceProxy proxy = new BussinessFundsEnterServiceProxy(getUrl());
		return proxy.dealFunds(xml);
	}

}
