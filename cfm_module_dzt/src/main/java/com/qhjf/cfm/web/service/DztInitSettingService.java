package com.qhjf.cfm.web.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.web.constant.BasicTypeConstant;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/25
 * @Description: 对账通 - 对账初始化设置
 */
public class DztInitSettingService {
	
	private static final String ACCOUNT_NOEXIST_ERRMSG = "银行账号[acc_id=%s]不存在";
	private static final String REPEAT_ADD_ERRMSG = "账号[%s]已添加初期余额，不能重复添加";
	private static final String REPEAT_ENABLE_ERRMSG = "重复启用";
	private static final String NOEXIST_ERRMSG = "修改的期初余额不存在";
	private static final String CANNOT_CHANGE = "已启用，不能再修改";
	/**
	 * 新增
	 */
	public Record add(final Record record) throws BusinessException {
		String accId = record.getStr("acc_id");
		Map<String, Object> account = TableDataCacheUtil.getInstance().getARowData("account", "acc_id", accId);
		if (null == account) {
			throw new DbProcessException(String.format(ACCOUNT_NOEXIST_ERRMSG, accId));
		}
		record.set("acc_no", account.get("acc_no"))
				.set("acc_name", account.get("acc_name"));
		SqlPara sqlPara = Db.getSqlPara("dztinit.findByAccId", record);
		Record find = Db.findFirst(sqlPara);
		if (null != find) {
			throw new DbProcessException(String.format(REPEAT_ADD_ERRMSG, account.get("acc_no")));
		}
		
		final String[] errMsg = new String[1];
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				List<Record> detailList = record.get("list");
				record.remove("list");
				
				boolean saveParent = Db.save("check_voucher_initdata", record);
				if (saveParent) {
					Long baseId = record.getLong("id");
					boolean saveSon = saveSon(record, baseId, detailList);
					if (saveSon) {
						return true;
					}else {
						errMsg[0] = "期初余额明细新增失败";
						return false;
					}
				}else {
					errMsg[0] = "期初余额新增失败";
					return false;
				}
			}
		});
		if (flag) {
			return record;
		}else {
			throw new DbProcessException(errMsg[0]);
		}
	}
	
	public Record chg(final Record record) throws BusinessException {
		final Long baseId = checkParentExist(record, 1);
		
		final String[] errMsg = new String[1];
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				//修改父表
				
				SqlPara sqlPara = Db.getSqlPara("dztinit.upd", record);
				int updParent = Db.update(sqlPara);
				if (updParent == 1) {
					//明细表先删后增
					return handleSon(record, baseId, errMsg);
				}else {
					errMsg[0] = "期初余额修改失败";
					return false;
				}
			}
		});
		if (flag) {
			return record;
		}else {
			throw new DbProcessException(errMsg[0]);
		}
	}
	
	public Record enable(final Record record) throws BusinessException {
		final Long baseId = checkParentExist(record, 2);
		
		final String[] errMsg = new String[1];
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				//修改父表
				SqlPara sqlPara = Db.getSqlPara("dztinit.enable", record);
				int updParent = Db.update(sqlPara);
				if (updParent != 1) {
					errMsg[0] = "期初余额启用时修改失败";
					return false;
					
				}
				//更新子表
				boolean handleSon = handleSon(record, baseId, errMsg);
				if (!handleSon) {
					return handleSon;
				}
				//父表check_voucher_initdata的数据插入check_voucher_acc_balance表
				SqlPara addBalanceSql = Db.getSqlPara("dztinit.addBalance", record);
				int add = Db.update(addBalanceSql);
				if (1 == add) {
					return true;
				} else{
					errMsg[0] = "balance新增失败";
					return false;
				}
			}
		});
		if (flag) {
			return record;
		}else {
			throw new DbProcessException(errMsg[0]);
		}
	}
	
	public List<Record> detail(Record record){
		SqlPara sqlPara = Db.getSqlPara("dztinit.finaItems", record);
		List<Record> items = Db.find(sqlPara);
		if (null == items) {
			return new ArrayList<>();
		}
		return items;
	}
	
	public Page<Record> list(int pageNum, int pageSize){
		SqlPara sqlPara = Db.getSqlPara("dztinit.findByPage");
        return Db.paginate(pageNum, pageSize, sqlPara);
	}
	
	
	/**
	 * 保存期初余额详情
	 * @param record
	 * @param baseId
	 * @param list
	 * @return
	 */
	private boolean saveSon(Record record, Long baseId, List<Record> list){
		List<Record> sons = new ArrayList<>();
		if (null != list) {
			if (list.size() > 0) {
				for (Record map : list) {
					int dataType = TypeUtils.castToInt(map.get("data_type"));
					int creditOrDebit = TypeUtils.castToInt(map.get("credit_or_debit"));
					BigDecimal amount = TypeUtils.castToBigDecimal(map.get("amount"));
					String memo = TypeUtils.castToString(map.get("memo"));
					Record son = new Record()
							.set("base_id", baseId)
							.set("acc_id", record.getStr("acc_id"))
							.set("data_type", dataType)
							.set("credit_or_debit", creditOrDebit)
							.set("amount", amount)
							.set("memo", memo);
					sons.add(son);
				}
				
				int[] batch = Db.batchSave("check_voucher_initdata_item", sons, BasicTypeConstant.BATCH_SIZE);
				
				boolean b = ArrayUtil.checkDbResult(batch);
				if (b) {
					return true;
				}else {
					return false;
				}
			}
		}
		return true;
	}
	
	private boolean handleSon(Record r, Long baseId, String[] errMsg){
		//明细表先删后增
		Db.update(Db.getSqlPara("dztinit.delSon", Kv.by("base_id", baseId)));
		List<Record> detailList = r.get("list");
		r.remove("list");
		boolean saveSon = saveSon(r, baseId, detailList);
		if (saveSon) {
			return true;
		}else {
			errMsg[0] = "期初余额明细修改失败";
			return false;
		}
	}
	
	private Long checkParentExist(Record r, int branch) throws DbProcessException{
		SqlPara sqlPara = Db.getSqlPara("dztinit.findByAccId", r);
		Record find = Db.findFirst(sqlPara);
		if (null == find) {
			throw new DbProcessException(NOEXIST_ERRMSG);
		}
		if (find.getInt("is_enabled") == 1) {
			if (branch == 2) {
				throw new DbProcessException(REPEAT_ENABLE_ERRMSG);
			}else if (branch ==1) {
				throw new DbProcessException(CANNOT_CHANGE);
			}
		}
		return find.getLong("id");
	}
}
