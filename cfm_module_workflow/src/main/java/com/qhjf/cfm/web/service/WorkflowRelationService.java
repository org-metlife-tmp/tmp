package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;

import java.sql.SQLException;
import java.util.Date;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/7/11
 * @Description: 工作流关系绑定
 */
public class WorkflowRelationService {

    /**
     * 工作流绑定列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> list(int pageNum, int pageSize, final Record record) {
        SqlPara sqlPara = Db.getSqlPara("relation.findRelationToPage", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 新增配置审批流程
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public Record add(final Record record) throws BusinessException {
        final Record relationRec = new Record();
        final long baseId = TypeUtils.castToLong(record.get("base_id"));

        validateBase(baseId);

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                relationRec.set("base_id", baseId);
                relationRec.set("name", TypeUtils.castToString(record.get("name")));
                relationRec.set("create_on", new Date());
                relationRec.set("org_exp", TypeUtils.castToString(record.get("org_exp")));
                relationRec.set("dept_exp", TypeUtils.castToString(record.get("dept_exp")));
                relationRec.set("biz_exp", TypeUtils.castToString(record.get("biz_exp")));
                relationRec.set("biz_setting_exp",TypeUtils.castToString(record.get("biz_setting_exp")));
                relationRec.set("persist_version", 0);

                return Db.save("cfm_workflow_relation", "id", relationRec);
            }
        });

        if (flag) {
            return Db.findById("cfm_workflow_relation", "id", TypeUtils.castToLong(relationRec.get("id")));
        }

        throw new DbProcessException("配置审批流程失败!");
    }

    /**
     * 修改配置审批流程
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public Record chg(final Record record) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));

        //根据id查询绑定明细信息
        final Record relationRec = Db.findById("cfm_workflow_relation", "id", id);
        if (relationRec == null) {
            throw new ReqDataException("未找到有效的配置流程信息!");
        }

        validateBase(TypeUtils.castToLong(relationRec.get("base_id")));

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {

                //修改关系绑定详细信息
                int old_version = TypeUtils.castToInt(record.get("persist_version"));
                record.set("persist_version", old_version + 1);

                //移除ID，兼容sqlserver
                record.remove("id");

                return CommonService.update("cfm_workflow_relation",record,new Record().set("id", id).set("persist_version", old_version));

            }
        });
        if (flag) {
            return Db.findById("cfm_workflow_relation", "id", TypeUtils.castToLong(relationRec.get("id")));
        }

        throw new DbProcessException("配置审批流程失败!");
    }

    /**
     * 删除配置审批流程
     *
     * @param record
     * @throws BusinessException
     */
    public void del(final Record record) throws BusinessException {
        final long id = TypeUtils.castToLong(record.get("id"));
        final int version = TypeUtils.castToInt(record.get("persist_version"));

        final Record detailRec = Db.findById("cfm_workflow_relation", "id", id);
        if (detailRec == null) {
            throw new ReqDataException("未找到有效的配置流程信息!");
        }

        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                int result = Db.delete(Db.getSql("relation.delRelationById"), id, version);
                return result > 0;
            }
        });

        if (!flag) {
            throw new DbProcessException("删除配置审批流程失败！");
        }

    }

    public void validateBase(long baseId) throws ReqDataException {
        Record baseRec = Db.findById("cfm_workflow_base_info", "id", baseId);
        if (baseRec == null) {
            throw new ReqDataException("未找到有效的流程定义信息!");
        }
    }
}
