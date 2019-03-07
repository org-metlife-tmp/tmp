package com.qhjf.cfm.web.service;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class UsrgroupService {

    public List<Record> busmenu() {
        SqlPara modulesSqlPara = Db.getSqlPara("usergroup.modules", Ret.by("isAdmin", 0));
        List<Record> modulesList = Db.find(modulesSqlPara);
        if (modulesList != null && modulesList.size() > 0) {
            for (Record modules : modulesList) {
                SqlPara menusSqlPara = Db.getSqlPara("usergroup.menus", Ret.by("mc", modules.get("code")));
                modules.set("menus", Db.find(menusSqlPara));
            }
        }
        return modulesList;
    }

    public Page<Record> list(int pageNum, int pageSize, Record record) {
        SqlPara usergroupSqlPara = Db.getSqlPara("usergroup.usergroup", Ret.by("map", record.getColumns()));
        Page<Record> page = Db.paginate(pageNum, pageSize, usergroupSqlPara);
        List<Record> list = page.getList();
        if (list != null && list.size() > 0) {
            for (Record usergroup : list) {
                SqlPara ugMenusSqlPara = Db.getSqlPara("usergroup.usergroupMenus", usergroup.get("group_id"));
                usergroup.set("menus", Db.query(ugMenusSqlPara.getSql(), ugMenusSqlPara.getPara()));
            }
        }
        return page;
    }

    public void add(final Record record) throws BusinessException {
        //提取菜单信息
        final List<String> menusList = record.get("menus");
        record.remove("menus");
        record.set("is_builtin", 0);
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                boolean save = Db.save("user_group", "group_id", record);
                if (save && menusList != null && menusList.size() > 0) {
                    return saveUserGroupWithMenu(menusList, record);
                }
                return save;
            }
        });
        if (!flag) {
            throw new DbProcessException("新增用户组失败！");
        }
        record.set("menus", menusList);
    }

    public void del(final Record record) throws BusinessException {
        //校验是否允许删除
        Record userGroup = Db.findById("user_group", "group_id", record.get("group_id"));
        if (userGroup == null) {
            throw new ReqDataException("用户组信息不存在，请刷新重试！");
        }

        if (userGroup.getInt("is_builtin") == 1) {
            throw new ReqDataException("此菜单为内置菜单，不允许删除！");
        }
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //清空关联表信息-user_group_with_menu|user_group_with_user
                Db.deleteById("user_group_with_menu", "group_id", record.get("group_id"));
                Db.deleteById("user_group_with_user", "group_id", record.get("group_id"));
                boolean result = Db.deleteById("user_group", "group_id", record.get("group_id"));
                return result;
            }
        });
        if (!flag) {
            throw new DbProcessException("删除用户组失败！");
        }
    }

    public void chg(final Record record) throws BusinessException {
        Record userGroup = Db.findById("user_group", "group_id", record.get("group_id"));
        if (userGroup == null) {
            throw new ReqDataException("用户组信息不存在，请刷新重试！");
        }
        //提取菜单信息
        final List<String> menusList = record.get("menus");
        record.remove("menus");
        //进行数据更新操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //清除原有关联信息
                Db.deleteById("user_group_with_menu", "group_id", record.get("group_id"));
                boolean update = Db.update("user_group", "group_id", record);
                if (update && menusList != null && menusList.size() > 0) {
                    return saveUserGroupWithMenu(menusList, record);
                }
                return update;
            }
        });
        if (!flag) {
            throw new DbProcessException("修改用户组信息失败！");
        }
        record.set("menus", menusList);
    }

    private boolean saveUserGroupWithMenu(List<String> menusList, Record record) {
        List<Record> userGroupWithMenu = new LinkedList<>();
        for (String s : menusList) {
            Record r = new Record();
            r.set("group_id", record.get("group_id"));
            r.set("menu_code", s);
            userGroupWithMenu.add(r);
        }
        int[] resultArr = Db.batchSave("user_group_with_menu", userGroupWithMenu, 1000);
        return ArrayUtil.checkDbResult(resultArr);
    }

    public void allot(Record record) throws BusinessException {
        //uodp_ids --- group_id
        final int group_id = record.get("group_id");
        //获取 group_ids
        List<Integer> uodp_ids = record.get("uodp_ids");
        final List<Record> saveList = new ArrayList<>(uodp_ids.size() + 1);
        for (int uodp_id : uodp_ids) {
            saveList.add(new Record().set("uodp_id", uodp_id).set("group_id", group_id));
        }
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //清空原有菜单
                Db.deleteById("user_group_with_user", "group_id", group_id);
                int[] resultArr = Db.batchSave("user_group_with_user", saveList, 1000);
                return ArrayUtil.checkDbResult(resultArr);
            }
        });
        if (!flag) {
            throw new DbProcessException("分配用户组菜单失败！");
        }
    }

    public Page<Record> list2(int pageNum, int pageSize, Record record) {
        SqlPara usergroupSqlPara = Db.getSqlPara("usergroup.usergroup", Ret.by("map", record.getColumns()));
        Page<Record> page = Db.paginate(pageNum, pageSize, usergroupSqlPara);
        List<Record> list = page.getList();
        if (list != null && list.size() > 0) {
            for (Record usergroup : list) {
                usergroup.set("uodp_ids", Db.query(Db.getSql("usergroup.uodp_ids"), usergroup.get("group_id")));
            }
        }
        return page;
    }
}
