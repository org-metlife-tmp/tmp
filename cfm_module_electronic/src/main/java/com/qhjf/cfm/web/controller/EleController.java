package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.EleService;

import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/21
 * @Description: 电子回单
 */
@SuppressWarnings("unused")
public class EleController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(EleController.class);
    private EleService service = new EleService();

    /**
     * 电子回单类型
     */
    public void type() {
        Record record = getRecordByParamsStrong();
        List<Record> list = service.type(record);
        renderOk(list);
    }

    /**
     * 电子回单列表
     */
    @Auth(hasForces = {"ElectronicBillMgr"})
    public void list() {
        Record record = getRecordByParamsStrong();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.list(pageNum, pageSize, record);
        renderOkPage(page);
    }

    /**
     * 电子回单模版
     */
    @Auth(hasForces = {"ElectronicBillMgr"})
    public void template() {
        Record record = getRecordByParamsStrong();
        record =  service.template(record);
        renderOk(record);
    }
}
