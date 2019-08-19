package com.qhjf.cfm.web.validates;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqValidateException;
import com.qhjf.cfm.web.constant.WebConstant;

public class UdopsValidate extends RequiredParamsValidate {

    public UdopsValidate() {
        super(new String[]{"udops"});

    }


    public boolean validate(JSONObject jobj) throws BusinessException {
        boolean result = super.validate(jobj);
        if (result) {
            JSONArray udops = getParams(jobj).getJSONArray("udops");
            if (udops != null && udops.size() > 0) {
                JSONObject[] udop_items = udops.toArray(new JSONObject[0]);
                for (JSONObject udop_item : udop_items) {
                    Integer is_default = TypeUtils.castToInt(udop_item.get("is_default"));
                    if (is_default != null && is_default == WebConstant.YesOrNo.YES.getKey()) {
                        return result;
                    }

                }
                throw new ReqValidateException("请设定一个默认的所属机构部门！");
            } else {
                throw new ReqValidateException("请设置所属机构部门");
            }
        }

        return result;
    }
}
