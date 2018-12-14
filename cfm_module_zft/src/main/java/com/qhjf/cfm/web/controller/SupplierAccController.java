package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.SupplierAccService;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/8/9
 * @Description: 供应商信息管理
 */
public class SupplierAccController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(SupplierAccController.class);
    private SupplierAccService service = new SupplierAccService();

    @Auth(hasForces = {"ZFTSuplierAcc"})
    public void list() {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.list(pageNum, pageSize, record);
        renderOkPage(page);
    }

    @Auth(hasForces = {"ZFTSuplierAcc"})
    public void add() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        try {
            record = service.add(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("添加供应商信息失败!", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"ZFTSuplierAcc"})
    public void chg() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        try {
            record = service.chg(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("修改供应商信息失败!", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"ZFTSuplierAcc"})
    public void del() {
        Record record = getRecordByParamsStrong();
        try {
            service.del(record);
            renderOk(null);
        } catch (BusinessException e) {
            logger.error("删除供应商信息失败!", e);
            renderFail(e);
        }
    }


}
