package com.qhjf.cfm.web.controller;

import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.OaService;

import java.util.List;

public class OaController extends CFMBaseController{
    private OaService service = new OaService();
/**
 * OA报表
 *
 * @author wy
 * @date 2019年7月3日
 */

    /**
     * OA报表查询
     */
//@Auth(hasForces = {"OaReportList"})
    public void OaReportList() throws Exception {
        Record record = getRecordByParamsStrong();
        UodpInfo uodpInfo = getCurUodp();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        AccCommonService.setSktListStatus(record, "service_status");
        // 查询所有单据总金额
        Page<Record> page = service.OaReportList(pageNum, pageSize,record, uodpInfo);
        renderOkPage(page);
    }
/*public void OaReportList() throws Exception {
    Record record = getRecordByParamsStrong(); //想要获取具体的或者修改前端传的值 直接record.get

    UodpInfo uodpInfo = getCurUodp();

    int pageNum = getPageNum(record);
    int pageSize = getPageSize(record);
    //AccCommonService.setSktListStatus(record, "service_status");
    AccCommonService.setOaTodoStatus(record, "service_status");
    List<Record> oaList = service.OaReportList(record,uodpInfo);
    // 查询所有单据总金额
    renderOk(oaList);
}*/


    /**
     * 数据接口管理导出
     */
    public void listexport() {
        doExport();
    }

}
