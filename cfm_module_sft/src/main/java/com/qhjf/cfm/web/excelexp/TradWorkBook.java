package com.qhjf.cfm.web.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 月度统一计提银行未达账项凭证
 */
public class TradWorkBook extends AbstractWorkBook {
    private final static Log logger = LogbackLog.getLog(TradWorkBook.class);

    public TradWorkBook() {
        this.optype = "sftvoucherlist_tradxport";
        this.fileName = "业务明细列表.xls";
        this.titleNames = new String[]{
                "trans_date", "bankcode", "acc_no", "acc_name", "bank_name",
                "org_name", "direction", "opp_acc_no", "opp_acc_name", "opp_acc_bank", "period_date"
                ,"summary", "is_checked", "precondition", "check_user_name"

        };
        this.titles = new String[]{
                "对账单日期", "Bankcode", "银行账号", "账户名称", "开户行", "开户机构", "收付方向", "对方银行账号", "对方账户名称",
                "对方开户行", "财务月" ,"摘要" ,"对账状态","预提状态","操作人"
        };
        this.sheetName = "业务明细列表";
    }

    @Override
    public Workbook getWorkbook() {
        SqlPara sqlPara = Db.getSqlPara("voucher.voucherlist", Kv.by("map", getRecord().getColumns()));
        List<Record> recordList = Db.find(sqlPara);
        return POIUtil.createExcel(recordList, this);
    }
}
