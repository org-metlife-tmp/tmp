package com.qhjf.cfm.web.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.excel.bean.ExcelResultBean;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.BasicTypeConstant;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

/**
 * 柜面收 POS收款记录与POS明细对账
 * 
 * @author pc_liweibing
 *
 */
public class RecvCounterPosRecordCheckPosDetailService {

	private final static Log logger = LogbackLog.getLog(RecvCounterPosRecordCheckPosDetailService.class);
	
	/**
	 *  POS保单列表
	 * @param record
	 * @param curUodp
	 * @param pageNum 
	 * @param pageSize 
	 * @return 
	 */
	public Page<Record> list(Record record, UodpInfo curUodp, int pageSize, int pageNum) {
		record.set("recv_mode",  WebConstant.Sft_RecvPersonalCounter_Recvmode.POS.getKey());
		SqlPara sqlPara = Db.getSqlPara("recv_counter.personalList", Kv.by("map", record.getColumns()));
		Page<Record> paginate = Db.paginate(pageNum, pageSize, sqlPara);
		return paginate;	
	}

	/**
	 * POS导入记录
	 * @param record
	 * @return
	 */
	public List<Record> postlist(Record record) {
		SqlPara sqlPara = Db.getSqlPara("recv_counter_pos_import.recvcounterPoslist", Kv.by("map", record.getColumns()));
		return Db.find(sqlPara);
	}
   
	
	/**
	 * POS收款记录与POS明细对账
	 * @param record
	 * @param userInfo
	 * 
	 */
	public void confirm(Record record, final UserInfo userInfo) throws BusinessException {
		//pos id
		final ArrayList<Integer> posId = record.get("pos_id");
		final ArrayList<Integer> posPersist = record.get("pos_persist");
		//明细 id
		final ArrayList<Integer> detailId = record.get("detail_id");
		final ArrayList<Integer> detailPersist = record.get("detail_persist");

		//获取勾选的交易和批次
		final List<Record> posList = Db.find(Db.getSqlPara("recvcountercheck.findBillList", Kv.by("batchNo", posId)));
		final List<Record> detailList = Db.find(Db.getSqlPara("recv_counter_pos_trans_check.findBillList", Kv.by("batchNo", detailId)));

		if(posList.size()==0 || detailList.size()==0){
			throw new ReqDataException("请选择要核对的POS记录和明细！");
		}

		//判断是否已核对过
		HashSet<Integer> poscheckedSet = new HashSet<>();
		HashSet<Integer> detailcheckedSet = new HashSet<>();
		BigDecimal batchAmount = new BigDecimal(0);
		for(Record r : posList){
			int isChecked = TypeUtils.castToInt(r.get("is_checked"));
			if(isChecked == 1){
				poscheckedSet.add(isChecked);
			}
			batchAmount = batchAmount.add(TypeUtils.castToBigDecimal(r.get("amount")));
		}
		if(poscheckedSet.size() >= 1){
			throw new ReqDataException("已勾选的存在已核对的POS记录！");
		}

		BigDecimal tradAmount = new BigDecimal(0);
		for(Record r : detailList){
			int isChecked = TypeUtils.castToInt(r.get("bill_checked"));
			if(isChecked == 1){
				detailcheckedSet.add(isChecked);
			}
			tradAmount = tradAmount.add(TypeUtils.castToBigDecimal(r.get("trade_amount")));
		}
		if(detailcheckedSet.size() >= 1){
			throw new ReqDataException("已勾选的存在已核对的明细记录！");
		}
		if(batchAmount.compareTo(tradAmount) != 0){
			throw new ReqDataException("POS记录金额和明细金额不相同！");
		}

		final String checkSerialSeqNo = RedisSericalnoGenTool.genCheckSerialSeqNo();//生成十六进制流水号

		final List<Record> records = CommonService.genPayConfirmRecords(detailId, posId, userInfo, checkSerialSeqNo);

		//进行数据新增操作
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				//更新明细
				String seqnoOrstatmentCode = RedisSericalnoGenTool.genVoucherSeqNo();//生成十六进制序列号/凭证号
				for(int num=0; num<detailId.size(); num++){
					boolean s = CommonService.update("recv_counter_pos",
							new Record().set("bill_checked", 1)
									.set("persist_version", detailPersist.get(num)+1)
									.set("bill_statement_code", seqnoOrstatmentCode)
									.set("bill_check_service_number", checkSerialSeqNo)
									.set("bill_check_user_id", userInfo.getUsr_id())
									.set("bill_check_user_name", userInfo.getName())
									.set("bill_check_date", new Date()),
							new Record().set("id", detailId.get(num)).set("persist_version", detailPersist.get(num)));
					if(!s){
						return false;
					}
				}
				//更新pos记录
				for(int num=0; num<posId.size(); num++){
					boolean s = CommonService.update("recv_counter_bill",
							new Record().set("is_checked", 1)
									.set("persist_version", posPersist.get(num)+1)
									.set("statement_code", seqnoOrstatmentCode)
									.set("check_service_number", checkSerialSeqNo)
									.set("check_user_id", userInfo.getUsr_id())
									.set("check_user_name", userInfo.getName())
									.set("check_date", new Date()),
							new Record().set("id", posId.get(num)).set("persist_version", posPersist.get(num)));
					if(!s){
						return false;
					}
				}
				//存储关系
				int[] result = Db.batchSave("recv_counter_pos_detail_checked", records, 1000);
				if(!ArrayUtil.checkDbResult(result)){
					return false;
				}
				try {
					//生成凭证信息
					logger.info("POS记录与明细对账凭证生成开始");
					boolean vouFlag = CheckVoucherService.sunVoucherData(posId, detailId, posList, detailList, WebConstant.MajorBizType.GMSTD.getKey(), seqnoOrstatmentCode, userInfo);
					logger.info("POS记录与明细对账凭证生成结束");
					return vouFlag;
				} catch (BusinessException e) {
					e.printStackTrace();
					return false;
				}
			}
		});
		if (!flag) {
			throw new DbProcessException("结算对账失败！");
		}
		
	}

	/**
	 * 
	 * @param bean
	 * @param userInfo
	 * @throws ReqDataException 
	 * @throws DbProcessException 
	 */
	public void importPos(ExcelResultBean bean, UserInfo userInfo) throws ReqDataException, DbProcessException{
		List<Map<String,Object>> rowData = bean.getRowData();
		if (null == rowData || rowData.isEmpty()) {
			throw new ReqDataException("导入数据为空!");
		}
		
		List<Record> data = handleList(rowData, userInfo.getUsr_id());
		
		savePos(data);
	}
	
	private List<Record> handleList(List<Map<String, Object>> rowData, Long userId) {
		List<Record> data = new ArrayList<>();
		Date time = new Date();
		
		for (Map<String, Object> row : rowData) {
			String liquidateDate = TypeUtils.castToString(row.get("liquidateDate"));
			String transDate = TypeUtils.castToString(row.get("transDate"));
			String transTime = TypeUtils.castToString(row.get("transTime"));
			if (StringUtils.isBlank(liquidateDate) && StringUtils.isBlank(transDate) && StringUtils.isBlank(transTime)) {
				continue;
			}
			
			if (liquidateDate.indexOf("总笔数") != -1) {
				logger.debug(Arrays.toString(row.values().toArray()));
				continue;
			}
			
			Record rowRecord = new Record();
			rowRecord.set("liquidate_date", liquidateDate);
			rowRecord.set("trans_date", transDate);
			rowRecord.set("trans_time", transTime);
			rowRecord.set("terminal_no", row.get("terminalNo"));
			rowRecord.set("trans_amount", row.get("transAmount"));
			rowRecord.set("service_charge", row.get("serviceCharge"));
			rowRecord.set("entry_amount", row.get("entryAmount"));
			rowRecord.set("sys_tracer_no", row.get("sysTracerNo"));
			rowRecord.set("search_refer_no", row.get("searchReferNo"));
			rowRecord.set("Service_no", row.get("serviceNo"));
			rowRecord.set("tran_type", row.get("transType"));
			rowRecord.set("account_no", row.get("accountNo"));
			rowRecord.set("account_type", row.get("accountType"));
			rowRecord.set("bank_name", row.get("bankName"));
			rowRecord.set("non_touch_mark", row.get("nonTouchMark"));
			rowRecord.set("import_time", time);
			rowRecord.set("create_on", time);
			rowRecord.set("create_by", userId);
			
			data.add(rowRecord);
		}
		System.out.println(data);
		return data;
	}
	
	private void savePos(final List<Record> data) throws DbProcessException{
    	boolean result = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
		    	int[] batchSave = Db.batchSave("recv_counter_pos_data", data, BasicTypeConstant.BATCH_SIZE);
		    	boolean checkDbResult = ArrayUtil.checkDbResult(batchSave);
		    	if (!checkDbResult) {
		    		logger.error(String.format("POS导入失败，[{}]", Arrays.toString(batchSave)));
		    		return false;
				}
		    	return true;
			}
		});
    	if (!result) {
			throw new DbProcessException("批量导入POS数据异常！");
		}
	}
       
}

