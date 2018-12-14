package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.constant.WebConstant;

import java.sql.SQLException;

import static com.qhjf.cfm.web.controller.CFMBaseController.*;

/**
 * 基础数据 - 部门
 *
 * @auther zhangyuanyuan
 * @create 2018/5/23
 */

public class BaseDataDeptService {

    /**
     * 获取部门列表
     *
     * @param record
     * @return
     */
    public Page<Record> findDeptPage(int pageNum,int pageSize,final Record record) {

        SqlPara sqlPara = Db.getSqlPara("department.getDepartmentList", Kv.by("cond", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 添加部门信息(状态默认为 1：正常)
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public Record add(final Record record) throws BusinessException {
        record.remove("dept_id");
        uniqueNameAdd(record);
        boolean flag = Db.tx(new IAtom() {

            @Override
            public boolean run() throws SQLException {
                record.set("status", WebConstant.OrgDeptStatus.NORMAL.getKey());

                boolean flag = Db.save("department", "dept_id", record);
                if (flag) {
                    return true;
                }
                return false;
            }
        });

        if (flag) {
            return findDeptById(TypeUtils.castToLong(record.get("dept_id")));
        }

        throw new DbProcessException("添加部门失败!");
    }

    /**
     * 修改部门信息
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public Record update(final Record record) throws BusinessException {
        Long deptId = TypeUtils.castToLong(record.get("dept_id"));

        //校验该部门是否存在
        Record dept = findDeptById(deptId);
        if (dept == null) {
            throw new ReqDataException("未找到有效的部门信息!");
        }

        uniqueNameChg(record);

        boolean flag = updateDeptInfo(record);

        if (flag) {
            return findDeptById(deptId);
        }

        throw new DbProcessException("修改部门失败!");
    }

    /**
     * 删除部门信息（逻辑删除，将部门状态改为  3：删除）
     *
     * @param record
     * @return
     * @throws BusinessException
     */
    public void delete(final Record record) throws BusinessException {
        Long deptId = TypeUtils.castToLong(record.get("dept_id"));

        //校验该部门是否存在
        Record dept = findDeptById(deptId);
        if (dept == null) {
            throw new ReqDataException("未找到有效的部门信息!");
        }

        //校验部门是否被使用
        String usesql = Db.getSql("department.checkDeptUse");
        long checkuse = Db.queryLong(usesql, deptId);
        if (checkuse > 0) {
            throw new ReqDataException("此部门已被用户使用，不可直接删除！");
        }

        record.set("status", WebConstant.OrgDeptStatus.DELETE.getKey());
        record.set("name", TypeUtils.castToString(dept.get("name")) + "_" + TypeUtils.castToLong(dept.get("dept_id")));
        boolean flag = updateDeptInfo(record);
        if (!flag) {
            throw new DbProcessException("删除部门失败!");
        }

    }

    /**
     * 修改部门状态（1：正常  2：禁用）
     *
     * @param record
     * @return
     */
    public Record setstatus(final Record record) throws BusinessException {
        Long deptId = TypeUtils.castToLong(record.get("dept_id"));

        //校验该部门是否存在
        Record dept = findDeptById(deptId);
        if (dept == null) {
            throw new ReqDataException("未找到有效的部门信息!");
        }

        Integer status = TypeUtils.castToInt(dept.get("status"));

        if (status == WebConstant.OrgDeptStatus.NORMAL.getKey()) {//正常
            record.set("status", WebConstant.OrgDeptStatus.DISUSE.getKey());//禁用
        } else {
            record.set("status", WebConstant.OrgDeptStatus.NORMAL.getKey());
        }

        boolean flag = updateDeptInfo(record);

        if (flag) {
            return findDeptById(deptId);
        }

        throw new DbProcessException("修改部门状态失败!");
    }

    /**
     * 根据部门id查询部门信息
     *
     * @param id
     * @return
     */
    public Record findDeptById(Long id) {
        String sql = Db.getSql("department.findDepartmentById");
        return Db.findFirst(sql, id, WebConstant.OrgDeptStatus.DELETE.getKey());
    }

    /**
     * 修改部门信息
     *
     * @param record
     * @return
     */
    public boolean updateDeptInfo(final Record record) {
        return Db.tx(new IAtom() {

            @Override
            public boolean run() throws SQLException {

                boolean flag = Db.update("department", "dept_id", record);
                if (flag) {
                    return true;
                }
                return false;
            }
        });
    }

    private void uniqueNameAdd(Record record) throws ReqDataException {
        long re = Db.queryLong(Db.getSql("department.getDeptNumByName"), record.get("name"));
        if (re > 0) {
            throw new ReqDataException("系统已存在相同部门名称" + record.get("name") + "，请重新修改后保存！");
        }
    }

    private void uniqueNameChg(Record record) throws ReqDataException {
        long re = Db.queryLong(Db.getSql("department.getDeptNumByNameExcludeId"), record.get("name"), record.get("dept_id"));
        if (re > 0) {
            throw new ReqDataException("系统已存在相同部门名称" + record.get("name") + "，请重新修改后保存！");
        }
    }
}
