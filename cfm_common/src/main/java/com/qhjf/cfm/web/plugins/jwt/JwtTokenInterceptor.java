package com.qhjf.cfm.web.plugins.jwt;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public class JwtTokenInterceptor implements Interceptor {

    private static final Log logger = LogbackLog.getLog(JwtTokenInterceptor.class);

    @Override
    public void intercept(Invocation invocation) {
        Integer code = recoverAuthFromRequest(invocation.getController().getRequest());
        switch (code) {
            case 200: {
                invocation.invoke();
                break;
            }
            default: {
                invocation.getController().renderError(401);
            }
        }
        invocation.getController().getRequest().removeAttribute("me");// 移除避免暴露当前角色信息

    }

    private Integer recoverAuthFromRequest(HttpServletRequest request) {
        logger.debug("enter into recoverAuthFromRequest(HttpServletRequest request)");
        //没有权限用户
        try {
            IJwtAble jwtBean = getMe(request);
            if (jwtBean == null) return 404;
            return 200;
        } catch (Exception e) {
            e.printStackTrace();
            return 404;
        }
    }

    /**
     * 从请求头解析出me
     *
     * @param request
     * @return
     */
    protected static IJwtAble getMe(HttpServletRequest request) {
        logger.debug("getMe(HttpServletRequest request)");
        //从request中获取me ，有则直接返回。
        IJwtAble me = (IJwtAble) request.getAttribute("me");
        if (null != me) {
            logger.debug("me is null!");
            return me;
        }

        String authHeader = request.getHeader(JwtKit.header);
        logger.debug("authHeader is "+authHeader);
        if (StrKit.isBlank(authHeader) || authHeader.length() < JwtKit.tokenPrefix.length()) {
            return null;
        }
        String authToken = authHeader.substring(JwtKit.tokenPrefix.length());
        logger.debug("authToken is "+authToken);
        String jwtUser = JwtKit.getJwtUser(authToken);                      // 从token中解析出jwtAble
        logger.debug("jwtUser is :"+jwtUser);
        if (jwtUser != null){
            logger.debug("jwtUser is not null");
            Date created = JwtKit.getCreatedDateFormToken(authToken);

            IJwtAble oldme = JwtKit.getJwtBean(jwtUser,created);
            if(oldme != null){
                if(authToken.equals(oldme.getLastToken())){
                    logger.debug("没有其他设备登录，lastToke 未发生变化");
                    me = oldme;
                    request.setAttribute("me", me);
                }else{
                    logger.debug("有其它登录发生在此次登录之后，此次登录失效");
                    me = null;
                }
            }
        }
        return me;
    }


}
