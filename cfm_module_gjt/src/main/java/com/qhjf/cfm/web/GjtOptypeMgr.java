package com.qhjf.cfm.web;

import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

public class GjtOptypeMgr extends AbstractOptypeMgr {
    @Override
    public void registe() {
        //新增
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_add")
                .registKeepParams(new String[]{
                        "main_list", "timesetting_list", "files", "collect_type", "topic",
                        "collect_frequency", "collect_amount", "summary"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "main_list", "timesetting_list", "collect_type", "topic",
                        "collect_frequency"
                })));
        //修改
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_chg")
                .registKeepParams(new String[]{
                        "main_list", "timesetting_list", "files", "collect_type", "topic",
                        "collect_frequency", "collect_amount", "summary", "id", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version", "main_list", "timesetting_list", "collect_type", "topic",
                        "collect_frequency", "collect_amount"
                })));
        //删除
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_del")
                .registKeepParams(new String[]{
                        "id", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version"
                })));

        //预提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_presubmit")
                .registKeepParams(new String[]{
                        "main_list", "timesetting_list", "files", "collect_type", "topic",
                        "collect_frequency", "collect_amount", "summary", "id", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "main_list", "timesetting_list", "collect_type", "topic",
                        "collect_frequency"
                })));

        //提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_submit")
                .registKeepParams(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })));
        //撤回
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_revoke")
                .registKeepParams(new String[]{"id", "persist_version", "service_status"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"})));

        //同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //批量同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        //加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //批量加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"page_size", "page_num", "biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));

        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_processedtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"page_size", "page_num", "biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));


        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_detail")
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));

        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_getchildacclist")
                .registKeepParams(new String[]{"query_key", "bank_type", "acc_type", "exclude_ids", "exclude_main_ids"}));

        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_morebill")
                .registKeepParams(new String[]{
                        "page_num", "page_size", "collect_type", "collect_frequency", "topic", "main_acc_query_key", "service_status", "is_activity"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_num", "page_size"
                })));

        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_copy")
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));

        optypes.add(new Optype(Optype.Mode.NORMAL, "collectmanage_list")
                .registKeepParams(new String[]{
                        "collect_type", "collect_frequency", "topic", "main_acc_query_key", "is_activity"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "collectmanage_setstate")
                .registKeepParams(new String[]{"id", "persist_version"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version"})));

        optypes.add(new Optype(Optype.Mode.NORMAL, "collectmanage_cancel")
                .registKeepParams(new String[]{"id", "persist_version"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version"})));

        optypes.add(new Optype(Optype.Mode.NORMAL, "collectsetting_accs")
                .registKeepParams(new String[]{"status", "acc_id", "exclude_ids"}));

        // ==============================归集通查看,报表,交易核对=========================
        //归集列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectview_collections")
                .registKeepParams(new String[]{"page_num", "page_size", "collect_main_key",
                        "topic", "service_status", "collect_type", "collect_frequency", "is_activity"}));
        //归集详情
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectview_datail")
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));

        //归集报表
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectreport_reports")
                .registKeepParams(new String[]{"page_num", "page_size", "pay_account_org_id",
                        "query_key", "pay_bank_cnaps", "start_date", "end_date"}));
        //归集报表_图标
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectreport_reportsChart")
                .registKeepParams(new String[]{"page_num", "page_size", "pay_account_org_id",
                        "query_key", "pay_bank_cnaps", "start_date", "end_date"}));

        //已核对/未核对单据
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectcheck_checkbillList")
                .registKeepParams(new String[]{"pay_account_no", "recv_account_no", "is_checked",
                        "min", "max", "page_size", "page_num", "start_date", "end_date"})
                .registerValidate(new RequiredParamsValidate(new String[]{"is_checked"})));
        //根据单据号,查询未核对交易号
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectcheck_checkNoCheckTradeList")
                .registKeepParams(new String[]{"id","date_validate","recv_validate"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id","date_validate","recv_validate"})));

        //根据单据号,查询已核对交易号  checkAlreadyTradeList
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectcheck_checkAlreadyTradeList")
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));
        //交易通_确认核对
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectcheck_confirmCheck")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_id", "trade_id", "persist_version"}))
                .registKeepParams(new String[]{"bill_id", "trade_id", "persist_version"}));


        //归集发送
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectmanage_sendPayList")
                .registerValidate(new RequiredParamsValidate(new String[]{"ids"}))
                .registKeepParams(new String[]{"ids"}));

        //归集日志
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectmanage_instruction")
                .registerValidate(new RequiredParamsValidate(new String[]{"collect_id"}))
                .registKeepParams(new String[]{"collect_id"}));

        //归集作废
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectmanage_cancelinstruction")
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "repeat_count"}))
                .registKeepParams(new String[]{"id", "repeat_count"}));

        //非直联归集 start
        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_addbillexcel")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "uuid", "object_id", "document_id", "origin_name"
                }))
                .registKeepParams(new String[]{
                        "uuid", "object_id", "batchno", "document_id", "origin_name"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_delbillexcel")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batchno", "uuid", "info_id"
                }))
                .registKeepParams(new String[]{
                        "batchno", "uuid", "info_id"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_addbill")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "uuid", "batchno", "recv_account_id", "pay_mode", "apply_on"
                }))
                .registKeepParams(new String[]{
                        "uuid", "batchno", "files", "memo", "recv_account_id", "pay_mode", "biz_id", "biz_name", "apply_on"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_chgbill")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "uuid", "batchno", "id", "persist_version", "recv_account_id", "pay_mode", "apply_on"
                }))
                .registKeepParams(new String[]{
                        "uuid", "batchno", "files", "memo", "id", "persist_version", "recv_account_id", "pay_mode", "biz_id", "biz_name", "apply_on"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_prechgbill")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "uuid", "batchno", "id"
                }))
                .registKeepParams(new String[]{
                        "uuid", "batchno", "id"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_delbill")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version"
                }))
                .registKeepParams(new String[]{
                        "id", "persist_version"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_accs"));

        //指定附件详细列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_batchbillattlist")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "batchno"
                }))
                .registKeepParams(new String[]{
                        "id", "batchno"
                }));

        //预提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_presubmit")
                .registKeepParams(new String[]{
                        "uuid", "batchno", "files", "memo", "id", "persist_version", "recv_account_id", "pay_mode", "biz_id", "biz_name", "apply_on"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "uuid", "batchno", "recv_account_id", "pay_mode", "apply_on"
                })));

        //提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_submit")
                .registKeepParams(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })));
        //撤回
        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_revoke")
                .registKeepParams(new String[]{"id", "persist_version", "service_status"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"})));

        //同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //批量同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        //加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //批量加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"page_size", "page_num", "biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));


        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_detail")
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));

        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_morebill")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_num", "page_size"
                }))
                .registKeepParams(new String[]{
                        "page_num", "page_size", "pay_account_no",
                        "recv_account_no", "min_amount", "max_amount", "service_status",
                        "start_date", "end_date"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_batchlist")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_num", "page_size"
                }))
                .registKeepParams(new String[]{
                        "page_num", "page_size", "pay_account_no",
                        "recv_account_no", "min_amount", "max_amount",
                        "start_date", "end_date", "service_status"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_attclist")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_num", "page_size", "info_id", "uuid", "batchno"
                }))
                .registKeepParams(new String[]{
                        "page_num", "page_size", "info_id", "uuid", "batchno"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "ndc_billdetaillist")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_num", "page_size", "batchno"
                }))
                .registKeepParams(new String[]{
                        "page_num", "page_size", "query_key",
                        "min_amount", "max_amount", "batchno"
                }));
        //非直联归集 end

        /** ============================ 归集通批量交易核对 begin ============================ */

        //单据未核对查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectbatchcheck_checkbillList")
                .registKeepParams(new String[]{"pay_account_no", "recv_account_no", "is_checked",
                        "min", "max", "page_size", "page_num", "start_date", "end_date"}));

        //勾选 查找交易流水
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectbatchcheck_checkNoCheckTradeList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id","date_validate","recv_validate"
                }))
                .registKeepParams(new String[]{"id","date_validate","recv_validate"}));

        //确认交易
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectbatchcheck_confirmCheck")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_id", "trade_id", "persist_version"
                }))
                .registKeepParams(new String[]{"bill_id", "trade_id", "persist_version"}));
        //单据已核对交易查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "collectbatchcheck_checkAlreadyTradeList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                }))
                .registKeepParams(new String[]{"id"}));
        /** ============================ 归集通批量交易核对 end ============================ */
    }
}
