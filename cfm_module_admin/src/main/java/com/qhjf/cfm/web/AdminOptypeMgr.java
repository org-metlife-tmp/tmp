package com.qhjf.cfm.web;

import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;
import com.qhjf.cfm.web.validates.UdopsValidate;
import com.qhjf.cfm.web.validates.WebConstantParamsValidate;

public class AdminOptypeMgr extends AbstractOptypeMgr {

    @Override
    public void registe() {
        /** 机构维护 start**/
        optypes.add(new Optype(Optype.Mode.ADMIN, "org_list")
                .registKeepParams(new String[]{"page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "org_add")
                .registerValidate(new RequiredParamsValidate(new String[]{"parent_id", "name", "code"}))
                .registKeepParams(new String[]{"parent_id", "code", "province", "city", "address", "name", "extra_infos"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "org_del")
                .registerValidate(new RequiredParamsValidate(new String[]{"org_id"}))
                .registKeepParams(new String[]{"org_id"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "org_chg")
                .registerValidate(new RequiredParamsValidate(new String[]{"org_id"}))
                .registKeepParams(new String[]{"org_id", "parent_id", "code", "province", "city", "address", "name", "extra_infos"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "org_setstatus")
                .registerValidate(new RequiredParamsValidate(new String[]{"org_id", "status"},
                        new WebConstantParamsValidate("statue", WebConstant.OrgDeptStatus.class)))
                .registKeepParams(new String[]{"org_id"}));
        /** 机构维护 end**/

        /** 部门维护 start **/
        optypes.add(new Optype(Optype.Mode.ADMIN, "dept_list")
                .registKeepParams(new String[]{"page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "dept_add")
                .registerValidate(new RequiredParamsValidate(new String[]{"name"},
                        new WebConstantParamsValidate("statue", WebConstant.OrgDeptStatus.class)))
                .registKeepParams(new String[]{"name", "desc", "status"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "dept_del")
                .registerValidate(new RequiredParamsValidate(new String[]{"dept_id"},
                        new WebConstantParamsValidate("statue", WebConstant.OrgDeptStatus.class)))
                .registKeepParams(new String[]{"dept_id"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "dept_chg")
                .registerValidate(new RequiredParamsValidate(new String[]{"dept_id"},
                        new WebConstantParamsValidate("statue", WebConstant.OrgDeptStatus.class)))
                .registKeepParams(new String[]{"dept_id", "name", "desc", "status"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "dept_setstatus")
                .registerValidate(new RequiredParamsValidate(new String[]{"dept_id", "status"},
                        new WebConstantParamsValidate("statue", WebConstant.OrgDeptStatus.class)))
                .registKeepParams(new String[]{"dept_id"}));
        /** 部门维护 end **/

        /** 币种维护 start **/
        optypes.add(new Optype(Optype.Mode.ADMIN, "currency_list")
                .registKeepParams(new String[]{"page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "currency_setdefault")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"},
                        new WebConstantParamsValidate("is_default", WebConstant.YesOrNo.class)))
                .registKeepParams(new String[]{"id"}));
        /** 币种维护 end **/

        /** 用户维护 start**/
        optypes.add(new Optype(Optype.Mode.ADMIN, "usr_list")
                .registKeepParams(new String[]{"page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "usr_add")
                .registerValidate(new RequiredParamsValidate(new String[]{"login_name"}, new UdopsValidate()))
                .registKeepParams(new String[]{"name", "phone", "email", "login_name", "register_date", "password",
                        "salt", "pcount", "pin", "pwd_last_change_date", "try_times", "status",
                        "is_admin", "is_boss", "persist_version", "is_have_extra",
                        "extra_infos", "udops"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "usr_del")
                .registerValidate(new RequiredParamsValidate(new String[]{"usr_id"}))
                .registKeepParams(new String[]{"usr_id"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "usr_chg")
                .registerValidate(new RequiredParamsValidate(new String[]{"usr_id"}, new UdopsValidate()))
                .registKeepParams(new String[]{"usr_id", "name", "phone", "email", "login_name", "register_date", "password",
                        "salt", "pcount", "pin", "pwd_last_change_date", "try_times", "status",
                        "is_admin", "is_boss", "persist_version", "is_have_extra",
                        "extra_infos", "udops"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "usr_detail")
                .registerValidate(new RequiredParamsValidate(new String[]{"usr_id"}))
                .registKeepParams(new String[]{"usr_id"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "usr_setstatus")
                .registerValidate(new RequiredParamsValidate(new String[]{"usr_id", "status"},
                        new WebConstantParamsValidate("statue", WebConstant.UserStatus.class)))
                .registKeepParams(new String[]{"usr_id"}));
        /** 用户维护 end**/

        /** 结算账户维护 start **/
        optypes.add(new Optype(Optype.Mode.ADMIN, "settacc_list")
                .registKeepParams(new String[]{"org_id", "bank_type", "query_key", "page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "settacc_add")
                .registerValidate(new RequiredParamsValidate(new String[]{"acc_no",
                        "acc_name", "org_id", "curr_id", "cnaps_code", "open_date", "org_seg", "detail_seg", "pay_recv_attr"}))
                .registKeepParams(new String[]{"acc_no", "acc_name", "org_id", "org_name", "curr_id", "curr_name",
                        "bank_name", "bank_type", "cnaps_code", "open_date", "org_seg",
                        "detail_seg", "pay_recv_attr", "status", "memo"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "settacc_del")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
                .registKeepParams(new String[]{"id"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "settacc_chg")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
                .registKeepParams(new String[]{"id", "acc_no", "acc_name", "org_id", "org_name", "curr_id", "curr_name",
                        "bank_name", "bank_type", "cnaps_code", "open_date", "org_seg",
                        "detail_seg", "pay_recv_attr", "status", "memo"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "settacc_setstatus")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
                .registKeepParams(new String[]{"id"}));
        /** 结算账户维护 end **/

        /** 商户号维护 start **/
        optypes.add(new Optype(Optype.Mode.ADMIN, "merchacc_list")
                .registKeepParams(new String[]{"org_id", "query_key", "channel_code", "page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "merchacc_add")
                .registerValidate(new RequiredParamsValidate(new String[]{"acc_no",
                        "acc_name", "org_id", "curr_id", "open_date", "org_seg", "detail_seg", "pay_recv_attr", "settle_acc_id"}))
                .registKeepParams(new String[]{"acc_no", "acc_name", "org_id", "org_name", "curr_id", "curr_name",
                        "channel_code", "channel_name", "open_date", "org_seg", "detail_seg",
                        "pay_recv_attr", "status", "settle_acc_id", "settle_acc_no", "memo"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "merchacc_del")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
                .registKeepParams(new String[]{"id"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "merchacc_chg")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
                .registKeepParams(new String[]{"id", "acc_no", "acc_name", "org_id", "org_name", "curr_id", "curr_name",
                        "channel_code", "channel_name", "open_date", "org_seg", "detail_seg",
                        "pay_recv_attr", "status", "settle_acc_id", "settle_acc_no", "memo"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "merchacc_setstatus")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
                .registKeepParams(new String[]{"id"}));
        /** 商户号维护 end **/


        /** 渠道维护 start **/
        optypes.add(new Optype(Optype.Mode.ADMIN, "handlechannel_list")
                .registKeepParams(new String[]{"query_key", "page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "handlechannel_add")
                .registerValidate(new RequiredParamsValidate(new String[]{"code", "name", "third_party_flag"}))
                .registKeepParams(new String[]{"code", "name", "third_party_flag", "is_activate", "memo"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "handlechannel_setstatus")
                .registerValidate(new RequiredParamsValidate(new String[]{"code"}))
                .registKeepParams(new String[]{"code"}));
        /** 渠道维护 end **/

        /** 路由维护 start **/
        optypes.add(new Optype(Optype.Mode.ADMIN, "handleroute_list")
                .registKeepParams(new String[]{"org_exp", "biz_type_exp", "insurance_type_exp",
                        "source_code", "pay_recv_mode", "pay_item",
                        "page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "handleroute_add")
                .registerValidate(new RequiredParamsValidate(new String[]{"source_code", "pay_recv_mode", "org_exp", "biz_type_exp", "insurance_type_exp"}))
                .registKeepParams(new String[]{"source_code", "pay_recv_mode", "pay_item", "is_activate",
                        "memo", "org_exp", "biz_type_exp", "insurance_type_exp", "items"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "handleroute_del")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
                .registKeepParams(new String[]{"id"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "handleroute_chg")
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "source_code", "pay_recv_mode", "org_exp", "biz_type_exp", "insurance_type_exp"}))
                .registKeepParams(new String[]{"id", "source_code", "pay_recv_mode", "pay_item", "is_activate",
                        "memo", "org_exp", "biz_type_exp", "insurance_type_exp", "items"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "handleroute_setstatus")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
                .registKeepParams(new String[]{"id"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "handleroute_detail")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
                .registKeepParams(new String[]{"id"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "handleroute_setormeracc")
                .registerValidate(new RequiredParamsValidate(new String[]{"code"}))
                .registKeepParams(new String[]{"code"}));
        /** 路由维护 end **/

        /** 用户组维护 start **/
        optypes.add(new Optype(Optype.Mode.ADMIN, "usrgroup_busmenu")
                .registKeepParams(new String[]{}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "usrgroup_list")
                .registKeepParams(new String[]{"group_id", "name", "memo", "is_builtin",
                        "page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "usrgroup_add")
                .registerValidate(new RequiredParamsValidate(new String[]{"name"}))
                .registKeepParams(new String[]{"name", "memo", "is_builtin", "menus"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "usrgroup_del")
                .registerValidate(new RequiredParamsValidate(new String[]{"group_id"}))
                .registKeepParams(new String[]{"group_id"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "usrgroup_chg")
                .registKeepParams(new String[]{"group_id", "name", "memo", "is_builtin", "menus"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "usrgroup_list2")
                .registKeepParams(new String[]{"page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "usrgroup_allot")
                .registKeepParams(new String[]{"group_id", "uodp_ids"}));
        /** 用户组维护 end **/

        /** 职位维护 end **/
        optypes.add(new Optype(Optype.Mode.ADMIN, "position_list")
                .registKeepParams(new String[]{"page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "position_add")
                .registKeepParams(new String[]{"name", "desc", "status"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "position_del")
                .registKeepParams(new String[]{"pos_id"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "position_chg")
                .registKeepParams(new String[]{"pos_id", "name", "desc", "status"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "position_setstatus")
                .registKeepParams(new String[]{"pos_id"}));

        /** 职位维护 end **/


        /** 用户菜单 start **/
        optypes.add(new Optype(Optype.Mode.ADMIN, "usrmenu_list")
                .registKeepParams(new String[]{"page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "usrmenu_allot")
                .registerValidate(new RequiredParamsValidate(new String[]{"uodp_id", "group_ids"}))
                .registKeepParams(new String[]{"uodp_id", "name", "group_ids"}));
        /** 用户菜单 end **/


        /** 业务类型自定义 start **/
        optypes.add(new Optype(Optype.Mode.ADMIN, "biztype_list"));

        optypes.add(new Optype(Optype.Mode.ADMIN, "biztype_add")
                .registerValidate(new RequiredParamsValidate(new String[]{"p_id", "biz_name"}))
                .registKeepParams(new String[]{"p_id", "biz_name", "memo"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "biztype_chg")
                .registerValidate(new RequiredParamsValidate(new String[]{"p_id", "biz_name", "biz_id"}))
                .registKeepParams(new String[]{"p_id", "biz_name", "memo", "biz_id"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "biztype_del")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_id"}))
                .registKeepParams(new String[]{"biz_id"}));

        optypes.add(new Optype(Optype.Mode.ADMIN, "biztype_setstatus")
                .registerValidate(new RequiredParamsValidate(new String[]{"biz_id"}))
                .registKeepParams(new String[]{"biz_id"}));


        /** 业务类型自定义 end **/
    }
}
