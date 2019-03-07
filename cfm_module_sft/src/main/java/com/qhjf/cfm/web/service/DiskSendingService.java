package com.qhjf.cfm.web.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.impl.batch.SysBatchPayInter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;



public class DiskSendingService {
	
    private static final Logger logger = LoggerFactory.getLogger(DiskSendingService.class);

    
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
		SqlPara sqlPara = Db.getSqlPara("disk_downloading.findDiskSendingList", Kv.by("map", record.getColumns()));	
		return Db.paginate(pageNum, pageSize, sqlPara);	
			
	}

	public void sendbank(Record record, final UserInfo userInfo) throws ReqDataException {
		//1. 根据前端传入 子批次id查询子批次
		final long id = TypeUtils.castToLong(record.get("id"));
		final Record cbRecord = Db.findById("pay_batch_total", "id", id);
		if(cbRecord == null){
			throw new ReqDataException("未找到相应的子批次！");
		}
		//1.1 校验发送状态是否合法
		Integer serviceStatus = cbRecord.getInt("service_status");
		if (null == serviceStatus){
			throw new ReqDataException("子批次状态为空！");
		} 
		if (serviceStatus != WebConstant.SftCheckBatchStatus.SPWFS.getKey()
				&& serviceStatus != WebConstant.SftCheckBatchStatus.YHT.getKey()) {
			throw new ReqDataException("子批次状态非法！");
		}
		
		//2. 根据子批次记录中的主批次号查询 主批次
		Record mbRecord = Db.findFirst(Db.getSql("disk_downloading.findMasterByBatchNo"),
				TypeUtils.castToString(cbRecord.get("master_batchno")));
		if(mbRecord == null){
			throw new ReqDataException("未找到相应的主批次！");
		}
		//2.1 校验当前付款账户余额是否足够
		Record accNoBalance = Db.findFirst(Db.getSql("disk_downloading.selAccNoBalence"), mbRecord.getStr("pay_acc_no"));
		if (null == accNoBalance) {
			throw new ReqDataException(String.format("付款账号[%s]当日余额无数据！", mbRecord.getStr("pay_acc_no")));
		}
		BigDecimal totalAmount = mbRecord.getBigDecimal("total_amount");
		BigDecimal availableBal = accNoBalance.getBigDecimal("available_bal");
		if (totalAmount.compareTo(availableBal) > 0) {
			throw new ReqDataException(String.format("付款账号[%s]当日余额[%s]不足！", mbRecord.getStr("pay_acc_no"), availableBal));
		}
		
		//3. 根据子批次id查询pay_batch_detail所有的明细
		final List<Record> detailRecords = Db.find(Db.getSql("disk_downloading.findDatailInfo"), id);
		
		Map<String, String> accountAndBankInfo = getAccountAndBankInfo(mbRecord.getStr("pay_acc_no"));
		
		List<Record> list = new ArrayList<>();
		for(Record r : detailRecords){
			Record l = new Record();
			//汇总表信息
			l.set("source_ref", "pay_batch_total");
			l.set("bill_id", id);
			l.set("pay_account_no",TypeUtils.castToString(mbRecord.get("pay_acc_no")));
			l.set("pay_account_name",TypeUtils.castToString(mbRecord.get("pay_acc_name")));
			l.set("pay_account_bank",TypeUtils.castToString(mbRecord.get("pay_bank_name")));
			l.set("pay_account_cur", accountAndBankInfo.get("iso_code"));
			l.set("pay_bank_cnaps", accountAndBankInfo.get("bank_cnaps_code"));
			l.set("pay_bank_prov", accountAndBankInfo.get("province"));
			l.set("pay_bank_city", accountAndBankInfo.get("city"));
			l.set("pay_bank_type", accountAndBankInfo.get("bank_type"));
			
			//明细表信息
			Map<String, String> detailAccountAndBankInfo = getRecvAccountAndBankInfo(r.getStr("recv_acc_no"));
			l.set("id", r.get("id"));
			l.set("package_seq", r.get("package_seq"));
			l.set("amount", r.get("amount"));
			l.set("recv_account_no", r.getStr("recv_acc_no"));
			l.set("recv_account_name", r.getStr("recv_acc_name"));
			l.set("recv_account_cur", detailAccountAndBankInfo.get("iso_code"));
			l.set("recv_account_bank", r.getStr("recv_bank_name"));
			l.set("recv_bank_cnaps", detailAccountAndBankInfo.get("bank_cnaps_code"));
			l.set("recv_bank_prov", detailAccountAndBankInfo.get("province"));
			l.set("recv_bank_city", detailAccountAndBankInfo.get("city"));
			l.set("recv_bank_type", detailAccountAndBankInfo.get("bank_type"));
			
			list.add(l);
		}
		
		Record instrRecord = new Record().set("list", list);
		
		String shortPayCnaps = accountAndBankInfo.get("bank_cnaps_code").substring(0, 3);
		IChannelInter channelInter = null;
		try {
			channelInter = ChannelManager.getInter(shortPayCnaps, "BatchPay");
		} catch (Exception e) {
			logger.error("获取银行原子接口失败！", e);
		}
		final SysBatchPayInter sysInter = new SysBatchPayInter();
        sysInter.setChannelInter(channelInter);
        final Record instr = sysInter.genInstr(instrRecord);
        if (instr == null) {
        	throw new ReqDataException("锁定批次单据失败！");
		}
		
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				//保存锁定表
				boolean saveIntr = sysInter.saveIntr();
				if(saveIntr){
					//子批次汇总表状态改为：4已发送未回盘
					int updPayBatchTotal = CommonService.updateRows("pay_batch_total"
							, new Record().set("service_status", 4)
										  .set("persist_version", cbRecord.getInt("persist_version") + 1)
										  .set("send_user_name", userInfo.getName())
										  .set("send_on", new Date())
							, new Record().set("id", id).set("persist_version", cbRecord.getInt("persist_version")));
					if (updPayBatchTotal == 1) {
						return true;
					}else {
						logger.error("批付单据发送时，更新子批次汇总表状态为‘已发送未回盘’失败。子批次汇总表id={}", id);
						return false;
					}
				}
				logger.error("批付单据发送时，保存批收指令表失败。子批次汇总表id={}", id);
				return false;
			}
		});

		if (flag) {
            QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(instr), shortPayCnaps);
            ProductQueue productQueue = new ProductQueue(bean);
            new Thread(productQueue).start();
        } else {
            throw new ReqDataException("发送失败，请联系管理员！");
        }

	}

	/**
	 * 通过银行账号，获取付方账号的cnaps、币种、开卡省份、开卡市、开卡银行类型
	 * @param accNo	银行账号
	 * @return
	 * @throws ReqDataException
	 */
	public static Map<String, String> getAccountAndBankInfo(String accNo) throws ReqDataException {
		// 1. 通过pay_acc_no查询 cnaps号
		Map<String, Object> account = TableDataCacheUtil.getInstance().getARowData("account", "acc_no", accNo);
		if (account == null) {
			throw new ReqDataException(String.format("未查到直连账户[%s]的信息", accNo));
		}
		String bankCnapsCode = account.get("bank_cnaps_code").toString();
		// 2. 通过curr_id查询付款账户币种
		Map<String, Object> currency = TableDataCacheUtil.getInstance().getARowData("currency", "id",
				TypeUtils.castToString(account.get("curr_id")));
		String isoCode = currency == null ? "CNY" : TypeUtils.castToString(currency.get("iso_code"));
		// 3. 通过cnaps号查询银行信息
		Map<String, Object> allBankInfo = TableDataCacheUtil.getInstance().getARowData("all_bank_info", "cnaps_code",
				bankCnapsCode);
		if (allBankInfo == null) {
			throw new ReqDataException(String.format("通过CNAPS号=[%s]未查到银行信息信息", bankCnapsCode));
		}
		String province = TypeUtils.castToString(allBankInfo.get("province"));
		String city = TypeUtils.castToString(allBankInfo.get("city"));
		String bankType = TypeUtils.castToString(allBankInfo.get("bank_type"));
		
		Map<String, String> result = new HashMap<>();
		result.put("bank_cnaps_code", bankCnapsCode);
		result.put("iso_code", isoCode);
		result.put("province", province);
		result.put("city", city);
		result.put("bank_type", bankType);
		return result;
	}
	
	/**
	 * 通过银行账号，获取收方账号的cnaps、币种、开卡省份、开卡市、开卡银行类型
	 * @param accNo	银行账号
	 * @return
	 * @throws ReqDataException
	 */
	private Map<String, String> getRecvAccountAndBankInfo(String accNo) {
		Map<String, String> result = new HashMap<>();
		// 1. 通过pay_acc_no查询 cnaps号
		Map<String, Object> supplier = TableDataCacheUtil.getInstance().getARowData("supplier_acc_info", "acc_no", accNo);
		if (supplier == null) {
			return result;
		}
		String bankCnapsCode = supplier.get("cnaps_code").toString();
		result.put("bank_cnaps_code", bankCnapsCode);
		// 2. 通过curr_id查询付款账户币种
		Map<String, Object> currency = TableDataCacheUtil.getInstance().getARowData("currency", "id",
				TypeUtils.castToString(supplier.get("curr_id")));
		String isoCode = currency == null ? "CNY" : TypeUtils.castToString(currency.get("iso_code"));
		result.put("iso_code", isoCode);
		// 3. 通过cnaps号查询银行信息
		Map<String, Object> allBankInfo = TableDataCacheUtil.getInstance().getARowData("all_bank_info", "cnaps_code",
				bankCnapsCode);
		if (allBankInfo == null) {
			return result;
		}
		String province = TypeUtils.castToString(allBankInfo.get("province"));
		String city = TypeUtils.castToString(allBankInfo.get("city"));
		String bankType = TypeUtils.castToString(allBankInfo.get("bank_type"));
		result.put("province", province);
		result.put("city", city);
		result.put("bank_type", bankType);
		return result;
	}
}
