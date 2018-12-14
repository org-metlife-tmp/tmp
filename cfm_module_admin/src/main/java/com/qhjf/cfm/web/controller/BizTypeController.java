package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.BizTypeService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
@Auth(withRoles = {"admin"})
public class BizTypeController extends CFMBaseController {

    private static final LogbackLog log = LogbackLog.getLog(BizTypeController.class);

    private BizTypeService service = new BizTypeService();

//    @Auth(hasForces = {"BizTypeDef"})
    public void list() {
        List<Record> result = new ArrayList<>();
        Map<String, List<WebConstant.MajorBizType>> allMajorBizType =
                WebConstant.BizType.getAllMajorBizType();
        List<Record> list = service.getAllBizTypes();
        for (Map.Entry<String, List<WebConstant.MajorBizType>> entry : allMajorBizType.entrySet()) {
            //一级
            Record record = new Record();
            WebConstant.BizType bizType = WebConstant.BizType.valueOf(entry.getKey());
            record.set("biz_id", "o_" + bizType.getKey());
            record.set("biz_name", bizType.getDesc());
            //二级集合
            List<WebConstant.MajorBizType> majorBizTypes = entry.getValue();
            //构建二级集合
            List<Record> majorBizTypeRecordList = new ArrayList<>();
            for (WebConstant.MajorBizType majorBizType : majorBizTypes) {
                Record m = new Record()
                        .set("biz_id", majorBizType.getKey())
                        .set("biz_name", majorBizType.getDesc())
                        .set("p_id", record.get("biz_id"));
                //构建三级（自定义）集合
                List<Record> bizTypes = new ArrayList<>();
                Iterator<Record> iterator = list.iterator();
                int key = majorBizType.getKey();
                while (iterator.hasNext()) {
                    Record next = iterator.next();
                    int p_id = TypeUtils.castToInt(next.get("p_id"));
                    if (key == p_id) {
                        bizTypes.add(next);
                        iterator.remove();
                    }
                }
                m.set("children", bizTypes);
                majorBizTypeRecordList.add(m);
            }
            record.set("children", majorBizTypeRecordList);
            result.add(record);
        }
        renderOk(result);
    }

//    @Auth(hasForces = {"BizTypeDef"})
    public void add() {
        try {
            Record record = getParamsToRecord();
            service.add(record);
            renderOk(record);
        } catch (BusinessException e) {
            log.error("添加业务类型失败！", e);
            renderFail(e);
        }
    }

//    @Auth(hasForces = {"BizTypeDef"})
    public void chg() {
        try {
            Record record = getParamsToRecord();
            service.chg(record);
            renderOk(record);
        } catch (BusinessException e) {
            log.error("修改业务类型失败！", e);
            renderFail(e);
        }
    }

//    @Auth(hasForces = {"BizTypeDef"})
    public void del() {
        try {
            Record record = getParamsToRecord();
            service.del(record);
            renderOk(null);
        } catch (BusinessException e) {
            log.error("删除业务类型失败！", e);
            renderFail(e);
        }
    }

//    @Auth(hasForces = {"BizTypeDef"})
    public void setstatus() {
        try {
            Record record = getParamsToRecord();
            service.setstatus(record);
            renderOk(null);
        } catch (BusinessException e) {
            log.error("修改业务类型状态失败！", e);
            renderFail(e);
        }
    }

}
