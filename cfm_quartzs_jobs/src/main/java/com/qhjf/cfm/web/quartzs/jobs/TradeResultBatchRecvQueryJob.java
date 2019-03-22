package com.qhjf.cfm.web.quartzs.jobs;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.impl.SysTradeResultRecvBatchQueryInter;
import com.qhjf.cfm.web.quartzs.jobs.pub.PubJob;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * 实现原子接口：代收
 * @Auther: cht
 * @Date: 2019/2/27
 * @Description:
 */
public class TradeResultBatchRecvQueryJob extends PubJob {
    private static Logger log = LoggerFactory.getLogger(PubJob.class);

    @Override
    public String getJobCode() {
        return "TradeResultBatchRecvQuery";
    }

    @Override
    public String getJobName() {
        return "批量收明细查询";
    }

    @Override
    public Logger getLog() {
        return log;
    }


    @Override
    public Record getOldInstr(Record currInstr) {
        String reqnbr = currInstr.getStr("reqnbr");
        return Db.findFirst(Db.getSql("batchrecvjob.getOldBatchTrade")
                , currInstr.getStr("bank_serial_number")
                , reqnbr);
    }

    @Override
    public List<Record> getSourceDataList() throws JobExecutionException {
        return Db.find(Db.getSql("batchrecvjob.getTradeResultBatchQrySourceList"));
    }

    @Override
    public String getInstrTableName() {
        //批量收付查询状态指令表
        return "batch_recv_instr_queue_lock";
    }

    @Override
    public ISysAtomicInterface getSysInter() {
        return new SysTradeResultRecvBatchQueryInter();
    }

    @Override
    public boolean needReTrySaveInstr() {
        return true;
    }
}
