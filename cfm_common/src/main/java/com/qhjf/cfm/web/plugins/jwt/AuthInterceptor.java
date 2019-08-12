package com.qhjf.cfm.web.plugins.jwt;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.log.Log;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class AuthInterceptor implements Interceptor {

    private static final Log log = LogbackLog.getLog(AuthInterceptor.class);

    @Override
    public void intercept(Invocation inv) {
        log.debug("enter into intercept ");
        Controller controller = inv.getController();
        Method method = inv.getMethod();
        Class clazz = inv.getTarget().getClass();
        if (handleClass(clazz, method, controller) && handleMethod(method, controller)) {
            inv.invoke();
        } else {
            controller.renderError(401);
        }
        //inv.getController().getRequest().removeAttribute("me");// 移除避免暴露当前角色信息
    }


    /**
     * 权限角色判断机制
     *
     * @param method
     * @param controller
     * @return
     */
    private boolean handleMethod(Method method, Controller controller) {
        log.debug("handleClass(Class clazz, Method method, Controller controller)");
        log.debug(method.getName()+"  "+controller.getViewPath());
        // 判断是否有这个注解
        if (method.isAnnotationPresent(Auth.class)) {
            Auth auth = method.getAnnotation(Auth.class);
            IJwtAble jwt_me = (IJwtAble) JwtTokenInterceptor.getMe(controller.getRequest()); // 从请求头解析出me
            if (jwt_me == null){
                log.debug("jwt_me is null , 直接无权访问");
                return false;            // 直接无权访问
            }

            String[] handlerArray = auth.withForces();
            if (handlerArray != null && handlerArray.length > 0) return withForcesHandle(handlerArray, jwt_me);

            handlerArray = auth.withRoles();
            if (handlerArray != null && handlerArray.length > 0) return withRolesHandle(handlerArray, jwt_me);

            handlerArray = auth.hasForces();
            if (handlerArray != null && handlerArray.length > 0)
                return hasForcesHandle(handlerArray, jwt_me);
            handlerArray = auth.hasRoles();
            return hasRolesHandle(handlerArray, jwt_me);
        }
        // 没有这个注解直接放行
        log.debug("没有这个注解直接放行");
        return true;
    }

    /**
     * 类级别判断机制
     *
     * @param clazz
     * @param controller
     * @return
     */
    private boolean handleClass(Class clazz, Method method, Controller controller) {
        log.debug("handleClass(Class clazz, Method method, Controller controller)");
        log.debug(clazz.getName()+"  "+method.getName()+"  "+controller.getViewPath());
        // 方法上如果有了以方法上的为准
        if ( !method.isAnnotationPresent(Auth.class) && clazz.isAnnotationPresent(Auth.class)) {
            Auth auth = (Auth) clazz.getAnnotation(Auth.class);
            IJwtAble jwt_me = (IJwtAble) JwtTokenInterceptor.getMe(controller.getRequest()); // 从请求头解析出me
            if (jwt_me == null) {
                log.debug("jwt_me == null 无权访问");
                return false;   // 直接无权访问
            }

            String[] handlerArray = auth.withForces();
            if (handlerArray != null && handlerArray.length > 0)
                return withForcesHandle(handlerArray, jwt_me);

            handlerArray = auth.withRoles();
            if (handlerArray != null && handlerArray.length > 0)
                return withRolesHandle(handlerArray, jwt_me);

            handlerArray = auth.hasForces();
            if (handlerArray != null && handlerArray.length > 0)
                return hasForcesHandle(handlerArray, jwt_me);

            handlerArray = auth.hasRoles();
            return hasRolesHandle(handlerArray, jwt_me);
        }
        // 没有这个注解直接放行
        log.debug("没有这个注解直接放行");
        return true;
    }

    /**
     * 优先级第一--并关系权限处理方案
     *
     * @param withForces
     * @param me
     * @return
     */
    private boolean withForcesHandle(String[] withForces, IJwtAble me) {
        log.debug("Enter into withForcesHandle ...");
        List<String> forces = me.getForces();
        if (null != forces && forces.size() > 0 && forces.containsAll(Arrays.asList(withForces))) return true;
        return false;
    }

    /**
     * 优先级第二--并关系角色处理方案
     *
     * @param withRoles
     * @param me
     * @return
     */
    private boolean withRolesHandle(String[] withRoles, IJwtAble me) {
        log.debug("Enter into withRolesHandle ...");
        List<String> roles = me.getRoles();
        if (null != roles && roles.containsAll(Arrays.asList(withRoles))) return true;
        return false;
    }

    /**
     * 优先级第三--或关系权限处理方案
     *
     * @param hasForces
     * @param me
     * @return
     */
    private boolean hasForcesHandle(String[] hasForces, IJwtAble me) {
        log.debug("Enter into hasForcesHandle ...");
        List<String> forces = me.getForces();
        for (String force : hasForces)
            if (forces.contains(force)) {
                return true;
            }
        return false;
    }

    /**
     * 优先级第四--或关系角色处理方案
     *
     * @param hasRoles
     * @param me
     * @return
     */
    private boolean hasRolesHandle(String[] hasRoles, IJwtAble me) {
        log.debug("Enter into hasRolesHandle ...");
        List<String> roles = me.getRoles();
        for (String role : hasRoles)
            if (roles.contains(role)) {
                return true;
            }
        return false;
    }

}
