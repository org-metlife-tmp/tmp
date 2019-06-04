package com.qhjf.cfm.web.quartzs.jobs;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
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
 * 针对撤销，但是未复核的信息，8点前进行更新状态为已提交
 */

public class ChargeOffConfirmJob implements Job {

    private static Logger log = LoggerFactory.getLogger(ChargeOffConfirmJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.info("【撤销未复核更新状态---begin】");
        /**
         * 每天八点执行
         */
        final List<Record> tradlist = Db.find(Db.getSqlPara("voucher.findConfirmCancelTrad"));
        if(tradlist==null || tradlist.size()==0){
            log.info("未找到撤销未复核的数据！");
        }

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                for(Record r : tradlist){
                    if(CommonService.updateRows("acc_his_transaction_ext",
                            new Record().set("precondition", WebConstant.PreSubmitStatus.YYT.getKey()),
                            new Record().set("id", r.getLong("extId"))) != 1){
                        log.debug("【撤销未复核id为"+r.getLong("extId")+"的更新失败】");
                    }
                }
                return true;
            }
        });

        log.debug("【撤销未复核更新状态---end】");
    }

}
