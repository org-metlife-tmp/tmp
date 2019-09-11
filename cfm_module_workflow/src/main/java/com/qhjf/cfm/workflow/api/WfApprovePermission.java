package com.qhjf.cfm.workflow.api;

/**
 * 审批流节点审批用户表达式
 */
public class WfApprovePermission {

    private Long[] users;
    private String push_org;
    private Long[] position;
    private Long[] exlude_users;

    public Long[] getUsers() {
        return users;
    }

    public void setUsers(Long[] users) {
        this.users = users;
    }

    public String getPush_org() {
        return push_org;
    }

    public void setPush_org(String push_org) {
        this.push_org = push_org;
    }

    public Long[] getPosition() {
        return position;
    }

    public void setPosition(Long[] position) {
        this.position = position;
    }

    public Long[] getExlude_users() {
        return exlude_users;
    }

    public void setExlude_users(Long[] exlude_users) {
        this.exlude_users = exlude_users;
    }
}
