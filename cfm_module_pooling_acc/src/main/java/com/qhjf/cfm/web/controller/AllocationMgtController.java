package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.AllocationMgtService;

import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/19
 * @Description: 资金下拨管理
 */
@SuppressWarnings("unused")
public class AllocationMgtController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(AllocationMgtController.class);
    private AllocationMgtService service = new AllocationMgtService();

    /**
     * 下拨管理列表
     */
    @Auth(hasForces = {"ZJXBMgr"})
    public void list() {
        Record record = getRecordByParamsStrong();
        try {
            UserInfo userInfo = getUserInfo();
            UodpInfo uodpInfo = getCurUodp();
            AccCommonService.setAllocationMgtListStatus(record, "service_status");

            List<Record> resultList = service.mgtlist(record, userInfo, uodpInfo);
            renderOk(resultList);
        } catch (BusinessException e) {
            logger.error("查询下拨管理列表!", e);
            renderFail(e);
        }
    }

    @Auth(hasForces = {"ZJXBMgr"})
    public void setstatus() {
        Record record = getRecordByParamsStrong();
        try {
            record = service.setisactivity(record);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    @Auth(hasForces = {"ZJXBMgr"})
    public void cancel() {
        Record record = getRecordByParamsStrong();
        try {
            record = service.cancel(record);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }
}
