package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.web.constant.BasicTypeConstant;
import com.qhjf.cfm.web.util.jyyet.HisBalanceImportBean;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

public class YetService {
	
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

	public Page<Record> curdetaillist(int pageNum, int pageSize, Record record) {
		SqlPara sqlPara = Db.getSqlPara("curyet.curdetaillist", Kv.by("map", record.getColumns()));
		return Db.paginate(pageNum, pageSize, sqlPara);
	}

	public Record cursum(Record record) {
		SqlPara sqlPara = Db.getSqlPara("curyet.cursum", Kv.by("map", record.getColumns()));
		return Db.findFirst(sqlPara);
	}

	public Page<Record> curcollectlist(int pageNum, int pageSize, Record record) {
		int type = TypeUtils.castToInt(record.get("type"));
		record.remove("type");
		if (type == 1) {
			SqlPara sqlPara = Db.getSqlPara("curyet.curcollectorglist", Kv.by("map", record.getColumns()));
			return Db.paginate(pageNum, pageSize, sqlPara);
		} else if (type == 2) {
			SqlPara sqlPara = Db.getSqlPara("curyet.curcollectbanklist", Kv.by("map", record.getColumns()));
			return Db.paginate(pageNum, pageSize, sqlPara);
		}
		return new Page<>(null, pageNum, pageSize, 0, 0);
	}

	public List<Record> curwavetopchart(Record record) {
		List<Record> records = Db.find(Db.getSql("curyet.curwavetopchart"), record.get("acc_id"));
		// 如果没有数据,不去添加期初余额的节点了,直接返回
		if (null == records || records.size() == 0) {
			return records;
		}
		// 期初余额.昨天的最终余额. 可能有,可能没有
		List<Record> hisRecords = Db.find(Db.getSql("hisyet.initialBalance"), record.get("acc_id"));
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date zero = calendar.getTime();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		if (null != hisRecords && hisRecords.size() > 0) {
			records.add(0, hisRecords.get(0).set("import_time", sdf.format(zero)));
		}
		return records;
	}

	public Page<Record> hisdetaillist(int page_num, int page_size, Record record) {
		SqlPara sqlPara = Db.getSqlPara("hisyet.hisdetaillist", Kv.by("map", record.getColumns()));
		return Db.paginate(page_num, page_size, sqlPara);
	}

	public Record hissum(Record record) {
		return Db.findFirst(Db.getSqlPara("hisyet.hissum", Kv.by("map", record.getColumns())));
	}

	public Page<Record> hiscollectlist(int page_num, int page_size, Record record) {
		int type = TypeUtils.castToInt(record.get("type"));
		record.remove("type");
		if (type == 1) {
			SqlPara sqlPara = Db.getSqlPara("hisyet.hiscollectorglist", Kv.by("map", record.getColumns()));
			return Db.paginate(page_num, page_size, sqlPara);
		} else if (type == 2) {
			SqlPara sqlPara = Db.getSqlPara("hisyet.hiscollectbanklist", Kv.by("map", record.getColumns()));
			return Db.paginate(page_num, page_size, sqlPara);
		}
		return new Page<>(null, page_num, page_size, 0, 0);
	}

	public Page<Record> hiswavelist(int page_num, int page_size, Record record) {
		SqlPara sqlPara = Db.getSqlPara("hisyet.hiswavelist", Kv.by("map", record.getColumns()));
		return Db.paginate(page_num, page_size, sqlPara);
	}

	public List<Record> hiswavetopchart(Record record) {
		return Db.find(Db.getSql("hisyet.hiswavetopchart"), record.get("acc_id"), record.get("start_date"),
				record.get("end_date"));
	}

	/**
	 * 当日余额导入 余额波动表一个账号一天只有一个日中数据
	 * 
	 * @throws BusinessException
	 */
	public void curBlanceImport(final List<Record> list) throws BusinessException {
		
		if (null == list || list.isEmpty()) {
			throw new ReqDataException("导入数据为空！");
		}
		
		//key:acc_no	value:当前账号交易时间最近的一条数据
		final Map<String, Record> accNoGroup = new HashMap<>();
		//搜索导入时间最大的数据，即最近的数据
		for (Record r : list) {
			String accNo = r.getStr("acc_no");
			if (!accNoGroup.containsKey(accNo)) {//分组不存在，则加入分组
				accNoGroup.put(accNo, r);
			}else {//r的交易时间比old交易时间新，则替换old
				Record old = accNoGroup.get(accNo);
				if (old.getStr("import_time").compareTo(r.getStr("import_time")) <= 0) {
					accNoGroup.put(accNo, r);//覆盖
				}
			}
			
		}

		// 1.循环插入当日余额，如果失败则修改
		final String[] error = new String[1];

		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				//当日余额，一个账户只有一条数据，存入import_time最大的那条数据（余额时间保存在import_time中的）
				
				Collection<Record> recordList = accNoGroup.values();
				for (Record record : recordList) {
					Record findById = Db.findById("acc_cur_balance", "acc_no", record.getStr("acc_no"));
					if (null == findById) {
						boolean save = Db.save("acc_cur_balance", record);
						if (!save) {
							error[0] = String.format("保存账户【%s】失败！", record.getStr("acc_no"));
							return false;
						}
						record.remove("id"); //为了保证下面波动的插入成功
					}else {
						boolean update = Db.update("acc_cur_balance", "acc_no", record);
						if (!update) {
							error[0] = String.format("更新账户【%s】失败！", record.getStr("acc_no"));
							return false;
						}
					}
				}

				// 余额波动开始。。。。。
				// 1.删除excel中所有acc_no的当日余额波动数据
				Set<String> accNoSet = new HashSet<>();
				for (Record r : list) {
					accNoSet.add(r.getStr("acc_no"));
				}
				SqlPara sqlPara = Db.getSqlPara("curyet.deleteBalenceWave", Kv.create().set("accNoList", accNoSet));
				Db.update(sqlPara);
				// 2.批量插入当日余额波动数据
				int[] batchSave = Db.batchSave("acc_cur_balance_wave", list, BasicTypeConstant.BATCH_SIZE);
				boolean result = ArrayUtil.checkDbResult(batchSave);
				if (!result) {
					error[0] = "批量导入当日余额波动表异常！";
					return false;
				}
				return true;
			}
		});
		if (!flag) {
			throw new DbProcessException(error[0]);
		}
	}

	/**
	 * 历史余额导入
	 * 	要求:同一个账号，同一天只能含有一条余额数据
	 * 
	 * @throws BusinessException
	 */
	public void hisBlanceImport(final List<Record> list) throws BusinessException {
		if (null == list || list.isEmpty()) {
			throw new ReqDataException("导入数据为空！");
		}
		
		//1.按照银行账号分组、去重、计算时间段
		final Map<String, HisBalanceImportBean> accNoGroup = new HashMap<>();
		for (Record r : list) {
			String accNo = r.getStr("acc_no");
			if (!accNoGroup.containsKey(accNo)) {
				HisBalanceImportBean bean = new HisBalanceImportBean();
				bean.addRecord(r);
				accNoGroup.put(accNo, bean);
			}else {
				HisBalanceImportBean bean = accNoGroup.get(accNo);
				bean.addRecord(r);
			}
		}
		//2.分组后的余额数据汇总；
		Iterator<HisBalanceImportBean> iterator = accNoGroup.values().iterator();
		final List<Record> listNew = new ArrayList<>();
		while (iterator.hasNext()) {
			HisBalanceImportBean next = iterator.next();
			listNew.addAll(next.getMap().values());
		}
		
		
		final String[] error = new String[1];
		
		boolean result = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				//3.按账号以及时间段删除余额数据
				Iterator<HisBalanceImportBean> iter = accNoGroup.values().iterator();
				while (iter.hasNext()) {
					HisBalanceImportBean next = iter.next();
					Db.delete("DELETE FROM acc_his_balance WHERE acc_no=? and bal_date>=? and bal_date<=?",
							next.getAccNo(), next.getStart(), next.getEnd());
				}
				// 4.批量插入数据
				int[] resultBalanceWave = Db.batchSave("acc_his_balance", listNew, 1000);
				boolean result = ArrayUtil.checkDbResult(resultBalanceWave);
				if (!result) {
					error[0] = "批量导入历史余额波动表异常！";
					return false;
				}
				return true;
			}
		});
		
		if (!result) {
			throw new DbProcessException(error[0]);
		}
	}
}
