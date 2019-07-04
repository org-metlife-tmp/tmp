package com.qhjf.cfm.web.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.web.config.GmfConfigAccnoSection;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.UnsupportedEncodingException;
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
				"recv_acc_no", "recv_bank_name", "pay_code", "status", "pay_account_no", "pay_account_bank", "service_status",
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
    			Integer source_sys = TypeUtils.castToInt(record.get("source_sys"));
    			
    			if(findById.getInt("level_num") != 1){
    				List<Integer> org_ids = new ArrayList<>();
    				List<Record> find = Db.find(Db.getSql("pay_counter.getSonOrg"), org_id);
    				for (int i = 0; i < find.size(); i++) {
    					org_ids.add(find.get(i).getInt("org_id"));
    				}
    				record.set("org_ids", org_ids);
    			}
    			List<Integer> status = record.get("status");
    			String service_status_origin = TypeUtils.castToString(record.get("service_status"));
    			if(null == status || status.size() == 0) {
    				record.remove("status");			
    			}
    			if(StringUtils.isBlank(service_status_origin)) {
    				record.remove("service_status");			
    			}else {
    			record.set("service_status_origin",record.get("service_status"));
    			List<Integer> service_status = new ArrayList<>();
    			switch (TypeUtils.castToInt(service_status_origin)) {
    				case 0 :  //WebConstant.Sft_Billstatus.WJF.getKey()
    					service_status.add(1);  //WebConstant.BillStatus.SUBMITED.getKey()				
    					break;
    				case 1 :
    					service_status.add(3);
    					service_status.add(2);
    					break;
    				case 2 :
    					service_status.add(5);
    					break;
    				case 3 :
    					service_status.add(4);
    					service_status.add(6);
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
    			record.set("service_status", service_status);
    			}
    			// 如果状态 未给付 , 可以是未提交.  其他状态必须是已提交 .否则查找任何状态都会带出未给付状态
    			String pay_mode = record.getStr("pay_mode");
    			//柜面付  默认网银
    			if( StringUtils.isBlank(pay_mode) ) {
    				record.set("pay_mode", WebConstant.SftDoubtPayMode.WY.getKeyc());
    			}else {
    				record.set("pay_mode", WebConstant.SftDoubtPayMode.getSftDoubtPayModeByKey(Integer.valueOf(pay_mode)).getKeyc());
    			}
    			SqlPara sqlPara = null;
    			String pre  = null ;
    			if(source_sys == 0) {
    				pre = "LA";
    				sqlPara = Db.getSqlPara("pay_counter.findLAPayCounterList", Kv.by("map", record.getColumns()));				
    			}else {
    				pre = "EBS";
    				sqlPara = Db.getSqlPara("pay_counter.findEBSPayCounterList", Kv.by("map", record.getColumns()));				
    			}
    			List<Record> list = Db.find(sqlPara);
    			 SymmetricEncryptUtil  util = SymmetricEncryptUtil.getInstance();
    			 try {
					util.recvmask(list);
				} catch (UnsupportedEncodingException | BusinessException e) {
					e.printStackTrace();
				}
    			 if(null != list && list.size() > 0) {
    				 for (Record rec : list) {
    					 rec.set("pay_account_no", pay_account_no);
    					 rec.set("pay_account_bank", pay_account_bank);
    				}
    			 }
    			 
    			 this.fileName = pre + "_Ebankpayment_" +DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
                 return POIUtil.createExcel(list, this);
    }
}
