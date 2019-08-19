package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.utils.RandomStrGenerator;

import java.sql.SQLException;
import java.util.*;


public class BaseDataUsrService {

    public void setStatus(final Record record) throws BusinessException {
        Record thisRecord = Db.findFirst(Db.getSql("user.userInfo"), record.get("usr_id"));
        if (thisRecord == null) {
            throw new ReqDataException("用户信息不存在！");
        }
        int status = TypeUtils.castToInt(thisRecord.get("status"));
        record.set("status", status == 1 ? 2 : 1);
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.update("user_info", "usr_id", record);
            }
        });
        if (!flag)
            throw new DbProcessException("修改用户状态失败！");
    }

    public Page<Record> getUsrPage(int pageNum, int pageSize, Record record) {
        SqlPara sqlPara = Db.getSqlPara("user.userPage", Ret.by("map", record.getColumns()));
        Page<Record> page = Db.paginate(pageNum, pageSize, sqlPara);
        List<Record> list = page.getList();
        if (list != null && list.size() > 0) {
            for (Record r : list) {
                r.set("udops", Db.find(Db.getSql("user.usrUdopList"), r.get("usr_id")));
            }
        }
        return page;
    }

    public Record saveUsrInfo(final Record record) throws BusinessException {
        record.remove("usr_id");
        String login_name = record.getStr("login_name").trim();
        String password = record.getStr("password");
        if (StrKit.isBlank(password)) {
            password = "123456";
        }
        password = password.trim();
        //校验登录名是否已使用
        if (addLoginNameExists(login_name) > 0) {
            throw new ReqDataException("登录名已使用，请重新输入！");

        }
        final List<Record> extRecordList = record.get("extra_infos");
        final List<Record> udopRecordList = record.get("udops");
        if (null == udopRecordList || udopRecordList.isEmpty()) {
            throw new ReqDataException("请选择机构、部门、职位！");
        }
        //移除基本信息中扩展以及关联信息字段
        final int hasExt = (null != extRecordList && extRecordList.size() > 0) ? 1 : 0;
        final int hasUdop = (null != udopRecordList && udopRecordList.size() > 0) ? 1 : 0;
        record.remove("extra_infos", "udops");
        // 密码加盐 hash
        String salt = HashKit.generateSalt(16);
        //加密
        int pcount = RandomStrGenerator.generateIntRandomCode(2, 9);
        password = MD5Kit.encryptPwd(password, salt, pcount);
        record.set("pcount", pcount);
        record.set("salt", salt);
        record.set("password", password);
        record.set("is_have_extra", hasExt);
        record.set("register_date", new Date());
        record.set("pwd_last_change_date", new Date());
        record.set("status", 1);
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //存储用户基本信息
                boolean save = Db.save("user_info", "usr_id", record);
                if (save) {
                    //插入用户、机构、公司、职位关联信息
                    boolean hasUdopBool = isHasUdopBool(hasUdop, udopRecordList, record);
                    boolean hasExtBool = isHasExtBool(hasExt, extRecordList, record);
                    return hasUdopBool && hasExtBool;
                }
                return save;
            }
        });

        if (!flag) {
            throw new DbProcessException("新增用户失败！");
        }
        //操作完毕重新返回。
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("usr_id", record.get("usr_id"));
        Record result = Db.findFirst(Db.getSqlPara("user.userPage", Ret.by("map", paraMap)));
        result.set("udops", udopRecordList);
        return result;
    }

    private long addLoginNameExists(String loginName) {
        Kv cond = Kv.by("status != ", 3).set("login_name = ", loginName);
        SqlPara sqlPara = Db.getSqlPara("user.checkUserLoginName", Kv.by("map", cond));
        return Db.queryLong(sqlPara.getSql(), sqlPara.getPara());
    }

    private long chgLoginNameExists(String loginName, long usrId) {
        Kv cond = Kv.by("status != ", 3).set("login_name = ", loginName).set("usr_id != ", usrId);
        SqlPara sqlPara = Db.getSqlPara("user.checkUserLoginName", Kv.by("map", cond));
        return Db.queryLong(sqlPara.getSql(), sqlPara.getPara());
    }

    public Record findUsrInfo(Record record) {
        //查询未删除的用户
        Record usrInfo = Db.findFirst(Db.getSqlPara("user.userPage", Ret.by("map", record.getColumns())));
        if (usrInfo == null) {
            return null;
        }
        //查询扩展信息
        List<Record> extRecordList = null;
        if (usrInfo.getInt("is_have_extra") != null
                && usrInfo.getInt("is_have_extra") == 1) {
            extRecordList = Db.find(Db.getSql("user.userExtInfo"), record.get("usr_id"));
        }
        usrInfo.set("extra_infos", extRecordList);
        //查询用户、机构、部门、职位关联信息
        List<Record> uodpRecordList = Db.find(Db.getSql("user.usrUdopList"), record.get("usr_id"));
        usrInfo.set("udops", uodpRecordList);
        return usrInfo;
    }

    public void deleteUsr(Record record) throws BusinessException {
        long usr_id = TypeUtils.castToLong(record.get("usr_id"));
        final Record usrInfo = Db.findFirst(Db.getSql("user.userInfo"), usr_id);
        if (usrInfo == null) {
            throw new ReqDataException("用户信息不存在！");
        }
        usrInfo.set("login_name", usrInfo.getStr("login_name") + "_" + usr_id);
        usrInfo.set("status", 3);
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.update("user_info", "usr_id", usrInfo);
            }
        });
        if (!flag) {
            throw new DbProcessException("更新用户状态为删除失败！");
        }
    }

    public Record updateUsrInfo(Record record) throws BusinessException {
        //校验用户信息是否存在
        final long usr_id = TypeUtils.castToLong(record.get("usr_id"));
        final Record usrInfo = Db.findFirst(Db.getSql("user.userInfo"), usr_id);
        if (usrInfo == null) {
            throw new ReqDataException("用户信息不存在！");
        }
        String login_name = record.getStr("login_name").trim();
        //校验登录名是否已使用
        if (chgLoginNameExists(login_name, usr_id) > 0) {
            throw new ReqDataException("登录名已使用，请重新输入！");

        }
        usrInfo.set("login_name", login_name);
        //如果密码为空则表示本次不修改密码
        String password = record.getStr("password");
        if (StrKit.notBlank(password)) {
            password = password.trim();
            // 密码加盐 hash
            String salt = HashKit.generateSalt(16);
            //加密
            int pcount = RandomStrGenerator.generateIntRandomCode(2, 9);
            password = MD5Kit.encryptPwd(password, salt, pcount);
            usrInfo.set("salt", salt);
            usrInfo.set("password", password);
            usrInfo.set("pcount", pcount);
        }
        final List<Record> extRecordList = record.get("extra_infos");
        final List<Record> udopRecordList = record.get("udops");
        if (null == udopRecordList || udopRecordList.isEmpty()) {
            throw new ReqDataException("请选择机构、部门、职位！");
        }
        //移除基本信息中扩展以及关联信息字段
        final int hasExt = (null != extRecordList && extRecordList.size() > 0) ? 1 : 0;
        final int hasUdop = (null != udopRecordList && udopRecordList.size() > 0) ? 1 : 0;
        usrInfo.set("is_have_extra", hasExt);
        record.remove("extra_infos", "udops", "org_name", "dept_name", "pos_name");
        record.remove("salt", "password", "pcount");
        usrInfo.setColumns(record);
        usrInfo.set("pwd_last_change_date", new Date());
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //存储用户基本信息
                boolean update = Db.update("user_info", "usr_id", usrInfo);
                if (update) {
                    //清除原有关联信息--由sql模板改为方法删除 删除之前处理 用户身份与权限的关系
                    //Db.deleteById("user_org_dept_pos", "usr_id", usr_id);
                    //插入用户、机构、公司、职位关联信息
                    //查询原先的权限关系。与 udopRecordList 作对比，获取到权限的菜单信息。与新权限进行绑定，插入
                    boolean hasUdopBool = isHasUdopBool(hasUdop, udopRecordList, usrInfo);
                    Db.deleteById("user_extra_info", "usr_id", usr_id);
                    //插入用户扩展信息
                    boolean hasExtBool = isHasExtBool(hasExt, extRecordList, usrInfo);
                    return hasUdopBool && hasExtBool;
                }
                return update;
            }
        });
        if (!flag) {
            throw new DbProcessException("修改用户信息失败！");
        }
        //操作完毕重新返回。
        Map<String, Object> paraMap = new HashMap<>();
        paraMap.put("usr_id", record.get("usr_id"));
        Record result = Db.findFirst(Db.getSqlPara("user.userPage", Ret.by("map", paraMap)));
        result.set("udops", udopRecordList);
        return result;
    }

    private boolean isHasExtBool(int hasExt, List<Record> extRecordList, Record usrInfo) {
        boolean hasExtBool;
        if (hasExt > 0) {
            for (Record extR : extRecordList) {
                extR.set("usr_id", usrInfo.get("usr_id"));
            }
            int[] extResultArr = Db.batchSave("user_extra_info", extRecordList, 1000);
            hasExtBool = ArrayUtil.checkDbResult(extResultArr);
        } else {
            hasExtBool = true;
        }
        return hasExtBool;
    }

    private boolean isHasUdopBool(int hasUdop, List<Record> udopRecordList, Record usrInfo) {

        long usr_id = TypeUtils.castToLong(usrInfo.get("usr_id"));

        String sql = "SELECT DISTINCT\n" +
                "\tCONCAT (\n" +
                "\t\t'uodp_',\n" +
                "\t\tuodp.org_id,\n" +
                "'_'," +
                "\t\tuodp.dept_id,\n" +
                "'_'," +
                "\t\tuodp.pos_id\n" +
                "\t) as uodp,\n" +
                "\tugwu.group_id\n" +
                "FROM\n" +
                "\tuser_info usr\n" +
                "JOIN user_org_dept_pos uodp ON uodp.usr_id = usr.usr_id\n" +
                "JOIN user_group_with_user ugwu ON ugwu.uodp_id = uodp.id\n" +
                "WHERE\n" +
                "\tusr.usr_id = ?";

        String sql2 = "SELECT DISTINCT\n" +
                "\tCONCAT (\n" +
                "\t\t'uodp_',\n" +
                "\t\tuodp.org_id,\n" +
                "'_'," +
                "\t\tuodp.dept_id,\n" +
                "'_'," +
                "\t\tuodp.pos_id\n" +
                "\t) as uodp,\n" +
                "\tuodp.id\n" +
                "FROM\n" +
                "\tuser_info usr\n" +
                "JOIN user_org_dept_pos uodp ON uodp.usr_id = usr.usr_id\n" +
                "WHERE\n" +
                "\tusr.usr_id = ?";

        String sql3 = "DELETE\n" +
                "FROM\n" +
                "\tuser_group_with_user\n" +
                "WHERE\n" +
                "\tuodp_id IN (\n" +
                "\t\tSELECT\n" +
                "\t\t\tuodp.id\n" +
                "\t\tFROM\n" +
                "\t\t\tuser_info usr\n" +
                "\t\tJOIN user_org_dept_pos uodp ON uodp.usr_id = usr.usr_id\n" +
                "\t\tWHERE\n" +
                "\t\t\tusr.usr_id = ?\n" +
                "\t)";

        //通过usr_id 获取old权限信息
        List<Record> oldMeuns = Db.find(sql, usr_id);
        //删除旧的菜单权限信息并且删除菜单信息。
        Db.delete(sql3, usr_id);
        Db.deleteById("user_org_dept_pos", "usr_id", TypeUtils.castToLong(usrInfo.get("usr_id")));
        boolean hasUdopBool;
        if (hasUdop > 0) {
            //关联信息插入 usr_id
            for (Record udopR : udopRecordList) {
                udopR.remove("pos_name");
                udopR.remove("org_name");
                udopR.remove("dept_name");
                udopR.remove("id");

                udopR.set("usr_id", usrInfo.get("usr_id"));
            }
            int[] uodpResultArr = Db.batchSave("user_org_dept_pos", udopRecordList, 1000);
            hasUdopBool = ArrayUtil.checkDbResult(uodpResultArr);
            //新职位保存成功后，维护新位置与菜单的关系
            if (hasUdopBool && oldMeuns != null && oldMeuns.size() > 0) {
                //新职位
                List<Record> newUODPs = Db.find(sql2, usr_id);

                List<Record> newMenus = new ArrayList<>();
                //新职位与旧职位就行匹配，插入关系。
                for (Record newUODP : newUODPs) {
                    String s = TypeUtils.castToString(newUODP.get("uodp"));
                    long id = TypeUtils.castToLong(newUODP.get("id"));

                    for (Record oldMeun : oldMeuns) {
                        Record r = new Record();
                        String _ss = TypeUtils.castToString(oldMeun.get("uodp"));
                        long group_id = TypeUtils.castToLong(oldMeun.get("group_id"));
                        if (_ss.equals(s)) {
                            r.set("group_id", group_id);
                            r.set("uodp_id", id);
                            newMenus.add(r);
                        }
                    }
                }
                if(newMenus != null && newMenus.size() > 0){
                    hasUdopBool = ArrayUtil.checkDbResult(Db.batchSave("user_group_with_user", newMenus, 1000));
                }

            }
        } else {
            hasUdopBool = true;
        }
        return hasUdopBool;
    }
}
