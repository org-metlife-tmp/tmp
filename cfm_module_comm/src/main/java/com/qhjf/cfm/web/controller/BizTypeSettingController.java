package com.qhjf.cfm.web.controller;

import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.BizTypeSettingService;

import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/8/30
 * @Description: 业务类型自定义
 */
@Auth(hasRoles = {"admin", "normal"})
public class BizTypeSettingController extends CFMBaseController {

    private static final LogbackLog log = LogbackLog.getLog(BizTypeSettingController.class);

    private BizTypeSettingService service = new BizTypeSettingService();

    public void biztypes() {
        Record record = getParamsToRecord();
        List<Record> list = service.biztypes(record);
        renderOk(list);
    }
}
