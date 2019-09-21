package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.MerchantAccInfoService;

/**
 * 账户管理 - 商户号管理
 *
 * @auther zhangyuanyuan
 * @create 2018/5/24
 */

@SuppressWarnings("unused")
@Auth(withRoles = {"admin"})
public class MerchantAccInfoMgtController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(MerchantAccInfoMgtController.class);
    private MerchantAccInfoService service = new MerchantAccInfoService();

    /**
     * 获取商户号列表
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
     * 查看商户号信息
     */
    public void detail() {
        logger.debug("Enter into detail()");

        try {
            Record record = service.detail(getParamsToRecord());
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("查看商户号信息失败!", e);
            renderFail(e);
        }
    }

    /**
     * 添加商户号信息
     */
    public void add() {
        logger.debug("Enter into add()");
        try {
            Record record = service.add(getParamsToRecord());
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("添加商户号信息失败!", e);
            renderFail(e);
        }
    }

    /**
     * 修改商户号信息
     */
    public void chg() {
        logger.debug("Enter into chg()");

        try {
            Record record = service.chg(getParamsToRecord());
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("修改商户号信息失败!", e);
            renderFail(e);
        }
    }


    /**
     * 删除商户号信息
     */
    public void del() {
        logger.debug("Enter into del()");

        try {
            service.del(getParamsToRecord());
            renderOk(null);
        } catch (BusinessException e) {
            logger.error("删除商户号信息失败!", e);
            renderFail(e);
        }
    }

    /**
     * 修改商户号状态
     */
    public void setstatus() {
        logger.debug("Enter into setstatus()");

        try {
            Record record = service.setstatus(getParamsToRecord());
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("修改商户号信息状态失败!");
            renderFail(e);
        }
    }
}
