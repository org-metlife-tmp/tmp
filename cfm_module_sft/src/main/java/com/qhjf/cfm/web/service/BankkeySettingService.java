package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 通道设置
 *
 * @author GJF
 * @date 2018年9月18日
 */
public class BankkeySettingService {
    private static Logger log = LoggerFactory.getLogger(ChannelSettingService.class);
    /**
     * bankkey设置 - 查看分页列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> bankkeylist(int pageNum, int pageSize, final Record record) {
        SqlPara sqlPara = Db.getSqlPara("bankkey_setting.bankkeylist", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 新增一个bankkey
     * @param record
     * @param userInfo
     * @return
     * @throws BusinessException
     */
    public Record addbankkey(final Record record, final UserInfo userInfo) throws BusinessException {
        long channelId = TypeUtils.castToLong(record.get("channel_id"));
        Record cahnnelRec = Db.findById("channel_setting", "id", channelId);
        String bankkey = TypeUtils.castToString(record.get("bankkey"));
        String payMode = TypeUtils.castToString(record.get("pay_mode"));
        if (cahnnelRec == null) {
            throw new ReqDataException("未找到有效的通道!");
        }

        long orgId = TypeUtils.castToLong(record.get("org_id"));
        Record orgRec = Db.findById("organization", "org_id", orgId);
        if (orgRec == null) {
            throw new ReqDataException("未找到有效的机构!");
        }

        //当前通道编码是否存在
        Record code = Db.findById("bankkey_setting", "bankkey,org_id,pay_mode", bankkey, orgId, payMode);
        if(code != null){
            throw new ReqDataException("该BANKKEY已经存在!");
        }

        record.set("create_by", userInfo.getUsr_id());
        record.set("create_on", new Date());
        record.set("update_by", userInfo.getUsr_id());
        record.set("update_on", new Date());
        record.set("persist_version", 0);


        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.save("bankkey_setting", record);
            }
        });
        if (flag) {
            return Db.findFirst(Db.getSql("bankkey_setting.getbankkeybyid"), record.get("id"));
        }

        throw new DbProcessException("保存bankkey失败!");
    }

    /**
     * 修改一个bankkey
     * @param record
     * @param userInfo
     * @return
     * @throws BusinessException
     */
    public Record chgbankkey(final Record record, final UserInfo userInfo) throws BusinessException {

        final long id = TypeUtils.castToLong(record.get("id"));
        // 根据单据id查询通道信息
        Record bankkeyRec = Db.findById("bankkey_setting", "id", TypeUtils.castToLong(record.get("id")));
        if (bankkeyRec == null) {
            throw new ReqDataException("未找到有效的bankkey信息!");
        }

        long channelId = TypeUtils.castToLong(record.get("channel_id"));
        Record cahnnelRec = Db.findById("channel_setting", "id", channelId);
        if (cahnnelRec == null) {
            throw new ReqDataException("未找到有效的通道!");
        }
        long orgId = TypeUtils.castToLong(record.get("org_id"));
        Record orgRec = Db.findById("organization", "org_id", orgId);
        if (orgRec == null) {
            throw new ReqDataException("未找到有效的机构!");
        }

        record.set("update_by", userInfo.getUsr_id());
        record.set("update_on", new Date());
        record.remove("id");

        final int old_version = TypeUtils.castToInt(record.get("persist_version"));
        record.set("persist_version", old_version + 1);


        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return CommonService.update("bankkey_setting",
                        record,
                        new Record().set("id", id).set("persist_version", old_version));
            }
        });
        if (flag) {
            return Db.findFirst(Db.getSql("bankkey_setting.getbankkeybyid"), id);
        }
        throw new DbProcessException("更新bankkey失败!");
    }

    /**
     * 查看bankkey详情
     *
     * @param record
     * @return
     * @throws ReqDataException
     */
    public Record bankkeydetail(Record record) throws BusinessException {
        long id = TypeUtils.castToLong(record.get("id"));
        // 根据通道id查询bankkey信息
        Record bankkeyRec = Db.findById("bankkey_setting", "id", id);
        if (bankkeyRec == null) {
            throw new ReqDataException("未找到有效的bankkey!");
        }
        return bankkeyRec;
    }

    /**
     * 获取所有机构 机构
     *
     * @param record
     * @return
     * @throws ReqDataException
     */
    public List<Record> getorg(Record record) throws BusinessException {
        List<Record> records = Db.find(Db.getSql("bankkey_setting.getorg"));
        return records;
    }

    /**
     * 获取通道根据收付属性
     */
    public List<Record> getchanbypaymode(Record record) throws BusinessException  {

        int payMode = TypeUtils.castToInt(record.get("pay_mode"));
        // 获取通道根据收付属性
        List<Record> records = Db.find(Db.getSql("channel_setting.getchanbypaymode"),payMode);
        return records;
    }

}
