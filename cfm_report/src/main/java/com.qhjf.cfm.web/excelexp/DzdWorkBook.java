package com.qhjf.cfm.web.excelexp;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Date;
import java.util.List;

/**
 * @Description: 报表数据导出
 */
public class DzdWorkBook extends AbstractWorkBook {

    public DzdWorkBook() {
        this.optype = "dzdReport_listexport";
        this.fileName = "银行对账单报表_"+DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
        this.titleNames = new String[]{
                "acc_name", "trans_time", "ref_bill_id", "acc_no", "bank_type", "opp_acc_no",
                "opp_acc_bank", "amount", "summary", "statement_code", "is_checked", "check_user_name", "check_user_name",
        };
        this.titles = new String[]{
                "银行账户", "日期", "单据号", "付款方账号", "付款方银行", "收款方账号", "收款方银行",
                "金额", "摘要", "对账码", "交易核对标识", "操作员", "核对人"
        };
        this.sheetName = "银行对账单报表";
    }

    @Override
    public Workbook getWorkbook() {
        SqlPara sqlPara = Db.getSqlPara("dzdbb.gjtList", Kv.by("map", getRecord().getColumns()));
        List<Record> recordList = Db.find(sqlPara);
        /*for(Record record : recordList){
            int os_source = TypeUtils.castToInt(record.get("os_source"));
            record.set("os_source", WebConstant.SftOsSource.getSftOsSource(os_source).getDesc());
            int is_source_back = TypeUtils.castToInt(record.get("is_source_back"));
            record.set("is_source_back_name", is_source_back==0? "否" : "是");
            int bankkey_status = TypeUtils.castToInt(record.get("bankkey_status"));
            record.set("bankkey_status_name", bankkey_status==0? "关闭" : "启用");
            int subordinate_channel = TypeUtils.castToInt(record.get("subordinate_channel"));
            record.set("subordinate_channel", WebConstant.SftSubordinateChannel.getSftSubordinateChannel(subordinate_channel).getDesc());
            int pay_mode = TypeUtils.castToInt(record.get("pay_mode"));
            record.set("pay_mode", WebConstant.SftPayAttr.getSftPayAttr(pay_mode).getDesc());

        }*/
        return POIUtil.createExcel(recordList, this);
    }
}
