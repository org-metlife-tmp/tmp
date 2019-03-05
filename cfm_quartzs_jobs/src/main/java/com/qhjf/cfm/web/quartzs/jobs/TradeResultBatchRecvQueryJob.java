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
        return "批量收付状态查询";
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
        List<Record> result = null;

        //Db.find的入参为查询多少个处理中的批次
        List<Record> sourceList = Db.find(Db.getSql("batchrecvjob.getTradeResultBatchQrySourceList"));

        if (null != sourceList && sourceList.size() > 0) {
            result = new ArrayList<>();

            for (Record source : sourceList) {
            	Date initSendTime = source.getDate("init_send_time");
                Calendar c = Calendar.getInstance();
                c.setTime(initSendTime);
                c.add(Calendar.MINUTE, 10);
                //指令发出后10分钟才能查询指令状态
                if (c.getTime().compareTo(new Date()) < 0 ) {
                	source.set("bank_cnaps_code", source.getStr("recv_bank_cnaps"));
                	result.add(source);
				}
            }
        }
        return result;
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
