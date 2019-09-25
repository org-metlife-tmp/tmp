package com.qhjf.cfm.web.service;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

/**
 * 柜面收待匹配收款
 * 
 * @author pc_liweibing
 *
 */
public class RecvCounterWaitingForMatchService {

	private final static Log logger = LogbackLog.getLog(RecvCounterWaitingForMatchService.class);

	private  RecvCounterService  recvCounterService = new RecvCounterService();
	
	private RecvGroupCounterService recvGroupCounterService = new RecvGroupCounterService();
	
	/**
	 * 待匹配收款列表
	 * @param pageSize
	 * @param pageNum
	 * @param record
	 * @param userInfo
	 * @param curUodp
	 * @return
	 * @throws BusinessException 
	 * @throws UnsupportedEncodingException 
	 */
	public Page<Record> list(int pageSize, int pageNum, Record record, UserInfo userInfo, UodpInfo curUodp) throws UnsupportedEncodingException, BusinessException {
		logger.info("====查询待匹配列表===="); 
		 SqlPara sqlPara = Db.getSqlPara("recv_counter_wait.personalWaitList", Kv.by("map", record.getColumns()));
		 Page<Record> paginate = Db.paginate(pageNum, pageSize, sqlPara);
		 List<Record> list = paginate.getList();
		 SymmetricEncryptUtil  util = SymmetricEncryptUtil.getInstance();
		 util.recvmask(list);
		 return paginate;
	}

	/**
	 * 未匹配新增
	 * @param record
	 * @param userInfo
	 * @param curUodp
	 * @throws ReqDataException 
	 * @throws EncryAndDecryException 
	 */
	public void add(Record record, UserInfo userInfo, UodpInfo curUodp) throws ReqDataException, EncryAndDecryException {
		//来自待匹配的数据
		String consumer_acc_no = TypeUtils.castToString(record.get("consumer_acc_no"));
		String recv_acc_no = TypeUtils.castToString(record.get("recv_acc_no"));
		Record const_bank_type = Db.findById("const_bank_type", "code", record.get("consumer_bank_name"));
		//加密
		SymmetricEncryptUtil  util = SymmetricEncryptUtil.getInstance();
		consumer_acc_no = util.encrypt(consumer_acc_no);
		recv_acc_no = util.encrypt(recv_acc_no);
		Record currency = Db.findById("currency", "id", record.get("currency"));
		record.set("update_on", new Date())
		      .set("create_on", new Date())
		      .set("currency", currency.get("name"))
		      .set("create_by", userInfo.getUsr_id())
		      .set("create_user_name", userInfo.getName())
		      .set("recv_org_id", curUodp.getOrg_id())
		      .set("update_by", userInfo.getUsr_id())
		      .set("match_recv_acc_name", record.get("payer"))
		      .set("match_recv_acc_no", consumer_acc_no)
		      .set("recv_acc_no", recv_acc_no)
		      .set("consumer_acc_no", consumer_acc_no)
		      .set("match_recv_bank_name", const_bank_type.get("name"))
		      .set("match_status", WebConstant.SftRecvCounterMatchStatus.DPP.getKey());
		boolean save = Db.save("recv_counter_match", "id", record);
		if(!save) {
			logger.error("====待匹配列表新增入库失败====");
			throw new ReqDataException("待匹配收款新增失败");
		}
	}

	/**
	 * 匹配
	 * @param record
	 * @param userInfo
	 * @param curUodp
	 * @throws BusinessException 
	 * @throws UnsupportedEncodingException 
	 */
	public Record match(Record record, UserInfo userInfo, UodpInfo curUodp) throws UnsupportedEncodingException, BusinessException {   
		Long id = record.getLong("id");
		Record recv_counter_match = Db.findById("recv_counter_match", "id", id);
		if(null == recv_counter_match) {
			throw new ReqDataException("此数据已过期,请刷新页面");
		}
		if(recv_counter_match.getInt("match_status") != WebConstant.SftRecvCounterMatchStatus.DPP.getKey()) {
			throw new ReqDataException("只有待匹配数据可以进行此操作");
		}
		//缴费人和缴费证件号不反写在新增页面,是用来退票跳转柜面付页面使用
		recv_counter_match.remove("payer");
		recv_counter_match.remove("payer_cer_no");
		recv_counter_match.set("wait_match_id", recv_counter_match.get("id"));
		recv_counter_match.set("wait_match_flag",WebConstant.YesOrNo.YES.getKey());
		SymmetricEncryptUtil  util = SymmetricEncryptUtil.getInstance();
		String recv_acc_no = new String(util.decrypt(recv_counter_match.getStr("recv_acc_no")), "utf-8");
		String consumer_acc_no = new String(util.decrypt(recv_counter_match.getStr("consumer_acc_no")), "utf-8");
		recv_counter_match.set("recv_acc_no",recv_acc_no);
		recv_counter_match.set("consumer_acc_no",consumer_acc_no);
        Record const_bank_type = Db.findById("const_bank_type", "code", recv_counter_match.get("consumer_bank_name"));
        recv_counter_match.set("name", const_bank_type.get("name"));
        recv_counter_match.set("display_name", const_bank_type.get("name"));
        recv_counter_match.set("code", const_bank_type.get("code"));
		recv_counter_match.set("consumer_acc_no",consumer_acc_no);
		return recv_counter_match ;
	}

	/**
	 * 未匹配_退费
	 * @param record
	 * @param userInfo
	 * @param curUodp
	 * @throws BusinessException 
	 * @throws UnsupportedEncodingException 
	 */
	public Record refund(Record record, UserInfo userInfo, UodpInfo curUodp) throws UnsupportedEncodingException, BusinessException {
		Long id = record.getLong("id");
		logger.info("====需要退费的待匹配数据id===="+id);
		Record findById = Db.findById("recv_counter_match", "id", id);
		//List<Record> all_bank_info = Db.find(Db.getSql("oa_interface.getBankJQ"), record.getStr("op_bank_name"));
		/*if(all_bank_info == null || all_bank_info.size() == 0){
			throw new ReqDataException("未在系统内找到此开户行信息");
		}*/
		if(findById.getInt("match_status")!= WebConstant.SftRecvCounterMatchStatus.DPP.getKey()) {
			throw new ReqDataException("此数据状态不允许进行此操作");
		}
		
		boolean update = CommonService.update("recv_counter_match", 
				              new  Record().set("match_status", WebConstant.SftRecvCounterMatchStatus.TFZ.getKey())
				              .set("refund_user_name", userInfo.getName()).set("refund_on", new Date())
				              .set("status", WebConstant.SftLegalData.NOGROUP.getKey()), 
				              new Record().set("id", id));
		if(!update) {
			throw new ReqDataException("未匹配数据退费更新状态失败");
		}
		//返回需要补录的数据
		Record rec = new Record();
		rec.set("pay_id", id);
		rec.set("persist_version", findById.get("persist_version"));
		//rec.set("recv_cnaps_code", all_bank_info.get(0).get("cnaps_code"));
		rec.set("recv_acc_no", findById.get("match_recv_acc_no"));
		rec.set("recv_acc_name", findById.get("match_recv_acc_name"));
		rec.set("recv_bank_name", findById.get("match_recv_bank_name"));
		SymmetricEncryptUtil  util = SymmetricEncryptUtil.getInstance();
		util.recvmaskforSingle(rec);
		return rec ;
	}

	/**
	 * 
	 * @param record
	 * @param userInfo
	 * @param curUodp
	 * @throws ReqDataException 
	 */
	public void revoke(Record record, UserInfo userInfo, UodpInfo uodpInfo) throws ReqDataException {
		
		Integer wait_match_id = record.getInt("id");
		logger.info("====待匹配页面需要撤销id===="+wait_match_id);
		
		try {
						
			Record recv_counter_bill = Db.findById("recv_counter_bill", "wait_match_id", wait_match_id);
			
			if(null == recv_counter_bill) {
				throw new ReqDataException("此条数据已过期,请刷新页面");
			}
			
			if(0 == TypeUtils.castToInt(recv_counter_bill.get("bill_type"))) {
				logger.info("====待匹配页面撤销数据为个单====");
				recvCounterService.revoke(record, userInfo, uodpInfo);
			} else {
				logger.info("====待匹配页面撤销数据为团单====");
				recvGroupCounterService.revoke(record, userInfo, uodpInfo);
			}
			//把此数据状态改为已撤销
			boolean update = CommonService.update("recv_counter_match", 
					new Record().set("update_on", new Date()).set("update_by", userInfo.getUsr_id())
					.set("match_status", WebConstant.SftRecvCounterMatchStatus.YCX.getKey()), 
					new Record().set("id", wait_match_id));
		    if(!update) {
		    	throw new ReqDataException("更新数据为已撤销失败");
		    }  		
		} catch (Exception e) {
			logger.error("====待匹配页面数据撤销失败===="+wait_match_id);
			e.printStackTrace();
			throw new ReqDataException(e.getMessage());
		}
		
		
		
	}

	/**
	 * 柜面收_详情
	 * @param record
	 * @param userInfo
	 * @param curUodp
	 * @return
	 */
	public Record detail(Record record, UserInfo userInfo, UodpInfo curUodp) throws BusinessException, UnsupportedEncodingException {
		
		
		Record retunRecord = new Record();
		
		Integer id = TypeUtils.castToInt(record.get("id"));
		
		Record recv_counter_match = Db.findById("recv_counter_match", "id", id);

		SymmetricEncryptUtil  util = SymmetricEncryptUtil.getInstance();
		try{
			util.recvmaskforSingle(recv_counter_match);
		}catch (BusinessException e) {
			throw  e;
		}catch (UnsupportedEncodingException e) {
			throw  e;
		}


		retunRecord.set("detail", recv_counter_match);
		
		return retunRecord;
	}


    
       
}

