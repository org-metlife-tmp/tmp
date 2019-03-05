package com.qhjf.cfm.web.service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.util.TypeUtils;
import com.ibm.icu.text.SimpleDateFormat;
import com.jfinal.kit.Kv;
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
import com.qhjf.cfm.web.config.DiskUpLoadSection;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.excelup.UploadFileScaffold;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;
import com.qhjf.cfm.web.webservice.sft.SftRecvCallBack;

/**
 * 把回盘文件写到服务器指定地点
 *
 * @author GJF
 * @date 2018年9月18日
 */
public class RecvDiskBackingService {
	private static Logger logger = LoggerFactory.getLogger(ChannelSettingService.class);

	/**
	 * 盘片写入指定路径
	 * 
	 * @param ufs
	 * @throws IOException
	 */
	public void writeFileToDirection(UploadFileScaffold ufs) throws IOException {
		DiskUpLoadSection diskUpLoadSection = DiskUpLoadSection.getInstance();
		String path = diskUpLoadSection.getPath();
		byte[] content = ufs.getContent();
		String filename = ufs.getFilename();
		File f = new File(path + filename);
		logger.info("=======回盘文件上传路径=====" + path + filename);
		if (!f.exists()) {
			boolean flag = f.createNewFile();
			logger.info("========创建新文件结果=======" + flag);
		}
		OutputStream out = null;
		InputStream is = null;
		try {
			out = new FileOutputStream(f);
			is = new ByteArrayInputStream(content);
			byte[] buff = new byte[1024];
			int len = 0;
			while ((len = is.read(buff)) != -1) {
				out.write(buff, 0, len);
			}
		} catch (Exception e) {
			logger.error("========文件写入服务器错误");
		} finally {
			if (is != null) {
				is.close();
			}
			if (out != null) {
				out.close();
			}
		}
	}

	/**
	 * @获取回盘列表
	 * @param pageNum
	 * @param pageSize
	 * @param record
	 * @param curUodp
	 * @return
	 * @throws ReqDataException
	 */
	public Page<Record> list(int pageNum, int pageSize, Record record, UodpInfo uodpInfo) throws ReqDataException {
		Long org_id = uodpInfo.getOrg_id();
		logger.info("=====当前登录人org_id===" + org_id);
		Record findById = Db.findById("organization", "org_id", org_id);
		if (null == findById) {
			throw new ReqDataException("当前登录人的机构信息未维护");
		}
		List<String> codes = new ArrayList<>();
		if(findById.getInt("level_num") == 1){
			logger.info("========目前登录机构为总公司");
			codes = Arrays.asList("0102","0101","0201","0202","0203","0204","0205","0500");
		}else{
			logger.info("========目前登录机构为分公司公司");
			List<Record> rec = Db.find(Db.getSql("org.getCurrentUserOrgs"), org_id);
			for (Record o : rec) {
				codes.add(o.getStr("code"));
			}
		}
		record.set("codes", codes);
		List<Integer> status = record.get("status");
		if (status == null || status.size() == 0) {
			record.set("status", new int[] { WebConstant.SftCheckBatchStatus.FSWHP.getKey(),
					WebConstant.SftCheckBatchStatus.HPCG.getKey(), WebConstant.SftCheckBatchStatus.HPYC.getKey(),
					WebConstant.SftCheckBatchStatus.YHT.getKey() });
		}
		SqlPara sqlPara = Db.getSqlPara("recv_disk_backing.findDiskBackingList", Kv.by("map", record.getColumns()));
		return Db.paginate(pageNum, pageSize, sqlPara);
	}

	/**
	 * 
	 * @param user_id
	 * @param ufs
	 * @param result
	 * @param pay_master_id2 
	 * @param channel_id2 
	 * @param pay_id2 
	 * @throws IOException
	 * @throws ReqDataException
	 */
	@SuppressWarnings("unused")
	public Map<String, Object> validateAndWriteToUrl(String user_id, final UploadFileScaffold ufs,
			Map<String, Object> result, String recv_master_id, final String recv_id, String channel_id) throws IOException, ReqDataException {

		final Record user_record = Db.findById("user_info", "usr_id", user_id);
		if (null == user_record) {
			throw new ReqDataException("当前登录人未在用户信息表内配置");
		}

		// 判断此条数据的出盘模板属于哪个通道,判断回盘文件的文件格式
		Record recv_batch_total_master = Db.findById("recv_batch_total_master", "id", recv_master_id);
		Record recv_batch_total = Db.findById("recv_batch_total", "id", recv_id);
		final String child_batchno = recv_batch_total.getStr("child_batchno");
		final Record channel_setting = Db.findById("channel_setting", "id", channel_id);
		Integer detail_id = channel_setting.getInt("document_moudle");
		Record configs_tail = Db.findById("document_detail_config", "id", detail_id);
    	int document_moudle = Integer.valueOf(configs_tail.getStr("document_moudle")) ;
		final Integer source_sys = recv_batch_total_master.getInt("source_sys");

		String filename = ufs.getFilename();
		byte[] content = ufs.getContent();
		logger.info("===========当前文件名====" + filename);
		// 判断文件是否符合规则
		boolean flag = true;
		if (document_moudle == WebConstant.Channel.JP.getKey() || document_moudle == WebConstant.Channel.TP.getKey()) {
			// 建行 和 通联 文件后缀 .txt
			flag = filename.toLowerCase().endsWith(".txt");
		} else {
			// 广银联批量 广银联信用卡 融汇通 .rnt
			flag = filename.toLowerCase().endsWith(".rnt");
		}
		if (!flag) {
			logger.error("=======文件名后缀与渠道要求不匹配");
			result.put("success", false);
			result.put("error_code", "FileTypeError");
			result.put("error_message", "文件类型与此渠道要求不匹配");
			return result;
		}

		// 首付属性 0收 1付
		Integer pay_attr = channel_setting.getInt("pay_attr");
		int document_type = pay_attr == 0 ? WebConstant.DocumentType.SH.getKey() : WebConstant.DocumentType.FH.getKey();
		// 读取回盘的汇总(可能没有)/详情配置(必须有)信息
		List<Record> configs = Db.find(Db.getSql("disk_downloading.findTotalConfig"), document_type, document_moudle);
		//Record configs_tail = Db.findById("document_detail_config", "id", detail_id);

		// 默认汇总信息总金额在第0列 ,
		int total_amount = 0;
		int total_num = 0;
		// 默认详情信息 总金额在第0列 序列号在第0列 , 响应码在第0列 , 响应码在第0列
		int amount = 0;
		int package_seq = 0;
		int response_code = 0;
		int response_message = 0;
		
		if (configs_tail == null) {
			logger.error("=======此渠道回盘配置信息未进行初始化=========" + document_moudle);
			result.put("success", false);
			result.put("error_code", "ChannelConfigError");
			result.put("error_message", "此渠道回盘配置错误");
		}
		// 存在汇总信息
		if (configs != null && configs.size() == 1) {
			for (int i = 1; i <= configs.get(0).getColumns().size(); i++) {
				if ("total_amount".equals(configs.get(0).getStr("field_" + i))) {
					total_amount = i;
				}
				if ("total_num".equals(configs.get(0).getStr("field_" + i))) {
					total_num = i;
				}
			}
		}

		for (int i = 0; i < configs_tail.getColumns().size(); i++) {
			if ("amount".equals(configs_tail.getStr("field_" + i))) {
				amount = i;
			}
			if ("package_seq".equals(configs_tail.getStr("field_" + i))) {
				package_seq = i;
			}
			if ("response_code".equals(configs_tail.getStr("field_" + i))) {
				response_code = i;
			}
			if ("response_message".equals(configs_tail.getStr("field_" + i))) {
				response_message = i;
			}
		}

		// 开始校验总列数,总行数等信息
		InputStreamReader input = new InputStreamReader(new ByteArrayInputStream(content));// 考虑到编码格式
		BufferedReader bufferedReader = new BufferedReader(input);
		// 子批次表中存储的报盘总行数
		final int sendnum = recv_batch_total.getInt("total_num");
		// 子批次表中存储的报盘总金额
		BigDecimal sendamount = recv_batch_total.getBigDecimal("total_amount");
		// 回盘文件总行数, 
		int backnum = 0;
		// 回盘文件子批次行数
		int sonnum = 0;
		// 回盘文件总金额
		BigDecimal backamount = new BigDecimal(0);
		// 回盘中失败数量
		int backnum_faild = 0;
		// 回盘中失败金额
		BigDecimal backamount_faild = new BigDecimal(0);
		// 回盘中成功数量
		int backnum_success = 0;
		// 回盘中成功金额
		BigDecimal backamount_success = new BigDecimal(0);

		String linefile = "";
		final List<Record> updateDetailRecords = new ArrayList<>();
		try {
			while ((linefile = bufferedReader.readLine()) != null) {
				Record updateDetailRecord = new Record();
				if (++backnum == 1) {
					if (configs != null && configs.size() == 1) {
						// 开始校验汇总信息.列数 . 总金额 . 总笔数
						// 只有建行批量不存在汇总信息.
						if (linefile.split(",").length != 6) {
							// 汇总信息列数不正确
							logger.error("===========首行汇总信息列数不对");
							result.put("success", false);
							result.put("error_code", "FileFormatError");
							result.put("error_message", "文件格式错误");
							return result;
						} else {							
							// 汇总信息列数正确 ..只有建行单位是元
							Integer file_total_num = TypeUtils.castToInt(linefile.split(",")[total_num - 1]);
							BigDecimal file_total_amount = new BigDecimal(linefile.split(",")[total_amount - 1]).divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP);
							if (file_total_num != sendnum || file_total_amount.compareTo(sendamount) != 0) {
								logger.error("=============回盘文件首行汇总金额或者笔数与表中报盘记录不一致");
								result.put("success", false);
								result.put("error_code", "FileDataError");
								result.put("error_message", "文件中首行汇总数据错误");
								return result;
							}
							continue;
						}
					} else {
						sonnum ++ ;
						// 建行直接开始校验详情列数
						if (linefile.split("\\|").length != 12) {
							// 汇总信息列数不正确
							logger.error("===========建行详情列数不对");
							result.put("success", false);
							result.put("error_code", "FileFormatError");
							result.put("error_message", "文件格式错误");
							return result;
						}
						backamount = backamount.add(new BigDecimal(linefile.split("\\|")[amount - 1]));
						updateDetailRecord.set("child_batchno", recv_batch_total.getStr("child_batchno"));
						updateDetailRecord.set("package_seq", linefile.split("\\|")[package_seq - 1]);
						if (linefile.split("\\|")[response_code - 1].contains("0000")
								|| linefile.split("\\|")[response_code - 1].contains("成功")) {
							// 0000 S0000 F0000 成功 都表示交易成功
							updateDetailRecord.set("status", 1);
							backnum_success++;
							backamount_success = backamount_success.add(new BigDecimal(linefile.split("\\|")[amount - 1]));
						} else {
							updateDetailRecord.set("status", 2);
							backnum_faild++;
							backamount_faild = backamount_faild.add(new BigDecimal(linefile.split("\\|")[amount - 1]));
						}
						updateDetailRecord.set("bank_err_code", linefile.split("\\|")[response_code - 1]);
						updateDetailRecord.set("bank_err_msg", linefile.split("\\|")[response_message - 1]);
						updateDetailRecords.add(updateDetailRecord);
						continue;
					}
				}
				sonnum ++ ;
				// 非第一行
				if (document_moudle == WebConstant.Channel.JP.getKey()) {
					if (linefile.split("\\|")[response_code - 1].contains("0000")
							|| linefile.split("\\|")[response_code - 1].contains("成功")) {
						// 0000 S0000 F0000 成功 都表示交易成功
						updateDetailRecord.set("status", 1);
						backnum_success++;
						backamount_success = backamount_success.add(new BigDecimal(linefile.split("\\|")[amount - 1]));
					} else {
						updateDetailRecord.set("status", 2);
						backnum_faild++;
						backamount_faild = backamount_faild.add(new BigDecimal(linefile.split("\\|")[amount - 1]));
					}
					updateDetailRecord.set("bank_err_code", linefile.split("\\|")[response_code - 1]);
					updateDetailRecord.set("bank_err_msg", linefile.split("\\|")[response_message - 1]);
					updateDetailRecord.set("child_batchno", recv_batch_total.getStr("child_batchno"));
					updateDetailRecord.set("package_seq", linefile.split("\\|")[package_seq - 1]);
					// 建行不校验了
					backamount = backamount.add(new BigDecimal(linefile.split("\\|")[amount - 1]));
					updateDetailRecords.add(updateDetailRecord);
					continue;
				} else if (document_moudle == WebConstant.Channel.RP.getKey()) {
					// 融汇通详情列数 24
					if (linefile.split(",").length != 24) {
						// 汇总信息列数不正确
						logger.error("===========融汇通详情列数不对,行数为===" + backnum);
						result.put("success", false);
						result.put("error_code", "FileFormatError");
						result.put("error_message", "文件格式错误");
						return result;
					}
				} else {
					// 广银联批量 ,广银联信用卡 , 通联批量 详情21列
					if (linefile.split(",").length != 21) {
						// 汇总信息列数不正确
						logger.error("===========详情列数不对,行数为===" + backnum);
						result.put("success", false);
						result.put("error_code", "FileFormatError");
						result.put("error_message", "文件格式错误");
						return result;
					}
				}
				if (linefile.split(",")[response_code - 1].contains("0000")
						|| linefile.split(",")[response_code - 1].contains("成功")) {
					// 0000, S0000, F0000 ,成功 ,都表示交易成功
					updateDetailRecord.set("status", 1);
					backnum_success++;
					backamount_success = backamount_success.add(new BigDecimal(linefile.split(",")[amount - 1])
							.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
				} else {
					updateDetailRecord.set("status", 2);
					backnum_faild++;
					backamount_faild = backamount_faild.add(new BigDecimal(linefile.split(",")[amount - 1])
							.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
				}
				updateDetailRecord.set("bank_err_code", linefile.split(",")[response_code - 1]);
				updateDetailRecord.set("bank_err_msg", linefile.split(",")[response_message - 1]);
				updateDetailRecord.set("child_batchno", recv_batch_total.getStr("child_batchno"));
				updateDetailRecord.set("package_seq", linefile.split(",")[package_seq - 1]);
				backamount = backamount.add(new BigDecimal(linefile.split(",")[amount - 1])
						.divide(new BigDecimal(100), 2, BigDecimal.ROUND_HALF_UP));
				updateDetailRecords.add(updateDetailRecord);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			input.close();
			bufferedReader.close();
		}
		final int final_backnum_faild = backnum_faild;
		final BigDecimal final_backamount_faild = backamount_faild;
		final int final_backnum_success = backnum_success;
		final BigDecimal final_backamount_success = backamount_success;
		logger.info("===失败个数===" + final_backnum_faild + "===失败金额===" + final_backamount_faild + "===成功金额==="
				+ final_backamount_success + "===成功个数===" + final_backnum_success);
		logger.info("======总行数==" + backnum + "===总金额===" + backamount+ "===明细总行数===" + sonnum);
		final int compareStatus = this.compareStatus(sonnum, backamount, sendnum, sendamount);
		if (WebConstant.SftCheckBatchStatus.HPYC.getKey() == compareStatus) {
			logger.error("========回盘文件中详情累加总金额或者总笔数与报盘不一致");
			result.put("success", false);
			result.put("error_code", "FileDetailDataError");
			result.put("error_message", "文件详情数据错误");
			return result;
		}
		// 开始更新表状态
		boolean updateflag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				boolean update = CommonService.update("recv_batch_total",
						new Record().set("service_status", compareStatus).set("back_on", new Date())
								.set("back_user_name", user_record.get("name")).set("fail_num", final_backnum_faild)
								.set("fail_amount", final_backamount_faild).set("success_num", final_backnum_success)
								.set("success_amount", final_backamount_success),
						new Record().set("id", recv_id));
				logger.info("=====更新recv_batch_total结果===" + update);
				if (update) {
					int[] batchUpdate = Db.batchUpdate("recv_batch_detail", "package_seq,child_batchno",updateDetailRecords, 1000);
					boolean checkDbResult = ArrayUtil.checkDbResult(batchUpdate);
					logger.info("=====更新pay_batch_detail结果===" + checkDbResult);
					if (checkDbResult) {
						// 开始更新原始数据状态
						Boolean origin_flag = false;
						if (source_sys == 0) {
							origin_flag = (Db.update(Db.getSql("recv_disk_backing.updateLaOriginData"),child_batchno) == sendnum);
						} 
						logger.info("=====更新原始数据表结果===" + origin_flag);
						if (origin_flag) {
							// 写到服务器上
							try {
								writeFileToDirection(ufs);
								return true ;
							} catch (IOException e) {
								e.printStackTrace();
								logger.error("========回盘文件写入服务器错误");
								return false;
							}
						}
					}
				}
				return false;
			}
		});
		if (!updateflag) {
			logger.error("========详情表或者原始数据表 更新错误");
			result.put("success", false);
			result.put("error_code", "DataBaseError");
			result.put("error_message", "数据库更新操作错误");
		} else {
			logger.info("========回盘文件解析成功");
			result.put("success", true);
			result.put("error_code", "success");
			List<Record> originRecord = new ArrayList<>();
			try {
				if(source_sys == 0) {
					logger.info("======回调LA");
					originRecord = Db.find(Db.getSql("recv_disk_backing.selectLaOriginData"), child_batchno);
				}
				SftRecvCallBack callback = new SftRecvCallBack();
				callback.callback(source_sys, originRecord);				
			}catch(Exception e){
				e.printStackTrace();
				logger.error("===============回调失败");
			}
		}
		return result;
	}

	/**
	 * 文件读取完毕. 开始更新表状态
	 * 
	 * @param backnum
	 * @param backamount
	 * @param sendnum
	 * @param sendamount
	 */
	private int compareStatus(int sonnum, BigDecimal backamount, int sendnum, BigDecimal sendamount) {
		int total_status = WebConstant.SftCheckBatchStatus.HPCG.getKey();
		if (sonnum != sendnum || backamount.compareTo(sendamount) != 0) {
			// 出盘个数 和 回盘个数
			total_status = WebConstant.SftCheckBatchStatus.HPYC.getKey();
		}
		return total_status;
	}

}
