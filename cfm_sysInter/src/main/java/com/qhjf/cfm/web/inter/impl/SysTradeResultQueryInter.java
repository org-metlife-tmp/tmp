package com.qhjf.cfm.web.inter.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.ibm.icu.text.SimpleDateFormat;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.inter.api.ISingleResultChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.config.GmfConfigAccnoSection;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.manager.SysInterManager;
import com.qhjf.cfm.web.webservice.nc.callback.NcCallback;
import com.qhjf.cfm.web.webservice.oa.callback.OaCallback;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

public class SysTradeResultQueryInter implements ISysAtomicInterface {

    private static Logger log = LoggerFactory.getLogger(SysTradeResultQueryInter.class);
    private ISingleResultChannelInter channelInter;
    private Record instr;

    @Override
    public Record genInstr(Record record) {
        this.instr = new Record();
        Date date = new Date();
        instr.set("bank_serial_number", record.getStr("bank_serial_number"));
        instr.set("source_ref", record.getStr("source_ref"));
        instr.set("bill_id", record.getLong("id"));
        instr.set("process_bank_type", record.getStr("process_bank_type"));
        instr.set("pre_query_time", DateKit.toDate(DateKit.toStr(date, DateKit.timeStampPattern)));
        return this.instr;
    }

    @Override
    public Record getInstr() {
        return this.instr;
    }

    public Record updateBillStatus(int status, Record billRecord) {
        if (status == 1) {
            billRecord.set("service_status", WebConstant.BillStatus.SUCCESS.getKey());
        } else {
            billRecord.set("service_status", WebConstant.BillStatus.FAILED.getKey());
        }
        return billRecord;
    }

    @Override
    public void callBack(String jsonStr) throws Exception {
        log.debug("查询交易状态指令回写开始");
        Db.delete("trade_result_query_instr_queue_lock", instr);
        if(jsonStr == null || jsonStr.length() == 0){
        	log.error("交易状态返回报文为空,不错处理");
        	return;
        }
        JSONObject json = JSONObject.parseObject(jsonStr);
        json.put("bank_serial_number", this.getInstr().getStr("bank_serial_number"));
        final Record parseRecord = channelInter.parseResult(json.toJSONString());
        final int status = parseRecord.getInt("status");
        if (status == WebConstant.PayStatus.SUCCESS.getKey() || status == WebConstant.PayStatus.FAILD.getKey()) {
            final Record instrRecord = Db.findById("single_pay_instr_queue", "id", instr.getLong("bill_id"));
            if (instrRecord == null) {
                throw new Exception("发送指令不存在");
            }
            final String source_ref = instrRecord.getStr("source_ref");
            final String primaryKey = SysInterManager.getSourceRefPrimaryKey(source_ref);
            final Record billRecord = Db.findById(instrRecord.getStr("source_ref"), primaryKey, instrRecord.getLong("bill_id"));
            if (billRecord == null) {
                throw new Exception("单据不存在");
            }

            if ("oa_head_payment".equals(instrRecord.getStr("source_ref")) ||
                    "oa_branch_payment_item".equals(instrRecord.getStr("source_ref"))) {
                oaCallBack(billRecord, instrRecord, status, parseRecord);
            } else if("gmf_bill".equals(instrRecord.getStr("source_ref"))){
            	gmfCallBack(billRecord, instrRecord, status, parseRecord);
            }else if("nc_head_payment".equals(instrRecord.getStr("source_ref"))){
                ncCallBack(billRecord, instrRecord, status, parseRecord);
            }
            else {

                boolean flag = Db.tx(new IAtom() {
                    @Override
                    public boolean run() throws SQLException {

                        int persist_version = TypeUtils.castToInt(billRecord.get("persist_version"));
                        Record bill_setRecord = new Record().set("persist_version", persist_version + 1);
                        Record bill_whereRecord = new Record().set(primaryKey, billRecord.get(primaryKey))
                                .set("persist_version", persist_version);

                        Record instr_setRecord = new Record().set("status", status).set("init_resp_time", new Date()); //添加初始反馈时间;

                        Record instr_whereRecord = new Record().set("id", instrRecord.getLong("id"))
                                .set("status", WebConstant.PayStatus.HANDLE.getKey());

                        String statusField = SysInterManager.getStatusFiled(source_ref);
                        Integer statusEnum = null;
                        if (status == WebConstant.PayStatus.SUCCESS.getKey()) {
                            statusEnum = SysInterManager.getSuccessStatusEnum(source_ref);
                            bill_setRecord.set(statusField, statusEnum);
                            bill_setRecord.set("feed_back", "success");
                        } else {
                            statusEnum = SysInterManager.getFailStatusEnum(source_ref);
                            bill_setRecord.set(statusField, statusEnum);
                            bill_setRecord.set("feed_back", parseRecord.getStr("message"));
                        }

                        if (CommonService.updateRows(source_ref, bill_setRecord, bill_whereRecord) == 1) { //修改单据状态
                            return CommonService.updateRows("single_pay_instr_queue", instr_setRecord, instr_whereRecord) == 1;
                        } else {
                            log.error("数据过期！");
                            return false;
                        }
                    }
                });
                if (!flag) {
                    log.error("修改单据状态失败！");
                }

            }

        }
        log.debug("查询交易状态指令回写结束");
    }
   

	@Override
    public void setChannelInter(IChannelInter channelInter) {
        this.channelInter = (ISingleResultChannelInter) channelInter;
    }

    @Override
    public IChannelInter getChannelInter() {
        return this.channelInter;
    }

    @Override
    public void callBack(Exception e) throws Exception {
        Db.delete("trade_result_query_instr_queue_lock", instr);
    }

    private void oaCallBack(final Record billRecord, final Record instrRecord, final int status, final Record parseRecord) throws Exception {

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Record instr_setRecord = new Record().set("status", status).set("init_resp_time", new Date()); //添加初始反馈时间;;
                Record instr_whereRecord = new Record().set("id", instrRecord.getLong("id"));

                Record bill_setRecord = new Record().set("persist_version", billRecord.getInt("persist_version") + 1);  //乐观锁机制
                Record bill_whereRecord = new Record().set("id", billRecord.getLong("id"))
                        .set("persist_version", billRecord.getInt("persist_version"));
                String statusField = SysInterManager.getStatusFiled(instrRecord.getStr("source_ref"));
                if (status == WebConstant.PayStatus.SUCCESS.getKey()) {
                    bill_setRecord.set("feed_back", "success");
                    instr_setRecord.set("bank_err_msg", " ").set("bank_err_code", "");
                    if ("oa_branch_payment_item".equals(instrRecord.getStr("source_ref"))) {
                        bill_setRecord.set("service_status", WebConstant.PayStatus.SUCCESS.getKey());
                        bill_whereRecord.set("service_status", WebConstant.PayStatus.HANDLE.getKey());
                        if (CommonService.updateRows("oa_branch_payment_item", bill_setRecord, bill_whereRecord) == 1) {
                            if (billRecord.getInt("item_type") == 2) { //返回的是分公司付款的指令状态，进行主单的更新
                                Long baseId = billRecord.getLong("base_id");
                                Record oaBranchPayment = Db.findById("oa_branch_payment", "id", baseId);

                                Long originDataId = oaBranchPayment.getLong("ref_id");
                                /**
                                 * 修改主单状态，原始表状态，并加入oa回调队列
                                 */
                                if (CommonService.updateRows("oa_branch_payment",
                                        new Record().set("service_status", WebConstant.BillStatus.SUCCESS.getKey())
                                                .set("feed_back", "success"),
                                        new Record().set("id", baseId).set("service_status", WebConstant.BillStatus.PROCESSING.getKey())) == 1) {
                                    Db.update(Db.getSql("oa_interface.updOriginDataInterfaceStatus"),
                                            WebConstant.OaInterfaceStatus.OA_INTER_PROCESS_S.getKey(), null, null,
                                            WebConstant.OaProcessStatus.OA_TRADE_SUCCESS.getKey(), originDataId);
                                    new OaCallback().callback(Db.findById("oa_origin_data", originDataId));
                                }
                            } else {
                                log.info("返回的是分公司付款，下拨指令的指令状态");
                            }
                        } else {
                            log.error("已进行过状态更新！");
                            return false;
                        }
                    } else if ("oa_head_payment".equals(instrRecord.getStr("source_ref"))) {
                        bill_setRecord.set("service_status", WebConstant.BillStatus.SUCCESS.getKey());
                        bill_setRecord.set("feed_back", "success");
                        bill_whereRecord.set(statusField, WebConstant.BillStatus.PROCESSING.getKey());
                        if (CommonService.updateRows("oa_head_payment", bill_setRecord, bill_whereRecord) == 1) {
                            /**
                             * 修改原始表状态，并加入oa回调队列
                             */
                            Long originDataId = billRecord.getLong("ref_id");
                            Db.update(Db.getSql("oa_interface.updOriginDataInterfaceStatus"),
                                    WebConstant.OaInterfaceStatus.OA_INTER_PROCESS_S.getKey(), null, null,
                                    WebConstant.OaProcessStatus.OA_TRADE_SUCCESS.getKey(), originDataId);
                            new OaCallback().callback(Db.findById("oa_origin_data", originDataId));
                        } else {
                            log.error("已进行过状态更新！");
                            return false;
                        }
                    } else {
                        log.error("source_ref is " + instrRecord.getStr("source_ref") + ", is error");
                        return false;
                    }
                } else if (status == WebConstant.PayStatus.FAILD.getKey()) {
                    bill_setRecord.set("feed_back", parseRecord.getStr("message"));
                    instr_setRecord.set("bank_err_msg", parseRecord.getStr("message"));
                    if ("oa_branch_payment_item".equals(instrRecord.getStr("source_ref"))) {
                        bill_setRecord.set("service_status", WebConstant.PayStatus.FAILD.getKey());
                        bill_whereRecord.set("service_status", WebConstant.PayStatus.HANDLE.getKey());
                        if (CommonService.updateRows("oa_branch_payment_item", bill_setRecord, bill_whereRecord) == 1) {
                            Long baseId = billRecord.getLong("base_id");
                            Record oaBranchPayment = Db.findById("oa_branch_payment", "id", baseId);

                            Long originDataId = oaBranchPayment.getLong("ref_id");
                            if (CommonService.updateRows("oa_branch_payment",
                                    new Record().set("service_status", WebConstant.BillStatus.FAILED.getKey())
                                            .set("feed_back", parseRecord.getStr("message")),
                                    new Record().set("id", baseId).set("service_status", WebConstant.BillStatus.PROCESSING.getKey())) == 1) {
                                Db.update(Db.getSql("oa_interface.updOriginDataProcessStatus"),
                                        WebConstant.OaProcessStatus.OA_TRADE_FAILED.getKey(), parseRecord.getStr("message"),
                                        parseRecord.getStr("message"), originDataId);
                            }
                        } else {
                            log.error("已进行过状态更新！");
                            return false;
                        }
                    } else if ("oa_head_payment".equals(instrRecord.getStr("source_ref"))) {
                        bill_setRecord.set("service_status", WebConstant.BillStatus.FAILED.getKey());
                        bill_whereRecord.set(statusField, WebConstant.BillStatus.PROCESSING.getKey());

                        if (CommonService.updateRows("oa_head_payment", bill_setRecord, bill_whereRecord) == 1) {

                            Long originDataId = billRecord.getLong("ref_id");
                            /**
                             * 修改原始表状态，并加入oa回调队列
                             */
                            Db.update(Db.getSql("oa_interface.updOriginDataProcessStatus"),
                                    WebConstant.OaProcessStatus.OA_TRADE_FAILED.getKey(), parseRecord.getStr("message"),
                                    parseRecord.getStr("message"), originDataId);

                        } else {
                            log.error("已进行过状态更新！");
                            return false;
                        }
                    } else {
                        log.error("source_ref is " + instrRecord.getStr("source_ref") + ", is error");
                        return false;
                    }

                }
                //修改指令信息
                return CommonService.update("single_pay_instr_queue", instr_setRecord, instr_whereRecord);
            }
        });

        if (flag) {
            if (status == WebConstant.PayStatus.SUCCESS.getKey()
                    && "oa_branch_payment_item".equals(instrRecord.getStr("source_ref"))
                    && billRecord.getInt("item_type") == 1) { //oa_branch_payment_item 的下拨指令成功， 开始发送支付指令
                sendBanchPayment(billRecord);
            }
        } else {
            log.error("修改单据状态失败！");
        }

    }
    private void ncCallBack(final Record billRecord, final Record instrRecord, final int status, final Record parseRecord) throws Exception {

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Record instr_setRecord = new Record().set("status", status).set("init_resp_time", new Date()); //添加初始反馈时间;;
                Record instr_whereRecord = new Record().set("id", instrRecord.getLong("id"));

                Record bill_setRecord = new Record().set("persist_version", billRecord.getInt("persist_version") + 1);  //乐观锁机制
                Record bill_whereRecord = new Record().set("id", billRecord.getLong("id"))
                        .set("persist_version", billRecord.getInt("persist_version"));
                String statusField = SysInterManager.getStatusFiled(instrRecord.getStr("source_ref"));
                if (status == WebConstant.PayStatus.SUCCESS.getKey()) {
                    bill_setRecord.set("feed_back", "success");
                    instr_setRecord.set("bank_err_msg", " ").set("bank_err_code", "");
                    if ("nc_head_payment".equals(instrRecord.getStr("source_ref"))) {
                        bill_setRecord.set("service_status", WebConstant.BillStatus.SUCCESS.getKey());
                        bill_setRecord.set("feed_back", "success");
                        bill_whereRecord.set(statusField, WebConstant.BillStatus.PROCESSING.getKey());
                        if (CommonService.updateRows("nc_head_payment", bill_setRecord, bill_whereRecord) == 1) {
                            /**
                             * 修改原始表状态，并加入oa回调队列
                             */
                            Long originDataId = billRecord.getLong("ref_id");
                            Db.update(Db.getSql("nc_interface.updOriginDataInterfaceStatus"),
                                    WebConstant.OaInterfaceStatus.OA_INTER_PROCESS_S.getKey(), null, null,
                                    WebConstant.OaProcessStatus.OA_TRADE_SUCCESS.getKey(), originDataId);
                            new NcCallback().callback(Db.findById("nc_origin_data", originDataId),parseRecord);
                        } else {
                            log.error("已进行过状态更新！");
                            return false;
                        }
                    } else {
                        log.error("source_ref is " + instrRecord.getStr("source_ref") + ", is error");
                        return false;
                    }
                } else if (status == WebConstant.PayStatus.FAILD.getKey()) {
                    bill_setRecord.set("feed_back", parseRecord.getStr("message"));
                    instr_setRecord.set("bank_err_msg", parseRecord.getStr("message"));
                     if ("nc_head_payment".equals(instrRecord.getStr("source_ref"))) {
                        bill_setRecord.set("service_status", WebConstant.BillStatus.FAILED.getKey());
                        bill_whereRecord.set(statusField, WebConstant.BillStatus.PROCESSING.getKey());

                        if (CommonService.updateRows("nc_head_payment", bill_setRecord, bill_whereRecord) == 1) {

                            Long originDataId = billRecord.getLong("ref_id");
                            /**
                             * 修改原始表状态，并加入nc回调队列
                             */
                            Db.update(Db.getSql("nc_interface.updOriginDataProcessStatus"),
                                    WebConstant.OaProcessStatus.OA_TRADE_FAILED.getKey(), parseRecord.getStr("message"),
                                    parseRecord.getStr("message"), originDataId);
                            Db.update(Db.getSql("nc_interface.updOriginDataInterfaceStatus"),
                                    WebConstant.OaInterfaceStatus.OA_INTER_PROCESS_F.getKey(), "P00098", parseRecord.getStr("message"),
                                    WebConstant.OaProcessStatus.OA_TRADE_FAILED.getKey(), originDataId);
                            new NcCallback().callback(Db.findById("nc_origin_data", originDataId),null);
                        } else {
                            log.error("已进行过状态更新！");
                            return false;
                        }
                    } else {
                        log.error("source_ref is " + instrRecord.getStr("source_ref") + ", is error");
                        return false;
                    }

                }
                //修改指令信息
                return CommonService.update("single_pay_instr_queue", instr_setRecord, instr_whereRecord);
            }
        });

        if (!flag){
            log.error("修改单据状态失败！");
        }

    }
    /**
     * 柜面付更新表数据
     * @param billRecord  单据数据
     * @param instrRecord  指令数据
     * @param status  银行返回状态
     * @param parseRecord  返回报文
     */
    private void gmfCallBack(final Record billRecord, final Record instrRecord, final int status, final Record parseRecord) {
    	log.info("=========定时任务查询柜面付指令结果");
		boolean flag = Db.tx(new IAtom() {			
			@Override
			public boolean run() throws SQLException {
				String source_ref = instrRecord.getStr("source_ref");
                String statusField = SysInterManager.getStatusFiled(source_ref);
                final String primaryKey = SysInterManager.getSourceRefPrimaryKey(source_ref);

                int persist_version = TypeUtils.castToInt(billRecord.get("persist_version"));
                Record bill_setRecord = new Record().set("persist_version", persist_version + 1);
                Record bill_whereRecord = new Record().set(primaryKey, billRecord.get(primaryKey))
                        .set("persist_version", persist_version);

                Record instr_setRecord = new Record().set("status", status).set("init_resp_time", new Date()); //添加初始反馈时间;;
                Record instr_whereRecord = new Record().set("id", instrRecord.getLong("id"))
                        .set("status", WebConstant.PayStatus.HANDLE.getKey());

                Integer statusEnum = null;
                int origin_status = 1;
                if (status == WebConstant.PayStatus.SUCCESS.getKey()) {
                    statusEnum = SysInterManager.getSuccessStatusEnum(source_ref);
                    bill_setRecord.set(statusField, statusEnum);
                    bill_setRecord.set("feed_back", "success");
                } else {
                    statusEnum = SysInterManager.getFailStatusEnum(source_ref);
                    bill_setRecord.set(statusField, statusEnum);
                    bill_setRecord.set("feed_back", parseRecord.getStr("message"));
                    origin_status = 2 ;
                }

                if (CommonService.updateRows(source_ref, bill_setRecord, bill_whereRecord) == 1) { //修改单据状态
                    if (CommonService.updateRows("single_pay_instr_queue", instr_setRecord, instr_whereRecord) == 1) {                        	                       	                   	
                    	if(WebConstant.YesOrNo.YES.getKey()==TypeUtils.castToInt(billRecord.get("is_match"))) {
                    		log.info("====来自未匹配数据====");     
                    		//如果指令成功,更新为已退费 . 
                    		if( 1== origin_status) {
                    			log.info("====来自未匹配数据,指令成功===="); 
                    			int	match_status = WebConstant.SftRecvCounterMatchStatus.YTF.getKey() ;
                    			return CommonService.update("recv_counter_match", 
                    					new Record().set("match_status", match_status),
                    					new Record().set("id", billRecord.get("legal_id")));
                    		} else {
                    			log.info("====来自未匹配数据,指令失败===="); 
                    			Record recv_counter_match = Db.findById("recv_counter_match", "id", billRecord.get("legal_id"));
                    			boolean update = CommonService.update("recv_counter_match", 
                    					new Record().set("delete_flag", 1),
                    					new Record().set("id", billRecord.get("legal_id")));
                    			if(!update) {
                    				log.error("====更新recv_counter_match为删除失败===="+billRecord.get("legal_id"));
                    				return update ;
                    			}
                    			recv_counter_match.remove("id");
                    			recv_counter_match.set("status", WebConstant.SftLegalData.NOGROUP.getKey());
                    			recv_counter_match.set("match_status", WebConstant.SftRecvCounterMatchStatus.DPP.getKey());
                    			return Db.save("recv_counter_match", "id", recv_counter_match);
                    		}
                    		       		
                    	}else {
                    		if(0 == billRecord.getInt("source_sys")) {
                        		log.info("====LA系统数据");
                        		int update = Db.update(Db.getSql("pay_counter.updateLaOriginData"), origin_status ,bill_setRecord.get("feed_back") ,billRecord.get("origin_id"));
                        		return  update > 0 ;
                        	} else {
                        		log.info("====EBS系统数据");
                        		String pay_acc_no = GmfConfigAccnoSection.getInstance().getAccno();
    							log.info("===========配置文件获取到的账号======="+pay_acc_no);
    							
    							final Map<String, Object> aRowData = TableDataCacheUtil.getInstance().getARowData("account", "acc_no", pay_acc_no);

    							String bankcode = null;
    							if (null != aRowData) {
    								bankcode = TypeUtils.castToString(aRowData.get("bankcode"));
    							}else {
    								log.error("=====未查询到此账号");
    								bankcode = String.format("银行账号：(%s)未维护到account表", pay_acc_no);
    							}
    							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    							SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
    							String pay_date = sdf.format(new Date());
    							String pay_time = sdf1.format(new Date());
    							int update = Db.update(Db.getSql("pay_counter.updateEbsOriginData"), origin_status ,bill_setRecord.get("feed_back"), pay_acc_no , 
    									bankcode , pay_date ,pay_time , billRecord.get("origin_id")) ;
                        		return update > 0 ;
                        	}
                    	}                    	                  	
                    };
                    log.error("=======更新指令表失败");
                    return false ;
                } else {
                    log.error("数据过期！");
                    return false;
                }			
			}
		});
		if (!flag) {
            log.error("回写更新数据库失败！");
        }
        //将回调写在事物外,回调失败,不影响表的回写
        Record originRecord = null ;
        if(0 == billRecord.getInt("source_sys")) {
			originRecord = Db.findById("la_origin_pay_data", "id", billRecord.getInt("origin_id"));                        			
        }else {
			originRecord = Db.findById("ebs_origin_pay_data", "id", billRecord.getInt("origin_id"));                        			
        }
        SftCallBack callback = new SftCallBack();
		callback.callback(billRecord.getInt("source_sys"), originRecord);
	}
    
    
    
    private void sendBanchPayment(final Record record)  {
        final Record branchRecord = Db.findFirst(Db.getSql("branch_org_oa.findDetailByItem"), 2, record.get("base_id"));
        final int oldRepearCount = branchRecord.getInt("repeat_count");

        String payCnaps = branchRecord.getStr("pay_bank_cnaps");
        String payBankCode = payCnaps.substring(0, 3);
        IChannelInter channelInter = null;
        String bankSerialNumber = null;
        try {
            channelInter = ChannelManager.getInter(payBankCode, "SinglePay");
            bankSerialNumber = ChannelManager.getSerianlNo(payBankCode);
        } catch (Exception e) {
            e.printStackTrace();
            Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    Long baseId = branchRecord.getLong("base_id");
                    int changeCount = CommonService.updateRows("oa_branch_payment_item",
                            new Record().set("service_status", WebConstant.PayStatus.FAILD.getKey())
                                    .set("repeat_count", oldRepearCount + 1),
                            new Record().set("id", branchRecord.get("id"))
                                    .set("service_status", WebConstant.PayStatus.INIT.getKey()).set("repeat_count", oldRepearCount));
                    if (changeCount == 1) {
                        Record oaBranchPayment = Db.findById("oa_branch_payment", "id", baseId);

                        Long originDataId = oaBranchPayment.getLong("ref_id");
                        if (CommonService.updateRows("oa_branch_payment",
                                new Record().set("service_status", WebConstant.BillStatus.FAILED.getKey()),
                                new Record().set("id", baseId).set("service_status", WebConstant.BillStatus.PROCESSING.getKey())) == 1) {
                            Db.update(Db.getSql("oa_interface.updOriginDataProcessStatus"),
                                    WebConstant.OaProcessStatus.OA_TRADE_FAILED.getKey(), "银行渠道不可用",
                                    "银行渠道不可用", originDataId);
                        }

                    } else {
                        log.error("数据错误！");
                        return false;
                    }
                    return true;
                }
            });
        }

        branchRecord.set("source_ref", "oa_branch_payment_item");
        branchRecord.set("repeat_count", oldRepearCount + 1);
        branchRecord.set("bank_serial_number", bankSerialNumber);
        SysOaSinglePayInter sysInter = new SysOaSinglePayInter();
        sysInter.setChannelInter(channelInter);
        final Record instr = sysInter.genInstr(branchRecord);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean save = Db.save("single_pay_instr_queue", instr);
                if (save) {
                    int result = Db.update(Db.getSql("branch_org_oa.updBillById"),
                            instr.getStr("bank_serial_number"), instr.getInt("repeat_count"),
                            WebConstant.PayStatus.HANDLE.getKey(), instr.getStr("instruct_code"),
                            branchRecord.get("id"), oldRepearCount, branchRecord.getInt("service_status"));
                    return result == 1;
                }
                return save;
            }
        });
        // 存储成功， 添加到队列
        if (flag) {
            log.debug("sysInter :"+sysInter);
            log.debug("instr :"+instr);
            log.debug("instr'id is:"+instr.get("id"));
            QueueBean bean = new QueueBean(sysInter, channelInter.genParamsMap(instr), payBankCode);
            ProductQueue productQueue = new ProductQueue(bean);
            new Thread(productQueue).start();
        } else {
           log.error("发送失败！");
        }
    }
}
