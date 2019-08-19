package com.qhjf.cfm.web.validates;

import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.exceptions.BusinessException;

public interface ReqValidate {

    boolean validate(JSONObject jobj) throws BusinessException;
}
