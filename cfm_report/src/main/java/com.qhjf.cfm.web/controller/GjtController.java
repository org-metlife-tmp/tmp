package com.qhjf.cfm.web.controller;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.GjtService;
import com.qhjf.cfm.web.service.OaService;

public class GjtController extends CFMBaseController{
    private GjtService service = new GjtService();
/**
 * OA报表
 *
 * @author wy
 * @date 2019年7月3日
 */

    /**
     * 归集通报表查询
     */
    public void GjtReportList() throws Exception {
        Record record = getRecordByParamsStrong();
        UodpInfo uodpInfo = getCurUodp();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        AccCommonService.setSktListStatus(record, "service_status");
        // 查询所有单据总金额
        Page<Record> page = service.GjtReportList(pageNum, pageSize,record, uodpInfo);
        renderOkPage(page);
    }


    /**
     * 数据导出
     */
    public void listexport() {
        doExport();
    }

}
