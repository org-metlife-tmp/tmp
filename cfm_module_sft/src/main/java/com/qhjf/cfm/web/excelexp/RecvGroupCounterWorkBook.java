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
 * @Description: 柜面收团单导出
 */
public class RecvGroupCounterWorkBook extends AbstractWorkBook {

    public RecvGroupCounterWorkBook() {
        this.optype = "recvgroupcounter_listexport";
        this.fileName = "counter_collection"+DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
        this.titleNames = new String[]{
                "recv_date", "batch_process_no", "source_sys", "batch_no", "preinsure_bill_no", "insure_bill_no", "bill_org_name", "recv_mode",
                "recv_bank_name", "recv_acc_no", "use_funds", "bill_status", "bill_number", "insure_acc_no", "insure_name", "business_acc_no", "business_acc",
                "consumer_no", "consumer_acc_name", "amount", "consumer_bank_name", "consumer_acc_no", "third_payment", "payer", "pay_code", "create_user_name",
                "recv_org_name" ,"pay_status"
        };
        this.titles = new String[]{
                "收款日期","批处理号","核心系统","批单号","投保单号","保单号","保单机构","收款方式","收款银行",
                "收款账号","资金用途","票据状态","票据编号","投保人客户号","投保人","业务所属客户号","业务所属客户",
                "客户号","客户名称","金额","客户银行","客户账号","第三方缴费","缴费人","缴费编码","操作人","收款机构","状态"
        };
        this.sheetName = "团单收款列表";
    }

    @Override
    public Workbook getWorkbook() {
    	Record rec = getRecord();
        SqlPara sqlPara = Db.getSqlPara("recv_group_counter.grouplist", Kv.by("map", rec.getColumns()));
        List<Record> recordList = Db.find(sqlPara);
        for(Record rd : recordList){
            int source_sys = TypeUtils.castToInt(rd.get("source_sys"));
            rd.set("source_sys", WebConstant.SftOsSource.getSftOsSource(source_sys).getDesc());
            int recv_mode = TypeUtils.castToInt(rd.get("recv_mode"));
            rd.set("recv_mode", WebConstant.Sft_RecvGroupCounter_Recvmode.getSft_RecvGroupCounter_RecvmodeByKey(recv_mode).getDesc());
            int use_funds = TypeUtils.castToInt(rd.get("use_funds"));
            rd.set("use_funds", WebConstant.SftRecvGroupCounterUseFunds.getSftRecvGroupCounterUseFundsByKey(use_funds).getDesc());
            int bill_status = TypeUtils.castToInt(rd.get("bill_status"));
            rd.set("bill_status", WebConstant.SftRecvCounterBillStatus.getByKey(bill_status).getDesc());
            int third_payment = TypeUtils.castToInt(rd.get("third_payment"));
            rd.set("third_payment", third_payment==0? "否" : "是");
            int pay_status = TypeUtils.castToInt(rd.get("pay_status"));
            rd.set("pay_status", pay_status==1? "确认" : "撤消");

        }
        return POIUtil.createExcel(recordList, this);
    }
}
