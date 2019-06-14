package com.qhjf.cfm.web.controller;

import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.ChannelSettingService;
import com.qhjf.cfm.web.service.VoucherService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 资金系统月末预提凭证操作
 *
 * @author GJF
 * @date 2019年4月10日
 */
public class VoucherController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(VoucherController.class);
    private VoucherService service = new VoucherService();

    /**
     * 列表
     */
    @Auth(hasForces = {"PREVOUCHEROPERATION", "PREVOUCHERCONFIRM", "PREVOUCHERQUERY"})
    public void voucherlist() {
        Record record = getRecordByParamsStrong();
        int pageFlag = record.getInt("page_flag");
        //pageFlag 0:查询所有的记录，1:查询确认页面，2:查询所有复核页面
        List status = record.get("precondition");
        if(pageFlag == 1){
            if (status==null || status.size()==0) {
                record.set("precondition", new int[]{
                        WebConstant.PreSubmitStatus.WYT.getKey(),
                        WebConstant.PreSubmitStatus.YYT.getKey()
                });
            }
        }else if(pageFlag == 2){
            if (status==null || status.size()==0) {
                record.set("precondition", new int[]{
                        WebConstant.PreSubmitStatus.YTFHZ.getKey(),
                        WebConstant.PreSubmitStatus.CXFHZ.getKey()
                });
            }
        }
        record.remove("page_flag");

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        try {
            Page<Record> page = service.voucherlist(pageNum, pageSize, record);
            renderOkPage(page);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }


    /**
     * 根据机构获取用户
     */
    public void getaccbyorg() {
        Record record = getRecordByParamsStrong();
        List<Record> records = service.getaccbyorg(record);
        renderOk(records);
    }

    /**
     * 预提提交
     */
    @Auth(hasForces = {"PREVOUCHEROPERATION"})
    public void presubmit() {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            UodpInfo uodpInfo = null;
            service.presubmit(record, userInfo, uodpInfo);
            renderOk(null);

        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 预提提交确认
     */
    @Auth(hasForces = {"PREVOUCHERCONFIRM"})
    public void presubmitconfirm() {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            UodpInfo uodpInfo = null;
            service.presubmitconfirm(record, userInfo, uodpInfo);
            renderOk(null);

        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 撤销提交
     */
    @Auth(hasForces = {"PREVOUCHEROPERATION"})
    public void precancel() {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            UodpInfo uodpInfo = null;
            service.precancel(record, userInfo, uodpInfo);
            renderOk(null);

        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 撤销提交确认
     */
    @Auth(hasForces = {"PREVOUCHERCONFIRM"})
    public void precancelconfirm() {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            UodpInfo uodpInfo = null;
            service.precancelconfirm(record, userInfo, uodpInfo);
            renderOk(null);

        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 导出业务明细
     */
    @Auth(hasForces = {"PREVOUCHEROPERATION", "PREVOUCHERCONFIRM", "PREVOUCHERQUERY"})
    public void tradxport() {
        doExport();
    }

    /**
     * 导出财务账
     */
    @Auth(hasForces = {"PREVOUCHERQUERY"})
    public void voucherexport() {
        doExport();
    }

}
