package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 异常处理页面
 *
 * @author GJF
 */
public class ExceptService {
    private static Logger log = LoggerFactory.getLogger(ExceptService.class);
    /**
     * 异常处理 - 查看分页列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> exceptlist(int pageNum, int pageSize, final Record record, UodpInfo uodpInfo) throws BusinessException {

        Long org_id = uodpInfo.getOrg_id();
        Record findById = Db.findById("organization", "org_id", org_id);
        if(null == findById){
            throw new ReqDataException("当前登录人的机构信息未维护");
        }
        List<String> codes = new ArrayList<>();
        if(findById.getInt("level_num") == 1){
            log.info("========目前登录机构为总公司");
            codes = Arrays.asList("0102","0101","0201","0202","0203","0204","0205","0500");
        }else{
            log.info("========目前登录机构为分公司公司");
            List<Record> rec = Db.find(Db.getSql("org.getCurrentUserOrgs"), org_id);
            for (Record o : rec) {
                codes.add(o.getStr("code"));
            }
        }
        record.set("codes", codes);
        SqlPara sqlPara = Db.getSqlPara("except.exceptlist", Kv.by("map", record.getColumns()));

        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 异常数据退回 - 查看单据详情
     *
     * @param record
     * @param userInfo
     * @return
     */
    public Record detail(final Record record, UserInfo userInfo, UodpInfo uodpInfo) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));

        //根据单据id查询单据信息
        List<Record> records = Db.find(Db.getSql("except.findbatchtotalbyid"),id);
        Record dbRec = null;
        if(records==null || records.size()==0){
            throw new ReqDataException("未找到有效的单据!");
        }
        dbRec = records.get(0);
        //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
        //否则进行机构权限的校验
        if (record.get("wf_inst_id") == null || record.get("biz_type") == null) {
            CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(), dbRec.getLong("org_id"));
        }


        return dbRec;
    }

    /**
     * 回退
     * @param record
     * @return
     * @throws BusinessException
     */
    public void revoke(final Record record, final UserInfo userInfo,  final UodpInfo curUodp) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        final Record childRecord = Db.findById("pay_batch_total", "id", id);
        if(childRecord == null){
            throw new ReqDataException("未找到对应的批次!");
        }

        //已发送未回盘 和 回盘异常 的才能回退
        int status = TypeUtils.castToInt(childRecord.get("service_status"));
        if(!(WebConstant.SftCheckBatchStatus.getByKey(status) == WebConstant.SftCheckBatchStatus.FSWHP
                || WebConstant.SftCheckBatchStatus.getByKey(status) == WebConstant.SftCheckBatchStatus.HPYC)){
            throw new ReqDataException("只能回退已发送未回盘和回盘异常的批次!");
        }
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //走审批流
                List<Record> flows = null;
                try {
                    flows = CommonService.displayPossibleWf(WebConstant.MajorBizType.PLF_EXCEPT_BACK.getKey(), curUodp.getOrg_id() ,null);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    log.error("============获取批量付回退审批流异常");
                    return false ;
                }
                if(flows == null || flows.size() ==0){
                    log.error("============获取批量付回退审批流异常");
                    return false ;
                }
                Record flow = flows.get(0);
                childRecord.set("define_id", flow.getLong("define_id"));
                childRecord.set("service_serial_number", childRecord.get("child_batchno"));
                //更新批次表状态由“已发送未回盘”改为“回退审批中”
//                childRecord.set("service_status", WebConstant.SftCheckBatchStatus.HTSPZ.getKey());
                //TODO
                WfRequestObj wfRequestObj = new WfRequestObj(WebConstant.MajorBizType.PLF_EXCEPT_BACK, "pay_batch_total", childRecord) {
                    @Override
                    public <T> T getFieldValue(WebConstant.WfExpressType type) {
                        return null ;
                    }

                    @Override
                    public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
                        return null;
                    }

                };
                WorkflowProcessService workflowProcessService = new WorkflowProcessService();
                boolean submitFlowFlg = false;
                try {
                    submitFlowFlg = workflowProcessService.startWorkflow(wfRequestObj, userInfo);
                } catch (WorkflowException e) {
                    e.printStackTrace();
                    log.error("=========批量付异常数据回退提交审批流失败");
                    return false ;
                }catch(Exception e){
                    e.printStackTrace();
                    return false;
                }
                if(!submitFlowFlg){
                    return false ;
                }

                //更新批次表状态由“已发送未回盘”改为“回退审批中”
                boolean flag = CommonService.update("pay_batch_total",
                        new Record().set("service_status", WebConstant.SftCheckBatchStatus.HTSPZ.getKey())
                                .set("persist_version", TypeUtils.castToInt(childRecord.get("persist_version"))+2)
                                .set("error_msg", TypeUtils.castToString(record.getStr("op_reason")))
                                .set("revoke_user_id", userInfo.getUsr_id())
                                .set("revoke_user_name", userInfo.getName())
                                .set("revoke_date", new Date())
                                .set("create_by", userInfo.getUsr_id())
                                .set("create_on", new Date())
                                .set("update_by", userInfo.getUsr_id())
                                .set("update_on", new Date()),
                        new Record().set("id", id).set("persist_version",TypeUtils.castToInt(childRecord.get("persist_version"))+1));
                if(!flag){
                    return false;
                }

                return true;
            }
        });
        if(!flag){
            throw new DbProcessException("回退失败！");
        }
    }

    public boolean hookPass(Record record) {
    	String positionName = null;
    	Record position = Db.findFirst(Db.getSql("except.findFirstApprovePosition"), WebConstant.MajorBizType.PLF_EXCEPT_BACK.getKey(),record.getLong("id"));
    	if(position != null){
    		positionName = position.getStr("name");
    	}
        return CommonService.update("pay_batch_total",
                new Record().set("service_status", WebConstant.SftCheckBatchStatus.YHT.getKey())
                        .set("exam_position_name", positionName)
                        .set("exam_time", new Date())
                        .set("persist_version", TypeUtils.castToInt(record.get("persist_version"))+2),
                new Record().set("id", TypeUtils.castToInt(record.get("id"))).set("persist_version", TypeUtils.castToInt(record.get("persist_version"))+1));
    }

    public boolean hookReject(Record record) {
    	String positionName = null;
    	Record position = Db.findFirst(Db.getSql("except.findFirstApprovePosition"), WebConstant.MajorBizType.PLF_EXCEPT_BACK.getKey(),record.getLong("id"));
    	if(position != null){
    		positionName = position.getStr("name");
    	}
        return CommonService.update("pay_batch_total",
                new Record().set("service_status", WebConstant.SftCheckBatchStatus.FSWHP.getKey())
		                .set("exam_position_name", positionName)
		                .set("exam_time", new Date())
                        .set("persist_version", TypeUtils.castToInt(record.get("persist_version"))+2),
                new Record().set("id", TypeUtils.castToInt(record.get("id"))).set("persist_version", TypeUtils.castToInt(record.get("persist_version"))+1));
    }
}
