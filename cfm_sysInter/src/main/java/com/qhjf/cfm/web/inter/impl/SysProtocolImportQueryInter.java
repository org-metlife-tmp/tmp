package com.qhjf.cfm.web.inter.impl;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.impl.batch.SysBatchRecvInter;

public class SysProtocolImportQueryInter implements ISysAtomicInterface {

	private static Logger log = LoggerFactory.getLogger(SysProtocolImportQueryInter.class);
	private IMoreResultChannelInter channelInter;
	private Record instr;

	@Override
	public Record genInstr(Record record) {
		Record result = new Record();
		result.set("bank_seriral_no", record.getStr("bank_seriral_no"));
		result.set("bank_cnaps_code", record.getStr("bank_cnaps_code"));
		result.set("query_date", new Date());
		result.set("pre_query_time", new Date());

		this.instr = result;
		return result;
	}

	@Override
	public Record getInstr() {
		return this.instr;
	}

	@Override
	public void callBack(final String jsonStr) throws Exception {
		Db.delete("protocol_import_instr_query_lock", instr);

		int resultCount = channelInter.getResultCount(jsonStr);
		Long baseId = null;// 主表主键
		for (int i = 0; i < resultCount; i++) {

			Record rd = channelInter.parseResult(jsonStr, i);
			String bankSeriralNo = rd.getStr("bank_seriral_no");
			String packageSeq = rd.getStr("package_seq");
			Record detail = Db.findFirst(Db.getSql("batchrecv.qryProtocolDetail"), bankSeriralNo, packageSeq);
			if (detail == null) {
				log.error("批收协议导入状态查询，通过bankSeriralNo={}，packageSeq={}未查询到明细！", bankSeriralNo, packageSeq);
				continue;
			}
			if (baseId == null) {
				baseId = detail.getLong("base_id");
			}
			detail.set("dead_line", rd.getStr("dead_line"));
			detail.set("status", TypeUtils.castToInt(rd.get("status")));
			detail.set("bank_err_code", rd.getStr("bank_err_code"));
			detail.set("bank_err_msg", rd.getStr("bank_err_msg"));
			boolean update = Db.update("protocol_import_instr_detail", detail);
			if (!update) {
				log.error("批收协议导入状态查询，更新明细失败！bankSeriralNo={}，packageSeq={}", bankSeriralNo, packageSeq);
			}
		}
		// 回写主表
		writetBack(baseId);
	}

	private void writetBack(Long baseId) {
		Record r = Db.findFirst(Db.getSql("batchrecv.qryHandllingProtocolSize"), baseId);
		if (r != null && r.getInt("size") == 0) {
			//回写主表
			Record total = Db.findById("protocol_import_instr_total", baseId);
			total.set("status", 1);
			boolean update = Db.update("protocol_import_instr_total", total);
			if (update) {
				//发送批收指令
				sendRecvInstr(total);
			}
		}
	}
	
	/**
	 * 发送批收指令
	 * 
	 * @param total
	 */
	private void sendRecvInstr(Record total) {
		String cnaps = total.getStr("cnaps");
		
		Record instrTotal = Db.findById("batch_recv_instr_queue_total", total.getInt("instr_total_id"));
		List<Record> instrDetail = Db.find(Db.getSql("batchrecv.findInstrDetailByBaseId"), instrTotal.getInt("id"));

		SysBatchRecvInter recvInter = new SysBatchRecvInter();
		Record set = new Record().set("total", instrTotal).set("detail", instrDetail);
		recvInter.setInnerInstr(set);
		IChannelInter channelInter = null;
		try {
			channelInter = ChannelManager.getInter(cnaps, "BatchRecv");
		} catch (Exception e) {
			log.error("获取银行原子接口失败！", e);
		}
		recvInter.setChannelInter(channelInter);

		QueueBean bean = new QueueBean(recvInter, channelInter.genParamsMap(recvInter.getInstr()), cnaps);
		ProductQueue productQueue = new ProductQueue(bean);
		new Thread(productQueue).start();
	}

	@Override
	public void callBack(Exception e) throws Exception {
		Db.delete("protocol_import_instr_query_lock", instr);
	}

	@Override
	public void setChannelInter(IChannelInter channelInter) {
		this.channelInter = (IMoreResultChannelInter) channelInter;
	}

	@Override
	public IChannelInter getChannelInter() {
		return this.channelInter;
	}

}
