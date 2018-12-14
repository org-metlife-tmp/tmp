package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.JSONArray;
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
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.NonDirectConnectionService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NonDirectSettingController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(NonDirectSettingController.class);

    private NonDirectConnectionService service = new NonDirectConnectionService();

    /**
     * 只处理上传环节使用
     */
    @Auth(hasForces = {"GJBatchBill"})
    public void addbillexcel() {
        try {
            Record record = getParamsToRecord();
            Record result = service.addbillexcel(record, getUserInfo().getUsr_id(), getCurUodp().getOrg_id());
            renderOk(result);
        } catch (BusinessException e) {
            log.error("新增非直连归集批量文件失败！", e);
            renderFail(e);
        }
    }

    /**
     * 标记删除数据-只处理上传环节使用
     */
    @Auth(hasForces = {"GJBatchBill", "GJBatchView"})
    public void delbillexcel() {
        try {
            Record record = getParamsToRecord();
            Record result = service.delbillexcel(record);
            renderOk(result);
        } catch (BusinessException e) {
            log.error("删除非直连归集批量文件失败！", e);
            renderFail(e);
        }
    }

    /**
     * 新增单据
     */
    @Auth(hasForces = {"GJBatchBill", "GJBatchView"})
    public void addbill() {
        try {
            Record record = getParamsToRecord();
            Record result = service.addbill(record, getUserInfo().getUsr_id(), getCurUodp().getOrg_id());
            renderOk(result);
        } catch (BusinessException e) {
            log.error("新增非直联批量支付单据失败！", e);
            renderFail(e);
        }
    }

    /**
     * 删除单据
     */
    @Auth(hasForces = {"GJBatchView", "GJBatchBill"})
    public void delbill() {
        try {
            Record record = getParamsToRecord();
            service.delbill(record, getUserInfo());
            renderOk(null);
        } catch (BusinessException e) {
            log.error("删除非直联批量单据失败！", e);
            renderFail(e);
        }
    }

    /**
     * 修改单据
     */
    @Auth(hasForces = {"GJBatchView", "GJBatchBill"})
    public void chgbill() {
        try {
            Record record = getParamsToRecord();
            Record result = service.chgbill(record, getUserInfo());
            renderOk(result);
        } catch (BusinessException e) {
            log.error("修改非直联批量单据失败！", e);
            renderFail(e);
        }
    }

    /**
     * 获取当前机构所有账户列表信息
     */
    @Auth(hasForces = {"GJBatchView", "GJBatchBill"})
    public void accs() {
        try {
            renderOk(service.accs(getCurUodp().getOrg_id()));
        } catch (BusinessException e) {
            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 预处理修改单据,点击修改按钮调用
     */
    @Auth(hasForces = {"ZFTBatchView", "ZFTBatchBill"})
    public void prechgbill() {
        try {
            Record record = getParamsToRecord();
            Record result = service.prechgbill(record, getUserInfo());
            renderOk(result);
        } catch (BusinessException e) {
            log.error("预处理修改非直联归集批量单据失败！", e);
            renderFail(e);
        }
    }

    /**
     * 指定附件详细列表
     */
    @Auth(hasForces = {"GJBatchView", "GJBatchBill"})
    public void batchbillattlist() {
        Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.batchBillAttachPage(pageNum, pageSize, record);
        Record totalRec = service.batchBillAttachTotal(record);
        renderOkPage(page, totalRec);
    }


    @Override
    protected Record saveOrChg() throws BusinessException {
        Record record = getParamsToRecord();
        if (record.get("id") != null && !"".equals(record.get("id"))) {
            record = service.chgbill(record, getUserInfo());
        } else {
            record.remove("id");
            record = service.addbill(record, getUserInfo().getUsr_id(), getCurUodp().getOrg_id());
        }
        return record;
    }

    protected WfRequestObj genWfRequestObj() throws BusinessException {
        final Record record = getParamsToRecord();
        return new WfRequestObj(WebConstant.MajorBizType.CBB, "collect_batch_baseinfo", record) {
            @Override
            public <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException {
                Record bill_info = getBillRecord();
                if (type.equals(WebConstant.WfExpressType.AMOUNT)) {
                    return bill_info.get("total_amount");
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
                //审批完结 是否需要清空 临时表数据。TODO
                return service.hookPass(record);
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
                        add(new WfRequestObj(WebConstant.MajorBizType.CBB, "collect_batch_baseinfo", rec) {
                            @Override
                            public <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException {
                                Record bill_info = getBillRecord();
                                if (type.equals(WebConstant.WfExpressType.AMOUNT)) {
                                    return bill_info.get("total_amount");
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
                                //审批完结 是否需要清空 临时表数据。TODO
                                return service.hookPass(rec);
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
        return Db.getSqlPara("collect_ndc.findCBBPendingList", Kv.by("map", kv));
    }

    @Override
    protected List<Record> displayPossibleWf(Record record) throws BusinessException {
        return CommonService.displayPossibleWf(WebConstant.MajorBizType.CBB.getKey(), getCurUodp().getOrg_id(), null);
    }

    /**
     * 单据详情
     */
    @Auth(hasForces = {"GJBatchView", "GJBatchBill", "MyWFPLAT"})
    public void detail() {
        try {
            renderOk(service.billdetail(getParamsToRecord(), getUserInfo()));
        } catch (BusinessException e) {
            renderFail(e);
        }
    }
    //TODO

    /**
     * 批量支付-查看
     */
    @Auth(hasForces = {"GJBatchView", "GJBatchBill"})
    public void batchlist() {
        try {
            Record record = getParamsToRecord();
            int page_num = getPageNum(record);
            int page_size = getPageSize(record);

            UodpInfo uodpInfo = getCurUodp();
            //根据机构id查询机构信息
            Record orgRec = Db.findById("organization", "org_id", uodpInfo.getOrg_id());

            record.set("level_code", orgRec.get("level_code"));
            record.set("level_num", orgRec.get("level_num"));

            record.set("pay_status",
                    new int[]{
                            WebConstant.PayStatus.SUCCESS.getKey(),
                            WebConstant.PayStatus.FAILD.getKey(),
                            WebConstant.PayStatus.HANDLE.getKey(),
                            WebConstant.PayStatus.CANCEL.getKey(),
                            WebConstant.PayStatus.INIT.getKey()
                    });
            if (record.get("service_status") == null || ((List) (record.get("service_status"))).size() == 0) {
                JSONArray jsonArray = new JSONArray();
                jsonArray.addAll(Arrays.asList(
                        WebConstant.BillStatus.SUBMITED.getKey(),
                        WebConstant.BillStatus.AUDITING.getKey(),
                        WebConstant.BillStatus.PASS.getKey(),
                        WebConstant.BillStatus.PROCESSING.getKey(),
                        WebConstant.BillStatus.SUCCESS.getKey(),
                        WebConstant.BillStatus.FAILED.getKey(),
                        WebConstant.BillStatus.CANCEL.getKey(),
                        WebConstant.BillStatus.NOCOMPLETION.getKey(),
                        WebConstant.BillStatus.COMPLETION.getKey(),
                        WebConstant.BillStatus.WAITPROCESS.getKey()
                ));
                record.set("service_status", jsonArray);
            }
            Page<Record> page = service.morebill(page_num, page_size, record);
            Record ext = service.morebillsum(record);
            renderOkPage(page, ext);
        } catch (BusinessException e) {
            renderFail(e);
        }
    }

    /**
     * 更多单据
     */
    @Auth(hasForces = {"GJBatchView", "GJBatchBill"})
    public void morebill() {
        Record record = getParamsToRecord();
        int page_num = getPageNum(record);
        int page_size = getPageSize(record);

        record.set("create_by", getUserInfo().getUsr_id());
        record.set("pay_status", new int[]{WebConstant.PayStatus.SUCCESS.getKey()});
        Page<Record> page = service.morebill(page_num, page_size, record);
        Record ext = service.morebillsum(record);
        renderOkPage(page, ext);
    }

    /**
     * 获取excel详细信息
     */
    @Auth(hasForces = {"GJBatchView", "GJBatchBill"})
    public void attclist() {
        Record record = getParamsToRecord();
        int page_num = getPageNum(record);
        int page_size = getPageSize(record);
        Page<Record> page = service.attclist(page_num, page_size, record);
        Record ext = service.getCollect(record);
        renderOkPage(page, ext);
    }

    @Auth(hasForces = {"GJBatchView", "GJBatchBill", "MyWFPLAT"})
    public void billdetaillist() {
        Record record = getParamsToRecord();
        int page_num = getPageNum(record);
        int page_size = getPageSize(record);
        Page<Record> page = service.billdetaillist(page_num, page_size, record);
        Record ext = service.billdetailsum(record);
        renderOkPage(page, ext);
    }
}
