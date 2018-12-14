package com.qhjf.cfm.web.webservice.ann;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
public @interface FieldValidate {
	
	//是否可以为空
    boolean nullable() default false;

    //是否需要setvalue
    boolean setable() default  true;

    //是否是唯一序列标识
    boolean isSerialno() default  false;

    //是否是唯一用户标识
    boolean isUserIdentity() default  false;


    //内嵌list的类
    Class nestClass() default Object.class;

    //最大长度
    int maxLength() default 0;

    //最小长度
    int minLength() default 0;

    //自定义正则验证
    String regexExpression() default "";

    //参数或者字段描述,这样能够显示友好的异常信息
    String description() default "";

}
