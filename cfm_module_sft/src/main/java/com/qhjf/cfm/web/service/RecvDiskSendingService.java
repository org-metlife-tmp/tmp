package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.config.DDHLARecvConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.impl.SysProtocolImportInter;
import com.qhjf.cfm.web.inter.impl.batch.SysBatchRecvInter;
import com.qhjf.cfm.web.webservice.tool.OminiUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;


public class RecvDiskSendingService {
	
    private static final Logger logger = LoggerFactory.getLogger(RecvDiskSendingService.class);

	private static DDHLARecvConfigSection section = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLaRecv);

    /**
     * @盘片发送列表
     * @param record
     * @param uodpInfo 
     * @return 
     * @throws ReqDataException 
     */
	public Page<Record> list(int pageNum , int pageSize ,  Record record, UodpInfo uodpInfo) throws ReqDataException {
		Long org_id = uodpInfo.getOrg_id();
    	logger.info("=====当前登录人org_id==="+org_id);  
    	Record findById = Db.findById("organization", "org_id", org_id);
		if(null == findById){
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
    	if(status == null || status.size() == 0){
    		record.remove("status");
    	}
		SqlPara sqlPara = Db.getSqlPara("recv_disk_downloading.findDiskSendingList", Kv.by("map", record.getColumns()));	
		return Db.paginate(pageNum, pageSize, sqlPara);	
			
	}


	/**
	 * 发送银行
	 * @param record
	 */
	public void sendbank(Record record, final UserInfo userInfo) throws ReqDataException {
		//1. 根据前端传入 子批次id查询子批次
		final Integer persistVersion = TypeUtils.castToInt(record.get("persist_version"));
		final long id = TypeUtils.castToLong(record.get("id"));
		//防止重复发送
		final Record[] total = new Record[1];
		final String errMst[] = new String[1];
		boolean tx = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				total[0] = Db.findFirst("select * from recv_batch_total where id=? and persist_version=?", id, persistVersion);
				if (total[0] == null) {
					errMst[0] = "未找到相应的子批次！";
					return false;
				}
				
				int update = Db.update("update recv_batch_total set persist_version = ? where id=? and persist_version=?", (persistVersion+1), id, persistVersion);
				if (update != 1) {
					errMst[0] = "升级相应的子批次版本号失败！";
					return false;
				}
				
				Record t = total[0];
				t.set("persist_version", persistVersion + 1);
				return true;
			}
		});
		if (!tx) {
			throw new ReqDataException(errMst[0]);
		}
		
		final Record cbRecord = total[0];
		
		//2. 根据子批次记录中的主批次号查询 主批次
		Record mbRecord = Db.findFirst(Db.getSql("recv_disk_downloading.findMasterByBatchNo"),
				TypeUtils.castToString(cbRecord.get("master_batchno")));
		if(mbRecord == null){
			throw new ReqDataException("未找到相应的主批次！");
		}
		if (mbRecord.getInt("source_sys") == WebConstant.SftOsSource.EBS.getKey()) {
			throw new ReqDataException("批量收款暂时不持支直连发送银行！");
		}
		//3. 根据子批次id查询pay_batch_detail所有的明细
		final List<Record> detailRecords = Db.find(Db.getSql("recv_disk_downloading.findDatailInfo"), id);
		
		Map<String, String> accountAndBankInfo = DiskSendingService.getAccountAndBankInfo(mbRecord.getStr("recv_acc_no"));
		
		List<Record> list = new ArrayList<>();
		for(Record r : detailRecords){
			Record l = new Record();
			//汇总表信息
			l.set("source_ref", "recv_batch_total");
			l.set("bill_id", id);
			l.set("recv_account_no",TypeUtils.castToString(mbRecord.get("recv_acc_no")));
			l.set("recv_account_name",TypeUtils.castToString(mbRecord.get("recv_acc_name")));
			l.set("recv_account_bank",TypeUtils.castToString(mbRecord.get("recv_bank_name")));
			l.set("recv_bank_type",accountAndBankInfo.get("bank_type"));
			l.set("recv_account_cur", accountAndBankInfo.get("iso_code"));
			l.set("recv_bank_cnaps", accountAndBankInfo.get("bank_cnaps_code"));
			l.set("recv_bank_prov", accountAndBankInfo.get("province"));
			l.set("recv_bank_city", accountAndBankInfo.get("city"));
			
			//明细表信息：付方账户没有在资金系统中维护，无法查询到cnaps、cur、prov、city、type，留空
			l.set("id", r.get("id"));
			l.set("package_seq", r.get("package_seq"));
			l.set("amount", r.get("amount"));
			//付方账号做解密处理以便后续发送给银行端
			String pay_acc_no = "";
			try {
				pay_acc_no = SymmetricEncryptUtil.getInstance().decryptToStr(r.getStr("pay_acc_no"));
			} catch (EncryAndDecryException e) {
				logger.error("RecvDiskSendingService.sendbank：付方账号解密失败！", e);
			}
			l.set("pay_account_no", pay_acc_no);
			l.set("pay_account_name", r.getStr("pay_acc_name"));
			l.set("pay_account_bank", r.getStr("pay_bank_name"));
			l.set("pay_account_cur", "");
			l.set("pay_bank_cnaps", "");
			l.set("pay_bank_prov", r.getStr("province"));
			l.set("pay_bank_city", "");
			l.set("pay_bank_type", r.getStr("pay_bank_type"));
			//暂时测试使用，上生产时该段判断代码需要去掉，保留保单号的使用
			String cnaps = accountAndBankInfo.get("bank_cnaps_code").substring(0, 3);
			if("102".equals(cnaps)){
				Record channel = Db.findFirst(Db.getSql("recv_disk_downloading.qryChannelId"),mbRecord.getStr("channel_id"));
				Record pro = Db.findFirst(Db.getSql("recv_disk_downloading.qryProtocolInfoImp"), pay_acc_no);
				if(!OminiUtils.isNullOrEmpty(pro)){
					l.set("insure_bill_no",pro.getStr("insure_bill_no"));
				}else {
					l.set("insure_bill_no", r.getStr("insure_bill_no"));
				}
				//根据卡种判断协议编号的生成
				if(!OminiUtils.isNullOrEmpty(channel)){
					if("1".equals(channel.getStr("card_type")) || "2".equals(channel.getStr("card_type"))){
						BigDecimal amt = new BigDecimal(r.getStr("amount"));
						if(amt.compareTo(BigDecimal.valueOf(5000000)) == -1){
							l.set("ContractNo","BDP300236427");
						} else if(amt.compareTo(BigDecimal.valueOf(5000000)) == 1 && amt.compareTo(BigDecimal.valueOf(10000000)) == 0){
							l.set("ContractNo","BDP300236427");
						}
					} else if("3".equals(channel.getStr("card_type"))){
						l.set("ContractNo","BDP300236427");
					} else {
						l.set("ContractNo","BDP300236427");
					}
				}
			}
			list.add(l);
		}
		Record instrRecord = new Record().set("list", list);

		String shortPayCnaps = accountAndBankInfo.get("bank_cnaps_code").substring(0, 3);
		IChannelInter channelInter = null;
		try {
			channelInter = ChannelManager.getInter(shortPayCnaps, "BatchRecv");
		} catch (Exception e) {
			logger.error("获取银行原子接口失败！", e);
		}
		final SysBatchRecvInter sysInter = new SysBatchRecvInter();
        sysInter.setChannelInter(channelInter);
        final Record instr = sysInter.genInstr(instrRecord);
        if (instr == null) {
        	throw new ReqDataException("生成银行指令失败！");
		}

        final String[] errMsg = new String[1];
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				//保存锁定表
				boolean saveIntr = false;
				try {
					saveIntr = sysInter.saveIntr();
				} catch (ReqDataException e) {
					e.printStackTrace();
					errMsg[0] = e.getMessage();
					return false;
				}
				if(saveIntr){
					//子批次汇总表状态改为：4已发送未回盘
					int updPayBatchTotal = CommonService.updateRows("recv_batch_total"
							, new Record().set("service_status", 4)
										  .set("persist_version", cbRecord.getInt("persist_version") + 1)
										  .set("send_user_name", userInfo.getName())
										  .set("send_on", new Date())
							, new Record().set("id", id).set("persist_version", cbRecord.getInt("persist_version")));
					if (updPayBatchTotal == 1) {
						return true;
					}else {
						logger.error("批收单据发送时，更新子批次汇总表状态为‘已发送未回盘’失败。子批次汇总表id={}", id);
						return false;
					}
				}
				logger.error("批收单据发送时，保存批收指令表失败。子批次汇总表id={}", id);
				return false;
			}
		});

		if (flag) {
			if ("102".equals(shortPayCnaps)) {
				//工行业务逻辑：先查询协议明细表，判断是否已经完全上送了协议：如果是则直接发送收款指令，如果不是先发送上送协议指令
				Record instrTotal = (Record) sysInter.getInstr().get("total");
				if(sendProtocolInstr(shortPayCnaps, detailRecords, mbRecord, instrTotal.getLong("id"), id)){
					/**
					 * 重新拆包操作，因工行不支持zip压缩的方式，故以xml方式发送，而xml最大支持数据为150笔，
					 * 避免工行出现最大数据超限，因此暂拆包为一批次分多次发送，每次发送145笔交易。
					 */
					List<Record> detail = instrRecord.get("list");
					List<Record> oldDetail = instr.get("detail");
					Integer countSize = Integer.valueOf(section.getCountSize());
					if(detail.size() > countSize){
						int k = detail.size() / countSize;
						int a = detail.size() % countSize;
						if(a > 0){
							k = k + 1;
						}
						for (int i = 0; i < k; i++) {
							if (detail.size() > countSize) {
								List<Record> newList = new ArrayList<>();
								newList.addAll((Collection<? extends Record>) detail.subList(0,countSize));
								instrRecord.set("list",newList);
								detail.removeAll(newList);
								final Record newInstr = sysInter.genInstr(instrRecord);
								List<Record> updList = newInstr.get("detail");
								Record newTotal = newInstr.get("total");
								for(int j = 0; j < updList.size();j++){
									CommonService.update("batch_recv_instr_queue_detail", new Record().set("bank_serial_number_unpack", newTotal.getStr("bank_serial_number")),
											new Record().set("detail_bank_service_number", oldDetail.get(i).getStr("detail_bank_service_number")));
								}
								Record oldTotal = instr.get("total");
								newTotal.set("bus_type",oldTotal.getStr("bus_type"));
								QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(newInstr), shortPayCnaps);
								ProductQueue productQueue = new ProductQueue(bean);
								new Thread(productQueue).start();
							} else {
								List<Record> newList = new ArrayList<>();
								newList.addAll((Collection<? extends Record>) detail.subList(0,countSize));
								instrRecord.set("list",newList);
								detail.removeAll(newList);
								final Record newInstr = sysInter.genInstr(instrRecord);
								List<Record> updList = newInstr.get("detail");
								Record newTotal = newInstr.get("total");
								for(int j = 0; j < updList.size();j++){
									CommonService.update("batch_recv_instr_queue_detail", new Record().set("bank_serial_number_unpack", newTotal.getStr("bank_serial_number")),
											new Record().set("detail_bank_service_number", oldDetail.get(i).getStr("detail_bank_service_number")));
								}
								Record oldTotal = instr.get("total");
								newTotal.set("bus_type",oldTotal.getStr("bus_type"));
								QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(newInstr), shortPayCnaps);
								ProductQueue productQueue = new ProductQueue(bean);
								new Thread(productQueue).start();
							}
						}
					} else {
						QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(instr), shortPayCnaps);
						ProductQueue productQueue = new ProductQueue(bean);
						new Thread(productQueue).start();
					}
				}
			} else {
				QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(instr), shortPayCnaps);
				ProductQueue productQueue = new ProductQueue(bean);
				new Thread(productQueue).start();
			}
        } else {
        	if (errMsg[0] != null) {
        		throw new ReqDataException(errMsg[0]);
			}
            throw new ReqDataException("批收单据发送失败，请联系管理员！");
        }
	}
	/**
	 * 查询批收数据的收款协议是否已经导入，如果已经导入返回true，如果没有导入，这发送协议导入指令并返回false
	 * @param cnaps 银行类型
	 * @param instr 批次明细
	 * @param instrTotalId 批收指令汇总表主键
	 * @param batchTotalId 单据批次汇总表主键
	 * @return
	 * @throws ReqDataException
	 */
	private boolean sendProtocolInstr(String cnaps, List<Record> instr,Record mbRecord, long instrTotalId, long batchTotalId) throws ReqDataException{
		IChannelInter channelInter = null;
		try {
			channelInter = ChannelManager.getInter(cnaps, "ProtocolImport");
		} catch (Exception e) {
			logger.error("获取银行原子接口失败！", e);
			throw new ReqDataException("该渠道不支持协议上传！");
		}
		SysProtocolImportInter sysInter = new SysProtocolImportInter();
        sysInter.setChannelInter(channelInter);

		//生成指令后，先判断指令是否已经被人工导入，如果存在未人工导入的收款协议，则通过指令自动导入
        List<Record> newDetails = new ArrayList<>();
        for (Record record : instr) {
			/*Record findFirst = Db.findFirst(Db.getSql("recv_disk_downloading.qryProtocolDetailBeforeImp")
					, total.getStr("protocol_no")
					, record.getStr("pay_acc_no")
					, record.getStr("pay_no"));*/
			//付方账号做解密处理以便后续发送给银行端
			String pay_acc_no = "";
			try {
				pay_acc_no = SymmetricEncryptUtil.getInstance().decryptToStr(record.getStr("pay_acc_no"));
			} catch (EncryAndDecryException e) {
				logger.error("RecvDiskSendingService.sendbank：付方账号解密失败！", e);
			}
			Record findFirst = Db.findFirst(Db.getSql("recv_disk_downloading.qryProtocol")
					, pay_acc_no
					, TypeUtils.castToString(mbRecord.get("recv_acc_no")));
			if (findFirst == null) {
				//可以在这里添加未导入的协议数据
				//sysInter.seveInfo(record, total);
				newDetails.add(record);
			}
		}
        
        if (newDetails.size() > 0) {//存在未手工导入的协议
			Integer protocolSize = Integer.valueOf(section.getProtocolSize());
			boolean seveInstr = false;
			if(newDetails.size() > protocolSize){
				int k = newDetails.size() / protocolSize;
				int a = newDetails.size() % protocolSize;
				if(a > 0){
					k = k + 1;
				}
				for (int i = 0; i < k; i++) {
					if (newDetails.size() > protocolSize) {
						List<Record> newList = new ArrayList<>();
						newList.addAll((Collection<? extends Record>) newDetails.subList(0,protocolSize));
						newDetails.removeAll(newList);
						//生成指令
						Record batchDetailList = new Record()
								.set("batchDetailList", newList)
								.set("instrTotalId", instrTotalId)
								.set("batchTotalId", batchTotalId)
								.set("cnaps", cnaps)
								.set("recv_account_no", TypeUtils.castToString(mbRecord.get("recv_acc_no")))
								.set("recv_account_name", TypeUtils.castToString(mbRecord.get("recv_acc_name")))
								.set("recv_account_bank", TypeUtils.castToString(mbRecord.get("recv_bank_name")));
						Record genInstr = sysInter.genInstr(batchDetailList);
						//保存指令
						seveInstr = sysInter.seveInstr();
						//指令入队
						if (seveInstr) {
							QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(genInstr), cnaps);
							ProductQueue productQueue = new ProductQueue(bean);
							new Thread(productQueue).start();
						}
					} else {
						List<Record> newList = new ArrayList<>();
						newList.addAll((Collection<? extends Record>) newDetails.subList(0,protocolSize));
						newDetails.removeAll(newList);
						//生成指令
						Record batchDetailList = new Record()
								.set("batchDetailList", newList)
								.set("instrTotalId", instrTotalId)
								.set("batchTotalId", batchTotalId)
								.set("cnaps", cnaps)
								.set("recv_account_no", TypeUtils.castToString(mbRecord.get("recv_acc_no")))
								.set("recv_account_name", TypeUtils.castToString(mbRecord.get("recv_acc_name")))
								.set("recv_account_bank", TypeUtils.castToString(mbRecord.get("recv_bank_name")));
						Record genInstr = sysInter.genInstr(batchDetailList);
						//保存指令
						seveInstr = sysInter.seveInstr();
						//指令入队
						if (seveInstr) {
							QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(genInstr), cnaps);
							ProductQueue productQueue = new ProductQueue(bean);
							new Thread(productQueue).start();
						}
					}
				}
			} else {
				//生成指令
				Record batchDetailList = new Record()
						.set("batchDetailList", newDetails)
						.set("instrTotalId", instrTotalId)
						.set("batchTotalId", batchTotalId)
						.set("cnaps", cnaps)
						.set("recv_account_no", TypeUtils.castToString(mbRecord.get("recv_acc_no")))
						.set("recv_account_name", TypeUtils.castToString(mbRecord.get("recv_acc_name")))
						.set("recv_account_bank", TypeUtils.castToString(mbRecord.get("recv_bank_name")));
				Record genInstr = sysInter.genInstr(batchDetailList);
				//保存指令
				seveInstr = sysInter.seveInstr();
				//指令入队
				if (seveInstr) {
					QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(genInstr), cnaps);
					ProductQueue productQueue = new ProductQueue(bean);
					new Thread(productQueue).start();
				}
			}
            return seveInstr;
		}else {//不存在未手工导入的协议
			return true;
		}
	}

}
