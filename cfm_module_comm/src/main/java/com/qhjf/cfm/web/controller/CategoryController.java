package com.qhjf.cfm.web.controller;


import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.CategoryService;
import com.qhjf.cfm.web.utils.CategoryInfo;

import java.util.List;

/**
 * 码表
 *
 * @auther zhangyuanyuan
 * @create 2018/7/2
 */
@Auth(hasRoles = {"admin", "normal"})
public class CategoryController extends CFMBaseController {
    private final static Log logger = LogbackLog.getLog(CategoryController.class);

    private final CategoryService service = new CategoryService();

    /**
     * 码表信息列表
     */
    public void list() {
        Record record = getParamsToRecord();
        List<Record> list = service.list(record);
        renderOk(list);
    }


    public void listN() {
        Record record = getParamsToRecord();
        List<CategoryInfo> list = service.listN(record);
        renderOk(list);
    }
}
