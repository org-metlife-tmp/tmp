package com.qhjf.cfm.web;

import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

public class GylOptypeMgr extends AbstractOptypeMgr {
    @Override
    public void registe() {

        optypes.add(new Optype(Optype.Mode.NORMAL, "gylsetting_add")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "topic", "gyl_allocation_type", //"gyl_allocation_amount",
                        "gyl_allocation_frequency", "summary", "pay_acc_org_id",
                        "pay_acc_org_name", "pay_acc_id", "pay_acc_no", "pay_acc_name",
                        "pay_acc_bank_name", "pay_acc_bank_cnaps_code", "recv_acc_no",
                        "recv_acc_name", "pay_acc_cur", "timesetting_list"
                }))
                .registKeepParams(new String[]{
                        "topic", "gyl_allocation_type", "gyl_allocation_amount",
                        "gyl_allocation_frequency", "summary", "pay_acc_org_id",
                        "pay_acc_org_name", "pay_acc_id", "pay_acc_no", "pay_acc_name",
                        "pay_acc_bank_name", "pay_acc_bank_cnaps_code", "recv_acc_no",
                        "recv_acc_name", "files", "pay_acc_cur", "timesetting_list"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylsetting_chg")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "topic", "gyl_allocation_type", "gyl_allocation_amount",
                        "gyl_allocation_frequency", "summary", "pay_acc_org_id",
                        "pay_acc_org_name", "pay_acc_id", "pay_acc_no", "pay_acc_name",
                        "pay_acc_bank_name", "pay_acc_bank_cnaps_code", "recv_acc_no",
                        "recv_acc_name", "id", "persist_version", "pay_acc_cur", "timesetting_list"
                }))
                .registKeepParams(new String[]{
                        "topic", "gyl_allocation_type", "gyl_allocation_amount",
                        "gyl_allocation_frequency", "summary", "pay_acc_org_id",
                        "pay_acc_org_name", "pay_acc_id", "pay_acc_no", "pay_acc_name",
                        "pay_acc_bank_name", "pay_acc_bank_cnaps_code", "recv_acc_no",
                        "recv_acc_name", "id", "persist_version", "files", "pay_acc_cur", "timesetting_list"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylsetting_del")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version"
                }))
                .registKeepParams(new String[]{
                        "id", "persist_version"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylsetting_detail")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
                .registKeepParams(new String[]{"id"}));
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylsetting_presubmit")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "topic", "gyl_allocation_type", "gyl_allocation_amount",
                        "gyl_allocation_frequency", "summary", "pay_acc_org_id",
                        "pay_acc_org_name", "pay_acc_id", "pay_acc_no", "pay_acc_name",
                        "pay_acc_bank_name", "pay_acc_bank_cnaps_code", "recv_acc_no",
                        "recv_acc_name", "timesetting_list", "pay_acc_cur"
                }))
                .registKeepParams(new String[]{
                        "topic", "gyl_allocation_type", "gyl_allocation_amount",
                        "gyl_allocation_frequency", "summary", "pay_acc_org_id",
                        "pay_acc_org_name", "pay_acc_id", "pay_acc_no", "pay_acc_name",
                        "pay_acc_bank_name", "pay_acc_bank_cnaps_code", "recv_acc_no",
                        "recv_acc_name", "id", "persist_version", "files", "timesetting_list", "pay_acc_cur"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylsetting_submit")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                }))
                .registKeepParams(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylsetting_revoke")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version", "service_status"
                }))
                .registKeepParams(new String[]{
                        "id", "persist_version", "service_status"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylsetting_agree")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                }))
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                }));

        //批量同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylsetting_batchagree")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                }))
                .registKeepParams(new String[]{
                        "batch_list"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "gylsetting_reject")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                }))
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylsetting_append")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                }))
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                }));

        //批量加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylsetting_batchappend")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                }))
                .registKeepParams(new String[]{
                        "batch_list"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "gylsetting_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_size", "page_num", "biz_type"
                }))
                .registKeepParams(new String[]{
                        "page_size", "page_num", "biz_type"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "gylsetting_morebill")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_num", "page_size"
                }))
                .registKeepParams(new String[]{
                        "page_num", "page_size",
                        "gyl_allocation_type", "gyl_allocation_frequency",
                        "topic", "pay_acc_query_key", "service_status", "is_activity"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylmanage_list")
                .registKeepParams(new String[]{
                        "gyl_allocation_type", "gyl_allocation_frequency",
                        "topic", "pay_acc_query_key", "service_status", "is_activity"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylmanage_setstate")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version"
                }))
                .registKeepParams(new String[]{
                        "id", "persist_version"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylmanage_cancel")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version"
                }))
                .registKeepParams(new String[]{
                        "id", "persist_version"
                }));

        //=====================广易联列表查看=============
        //广易联列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylview_collections")
                .registKeepParams(new String[]{"page_num", "page_size", "pay_query_key", "service_status",
                        "recv_query_key", "topic", "gyl_allocation_type", "gyl_allocation_frequency", "is_activity"}));

        //=====================广易联交易核对=============
        //已核对/未核对单据
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylcheck_checkbillList")
                .registKeepParams(new String[]{"pay_account_no", "recv_account_no", "is_checked",
                        "min", "max", "page_size", "page_num", "start_date", "end_date"})
                .registerValidate(new RequiredParamsValidate(new String[]{"is_checked"})));
        //根据单据号,查询未核对交易号
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylcheck_checkNoCheckTradeList")
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));

        //根据单据号,查询已核对交易号  checkAlreadyTradeList
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylcheck_checkAlreadyTradeList")
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));
        //交易通_确认核对
        optypes.add(new Optype(Optype.Mode.NORMAL, "gylcheck_confirmCheck")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_id", "trade_id", "persist_version"}))
                .registKeepParams(new String[]{"bill_id", "trade_id", "persist_version"}));
    }
}
