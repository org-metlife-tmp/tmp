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
 * @Description: 通道数据导出
 */
public class BankkeySettingWorkBook extends AbstractWorkBook {

    public BankkeySettingWorkBook() {
        this.optype = "sftbankkey_listexport";
        this.fileName = "BANKKEY_"+DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
        this.titleNames = new String[]{
                "os_source", "bankkey", "bankkey_desc", "channel_code", "channel_desc",
                "orgname", "bankcode", "acc_no", "subordinate_channel", "is_source_back_name", "bankkey_status_name"

        };
        this.titles = new String[]{
                "来源系统", "bankkey", "bankkey描述", "通道编码", "通道描述", "机构名称", "Bankcode", "银行帐号", "所属渠道",
                "原通道退款", "状态"
        };
        this.sheetName = "BANKKEY列表";
    }

    @Override
    public Workbook getWorkbook() {
        SqlPara sqlPara = Db.getSqlPara("bankkey_setting.bankkeylist", Kv.by("map", getRecord().getColumns()));
        List<Record> recordList = Db.find(sqlPara);
        for(Record record : recordList){
            int os_source = TypeUtils.castToInt(record.get("os_source"));
            record.set("os_source", WebConstant.SftOsSource.getSftOsSource(os_source).getDesc());
            int is_source_back = TypeUtils.castToInt(record.get("is_source_back"));
            record.set("is_source_back_name", is_source_back==0? "否" : "是");
            int bankkey_status = TypeUtils.castToInt(record.get("bankkey_status"));
            record.set("bankkey_status_name", bankkey_status==0? "关闭" : "启用");
            int subordinate_channel = TypeUtils.castToInt(record.get("subordinate_channel"));
            record.set("subordinate_channel", WebConstant.SftSubordinateChannel.getSftSubordinateChannel(subordinate_channel).getDesc());

        }
        return POIUtil.createExcel(recordList, this);
    }
}
