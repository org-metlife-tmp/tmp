package com.qhjf.cfm.web.plugins.autoroute;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface ControllerBind {
    String controllerKey();

    String viewPath() default "";
}