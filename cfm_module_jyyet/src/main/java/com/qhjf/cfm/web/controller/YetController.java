package com.qhjf.cfm.web.controller;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.YetService;
import com.qhjf.cfm.web.util.jyyet.CollectionMergeUtil;
import com.qhjf.cfm.web.util.jyyet.ExcelCacheUtil;

import java.util.List;
import java.util.Map;

public class YetController extends CFMBaseController {

    private static final LogbackLog log = LogbackLog.getLog(YetController.class);

    private YetService service = new YetService();

    /**
     * 当日余额明细列表
     */
    @Auth(hasForces = {"CurBal","CulBalWave"})
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
            log.error("获取当日余额明细列表失败！", e);
            renderFail(e);
        }
    }

    /**
     * 当日余额明细chart
     */
    public void curdetailtopchart() {
        //与列表返回数据一致
    }

    /**
     * 当日余额汇总列表
     */
    @Auth(hasForces = "CulBalSummary")
    public void curcollectlist() {
        try {
            Record record = getParamsToRecord();
            record.set("org_id", getCurUodp().getOrg_id());
            //根据类型加载不同的数据。1：机构，2：银行 type
            int page_num = getPageNum(record);
            int page_size = getPageSize(record);
            Page<Record> page = service.curcollectlist(page_num, page_size, record);
            //获取金额统计
            Record sum = service.cursum(record);
            renderOkPage(page, sum);
        } catch (ReqDataException e) {
            log.error("获取当日余额明细列表失败！", e);
            renderFail(e);
        }
    }

    /**
     * 当日余额汇总chart
     */
    public void curcollecttopchart() {
        //与列表返回数据一致
    }

    /**
     * 当日余额波动列表
     */
    public void curwavelist() {
        //与当日余额明细列表一致
    }

    /**
     * 当日余额波动chart
     */
    @Auth(hasForces = "CulBalWave")
    public void curwavetopchart() {
        Record record = getParamsToRecord();
        List<Record> list = service.curwavetopchart(record);
        renderOk(list);
    }

    /**
     * 历史余额明细列表
     */
    @Auth(hasForces = "HisBal")
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
            log.error("获取历史余额明细列表失败！", e);
            renderFail(e);
        }
    }

    /**
     * 历史余额明细chart
     */
    public void hisdetailtopchart() {
        //与历史余额明细列表一致
    }

    /**
     * 历史余额汇总列表
     */
    @Auth(hasForces = "HisBalSummary")
    public void hiscollectlist() {
        try {
            Record record = getParamsToRecord();
            record.set("org_id", getCurUodp().getOrg_id());
            //根据类型加载不同的数据。1：机构，2：银行 type
            int page_num = getPageNum(record);
            int page_size = getPageSize(record);
            Page<Record> page = service.hiscollectlist(page_num, page_size, record);
            //获取金额统计
            Record sum = service.hissum(record);
            renderOkPage(page, sum);
        } catch (ReqDataException e) {
            log.error("获取当日余额明细列表失败！", e);
            renderFail(e);
        }
    }

    /**
     * 历史余额汇总chart
     */
    public void hiscollecttopchart() {
        //与历史余额汇总列表一致
    }

    /**
     * 历史余额波动列表
     */
    @Auth(hasForces = "HisBalWave")
    public void hiswavelist() {
        try {
            Record record = getParamsToRecord();
            record.set("org_id", getCurUodp().getOrg_id());
            int page_num = getPageNum(record);
            int page_size = getPageSize(record);
            Page<Record> page = service.hiswavelist(page_num, page_size, record);
            renderOkPage(page);
        } catch (ReqDataException e) {
            log.error("获取历史余额波动列表失败！", e);
            renderFail(e);
        }
    }

    /**
     * 历史余额波动chart
     */
    @Auth(hasForces = {"HisBalWave"})
    public void hiswavetopchart() {
        Record record = getParamsToRecord();
        List<Record> list = service.hiswavetopchart(record);
        renderOk(list);
    }

    /**
     * 导入余额明细
     */
    public void export() {

    }
    
    /**
	 * 当日余额导入
	 */
    @Auth(hasForces = "BalImport")
	public void curBlanceImport() {
		try {
			// 从redis中获取excel上传的数据
			List<Map<String, Object>> list = ExcelCacheUtil.getExcelDataObj(getParamsToRecord());

			/*//从list<Map>中提取银行账号列表
			List<String> accNoList = AccountUtil.getAccNoListDeDuplicate(list);
			
			// 通过excel中的账户号查询：账户id与银行大类
			List<Record> accountInfo = AccountUtil.getAccountInfo(accNoList);*/

			// 把账户id和银行大类加到 excel导入数据中
			List<Record> excelDataObj = CollectionMergeUtil.mergeAccountInfo(list, 1);

			service.curBlanceImport(excelDataObj);
			renderOk(null);
		} catch (BusinessException e) {
			renderFail(e);
		}
	}

	/**
	 * 历史余额导入（只支持单个账号历史余额导入）
	 */
    @Auth(hasForces = "BalImport")
	public void hisBlanceImport() {
		try {
			// 从redis中获取excel上传的数据
			List<Map<String, Object>> list = ExcelCacheUtil.getExcelDataObj(getParamsToRecord());

			/*//从list<Map>中提取银行账号列表
			List<String> accNoList = AccountUtil.getAccNoListDeDuplicate(list);
			
			// 通过excel中的账户号查询：账户id与银行大类
			List<Record> accountInfo = AccountUtil.getAccountInfo(accNoList);*/

			// 把账户id和银行大类加到 excel导入数据中
			List<Record> excelDataObj = CollectionMergeUtil.mergeAccountInfo(list, 2);

			service.hisBlanceImport(excelDataObj);
			renderOk(null);
		} catch (BusinessException e) {
			renderFail(e);
		}
	}

	public void curcollectlistbankexport(){
        try {
            doExport(new Record().set("org_id", getCurUodp().getOrg_id()));
        } catch (ReqDataException e) {
            renderFail(e);
        }
    }
	public void curcollectlistorgexport(){
        try {
            doExport(new Record().set("org_id", getCurUodp().getOrg_id()));
        } catch (ReqDataException e) {
            renderFail(e);
        }
    }
	public void currtransexport(){
        try {
            doExport(new Record().set("org_id", getCurUodp().getOrg_id()));
        } catch (ReqDataException e) {
            renderFail(e);
        }
    }
	public void hiscollectlistbankexport(){
        try {
            doExport(new Record().set("org_id", getCurUodp().getOrg_id()));
        } catch (ReqDataException e) {
            renderFail(e);
        }
    }
	public void hiscollectlistorgexport(){
        try {
            doExport(new Record().set("org_id", getCurUodp().getOrg_id()));
        } catch (ReqDataException e) {
            renderFail(e);
        }
    }
	public void histransexport(){
        try {
            doExport(new Record().set("org_id", getCurUodp().getOrg_id()));
        } catch (ReqDataException e) {
            renderFail(e);
        }
    }
	public void hiswavelistexport(){
        try {
            doExport(new Record().set("org_id", getCurUodp().getOrg_id()));
        } catch (ReqDataException e) {
            renderFail(e);
        }
    }

    public void currwavelistexport(){
        try {
            doExport(new Record().set("org_id", getCurUodp().getOrg_id()));
        } catch (ReqDataException e) {
            renderFail(e);
        }
    }
}
