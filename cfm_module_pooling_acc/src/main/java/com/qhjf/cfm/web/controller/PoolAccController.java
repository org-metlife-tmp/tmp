package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.PoolAccService;

import java.util.List;

/**
 * 调拨通核对
 *
 * @author GJF
 */
@SuppressWarnings("unused")
public class PoolAccController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(PoolAccController.class);
    private PoolAccService service = new PoolAccService();


    /**
     * 资金池账户列表
     */
    @Auth(hasForces = {"ZJXBApSetting"})
    public void acclist() {
        Record record = getRecordByParamsStrong();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.acclist(pageNum, pageSize, record);

        renderOkPage(page);
    }

    /**
     * 新增一条账户信息
     */
    @Auth(hasForces = {"ZJXBApSetting"})
    public void add() {
        Record record = getRecordByParamsStrong();
        try {
            Record result = service.add(record);
            renderOk(record);
        } catch (BusinessException e) {
            logger.error("添加资金池账户信息失败!", e);
            renderFail(e);
        }
    }

    /**
     * 删除一条账户信息
     */
    @Auth(hasForces = {"ZJXBApSetting"})
    public void delete() {
        Record record = getRecordByParamsStrong();
        try {
            service.delete(record);
            renderOk(null);
        } catch (BusinessException e) {
            logger.error("删除资金池账户信息失败!", e);
            renderFail(e);
        }
    }

    /**
     * 设为默认
     */
    @Auth(hasForces = {"ZJXBApSetting"})
    public void defaultset() {
        Record record = getRecordByParamsStrong();
        try {
            service.defaultset(record);
            renderOk(null);
        } catch (BusinessException e) {
            logger.error("设置默认资金账户失败!", e);
            renderFail(e);
        }
    }

    /**
     * 根据银行大类id查询账户
     */
    @Auth(hasForces = {"ZJXBApSetting"})
    public void getaccbybank() {
        Record record = getRecordByParamsStrong();
        try {
            List<Record> resultList = service.getaccbybank(record);
            renderOk(resultList);
        } catch (BusinessException e) {
            logger.error("查询账户失败!", e);
            renderFail(e);
        }
    }

    /**
     * 查询资金池账户设置默认帐号信息
     */
    @Auth(hasForces = {"ZJXBApSetting", "OABranchPay", "OACheck", "OACheckDoubtful", "OAHeadPay"})
    public void getpoolaccinfo() {
        Record record = getRecordByParamsStrong();
        List<Record> list = service.getpoolaccinfo(record);
        renderOk(list);
    }


    /**
     * 总公司资金池账户选取默认账户
     */
    @Auth(hasForces = {"ZJXBApSetting"})
    public void getDefaultpoolacc() {
        Record record = getRecordByParamsStrong();
        if (null == record) {
            record = new Record();
        }
        List<Record> list = service.getDefaultpoolacc(record);
        renderOk(list);
    }

}
