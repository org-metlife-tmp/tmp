package com.qhjf.cfm.web.excelexp;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Date;
import java.util.List;

/**
 * @Description: 月度统一计提银行未达账项凭证
 */
public class VoucherWorkBook extends AbstractWorkBook {
    private final static Log logger = LogbackLog.getLog(VoucherWorkBook.class);

    public VoucherWorkBook() {
        this.optype = "sftvoucherlist_voucherexport";
        this.fileName = "财务账列表.xls";
        this.titleNames = new String[]{
                "account_code", "account_period", "a_code1", "a_code2", "a_code3",
                "a_code5", "a_code6", "a_code7", "a_code10", "base_amount", "currency_code"
                ,"debit_credit", "description", "journal_source", "transaction_amount", "transaction_date", "transaction_reference"

        };
        this.titles = new String[]{
                "科目编号", "入账区间", "a_code1", "a_code2", "a_code3", "a_code5", "a_code6", "a_code7", "a_code10",
                "金额", "币种编号", "借贷标识", "描述信息", "凭证来源", "交易金额", "交易时间", "交易标识"
        };
        this.sheetName = "财务账列表";
    }

    @Override
    public Workbook getWorkbook() {
        SqlPara sqlPara = Db.getSqlPara("voucher.getvoucherlist", Kv.by("map", getRecord().getColumns()));
        List<Record> recordList = Db.find(sqlPara);
        return POIUtil.createExcel(recordList, this);
    }
}
