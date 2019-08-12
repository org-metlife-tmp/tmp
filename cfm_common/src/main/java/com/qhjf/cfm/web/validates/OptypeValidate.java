package com.qhjf.cfm.web.validates;

import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqValidateException;

public class OptypeValidate implements ReqValidate {

    private String optype;

    private ParamsValidate innerVld ;

    public OptypeValidate(String optype) {
        this.optype = optype;
    }

    public OptypeValidate(String optype, ParamsValidate innerVld) {
        this.optype = optype;
        this.innerVld = innerVld;
    }


    @Override
    public boolean validate(JSONObject jobj) throws BusinessException {
        String jobj_optype = getObjOptype(jobj);
        if(jobj_optype != null && jobj_optype.equals(optype)){
            return true && (innerVld!=null ? innerVld.validate(jobj) : true);
        }
        throw new ReqValidateException("optype不正确");
    }


    private final String getObjOptype(JSONObject jobj){
        if (jobj != null){
            return jobj.getString("optype");
        }
        return null;
    }

    public ParamsValidate getInnerVld() {
        return innerVld;
    }

    public void setInnerVld(ParamsValidate innerVld) {
        this.innerVld = innerVld;
    }


}
