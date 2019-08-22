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

public class YetHisTransDetailWorkBook extends AbstractWorkBook {

    public YetHisTransDetailWorkBook() {
        this.optype = "yet_histransexport";
        this.fileName = "历史余额明细.xls";
        this.titleNames = new String[]{
                "org_name", "acc_no", "acc_name", "acc_attr_name", "bank_type_name", "bank_name", "bal", "import_time"

        };
        this.titles = new String[]{
                "公司名称", "账户号", "账户名称", "账户性质", "银行大类", "所属银行", "当前余额", "同步时间"
        };
        this.sheetName = "历史余额明细";
    }

    @Override
    public Workbook getWorkbook() {

        List<Record> recordList = Db.find(Db.getSqlPara("hisyet.hisdetaillist", Kv.by("map", getRecord().getColumns())));

        return POIUtil.createExcel(recordList, this);
    }
}
