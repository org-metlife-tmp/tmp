package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.DztAdjustService;

import java.text.ParseException;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/25
 * @Description: 对账通 - 余额调节表
 */
public class DztAdjustController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(DztAdjustController.class);
    private DztAdjustService service = new DztAdjustService();

    /**
     * 余额调节表列表
     */
    @Auth(hasForces = {"DztBalAdjust"})
    public void list() {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.list(pageNum, pageSize, record);
        renderOkPage(page);
    }

    /**
     * 查询余额调节表
     */
    @Auth(hasForces = {"DztBalAdjust"})
    public void build() {
        Record record = getRecordByParamsStrong();
        try {
            record = service.build(record);
            renderOk(record);
        } catch (BusinessException e) {
            renderFail(e);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Auth(hasForces = {"DztBalAdjust"})
    public void detail() {
        Record record = getRecordByParamsStrong();
        try {
            record = service.detail(record);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 启用
     */
    @Auth(hasForces = {"DztBalAdjust"})
    public void confirm() {
        Record record = getRecordByParamsStrong();

        try {
            record = service.confirm(record);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }
}
