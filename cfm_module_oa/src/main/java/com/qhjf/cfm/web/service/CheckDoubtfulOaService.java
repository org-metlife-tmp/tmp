package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.webservice.oa.callback.OaCallback;
import com.qhjf.cfm.web.webservice.oa.service.ProcessService;

import java.sql.SQLException;
import java.util.List;

public class CheckDoubtfulOaService {
	
	private static final Log log = LogbackLog.getLog(CheckDoubtfulOaService.class);
	private OaCallback oaCallback = new OaCallback();

	/**
	 * 可疑数据列表
	 * @param pageNum
	 * @param pageSize
	 * @param record
	 * @return
	 */
	public Page<Record> list(int pageNum, int pageSize, final Record record) {
		String recv_query_key = record.get("recv_acc_no");
		//是否包含中文
		boolean recvFlag = StringKit.isContainChina(recv_query_key);
		if (recvFlag) {
			//名称
			record.set("recv_acc_name", recv_query_key);
		} else {
			//帐号
			record.set("recv_acc_no", recv_query_key);
		}
		record.remove("recv_acc_no");
		SqlPara sqlPara = Db.getSqlPara("check_doubtful_oa.list", Kv.by("map", record.getColumns()));
		return Db.paginate(pageNum, pageSize, sqlPara);
	}
	/**
	 * 查看疑似重复单据列表
	 * @param record
	 * @return
	 */
	public List<Record> doubtlist(final Record record) {
		SqlPara sqlPara = Db.getSqlPara("check_doubtful_oa.doubtlist", Kv.by("map", record.getColumns()));
		return Db.find(sqlPara);
	}
	
	/**
     * 
     *@  支付作废
     * @param paramsToRecord
     */
    public void payOff(Record paramsToRecord) throws Exception {
		//总公司支付作废按钮 oa_head_payment  , oa_origin_data 表

        final List<Long> ids = paramsToRecord.get("ids");
        final List<Integer> persist_versions = paramsToRecord.get("persist_version");
        for (int i = 0; i < ids.size(); i++) {
            long id = TypeUtils.castToLong(ids.get(i));
            Integer old_version = TypeUtils.castToInt(persist_versions.get(i));
            Record innerRec = Db.findById("oa_check_doubtful", "id", id);
            if (innerRec == null) {
                throw new ReqDataException("未找到有效的单据!");
            }
            Long originId = TypeUtils.castToLong(innerRec.get("origin_id"));
            int is_doubtful = TypeUtils.castToInt(innerRec.get("is_doubtful"));
            // 判断为可疑数据可以发送，其他状态需抛出异常！
            if (is_doubtful == WebConstant.YesOrNo.YES.getKey()) {
                Record set = new Record();
                Record where = new Record();  
                set.set("persist_version", old_version + 1) ; 
                set.set("is_doubtful", WebConstant.YesOrNo.NO.getKey());
                where.set("id", id);
                where.set("persist_version", old_version);
                boolean flag = CommonService.update("oa_check_doubtful", set, where);
                if (flag) {
                	set.clear();
                	where.clear();
            		Record originRecord = Db.findById("oa_origin_data", "id", originId);
            		if(null == originRecord){
            			throw new ReqDataException("未找到此单据的原始数据!");
            		}
            		set.set("lock_id", originId);
            		set.set("interface_status", 4);
            		set.set("interface_fb_code","P0098");
            		set.set("interface_fb_msg", TypeUtils.castToString(paramsToRecord.get("feed_back")));
            		where.set("id", originId);
            		flag = CommonService.update("oa_origin_data", set, where);
            		if(flag){
            			//调用callback接口
                		originRecord = Db.findById("oa_origin_data", "id", originId);
						oaCallback.callback(originRecord);
            		}else {
            			throw new DbProcessException("单据作废失败!");	
            		}
                }else{
                	throw new DbProcessException("单据作废失败!");	
                }
            } else {
                throw new ReqDataException("单据状态不正确!");
            }
        }
    
	}
    
    public void pass(final Long id,Integer version)throws Exception{
    	final Record check = Db.findById("oa_check_doubtful", id);
    	final Record originData = Db.findById("oa_origin_data", check.getLong("origin_id"));
    	String oaOrgCode = originData.getStr("apply_org");
    	final Record applyOrg = Db.findFirst(Db.getSql("oa_interface.getOrgByCode"),oaOrgCode);
    	String oaRecvBank = originData.getStr("recv_bank");
    	List<Record> banks = Db.find(Db.getSql("oa_interface.getBankJQ"),oaRecvBank);
		if(banks == null || banks.size() ==0){
			banks = Db.find(Db.getSql("oa_interface.getBank"),"%"+oaRecvBank+"%");
		}
		final Record recvBank = banks.get(0);

		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				new ProcessService().process(applyOrg, recvBank, originData, id);
				Record set = new Record();
				Record where = new Record();
				set.set("is_doubtful", WebConstant.YesOrNo.NO.getKey());
				set.set("persist_version", check.getInt("persist_version")+1);
				where.set("id", id);
				where.set("persist_version", check.getInt("persist_version"));
				return CommonService.update("oa_check_doubtful", set, where);
			}
		});


         if(!flag){
        	 throw new DbProcessException("数据过期!");
 		}
    }
}

