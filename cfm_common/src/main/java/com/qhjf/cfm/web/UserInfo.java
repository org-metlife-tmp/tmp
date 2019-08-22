package com.qhjf.cfm.web;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.IJwtAble;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class UserInfo implements IJwtAble, Serializable {

    private static final long serialVersionUID = 4437700679219643268L;

    private Long usr_id;

    private String name;

    private String phone;

    private String email;

    private String login_name;

    private Timestamp register_date;

    private Date pwd_last_change_date;

    private Integer is_have_extra;

    private Integer persist_version;

    private Integer is_admin;

    private Integer is_boss;

    private Integer status;

    private List<ModuleInfo> menu_info;

    private List<UodpInfo> uodp;

    private Long cur_uodp_Id; // 当前的uodpId;


    private String _token;


    public UserInfo(Record userInfo) {
        this.usr_id = TypeUtils.castToLong(userInfo.get("usr_id"));
        this.name = TypeUtils.castToString(userInfo.get("name"));
        this.email = TypeUtils.castToString(userInfo.get("email"));
        this.phone = TypeUtils.castToString(userInfo.get("phone"));
        this.login_name = TypeUtils.castToString(userInfo.get("login_name"));
        this.register_date = TypeUtils.castToTimestamp(userInfo.get("register_date"));
        this.pwd_last_change_date = TypeUtils.castToDate(userInfo.get("pwd_last_change_date"));
        this.persist_version = TypeUtils.castToInt(userInfo.get("persist_version"));
        this.is_boss = TypeUtils.castToInt(userInfo.get("is_boss"));
        this.is_admin = TypeUtils.castToInt(userInfo.get("is_admin"));
        this.status = TypeUtils.castToInt(userInfo.get("status"));
    }

    @Override
    public List<String> getRoles() {
        ArrayList result = new ArrayList();
        if (is_admin == WebConstant.YesOrNo.YES.getKey()) {
            result.add(WebConstant.UserRole.admin.name());
        } else {
            result.add(WebConstant.UserRole.normal.name());
        }
        return result;
    }

    @Override
    public List<String> getForces() {
        ArrayList result = new ArrayList();
        if (menu_info != null) {
            for (ModuleInfo moduleInfo : menu_info) {
                for (ModuleInfo.MenuItem menuItem : moduleInfo.getItems()) {
                    result.add(menuItem.getCode());
                }
            }
        }

        return result;
    }

    @Override
    public Date getLastModifyPasswordTime() {
        return pwd_last_change_date;
    }

    @Override
    public void refLastToken(String lastToken) {
        this._token = lastToken;
    }

    @Override
    public String getLastToken() {
        return this._token;
    }

    public Long getUsr_id() {
        return usr_id;
    }

    public void setUsr_id(Long usr_id) {
        this.usr_id = usr_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLogin_name() {
        return login_name;
    }

    public void setLogin_name(String login_name) {
        this.login_name = login_name;
    }

    public Timestamp getRegister_date() {
        return register_date;
    }

    public void setRegister_date(Timestamp register_date) {
        this.register_date = register_date;
    }

    public Date getPwd_last_change_date() {
        return pwd_last_change_date;
    }

    public void setPwd_last_change_date(Date pwd_last_change_date) {
        this.pwd_last_change_date = pwd_last_change_date;
    }

    public Integer getIs_have_extra() {
        return is_have_extra;
    }

    public void setIs_have_extra(Integer is_have_extra) {
        this.is_have_extra = is_have_extra;
    }

    public Integer getPersist_version() {
        return persist_version;
    }

    public void setPersist_version(Integer persist_version) {
        this.persist_version = persist_version;
    }

    public Integer getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(Integer is_admin) {
        this.is_admin = is_admin;
    }

    public Integer getIs_boss() {
        return is_boss;
    }

    public void setIs_boss(Integer is_boss) {
        this.is_boss = is_boss;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<ModuleInfo> getMenu_info() {
        return menu_info;
    }

    public void setMenu_info(List<ModuleInfo> menu_info) {
        this.menu_info = menu_info;
    }

    public List<UodpInfo> getUodp() {
        return uodp;
    }

    public void setUodp(List<UodpInfo> uodp) {
        this.uodp = uodp;
    }


    public Long getCur_uodp_Id() {
        return cur_uodp_Id;
    }

    public void setCur_uodp_Id(Long cur_uodp_Id) {
        this.cur_uodp_Id = cur_uodp_Id;
    }

    public void addUodp(List<Record> uodps) {
        if (this.uodp == null) {
            this.uodp = new ArrayList<>();
        }
        for (Record record : uodps) {
            UodpInfo info = new UodpInfo();
            info.setUodp_id(TypeUtils.castToLong(record.get("uodp_id")));
            info.setDept_id(TypeUtils.castToLong(record.get("dept_id")));
            info.setDept_name(TypeUtils.castToString(record.get("dept_name")));
            info.setOrg_id(TypeUtils.castToLong(record.get("org_id")));
            info.setOrg_name(TypeUtils.castToString(record.get("org_name")));
            info.setPos_id(TypeUtils.castToLong(record.get("pos_id")));
            info.setPos_name(TypeUtils.castToString(record.get("pos_name")));
            info.setIs_default(TypeUtils.castToInt(record.get("is_default")));
            if (info.getIs_default() == WebConstant.YesOrNo.YES.getKey()) {
                this.cur_uodp_Id = info.getUodp_id();
            }
            this.uodp.add(info);
        }
    }

    public void addMenus(List<Record> li) {
        if (li != null && li.size() > 0) {
            if (this.menu_info == null) {
                this.menu_info = new ArrayList<>();
            }
            String cur_module = null;
            ModuleInfo info = null;
            for (Record record : li) {
                String module_name = TypeUtils.castToString(record.get("module_name"));
                String module_code = TypeUtils.castToString(record.get("module_code"));
                if (cur_module == null || !cur_module.equals(module_code)) {
                    info = new ModuleInfo(module_code, module_name);
                    this.menu_info.add(info);
                    cur_module = module_code;
                }
                info.addItem(TypeUtils.castToString(record.get("menu_code")), TypeUtils.castToString(record.get("menu_name")));
            }


        }
    }

    /**
     * 获取当前Uodp对象
     *
     * @return
     */
    public UodpInfo getCurUodp() {
        UodpInfo curUodpInfo = null;
        if (uodp != null && uodp.size() > 0) {
            for (UodpInfo info : uodp) {
                if (info.getUodp_id().longValue() == this.cur_uodp_Id.longValue()) {
                    curUodpInfo = info;
                    break;
                }
            }
        }
        return curUodpInfo;
    }


    /**
     * 获取用户对应的审批流OP(机构，职位)键值
     *
     * @return
     */
    public String[] getWfOPDefine() {
        String[] result = null;
        List<String> temp = new ArrayList<>();
        if (uodp != null && uodp.size() > 0) {
            for (int i = 0; i < uodp.size(); i++) {
                UodpInfo info = uodp.get(i);
                temp.add("O" + info.getOrg_id());
                temp.add("O" + info.getOrg_id() + "P" + info.getPos_id());
            }
            if (temp != null && temp.size() > 0) {
                result = temp.toArray(new String[0]);
            }
        }
        return result;
    }


    /**
     * 刷新菜单信息
     */
    public void refMenuInfo() {
        this.menu_info = null;
        List<Record> li = null;
        if (getIs_admin() == WebConstant.YesOrNo.YES.getKey()) {
            String sql = Db.getSql("getAdminMenu");
            li = Db.find(sql);
        } else {
            String sql = Db.getSql("getUseMenu");
            li = Db.find(sql, getCur_uodp_Id());
        }
        addMenus(li);
    }
}

