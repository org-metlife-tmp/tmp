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
public class ChannelSettingWorkBook extends AbstractWorkBook {

    public ChannelSettingWorkBook() {
        this.optype = "sftchannel_listexport";
        this.fileName = "FH_"+DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
        this.titleNames = new String[]{
                "channel_code", "channel_desc", "pay_mode_name", "pay_attr_name", "interactive_mode_name", "card_type",
                "single_amount_limit", "amount_percent", "amount_number", "bankcode", "acc_no", "org_name", "single_file_limit",
                "is_checkout_name", "net_mode", "update_user", "update_on"

        };
        this.titles = new String[]{
                "通道编码", "通道描述", "支付方式", "收付属性", "交互方式", "支持卡种", "single_amount_limit", "手续费（按金额）", "手续费（按笔数）",
                "bankcode", "银行帐号", "文件生成归属地", "单批次文件笔数限制", "状态", "结算模式", "操作人", "操作日期"
        };
        this.sheetName = "通道列表";
    }

    @Override
    public Workbook getWorkbook() {
        SqlPara sqlPara = Db.getSqlPara("channel_setting.channellist", Kv.by("map", getRecord().getColumns()));
        List<Record> recordList = Db.find(sqlPara);
        for(Record record : recordList){
            String pay_mode = TypeUtils.castToString(record.get("pay_mode"));
            record.set("pay_mode_name", WebConstant.SftPayMode.getSftPayModeByKeyc(pay_mode).getDesc());
            int pay_attr = TypeUtils.castToInt(record.get("pay_attr"));
            record.set("pay_attr_name", WebConstant.SftPayAttr.getSftPayAttr(pay_attr).getDesc());
            int interactive_mode = TypeUtils.castToInt(record.get("interactive_mode"));
            record.set("interactive_mode_name", WebConstant.SftInteractiveMode.getSftInteractiveMode(interactive_mode).getDesc());
            int is_checkout = TypeUtils.castToInt(record.get("is_checkout"));
            record.set("is_checkout_name", is_checkout==0? "关闭" : "启用");
            int net_mode = TypeUtils.castToInt(record.get("net_mode"));
            record.set("net_mode", is_checkout==0? "净额模式" : "全额模式");

        }
        return POIUtil.createExcel(recordList, this);
    }
}
