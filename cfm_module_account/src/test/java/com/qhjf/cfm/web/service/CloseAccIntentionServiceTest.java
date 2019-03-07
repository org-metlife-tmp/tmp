package com.qhjf.cfm.web.service;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by zhangsq on 2018/6/26.
 */
public class CloseAccIntentionServiceTest {

    private static Set<String> set = new HashSet<String>(){{
        add("acc_no");
        add("acc_name");
        add("acc_attr");
        add("acc_purpose");
        add("open_date");
        add("lawfull_man");
        add("cancel_date");
        add("curr_id");
        add("bank_cnaps_code");
        add("org_id");
        add("interactive_mode");
        add("memo");
        add("is_activity");
        add("is_virtual");
        add("status");
    }};

    @Test
    public void todochg() throws Exception {
        DruidPlugin dp = new DruidPlugin("jdbc:mysql://172.100.1.199:3306/corpzone_sunlife","cfm","cfm");
        dp.start();
        ActiveRecordPlugin arp = new ActiveRecordPlugin(dp.getDataSource());
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
        arp.addSqlTemplate("cai.sql");
        arp.addSqlTemplate("caf.sql");
        arp.setDevMode(true);
        arp.setShowSql(true);
        arp.start();

        Record record = new Record();
        record.set("create_by", 1);
        record.set("org_id", 1);
        record.set("user_id",1);
        SqlPara sqlPara = Db.getSqlPara("getTodoPage", Kv.by("map", record.getColumns()));
        System.out.println(sqlPara);
//        List<Record> list = new ArrayList<>();
//        for(int i = 3;i<99;i++){
//            Record r = new Record();
//            for (String s : set) {
//                if("acc_no".equals(s)
//                        || "acc_name".equals(s)
//                        || "acc_attr".equals(s)
//                        || "acc_purpose".equals(s)
//                        || "lawfull_man".equals(s)){
//                    r.set(s,""+i+i+i);
//                }else if("open_date".equals(s) || "cancel_date".equals(s)){
//                    r.set(s,new Date());
//                }
//            }
//            r.set("curr_id",1);
//            r.set("bank_cnaps_code","001100011002");
//            r.set("org_id",1);
//            r.set("interactive_mode",1);
//            r.set("is_activity",1);
//            r.set("status",1);
//            list.add(r);
//        }
//        Db.batchSave("account",list,list.size());
        arp.stop();
        dp.stop();
    }

}