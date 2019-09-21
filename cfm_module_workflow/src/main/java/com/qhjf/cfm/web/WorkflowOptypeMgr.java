package com.qhjf.cfm.web;

import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;
import com.qhjf.cfm.web.validates.WebConstantParamsValidate;

public class WorkflowOptypeMgr extends AbstractOptypeMgr {
    @Override
    public void registe() {

        /** 工作流定义 start**/
        optypes.add(new Optype(Optype.Mode.ADMIN, "wfdefine_list")
                .registKeepParams(new String[]{"page_size", "page_num", "query_key"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "wfdefine_add")
                .registerValidate(new RequiredParamsValidate(new String[]{"workflow_name"}))
                .registKeepParams(new String[]{"workflow_name"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "wfdefine_del")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
                .registKeepParams(new String[]{"id"}));


//        optypes.add(new Optype(Optype.Mode.ADMIN, "wfdefine_detail")
//                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
//                .registKeepParams(new String[]{"id"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "wfdefine_setstatus")
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version"}))
                .registKeepParams(new String[]{"id", "persist_version"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "wfchart_add")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "workflow_name", "reject_strategy", "lanes", "design_data"}))
                .registKeepParams(new String[]{"base_id", "workflow_name",
                        "reject_strategy", "lanes", "design_data"
                }));

        /** 工作流定义 end**/


        /** 工作流流转 start**/
        optypes.add(new Optype(Optype.Mode.NORMAL, "wfprocess_submit")
                //.registKeepParams(new String[]{"page_size", "page_num", "query_key"}))
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type", "bill_id", "bill_code", "persist_version", "define_id"},
                        new WebConstantParamsValidate("biz_type", WebConstant.MajorBizType.class))));

        optypes.add(new Optype(Optype.Mode.NORMAL, "wfprocess_revoke")
                //.registKeepParams(new String[]{"page_size", "page_num", "query_key"}))
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type", "bill_id", "bill_code", "persist_version", "base_id"},
                        new WebConstantParamsValidate("biz_type", WebConstant.MajorBizType.class))));

        optypes.add(new Optype(Optype.Mode.NORMAL, "wfprocess_agree")
                //.registKeepParams(new String[]{"page_size", "page_num", "query_key"}))
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type", "bill_id", "bill_code", "persist_version", "base_id"},
                        new WebConstantParamsValidate("biz_type", WebConstant.MajorBizType.class))));


        optypes.add(new Optype(Optype.Mode.NORMAL, "wfprocess_reject")
                .registKeepParams(new String[]{"biz_type", "bill_id", "bill_code", "persist_version", "base_id", "assignee_memo"})
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type", "bill_id", "bill_code", "persist_version", "base_id", "assignee_memo"},
                        new WebConstantParamsValidate("biz_type", WebConstant.MajorBizType.class))));

        optypes.add(new Optype(Optype.Mode.NORMAL, "wfprocess_append")
                .registKeepParams(new String[]{"biz_type", "bill_id", "bill_code", "persist_version", "base_id", "assignee_memo"})
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type", "bill_id", "bill_code", "persist_version", "base_id"},
                        new WebConstantParamsValidate("biz_type", WebConstant.MajorBizType.class))));

        /** 工作流流转 end**/

        /** 配置审批流程 start */

        optypes.add(new Optype(Optype.Mode.ADMIN, "wfrelation_list")
                .registerValidate(new RequiredParamsValidate(new String[]{"base_id"}))
                .registKeepParams(new String[]{"base_id", "page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "wfrelation_add")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "base_id", "name", "org_exp", "dept_exp", "biz_exp"
                }))
                .registKeepParams(new String[]{
                        "base_id", "name", "org_exp", "dept_exp", "biz_exp", "biz_setting_exp"
                }));

        optypes.add(new Optype(Optype.Mode.ADMIN, "wfrelation_chg")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "id", "base_id", "name", "org_exp", "dept_exp", "biz_exp", "persist_version"
                }))
                .registKeepParams(new String[]{
                        "id", "base_id", "name", "org_exp", "dept_exp", "biz_exp", "biz_setting_exp", "persist_version"
                }));

        optypes.add(new Optype(Optype.Mode.ADMIN, "wfrelation_del")
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version"}))
                .registKeepParams(new String[]{"id", "persist_version"}));

        /** 配置审批流程 end */

        /** 业务跟踪 start */
        //列表
        optypes.add(new Optype(Optype.Mode.ADMIN, "wftrace_list")
//                .registerValidate(new RequiredParamsValidate(new String[]{}))
                .registKeepParams(new String[]{"bill_code", "biz_type", "start_time", "end_time", "page_size", "page_num"}));
        //撤回
        optypes.add(new Optype(Optype.Mode.ADMIN, "wftrace_approvrevoke")
                .registerValidate(new RequiredParamsValidate(new String[]{"wf_inst_id", "id", "service_status", "persist_version", "biz_type"}))
                .registKeepParams(new String[]{"wf_inst_id", "id", "service_status", "persist_version", "biz_type"}));

        /** 业务跟踪end */

        //审批平台待办列表
        optypes.add(new Optype(Optype.Mode.COMM, "wfquery_pendingtasksall")
                .registKeepParams(new String[]{"page_size", "page_num"}));
        //审批平台待办列表统计
        optypes.add(new Optype(Optype.Mode.COMM, "wfquery_pendingtaskallnum")
                .registKeepParams(new String[]{}));
        //审批平台指定业务待办列表
        optypes.add(new Optype(Optype.Mode.COMM, "wfquery_pendingtasks")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_type"}))
                .registKeepParams(new String[]{"page_size", "page_num", "biz_type"}));

        //审批平台待办列表查看单据详情
        optypes.add(new Optype(Optype.Mode.COMM, "wfquery_pendingitem")
                .registerValidate(new RequiredParamsValidate(new String[]{"wf_inst_id","id","biz_type"}))
                .registKeepParams(new String[]{"wf_inst_id","id","biz_type"}));


        //审批平台已办列表
        optypes.add(new Optype(Optype.Mode.COMM, "wfquery_processtasksall")
                .registerValidate(new RequiredParamsValidate(new String[]{"assignee_id"}))
                .registKeepParams(new String[]{"page_size", "page_num", "assignee_id",
                        "biz_type", "init_user_name", "init_org_name", "init_dept_name",
                        "start_time", "end_time"}));

        optypes.add(new Optype(Optype.Mode.COMM, "wfquery_approvedetail")
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "biz_type"}))
                .registKeepParams(new String[]{"id", "biz_type"}));

        optypes.add(new Optype(Optype.Mode.COMM, "wfquery_wfdetail")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
                .registKeepParams(new String[]{"id"}));

        //审批平台已办列表查看单据详情
        optypes.add(new Optype(Optype.Mode.COMM, "wfquery_processitem")
                .registerValidate(new RequiredParamsValidate(new String[]{"wf_inst_id","id","biz_type"}))
                .registKeepParams(new String[]{"wf_inst_id","id","biz_type"}));






        /** 审批转移 */
        optypes.add(new Optype(Optype.Mode.NORMAL, "wftrans_list")
                .registKeepParams(new String[]{"page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.NORMAL, "wftrans_findauthorizename")
                .registKeepParams(new String[]{"query_key", "page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.NORMAL, "wftrans_add")
                .registerValidate(new RequiredParamsValidate(new String[]{"be_authorize_usr_id", "be_authorize_usr_name", "start_date", "end_date"}))
                .registKeepParams(new String[]{"be_authorize_usr_id", "be_authorize_usr_name", "start_date", "end_date"}));
    }
}
