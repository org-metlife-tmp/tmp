package com.qhjf.cfm.web.excelexp;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Date;
import java.util.List;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/10/31
 * @Description: 可疑数据管理导出
 */
public class CheckDoubtfulNcWorkBook extends AbstractWorkBook {

    public CheckDoubtfulNcWorkBook() {
        this.optype = "checkdoubtfulnc_listexport";
        this.fileName =  "NEWCOMP_DOUBTFUL_"+ DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
        this.titleNames = new String[]{
                "flow_id", "apply_user", "amount", "recv_acc_no", "recv_acc_name", "apply_date","send_count", "memo"

        };
        this.titles = new String[]{
                "报销单申请号", "申请人", "金额", "收款人帐号", "收款人账户名称", "申请日期","重发次数", "摘要"
        };
        this.sheetName = "NEWCOMP_DOUBTFUL_"+ DateKit.toStr(new Date(), "YYYYMMdd");
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
        SqlPara sqlPara = Db.getSqlPara("check_doubtful_nc.list", Kv.by("map", getRecord().getColumns()));
        List<Record> recordList = Db.find(sqlPara);
        return POIUtil.createExcel(recordList, this);
    }
}
