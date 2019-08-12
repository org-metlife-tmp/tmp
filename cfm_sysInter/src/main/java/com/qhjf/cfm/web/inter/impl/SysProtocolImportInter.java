package com.qhjf.cfm.web.inter.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.qhjf.cfm.exceptions.EncryAndDecryException;
import com.qhjf.cfm.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.exceptions.BankInterfaceException;
import com.qhjf.cfm.queue.ProductQueue;
import com.qhjf.cfm.queue.QueueBean;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.config.DDHLARecvConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.impl.batch.SysBatchRecvInter;

/**
 * 收款协议导入
 * 
 * @author CHT
 *
 */
public class SysProtocolImportInter implements ISysAtomicInterface {
	private static DDHLARecvConfigSection recvSection = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLaRecv);
	private static Logger log = LoggerFactory.getLogger(SysProtocolImportInter.class);
	private IMoreResultChannelInter channelInter;
	private Record instr;
	private boolean retryFlag = false;// 异常重发
	private String cnaps;

	@Override
	public Record genInstr(Record record) {
		@SuppressWarnings("unchecked")
		List<Record> batchDetailList = (List<Record>) record.get("batchDetailList");
		if (batchDetailList == null || batchDetailList.isEmpty()) {
			return null;
		}

		Long instrTotalId = record.getLong("instrTotalId");
		Long batchTotalId = record.getLong("batchTotalId");
		this.cnaps = record.getStr("cnaps");
		this.instr = record.set("total", genTotal(batchDetailList.size(), instrTotalId, batchTotalId));

		List<Record> detailList = new ArrayList<>();
		int index = 1;
		for (Record r : batchDetailList) {
			Record detail = new Record();
			detail.set("package_seq", index++);
			// 缴费编号=保单号
			detail.set("pay_no", r.getStr("insure_bill_no"));
			detail.set("pay_acc_name", r.getStr("pay_acc_name"));
			//付方账号做解密处理以便后续发送给银行端
			String pay_acc_no = "";
			try {
				pay_acc_no = SymmetricEncryptUtil.getInstance().decryptToStr(r.getStr("pay_acc_no"));
			} catch (EncryAndDecryException e) {
				log.error("SysProtocolImportInter.genInstr：付方账号解密失败！", e);
			}
			detail.set("pay_acc_no", pay_acc_no);
			detail.set("cert_type", r.getStr("pay_cert_type"));
			detail.set("cert_no", r.getStr("pay_cert_code"));
			detail.set("dead_line", genDeadLine());

			detailList.add(detail);
		}
		this.instr.set("detail", detailList);
		return this.instr;
	}

	/**
	 * 协议过期日期生成
	 * 
	 * @return
	 */
	private String genDeadLine() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.YEAR, 70);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		return sdf.format(c.getTime());
	}

	/**
	 * 保存导入协议指令数据
	 * 这里的导入为新加的，需要修改，只需添加不存在的导入协议即可，但现在因循环的关系导致存在的数据也被导入
	 * @param r
	 */
	public void seveInfo(Record r,Record total){
		r.set("acct_name",total.getStr("enterprise_name"));
		r.set("acct_no",total.getStr("enterprise_acc_no"));
		r.set("contract_no",total.getStr("protocol_no"));
		r.set("pay_type",total.getStr("pay_type"));
		r.set("tmp_1",total.getStr("id"));
		r.set("pay_acct_no",r.getStr("pay_acc_no"));
		r.set("pay_acct_name",r.getStr("pay_acc_name"));
		r.remove("package_seq");
		r.remove("pay_acc_no");
		r.remove("pay_acc_name");
		Db.save("protocol_import_info", r);
	}
	@Override
	public Record getInstr() {
		return this.instr;
	}

	@Override
	public void callBack(String jsonStr) throws Exception {
		CommKit.debugPrint(log, "收费协议上送成功，jsonStr={}", jsonStr);
		Record total = (Record) instr.get("total");

		int resultCount = channelInter.getResultCount(jsonStr);
		for (int i = 0; i < resultCount; i++) {
			Record rd = channelInter.parseResult(jsonStr, i);
			String status = rd.getStr("status");
			String errCode = rd.getStr("bank_err_code");
			String errmsg = rd.getStr("bank_err_msg");
			if ("1".equals(rd.getStr("isFileReturn"))) {
				// 回写指令头表
				CommonService.update(
						"protocol_import_instr_total", new Record().set("status", status).set("err_code", errCode)
								.set("err_msg", errmsg).set("resp_time", new Date()),
						new Record().set("id", total.getInt("id")));

			} else {
				// 回写指令子表
				CommonService.update("protocol_import_instr_detail",
						new Record().set("status", status).set("bank_err_code", errCode).set("bank_err_msg", errmsg),
						new Record().set("id", total.getInt("id")).set("package_seq", rd.getInt("package_seq")));
			}
		}

	}

	@Override
	public void callBack(Exception e) throws Exception {
		if (e instanceof BankInterfaceException) {
			if (this.retryFlag == false) {
				log.error("批收协议导入指令重发开始...");
				QueueBean bean = new QueueBean(this, channelInter.genParamsMap(instr), this.cnaps);
				ProductQueue productQueue = new ProductQueue(bean);
				new Thread(productQueue).start();
				this.retryFlag = true;
			}
		} else {
			log.error("发送扣费协议上送指令失败，{}", instr.get("total"));
		}
	}

	@Override
	public void setChannelInter(IChannelInter channelInter) {
		this.channelInter = (IMoreResultChannelInter) channelInter;
	}

	@Override
	public IChannelInter getChannelInter() {
		return this.channelInter;
	}

	private Record genTotal(int num, Long instrTotalId, Long batchTotalId) {
		Record result = new Record();
		result.set("instr_total_id", instrTotalId);
		result.set("batch_total_id", batchTotalId);
		result.set("bank_seriral_no", RedisSericalnoGenTool.genBankSeqNo());
		result.set("total_num", num);
		result.set("protocol_no", recvSection.getProtocolNo());
		result.set("pay_type", recvSection.getPayType());
		result.set("enterprise_name", recvSection.getEnterpriseName());
		result.set("enterprise_acc_no", recvSection.getEnterpriseAccNo());
		result.set("cnaps", this.cnaps);
		result.set("send_time", new Date());

		return result;
	}

	public boolean seveInstr() {
		if (this.instr == null) {
			log.error("生成的协议导入指令为空");
			return false;
		}
		Record total = (Record) this.instr.get("total");
		boolean save = Db.save("protocol_import_instr_total", total);

		if (save) {
			@SuppressWarnings("unchecked")
			List<Record> detail = (List<Record>) this.instr.get("detail");
			for (Record record : detail) {
				record.set("base_id", total.get("id"));
			}

			int[] batchSave = Db.batchSave("protocol_import_instr_detail", detail, 1000);
			return ArrayUtil.checkDbResult(batchSave);
		}

		log.error("批收协议导入时，保存指令汇总表失败！");
		return false;
	}
	
}
