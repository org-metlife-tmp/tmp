package com.qhjf.cfm.web.constant;

public interface MenuInfo {


    enum DataManagement implements MenuInfo {
        BasicDataMgt("基础数据维护"),
        SettAccMgt("结算账户设置"),
        RouteMgt("路由设置"), ChannelMgt("渠道设置"), MerchMgt("商户号设置");

        String disp_name;

        DataManagement(String name) {
            this.disp_name = name;
        }

        public String getDisp_name() {
            return disp_name;
        }
    }
}
