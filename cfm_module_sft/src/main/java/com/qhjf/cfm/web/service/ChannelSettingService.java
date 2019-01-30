package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.bankinterface.api.utils.LoadAtomicInterfaceUtils;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.constant.WebConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

/**
 * channel设置
 *
 * @author GJF
 * @date 2018年9月18日
 */
public class ChannelSettingService {

    private static Logger log = LoggerFactory.getLogger(ChannelSettingService.class);
    /**
     * 通道设置 - 查看分页列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> channellist(int pageNum, int pageSize, final Record record) {
        if(record.get("pay_mode")!=null && !"".equals(TypeUtils.castToString(record.get("pay_mode")))){
            record.set("pay_mode", WebConstant.SftPayMode.getSftPayModeByKey(
                    TypeUtils.castToInt(record.get("pay_mode"))).getKey());
        }
        SqlPara sqlPara = Db.getSqlPara("channel_setting.channellist", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 新增一个通道
     * @param record
     * @param userInfo
     * @return
     * @throws BusinessException
     */
    public Record add(final Record record, final UserInfo userInfo) throws BusinessException {
        int interactiveMode = TypeUtils.castToInt(record.get("interactive_mode"));
        String channel_code = TypeUtils.castToString(record.get("channel_code"));
        int pay_attr = TypeUtils.castToInt(record.get("pay_attr"));
        if(pay_attr == WebConstant.SftPayAttr.PAY.getKey()){
            if(StringUtils.isEmpty(record.getStr("pay_attr"))){
                throw new ReqDataException("付方向结算模式不能为空!");
            }
        }
        if(StringUtils.isEmpty(record.getStr("charge_amount"))){
            record.set("charge_amount", 0);
        }
        if(StringUtils.isEmpty(record.getStr("single_amount_limit"))){
            record.set("single_amount_limit", 0);
        }
        //当前通道编码是否存在
        Record code = Db.findById("channel_setting", "channel_code", channel_code);
        if(code != null){
            throw new ReqDataException("该通道已经存在!");
        }
        //报盘的 报盘模板不能为空，直联的 直联通道不能为空
        if(interactiveMode == 0){
            if(StringUtils.isEmpty(record.getStr("direct_channel"))){
                throw new ReqDataException("直联通道不能为空!");
            }
        }else if(interactiveMode == 1){
            if(StringUtils.isEmpty(record.getStr("document_moudle"))){
                throw new ReqDataException("报盘模板不能为空!");
            }
        }else{
            throw new ReqDataException("交互方式不支持!");
        }
        String bankcode = TypeUtils.castToString(record.get("bankcode"));
        Record account = Db.findFirst(Db.getSql("channel_setting.getAccByBankCode"), bankcode);
        if(account == null){
            throw new ReqDataException("该账户不存在!");
        }else{
            record.set("acc_id", account.get("acc_id"));
            record.set("acc_no", account.get("acc_no"));
            record.set("acc_name", account.get("acc_name"));
            record.set("bank_name", account.get("bank_name"));
            record.set("is_inner", 1);
        }
        if(StringUtils.isNotEmpty(record.getStr("op_acc_no"))){
            if(StringUtils.isEmpty(record.getStr("op_acc_name")) || StringUtils.isEmpty(record.getStr("op_bank_name"))){
                throw new ReqDataException("请补充完整通道指定的调拨/支付账号!");
            }else{
                /**
                 * 判断是第三方付款，还是内部调拨的账号
                 * 判断总批次表里的付款房信息能否在账户表中找到，找到说明是内部调拨账号，没有说明是第三方
                 */
                Record paybean = Db.findById("account", "acc_no,acc_name",
                        TypeUtils.castToString(record.getStr("op_acc_no")),
                        TypeUtils.castToString(record.getStr("op_acc_name")));
                if(paybean == null){
                    record.set("is_inner", 0);
                }else{
                    record.set("is_inner", 1);
                }

            }
        }



        record.set("create_by", userInfo.getUsr_id());
        record.set("create_on", new Date());
        record.set("update_by", userInfo.getUsr_id());
        record.set("update_on", new Date());
        record.set("persist_version", 0);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.save("channel_setting", record);
            }
        });
        if (flag) {
            return Db.findFirst(Db.getSql("channel_setting.getchannelbyid"), record.get("id"));
        }

        throw new DbProcessException("保存通道失败!");
    }

    /**
     * 修改一个通道
     * @param record
     * @param userInfo
     * @return
     * @throws BusinessException
     */
    public Record chgchannel(final Record record, final UserInfo userInfo) throws BusinessException {

        final long id = TypeUtils.castToLong(record.get("id"));
        // 根据单据id查询通道信息
        Record channelRec = Db.findById("channel_setting", "id", TypeUtils.castToLong(record.get("id")));
        if (channelRec == null) {
            throw new ReqDataException("未找到有效的通道信息!");
        }
        int interactiveMode = TypeUtils.castToInt(record.get("interactive_mode"));
        //报盘的 报盘模板不能为空，直联的 直联通道不能为空
        if(interactiveMode == 0){
            if(StringUtils.isEmpty(record.getStr("direct_channel"))){
                throw new ReqDataException("直联通道不能为空!");
            }
        }else if(interactiveMode == 1){
            if(StringUtils.isEmpty(record.getStr("document_moudle"))){
                throw new ReqDataException("报盘模板不能为空!");
            }
        }else{
            throw new ReqDataException("交互方式不支持!");
        }

        String bankcode = TypeUtils.castToString(record.get("bankcode"));
        Record account = Db.findFirst(Db.getSql("channel_setting.getAccByBankCode"), bankcode);
        if(account == null){
            throw new ReqDataException("该账户不存在!");
        }else{
            record.set("acc_id", account.get("acc_id"));
            record.set("acc_no", account.get("acc_no"));
            record.set("acc_name", account.get("acc_name"));
            record.set("bank_name", account.get("bank_name"));
            record.set("is_inner", 1);
        }
        if(StringUtils.isNotEmpty(record.getStr("op_acc_no"))){
            if(StringUtils.isEmpty(record.getStr("op_acc_name")) || StringUtils.isEmpty(record.getStr("op_bank_name"))){
                throw new ReqDataException("请补充完整通道指定的调拨/支付账号!");
            }else{
                /**
                 * 判断是第三方付款，还是内部调拨的账号
                 * 判断总批次表里的付款房信息能否在账户表中找到，找到说明是内部调拨账号，没有说明是第三方
                 */
                Record paybean = Db.findById("account", "acc_no,acc_name",
                        TypeUtils.castToString(record.getStr("op_acc_no")),
                        TypeUtils.castToString(record.getStr("op_acc_name")));
                if(paybean == null){
                    record.set("is_inner", 0);
                }else{
                    record.set("is_inner", 1);
                }

            }
        }

        record.set("update_by", userInfo.getUsr_id());
        record.set("update_on", new Date());
        record.remove("id");
        record.remove("channel_code");

        final int old_version = TypeUtils.castToInt(record.get("persist_version"));
        record.set("persist_version", old_version + 1);


        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return CommonService.update("channel_setting",
                        record,
                        new Record().set("id", id).set("persist_version", old_version));
            }
        });
        if (flag) {
            return Db.findFirst(Db.getSql("channel_setting.getchannelbyid"), id);
        }
        throw new DbProcessException("更新通道失败!");
    }

    /**
     * 查看通道详情
     *
     * @param record
     * @return
     * @throws ReqDataException
     */
    public Record channeldetail(Record record) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));
        // 根据通道id查询通道信息
        Record channelRec = Db.findById("channel_setting", "id", id);
        if (channelRec == null) {
            throw new ReqDataException("未找到有效的通道!");
        }
        return channelRec;
    }

    /**
     * 获取直联通道
     *
     * @param record
     * @return
     * @throws ReqDataException
     */
    public Map<String, String> getdirectchannel(Record record) throws BusinessException {
        return ChannelManager.getBankMap();
    }

    /**
     * 获取所有通道
     *
     * @param record
     * @return
     * @throws ReqDataException
     */
    public List<Record> getallchannel(Record record) throws BusinessException {
        List<Record> records = Db.find(Db.getSql("channel_setting.getAllChannel"));
        return records;
    }

    /**
     * 获取所有bankcode
     *
     */
    public List<Record> getallbankcode() {
        List<Record> records = Db.find(Db.getSql("channel_setting.getAllBankcode"));
        return records;
    }

    /**
     * 根据收付属性获取报盘模板
     */
    public List<Record> getdoucument(Record record) throws BusinessException  {

        long payAttr = TypeUtils.castToLong(record.get("pay_attr"));
        int documentType;
        if(payAttr == 0){
            documentType = 3;
        }else if(payAttr == 1){
            documentType = 1;
        }else{
            throw new ReqDataException("未找到有效的通道!");
        }
        // 根据通道id查询通道信息
        List<Record> records = Db.find(Db.getSql("channel_setting.getdoucument"),documentType);
        return records;
    }

}
