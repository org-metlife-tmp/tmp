package com.qhjf.cfm.web;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.web.constant.WebConstant;

public  abstract  class WfRequestObj {


    /**
     * 业务类型
     */
    private WebConstant.MajorBizType bizType ;

    /**
     * 业务所对应的数据库表名
     */
    private String tableName ;

    /**
     * 前端业务数据
     */
    private Record record ;


    private IWfRequestExtQuery extQuery;


    /**
     * 获取工作流define_id
     * @return
     * @throws WorkflowException
     */
    public Long getDefineId() throws WorkflowException {
        if(record != null && record.get("define_id") != null){
            return TypeUtils.castToLong(record.get("define_id"));
        }else{
            throw  new WorkflowException("请求对象中define_id为null");
        }
    }


    /**
     * 获取工作流当前实例的id wf_inst_id
     */
    public Long getInstId() throws WorkflowException {
        if(record != null && record.get("wf_inst_id") != null){
            return TypeUtils.castToLong(record.get("wf_inst_id"));
        }else{
            throw  new WorkflowException("请求对象中wf_inst_id为null");
        }
    }


    /**
     * 获取业务信息中bill_id
     * @return
     * @throws WorkflowException
     */
    public Long getBillId() throws WorkflowException{
        if(record != null && record.get("id") != null){
            return TypeUtils.castToLong(record.get("id"));
        }else{
            throw  new WorkflowException("请求对象中bill_id为null");
        }
    }





    /**
     * 获取业务信息中bill_code
     * @return
     * @throws WorkflowException
     */
    public String getBillCode() throws WorkflowException{
        if(record != null && record.get("service_serial_number") != null){
            return TypeUtils.castToString(record.get("service_serial_number"));
        }else{
            throw  new WorkflowException("请求对象中bill_code为null");
        }
    }



    /**
     * 获取审批意见
     * @return
     */
    public String getApproveMemo(){
        return record!= null ? record.getStr("assignee_memo"):"";
    }


    /**
     * 获取加签用户id
     * @return
     * @throws WorkflowException
     */
    public Long getShadowUserId()throws WorkflowException{
        if(record != null && record.get("shadow_user_id") != null){
            return TypeUtils.castToLong(record.get("shadow_user_id"));
        }else{
            throw  new WorkflowException("请求对象中shadow_user_id为null");
        }
    }

    /**
     * 获取加签用户名称
     * @return
     * @throws WorkflowException
     */
    public String getShadowUserName(){
        return record!= null ? record.getStr("shadow_user_name"):"";
    }



    protected WfRequestObj(Record record) throws WorkflowException {
            init(record);
    }

    private  void init(Record record) throws WorkflowException{
        this.bizType = WebConstant.MajorBizType.getBizType(TypeUtils.castToInt(record.get("biz_type")));
        this.tableName = this.bizType.getTableName();
        this.record = record;

    }


    public WfRequestObj(WebConstant.MajorBizType bizType, String tableName, Record record) {
        this.bizType = bizType;
        this.tableName = tableName;
        this.record = record;
    }


    public boolean chgStatus(WebConstant.BillStatus status){
        String  sql = "update "+tableName +" set service_status = ?, " +
                "persist_version = persist_version +1  where id = ? " +
                "and service_status = ? and persist_version = ?";

        boolean dbResult =  Db.update(sql, status.getKey() , record.get("id"), record.get("service_status")
                ,record.get("persist_version")) == 1;

        if(status == WebConstant.BillStatus.PASS){
            return dbResult && hookPass();
        }else if(status == WebConstant.BillStatus.REJECT){
            return dbResult && hookReject();
        }else{
            return dbResult;
        }


    }


    /**
     * 获取对应的业务单据详情
     * @return
     * @throws WorkflowException
     */
    public Record getBillRecord() throws WorkflowException{
        Long bill_id = getBillId();
        Record info = Db.findById(tableName,"id",bill_id);
        if(info != null){
            return info;
        }else{
            throw new WorkflowException(tableName+ ":"+bill_id+" not exists");
        }
    }


    /**
     * 单据状态为审批通过的钩子，默认返回true, 如果特定业务需要特殊处理，需要重载实现
     * @return
     */
    protected boolean hookPass(){
        return true;
    }
    
    /**
     * 单据状态为审批拒绝的钩子，默认返回true, 如果特定业务需要特殊处理，需要重载实现
     * @return
     */
    protected boolean hookReject(){
        return true;
    }


    public WebConstant.MajorBizType getBizType() {
        return bizType;
    }

    public void setBizType(WebConstant.MajorBizType bizType) {
        this.bizType = bizType;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }


    /**
     * 根据表达式获取值
     * @param type
     * @param <T>
     * @return
     */
    public abstract  <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException;


    /**
     * 获取待处理工作流实例查询SQL
     */
    public abstract SqlPara getPendingWfSql(Long[] inst_id , Long[] exclude_inst_id);


    public IWfRequestExtQuery getExtQuery() {
        return extQuery;
    }

    public void setExtQuery(IWfRequestExtQuery extQuery) {
        this.extQuery = extQuery;
    }
}
