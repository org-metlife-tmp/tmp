package com.qhjf.cfm.web.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.AccCommonService;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/10/30
 * @Description: 总公司未付款导出
 */
public class HeadOrgOaDoneWorkBook extends AbstractWorkBook {

    private static final Log log = LogbackLog.getLog(HeadOrgOaDoneWorkBook.class);

    public HeadOrgOaDoneWorkBook() {
        this.optype = "headorgoa_donelistexport";
        this.fileName = "总公司付款-已处理.xls";
        this.titleNames = new String[]{
                "bill_no", "org_name", "create_on", "update_on", "pay_account_no", "pay_account_bank", "recv_account_name", "recv_account_no",
                "recv_account_bank", "payment_amount", "payment_summary", "service_status", "feed_back"

        };
        this.titles = new String[]{
                "报销单申请号", "申请单位", "申请日期", "发送日期", "付款方帐号", "付款方银行", "收款人", "收款方帐号", "收款方银行", "收款金额", "摘要", "状态",
                "失败原因"

        };
        this.sheetName = "总公司付款-已处理";
    }

    @Override
    public Workbook getWorkbook() {
        Record record = getRecord();
        List<Record> recordList = null;
        record.set("org_id", getUodpInfo().getOrg_id());
        List status = record.get("service_status");
        if (status == null || status.size() == 0) {
            AccCommonService.setOaDoneStatus(record, "service_status");
        }
        recordList = Db.find(Db.getSqlPara("head_org_oa.getDonePage", Kv.by("map", record.getColumns())));
        for (Record r : recordList) {

            int service_status = TypeUtils.castToInt(r.get("service_status"));
            r.set("service_status", WebConstant.BillStatus.getBillStatus(service_status).getDesc());

        }

        return POIUtil.createExcel(recordList, this);
    }

}
