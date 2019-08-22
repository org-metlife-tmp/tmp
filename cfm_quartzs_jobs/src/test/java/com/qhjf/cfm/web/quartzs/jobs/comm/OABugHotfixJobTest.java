package com.qhjf.cfm.web.quartzs.jobs.comm;

import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.quartz.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OABugHotfixJobTest {

    private static DruidPlugin dp ;
    private static ActiveRecordPlugin arp;

    @Before
    public void setup(){
        dp = new DruidPlugin("jdbc:sqlserver://10.1.1.2:1433;DatabaseName=corpzone_pretest" , "sa","Admin123");

        Slf4jLogFilter slf4jLogFilter = new Slf4jLogFilter();
        slf4jLogFilter.setConnectionLogEnabled(false);
        slf4jLogFilter.setStatementLogEnabled(true);
        slf4jLogFilter.setStatementLogErrorEnabled(true);
        slf4jLogFilter.setResultSetLogEnabled(false);
        dp.addFilter(slf4jLogFilter);
        arp = new ActiveRecordPlugin(dp);
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
        arp.setDevMode(true);
        arp.setShowSql(true);

        arp.addSqlTemplate("/sql/sqlserver/oa_hotfix_cfm.sql");
        dp.start();
        arp.start();

    }

    @Test
    public void run(){
        OABugHotfixJob job = new OABugHotfixJob();
        JobExecutionContext context = new JobExecutionContext() {
            @Override
            public Scheduler getScheduler() {
                return null;
            }

            @Override
            public Trigger getTrigger() {
                return null;
            }

            @Override
            public Calendar getCalendar() {
                return null;
            }

            @Override
            public boolean isRecovering() {
                return false;
            }

            @Override
            public TriggerKey getRecoveringTriggerKey() throws IllegalStateException {
                return null;
            }

            @Override
            public int getRefireCount() {
                return 0;
            }

            @Override
            public JobDataMap getMergedJobDataMap() {
                Map params = new HashMap<Object, Object>();
                params.put("start","2019-03-01");
                params.put("end","2019-03-20");
                return new JobDataMap(params);
            }

            @Override
            public JobDetail getJobDetail() {
                return null;
            }

            @Override
            public Job getJobInstance() {
                return null;
            }

            @Override
            public Date getFireTime() {
                return null;
            }

            @Override
            public Date getScheduledFireTime() {
                return null;
            }

            @Override
            public Date getPreviousFireTime() {
                return null;
            }

            @Override
            public Date getNextFireTime() {
                return null;
            }

            @Override
            public String getFireInstanceId() {
                return null;
            }

            @Override
            public Object getResult() {
                return null;
            }

            @Override
            public void setResult(Object result) {

            }

            @Override
            public long getJobRunTime() {
                return 0;
            }

            @Override
            public void put(Object key, Object value) {

            }

            @Override
            public Object get(Object key) {
                return null;
            }
        };
        try {
            job.execute(context);
        } catch (JobExecutionException e) {
            e.printStackTrace();
        }

    }


    @After
    public void shutdown(){
        dp.stop();
        arp.stop();
    }

}