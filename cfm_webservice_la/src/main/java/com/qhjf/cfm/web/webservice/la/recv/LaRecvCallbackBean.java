package com.qhjf.cfm.web.webservice.la.recv;

import com.ibm.icu.text.SimpleDateFormat;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LaRecvCallbackBean {
	private static final Logger log = LoggerFactory.getLogger(LaRecvCallbackBean.class);
	/**
	 * 用于创建对象【LaRecvCallbackBean】失败时，回写la_origin_recv_data
	 */
	private Long id;
	/**
	 * 分公司
	 */
	private String company;
	/**
	 * 机构代码
	 */
	private String branch;
	/**
	 * 保单号码
	 */
	private String cownsel;
	/**
	 * 缴费方式
	 */
	private String paytype;
	/**
	 * 缴费日期
	 */
	private String tchqdate;
	/**
	 * 金额
	 */
	private String docorigamt;
	/**
	 * 银行账户
	 */
	private String bankacckey;
	/**
	 * 银行名称
	 */
	private String znbnkkey;
	/**
	 * 开户行
	 */
	private String bankdesc;
	/**
	 * 保单号+支付号
	 */
	private String bankaccdsc;
	//insure_bill_no
	private String insureBillNo;
	/**
	 * 批次号
	 */
	private String jobno;
	/**
	 * 交易代码
	 */
	private String trancd;
	/**
	 * Bank code
	 */
	private String bankcode;
	/**
	 * LP（保费悬账）
	 */
	private String sacscode;
	/**
	 * S（保费悬账）
	 */
	private String sacstype;
	/**
	 * 下次扣款时间
	 */
	private String nextdate;
	/**
	 * 扣款状态
	 */
	private String znstat;
	/**
	 * 扣款成功/失败原因
	 */
	private String txtline;
	/**
	 * 出盘时生成的流水号
	 */
	private String ddderef;
	
	public LaRecvCallbackBean(Record origin) throws Exception{
		this.id = origin.getLong("id");
		this.company = origin.getStr("branch_code");
		this.branch = origin.getStr("org_code");
		this.cownsel = origin.getStr("insure_bill_no");
		this.paytype = origin.getStr("fee_mode");
		this.tchqdate = origin.getStr("recv_date");
		this.docorigamt = origin.getStr("amount");
		this.bankacckey = origin.getStr("pay_acc_no");
		//银行名称（付方银行大类）
		this.znbnkkey = origin.getStr("pay_bank_name");
		//开户行（收方）：付方账户名
		this.bankdesc = origin.getStr("insure_bill_no") + origin.getStr("pay_code");
		this.bankaccdsc = origin.getStr("pay_acc_name");
		this.insureBillNo = origin.getStr("insure_bill_no");
		//批次号
		this.jobno = origin.getStr("job_no");
		this.trancd = origin.getStr("trans_code");
		//收方bankcode
		this.bankcode = getBankCode(origin);
		this.sacscode = origin.getStr("sacscode");
		this.sacstype = origin.getStr("sacstyp");
		//下次扣款时间：当天日期
		Date date = new Date();
		this.nextdate = new SimpleDateFormat("ddMMyyyy").format(date);
		
		Integer tmpStatus = origin.getInt("tmp_status");
		if (tmpStatus == null) {
			throw new Exception(origin.getLong("id")+"：LA批收原始数据状态有误,无法回写,状态为：空");
		}
		if(tmpStatus == WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_S.getKey()){
			this.znstat = "02";
		}else if(tmpStatus == WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_F.getKey()){
			this.znstat = "03";
			this.txtline = origin.getStr("tmp_err_message");
		}else{
			throw new Exception(origin.getLong("id")+"：LA批收原始数据状态有误,无法回写,状态为："+origin.getInt("tmp_status"));
		}
		//出盘时生成的流水号
		this.ddderef = origin.getStr("pay_code");
		
	}
	/**
	 * 需要拼接到回调报文里面的   参数
	 * @return
	 */
	public Map<String, String> toMap(){
		Map<String, String> map = new HashMap<>();
		map.put("COMPANY", this.company);
		map.put("BRANCH", this.branch);
		map.put("COWNSEL", this.cownsel);
		map.put("PAYTYPE", this.paytype);
		map.put("TCHQDATE", this.tchqdate);
		map.put("DOCORIGAMT", this.docorigamt);
		map.put("BANKACCKEY", this.bankacckey);
		map.put("ZNBNKKEY", this.znbnkkey);
		map.put("BANKDESC", this.bankdesc);
		map.put("BANKACCDSC", this.bankaccdsc);
		map.put("JOBNO", this.jobno);
		map.put("TRANCD", this.trancd);
		map.put("BANKCODE", this.bankcode);
		map.put("SACSCODE", this.sacscode);
		map.put("SACSTYPE", this.sacstype);
		map.put("NEXTDATE", this.nextdate);
		map.put("ZNSTAT", this.znstat);
		map.put("TXTLINE", this.txtline);
		map.put("DDDEREF", this.ddderef);
		return map;
	}
	
	private String getBankCode(Record origin){
		String bankcode = origin.getStr("bankcode");
		
		String err = origin.getStr("tmp_err_message");
		if ("未匹配到机构".equals(err) || "未匹配到通道".equals(err) || "匹配到多个通道".equals(err)) {
			return bankcode;
		}
		
		if (null == bankcode || "".equals(bankcode.trim())) {
			String sql = Db.getSql("webservice_la_recv_cfm.getBankCodeByOrgin");
			Record find = Db.findFirst(sql, origin.getStr("bank_key"), origin.getStr("org_code"), origin.getStr("branch_code"));
			if (null != find) {
				bankcode = find.getStr("bankcode");
			}else {
				log.error("bank_key={}，org_code={}，branch_code={}，未查询到bankcode", origin.getStr("bank_key")
						,origin.getStr("org_code"), origin.getStr("branch_code"));
			}
		}
		return bankcode;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	public String getCownsel() {
		return cownsel;
	}

	public void setCownsel(String cownsel) {
		this.cownsel = cownsel;
	}

	public String getPaytype() {
		return paytype;
	}

	public void setPaytype(String paytype) {
		this.paytype = paytype;
	}

	public String getTchqdate() {
		return tchqdate;
	}

	public void setTchqdate(String tchqdate) {
		this.tchqdate = tchqdate;
	}

	public String getDocorigamt() {
		return docorigamt;
	}

	public void setDocorigamt(String docorigamt) {
		this.docorigamt = docorigamt;
	}

	public String getBankacckey() {
		return bankacckey;
	}

	public void setBankacckey(String bankacckey) {
		this.bankacckey = bankacckey;
	}

	public String getZnbnkkey() {
		return znbnkkey;
	}

	public void setZnbnkkey(String znbnkkey) {
		this.znbnkkey = znbnkkey;
	}

	public String getBankdesc() {
		return bankdesc;
	}

	public void setBankdesc(String bankdesc) {
		this.bankdesc = bankdesc;
	}

	public String getBankaccdsc() {
		return bankaccdsc;
	}

	public void setBankaccdsc(String bankaccdsc) {
		this.bankaccdsc = bankaccdsc;
	}

	public String getInsureBillNo() {
		return insureBillNo;
	}
	public void setInsureBillNo(String insureBillNo) {
		this.insureBillNo = insureBillNo;
	}
	public String getJobno() {
		return jobno;
	}

	public void setJobno(String jobno) {
		this.jobno = jobno;
	}

	public String getTrancd() {
		return trancd;
	}

	public void setTrancd(String trancd) {
		this.trancd = trancd;
	}

	public String getBankcode() {
		return bankcode;
	}

	public void setBankcode(String bankcode) {
		this.bankcode = bankcode;
	}

	public String getSacscode() {
		return sacscode;
	}

	public void setSacscode(String sacscode) {
		this.sacscode = sacscode;
	}

	public String getSacstype() {
		return sacstype;
	}

	public void setSacstype(String sacstype) {
		this.sacstype = sacstype;
	}

	public String getNextdate() {
		return nextdate;
	}

	public void setNextdate(String nextdate) {
		this.nextdate = nextdate;
	}

	public String getZnstat() {
		return znstat;
	}

	public void setZnstat(String znstat) {
		this.znstat = znstat;
	}

	public String getTxtline() {
		return txtline;
	}

	public void setTxtline(String txtline) {
		this.txtline = txtline;
	}

	public String getDdderef() {
		return ddderef;
	}

	public void setDdderef(String ddderef) {
		this.ddderef = ddderef;
	}
}
