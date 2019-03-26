package com.qhjf.cfm.web.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.config.GmfConfigAccnoSection;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description:
 */
public class PayCounterWorkBook extends AbstractWorkBook {

	public PayCounterWorkBook() {
		this.optype = "paycounter_listexport";
		this.titleNames = new String[] { "pay_date", "name", "pay_mode", "biz_code", "preinsure_bill_no",
				"insure_bill_no", "type_name", "amount", "recv_acc_name", "recv_cert_code", "recv_acc_name",
				"recv_acc_no", "recv_bank_name", "pay_code", "status", "pay_acc_no", "pay_bank_name", "service_status",
				"op_user_name", "op_date", "actual_payment_date"

		};
		this.titles = new String[] { "业务操作日期", "机构名称", "支付方式", "业务号码", "投保单号", "保单号", "业务类型", "金额", "客户名称", "客户号码",
				"收款账号户名", "收款银行账号", "开户行", "支付号码", "支付结果", "付款账号", "付款银行", "审批状态", "操作人", "操作日期", "实付日期"

		};
		this.sheetName = "柜面付款工作台列表";
	}

	@Override
    public Workbook getWorkbook() {
    	        Record record = getRecord();
    	        // 获取付款账号
               Long org_id = getUodpInfo().getOrg_id();
    			String pay_account_no = GmfConfigAccnoSection.getInstance().getAccno();
    			Record payRec = Db.findFirst(Db.getSql("nbdb.findAccountByAccno"), pay_account_no);
    			String pay_account_bank = payRec.getStr("bank_name");
    	    	Record findById = Db.findById("organization", "org_id", org_id);
    			Integer source_sys = record.getInt("source_sys");
    			
    			List<String> codes = new ArrayList<>();
    			if(findById.getInt("level_num") == 1){
    				codes = Arrays.asList("0102","0101","0201","0202","0203","0204","0205","0500");
    			}else{
    				Record findFirst = Db.findFirst(Db.getSql("org.getCurrentUserOrgs"), org_id);
    				codes.add(findFirst.getStr("code"));
    			}
    			record.set("codes", codes);
    			List<Integer> status = record.get("status");
    			List<Integer> service_status_origin = record.get("service_status");
    			if(null == status || status.size() == 0) {
    				record.remove("status");			
    			}
    			if(null == service_status_origin || service_status_origin.size() == 0) {
    				record.remove("service_status");			
    			}else {
    			List<Integer> service_status = new ArrayList<>();
    			for (int i = 0; i < service_status_origin.size(); i++) {
    				WebConstant.Sft_Billstatus.WJF.getKey();
    				switch (service_status_origin.get(i)) {
    				case 0 :  //WebConstant.Sft_Billstatus.WJF.getKey()
    					service_status.add(1);  //WebConstant.BillStatus.SUBMITED.getKey()
    					service_status.add(2);
    					break;
    				case 1 :
    					service_status.add(3);
    					break;
    				case 2 :
    					service_status.add(5);
    					break;
    				case 3 :
    					service_status.add(4);
    					break;
    				case 4 :
    					service_status.add(7);
    					break;
    				case 5 :
    					service_status.add(8);
    					break;
    				default:
    					break;
    				}
    			 }
    			record.set("service_status", service_status);
    			}
    			String pay_mode = record.getStr("pay_mode");
    			//柜面付  默认网银
    			if( StringUtils.isBlank(pay_mode) ) {
    				record.set("pay_mode", WebConstant.SftDoubtPayMode.WY.getKeyc());
    			}
    			SqlPara sqlPara = null;
    			if(source_sys == 0) {
    				sqlPara = Db.getSqlPara("pay_counter.findLAPayCounterList", Kv.by("map", record.getColumns()));				
    			}else {
    				sqlPara = Db.getSqlPara("pay_counter.findEBSPayCounterList", Kv.by("map", record.getColumns()));				
    			}
    			List<Record> list = Db.find(sqlPara);
    			 if(null != list && list.size() > 0) {
    				 for (Record rec : list) {
    					 rec.set("pay_account_no", pay_account_no);
    					 rec.set("pay_account_bank", pay_account_bank);
    				}
    			 }
                  return POIUtil.createExcel(list, this);
    }
}
