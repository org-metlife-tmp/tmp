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
 * @Description: 柜面收POS机明细导入的导出按钮
 */
public class RecvCounterImportPOSWorkBook extends AbstractWorkBook {

    public RecvCounterImportPOSWorkBook() {
        this.optype = "recvcounterimportpos_listexport";                       
        this.fileName = "import_pos_"+DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
        this.titleNames = new String[]{
                "liquidation_date", "trade_date", "trade_time", "terminal_no", "trade_amount", "procedures_amount",
                "entry_account_amount", "system_track_number", "retrieval_reference_number", "serial_number", "trade_type", "card_no", "card_type", "card_issue_bank"
                , "no_identity_mark", "import_date", "bill_checked"
        };
        this.titles = new String[]{
                "清算日期", "交易日期", "交易时间", "终端号", "交易金额", "手续费", "入账金额", "系统跟踪号", "检索参考号", "流水号",
                "交易类型", "卡号", "卡类型", "发卡行", "非接标识", "导入日期", "状态"
        };
        this.sheetName = "pos机明细";
    }

    @Override
    public Workbook getWorkbook() {
        SqlPara sqlPara = Db.getSqlPara("recv_counter_pos_import.recvcounterPoslist", Kv.by("map", getRecord().getColumns()));
        List<Record> recordList = Db.find(sqlPara);
        return POIUtil.createExcel(recordList, this);
    }
}
