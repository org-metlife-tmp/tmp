package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import com.qhjf.cfm.web.service.CollectBatchCheckService;
import com.qhjf.cfm.web.service.CollectCheckService;

import java.util.List;

/**
 * @  批量_交易核对  涉及表  collect_batch_bus_attach_detail   collect_batch_trans_checked  acc_his_transaction
 *
 */
public class CollectBatchCheckController  extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(CollectBatchCheckController.class);

    private CollectBatchCheckService service = new CollectBatchCheckService();
    private CollectCheckService collectService = new CollectCheckService();

    /**
     * @ 归集通批量交易核对_单据列表
     */
    @Auth(hasForces = {"GJCheck"})
    public void checkbillList() throws Exception {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        UodpInfo uodpInfo = getCurUodp();
        record.set("org_id", uodpInfo.getOrg_id());
        //状态为已成功的单据
        AccCommonService.setInnerBatchTradStatus(record, "service_status");
        Page<Record> page = service.checkbillList(pageNum, pageSize, record);
        renderOkPage(page);
    }


    /**
     * @throws ReqDataException
     * @归集通交易核对_未核对交易列表
     */
    @Auth(hasForces = {"GJCheck"})
    public void checkNoCheckTradeList() throws ReqDataException {
        Record record = getRecordByParamsStrong();
        List<Record> page = service.checkTradeList(record);
        renderOk(page);
    }

    /**
     * @throws ReqDataException
     * @归集通交易核对_已核对交易列表
     */
    @Auth(hasForces = {"GJCheck"})
    public void checkAlreadyTradeList() throws ReqDataException {
        Record record = getRecordByParamsStrong();
        List<Record> page = service.checkAlreadyTradeList(record);
        renderOk(page);
    }

    /**
     * @ 核对
     */
    @Auth(hasForces = {"GJCheck"})
    public void confirmCheck() throws Exception {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            Page<Record> page = service.confirm(record, userInfo);
            renderOkPage(page);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }
}
