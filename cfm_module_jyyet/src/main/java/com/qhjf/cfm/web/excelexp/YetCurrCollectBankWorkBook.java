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

public class YetCurrCollectBankWorkBook extends AbstractWorkBook {

    public YetCurrCollectBankWorkBook() {
        this.optype = "yet_curcollectlistbankexport";
        this.fileName = "当日余额汇总-银行.xls";
        this.titleNames = new String[]{
                "name", "bal"

        };
        this.titles = new String[]{
                "银行名称", "余额"
        };
        this.sheetName = "当日余额汇总-银行";
    }

    @Override
    public Workbook getWorkbook() {

        List<Record> recordList = Db.find(Db.getSqlPara("curyet.curcollectbanklist", Kv.by("map", getRecord().getColumns())));

        return POIUtil.createExcel(recordList, this);
    }
}
