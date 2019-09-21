package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.BaseDataDeptService;

/**
 * 基础数据 - 部门
 *
 * @auther zhangyuanyuan
 * @create 2018/5/23
 */

@SuppressWarnings("unused")
@Auth(withRoles = {"admin"})
public class BaseDataDeptMgtController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(BaseDataDeptMgtController.class);
    //    private BaseDataDeptService service = enhance(BaseDataDeptService.class);
    private BaseDataDeptService service = new BaseDataDeptService();

    /**
     * 获取部门列表
     */
    public void list() {
        logger.debug("Enter into list()");

        Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.findDeptPage(pageNum, pageSize, record);
        renderOkPage(page);
    }

    /**
     * 新增部门
     */
    public void add() {
        logger.debug("Enter into add()");

        try {
            Record record = service.add(getParamsToRecord());
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("新增部门信息失败!", e);
            renderFail(e);
        }

    }

    /**
     * 修改部门信息
     */
    public void chg() {
        logger.debug("Enter into update()");

        try {
            Record record = service.update(getParamsToRecord());
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("修改部门信息失败!", e);
            renderFail(e);
        }
    }

    /**
     * 删除部门信息
     */
    public void del() {
        logger.debug("Enter into delete()");

        try {
            service.delete(getParamsToRecord());
            renderOk(null);
        } catch (BusinessException e) {
            logger.error("删除部门信息失败!", e);
            renderFail(e);
        }
    }

    /**
     * 修改部门状态
     */
    public void setstatus() {
        logger.debug("Enter into delete()");

        try {
            Record record = service.setstatus(getParamsToRecord());
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("修改部门状态失败!", e);
            renderFail(e);
        }
    }
}
