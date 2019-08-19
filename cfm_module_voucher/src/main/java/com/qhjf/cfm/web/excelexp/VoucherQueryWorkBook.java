package com.qhjf.cfm.web.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2019/6/3
 * @Description: 凭证查询导出
 */
public class VoucherQueryWorkBook extends AbstractWorkBook {

    public VoucherQueryWorkBook() {
        this.optype = "voucher_listexport";
        this.fileName = "凭证查询导出.xls";
        this.titleNames = new String[]{
                "statement_code", "business_ref_no", "account_code", "account_period", "base_amount", "debit_credit_name",
                "description", "journal_source", "transaction_amount", "transaction_date", "transaction_reference",
                "operator_name", "operator_org_name", "docking_status_name",
                "a_code1", "a_code2", "a_code3", "a_code5", "a_code6", "a_code7", "a_code10_name",
                "currency_code"
        };
        this.titles = new String[]{
                "对账唯一识别号码", "其他业务代码", "科目编号", "入账区间", "金额", "借贷标识",
                "描述", "凭证来源", "交易金额", "交易日期", "交易标识",
                "操作人", "操作人所属机构", "接口状态",
                "AnalysisCode1", "AnalysisCode2", "AnalysisCode3", "AnalysisCode5", "AnalysisCode6", "AnalysisCode7", "AnalysisCode10",
                "币种编号"
        };
        this.sheetName = "凭证查询导出";
    }

    @Override
    public Workbook getWorkbook() {
        Record record = getRecord();
        long orgId = getUodpInfo().getOrg_id();
        record.remove("org_id");
        //根据orgid查询机构信息
        Record orgRec = Db.findById("organization", "org_id", orgId);
        if (orgRec == null) {
            return null;
        }

        record.set("level_num", TypeUtils.castToInt(orgRec.get("level_num")));
        record.set("level_code", TypeUtils.castToString(orgRec.get("level_code")));
        SqlPara sqlPara = Db.getSqlPara("vquery.findSunVoucherDataList", Kv.by("map", record.getColumns()));
        List<Record> list = Db.find(sqlPara);

        return POIUtil.createExcel(list, this);
    }
}
