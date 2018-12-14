package com.qhjf.cfm.web;

import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

public class CommOptypeMgr extends AbstractOptypeMgr {

    @Override
    public void registe() {

        /** 地区,银行查询 start **/
        optypes.add(new Optype(Optype.Mode.COMM, "area_toplevel")
                .registKeepParams(new String[]{"name", "area_type", "pinyin", "jianpin"}));

        optypes.add(new Optype(Optype.Mode.COMM, "area_list")
                .registKeepParams(new String[]{"top_super", "query_key",
                        "name", "area_type", "pinyin", "jianpin",
                        "page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.COMM, "bank_typelist")
                .registKeepParams(new String[]{"query_key", "name", "pinyin", "jianpin",
                        "page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.COMM, "bank_list")
                .registKeepParams(new String[]{"query_key", "name", "pinyin", "jianpin",
                        "province", "city", "area_code", "bank_type",
                        "page_size", "page_num"}));
        /** 地区查询 end **/

        /** 用户查询 start **/
        optypes.add(new Optype(Optype.Mode.COMM, "user_list")
                .registKeepParams(new String[]{
                        "name"
                }));
        optypes.add(new Optype(Optype.Mode.COMM, "user_userinfo")
                .registKeepParams(new String[]{}));

        optypes.add(new Optype(Optype.Mode.COMM, "user_switchuodp")
                .registerValidate(new RequiredParamsValidate(new String[]{"uodp_id"}))
                .registKeepParams(new String[]{"uodp_id"}));

        optypes.add(new Optype(Optype.Mode.COMM, "user_chg")
                .registerValidate(new RequiredParamsValidate(new String[]{}))
                .registKeepParams(new String[]{"phone", "email"}));

        optypes.add(new Optype(Optype.Mode.COMM, "user_chgpwd")
                .registerValidate(new RequiredParamsValidate(new String[]{}))
                .registKeepParams(new String[]{"old_password", "password","confirm_password"}));
        /** 用户查询 end **/

        /** 文件start **/
        //上传
        optypes.add(new Optype(Optype.Mode.COMM, "attachment_upload"));
        //下载
//        optypes.add(new Optype(Optype.Mode.COMM, "attachment_download")
//                .registKeepParams(new String[]{"object_id"}));
        //获取对应列表
        optypes.add(new Optype(Optype.Mode.COMM, "attachment_list")
                .registKeepParams(new String[]{"bill_id", "biz_type"}));
        /** 文件end **/

        /** 码表 start */
        optypes.add(new Optype(Optype.Mode.COMM, "category_list")
                .registKeepParams(new String[]{
                        "code", "key", "query_key"
                }));
        optypes.add(new Optype(Optype.Mode.COMM, "category_listN")
                .registKeepParams(new String[]{
                        "code", "key", "query_key"
                }));
        /** 码表 end */

        /** 币种 start */
        optypes.add(new Optype(Optype.Mode.COMM, "currency_list")
                .registKeepParams(new String[]{"page_size", "page_num"}));
        /** 币种 end */

        /** 机构 start */
        optypes.add(new Optype(Optype.Mode.COMM, "org_list")
                .registKeepParams(new String[]{"page_size", "page_num"}));

        optypes.add(new Optype(Optype.Mode.COMM, "org_curlist"));
        /** 机构 end */

        /** 业务类型自定义 start **/
        optypes.add(new Optype(Optype.Mode.COMM, "biztype_biztypes")
                .registerValidate(new RequiredParamsValidate(new String[]{"p_id"}))
                .registKeepParams(new String[]{"p_id"}));

        /** 业务类型自定义 end **/

        optypes.add(new Optype(Optype.Mode.COMM, "account_normallist")
                .registKeepParams(new String[]{"exclude_ids", "interactive_mode"}));
    }
}
