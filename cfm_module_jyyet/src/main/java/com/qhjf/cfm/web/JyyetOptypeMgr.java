package com.qhjf.cfm.web;

import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

public class JyyetOptypeMgr extends AbstractOptypeMgr {
    @Override
    public void registe() {
        /** 余额通start **/
        //当日余额明细列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "yet_curdetaillist")
                .registKeepParams(new String[]{
                        "page_num", "page_size", "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes", "bank_type", "acc_no"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "yet_currwavelistexport")
                .registKeepParams(new String[]{
                        "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes", "bank_type", "acc_no"
                }));

        optypes.add(new Optype(Optype.Mode.NORMAL, "yet_currtransexport")
                .registKeepParams(new String[]{
                        "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes", "bank_type", "acc_no"
                }));
        //当日余额汇总列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "yet_curcollectlist")
                .registKeepParams(new String[]{
                        "page_num", "page_size", "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes", "type", "bank_type", "acc_no"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "yet_curcollectlistorgexport")
                .registKeepParams(new String[]{
                        "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes", "bank_type", "acc_no"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "yet_curcollectlistbankexport")
                .registKeepParams(new String[]{
                        "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes", "bank_type", "acc_no"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "yet_curwavetopchart")
                .registKeepParams(new String[]{
                        "acc_id"
                }));
        //历史余额明细列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "yet_hisdetaillist")
                .registKeepParams(new String[]{
                        "page_num", "page_size", "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes",
                        "start_date", "end_date", "bank_type", "acc_no"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "yet_histransexport")
                .registKeepParams(new String[]{
                        "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes",
                        "start_date", "end_date", "bank_type", "acc_no"
                }));
        //历史余额汇总列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "yet_hiscollectlist")
                .registKeepParams(new String[]{
                        "page_num", "page_size", "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes",
                        "start_date", "end_date", "type", "bank_type", "acc_no"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "yet_hiscollectlistbankexport")
                .registKeepParams(new String[]{
                        "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes",
                        "start_date", "end_date", "bank_type", "acc_no"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "yet_hiscollectlistorgexport")
                .registKeepParams(new String[]{
                        "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes",
                        "start_date", "end_date", "bank_type", "acc_no"
                }));
        //历史余额波动列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "yet_hiswavelist")
                .registKeepParams(new String[]{
                        "page_num", "page_size", "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes",
                        "start_date", "end_date", "bank_type", "acc_no"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "yet_hiswavelistexport")
                .registKeepParams(new String[]{
                        "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes",
                        "start_date", "end_date", "bank_type", "acc_no"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "yet_hiswavetopchart")
                .registKeepParams(new String[]{
                        "acc_id", "start_date", "end_date"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "acc_id", "start_date", "end_date"
                })));
        //余额通导入
        optypes.add(new Optype(Optype.Mode.NORMAL, "yet_import"));
        /** 余额通end **/

        /** 交易通start **/
        //当日交易明细列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "jyt_curdetaillist")
                .registKeepParams(new String[]{
                        "page_num", "page_size", "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes", "acc_no", "bank_type"
                }));
        //当日交易汇总列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "jyt_curcollectlist")
                .registKeepParams(new String[]{
                        "page_num", "page_size", "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes", "type"
                }));

        //当日交易汇总列表导出 - 账户
        optypes.add(new Optype(Optype.Mode.NORMAL, "jyt_curcollectlistaccexport")
                .registKeepParams(new String[]{
                        "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes", "org_id"
                }));
        //当日交易汇总列表导出 - 公司
        optypes.add(new Optype(Optype.Mode.NORMAL, "jyt_curcollectlistorgexport")
                .registKeepParams(new String[]{
                        "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes", "org_id"
                }));
        //当日交易汇总列表导出 - 银行
        optypes.add(new Optype(Optype.Mode.NORMAL, "jyt_curcollectlistbankexport")
                .registKeepParams(new String[]{
                        "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes", "org_id"
                }));

        //当日交易明细列表导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "jyt_currtransexport")
                .registKeepParams(new String[]{
                        "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes", "bank_type", "acc_no", "org_id"
                }));

        //历史交易明细列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "jyt_hisdetaillist")
                .registKeepParams(new String[]{
                        "page_num", "page_size", "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes", "acc_no", "bank_type",
                        "start_date", "end_date"
                }));

        //历史交易汇总列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "jyt_hiscollectlist")
                .registKeepParams(new String[]{
                        "page_num", "page_size", "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes",
                        "start_date", "end_date", "type"
                }));

        //历史交易汇总列表导出 - 账户
        optypes.add(new Optype(Optype.Mode.NORMAL, "jyt_hiscollectlistaccexport")
                .registKeepParams(new String[]{
                        "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes",
                        "start_date", "end_date", "org_id"
                }));
        //历史交易汇总列表导出 - 公司
        optypes.add(new Optype(Optype.Mode.NORMAL, "jyt_hiscollectlistorgexport")
                .registKeepParams(new String[]{
                        "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes",
                        "start_date", "end_date", "org_id"
                }));
        //历史交易汇总列表导出 - 银行
        optypes.add(new Optype(Optype.Mode.NORMAL, "jyt_hiscollectlistbankexport")
                .registKeepParams(new String[]{
                        "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes",
                        "start_date", "end_date", "org_id"
                }));

        //历史交易明细列表导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "jyt_histransexport")
                .registKeepParams(new String[]{
                        "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes", "acc_no", "bank_type",
                        "start_date", "end_date", "org_id"
                }));

        //历史交易波动列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "jyt_hiswavelist")
                .registKeepParams(new String[]{
                        "page_num", "page_size", "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes",
                        "start_date", "end_date", "acc_no"
                }));
        optypes.add(new Optype(Optype.Mode.NORMAL, "jyt_hiswavetopchart")
                .registKeepParams(new String[]{
                        "acc_id", "start_date", "end_date", "acc_no"
                })
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "acc_id", "start_date", "end_date"
                })));

        //历史交易波动列表导出
        optypes.add(new Optype(Optype.Mode.NORMAL, "jyt_hiswavelistexport")
                .registKeepParams(new String[]{
                        "org_ids", "cnaps_codes", "acc_attrs", "interactive_modes",
                        "start_date", "end_date", "org_id"
                }));

        //交易数据导入
        optypes.add(new Optype(Optype.Mode.NORMAL, "jyt_import")
                .registerValidate(new RequiredParamsValidate(new String[]{""}))
                .registKeepParams(new String[]{""}));
        /** 交易通end **/
    }
}
