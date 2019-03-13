package com.qhjf.cfm.web.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
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
 * @Description: 组批数据导出
 */
public class CheckBatchWorkBook extends AbstractWorkBook {

    public CheckBatchWorkBook() {
        this.optype = "checkbatch_listexport";
        this.titleNames = new String[]{
                "pay_date", "channel_code", "channel_desc", "name", "preinsure_bill_no",
                "insure_bill_no", "amount", "recv_acc_name", "recv_cert_code", "recv_acc_no", "pay_code" 
                ,"status","op_user_name","op_date"

        };
        this.titles = new String[]{
                "日期", "通道编码", "通道描述", "机构名称", "投保单号", "保单号", "金额", "客户姓名", "证件号码",
                "客户帐号", "支付号码" ,"状态" ,"操作人","操作日期"
        };
        this.sheetName = "核对组批列表";
    }

    @Override
    public Workbook getWorkbook() {
    	Record record = getRecord();
    	long source_sys = TypeUtils.castToLong(record.get("source_sys"));
        SqlPara sqlPara = null;
        Long org_id = getUodpInfo().getOrg_id();
		Record findById = Db.findById("organization", "org_id", org_id);		
		// 导出excel文件名,当传输渠道编码 .FH -- 渠道编码  . 否则文件名中 用 FH
		String FH = "FH";
		if(StringUtils.isNotBlank(record.getStr("channel_id"))) {
			Record channel_setting = Db.findById("channel_setting", "id", record.getLong("channel_id"));
			FH = channel_setting.getStr("channel_code");
		}
		
        List<String> codes = new ArrayList<>();
        if (findById.getInt("level_num") == 1) {
            codes = Arrays.asList("0102", "0101", "0201", "0202", "0203", "0204", "0205", "0500");
        } else {
            List<Record> rec = Db.find(Db.getSql("org.getCurrentUserOrgs"), org_id);
            for (Record o : rec) {
                codes.add(o.getStr("code"));
            }
        }
        record.set("codes", codes);
        record.set("pay_mode", "C");
    	List<Integer> status = record.get("status");
    	if(status == null || status.size() == 0){
    		record.remove("status");
    	}
    	if(WebConstant.SftOsSource.LA.getKey() == source_sys){
            //LA
            this.fileName = "LA_Package_"+ FH + "_" + RedisSericalnoGenTool.genShortSerial() +"_"+DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
            sqlPara = Db.getSqlPara("check_batch.checkBatchLAlist", Kv.by("map", record.getColumns()));
        }else if(WebConstant.SftOsSource.EBS.getKey() == source_sys){
            //EBS
            this.fileName = "EBS_Package_"+ FH + "_" + RedisSericalnoGenTool.genShortSerial() +"_"+DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
            sqlPara = Db.getSqlPara("check_batch.checkBatchEBSlist", Kv.by("map", record.getColumns()));
        }
        List<Record> recordList = Db.find(sqlPara);
        return POIUtil.createExcel(recordList, this);
    }
}
