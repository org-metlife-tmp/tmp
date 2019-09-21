package com.qhjf.cfm.web;

import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/8/9
 * @Description:
 */
public class ZftOptypeMgr extends AbstractOptypeMgr {
    @Override
    public void registe() {
        //供应商列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "supplier_list")
                .registKeepParams(new String[]{"page_size", "page_num", "query_key"}));

        //供应商-新增
        optypes.add(new Optype(Optype.Mode.NORMAL, "supplier_add")
                .registKeepParams(new String[]{"acc_no", "acc_name", "cnaps_code", "type", "curr_id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"acc_no", "acc_name", "cnaps_code", "type", "curr_id"})));
        //供应商-修改
        optypes.add(new Optype(Optype.Mode.NORMAL, "supplier_chg")
                .registKeepParams(new String[]{"id", "acc_no", "acc_name", "cnaps_code", "type", "curr_id", "persist_version"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "acc_no", "acc_name", "cnaps_code", "type", "curr_id", "persist_version"})));
        //供应商-删除
        optypes.add(new Optype(Optype.Mode.NORMAL, "supplier_del")
                .registKeepParams(new String[]{"id", "persist_version"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version"})));
        //制单 -- 新增单据
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_addbill")
                .registKeepParams(new String[]{"pay_account_id", "payment_amount", "recv_account_id", "recv_account_no", "recv_account_name", "recv_bank_cnaps",
                        "payment_summary", "rev_persist_version", "files", "biz_id", "biz_name", "pay_mode", "apply_on"})
                .registerValidate(new RequiredParamsValidate(new String[]{"pay_account_id", "payment_amount", "pay_mode", "biz_id", "biz_name", "apply_on"})));
        //制单 -- 删除单据
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_delbill")
                .registKeepParams(new String[]{"id", "persist_version"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version"})));
        //制单 --更多单据
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_morebills")
                .registKeepParams(new String[]{"pay_query_key", "recv_query_key", "service_status",
                        "min", "max", "start_date", "end_date", "pay_mode",
                        "page_size", "page_num"}));
        //制单 --	单据列表 zft_bills
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_bills")
                .registKeepParams(new String[]{"pay_query_key", "recv_query_key", "service_status",
                        "min", "max", "start_date", "end_date", "pay_mode",
                        "page_size", "page_num"}));
        //制单 --	单据详情
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_billdetail")
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));
        //制单 --	单据修改
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_chgbill")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "pay_account_id", "payment_amount", "persist_version", "pay_mode", "biz_id", "biz_name", "apply_on"
                })).registKeepParams(new String[]{"pay_account_id", "payment_amount", "recv_account_id", "recv_account_no", "recv_account_name", "recv_bank_cnaps",
                        "payment_summary", "rev_persist_version", "files", "id", "persist_version", "biz_id", "biz_name", "pay_mode", "apply_on"}));

        //预提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_presubmit")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "pay_account_id", "payment_amount", "pay_mode", "biz_id", "biz_name", "apply_on"
                }))
                .registKeepParams(new String[]{
                        "id", "pay_account_id", "payment_amount", "recv_account_id", "recv_account_no", "recv_account_name", "recv_bank_cnaps",
                        "payment_summary", "rev_persist_version", "files", "persist_version", "biz_id", "biz_name", "pay_mode", "apply_on"
                }));

        //提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_submit")
                .registKeepParams(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })));

        //收款方账户列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_recvacclist")
                .registKeepParams(new String[]{"query_key", "page_size", "page_num"}));

        //支付作废  zft_payOff
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_payOff")
                .registerValidate(new RequiredParamsValidate(new String[]{"ids", "persist_version"}))
                .registKeepParams(new String[]{"ids", "persist_version", "feed_back"}));

        //支付列表  zft_payList
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_payList")
                .registKeepParams(new String[]{"pay_query_key", "recv_query_key", "service_status",
                        "min", "max", "start_date", "end_date",
                        "page_size", "page_num", "pay_mode"})
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_size", "page_num", "pay_mode"
                })));

        //支付发送 zft_sendPayList
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_sendPayList")
                .registerValidate(new RequiredParamsValidate(new String[]{"ids"}))
                .registKeepParams(new String[]{"ids"}));


        //单据核对列表查询     未核对 / 已核对
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_checkbillList")
                .registKeepParams(new String[]{"pay_account_no", "recv_account_no", "is_checked",
                        "min", "max", "page_size", "page_num", "start_date", "end_date"}));
        //根据单据号,查询未核对交易号  checkAlreadyTradeList
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_checkTradeList")
                .registKeepParams(new String[]{"id", "date_validate", "recv_validate"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "date_validate", "recv_validate"})));
        //根据单据号,查询已核对交易号  checkAlreadyTradeList
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_checkAlreadyTradeList")
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));

        //交易通_确认核对
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_confirmCheck")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_id", "trade_id", "persist_version"
                }))
                .registKeepParams(new String[]{"bill_id", "trade_id", "persist_version"}));

        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_revoke")
                .registKeepParams(new String[]{"id", "persist_version", "service_status"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"})));

        /** 支付通批量操作 start **/
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_addbillexcel")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "uuid", "object_id", "document_id", "origin_name"
                }))
                .registKeepParams(new String[]{
                        "uuid", "object_id", "batchno", "document_id", "origin_name"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_delbillexcel")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batchno", "uuid", "info_id"
                }))
                .registKeepParams(new String[]{
                        "batchno", "uuid", "info_id"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_addbill")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "uuid", "batchno", "pay_acc_id", "pay_mode", "biz_id", "biz_name", "apply_on"
                }))
                .registKeepParams(new String[]{
                        "uuid", "batchno", "files", "memo", "pay_acc_id", "pay_mode", "biz_id", "biz_name", "apply_on"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_chgbill")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "uuid", "batchno", "id", "version", "pay_acc_id", "pay_mode", "biz_id", "biz_name", "apply_on"
                }))
                .registKeepParams(new String[]{
                        "uuid", "batchno", "files", "memo", "id", "version", "pay_acc_id", "pay_mode", "biz_id", "biz_name", "apply_on"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_prechgbill")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "uuid", "batchno", "id"
                }))
                .registKeepParams(new String[]{
                        "uuid", "batchno", "id"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_attclist")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_num", "page_size", "info_id", "uuid", "batchno"
                }))
                .registKeepParams(new String[]{
                        "page_num", "page_size", "info_id", "uuid", "batchno"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_delbill")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "version"
                }))
                .registKeepParams(new String[]{
                        "id", "version"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_morebill")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_num", "page_size"
                }))
                .registKeepParams(new String[]{
                        "pay_query_key", "recv_query_key",
                        "min", "max", "start_date", "end_date", "service_status", "pay_mode",
                        "page_size", "page_num"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_paybatchlist")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_num", "page_size"
                }))
                .registKeepParams(new String[]{
                        "pay_query_key", "recv_query_key",
                        "min", "max", "start_date", "end_date", "service_status", "pay_mode",
                        "page_size", "page_num"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_billdetaillist")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_num", "page_size", "batchno"
                }))
                .registKeepParams(new String[]{
                        "page_num", "page_size", "query_key",
                        "min_amount", "max_amount", "batchno", "pay_status"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_billdetail")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                }))
                .registKeepParams(new String[]{
                        "id"
                }));
        //指定附件详细列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_batchbillattlist")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "batchno"
                }))
                .registKeepParams(new String[]{
                        "id", "batchno"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_presubmit")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "uuid", "batchno", "pay_acc_id", "pay_mode", "biz_id", "biz_name", "apply_on"
                }))
                .registKeepParams(new String[]{
                        "id", "uuid", "batchno", "files", "memo", "pay_acc_id", "pay_mode", "version", "biz_id", "biz_name", "apply_on"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_submit")
                .registKeepParams(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })));
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_revoke")
                .registKeepParams(new String[]{"id", "persist_version", "service_status"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"})));

        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //批量审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //批量审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));

        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));

        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_processedtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));

        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_detail")
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));

        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_detail")
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));


        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //批量审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //批量审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //支付作废  payOff
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_payOff")
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version"}))
                .registKeepParams(new String[]{"id", "persist_version", "feed_back"}));

        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_payOneOff")
                .registerValidate(new RequiredParamsValidate(new String[]{"ids", "persist_version", "batchno"}))
                .registKeepParams(new String[]{"ids", "persist_version", "feed_back", "batchno"}));

        //支付列表  payList
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_payList")
                .registKeepParams(new String[]{"pay_query_key", "recv_query_key", "service_status",
                        "min", "max", "start_date", "end_date", "pay_mode",
                        "page_size", "page_num"}));

        //支付发送 sendPayList
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_sendpaylist")
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "ids"}))
                .registKeepParams(new String[]{"id", "ids"}));

        optypes.add(new Optype(Optype.Mode.NORMAL, "zft_payok")
                .registKeepParams(new String[]{
                        "ids"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_payok")
                .registKeepParams(new String[]{
                        "id", "persist_version", "batchno"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatch_payokbyids")
                .registKeepParams(new String[]{
                        "ids", "persist_version", "batchno"
                }));

        /** ============================ 支付通批量交易核对 begin ============================ */

        //单据未核对查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatchcheck_checkbillList")
                .registKeepParams(new String[]{"pay_account_no", "recv_account_no", "is_checked",
                        "min", "max", "page_size", "page_num", "start_date", "end_date"}));

        //勾选 查找交易流水
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatchcheck_checkNoCheckTradeList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "date_validate", "recv_validate"
                }))
                .registKeepParams(new String[]{"id", "date_validate", "recv_validate"}));

        //确认交易
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatchcheck_confirmCheck")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_id", "trade_id", "persist_version"
                }))
                .registKeepParams(new String[]{"bill_id", "trade_id", "persist_version"}));
        //单据已核对交易查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftbatchcheck_checkAlreadyTradeList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                }))
                .registKeepParams(new String[]{"id"}));
        /** ============================ 支付通批量交易核对 end ============================ */

        /** ============================ 支付通-重复退票 begin =============================*/
        //支付通-重复退票-列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftrefund_list")
                .registKeepParams(new String[]{
                        "page_size", "page_num",
                        "start_trans_date", "end_trans_date",
                        "min", "max", "direction", "acc_no", "opp_acc_no"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "zftrefund_confirm")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "trans_id", "biz_id"
                }))
                .registKeepParams(new String[]{
                        "trans_id", "biz_id", "opp_acc_id", "opp_acc_no",
                        "opp_acc_name", "opp_cnaps_code", "summary"
                }));
        /** ============================ 支付通-重复退票 end =============================*/
    }


}
