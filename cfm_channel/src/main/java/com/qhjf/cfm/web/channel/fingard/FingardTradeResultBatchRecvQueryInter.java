package com.qhjf.cfm.web.channel.fingard;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.bankinterface.api.AtomicInterfaceConfig;
import com.qhjf.bankinterface.api.utils.OminiUtils;
import com.qhjf.bankinterface.fingard.api.FingardConstant;
import com.qhjf.cfm.web.channel.inter.api.IMoreResultChannelInter;

import java.util.HashMap;
import java.util.Map;

/**
 * @version 1.0
 * @program: sunlife_cfm_main->FingardTradeResultBatchRecvQueryInter
 * @description: 保融批量收查询接口
 * @author: 耿鑫
 * @create: 2019-08-01 18:08
 **/
public class FingardTradeResultBatchRecvQueryInter implements IMoreResultChannelInter {


    /**
     * json数据解析
     * @param json
     * @return
     * @throws Exception
     */
    @Override
    public int getResultCount(String json) throws Exception {
        JSONObject jsonObject = JSONObject.parseObject(json);
        JSONObject object = jsonObject.getJSONArray("TransResp").getJSONObject(0);
        JSONArray detailRecord = object.getJSONArray("DetailRecord");
        return detailRecord.size();
    }

    /**
     * 返回的json数据解析
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
        record.set("bank_err_code", eb0.getString("RespCode"));
        record.set("bank_err_msg", eb0.getString("RespInfo"));
        if("0".equals(eb0.getString("RespCode"))){
            JSONArray transresp = jsonObject.getJSONArray("TransResp");
            JSONObject trans = transresp.getJSONObject(0);
            if("3".equals(trans.getString("TransState"))){
                record.set("status", 2);
            }
            JSONObject object = jsonObject.getJSONArray("TransResp").getJSONObject(0);
            JSONArray detailRecord = object.getJSONArray("DetailRecord");
            if("2".equals(detailRecord.getJSONObject(index).getString("Result"))){
                record.set("package_seq", detailRecord.getJSONObject(index).getString("PostScript"));
                record.set("status", 1);
            } else if("3".equals(detailRecord.getJSONObject(index).getString("Result"))){
                record.set("package_seq", detailRecord.getJSONObject(index).getString("PostScript"));
                record.set("status", 2);
            }
        } else {
            record.set("status", 3);
        }
        return record;
    }

    /**
     * 批量代收查询拼装map数据
     * @param record
     * @return
     */
    @Override
    public Map<String, Object> genParamsMap(Record record) {
        Map<String, Object> map = new HashMap();
        map.put("ReqSeqID", record.getStr("bank_serial_number"));
        Record recvTotal = Db.findFirst(Db.getSql("batchrecvjob.getTradeResultBatchQry"),record.getStr("bank_serial_number"));
        if(!OminiUtils.isNullOrEmpty(recvTotal)){
            map.put("BgnDate", recvTotal.getStr("trade_date"));
            map.put("RecAct", recvTotal.getStr("recv_account_no"));
            map.put("RecArea", "");
            map.put("TotalNum", recvTotal.getStr("total_num"));
            map.put("TotalAmount", recvTotal.getStr("total_amount"));
        }
        Map<String, Object> corpToBankmap = new HashMap();
        corpToBankmap.put("EncryptFlag", "");

        map.put("CorpToBank", corpToBankmap);
        return map;
    }

    /**
     * 原子接口数据
     * @return
     */
    @Override
    public AtomicInterfaceConfig getInter() {
        return FingardConstant.FINGARD_BATCHRECEIVEQUERY;
    }
}
