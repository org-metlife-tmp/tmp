package com.qhjf.cfm.web.util;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;

import java.sql.SQLException;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/7/23
 * @Description:
 */
public class AccountUtil {

    /**
     * 修改账户信息状态（status： 1：正常、2：销户、3：冻结）
     *
     * @param accId
     * @param status
     * @return
     */
    public static boolean chgAccountStatus(final Long accId, Integer status) {
        final Record record = new Record();
        record.set("acc_id", accId);
        record.set("status", status);
        return Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                //删除锁定表信息
                boolean del = Db.deleteById("acc_process_lock", "acc_id", accId);

                //修改账户状态
                boolean chg = Db.update("account", "acc_id", record);
                return (del && chg);
            }
        });
    }

}
