package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.ChannelSettingService;
import com.qhjf.cfm.web.service.VoucherService;

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
    public void voucherlist() {
        Record record = getRecordByParamsStrong();

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
     * 对账确认
     */
    public void confirm() {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            service.confirm(record, userInfo);
            renderOk(null);

        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 导出业务明细
     */
    public void tradxport() {
        doExport();
    }

    /**
     * 导出财务账
     */
    public void voucherexport() {
        doExport();
    }

}
