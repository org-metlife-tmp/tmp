package com.qhjf.cfm.web.validates;

import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;

public abstract  class ParamsValidate implements ReqValidate {

    protected volatile  ParamsValidate innerVld ;

    protected ParamsValidate(ParamsValidate innerVld){
        this.innerVld = innerVld;
    }


    protected  final JSONObject getParams(JSONObject jobj) throws BusinessException {
        if (jobj != null){
            try{
                return jobj.getJSONObject("params");
            }catch(ClassCastException e){
                throw new ReqDataException("参数的格式不正确！");
            }
        }
        return null;
    }
}
