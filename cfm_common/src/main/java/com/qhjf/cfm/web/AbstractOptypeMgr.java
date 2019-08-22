package com.qhjf.cfm.web;

import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqValidateException;
import com.qhjf.cfm.web.validates.Optype;
import com.qhjf.cfm.web.validates.OptypeValidate;
import com.qhjf.cfm.web.validates.ReqValidate;
import com.qhjf.cfm.web.validates.RequiredParamsValidate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  optype管理器
 */
public abstract  class AbstractOptypeMgr {

    /**
     * 登录请求校验类
     */
    private final static ReqValidate loginVld = new OptypeValidate("login",
            new RequiredParamsValidate(new String[]{"login_name","password"}));

    protected static final Map<String, Optype> ALLOPTYPE = new HashMap<String, Optype>();


    protected List<Optype> optypes = new ArrayList<Optype>();


    public AbstractOptypeMgr(){

    }

    public abstract void registe();


    public AbstractOptypeMgr add(AbstractOptypeMgr optypeMgd){
        optypeMgd.registe();
        for (Optype optype : optypeMgd.optypes) {
            optypes.add(optype);
            if(ALLOPTYPE.containsKey(optype.getKey())){
                throw new RuntimeException(optype+" 重复定义！");
            }
            ALLOPTYPE.put(optype.getKey(),optype);
        }
        return this;
    }


    /**
     * 根据optype获取路由
     * @param str_optype
     * @return
     */
    public String getRoute(Optype.Mode mode, String str_optype){
        Optype optype = getOptype(mode, str_optype);
        if(optype != null){
            return optype.getRoute();
        }
        return null;
    }



    public Optype  getOptype(Optype.Mode mode, String str_optype){
        return ALLOPTYPE.get(mode.name()+"_"+str_optype);
    }


    /**
     * 验证登录的请求对象
     * @param obj
     * @return
     * @throws BusinessException
     */
    public boolean validareLoginReq(JSONObject obj) throws  BusinessException{
        return loginVld.validate(obj);
    }


    /**
     * 校验请求对象
     *
     * @param mode
     * @param obj
     * @throws BusinessException
     */
    public boolean validateReq(Optype.Mode mode, JSONObject obj) throws BusinessException {
        if(obj != null){
            String str_optype = obj.getString("optype");
            Optype optype = getOptype(mode,str_optype);
            if(optype != null){
                return optype.validateReq(obj);
            }else{
                throw new ReqValidateException(mode.name()+"模式下，optype["+str_optype+"] 未定义！");
            }

        }else{
            throw new ReqValidateException("请求参数为空！");
        }
    }









}
