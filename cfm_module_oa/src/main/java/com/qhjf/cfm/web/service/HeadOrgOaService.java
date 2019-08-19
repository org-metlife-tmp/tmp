package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.impl.SysOaSinglePayInter;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.webservice.oa.callback.OaCallback;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

public class HeadOrgOaService {

    private static final Log log = LogbackLog.getLog(HeadOrgOaService.class);

    OaCallback oaCallback = new OaCallback();

    public WfRequestObj genWfRequestObj(final Record record) {
        return new WfRequestObj(WebConstant.MajorBizType.OA_HEAD_PAY, "oa_head_payment", record) {
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
                final Kv kv = Kv.create();
                if (inst_id != null) {
                    kv.set("in", new Record().set("instIds", inst_id));
                }
                if (exclude_inst_id != null) {
                    kv.set("notin", new Record().set("excludeInstIds", exclude_inst_id));
                }
                kv.set("biz_type", record.get("biz_type"));
                return Db.getSqlPara("head_org_oa.findHeadOrgOAPendingList", Kv.by("map", kv));
            }

            @Override
            protected boolean hookPass() {
                return pass(record);
            }
        };
    }

    public Page<Record> getTodoPage(int page_num, int page_size, Record record) {
        SqlPara sqlPara = Db.getSqlPara("head_org_oa.getTodoPage", Kv.by("map", record.getColumns()));
        return Db.paginate(page_num, page_size, sqlPara);
    }

    public Page<Record> getDonePage(int page_num, int page_size, Record record) {
        SqlPara sqlPara = Db.getSqlPara("head_org_oa.getDonePage", Kv.by("map", record.getColumns()));
        return Db.paginate(page_num, page_size, sqlPara);
    }

    public Record chg(Record record, UserInfo userInfo) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        final int persist_version = TypeUtils.castToInt(record.get("persist_version"));
        final List<Object> fileList = record.get("files");
        Record oldHead = Db.findById("oa_head_payment", "id", id);

        /*//申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(oldHead.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }*/

        //校验状态
        CommonService.checkBillStatus(oldHead);
        final Record head = new Record()
                .set("pay_account_id", record.get("pay_account_id"))
                .set("pay_account_no", record.get("pay_account_no"))
                .set("pay_account_name", record.get("pay_account_name"))
                .set("pay_account_cur", record.get("pay_account_cur"))
                .set("pay_account_bank", record.get("pay_account_bank"))
                .set("pay_bank_cnaps", record.get("pay_bank_cnaps"))
                .set("pay_bank_prov", record.get("pay_bank_prov"))
                .set("pay_bank_city", record.get("pay_bank_city"))
                .set("pay_mode", record.get("pay_mode"))
                .set("payment_summary", record.get("payment_summary"))
                .set("service_status", WebConstant.BillStatus.SAVED.getKey())
                .set("update_by", userInfo.getUsr_id())
                .set("update_on", new Date())
                .set("delete_flag", WebConstant.YesOrNo.NO.getKey())
                .set("persist_version", persist_version + 1)
                .set("attachment_count", (fileList == null || fileList.isEmpty()) ? 0 : fileList.size())
                .set("process_bank_type", record.get("process_bank_type"));
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                CommonService.delFileRef(WebConstant.MajorBizType.OA_HEAD_PAY.getKey(), id);
                boolean update = CommonService.update("oa_head_payment",
                        head,
                        new Record().set("id", id).set("persist_version", persist_version));
                if (update) {
                    return CommonService.saveFileRef(WebConstant.MajorBizType.OA_HEAD_PAY.getKey(), id, fileList);
                }
                return update;
            }
        });
        if (!flag) {
            throw new DbProcessException("修改总公司付款单据失败！");
        }
        return Db.findById("oa_head_payment", "id", id);
    }

    public void del(Record record, UserInfo userInfo) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        final int persist_version = TypeUtils.castToInt(record.get("persist_version"));

        Record billRec = Db.findById("oa_head_payment", "id", id);
        //申请单创建人id非登录用户，不允许进行操作
        if (!TypeUtils.castToLong(billRec.get("create_by")).equals(userInfo.getUsr_id())) {
            throw new ReqDataException("无权进行此操作！");
        }

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return CommonService.update("oa_head_payment",
                        new Record()
                                .set("delete_flag", WebConstant.YesOrNo.YES.getKey())
                                .set("persist_version", persist_version + 1),
                        new Record()
                                .set("id", id)
                                .set("persist_version", persist_version));
            }
        });
        if (!flag) {
            throw new DbProcessException("删除总公司付款单据失败！");
        }
    }

    public Record detail(Record record, UserInfo userInfo) throws BusinessException {
        Record head = Db.findFirst(Db.getSql("head_org_oa.findHeadPayById"), record.get("id"));
        if (null == head) {
            throw new DbProcessException("单据不存在，请刷新重试!");
        }

        //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
        //否则进行机构权限的校验
        if (record.get("wf_inst_id") == null || record.get("biz_type") == null) {
            CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(), head.getLong("org_id"));
        }


        return head;
    }

    public boolean pass(final Record record) {
        Record headRecord = null;
        try {
            headRecord = Db.findFirst(Db.getSql("head_org_oa.findHeadPayById"), record.get("id"));
            String payCnaps = headRecord.getStr("pay_bank_cnaps");
            String payBankCode = payCnaps.substring(0, 3);
            IChannelInter channelInter = ChannelManager.getInter(payBankCode, "SinglePay");
            headRecord.set("source_ref", "oa_head_payment");
            final int old_repeat_count = TypeUtils.castToInt(headRecord.get("repeat_count"));
            headRecord.set("repeat_count", old_repeat_count+ 1);
            headRecord.set("bank_serial_number", ChannelManager.getSerianlNo(payBankCode));
            SysOaSinglePayInter sysInter = new SysOaSinglePayInter();
            sysInter.setChannelInter(channelInter);
            final Record instr = sysInter.genInstr(headRecord);

            boolean flag = Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    boolean save = Db.save("single_pay_instr_queue", instr);
                    if (save) {
                        return Db.update(Db.getSql("head_org_oa.updBillById"), instr.getStr("bank_serial_number"),
                                instr.getInt("repeat_count"), WebConstant.BillStatus.PROCESSING.getKey(), instr.getStr("instruct_code")
                                ,new Date(), record.get("id"),old_repeat_count) == 1;
                    }
                    return save;
                }
            });
            if (flag) {
                QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(instr), payBankCode);
                ProductQueue productQueue = new ProductQueue(bean);
                new Thread(productQueue).start();
            } else {
                throw new DbProcessException("发送银行失败！");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (headRecord != null) {
                String errMsg = null;
                if (e.getMessage() == null || e.getMessage().length() > 1000) {
                    errMsg = "发送银行失败！";
                } else {
                    errMsg = e.getMessage();
                }
                Long originDateId = headRecord.getLong("ref_id");
                Record billRecordCopy = new Record();
                billRecordCopy.set("id", record.get("id"));
                billRecordCopy.set("service_status", WebConstant.BillStatus.FAILED.getKey());
                billRecordCopy.set("feed_back", errMsg);
                Db.update("oa_head_payment", billRecordCopy);
                Db.update(Db.getSql("origin_data_oa.updProcessStatus"), WebConstant.OaProcessStatus.OA_TRADE_FAILED.getKey(),
                        errMsg, originDateId);
            }

        }

        return true;
    }


    /**
     * @param paramsToRecord
     * @param userInfo
     * @param uodpInfo
     * @ 支付作废___未添加事物的作废功能,不使用了
     */
    public void payOff_old(Record paramsToRecord, UserInfo userInfo, UodpInfo uodpInfo) throws Exception {
        //总公司支付作废按钮 oa_head_payment  , oa_origin_data 表

        final List<Long> ids = paramsToRecord.get("ids");
        final List<Integer> persist_versions = paramsToRecord.get("persist_version");
        for (int i = 0; i < ids.size(); i++) {
            long id = TypeUtils.castToLong(ids.get(i));
            Integer old_version = TypeUtils.castToInt(persist_versions.get(i));
            Record innerRec = Db.findById("oa_head_payment", "id", id);
            if (innerRec == null) {
                throw new ReqDataException("未找到有效的单据!");
            }

            /*//申请单创建人id非登录用户，不允许进行操作
            if (!TypeUtils.castToLong(innerRec.get("create_by")).equals(userInfo.getUsr_id())) {
                throw new ReqDataException("无权进行此操作！");
            }*/

            Long originId = TypeUtils.castToLong(innerRec.get("ref_id"));
            int service_status = TypeUtils.castToInt(innerRec.get("service_status"));
            // 判断单据状态为“审批通过”或“已失败”时可以发送，其他状态需抛出异常！
            if (service_status == WebConstant.BillStatus.SAVED.getKey() || service_status == WebConstant.BillStatus.REJECT.getKey()
                    || service_status == WebConstant.BillStatus.FAILED.getKey()) {
                paramsToRecord.set("id", id);
                paramsToRecord.set("persist_version", old_version + 1);
                paramsToRecord.set("update_by", userInfo.getUsr_id());
                paramsToRecord.set("update_on", new Date());
                paramsToRecord.set("service_status", WebConstant.BillStatus.CANCEL.getKey());
                paramsToRecord.set("feed_back", TypeUtils.castToString(paramsToRecord.get("feed_back")));
                Record set = new Record();
                Record where = new Record();
                set.set("persist_version", old_version + 1);
                set.set("service_status", WebConstant.BillStatus.CANCEL.getKey());
                set.set("update_by", userInfo.getUsr_id());
                set.set("update_on", new Date());
                where.set("id", id);
                where.set("persist_version", old_version);
                boolean flag = CommonService.update("oa_head_payment", set, where);
                if (flag) {
                    set.clear();
                    where.clear();
                    Record originRecord = Db.findById("oa_origin_data", "id", originId);
                    if (null == originRecord) {
                        throw new ReqDataException("未找到此单据的原始数据!");
                    }
                    set.set("process_status", 5);
                    set.set("lock_id", originId);
                    set.set("interface_status", 4);
                    set.set("interface_fb_code", "P0098");
                    set.set("interface_fb_msg", TypeUtils.castToString(paramsToRecord.get("feed_back")));
                    where.set("id", originId);
                    flag = CommonService.update("oa_origin_data", set, where);
                    if (flag) {
                        //调用callback接口
                        originRecord = Db.findById("oa_origin_data", "id", originId);
                        oaCallback.callback(originRecord);
                    } else {
                        throw new DbProcessException("单据作废失败!");
                    }
                } else {
                    throw new DbProcessException("单据作废失败!");
                }
            } else {
                throw new ReqDataException("单据状态不正确!");
            }
        }

    }
    
    /**
     * @param paramsToRecord
     * @param userInfo
     * @param uodpInfo
     * @ 支付作废
     */
    public void payOff(Record paramsToRecord, UserInfo userInfo, UodpInfo uodpInfo) throws Exception {
        //总公司支付作废按钮 oa_head_payment  , oa_origin_data 表
        final List<Long> ids = paramsToRecord.get("ids");
        final List<Integer> persist_versions = paramsToRecord.get("persist_version");
        for (int i = 0; i < ids.size(); i++) {
            long id = TypeUtils.castToLong(ids.get(i));
            Integer old_version = TypeUtils.castToInt(persist_versions.get(i));
            Record innerRec = Db.findById("oa_head_payment", "id", id);
            if (innerRec == null) {
                throw new ReqDataException("未找到有效的单据!");
            }
            Long originId = TypeUtils.castToLong(innerRec.get("ref_id"));
            int service_status = TypeUtils.castToInt(innerRec.get("service_status"));
            String feed_back = TypeUtils.castToString(paramsToRecord.get("feed_back"));
            // 判断单据状态为“审批通过”或“已失败”时可以发送，其他状态需抛出异常！
            if (service_status == WebConstant.BillStatus.SAVED.getKey() || service_status == WebConstant.BillStatus.REJECT.getKey()
                    || service_status == WebConstant.BillStatus.FAILED.getKey()) {    
            	String errorMessage = this.payOff(id,old_version,feed_back,userInfo,uodpInfo,originId);
                if(errorMessage != null){
                	throw new ReqDataException(errorMessage);
                }
            }else{          	
                throw new ReqDataException("此条单据信息已过期!");               
            }
        }

    }

    /**
     * 作废,添加事物
     * @param id
     * @param old_version
     * @param feed_back
     * @param userInfo
     * @param uodpInfo
     * @param originId 
     */
	private String payOff(final long id, final Integer old_version, final String feed_back, final UserInfo userInfo, UodpInfo uodpInfo, final Long originId) {
		// TODO Auto-generated method stub
		String errmsg = null;
		//添加事物
		boolean flag = Db.tx(new IAtom() {	
			@Override
			public boolean run() throws SQLException {
				 Record set = new Record();
	             Record where = new Record();
	             set.set("persist_version", old_version + 1);
	             set.set("service_status", WebConstant.BillStatus.CANCEL.getKey());
	             set.set("update_by", userInfo.getUsr_id());
	             set.set("update_on", new Date());
	             where.set("id", id);
	             where.set("persist_version", old_version);
	             int updateRows = CommonService.updateRows("oa_head_payment", set, where);
	             if(updateRows == 1){
	            	 log.debug("==================更新oa_head_payment条数==="+updateRows);
	            	 set.clear();
	                 where.clear();
	                 Record originRecord = Db.findById("oa_origin_data", "id", originId);
	                 if (null == originRecord) {
	                	 log.error("=======oa_origin_data此条单据原始数据信息已过期========");
		            	 return false ;
	                 }
	                 set.set("process_status", WebConstant.OaProcessStatus.OA_TRADE_CANCEL.getKey());
	                 set.set("lock_id", originId);
	                 set.set("interface_status", WebConstant.OaInterfaceStatus.OA_INTER_PROCESS_F.getKey());
	                 set.set("interface_fb_code", "P0098");
	                 set.set("interface_fb_msg", feed_back);
	                 where.set("id", originId);
	                 int rows = CommonService.updateRows("oa_origin_data", set, where);
	                 if(rows == 1){
	                	return true ;
	                 }else{
	                	 log.error("=======此条单据原始数据信息更新条数==="+rows);
	                	 return false ;
	                 }
	             }else{
	            	 log.error("=====oa_head_payment此条单据信息已过期,请刷新页面=====") ;
	            	 return false ;
	             }
			}
		});
		if(flag){
			log.debug("===========作废单据,更新数据库成功,开始回调接口===");
			try {
                Record originRecord = Db.findById("oa_origin_data", "id", originId);
				oaCallback.callback(originRecord);
			} catch (Exception e) {
				log.error("============回调接口异常了!========");
				e.printStackTrace();
				errmsg = "回调OA接口异常";
			}
		}else{
			log.error("============事物状态flag========"+flag);
			errmsg = "单据信息状态过期";
		}
		return errmsg ;
	}
    
}

