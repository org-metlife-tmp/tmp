package com.qhjf.cfm.web.service;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.WorkflowException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.BizSerialnoGenTool;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.DateFormatThreadLocal;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.WfRequestObj;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.queue.RecvCounterConfirmQueue;
import com.qhjf.cfm.web.webservice.sft.counter.recv.RecvCounterRemoteCall;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.GroupCustomerAccCancelReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.PersonBillCancelReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.PersonBillComfirmReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.req.PersonBillQryReqBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.GroupCustomerAccCancelRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.PersonBillCancelRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.PersonBillConfirmRespBean;
import com.qhjf.cfm.web.webservice.sft.counter.recv.bean.resp.PersonBillQryRespBean;

/**
 * 柜面收
 * 
 * @author pc_liweibing
 *
 */
public class RecvCounterService {

	private final static Log logger = LogbackLog.getLog(RecvCounterService.class);

	private  RecvCounterRemoteCall recvCounterRemoteCall = new RecvCounterRemoteCall();
	
    private RecvCounterRemoteCall recvCounter = new RecvCounterRemoteCall();


	/**
	 * 个单新增   异步
	 * @param record
	 * @param userInfo
	 * @param curUodp
	 * @throws ReqDataException
	 * @throws ParseException
	 * @throws EncryAndDecryException
	 */
	public void add(final Record record, final UserInfo userInfo, final UodpInfo curUodp) throws ReqDataException, ParseException, EncryAndDecryException{
		// 是否包含中文
		String currency = TypeUtils.castToString(record.get("currency"));
		boolean flag = StringKit.isContainChina(currency);
		if(!flag) {
			Record currencyRec = Db.findById("currency", "id", TypeUtils.castToString(record.get("currency")));
			currency = currencyRec.getStr("name");
		}
		final List<Record> policys = record.get("policy_infos");
		logger.info("====共传输保单号===="+policys.size());
		//Curreny需要取一下值
		String recv_date = TypeUtils.castToString(record.get("recv_date"));
		//String batch_process_no = TypeUtils.castToString(record.get("batch_process_no"));
		String recv_mode = TypeUtils.castToString(record.get("recv_mode"));
		String use_funds = TypeUtils.castToString(record.get("use_funds"));
		String bill_status = TypeUtils.castToString(record.get("bill_status"));
		String bill_number = TypeUtils.castToString(record.get("bill_number"));
		String bill_date = TypeUtils.castToString(record.get("bill_date"));
		String recv_bank_name = TypeUtils.castToString(record.get("recv_bank_name"));
		String recv_acc_no = TypeUtils.castToString(record.get("recv_acc_no"));
		String consumer_bank_name = TypeUtils.castToString(record.get("consumer_bank_name"));
		String consumer_acc_no = TypeUtils.castToString(record.get("consumer_acc_no"));
		String terminal_no = TypeUtils.castToString(record.get("terminal_no"));
		String payer_relation_insured = TypeUtils.castToString(record.get("payer_relation_insured"));
		String pay_reason = TypeUtils.castToString(record.get("pay_reason"));
		String bank_code = "31";
		Set<String> MD5 = new HashSet<>();
		final List<Record> records = new ArrayList<>();
		final List<Object> list = record.get("files");
		//总金额
		BigDecimal total_amount = TypeUtils.castToBigDecimal(record.get("amount"));
		BigDecimal total_amount_son =  new BigDecimal(0);

		//判断是否有重复票据编号
		if(bill_number != null){
			Record findBypjbh = Db.findFirst(Db.getSql("recv_counter.selbillnum"),bill_number);
			if(findBypjbh != null){
				throw new ReqDataException("待确认的保单票据票号已存在，请重新输入!") ;
			}

		}

		for (Record rec : policys) {
			String third_payment = TypeUtils.castToString(rec.get("third_payment"));
			String payer = TypeUtils.castToString(rec.get("payer"));
			String payer_cer_no = TypeUtils.castToString(rec.get("payer_cer_no"));
			String insure_bill_no = TypeUtils.castToString(rec.get("insure_bill_no"));
			String bill_org_id = TypeUtils.castToString(rec.get("bill_org_id"));
			String source_sys = TypeUtils.castToString(rec.get("source_sys"));
			//String bank_code = TypeUtils.castToString(rec.get("bank_code"));
			//String bank_code = "31";
			String amount = TypeUtils.castToString(rec.get("amount"));
			String insure_name = TypeUtils.castToString(rec.get("insure_name"));
			String insure_cer_no = TypeUtils.castToString(rec.get("insure_cer_no"));
			String isnot_electric_pay = TypeUtils.castToString(rec.get("isnot_electric_pay"));
            if(null!=isnot_electric_pay)
            {
                if(isnot_electric_pay.equals("0")){
                    isnot_electric_pay = "0";
                }else {
                    isnot_electric_pay = "1";
                }
            }
            String isnot_bank_transfer_premium = TypeUtils.castToString(rec.get("isnot_bank_transfer_premium"));
            if(null!=isnot_bank_transfer_premium)
            {
                if(isnot_bank_transfer_premium.equals("Y")){
                    isnot_bank_transfer_premium = "1";
                }else {
                    isnot_bank_transfer_premium = "0";
                }
            }
			String srce_bus = TypeUtils.castToString(rec.get("srce_bus"));
			String camp_aign = TypeUtils.castToString(rec.get("camp_aign"));
			String agnt_num  = TypeUtils.castToString(rec.get("agnt_num"));
			String bill_num = TypeUtils.castToString(rec.get("bill_number"));
			StringBuffer sb = new StringBuffer();
			sb.append(recv_date).append(currency).append(insure_bill_no).append(bill_org_id).append(source_sys).
					append(recv_mode).append(use_funds).append(bill_status).append(bill_num).append(bill_date).append(recv_bank_name).append(recv_acc_no).append(consumer_bank_name).
					append(consumer_acc_no).append(terminal_no).append(amount).append(insure_name).append(insure_cer_no).append(isnot_electric_pay).append(isnot_bank_transfer_premium).append(third_payment).append(payer).
					append(payer_cer_no).append(payer_relation_insured).append(pay_reason);
			//防重校验
			String md5String = MD5Kit.string2MD5(sb.toString());
			//record.set("md5", md5String) ;
			if(!MD5.add(md5String)) {
				logger.error("====录入的保单号重复了===="+insure_bill_no);
				throw new ReqDataException("待确认的保单存在重复") ;
			}
			Record findById = Db.findById("recv_counter_bill", "md5", md5String);
			if(findById != null) {
				logger.error("====录入的保单号已经确认完成了===="+insure_bill_no);
				throw new ReqDataException("待确认的保单在系统内已经存在!") ;
			}

			String serviceSerialNumber = BizSerialnoGenTool.getInstance().getSerial(WebConstant.MajorBizType.GMSGD);
			total_amount_son = total_amount_son.add(new BigDecimal(amount));
			Record insertRecord = new Record();
			//加密
			SymmetricEncryptUtil  util = SymmetricEncryptUtil.getInstance();

			insertRecord.set("create_on", new Date())
					.set("create_user_name", userInfo.getName())
					.set("md5", md5String)
					.set("create_by", userInfo.getUsr_id())
					.set("update_on", new Date())
					.set("update_by", userInfo.getUsr_id())
					.set("wait_match_flag", record.get("wait_match_flag"))
					.set("wait_match_id", record.get("wait_match_id"))
					.set("bill_type", WebConstant.SftRecvType.GDSK.getKey())
					.set("insure_cer_no", insure_cer_no)
					.set("total_amount", record.get("amount")) // 此批次号下的总金额
					.set("batch_process_no", record.get("batch_process_no")) // 批处理号
					.set("recv_date", record.get("recv_date")) // 收款日期
					.set("currency",currency) // 币种
					.set("recv_mode", recv_mode) // 收款方式
					.set("use_funds", use_funds) // 资金用途
					.set("bill_status", bill_status) // 票据状态
					.set("bill_number", bill_number) // 票据编号/票据票号
					.set("bill_date", record.get("bill_date")) // 票据日期
					.set("terminal_no", terminal_no) // 终端机编号
					.set("recv_acc_no", util.encrypt(recv_acc_no)) // 收款银行账号
					.set("recv_bank_name", recv_bank_name) // 收款银行 取的是bankcode
					.set("consumer_bank_name", consumer_bank_name) // 客户银行
					.set("consumer_acc_no", util.encrypt(consumer_acc_no)) // 客户账号
					.set("payer_relation_insured", record.get("payer_relation_insured")) // 与投保人关系
					.set("pay_reason", record.get("pay_reason")) // 代缴费原因
					.set("delete_flag", 0)
					.set("persist_version", 0)
					.set("attachment_count", list != null ? list.size() : 0) // 附件数量
					.set("recv_org_id", curUodp.getOrg_id()) // 收款机构
					.set("pay_status", WebConstant.SftRecvCounterPayStatus.QR.getKey()) // 直接将支付状态疯转改为确认
					// 开始封装保单号的相关信息
					.set("third_payment", rec.get("third_payment")) // 第三方缴费
					.set("payer", (payer == null || StringUtils.isBlank(payer))? insure_name : payer) // 缴费人
					.set("payer_cer_no", (payer_cer_no == null || StringUtils.isBlank(payer_cer_no))? insure_cer_no : payer_cer_no ) // 缴费人证件号
					.set("insure_bill_no",insure_bill_no) // 保单号
					.set("bill_org_id", TypeUtils.castToInt(bill_org_id)) // 保单机构
					.set("source_sys",source_sys) // 保单核心系统
					.set("amount", TypeUtils.castToBigDecimal(amount)) // 保单金额
					.set("insure_name", insure_name) // 投保人
					.set("insure_cer_no", insure_cer_no) // 投保人证件号
					.set("isnot_electric_pay", isnot_electric_pay) // 是否在电缴中
					// 是否银行转账中的保单缴费
					.set("isnot_bank_transfer_premium ", isnot_bank_transfer_premium)
					.set("service_serial_number", serviceSerialNumber)
					//.set("bank_code", bank_code)
					.set("bank_code", "31")
					.set("service_status", WebConstant.BillStatus.SAVED.getKey())
					.set("srce_bus", srce_bus)
					.set("camp_aign", camp_aign)
					.set("agnt_num", agnt_num);
			records.add(insertRecord);
		}

		//校验外部总金额与所有保单的子金额是否一致
		logger.info("====录入总金额===="+total_amount+"====所有保单金额之和===="+total_amount_son);
		if(total_amount_son.compareTo(total_amount) != 0) {
			throw new ReqDataException("录入的总金额与所有保单金额之和不一致");
		}

		// 此处可以将数据直接修改为确认完成了.未匹配来的数据同时要修改主表为已匹配
		boolean tx = Db.tx(new IAtom() {

			@Override
			public boolean run() throws SQLException {

				logger.info("====存储recv_counter_bill表====");
				for (int i = 0; i < records.size(); i++) {
					Record rec = records.get(i);
					boolean save = Db.save("recv_counter_bill", "id", rec);
					if(!save) {
						logger.error("====此保单号数据入库失败===="+rec.get("insure_bill_no"));
						return false ;
					}
					//开始库中插入共享附件
					CommonService.delFileRef(WebConstant.MajorBizType.GMSGD.getKey(), rec.getInt("id"));
					if (list != null && list.size() > 0) {
						// 保存附件
						boolean saveFileRef = CommonService.saveFileRef(WebConstant.MajorBizType.GMSGD.getKey(), rec.getInt("id"), list);
						if (!saveFileRef) {
							logger.error("====附件存储错误===="+rec.get("insure_bill_no"));
							return false ;
						}
					}
				}

				if(null !=  record.get("wait_match_flag")&& StringUtils.isNotBlank(record.getStr("wait_match_flag"))
						&& 1 == record.getInt("wait_match_flag")) {
					logger.info("====来源未匹配数据,更新匹配主表====");
					Integer wait_match_id = record.getInt("wait_match_id");
					boolean updateMatch = CommonService.update("recv_counter_match",
							new Record().set("match_on", new Date())
									.set("match_status", WebConstant.SftRecvCounterMatchStatus.YPP.getKey())
									.set("match_by",userInfo.getUsr_id())
									.set("match_user_name", userInfo.getName()),
							new Record().set("id", wait_match_id));
					logger.info("====更新recv_counter_match数据===="+wait_match_id);
					return updateMatch ;
				}
				return true;
			}
		});
		if (!tx) {
			throw new ReqDataException("柜面收个单确认失败");
		}
		logger.info("====校验成功,开启异步线程请求外部系统====");

		//再查一次保单，然后取到company和branch
		PersonBillQryRespBean InsureBillNo = null ;
		String insure_bill_no = record.getStr("insure_bill_no");
		PersonBillQryReqBean bean = new  PersonBillQryReqBean(insure_bill_no);
		InsureBillNo = recvCounterRemoteCall.qryBillByInsureBillNo(bean);
		String company = InsureBillNo.getCompany();
		String insureOrgCode = InsureBillNo.getInsureOrgCode();


		RecvCounterConfirmQueue recvCounterConfirmQueue = new RecvCounterConfirmQueue();
		recvCounterConfirmQueue.setUserInfo(userInfo);
		recvCounterConfirmQueue.setCurUodp(curUodp);
		recvCounterConfirmQueue.setRecord(record);
		recvCounterConfirmQueue.setCompany(company);
		recvCounterConfirmQueue.setInsureOrgCode(insureOrgCode);
		Thread thread =  new Thread(recvCounterConfirmQueue);
		thread.start();
	}
	/**
	 * 列表
	 * @param record
	 * @param curUodp
	 * @param pageNum 
	 * @param pageSize 
	 * @return 
	 * @throws BusinessException 
	 * @throws UnsupportedEncodingException 
	 */
	public Page<Record> list(Record record, UodpInfo curUodp, int pageSize, int pageNum) throws UnsupportedEncodingException, BusinessException {
		record.set("bill_type", WebConstant.SftRecvType.GDSK.getKey());
		record.set("wait_match_flag", 0);
		SqlPara sqlPara = Db.getSqlPara("recv_counter.personalList", Kv.by("map", record.getColumns()));
		Page<Record> paginate = Db.paginate(pageNum, pageSize, sqlPara);
		List<Record> list = paginate.getList();
		SymmetricEncryptUtil  util = SymmetricEncryptUtil.getInstance();
		util.recvmask(list);
		
		return paginate;
		
	}
  /*  *//**
     * 
     * @param record
     * @return
     *//*
	public Record totalinfo(Record record) {
		SqlPara sqlPara = Db.getSqlPara("", Kv.by("map", record.getColumns()));
        return Db.findFirst(sqlPara);
	}*/

	/**
	 * 查看详情,此处也可以上传附件
	 * @param record
	 * @return
	 * @throws BusinessException 
	 * @throws UnsupportedEncodingException 
	 */
	public Record detail(Record record) throws UnsupportedEncodingException, BusinessException {
        Integer id = TypeUtils.castToInt(record.get("id"));
        Record rec = Db.findById("recv_counter_bill", "id", id);
        if( null == rec) {
        	throw new ReqDataException("此条数据已过期,请刷新页面");
        }
        String batch_process_no = rec.getStr("batch_process_no");
        List<Record> find = Db.find(Db.getSql("recv_counter.detailList"), batch_process_no);
        rec.set("bill_org_name", find.get(0).get("bill_org_name"));
        rec.set("recv_mode", WebConstant.Sft_RecvPersonalCounter_Recvmode.getByKey(rec.getInt("recv_mode")).getDesc());
        rec.set("use_funds", WebConstant.SftRecvPersonalCounterUseFunds.getByKey(rec.getInt("use_funds")).getDesc());
        rec.set("bill_status", WebConstant.SftRecvCounterBillStatus.getByKey(rec.getInt("bill_status")).getDesc());
        Record const_bank_type = Db.findById("const_bank_type", "code", rec.get("consumer_bank_name"));
        rec.set("consumer_bank_name", const_bank_type.get("name"));
        rec.set("amount",rec.get("total_amount"));
        SymmetricEncryptUtil  util = SymmetricEncryptUtil.getInstance();
		util.recvmaskforSingle(rec);
        for (int i = 0; i < find.size(); i++) {
			Record reco = find.get(i);
			reco.set("source_sys", WebConstant.SftOsSourceCounter.getSftOsSource(rec.getInt("source_sys")).getDesc());
			//reco.set("isnot_electric_pay", 1 == reco.getInt("isnot_electric_pay")? "是" : "否");
			//reco.set("isnot_bank_transfer_premium", 1 == reco.getInt("isnot_bank_transfer_premium")? "是" : "否");
		}
        rec.set("policy_infos", find);

        return rec;
	}
	
	/**
	 * 获取柜面收中的批次号
	 * @param record
	 * @return
	 * @throws ReqDataException 
	 */
	public Record getBatchProcessno(Record record) throws ReqDataException {
		Integer recv_type = TypeUtils.castToInt(record.get("recv_type"));
		Record rec = new Record();
		String batch_process_no = "";
		if(WebConstant.SftRecvType.GDSK.getKey() == recv_type) {
			batch_process_no = RedisSericalnoGenTool.genBatchProcessno("GD"+DateKit.toStr(new Date(), "yyyyMMddHH"));
		}else if(WebConstant.SftRecvType.TDSK.getKey() == recv_type) {
			batch_process_no = RedisSericalnoGenTool.genBatchProcessno("TD"+DateKit.toStr(new Date(), "yyyyMMddHH"));
		}else{ 
			throw new ReqDataException("传输收款类型错误");
		}
		rec.set("batch_process_no", batch_process_no);
		return rec;
	}

	/**
	 * 撤回  同步
	 * @param record
	 * @param userInfo 
	 * @param uodpInfo 
	 * @return
	 * @throws ReqDataException 
	 */
	public void revoke(final Record record, final UserInfo userInfo, UodpInfo uodpInfo) throws ReqDataException {
		//点击撤回,撤回同一批次下的所有保单
		final Long usr_id = userInfo.getUsr_id();
		final Integer id = record.getInt("id");
		final Record recv_counter_data = Db.findById("recv_counter_bill", "id", id);
		if(null == recv_counter_data) {
			throw new ReqDataException("此条数据已过期,请刷新页面");
		}
		long userId = userInfo.getUsr_id();
		if(recv_counter_data.getLong("create_by") != userId){
			throw new ReqDataException("撤销人必须是当前数据录入人！");
		}
        //必须状态都是已确认. 创建时间为当天才允许撤销
        Date create_on = recv_counter_data.getDate("create_on");
        Integer pay_status = recv_counter_data.getInt("pay_status");
        if(!DateKit.toStr(create_on, "yyyyMMdd").equals(DateKit.toStr(new Date(), "yyyyMMdd"))){
        	throw new ReqDataException("只能对当天数据进行撤销");
        }
        if(pay_status != 1) {
        	throw new ReqDataException("只能对已确认数据进行撤销");
        }
        Integer service_status = TypeUtils.castToInt(recv_counter_data.get("service_status"));
        if(service_status != WebConstant.BillStatus.SAVED.getKey() && 
        		service_status != WebConstant.BillStatus.REJECT.getKey()) {
        	throw new ReqDataException("此条数据正处于审批状态,不允许再次撤销");
        }                
        // 走审批流
		List<Record> flows = null;
		try {
			flows = CommonService.displayPossibleWf(WebConstant.MajorBizType.GMS.getKey(),
					uodpInfo.getOrg_id(), null);
		} catch (BusinessException e) {
			e.printStackTrace();
			logger.error("============获取收付费审批流异常");
			throw new ReqDataException("柜面收个单撤销获取审批流异常,请联系管理员");
		}
		if (flows == null || flows.size() == 0) {
			logger.error("============未查询到收付费审批流");
			throw new ReqDataException("柜面收个单撤销未查询到审批流,请联系管理员进行配置");
		}
		Record flow = flows.get(0);
		recv_counter_data.set("define_id", flow.getLong("define_id"));
		WfRequestObj wfRequestObj = new WfRequestObj(WebConstant.MajorBizType.GMS, "recv_counter_bill",
				recv_counter_data) {
			@Override
			public <T> T getFieldValue(WebConstant.WfExpressType type) {
				return null;
			}

			@Override
			public SqlPara getPendingWfSql(Long[] inst_id, Long[] exclude_inst_id) {
				return null;
			}

		};
		WorkflowProcessService workflowProcessService = new WorkflowProcessService();
		boolean submitFlowFlg;
		try {
			submitFlowFlg = workflowProcessService.startWorkflow(wfRequestObj, userInfo);
		} catch (WorkflowException e) {
			e.printStackTrace();
			logger.error("=========收付费提交审批流失败");
			throw new ReqDataException("柜面收个单撤销提交审批流异常");
		}
		
        if (!submitFlowFlg) {
        	throw new ReqDataException("柜面收个单撤销提交审批流失败");
		}       
	}


	/**
	 * 详情确认
	 * @param record
	 * @throws ReqDataException 
	 */
	public void detailConfirm(Record record) throws ReqDataException {
		Integer id = TypeUtils.castToInt(record.get("id"));
        Record rec = Db.findById("recv_counter_bill", "id", id);
        if(null == rec) {
        	throw new ReqDataException("此条数据已过期,请刷新页面");
        }
        String batch_process_no = rec.getStr("batch_process_no");
        //因为是共享的附件
        List<Object> list = record.get("files");
        List<Record> recs = Db.find(Db.getSql("recv_counter.detailList"), batch_process_no);
        for (int i = 0; i < recs.size(); i++) {
        	Record red = recs.get(i);
        	// 删除附件
        	CommonService.delFileRef(WebConstant.MajorBizType.GMSGD.getKey(), red.getInt("id"));
        	if (list != null && list.size() > 0) {
        		// 保存附件
        		CommonService.saveFileRef(WebConstant.MajorBizType.GMSGD.getKey(), red.getInt("id"), list);
        	}				
		}
	}

	/**
	 * 获取保单信息
	 * @param record
	 * @return 
	 * @throws ReqDataException 
	 */
	public Record getPolicyInfo(Record record) throws ReqDataException {
		PersonBillQryRespBean qryBillByInsureBillNo = null ;
		logger.info("====进入接口查询保单信息====");
		try {
			String insure_bill_no = record.getStr("insure_bill_no");
			PersonBillQryReqBean bean = new  PersonBillQryReqBean(insure_bill_no);
			qryBillByInsureBillNo = recvCounterRemoteCall.qryBillByInsureBillNo(bean);			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ReqDataException("调用外部系统查询保单信息异常");
		}
		if (null == qryBillByInsureBillNo) {
			throw new ReqDataException("调用外部系统查询保单信息为空");
		}
		String sourceSys = qryBillByInsureBillNo.getSourceSys();
		Record orgRecord = null ;
		if("0".equalsIgnoreCase(sourceSys) || "2".equalsIgnoreCase(sourceSys)) {
			//查询getInsureOrgCode()
			orgRecord = Db.findById("la_org_mapping", "la_org_code", qryBillByInsureBillNo.getCompany());
		}else if("1".equalsIgnoreCase(sourceSys)) {
			orgRecord = Db.findById("ebs_org_mapping", "ebs_org_code", qryBillByInsureBillNo.getInsureOrgCode());
		}else {
			throw new ReqDataException("调用外部系统返回来源系统错误");
		}
		if(null ==orgRecord) {
			throw new ReqDataException("未根据外部系统的机构码在本系统内找到对应的机构");
		}
		
		//根据查询回来的值,封装一下表里的bank_code
		String bankcode = qryBillByInsureBillNo.getBankcode();
				
		Record findById = Db.findById("organization", "code", orgRecord.get("tmp_org_code"));
		Record rec = new Record();
		String isPadPayment = qryBillByInsureBillNo.getIsPadPayment();
		String isTransAccount = qryBillByInsureBillNo.getIsTransAccount();

		rec.set("insure_bill_no", record.getStr("insure_bill_no"));
		rec.set("bill_org_name", findById.get("name"));
		rec.set("bill_org_id", findById.get("org_id"));
		rec.set("insure_name", qryBillByInsureBillNo.getPolicyHolder());
		rec.set("source_sys", sourceSys);
		rec.set("bank_code", "31");
		rec.set("insure_cer_no", qryBillByInsureBillNo.getPolicyHolderCert());
		rec.set("insure_cer_no", qryBillByInsureBillNo.getPolicyHolderCert());
		rec.set("isnot_electric_pay", isPadPayment == null ? null :isPadPayment);
		rec.set("isnot_bank_transfer_premium", isTransAccount == null ? null : isTransAccount );
        rec.set("srce_bus", qryBillByInsureBillNo.getSrceBus());
        rec.set("camp_aign", qryBillByInsureBillNo.getCampAign());
        rec.set("agnt_num", qryBillByInsureBillNo.getAgntNum());
		return rec ;
	}
}

