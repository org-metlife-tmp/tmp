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
 * @Description: 盘片列表导出
 */
public class RecvDiskBackingWorkBook extends AbstractWorkBook {

    public RecvDiskBackingWorkBook() {
        this.optype = "recvdiskbacking_listexport";
        this.titleNames = new String[]{
                "source_sys", "master_batchno", "child_batchno", "interactive_mode", "channel_code",
                "channel_desc", "send_on", "back_on","recv_total_amount", "recv_total_num", "success_amount",
                "success_num","fail_amount","fail_num","status", "back_user_name"
               

        };
        this.titles = new String[]{
                "来源系统", "主批次号", "子批次号", "交互方式", "通道编码", "通道描述", "出盘日期", "回盘日期" , "总金额", "总笔数",
                "成功金额", "成功笔数","失败金额","失败笔数","状态","操作人"
        };
        this.sheetName = "盘片回盘列表";
    }

    @Override
    public Workbook getWorkbook() {
    	Record record = getRecord();
        SqlPara sqlPara = null;
        Long org_id = getUodpInfo().getOrg_id();
		Record findById = Db.findById("organization", "org_id", org_id);
        List<String> codes = new ArrayList<>();
        
		String FH = "FH";
		if(StringUtils.isNotBlank(record.getStr("channel_id"))) {
			Record channel_setting = Db.findById("channel_setting", "id", record.getLong("channel_id"));
			FH = channel_setting.getStr("channel_code");
		}
        
        if (findById.getInt("level_num") == 1) {
            codes = Arrays.asList("0102", "0101", "0201", "0202", "0203", "0204", "0205", "0500");
        } else {
            List<Record> rec = Db.find(Db.getSql("org.getCurrentUserOrgs"), org_id);
            for (Record o : rec) {
                codes.add(o.getStr("code"));
            }
        }
        record.set("codes", codes);
    	List<Integer> status = record.get("status");
    	if (status == null || status.size() == 0) {
			record.set("status", new int[] { WebConstant.SftCheckBatchStatus.FSWHP.getKey(),
					WebConstant.SftCheckBatchStatus.HPCG.getKey(), WebConstant.SftCheckBatchStatus.HPYC.getKey(),
					WebConstant.SftCheckBatchStatus.YHT.getKey() });
		}
        this.fileName = "LA_Return_"+FH+"_"+ RedisSericalnoGenTool.genShortSerial() +"_"+DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
        sqlPara = Db.getSqlPara("recv_disk_backing.findDiskBackingList", Kv.by("map", record.getColumns()));
        List<Record> recordList = Db.find(sqlPara);
        return POIUtil.createExcel(recordList, this);
    }
}
