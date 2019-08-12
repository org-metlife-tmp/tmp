package com.qhjf.cfm.web.plugins.jwt;

import com.qhjf.cfm.exceptions.BusinessException;

public interface IJwtUserService {

    /**
     * 登录接口 返回一个 IJwtAble的数据
     *
     * @param userName
     * @param password
     * @return
     */

    IJwtAble login(String userName, String password) throws BusinessException;
}
