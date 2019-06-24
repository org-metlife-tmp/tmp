package com.qhjf.test;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.plugin.redis.RedisPlugin;
import com.jfinal.plugin.redis.serializer.JdkSerializer;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.CfmRedisPlugin;
import com.qhjf.cfm.web.service.CheckVoucherService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SqlServerTest {
    DruidPlugin dp = null;
    RedisPlugin cfmRedis = null;
    ActiveRecordPlugin arp = null;

    @Before
    public void before() {
        dp = new DruidPlugin("jdbc:sqlserver://10.164.26.24:1433;DatabaseName=TreasureDB", "tmpadmin",
                "User123$");
        arp = new ActiveRecordPlugin(dp);
        arp.setDevMode(true);
        arp.setDialect(new SqlServerDialect());
        arp.setShowSql(true);
        arp.setBaseSqlTemplatePath(null);
        arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
        arp.addSqlTemplate("/sql/sqlserver/charge_off_cfm.sql");
        arp.addSqlTemplate("/sql/sqlserver/la_cfm.sql");
        arp.addSqlTemplate("/sql/sqlserver/la_recv_cfm.sql");
        arp.addSqlTemplate("/sql/sqlserver/common_cfm.sql");
        cfmRedis = new CfmRedisPlugin("cfm", "10.164.26.48");
        cfmRedis.setSerializer(new JdkSerializer());
        dp.start();
        arp.start();
        cfmRedis.start();
    }

    @After
    public void after() {
        if (null != arp) arp.stop();
        if (null != dp) dp.stop();
        if (null != dp) dp.stop();
    }

    //测试 nbdb.sql
    @Test
    public void testNbdp() throws Exception {
        /**
         * 下一财务月第一天,自动对已提交交易做冲销处理
         */
        //判断今天是否为财务月的下一天
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//        sdf.parse(sdf.format(cal.getTime()));
        Record calRecord = Db.findFirst(Db.getSql("charge_off_cfm.getcalbydate"),
                sdf.format(cal.getTime()));
        if(calRecord != null){
            boolean flag = Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    //查询所有预提交交易
                    List<Record> tradList = Db.find(Db.getSql("charge_off_cfm.chargeofflist"), new Date());
                    try {
                        //生成凭证信息
                        CheckVoucherService.sunVoucherData(tradList, WebConstant.MajorBizType.CWCX.getKey(), null);
                    } catch (BusinessException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
            });
        }else{
        }

    }

    private WebConstant.YesOrNo checkDoubtful(Record originData) throws Exception {

        Record checkDoubtful = new Record();

        Map<String, Object> columns = originData.getColumns();
        for (Map.Entry<String, Object> entry : columns.entrySet()) {
            String key = entry.getKey();
            if(key.equals("id")){
                checkDoubtful.set("origin_id", entry.getValue());
                continue;
            }
            if (key.equals("persist_version") || key.equals("source_sys") || key.equals("channel_code")) {
                continue;
            }
            checkDoubtful.set(key, entry.getValue());
        }
        String identification = MD5Kit.string2MD5(originData.getStr("insure_bill_no")
                + "_" +originData.getStr("recv_acc_name")
                + "_" +originData.getStr("amount"));

        checkDoubtful.set("identification", identification);
        checkDoubtful.set("is_doubtful", 0);

        Db.save("la_check_doubtful", checkDoubtful);

        /**
         * 根据保单号，收款人，金额查询合法表中是否存在数据，如果存在视为可疑数据，将合法表中的数据删除，更新可疑表数据状态为可疑
         */
        List<Record> legalRecordList = Db.find(Db.getSql("la_cfm.getpaylegal"),originData.getStr("insure_bill_no"),originData.getStr("recv_acc_name"),
                originData.getStr("amount"));
        if(legalRecordList!=null && legalRecordList.size()!=0){
            Record legalRecord = legalRecordList.get(0);
            //可疑数据
            CommonService.update("la_check_doubtful",
                    new Record().set("is_doubtful", 1),
                    new Record().set("id", checkDoubtful.getLong("id")));
            //删除合法表中数据和合法扩展表数据
            Db.deleteById("pay_legal_data","id",legalRecord.getLong("id"));
            Db.delete(Db.getSql("la_cfm.dellapaylegalext"),legalRecord.getLong("id"));
            CommonService.update("la_check_doubtful",
                    new Record().set("is_doubtful", 1),
                    new Record().set("origin_id", legalRecord.getLong("origin_id")));
            return WebConstant.YesOrNo.YES;

        }
        return WebConstant.YesOrNo.NO;
    }
    
}
