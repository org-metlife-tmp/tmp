package com.qhjf.cfm.web.excelexp;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;
import org.apache.poi.ss.usermodel.Workbook;

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
        this.sheetName = "批付核对组批列表";
    }

    @Override
    public Workbook getWorkbook() {
    	Record record = getRecord();
        Long org_id = getUodpInfo().getOrg_id();
		Record findById = Db.findById("organization", "org_id", org_id);
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
    	List<Integer> status = record.get("status");
    	if(status == null || status.size() == 0){
    		record.remove("status");
    	}
        //LA
        this.fileName = "LA_Package_FH_"+ RedisSericalnoGenTool.genShortSerial() +"_"+DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
		SqlPara sqlPara = Db.getSqlPara("recv_check_batch.recvcheckBatchLAlist", Kv.by("map", record.getColumns()));
        List<Record> recordList = Db.find(sqlPara);
        return POIUtil.createExcel(recordList, this);
    }
}
