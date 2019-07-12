package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.impl.SysProtocolImportInter;
import com.qhjf.cfm.web.inter.impl.batch.SysBatchRecvInter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;



public class RecvDiskSendingService {
	
    private static final Logger logger = LoggerFactory.getLogger(RecvDiskSendingService.class);

    
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
			l.set("pay_account_no", r.getStr("pay_acc_no"));
			l.set("pay_account_name", r.getStr("pay_acc_name"));
			l.set("pay_account_bank", r.getStr("pay_bank_name"));
			l.set("pay_account_cur", "");
			l.set("pay_bank_cnaps", "");
			l.set("pay_bank_prov", "");
			l.set("pay_bank_city", "");
			l.set("pay_bank_type", "");
			l.set("insure_bill_no",r.getStr("insure_bill_no"));
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
				if(sendProtocolInstr(shortPayCnaps, detailRecords, instrTotal.getLong("id"), id)){
					QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(instr), shortPayCnaps);
					ProductQueue productQueue = new ProductQueue(bean);
					new Thread(productQueue).start();
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
	private boolean sendProtocolInstr(String cnaps, List<Record> instr, long instrTotalId, long batchTotalId) throws ReqDataException{
		IChannelInter channelInter = null;
		try {
			channelInter = ChannelManager.getInter(cnaps, "ProtocolImport");
		} catch (Exception e) {
			logger.error("获取银行原子接口失败！", e);
			throw new ReqDataException("该渠道不支持协议上传！");
		}
		SysProtocolImportInter sysInter = new SysProtocolImportInter();
        sysInter.setChannelInter(channelInter);
        //生成指令
        Record batchDetailList = new Record()
        		.set("batchDetailList", instr)
        		.set("instrTotalId", instrTotalId)
        		.set("batchTotalId", batchTotalId)
        		.set("cnaps", cnaps);
        Record genInstr = sysInter.genInstr(batchDetailList);
        
        //生成指令后，先判断指令是否已经被人工导入，如果存在未人工导入的收款协议，则通过指令自动导入
        Record total = (Record) genInstr.get("total");
        @SuppressWarnings("unchecked")
		List<Record> details = (List<Record>)genInstr.get("detail");
        List<Record> newDetails = new ArrayList<>();
        for (Record record : details) {
			Record findFirst = Db.findFirst(Db.getSql("recv_disk_downloading.qryProtocolDetailBeforeImp")
					, total.getStr("protocol_no")
					, record.getStr("pay_acc_no")
					, record.getStr("pay_no"));
			if (findFirst == null) {
				newDetails.add(record);
			}
		}
        
        if (newDetails.size() > 0) {//存在未手工导入的协议
        	genInstr.set("detail", newDetails);
        	//保存指令
            boolean seveInstr = sysInter.seveInstr();
            //指令入队
            if (seveInstr) {
            	QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(genInstr), cnaps);
    			ProductQueue productQueue = new ProductQueue(bean);
    			new Thread(productQueue).start();
    		}
            return false;
		}else {//不存在未手工导入的协议
			return true;
		}
	}
}
