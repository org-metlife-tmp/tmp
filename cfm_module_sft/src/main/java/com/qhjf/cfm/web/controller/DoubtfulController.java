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
import com.qhjf.cfm.web.service.DoubtfulService;

/**
 * 可疑数据管理LA,EBS
 *
 * @author GJF
 * @date 2018年9月18日
 */
public class DoubtfulController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(DoubtfulController.class);
    private DoubtfulService service = new DoubtfulService();

    /**
     * 防重预警列表
     */
    @Auth(hasForces = {"DataAntiDualWaring"})
    public void doubtfullist() {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        UodpInfo uodpInfo = null;
        try {
            uodpInfo = getCurUodp();
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }

        try{
            Page<Record> page = service.doubtfullist(pageNum, pageSize, record, uodpInfo);
            renderOkPage(page);
        }catch(BusinessException e){
            e.printStackTrace();
            renderFail(e);
        }

    }


    /**
     * 拒绝打回，该数据反写到原始表，置状态为已失败
     */
    @Auth(hasForces = {"DataAntiDualWaring"})
    public void reject() {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            service.reject(record,userInfo);
            renderOk(null);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 放行
     */
    @Auth(hasForces = {"DataAntiDualWaring"})
    public void pass() {
        try {
            Record record = getRecordByParamsStrong();
            UserInfo userInfo = getUserInfo();
            service.pass(record,userInfo);
            renderOk(null);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 导出可疑数据
     */
    @Auth(hasForces = {"DataAntiDualWaring"})
    public void listexport() {
        doExport();
    }

}
