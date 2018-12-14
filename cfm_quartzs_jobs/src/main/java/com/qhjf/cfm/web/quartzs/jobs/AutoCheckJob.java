package com.qhjf.cfm.web.quartzs.jobs;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.service.CheckVoucherService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Map;

public class AutoCheckJob implements Job {

    private static Logger log = LoggerFactory.getLogger(AutoCheckJob.class);

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        log.debug("【自动核对任务---begin】");
        //支付通
        updateOneTrans("auto_check_cfm.outer_zf_payment_list", "outer_zf_payment",
                "id", "outer_pay_trans_checked", WebConstant.MajorBizType.ZFT.getKey());
        //OA总公司
        updateOneTrans("auto_check_cfm.oa_head_payment_list", "oa_head_payment",
                "id", "oa_head_payment_checked", WebConstant.MajorBizType.OA_HEAD_PAY.getKey());
        //OA分公司下拨
        updateTwoTrans("auto_check_cfm.oa_branch_payment_item_inlist", "oa_branch_payment_item",
                "id", "oa_branch_payment_checked", WebConstant.MajorBizType.OA_BRANCH_PAY.getKey());
        //OA分公司对外支付
        updateOneTrans("auto_check_cfm.oa_branch_payment_item_outlist", "oa_branch_payment_item",
                "id", "oa_branch_payment_checked", WebConstant.MajorBizType.OA_BRANCH_PAY.getKey());
        //归集通
        updateTwoTrans("auto_check_cfm.collect_execute_instruction_list", "collect_execute_instruction",
                "id", "collect_trans_checked", WebConstant.MajorBizType.GJT.getKey());
        //调拨通
        updateTwoTrans("auto_check_cfm.inner_db_payment_list", "inner_db_payment",
                "id", "inner_pay_trans_checked", WebConstant.MajorBizType.INNERDB.getKey());

        //调拨通--批量
        updateTwoTrans("auto_check_cfm.inner_batchpay_list", "inner_batchpay_bus_attach_detail",
                "detail_id", "inner_batchpay_trans_checked", WebConstant.MajorBizType.INNERDB_BATCH.getKey());
        //支付通--批量
        updateOneTrans("auto_check_cfm.outer_batchpay_list", "outer_batchpay_bus_attach_detail",
                "detail_id", "outer_batchpay_trans_checked", WebConstant.MajorBizType.OBP.getKey());

        log.debug("【自动核对任务---end】");
    }

    public void updateOneTrans(String sqlName, final String tableName, final String key,
                               final String checkTable, final int biz_type) {
        log.info("sqlName:=" + sqlName);
        List<Record> records = Db.find(Db.getSql(sqlName));
        int count = 0;
        for (Record record : records) {
            final List<Record> trans = Db.find(Db.getSql("auto_check_cfm.get_his_trans"),
                    record.get("payment_amount"), record.get("instruct_code"),
                    record.get("create_on"));
            final Integer billId = record.getInt("id");
            if (trans == null || trans.size() != 1) {
                continue;
            }
            boolean flag = Db.tx(new IAtom() {
                public boolean run() throws SQLException {
                    if (Db.update(tableName, key, new Record().set(key, billId).set("is_checked", 1))) {
                        ArrayList<Integer> transIdList = new ArrayList<Integer>();
                        for (int i = 0; i < trans.size(); i++) {
                            Record recd = trans.get(i);
                            Integer transId = recd.getInt("id");
                            boolean h = Db.save(checkTable, new Record().set("bill_id", billId)
                                    .set("trans_id", transId)
                                    .set("create_on", new Date()).set("create_by", null).set("delete_flag", 0));

                            boolean t = Db.update("acc_his_transaction", "id",
                                    new Record().set("id", transId).set("is_checked", 1)
                                            .set("ref_bill_id", billId).set("checked_ref", tableName));
                            if (!(h && t)) {
                                return false;
                            }
                            transIdList.add(transId);
                        }
                        try {
                            Map<Integer, Date> tradMap = CommonService.getPeriod(transIdList);//key= transid , value=所属结账日的年月
                            //生成凭证信息
                            CheckVoucherService.sunVoucherData(transIdList, billId, biz_type, tradMap);
                        } catch (BusinessException e) {
                            e.printStackTrace();
                            return false;
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            if (flag) {
                count++;
            }
        }
        log.info("已经对【" + tableName + "】核对了【" + count + "】条记录");
    }

    public void updateTwoTrans(String sqlName, final String tableName, final String key,
                               final String checkTable, final int biz_type) {
        List<Record> records = Db.find(Db.getSql(sqlName));
        int count = 0;
        for (Record record : records) {
            final List<Record> trans = Db.find(Db.getSqlPara("auto_check_cfm.get_his_trans_two", Kv.by("map", record.getColumns())));
            final Integer billId = record.getInt("id");
            if (trans == null || trans.size() != 2) {
                continue;
            }
            boolean flag = Db.tx(new IAtom() {
                public boolean run() throws SQLException {
                    if (Db.update(tableName, key, new Record().set(key, billId).set("is_checked", 1))) {
                        ArrayList<Integer> transIdList = new ArrayList<Integer>();
                        for (int i = 0; i < trans.size(); i++) {
                            Record recd = trans.get(i);
                            Integer transId = recd.getInt("id");
                            boolean h = Db.save(checkTable, new Record().set("bill_id", billId)
                                    .set("trans_id", transId)
                                    .set("create_on", new Date()).set("create_by", null).set("delete_flag", 0));

                            boolean t = Db.update("acc_his_transaction", "id",
                                    new Record().set("id", transId).set("is_checked", 1)
                                            .set("ref_bill_id", billId).set("checked_ref", tableName));
                            if (!(h && t)) {
                                return false;
                            }
                            transIdList.add(transId);
                        }
                        try {
                            Map<Integer, Date> tradMap = CommonService.getPeriod(transIdList);//key= transid , value=所属结账日的年月
                            //生成凭证信息
                            CheckVoucherService.sunVoucherData(transIdList, billId, biz_type, tradMap);
                        } catch (BusinessException e) {
                            e.printStackTrace();
                            return false;
                        }
                        return true;
                    } else {
                        return false;
                    }
                }
            });
            if (flag) {
                count++;
            }
        }
        log.info("已经对【" + tableName + "】核对了【" + count + "】条记录");
    }

}
