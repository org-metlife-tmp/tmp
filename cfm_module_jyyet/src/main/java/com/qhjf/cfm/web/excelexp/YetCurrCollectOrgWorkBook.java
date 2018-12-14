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

public class YetCurrCollectOrgWorkBook extends AbstractWorkBook {

    public YetCurrCollectOrgWorkBook() {
        this.optype = "yet_curcollectlistorgexport";
        this.fileName = "当日余额汇总-公司.xls";
        this.titleNames = new String[]{
                "name", "bal",
        };
        this.titles = new String[]{
                "公司名称", "余额"
        };
        this.sheetName = "当日余额汇总-公司";
    }

    @Override
    public Workbook getWorkbook() {

        List<Record> recordList = Db.find(Db.getSqlPara("curyet.curcollectorglist", Kv.by("map", getRecord().getColumns())));

        return POIUtil.createExcel(recordList, this);
    }
}
