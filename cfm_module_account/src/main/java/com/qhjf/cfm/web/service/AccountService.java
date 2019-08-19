package com.qhjf.cfm.web.service;


import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 账户信息维护
 *
 * @auther zhangyuanyuan
 * @create 2018/6/29
 */

public class AccountService {

    /**
     * 账户信息列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> findChangeToPage(int pageNum, int pageSize, final Record record, final UodpInfo uodpInfo) {
        Long orgId = uodpInfo.getOrg_id();
        Record orgRec = Db.findById("organization", "org_id", orgId);
        record.set("level_code", orgRec.get("level_code"));
        record.set("level_num", orgRec.get("level_num"));

        SqlPara sqlPara = Db.getSqlPara("acc.findAccountToPage", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 账户信息查看
     *
     * @param record
     * @param userInfo
     * @return
     * @throws BusinessException
     */
    public Record detail(final Record record, UserInfo userInfo) throws BusinessException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        long accId = TypeUtils.castToLong(record.get("acc_id"));

        //根据账户id查询账户信息
        Record accRec = Db.findById("account", "acc_id", accId);

        if(accRec != null){
            //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
            //否则进行机构权限的校验
            if (record.get("wf_inst_id") == null || record.get("biz_type") == null) {
                CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(), accRec.getLong("org_id"));
            }
        }else{
            throw new ReqDataException("未找到有效的账户信息!");
        }


        if(accRec.get("open_date") != null){
            accRec.set("open_date", format.format(TypeUtils.castToDate(accRec.get("open_date"))));
        }


        Record orgRec = Db.findById("organization", "org_id", TypeUtils.castToLong(accRec.get("org_id")));
        accRec.set("org_name", TypeUtils.castToString(orgRec.get("name")));

        //根据账户id查询账户拓展信息
        List<Record> extRecList = Db.find(Db.getSql("acc.findAccountExtInfo"), accId);
        for (Record ext : extRecList) {
            accRec.set(TypeUtils.castToString(ext.get("type_code")), TypeUtils.castToString(ext.get("value")));
        }


        //开户行
        Record bankRec = Db.findById("all_bank_info", "cnaps_code", TypeUtils.castToString(accRec.get("bank_cnaps_code")));
        if (bankRec != null) {
            accRec.set("bank_name", TypeUtils.castToString(bankRec.get("name")));
        }

        //币种
        Record currRec = Db.findById("currency", "id", TypeUtils.castToLong(accRec.get("curr_id")));
        if (currRec != null) {
            accRec.set("currency_name", TypeUtils.castToString(currRec.get("name")));
        }

        //账户属性
        Record attRec = Db.findFirst(Db.getSql("acc.findCategory"), "acc_attr", TypeUtils.castToString(accRec.get("acc_attr")));
        if (attRec != null) {
            accRec.set("acc_attr_name", TypeUtils.castToString(attRec.get("value")));
        }

        //账户用途
        Record purRec = Db.findFirst(Db.getSql("acc.findCategory"), "acc_purpose", TypeUtils.castToString(accRec.get("acc_purpose")));
        if (purRec != null) {
            accRec.set("acc_purpose_name", TypeUtils.castToString(purRec.get("value")));
        }

        SqlPara sqlPara = null;

        //账户开户 begin
        Record recordComRec = new Record();
        recordComRec.set("acc_id", accId);
        sqlPara = Db.getSqlPara("aoc.fingOpenCompleteById", Ret.by("cond", recordComRec.getColumns()));
        recordComRec = Db.findFirst(sqlPara);
        if (recordComRec != null) {
            //根据开户id查询
            recordComRec = Db.findById("acc_open_intention_apply", "id", TypeUtils.castToLong(recordComRec.get("relation_id")));
        }
        accRec.set("intention", recordComRec);

        //账户开户 end


        //账户变更 begin
        //根据账户id查询变更信息
        List<Record> chgList = new ArrayList<>();
        Record chgRec = new Record();
        chgRec.set("acc_id", accId);
        sqlPara = Db.getSqlPara("chg.fingchgById", Ret.by("cond", chgRec.getColumns()));
        chgRec = Db.findFirst(sqlPara);
        if (chgRec != null) {
            //根据变更信息id查询变更详细信息
            chgList = Db.find(Db.getSql("chg.findChangeDetailByApplyId"), TypeUtils.castToLong(chgRec.get("id")));
        }
        accRec.set("change", chgList);
        //账户变更 end


        //销户申请 begin
        Record closeCom = new Record();
        closeCom.set("acc_id", accId);
        sqlPara = Db.getSqlPara("caf.fingCloseCompleteById", Ret.by("cond", closeCom.getColumns()));
        closeCom = Db.findFirst(sqlPara);
        if (closeCom != null) {
            closeCom = Db.findById("acc_close_intertion_apply", "id", TypeUtils.castToLong(closeCom.get("relation_id")));
        }
        accRec.set("close", closeCom);
        //销户申请 end

        return accRec;
    }

    /**
     * 修改账户信息
     *
     * @param record
     * @param userInfo
     * @return
     * @throws BusinessException
     */
    public Record chg(final Record record, UserInfo userInfo) throws BusinessException {
        final long accId = TypeUtils.castToLong(record.get("acc_id"));
        //根据id查询账户信息
        Record accRec = Db.findById("account", "acc_id", accId);

        if (accRec != null){
            //如果recode中同时含有“wf_inst_id"和biz_type ,则判断是workflow模块forward过来的，不进行机构权限的校验
            //否则进行机构权限的校验
            if (record.get("wf_inst_id") == null || record.get("biz_type") == null) {
                CommonService.checkUseCanViewBill(userInfo.getCurUodp().getOrg_id(), accRec.getLong("org_id"));
            }
        }else{
            throw new ReqDataException("未找到有效的账户信息!");
        }


        //根据帐号id查询该帐号是否有拓展信息
        final List<Record> extRecList = Db.find(Db.getSql("acc.findAccountExtInfo"), accId);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean flag = Db.update("account", "acc_id", new Record().set("acc_id", accId)
//                        .set("acc_purpose", TypeUtils.castToString(record.get("acc_purpose")))
                                .set("deposits_mode", TypeUtils.castToString(record.get("deposits_mode")))
                );
                if (flag) {
                    String bank_address = TypeUtils.castToString(record.get("bank_address"));
                    String bank_contact = TypeUtils.castToString(record.get("bank_contact"));
                    String bank_contact_phone = TypeUtils.castToString(record.get("bank_contact_phone"));

                    Map<String, String> map = new HashMap<>();
                    List<String> delInfo = new ArrayList();
                    if(bank_address != null && !"".equals(bank_address)){
                        map.put("bank_address", bank_address);
                    }else{
                        delInfo.add("bank_address");
                    }

                    if(bank_contact != null && !"".equals(bank_contact)){
                        map.put("bank_contact", bank_contact);
                    }else{
                        delInfo.add("bank_contact");
                    }

                    if(bank_contact_phone != null && !"".equals(bank_contact_phone)){
                        map.put("bank_contact_phone", bank_contact_phone);
                    }else{
                        delInfo.add("bank_contact_phone");
                    }

                    if(delInfo != null && delInfo.size() > 0){
                        for (String code : delInfo) {
                            Db.deleteById("acc_extra_info","acc_id,type_code",accId,code);
                        }

                    }
                    if(map != null && map.size() > 0){
                        if(extRecList != null && extRecList.size() > 0){
                            for (Map.Entry<String, String> entry : map.entrySet()){
                                //先更新，没有更新的条目，进行插入
                                int result = Db.update(Db.getSql("acc.chgAccountExtraInfo"), entry.getValue(), accId, entry.getKey());
                                if(result <= 0){
                                    Record extRec = new Record();
                                    extRec.set("acc_id", accId);
                                    extRec.set("type_code", entry.getKey());
                                    extRec.set("value", entry.getValue());
                                    if(!Db.save("acc_extra_info",extRec)){
                                        return false;
                                    }
                                }
                            }

                        }else{
                            //新增
                            List<Record> extAccList = new ArrayList<>();
                            for (Map.Entry<String, String> entry : map.entrySet()){
                                Record extRec = new Record();
                                extRec.set("acc_id", accId);
                                extRec.set("type_code", entry.getKey());
                                extRec.set("value", entry.getValue());
                                extAccList.add(extRec);
                            }
                            //账户拓展信息插入
                            int[] result = Db.batchSave("acc_extra_info", extAccList, extAccList.size());
                            if (result.length < 0) {
                                return false;
                            }
                        }
                    }
                    return true;
                }
                return false;

            }
        });

        if (flag) {
            return record;
        }

        throw new DbProcessException("修改账户信息失败!");
    }

    public List<Record> findAccsByST(Record record) {
        List<Record> list = null;
        if (record == null) {
            return null;
        }
        Kv cond = Kv.by("status", TypeUtils.castToInt(record.get("status"))).set("org_id", TypeUtils.castToLong(record.get("org_id")));
        if (record.get("acc_id") != null) {
            cond.set(Kv.by("acc_id", TypeUtils.castToLong(record.get("acc_id"))));
        }
        if (record.get("exclude_ids") != null) {
            cond.set("exclude_ids", record.get("exclude_ids"));
        }
        SqlPara sqlPara = Db.getSqlPara("acc.listByST", cond);
        list = Db.find(sqlPara);

        return list;
    }
}
