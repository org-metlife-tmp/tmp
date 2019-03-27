package com.qhjf.cfm.web.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
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
 * @Description: 批付组批数据导出
 */
public class RecvCheckBatchWorkBook extends AbstractWorkBook {

    public RecvCheckBatchWorkBook() {
        this.optype = "recvcheckbatch_listexport";
        this.titleNames = new String[]{
                "recv_date", "channel_code", "channel_desc", "name", "preinsure_bill_no",
                "insure_bill_no", "biz_type", "amount", "pay_acc_name", "pay_cert_code", "pay_acc_no", "pay_code" 
                ,"status","op_user_name","op_date"

        };
        this.titles = new String[]{
                "日期", "通道编码", "通道描述", "机构名称", "投保单号", "保单号", "业务类型" ,"金额", "客户姓名", "证件号码",
                "客户帐号", "支付号码" ,"状态" ,"操作人","操作日期"
        };
        this.sheetName = "核对组批列表";
    }

    @Override
    public Workbook getWorkbook() {
    	Record record = getRecord();
        Long org_id = getUodpInfo().getOrg_id();
		Record findById = Db.findById("organization", "org_id", org_id);
        List<String> codes = new ArrayList<>();
        
		// 导出excel文件名,当传输渠道编码 .FH -- 渠道编码  . 否则文件名中 用 FH
		String FH = "FH";
		if(StringUtils.isNotBlank(record.getStr("channel_id"))) {
			Record channel_setting = Db.findById("channel_setting", "id", record.getLong("channel_id"));
			FH = channel_setting.getStr("channel_code");
		}
        
        if (findById.getInt("level_num") == 1) {
            codes = Arrays.asList("0102", "0101", "0201", "0202", "0203", "0204", "0205", "0500");
        } else {
            List<Record> rec = Db.find(Db.getSql("org.getCurrentUserOrgs"), org_id);
            for (Record o : rec) {
                codes.add(o.getStr("code"));;
            }
        }
        SymmetricEncryptUtil  util = SymmetricEncryptUtil.getInstance();
		String pay_acc_no = record.getStr("pay_acc_no");
		try {
			pay_acc_no = util.encrypt(pay_acc_no);
		} catch (EncryAndDecryException e) {
			e.printStackTrace();
		}
		record.set("pay_acc_no", pay_acc_no);
        record.set("codes", codes);
        record.set("pay_mode", WebConstant.SftDoubtRecvMode.PLSF.getKeyc());
    	List<Integer> status = record.get("status");
    	if(status == null || status.size() == 0){
    		record.remove("status");
    	}
    	// 首期  :  biz_Type : IP  ,  续期 : biz_Type : MP ,PP ,RP ,TP
    			if(StringUtils.isNotBlank(TypeUtils.castToString(record.get("biz_type")))) {
    				if(WebConstant.Sft_BizType.SQ.getKey() ==  TypeUtils.castToInt(record.get("biz_type"))) {
    					record.set("biz_type", new String[]{
    							"IP"
    					});
    				}else {
    					record.set("biz_type", new String[]{
    							"MP" ,"PP" ,"RP" ,"TP"
    					});
    				}
    			}
        //LA
        this.fileName = "LA_Package_"+FH+"_"+ RedisSericalnoGenTool.genShortSerial() +"_"+DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
		SqlPara sqlPara = Db.getSqlPara("recv_check_batch.recvcheckBatchLAlist", Kv.by("map", record.getColumns()));
        List<Record> recordList = Db.find(sqlPara);
        try {
			util.paymask(recordList);
		} catch (UnsupportedEncodingException | BusinessException e) {
			e.printStackTrace();
		}
        return POIUtil.createExcel(recordList, this);
    }
}
