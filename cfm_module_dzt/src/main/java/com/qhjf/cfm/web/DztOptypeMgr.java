package com.qhjf.cfm.web;

import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/9/25
 * @Description: 对账通
 */
public class DztOptypeMgr extends AbstractOptypeMgr {
    //controller
    public static final String DZTINIT = "dztinit";
    //方法
    public static final String ADD = "_add";
    public static final String CHG = "_chg";
    public static final String LIST = "_list";
    public static final String DETAIL = "_detail";
    public static final String ENABLE = "_enable";

    public static String getKey(String clazz, String method) {
        return clazz.concat(method);
    }

    @Override
    public void registe() {
        /** ============================ 期初余额初始化 begin ============================ */
        //新增
        optypes.add(new Optype(Optype.Mode.NORMAL, getKey(DZTINIT, ADD))
                .registerValidate(new RequiredParamsValidate(new String[]{"acc_id", "year", "month", "balance"}))
                .registKeepParams(new String[]{"acc_id", "year", "month", "balance", "list"}));
        //修改
        optypes.add(new Optype(Optype.Mode.NORMAL, getKey(DZTINIT, CHG))
                .registerValidate(new RequiredParamsValidate(new String[]{"acc_id", "year", "month", "balance"}))
                .registKeepParams(new String[]{"acc_id", "year", "month", "balance", "list"}));
        //启用
        optypes.add(new Optype(Optype.Mode.NORMAL, getKey(DZTINIT, ENABLE))
                .registerValidate(new RequiredParamsValidate(new String[]{"acc_id", "year", "month", "balance"}))
                .registKeepParams(new String[]{"acc_id", "year", "month", "balance", "list"}));
        //期初详情
        optypes.add(new Optype(Optype.Mode.NORMAL, getKey(DZTINIT, DETAIL))
                .registerValidate(new RequiredParamsValidate(new String[]{"acc_id", "year", "month"}))
                .registKeepParams(new String[]{"acc_id", "year", "month"}));
        //期初分页列表
        optypes.add(new Optype(Optype.Mode.NORMAL, getKey(DZTINIT, LIST))
                .registerValidate(new RequiredParamsValidate(new String[]{"page_size", "page_num"}))
                .registKeepParams(new String[]{"page_size", "page_num"}));
        /** ============================ 期初余额初始化 begin ============================ */
        /** ============================ 对账通交易核对 begin ============================ */

        //单据未核对查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "dztcheck_billList")
                .registKeepParams(new String[]{"query_key",
                        "min", "max", "page_size", "page_num", "credit_or_debit"}));

        //勾选 查找交易流水
        optypes.add(new Optype(Optype.Mode.NORMAL, "dztcheck_tradingList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "acc_no", "credit_or_debit", "amount", "year", "month"
                }))
                .registKeepParams(new String[]{"acc_no", "credit_or_debit", "amount", "year", "month"}));

        //确认交易
        optypes.add(new Optype(Optype.Mode.NORMAL, "dztcheck_confirm")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_no", "trading_no"
                }))
                .registKeepParams(new String[]{"bill_no", "trading_no"}));

        //单据已核对查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "dztcheck_confirmbillList")
                .registKeepParams(new String[]{"query_key",
                        "min", "max", "page_size", "page_num", "credit_or_debit"}));
        //单据已核对交易查询
        optypes.add(new Optype(Optype.Mode.NORMAL, "dztcheck_confirmTradingList")
                .registerValidate(new RequiredParamsValidate(new String[]{
                        "bill_no"
                }))
                .registKeepParams(new String[]{"bill_no"}));
        /** ============================ 对账通交易核对 end ============================ */
        //余额调节表列表
        optypes.add(new Optype(Optype.Mode.NORMAL, "dztadjust_list")
                .registKeepParams(new String[]{"year", "month", "query_key", "page_size", "page_num"}));

        //查询余额调节表
        optypes.add(new Optype(Optype.Mode.NORMAL, "dztadjust_build")
                .registerValidate(new RequiredParamsValidate(new String[]{"cdate", "acc_id"}))
                .registKeepParams(new String[]{"cdate", "acc_id"}));

        //余额调节表详情
        optypes.add(new Optype(Optype.Mode.NORMAL, "dztadjust_detail")
                .registerValidate(new RequiredParamsValidate(new String[]{"id"}))
                .registKeepParams(new String[]{"id"}));

        //余额调节表启用
        optypes.add(new Optype(Optype.Mode.NORMAL, "dztadjust_confirm")
                .registerValidate(new RequiredParamsValidate(new String[]{"id", "persist_version", "cdate"}))
                .registKeepParams(new String[]{"id", "persist_version", "cdate"}));
    }
}
