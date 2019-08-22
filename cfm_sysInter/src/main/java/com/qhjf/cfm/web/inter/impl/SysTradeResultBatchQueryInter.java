package com.qhjf.cfm.web.inter.impl;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.TableDataCacheUtil;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;
import com.qhjf.cfm.web.config.PlfConfigAccnoSection;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.impl.batch.SysBatchPayInter;
import com.qhjf.cfm.web.inter.manager.SysInterManager;
import com.qhjf.cfm.web.webservice.sft.SftCallBack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Deprecated
public class SysTradeResultBatchQueryInter implements ISysAtomicInterface {

    private static Logger log = LoggerFactory.getLogger(SysTradeResultBatchQueryInter.class);
    private PlfConfigAccnoSection section = PlfConfigAccnoSection.getInstance();
    private IMoreResultChannelInter channelInter;
    private Record instr;

    @Override
    public Record genInstr(Record record) {
        this.instr = new Record();
        Date date = new Date();
        
        String cnaps = record.getStr("pay_bank_cnaps");
		String packageSeq = record.getStr("package_seq");
		String detailBankServiceNumber = record.getStr("detail_bank_service_number");
		//工行是批量查询，
		if (cnaps.startsWith("102")) {
			detailBankServiceNumber = "0";
			packageSeq = "0";
		}
		
        instr.set("bank_serial_number", record.getStr("bank_serial_number"));
        instr.set("detail_bank_service_number", detailBankServiceNumber);
        instr.set("package_seq", packageSeq);
        instr.set("source_ref", record.getStr("source_ref"));
        instr.set("bill_id", record.getLong("bill_id"));
        instr.set("process_bank_type", record.getStr("pay_bank_type"));
        instr.set("trade_date", record.get("trade_date"));
        instr.set("pre_query_time", DateKit.toDate(DateKit.toStr(date, DateKit.timeStampPattern)));
        return this.instr;
    }

    @Override
    public Record getInstr() {
        return this.instr;
    }

    @Override
    public void callBack(String jsonStr) throws Exception {
    	Db.delete("trade_result_query_batch_instr_queue_lock", instr);
    	
        log.debug("批量收付查询历史交易状态指令回写开始。。。instr={}", instr);
        int resultCount = channelInter.getResultCount(jsonStr);
        if (resultCount <= 0) {
			return;
		}
        
        Record instrTotalRecord = null;
		String sourceRef = null;
		final String billTable[] = new String[1];
		final String billTablePrimaryKey[] = new String[1];
        
		for (int i = 0; i < resultCount; i++) {
			final Record parseRecord = channelInter.parseResult(jsonStr, i);
			final int status = parseRecord.getInt("status");

			String bankServiceNumber = parseRecord.getStr("bank_service_number");
			String detailBankServiceNumber = parseRecord.getStr("detail_bank_service_number");
			String packageSeq = parseRecord.getStr("package_seq");
			if (null == bankServiceNumber && null == detailBankServiceNumber) {
				log.error("批量收付查询历史交易状态指令回写，第[{}]笔即没有返回bank_service_number，又没有返回detail_bank_service_number，跳过该笔回写！", i+1);
				continue;
			}
			
			if (null == instrTotalRecord) {
				//查询付款指令汇总信息表
				instrTotalRecord = SysBatchPayInter.findInstrTotal(bankServiceNumber, detailBankServiceNumber, packageSeq);
				sourceRef = instrTotalRecord.getStr("source_ref");
				billTable[0] = SysInterManager.getDetailTableName(sourceRef);
				billTablePrimaryKey[0] = SysInterManager.getSourceRefPrimaryKey(billTable[0]);
			}
			
			if (status == WebConstant.PayStatus.SUCCESS.getKey() || status == WebConstant.PayStatus.FAILD.getKey()) {
				//查询付款指令明细信息
				final Record instrDetailRecord = SysBatchPayInter.findInstryDetail(bankServiceNumber, detailBankServiceNumber, packageSeq);
				
				Db.tx(new IAtom() {
					@Override
					public boolean run() throws SQLException {
						Long detailId = TypeUtils.castToLong(instrDetailRecord.get("detail_id"));
						Record bill_setRecord = new Record();
						Record bill_whereRecord = new Record().set(billTablePrimaryKey[0], detailId);

						Record instr_setRecord = new Record();
						Record instr_whereRecord = new Record().set("id", instrDetailRecord.getLong("id"));

						if (status == WebConstant.PayStatus.SUCCESS.getKey()) {
							bill_setRecord.set( SysInterManager.getStatusFiled(billTable[0])
									, SysInterManager.getSuccessStatusEnum(billTable[0]));
							
							instr_setRecord.set("status", SysInterManager.getSuccessStatusEnum(SysBatchPayInter.BATCH_PAY_INSTR_DETAIL_TALBE))
											.set("bank_back_time", new Date());
						} else {
							bill_setRecord.set( SysInterManager.getStatusFiled(billTable[0])
									, SysInterManager.getFailStatusEnum(billTable[0]));
							
							instr_setRecord.set("status", SysInterManager.getFailStatusEnum(SysBatchPayInter.BATCH_PAY_INSTR_DETAIL_TALBE))
											.set("bank_err_code", parseRecord.getStr("code"))
											.set("bank_err_msg", parseRecord.getStr("message"))
											.set("bank_back_time", new Date());
						}

						// 1.更新单据状态；2.修改队列表状态
						if (CommonService.updateRows(billTable[0], bill_setRecord, bill_whereRecord) == 1) { // 修改单据状态
							boolean updDetail = CommonService.updateRows(SysBatchPayInter.BATCH_PAY_INSTR_DETAIL_TALBE, instr_setRecord, instr_whereRecord) == 1;
							if (!updDetail) {
								log.error("批量支付状态查询回写时，[{}]更新失败！instr_setRecord=【{}】，instr_whereRecord=【{}】", SysBatchPayInter.BATCH_PAY_INSTR_DETAIL_TALBE, instr_setRecord, instr_whereRecord);
							}
							return true;
						} else {
							log.error("批量支付状态查询回写时，[{}]更新失败！bill_setRecord=【{}】，bill_whereRecord=【{}】", billTable[0], bill_setRecord, bill_whereRecord);
							return false;
						}
					}
				});
			}
		}
		writeBack(instrTotalRecord);
        log.debug("批量支付状态查询回写结束");
        
        
    }

    @Override
    public void setChannelInter(IChannelInter channelInter) {
        this.channelInter = (IMoreResultChannelInter) channelInter;
    }

    @Override
    public IChannelInter getChannelInter() {
        return this.channelInter;
    }

    @Override
    public void callBack(Exception e) throws Exception {
        Db.delete("trade_result_query_batch_instr_queue_lock", instr);
    }

    /**
	 * 单笔回写完毕，检查是否具备回写汇总表条件，具备则回写
	 */
	private void writeBack(final Record instrTotal){
		final Long instrTotalId = TypeUtils.castToLong(instrTotal.get("id"));
		//1.检查是否满足回写条件
		Integer count = Db.queryInt(Db.getSql("batchpay.countInstrDetailInHandling"), instrTotalId);
		if (count > 0) {
			//更新回写时间
			int upd = CommonService.updateRows(SysBatchPayInter.BATCH_PAY_INSTR_TOTLE_TALBE
					, new Record().set("init_resp_time", new Date())
					, new Record().set("id", instrTotal.get("id")));
			if(upd != 1){
				log.error("批量收付查询历史交易状态指令原始数据回写，更新[{}.init_resp_time]失败, updateRows=[{}]", SysBatchPayInter.BATCH_PAY_INSTR_TOTLE_TALBE, upd);
			}
			return;
		}
		
		//2.查询付款子批次汇总信息 对应的原始数据表名
		String billTotalTbName = instrTotal.getStr("source_ref");
		
		Record billTotalRecord = SysBatchPayInter.getBillTotalRecord(billTotalTbName, instrTotal.getLong("bill_id"));
		if (null == billTotalRecord) {
			log.error("批量收付查询历史交易状态指令原始数据回写，通过source_ref=[{}],bill_id=[{}]未找到单据！", billTotalTbName, instrTotal.getLong("bill_id"));
			return;
		}
		Integer sourceSys = SysBatchPayInter.getSourceSys(billTotalTbName, billTotalRecord);
		if (null == sourceSys) {
			return;
		}
		final String originTb = sourceSys == WebConstant.SftOsSource.LA.getKey() ? SysBatchPayInter.LA_ORIGIN : SysBatchPayInter.EBS_ORIGIN;
		
		//3.更新汇总表与原始数据
		boolean flag = Db.tx(new IAtom() {
			@Override
			public boolean run() throws SQLException {
				//更新batch_pay_instr_queue_total：明细中有一条成功，汇总就更新为成功
//				int updInstrTotal = Db.update(Db.getSql("batchpay.updInstrTotal"), instrTotalId, instrTotalId);
				int updInstrTotal = CommonService.updateRows("batch_pay_instr_queue_total"
						, new Record().set("status", 2)
						, new Record().set("id", instrTotalId));
				if (updInstrTotal == 1) {
					log.debug("批量收付查询历史交易状态指令原始数据回写，更新指令汇总表成功！");
					//更新pay_batch_total
					if (SysBatchPayInter.updBillTotal(instrTotalId, TypeUtils.castToLong(instrTotal.get("bill_id")), instrTotal.getStr("source_ref"))) {
						log.debug("批量收付查询历史交易状态指令原始数据回写，更新单据汇总表成功！");
						//更新la_origin_pay_data|ebs_origin_pay_data
						int updOrigin;
						if (originTb.equals(SysBatchPayInter.LA_ORIGIN)) {
							log.debug("批量收付查询历史交易状态指令原始数据回写，回写LA原始数据");
							updOrigin = Db.update(Db.getSql("batchpay.updOrginSuccLa"), instrTotalId);
						}else {
							log.debug("批量收付查询历史交易状态指令原始数据回写，回写EBS原始数据");
							Calendar c = Calendar.getInstance();
							String date = new SimpleDateFormat("yyyy-MM-dd").format(c.getTime());
							String time = new SimpleDateFormat("HH:mm:ss").format(c.getTime());
							
							
							String accno = section.getAccno();
							Map<String, Object> aRowData = TableDataCacheUtil.getInstance().getARowData("account", "acc_no", accno);
							String paybankcode = null;
							if (null != aRowData) {
								paybankcode = TypeUtils.castToString(aRowData.get("bankcode"));
							}else {
								paybankcode = String.format("银行账号：(%s)未维护到account表", accno);
							}
							
							SqlPara updOrginLaSqlPara = Db.getSqlPara("batchpay.updOrginSuccEbs");
							updOrigin = Db.update(updOrginLaSqlPara.getSql(), date, time, paybankcode, accno, instrTotalId);
							
						}
						if (updOrigin == instrTotal.getInt("total_num")) {
							return true;
						}else {
							log.error("批量收付查询历史交易状态指令原始数据回写，付款指令汇总信息表batch_pay_instr_queue_total.total_num与更新的原始数据条数[{}]不一致, instrTotalId={}", updOrigin, instrTotalId);
							return false;
						}
					}else {
						log.error("批量收付查询历史交易状态指令原始数据回写，更新{}失败", instrTotal.getStr("source_ref"));
						return false;
					}
				}
				log.error("批量收付查询历史交易状态指令原始数据回写，更新batch_pay_instr_queue_total失败！");
				return false;
			}
		});
		
		if (flag) {
			try {
				List<Record> originRecord = null;
				if (sourceSys == WebConstant.SftOsSource.LA.getKey()) {
					log.info("======回调LA");
					originRecord = Db.find(Db.getSql("disk_backing.selectLaOriginData"), billTotalRecord.get("child_batchno"));
				} else if (sourceSys == WebConstant.SftOsSource.EBS.getKey()) {
					log.info("======回调EBS");
					originRecord = Db.find(Db.getSql("disk_backing.selectEbsOriginData"), billTotalRecord.get("child_batchno"));
				}
				SftCallBack callback = new SftCallBack();
				callback.callback(sourceSys, originRecord);
			} catch (Exception e) {
				e.printStackTrace();
				log.error("===============回调失败");
			}
		}
	}
}
