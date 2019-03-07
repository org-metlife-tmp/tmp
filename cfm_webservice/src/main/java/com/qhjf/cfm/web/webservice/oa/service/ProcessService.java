package com.qhjf.cfm.web.webservice.oa.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.utils.BizSerialnoGenTool;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.constant.WebConstant.COMMONUodp;
import com.qhjf.cfm.web.constant.WebConstant.COMMONUser;
import com.qhjf.cfm.web.service.PoolAccService;
import com.qhjf.cfm.web.service.WorkflowProcessService;
import com.qhjf.cfm.web.webservice.oa.server.OaDataDoubtfulCache;
import com.qhjf.cfm.web.webservice.oa.server.request.OAReciveDateReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class ProcessService {
	
	private static Logger log = LoggerFactory.getLogger(ProcessService.class);
	
	/**
	 * 1，根据收款账号，金额，摘要生成唯一标识存储到oa_check_doubtful,并添加到redis key:OA_唯一标识，value: set oa_check_doubtful的id集合
	 * 2，redis中set.size > 1
	 * 			true  在oa_check_doubtful表中打上可疑标识结束，
	 * 		    false 根据pay_org_type分别存储到oa_head_payment和oa_branch_payment,回写oa_check_doubtful的ref_table,ref_bill_id,ref_service_serial_number字段
	 * @param req
	 * @param originData
	 */
	public void proces(OAReciveDateReq req,Record originData){
		HashMap<String, Long> map = dataCheckDoubtful(req, originData);
		log.debug("doubtful="+map.get("doubtful"));
		if(map.get("doubtful").equals(0l)){
			process(req.getApplyOrg(),req.getRecvBank(),originData,map.get("id"));
		}
	}
	
	public void process(Record applyOrg,Record recvBank,Record originData,Long checkId){
		Record originDataCopy = new Record();

		try{
				originDataCopy.set("id",originData.getLong("id"));
				originDataCopy.set("interface_status", WebConstant.OaInterfaceStatus.OA_INTER_PROCEEING.getKey());
				Integer payOrgTypeId = TypeUtils.castToInt(originData.get("pay_org_type"));
				if(payOrgTypeId == WebConstant.OaPayOrgType.OA_HEAD_PAY.getKey()){
					processHeadPayment(applyOrg.getLong("org_id"),recvBank,originData, checkId);
				}else if(payOrgTypeId == WebConstant.OaPayOrgType.OA_BRANCH_PAY.getKey()){
					processBranchPayment(applyOrg.getLong("org_id"),recvBank,originData, checkId);
				}
				originDataCopy.set("process_status", WebConstant.OaProcessStatus.OA_CONVERT_SUCCESS.getKey());
		}catch(Exception e){
			e.printStackTrace();
			String errMsg = null;
			if(e.getMessage() == null || e.getMessage().length()>100){
				errMsg = "系统处理异常";
			}else{
				errMsg = e.getMessage();
			}
			originDataCopy.set("process_status", WebConstant.OaProcessStatus.OA_CONVERT_FAILED.getKey());
			originDataCopy.set("process_msg", errMsg);
		}
		Db.update("oa_origin_data", originDataCopy);
	}

	/**
	 * 判断oa的数据是否为可疑数据，可疑根据收款账号+金额+摘要来判断
	 * @param req
	 * @param originData
	 * @return
	 */
	private HashMap<String,Long> dataCheckDoubtful(OAReciveDateReq req, Record originData){
		HashMap<String, Long> map = new HashMap<String, Long>();
		map.put("doubtful", 0l);
		Record record = new Record();
		record.set("flow_id", originData.get("flow_id"));
		record.set("bill_no", originData.get("bill_no"));
		record.set("origin_id", originData.get("id"));
		record.set("pay_org_type", originData.get("pay_org_type"));
		String identification = MD5Kit.string2MD5(originData.getStr("recv_acc_no")
				+ "_" +originData.getStr("amount")
				+ "_" +originData.getStr("memo"));

		record.set("identification", MD5Kit.string2MD5(identification));

		Db.save("oa_check_doubtful", record);

		OaDataDoubtfulCache oaCache = new OaDataDoubtfulCache();
		oaCache.addOaCacheValue(identification, record.getStr("id"));
		if(oaCache.getOaCacheValue(identification).size() > 1){
			//可疑数据
			CommonService.update("oa_check_doubtful",
					new Record().set("is_doubtful", 1),
					new Record().set("id", record.getLong("id")));
			map.put("doubtful", 1l);
		}
		map.put("id", record.getLong("id"));
		return map;
	}

	private void processHeadPayment(Long orgId,Record recvBank,Record originData, final Long checkId) throws Exception{


		final Record mainOrg = Db.findFirst(Db.getSql("oa_interface.getMainOrg"));
		if(mainOrg == null){
			throw new Exception("未查到总公司");
		}
		PoolAccService poolAccService = new PoolAccService();
		Record recvBankCopy = new Record();
		//recvBankCopy.set("bank_type", req.getRecvBank().getStr("bank_type"));
		List<Record> payAccouts = poolAccService.getDefaultpoolacc(recvBankCopy);
		Record payAccount = null;
		if(payAccouts != null && payAccouts.size()>0){
			payAccount = payAccouts.get(0);
		}else{
			throw new Exception("未查到付款账户");
		}
		String serviceSerialNumber = BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.OA_HEAD_PAY);
		final Record applyUser = new Record();
		applyUser.set("usr_id", COMMONUser.JZUser.getId());
		applyUser.set("name", COMMONUser.JZUser.getName());
		final Record headPayment = new Record();
		headPayment.set("org_id", orgId);
		headPayment.set("pay_account_id", payAccount.getLong("acc_id"));
		headPayment.set("pay_account_no", payAccount.getStr("acc_no"));
		headPayment.set("pay_account_name", payAccount.getStr("acc_name"));
		headPayment.set("pay_account_cur", payAccount.getStr("iso_code"));
		headPayment.set("pay_account_bank", payAccount.getStr("bank_name"));
		headPayment.set("pay_bank_cnaps", payAccount.getStr("cnaps_code"));
		headPayment.set("pay_bank_prov", payAccount.getStr("province"));
		headPayment.set("pay_bank_city", payAccount.getStr("city"));
		headPayment.set("recv_account_no", originData.getStr("recv_acc_no"));
		headPayment.set("recv_account_name", originData.getStr("recv_acc_name"));
		headPayment.set("recv_account_bank", recvBank.getStr("name"));
		headPayment.set("recv_bank_cnaps", recvBank.getStr("cnaps_code"));
		headPayment.set("recv_bank_prov", recvBank.getStr("province"));
		headPayment.set("recv_bank_city", recvBank.getStr("city"));
		headPayment.set("payment_amount", TypeUtils.castToBigDecimal(originData.get("amount")));
		headPayment.set("pay_mode", 1);
		headPayment.set("payment_summary", originData.getStr("memo"));
		headPayment.set("service_status", WebConstant.BillStatus.SUBMITED.getKey());
		headPayment.set("service_serial_number", serviceSerialNumber);
		headPayment.set("process_bank_type", payAccount.getStr("bank_type"));
		headPayment.set("persist_version", 0);
		headPayment.set("create_by", applyUser.getLong("usr_id"));
		headPayment.set("create_on", new Date());
		headPayment.set("update_by", applyUser.getLong("usr_id"));
		headPayment.set("update_on", new Date());
		headPayment.set("ref_id", originData.getLong("id"));

		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				boolean saveBillFlg = Db.save("oa_head_payment", headPayment);
				if(!saveBillFlg){
					throw new SQLException("保存单据失败");
				}

				boolean doubtfulFlag = CommonService.update("oa_check_doubtful",
						new Record().set("ref_bill_id", headPayment.getLong("id"))
								.set("ref_service_serial_number", headPayment.getStr("service_serial_number"))
								.set("ref_table", "oa_head_payment"),
						new Record().set("id", checkId));
				if(!doubtfulFlag){
					throw new SQLException("更新可疑数据表失败,id为["+checkId+"]");
				}

				try{
					List<Record> flows = CommonService.displayPossibleWf(WebConstant.MajorBizType.OA_HEAD_PAY.getKey(), mainOrg.getLong("org_id"),null);
					if(flows == null || flows.size() ==0){
						throw new Exception("未查到审批流");
					}
					Record flow = flows.get(0);

					headPayment.set("define_id", flow.getLong("define_id"));
					WfRequestObj wfRequestObj = new WfRequestObj(WebConstant.MajorBizType.OA_HEAD_PAY, "oa_head_payment", headPayment) {
						@Override
						public <T> T getFieldValue(WebConstant.WfExpressType type) {
							return null;
						}

						@Override
						public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
							return null;
						}

						@Override
						protected boolean hookPass() {
							return true;
						}
					};
					WorkflowProcessService workflowProcessService = new WorkflowProcessService();

					UserInfo userInfo = new UserInfo(applyUser);
					List<Record> uodps = new ArrayList<Record>();
					Record uodp = new Record();
					uodp.set("uodp_id", COMMONUodp.JZUodp.getId());
					uodp.set("dept_id", COMMONUodp.JZUodp.getDeptId());
					uodp.set("dept_name", COMMONUodp.JZUodp.getDeptName());
					uodp.set("org_id", mainOrg.getLong("org_id"));
					uodp.set("org_name", COMMONUodp.JZUodp.getOrgName());
					uodp.set("pos_id", COMMONUodp.JZUodp.getPosId());
					uodp.set("pos_name", COMMONUodp.JZUodp.getPosName());
					uodp.set("is_default", COMMONUodp.JZUodp.getIsDefault());
					uodps.add(uodp);
					userInfo.addUodp(uodps);
					boolean submitFlowFlg = workflowProcessService.startWorkflow(wfRequestObj, userInfo);
					if(!submitFlowFlg){
						throw new Exception("提交审批流失败");
					}
				}catch(Exception e){
					Record headPaymentCopy = new Record();
					headPaymentCopy.set("id", headPayment.getLong("id"));
					headPaymentCopy.set("service_status", WebConstant.BillStatus.FAILED.getKey());
					Db.update("oa_head_payment", headPaymentCopy);
					return false;
				}
				return true;

			}
		});
	}
	
	private void processBranchPayment(Long orgId,Record recvBank,Record originData, final Long checkId) throws Exception{

		String serviceSerialNumber = BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.OA_BRANCH_PAY);
		Record applyUser = new Record();
		applyUser = new Record();
		applyUser.set("usr_id", COMMONUser.JZUser.getId());
		applyUser.set("name", COMMONUser.JZUser.getName());
		final Record branchPayment = new Record();
		branchPayment.set("org_id", orgId);
		branchPayment.set("recv_account_no", originData.getStr("recv_acc_no"));
		branchPayment.set("recv_account_name", originData.getStr("recv_acc_name"));
		branchPayment.set("recv_account_bank", recvBank.getStr("name"));
		branchPayment.set("recv_bank_cnaps", recvBank.getStr("cnaps_code"));
		branchPayment.set("recv_bank_prov", recvBank.getStr("province"));
		branchPayment.set("recv_bank_city", recvBank.getStr("city"));
		branchPayment.set("payment_amount", TypeUtils.castToBigDecimal(originData.get("amount")));
		branchPayment.set("pay_mode", 1);
		branchPayment.set("payment_summary", originData.getStr("memo"));
		branchPayment.set("service_status", WebConstant.BillStatus.SAVED.getKey());
		branchPayment.set("service_serial_number", serviceSerialNumber);
		branchPayment.set("create_by", applyUser.getLong("usr_id"));
		branchPayment.set("create_on", new Date());
		branchPayment.set("update_by", applyUser.getLong("usr_id"));
		branchPayment.set("update_on", new Date());
		branchPayment.set("persist_version", 0);
		branchPayment.set("ref_id", originData.getLong("id"));
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {

				boolean saveBillFlg = Db.save("oa_branch_payment", branchPayment);
				if(!saveBillFlg){
					throw new SQLException("保存单据失败");
				}

				boolean doubtfulFlag = CommonService.update("oa_check_doubtful",
						new Record().set("ref_bill_id", branchPayment.getInt("id"))
								.set("ref_service_serial_number", branchPayment.getStr("service_serial_number"))
								.set("ref_table", "oa_branch_payment"),
						new Record().set("id", checkId));
				if(!doubtfulFlag){
					throw new SQLException("更新可疑数据表失败,id为["+checkId+"]");
				}
				return true;
			}
		});

	}

}
