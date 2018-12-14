package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.api.utils.LoadAtomicInterfaceUtils;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.web.constant.WebConstant;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 路由设置
 *
 * @auther zhangyuanyuan
 * @create 2018/5/28
 */

public class HandleRouteSettingService {


    /**
     * 获取路由设置列表
     *
     * @param record
     * @return
     */
    public Page<Record> list(int pageNum, int pageSize, final Record record) {
        SqlPara sqlPara = Db.getSqlPara("route.getHandleRouteSettingList", Ret.by("cond", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }


    /**
     * 添加路由设置
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public Record add(final Record record) throws BusinessException {
        record.remove("id");

        final List<Record> itemsRecordList = record.get("items");

        //修复判断逻辑
        if (itemsRecordList == null || itemsRecordList.isEmpty()) {
            throw new ReqDataException("至少选择一个渠道信息!");
        }

        if (TypeUtils.castToLong(record.get("pay_recv_mode")) == WebConstant.PayOrRecvMode.SMSK.getKey()) {//如果支付方式为扫描付款，则支付子项不允许为空
            if (TypeUtils.castToString(record.get("pay_item")) == null) {
                throw new ReqDataException("支付子项不能为空!");
            }
        }

        record.remove("items");

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                //保存路由设置信息
                boolean flag = Db.save("handle_route_setting", "id", record);
                if (flag) {

                    //保存路由设置渠道信息
                    for (Record itemre : itemsRecordList) {
                        itemre.remove("id");
                        itemre.remove("settle_or_merchant_acc_no");
                        itemre.remove("channel_interface_name");
                        itemre.set("route_id", TypeUtils.castToLong(record.get("id")));
                    }
                    int[] itemsArr = Db.batchSave("handle_route_setting_item", itemsRecordList, 1000);
                    if (ArrayUtil.checkDbResult(itemsArr)) {
                        return true;
                    }
                }
                return false;
            }
        });

        if (flag) {
            Record routeRec = Db.findById("handle_route_setting", "id", TypeUtils.castToLong(record.get("id")));

            String sql = Db.getSql("routeitem.getRouteItemList");
            List<Record> routeItmeRec = Db.find(sql, TypeUtils.castToLong(record.get("id")));
            routeRec.set("items", routeItmeRec);

            return routeRec;
        }

        throw new DbProcessException("添加路由设置信息失败!");

    }

    /**
     * 修改路由设置
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public Record chg(final Record record) throws BusinessException {
        final List<Record> itemsRecordList = record.get("items");

        if (itemsRecordList == null || itemsRecordList.isEmpty()) {
            throw new ReqDataException("至少选择一个渠道信息!");
        }

        if (TypeUtils.castToLong(record.get("pay_recv_mode")) == WebConstant.PayOrRecvMode.SMSK.getKey()) {//如果支付方式为扫描付款，则支付子项不允许为空
            if (TypeUtils.castToString(record.get("pay_item")) == null) {
                throw new ReqDataException("支付子项不能为空!");
            }
        }

        record.remove("items");

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                //保存路由设置信息
                boolean flag = Db.update("handle_route_setting", "id", record);
                if (flag) {
                    //物理删除路由设置子表信息
                    String delsql = Db.getSql("routeitem.deleteRouteItemByRouteId");
                    int result = Db.update(delsql, TypeUtils.castToLong(record.get("id")));
                    if (result > 0) {
                        //保存路由设置渠道信息
                        for (Record itemre : itemsRecordList) {
                            itemre.remove("id");
                            itemre.remove("settle_or_merchant_acc_no");
                            itemre.remove("channel_interface_name");
                            itemre.set("route_id", TypeUtils.castToLong(record.get("id")));
                        }
                        int[] itemsArr = Db.batchSave("handle_route_setting_item", itemsRecordList, 1000);
                        if (ArrayUtil.checkDbResult(itemsArr)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        });

        if (flag) {
            Record routeRec = Db.findById("handle_route_setting", "id", TypeUtils.castToLong(record.get("id")));

            String sql = Db.getSql("routeitem.getRouteItemList");
            List<Record> routeItmeRec = Db.find(sql, TypeUtils.castToLong(record.get("id")));
            routeRec.set("items", routeItmeRec);

            return routeRec;
        }
        throw new DbProcessException("修改路由设置信息失败!");
    }

    /**
     * 路由设置详情
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public Record detail(final Record record) throws BusinessException {
        final Long id = TypeUtils.castToLong(record.get("id"));
        Record routeRec = Db.findById("handle_route_setting", "id", id);
        if (routeRec == null) {
            throw new ReqDataException("未找到有效的路由设置信息!");
        }

        //根据路由设置id查询渠道项信息
        String sql = Db.getSql("routeitem.getRouteItemList");
        final List<Record> routeItmeRec = Db.find(sql, id);


        Record accRec = null;
        for (Record accrec : routeItmeRec) {

            Record channelRec = Db.findFirst(Db.getSql("channel.getChannelById"), TypeUtils.castToString(accrec.get("channel_code")));

            //通过第三方标识判断返回结算账户还是商户号信息
            if (TypeUtils.castToInt(channelRec.get("third_party_flag")) == WebConstant.YesOrNo.NO.getKey()) {

                //结算账户
                accRec = Db.findById("settle_account_info", "id", TypeUtils.castToLong(accrec.get("settle_or_merchant_acc_id")));
            } else {

                //商户号
                accRec = Db.findById("merchant_acc_info", "id", TypeUtils.castToLong(accrec.get("settle_or_merchant_acc_id")));
            }

            accrec.set("settle_or_merchant_acc_no", TypeUtils.castToString(accRec.get("acc_no")));

            LoadAtomicInterfaceUtils utils = LoadAtomicInterfaceUtils.getInstance();
            String channelCode = TypeUtils.castToString(accrec.get("channel_code"));
            String channelInterfaceCode = TypeUtils.castToString(accrec.get("channel_interface_code"));
            Map<String, AtomicInterfaceConfig> single = utils.getAllAtomicDesd4BankChannel(channelCode);
            accrec.set("channel_interface_name", single.get(channelInterfaceCode).getInterfaceDesc());
        }
        routeRec.set("items", routeItmeRec);


        return routeRec;
    }

    /**
     * 删除路由设置
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public void del(final Record record) throws BusinessException {
        final Long id = TypeUtils.castToLong(record.get("id"));
        Record routeRec = Db.findById("handle_route_setting", "id", id);
        if (routeRec == null) {
            throw new ReqDataException("未找到有效的路由设置信息!");
        }

        boolean flag = Db.tx(new IAtom() {

            @Override
            public boolean run() throws SQLException {

                //物理删除路由设置子表信息
                int result = Db.update(Db.getSql("routeitem.deleteRouteItemByRouteId"), id);
                if (result > 0) {
                    //物理删除路由设置主表信息
                    return Db.deleteById("handle_route_setting", "id", id);
                }
                return false;
            }
        });

        if (!flag) {
            throw new DbProcessException("删除路由设置信息失败!");
        }
    }

    /**
     * 修改路由设置状态
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public Record setstatus(final Record record) throws BusinessException {
        final Long id = TypeUtils.castToLong(record.get("id"));
        Record routeRec = Db.findById("handle_route_setting", "id", id);
        if (routeRec == null) {
            throw new ReqDataException("未找到有效的路由设置信息!");
        }

        Integer isActivate = TypeUtils.castToInt(routeRec.get("is_activate"));

        if (isActivate == WebConstant.YesOrNo.YES.getKey()) {
            record.set("is_activate", WebConstant.YesOrNo.NO.getKey());//否
        } else {
            record.set("is_activate", WebConstant.YesOrNo.YES.getKey());//否
        }

        boolean flag = Db.tx(new IAtom() {

            @Override
            public boolean run() throws SQLException {

                return Db.update("handle_route_setting", "id", record);
            }
        });

        if (flag) {
            return Db.findById("handle_route_setting", "id", id);
        }
        throw new DbProcessException("修改路由设置状态失败!");
    }

    public List<Record> setormeracc(final Record record) throws ReqDataException {
        //根据渠道CODE查找渠道信息
        Record channelRec = Db.findById("handle_channel_setting", "code", TypeUtils.castToString(record.get("code")));
        if (channelRec == null) {
            throw new ReqDataException("未找到有效的渠道信息");
        }

        record.remove("code");

        String accinfosql = "";
        SqlPara sqlPara = null;
        Kv cond = null;


        //通过第三方标识判断返回结算账户还是商户号信息
        if (TypeUtils.castToInt(channelRec.get("third_party_flag")) == WebConstant.YesOrNo.NO.getKey()) {

            //结算账户
            sqlPara = Db.getSqlPara("settle.settleList", Kv.by("cond", cond));
        } else {

            //商户号
            sqlPara = Db.getSqlPara("merchant.getMerchantList", Kv.by("cond", cond));
        }
        if (sqlPara != null) {

            return Db.find(sqlPara);
        }
        return null;
    }

}
