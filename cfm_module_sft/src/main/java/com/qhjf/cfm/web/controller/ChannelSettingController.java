package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.ChannelSettingService;

import java.util.List;
import java.util.Map;

/**
 * 通道设置
 *
 * @author GJF
 * @date 2018年9月18日
 */
public class ChannelSettingController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(ChannelSettingController.class);
    private ChannelSettingService service = new ChannelSettingService();

    /**
     * 查询通道设置列表
     */
    @Auth(hasForces = {"ChannelSet"})
    public void channellist() {
        Record record = getRecordByParamsStrong();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.channellist(pageNum, pageSize, record);
        renderOkPage(page);
    }


    /**
     * 新增通道
     */
    @Auth(hasForces = {"ChannelSet"})
    public void addchannel() {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            record = service.add(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 修改通道
     */
    @Auth(hasForces = {"ChannelSet"})
    public void chgchannel() {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            record = service.chgchannel(record, userInfo);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 查看通道详情
     */
    @Auth(hasForces = {"ChannelSet"})
    public void channeldetail() {
        try {
            Record record = getRecordByParamsStrong();
            record = service.channeldetail(record);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 获取直联通道
     */
    @Auth(hasForces = {"ChannelSet"})
    public void getdirectchannel() {
        try {
            Record record = getRecordByParamsStrong();
            Map<String, String> returnMap = service.getdirectchannel(record);
            renderOk(returnMap);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 获取所有通道
     */
    public void getallchannel() {
        try {
            Record record = getRecordByParamsStrong();
            List<Record> records = service.getallchannel(record);
            renderOk(records);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 获取所有bankcode
     */
    public void getallbankcode() {
        List<Record> records = service.getallbankcode();
        renderOk(records);
    }

    /**
     * 根据acc_no获取bankcode
     */
    public void getbankcodebyacc() {
        Record record = getRecordByParamsStrong();
        List<Record> records = null;
        try {
            record = service.getbankcodebyacc(record);
        } catch (BusinessException e) {
            e.printStackTrace();
        }
        renderOk(record);
    }
    /**
     * 根据收付属性获取报盘模板
     */
    public void getdoucument() {
        try {
            Record record = getRecordByParamsStrong();
            List<Record> records = service.getdoucument(record);
            renderOk(records);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 导出通道
     */
    public void listexport() {
        doExport();
    }

}
