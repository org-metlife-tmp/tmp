package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.ZftRefundService;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2019/7/22
 * @Description: 支付通-退票重复
 */
public class ZftRefundController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(ZftRefundController.class);
    private ZftRefundService service = new ZftRefundService();

    /**
     * 列表
     */
    @Auth(hasForces = {"ZFTRefund"})
    public void list() {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.list(pageNum, pageSize, record);
        renderOkPage(page);
    }


    /**
     * 确认
     */
    @Auth(hasForces = {"ZFTRefund"})
    public void confirm() {
        Record record = getRecordByParamsStrong();
        try {
            record = service.confirm(record, getUserInfo(), getCurUodp());
            renderOk(record);
        } catch (BusinessException e) {
            renderFail(e);
        }
    }
}
