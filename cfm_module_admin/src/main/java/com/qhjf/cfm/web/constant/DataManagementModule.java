package com.qhjf.cfm.web.constant;

public class DataManagementModule extends ModuleInfo<MenuInfo.DataManagement> {

    public DataManagementModule(){
        this.moduleName = "数据管理";
        this.moduleCode = ModuleName.DMMGT.name();
    }

    @Override
    public String getMenuName(String menuCode) {
        for (MenuInfo.DataManagement dataManagement : MenuInfo.DataManagement.values()) {
            if (dataManagement.name().equals(menuCode)){
                return dataManagement.getDisp_name();
            }
        }
        return "";
    }
}
