package com.qhjf.cfm.web;

import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

/**
 * @auther zhangyuanyuan
 * @create 2018/6/28
 */

public class AccountOptypeMgr extends AbstractOptypeMgr {

    private static final String TODOLIST = "_todolist";
    private static final String TODOADD = "_todoadd";
    private static final String TODOCHG = "_todochg";
    private static final String TODODEL = "_tododel";
    private static final String DONELIST = "_donelist";
    private static final String DONEEND = "_doneend";
    private static final String DONEISSUE = "_doneissue";


    //账户解冻
    private static final String ACC_DE_FREEZE = "accdefreeze";
    //账户冻结
    private static final String ACC_FREEZE = "accfreeze";
    //销户信息补录
    private static final String CLOSE_ACC_COMPLE = "closeacccomple";
    //销户
    private static final String CLOSE_ACC = "closeacc";

    private static String todolist(String key) {
        return key + TODOLIST;
    }

    private static String todoadd(String key) {
        return key + TODOADD;
    }

    private static String todochg(String key) {
        return key + TODOCHG;
    }

    private static String tododel(String key) {
        return key + TODODEL;
    }

    private static String donelist(String key) {
        return key + DONELIST;
    }

    private static String doneend(String key) {
        return key + DONEEND;
    }

    private static String doneissue(String key) {
        return key + DONEISSUE;
    }

    @Override
    public void registe() {
        /** 账户通开户事项申请 start */
        //待办
        optypes.add(new Optype(Optype.Mode.NORMAL, "openintent_todolist")
                .registKeepParams(new String[]{"query_key", "service_status",
                        "page_size", "page_num"}));
        //已办
        optypes.add(new Optype(Optype.Mode.NORMAL, "openintent_donelist")
                .registKeepParams(new String[]{"query_key", "service_status",
                        "start_date", "end_date",
                        "page_size", "page_num"}));
        //新增
        optypes.add(new Optype(Optype.Mode.NORMAL, "openintent_add")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "apply_on", "memo", "detail",
                        "bank_type", "area_code", "bank_cnaps_code",
                        "acc_attr", "acc_purpose", "interactive_mode", "curr_id",
                        "deposits_mode"
                }))
                .registKeepParams(new String[]{
                        "apply_on", "memo", "detail", "files",
                        "bank_type", "area_code", "bank_cnaps_code", "lawfull_man",
                        "acc_attr", "acc_purpose", "interactive_mode", "curr_id", "deposits_mode", "files"
                }));
        //修改
        optypes.add(new Optype(Optype.Mode.NORMAL, "openintent_chg")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version", "apply_on", "memo", "detail",
                        "bank_type", "area_code", "bank_cnaps_code",
                        "acc_attr", "acc_purpose", "interactive_mode", "curr_id", "deposits_mode"
                }))
                .registKeepParams(new String[]{
                        "id", "apply_on", "memo", "detail", "persist_version", "files",
                        "bank_type", "area_code", "bank_cnaps_code", "lawfull_man",
                        "acc_attr", "acc_purpose", "interactive_mode", "curr_id", "deposits_mode", "files"
                }));
        //详细
        optypes.add(new Optype(Optype.Mode.NORMAL, "openintent_detail")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                }))
                .registKeepParams(new String[]{"id"}));
        //删除
        optypes.add(new Optype(Optype.Mode.NORMAL, "openintent_del")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                }))
                .registKeepParams(new String[]{"id", "persist_version"}));
        //分发
        optypes.add(new Optype(Optype.Mode.NORMAL, "openintent_issue")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "iss_users"
                }))
                .registKeepParams(new String[]{"id", "iss_users"}));
        //办结
        optypes.add(new Optype(Optype.Mode.NORMAL, "openintent_finish")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version", "finally_memo"
                }))
                .registKeepParams(new String[]{"id", "persist_version", "finally_memo"}));

        /** 账户通开户事项申请 end */

        /** 账户通开户事项补录 start */
        //待办
        optypes.add(new Optype(Optype.Mode.NORMAL, "opencom_todolist")
                .registKeepParams(new String[]{"query_key", "service_status",
                        "page_size", "page_num"}));
        //已办
        optypes.add(new Optype(Optype.Mode.NORMAL, "opencom_donelist")
                .registKeepParams(new String[]{"query_key", "service_status",
                        "start_date", "end_date",
                        "page_size", "page_num"}));
        //新增
        optypes.add(new Optype(Optype.Mode.NORMAL, "opencom_add")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "relation_id", "acc_no", "acc_name", "open_date",
//                        "bank_address", "bank_contact", "bank_contact_phone",
                        "reserved_seal"
                }))
                .registKeepParams(new String[]{
                        "relation_id", "acc_no", "acc_name", "open_date",
                        "bank_address", "bank_contact", "bank_contact_phone",
                        "memo", "reserved_seal", "files"
                }));
        //修改
        optypes.add(new Optype(Optype.Mode.NORMAL, "opencom_chg")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "relation_id", "acc_no", "acc_name",
//                        "bank_address", "bank_contact", "bank_contact_phone",
                        "persist_version", "reserved_seal"
                }))
                .registKeepParams(new String[]{
                        "id", "relation_id", "acc_no", "acc_name",
                        "bank_address", "bank_contact", "bank_contact_phone",
                        "memo", "persist_version", "reserved_seal", "files"
                }));
        //详情
        optypes.add(new Optype(Optype.Mode.NORMAL, "opencom_detail")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                }))
                .registKeepParams(new String[]{"id"}));
        //删除
        optypes.add(new Optype(Optype.Mode.NORMAL, "opencom_del")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version"
                }))
                .registKeepParams(new String[]{"id", "persist_version"}));

        optypes.add(new Optype(Optype.Mode.NORMAL, "opencom_pass")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version"
                }))
                .registKeepParams(new String[]{"id"}));
        /** 账户通开户事项补录 end */

        /** 账户通账户变更申请 start */
        //待办
        optypes.add(new Optype(Optype.Mode.NORMAL, "openchg_todolist")
                .registKeepParams(new String[]{"query_key", "service_status",
                        "page_size", "page_num"}));
        //已办
        optypes.add(new Optype(Optype.Mode.NORMAL, "openchg_donelist")
                .registKeepParams(new String[]{"query_key", "service_status",
                        "start_date", "end_date",
                        "page_size", "page_num"}));
        //新增
        optypes.add(new Optype(Optype.Mode.NORMAL, "openchg_add")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "acc_id", "change_content"
                }))
                .registKeepParams(new String[]{"acc_id", "change_content", "memo", "files"}));
        //修改
        optypes.add(new Optype(Optype.Mode.NORMAL, "openchg_chg")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "acc_id", "change_content",
                        "persist_version"
                }))
                .registKeepParams(new String[]{
                        "id", "acc_id", "change_content",
                        "persist_version", "memo", "files"
                }));
        //删除
        optypes.add(new Optype(Optype.Mode.NORMAL, "openchg_del")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "persist_version"
                }))
                .registKeepParams(new String[]{"id", "persist_version"}));
        //详情
        optypes.add(new Optype(Optype.Mode.NORMAL, "openchg_detail")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                }))
                .registKeepParams(new String[]{"id"}));

        /** 账户通账户变更申请 end */

        /** 账户通账户信息管理 start */
        optypes.add(new Optype(Optype.Mode.NORMAL, "account_list")
                .registKeepParams(new String[]{"query_key", "service_status",
                        "acc_attr", "interactive_mode", "org_name",
                        "page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.NORMAL, "account_listexport")
                .registKeepParams(new String[]{"query_key", "service_status",
                        "acc_attr", "interactive_mode", "org_name"}));

        //详情
        optypes.add(new Optype(Optype.Mode.NORMAL, "account_detail")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "acc_id"
                }))
                .registKeepParams(new String[]{"acc_id"}));
        //修改
        optypes.add(new Optype(Optype.Mode.NORMAL, "account_chg")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "acc_id",
//                        "bank_address", "bank_contact", "bank_contact_phone",
                        "deposits_mode"
                }))
                .registKeepParams(new String[]{
                        "acc_id",
                        "bank_address", "bank_contact", "bank_contact_phone",
                        "deposits_mode"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "account_accs")
                .registKeepParams(new String[]{"status", "acc_id", "exclude_ids"}));

        /** 账户通账户信息管理 end */

        /** 销户事项申请start **/
        //待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, todolist(CLOSE_ACC))
                .registKeepParams(new String[]{
                        "page_num", "page_size", "query_key", "service_status"
                }));
        //已办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, donelist(CLOSE_ACC))
                .registKeepParams(new String[]{
                        "page_num", "page_size", "query_key", "service_status", "start_date", "end_date"
                }));
        //待办新增
        optypes.add(new Optype(Optype.Mode.NORMAL, todoadd(CLOSE_ACC))
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "memo", "detail", "acc_id"
                }))
                .registKeepParams(new String[]{"memo", "detail", "acc_id", "files"}));

        //待办修改
        optypes.add(new Optype(Optype.Mode.NORMAL, todochg(CLOSE_ACC))
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "memo", "detail", "acc_id"
                }))
                .registKeepParams(new String[]{"id", "memo", "detail", "acc_id", "files"}));
        //待办删除
        optypes.add(new Optype(Optype.Mode.NORMAL, tododel(CLOSE_ACC))
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                }))
                .registKeepParams(new String[]{"id"}));

        //详情
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC + "_detail")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                }))
                .registKeepParams(new String[]{"id"}));

        //已办分发
        optypes.add(new Optype(Optype.Mode.NORMAL, doneissue(CLOSE_ACC))
                .registKeepParams(new String[]{"id", "user_ids"}));
        //已办办结
        optypes.add(new Optype(Optype.Mode.NORMAL, doneend(CLOSE_ACC))
                .registKeepParams(new String[]{"id", "finally_memo"}));
        /** 销户事项申请end **/

        /** 销户事项申请补录start **/
        optypes.add(new Optype(Optype.Mode.NORMAL, todolist(CLOSE_ACC_COMPLE))
                .registKeepParams(new String[]{
                        "page_num", "page_size", "query_key", "service_status"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, donelist(CLOSE_ACC_COMPLE))
                .registKeepParams(new String[]{
                        "page_num", "page_size", "query_key", "service_status", "start_date", "end_date"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, todoadd(CLOSE_ACC_COMPLE))
                .registKeepParams(new String[]{
                        "relation_id",
                        "acc_id",
                        "close_date",
                        "memo",
                        "additionals",
                        "apply_on",
                        "files"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "apply_on"
                })));
        optypes.add(new Optype(Optype.Mode.NORMAL, todochg(CLOSE_ACC_COMPLE))
                .registKeepParams(new String[]{
                        "id",
                        "relation_id",
                        "acc_id",
                        "close_date",
                        "memo",
                        "additionals",
                        "apply_on",
                        "files"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, tododel(CLOSE_ACC_COMPLE))
                .registKeepParams(new String[]{
                        "id"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "closeacccomple_detail")
                .registKeepParams(new String[]{"id"}));
        /** 销户事项申请补录end **/

        /** 账户冻结/解冻 start **/
        //冻结待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, todolist(ACC_FREEZE))
                .registKeepParams(new String[]{
                        "page_num", "page_size", "query_key", "service_status"
                }));
        //冻结已办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, donelist(ACC_FREEZE))
                .registKeepParams(new String[]{
                        "page_num", "page_size", "query_key", "service_status", "start_date", "end_date"
                }));
        //冻结新增
        optypes.add(new Optype(Optype.Mode.NORMAL, todoadd(ACC_FREEZE))
                .registKeepParams(new String[]{
                        "acc_id", "memo", "files"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "acc_id", "memo"
                })));
        //冻结修改
        optypes.add(new Optype(Optype.Mode.NORMAL, todochg(ACC_FREEZE))
                .registKeepParams(new String[]{
                        "id", "acc_id", "memo", "files"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "acc_id", "memo"
                })));

        //冻结详情
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_FREEZE + "_detail")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                }))
                .registKeepParams(new String[]{"id"}));

        //冻结删除
        optypes.add(new Optype(Optype.Mode.NORMAL, tododel(ACC_FREEZE))
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));

        //解冻待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, todolist(ACC_DE_FREEZE))
                .registKeepParams(new String[]{
                        "page_num", "page_size", "query_key", "service_status"
                }));
        //解冻已办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, donelist(ACC_DE_FREEZE))
                .registKeepParams(new String[]{
                        "page_num", "page_size", "query_key", "service_status", "start_date", "end_date"
                }));
        //解冻新增
        optypes.add(new Optype(Optype.Mode.NORMAL, todoadd(ACC_DE_FREEZE))
                .registKeepParams(new String[]{
                        "acc_id", "memo", "files"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "acc_id", "memo"
                })));
        //解冻修改
        optypes.add(new Optype(Optype.Mode.NORMAL, todochg(ACC_DE_FREEZE))
                .registKeepParams(new String[]{
                        "id", "acc_id", "memo", "files"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "acc_id", "memo"
                })));

        //解冻详情
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_DE_FREEZE + "_detail")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id"
                }))
                .registKeepParams(new String[]{"id"}));

        //解冻删除
        optypes.add(new Optype(Optype.Mode.NORMAL, tododel(ACC_DE_FREEZE))
                .registKeepParams(new String[]{"id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id"})));
        /** 账户冻结/解冻 end **/

        /** 工作流 start **/
        //销户预提交
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC + "_presubmit")
                .registKeepParams(new String[]{"id", "memo", "detail", "acc_id", "files"}));
        //销户提交
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC + "_submit")
                .registKeepParams(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })));
        //销户撤回
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC + "_revoke")
                .registKeepParams(new String[]{"id", "persist_version", "service_status"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"})));

        //销户审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC + "_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //销户批量审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC + "_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //销户审批拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC + "_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        //销户审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC + "_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //销户批量审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC + "_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //销户待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC + "_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));

        //销户补录预提交
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC_COMPLE + "_presubmit")
                .registKeepParams(new String[]{
                        "id", "relation_id", "acc_id", "close_date", "memo", "additionals", "apply_on", "files"
                }));
        //销户补录提交
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC_COMPLE + "_submit")
                .registKeepParams(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })));
        //销户补录撤回
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC_COMPLE + "_revoke")
                .registKeepParams(new String[]{"id", "persist_version", "service_status"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"})));

        //销户补录审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC_COMPLE + "_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //销户补录批量审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC_COMPLE + "_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //销户补录审批拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC_COMPLE + "_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        //销户补录审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC_COMPLE + "_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //销户补录批量审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC_COMPLE + "_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //销户补录待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, CLOSE_ACC_COMPLE + "_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));

        //账户冻结预提交
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_FREEZE + "_presubmit")
                .registKeepParams(new String[]{
                        "id", "acc_id", "memo", "files"
                }));
        //账户冻结提交
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_FREEZE + "_submit")
                .registKeepParams(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })));
        //账户冻结撤回
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_FREEZE + "_revoke")
                .registKeepParams(new String[]{"id", "persist_version", "service_status"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"})));

        //账户冻结审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_FREEZE + "_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //账户冻结批量审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_FREEZE + "_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //账户冻结审批拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_FREEZE + "_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        //账户冻结审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_FREEZE + "_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //账户冻结批量审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_FREEZE + "_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //账户冻结待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_FREEZE + "_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));

        //账户解冻预提交
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_DE_FREEZE + "_presubmit")
                .registKeepParams(new String[]{
                        "id", "acc_id", "memo", "files"
                }));
        //账户解冻提交
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_DE_FREEZE + "_submit")
                .registKeepParams(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })));
        //账户解冻撤回
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_DE_FREEZE + "_revoke")
                .registKeepParams(new String[]{"id", "persist_version", "service_status"})
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"})));

        //账户解冻审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_DE_FREEZE + "_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //账户解冻批量审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_DE_FREEZE + "_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //账户解冻审批拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_DE_FREEZE + "_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        //账户解冻审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_DE_FREEZE + "_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //账户解冻批量审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_DE_FREEZE + "_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //账户解冻待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, ACC_DE_FREEZE + "_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));

        //开户预提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "openintent_presubmit")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "apply_on", "memo", "detail",
                        "bank_type", "area_code", "bank_cnaps_code",
                        "acc_attr", "acc_purpose", "interactive_mode", "curr_id", "deposits_mode"
                }))
                .registKeepParams(new String[]{
                        "id", "persist_version", "apply_on", "memo", "detail", "files",
                        "bank_type", "area_code", "bank_cnaps_code", "lawfull_man",
                        "acc_attr", "acc_purpose", "interactive_mode", "curr_id", "deposits_mode", "files"
                }));
        //开户提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "openintent_submit")
                .registKeepParams(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })));

        //开户撤回
        optypes.add(new Optype(Optype.Mode.NORMAL, "openintent_revoke")
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"}))
                .registKeepParams(new String[]{"id", "persist_version", "service_status"}));

        //开户审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "openintent_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //开户批量审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "openintent_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //开户审批拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, "openintent_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        //开户审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "openintent_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //开户批量审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "openintent_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //开户待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "openintent_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));

        //开户补录预提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "opencom_presubmit")
                .registKeepParams(new String[]{
                        "id", "relation_id", "acc_no", "acc_name", "open_date",
                        "bank_address", "bank_contact", "bank_contact_phone", "memo",
                        "persist_version", "reserved_seal", "files"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "relation_id", "acc_no", "acc_name", "open_date",
//                        "bank_address", "bank_contact", "bank_contact_phone", "memo",
                        "reserved_seal"
                })));
        //开户补录提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "opencom_submit")
                .registKeepParams(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                })));
        //开户补录撤回
        optypes.add(new Optype(Optype.Mode.NORMAL, "opencom_revoke")
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"}))
                .registKeepParams(new String[]{"id", "persist_version", "service_status"}));

        //开户补录审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "opencom_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //开户补录批量审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "opencom_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //开户补录审批拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, "opencom_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        //开户补录审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "opencom_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //开户补录批量审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "opencom_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //开户补录待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "opencom_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));

        //账户变更预提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "openchg_presubmit")
                .registKeepParams(new String[]{"id", "acc_id", "change_content", "memo",
                        "persist_version", "files"
                }));
        //账户变更提交
        optypes.add(new Optype(Optype.Mode.NORMAL, "openchg_submit")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "define_id", "id", "service_serial_number", "service_status", "persist_version"
                }))
                .registKeepParams(new String[]{"define_id", "id", "service_serial_number", "service_status", "persist_version"}));
        //账户变更撤回
        optypes.add(new Optype(Optype.Mode.NORMAL, "openchg_revoke")
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "service_status"}))
                .registKeepParams(new String[]{"id", "persist_version", "service_status"}));

        //账户变更审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "openchg_agree")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version"
                })));

        //账户变更批量审批同意
        optypes.add(new Optype(Optype.Mode.NORMAL, "openchg_batchagree")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //账户变更审批拒绝
        optypes.add(new Optype(Optype.Mode.NORMAL, "openchg_reject")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version", "assignee_memo"
                })));

        //账户变更审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "openchg_append")
                .registKeepParams(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name", "assignee_memo"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "wf_inst_id", "define_id", "id", "service_status", "persist_version",
                        "shadow_user_id", "shadow_user_name"
                })));

        //账户变更批量审批加签
        optypes.add(new Optype(Optype.Mode.NORMAL, "openchg_batchappend")
                .registKeepParams(new String[]{
                        "batch_list"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "batch_list"
                })));

        //账户变更待办列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "openchg_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));
        /** 工作流 end **/

        /** 账户确认start **/
        optypes.add(new Optype(Optype.Mode.NORMAL, "accconfirm_list")
                .registKeepParams(new String[]{
                        "page_size", "page_num", "query_key",
                        "acc_attr", "org_id", "interactive_mode"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_size", "page_num"
                })));

        optypes.add(new Optype(Optype.Mode.NORMAL, "accconfirm_setstatus")
                .registKeepParams(new String[]{"acc_id", "subject_code"})
                .registerValidate(new RequiredParamsValidate(new String[]{"acc_id", "subject_code"})));
        /** 账户确认end **/

        /** 账户销户确认start **/
        optypes.add(new Optype(Optype.Mode.NORMAL, "accconfirm_closelist")
                .registKeepParams(new String[]{
                        "page_size", "page_num", "query_key",
                        "acc_attr", "org_id", "interactive_mode"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "page_size", "page_num"
                })));

        optypes.add(new Optype(Optype.Mode.NORMAL, "accconfirm_closesetstatus")
                .registKeepParams(new String[]{"acc_id"})
                .registerValidate(new RequiredParamsValidate(new String[]{"acc_id"})));
        /** 账户销户确认end **/
    }
}
