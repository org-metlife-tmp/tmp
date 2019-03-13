package com.qhjf.cfm.web.excelexp;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 详情盘片列表导出
 */
public class DetailExportWorkBook extends AbstractWorkBook {

    public DetailExportWorkBook() {
        this.optype = "disksending_detaillistexport";
        this.titleNames = new String[]{
                 "child_batchno", "back_on","interactive_mode", "channel_code",
                "channel_desc", "recv_acc_name", "recv_acc_no", "amount", "bank_err_code", "bank_err_msg"

        };
        this.titles = new String[]{
                "子批次号","回盘日期" ,"交互方式", "通道编码", "通道描述", "客户姓名", "客户账号", "金额",
                "交易状态", "原因"
        };
        this.sheetName = "盘片详情列表";
    }

    @Override
    public Workbook getWorkbook() {
    	Record record = getRecord();
    	String child_batchno = record.getStr("child_batchno"); 
    	String sql = Db.getSql("disk_downloading.selectDetailByChildno");
        List<Record> recordList = Db.find(sql,child_batchno);
        this.fileName = "12o3jmsef"+".xls" ; 
        return POIUtil.createExcel(recordList, this);
    }
}
