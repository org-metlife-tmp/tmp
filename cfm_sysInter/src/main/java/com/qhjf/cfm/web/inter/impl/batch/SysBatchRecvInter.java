package com.qhjf.cfm.web.inter.impl.batch;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.bankinterface.api.exceptions.BankInterfaceException;
import com.qhjf.bankinterface.api.utils.OminiUtils;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.CommKit;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.web.channel.inter.api.IChannelBatchInter;
import com.qhjf.cfm.web.channel.inter.api.IChannelInter;
import com.qhjf.cfm.web.channel.manager.ChannelManager;
import com.qhjf.cfm.web.config.CMBCTestConfigSection;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.inter.api.ISysAtomicInterface;
import com.qhjf.cfm.web.inter.manager.SysInterManager;
import com.qhjf.cfm.web.webservice.sft.SftRecvCallBack;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: CHT
 * @Date: 2019/2/26
 * @Description:
 */
public class SysBatchRecvInter implements ISysAtomicInterface {
    private static Logger log = LoggerFactory.getLogger(SysBatchRecvInter.class);
    private static CMBCTestConfigSection configSection = CMBCTestConfigSection.getInstance();
    private IChannelBatchInter channelInter;
    private Record instr;

    /**
     * 收款指令汇总信息表
     */
    public static final String BATCH_RECV_INSTR_TOTLE_TALBE = "batch_recv_instr_queue_total";
    /**
     * 收款指令明细信息表
     */
    public static final String BATCH_RECV_INSTR_DETAIL_TALBE = "batch_recv_instr_queue_detail";
    /**
     * LA原始收款数据表：批量收付完结时回写该表
     */
    public static final String LA_ORIGIN = "la_origin_recv_data";

    private static final String BILL_NOT_EXIST = "批量收付回写时，通过source_ref=[{}],bill_id=[{}]未找到单据！";

    @Override
    public Record genInstr(Record record) {
        @SuppressWarnings("unchecked")
        List<Record> batchList = (List<Record>) record.get("list");
        if (null == batchList || batchList.size() == 0) {
            log.error("批量收付的单据列表为空！");
            return null;
        }

        String recvCnaps = batchList.get(0).getStr("recv_bank_cnaps");
        String recvBankCode = recvCnaps.substring(0, 3);

        //生成收款指令汇总信息
        Record total = genInstrTotal(batchList.get(0), batchList.size(), recvBankCode);
        if (total == null) {
            return null;
        }
        BigDecimal amount = new BigDecimal(0);
        List<Record> detailList = new ArrayList<>();
        for (Record batch : batchList) {
            //汇总每笔金额
            BigDecimal batchAmount = new BigDecimal(batch.getDouble("amount") == null ? "0" : Double.toString(batch.getDouble("amount")));
			amount = amount.add(batchAmount);

            //生成收款指令明细信息
            Record detail = genInstrDetail(batch, total.getStr("bank_serial_number"), recvBankCode);
            if (detail == null) {
                return null;
            }
            detailList.add(detail);
        }

        total.set("total_amount", amount);

        this.instr = new Record();
        //收款指令汇总信息
        this.instr.set("total", total);
        //收款指令明细信息
        this.instr.set("detail", detailList);
        return this.instr;
    }

    @Override
    public Record getInstr() {
        return this.instr;
    }

    public void setInnerInstr(Record record) {
        this.instr = record;
    }

    @Override
    public void callBack(String jsonStr) throws Exception {
        CommKit.debugPrint(log,"交易回写开始...jsonStr={}",jsonStr);
        int resultCount = channelInter.getResultCount(jsonStr);
        if (resultCount == 0) {
            log.error("银行返回数据条数为0");
            return;
        }

        Record instrTotal = (Record) this.instr.get("total");
        Record totalId = Db.findFirst(Db.getSql("batchrecv.qryTotalId"),instrTotal.getStr("bank_serial_number"));
        Long instrTotalId = TypeUtils.castToLong(instrTotal.get("id"));
        if(!OminiUtils.isNullOrEmpty(totalId)){
            instrTotalId = TypeUtils.castToLong(totalId.get("id"));
        }
        Record instrTotalRecord = Db.findById(BATCH_RECV_INSTR_TOTLE_TALBE, "id", instrTotalId);
        if (instrTotalRecord == null) {
            throw new Exception("批收单据发送指令不存在！id=" + instrTotalId);
        }
        
        for (int i = 0; i < resultCount; i++) {
            try {
                final Record parseRecord = channelInter.parseResult(jsonStr, i);
                String cnaps = instrTotalRecord.getStr("recv_bank_cnaps");
                Record channel = Db.findFirst(Db.getSql("batchrecv.qryChannel"),instrTotal.getStr("recv_account_no"));
                if(!OminiUtils.isNullOrEmpty(channel)){
                    Record dircet = Db.findFirst(Db.getSql("batchrecv.qryChannelSet"),channel.getStr("channel_id"));
                    cnaps = dircet.getStr("shortPayCnaps");
                }
                if (cnaps.startsWith("308")) {
                	String oldReqnbr = instrTotalRecord.getStr("reqnbr");
                	if (oldReqnbr == null || "".equals(oldReqnbr.trim())) {

                    	//更新指令汇总表的‘流程实例号’
                    	Record instrTotalSet = new Record();
                    	instrTotalSet.set("init_resp_time", new Date());

                    	if (instrTotal.getStr("recv_bank_cnaps").startsWith("308") ) {
                    		//招行该字段叫：流程实例号
                            String reqnbr = parseRecord.getStr("reqnbr");
                    		//CMBC会返回流程实例号reqnbr，用于查询交易状态与交易明细
                    		instrTotalSet.set("reqnbr", reqnbr);
						}

                    	if (StringUtils.isNotEmpty(parseRecord.getStr("bankErrMsg"))) {
                    		instrTotalSet.set("bank_err_msg", parseRecord.getStr("bankErrMsg"));
                    		instrTotalSet.set("bank_err_code", parseRecord.getStr("bankErrCode"));
						}

                    	Record instrTotalWhere = new Record();
                    	instrTotalWhere.set("id", instrTotalId);

                    	int updIntrTotal = CommonService.updateRows(BATCH_RECV_INSTR_TOTLE_TALBE, instrTotalSet, instrTotalWhere);
                    	if (updIntrTotal != 1) {
    						log.error("******需要人工处理：批收指令回写指令汇总表失败！*****");
    					}
					}else {
						log.error("批量收付回调, 指令汇总表的reqnbr字段已经回写，又再次回写，请人工核查原因 ！");
					}
				} else if(cnaps.startsWith("102")){
                    String oldReqnbr = instrTotalRecord.getStr("reqnbr");
                    if (oldReqnbr == null || "".equals(oldReqnbr.trim())) {

                        //更新指令汇总表的‘流程实例号’
                        Record instrTotalSet = new Record();
                        instrTotalSet.set("init_resp_time", new Date());
                        instrTotalSet.set("reqsta", 1);

                        if (StringUtils.isNotEmpty(parseRecord.getStr("status"))) {
                            String reqnbr = parseRecord.getStr("iSeqno");
                            instrTotalSet.set("reqnbr", reqnbr);
                            instrTotalSet.set("bank_err_msg", parseRecord.getStr("bankErrMsg"));
                            instrTotalSet.set("bank_err_code", parseRecord.getStr("status"));
                        }

                        Record instrTotalWhere = new Record();
                        instrTotalWhere.set("id", instrTotalId);

                        int updIntrTotal = CommonService.updateRows(BATCH_RECV_INSTR_TOTLE_TALBE, instrTotalSet, instrTotalWhere);
                        if (updIntrTotal != 1) {
                            log.error("******需要人工处理：批收指令回写指令汇总表失败！*****");
                        }
                    }else {
                        log.error("批量收付回调, 指令汇总表的reqnbr字段已经回写，又再次回写，请人工核查原因 ！");
                    }
                } else if (cnaps.startsWith("fingard")){
                    String oldReqnbr = instrTotalRecord.getStr("reqnbr");
                    if (oldReqnbr == null || "".equals(oldReqnbr.trim())) {

                        //更新指令汇总表的‘流程实例号’
                        Record instrTotalSet = new Record();
                        instrTotalSet.set("init_resp_time", new Date());
                        instrTotalSet.set("reqsta", 1);

                        if (StringUtils.isNotEmpty(parseRecord.getStr("status"))) {
                            String reqnbr = parseRecord.getStr("transSeqId");
                            instrTotalSet.set("reqnbr", reqnbr);
                            instrTotalSet.set("bank_err_msg", parseRecord.getStr("bankErrMsg"));
                            instrTotalSet.set("bank_err_code", parseRecord.getStr("status"));
                        }

                        Record instrTotalWhere = new Record();
                        instrTotalWhere.set("id", instrTotalId);

                        int updIntrTotal = CommonService.updateRows(BATCH_RECV_INSTR_TOTLE_TALBE, instrTotalSet, instrTotalWhere);
                        if (updIntrTotal != 1) {
                            log.error("******需要人工处理：批收指令回写指令汇总表失败！*****");
                        }
                    }else {
                        log.error("批量收付回调, 指令汇总表的reqnbr字段已经回写，又再次回写，请人工核查原因 ！");
                    }
                }

            } catch (Exception e) {
                CommKit.errorPrint(log,"批量收付回调,第【i={}】次循环交易回写失败，回调数据：【{}】",jsonStr);
                e.printStackTrace();
                continue;
            }
        }

        log.debug("批量收付交易回写结束");
    }

    /**
     * 银行处理 成功的回调
     */
    @Override
    public void callBack(Exception e) throws Exception {
        /**
         * 如果是底层抛出的BankInterfaceException ，明确为失败 如果是其他的异常，打印日志，不进行处理
         */
        if (e instanceof BankInterfaceException) {

            Record instrTotal = (Record) this.instr.get("total");
            final Long instrTotalId = TypeUtils.castToLong(instrTotal.get("id"));
            final String billTotalTb = instrTotal.getStr("source_ref");
            final Long billTotalId = instrTotal.getLong("bill_id");

            Record billTotalRecord = getBillTotalRecord(billTotalTb, billTotalId);
            if (null == billTotalRecord) {
                log.error(BILL_NOT_EXIST, billTotalTb, billTotalId);
                return;
            }
            Integer sourceSys = getSourceSys(billTotalTb, billTotalRecord);
            if (null == sourceSys) {
                return;
            }
            final String originTb = LA_ORIGIN;
            
            boolean flag = Db.tx(new IAtom() {
                @Override
                public boolean run() throws SQLException {

                    //1.更新batch_recv_instr_queue_total
                    if (CommonService.updateRows(BATCH_RECV_INSTR_TOTLE_TALBE
                            , new Record().set("status", 2).set("bank_err_msg", "发送失败").set("init_resp_time", new Date())
                            , new Record().set("id", instrTotalId)) != 1) {
                    	log.error("批收发送银行指令失败回写，更新指令汇总表失败");
                        return false;
                    }
                    //2.更新batch_recv_instr_queue_detail
                    if (!CommonService.update(BATCH_RECV_INSTR_DETAIL_TALBE
                            , new Record().set("status", 2).set("bank_err_msg", "发送失败").set("bank_back_time", new Date())
                            , new Record().set("base_id", instrTotalId))) {
                    	log.error("批收发送银行指令失败回写，更新指令明细表失败");
                        return false;
                    }
                    //3.更新recv_batch_detail
                    if (!CommonService.update(SysInterManager.getDetailTableName(billTotalTb)
                            , new Record().set("status", 2)
                            , new Record().set("base_id", billTotalId))) {
                    	log.error("批收发送银行指令失败回写，更新子批次明细表失败");
                        return false;
                    }
                    //4.更新recv_batch_total
                    if (Db.update(Db.getSql("batchrecv.updBillTotalToFail"), billTotalId) != 1) {
                    	log.error("批收发送银行指令失败回写，更新子批次汇总表失败");
                        return false;
                    }
                    //5.更新la_origin_recv_data
                    SqlPara updOriginFailLaSqlPara = null;
                    if (LA_ORIGIN.equals(originTb)) {
                        updOriginFailLaSqlPara = Db.getSqlPara("batchrecv.updOriginFailLa");
                    }else {
						log.error("批量收直连账户不支持EBS");
						return true;
					}
                    int updOrigin = Db.update(updOriginFailLaSqlPara.getSql(), instrTotalId);
                    if (updOrigin <= 0) {
                    	log.error("批收发送银行指令失败回写，更新原始数据表失败");
                        return false;
                    }
                    log.debug("批收发送银行指令失败回写，更新原始数据表条数：{},instrTotalId={}", updOrigin, instrTotalId);
                    
                    return true;
                }
            });
            if (!flag) {
                log.error("批收发送银行指令失败回写，修改指令、单据、原始数据状态失败！");
            } else {
                try {
                    List<Record> originRecord = null;
                    if (sourceSys == WebConstant.SftOsSource.LA.getKey()) {
                        log.info("======回调LA");
                        originRecord = Db.find(Db.getSql("disk_backing.selectLaRecvOriginData"), billTotalRecord.get("child_batchno"));
                    }
                    SftRecvCallBack callback = new SftRecvCallBack();
                    callback.callback(sourceSys, originRecord);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    log.error("===============批收发送银行指令失败回写，回调失败");
                }
            }
        } else {
        	log.error(e.getMessage());
        	log.error("*****************************需要人工处理***************************");
			log.error("******需要人工处理：银行指令发送异常，需要人工跟银行确认批收指令接收状态，已接受需要询问流程实例号，并手工插入指令汇总表*****");
			log.error("*****************************需要人工处理***************************");
        }
    }

    @Override
    public void setChannelInter(IChannelInter channelInter) {
        this.channelInter = (IChannelBatchInter) channelInter;
    }

    @Override
    public IChannelInter getChannelInter() {
        return this.channelInter;
    }

    /**
     * 生成付款指令汇总信息表 batch_recv_instr_queue_total
     *
     * @param r
     * @return
     */
    private Record genInstrTotal(Record r, int size, String recvBankCode) {
        Record total = new Record();
        //生成批量指令的银行流水号
        String bankSerialNumber = null;
        try {
            bankSerialNumber = ChannelManager.getSerianlNo(recvBankCode);
        } catch (Exception e1) {
            e1.printStackTrace();
            return null;
        }
        total.set("bank_serial_number", bankSerialNumber);
        total.set("repeat_count", r.getInt("repeat_count") == null ? 1 : r.getInt("repeat_count"));
        String sourceRef = r.getStr("source_ref");
        if (sourceRef == null || sourceRef.trim().equals("")) {
        	log.error("批收指令生成汇总表数据时，source_ref为空！");
            return null;
        }
        total.set("source_ref", sourceRef);
        total.set("bill_id", r.get("bill_id"));
        total.set("total_num", size);
//		total.set("total_amount", );
        total.set("recv_account_no", r.get("recv_account_no"));
        total.set("recv_account_name", r.get("recv_account_name"));
        total.set("recv_account_cur", r.get("recv_account_cur"));
        total.set("recv_account_bank", r.get("recv_account_bank"));
        total.set("recv_bank_cnaps", r.get("recv_bank_cnaps"));
        total.set("recv_bank_prov", r.get("recv_bank_prov"));
        total.set("recv_bank_city", r.get("recv_bank_city"));
        total.set("recv_bank_type", r.get("recv_bank_type"));
        total.set("status", 1);
        total.set("trade_date", DateKit.toStr(new Date(), "yyyy-MM-dd"));
        //发送时间不精确，从指令发送队列取指令时的时间更精确
        total.set("init_send_time", new Date());
        return total;
    }
    
    /**
	 * 生成发送日期
	 * @param cnaps
	 * @return
	 *//*
	private String genTradeDate(String cnaps){
		String result = null;
		if ("308".equals(cnaps)) {
			int preDay = configSection.getPreDay();
			result = DateUtil.getSpecifiedDayAfter(new Date(), preDay, "yyyyMMdd");
		}
		return result == null ? DateKit.toStr(new Date(), "yyyy-MM-dd") : result;
	}*/

    /**
     * 生成收款指令明细信息 ： batch_recv_instr_queue_detail
     */
    private Record genInstrDetail(Record batch, String bankSerialNumber, String recvBankCode) {
        Record detail = new Record();

        //detail_id：用于回写子批次明细时，关联用
        detail.set("detail_id", batch.get("id"));
        detail.set("bank_serial_number", bankSerialNumber);

        String detailBankServiceNumber = null;
        try {
            detailBankServiceNumber = ChannelManager.getSerianlNo(recvBankCode);
        } catch (Exception e1) {
        	log.error("批收指令生成明细表表数据时，生成detailBankServiceNumber异常！");
            e1.printStackTrace();
            return null;
        }
        detail.set("detail_bank_service_number", detailBankServiceNumber);
        detail.set("package_seq", batch.get("package_seq"));
        detail.set("amount", batch.get("amount"));

        detail.set("pay_account_no", batch.getStr("pay_account_no"));
        detail.set("pay_account_name", batch.getStr("pay_account_name"));
        detail.set("pay_account_cur", batch.getStr("pay_account_cur"));
        detail.set("pay_account_bank", batch.getStr("pay_account_bank"));
        detail.set("pay_bank_cnaps", batch.getStr("pay_bank_cnaps"));
        detail.set("pay_bank_prov", batch.getStr("pay_bank_prov"));
        detail.set("pay_bank_city", batch.getStr("pay_bank_city"));
        detail.set("pay_bank_type", batch.getStr("pay_bank_type"));
        detail.set("insure_bill_no",batch.getStr("insure_bill_no"));
        detail.set("contractNo",batch.getStr("ContractNo"));
        detail.set("useCN",batch.getStr("UseCN"));

        //批收只支持同行，默认不跨行
        int isCrossBank = 0;
        /*String cnaps = batch.getStr("pay_bank_cnaps");
        if (null != cnaps && cnaps.length() > 3 && !recvBankCode.equals(cnaps.substring(0, 3))) {
            isCrossBank = 1;
        }*/
        detail.set("is_cross_bank", isCrossBank);
        detail.set("status", 3);
        return detail;
    }


    public static Record getBillTotalRecord(String billTotalTb, Long billTotalId) {
        Record billTotalRec = Db.findById(billTotalTb
                , SysInterManager.getSourceRefPrimaryKey(billTotalTb)
                , billTotalId);
        return billTotalRec;
    }

    public static Integer getSourceSys(String billTotalTb, Record billTotalRec) {
        // 原始数据来源 0:LA;1:EBS
        final Integer sourceSys = billTotalRec.getInt("source_sys");
        if (null == sourceSys || (sourceSys != WebConstant.SftOsSource.LA.getKey()
                && sourceSys != WebConstant.SftOsSource.EBS.getKey())) {
            log.error("批量收付回写时，单据汇总表[{}]的数据来源[source_sys]=[{}]为空或不合法", billTotalTb, sourceSys);
            return null;
        }
        return sourceSys;
    }

    /**
     * 更新单据批次汇总表
     */
    public static boolean updBillTotal(Long instrTotalId, Long billTotalId, String sourceRef) {
        List<Record> instrDetails = Db.find(Db.getSql("batchrecv.findInstrDetailByBaseId"), instrTotalId);
        int succ = 0, fail = 0;
        double succAmount = 0, failAmount = 0;
        for (Record detail : instrDetails) {
            Integer status = detail.getInt("status");
            Double amount = detail.getDouble("amount");
            if (status == 1) {
                succ++;
                succAmount += amount == null ? 0 : amount;
            }
            if (status == 2) {
                fail++;
                failAmount += amount == null ? 0 : amount;
            }
        }
        return CommonService.updateRows(sourceRef
                , new Record().set("success_num", succ)
                			  .set("success_amount", succAmount)
                			  .set("fail_num", fail)
                			  .set("fail_amount", failAmount)
                			  .set("service_status", 5)
                			  .set("back_on", new Date())
                , new Record().set("id", billTotalId)) == 1;
    }

    /**
     * 保存付款指令
     * @throws ReqDataException 
     */
    public boolean saveIntr() throws ReqDataException {
        Record total = (Record) this.instr.get("total");
        //保存：收款指令汇总信息表
        boolean save = saveTotal(total);

        if (save) {
            //向收款指令明细信息表反写 上一步生成的汇总表主键
            @SuppressWarnings("unchecked")
            List<Record> detail = (List<Record>) this.instr.get("detail");
            for (Record record : detail) {
                record.set("base_id", total.get("id"));
            }

            int[] batchSave = Db.batchSave(BATCH_RECV_INSTR_DETAIL_TALBE, detail, 1000);
            return ArrayUtil.checkDbResult(batchSave);
        }
        
        log.error("批收单据发送时，保存指令汇总表失败！");
        return false;
    }
    
    /**
     * 保存指令汇总表，工行会生成：业务种类编号
     * @param total
     * @return
     * @throws ReqDataException 
     */
    private static synchronized boolean saveTotal(Record total) throws ReqDataException{
    	String cnaps = total.getStr("recv_bank_cnaps");
    	if (cnaps.startsWith("102")) {
    		//查询当天bus_type最大的值,如果为空，则值为2
    		String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
			Record findFirst = Db.findFirst(Db.getSql("batchrecv.qryMaxBusTypeFromTotal"), today);
            Integer busType = null;
			if(!OminiUtils.isNullOrEmpty(findFirst.getInt("bus_type_max"))){
                busType = null == findFirst ? 1 : findFirst.getInt("bus_type_max");
            }
			busType = null == busType || busType == 0 ? 1 : busType;
			if (++busType > 99) {
				throw new ReqDataException("工行一天只支持97笔批收指令！");
			}
			total.set("bus_type",busType);
		}
    	
    	boolean save = Db.save(BATCH_RECV_INSTR_TOTLE_TALBE, total);
    	return save;
    }
}
