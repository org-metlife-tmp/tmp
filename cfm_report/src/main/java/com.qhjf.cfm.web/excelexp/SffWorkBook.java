package com.qhjf.cfm.web.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Date;
import java.util.List;

/**
 * @Description: 报表数据导出
 */
public class SffWorkBook extends AbstractWorkBook {

    public SffWorkBook() {
        this.optype = "oaReport_listexport";
        this.fileName = "OA报表_"+DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
        this.titleNames = new String[]{
                "flow_id", "org_name", "apply_date", "send_on", "pay_account_no", "pay_account_bank",
                "recv_account_name", "recv_account_no", "recv_account_bank", "payment_amount", "payment_summary", "process_status", "process_msg"

        };
        this.titles = new String[]{
                "报销单申请号", "申请单位", "申请日期", "发送日期", "付款方账号", "付款方银行", "收款人", "收款方账号", "收款方银行", "收款金额",
                "摘要", "状态", "失败原因"
        };
        this.sheetName = "OA报表";
    }

    @Override
    public Workbook getWorkbook() {
        SqlPara sqlPara = Db.getSqlPara("oabb.oabbList", Kv.by("map", getRecord().getColumns()));
        List<Record> recordList = Db.find(sqlPara);
        for(Record record : recordList){
            int process_status = TypeUtils.castToInt(record.get("process_status"));
            record.set("process_status", WebConstant.OaProcessStatus.getOaProcessStatus(process_status).getDesc());
        }
        return POIUtil.createExcel(recordList, this);
    }
}
