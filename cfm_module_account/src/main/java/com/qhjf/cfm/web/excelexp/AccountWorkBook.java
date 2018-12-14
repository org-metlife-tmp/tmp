package com.qhjf.cfm.web.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;
import com.qhjf.cfm.web.service.AccCommonService;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @auther zhangyuanyuan
 * @create 2018/11/13
 */

public class AccountWorkBook extends AbstractWorkBook {

    public AccountWorkBook() {
        this.optype = "account_listexport";
        this.fileName = "账户信息维护.xls";
        this.titleNames = new String[]{
                "acc_no", "bank_name", "acc_name", "curr_name", "acc_attr_name", "interactive_mode_name", "acc_status", "is_close_confirm_des"

        };
        this.titles = new String[]{
                "账户号", "开户行", "账户名称", "币种", "账户性质", "账户模式", "账户状态", "销户确认"
        };
        this.sheetName = "账户信息维护";
    }

    @Override
    public Workbook getWorkbook() {
        Record record = getRecord();

        List<Record> recordList = Db.find(Db.getSqlPara("acc.findAccountToPage", Kv.by("map", record.getColumns())));

        for (Record rec : recordList) {
            //币种
            Record currRec = Db.findById("currency", "id", TypeUtils.castToLong(rec.get("curr_id")));
            rec.set("curr_name", TypeUtils.castToString(currRec.get("name")));

            //账户模式
            int interactiveMode = TypeUtils.castToInt(rec.get("interactive_mode"));
            rec.set("interactive_mode_name", WebConstant.InactiveMode.DIRECTCONN.getKey() == interactiveMode ? "直连" : "人工");

            //账户状态
            int status = TypeUtils.castToInt(rec.get("status"));
            WebConstant.AccountStatus accountStatus = WebConstant.AccountStatus.getByKey(status);
            if (accountStatus == null) {
                rec.set("acc_status", "");
            } else {
                rec.set("acc_status", accountStatus.getDesc());
            }

            //销户确认
            int isCloseConfirm = TypeUtils.castToInt(rec.get("is_close_confirm"));
            rec.set("is_close_confirm_des", isCloseConfirm == 0 ? "未确认" : "已确认");
        }

        return POIUtil.createExcel(recordList, this);
    }

}
