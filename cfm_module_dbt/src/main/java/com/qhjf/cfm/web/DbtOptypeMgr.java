package com.qhjf.cfm.web;

import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/8/9
 * @Description:
 */
public class DbtOptypeMgr extends AbstractOptypeMgr {
    @Override
    public void registe() {
        /** ============================ 调拨通业务 begin ============================ */
        //更多列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_morelist")
                .registKeepParams(new String[]{"pay_query_key", "recv_query_key", "service_status",
                        "payment_type", "min", "max", "start_date", "end_date", "pay_mode", "biz_id",
                        "page_size", "page_num"}));

        //查看列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_detaillist")
                .registKeepParams(new String[]{"pay_query_key", "recv_query_key", "service_status",
                        "payment_type", "min", "max", "start_date", "end_date", "pay_mode", "biz_id",
                        "page_size", "page_num"}));

        //支付列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_paylist")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "pay_mode"
                }))
                .registKeepParams(new String[]{"pay_query_key", "recv_query_key", "service_status",
                        "payment_type", "pay_mode", "min", "max", "start_date", "end_date", "pay_mode", "biz_id",
                        "page_size", "page_num"}));

        //新增
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_add")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "pay_account_id", "recv_account_id", "payment_amount", "pay_mode", "biz_id", "biz_name", "apply_on"
                }))
                .registKeepParams(new String[]{"pay_account_id", "recv_account_id", "payment_amount", "pay_mode", "payment_summary", "biz_id", "biz_name", "files", "apply_on"}));

        //修改
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_chg")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "pay_account_id", "recv_account_id", "payment_amount", "pay_mode", "persist_version", "biz_id", "biz_name", "apply_on"
                }))
                .registKeepParams(new String[]{"id", "pay_account_id", "recv_account_id", "payment_amount", "pay_mode", "payment_summary", "biz_id", "biz_name", "persist_version", "files", "apply_on"}));

        //详细
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_detail")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
                .registKeepParams(new String[]{"id"}));

        //删除
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_del")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
                .registKeepParams(new String[]{"id", "persist_version"}));

        //作废
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_cancel")
                .registerValidate(new RequiredParamsValidate(new String[]{"ids", "feed_back"}))
                .registKeepParams(new String[]{"ids", "feed_back"}));

        //发送
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_send")
                .registerValidate(new RequiredParamsValidate(new String[]{"ids"}))
                .registKeepParams(new String[]{"ids"}));

        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_sendPayList")
                .registerValidate(new RequiredParamsValidate(new String[]{"ids"}))
                .registKeepParams(new String[]{"ids"}));

        //付款方账户列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_payacclist")
                .registerValidate(new RequiredParamsValidate(new String[]{"interactive_mode"}))
                .registKeepParams(new String[]{"query_key", "exclude_ids", "interactive_mode", "page_size", "page_num"}));

        //收款方账户列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_recvacclist")
                .registKeepParams(new String[]{"query_key", "exclude_ids", "page_size", "page_num"}));

        //支付确认
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_payconfirm")
                .registerValidate(new RequiredParamsValidate(new String[]{"ids"}))
                .registKeepParams(new String[]{"ids"}));

        /** ============================ 调拨通业务 end ============================ */

        /** ============================ 调拨通工作流 begin ============================ */

        //预提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_presubmit")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "pay_account_id", "recv_account_id", "payment_amount", "pay_mode", "biz_id", "biz_name", "apply_on"
                }))
                .registKeepParams(new String[]{
                        "id", "persist_version", "pay_account_id", "recv_account_id", "payment_amount", "pay_mode", "payment_summary", "files", "biz_id", "biz_name", "apply_on"
                }));

        //提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_submit")
                .registKeepParams(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })));

        //撤回
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_revoke")
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"}))
                .registKeepParams(new String[]{"id", "persist_version", "service_status"}));

        //审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //批量审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //审批拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        //审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //批量审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbt_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));

        /** ============================ 工作流 end ============================ */

        /** ============================ 调拨通交易核对 begin ============================ */

        //单据未核对查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbttrad_billList")
                .registKeepParams(new String[]{"pay_query_key", "recv_query_key", "service_status",
                        "min", "max", "page_size", "page_num", "start_date", "end_date"}));

        //勾选 查找交易流水
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbttrad_tradingList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "pay_account_no", "recv_account_no", "payment_amount", "create_on"
                }))
                .registKeepParams(new String[]{"pay_account_no", "recv_account_no", "payment_amount", "create_on"}));

        //确认交易
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbttrad_confirm")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_no", "trading_no", "persist_version"
                }))
                .registKeepParams(new String[]{"bill_no", "trading_no", "persist_version"}));

        //单据已核对查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbttrad_confirmbillList")
                .registKeepParams(new String[]{"pay_query_key", "recv_query_key", "service_status",
                        "min", "max", "page_size", "page_num", "start_date", "end_date"}));
        //单据已核对交易查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbttrad_confirmTradingList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_no"
                }))
                .registKeepParams(new String[]{"bill_no"}));
        /** ============================ 调拨通交易核对 end ============================ */

        /** ============================ 调拨通批量交易核对 begin ============================ */

        //单据未核对查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatchtrad_billList")
                .registKeepParams(new String[]{"pay_query_key", "recv_query_key", "service_status",
                        "min", "max", "page_size", "page_num", "start_date", "end_date"}));

        //勾选 查找交易流水
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatchtrad_tradingList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "pay_account_no", "recv_account_no", "payment_amount", "create_on"
                }))
                .registKeepParams(new String[]{"pay_account_no", "recv_account_no", "payment_amount", "create_on"}));

        //确认交易
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatchtrad_confirm")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_no", "trading_no", "persist_version"
                }))
                .registKeepParams(new String[]{"bill_no", "trading_no", "persist_version"}));

        //单据已核对查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatchtrad_confirmbillList")
                .registKeepParams(new String[]{"pay_query_key", "recv_query_key", "service_status",
                        "min", "max", "page_size", "page_num", "start_date", "end_date"}));
        //单据已核对交易查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatchtrad_confirmTradingList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_no"
                }))
                .registKeepParams(new String[]{"bill_no"}));
        /** ============================ 调拨通批量交易核对 end ============================ */

        /** ============================ 调拨通批量业务 begin ============================ */
        //列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_list")
                .registerValidate(new RequiredParamsValidate(new String[]{

                }))
                .registKeepParams(new String[]{
                        "pay_query_key", "recv_query_key",
                        "min", "max", "start_date", "end_date", "service_status", "pay_mode",
                        "page_size", "page_num"
                }));

        //支付列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_paylist")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "pay_mode"
                }))
                .registKeepParams(new String[]{
                        "pay_query_key", "recv_query_key",
                        "min", "max", "start_date", "end_date", "service_status", "pay_mode",
                        "page_size", "page_num"
                }));

        //查看列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_viewlist")
                .registerValidate(new RequiredParamsValidate(new String[]{

                }))
                .registKeepParams(new String[]{
                        "pay_query_key", "recv_query_key",
                        "min", "max", "start_date", "end_date", "service_status", "pay_mode",
                        "page_size", "page_num"
                }));

        //导入
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_imports")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "uuid", "document_id", "origin_name", "object_id"
                }))
                .registKeepParams(new String[]{
                        "uuid", "document_id", "origin_name", "object_id", "batchno"
                }));

        //新增
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_add")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "uuid", "batchno", "pay_account_id",
                        "pay_mode", "biz_id", "biz_name", "apply_on"
                }))
                .registKeepParams(new String[]{
                        "uuid", "batchno", "pay_account_id",
                        "biz_id", "biz_name", "pay_mode", "payment_summary", "files", "apply_on"
                }));

        //修改
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_chg")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version",
                        "batchno", "uuid", "pay_account_id",
                        "pay_mode", "biz_id", "biz_name", "apply_on"
                }))
                .registKeepParams(new String[]{
                        "id", "persist_version",
                        "batchno", "uuid", "pay_account_id", "payment_summary",
                        "biz_id", "biz_name", "pay_mode", "files", "apply_on"
                }));

        //查看
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_detail")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "batchno"
                }))
                .registKeepParams(new String[]{
                        "id", "batchno"
                }));


        //删除
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_del")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version"
                }))
                .registKeepParams(new String[]{
                        "id", "persist_version"
                }));


        //初始化临时信息
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_initchgtemp")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batchno", "uuid", "id"
                }))
                .registKeepParams(new String[]{
                        "batchno", "uuid", "id"
                }));

        //临时表修改
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_removetemp")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "uuid", "batchno"
                }))
                .registKeepParams(new String[]{
                        "id", "uuid", "batchno"
                }));

        //查看批次汇总
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_viewbill")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batchno"
                }))
                .registKeepParams(new String[]{
                        "batchno"
                }));

        //批次详细列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_detaillist")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batchno"
                }))
                .registKeepParams(new String[]{
                        "batchno", "recv_query_key", "min", "max",
                        "page_size", "page_num", "pay_status"
                }));

        //指定附件详细列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_batchbillattlist")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "batchno"
                }))
                .registKeepParams(new String[]{
                        "id", "batchno", "recv_account_query", "min", "max", "page_size", "page_num"
                }));

        //批次作废
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_cancel")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version", "feed_back"
                }))
                .registKeepParams(new String[]{
                        "id", "persist_version", "feed_back"
                }));

        //详细作废
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_cancelids")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "detail_ids", "persist_version", "feed_back"
                }))
                .registKeepParams(new String[]{
                        "id", "detail_ids", "persist_version", "feed_back"
                }));

        //发送
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_sendpaylist")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "detail_ids"
                }))
                .registKeepParams(new String[]{
                        "id", "detail_ids"
                }));

        //批次支付确认
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_paybatchconfirm")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version"
                }))
                .registKeepParams(new String[]{
                        "id", "persist_version"
                }));

        //详细支付确认
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_payconfirm")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "detail_ids", "persist_version"
                }))
                .registKeepParams(new String[]{
                        "id", "detail_ids", "persist_version"
                }));

        /** ============================ 调拨通批量业务 end ============================ */

        /** ============================ 调拨通批量工作流 begin ============================ */
        //预提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_presubmit")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        //"id", "persist_version",
                        "batchno", "uuid", "pay_account_id",
                        "pay_mode", "biz_id", "biz_name", "apply_on"
                }))
                .registKeepParams(new String[]{
                        "id", "persist_version",
                        "batchno", "uuid", "pay_account_id",
                        "biz_id", "biz_name", "pay_mode", "payment_summary", "files", "apply_on"
                }));

        //提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_submit")
                .registKeepParams(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })));

        //撤回
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_revoke")
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"}))
                .registKeepParams(new String[]{"id", "persist_version", "service_status"}));

        //审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //批量审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //审批拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        //审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //批量审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "dbtbatch_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));

        /** ============================ 调拨通批量工作流 end ============================ */

    }
}
