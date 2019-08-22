package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.OriginDataOaService;

public class OriginDataOaController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(OriginDataOaController.class);

    private OriginDataOaService service = new OriginDataOaService();

    @Auth(hasForces = {"DataMgt"})
    public void list() {
        Record record = getParamsToRecord();
        int page_num = getPageNum(record);
        int page_size = getPageSize(record);
        Page<Record> page = service.getPage(page_num, page_size, record);
        renderOkPage(page);
    }

    /**
     * 数据接口管理导出
     */
    public void listexport() {
        doExport();
    }
}
