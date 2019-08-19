package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.HeadOrgNcService;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class HeadOrgNcController extends CFMBaseController {
    private static final Log log = LogbackLog.getLog(HeadOrgNcController.class);

    private HeadOrgNcService service = new HeadOrgNcService();

    public void todolist() {
        try {
            Record record = getParamsToRecord();
            int page_num = getPageNum(record);
            int page_size = getPageSize(record);
            String service_status_origin = TypeUtils.castToString(record.get("service_status"));
            if(StringUtils.isBlank(service_status_origin)||service_status_origin.equals("[]")) {
                record.remove("service_status");
            }
            record.set("org_id", getCurUodp().getOrg_id());
            Page<Record> page = service.getTodoPage(page_num, page_size, record);
            renderOkPage(page);
        } catch (BusinessException e) {
            log.error("获取总公司付款单据待处理列表失败！", e);
            renderFail(e);
        }
    }
    public void submit() {
        try {
            Record record = getParamsToRecord();
            if (record.get("id") != null && !"".equals(record.get("id"))) {
                boolean flag=service.pass(record);
                if (flag){
                    renderOk("银行处理该订单中！");
                }else{
                    renderOk("发送银行失败！");
                }
            } else {
                throw new ReqDataException("请求数据错误！");
            }
        } catch (BusinessException e) {
            log.error("newcomp总公司支付失败！", e);
            renderFail(e);
        }
    }



    /**
     * OA总公司付款未处理导出
     */
    public void todolistexport() {
        doExport();
    }

    /**
     * OA总公司付款已处理导出
     */
    public void donelistexport() {
        doExport();
    }
}
