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
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.PayCounterService;
import com.qhjf.cfm.web.service.WorkflowProcessService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * 柜面付款工作台
 * @author pc_liweibing
 *
 */
public class PayCounterController extends CFMBaseController{

    private final static Log logger = LogbackLog.getLog(PayCounterController.class);
	
    PayCounterService service = new  PayCounterService();
    
   /**
    * 柜面列表
 * @throws UnsupportedEncodingException 
    */
    @Auth(hasForces = {"PayCounterPlat"}) 
	public  void  list() throws ReqDataException, UnsupportedEncodingException {
		try {
			Record record = getRecordByParamsStrong();
	    	int pageNum = getPageNum(record);
	        int pageSize = getPageSize(record);   		
	    	Page<Record> page = service.list(pageNum,pageSize,record,getCurUodp().getOrg_id());
	    	renderOkPage(page);			
		} catch (BusinessException e) {
			logger.error("柜面列表获取失败");
			e.printStackTrace();
			renderFail(e);
		}
		
	}
    
	
	 /**
     *@ 柜面付列表详情,用于我的审批平台展示
     */
    @Auth(hasForces = {"PayCounterPlat", "MyWFPLAT"})
	public void  detail(){
		logger.info("============获取柜面付列表详情");
		Record record = getRecordByParamsStrong();          	
        Record detail = null;
		try {
			detail = service.detail(record.getLong("id"));
		} catch (ReqDataException e) {
			e.printStackTrace();
			renderFail(e);
		}	
        renderOk(detail);	
	}
	
	
	  /**
	    * 柜面补录
	    */	
       @Auth(hasForces = {"PayCounterPlat"}) 
		public  void  supplement() {		
			try {
				Record record = getRecordByParamsStrong();	
				service.supplement(record,getUserInfo());
				renderOk(null);
			}catch (BusinessException e) {
				logger.error("=====柜面补录失败");
				e.printStackTrace();
				renderFail(e);
			}			
		}
		
       
       /**
	    * 柜面付平台业务类型列表
	    */	
       @Auth(hasForces = {"PayCounterPlat"}) 
		public  void  typecode() {		
			try {
				Record record = getRecordByParamsStrong();	
				List<Record> rec = service.typecode(record);
				renderOk(rec);
			}catch (BusinessException e) {
				logger.error("=====柜面付获取业务类型列表失败");
				e.printStackTrace();
				renderFail(e);
			}			
		}

		/**
		 * 柜面批量拒绝
		 */
		@Auth(hasForces = {"PayCounterPlat"})
		public  void  batchreject() {
			try {

				List<WfRequestObj> wfobjs = genBatchWfRequestObjs();
				UserInfo userInfo = this.getUserInfo();
				List<String> errBillcodes = new ArrayList();
				WorkflowProcessService service = new WorkflowProcessService();
				if (wfobjs != null && wfobjs.size() > 0) {
					Iterator var4 = wfobjs.iterator();

					while(var4.hasNext()) {
						WfRequestObj wfobj = (WfRequestObj)var4.next();

						try {
							if (wfobj == null) {
								throw new WorkflowException("审批拒绝失败，空的提交请求数据！");
							}

							boolean flag = service.approvReject(wfobj, userInfo);
							if (!flag) {
								throw new WorkflowException("提交失败");
							}

							this.renderOk(flag);
						} catch (Exception var) {
							logger.error("审批拒绝失败！", var);
							service.compensateApprovePermission(wfobj, userInfo);
							if (var instanceof BusinessException) {
								this.renderFail((BusinessException)var);
							} else {
								this.renderFail(new WorkflowException("审批拒绝失败, 数据错误!"));
							}
						}
					}

					renderOk(Kv.create().set("error_list", errBillcodes));
				} else {
					renderFail(new WorkflowException("审批同意失败，空的提交请求数据！"));
				}
			} catch (BusinessException var2) {
				this.renderFail(var2);
			}
		}
		  /**
		    * 柜面拒绝
		    */	
           @Auth(hasForces = {"PayCounterPlat"}) 
			public  void  revokeToLaOrEbs() {		
				try {
					Record record = getRecordByParamsStrong();	
					service.revokeToLaOrEbs(record,getUserInfo());
					renderOk(null);
				}catch (BusinessException e) {
					logger.error("=====柜面付拒绝失败");
					e.printStackTrace();
					renderFail(e);
				}			
			}
			
			/**
			    * 柜面确认/提交
			    */	
               @Auth(hasForces = {"PayCounterPlat"}) 
				public  void  confirm() {		
					try {
						Record record = getRecordByParamsStrong();	
						service.confirm(record,getUserInfo(),getCurUodp());
						renderOk(null);
					}catch (BusinessException e) {
						logger.error("=====柜面确认失败");
						e.printStackTrace();
						renderFail(e);
					}		 	
				}
				
	     
				
				@Override
			    protected List<Record> displayPossibleWf(Record record) throws BusinessException {
			        //根据单据id查询是否有绑定到子业务类型
			        String biz_id = null;
			        Record innerRec = Db.findById("gmf_bill", "id", TypeUtils.castToLong(record.get("id")));
			        if (innerRec != null && record.get("biz_id") != null) {
			            biz_id = TypeUtils.castToString(record.get("biz_id"));
			        }
			        return CommonService.displayPossibleWf(WebConstant.MajorBizType.GMF.getKey(), getCurUodp().getOrg_id(), biz_id);
			    }
				
			    @Override
			    protected WfRequestObj genWfRequestObj() throws BusinessException {
			    	final Record record = getParamsToRecord();
			        return new WfRequestObj(WebConstant.MajorBizType.GMF, "gmf_bill", record) {
			            @Override
			            public <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException {
			                Record bill_info = getBillRecord();
			                if (type.equals(WebConstant.WfExpressType.AMOUNT)) {
			                    return bill_info.get("amount");
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
			            	UserInfo userInfo = getUserInfo();
			                return service.hookReject(record,userInfo);
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
			        return Db.getSqlPara("pay_counter.findPendingList", Kv.by("map", kv));
			    }
			    
			    
			    
			    @Override
			    protected List<WfRequestObj> genBatchWfRequestObjs() throws BusinessException {
			        Record record = getParamsToRecordStrong();
			        final List<Record> wfRequestObjs = record.get("batch_list");

			        if (wfRequestObjs != null && wfRequestObjs.size() > 0) {

			            return new ArrayList<WfRequestObj>() {
			                {
			                    for (final Record rec : wfRequestObjs) {
			                        add(new WfRequestObj(WebConstant.MajorBizType.GMF, "gmf_bill", rec) {
			                            @Override
			                            public <T> T getFieldValue(WebConstant.WfExpressType type) throws WorkflowException {
			                                Record bill_info = getBillRecord();
			                                if (type.equals(WebConstant.WfExpressType.AMOUNT)) {
			                                    return bill_info.get("amount");
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
			    
			    /**
			     * 柜台付列表导出
			     */
			    @Auth(hasForces = {"PayCounterPlat"}) 
			    public void listexport() {
			        doExport();
			    }
}
