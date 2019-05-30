package com.qhjf.cfm.web.quartzs.jobs;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.DateFormatThreadLocal;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.service.CheckVoucherService;
import org.joda.time.DateTime;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

/**
 * 冲销凭证
 */

public class ChargeOffJob implements Job {

    private static Logger log = LoggerFactory.getLogger(ChargeOffJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.debug("【冲销凭证---begin】");
        /**
         * 下一财务月第一天,自动对已提交交易做冲销处理
         */
        //判断今天是否为财务月的下一天
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.DATE, -1);

        Record calRecord = Db.findFirst(Db.getSql("charge_off_cfm.getcalbydate"),
                DateFormatThreadLocal.format("yyyy-MM-dd",cal.getTime()));
        if(calRecord != null){
            boolean flag = Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    //查询所有预提交交易
                    List<Record> tradList = Db.find(Db.getSql("charge_off_cfm.chargeofflist"), new Date());
                    try {
                        //生成凭证信息
                        CheckVoucherService.sunVoucherData(tradList, WebConstant.MajorBizType.CWCX.getKey());
                    } catch (BusinessException e) {
                        e.printStackTrace();
                        return false;
                    }
                    return true;
                }
            });
        }else{
            log.debug("【不是财务月下一天或者无已提交的交易】");
        }

        log.debug("【冲销凭证---end】");
    }

}
