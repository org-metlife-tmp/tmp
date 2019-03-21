package com.qhjf.cfm.web.excelexp;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.web.plugins.excelexp.AbstractWorkBook;
import com.qhjf.cfm.web.plugins.excelexp.POIUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 详情批付盘片列表导出
 */
public class RecvDetailExportWorkBook extends AbstractWorkBook {

    public RecvDetailExportWorkBook() {
    	 this.optype = "recvdiskbacking_detaillistexport";
    	 this.titleNames = new String[]{
                "child_batchno", "back_on","interactive_mode", "channel_code",
                "channel_desc", "pay_acc_name", "pay_acc_no", "amount", "bank_err_code", "bank_err_msg"

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
    	Integer type = record.getInt("type");
    	String sql = Db.getSql("recv_disk_downloading.selectDetailByChildno");
        List<Record> recordList = Db.find(sql,child_batchno);
        if(null == recordList || recordList.size() == 0) {
        	return null;
        }
        String channel_code = recordList.get(0).getStr("channel_code");
        String sbdxfnmb = null  ;
        if(1 == type) {
        	sbdxfnmb = "_Send_";
        }else if(2 == type) {
        	sbdxfnmb = "_Return_";
        }
        this.fileName = child_batchno+sbdxfnmb+channel_code+".xls" ; 
        SymmetricEncryptUtil  util = SymmetricEncryptUtil.getInstance();
        try {
			util.paymask(recordList);
		} catch (UnsupportedEncodingException | BusinessException e) {
			e.printStackTrace();
		}
        return POIUtil.createExcel(recordList, this);
    }
}
