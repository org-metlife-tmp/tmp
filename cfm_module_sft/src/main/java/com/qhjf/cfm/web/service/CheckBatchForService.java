package com.qhjf.cfm.web.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.IterableMap;
import org.apache.commons.collections.MapIterator;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.BizSerialnoGenTool;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.config.PlfConfigAccnoSection;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.constant.WebConstant.COMMONUodp;
import com.qhjf.cfm.web.constant.WebConstant.COMMONUser;
import com.qhjf.cfm.web.constant.WebConstant.MajorBizType;
import com.qhjf.cfm.web.constant.WebConstant.WfExpressType;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;

/**
 * 核对组批LA
 * 
 * @author pc_liweibing
 *
 */
public class CheckBatchForService {

	private final static Log logger = LogbackLog.getLog(CheckBatchForService.class);
	TxtDiskSendingService txtDiskSendingService = new TxtDiskSendingService();

	/**
	 * LA组批列表
	 * 
	 * @param record
	 * @param uodpInfo
	 * @param pageNum
	 * @param pageSize
	 * @return
	 * @throws ReqDataException
	 */
	public List<Record> list(Record record, UodpInfo uodpInfo) throws ReqDataException {
		Long org_id = uodpInfo.getOrg_id();
		Record findById = Db.findById("organization", "org_id", org_id);
		if (null == findById) {
			throw new ReqDataException("当前登录人的机构信息未维护");
		}
		List<String> codes = new ArrayList<>();
		if (findById.getInt("level_num") == 1) {
			logger.info("========目前登录机构为总公司");
			codes = Arrays.asList("0102", "0101", "0201", "0202", "0203", "0204", "0205", "0500");
		} else {
			logger.info("========目前登录机构为分公司公司");
			List<Record> rec = Db.find(Db.getSql("org.getCurrentUserOrgs"), org_id);
			for (Record o : rec) {
				codes.add(o.getStr("code"));
			}
		}
		record.set("codes", codes);
		List<Integer> status = record.get("status");
		if (status == null || status.size() == 0) {
			record.remove("status");
		}
		Integer source_sys = TypeUtils.castToInt(record.get("source_sys"));
		SqlPara sqlPara = null;
		if (0 == source_sys) {
			logger.info("=======数据来源LA======");
			sqlPara = Db.getSqlPara("check_batch.checkBatchLAlist", Kv.by("map", record.getColumns()));
		} else if (1 == source_sys) {
			logger.info("=======数据来源EBS======");
			sqlPara = Db.getSqlPara("check_batch.checkBatchEBSlist", Kv.by("map", record.getColumns()));
		} else {
			throw new ReqDataException("渠道传输不正确");
		}
		return Db.find(sqlPara);
	}

	/**
	 * 获取根据条件查到的总金额 和 总个数
	 * 
	 * @param record
	 * @return
	 * @throws ReqDataException
	 */
	public Record total(Record record) throws ReqDataException {
		Integer source_sys = record.getInt("source_sys");
		logger.info("==========当前的source_sys" + source_sys);
		SqlPara sqlPara = null;
		if (0 == source_sys) {
			sqlPara = Db.getSqlPara("check_batch.checkBatchLAFindAll", Kv.by("map", record.getColumns()));
		} else if (1 == source_sys) {
			sqlPara = Db.getSqlPara("check_batch.checkBatchEBSFindAll", Kv.by("map", record.getColumns()));
		} else {
			throw new ReqDataException("渠道传输不正确");
		}
		return Db.findFirst(sqlPara);
	}

	/**
	 * 组批LA勾选后提交
	 * 
	 * @param record
	 * @param dept_id
	 * @param org_id
	 * @param usr_id
	 * @param name
	 * @throws ReqDataException
	 */
	public void confirm(Record record, final UodpInfo curUodp, final UserInfo userInfo) throws ReqDataException {
		final Record main_record = new Record(); // 主批次对象
		final List<Long> ids = record.get("ids");
		final Record user_record = Db.findById("user_info", "usr_id", userInfo.getUsr_id());
		if (null == user_record) {
			throw new ReqDataException("当前登录人未在用户信息表内配置");
		}
		final Integer source_sys = TypeUtils.castToInt(record.get("source_sys"));
		final List<Integer> persist_version = record.get("persist_version");
		final Set<Integer> set = new HashSet<>();
		BigDecimal int_amount = new BigDecimal(0);
		List<Integer> list = new ArrayList<>();
		// 先判断版本号是否能与库中匹配上
		for (int i = 0; i < ids.size(); i++) {
			Record findById = Db.findById("pay_legal_data", "id", ids.get(i));
			if (findById.getInt("persist_version") != persist_version.get(i)) {
				throw new ReqDataException("勾选的列表中存在已被更改数据,请刷新页面");
			}
			set.add(findById.getInt("channel_id"));
			list.add(findById.getInt("status"));
			int_amount = int_amount.add(findById.getBigDecimal("amount"));
		}
		final BigDecimal total_amount_main = int_amount;
		if (set.size() > 1) {
			throw new ReqDataException("勾选的列表中存在不同渠道数据");
		}
		if (list.contains(1) || list.contains(2)) {
			throw new ReqDataException("勾选的列表中不允许存在已组批或者已作废数据");
		}
		// 封装 pay_batch_total 和 pay_batch_detail
		final Record channel_setting = Db.findById("channel_setting", "id", set.iterator().next());
		Integer is_checkout = TypeUtils.castToInt(channel_setting.get("is_checkout"));
		if (0 == is_checkout) {
			throw new ReqDataException("此渠道设置还未启动");
		}
		final Integer document_moudle = channel_setting.getInt("document_moudle");
		final BigDecimal limit = TypeUtils.castToBigDecimal(channel_setting.get("single_file_limit"));
		final BigDecimal num = new BigDecimal(ids.size()).divide(limit, 0, BigDecimal.ROUND_UP);
		logger.info("此渠道文件限制个数==" + limit + "分成的子批次个数==" + num);
		// 主批次入xx表.主批次走审批流
		// 子批次入pay_batch_total,pay_batch_detail ,并更新 pay_legal_data 表
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				// 封装主批次对象
				main_record.set("source_sys", source_sys)
				           .set("master_batchno", CommonService.getSftMasterBatchno())
						   .set("channel_id", set.iterator().next())
						   .set("org_id", curUodp.getOrg_id())
						   .set("dept_id", curUodp.getDept_id())
						   .set("total_num", ids.size())
					       .set("total_amount", total_amount_main)
					       .set("delete_flag", 0)
						   .set("process_bank_type", channel_setting.get("direct_channel"))
						   .set("is_checked", 0)
						   .set("pay_acc_no",channel_setting.get("acc_no") == null ? channel_setting.get("op_acc_no")
										: channel_setting.get("acc_no"))
						   .set("pay_acc_name",channel_setting.get("acc_name") == null ? channel_setting.get("op_acc_name")
										: channel_setting.get("acc_name"))
						   .set("pay_bank_name",channel_setting.get("bank_name") == null ? channel_setting.get("op_bank_name")
										: channel_setting.get("bank_name"))
						   .set("create_by", userInfo.getUsr_id())
						   .set("create_on", new Date())
						   .set("update_by", userInfo.getUsr_id())
						   .set("update_on", new Date())
						   .set("persist_version", 0)
						   .set("service_status", WebConstant.BillStatus.SUBMITED.getKey())
						   .set("is_inner", channel_setting.get("is_inner"))
						   .set("net_mode", channel_setting.get("net_mode"));
				boolean save_main = Db.save("pay_batch_total_master", "id", main_record);
				logger.info("=================入库pay_batch_total_master表==" + save_main);
				if (!save_main) {
					return false;
				}
				for (int j = 1; j <= num.intValue(); j++) {
					int total_num = limit.intValue();
					List<Long> new_ids = new ArrayList<>();
					if (ids.size() <= limit.intValue()) {
						logger.info("===========勾选合法数据个数小于等于限制个数");
						total_num = ids.size();
						new_ids = ids;
					} else {
						logger.info("===========勾选合法数据个数大于限制个数");
						if (j == num.intValue()) {
							total_num = new BigDecimal(ids.size()).subtract(new BigDecimal(j - 1).multiply(limit))
									.intValue();
							new_ids = ids.subList((j - 1) * limit.intValue(), ids.size());
						} else {
							new_ids = ids.subList((j - 1) * limit.intValue(), j * limit.intValue());
						}
					}
					Record sunAmount = Db
							.findFirst(Db.getSqlPara("check_batch.checkBatchSumAmount", Kv.by("map", new_ids)));
					final Record pay_batch_total = new Record();
					pay_batch_total.set("child_batchno", CommonService.getSftSonBatchno())
							       .set("master_batchno", main_record.get("master_batchno"))
							       .set("total_num", total_num)
							       .set("total_amount", sunAmount.get("sumAmount"))
							       .set("success_num", 0)
							       .set("success_amount", new BigDecimal(0))
							       .set("fail_num", 0)
							       .set("fail_amount", new BigDecimal(0))
							       .set("service_status", WebConstant.SftCheckBatchStatus.SPZ.getKey())
							       .set("source_sys", source_sys);
					List<Record> find = null;
					if (0 == source_sys) {
						find = Db.find(Db.getSqlPara("check_batch.checkBatchLADetail", Kv.by("map", new_ids)));
					} else if (1 == source_sys) {
						find = Db.find(Db.getSqlPara("check_batch.checkBatchEBSDetail", Kv.by("map", new_ids)));
					} else {
						return false;
					}
					logger.info("===============开始入库pay_batch_total");
					boolean save = Db.save("pay_batch_total", "id", pay_batch_total);
					logger.info("===============入库pay_batch_total==" + save);
					if (!save) {
						return false;
					}
					final List<Record> lists = new ArrayList<>();
					for (int i = 0; i < find.size(); i++) {
						Record pay_batch_detail = new Record();
						Record rec = find.get(i);
						pay_batch_detail.set("legal_id", rec.get("pay_id"))
						                .set("base_id", pay_batch_total.getInt("id"))
								        .set("org_id", rec.get("org_id"))
								        .set("origin_id", rec.get("origin_id"))
								        .set("org_code", rec.get("org_code"))
								        .set("amount", rec.get("amount"))
								        .set("recv_acc_name", rec.get("recv_acc_name"))
								        .set("recv_cert_type", rec.get("recv_cert_type"))
								        .set("recv_cert_code", rec.get("recv_cert_code"))
								        .set("recv_bank_name", rec.get("recv_bank_name"))
								        .set("recv_bank_type", rec.get("recv_bank_type"))
								        .set("recv_acc_no", rec.get("recv_acc_no"))
								        .set("master_batchno", main_record.get("master_batchno"))
								        .set("child_batchno", pay_batch_total.get("child_batchno"));
						if (WebConstant.Channel.JP.getKey() == document_moudle) {
							pay_batch_detail.set("package_seq", i + 1);
						} else {
							pay_batch_detail.set("package_seq", txtDiskSendingService.getCode(i + 1, 6));
						}
						lists.add(pay_batch_detail);
					}
					int[] batchSave = Db.batchSave("pay_batch_detail", lists, 1000);
					boolean save1 = ArrayUtil.checkDbResult(batchSave);
					if (save1) {
						logger.info("======入库pay_batch_detail结果==" + save1);
						// 更新pay_legal_data表
						int update = Db.update(Db.getSqlPara("check_batch.updateLegalByGroup",
								Kv.by("map", new Record().set("ids", new_ids).set("op_date", new Date())
										.set("op_user_name", user_record.get("name")).getColumns())));
						logger.info("============更新合法数据表条数pay_legal_data==" + update);
						if (update != new_ids.size()) {
							return false;
						}
					} else {
						return false;
					}
				}
				// 走审批流
				List<Record> flows = null;
				try {
					flows = CommonService.displayPossibleWf(WebConstant.MajorBizType.PLF.getKey(), curUodp.getOrg_id(),
							null);
				} catch (BusinessException e) {
					e.printStackTrace();
					logger.error("============获取收付费审批流异常");
					return false;
				}
				if (flows == null || flows.size() == 0) {
					logger.error("============未查询到收付费审批流");
					return false;
				}
				Record flow = flows.get(0);
				main_record.set("define_id", flow.getLong("define_id"));
				main_record.set("service_serial_number", main_record.get("master_batchno"));
				// TODO
				WfRequestObj wfRequestObj = new WfRequestObj(WebConstant.MajorBizType.PLF, "pay_batch_total_master",
						main_record) {
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
					logger.error("=========收付费提交审批流失败");
					return false;
				}
				if (!submitFlowFlg) {
					return false;
				}
				return true;
			}
		});
		if (!flag) {
			throw new ReqDataException("组批失败");
		}
	}

	/**
	 * @审批通过,重写此方法,开启一个调拨或者支付通的审批流
	 * @param record
	 * @param userInfo
	 * @return
	 */
	public boolean hookPass(Record record, final UserInfo userInfo) {
		Long master_id = record.getLong("id");
		final Record main_record = Db.findById("pay_batch_total_master", "id", master_id);
		if (null == main_record) {
			logger.error("===============此条数据数据库中未找到====" + master_id);
		}
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				// 此处需要把子批次表更新为已审批未发送
				boolean update = CommonService.update("pay_batch_total",
						new Record().set("service_status", WebConstant.SftCheckBatchStatus.SPWFS.getKey()),
						new Record().set("master_batchno", main_record.get("master_batchno")));
				logger.info(" ==========子批表更新为已审批未发送==" + update);
				if (update) {
					// 审批通过,此处需要开启一个 调拨/支付审批流
					Integer is_inner = main_record.getInt("is_inner");
					MajorBizType type;
					String serviceSerialNumber = "";
					String accno = PlfConfigAccnoSection.getInstance().getAccno();
					logger.info("===========配置文件获取到的账号======="+accno);
					Record payRec = Db.findFirst(Db.getSql("nbdb.findAccountByAccNo"), accno);
					if (payRec == null) {
						logger.error("=============未在系统内找到此账户======" + accno);
					}
					Record insertRecord = new Record();
					insertRecord.set("org_id", main_record.getInt("org_id"))
							.set("dept_id", main_record.getInt("dept_id"))
							.set("pay_account_id", payRec.get("acc_id"))
							.set("pay_account_no", payRec.get("acc_no"))
							.set("pay_account_name", payRec.get("acc_name"))
							.set("pay_account_cur", "CNY")
							.set("pay_account_bank", payRec.get("bank_name"))
							.set("pay_bank_cnaps", payRec.get("cnaps_code"))
							.set("pay_bank_prov", payRec.get("province"))
							.set("pay_bank_city", payRec.get("city"))
							.set("recv_account_no", main_record.get("pay_acc_no"))
							.set("recv_account_name", main_record.get("pay_acc_name"))
							.set("recv_account_bank", main_record.get("pay_bank_name"))
							.set("payment_amount", main_record.get("total_amount"))
							.set("service_status", WebConstant.BillStatus.SAVED.getKey())
							.set("create_by", userInfo.getUsr_id())
							.set("create_on", new Date())
							.set("delete_flag", 0)
							.set("persist_version", 0);

					if (0 == is_inner) {
						logger.info("===============此处需要开启一个对外支付的审批流");
						type = WebConstant.MajorBizType.ZFT;
						serviceSerialNumber = BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.ZFT);
						insertRecord.set("biz_id", "801dbfb1bfb34817b9e61ce29d86b47b")
						            .set("biz_name", "对外支付")
								    .set("service_serial_number", serviceSerialNumber);
						//封装 recv_account_id
						List<Record> find = Db.find(Db.getSql("supplier.querySupplier"),main_record.get("pay_acc_no"));
						insertRecord.set("recv_account_id", find.get(0).get("id"));
						boolean save = Db.save("outer_zf_payment", "id", insertRecord);
						logger.info("===============入库支付通的结果==="+ save + "==id=="+ insertRecord.getLong("id"));
						
					} else {
						logger.info("===============此处需要开启一个调拨的审批流");
						type = WebConstant.MajorBizType.INNERDB;
						// 获取配置的调拨付款方账号
						serviceSerialNumber = BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.INNERDB);
						insertRecord.set("biz_id", "03bead791d6244a9B513bb789799fa3a")
						            .set("biz_name", "资金调拨")
								    .set("service_serial_number", serviceSerialNumber)
						            .set("pay_mode",1);
						//封装 recv_account_id  
						List<Record> find = Db.find(Db.getSql("acc.findAccountByAccNo"),main_record.get("pay_acc_no"));
						insertRecord.set("recv_account_id", find.get(0).get("id"));
						boolean save = Db.save("inner_db_payment", "id", insertRecord);
						logger.info("===============入库调拨通的结果==="+ save + "==id=="+ insertRecord.getLong("id"));
					}
					// 走审批流
					List<Record> flows = null;
					try {
						flows = CommonService.displayPossibleWf(type.getKey(), insertRecord.getInt("org_id"), null);
					} catch (BusinessException e) {
						e.printStackTrace();
						logger.error("============获取审批流异常");
						return false;
					}
					if (flows == null || flows.size() == 0) {
						logger.error("============未查询到审批流");
						return false;
					}
					Record flow = flows.get(0);
					insertRecord.set("define_id", flow.getLong("define_id"));
				    String tableName = null;
					try {
						tableName = type.getTableName();
					} catch (WorkflowException e1) {
						e1.printStackTrace();
					}
					WfRequestObj wfRequestObj = new WfRequestObj(type, tableName, insertRecord) {

						@Override
						public <T> T getFieldValue(WfExpressType type) throws WorkflowException {
							// TODO Auto-generated method stub
							return null;
						}

						@Override
						public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
							// TODO Auto-generated method stub
							return null;
						}};
					WorkflowProcessService workflowProcessService = new WorkflowProcessService();
					boolean submitFlowFlg;
					try {
						submitFlowFlg = workflowProcessService.startWorkflow(wfRequestObj, userInfo);
					} catch (WorkflowException e) {
						e.printStackTrace();
						logger.error("=========提交审批流失败");
						return false;
					}
					if (!submitFlowFlg) {
						return false;
					}
					return true;
				}
				return false;
			}
		});
		return flag;
	}

	/**
	 * 核对组批撤回
	 * 
	 * @param record
	 * @return
	 * @throws ReqDataException
	 */
	public void revokeToLaOrEbs(Record record) throws ReqDataException {
		final Integer source_sys = TypeUtils.castToInt(record.get("source_sys"));
		final Integer id = TypeUtils.castToInt(record.get("id"));
		final Integer persist_version = TypeUtils.castToInt(record.get("persist_version"));
		final String feed_back = record.getStr("feed_back");
		Record findById = Db.findById("pay_legal_data", "id", id);
		if (null == findById) {
			throw new ReqDataException("此条数据已经过期,请刷新页面");
		}
		Integer status = TypeUtils.castToInt(findById.get("status"));
		if (WebConstant.SftLegalData.NOGROUP.getKey() != status) {
			throw new ReqDataException("此条数据状态不符合撤回要求");
		}
		final Long origin_id = findById.getLong("origin_id");
		logger.info("=======原始数据id======" + origin_id);
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				boolean update = CommonService.update("pay_legal_data",
						new Record().set("status", WebConstant.SftLegalData.REVOKE.getKey())
								.set("process_msg", feed_back).set("persist_version", persist_version + 1),
						new Record().set("id", id).set("persist_version", persist_version));
				if (update) {
					logger.info("====撤回更新pay_legal_data====" + update);
					boolean update1 = false;
					if (0 == source_sys) {
						logger.info("=========系统来源LA,更新la_origin_pay_data表");
						Record origin = Db.findById("la_origin_pay_data", "id", origin_id);
						update1 = CommonService.update("la_origin_pay_data",
								new Record().set("tmp_status", 2).set("tmp_err_message", feed_back)
										.set("persist_version", origin.getInt("persist_version") + 1),
								new Record().set("id", origin_id).set("persist_version",
										origin.getInt("persist_version")));
					} else if (1 == source_sys) {
						logger.info("=========系统来源EBS,更新ebs_origin_pay_data表");
						Record origin = Db.findById("ebs_origin_pay_data", "id", origin_id);
						update1 = CommonService.update("ebs_origin_pay_data",
								new Record().set("tmp_status", 2).set("tmp_err_message", feed_back)
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
			logger.error("=============组批撤回数据库更新失败");
			throw new ReqDataException("组批撤回失败");
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
	 * 获取渠道编码列表
	 * 
	 * @param record
	 * @return
	 */
	public List<Record> channelCodeList(Record record) {
		return Db.find(Db.getSqlPara("check_batch.selectChannelCodeList", Kv.by("map", record.getColumns())));
	}

	/**
	 * 审批拒绝通过后,修改相关表状态 更新合法数据表状态为未组批.更新组批详情表的delete_num为此条数据id
	 * 
	 * @param record
	 * 
	 */

	public boolean hookReject(Record record) {
		Integer id = TypeUtils.castToInt(record.get("id"));
		logger.info("=========pay_batch_total_master表id==" + id);
		final Record findById = Db.findById("pay_batch_total_master", "id", id);
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				// 重写的reject就会更新主批次表==单据表
				int update = Db.update(Db.getSql("check_batch.updateLegalDataByTotal"), findById.get("master_batchno"),
						0);
				logger.info("=====更新pay_batch_data条数==" + update);
				if (update > 0) {
					int update1 = Db.update(Db.getSql("check_batch.updateDetailByTotal"),
							findById.get("master_batchno"), 0);
					if (update == update1) {
						// 开始更新原始数据表

						logger.info("=====更新子批次表pay_batch_total状态为审批拒绝");
						return CommonService.update("pay_batch_total",
								new Record().set("service_status", WebConstant.SftCheckBatchStatus.SPJJ.getKey()),
								new Record().set("master_batchno", findById.get("master_batchno")));
					}
				}
				return false;
			}
		});
		return flag;
	}

	/**
	 * 
	 * @param
	 * @return
	 * @throws ReqDataException
	 */
	public Record detail(Long id) throws ReqDataException {
		Record pay_batch_total_master = Db.findById("pay_batch_total_master", "id", id);
		if (null == pay_batch_total_master) {
			throw new ReqDataException("此条数据已经过期");
		}
		return Db.findFirst(Db.getSql("disk_downloading.findBatchDetail"), id);
	}

	/**
	 * @根据主单次号查找子单次详情
	 * @param record
	 * @return 
	 */
	public List<Record> findSonByMasterBatch(Record record) {
		// TODO Auto-generated method stub
		String master_batchno = record.getStr("master_batchno");
		return Db.find(Db.getSql("check_batch.selectSonByMasterno"), master_batchno);
	}



}
