package com.qhjf.cfm.web.controller;

import java.util.ArrayList;
import java.util.List;

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
import com.qhjf.cfm.web.service.CheckBatchForService;

/**
 * 核对组批LA
 * @author pc_liweibing
 *
 */
public class CheckBatchForController extends CFMBaseController{

    private final static Log logger = LogbackLog.getLog(CheckBatchForController.class);
	
    CheckBatchForService service = new  CheckBatchForService();
    
    /**
     *@ 组批LA/EBS列表
     */
    @Auth(hasForces = {"PayCheckAllot"})
	public void  list(){
		logger.info("============获取核对组批LA/EBS列表");
		Record record = getRecordByParamsStrong();
        try {            	
        	List<Record> page = service.list(record,getCurUodp());	
        	renderOk(page);
		} catch (BusinessException e) {
			logger.error("获取组批LA列表失败");
			renderFail(e);
		}
	}	
	
	
	 /**
     *@ 组批LA/EBS列表详情,用于我的审批平台展示
     */
    @Auth(hasForces = {"PayCheckAllot", "MyWFPLAT"})
	public void  detail(){
		logger.info("============获取核对组批LA/EBS列表详情");
		Record record = getRecordByParamsStrong();
        try {            	
        	Record detail = service.detail(record.getLong("id"));	
        	renderOk(detail);
		} catch (BusinessException e) {
			logger.error("获取组批列表详情失败");
			renderFail(e);
		}
	}	
	
	
	 /**
     *@ 组批LA/EBS撤回
     */
    @Auth(hasForces = {"PayCheckAllot"})
	public void  revokeToLaOrEbs(){
		logger.info("============撤回至LA/EBS");
		Record record = getRecordByParamsStrong();
        try {
        	service.revokeToLaOrEbs(record);	
        	renderOk(null);
		} catch (BusinessException e) {
			logger.error("核对组批撤回失败");
			renderFail(e);
		}
	}	
	
	
	/*@Override
	public void revoke() {
        try {
        	//更新合法数据表状态为未组批.更新组批详情表的delete_num
        	setAttr("_wfobj", genWfRequestObj());
        	Record record = getRecordByParamsStrong();
        	service.updateTable(record);
            forwardAction("/normal/wfprocess/revoke");
        } catch (BusinessException e) {
            renderFail(e);
        }
    }*/
	
	

	/**
	 * 组批确定按钮
	 */
    @Auth(hasForces = {"PayCheckAllot"})
	public void confirm() {
        try {
        	Record record = getRecordByParamsStrong(); 
            service.confirm(record,getCurUodp(),getUserInfo());  
            renderOk(null);
        } catch (BusinessException e) {
            renderFail(e);
            logger.error("=======组批确认失败");
        }
    }
	
	
	
	@Override
    protected List<Record> displayPossibleWf(Record record) throws BusinessException {
        //根据单据id查询是否有绑定到子业务类型
        String biz_id = null;
        Record innerRec = Db.findById("pay_batch_total_master", "id", TypeUtils.castToLong(record.get("id")));
        if (innerRec != null && record.get("biz_id") != null) {
            biz_id = TypeUtils.castToString(record.get("biz_id"));
        }
        return CommonService.displayPossibleWf(WebConstant.MajorBizType.PLF.getKey(), getCurUodp().getOrg_id(), biz_id);
    }
	
	
/*    protected WfRequestObj genWfRequestObj(final Record submitRecord) throws BusinessException {
        return new WfRequestObj(WebConstant.MajorBizType.PLF, "pay_batch_total_master", submitRecord) {
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
                return getSqlPara(inst_id, exclude_inst_id, submitRecord);
            }

            @Override
            public boolean hookPass() {
                return service.hookPass(submitRecord);
            }
        };
    }*/
    
    @Override
    protected WfRequestObj genWfRequestObj() throws BusinessException {
    	final Record record = getParamsToRecord();
        return new WfRequestObj(WebConstant.MajorBizType.PLF, "pay_batch_total_master", record) {
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
            public boolean hookPass() {
            	UserInfo userInfo = getUserInfo();
                return service.hookPass(record,userInfo);
            }
            @Override
            public boolean hookReject() {
                return service.hookReject(record);
            }
            
        };
    }
    //TODO
    private SqlPara getSqlPara(Long[] inst_id, Long[] exclude_inst_id, Record rec) {
        final Kv kv = Kv.create();
        if (inst_id != null) {
            kv.set("in", new Record().set("instIds", inst_id));
        }
        if (exclude_inst_id != null) {
            kv.set("notin", new Record().set("excludeInstIds", exclude_inst_id));
        }
        kv.set("biz_type", rec.get("biz_type"));
        return Db.getSqlPara("check_batch.findPendingList", Kv.by("map", kv));
    }
    
    
    /**
     * 通道编码列表
     * @throws ReqDataException 
     */
    public void channelCodeList() throws ReqDataException {   	
    	logger.info("===========进入通道编码列表");
    	Record record = getRecordByParamsStrong();
    	//Long org_id = getCurUodp().getOrg_id();
    	//record.set("org_id", org_id);
        List<Record> lists = service.channelCodeList(record);	
        renderOk(lists);    	
    }
    
    
    /**
      *@ 核对组批导出
      */
    @Auth(hasForces = {"PayCheckAllot"})
    public void listexport() {
        doExport();
    }
    
    
    /**
     * @审批平台页面_根据主批次号查找子批次
     */
    @Auth(hasForces = {"PayCheckAllot,MyWFPLAT"})
    public void findSonByMasterBatch() {
        Record record = getRecordByParamsStrong();
        List<Record> list = service.findSonByMasterBatch(record);
        renderOk(list);
    }
    
    
    
    @Override
    protected List<WfRequestObj> genBatchWfRequestObjs() throws BusinessException {
        Record record = getParamsToRecordStrong();
        final List<Record> wfRequestObjs = record.get("batch_list");

        if (wfRequestObjs != null && wfRequestObjs.size() > 0) {

            return new ArrayList<WfRequestObj>() {
                {
                    for (final Record rec : wfRequestObjs) {
                        add(new WfRequestObj(WebConstant.MajorBizType.PLF, "pay_batch_total_master", rec) {
                            @Override
                            public <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException {
                                Record bill_info = getBillRecord();
                                if (type.equals(WebConstant.WfExpressType.AMOUNT)) {
                                    return bill_info.get("payment_amount");
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
                            public boolean hookPass() {
                            	UserInfo userInfo = getUserInfo();
                                return service.hookPass(rec,userInfo);
                            }
                        });
                    }
                }
            };

        }

        throw new WorkflowException("没有可操作的单据!");
    }
}
