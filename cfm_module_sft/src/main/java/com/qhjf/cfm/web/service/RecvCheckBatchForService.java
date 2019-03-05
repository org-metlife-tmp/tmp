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
import org.apache.commons.lang3.StringUtils;

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
import com.qhjf.cfm.web.queue.DiskDownloadingQueue;
import com.qhjf.cfm.web.queue.RecvDiskDownloadingQueue;
import com.qhjf.cfm.web.webservice.oa.server.OaDataDoubtfulCache;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;
import com.qhjf.cfm.web.webservice.sft.SftRecvCallBack;

/**
 * 核对组批LA
 * 
 * @author pc_liweibing
 *
 */
public class RecvCheckBatchForService {

	private final static Log logger = LogbackLog.getLog(RecvCheckBatchForService.class);
	TxtDiskSendingService txtDiskSendingService = new TxtDiskSendingService();

	private static String recv_la_pre = "recv_la_batch";
	/**
	 *    批收LA/EBS组批列表
	 * @param pageSize 
	 * @param pageNum 
	 * 
	 * @param record
	 * @param uodpInfo
	 * @return
	 * @throws ReqDataException
	 */
	public Page<Record> list(int pageNum, int pageSize, Record record, UodpInfo uodpInfo) throws ReqDataException {
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
		SqlPara sqlPara = Db.getSqlPara("recv_check_batch.recvcheckBatchLAlist", Kv.by("map", record.getColumns()));
		return Db.paginate(pageNum, pageSize, sqlPara);
	}


	/**
	 * 批付组批LA勾选后提交
	 * 
	 * @param record
	 * @param curUodp
	 * @param userInfo
	 * @throws ReqDataException
	 */
	public void confirm(final Record record, final UodpInfo curUodp, final UserInfo userInfo) throws ReqDataException {
		int flag_redis = 0 ;  // 是否确实有人在组批此渠道数据
		final Long channel_id = TypeUtils.castToLong(record.get("channel_id"));
		OaDataDoubtfulCache oaDataDoubtfulCache = new OaDataDoubtfulCache();
		final Record main_record = new Record(); // 主批次对象
		try {
			final Record user_record = Db.findById("user_info", "usr_id", userInfo.getUsr_id());
			if (null == user_record) {
				throw new ReqDataException("当前登录人未在用户信息表内配置");
			}
			List<Long> status = record.get("status");
			
			List<Long> remove_ids = record.get("remove_ids");
			if( null ==remove_ids || remove_ids.size() == 0) {
				record.remove("remove_ids");
			}

			final Record channel_setting = Db.findById("channel_setting", "id", channel_id);
			Integer is_checkout = TypeUtils.castToInt(channel_setting.get("is_checkout"));
			if (0 == is_checkout) {
				throw new ReqDataException("此渠道设置还未启动");
			}

			if (status != null && status.size() > 0) {
				if (status.contains("1") || status.contains("2")) {
					throw new ReqDataException("查询条件中请剔除已组批或者已撤回");
				}
			} else {
				record.set("status", new int[] { WebConstant.SftLegalData.NOGROUP.getKey() });
			}
			Set<String> oaCacheValue = oaDataDoubtfulCache.GetValue(recv_la_pre);
			if (null == oaCacheValue) {
				logger.info(recv_la_pre + "==========此key在redis内不存在");
				logger.info(channel_id + "==========此value新增入redis");
				oaDataDoubtfulCache.sAddValue(recv_la_pre, String.valueOf(channel_id));
			} else {
				if (oaCacheValue.contains(channel_id)) {
					flag_redis = 1 ;
					logger.error( "批收中此渠道==" + channel_id + "正在审批中");
					throw new ReqDataException("此渠道正在审批中,暂不允许操作");
				} else {
					logger.error("批收中此渠道==" + channel_id + "可以正常组批");
					oaDataDoubtfulCache.sAddValue(recv_la_pre, String.valueOf(channel_id));
				}
			}
			Long org_id = curUodp.getOrg_id();
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
				Record rec = Db.findFirst(Db.getSql("org.getCurrentUserOrgs"), org_id);
			    codes.add(rec.getStr("code"));
			}
			record.set("codes", codes);
			
			// 主批次入recv_batch_total_master表
			// 子批次入pay_batch_total,pay_batch_detail ,并更新 pay_legal_data 表
			boolean flag = Db.tx(new IAtom() {
				@Override
				public boolean run() throws SQLException {
					Record total_amount_master = null ;
					List<Record>  ids = Db
							   .find(Db.getSqlPara("recv_check_batch.checkBatchLAlist_confirm", Kv.by("map", record.getColumns())));	
					   total_amount_master = Db
							   .findFirst(Db.getSqlPara("recv_check_batch.checkBatchLAlistAmount_confirm", Kv.by("map", record.getColumns())));			

					// 封装 recv_batch_total 和 recv_batch_detail
					final Integer document_moudle = channel_setting.getInt("document_moudle");

					final BigDecimal limit = channel_setting.get("single_file_limit") == null ? new BigDecimal(ids.size())
							: new BigDecimal(channel_setting.getInt("single_file_limit"));
					final BigDecimal num = new BigDecimal(ids.size()).divide(limit, 0, BigDecimal.ROUND_UP);
					logger.info("此渠道文件限制个数==" + limit + "分成的子批次个数==" + num);
					
					// 封装主批次对象
					main_record.set("source_sys", 0)
					        .set("master_batchno", CommonService.getSftMasterBatchno())
							.set("channel_id", channel_id)
							.set("org_id", curUodp.getOrg_id())
							.set("dept_id", curUodp.getDept_id())
							.set("total_num", ids.size())
							.set("total_amount", total_amount_master.getBigDecimal("total_amount_master"))
							.set("delete_flag", 0)
							.set("process_bank_type", channel_setting.get("direct_channel"))
							.set("is_checked", 0)
							.set("recv_acc_no",
									StringUtils.isBlank(channel_setting.getStr("op_acc_no")) ? channel_setting.get("acc_no")
											: channel_setting.get("op_acc_no"))
							.set("recv_acc_name",
									StringUtils.isBlank(channel_setting.getStr("op_acc_name")) ? channel_setting.get("acc_name")
											: channel_setting.get("op_acc_name"))
							.set("recv_bank_name",
									StringUtils.isBlank(channel_setting.getStr("op_bank_name")) ? channel_setting.get("bank_name")
											: channel_setting.get("op_bank_name"))
							.set("create_by", userInfo.getUsr_id())
							.set("create_on", new Date())
							.set("update_by", userInfo.getUsr_id())
							.set("update_on", new Date())
							.set("persist_version", 0)
							.set("service_status", WebConstant.BillStatus.SUBMITED.getKey());
					boolean save_main = Db.save("recv_batch_total_master", "id", main_record);
					logger.info("=================入库recv_batch_total_master表==" + save_main);
					if (!save_main) {
						return false;
					}
					for (int j = 1; j <= num.intValue(); j++) {
						int total_num = limit.intValue();
						List<Record> new_ids = new ArrayList<>();
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
						//查询 new_ids 内合法数据总金额
						Record sumamount_rec = Db.findFirst(Db.getSqlPara("recv_check_batch.checkBatchSumAmount", Kv.by("map", new_ids)));
						final Record recv_batch_total = new Record();
						recv_batch_total.set("child_batchno", CommonService.getSftSonBatchno())
								.set("master_batchno", main_record.get("master_batchno"))
								.set("total_num", total_num)
								.set("total_amount", sumamount_rec.getBigDecimal("sumAmount"))
								.set("success_num", 0)
								.set("success_amount", new BigDecimal(0))
								.set("fail_num", 0)
								.set("fail_amount", new BigDecimal(0))
								.set("service_status", WebConstant.SftCheckBatchStatus.ZPWFS.getKey())  //已组批未发送
								.set("source_sys", 0)
								.set("create_by", userInfo.getUsr_id())
								.set("create_on", new Date());
						logger.info("===============开始入库recv_batch_total");
						boolean save = Db.save("recv_batch_total", "id", recv_batch_total);
						logger.info("===============入库recv_batch_total==" + save);
						if (!save) {
							return false;
						}
						List<Record> 
							find = Db.find(Db.getSqlPara("recv_check_batch.checkBatchLADetail", Kv.by("map", new_ids)));
						final List<Record> lists = new ArrayList<>();
						for (int i = 0; i < find.size(); i++) {
							Record recv_batch_detail = new Record();
							Record rec = find.get(i);
							recv_batch_detail.set("legal_id", rec.get("recv_id"))
									.set("base_id", recv_batch_total.getInt("id"))
									.set("org_id", rec.get("org_id"))
									.set("origin_id", rec.get("origin_id"))
									.set("org_code", rec.get("org_code"))
									.set("amount", rec.get("amount"))
									.set("pay_acc_name", rec.get("pay_acc_name"))
									.set("pay_cert_type", rec.get("pay_cert_type"))
									.set("pay_cert_code", rec.get("pay_cert_code"))
									.set("pay_bank_name", rec.get("pay_bank_name"))
									.set("pay_bank_type", rec.get("pay_bank_type"))
									.set("pay_acc_no", rec.get("pay_acc_no"))
									.set("master_batchno", main_record.get("master_batchno"))
									.set("child_batchno", recv_batch_total.get("child_batchno"));
							if (WebConstant.Channel.JP.getKey() == document_moudle) {
								recv_batch_detail.set("package_seq", i + 1);
							} else {
								recv_batch_detail.set("package_seq", txtDiskSendingService.getCode(i + 1, 6));
							}
							lists.add(recv_batch_detail);
						}

						int[] batchSave = Db.batchSave("recv_batch_detail", lists, 1000);
						boolean save1 = ArrayUtil.checkDbResult(batchSave);
						if (save1) {
							logger.info("======入库recv_batch_detail结果==" + save1);
							// 更新recv_legal_data表
							int update = Db.update(Db.getSqlPara("recv_check_batch.updateLegalByGroup",
									Kv.by("map", new Record().set("ids", new_ids).set("op_date", new Date())
											.set("op_user_name", user_record.get("name")).getColumns())));
							logger.info("============更新合法数据表条数recv_legal_data==" + update);
							if (update != new_ids.size()) {
								return false;
							}
						} else {
							return false;
						}
					}
					return true;
				}
			});
			if (!flag) {
				throw new ReqDataException("组批失败");
			}
		}  finally {
			// 组批成功,删除redis中值
			if(flag_redis == 0) {
				oaDataDoubtfulCache.sremValue(recv_la_pre, String.valueOf(channel_id));				
			}
		}
		//此时直接开启一个异步线程,下载盘片
		RecvDiskDownloadingQueue recvDiskDownloadingQueue = new RecvDiskDownloadingQueue();
		recvDiskDownloadingQueue.setMain_record(main_record);
		Thread thread = new Thread(recvDiskDownloadingQueue); 
		thread.start();
	}



	/**
	 * 核对组批撤回
	 * 
	 * @param record
	 * @return
	 * @throws ReqDataException
	 */
	public void revokeToLaOrEbs(Record record) throws ReqDataException {
		final Integer id = TypeUtils.castToInt(record.get("id"));
		final Integer persist_version = TypeUtils.castToInt(record.get("persist_version"));
		final String feed_back = record.getStr("feed_back");
		Record findById = Db.findById("recv_legal_data", "id", id);
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
				boolean update = CommonService.update("recv_legal_data",
						new Record().set("status", WebConstant.SftLegalData.REVOKE.getKey())
								.set("process_msg", feed_back).set("persist_version", persist_version + 1),
						new Record().set("id", id).set("persist_version", persist_version));
				logger.info("====撤回更新recv_legal_data====" + update);
				if (update) {
						logger.info("=========系统来源LA,更新la_origin_recv_data表");
						Record origin = Db.findById("la_origin_recv_data", "id", origin_id);
						return	 CommonService.update("la_origin_recv_data",
								new Record().set("tmp_status", 2).set("tmp_err_message", feed_back)
										.set("persist_version", origin.getInt("persist_version") + 1),
								new Record().set("id", origin_id).set("persist_version",
										origin.getInt("persist_version")));
				}
				return false;
			}
		});
		if (!flag) {
			logger.error("=============组批撤回数据库更新失败");
			throw new ReqDataException("组批撤回失败");
		}
		try {
			SftRecvCallBack callback = new SftRecvCallBack();
			callback.callback(0, origin_id);
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
	 * @根据主单次号查找子单次详情
	 * @param record
	 * @return 
	 */
	public List<Record> findSonByMasterBatch(Record record) {
		// TODO Auto-generated method stub
		String master_batchno = record.getStr("master_batchno");
		return Db.find(Db.getSql("check_batch.selectSonByMasterno"), master_batchno);
	}


	/**
	 * 获取未提交的总金额,总个数
	 * @param record
	 * @param curUodp
	 * @return
	 * @throws ReqDataException 
	 */
	public Record totalInfo(Record record, UodpInfo curUodp) throws ReqDataException {
		Long org_id = curUodp.getOrg_id();
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
		record.set("status", new int[] {
				WebConstant.SftLegalData.NOGROUP.getKey()
		});
		SqlPara sqlPara =
	          Db.getSqlPara("recv_check_batch.checkBatchLAAmount", Kv.by("map", record.getColumns()));
		return Db.findFirst(sqlPara);
	}



}
