package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.VoucherQueryService;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2019/5/31
 * @Description: 凭证查询
 */
public class VoucherQueryController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(VoucherQueryController.class);
    private VoucherQueryService service = new VoucherQueryService();

    /**
     * 凭证查询列表
     */
    @Auth(hasForces = {"VoucherQuery"})
    public void list() {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        try {
            UodpInfo uodpInfo = getCurUodp();
            record.set("org_id", uodpInfo.getOrg_id());
            Page<Record> page = service.list(pageNum, pageSize, record);
            renderOkPage(page);
        } catch (BusinessException e) {
            logger.error("sunVoucherData list fail!");
            renderFail(e);
        }

    }

    /**
     * 凭证查询导出
     */
    @Auth(hasForces = {"VoucherQuery"})
    public void export() {
        doExport();
    }
}
