package com.qhjf.cfm.workflow.api;

import com.jfinal.plugin.activerecord.IAtom;

import java.sql.SQLException;

/**
 * 工作流事务处理
 */
public abstract  class WfIAtom implements IAtom {

    protected String errMsg;         //返回给调用系统的错误消息


    @Override
    public abstract  boolean run() throws SQLException;

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

}
