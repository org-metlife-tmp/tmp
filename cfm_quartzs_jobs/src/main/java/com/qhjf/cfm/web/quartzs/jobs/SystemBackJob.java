package com.qhjf.cfm.web.quartzs.jobs;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.DateFormatThreadLocal;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.service.CheckVoucherService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 柜面收核心系统回调成功的 生成凭证
 */

public class SystemBackJob implements Job {

    private static Logger log = LoggerFactory.getLogger(SystemBackJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("【柜面收核心系统回调成功的---begin】");
        List<Record> find = Db.find(Db.getSql("auto_recv_counter.confirm_novoucher_list"));
        for(int i=0; i<find.size(); i++){
            final Record rec = find.get(i);
            boolean flag = Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {
                    if(CommonService.updateRows("recv_counter_bill",
                            new Record().set("is_sunvouder", 1),
                            new Record().set("id", rec.get("id"))) == 1){
                        log.info("自动任务保单号===" + rec.get("insure_bill_no") + "生成凭证---begin");
                        try {
                            CheckVoucherService.gmsConfirmVoucher(WebConstant.SftOsSource.getSftOsSource(TypeUtils.castToInt(rec.get("source_sys"))).getDesc(),
                                    rec
                            );
                        } catch (BusinessException e) {
                            log.error("自动任务保单号===" + rec.get("insure_bill_no") + "生成凭证失败");
                            e.printStackTrace();
                            return false;
                        }
                        log.info("自动任务保单号===" + rec.get("insure_bill_no") + "生成凭证---end");
                    }
                    return true;
                }
            });
        }

        log.info("【柜面收核心系统回调成功的---end】");
    }

}
