package com.qhjf.cfm.web.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/10/30
 * @Description: 接口数据管理
 */
public class OriginDataOaWorkBook extends AbstractWorkBook {
    public OriginDataOaWorkBook() {
        this.optype = "origindataoa_listexport";
        this.fileName = "接口数据管理.xls";
        this.titleNames = new String[]{
                "bill_no", "pay_org_type", "org_name", "apply_date", "recv_acc_name", "recv_acc_no", "recv_bank", "amount", "interface_status", "process_status"

        };
        this.titles = new String[]{
                "报销单申请号", "付款机构类型", "申请单位", "申请日期", "收款人", "收款方帐号", "收款方银行", "金额", "接口状态", "处理状态"
        };
        this.sheetName = "接口数据管理";
    }

    @Override
    public Workbook getWorkbook() {


        List<Record> recordList = Db.find(Db.getSqlPara("origin_data_oa.getPage", Kv.by("map", getRecord().getColumns())));

        for (Record record : recordList) {
            int pay_org_type = TypeUtils.castToInt(record.get("pay_org_type"));
            record.set("pay_org_type", pay_org_type == 1 ? "总公司付款" : "分公司付款");

            int interface_status = TypeUtils.castToInt(record.get("interface_status"));
            record.set("interface_status", WebConstant.OaInterfaceStatus.getOaInterfaceStatus(interface_status).getDesc());

            int process_status = TypeUtils.castToInt(record.get("process_status"));
            record.set("process_status", WebConstant.OaProcessStatus.getOaProcessStatus(process_status).getDesc());
        }

        return POIUtil.createExcel(recordList, this);
    }
}
