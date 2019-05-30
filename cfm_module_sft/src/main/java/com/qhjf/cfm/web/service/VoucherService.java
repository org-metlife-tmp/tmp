package com.qhjf.cfm.web.service;

import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.DateFormatThreadLocal;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

/**
 * 资金系统月末预提凭证操作
 *
 * @author GJF
 * @date 2019年4月10日
 */
public class VoucherService {

    private static Logger log = LoggerFactory.getLogger(VoucherService.class);
    /**
     * 未核对流水列表
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> voucherlist(int pageNum, int pageSize, final Record record) throws BusinessException {

        SqlPara sqlPara = Db.getSqlPara("voucher.voucherlist", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 根据机构获取用户
     *
     */
    public List<Record> getaccbyorg(Record record) {
        return Db.find(Db.getSqlPara("voucher.getaccbyorg", Kv.by("map", record.getColumns())));
    }

    /**
     * 预提提交
     * @param record
     * @return
     * @throws DbProcessException
     * @throws ReqDataException
     */
    public void presubmit(final Record record, final UserInfo userInfo, final UodpInfo uodpInfo) throws BusinessException {

        final List<Integer> idList = record.get("id");
        /** 只有未预提且未核对的交易才能预提 */
        List<Record> trads = Db.find(Db.getSqlPara("voucher.findLoadSubmitTrad", Kv.by("map", record.getColumns())));
        if (trads!=null && trads.size()!=idList.size()) {
            throw new ReqDataException("只有未预提且未核对的交易才能预提!");
        }
        final List<Record> tradlist = Db.find(Db.getSqlPara("voucher.findTrad", Kv.by("map", record.getColumns())));

        if(tradlist==null || tradlist.size()==0){
            throw new ReqDataException("未找到记录!");
        }

        //进行数据新增操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                try{
                    for (Record r : tradlist) {
                        Record extRecord = new Record();
                        String seqnoOrstatmentCode = RedisSericalnoGenTool.genVoucherSeqNo();//生成十六进制流水号
                        extRecord.set("trans_id", r.getLong("id"));
                        extRecord.set("presubmit_date", new Date());
                        extRecord.set("presubmit_user_name", userInfo.getName());
                        extRecord.set("presubmit_code", seqnoOrstatmentCode);
                        extRecord.set("period_date", DateFormatThreadLocal.format("YYYY-MM",CommonService.getPeriodByCurrentDay(r.getDate("trans_date"))));
                        extRecord.set("create_on", new Date());
                        extRecord.set("precondition", WebConstant.PreSubmitStatus.YTFHZ.getKey());
                        boolean i = Db.save("acc_his_transaction_ext", extRecord);
                        if (!i) {
                            return false;
                        }
                    }
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        });
        if (!flag) {
            throw new DbProcessException("预提失败！");
        }
    }

    /**
     * 预提提交确认
     * @param record
     * @return
     * @throws DbProcessException
     * @throws ReqDataException
     */
    public void presubmitconfirm(final Record record, final UserInfo userInfo, final UodpInfo uodpInfo) throws BusinessException {

        final List<Integer> idList = record.get("id");
        /** 确认复核中的状态才能预提确认 */
        List<Record> trads = Db.find(Db.getSqlPara("voucher.findConfirmSubmitTrad", Kv.by("map", record.getColumns())));
        if (trads!=null && trads.size()!=idList.size()) {
            throw new ReqDataException("只能确认已预提的交易!");
        }
        final List<Record> tradlist = Db.find(Db.getSqlPara("voucher.findTradList", Kv.by("map", record.getColumns())));
        if(tradlist==null || tradlist.size()==0){
            throw new ReqDataException("未找到记录!");
        }

        //进行数据新增操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                for (Record r : tradlist) {
                    if(CommonService.updateRows("acc_his_transaction_ext",
                            new Record().set("precondition", WebConstant.PreSubmitStatus.YYT.getKey())
                                .set("presubmit_confirm_date", new Date())
                                .set("presubmit_confirm_user_name", userInfo.getName()),
                            new Record().set("id", r.getLong("extId"))) != 1){
                        return false;
                    }
                }
                try {
                    //生成凭证信息
                    CheckVoucherService.sunVoucherData(tradlist, WebConstant.MajorBizType.CWYTJ.getKey());
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        });
        if (!flag) {
            throw new DbProcessException("预提确认失败！");
        }
    }

    /**
     * 撤销提交
     * @param record
     * @return
     * @throws DbProcessException
     * @throws ReqDataException
     */
    public void precancel(final Record record, final UserInfo userInfo, final UodpInfo uodpInfo) throws BusinessException {

        final List<Integer> idList = record.get("id");
        /** 当天的已经预提且预提通过的交易才能撤销 */
        List<Record> trads = Db.find(Db.getSqlPara("voucher.findLoadCancelTrad", Kv.by("map", record.getColumns())));
        if (trads!=null && trads.size()!=idList.size()) {
            throw new ReqDataException("只能撤销当天且已预提的交易!");
        }
        final List<Record> tradlist = Db.find(Db.getSqlPara("voucher.findTradList", Kv.by("map", record.getColumns())));
        if(tradlist==null || tradlist.size()==0){
            throw new ReqDataException("未找到记录!");
        }

        //进行数据新增操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                for (Record r : tradlist) {
                    if(CommonService.updateRows("acc_his_transaction_ext",
                            new Record().set("precondition", WebConstant.PreSubmitStatus.CXFHZ.getKey())
                                    .set("cancel_date", new Date())
                                    .set("cancel_user_name", userInfo.getName()),
                            new Record().set("id", r.getLong("extId"))) != 1){
                        return false;
                    }
                }
                return true;
            }
        });
        if (!flag) {
            throw new DbProcessException("撤销失败！");
        }
    }

    /**
     * 撤销提交确认
     * @param record
     * @return
     * @throws DbProcessException
     * @throws ReqDataException
     */
    public void precancelconfirm(final Record record, final UserInfo userInfo, final UodpInfo uodpInfo) throws BusinessException {

        final List<Integer> idList = record.get("id");
        /** 是否都是确认复核中的状态 */
        List<Record> trads = Db.find(Db.getSqlPara("voucher.findConfirmCancelTrad", Kv.by("map", record.getColumns())));
        if (trads!=null && trads.size()!=idList.size()) {
            throw new ReqDataException("只能确认已撤销的交易!");
        }
        final List<Record> tradlist = Db.find(Db.getSqlPara("voucher.findTradList", Kv.by("map", record.getColumns())));
        if(tradlist==null || tradlist.size()==0){
            throw new ReqDataException("未找到记录!");
        }

        //进行数据新增操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                for (Record r : tradlist) {
                    if(CommonService.updateRows("acc_his_transaction_ext",
                            new Record().set("precondition", WebConstant.PreSubmitStatus.YCHEX.getKey())
                                    .set("cancel_confirm_date", new Date())
                                    .set("cancel_confirm_user_name", userInfo.getName()),
                            new Record().set("id", r.getLong("extId"))) != 1){
                        return false;
                    }
                }
                //删除凭证表中的记录
                int delNums = Db.update(Db.getSqlPara("voucher.delVoucherList", Kv.by("map", record.getColumns())));
                if(delNums!=idList.size()*2){
                    return false;
                }
                return true;
            }
        });
        if (!flag) {
            throw new DbProcessException("撤销确认失败！");
        }
    }

}
