package com.qhjf.cfm.web.validates;

import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqValidateException;
import com.qhjf.cfm.utils.CommKit;

public class RequiredParamsValidate extends ParamsValidate {

    private String[] requireFields ;

    public RequiredParamsValidate(String[] requireFields) {
        super(null);
        this.requireFields = requireFields;
    }

    public RequiredParamsValidate(String[] requireFields, ParamsValidate innerVld) {
        super(innerVld);
        this.requireFields = requireFields;
    }

    @Override
    public boolean validate(JSONObject jobj) throws BusinessException {
        JSONObject params = getParams(jobj);
        if(params != null){
            for (String requireField : requireFields) {
                if(CommKit.isNullOrEmpty(params.get(requireField))){
                    throw new ReqValidateException("请求体params参数["+requireField+"]不能为空！");
                }
            }
            return true && (innerVld != null ? innerVld.validate(jobj) : true);
        }
        throw new ReqValidateException("请求体params为空！");
    }
}
