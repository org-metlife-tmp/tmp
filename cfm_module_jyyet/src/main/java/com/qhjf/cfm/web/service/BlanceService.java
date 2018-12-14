package com.qhjf.cfm.web.service;

import java.util.List;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.web.constant.BasicTypeConstant;

/**
 * 余额导入
 * @author CHT
 *
 */
public class BlanceService {

	/**
     * 当日余额导入
	 * @throws BusinessException 
     */
	public void curBlanceImport(List<Record> list) throws BusinessException{
		boolean result = false;
		if (null == list || list.isEmpty()) {
			throw new ReqDataException("导入数据为空！");
		}
		//1.循环插入当日余额，如果失败则修改
		boolean save = false;
		boolean update = false;
		for (Record record : list) {
			//新增
			save = Db.save("acc_cur_balance", record);
			if (!save) {
				//修改
				update = Db.update("acc_cur_balance", "acc_no", record);
				if (!update) {
					throw new DbProcessException(String.format("账户【%s】导入失败！", record.getStr("acc_no")));
				}
			}
		}
		//2.批量插入当日余额波动表
		int[] batchSave = Db.batchSave("acc_cur_balance_wave", list, BasicTypeConstant.BATCH_SIZE);
		result = ArrayUtil.checkDbResult(batchSave);
		if (!result) {
			throw new DbProcessException("批量导入当日余额波动表异常！");
		}
	}
	
	/**
     * 历史余额导入
	 * @throws BusinessException 
     */
	public void hisBlanceImport(List<Record> list) throws BusinessException{
		boolean result = false;
		if (null == list || list.isEmpty()) {
			throw new ReqDataException("导入数据为空！");
		}
		//1.批量插入数据
		int[] resultBalanceWave = Db.batchSave("acc_his_balance", list, 1000);
		result = ArrayUtil.checkDbResult(resultBalanceWave);
		if (!result) {
			throw new DbProcessException("批量导入历史余额波动表异常！");
		}
	}
}
