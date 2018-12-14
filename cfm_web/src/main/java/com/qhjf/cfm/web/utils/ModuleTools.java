package com.qhjf.cfm.web.utils;

import com.qhjf.cfm.web.constant.DataManagementModule;
import com.qhjf.cfm.web.constant.ModuleInfo;

public class ModuleTools {

    public static ModuleInfo.ModuleName getModuleByName(String name){
        if(name != null && !"".equals(name)){
            for (ModuleInfo.ModuleName moduleName : ModuleInfo.ModuleName.values()) {
                if(moduleName.name().equals(name)){
                    return moduleName;
                }
            }
        }
        return null;
    }

    public static ModuleInfo getModuleInfoByName(ModuleInfo.ModuleName module){
        switch (module){
            case DMMGT:
                return new DataManagementModule();
            default:
                return null;
        }
    }
}
