package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.BankkeySettingService;

import java.util.List;

/**
 * bankkey设置
 *
 * @author GJF
 * @date 2018年9月18日
 */
public class BankkeySettingController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(BankkeySettingController.class);
    private BankkeySettingService service = new BankkeySettingService();

    /**
     * 查询bankkey设置列表
     */
    @Auth(hasForces = {"BankkeySet"})
    public void bankkeylist() {
        Record record = getRecordByParamsStrong();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.bankkeylist(pageNum, pageSize, record);
        renderOkPage(page);
    }


    /**
     * 新增bankkey
     */
    @Auth(hasForces = {"BankkeySet"})
    public void addbankkey() {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            record = service.addbankkey(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 修改bankkey
     */
    @Auth(hasForces = {"BankkeySet"})
    public void chgbankkey() {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            record = service.chgbankkey(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 查看bankkey详情
     */
    @Auth(hasForces = {"BankkeySet"})
    public void bankkeydetail() {
        try {
            Record record = getRecordByParamsStrong();
            record = service.bankkeydetail(record);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 获取所有机构
     */
    public void getorg() {
        try {
            Record record = getRecordByParamsStrong();
            List<Record> records = service.getorg(record);
            renderOk(records);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 获取通道根据收付属性
     */
    public void getchanbypaymode() {
        try {
            Record record = getRecordByParamsStrong();
            List<Record> records = service.getchanbypaymode(record);
            renderOk(records);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 导出BANKKEY
     */
    public void listexport() {
        doExport();
    }

}
