package com.qhjf.cfm.web.service;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class BankQueryServiceTest {

    private static DruidPlugin dp;
    private static ActiveRecordPlugin ap;
    private static BankQueryService me = new BankQueryService();

    @BeforeClass
    public static void setup() {
        TestEnv.setup(dp,ap);
    }

    @Test
    public void getBankType() {
        Record re = new Record();
        Record li =  me.getBankType(re);
        System.out.println(li);
        org.junit.Assert.assertTrue(li != null);

        re.set("query_key","gh");
        li =  me.getBankType(re);
        System.out.println(li);
        org.junit.Assert.assertTrue(li != null);
    }

    @Test
    public void getBankList() {
        Record re = new Record();
        List<Record> li =  me.getBankList(re);
        System.out.println(li.size());
        org.junit.Assert.assertTrue(li.size() > 0);

        re.set("bank_type","103");
        li =  me.getBankList(re);
        System.out.println(li.size());
        org.junit.Assert.assertTrue(li.size() > 0);


        re.set("bank_type","103");
        re.set("area_code", "1000");
        //re.set("query_key","gh");
        li =  me.getBankList(re);
        System.out.println(li.size());
        org.junit.Assert.assertTrue(li.size() > 0);


    }
}