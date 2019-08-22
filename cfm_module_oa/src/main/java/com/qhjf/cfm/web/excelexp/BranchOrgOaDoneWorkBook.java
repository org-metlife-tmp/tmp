package com.qhjf.cfm.web.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/10/30
 * @Description: 分公司付款已处理导出
 */
public class BranchOrgOaDoneWorkBook extends AbstractWorkBook {

    public BranchOrgOaDoneWorkBook() {
        this.optype = "branchorgoa_donelistexport";
        this.fileName = "分公司付款-已处理.xls";
        this.titleNames = new String[]{
                "bill_no", "org_name", "create_on", "update_on", "pool_account_no", "pay_pay_account_no", "recv_account_name", "recv_account_no",
                "recv_account_bank", "payment_amount", "payment_summary", "service_status", "feed_back"

        };
        this.titles = new String[]{
                "报销单申请号", "申请单位", "申请日期", "发送日期", "资金池账户", "付款方帐号", "收款人", "收款方账号", "收款方银行", "收款金额", "摘要", "状态", "失败原因"
        };
        this.sheetName = "分公司付款-未处理";
    }

    @Override
    public Workbook getWorkbook() {
        getRecord().set("delete_flag", 0);
        this.setDoneTradStatus(getRecord(), "service_status");
        CommonService.processQueryKey(getRecord(), "recv_query_key", "recv_account_name", "recv_account_no");
        SqlPara sqlPara = Db.getSqlPara("branch_org_oa.findOaList", Kv.by("map", getRecord().getColumns()));
        List<Record> recordList = Db.find(sqlPara);

        for (Record record : recordList) {

            int service_status = TypeUtils.castToInt(record.get("service_status"));
            record.set("service_status", WebConstant.BillStatus.getBillStatus(service_status).getDesc());
        }

        return POIUtil.createExcel(recordList, this);
    }

    private void setDoneTradStatus(Record record, String statusName) {
    	List status = record.get(statusName);
        if (status == null || status.size() == 0 ) {
            record.set(statusName, new int[]{
                    WebConstant.BillStatus.SUBMITED.getKey(),
                    WebConstant.BillStatus.AUDITING.getKey(),
                    WebConstant.BillStatus.PASS.getKey(),
                    WebConstant.BillStatus.PROCESSING.getKey(),
                    WebConstant.BillStatus.SUCCESS.getKey(),
                    WebConstant.BillStatus.FAILED.getKey(),
                    WebConstant.BillStatus.CANCEL.getKey(),
                    WebConstant.BillStatus.NOCOMPLETION.getKey(),
                    WebConstant.BillStatus.COMPLETION.getKey(),
                    WebConstant.BillStatus.WAITPROCESS.getKey()
            });
        }
    }
}
