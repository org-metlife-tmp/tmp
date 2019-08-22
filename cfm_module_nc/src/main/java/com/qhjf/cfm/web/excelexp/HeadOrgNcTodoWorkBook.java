package com.qhjf.cfm.web.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Date;
import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/10/30
 * @Description: 总公司未付款导出
 */
public class HeadOrgNcTodoWorkBook extends AbstractWorkBook {

    private static final Log log = LogbackLog.getLog(HeadOrgNcTodoWorkBook.class);

    public HeadOrgNcTodoWorkBook() {
        this.optype = "headorgnc_todolistexport";
        this.fileName = "NEWCOMP_PAYMENT_"+ DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
        this.titleNames = new String[]{
                "flow_id", "apply_user","org_name", "create_on", "pay_account_no", "pay_account_bank", "payment_amount", "recv_account_name", "recv_account_no",
                "recv_account_bank", "payment_summary", "service_status"

        };
        this.titles = new String[]{
                "流程ID","申请人", "申请单位", "申请日期", "付款方帐号", "付款方银行", "收款金额", "收款人", "收款方帐号", "收款方开户行", "摘要", "状态"
        };
        this.sheetName = "NEWCOMP_PAYMENT_"+ DateKit.toStr(new Date(), "YYYYMMdd");
    }

    @Override
    public Workbook getWorkbook() {
        Record record = getRecord();
        List<Record> recordList = null;
        String service_status_origin = TypeUtils.castToString(record.get("service_status"));
        if(StringUtils.isBlank(service_status_origin)||service_status_origin.equals("[]")) {
            record.remove("service_status");
        }
        record.set("org_id", getUodpInfo().getOrg_id());
        recordList = Db.find(Db.getSqlPara("head_org_nc.getTodoPage", Kv.by("map", record.getColumns())));
        for (Record r : recordList) {

            int service_status = TypeUtils.castToInt(r.get("service_status"));
            r.set("service_status", WebConstant.BillStatus.getBillStatus(service_status).getDesc());

        }

        return POIUtil.createExcel(recordList, this);
    }

}
