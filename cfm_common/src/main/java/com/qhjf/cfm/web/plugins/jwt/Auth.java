package com.qhjf.cfm.web.plugins.jwt;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Auth {

    String[] withForces() default {};              // 需要的权限 满足全部才可以访问--优先级第一

    String[] withRoles()  default {};              // 满足的橘色 满足全部才可以访问--优先级第二

    String[] hasForces() default {};               // 需要的权限 满足一个就可以访问--优先级第三

    String[] hasRoles() default {};                // 满足的角色 满足一个就可以访问--优先级第四

}
