package com.qhjf.cfm.web.validates;

import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.exceptions.BusinessException;

public class Optype {

    public static enum Mode{
        ADMIN, COMM, NORMAL;
        private String getPrefix(){
            return "/"+this.name().toLowerCase();
        }
    }

    /**
     * optype的名称
     */
    private String name ;

    /**
     * optype的类型
     */
    private Mode mode;

    /**
     * 传递到后台需要保留的params参数列表
     */
    private String[] keepParams;


    private OptypeValidate validate;

    public Optype(Mode mode,String name){
        this.mode = mode;
        this.name = name;
        this.validate = new OptypeValidate(this.name);
    }


    /**
     * 注册参数校验
     * @param innerVld
     * @return
     */
    public Optype registerValidate(ParamsValidate innerVld){
        this.validate.setInnerVld(innerVld);
        return this;
    }


    /**
     * 注册保留参数
     * @param keepParams
     * @return
     */
    public Optype registKeepParams(String[] keepParams){
        this.keepParams = keepParams;
        return this;
    }


    /**
     * 获取optype对应的路由
     * @return
     */
    public String getRoute(){
        return this.mode.getPrefix()+"/"+this.name.replaceAll("_","/");
    }


    public String getName() {
        return name;
    }

    public Mode getMode() {
        return mode;
    }

    public String[] getKeepParams() {
        return keepParams;
    }


    public boolean validateReq(JSONObject req) throws BusinessException {
        return this.validate.validate(req);
    }

    public String getKey(){
        return this.getMode().name()+"_"+this.getName();
    }
}
