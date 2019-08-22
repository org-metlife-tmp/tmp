package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.AllocationViewService;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/19
 * @Description: 资金下拨管理
 */
@SuppressWarnings("unused")
public class AllocationViewController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(AllocationViewController.class);
    private AllocationViewService service = new AllocationViewService();

    /**
     * 下拨查看列表
     */
    @Auth(hasForces = {"ZJXBView"})
    public void list() {
        Page<Record> page = null;
        Record record = getRecordByParamsStrong();
        try {
            UodpInfo uodpInfo = getCurUodp();
            int pageNum = getPageNum(record);
            int pageSize = getPageSize(record);

            AccCommonService.setAllocationViewListStatus(record, "service_status");

            page = service.list(pageNum, pageSize, record, uodpInfo);
            renderOkPage(page);
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }
}
