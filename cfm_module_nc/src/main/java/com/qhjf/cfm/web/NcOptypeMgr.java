package com.qhjf.cfm.web;

import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

public class NcOptypeMgr extends AbstractOptypeMgr {
    @Override
    public void registe() {
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgnc_todolist")
                .registKeepParams(new String[]{
                        "page_num", "page_size", "service_status", "recv_account_query_key", "min", "max", "flow_id",
                        "apply_start_date", "apply_end_date"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_num", "page_size"
                })));
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgnc_submit")
                .registKeepParams(new String[]{
                        "id"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                })));
        //查询列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkdoubtfulnc_list")
                .registKeepParams(new String[]{"flow_id","apply_start_date", "apply_end_date","apply_user",
                        "recv_account_query_key", "min_amount", "max_amount", "page_size", "page_num"}));
        //可疑数据通过
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkdoubtfulnc_pass")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version"
                }))
                .registKeepParams(new String[]{"id", "persist_version"}));
        //总公司导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgnc_todolistexport")
                .registKeepParams(new String[]{
                        "service_status", "recv_account_query_key", "min", "max", "org_name", "flow_id",
                        "apply_start_date", "apply_end_date"
                }));
        //可疑表导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkdoubtfulnc_listexport")
                .registKeepParams(new String[]{
                        "flow_id","apply_start_date", "apply_end_date","apply_user",
                        "recv_account_query_key", "min_amount", "max_amount"
                }));
        /** ============================ OA总公司交易核对 begin ============================ */

        //单据未核对查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgnccheck_checkbillList")
                .registKeepParams(new String[]{"pay_account_no", "recv_account_no", "is_checked",
                        "min", "max", "page_size", "page_num", "start_date", "end_date"}));

        //勾选 查找交易流水
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgnccheck_checkTradeList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id","date_validate","recv_validate"
                }))
                .registKeepParams(new String[]{"id","date_validate","recv_validate"}));

        //确认交易
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgnccheck_confirmCheck")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_id", "trade_id", "persist_version"
                }))
                .registKeepParams(new String[]{"bill_id", "trade_id", "persist_version"}));
        //单据已核对交易查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgnccheck_checkAlreadyTradeList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                }))
                .registKeepParams(new String[]{"id"}));
        /** ============================ OA总公司交易核对 end ============================ */
    }


}
