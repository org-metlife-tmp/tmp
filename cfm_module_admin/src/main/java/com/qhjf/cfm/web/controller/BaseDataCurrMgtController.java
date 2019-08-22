package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.BaseDataCurrService;

/**
 * 基础数据 - 币种
 *
 * @auther zhangyuanyuan
 * @create 2018/5/22
 */

@SuppressWarnings("unused")
@Auth(withRoles = {"admin"})
public class BaseDataCurrMgtController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(BaseDataCurrMgtController.class);

    //    private BaseDataCurrService service = enhance(BaseDataCurrService.class);
    private BaseDataCurrService service = new BaseDataCurrService();

    /**
     * 获取币种列表
     */
    public void list() {
        logger.debug("Enter into list()");

        Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.findCurrencyPage(pageNum, pageSize, record);
        renderOkPage(page);

    }

    /**
     * 设置默认币种
     */
    public void setdefault() {
        logger.debug("Enter into setdefault()");

        try {
            service.currSetDefault(getParamsToRecord());
            renderOk(null);
        } catch (BusinessException e) {
            logger.error("修改默认币种失败!", e);
            renderFail(e);
        }
    }
}
