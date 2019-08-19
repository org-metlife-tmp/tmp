package com.qhjf.cfm.web.controller;


import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.RecvCounterService;
import com.qhjf.cfm.web.service.RecvGroupCounterService;

import java.util.ArrayList;
import java.util.List;


/**
 * 柜面收 团单
 *
 */
public class RecvGroupCounterController extends CFMBaseController{

    private final static Log logger = LogbackLog.getLog(RecvGroupCounterController.class);

	RecvGroupCounterService service = new  RecvGroupCounterService();
    
    /**
     * 柜面收_团单新增
     */
	@Auth(hasForces = {"RECVCOUNTERGROUP"})
    public void add() {
    	
    	Record record = getRecordByParamsStrong();
    	try {
    		UserInfo userInfo = getUserInfo();
    		UodpInfo curUodp = getCurUodp();
    		service.add(record,userInfo,curUodp);
    		renderOk(null);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}     	
    }

	/**
	 * 获取bankcode
	 */
	public void getBankcode(){
		/**
		 * 取 bankcode,默认模糊匹配，（默认登录人当前所在机构范围及下级单位。总公司能看到所有账号）
		 */
		Record record = getRecordByParamsStrong();
		try {
			UodpInfo uodpInfo = getCurUodp();
			List<Record> records = service.getBankcode(record,uodpInfo);
			renderOk(records);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}
	}

	/**
	 * 柜面收团单撤销
	 */
	@Auth(hasForces = {"RECVCOUNTERGROUP"})
	public void revoke() {
		Record record = getRecordByParamsStrong();
		try {
			UserInfo userInfo = getUserInfo();
			UodpInfo uodpInfo = getCurUodp();
			service.revoke(record, userInfo, uodpInfo);
			renderOk(null);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}
	}

    /**
     * 柜面收团单列表
     */
	@Auth(hasForces = {"RECVCOUNTERGROUP"})
    public void list() {
    	Record record = getRecordByParamsStrong();
    	try {
    		int pageSize = getPageSize(record);
    		int pageNum = getPageNum(record);
    		Page<Record> list = service.list(record,getCurUodp(),pageSize,pageNum);
    		renderOkPage(list);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}
    }
    
    
    /**
     * 柜面收团单详情
     */
	@Auth(hasForces = {"RECVCOUNTERGROUP", "MyWFPLAT"})
    public void detail() {
    	Record record = getRecordByParamsStrong();
    	try {
    		Record rec = service.detail(record);
    		renderOk(rec);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}
    }

	/**
	 * 柜面收团单列表详情确认
	 */
	@Auth(hasForces = {"RECVCOUNTERGROUP"})
	public void detailconfirm() {
		Record record = getRecordByParamsStrong();
		try {
			service.detailconfirm(record);
			renderOk(null);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}
	}

	/**
	 * 非客户账号类型的搜索
	 */
	@Auth(hasForces = {"RECVCOUNTERGROUP"})
	public void customotherlist() {
		Record record = getRecordByParamsStrong();
		try {
			Object object = service.customOtherList(record);
			renderOk(object);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}
	}


	/**
	 *	客户账号的搜索
	 */
	@Auth(hasForces = {"RECVCOUNTERGROUP"})
	public void customlist() {
		Record record = getRecordByParamsStrong();
		try {
			Object object = service.customList(record);
			renderOk(object);
		} catch (BusinessException e) {
			e.printStackTrace();
			renderFail(e);
		}
	}

	@Override
	protected List<Record> displayPossibleWf(Record record) throws BusinessException {
		//根据单据id查询是否有绑定到子业务类型
		String biz_id = null;
		Record innerRec = Db.findById("recv_counter_bill", "id", TypeUtils.castToLong(record.get("id")));
		if (innerRec != null && record.get("biz_id") != null) {
			biz_id = TypeUtils.castToString(record.get("biz_id"));
		}
		return CommonService.displayPossibleWf(WebConstant.MajorBizType.GMS.getKey(), getCurUodp().getOrg_id(), biz_id);
	}

	@Override
	protected WfRequestObj genWfRequestObj() throws BusinessException {
		final Record record = getParamsToRecord();
		return new WfRequestObj(WebConstant.MajorBizType.GMS, "recv_counter_bill", record) {
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
		return Db.getSqlPara("recv_group_counter.findPendingList", Kv.by("map", kv));
	}



	@Override
	protected List<WfRequestObj> genBatchWfRequestObjs() throws BusinessException {
		Record record = getParamsToRecordStrong();
		final List<Record> wfRequestObjs = record.get("batch_list");

		if (wfRequestObjs != null && wfRequestObjs.size() > 0) {

			return new ArrayList<WfRequestObj>() {
				{
					for (final Record rec : wfRequestObjs) {
						add(new WfRequestObj(WebConstant.MajorBizType.GMS, "recv_counter_bill", rec) {
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
     *@ 柜面收导出
     */
	@Auth(hasForces = {"RECVCOUNTERGROUP"})
   public void listexport() {
       doExport();
   }
   
}
