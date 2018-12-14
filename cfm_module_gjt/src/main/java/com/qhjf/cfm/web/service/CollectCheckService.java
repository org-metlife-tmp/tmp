package com.qhjf.cfm.web.service;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.SqlPara;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;

public class CollectCheckService {

    /**
     * 设置可以核对单据的状态
     *
     * @param record
     * @param statusName
     */
    public void setInnerTradStatus(Record record, String statusName) {
        List status = record.get(statusName);
        if (status == null || status.size() == 0) {
            record.set(statusName, new int[]{
                    WebConstant.CollOrPoolRunStatus.SUCCESS.getKey()
            });
        }

    }

    /**
     * 查询 核对/未核对单据
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> checkbillList(int pageNum, int pageSize, Record record) {
        //record.set("delete_flag", 0);
        SqlPara sqlPara = Db.getSqlPara("collect_check.billList", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 根据支/付金额,模糊根据单据查看未核对交易
     *
     * @param record
     * @return
     * @throws ReqDataException
     */
    public List<Record> checkNoCheckTradeList(Record record) throws ReqDataException {
        Long id = TypeUtils.castToLong(record.get("id"));
        Record billList = Db.findById("collect_execute_instruction", "id", id);
        record.remove("id");
        record.set("pay_account_no", billList.get("pay_account_no"));
        record.set("recv_account_no", billList.get("recv_account_no"));
        record.set("collect_amount", billList.get("collect_amount"));
        Date create = TypeUtils.castToDate(billList.get("create_on"));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String create_on = sdf.format(create);
        record.set("create_on", create_on);
        SqlPara sqlPara = Db.getSqlPara("collect_check.nochecktradingList", Kv.by("map", record.getColumns()));
        List<Record> find = Db.find(sqlPara);
        if (null != find && find.size() > 0) {
            for (Record rec : find) {
                Long acc_id = TypeUtils.castToLong(rec.get("acc_id"));
                Record queryPayInfo = this.queryPayInfo(acc_id);
                String bank_name = TypeUtils.castToString(queryPayInfo.get("bank_name"));
                rec.set("bank_name", bank_name);
            }
        }
        return find;
    }

    /**
     * 根据单据id,在中间表内查询准确的已核对交易
     *
     * @param record
     * @return
     * @throws ReqDataException
     */
    public List<Record> checkAlreadyTradeList(Record record) throws ReqDataException {
        List<Record> find = Db.find(Db.getSql("collect_check.confirmTradingList"), record.getInt("id"));
        if (null != find && find.size() > 0) {
            for (Record rec : find) {
                Long acc_id = TypeUtils.castToLong(rec.get("acc_id"));
                Record queryPayInfo = this.queryPayInfo(acc_id);
                String bank_name = TypeUtils.castToString(queryPayInfo.get("bank_name"));
                rec.set("bank_name", bank_name);
            }
        }
        return find;
    }

    /**
     * 确认核对
     *
     * @param record
     * @param userInfo
     * @return
     * @throws Exception
     */
    public Page<Record> confirm(Record record, UserInfo userInfo) throws Exception {
        final Long billId = TypeUtils.castToLong(record.get("bill_id"));

        Record innerRec = Db.findById("collect_execute_instruction", "id", billId);
        if (innerRec == null) {
            throw new ReqDataException("未找到有效的单据!");
        }
        final List<Integer> trade_id = record.get("trade_id");
        if (trade_id.size() != 2) {
            throw new ReqDataException("交易流水应保留支付各一条!");
        }
        List<Record> nums = Db.find(Db.getSqlPara("dbttrad.findNumByTradingNo", Kv.by("tradingNo", trade_id)));
        if (nums.size() != 2) {
            throw new ReqDataException("交易流水应保留支付各一条!");
        }
        final List<Record> records = CommonService.genConfirmRecords(trade_id, billId, userInfo);


        final int old_version = TypeUtils.castToInt(record.get("persist_version"));

        final Map<Integer, Date> tradMap = CommonService.getPeriod(trade_id);//key= transid , value=所属结账日的年月

        //进行数据新增操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                Record set = new Record();
                set.set("persist_version", old_version + 1);
                set.set("is_checked", 1);
                Record where = new Record();
                where.set("id", billId);
                where.set("persist_version", old_version);
                if (CommonService.update("collect_execute_instruction", set, where)) {
                    for (Record r : records) {
                        boolean i = Db.save("collect_trans_checked", r);
                        boolean t = Db.update("acc_his_transaction", "id", new Record().set("id", r.getInt("trans_id")).set("is_checked", 1)
                                .set("ref_bill_id", billId).set("checked_ref", "collect_execute_instruction"));
                        if (!(i && t)) {
                            return false;
                        }
                    }

                    try {
                        //生成凭证信息
                        CheckVoucherService.sunVoucherData(trade_id, billId, WebConstant.MajorBizType.GJT.getKey(), tradMap);
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
        if (!flag) {
            throw new DbProcessException("交易核对失败！");
        } else {
            //返回未核对的单据列表
            Record rd = new Record();
            rd.set("is_checked", 0);
            rd.set("pay_account_org_id", userInfo.getCurUodp().getOrg_id());
            setInnerTradStatus(rd, "collect_status");
            SqlPara sqlPara = Db.getSqlPara("collect_check.billList", Kv.by("map", rd.getColumns()));
            return Db.paginate(1, 10, sqlPara);
        }
    }

    /**
     * 根据付款方id查询付款方信息
     *
     * @param payAccountId
     * @throws ReqDataException
     */
    public Record queryPayInfo(long payAccountId) throws ReqDataException {
        Record payRec = Db.findFirst(Db.getSql("nbdb.findAccountByAccId"), payAccountId);
        if (payRec == null) {
            throw new ReqDataException("未找到有效的付款方帐号!");
        }
        return payRec;
    }
}
