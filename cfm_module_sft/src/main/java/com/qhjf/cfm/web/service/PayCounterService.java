package com.qhjf.cfm.web.service;


import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.config.GmfConfigAccnoSection;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;


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
	 * @throws ReqDataException 
	 */
	public Page<Record> list(int pageNum, int pageSize, Record record, Long org_id) throws ReqDataException {
		
		// 获取付款账号
		String pay_account_no = GmfConfigAccnoSection.getInstance().getAccno();
		logger.info("============配置文件中柜面付账号=="+pay_account_no);
		Record payRec = Db.findFirst(Db.getSql("nbdb.findAccountByAccno"), pay_account_no);
		if (payRec == null) {
			logger.error("=============未在系统内找到此账户======" + pay_account_no);
		}
		String pay_account_bank = payRec.getStr("pay_account_bank");
		logger.info("=====当前登录人org_id==="+org_id);  
    	Record findById = Db.findById("organization", "org_id", org_id);
		if(null == findById){
			throw new ReqDataException("当前登录人的机构信息未维护");
		}
		Integer source_sys = record.getInt("source_sys");
		
		List<String> codes = new ArrayList<>();
		if(findById.getInt("level_num") == 1){
			logger.info("========目前登录机构为总公司");
			codes = Arrays.asList("0102","0101","0201","0202","0203","0204","0205","0500");
		}else{
			logger.info("========目前登录机构为分公司公司");
			Record findFirst = Db.findFirst(Db.getSql("org.getCurrentUserOrgs"), org_id);
			codes.add(findFirst.getStr("code"));
		}
		record.set("codes", codes);
		List<Integer> status = record.get("status");
		if(null == status) {
			record.remove("status");			
		}
		String pay_mode = record.getStr("pay_mode");
		//柜面付  默认网银
		if( StringUtils.isBlank(pay_mode) ) {
			record.set("pay_mode", WebConstant.SftDoubtPayMode.WY.getKeyc());
		}
		SqlPara sqlPara = null;
		if(source_sys == 0) {
			sqlPara = Db.getSqlPara("pay_counter.findPayCounterList", Kv.by("map", record.getColumns()));				
		}else {
			
		}
		 Page<Record> paginate = Db.paginate(pageNum, pageSize, sqlPara);
		 List<Record> list = paginate.getList();
		 if(null != list && list.size() > 0) {
			 for (Record rec : list) {
				 rec.set("pay_account_no", pay_account_no);
				 rec.set("pay_account_bank", pay_account_bank);
			}
		 }
		 return paginate ;
	}

	/**
	 * 补录按钮
	 * @param record
	 * @param userInfo
	 * @throws ReqDataException 
	 */
	public void supplement(final Record record, UserInfo userInfo) throws ReqDataException {
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
			
		// 开始更新合法数据表
		// supply_status 更新为  1:已补录    
		boolean flag = Db.tx(new IAtom() {			
			@Override
			public boolean run() throws SQLException {
					boolean update = CommonService.update("pay_legal_data", 
						 new Record().set("op_date", new Date()).set("op_user_name", user_record.get("name"))
						.set("supply_status", WebConstant.Sft_supplyStatus.YBL.getKey()).set("recv_acc_no", record.get("recv_acc_no"))
						.set("recv_bank_name", record.get("recv_bank_name")).set("recv_bank_cnaps", record.get("recv_cnaps_code"))
						.set("payment_summary", record.get("payment_summary")).set("persist_version", persist_version+1), 
						 new Record().set("id", record.get("pay_id")));
					if(update) {
						// 删除附件  暂时将附件与合法数据id关联 ===提交后将附件与单据关联
						CommonService.delFileRef(WebConstant.MajorBizType.GMF.getKey(), record.getInt("pay_id"));
						if (list != null && list.size() > 0) {
							// 保存附件
							return CommonService.saveFileRef(WebConstant.MajorBizType.GMF.getKey(), record.getInt("pay_id"), list);
						}						
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
		Long pay_id = record.getLong("pay_id");
		final Integer source_sys = record.getInt("source_sys");
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
			//TODO
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
	public void confirm (Record record, UserInfo userInfo, UodpInfo uodpInfo) throws ReqDataException{
		
		// 获取付款账号
				String pay_account_no = GmfConfigAccnoSection.getInstance().getAccno();
				logger.info("============配置文件中柜面付账号=="+pay_account_no);
				Record payRec = Db.findFirst(Db.getSql("nbdb.findAccountByAccno"), pay_account_no);
				if (payRec == null) {
					logger.error("=============未在系统内找到此账户======" + pay_account_no);
				}
		
		Integer source_sys = record.getInt("source_sys");
		List<Integer> pay_ids = record.get("pay_id");
		Long org_id = uodpInfo.getOrg_id();
		List<Record> statuss = Db.find(Db.getSqlPara("pay_counter.findDistinctStatus", Kv.by("map", pay_ids)));
		if(null != statuss && statuss.size() > 0) {
			for (Record sta : statuss) {
				if(sta.getInt("status") == WebConstant.SftLegalData.GROUPBATCH.getKey() 
						|| sta.getInt("status") == WebConstant.SftLegalData.REVOKE.getKey() ) {
					throw new ReqDataException("选中提交的数据中包含已提交/已作废数据");
				}
			}
		}		
		List<Record> Details = null ;
		if(0 == source_sys) {
			logger.info("===========LA数据进行提交");
			Details = Db.find(Db.getSqlPara("check_batch.checkBatchLADetail", Kv.by("map", pay_ids)));			
		}else {
			logger.info("===========EBS数据进行提交");
			Details = Db.find(Db.getSqlPara("check_batch.checkBatchEBSDetail", Kv.by("map", pay_ids)));			
		}
        if(null == Details || pay_ids.size() != Details.size()) {
        	throw new ReqDataException("勾选数据中部分数据已过期,请刷新页面");
        }
        final List<Record>  insertRecords = new ArrayList<>();
        final List<Record>  updateLegalRecords = new ArrayList<>();
        //产生单据,并开启审批流
        for (Record rec : Details) {
        	Record updateLegalRecord  = new Record();
        	Record insertrec  = new Record();
        	Record cnapsRecord = Db.findById("all_bank_info", "cnaps_code", rec.get("recv_bank_cnaps"));
        	insertrec.set("legal_id", rec.get("pay_id"))
        	          .set("origin_id", rec.get("origin_id"))
        	          .set("source_sys", source_sys)
        	          .set("org_id", org_id)
        	          .set("dept_id",uodpInfo.getDept_id())
        	          .set("amount", rec.get("amount"))
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
        	          .set("create_on", new Date());
        	updateLegalRecord.set("id", rec.get("pay_id"))
        	                 .set("status", WebConstant.SftLegalData.GROUPBATCH.getKey());
        	insertRecords.add(insertrec);
        	updateLegalRecords.add(updateLegalRecord);
		}	
        // 单据入库 ,合法数据表更新状态
      /*  boolean flag = Db.tx(new IAtom() {			
			@Override
			public boolean run() throws SQLException {
				
				// 先更新合法数据为已提交 , 入库单据表 , 将补录的附件关联到单据上 , 所有单据开启审批流  
			    int[] batchUpdate = Db.batchUpdate("pay_legal_data", "id", updateLegalRecords, 1000);
			    boolean updateResult = ArrayUtil.checkDbResult(batchUpdate);
				int[] batchSave = Db.batchSave("gmf_bill", insertRecords, 1000);
				boolean checkDbResult = ArrayUtil.checkDbResult(batchSave);
			}   
		});
        if(!flag) {
        	throw new ReqDataException("柜面付页面确认失败");
        }*/
	}
		
}
