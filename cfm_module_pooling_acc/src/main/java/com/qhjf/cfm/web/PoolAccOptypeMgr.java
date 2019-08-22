package com.qhjf.cfm.web;


import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

/**
 * @author GJF
 * @date 2018年9月12日
 */
public class PoolAccOptypeMgr extends AbstractOptypeMgr {
    @Override
    public void registe() {

        /** ============================ 资金池账户设置 begin ============================ */

        //资金池账户列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "poolacc_acclist")
                .registKeepParams(new String[]{"bank_type", "acc_no", "page_size", "page_num"}));

        //新增一条资金池账户
        optypes.add(new Optype(Optype.Mode.NORMAL, "poolacc_add")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bank_type", "acc_id", "default_flag"
                }))
                .registKeepParams(new String[]{"bank_type", "acc_id", "default_flag"}));

        //删除一条账户信息
        optypes.add(new Optype(Optype.Mode.NORMAL, "poolacc_delete")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id",
                }))
                .registKeepParams(new String[]{"id"}));

        //设为默认
        optypes.add(new Optype(Optype.Mode.NORMAL, "poolacc_defaultset")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                }))
                .registKeepParams(new String[]{"id"}));

        //根据银行大类id查询账户	
        optypes.add(new Optype(Optype.Mode.NORMAL, "poolacc_getaccbybank")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bank_type"
                }))
                .registKeepParams(new String[]{"bank_type"}));

        //查询资金池账户设置默认帐号信息
        optypes.add(new Optype(Optype.Mode.NORMAL, "poolacc_getpoolaccinfo")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bank_type"
                }))
                .registKeepParams(new String[]{
                        "bank_type"
                }));

        //总公司选取资金池账户___default_flag = 1 
        optypes.add(new Optype(Optype.Mode.NORMAL, "poolacc_getDefaultpoolacc"));


        /** ============================ 资金池账户设置 end ============================ */

        /** ============================ 资金下拨交易核对 begin ============================ */

        //单据未核对查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "pooltrad_billList")
                .registKeepParams(new String[]{"pay_query_key", "recv_query_key", "service_status",
                        "min", "max", "page_size", "page_num", "start_date", "end_date"}));

        //勾选 查找交易流水
        optypes.add(new Optype(Optype.Mode.NORMAL, "pooltrad_tradingList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "pay_account_no", "recv_account_no", "payment_amount", "create_on"
                }))
                .registKeepParams(new String[]{"pay_account_no", "recv_account_no", "payment_amount", "create_on"}));

        //确认交易
        optypes.add(new Optype(Optype.Mode.NORMAL, "pooltrad_confirm")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_no", "trading_no", "persist_version"
                }))
                .registKeepParams(new String[]{"bill_no", "trading_no", "persist_version"}));

        //单据已核对查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "pooltrad_confirmbillList")
                .registKeepParams(new String[]{"pay_query_key", "recv_query_key", "service_status",
                        "min", "max", "page_size", "page_num", "start_date", "end_date"}));
        //单据已核对交易查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "pooltrad_confirmTradingList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_no"
                }))
                .registKeepParams(new String[]{"bill_no"}));
        /** ============================ 资金下拨交易核对 end ============================ */

        /** ============================ 资金下拨 begin ============================ */
        //资金下拨 - 新增
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocset_add")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "main_accounts", "topic", "allocation_type", "allocation_amount",
                        "allocation_frequency", "summary", "frequency_detail"
                }))
                .registKeepParams(new String[]{
                        "files", "main_accounts", "topic", "allocation_type", "allocation_amount",
                        "allocation_frequency", "summary", "frequency_detail"
                }));
        //资金下拨 - 修改
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocset_chg")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "main_accounts", "topic", "allocation_type", "allocation_amount",
                        "allocation_frequency", "summary", "frequency_detail", "persist_version"
                }))
                .registKeepParams(new String[]{
                        "id", "main_accounts", "topic", "allocation_type", "allocation_amount",
                        "allocation_frequency", "summary", "frequency_detail", "files", "persist_version"
                }));
        //资金下拨 - 查看详细
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocset_detail")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                }))
                .registKeepParams(new String[]{
                        "id"
                }));
        //资金下拨 - 删除
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocset_del")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version"
                }))
                .registKeepParams(new String[]{
                        "id", "persist_version"
                }));
        //资金下拨 - 主账户列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocset_mainacclist")
                .registerValidate(new RequiredParamsValidate(new String[]{

                }))
                .registKeepParams(new String[]{
                        "query_key", "excludeInstIds"
                }));
        //资金下拨 - 子账户列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocset_childacclist")
                .registerValidate(new RequiredParamsValidate(new String[]{

                }))
                .registKeepParams(new String[]{
                        "query_key", "excludeInstIds", "acc_attr", "bank_type"
                }));
        //资金下拨 - 更多列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocset_morelist")
                .registerValidate(new RequiredParamsValidate(new String[]{

                }))
                .registKeepParams(new String[]{
                        "page_size", "page_num", "query_key", "main_account_query", "service_status", "allocation_type", "allocation_frequency", "is_activity"
                }));

        //-------------------------------------------------------------------------------------

        //资金下拨 - 查看列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocview_list")
                .registerValidate(new RequiredParamsValidate(new String[]{

                }))
                .registKeepParams(new String[]{
                        "page_size", "page_num", "query_key", "main_account_query", "service_status", "allocation_type", "allocation_frequency", "is_activity"
                }));

        //资金下拨 - 管理列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocmgt_list")
                .registerValidate(new RequiredParamsValidate(new String[]{

                }))
                .registKeepParams(new String[]{
                        "query_key", "main_account_query", "service_status", "allocation_type", "allocation_frequency", "is_activity"
                }));

        //资金下拨 - 管理列表 - 激活/暂停
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocmgt_setstatus")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version"
                }))
                .registKeepParams(new String[]{
                        "id", "persist_version"
                }));

        //资金下拨 - 管理列表 - 作废
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocmgt_cancel")
                .registerValidate(new RequiredParamsValidate(new String[]{

                }))
                .registKeepParams(new String[]{
                        "id", "persist_version"
                }));

        //资金下拨 - 报表 - 帐号
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocreport_acctopchar")
                .registerValidate(new RequiredParamsValidate(new String[]{

                }))
                .registKeepParams(new String[]{
                        "start_date", "end_date", "pay_account_org_id", "pay_bank_cnaps"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "allocreport_acclist")
                .registerValidate(new RequiredParamsValidate(new String[]{

                }))
                .registKeepParams(new String[]{
                        "start_date", "end_date", "pay_account_org_id", "pay_bank_cnaps"
                }));

        //资金下拨 - 报表 - 公司
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocreport_orgtopchar")
                .registerValidate(new RequiredParamsValidate(new String[]{

                }))
                .registKeepParams(new String[]{
                        "start_date", "end_date", "pay_account_org_id", "pay_bank_cnaps"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "allocreport_orglist")
                .registerValidate(new RequiredParamsValidate(new String[]{

                }))
                .registKeepParams(new String[]{
                        "start_date", "end_date", "pay_account_org_id", "pay_bank_cnaps"
                }));

        /** ============================ 资金下拨 end ============================ */

        /** ============================ 资金下拨审批流 start ============================ */
        //预提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocset_presubmit")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "main_accounts", "topic", "allocation_type", "allocation_amount",
                        "allocation_frequency", "summary", "frequency_detail"
                }))
                .registKeepParams(new String[]{
                        "id", "main_accounts", "topic", "allocation_type", "allocation_amount",
                        "allocation_frequency", "summary", "frequency_detail", "files", "persist_version"
                }));

        //提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocset_submit")
                .registKeepParams(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })));

        //撤回
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocset_revoke")
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"}))
                .registKeepParams(new String[]{"id", "persist_version", "service_status"}));

        //审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocset_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //批量审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocset_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //审批拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocset_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        //审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocset_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //批量审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocset_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "allocset_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));
        /** ============================ 资金下拨审批流 end ============================ */

    }
}
