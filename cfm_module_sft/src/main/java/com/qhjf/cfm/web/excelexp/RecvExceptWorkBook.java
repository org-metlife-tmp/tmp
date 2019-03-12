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
import com.qhjf.cfm.web.service.AccCommonService;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @Description: 批量收异常数据导出
 */
public class RecvExceptWorkBook extends AbstractWorkBook {

    public RecvExceptWorkBook() {
        this.optype = "sftrecvexcept_listexport";
        this.titleNames = new String[]{
                "source_sys", "master_batchno", "child_batchno", "interactive_mode", "channel_code",
                "channel_desc", "send_on", "total_amount", "total_num", "service_status", "error_msg",
                "revoke_user_name", "revoke_date", "exam_position_name", "exam_time"
        };
        this.titles = new String[]{
                "来源系统", "主批次号", "子批次号", "交互方式", "通道编码", "通道描述", "出盘日期", "总金额", "总笔数", "状态", "异常原因"
                , "回退申请人", "申请日期", "审批人", "审批日期"
        };
        this.sheetName = "批量收异常数据列表";
    }

    @Override
    public Workbook getWorkbook() {
        Record record = getRecord();
        int osSource = TypeUtils.castToInt(record.get("source_sys"));
        SqlPara sqlPara = null;

        Long org_id = getUodpInfo().getOrg_id();
        Record findById = Db.findById("organization", "org_id", org_id);
        List<String> codes = new ArrayList<>();
        if(findById.getInt("level_num") == 1){
            codes = Arrays.asList("0102","0101","0201","0202","0203","0204","0205","0500");
        }else{
            List<Record> rec = Db.find(Db.getSql("org.getCurrentUserOrgs"), org_id);
            for (Record o : rec) {
                codes.add(o.getStr("code"));
            }
        }

        record.set("codes", codes);
        AccCommonService.setExceptStatus(record, "service_status");
        if(WebConstant.SftOsSource.LA.getKey() == osSource){
            //LA
            this.fileName = "LA_Abnormal_FH_" + RedisSericalnoGenTool.genShortSerial() + "_"
                    +DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
        }else if(WebConstant.SftOsSource.EBS.getKey() == osSource){
            //EBS
            this.fileName = "EBS_Abnormal_FH_" + RedisSericalnoGenTool.genShortSerial() + "_"
                    +DateKit.toStr(new Date(), "YYYYMMdd")+".xls";
        }
        List<Record> recordList = Db.find(Db.getSqlPara("recvexcept.exceptlist", Kv.by("map", record.getColumns())));
        for(Record rd : recordList){
            rd.set("source_sys", WebConstant.SftOsSource.getSftOsSource(osSource).getDesc());
            int interactive_mode = TypeUtils.castToInt(rd.get("interactive_mode"));
            rd.set("interactive_mode", WebConstant.SftInteractiveMode.getSftInteractiveMode(interactive_mode).getDesc());
            int status = TypeUtils.castToInt(rd.get("service_status"));
            rd.set("service_status", WebConstant.SftCheckBatchStatus.getByKey(status).getDesc());

        }
        return POIUtil.createExcel(recordList, this);
    }
}
