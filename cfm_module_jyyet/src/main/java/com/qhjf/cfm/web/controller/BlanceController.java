package com.qhjf.cfm.web.controller;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.redis.Redis;
import com.qhjf.cfm.excel.bean.ExcelResultBean;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.config.RedisCacheConfigSection;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.BlanceService;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 余额导入
 * 
 * @author CHT
 *
 */
public class BlanceController extends CFMBaseController {

	private static final LogbackLog log = LogbackLog.getLog(BlanceController.class);

	private  static final RedisCacheConfigSection config = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Redis);

	private BlanceService service = new BlanceService();

	/**
	 * 当日余额导入 余额日期可以不录
	 */
	public void curBlanceImport() {
		try {
			// 从redis中获取excel上传的数据
			List<Map<String, Object>> list = getExcelDataObj();

			// 通过excel中的账户号查询：账户id与银行大类
			List<Record> accountInfo = getAccountInfo(list);

			// 把账户id和银行大类加到 excel导入数据中
			List<Record> excelDataObj = mergeAccountInfo(list, accountInfo);

			service.curBlanceImport(excelDataObj);
			renderOk(null);
		} catch (BusinessException e) {
			renderFail(e);
		}
	}

	/**
	 * 历史余额导入
	 */
	public void hisBlanceImport() {
		try {
			// 从redis中获取excel上传的数据
			List<Map<String, Object>> list = getExcelDataObj();

			// 通过excel中的账户号查询：账户id与银行大类
			List<Record> accountInfo = getAccountInfo(list);

			// 把账户id和银行大类加到 excel导入数据中
			List<Record> excelDataObj = mergeAccountInfo(list, accountInfo);

			service.hisBlanceImport(excelDataObj);
			renderOk(null);
		} catch (BusinessException e) {
			renderFail(e);
		}
	}

	/**
	 * 从redis中取出当次上传的Excel中的数据，并转换为List《Record》
	 * 
	 * @return
	 * @throws BusinessException
	 */
	private List<Map<String, Object>> getExcelDataObj() throws BusinessException {
		Record record = getParamsToRecord();
		String objectId = record.getStr("object_id");
		if (StringUtils.isBlank(objectId)) {
			throw new ReqDataException("请求参数object_id为空");
		}

		// 从redis中获取excel导入的数据
		String redisDb = config.getCacheName();

		log.debug(String.format("请求参数：【%s】,Redis库：【%s】", record, redisDb));

		ExcelResultBean excelResultBean = (ExcelResultBean) Redis.use(redisDb).get(objectId);
		List<Map<String, Object>> rowData = excelResultBean.getRowData();
		if (null == rowData || rowData.isEmpty()) {
			throw new ReqDataException(String.format("导入数据为空!object_id=%s", objectId));
		}
		return rowData;
	}

	/**
	 * 通过银行账号列表查询银行账号信息
	 * 
	 * @param list
	 *            银行账号列表
	 * @return
	 * @throws BusinessException
	 */
	public static List<Record> getAccountInfo(List<Map<String, Object>> list) throws BusinessException {
		List<String> accNoList = new ArrayList<>();
		String acc_no = null;
		for (Map<String, Object> map : list) {
			acc_no = map.get("acc_no").toString();
			if (StringUtils.isBlank(acc_no)) {
				throw new ReqDataException(String.format("导入数据银行账号为空，数据为：【%s】", map.toString()));
			}
			accNoList.add(acc_no);
		}
		SqlPara sqlPara = Db.getSqlPara("curyet.accountInfo", accNoList);
		List<Record> find = Db.find(sqlPara);
		if (find == null || accNoList.size() != find.size()) {
			throw new DbProcessException(String.format("某些银行账号在系统中不存在!账号列表=【%s】", accNoList));
		}
		return find;
	}

	/**
	 * 把source中的字段合并到dest中
	 * 
	 * @param dest
	 * @param source
	 */
	public static List<Record> mergeAccountInfo(List<Map<String, Object>> dest, List<Record> source) {
		List<Record> excelDataObj = new ArrayList<>();
		for (Map<String, Object> map : dest) {
			//	余额与可用余额保持一致
			Record record = new Record().setColumns(map).set("data_source", 2).set("available_bal", map.get("bal"));// 添加字段：数据来源为手工导入

			for (Record accountInfo : source) {
				if (accountInfo.getStr("acc_no").equals(map.get("acc_no").toString())) {
					record.set("acc_id", accountInfo.get("acc_id")).set("bank_type", accountInfo.get("bank_type")).set("acc_name", accountInfo.get("acc_name"));
				}
			}
			excelDataObj.add(record);
		}
		return excelDataObj;
	}
}
