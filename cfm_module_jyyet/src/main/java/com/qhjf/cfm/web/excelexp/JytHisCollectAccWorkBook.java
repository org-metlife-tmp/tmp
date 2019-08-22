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

public class JytHisCollectAccWorkBook extends AbstractWorkBook {

    public JytHisCollectAccWorkBook() {
        this.optype = "jyt_hiscollectlistaccexport";
        this.fileName = "历史交易汇总-账户.xls";
        this.titleNames = new String[]{
                "acc_no", "acc_name", "totalrecv", "totalpay", "totalnetrecv"

        };
        this.titles = new String[]{
                "账户号", "账户名称", "收入", "支出", "净收支"
        };
        this.sheetName = "历史交易汇总-账户";
    }

    @Override
    public Workbook getWorkbook() {

        List<Record> recordList = Db.find(Db.getSqlPara("hisjyt.hiscollectacclist", Kv.by("map", getRecord().getColumns())));

        return POIUtil.createExcel(recordList, this);
    }
}
