package com.qhjf.cfm.web.service;


import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;


/**
 * 结算对账 pos明细与流水
 */
public class RecvCounterPosTransCheckService {
	
    private static Logger log = LoggerFactory.getLogger(RecvCounterPosTransCheckService.class);

    /**
     * 单据列表
     * @param pageSize
     * @param pageNum
     * @param record
     * @param userInfo
     * @param curUodp
     * @return
     */
	public Page<Record> list(int pageSize, int pageNum, Record record, UserInfo userInfo, UodpInfo curUodp) {
		SqlPara sqlPara = Db.getSqlPara("recv_counter_pos_import.recvcounterPoslist", Kv.by("map", record.getColumns()));
		Page<Record> paginate = Db.paginate(pageNum, pageSize, sqlPara);
		return paginate;
	}

	/**
	 * 查找交易流水
	 * @param record
	 * @return
	 */
	public List<Record> tradingList(final Record record){
		SqlPara sqlPara = Db.getSqlPara("paycheck.tradingList", Kv.by("map", record.getColumns()));
		return Db.find(sqlPara);
	}

	/**
	 * 确认
	 * @param record
	 * @param userInfo
	 * @return
	 * @throws BusinessException
	 */
	public void confirm(final Record record, final UserInfo userInfo) throws BusinessException {
		//交易id
		final ArrayList<Integer> tradingNo = record.get("trading_no");
		//单据号
		final ArrayList<Integer> batchNo = record.get("batchid");
		final ArrayList<Integer> persistVersion = record.get("persist_version");

		//获取勾选的交易和批次
		final List<Record> batchList = Db.find(Db.getSqlPara("recv_counter_pos_trans_check.findBillList", Kv.by("batchNo", batchNo)));
		final List<Record> tradList = Db.find(Db.getSqlPara("paycheck.findTradList", Kv.by("tradingNo", tradingNo)));

		if(batchList.size()==0 || tradList.size()==0){
			throw new ReqDataException("请选择要核对的单据和交易！");
		}

		//判断是否已核对过
		HashSet<Integer> checkedSet = new HashSet<>();
		BigDecimal batchAmount = new BigDecimal(0);
		for(Record r : batchList){
			int isChecked = TypeUtils.castToInt(r.get("trade_checked"));
			if(isChecked == 1){
				checkedSet.add(isChecked);
			}
			batchAmount = batchAmount.add(TypeUtils.castToBigDecimal(r.get("trade_amount")));
		}
		if(checkedSet.size() >= 1){
			throw new ReqDataException("已勾选的存在已核对的批次！");
		}
		List<Record> tradSize = Db.find(Db.getSqlPara("paycheck.findTradListBusiness",
				Kv.by("map", new Record().set("is_checked", 0).set("tradingNo", tradingNo).getColumns())));
		if(tradSize.size() != tradingNo.size()){
			throw new ReqDataException("存在已核对的交易再次进行核对！");
		}

		BigDecimal tradAmount = new BigDecimal(0);
		for(Record r : tradList){
			int direction = TypeUtils.castToInt(r.get("direction"));
			//1付 2收
			if(direction == 1){
				tradAmount = tradAmount.subtract(TypeUtils.castToBigDecimal(r.get("amount")));
			}else if(direction == 2){
				tradAmount = tradAmount.add(TypeUtils.castToBigDecimal(r.get("amount")));
			}
		}
		if(batchAmount.compareTo(tradAmount) != 0){
			throw new ReqDataException("核对单据金额和交易金额不相同！");
		}

		final String checkSerialSeqNo = RedisSericalnoGenTool.genCheckSerialSeqNo();//生成十六进制流水号

		final List<Record> records = CommonService.genPayConfirmRecords(batchNo, tradingNo, userInfo, checkSerialSeqNo);

		//进行数据新增操作
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				//更新交易
				String seqnoOrstatmentCode = RedisSericalnoGenTool.genVoucherSeqNo();//生成十六进制序列号/凭证号
				for(Integer trad : tradingNo){
					boolean s = CommonService.update("acc_his_transaction",
							new Record().set("is_checked", 1)
									.set("statement_code", seqnoOrstatmentCode)
									.set("check_service_number", checkSerialSeqNo)
									.set("check_user_id", userInfo.getUsr_id())
									.set("check_user_name", userInfo.getName())
									.set("check_date", new Date()),
							new Record().set("id", trad));
					if(!s){
						return false;
					}
				}
				//更新批次
				for(int num=0; num<batchNo.size(); num++){
					boolean s = CommonService.update("recv_counter_pos",
							new Record().set("trade_checked", 1)
									.set("persist_version", persistVersion.get(num)+1)
									.set("trade_statement_code", seqnoOrstatmentCode)
									.set("trade_check_service_number", checkSerialSeqNo)
									.set("trade_check_user_id", userInfo.getUsr_id())
									.set("trade_check_user_name", userInfo.getName())
									.set("trade_check_date", new Date()),
							new Record().set("id", batchNo.get(num)).set("persist_version", persistVersion.get(num)));
					if(!s){
						return false;
					}
				}
				//存储关系
				int[] result = Db.batchSave("recv_counter_pos_trans_checked", records, 1000);
				if(!ArrayUtil.checkDbResult(result)){
					return false;
				}
				return true;
			}
		});
		if (!flag) {
			throw new DbProcessException("结算对账失败！");
		}
	}
	
	
}
