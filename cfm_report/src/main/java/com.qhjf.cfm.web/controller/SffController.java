package com.qhjf.cfm.web.controller;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.SffService;

public class SffController extends CFMBaseController{
    private SffService service = new SffService();
/**
 * Sff报表
 *
 * @author wy
 * @date 2019年7月3日
 */

    /**
     * Sff报表查询
     */
//@Auth(hasForces = {"SffReportList"})
    public void SffReportList() throws Exception {
        Record record = getRecordByParamsStrong();
        UodpInfo uodpInfo = getCurUodp();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        AccCommonService.setSktListStatus(record, "service_status");
        // 查询所有单据总金额
        Page<Record> page = service.sffReportList(pageNum, pageSize,record, uodpInfo);
        renderOkPage(page);
    }
/*public void SffReportList() throws Exception {
    Record record = getRecordByParamsStrong(); //想要获取具体的或者修改前端传的值 直接record.get

    UodpInfo uodpInfo = getCurUodp();

    int pageNum = getPageNum(record);
    int pageSize = getPageSize(record);
    //AccCommonService.setSktListStatus(record, "service_status");
    AccCommonService.setSffTodoStatus(record, "service_status");
    List<Record> SffList = service.SffReportList(record,uodpInfo);
    // 查询所有单据总金额
    renderOk(SffList);
}*/


    /**
     * 数据接口管理导出
     */
    public void listexport() {
        doExport();
    }

}
