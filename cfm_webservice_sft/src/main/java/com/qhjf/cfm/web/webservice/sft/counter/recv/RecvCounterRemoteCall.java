package com.qhjf.cfm.web.webservice.sft.counter.recv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.axiom.om.OMElement;
import org.apache.commons.lang3.StringUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.XmlTool;
import com.qhjf.cfm.web.config.DDHLARecvConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.webservice.ebs.counter.recv.tool.EbsSendTool;
import com.qhjf.cfm.web.webservice.la.eai.tool.LaSendTool;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.midle.la.LaInsureBillCancelBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.midle.la.LaInsureBillConfirmBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.midle.la.LaInsureBillQryBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.midle.nb.NbInsureBillQryBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.GroupCustomerAccQryReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.GroupAdvanceReceiptStatusQryReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.GroupBizPayConfirmReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.GroupBizPayQryReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.GroupCustomerAccCancelReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.GroupCustomerAccConfirmReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.PersonBillCancelReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.PersonBillComfirmReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.PersonBillQryReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupCustomerAccQryRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupAdvanceReceiptStatusQryRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupBizPayConfirmRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupBizPayQryRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupCustomerAccCancelRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupCustomerAccConfirmRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.PersonBillCancelRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.PersonBillConfirmRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.PersonBillQryRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.util.RestClientTool;
import com.qhjf.cfm.web.webservice.sft.counter.recv.util.resp.ResponseResolveTool;
import com.qhjf.cfm.web.webservice.sft.counter.recv.util.resp.ResponseResolveToolFac;
import com.qhjf.cfm.web.webservice.sft.counter.recv.util.resp.ResponseResolveToolFac.SourceSysType;

/**
 * 柜面收远程调用：1.从LA/NB/EBS查询保单；2.补录保单回传LA/NB/EBS;3.撤销保单
 * 
 * @author CHT
 *
 */
public class RecvCounterRemoteCall {
	// private static Logger log =
	// LoggerFactory.getLogger(RecvCounterRemoteCall.class);
	private static DDHLARecvConfigSection config = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLaRecv);
	private static final String DATA_IS_NULL = "回调核心的单据信息为空";
	private static final String BILLNO_IS_NULL = "个单回调核心的保单号为空";
	// 固定的分公司编码
	//private static final String BRANCH_CODE = "1";
	// 固定的机构编码
	//private static final String ORG_CODE = "SH";

	private static String BRANCH_CODE = "1";
	private static String ORG_CODE = "SH";

	protected static Map<String, String> map = new HashMap<>();

	/**
	 * 个单查询, 通过保单号查询保单:先查询LA，没有再查询NB
	 * 
	 * @param r
	 *            请求参数
	 * @return
	 * @throws ReqDataException
	 */
	public PersonBillQryRespBean qryBillByInsureBillNo(PersonBillQryReqBean bean) throws ReqDataException {
		if (null == bean) {
			return null;
		}

		if (StringUtils.isBlank(bean.getInsureBillNo())) {
			throw new ReqDataException(BILLNO_IS_NULL);
		}

		PersonBillQryRespBean response = null;
		// 调用LA
		response = laQryInsureBill(bean, ORG_CODE, BRANCH_CODE);

		//定义缓存，设置
		map.put("company",response.getCompany());
		map.put("orgcode",response.getInsureOrgCode());

		// 调用NB
		if (response == null && "1".equals(config.getNbIsOpen())) {
			response = nbQryInsureBill(bean);
		}

		return response;
	}

	/**
	 * 针对资金用途为“客户账户”的  团单查询
	 * 
	 * @param bean
	 * @return
	 * @throws ReqDataException 
	 */
	public GroupCustomerAccQryRespBean ebsCustomerAccQry(GroupCustomerAccQryReqBean bean) throws ReqDataException {
		JSONObject resp = EbsSendTool.getInstance().send(bean);
		
		ResponseResolveTool tool = ResponseResolveToolFac.getInstance()
				.getRespResolveTool(SourceSysType.EBS_CUSTOMER_ACC_QRY);
		Object parseResponse = tool.parseResponse(resp);
		return parseResponse == null ? null : (GroupCustomerAccQryRespBean) parseResponse;
	}

	/**
	 * 针对资金用途为:"新单签发、保全收费、定期结算收费、续期收费、不定期收费"的  团单查询
	 * 
	 * @param bean
	 * @return
	 * @throws ReqDataException 
	 */
	public GroupBizPayQryRespBean ebsBizPayQry(GroupBizPayQryReqBean bean) throws ReqDataException {
		JSONObject resp = EbsSendTool.getInstance().send(bean);
		
		ResponseResolveTool tool = ResponseResolveToolFac.getInstance()
				.getRespResolveTool(SourceSysType.EBS_BIZ_PAY_QRY);
		Object parseResponse = tool.parseResponse(resp);
		return parseResponse == null ? null : (GroupBizPayQryRespBean) parseResponse;
	}

	/**
	 * 个单确认（一笔或是批量）
	 * 
	 * @param bill
	 *            补录的保单信息
	 * @return
	 * @throws ReqDataException
	 */
	public List<PersonBillConfirmRespBean> personConfirmBill(List<PersonBillComfirmReqBean> bill)
			throws ReqDataException {
		if (null == bill || bill.size() == 0) {
			throw new ReqDataException(DATA_IS_NULL);
		}

		List<PersonBillConfirmRespBean> bean = laConfirmInsureBill(bill,map.get("company"),map.get("orgcode"));
		map.clear();
		return bean;
	}

	/**
	 * 针对资金用途为“客户账户”的  团单确认
	 * 
	 * @param bill
	 * @return
	 * @throws ReqDataException 
	 */
	public GroupCustomerAccConfirmRespBean ebsCustomerAccConfirm(GroupCustomerAccConfirmReqBean bill) throws ReqDataException {
		if (null == bill) {
			throw new ReqDataException(DATA_IS_NULL);
		}
		JSONObject resp = EbsSendTool.getInstance().send(bill);
		
		ResponseResolveTool tool = ResponseResolveToolFac.getInstance()
				.getRespResolveTool(SourceSysType.EBS_CUSTOMER_ACC_CONFIRM);
		Object parseResponse = tool.parseResponse(resp);
		return parseResponse == null ? null : (GroupCustomerAccConfirmRespBean) parseResponse;
	}
	/**
	 * 针对资金用途为"新单签发、保全收费、定期结算收费、续期收费、不定期收费"的 团单确认
	 * 
	 * @param bill
	 * @return
	 * @throws ReqDataException 
	 */
	public GroupBizPayConfirmRespBean ebsBizPayConfirm(GroupBizPayConfirmReqBean bill) throws ReqDataException {
		if (null == bill) {
			throw new ReqDataException(DATA_IS_NULL);
		}
		JSONObject resp = EbsSendTool.getInstance().send(bill);
		
		ResponseResolveTool tool = ResponseResolveToolFac.getInstance()
				.getRespResolveTool(SourceSysType.EBS_BIZ_PAY_CONFIRM);
		Object parseResponse = tool.parseResponse(resp);
		return parseResponse == null ? null : (GroupBizPayConfirmRespBean) parseResponse;
	}
	
	/**
	 * 个单撤销
	 * 
	 * @return
	 * @throws ReqDataException
	 */
	public PersonBillCancelRespBean personCancelBill(PersonBillCancelReqBean bean) throws ReqDataException {
		if (null == bean || bean.getRecept() == null) {
			throw new ReqDataException("撤销保单的收据号为空");
		}

		PersonBillCancelRespBean rs = laCancelInsureBill(bean, ORG_CODE, BRANCH_CODE);
		return rs;
	}

	/**
	 * 针对资金用途为“客户账户”的  团单撤销
	 * 
	 * @param bill
	 * @return
	 * @throws ReqDataException
	 */
	public GroupCustomerAccCancelRespBean ebsCustomerAccCancel(GroupCustomerAccCancelReqBean bill) throws ReqDataException {
		if (null == bill) {
			throw new ReqDataException(DATA_IS_NULL);
		}
		JSONObject resp = EbsSendTool.getInstance().send(bill);
		
		ResponseResolveTool tool = ResponseResolveToolFac.getInstance()
				.getRespResolveTool(SourceSysType.EBS_CUSTOMER_ACC_CANCEL);
		Object parseResponse = tool.parseResponse(resp);
		return parseResponse == null ? null : (GroupCustomerAccCancelRespBean) parseResponse;
	}
	/**
	 * 团单确认状态查询
	 * @param bean
	 * @return
	 * @throws ReqDataException 
	 */
	public GroupAdvanceReceiptStatusQryRespBean ebsConfirmStatusQry(GroupAdvanceReceiptStatusQryReqBean bean) throws ReqDataException{
		if (null == bean) {
			throw new ReqDataException("回调核心的请求对象为空");
		}
		JSONObject resp = EbsSendTool.getInstance().send(bean);
		
		ResponseResolveTool tool = ResponseResolveToolFac.getInstance()
				.getRespResolveTool(SourceSysType.EBS_ADVANCE_RECEIPT_STATUS_QRY);
		Object parseResponse = tool.parseResponse(resp);
		return parseResponse == null ? null : (GroupAdvanceReceiptStatusQryRespBean) parseResponse;
	}

	/**
	 * 个单LA保单查询
	 * 
	 * @param bean
	 * @param branch
	 * @param company
	 * @return
	 * @throws ReqDataException
	 */
	private PersonBillQryRespBean laQryInsureBill(PersonBillQryReqBean bean, String branch, String company)
			throws ReqDataException {
		LaInsureBillQryBean labean = new LaInsureBillQryBean(bean.getInsureBillNo(), branch, company);

		ArrayList<Record> list = new ArrayList<Record>();
		list.add(labean.getReq());
		LaSendTool sendTool = new LaSendTool(list, "POLService", branch, company);
		OMElement result = sendTool.sendEAI();

		if (null == result) {
			return null;
		}

		try {
			JSONObject json = XmlTool.documentToJSONObject(result.toString());

			ResponseResolveTool respResolveTool = ResponseResolveToolFac.getInstance()
					.getRespResolveTool(SourceSysType.LA_INSURE_QRY);
			Object parseResponse = respResolveTool.parseResponse(json);
			return parseResponse == null ? null : (PersonBillQryRespBean) parseResponse;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 个单NB保单查询
	 * 
	 * @param b
	 * @return
	 * @throws ReqDataException
	 */
	private PersonBillQryRespBean nbQryInsureBill(PersonBillQryReqBean b) throws ReqDataException {
		NbInsureBillQryBean bean = new NbInsureBillQryBean(b.getInsureBillNo());
		String result = RestClientTool.getInstance().sendPost(config.getNbBillQryUrl(), bean.getJsonStr());
		if (null == result) {
			return null;
		}
		JSONObject jo = JSON.parseObject(result);

		ResponseResolveTool respResolveTool = ResponseResolveToolFac.getInstance()
				.getRespResolveTool(SourceSysType.NB_INSURE_QRY);
		Object parseResponse = respResolveTool.parseResponse(jo);
		return parseResponse == null ? null : (PersonBillQryRespBean) parseResponse;
	}

	/**
	 * 个单确认
	 * 
	 * @param bill
	 * @return
	 * @throws ReqDataException
	 */
	private List<PersonBillConfirmRespBean> laConfirmInsureBill(List<PersonBillComfirmReqBean> bill, String branch,
			String company) throws ReqDataException {
		LaInsureBillConfirmBean middle = new LaInsureBillConfirmBean(bill);
		List<Record> list = middle.getData();

		LaSendTool sendTool = new LaSendTool(list, "DRNService", branch, company);
		OMElement result = sendTool.sendEAI();

		if (null == result) {
			return null;
		}

		try {
			JSONObject json = XmlTool.documentToJSONObject(result.toString());

			ResponseResolveTool respResolveTool = ResponseResolveToolFac.getInstance()
					.getRespResolveTool(SourceSysType.LA_INSURE_COMFIRM);
			Object parseResponse = respResolveTool.parseResponse(json);

			return parseResponse == null ? null : (List<PersonBillConfirmRespBean>) parseResponse;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private PersonBillCancelRespBean laCancelInsureBill(PersonBillCancelReqBean bill, String branch, String company)
			throws ReqDataException {
		LaInsureBillCancelBean middle = new LaInsureBillCancelBean(bill);
		ArrayList<Record> list = new ArrayList<Record>();
		list.add(middle.getData());

		LaSendTool sendTool = new LaSendTool(list, "DELService", branch, company);
		OMElement result = sendTool.sendEAI();

		if (null == result) {
			return null;
		}

		try {
			JSONObject json = XmlTool.documentToJSONObject(result.toString());

			ResponseResolveTool respResolveTool = ResponseResolveToolFac.getInstance()
					.getRespResolveTool(SourceSysType.LA_INSURE_CANCEL);
			Object parseResponse = respResolveTool.parseResponse(json);
			return parseResponse == null ? null : (PersonBillCancelRespBean) parseResponse;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
