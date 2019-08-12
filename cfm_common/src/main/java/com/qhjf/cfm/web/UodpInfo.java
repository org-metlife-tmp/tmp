package com.qhjf.cfm.web;

import java.io.Serializable;

/**
 * 用户、机构、部门、职位关联信息
 */
public class UodpInfo implements Serializable {

    private static final long serialVersionUID = 4805937927045642886L;
    private Long uodp_id;
    private Long org_id;
    private String org_name;
    private Long dept_id;
    private String dept_name;
    private Long pos_id;
    private String pos_name;
    private Integer is_default;

    public Long getOrg_id() {
        return org_id;
    }

    public void setOrg_id(Long org_id) {
        this.org_id = org_id;
    }

    public String getOrg_name() {
        return org_name;
    }

    public void setOrg_name(String org_name) {
        this.org_name = org_name;
    }

    public Long getDept_id() {
        return dept_id;
    }

    public void setDept_id(Long dept_id) {
        this.dept_id = dept_id;
    }

    public String getDept_name() {
        return dept_name;
    }

    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }

    public Long getPos_id() {
        return pos_id;
    }

    public void setPos_id(Long pos_id) {
        this.pos_id = pos_id;
    }

    public String getPos_name() {
        return pos_name;
    }

    public void setPos_name(String pos_name) {
        this.pos_name = pos_name;
    }

    public Integer getIs_default() {
        return is_default;
    }

    public void setIs_default(Integer is_default) {
        this.is_default = is_default;
    }

    public Long getUodp_id() {
        return uodp_id;
    }

    public void setUodp_id(Long uodp_id) {
        this.uodp_id = uodp_id;
    }
}
