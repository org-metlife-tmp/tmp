package com.qhjf.cfm.web.validates;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqValidateException;
import com.qhjf.cfm.web.constant.WebConstant;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class WebConstantParamsValidate extends ParamsValidate {

    private String filed;

    private Class<? extends WebConstant> cls;

    public WebConstantParamsValidate(String filed, Class<? extends WebConstant> cls) {
        super(null);
        this.filed = filed;
        this.cls = cls;
    }

    public WebConstantParamsValidate(String filed, Class<? extends WebConstant> cls, ParamsValidate innerVld) {
        super(innerVld);
        this.filed = filed;
        this.cls = cls;
    }

    @Override
    public boolean validate(JSONObject jobj) throws BusinessException {
        JSONObject params = getParams(jobj);
        if(jobj != null){
            Integer value =TypeUtils.castToInt(params.get(filed));
            if(innerValidate(value)){
                return true && (innerVld != null ? innerVld.validate(jobj) : true);
            }else{
                throw new ReqValidateException("请求体params参数["+filed+"]设置有误！");
            }
        }else{
            return true;
        }
    }

    private boolean innerValidate(Integer value) throws BusinessException{
        if(value != null){
            try {
                Method method = cls.getMethod("values");
                WebConstant[] constants = (WebConstant[])method.invoke(null, null);
                for (WebConstant constant : constants) {
                    if(constant.getKey() == value ){
                        return true;
                    }
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }else{
            return true;
        }
        return false;
    }
}
