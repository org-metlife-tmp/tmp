package com.qhjf.cfm.web.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/10/31
 * @Description: 可疑数据管理导出
 */
public class CheckDoubtfulOaWorkBook extends AbstractWorkBook {

    public CheckDoubtfulOaWorkBook() {
        this.optype = "checkdoubtfuloa_listexport";
        this.fileName = "可疑数据管理.xls";
        this.titleNames = new String[]{
                "bill_no", "pay_org_type", "org_name", "recv_acc_name", "recv_acc_no", "recv_bank", "amount", "memo"

        };
        this.titles = new String[]{
                "报销单申请号", "付款机构类型", "申请单位", "收款人", "收款方帐号", "收款方银行", "金额", "摘要"
        };
        this.sheetName = "可疑数据管理";
    }

    @Override
    public Workbook getWorkbook() {
        String recv_query_key = getRecord().get("recv_acc_no");
        //是否包含中文
        boolean recvFlag = StringKit.isContainChina(recv_query_key);
        if (recvFlag) {
            //名称
            getRecord().set("recv_acc_name", recv_query_key);
        } else {
            //帐号
            getRecord().set("recv_acc_no", recv_query_key);
        }
        getRecord().remove("recv_acc_no");
        SqlPara sqlPara = Db.getSqlPara("check_doubtful_oa.list", Kv.by("map", getRecord().getColumns()));
        List<Record> recordList = Db.find(sqlPara);
        return POIUtil.createExcel(recordList, this);
    }
}
