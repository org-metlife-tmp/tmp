package com.qhjf.cfm.web;

import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2019/5/31
 * @Description: 凭证查询
 */
public class VoucherQueryOptypeMgr extends AbstractOptypeMgr {
    @Override
    public void registe() {
        optypes.add(new Optype(Optype.Mode.NORMAL, "voucher_list")
                .registKeepParams(new String[]{
                        "start_trans_date", "end_trans_date", "statement_code", "account_code", "a_code10",
                        "min", "max", "docking_status", "start_accounting_period", "end_accounting_period",
                        "operator", "operator_org", "page_size", "page_num"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{})));

        optypes.add(new Optype(Optype.Mode.NORMAL, "voucher_export")
                .registKeepParams(new String[]{
                        "start_trans_date", "end_trans_date", "statement_code", "account_code", "a_code10",
                        "min", "max", "docking_status", "start_accounting_period", "end_accounting_period",
                        "operator", "operator_org"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{})));

        optypes.add(new Optype(Optype.Mode.NORMAL, "voucher_listexport")
                .registKeepParams(new String[]{
                        "start_trans_date", "end_trans_date", "statement_code", "account_code", "a_code10",
                        "min", "max", "docking_status", "start_accounting_period", "end_accounting_period",
                        "operator", "operator_org"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{})));

    }
}
