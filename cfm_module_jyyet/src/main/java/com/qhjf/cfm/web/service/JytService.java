package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.web.constant.BasicTypeConstant;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * @Auther: zhangyuan
 * @Date: 2018/7/23 10:19
 * @Description: 交易通
 */
public class JytService {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	private static final String HAS_CHECKED_OR_STM_ERROR = "导入账号[%s]在导入数据交易日期时间段[%s]-[%s]，存在已核对或已生成凭证的历史交易：【%s】";

    public Page<Record> curdetaillist(int page_num, int page_size, Record record) {
        SqlPara sqlPara = Db.getSqlPara("curjyt.curdetaillist", Kv.by("map", record.getColumns()));
        return Db.paginate(page_num, page_size, sqlPara);
    }

    public Record cursum(Record record) {
        return Db.findFirst(Db.getSqlPara("curjyt.curdetailsum", Kv.by("map", record.getColumns())));
    }

    public Page<Record> curcollectlist(int page_num, int page_size, Record record) {
        int type = TypeUtils.castToInt(record.get("type"));
        record.remove("type");
        if (type == 1) {
            SqlPara sqlPara = Db.getSqlPara("curjyt.curcollectorglist", Kv.by("map", record.getColumns()));
            Page<Record> page = Db.paginate(page_num, page_size, sqlPara);
            return page;
        } else if (type == 2) {
            SqlPara sqlPara = Db.getSqlPara("curjyt.curcollectbanklist", Kv.by("map", record.getColumns()));
            Page<Record> page = Db.paginate(page_num, page_size, sqlPara);
            return page;
        } else if (type == 3) {
            SqlPara sqlPara = Db.getSqlPara("curjyt.curcollectacclist", Kv.by("map", record.getColumns()));
            Page<Record> page = Db.paginate(page_num, page_size, sqlPara);
            return page;
        }
        return new Page<>(null, page_num, page_size, 0, 0);
    }

    public Page<Record> hisdetaillist(int page_num, int page_size, Record record) {
        SqlPara sqlPara = Db.getSqlPara("hisjyt.hisdetaillist", Kv.by("map", record.getColumns()));
        return Db.paginate(page_num, page_size, sqlPara);
    }

    public Record hissum(Record record) {
        return Db.findFirst(Db.getSqlPara("hisjyt.hisdetailsum", Kv.by("map", record.getColumns())));
    }

    public Page<Record> hiscollectlist(int page_num, int page_size, Record record) {
        int type = TypeUtils.castToInt(record.get("type"));
        record.remove("type");
        if (type == 1) {
            SqlPara sqlPara = Db.getSqlPara("hisjyt.hiscollectorglist", Kv.by("map", record.getColumns()));
            Page<Record> page = Db.paginate(page_num, page_size, sqlPara);
            return page;
        } else if (type == 2) {
            SqlPara sqlPara = Db.getSqlPara("hisjyt.hiscollectbanklist", Kv.by("map", record.getColumns()));
            Page<Record> page = Db.paginate(page_num, page_size, sqlPara);
            return page;
        } else if (type == 3) {
            SqlPara sqlPara = Db.getSqlPara("hisjyt.hiscollectacclist", Kv.by("map", record.getColumns()));
            Page<Record> page = Db.paginate(page_num, page_size, sqlPara);
            return page;
        }
        return new Page<>(null, page_num, page_size, 0, 0);
    }

    public Page<Record> hiswavelist(int page_num, int page_size, Record record) {
        SqlPara sqlPara = Db.getSqlPara("hisjyt.hiswavelist", Kv.by("map", record.getColumns()));
        return Db.paginate(page_num, page_size, sqlPara);
    }

    public List<Record> hiswavetopchart(Record record) {
        return Db.find(Db.getSqlPara("hisjyt.hiswavetopchart",Kv.by("map",record.getColumns())));
    }
    
    public void curTransImport(final List<Record> data) throws BusinessException{
    	final String[] error = new String[1];
    	boolean result = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				//1.删除
				Db.delete("delete from acc_cur_transaction where acc_no=?", data.get(0).get("acc_no"));
		    	//2.批量新增
		    	int[] batchSave = Db.batchSave("acc_cur_transaction", data, BasicTypeConstant.BATCH_SIZE);
		    	boolean checkDbResult = ArrayUtil.checkDbResult(batchSave);
		    	if (!checkDbResult) {
		    		error[0] = "批量导入当日交易表异常！";
		    		return false;
				}
		    	return true;
			}
		});
    	if (!result) {
			throw new DbProcessException(error[0]);
		}
    }
    
    public void hisTransImport(final List<Record> data, String importType) throws BusinessException{
    	if (null == data || data.isEmpty()) {
			throw new ReqDataException("导入数据为空！");
		}
		// 1.取导入数据的时间段；2.汇总导入数据
		String min = sdf.format(new Date());
		String max = "1970-01-01";
		
		Map<String, List<Record>> transDateGroup = new HashMap<>();
		
		for (Record r : data) {
			String transDate = r.getStr("trans_date");
			//获取最大时间
			if (max.compareTo(transDate) <= 0) {
				max = transDate;
			}
			//获取最小时间
			if (min.compareTo(transDate) >= 0) {
				min = transDate;
			}
			//按交易日期分组
			if (!transDateGroup.containsKey(transDate)) {
				List<Record> aGroup = new ArrayList<Record>();
				aGroup.add(r);
				transDateGroup.put(transDate, aGroup);
			}else {
				transDateGroup.get(transDate).add(r);
			}
		}
		
		final String start = min;
		final String end = max;
		
		//按日期分组汇总
		final List<Record> summaryRecord = new ArrayList<Record>();
		Set<Entry<String, List<Record>>> entrySet = transDateGroup.entrySet();
		for (Entry<String, List<Record>> entry : entrySet) {
			List<Record> value = entry.getValue();
			Record summary = new Record();
			boolean flag = true;
			double pay_amount = 0;//支出
			double recv_amount = 0;//收入
			for (Record record : value) {
				Integer direction = record.getInt("direction");//收支方向*（1：支出；2：收入）
				if (flag) {//第一次循环
					summary.set("acc_id", record.get("acc_id"));
					summary.set("acc_no", record.get("acc_no"));
					summary.set("acc_name", record.get("acc_name"));
					summary.set("bank_type", record.get("bank_type"));
					summary.set("statistics_date", record.get("trans_date"));
//					summary.set("import_time", record.get("import_time"));
					flag = false;
				}
				if(1 == direction){//支出
					pay_amount = pay_amount + record.getDouble("amount");
				}else{//收入
					recv_amount = recv_amount + record.getDouble("amount");
				}
			}
			summary.set("pay_amount", pay_amount);
			summary.set("recv_amount", recv_amount);
			summaryRecord.add(summary);
		}
    	
		//根据导入类型分支
    	if ("1".equals(importType)) {
    		coverImport(data, start, end, summaryRecord);
		}else {
			incrementImport(data, start, end, summaryRecord);
		}
    }
    
    //按导入日期时间段-增量导入
    /**
     * 
     * @param data	excel导入数据
     * @param start	导入数据的开始日期
     * @param end	导入数据的结束日期
     * @param summaryRecord	导入数据按交易日期分组汇总后的列表
     * @throws DbProcessException
     */
    private void incrementImport(final List<Record> data, final String start, final String end, final List<Record> summaryRecord) throws DbProcessException{
    	//1.判断导入时间段内导入数据的 identifier是否在库中已存在
    	List<String> identifierList = getAKeyList(data, "identifier");
    	SqlPara sqlPara = Db.getSqlPara("hisjyt.identifiercheckexit", Kv.create().set("identifierList", identifierList));
        List<Record> find = Db.find(sqlPara);
        if (null != find && find.size() > 0) {
        	throw new DbProcessException(String.format("导入历史交易【%s】在库中已存在", listRecordToString(find)));
		}
        
    	final String[] error = new String[1];
		boolean result = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				//2.批量插入acc_his_transaction；
				int[] batchSave = Db.batchSave("acc_his_transaction", data, BasicTypeConstant.BATCH_SIZE);
				boolean checkDbResult = ArrayUtil.checkDbResult(batchSave);
				if (!checkDbResult) {
					error[0] = "增量导入时，批量插入历史交易表异常！";
					return false;
				}
		    	//3.按账号、日期查询acc_data_report，与导入数据进行合并；
				String accNo = data.get(0).get("acc_no");
				List<String> statisticsDateList = getAKeyList(summaryRecord, "statistics_date");
				Kv kv = Kv.create().set("acc_no", accNo).set("statisticsDateList", statisticsDateList);
				SqlPara qryreportbyaccno = Db.getSqlPara("hisjyt.qryreportbyaccno", kv);
		    	List<Record> reportList = Db.find(qryreportbyaccno);
		    	//合并账户日报数据
		    	List<Record> reportData = null;
		    	if(null != reportList && reportList.size() > 0){
		    		reportData = mergeReportData(summaryRecord, reportList);
		    		//4.按账号、导入日期删除acc_data_report；
		    		SqlPara delreportbyaccno = Db.getSqlPara("hisjyt.delreportbyaccno", kv);
			    	Db.update(delreportbyaccno);
		    	}else {
		    		reportData = summaryRecord;
				}
		    	//5.批量插入acc_data_report;
		    	int[] batchSave2 = Db.batchSave("acc_data_report", reportData, BasicTypeConstant.BATCH_SIZE);
		    	boolean checkDbResult2 = ArrayUtil.checkDbResult(batchSave2);
				if (!checkDbResult2) {
					error[0] = "增量导入时，批量导入账户日报表异常！";
					return false;
				}
				return true;
			}
		});
		if (!result) {
			throw new DbProcessException(error[0]);
		}
    }
    
    //按导入日期时间段-覆盖导入
    private void coverImport(final List<Record> data, final String start, final String end, final List<Record> summaryRecord) throws DbProcessException{
    	//1.判断导入数据中的acc_id、trans_date时间段内是否含有已生成凭证、已核对的历史交易；
    	final String accNo = data.get(0).get("acc_no");
    	
    	Kv kv = Kv.create().set("acc_no", accNo).set("start", start).set("end", end);
    	SqlPara checkrefundstatementconfirm = Db.getSqlPara("hisjyt.checkrefundstatementconfirm", kv);
    	List<Record> chkAndStm = Db.find(checkrefundstatementconfirm);
    	/*List<Record> chkAndStm = Db.find("select * from (select * from acc_his_transaction where acc_no=? and trans_date>=? and trans_date<=?)tmp where (statement_code is not null and statement_code <> '') or is_checked=1 or (refund_bill_id is not null and refund_bill_id <> '')"
    			, accNo, start, end);*/
    	if (null != chkAndStm && chkAndStm.size() > 0) {
    		throw new DbProcessException(String.format(HAS_CHECKED_OR_STM_ERROR, accNo, start, end, listRecordToString(chkAndStm)));
		}
    	
    	final String[] error = new String[1];
		boolean result = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				// 2.acc_his_transaction：按传入数据时间段删除
				Db.delete("delete from acc_his_transaction where acc_no=? and trans_date>=? and trans_date<=?",
						accNo, start, end);
				// 3.acc_his_transaction:批量插入
				int[] batchSave = Db.batchSave("acc_his_transaction", data, BasicTypeConstant.BATCH_SIZE);
				boolean checkDbResult = ArrayUtil.checkDbResult(batchSave);
				if (!checkDbResult) {
					error[0] = "批量导入历史交易表异常！";
					return false;
				}
				//4.acc_data_report账户日报表：按传入数据时间段删除
				Db.delete("delete from acc_data_report where acc_no=? and statistics_date>=? and statistics_date<=?",
						accNo, start, end);
				//5.acc_data_report账户日报表：批量插入
				int[] batchSave2 = Db.batchSave("acc_data_report", summaryRecord, BasicTypeConstant.BATCH_SIZE);
				boolean checkDbResult2 = ArrayUtil.checkDbResult(batchSave2);
				if (!checkDbResult2) {
					error[0] = "批量导入账户日报表异常！";
					return false;
				}
				return true;
			}
		});
    	if (!result) {
			throw new DbProcessException(error[0]);
		}
    }
    
    //获取List<Record>的Record某一列的列表
    private List<String> getAKeyList(List<Record> data, String key){
    	List<String> result = new ArrayList<>();
    	for (Record r : data) {
    		result.add(r.getStr(key));//"identifier",statistics_date
		}
    	return result;
    }
    //导入历史交易汇总后，与数据库的账户日报数据进行合并
    private List<Record> mergeReportData(List<Record> newly, List<Record> old){
    	for (Record n : newly) {
			for (Record o : old) {
				if (n.getStr("statistics_date").equals(o.getStr("statistics_date"))) {
					n.set("pay_amount", n.getDouble("pay_amount") + o.getDouble("pay_amount"));
					n.set("recv_amount", n.getDouble("recv_amount") + o.getDouble("recv_amount"));
					break;
				}
			}
		}
    	return newly;
    }
    //返回错误信息给前端
    private String listRecordToString(List<Record> data){
    	StringBuffer sb = new StringBuffer();
    	for (Record record : data) {
    		sb.append("{");
    		sb.append("账户号=").append(record.get("acc_no"));
    		sb.append(",收付方向=").append("1".equals(record.get("direction")) ? "付":"收");
    		sb.append(",交易金额=").append(record.get("amount"));
    		sb.append(",对方账户号=").append(record.get("opp_acc_no"));
    		sb.append(",交易日期=").append(record.get("trans_date"));
    		sb.append(",交易时间=").append(record.get("trans_time"));
    		sb.append("}");
		}
    	return sb.toString();
    }
}
