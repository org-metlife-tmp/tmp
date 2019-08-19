package com.qhjf.cfm.web.service;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;


public class AreaQueryServiceTest {

    private static DruidPlugin dp;
    private static ActiveRecordPlugin ap;
    private static AreaQueryService me = new AreaQueryService();


    @BeforeClass
    public static void setup(){
        TestEnv.setup(dp,ap);
    }



    @Test
    public void allTopLeveTest(){
        Record re = new Record();
        List<Record> li =  me.getAllTopLevel(re);
        System.out.println(li);
        org.junit.Assert.assertTrue(li.size() > 0);
        re.set("query_key","上海");
        li = me.getAllTopLevel(re);
        System.out.println(li);
        org.junit.Assert.assertTrue(li.size() == 1);
        re.set("query_key","sh");
        li = me.getAllTopLevel(re);
        System.out.println(li);
        org.junit.Assert.assertTrue(li.size() == 1);
        re.set("query_key","shanghai");
        li = me.getAllTopLevel(re);
        System.out.println(li);
        org.junit.Assert.assertTrue(li.size() == 1);
    }

    @Test
    public void areaList(){
        Record re = new Record();
        List<Record> li =  me.getAreaList(re);
        System.out.println(li.size());
        org.junit.Assert.assertTrue(li.size() > 0);

        re.set("top_super", "上海");
        li = me.getAreaList(re);
        System.out.println(li.size());
        org.junit.Assert.assertTrue(li.size() == 1);


        re.set("top_super", "河北");
        li = me.getAreaList(re);
        System.out.println(li.size());
        org.junit.Assert.assertTrue(li.size() > 1);


        re.set("top_super", "河北");
        re.set("query_key","石家庄");
        li = me.getAreaList(re);
        System.out.println(li.size());
        org.junit.Assert.assertTrue(li.size() == 1);

        re.set("top_super", "河北");
        re.set("query_key","sjz");
        li = me.getAreaList(re);
        System.out.println(li.size());
        org.junit.Assert.assertTrue(li.size() >= 1);
    }

}