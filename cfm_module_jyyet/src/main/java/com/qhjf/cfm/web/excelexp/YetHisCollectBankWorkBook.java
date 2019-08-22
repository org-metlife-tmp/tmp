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

public class YetHisCollectBankWorkBook extends AbstractWorkBook {

    public YetHisCollectBankWorkBook() {
        this.optype = "yet_hiscollectlistbankexport";
        this.fileName = "历史余额汇总-银行.xls";
        this.titleNames = new String[]{
                "name", "bal"

        };
        this.titles = new String[]{
                "银行名称", "金额"
        };
        this.sheetName = "历史余额汇总-银行";
    }

    @Override
    public Workbook getWorkbook() {

        List<Record> recordList = Db.find(Db.getSqlPara("hisyet.hiscollectbanklist", Kv.by("map", getRecord().getColumns())));

        return POIUtil.createExcel(recordList, this);
    }
}
