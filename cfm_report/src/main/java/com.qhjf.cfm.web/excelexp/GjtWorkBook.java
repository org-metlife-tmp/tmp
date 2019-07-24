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
public class GjtWorkBook extends AbstractWorkBook {

    public GjtWorkBook() {
        this.optype = "gjtReport_listexport";
        this.fileName = "归集通报表_"+DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
        this.titleNames = new String[]{
                "pay_account_no", "pay_account_org_name", "recv_account_bank", "recv_account_no", "collect_amount", "collect_status",
                "execute_time"

        };
        this.titles = new String[]{
                "被归集账号", "被归集账户所属分公司", "归集账号开户行", "归集主账号", "归集金额", "状态", "日期"
        };
        this.sheetName = "归集通报表";
    }

    @Override
    public Workbook getWorkbook() {
        SqlPara sqlPara = Db.getSqlPara("gjtbb.gjtList", Kv.by("map", getRecord().getColumns()));
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
