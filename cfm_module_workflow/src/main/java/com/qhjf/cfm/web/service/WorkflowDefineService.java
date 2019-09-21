package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.constant.WorkflowConstant;

import java.sql.SQLException;
import java.util.Date;

/**
 * Created by zhangsq on 2018/7/4.
 */
public class WorkflowDefineService {

    public Record add(Record record, final long create_by) throws BusinessException {
        String workflow_name = TypeUtils.castToString(record.get("workflow_name"));
        //检查流程名称是否使用
        checkWorkFlowName(workflow_name);
        final Record base_info = new Record()
                .set("workflow_name", workflow_name)
                .set("workflow_type", WorkflowConstant.WorkFlowType.NORMAL.getKey())
                .set("laster_version", 0)
                .set("is_activity", 1)
                .set("create_on", new Date())
                .set("create_by", create_by)
                .set("persist_version", 0)
                .set("delete_flag", WebConstant.YesOrNo.NO.getKey());
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //插入工作流定义基础信息
                boolean base = Db.save("cfm_workflow_base_info", "id", base_info);
                return base;
            }
        });
        if (!flag) {
            throw new DbProcessException("保存工作流基础信息失败！");
        }
        return base_info;
    }

    private void checkWorkFlowName(String name) throws BusinessException {
        Record record = Db.findById("cfm_workflow_base_info", "workflow_name", name);
        if (null != record) {
            throw new ReqDataException("流程名称已使用！");
        }
    }

    public Page<Record> getPage(int page_num, int page_size, Record record) {
        SqlPara sqlPara = Db.getSqlPara("define.getPage", Kv.by("map", record.getColumns()));
        Page<Record> page = Db.paginate(page_num, page_size, sqlPara);
        return page;
    }

    public void del(final Record record) throws BusinessException {
        Long id = TypeUtils.castToLong(record.get("id"));
        Record baseRec = Db.findById("cfm_workflow_base_info", "id", id);

        record.set("delete_flag", WebConstant.YesOrNo.YES.getKey());
        record.set("workflow_name", TypeUtils.castToString(baseRec.get("workflow_name")) + "_" + id);

        //检查流程是否已使用
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return Db.update("cfm_workflow_base_info", "id", record);
            }
        });
        if (!flag) {
            throw new DbProcessException("删除流程失败！");
        }
    }

    /**
     * 设置流程状态失败
     *
     * @param record
     * @param userInfo
     * @throws BusinessException
     */
    public Record setstatus(final Record record, final UserInfo userInfo) throws BusinessException {
        final Long id = TypeUtils.castToLong(record.get("id"));

        //根据id查询工作流定义基础信息
        final Record baseRec = Db.findById("cfm_workflow_base_info", "id", id);
        if (baseRec == null) {
            throw new ReqDataException("未找到有效的流程定义信息!");
        }

        final int old_version = TypeUtils.castToInt(record.get("persist_version"));
        int isActivity = TypeUtils.castToInt(baseRec.get("is_activity"));

        record.set("persist_version", old_version + 1);
        record.set("is_activity", isActivity == WebConstant.YesOrNo.NO.getKey() ? WebConstant.YesOrNo.YES.getKey() : WebConstant.YesOrNo.NO.getKey());
        record.set("update_on", new Date());
        record.set("update_by", userInfo.getUsr_id());

        //移除id。兼容sqlserver
        record.remove("id");

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                return CommonService.update("cfm_workflow_base_info", record, new Record().set("id", id).set("persist_version", old_version));
            }
        });
        if (flag) {
            return Db.findById("cfm_workflow_base_info", "id", id);
        }
        throw new DbProcessException("状态设置失败!");
    }
}
