package com.qhjf.cfm.web.excelexp;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @auther zhangyuanyuan
 * @create 2018/9/14
 */

public class JytHisWaveWorkBook extends AbstractWorkBook {

    public JytHisWaveWorkBook() {
        this.optype = "jyt_hiswavelistexport";
        this.fileName = "历史交易波动.xls";
        this.titleNames = new String[]{
                "acc_no", "org_name", "recv", "pay", "netrecv"

        };
        this.titles = new String[]{
                "账户号", "公司名称", "收入", "支出", "净收支"
        };
        this.sheetName = "历史交易波动";
    }

    @Override
    public Workbook getWorkbook() {

        List<Record> recordList = Db.find(Db.getSqlPara("hisjyt.hiswavelist", Kv.by("map", getRecord().getColumns())));

        return POIUtil.createExcel(recordList, this);
    }
}
