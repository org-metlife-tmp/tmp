package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.web.constant.WebConstant;

import java.sql.SQLException;
import java.util.List;

/**
 * 账户管理 - 商户号管理
 *
 * @auther zhangyuanyuan
 * @create 2018/5/24
 */

public class MerchantAccInfoService {

    /**
     * 获取商户号列表
     *
     * @param record
     * @return
     */
    public Page<Record> list(int pageNum,int pageSize,final Record record) {
        String querykey = TypeUtils.castToString(record.get("query_key"));

        if (querykey != null && !"".equals(querykey)) {
            //判断是否包含中文
            if (StringKit.isContainChina(querykey)) {
                record.set("acc_name", querykey);
            } else {
                record.set("acc_no", querykey);
            }
        }

        record.remove("query_key");

        SqlPara sqlPara = Db.getSqlPara("merchant.getMerchantList", Kv.by("cond", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);

    }

    /**
     * 查看商户信息明细
     *
     * @param record
     * @return
     */
    public Record detail(final Record record) throws BusinessException {
        Long id = TypeUtils.castToLong(record.get("id"));

        Record MerRec = getMerchantAccInfoById(id);
        if (MerRec == null) {
            throw new ReqDataException("未找到有效的商户号信息!");
        }
        return MerRec;
    }

    /**
     * 添加商户信息
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public Record add(final Record record) throws BusinessException {
        record.remove("id");
        validateChannelAndSettle(record);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                boolean flag = Db.save("merchant_acc_info", "id", record);
                if (flag) {
                    return true;
                }
                return false;
            }
        });

        if (flag) {
            return getMerchantAccInfoById(TypeUtils.castToLong(record.get("id")));
        }

        throw new DbProcessException("添加商户号信息失败!");
    }

    /**
     * 修改商户信息
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public Record chg(final Record record) throws BusinessException {

        Record merRec = getMerchantAccInfoById(TypeUtils.castToLong(record.get("id")));
        if(merRec == null){
            throw new ReqDataException("未找到有效的商户号信息!");
        }

        validateChannelAndSettle(record);

        boolean flag = updateMerchantAccInfo(record);

        if (flag) {
            return getMerchantAccInfoById(TypeUtils.castToLong(record.get("id")));
        }

        throw new DbProcessException("修改商户号信息失败!");
    }

    /**
     * 删除商户信息
     * 逻辑删除，修改status = 3 并且acc_no 修改为： 账户编号_账户ID
     *
     * @return
     */
    public void del(final Record record) throws BusinessException {
        //根据商户ID查询商户信息
        Record merRec = getMerchantAccInfoById(TypeUtils.castToLong(record.get("id")));
        if (merRec == null) {
            throw new ReqDataException("未找到有效的商户号信息!");
        }

        record.set("acc_no", TypeUtils.castToString(merRec.get("acc_no") + "_" + TypeUtils.castToInt(merRec.get("id"))));
        record.set("status", WebConstant.SetAccAndMerchStatus.DELETE.getKey());

        boolean flag = updateMerchantAccInfo(record);

        if (!flag) {
            throw new DbProcessException("删除商户号信息失败!");
        }
    }

    public Record setstatus(final Record record) throws BusinessException {
        Long id = TypeUtils.castToLong(record.get("id"));

        //根据商户ID查询
        Record merRec = getMerchantAccInfoById(id);
        if (merRec == null) {
            throw new ReqDataException("未找到有效的商户号信息!");
        }

        Integer status = TypeUtils.castToInt(merRec.get("status"));

        if(status == WebConstant.SetAccAndMerchStatus.NORMAL.getKey()){//正常
            record.set("status",WebConstant.SetAccAndMerchStatus.FROZEN.getKey());//禁用
        }else{
            record.set("status",WebConstant.SetAccAndMerchStatus.NORMAL.getKey());
        }

        boolean flag = updateMerchantAccInfo(record);

        if (!flag) {
            throw new DbProcessException("修改商户号状态失败!");
        }

        return getMerchantAccInfoById(id);

    }

    /**
     * 根据商户ID查询商户信息（状态不为 3：删除）
     *
     * @param id
     * @return
     */
    public Record getMerchantAccInfoById(Long id) {
        String sql = Db.getSql("merchant.getMerchantAccInfoById");
        return Db.findFirst(sql, id, WebConstant.SetAccAndMerchStatus.DELETE.getKey());
    }

    /**
     * 验证渠道、结算账户信息
     *
     * @param record
     * @throws ReqDataException
     */
    public void validateChannelAndSettle(final Record record) throws ReqDataException {
        //判断该商户号是否存在
        String mersql = Db.getSql("merchant.getMerchantAccInfoByAccNo");
        Record merRec = Db.findFirst(mersql,TypeUtils.castToString(record.get("acc_no")));
        if(merRec != null ){//帐号已存在
            if(TypeUtils.castToLong(record.get("id")) == null) {
                throw new ReqDataException("商户号编号已存在!");
            }
        }

        //根据渠道编号查询渠道信息
        String channelsql = Db.getSql("channel.getChannelById");
        Record channelRec = Db.findFirst(channelsql, TypeUtils.castToString(record.get("channel_code")));
        if (channelRec == null) {
            throw new ReqDataException("未找到有效的支付渠道!");
        }
        record.set("channel_name", TypeUtils.castToString(channelRec.get("name")));

        //结算账户ID查询结算账户信息
        String settlesql = Db.getSql("settle.getSettleById");
        Record settleRec = Db.findFirst(settlesql, TypeUtils.castToString(record.get("settle_acc_id")));
        if (settleRec == null) {
            throw new ReqDataException("未找到有效的结算帐号!");
        }
        record.set("settle_acc_no", TypeUtils.castToString(settleRec.get("acc_name")));

        //根据机构ID查询机构信息
        SqlPara sqlPara = Db.getSqlPara("org.findOrgInfoById", TypeUtils.castToLong(record.get("org_id")));
        List<Record> orgRecList = Db.find(sqlPara);
        if (orgRecList == null) {
            throw new ReqDataException("未找到有效的机构信息!");
        }
        record.set("org_name", TypeUtils.castToString(orgRecList.get(0).get("name")));

        //根据币种ID查询币种信息
        String currsql = Db.getSql("currency.findCurrencyById");
        Record currRec = Db.findFirst(currsql, TypeUtils.castToLong(record.get("curr_id")));
        if (currRec == null) {
            throw new ReqDataException("未找到有效的币种信息!");
        }
        record.set("curr_name", TypeUtils.castToString(currRec.get("name")));
    }

    public boolean updateMerchantAccInfo(final Record record) {
        return Db.tx(new IAtom() {

            @Override
            public boolean run() throws SQLException {

                boolean flag = Db.update("merchant_acc_info", "id", record);
                if (flag) {
                    return true;
                }
                return false;
            }
        });
    }
}
