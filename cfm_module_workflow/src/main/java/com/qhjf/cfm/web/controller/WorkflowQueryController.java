package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
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
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.constant.WebConstant.BillStatus;
import com.qhjf.cfm.web.constant.WebConstant.COMMONUser;
import com.qhjf.cfm.web.constant.WebConstant.MajorBizType;
import com.qhjf.cfm.web.constant.WebConstant.WfExpressType;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.WorkflowQueryService;

import java.util.ArrayList;
import java.util.List;


/**
 * @Auther: zhangyuan
 * @Date: 2018/7/23 14:52
 * @Description:
 */
public class WorkflowQueryController extends CFMBaseController {
    private final static Log logger = LogbackLog.getLog(WorkflowQueryController.class);

    private WorkflowQueryService service = new WorkflowQueryService();


    /**
     * 审批平台 - 我的待办列表(全部业务类型)
     */
    @Auth(hasForces = {"MyWFPLAT"})
    public void pendingtasksall() {
        UserInfo userInfo = this.getUserInfo();
        Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.getPendingApproveByUser(userInfo, pageNum, pageSize);
        for (Record list : page.getList()) {
            try {
                long billId = TypeUtils.castToLong(list.get("bill_id"));
                int bizType = TypeUtils.castToInt(list.get("biz_type"));

                List<Record> records = service.getCurrApproveUser(billId, bizType);
                list.set("nextUserList", records);

                Kv kv = Kv.create();
                kv.set("bill_id", billId);
                kv.set("biz_type", bizType);
                List<Record> hisRec = Db.find(Db.getSqlPara("query.findhisexecuteinstList", Kv.by("map", kv)));
                list.set("history", hisRec);
            } catch (WorkflowException e) {
                renderFail(e);
                return;
            }
        }
        renderOkPage(page);
    }


    /**
     * 审批平台 - 我的待办列表统计（全部）
     */
    public void pendingtaskallnum() {
        UserInfo userInfo = getUserInfo();

        Long[] inst_ids = service.getInstIdsWithUser(userInfo);      //符合条件的实例ID
        Long[] exclude_inst_ids = service.getExcludeInstIdsWithUser(userInfo); //需要排除实例ID

        Kv kv = Kv.create();
        if (inst_ids != null) {
            kv.set("in", new Record().set("instIds", inst_ids));
        }
        if (exclude_inst_ids != null) {
            kv.set("notin", new Record().set("excludeInstIds", exclude_inst_ids));
        }

        Record finalRec = new Record();
        int totalNum = 0;
        List<Record> recordList = new ArrayList<>();
        if (inst_ids != null) {

            SqlPara sqlPara = Db.getSqlPara("query.findPendingTaskAllNum", Kv.by("map", kv));
            recordList = Db.find(sqlPara);

            for (Record record : recordList) {
                totalNum += TypeUtils.castToInt(record.get("num"));
            }

        }
        finalRec.set("total_num", totalNum);
        finalRec.set("pending_list", recordList);

        renderOk(finalRec);
    }


    /**
     * 审批平台 - 我的已办列表统计（全部）
     */
    @Auth(hasForces = {"MyWFPLAT"})
    public void processedtaskallnum() {
        UserInfo userInfo = getUserInfo();

        Kv kv = Kv.create();
        kv.set("usr_id", userInfo.getUsr_id());

        Record finalRec = new Record();
        int totalNum = 0;
        List<Record> recordList = new ArrayList<>();

        SqlPara sqlPara = Db.getSqlPara("query.findProcessTaskAllNum", Kv.by("map", kv));
        recordList = Db.find(sqlPara);

        if (recordList != null && recordList.size() > 0) {
            for (Record record : recordList) {
                totalNum += TypeUtils.castToInt(record.get("num"));
            }
        }

        finalRec.set("total_num", totalNum);
        finalRec.set("pending_list", recordList);

        renderOk(finalRec);
    }


    /**
     * 审批平台 - 指定业务类型待办列表（根据业务类型区分）
     */
    @Auth(hasForces = {"MyWFPLAT"})
    public void pendingtasks() {
        WfRequestObj wfobj = this.getAttr("_wfobj");
        if (wfobj == null) {
            renderFail(new WorkflowException("未指定具体的业务类型"));
        }
        UserInfo userInfo = this.getUserInfo();
        Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        Page<Record> page = service.getPendingApproveByUser(wfobj, userInfo, pageNum, pageSize);


        for (Record feature : page.getList()) {
            long billId = TypeUtils.castToLong(feature.get("bill_id"));
            int bizType = TypeUtils.castToInt(feature.get("biz_type"));

            if(WebConstant.MajorBizType.GMF.getKey() == bizType || WebConstant.MajorBizType.GMS.getKey()==bizType) {
            	//柜面付收款账号需要解密,再变成掩码                            //审批平台柜面收撤销收款账号变为明文
            	try {
            		SymmetricEncryptUtil  util = SymmetricEncryptUtil.getInstance();
            		util.recvmaskforSingle(feature);
            	} catch (Exception e) {			
            		e.printStackTrace();
            		logger.error("===解密修改为掩码失败");
            	}            	
            }
            if(WebConstant.MajorBizType.GMS.getKey()==bizType){
                if(TypeUtils.castToString(feature.get("bill_type")).equals("0")){
                    String recvdate = TypeUtils.castToString(feature.get("recv_date"));
                    if (null != recvdate) {
                        recvdate = recvdate.substring(0, 9);
                        feature.set("recv_date", recvdate);
                    }
                    if (null != TypeUtils.castToString(feature.get("recv_mode"))) {
                        if (TypeUtils.castToString(feature.get("recv_mode")).equals("0") || TypeUtils.castToString(feature.get("recv_mode"))=="0") {
                            feature.set("recv_mode", "POS机");
                        }else if(TypeUtils.castToString(feature.get("recv_mode")).equals("1") || TypeUtils.castToString(feature.get("recv_mode"))=="1"){
                            feature.set("recv_mode", "现金解款单");
                        }else if(TypeUtils.castToString(feature.get("recv_mode")).equals("2") || TypeUtils.castToString(feature.get("recv_mode"))=="2"){
                            feature.set("recv_mode", "支票");
                        }else if(TypeUtils.castToString(feature.get("recv_mode")).equals("3") || TypeUtils.castToString(feature.get("recv_mode"))=="3"){
                            feature.set("recv_mode", "网银/汇款");
                        }
                    }
                    if (null != TypeUtils.castToString(feature.get("use_funds"))) {
                        if (TypeUtils.castToString(feature.get("use_funds")) == "6" || TypeUtils.castToString(feature.get("use_funds")).equals("6")) {
                            feature.set("use_funds", "保单暂记");
                        }else if(TypeUtils.castToString(feature.get("use_funds")) == "7" || TypeUtils.castToString(feature.get("use_funds")).equals("7")){
                            feature.set("use_funds", "追加投资悬账");
                        }
                    }
                }
            }
            List<Record> records = null;
            try {
                records = service.getCurrApproveUser(billId, bizType);
                feature.set("nextUserList", records);
            } catch (WorkflowException e) {
                renderFail(e);
                return;
            }
        }
        renderOkPage(page);
    }

    /**
     * 审批平台 - 我的已办列表 (全部业务类型)
     */
    @Auth(hasForces = {"MyWFPLAT"})
    public void processtasksall() {
        UserInfo userInfo = this.getUserInfo();
        Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        try {
            Page<Record> page = service.getProcessedApproveByUser(record, userInfo, pageNum, pageSize);
            //查询单据状态
            for (Record rec : page.getList()) {
                List<Record> records = new ArrayList<>();
                long billId = TypeUtils.castToLong(rec.get("bill_id"));
                int bizType = TypeUtils.castToInt(rec.get("biz_type"));


                MajorBizType majorBizType = MajorBizType.getBizType(bizType);
                String tableName = null;

                tableName = majorBizType.getTableName();


                Record billRec = Db.findById(tableName, "id", billId);
                if (billRec == null) {
                    continue;
                }
                int status = TypeUtils.castToInt(billRec.get("service_status"));

                /**
                 *  如果是申请回退的业务类型，单独处理
                 *  如果批次状态为已回退，则更新为审批通过
                 *  如果批次状态为已发送未回盘，则更新为拒绝
                 */
                if (bizType == MajorBizType.PLF_EXCEPT_BACK.getKey()) {
                    if (status == WebConstant.SftCheckBatchStatus.YHT.getKey()) {
                        status = BillStatus.PASS.getKey();
                    } else if (status == WebConstant.SftCheckBatchStatus.FSWHP.getKey()) {
                        status = BillStatus.REJECT.getKey();
                    }
                }
                rec.set("status", status);

                if (status == BillStatus.AUDITING.getKey()) {
                    //代办人
                    records = service.getCurrApproveUser(billId, bizType);
                }
                rec.set("current", records);

            }
            renderOkPage(page);
        } catch (WorkflowException e) {
            renderFail(e);
        }
    }


    /**
     * 审批平台 - 我的已办列表 (指定业务类型)
     */
    @Auth(hasForces = {"MyWFPLAT"})
    public void processedtasks() {
        WfRequestObj wfobj = this.getAttr("_wfobj");
        if (wfobj == null) {
            renderFail(new WorkflowException("未指定具体的业务类型"));
        }
        UserInfo userInfo = this.getUserInfo();
        Record record = getParamsToRecord();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);

        try {
            Page<Record> page = service.getProcessedApproveByUser(wfobj, userInfo, pageNum, pageSize);
            //查询单据状态
            for (Record rec : page.getList()) {
                List<Record> records = new ArrayList<>();
                long billId = TypeUtils.castToLong(rec.get("bill_id"));

                if (rec.get("service_status") != null) {
                    int status = TypeUtils.castToInt(rec.get("service_status"));
                    rec.set("status", status);
                    if (status == BillStatus.AUDITING.getKey()) {
                        //待办人
                        records = service.getCurrApproveUser(billId, wfobj.getBizType().getKey());
                    }
                    rec.set("current", records);
                }
            }
            renderOkPage(page);
        } catch (WorkflowException e) {
            renderFail(e);
        }
    }


    /**
     * 审批详情
     */
    public void approvedetail() {
        Record record = getParamsToRecord();

        WfRequestObj wfobj;
        try {
            long billId = TypeUtils.castToLong(record.get("id"));
            int bizType = TypeUtils.castToInt(record.get("biz_type"));
            MajorBizType majorBizType = MajorBizType.getBizType(bizType);

            wfobj = new WfRequestObj(majorBizType, majorBizType.getTableName(), record) {
                @Override
                public <T> T getFieldValue(WfExpressType type) throws WorkflowException {
                    return null;
                }

                @Override
                public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
                    return null;
                }
            };

            Kv kv = Kv.create();
            kv.set("bill_id", billId);
            kv.set("biz_type", bizType);
            Record billRecord = Db.findById(wfobj.getTableName(), "id", wfobj.getBillId()); //单据详情
            if (billRecord != null) {
                //查询当前实例的初始提交人
                List<Record> submitRec = Db.find(Db.getSqlPara("query.findrunexecuteinstList", Kv.by("map", kv)));
                if (submitRec.size() == 0) {

                    String tableName = majorBizType.getTableName();
                    Record subRec = Db.findById(tableName, "id", billId);
                    Long userId = TypeUtils.castToLong(subRec.get("create_by"));
                    String userName = null;
                    if (userId < 0) {
                        userName = COMMONUser.JZUser.getName();
                    } else {
                        Record userRec = Db.findById("user_info", "usr_id", TypeUtils.castToLong(subRec.get("create_by")));
                        userName = userRec.get("name");
                    }

                    submitRec.add(new Record().set("init_user_name", userName).set("start_time", TypeUtils.castToDate(subRec.get("create_on"))));
                }
                //查询历史审批节点
                List<Record> hisRec = Db.find(Db.getSqlPara("query.findhisexecuteinstList", Kv.by("map", kv)));
                //查询当前审批节点
                List<Record> currRec = service.getCurrApproveUser(billId, bizType);
                //查询下一级审批节点
                List<Record> nextRec = service.getNextApproveUser(1, wfobj);

                Record newRec = new Record();

                List<Record> final_hisRec = null;
                String initSubmit_time = "";   //初始提交时间
                String lastSubmit_time = "";   //最后一次提交时间

                //如果存在历史节点 ，对历史节点进行补充 ，历史节点按照时间升序进行排列
                if (hisRec != null && hisRec.size() > 0) {
                    final_hisRec = new ArrayList<>();
                    for (int i = 0; i < hisRec.size(); i++) {
                        /**
                         * step_number 为1 的节点上添加一个虚拟提交节点，第一个节点除外
                         */
                        Record item = hisRec.get(i);
                        if (i == 0) {
                            initSubmit_time = DateKit.toStr(TypeUtils.castToDate(item.get("start_time")), DateKit.timeStampPattern);
                            lastSubmit_time = DateKit.toStr(TypeUtils.castToDate(item.get("start_time")), DateKit.timeStampPattern);
                        } else {
                            if (item.getInt("step_number") == 1) {
                                lastSubmit_time = DateKit.toStr(TypeUtils.castToDate(item.get("start_time")), DateKit.timeStampPattern);
                                //start_time    assignee     assignee_name    assignee_memo
                                final_hisRec.add(new Record().set("id", item.getStr("id") + "@")
                                        .set("start_time", "").set("end_time", lastSubmit_time)
                                        .set("assignee", item.getStr("submitter_name"))
                                        .set("assignee_memo", "重新提交"));
                            }
                        }
                        final_hisRec.add(item);
                    }
                    submitRec.get(0).set("initSubmit_time", initSubmit_time);
                    submitRec.get(0).set("lastSubmit_time", lastSubmit_time);
                } else {
                    Record submit = submitRec.get(0);
                    submit.set("initSubmit_time", TypeUtils.castToDate(submit.get("start_time")));
                    submit.set("lastSubmit_time", TypeUtils.castToDate(submit.get("start_time")));
                }

                newRec.set("submiter", submitRec);
                newRec.set("current", currRec == null ? new ArrayList<>() : currRec);
                newRec.set("future", nextRec == null ? new ArrayList<>() : nextRec);
                newRec.set("history", final_hisRec == null ? new ArrayList<>() : final_hisRec);
                if (canViewApprovedetail(majorBizType, billRecord, hisRec, currRec)) {
                    renderOk(newRec);
                } else {
                    throw new WorkflowException("无权查看！");
                }

            } else {
                throw new WorkflowException("查询单据失败！");
            }
        } catch (WorkflowException e) {
//            e.printStackTrace();
            renderFail(e);
        }
    }

    /**
     * 判断用户是否有权限查看审批详情
     *
     * @param majorBizType 业务类型
     * @param billRecord   单据详情
     * @param hisRec       历史审批记录
     * @param curRec       当前审批人列表
     * @return
     */
    private boolean canViewApprovedetail(MajorBizType majorBizType, Record billRecord, List<Record> hisRec, List<Record> curRec) {
        UserInfo userInfo = getUserInfo(); //获取当前用户
        //先判断用户是不是制单人
        boolean isProducer = (userInfo.getUsr_id().longValue() == billRecord.getLong("create_by").longValue());
        if (isProducer) {
            return true;
        }
        //先根据业务类性判断权限
        String[] biz_forces = WebConstant.MajorBizType.getCorrespondingForces(majorBizType);
        boolean judegByForces = false;
        if (biz_forces != null) {
            List<String> userForces = userInfo.getForces();
            boolean hasForce = false;
            for (String biz_force : biz_forces) {
                if (userForces.contains(biz_force)) {
                    hasForce = true;
                    break;
                }
            }
            if (hasForce) {
                try {
                    //归集通机构特殊处理
                    if (majorBizType == MajorBizType.GJT) {
                        billRecord.set("org_id", billRecord.getLong("create_org_id"));
                    }

                    judegByForces = CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(), billRecord.getLong("org_id"));
                } catch (BusinessException e) {
                    judegByForces = false;
                }
            }
        }
        if (judegByForces) {
            return true;
        } else {
            //判断是否在历史审批人及当前审批人中
            return CommonService.isUserInList(userInfo, hisRec) || CommonService.isUserInList(userInfo, curRec);
        }
    }


    /**
     * 查看待处理的单据详情
     */
    @Auth(hasForces = {"MyWFPLAT"})
    public void pendingitem() {
        Record record = getParamsToRecord();
        UserInfo userInfo = this.getUserInfo(); //获取当前用户
        long wf_inst_id = TypeUtils.castToLong(record.get("wf_inst_id"));// 获取实例id
        long billId = TypeUtils.castToLong(record.get("id"));
        int bizType = TypeUtils.castToInt(record.get("biz_type"));

        Long[] inst_ids = service.getInstIdsWithUser(userInfo);      //符合获取能够访问的实例ID
        boolean isValidate = false;
        if (inst_ids != null && inst_ids.length > 0) {
            for (Long inst_id : inst_ids) {
                if (inst_id == wf_inst_id) {
                    isValidate = true;
                    break;
                }
            }
        }

        if (isValidate) {
            //包含此流程实例，则允许查看
            viewItembyBizType(bizType);

        } else {
            renderFail(new ReqDataException("无权查看此单据！"));
        }
    }


    /**
     * 查看已处理的单据详情
     */
    @Auth(hasForces = {"MyWFPLAT"})
    public void processitem() {
        Record record = getParamsToRecord();
        UserInfo userInfo = this.getUserInfo();
        long wf_inst_id = TypeUtils.castToLong(record.get("wf_inst_id"));
        long billId = TypeUtils.castToLong(record.get("id"));
        int bizType = TypeUtils.castToInt(record.get("biz_type"));
        Kv kv = Kv.by("id", wf_inst_id).set("bill_id", billId).set("biz_type", bizType).set("assignee_id", userInfo.getUsr_id());
        List<Record> hisRec = Db.find(Db.getSqlPara("query.findhisexecuteinstList", Kv.by("map", kv)));
        if (hisRec != null && hisRec.size() > 0) {
            viewItembyBizType(bizType);
        } else {
            renderFail(new ReqDataException("无权查看此单据！"));
        }
    }


    private void viewItembyBizType(int bizType) {
        try {
            MajorBizType type = MajorBizType.getBizType(bizType);
            String url = null;
            switch (type) {
                case ACC_OPEN_INT:
                    url = "/normal/openintent/detail";
                    break;
                case ACC_OPEN_COM:
                    url = "/normal/opencom/detail";
                    break;
                case ACC_CHG_APL:
                    url = "/normal/openchg/detail";
                    break;
                case ACC_DEFREEZE_APL:
                    url = "/normal/accdefreeze/detail";
                    break;
                case ACC_FREEZE_APL:
                    url = "/normal/accfreeze/detail";
                    break;
                case ACC_CLOSE_INT:
                    url = "/normal/closeacc/detail";
                    break;
                case ACC_CLOSE_COM:
                    url = "/normal/closeacccomple/detail";
                    break;
                case INNERDB:
                    url = "/normal/dbt/detail";
                    break;
                case INNERDB_BATCH:
                    url = "/normal/dbtbatch/detail";
                    break;
                case ZFT:
                    url = "/normal/zft/detail";
                    break;
                case OBP:
                    url = "/normal/zftbatch/detail";
                    break;
                case GJT:
                    url = "/normal/collectsetting/detail";
                    break;
                case ALLOCATION:
                    url = "/normal/allocset/detail";
                    break;
                case GYL:
                    url = "/normal/gylsetting/detail";
                    break;
                case SKT:
                    url = "/normal/skt/billdetail";
                    break;
                case DZT:
                    //TODO
                    break;
                case MONITOR:
                    //TODO
                    break;
                case ELECTRONIC:
                    //TODO
                    break;
                case CBB:
                    url = "/normal/ndc/detail";
                    break;
                case OA_HEAD_PAY:
                    url = "/normal/headorgoa/detail";
                    break;
                case OA_BRANCH_PAY:
                    url = "/normal/branchorgoa/detail";
                    break;
                default:
                    throw new ReqDataException("未实现BizType！");
            }
            forwardAction(url);
        } catch (BusinessException e) {
            renderFail(e);
        }


    }

    /**
     * 查看工作流图明细
     */
    public void wfdetail() {
        try {
            Record record = getParamsToRecord();
            Record detail = service.detail(record);
            renderOk(detail);
        } catch (BusinessException e) {
            logger.error("获取流程详情失败！", e);
            renderFail(e);
        }
    }
}
