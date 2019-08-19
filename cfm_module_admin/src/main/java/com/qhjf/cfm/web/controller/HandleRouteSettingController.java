package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.HandleRouteSettingService;

import java.util.List;

/**
 * 路由设置
 *
 * @auther zhangyuanyuan
 * @create 2018/5/28
 */

@SuppressWarnings("unused")
@Auth(withRoles = {"admin"})
public class HandleRouteSettingController extends CFMBaseController {

    private static final Log logger = LogbackLog.getLog(HandleRouteSettingController.class);
    private HandleRouteSettingService service = new HandleRouteSettingService();


    /**
     * 路由设置列表
     */
    public void list() {
        logger.debug("Enter into list()");

        Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.list(pageNum, pageSize, record);
        renderOkPage(page);
    }

    /**
     * 添加路由设置
     */
    public void add() {
        logger.debug("Enter into add()");

        try {
            Record record = service.add(getRecordByParamsStrong());
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("添加路由设置信息失败!", e);
            renderFail(e);
        }
    }

    /**
     * 修改路由设置
     */
    public void chg() {
        logger.debug("Enter into chg()");

        try {
            Record record = service.chg(getRecordByParamsStrong());
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("修改路由设置信息失败!", e);
            renderFail(e);
        }
    }

    /**
     * 查看路由设置详情
     */
    public void detail() {
        logger.debug("Enter into detail()");

        try {
            Record record = service.detail(getParamsToRecord());
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("查看路由设置信息失败!", e);
            renderFail(e);
        }
    }

    /**
     * 删除路由设置
     */
    public void del() {
        logger.debug("Enter into del()");

        try {
            service.del(getParamsToRecord());
            renderOk(null);
        } catch (BusinessException e) {
            logger.error("删除路由设置信息失败!", e);
            renderFail(e);
        }
    }

    /**
     * 修改路由设置状态
     */
    public void setstatus() {
        logger.debug("Enter into setstatus()");

        try {
            Record record = service.setstatus(getParamsToRecord());
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("修改路由设置信息状态失败!");
            renderFail(e);
        }
    }

    /**
     * 选择路由设置对应帐号
     */
    public void setormeracc() {
        logger.debug("Enter into setormeracc()");

        try {
            Record record = getParamsToRecord();
            List<Record> recordList = service.setormeracc(record);
            renderOk(recordList);
        } catch (BusinessException e) {
            logger.error("帐号获取失败!");
            renderFail(e);
        }
    }

}
