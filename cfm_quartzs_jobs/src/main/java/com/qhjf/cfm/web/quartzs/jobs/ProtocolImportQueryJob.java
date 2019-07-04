package com.qhjf.cfm.web.quartzs.jobs;

import java.util.List;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.impl.SysProtocolImportQueryInter;
import com.qhjf.cfm.web.quartzs.jobs.pub.PubJob;

public class ProtocolImportQueryJob extends PubJob {
	private static Logger log = LoggerFactory.getLogger(ProtocolImportQueryJob.class);

	@Override
	public String getJobCode() {
		return "ProtocolImportQuery";
	}

	@Override
	public String getJobName() {
		return "批收协议上送指令结果查询";
	}

	@Override
	public Logger getLog() {
		return log;
	}

	@Override
	public List<Record> getSourceDataList() throws JobExecutionException {
		return Db.find(Db.getSql("batchrecvjob.queryProtocolTotal"));
	}

	@Override
	public String getInstrTableName() {
		return "protocol_import_instr_query_lock";
	}

	@Override
	public ISysAtomicInterface getSysInter() {
		return new SysProtocolImportQueryInter();
	}

	@Override
	public Record getOldInstr(Record currInstr) {
		return Db.findFirst(Db.getSql("batchrecvjob.queryOldProtocolImportQueryLock"),
				currInstr.getLong("bank_seriral_no"), currInstr.getStr("bank_cnaps_code"));
	}

	@Override
	public boolean needReTrySaveInstr() {
		return true;
	}
}
