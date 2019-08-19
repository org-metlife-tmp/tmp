package com.qhjf.cfm.web.controller;


import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.service.CurrencyService;


/**
 * 币种
 *
 * @auther zhangyuanyuan
 * @create 2018/7/3
 */

@Auth(hasRoles = {"admin", "normal"})
public class CurrencyController extends CFMBaseController {

    private CurrencyService service = new CurrencyService();

    /**
     * 获取币种列表
     */
    public void list() {
        Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.findCurrencyPage(pageNum, pageSize, record);
        renderOkPage(page);

    }

}
