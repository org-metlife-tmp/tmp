package com.qhjf.cfm.web.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 可疑数据导出
 */
public class RecvDoubtfulWorkBook extends AbstractWorkBook {

    public RecvDoubtfulWorkBook() {
        this.optype = "sftrecvdoubtful_listexport";
        this.titleNames = new String[]{
                "push_date", "pay_code", "pay_mode", "bank_key", "bankkey_desc",
                "biz_type", "org_name", "preinsure_bill_no", "insure_bill_no", "amount",
                "pay_acc_name", "pay_cert_code", "pay_acc_no", "status", "op_user_name", "op_date", "op_reason"

        };
        this.titles = new String[]{
                "应收日期", "支付号码", "支付方式", "bankkey", "bankkey描述", "业务类型", "机构名称", "投保单号", "保单号", "金额"
                , "客户姓名", "证件号码", "银行帐号", "状态", "操作人", "操作日期", "操作理由"
        };
        this.sheetName = "可疑数据列表";
    }

    @Override
    public Workbook getWorkbook() {
        Record record = getRecord();
        int osSource = TypeUtils.castToInt(record.get("os_source"));
        SqlPara sqlPara = null;
        record.remove("os_source");

        Long org_id = getUodpInfo().getOrg_id();
        Record findById = Db.findById("organization", "org_id", org_id);
        List<String> codes = new ArrayList<>();
        if(findById.getInt("level_num")==1 &&
                (!WebConstant.SftDoubtPayMode.WY.getKeyc().equals(record.getStr("pay_mode")))){
            codes = Arrays.asList("0102","0101","0201","0202","0203","0204","0205","0500");
        }else{
            List<Record> rec = Db.find(Db.getSql("org.getCurrentUserOrgs"), org_id);
            for (Record o : rec) {
                codes.add(o.getStr("code"));
            }
        }
        if(WebConstant.SftDoubtPayMode.WY.getKeyc().equals(record.getStr("pay_mode"))){
            record.set("gmfcodes", codes);
        }else{
            record.set("codes", codes);
        }
        if(WebConstant.SftOsSource.LA.getKey() == osSource){
            //LA
            this.fileName = "LA_DoubleCheck_"+DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
            sqlPara = Db.getSqlPara("recvdoubtful.doubtfulLalist", Kv.by("map", record.getColumns()));
        }
        List<Record> recordList = Db.find(sqlPara);
        for(Record rd : recordList){
            int status = TypeUtils.castToInt(rd.get("status"));
            rd.set("status", status==0? "未处理" : "已处理");

        }
        try{
            SymmetricEncryptUtil util = SymmetricEncryptUtil.getInstance();
            util.recvmask(recordList);
        }catch (Exception e){
            e.printStackTrace();
        }
        return POIUtil.createExcel(recordList, this);
    }
}
