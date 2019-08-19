package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.HashKit;
import com.jfinal.kit.Kv;
import com.jfinal.kit.Ret;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.ReqValidateException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.plugins.jwt.JwtKit;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by zhangsq on 2018/6/28.
 */
public class UserQueryService {

    public List<Record> list(Record record) {
        SqlPara sqlPara = Db.getSqlPara("user_query.list", Kv.by("map", record.getColumns()));
        return Db.find(sqlPara);
    }

    /**
     * 个人设置信息
     *
     * @param record
     * @param userInfo
     * @return
     */
    public Record userinfo(final Record record, final UserInfo userInfo, final UodpInfo uodpInfo) {
        record.set("usr_id", userInfo.getUsr_id());
        SqlPara sqlPara = Db.getSqlPara("user.userPage", Ret.by("map", record.getColumns()));
        Record rec = Db.findFirst(sqlPara);
        rec.set("udops", Db.find(Db.getSql("user.usrUdopList"), rec.get("usr_id")));
        if (userInfo.getIs_admin() != 1) {
            rec.set("cur_uodp_id", uodpInfo.getUodp_id());
        } else {
            rec.set("cur_uodp_id", "");
        }
        return rec;
    }

    /**
     * 修改用户信息
     *
     * @param record
     * @param userInfo
     * @throws BusinessException
     */
    public Record chg(final Record record, final UserInfo userInfo) throws BusinessException {
        final Record where = new Record();

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                where.set("usr_id", userInfo.getUsr_id());

                return CommonService.update("user_info", record, where);
            }
        });
        if (flag) {
            return new Record().set("menu_info", userInfo);
        }
        throw new DbProcessException("修改用户信息失败!");
    }

    /**
     * 修改密码
     *
     * @param record
     * @param userInfo
     * @throws BusinessException
     */
    public void chgpwd(final Record record, final UserInfo userInfo) throws BusinessException {
        final Record where = new Record();
        //原密码
        String oldPwd = TypeUtils.castToString(record.get("old_password"));
        //新密码
        String pwd = TypeUtils.castToString(record.get("password"));
        //确认密码
        String confirmPwd = TypeUtils.castToString(record.get("confirm_password"));

        record.remove("old_password");
        record.remove("confirm_password");

        //根据用户id查询用户信息
        Record userRec = Db.findById("user_info", "usr_id", userInfo.getUsr_id());
        if (userRec == null) {
            throw new ReqDataException("未找到该用户信息!");
        }

        //原密码加密
        String encryptPwd = MD5Kit.encryptPwd(oldPwd, TypeUtils.castToString(userRec.get("salt")), TypeUtils.castToInt(userRec.get("pcount")));
        //校验原密码与当前密码是否一致
        if (!encryptPwd.equals(TypeUtils.castToString(userRec.get("password")))) {
            throw new ReqValidateException("原密码输入错误!");
        }

        //判断两次密码输入是否一致
        if (!pwd.equals(confirmPwd)) {
            throw new ReqValidateException("两次密码输入不一致!");
        }


        // 密码加盐 hash
        String salt = HashKit.generateSalt(16);
        //加密
        int pcount = TypeUtils.castToInt(userRec.get("pcount")) + 1;
        pwd = MD5Kit.encryptPwd(pwd, salt, pcount);

        record.set("password", pwd);
        record.set("salt", salt);
        record.set("pcount", pcount);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                where.set("usr_id", userInfo.getUsr_id());

                return CommonService.update("user_info", record, where);
            }
        });
        if (!flag) {

            throw new DbProcessException("修改密码失败!");
        }
    }

    /**
     * 切换机构
     *
     * @param record
     * @return
     */
    public Record switchUodp(final Record record, final UserInfo userInfo) {
        return new Record().set("user_info", JwtKit.switchUodp(TypeUtils.castToLong(record.get("uodp_id")), userInfo.getLogin_name()));
    }
}
