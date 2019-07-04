package com.qhjf.cfm.web.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Date;
import java.util.List;

/**
 * @Description: 柜面收导出
 */
public class RecvCounterWorkBook extends AbstractWorkBook {

    public RecvCounterWorkBook() {
        this.optype = "recvcounter_listexport";
        this.fileName = "counter_personal"+DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
        this.titleNames = new String[]{
                "recv_date", "batch_process_no", "source_sys", "insure_bill_no", "bill_org_name", "recv_mode",
                "recv_bank_name", "recv_acc_no", "use_funds", "bill_status", "bill_number", "bill_date", "terminal_no", "amount"
                , "consumer_bank_name", "consumer_acc_no", "insure_name", "insure_cer_no", "third_payment", "payer", "payer_cer_no", "create_user_name"
                , "recv_org_name" ,"match_status"
        };
        this.titles = new String[]{
                "收款日期", "批处理号", "核心系统", "保单号", "保单机构", "收款方式", "收款银行", "收款账号", "资金用途", "票据状态",
                "票据票号", "票据日期", "终端机编号", "金额", "客户银行", "客户账号", "投保人", "投保人证件号", "第三方缴费", "缴费人",
                "缴费人证件号", "操作人","收款机构","状态"
        };
        this.sheetName = "个单收款列表";
    }

    @Override
    public Workbook getWorkbook() {
    	Record rec = getRecord();
    	rec.set("bill_type", WebConstant.SftRecvType.GDSK.getKey());
        SqlPara sqlPara = Db.getSqlPara("recv_counter.personalList", Kv.by("map", rec.getColumns()));
        List<Record> recordList = Db.find(sqlPara);
        for(Record rd : recordList){
            int match_status = TypeUtils.castToInt(rd.get("match_status"));
            rd.set("match_status", WebConstant.SftRecvCounterMatchStatus.getByKey(match_status));
            int source_sys = TypeUtils.castToInt(rd.get("source_sys"));
            rd.set("source_sys", WebConstant.SftOsSourceCounter.getSftOsSource(source_sys));
            int recv_mode = TypeUtils.castToInt(rd.get("recv_mode"));
            rd.set("recv_mode", WebConstant.Sft_RecvPersonalCounter_Recvmode.getByKey(recv_mode));
            int bill_status = TypeUtils.castToInt(rd.get("bill_status"));
            rd.set("bill_status", WebConstant.SftRecvCounterBillStatus.getByKey(bill_status));
            int third_payment = TypeUtils.castToInt(rd.get("third_payment"));
            rd.set("third_payment", third_payment == 0 ?  "否" : "是");
        }
        return POIUtil.createExcel(recordList, this);
    }
}
