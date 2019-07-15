package com.qhjf.cfm.web.service;


import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.BizSerialnoGenTool;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.config.GmfConfigAccnoSection;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.impl.SysSftSinglePayInter;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


/**
 * 柜面付
 * 
 * @author pc_liweibing
 *
 */
public class PayCounterService {

	private final static Log logger = LogbackLog.getLog(PayCounterService.class);

	/**
	 * 柜面付列表
	 * @param pageSize 
	 * @param pageNum 
	 * @param record
	 * @param long1 
	 * @return 
	 * @throws BusinessException 
	 * @throws UnsupportedEncodingException 
	 */
	public Page<Record> list(int pageNum, int pageSize, Record record, Long org_id) throws UnsupportedEncodingException, BusinessException {
		
		// 获取付款账号
		String pay_account_no = GmfConfigAccnoSection.getInstance().getAccno();
		logger.info("============配置文件中柜面付账号=="+pay_account_no);
		Record payRec = Db.findFirst(Db.getSql("nbdb.findAccountByAccno"), pay_account_no);
		if (payRec == null) {
			logger.error("=============未在系统内找到此账户======" + pay_account_no);
		}
		String pay_account_bank = payRec.getStr("bank_name");
		logger.info("=====当前登录人org_id==="+org_id);  
    	Record findById = Db.findById("organization", "org_id", org_id);
		if(null == findById){
			throw new ReqDataException("当前登录人的机构信息未维护");
		}
		Integer source_sys = TypeUtils.castToInt(record.get("source_sys"));		
		if(findById.getInt("level_num") != 1){
			logger.info("====目前登录机构非总公司,只能查询当前及以下机构保单====");
			List<Integer> org_ids = new ArrayList<>();
			List<Record> find = Db.find(Db.getSql("pay_counter.getSonOrg"), org_id);
			for (int i = 0; i < find.size(); i++) {
				org_ids.add(find.get(i).getInt("org_id"));
			}
			record.set("org_ids", org_ids);
		}
		List<Integer> status = record.get("status");
		String service_status_origin = TypeUtils.castToString(record.get("service_status"));
		if(null == status || status.size() == 0) {
			record.remove("status");			
		}
		if(StringUtils.isBlank(service_status_origin)) {
			record.remove("service_status");			
		}else {
		record.set("service_status_origin",record.get("service_status"));
		List<Integer> service_status = new ArrayList<>();
		switch (TypeUtils.castToInt(service_status_origin)) {
			case 0 :  //WebConstant.Sft_Billstatus.WJF.getKey()
				service_status.add(1);  //WebConstant.BillStatus.SUBMITED.getKey()				
				break;
			case 1 :
				service_status.add(3);
				service_status.add(2);
				break;
			case 2 :
				service_status.add(5);
				break;
			case 3 :
				service_status.add(4);
				service_status.add(6);
				break;
			case 4 :
				service_status.add(7);
				break;
			case 5 :
				service_status.add(8);
				break;
			default:
				break;
			}
		record.set("service_status", service_status);
		}
		// 如果状态 未给付 , 可以是未提交.  其他状态必须是已提交 .否则查找任何状态都会带出未给付状态
		String pay_mode = record.getStr("pay_mode");
		//柜面付  默认网银
		if( StringUtils.isBlank(pay_mode) ) {
			record.set("pay_mode", WebConstant.SftDoubtPayMode.WY.getKeyc());
		}else {
			record.set("pay_mode", WebConstant.SftDoubtPayMode.getSftDoubtPayModeByKey(Integer.valueOf(pay_mode)).getKeyc());
		}
		SqlPara sqlPara = null;
		if(source_sys == 0) {
			logger.info("======LA系统数据");
			sqlPara = Db.getSqlPara("pay_counter.findLAPayCounterList", Kv.by("map", record.getColumns()));				
		}else {
			logger.info("======EBS系统数据");
			sqlPara = Db.getSqlPara("pay_counter.findEBSPayCounterList", Kv.by("map", record.getColumns()));				
		}
		 Page<Record> paginate = Db.paginate(pageNum, pageSize, sqlPara);
		 List<Record> list = paginate.getList();
		 SymmetricEncryptUtil  util = SymmetricEncryptUtil.getInstance();
					   
		 if(null != list && list.size() > 0) {
			 for (Record rec : list) {
				 String dec_recv_acc_no = StringUtils.isBlank(rec.getStr("recv_acc_no"))?null :
					 util.decryptToStr(rec.getStr("recv_acc_no"));
				 rec.set("pay_acc_no", pay_account_no);
				 rec.set("pay_bank_name", pay_account_bank);
				 rec.set("recv_acc_no", dec_recv_acc_no);
				 rec.set("recv_account_no", dec_recv_acc_no);
			}
		 }
		 return paginate ;
	}

	/**
	 * 补录按钮
	 * @param record
	 * @param userInfo
	 * @throws ReqDataException 
	 * @throws EncryAndDecryException 
	 */
	public void supplement(final Record record, UserInfo userInfo) throws ReqDataException, EncryAndDecryException {
		Long usr_id = userInfo.getUsr_id();
		final Record user_record = Db.findById("user_info", "usr_id",usr_id);
		final List<Object> list = record.get("files");
		final Integer persist_version = record.getInt("persist_version");
		if(null == user_record){
			throw new ReqDataException("当前登录人未在用户信息表内配置");
		}
		Long pay_id = record.getLong("pay_id");
		Record pay_legal_data = Db.findById("pay_legal_data", "id",pay_id);
		if(WebConstant.SftLegalData.REVOKE.getKey() == pay_legal_data.getInt("status")
				|| WebConstant.SftLegalData.GROUPBATCH.getKey() == pay_legal_data.getInt("status")) {
			logger.error("====此状态的数据不能进行补录==="+pay_legal_data.getInt("status"));
			throw new ReqDataException("只有未提交数据才可以进行补录");
		}
		if(StringUtils.isBlank(record.getStr("recv_cnaps_code"))) {
			Record findFirst = Db.findFirst(Db.getSql("oa_interface.getBankJQ"), record.get("recv_bank_name"));
			record.set("recv_cnaps_code", findFirst.get("cnaps_code"));
		}
		
		
		SymmetricEncryptUtil  util = SymmetricEncryptUtil.getInstance();
		String recv_acc_no = record.getStr("recv_acc_no");
		final String encry_recv_acc_no = util.encrypt(recv_acc_no);
			
		// 开始更新合法数据表
		// supply_status 更新为  1:已补录    
		boolean flag = Db.tx(new IAtom() {			
			@Override
			public boolean run() throws SQLException {
					boolean update = CommonService.update("pay_legal_data", 
						 new Record().set("op_date", new Date()).set("op_user_name", user_record.get("name"))
						.set("supply_status", WebConstant.Sft_supplyStatus.YBL.getKey()).set("recv_acc_no",encry_recv_acc_no)
						.set("recv_bank_name", record.get("recv_bank_name")).set("recv_bank_cnaps", record.get("recv_cnaps_code"))
						.set("payment_summary", record.get("payment_summary")).set("persist_version", persist_version+1).set("recv_acc_name", record.get("recv_acc_name")), 
						 new Record().set("id", record.get("pay_id")).set("persist_version", persist_version));
					if(update) {
						// 删除附件  暂时将附件与合法数据id关联 ===提交后将附件与单据关联
						CommonService.delFileRef(WebConstant.MajorBizType.GMF.getKey(), record.getInt("pay_id"));
						if (list != null && list.size() > 0) {
							// 保存附件
							return CommonService.saveFileRef(WebConstant.MajorBizType.GMF.getKey(), record.getInt("pay_id"), list);
						}	
						return true ;
					}
					return false ;
				 
			}
		});
		if(!flag) {
			throw new ReqDataException("补录信息插入数据库失败");
		}
	}

	
	/**
	 * 作废
	 * @param record
	 * @param userInfo
	 * @throws ReqDataException 
	 */
	public void revokeToLaOrEbs(final Record record, UserInfo userInfo) throws ReqDataException {
		Long usr_id = userInfo.getUsr_id();
		final Record user_record = Db.findById("user_info", "usr_id",usr_id);
		if(null == user_record){
			throw new ReqDataException("当前登录人未在用户信息表内配置");
		}
		Long pay_id = TypeUtils.castToLong(record.get("pay_id"));
		final Integer source_sys = TypeUtils.castToInt(record.get("source_sys")); 
		// 更新 status 为 已拒绝 . 
		Record pay_legal_data = Db.findById("pay_legal_data", "id",pay_id);
		if(WebConstant.SftLegalData.REVOKE.getKey() == pay_legal_data.getInt("status")
				|| WebConstant.SftLegalData.GROUPBATCH.getKey() == pay_legal_data.getInt("status")) {
			logger.error("====此状态的数据不能进行拒绝==="+pay_legal_data.getInt("status"));
			throw new ReqDataException("此数据状态不符合拒绝要求");
		}
		final Long origin_id = pay_legal_data.getLong("origin_id");
		logger.info("=======原始数据id======" + origin_id);
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				boolean update = CommonService.update("pay_legal_data",
						new Record().set("status", WebConstant.SftLegalData.REVOKE.getKey())
								.set("process_msg", record.get("feed_back")).set("persist_version", record.getInt("persist_version") + 1)
								.set("op_date", new Date()).set("op_user_name", user_record.get("name")),
						new Record().set("id", record.get("pay_id")).set("persist_version", record.getInt("persist_version")));
				if (update) {
					logger.info("====撤回更新pay_legal_data====" + update);
					boolean update1 = false;
					if (0 == source_sys) {
						logger.info("=========系统来源LA,更新la_origin_pay_data表");
						Record origin = Db.findById("la_origin_pay_data", "id", origin_id);
						update1 = CommonService.update("la_origin_pay_data",
								new Record().set("tmp_status", 2).set("tmp_err_message", record.get("feed_back"))
										.set("persist_version", origin.getInt("persist_version") + 1),
								new Record().set("id", origin_id).set("persist_version",
										origin.getInt("persist_version")));
					} else if (1 == source_sys) {
						logger.info("=========系统来源EBS,更新ebs_origin_pay_data表");
						Record origin = Db.findById("ebs_origin_pay_data", "id", origin_id);
						update1 = CommonService.update("ebs_origin_pay_data",
								new Record().set("tmp_status", 2).set("tmp_err_message", record.get("feed_back"))
										.set("persist_version", origin.getInt("persist_version") + 1),
								new Record().set("id", origin_id).set("persist_version",
										origin.getInt("persist_version")));
					}
					return update1;
				}
				return false;
			}
		});
		if (!flag) {
			logger.error("=============撤回数据库更新失败");
			throw new ReqDataException("撤回失败");
		}
		try {
			SftCallBack callback = new SftCallBack();
			callback.callback(source_sys, origin_id);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("回调核心接口失败");
		}
		
		
	}

	/**
	 * 柜面确认
	 * @param record
	 * @param userInfo
	 * @param uodpInfo 
	 * @throws ReqDataException
	 */
	public void confirm (Record record, final UserInfo userInfo, UodpInfo uodpInfo) throws ReqDataException{
		
		// 获取付款账号
		String pay_account_no = GmfConfigAccnoSection.getInstance().getAccno();
		logger.info("============配置文件中柜面付账号=="+pay_account_no);
		Record payRec = Db.findFirst(Db.getSql("nbdb.findAccountByAccno"), pay_account_no);
		//查询此账号余额
		List<Record> find = Db.find(Db.getSql("curyet.findCurrentBal"),pay_account_no);
		if(null == find || find.size() == 0 ){
			throw new ReqDataException("未在系统内查询到付款账号的留存余额");
		} 
		Integer source_sys = TypeUtils.castToInt(record.get("source_sys"));
		List<Integer> pay_ids = record.get("pay_id");
		final Long org_id = uodpInfo.getOrg_id();
		List<Record> statuss = Db.find(Db.getSqlPara("pay_counter.findDistinctStatus", Kv.by("map", pay_ids)));
		if(null != statuss && statuss.size() > 0) {
			for (Record sta : statuss) {
				if(sta.getInt("status") == WebConstant.SftLegalData.GROUPBATCH.getKey() 
						|| sta.getInt("status") == WebConstant.SftLegalData.REVOKE.getKey() ) {
					throw new ReqDataException("选中提交的数据中不能包含已提交/已作废数据");
				}
				if(sta.getInt("supply_status") == WebConstant.Sft_supplyStatus.WBL.getKey()) {
					throw new ReqDataException("选中提交的数据中不能包含未补录数据");
				}
			}
		}
		List<Record> Details = null ;
		if(0 == source_sys) {
			logger.info("===========LA数据进行提交");
			Details = Db.find(Db.getSqlPara("pay_counter.checkBatchLADetail", Kv.by("map", pay_ids)));			
		}else {
			logger.info("===========EBS数据进行提交");
			Details = Db.find(Db.getSqlPara("pay_counter.checkBatchEBSDetail", Kv.by("map", pay_ids)));			
		}
				
        if(null == Details || pay_ids.size() != Details.size()) {
        	throw new ReqDataException("勾选数据中部分数据已过期,请刷新页面");
        }
        final List<Record>  insertRecords = new ArrayList<>();
        final List<Record>  updateLegalRecords = new ArrayList<>();
        //产生单据,并开启审批流
        BigDecimal taotal_amount = new BigDecimal(0);
        for (Record rec : Details) {
            String serviceSerialNumber = BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.GMF);
        	Record updateLegalRecord  = new Record();
        	Record insertrec  = new Record();
        	Record cnapsRecord = Db.findById("all_bank_info", "cnaps_code", rec.get("recv_bank_cnaps"));
        	insertrec.set("legal_id", rec.get("pay_id"))
        	          .set("origin_id", rec.get("origin_id"))
        	          .set("source_sys", source_sys)
        	          .set("org_id", org_id)
        	          .set("dept_id",uodpInfo.getDept_id())
        	          .set("amount", rec.get("amount"))
        	          .set("payment_summary", rec.get("payment_summary"))
        	          .set("pay_account_no", pay_account_no)
        	          .set("pay_account_name", payRec.get("acc_name"))
        	          .set("pay_account_cur", payRec.get("curr_code"))
        	          .set("pay_bank_name", payRec.get("bank_name"))
        	          .set("pay_bank_cnaps", payRec.get("cnaps_code"))
        	          .set("pay_bank_prov", payRec.get("province"))
        	          .set("pay_bank_city", payRec.get("city"))
        	          .set("pay_bank_type", payRec.get("bank_type"))
        	          .set("recv_account_no", rec.get("recv_acc_no"))
        	          .set("recv_account_name", rec.get("recv_acc_name"))
        	          .set("recv_account_cur", "CNY")
        	          .set("recv_bank_prov", cnapsRecord.get("province"))
        	          .set("recv_bank_city", cnapsRecord.get("city"))
        	          .set("recv_bank_name", rec.get("recv_bank_name"))
        	          .set("recv_bank_cnaps", rec.get("recv_bank_cnaps"))
        	          .set("recv_bank_type", rec.get("recv_bank_type"))
        	          .set("create_by", userInfo.getUsr_id())
        	          .set("persist_version", 0)
        	          .set("service_serial_number", serviceSerialNumber)
        	          .set("service_status", WebConstant.BillStatus.AUDITING.getKey())
        	          .set("create_on", new Date());
        	updateLegalRecord.set("id", rec.get("pay_id"))
        	                 .set("status", WebConstant.SftLegalData.GROUPBATCH.getKey());
        	insertRecords.add(insertrec);
        	updateLegalRecords.add(updateLegalRecord);
        	taotal_amount = taotal_amount.add(TypeUtils.castToBigDecimal(rec.get("amount")))
        			         .setScale(2, BigDecimal.ROUND_HALF_UP);
		}	
        Record rec = find.get(0);
		BigDecimal amount = TypeUtils.castToBigDecimal(rec.get("bal"));
        if(taotal_amount.compareTo(amount)>0) {
        	throw new ReqDataException("此付款账号余额小于勾选数据的支付金额");
        }
        // 单据入库 ,合法数据表更新状态
      boolean flag = Db.tx(new IAtom() {			
			@Override
			public boolean run() throws SQLException {
				
				// 先更新合法数据为已提交 , 入库单据表 , 将补录的附件关联到单据上 , 所有单据开启审批流  
			    int[] batchUpdate = Db.batchUpdate("pay_legal_data", "id", updateLegalRecords, 1000);
			    boolean updateResult = ArrayUtil.checkDbResult(batchUpdate);
			    logger.info("======更新pay_legal_data结果====="+updateResult);
			    if(updateResult) {
			    	    //int[] batchSave = Db.batchSave("gmf_bill", insertRecords, 1000);
			    		for (Record rec : insertRecords) {
			    			Db.update(Db.getSql("pay_counter.updateDeleteByLegal"), rec.get("legal_id"),0);
			    			boolean save = Db.save("gmf_bill", "id", rec);
			    			logger.info("=======插入gmf_bill表结果==="+save);			    			
			    			if(save) {
			    				//这里新增一个附件关联关系
			    				List<Record> files = Db.find(Db.getSql("pay_counter.getatchment"),WebConstant.MajorBizType.GMF.getKey(),rec.get("legal_id"));
			    				if(null != files && files.size()>0) {
			    					for (Record re : files) {
			    						re.set("bill_id", rec.get("id"));
									}
			    					Db.batchSave("common_attachment_info_ref", files, files.size());
			    				}
				    		    //开启审批流
				    			List<Record> flows = null;
								try {
									flows = CommonService.displayPossibleWf(WebConstant.MajorBizType.GMF.getKey(),
											org_id, null);
								} catch (BusinessException e) {
									e.printStackTrace();
									logger.error("============获取柜面付审批流异常");
									return false;
								}
								if (flows == null || flows.size() == 0) {
									logger.error("============未查询到柜面付审批流");
									return false;
								}
								Record flow = flows.get(0);
								rec.set("define_id", flow.getLong("define_id"));
								rec.set("service_serial_number", rec.get("service_serial_number"));
								// TODO
								WfRequestObj wfRequestObj = new WfRequestObj(WebConstant.MajorBizType.GMF, "gmf_bill",
										rec) {
									@Override
									public <T> T getFieldValue(WebConstant.WfExpressType type) {
										return null;
									}

									@Override
									public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
										return null;
									}

								};
								WorkflowProcessService workflowProcessService = new WorkflowProcessService();
								boolean submitFlowFlg;
								try {
									submitFlowFlg = workflowProcessService.startWorkflow(wfRequestObj, userInfo);
								} catch (WorkflowException e) {
									e.printStackTrace();
									logger.error("=========柜面付审批流提交失败");
									return false;
								}
								if (!submitFlowFlg) {
									return false;
								}
			    			}else {
			    				return false ;
			    			}
			    		}
			    		return true ;
			    	}
			    return false ;		    	
			}   
		});
        if(!flag) {
        	throw new ReqDataException("柜面付页面确认失败");
        }
	}

	/**
	 * 获取柜面付列表详情
	 * @param long1
	 * @return
	 * @throws ReqDataException 
	 */
	public Record detail(Long id) throws ReqDataException {
		Record findById = Db.findById("gmf_bill", "id", id);
		Record findFirst = null ;
		try {
			if(0 == TypeUtils.castToInt(findById.get("source_sys"))) {
				findFirst = Db.findFirst(Db.getSql("pay_counter.findLaDetailById"), id);
			}  else if(1 == TypeUtils.castToInt(findById.get("source_sys"))) {
				findFirst = Db.findFirst(Db.getSql("pay_counter.findEBSDetailById"), id);
			}else {
				findFirst = Db.findFirst(Db.getSql("pay_counter.findTMPDetailById"), id);
			}		
																			 
	 
		} catch (Exception e) {
			throw new ReqDataException("此条数据已过期");
		}
		//解密,并改为掩码
		SymmetricEncryptUtil  util = SymmetricEncryptUtil.getInstance();
		try {
			util.recvmaskforSingle(findFirst);
		} catch (Exception e) {			
			e.printStackTrace();
			logger.error("===解密修改为掩码失败");
		} 
		return findFirst;
	}

	/**
	 * 
	 * 审批通过  同时生成指令 , 存入指令表 , 回写 actual_payment_date 
	 * @param record
	 * @param userInfo
	 * @return
	 */
	public boolean hookPass(Record record, UserInfo userInfo) {
		final Long id = record.getLong("id");
        try {
            sendPayDetail(id);
        } catch (Exception e) {
            e.printStackTrace();
            String errMsg = null;
            if (e.getMessage() == null || e.getMessage().length() > 1000) {
                errMsg = "发送银行失败！";
            } else {
                errMsg = e.getMessage();
            }
            logger.error("====发送银行错误信息===="+errMsg);
            final Record innerRec = Db.findById("gmf_bill", "id", id);
            final Integer status = innerRec.getInt("service_status");
            final int persist_version = TypeUtils.castToInt(innerRec.get("persist_version"));
            final String feedBack = errMsg;
            boolean flag = Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {

                    if (WebConstant.BillStatus.PASS.getKey() != status && WebConstant.BillStatus.FAILED.getKey() != status) {
                        logger.error("单据状态有误!======"+status);
                        return false;
                    }
                    Record setRecord = new Record();
                    Record whereRecord = new Record();

                    setRecord.set("service_status", WebConstant.BillStatus.FAILED.getKey()).set("feed_back", feedBack)
                            .set("persist_version", persist_version + 1);
                    whereRecord.set("id", id).set("service_status", status).set("persist_version", persist_version);
                    boolean update = CommonService.updateRows("gmf_bill", setRecord, whereRecord) == 1;
                    if(update) {
                    	if(WebConstant.YesOrNo.YES.getKey() == TypeUtils.castToInt(innerRec.get("is_match"))
                    			&&  WebConstant.BillStatus.FAILED.getKey() == status) {
                			Record recv_counter_match = Db.findById("recv_counter_match", "id", innerRec.get("legal_id"));
                			boolean upd = CommonService.update("recv_counter_match", 
                					new Record().set("delete_flag", 1),
                					new Record().set("id", innerRec.get("legal_id")));
                			if(!upd) {
                				logger.error("====更新recv_counter_match为删除失败===="+innerRec.get("legal_id"));
                				return update ;
                			}
                			recv_counter_match.remove("id");
                			recv_counter_match.set("status", WebConstant.SftLegalData.NOGROUP.getKey());
                			recv_counter_match.set("match_status", WebConstant.SftRecvCounterMatchStatus.DPP.getKey());
                			return Db.save("recv_counter_match", "id", recv_counter_match);
                    	}
                    }
                    return false ;				  
                }
            });
            if (!flag) {
                logger.error("数据过期！,更新单据表gmf_bill失败");
            }
        }				
		return true;
	}

	/**
	 * 审批拒绝  单据表的delete_num 修改成 id . 更新合法数据为未组批
	 * @param record
	 * @param userInfo 
	 * @return
	 */
	public boolean hookReject(Record record, final UserInfo userInfo) {
		
		final Integer id = TypeUtils.castToInt(record.get("id"));
		logger.info("======单据表gmf_bill==id=="+id);
		final Record rec = Db.findById("gmf_bill", "id", id);
		if(null == rec ) {
			logger.error("===========根据id未在表中查找到此单据");
			return false ;
		}
		final Integer source_sys = TypeUtils.castToInt(rec.get("source_sys"));																
		boolean flag = Db.tx(new IAtom() {
			
			@Override
			public boolean run() throws SQLException {
				
				boolean update  = CommonService.update("gmf_bill", 
				   new Record().set("update_by", userInfo.getUsr_id()).set("update_on", new Date())/*.set("delete_num", id)*/
				   .set("persist_version", rec.getInt("persist_version")+1 ), 
				   new Record().set("id", id));
				logger.info("========更新gmf_bill状态====="+update);
				if(update) {
					if(source_sys == 3) {
						logger.info("====此柜面付数据来源于TMP====");
						return CommonService.update("recv_counter_match", 
								new Record().set("status", WebConstant.SftLegalData.NOGROUP.getKey()),
								new Record().set("id", rec.get("legal_id")));
					}else {
						return CommonService.update("pay_legal_data", 
								new Record().set("status", WebConstant.SftLegalData.NOGROUP.getKey()),
								new Record().set("id", rec.get("legal_id")));											
					}
				}
				return false;
			}
		});
		if(!flag) {
			logger.error("====更新单据表 或者 合法数据表失败====");
			return flag ;
		}		
		return true;
	}
		
	/**
	 * 开始封装指令对象
	 * @param id
	 * @throws Exception
	 */
    private void sendPayDetail(final Long id) throws Exception {
        Record innerRec = Db.findById("gmf_bill", "id", id);
        if (innerRec == null) {
            throw new Exception("数据过期,未查询到此单据");
        }
        final Integer status = innerRec.getInt("service_status");
        if (status != WebConstant.BillStatus.FAILED.getKey() && status != WebConstant.BillStatus.PASS.getKey()) {
            throw new Exception("单据状态有误!:" + id);
        }
        String str = innerRec.getStr("recv_account_no"); //加密后的..需要解密
        SymmetricEncryptUtil  util = SymmetricEncryptUtil.getInstance();
		String recv_account_no = new String(util.decrypt(str), "utf-8");
		innerRec.set("recv_account_no", recv_account_no);
        String payCnaps = innerRec.getStr("pay_bank_cnaps");
        String payBankCode = payCnaps.substring(0, 3);
        IChannelInter channelInter = ChannelManager.getInter(payBankCode, "SinglePay");    
        innerRec.set("source_ref", "gmf_bill");
        final int old_repeat_count = TypeUtils.castToInt(innerRec.get("repeat_count"));
        innerRec.set("pay_account_bank", innerRec.get("pay_bank_name"));
        innerRec.set("recv_account_bank", innerRec.get("recv_bank_name"));
        innerRec.set("process_bank_type", innerRec.get("pay_bank_type"));
        innerRec.set("repeat_count", old_repeat_count + 1);
        innerRec.set("bank_serial_number", ChannelManager.getSerianlNo(payBankCode));
        innerRec.set("payment_amount", innerRec.get("amount"));
        SysSftSinglePayInter sysInter = new SysSftSinglePayInter();
        sysInter.setChannelInter(channelInter);
        final Record instr = sysInter.genInstr(innerRec);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean save = Db.save("single_pay_instr_queue", instr);
                if (save) {
                    return Db.update(Db.getSql("pay_counter.updBillById"), instr.getStr("bank_serial_number"),
                            instr.getInt("repeat_count"), WebConstant.BillStatus.PROCESSING.getKey(), instr.getStr("instruct_code"), new Date() , new Date(), id,
                            status, old_repeat_count) == 1;
                }
                return save;
            }
        });
        if (flag) {
            QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(instr), payBankCode);
            ProductQueue productQueue = new ProductQueue(bean);
            new Thread(productQueue).start();
        } else {
            throw new DbProcessException("发送失败，请联系管理员！");
        }
    }

    /**
     * 业务类型列表
     * @param record
     * @throws ReqDataException 
     */
	public List<Record> typecode(Record record) throws ReqDataException {
		Integer source_sys = TypeUtils.castToInt(record.get("source_sys"));
		List<Record> records  = new ArrayList<>() ;
		if(0==source_sys) {
			records = Db.find(Db.getSql("pay_counter.gettypecode"),1);
		}else if(1==source_sys){
			Record rec1 = new Record();
			Record rec2 = new Record();
			Record rec3 = new Record();
			Record rec4 = new Record();
			Record rec5 = new Record();
			rec1.set("type_code", "1");
			rec1.set("type_name", "定期结算退费");
			rec2.set("type_code", "5");
			rec2.set("type_name", "理赔给付");
			rec3.set("type_code", "10");
			rec3.set("type_name", "保全退费");
			rec4.set("type_code", "12");
			rec4.set("type_name", "基金单满期退费");
			rec5.set("type_code", "13");
			rec5.set("type_name", "客户账户退费");
			records.add(rec1);
			records.add(rec2);
			records.add(rec3);
			records.add(rec4);
			records.add(rec5);
		}else {
			return null;
		}
		return records ;
	}
      
}												
