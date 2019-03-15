package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 可疑数据管理LA
 *
 * @author GJF
 */
public class RecvDoubtfulService {
    private static Logger log = LoggerFactory.getLogger(RecvDoubtfulService.class);
    /**
     * 可疑数据 - 查看分页列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> doubtfullist(int pageNum, int pageSize, final Record record, UodpInfo uodpInfo) throws BusinessException {
        long osSource = TypeUtils.castToLong(record.get("os_source"));
        if(record.get("pay_mode")!=null && !"".equals(TypeUtils.castToString(record.get("pay_mode")))){
            record.set("pay_mode", WebConstant.SftDoubtRecvMode.getSftDoubtRecvModeByKey(
                    TypeUtils.castToInt(record.get("pay_mode"))).getKeyc());
        }
        SqlPara sqlPara = null;
        record.remove("os_source");
        Long org_id = uodpInfo.getOrg_id();
        Record findById = Db.findById("organization", "org_id", org_id);
        if(null == findById){
            throw new ReqDataException("当前登录人的机构信息未维护");
        }

        List<String> codes = new ArrayList<>();
        if(findById.getInt("level_num") == 1){
            log.info("========目前登录机构为总公司");
            codes = Arrays.asList("0102","0101","0201","0202","0203","0204","0205","0500");
        }else{
            log.info("========目前登录机构为分公司公司");
            List<Record> rec = Db.find(Db.getSql("org.getCurrentUserOrgs"), org_id);
            for (Record o : rec) {
                codes.add(o.getStr("code"));
            }
        }
        record.set("codes", codes);

        if(WebConstant.SftOsSource.LA.getKey() == osSource){
            //LA
            sqlPara = Db.getSqlPara("recvdoubtful.doubtfulLalist", Kv.by("map", record.getColumns()));
        }else{
            throw new DbProcessException("未找到对应的系统来源!");
        }

        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 拒绝打回
     * @param record
     * @return
     * @throws BusinessException
     */
    public void reject(final Record record, final UserInfo userInfo) throws BusinessException {
        int osSource = TypeUtils.castToInt(record.get("os_source"));
        final long id = TypeUtils.castToLong(record.get("id"));
        final int doubtfulVersion = TypeUtils.castToInt(record.get("persist_version"));

        final String doubtfulTable,originTable;
        if(WebConstant.SftOsSource.LA.getKey() == osSource){
            originTable = "la_origin_recv_data";
            doubtfulTable = "la_recv_check_doubtful";
        }else{
            throw new ReqDataException("未找到对应的系统来源!");
        }
        Record doubtfulRecord = Db.findById(doubtfulTable, "id", id);
        if (doubtfulRecord == null) {
            throw new ReqDataException("未找到有效的可疑数据!");
        }
        long doubtfulStatus = TypeUtils.castToLong(doubtfulRecord.get("status"));
        if(doubtfulStatus == 1){
            throw new ReqDataException("该数据已处理！");
        }

        //获取原始数据id
        final long originId = TypeUtils.castToLong(doubtfulRecord.get("origin_id"));
        Record originRecord = Db.findById(originTable, "id", originId);
        if (originRecord == null) {
            throw new ReqDataException("未找到有效的原始数据!");
        }
        final int originVersion = TypeUtils.castToInt(originRecord.get("persist_version"));
        final String errorMsg = TypeUtils.castToString(record.get("op_reason"));

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean b = CommonService.update(doubtfulTable,
                        new Record()
                                .set("persist_version", doubtfulVersion + 1)
                                .set("op_user_id", userInfo.getUsr_id())
                                .set("op_user_name", userInfo.getName())
                                .set("op_date", new Date())
                                .set("op_reason", errorMsg)
                                .set("status", 1),
                        new Record().set("id", id).set("persist_version", doubtfulVersion));
                if (!b) return false;

                return CommonService.update(originTable,
                        new Record()
                                .set("persist_version", originVersion + 1)
                                .set("tmp_err_message", errorMsg)
                                .set("tmp_status", 2),
                        new Record().set("id", originId).set("persist_version", originVersion));
            }
        });

        if (!flag) {
            throw new DbProcessException("拒绝失败，请重新尝试！");
        }
        try{
            SftCallBack callback = new SftCallBack();
            callback.callback(osSource, originId);
        }catch(Exception e){
            e.printStackTrace();
            log.error("回调核心接口失败");
        }

    }

    /**
     * 通过放行
     * @param record
     * @return
     * @throws BusinessException
     */
    public void pass(final Record record, final UserInfo userInfo) throws BusinessException {
        final long osSource = TypeUtils.castToLong(record.get("os_source"));
        final long id = TypeUtils.castToLong(record.get("id"));
        final int doubtfulVersion = TypeUtils.castToInt(record.get("persist_version"));
        //封装合法数据公用表
        final Record legalRecord = new Record();

        final String doubtfulTable,legalExtTable;
        if(WebConstant.SftOsSource.LA.getKey() == osSource){
            doubtfulTable = "la_recv_check_doubtful";
        }else{
            throw new ReqDataException("未找到对应的系统来源!");
        }
        final Record doubtfulRecord = Db.findById(doubtfulTable, "id", id);
        if (doubtfulRecord == null) {
            throw new ReqDataException("未找到有效的可疑数据!");
        }
        long doubtfulStatus = TypeUtils.castToLong(doubtfulRecord.get("status"));
        if(doubtfulStatus == 1){
            throw new ReqDataException("该数据已处理！");
        }

        legalRecord.set("source_sys", osSource);
        legalRecord.set("origin_id", TypeUtils.castToLong(doubtfulRecord.get("origin_id")));
        legalRecord.set("pay_code", TypeUtils.castToString(doubtfulRecord.get("pay_code")));
        legalRecord.set("channel_id", TypeUtils.castToString(doubtfulRecord.get("channel_id")));
        legalRecord.set("org_id", TypeUtils.castToInt(doubtfulRecord.get("tmp_org_id")));
        legalRecord.set("org_code", TypeUtils.castToString(doubtfulRecord.get("tmp_org_code")));
        legalRecord.set("amount", TypeUtils.castToBigDecimal(doubtfulRecord.get("amount")));
        legalRecord.set("pay_acc_name", TypeUtils.castToString(doubtfulRecord.get("pay_acc_name")));
        legalRecord.set("pay_cert_type", TypeUtils.castToString(doubtfulRecord.get("pay_cert_type")));
        legalRecord.set("pay_cert_code", TypeUtils.castToString(doubtfulRecord.get("pay_cert_code")));
        legalRecord.set("pay_bank_name", TypeUtils.castToString(doubtfulRecord.get("pay_bank_name")));
        legalRecord.set("pay_bank_type", TypeUtils.castToString(doubtfulRecord.get("pay_bank_type")));
        legalRecord.set("pay_acc_no", TypeUtils.castToString(doubtfulRecord.get("pay_acc_no")));
        legalRecord.set("status", 0);
        legalRecord.set("persist_version", 0);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean b = CommonService.update(doubtfulTable,
                        new Record()
                                .set("persist_version", doubtfulVersion + 1)
                                .set("op_user_id", userInfo.getUsr_id())
                                .set("op_user_name", userInfo.getName())
                                .set("op_date", new Date())
                                .set("op_reason", TypeUtils.castToString(record.get("op_reason")))
                                .set("status", 1),
                        new Record().set("id", id).set("persist_version", doubtfulVersion));
                if (!b) return false;

                //保存合法数据表
                boolean c = Db.save("recv_legal_data", legalRecord);
                if (!c) return false;
                //获取合法数据表id
                long zid = TypeUtils.castToLong(legalRecord.get("id"));
                Record recordExt = new Record();
                if(WebConstant.SftOsSource.LA.getKey() == osSource){
                    recordExt.set("legal_id", zid);
                    recordExt.set("origin_id", TypeUtils.castToLong(doubtfulRecord.get("origin_id")));
                    recordExt.set("branch_code", TypeUtils.castToString(doubtfulRecord.get("branch_code")));
                    recordExt.set("org_code", TypeUtils.castToString(doubtfulRecord.get("org_code")));
                    recordExt.set("preinsure_bill_no", TypeUtils.castToString(doubtfulRecord.get("preinsure_bill_no")));
                    recordExt.set("insure_bill_no", TypeUtils.castToString(doubtfulRecord.get("insure_bill_no")));
                    recordExt.set("biz_type", TypeUtils.castToString(doubtfulRecord.get("biz_type")));
                    recordExt.set("pay_mode", TypeUtils.castToString(doubtfulRecord.get("pay_mode")));
                    recordExt.set("recv_date", TypeUtils.castToDate(doubtfulRecord.get("recv_date")));
                    recordExt.set("pay_cert_type", TypeUtils.castToString(doubtfulRecord.get("pay_cert_type")));
                    recordExt.set("bank_key", TypeUtils.castToString(doubtfulRecord.get("bank_key")));
                    recordExt.set("sale_code", TypeUtils.castToString(doubtfulRecord.get("sale_code")));
                    recordExt.set("sale_name", TypeUtils.castToString(doubtfulRecord.get("sale_name")));
                    recordExt.set("op_code", TypeUtils.castToString(doubtfulRecord.get("op_code")));
                    recordExt.set("op_name", TypeUtils.castToString(doubtfulRecord.get("op_name")));
                    boolean d = Db.save("la_recv_legal_data_ext", recordExt);
                    if (!d) return false;
                }else{
                    return false;
                }
                return true;
            }
        });

        if (!flag) {
            throw new DbProcessException("拒绝失败，请重新尝试！");
        }
    }

}
