package com.qhjf.cfm.web;

import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

public class OaOptypeMgr extends AbstractOptypeMgr {
    @Override
    public void registe() {
        //修改
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoa_chg")
                .registKeepParams(new String[]{
                        "id", "persist_version", "pay_account_id", "pay_account_no",
                        "pay_account_name", "pay_account_cur", "pay_account_bank", "pay_bank_cnaps",
                        "pay_bank_prov", "pay_bank_city", "pay_mode", "payment_summary", "files"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version", "pay_account_id", "pay_account_no",
                        "pay_account_name", "pay_account_cur", "pay_account_bank", "pay_bank_cnaps",
                        "pay_bank_prov", "pay_bank_city", "pay_mode", "payment_summary"
                })));
        //删除
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoa_del")
                .registKeepParams(new String[]{
                        "id", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version"
                })));

        //预提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoa_presubmit")
                .registKeepParams(new String[]{
                        "id", "persist_version", "pay_account_id", "pay_account_no",
                        "pay_account_name", "pay_account_cur", "pay_account_bank", "pay_bank_cnaps",
                        "pay_bank_prov", "pay_bank_city", "pay_mode", "payment_summary", "files"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "pay_account_id", "pay_account_no",
                        "pay_account_name", "pay_account_cur", "pay_account_bank", "pay_bank_cnaps",
                        "pay_bank_prov", "pay_bank_city", "pay_mode", "payment_summary"
                })));

        //提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoa_submit")
                .registKeepParams(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })));
        //撤回
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoa_revoke")
                .registKeepParams(new String[]{"id", "persist_version", "service_status"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"})));

        //同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoa_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //批量同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoa_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoa_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        //加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoa_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //批量加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoa_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoa_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"page_size", "page_num", "biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));


        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoa_detail")
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));


        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoa_todolist")
                .registKeepParams(new String[]{
                        "page_num", "page_size", "service_status", "recv_account_query_key", "min", "max", "org_name", "bill_no",
                        "apply_start_date", "apply_end_date"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_num", "page_size"
                })));
        //总公司未处理导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoa_todolistexport")
                .registKeepParams(new String[]{
                        "service_status", "recv_account_query_key", "min", "max", "org_name", "bill_no",
                        "apply_start_date", "apply_end_date"
                }));
        //总公司已处理导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoa_donelistexport")
                .registKeepParams(new String[]{
                        "service_status", "recv_account_query_key", "min", "max", "org_name", "bill_no",
                        "apply_start_date", "apply_end_date", "send_start_date", "send_end_date"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoa_donelist")
                .registKeepParams(new String[]{
                        "page_num", "page_size", "service_status", "recv_account_query_key", "min", "max", "org_name", "bill_no",
                        "apply_start_date", "apply_end_date", "send_start_date", "send_end_date"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_num", "page_size"
                })));

        optypes.add(new Optype(Optype.Mode.NORMAL, "origindataoa_list")
                .registKeepParams(new String[]{
                        "page_num", "page_size", "status", "bill_no", "org_name",
                        "recv_acc_query_key", "pay_org_type", "min_amount", "max_amount",
                        "apply_start_date", "apply_end_date"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_num", "page_size"
                })));

        optypes.add(new Optype(Optype.Mode.NORMAL, "origindataoa_listexport")
                .registKeepParams(new String[]{
                        "status", "bill_no", "org_name",
                        "recv_acc_query_key", "pay_org_type", "min_amount", "max_amount",
                        "apply_start_date", "apply_end_date"
                }));
        //总公司支付作废  payOff
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoa_payOff")
                .registerValidate(new RequiredParamsValidate(new String[]{"ids", "persist_version"}))
                .registKeepParams(new String[]{"ids", "persist_version", "feed_back"}));

        //===============================分公司付费====================================

        //分公司公司支付作废  payOff
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_payOff")
                .registerValidate(new RequiredParamsValidate(new String[]{"ids", "persist_version"}))
                .registKeepParams(new String[]{"ids", "persist_version", "feed_back"}));

        //修改
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_chgBranchPayment")
                .registKeepParams(new String[]{
                        "id", "persist_version", "pool_account_id", "pay_account_id",
                        "pay_mode", "payment_summary", "files"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version", "pool_account_id", "pay_account_id",
                        "pay_mode"
                })));

        // 分公司付费未处理列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_oaTodoList")
                .registKeepParams(new String[]{
                        "page_num", "page_size", "service_status", "recv_query_key", "min", "max", "org_name", "bill_no",
                        "apply_start_date", "apply_end_date"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_num", "page_size"
                })));

        // 分公司付费未处理列表导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_todolistexport")
                .registKeepParams(new String[]{
                        "service_status", "recv_query_key", "min", "max", "org_name", "bill_no",
                        "apply_start_date", "apply_end_date"
                }));

        // 分公司付费已处理列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_oaDoneList")
                .registKeepParams(new String[]{
                        "page_num", "page_size", "service_status", "recv_query_key", "min", "max", "org_name", "bill_no",
                        "apply_start_date", "apply_end_date", "send_start_date", "send_end_date"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_num", "page_size"
                })));

        // 分公司付费已处理列表导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_donelistexport")
                .registKeepParams(new String[]{
                        "service_status", "recv_query_key", "min", "max", "org_name", "bill_no",
                        "apply_start_date", "apply_end_date", "send_start_date", "send_end_date"
                }));

        // 分公司付费_中间账户列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_accListByOrg")
                .registKeepParams(new String[]{
                        "id"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                })));


        //分公司付费_资金池账户列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_poolAccListByBankType")
                .registKeepParams(new String[]{
                        "bank_type"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bank_type"
                })));
        //分公司付费_详情
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_detail")
                .registKeepParams(new String[]{
                        "id"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                })));

        //分公司付费_预提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_presubmit")
                .registKeepParams(new String[]{
                        "id", "persist_version", "pool_account_id", "pay_account_id",
                        "pay_mode", "payment_summary", "files"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version", "pool_account_id", "pay_account_id",
                        "pay_mode"
                })));

        //分公司付费_提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_submit")
                .registKeepParams(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })));
        //撤回
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_revoke")
                .registKeepParams(new String[]{"id", "persist_version", "service_status"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"})));

        //同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //批量同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        //加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //批量加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoa_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"page_size", "page_num", "biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));


        /** ============================ OA分公司交易核对 begin ============================ */

        //单据核对列表查询     未核对 / 已核对
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoacheck_checkbillList")
                .registKeepParams(new String[]{"pay_account_no", "recv_account_no", "is_checked",
                        "min", "max", "page_size", "page_num", "start_date", "end_date"}));

        //勾选 查找交易流水
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoacheck_checkTradeList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id","date_validate","recv_validate"
                }))
                .registKeepParams(new String[]{"id","date_validate","recv_validate"}));

        //确认交易
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoacheck_confirmCheck")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_id", "trade_id", "persist_version"
                }))
                .registKeepParams(new String[]{"bill_id", "trade_id", "persist_version"}));
        //单据已核对交易查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "branchorgoacheck_checkAlreadyTradeList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                }))
                .registKeepParams(new String[]{"id"}));
        /** ============================ OA分公司交易核对 end ============================ */


        /** ============================ OA总公司交易核对 begin ============================ */

        //单据未核对查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoacheck_checkbillList")
                .registKeepParams(new String[]{"pay_account_no", "recv_account_no", "is_checked",
                        "min", "max", "page_size", "page_num", "start_date", "end_date"}));

        //勾选 查找交易流水
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoacheck_checkTradeList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id","date_validate","recv_validate"
                }))
                .registKeepParams(new String[]{"id","date_validate","recv_validate"}));

        //确认交易
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoacheck_confirmCheck")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_id", "trade_id", "persist_version"
                }))
                .registKeepParams(new String[]{"bill_id", "trade_id", "persist_version"}));
        //单据已核对交易查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "headorgoacheck_checkAlreadyTradeList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                }))
                .registKeepParams(new String[]{"id"}));
        /** ============================ OA总公司交易核对 end ============================ */

        /** ============================ OA可疑数据管理 begin ============================ */

        //查询列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkdoubtfuloa_list")
                .registKeepParams(new String[]{"bill_no", "org_name", "recv_acc_no",
                        "pay_org_type", "min_amount", "max_amount", "page_size", "page_num"}));

        //查询列表导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkdoubtfuloa_listexport")
                .registKeepParams(new String[]{"bill_no", "org_name", "recv_acc_no",
                        "pay_org_type", "min_amount", "max_amount"}));

        //查看疑似重复单据列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkdoubtfuloa_doubtlist")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_no", "identification"
                }))
                .registKeepParams(new String[]{"bill_no", "identification"}));

        //可疑数据作废
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkdoubtfuloa_payoff")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "ids", "persist_version", "feed_back"
                }))
                .registKeepParams(new String[]{"ids", "persist_version", "feed_back"}));

        //可疑数据通过
        optypes.add(new Optype(Optype.Mode.NORMAL, "checkdoubtfuloa_pass")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version"
                }))
                .registKeepParams(new String[]{"id", "persist_version"}));

        /** ============================OA可疑数据管理 end ============================ */
    }


}
