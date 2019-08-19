package com.qhjf.cfm.web.service;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;

public class TestEnv {

    public static void setup(DruidPlugin dp , ActiveRecordPlugin ap){
        dp = new DruidPlugin("jdbc:mysql://10.1.1.2:3306/corpzone_sunlife", "cfm", "cfm");
        ap = new ActiveRecordPlugin(dp);
        ap.getEngine().setSourceFactory(new ClassPathSourceFactory());
        ap.addSqlTemplate("comm_cfm.sql");
        dp.start();
        ap.start();
    }
}
