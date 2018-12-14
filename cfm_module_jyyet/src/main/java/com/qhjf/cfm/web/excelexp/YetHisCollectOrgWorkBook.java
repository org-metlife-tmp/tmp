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

public class YetHisCollectOrgWorkBook extends AbstractWorkBook {

    public YetHisCollectOrgWorkBook() {
        this.optype = "yet_hiscollectlistorgexport";
        this.fileName = "历史余额汇总-公司.xls";
        this.titleNames = new String[]{
                "name", "bal"

        };
        this.titles = new String[]{
                "公司名称", "金额"
        };
        this.sheetName = "历史余额汇总-公司";
    }

    @Override
    public Workbook getWorkbook() {

        List<Record> recordList = Db.find(Db.getSqlPara("hisyet.hiscollectorglist", Kv.by("map", getRecord().getColumns())));

        return POIUtil.createExcel(recordList, this);
    }
}
