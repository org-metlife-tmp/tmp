package cfm_webservice_sft;

import org.junit.Test;

import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.webservice.sft.counter.recv.RecvCounterRemoteCall;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.GroupCustomerAccQryReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.GroupAdvanceReceiptStatusQryReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.GroupBizPayConfirmReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.GroupBizPayQryReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.GroupCustomerAccCancelReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.GroupCustomerAccConfirmReqBean;

public class EbsCounterRecvTest {

	/**
	 * 客户账户查询
	 */
	@Test
	public void ebsCustomerAccQry() {
		RecvCounterRemoteCall call = new RecvCounterRemoteCall();
		GroupCustomerAccQryReqBean bean = null;
		try {
			bean = new GroupCustomerAccQryReqBean(null, "大");
		} catch (ReqDataException e) {
			e.printStackTrace();
		}
		try {
			System.out.println(call.ebsCustomerAccQry(bean));
		} catch (ReqDataException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 收款业务查询 业务类型 ：1-新单签发收费,2-保全收费,3-定期结算收费,4-续期首付,5-不定期收费
	 */
	@Test
	public void ebsBizPayQry() {
		RecvCounterRemoteCall call = new RecvCounterRemoteCall();
		GroupBizPayQryReqBean bean = null;
		try {
			bean = new GroupBizPayQryReqBean(null, "大", "", "", "", "1");
		} catch (ReqDataException e) {
			e.printStackTrace();
		}
		try {
			System.out.println(call.ebsBizPayQry(bean));
		} catch (ReqDataException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 客户账户确认收款
	 */
	@Test
	public void ebsCustomerAccConfirm() {
		RecvCounterRemoteCall call = new RecvCounterRemoteCall();
		GroupCustomerAccConfirmReqBean bean = new GroupCustomerAccConfirmReqBean();
		bean.setPayNo("PayNo");
		bean.setPayCustomerNo("PayCustomerNo");
		bean.setPayWay("2");
		bean.setPayMoney("2.123");
		bean.setChequeNo(null);
		bean.setChequeDate(null);
		bean.setBankCode("SH");
		bean.setBankAccNo("623347721384727136");
		bean.setBankAccName("张三");
		bean.setInBankCode("SH");
		bean.setInBankAccNo("1102001102321");
		try {
			System.out.println(call.ebsCustomerAccConfirm(bean));
		} catch (ReqDataException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 客户账户收款撤销
	 */
	@Test
	public void ebsCustomerAccCancel() {
		RecvCounterRemoteCall call = new RecvCounterRemoteCall();
		GroupCustomerAccCancelReqBean bean = new GroupCustomerAccCancelReqBean("payNo", "cPayNo");
		try {
			System.out.println(call.ebsCustomerAccCancel(bean));
		} catch (ReqDataException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 其他业务收款确认
	 */
	@Test
	public void ebsReciveBizConfirm() {
		RecvCounterRemoteCall call = new RecvCounterRemoteCall();
		GroupBizPayConfirmReqBean bean = new GroupBizPayConfirmReqBean();
		bean.setPayNo("payNo");
		bean.setInsureBillNo("GrpContNo");
		bean.setPreinsureBillNo("preinsureBillNo");
		bean.setBussinessNo("BussinessNo");
		bean.setBussinessType("1");
		bean.setBussinessType("1");
		bean.setPayCustomerNo("PayCustomerNo");
		bean.setPayWay("2");
		bean.setPayMoney("1.98");
		bean.setChequeNo(null);
		bean.setChequeDate(null);
		bean.setBankCode("BankCode");
		bean.setBankAccNo("BankAccNo");
		bean.setBankAccName("BankAccName");
		bean.setInBankCode("InBankCode");
		bean.setInBankAccNo("InBankAccNo");
		try {
			System.out.println(call.ebsBizPayConfirm(bean));
		} catch (ReqDataException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 收款状态查询
	 */
	@Test
	public void ebsConfirmStatusQry() {
		RecvCounterRemoteCall call = new RecvCounterRemoteCall();
		GroupAdvanceReceiptStatusQryReqBean bean = new GroupAdvanceReceiptStatusQryReqBean("payno");
		try {
			System.out.println(call.ebsConfirmStatusQry(bean));
		} catch (ReqDataException e) {
			e.printStackTrace();
		}
	}
}
