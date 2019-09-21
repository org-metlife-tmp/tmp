package com.qhjf.cfm.web.channel.fingard;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.api.utils.DateUtil;
import com.qhjf.bankinterface.fingard.api.FingardConstant;
import com.qhjf.cfm.web.channel.inter.api.IChannelBatchInter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * @program: metlife_cfm_main->FingardBatchRecvInter
 * @description: 保融批量收接口实现
 * @author: 耿鑫
 * @create: 2019-08-01 11:56
 **/
public class FingardBatchRecvInter implements IChannelBatchInter {

    public static final String ERROR_MSG = "批量收款，银行受理失败,失败原因:{}-{}";

    private static final Logger log = LoggerFactory.getLogger(FingardBatchRecvInter.class);

    /**
     * 解析返回的json数据
     * @param json
     * @return
     * @throws Exception
     */
    @Override
    public int getResultCount(String json) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONArray head = jsonObject.getJSONArray("Head");
        JSONObject eb0 = head.getJSONObject(0);
        String respcode = eb0.getString("RespCode");
        String retMsg = eb0.getString("RespInfo");
        if ("0".equals(respcode)) {
            log.debug("保融成功受理批收指令！");
        }else {
            log.error(ERROR_MSG, respcode, retMsg);
        }
        return 1;
    }

    /**
     * 解析返回的json数据
     * @param json
     * @param index
     * @return
     * @throws Exception
     */
    @Override
    public Record parseResult(String json, int index) throws Exception {
        Record record = new Record();
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONArray head = jsonObject.getJSONArray("Head");
        JSONObject eb0 = head.getJSONObject(0);
        String respcode = eb0.getString("RespCode");
        String retMsg = eb0.getString("RespInfo");
        if("0".equals(respcode)){
            JSONArray transresp = jsonObject.getJSONArray("TransResp");
            JSONObject trans = transresp.getJSONObject(0);
            if("4".equals(trans.getString("TransState"))){
                record.set("transSeqId",eb0.get("TransSeqID"));
                record.set("status", "S");
                return record;
            } else if("3".equals(trans.get("TransState"))){
                return record.set("status", "F").set("bankErrMsg", "整批失败").set("bankErrCode", respcode);
            }
        }
        return record.set("status", "F").set("bankErrMsg", retMsg).set("bankErrCode", respcode);
    }

    /**
     *
     * @return
     */
    @Override
    public int getBatchSize() {
        return 0;
    }

    /**
     * 拼装保融批量代收的map数据
     * @param record
     * @return
     */
    @Override
    public Map<String, Object> genParamsMap(Record record) {
        Map<String, Object> map = new HashMap();
        Record total = (Record)record.get("total");
        if (null == total) {
            return null;
        }
        map.put("ReqSeqID", total.getStr("bank_serial_number"));

        map.put("AccountingFlag", "");

        Map<String, Object> corpToBankMap = new HashMap();
        corpToBankMap.put("EncryptFlag", "");
        corpToBankMap.put("SourceDataMD5", "");
        corpToBankMap.put("SourceTotalAmount", "");

        Map<String, Object> transParamMap = new HashMap();

        transParamMap.put("RecAct", total.getStr("recv_account_no"));
        transParamMap.put("RecArea", "");
        transParamMap.put("RecAreaName", "");
        transParamMap.put("CorpBankCode", total.getStr("recv_bank_type"));

        List<Map<String, Object>> list = new ArrayList();
        map.put("TotalNum", total.getStr("total_num"));
        map.put("TotalAmt", total.getStr("total_amount"));
        List<Record> detail = (List<Record>)record.get("detail");
        if (detail != null && detail.size() > 0) {
            for (Record r : detail) {
                Map<String, Object> detailRecordMap = new HashMap();
                detailRecordMap.put("PayBnk", "");
                detailRecordMap.put("PayBankName", r.getStr("pay_account_bank"));
                detailRecordMap.put("PayAreaName", "");
                detailRecordMap.put("RecName", "");
                detailRecordMap.put("RecBnk", "");
                detailRecordMap.put("RecBankName", "");
                detailRecordMap.put("RecArea", "");
                detailRecordMap.put("RecAreaName", "");
                detailRecordMap.put("SameCity", "");
                detailRecordMap.put("SameBnk", "");
                detailRecordMap.put("PayDate", DateUtil.toStr(new Date(),DateUtil.DEFAULT_DATEPATTERN));
                detailRecordMap.put("PayTime", DateUtil.toStr(new Date(),DateUtil.DEFAULT_TIMEPATTERN));
                detailRecordMap.put("RecCur", "");
                detailRecordMap.put("CertType", "");
                detailRecordMap.put("CertNum", "");
                detailRecordMap.put("CreditCardSecCode", "");
                detailRecordMap.put("CreditCardValidity", "");
                detailRecordMap.put("Usage", "批量收款");
                detailRecordMap.put("Memo", "");
                detailRecordMap.put("ReqReserve", "");
                detailRecordMap.put("CNAPSCode", "");
                detailRecordMap.put("CNAPSName", "");
                detailRecordMap.put("IsPrivate", "");
                detailRecordMap.put("SourceNote", "");
                detailRecordMap.put("ORGCode", "");
                detailRecordMap.put("Extend1", "");
                detailRecordMap.put("Extend2", "");
                detailRecordMap.put("PolicyNumber", "");
                detailRecordMap.put("ProtocolCode", "");

                detailRecordMap.put("PayArea", "");
                detailRecordMap.put("PayName", r.getStr("pay_account_name"));
                detailRecordMap.put("PayAct", r.getStr("pay_account_no"));
                detailRecordMap.put("PayBankCode", r.getStr("pay_bank_type"));
                detailRecordMap.put("RecAct", total.getStr("recv_account_no"));
                detailRecordMap.put("RecBankCode", total.getStr("recv_bank_type"));
                detailRecordMap.put("CardType", "0");
                detailRecordMap.put("PayAmount", r.getStr("amount"));
                detailRecordMap.put("PayCur", "CNY");
                detailRecordMap.put("PostScript", r.getStr("package_seq"));
                list.add(detailRecordMap);
            }
        }
        transParamMap.put("DetailRecord", list);

        map.put("TransParam", transParamMap);
        map.put("CorpToBank", corpToBankMap);
        return map;
    }

    /**
     * 原子接口底层实现
     * @return
     */
    @Override
    public AtomicInterfaceConfig getInter() {
        return FingardConstant.FINGARD_BATCHRECEIVE;
    }
}
