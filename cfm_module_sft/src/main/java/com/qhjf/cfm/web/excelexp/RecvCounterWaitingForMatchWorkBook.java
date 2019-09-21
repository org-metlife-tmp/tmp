package com.qhjf.cfm.web.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
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

/** 柜面收_待匹配列表导出
 * @Description:
 */
public class RecvCounterWaitingForMatchWorkBook extends AbstractWorkBook {

	public RecvCounterWaitingForMatchWorkBook() {
		this.optype = "recvcounterwaitingformatch_listexport";
		this.titleNames = new String[] { "recv_date", "batch_process_no", "recv_mode", "recv_bank_name", "recv_acc_no",
				"bill_status", "bill_date","terminal_no", "amount", "consumer_bank_name", "consumer_acc_no", "create_user_name",
				"recv_org_name", "match_status", "bill_type", "source_sys", "batch_no", "preinsure_bill_no", "insure_bill_no",
				"bill_org_name", "use_funds", "insure_acc_no","insure_name","business_acc_no","business_acc","consumer_no","consumer_acc_name",
				"third_payment","payer","pay_code","match_user_name","match_on","refund_user_name","fefund_bank_name","refund_acc_no"
				

		};
		this.titles = new String[] { "收款日期", "批处理号", "收款方式", "收款银行", "收款账号", "票据状态", "票据编号","票据日期","终端机编号","金额",
				"客户银行","客户账号","操作人","收款机构","状态","业务类型","核心系统","批单号","投保单号","保单号","保单机构","资金用途","投保人客户号",
				"投保人","业务所属客户号","业务所属客户","客户号","客户名称","第三方缴费","缴费人","缴费人编码","匹配人","匹配时间","退款人","退款银行","退款银行账号"
		};
		this.sheetName = "待匹配收款列表";
	}

	@Override
    public Workbook getWorkbook() {
    	         Record record = getRecord();
    	         SqlPara sqlPara = Db.getSqlPara("recv_counter_wait.personalWaitList", Kv.by("map", record.getColumns()));
    			 List<Record> find = Db.find(sqlPara);
    			 SymmetricEncryptUtil  util = SymmetricEncryptUtil.getInstance();
    			 try {
					util.recvmask(find);
				 }  catch (Exception e) {
					e.printStackTrace();
				}
    			 
    			 for(Record rd : find){
    		            int bill_status = TypeUtils.castToInt(rd.get("bill_status"));
    		            rd.set("bill_status", WebConstant.SftRecvCounterBillStatus.getByKey(bill_status).getDesc());
    		            int recv_mode = TypeUtils.castToInt(rd.get("recv_mode"));
    		            rd.set("recv_mode", WebConstant.Sft_RecvPersonalCounter_Recvmode.getByKey(recv_mode).getDesc());
    		            int match_status = TypeUtils.castToInt(rd.get("match_status"));
    		            rd.set("match_status", WebConstant.SftRecvCounterMatchStatus.getByKey(match_status).getDesc());
    		            int bill_type = TypeUtils.castToInt(rd.get("bill_type"));
    		            rd.set("bill_type", WebConstant.SftRecvType.getByKey(bill_type).getDesc());
    		            int source_sys = TypeUtils.castToInt(rd.get("source_sys"));
    		            rd.set("source_sys", WebConstant.SftOsSourceCounter.getSftOsSource(source_sys).getDesc());
    		            int third_payment = TypeUtils.castToInt(rd.get("third_payment"));
    		            rd.set("third_payment", third_payment == 1 ? "是" :"否");
    		     }    			 
    			 this.fileName = "_Ebankpayment_" +DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
                 return POIUtil.createExcel(find, this);
    }
}
