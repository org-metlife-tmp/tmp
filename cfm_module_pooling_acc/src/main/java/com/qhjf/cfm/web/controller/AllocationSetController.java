package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
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
import com.qhjf.cfm.web.service.AllocationSetService;

import java.util.ArrayList;
import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/17
 * @Description: 自动下拨设置
 */
@SuppressWarnings("unused")
public class AllocationSetController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(AllocationSetController.class);
    private AllocationSetService service = new AllocationSetService();

    /**
     * 下拨主账户
     */
    @Auth(hasForces = {"ZJXBSetting"})
    public void mainacclist() {
        List<Record> list = null;
        try {
            Record record = getRecordByParamsStrong();
            UodpInfo uodpInfo = getCurUodp();

            list = service.mainacclist(record, uodpInfo);
            renderOk(list);
        } catch (ReqDataException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 下拨子账户
     */
    @Auth(hasForces = {"ZJXBSetting"})
    public void childacclist() {
        List<Record> list = null;
        try {
            Record record = getRecordByParamsStrong();
            UodpInfo uodpInfo = getCurUodp();
            list = service.childacclist(record, uodpInfo);
            renderOk(list);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 保存下拨设置
     */
    @Auth(hasForces = {"ZJXBSetting"})
    public void add() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        UodpInfo uodpInfo = null;
        try {
            uodpInfo = getCurUodp();
            record = service.add(record, userInfo, uodpInfo);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 修改下拨设置
     */
    @Auth(hasForces = {"ZJXBSetting"})
    public void chg() {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        UodpInfo uodpInfo = null;
        try {
            uodpInfo = getCurUodp();
            record = service.chg(record, userInfo, uodpInfo);
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 查看下拨设置
     */
    @Auth(hasForces = {"ZJXBSetting", "MyWFPLAT"})
    public void detail() {
        Record record = getRecordByParamsStrong();
        try {
            record = service.detail(record, getUserInfo());
            renderOk(record);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 删除下拨设置
     */
    @Auth(hasForces = {"ZJXBSetting"})
    public void del() {
        Record record = getRecordByParamsStrong();
        try {
            service.del(record, getUserInfo());
            renderOk(null);
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 更多下拨设置列表
     */
    @Auth(hasForces = {"ZJXBSetting"})
    public void morelist() {
        Page<Record> page = null;
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();

        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        AccCommonService.setAllocationMoreListStatus(record, "service_status");

        page = service.morelist(pageNum, pageSize, record, userInfo);
        renderOkPage(page);
    }

    @Override
    protected WfRequestObj genWfRequestObj() throws BusinessException {
        final Record record = getRecordByParamsStrong();
        return new WfRequestObj(WebConstant.MajorBizType.ALLOCATION, "allocation_topic", record) {
            @Override
            public <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException {
                Record bill_info = getBillRecord();
                if (type.equals(WebConstant.WfExpressType.AMOUNT)) {
                    return bill_info.get("allocation_amount");
                } else if (type.equals(WebConstant.WfExpressType.STATUS)) {
                    return bill_info.get("service_status");
                } else {
                    throw new WorkflowException("类型不支持");
                }
            }

            @Override
            public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
                return getSqlPara(inst_id, exclude_inst_id, record);
            }

            @Override
            protected boolean hookPass() {
                //插入定时任务表cfm_quartz
                return service.pass(record);
            }
        };
    }

    @Override
    protected List<WfRequestObj> genBatchWfRequestObjs() throws BusinessException {
        Record record = getParamsToRecordStrong();
        final List<Record> wfRequestObjs = record.get("batch_list");

        if (wfRequestObjs != null && wfRequestObjs.size() > 0) {

            return new ArrayList<WfRequestObj>() {
                {
                    for (final Record rec : wfRequestObjs) {
                        add(new WfRequestObj(WebConstant.MajorBizType.ALLOCATION, "allocation_topic", rec) {
                            @Override
                            public <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException {
                                Record bill_info = getBillRecord();
                                if (type.equals(WebConstant.WfExpressType.AMOUNT)) {
                                    return bill_info.get("allocation_amount");
                                } else if (type.equals(WebConstant.WfExpressType.STATUS)) {
                                    return bill_info.get("service_status");
                                } else {
                                    throw new WorkflowException("类型不支持");
                                }
                            }

                            @Override
                            public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
                                return getSqlPara(inst_id, exclude_inst_id, rec);
                            }

                            @Override
                            protected boolean hookPass() {
                                //插入定时任务表cfm_quartz
                                return service.pass(rec);
                            }
                        });
                    }
                }
            };

        }

        throw new WorkflowException("没有可操作的单据!");
    }

    private SqlPara getSqlPara(Long[] inst_id, Long[] exclude_inst_id, Record rec) {
        final Kv kv = Kv.create();
        if (inst_id != null) {
            kv.set("in", new Record().set("instIds", inst_id));
        }
        if (exclude_inst_id != null) {
            kv.set("notin", new Record().set("excludeInstIds", exclude_inst_id));
        }
        kv.set("biz_type", rec.get("biz_type"));
        return Db.getSqlPara("allocation.findAllocationPendingList", Kv.by("map", kv));
    }

    @Override
    protected Record saveOrChg() throws BusinessException {
        Record record = getRecordByParamsStrong();
        UserInfo userInfo = getUserInfo();
        UodpInfo uodp = getCurUodp();
        if (record.get("id") != null && !"".equals(record.get("id"))) {
            return service.chg(record, userInfo, uodp);
        } else {
            record.remove("id");
            return service.add(record, userInfo, uodp);
        }
    }

    @Override
    protected List<Record> displayPossibleWf(Record record) throws BusinessException {
        //根据单据id查询是否有绑定到子业务类型
        String biz_id = null;
        Record innerRec = Db.findById("allocation_topic", "id", TypeUtils.castToLong(record.get("id")));
        if (innerRec != null && record.get("biz_id") != null) {
            biz_id = TypeUtils.castToString(record.get("biz_id"));
        }
        return CommonService.displayPossibleWf(WebConstant.MajorBizType.ALLOCATION.getKey(), getCurUodp().getOrg_id(), biz_id);
    }
}
