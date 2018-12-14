package com.qhjf.cfm.web;

import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

/**
 * 收款通路由
 *
 * @author GJF
 * @date 2018年9月18日
 */
public class SktOptypeMgr extends AbstractOptypeMgr {

    @Override
    public void registe() {
        /** ============================ 收款通业务 begin ============================ */
        //制单 -- 新增单据
        optypes.add(new Optype(Optype.Mode.NORMAL, "skt_addbill")
                .registKeepParams(new String[]{"recv_account_id", "receipts_amount", "pay_account_id", "pay_account_no", "pay_account_name", "pay_bank_cnaps",
                        "receipts_summary", "pay_persist_version", "files", "biz_id", "biz_name"})
                .registerValidate(new RequiredParamsValidate(new String[]{"recv_account_id", "pay_account_no",
                        "pay_account_name", "pay_bank_cnaps", "receipts_amount", "biz_id", "biz_name"})));

        //制单 --	单据修改
        optypes.add(new Optype(Optype.Mode.NORMAL, "skt_chgbill")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "recv_account_id", "pay_account_id", "pay_account_no",
                        "pay_account_name", "pay_bank_cnaps", "receipts_amount", "biz_id", "biz_name"
                })).registKeepParams(new String[]{"recv_account_id", "receipts_amount", "pay_account_id", "pay_account_no", "pay_account_name", "pay_bank_cnaps",
                        "receipts_summary", "pay_persist_version", "files", "id", "persist_version", "biz_id", "biz_name"}));

        //制单 -- 删除单据
        optypes.add(new Optype(Optype.Mode.NORMAL, "skt_delbill")
                .registKeepParams(new String[]{"id", "persist_version"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version"})));

        //制单 --更多单据
        optypes.add(new Optype(Optype.Mode.NORMAL, "skt_morebills")
                .registKeepParams(new String[]{"pay_query_key", "recv_query_key", "service_status",
                        "min", "max", "start_date", "end_date",
                        "page_size", "page_num"}));

        //制单 --	单据列表 skt_bills
        optypes.add(new Optype(Optype.Mode.NORMAL, "skt_bills")
                .registKeepParams(new String[]{"pay_query_key", "recv_query_key", "service_status",
                        "min", "max", "start_date", "end_date",
                        "page_size", "page_num"}));
        //制单 --	单据详情
        optypes.add(new Optype(Optype.Mode.NORMAL, "skt_billdetail")
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));

        optypes.add(new Optype(Optype.Mode.NORMAL, "skt_chgservicestatus")
                .registKeepParams(new String[]{"id", "persist_version"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version"})));

        /** ============================ 收款通业务 end ============================ */

        /** ============================ 收款通工作流 begin ============================ */

        //预提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "skt_presubmit")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "recv_account_id", "pay_account_no",
                        "pay_account_name", "pay_bank_cnaps", "receipts_amount", "biz_id", "biz_name"
                }))
                .registKeepParams(new String[]{"recv_account_id", "receipts_amount", "pay_account_id", "pay_account_no", "pay_account_name", "pay_bank_cnaps",
                        "receipts_summary", "pay_persist_version", "files", "id", "persist_version", "biz_id", "biz_name"
                }));

        //提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "skt_submit")
                .registKeepParams(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })));
        //撤回
        optypes.add(new Optype(Optype.Mode.NORMAL, "skt_revoke")
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"}))
                .registKeepParams(new String[]{"id", "persist_version", "service_status"}));

        //审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "skt_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //批量审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "skt_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //审批拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, "skt_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        //审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "skt_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //批量审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "skt_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "skt_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));

        /** ============================ 收款通工作流 end ============================ */

        /** ============================ 收款通交易核对 begin ============================ */

        //单据未核对查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "skttrad_billList")
                .registKeepParams(new String[]{"pay_query_key", "recv_query_key", "service_status",
                        "min", "max", "page_size", "page_num", "start_date", "end_date"}));

        //勾选 查找交易流水
        optypes.add(new Optype(Optype.Mode.NORMAL, "skttrad_tradingList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "pay_account_no", "recv_account_no", "payment_amount", "create_on"
                }))
                .registKeepParams(new String[]{"pay_account_no", "recv_account_no", "payment_amount", "create_on"}));

        //确认交易
        optypes.add(new Optype(Optype.Mode.NORMAL, "skttrad_confirm")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_no", "trading_no", "persist_version"
                }))
                .registKeepParams(new String[]{"bill_no", "trading_no", "persist_version"}));

        //单据已核对查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "skttrad_confirmbillList")
                .registKeepParams(new String[]{"pay_query_key", "recv_query_key", "service_status",
                        "min", "max", "page_size", "page_num", "start_date", "end_date"}));
        //单据已核对交易查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "skttrad_confirmTradingList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_no"
                }))
                .registKeepParams(new String[]{"bill_no"}));
        /** ============================ 收款通交易核对 end ============================ */

    }
}
