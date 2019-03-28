package com.qhjf.cfm.web.controller;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.JytService;
import com.qhjf.cfm.web.util.jyyet.CollectionMergeUtil;
import com.qhjf.cfm.web.util.jyyet.ExcelCacheUtil;

import java.util.List;
import java.util.Map;

/**
 * @Auther: zhangyuan
 * @Date: 2018/7/23
 * @Description: 交易通
 */
public class JytController extends CFMBaseController {

    private static final LogbackLog log = LogbackLog.getLog(JytController.class);

    private JytService service = new JytService();

    @Auth(hasForces = "CurTrans")
    public void curdetaillist() {
        try {
            Record record = getParamsToRecord();
            record.set("org_id", getCurUodp().getOrg_id());
            int page_num = getPageNum(record);
            int page_size = getPageSize(record);
            Page<Record> page = service.curdetaillist(page_num, page_size, record);
            //获取金额统计
            Record sum = service.cursum(record);
            renderOkPage(page, sum);
        } catch (ReqDataException e) {
            log.error("获取当日交易明细列表失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = "CurTransSummary")
    public void curcollectlist() {
        try {
            Record record = getParamsToRecord();
            record.set("org_id", getCurUodp().getOrg_id());
            //根据类型加载不同的数据。1：机构，2：银行 type 3：账户
            int page_num = getPageNum(record);
            int page_size = getPageSize(record);
            Page<Record> page = service.curcollectlist(page_num, page_size, record);
            //获取金额统计
            Record sum = service.cursum(record);
            renderOkPage(page, sum);
        } catch (ReqDataException e) {
            log.error("获取当日交易汇总列表失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = "HisTrans")
    public void hisdetaillist() {
        try {
            Record record = getParamsToRecord();
            record.set("org_id", getCurUodp().getOrg_id());
            int page_num = getPageNum(record);
            int page_size = getPageSize(record);
            Page<Record> page = service.hisdetaillist(page_num, page_size, record);
            //获取金额统计
            Record sum = service.hissum(record);
            renderOkPage(page, sum);
        } catch (ReqDataException e) {
            log.error("获取历史交易明细列表失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"HisTransSummary"})
    public void hiscollectlist() {
        try {
            Record record = getParamsToRecord();
            record.set("org_id", getCurUodp().getOrg_id());
            //根据类型加载不同的数据。1：机构，2：银行 type 3：账户
            int page_num = getPageNum(record);
            int page_size = getPageSize(record);
            Page<Record> page = service.hiscollectlist(page_num, page_size, record);
            //获取金额统计
            Record sum = service.hissum(record);
            renderOkPage(page, sum);
        } catch (ReqDataException e) {
            log.error("获取历史交易汇总列表失败！", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"HisTransWave"})
    public void hiswavelist() {
        try {
            Record record = getParamsToRecord();
            record.set("org_id", getCurUodp().getOrg_id());
            int page_num = getPageNum(record);
            int page_size = getPageSize(record);
            Page<Record> page = service.hiswavelist(page_num, page_size, record);
            renderOkPage(page);
        } catch (ReqDataException e) {
            log.error("获取历史交易波动列表失败！", e);
            renderFail(e);
        }
    }

    /**
     * 历史余额波动chart
     */
    @Auth(hasForces = {"HisTransWave"})
    public void hiswavetopchart() {
        Record record = getParamsToRecord();
        List<Record> list = service.hiswavetopchart(record);
        renderOk(list);
    }

    /**
     * 当日交易明细导出
     */
    public void currtransexport() {
        doExport();
    }

    /**
     * 历史交易明细导出
     */
    public void histransexport() {
        doExport();
    }

    /**
     * 历史波动导出
     */
    public void hiswavelistexport() {
        doExport();
    }

    /**
     * 当日交易汇总 - 账户
     */
    public void curcollectlistaccexport() {
        doExport();
    }

    /**
     * 当日交易汇总 - 公司
     */
    public void curcollectlistorgexport() {
        doExport();
    }

    /**
     * 当日交易汇总 - 银行
     */
    public void curcollectlistbankexport() {
        doExport();
    }

    /**
     * 历史交易汇总 - 账户
     */
    public void hiscollectlistaccexport() {
        doExport();
    }

    /**
     * 历史交易汇总 - 公司
     */
    public void hiscollectlistorgexport() {
        doExport();
    }

    /**
     * 历史交易汇总 - 银行
     */
    public void hiscollectlistbankexport() {
        doExport();
    }
  
    
    /**
     * 当日交易导入
     */
    @Auth(hasForces = "TransImport")
    public void curTransImport(){
    	
    	try {
    		// 从redis中获取excel上传的数据
			List<Map<String, Object>> list = ExcelCacheUtil.getExcelDataObj(getParamsToRecord());
			
			/*//从list<Map>中提取银行账号列表
			List<String> accNoList = AccountUtil.getAccNoListDeDuplicate(list);
			
			// 通过excel中的账户号查询：账户id与银行大类
			List<Record> accountInfo = AccountUtil.getAccountInfo(accNoList);*/
			
			// 把账户id和银行大类加到 excel导入数据中
			List<Record> excelDataObj = CollectionMergeUtil.transMergeAccountInfo(list);
			
			//存库
			service.curTransImport(excelDataObj);
			renderOk(null);
		} catch (BusinessException e) {
			renderFail(e);
		}
    }
    
    /**
     * 历史交易导入
     */
    @Auth(hasForces = "TransImport")
	public void hisTransImport() {
		try {
    		// 从redis中获取excel上传的数据
			List<Map<String, Object>> list = ExcelCacheUtil.getExcelDataObj(getParamsToRecord());
			
			// 把账户id和银行大类加到 excel导入数据中
			List<Record> excelDataObj = CollectionMergeUtil.transMergeAccountInfo(list);
			
			//导入类型:1：覆盖导入；2：增量导入；为空则进行覆盖导入
			String importType = getParamsToRecord().getStr("import_type");
			if (!"2".equals(importType)) {
				importType = "1";
			}
			
			//存库
			service.hisTransImport(excelDataObj, importType);
			renderOk(null);
		} catch (BusinessException e) {
			renderFail(e);
		}
	}
    
}
