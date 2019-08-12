package com.qhjf.cfm.web.constant;

public abstract  class ModuleInfo<T extends MenuInfo> {

    public enum ModuleName{
        DMMGT;
    }

    /**
     * 模块名称
     */
    protected  String moduleName ;


    /**
     * 模块编号
     */
    protected  String moduleCode;


    public abstract  String getMenuName(String menuCode);



}
