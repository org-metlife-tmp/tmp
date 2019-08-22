package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.LevelCodeGenerator;

import java.sql.SQLException;
import java.util.List;

public class BaseDataOrgService {

    public List<Record> getOrgList() {
        String sql = Db.getSql("org.getOrgList");
        return Db.find(sql);
    }

    public void saveOrgInfo(final Record record) throws BusinessException {
        record.remove("org_id");

        uniqueNameAdd(record);
        //得到父ID
        Long parent_id = TypeUtils.castToLong(record.get("parent_id"));
        if (null == parent_id) {
            throw new ReqDataException("机构[parent_id]不可为空！");
        }
        //查询父公司
        Record parent_record = Db.findFirst(Db.getSqlPara("org.findOrgInfoById", parent_id));
        if (null == parent_record) {
            throw new ReqDataException("添加机构时，父级节点为空！");
        }
        //获取父级level_code
        String parent_levelCode = parent_record.getStr("level_code");
        //获取父级level_num
        int parent_levelNum = parent_record.getInt("level_num");
        //构建level_code生成器
        LevelCodeGenerator gen = new LevelCodeGenerator(parent_levelCode, parent_levelNum);
        //子级默认level_num加1
        int curLevelNum = parent_levelNum + 1;
        //查询最大
        Record maxCodeRecord = Db.findFirst(Db.getSql("org.getMAXCodeSql"), parent_levelCode,
                curLevelNum);
        String maxLevelCode = "";
        if (maxCodeRecord != null) {
            maxLevelCode = maxCodeRecord.getStr("level_code");
        }
        String curLevelCode = gen.genChildCodeByParent(parent_levelCode, maxLevelCode);
        //获取扩展信息
        final List<Record> extList = record.get("extra_infos");
        record.remove("extra_infos");
        final int hasExt = (extList != null && extList.size() > 0) ? 1 : 0;
        record.set("is_have_extra", hasExt);
        record.set("level_num", curLevelNum);
        record.set("level_code", curLevelCode);
        record.set("status", 1);
        boolean result = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                record.remove("children");
                boolean flag = Db.save("organization", "org_id", record);
                if (flag && hasExt == 1) {
                    return saveOrgExtInfo(extList, record.getLong("org_id"));
                }
                return flag;
            }
        });
        if (!result)
            throw new DbProcessException("新增机构信息失败！");
    }

    public void chgOrgInfo(final Record record) throws BusinessException {
        uniqueNameChg(record);
        //获取到基础信息
        //设置是否有扩展信息
        final List<Record> extList = record.get("extra_infos");
        record.remove("extra_infos");
        final int hasExt = (extList != null && extList.size() > 0) ? 1 : 0;
        record.set("is_have_extra", hasExt);
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                record.remove("children");
                boolean result = Db.update("organization", "org_id", record);
                //清空原有扩展信息
                Db.delete(Db.getSql("org.delOrgExt"), record.get("org_id"));
                if (result && hasExt == 1) {
                    return saveOrgExtInfo(extList, record.getLong("org_id"));
                }
                return result;
            }
        });
        if (!flag)
            throw new DbProcessException("修改机构信息失败");
    }

    private boolean saveOrgExtInfo(List<Record> list, long org_id) {
        //重新添加新扩展信息
        for (Record record : list) {
            record.set("org_id", org_id);
        }
        int[] org_extra_infos = Db.batchSave("org_extra_info", list, 1000);
        return ArrayUtil.checkDbResult(org_extra_infos);
    }

    public void delOrgInfo(final Record record) throws BusinessException {
        //获取机构ID
        Long org_id = TypeUtils.castToLong(record.get("org_id"));
        if (null == org_id) {
            throw new ReqDataException("机构ID不可为空！");
        }
        //校验此机构下是否含有子公司
        long childOrgNum = Db.queryLong(Db.getSql("org.childOrgNum"), org_id);
        if (childOrgNum > 0) {
            throw new ReqDataException("此机构下含有子公司，不可直接删除！");
        }
        //校验此机构是否与用户关联
        long orgUserNum = Db.queryLong(Db.getSql("org.orgUserNum"), org_id);
        if (orgUserNum > 0) {
            throw new ReqDataException("此机构已被用户使用，不可直接删除！");
        }
        //查询机构信息，用于重置机构编号
        final Record orgInfo = Db.findFirst(Db.getSqlPara("org.findOrgInfoById", org_id));
        if (orgInfo == null) {
            throw new ReqDataException("机构信息不存在！");
        }
        //重置机构编号,oldCode+"_"+org_id
        orgInfo.set("code", orgInfo.getStr("code") + "_" + org_id);
        //修改机构状态
        orgInfo.set("status", 3);
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //删除机构信息，逻辑删除，修改状态
                return Db.update("organization", "org_id", orgInfo);
            }
        });
        if (!flag)
            throw new DbProcessException("删除机构信息失败");
    }

    public void setState(final Record record) throws BusinessException {
        Record thisRecord = Db.findFirst(Db.getSqlPara("org.findOrgInfoById",record.get("org_id")));
        if (thisRecord == null) {
            throw new ReqDataException("机构信息不存在！");
        }
        int status = TypeUtils.castToInt(thisRecord.get("status"));
        record.set("status", status == 1 ? 2 : 1);
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.update("organization", "org_id", record);
            }
        });
        if (!flag)
            throw new DbProcessException("修改机构状态失败！");
    }

    private void uniqueNameAdd(Record record) throws ReqDataException {
        long re = Db.queryLong(Db.getSql("org.getOrgNumByCode"), record.get("code"));
        if (re > 0) {
            throw new ReqDataException("系统已存在公司编号" + record.get("code") + "，请重新修改后保存！");
        }
    }

    private void uniqueNameChg(Record record) throws ReqDataException {
        long re = Db.queryLong(Db.getSql("org.getOrgNumByCodeExcludeId"), record.get("code"), record.get("org_id"));
        if (re > 0) {
            throw new ReqDataException("系统已存在公司编号" + record.get("code") + "，请重新修改后保存！");
        }
    }
}
