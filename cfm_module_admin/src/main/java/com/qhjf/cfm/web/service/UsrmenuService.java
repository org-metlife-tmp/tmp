package com.qhjf.cfm.web.service;

import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.utils.ArrayUtil;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UsrmenuService {

    public Page<Record> getUsrPage(int pageNum, int pageSize, Record record) {
        SqlPara usrSqlPara = Db.getSqlPara("user.userMenuPage", Ret.by("map", record.getColumns()));
        Page<Record> page = Db.paginate(pageNum, pageSize, usrSqlPara);
        List<Record> list = page.getList();
        if(list != null && list.size() > 0){
            for(Record r : list){
                r.set("group_ids",Db.query(Db.getSql("user.userGroupIds"),r.get("uodp_id")));
            }
        }
        return page;
    }

    public void allot(Record record) throws BusinessException {
        //uodp_id --- group_ids
        final int uodp_id = record.get("uodp_id");
        //获取 group_ids
        List<Integer> group_ids = record.get("group_ids");
        final List<Record> saveList = new ArrayList<>(group_ids.size() + 1);
        for (int group_id : group_ids) {
            saveList.add(new Record().set("group_id", group_id).set("uodp_id", uodp_id));
        }
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //清空原有菜单
                Db.deleteById("user_group_with_user", "uodp_id", uodp_id);
                int[] resultArr = Db.batchSave("user_group_with_user", saveList, 1000);
                return ArrayUtil.checkDbResult(resultArr);
            }
        });
        if (!flag) {
            throw new DbProcessException("分配用户菜单失败！");
        }
    }
}
