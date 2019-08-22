package com.qhjf.cfm.web.plugins.jwt;

import java.util.Date;
import java.util.List;

public interface IJwtAble {

    /**
     * 获取角色集合
     *
     * @return
     */
    List<String> getRoles();

    /**
     * 获取权限集合
     *
     * @return
     */
    List<String> getForces();

    /**
     * 上次修改密码时间
     *
     * @return
     */
    Date getLastModifyPasswordTime();


    /**
     * 关联最后的token
     * @param lastToken
     */
    void refLastToken(String lastToken);


    /**
     * 获取最后登录的Token
     * @return
     */
    String getLastToken();

}
