package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 柜面付 结算对账
 *
 * @author GJF
 * @date 2019年03月14日
 */
public class PayCounterCheckService {
    private static Logger log = LoggerFactory.getLogger(ChannelSettingService.class);
    /**
     * 查询所有批次
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> batchlist(int pageNum, int pageSize, final Record record) throws BusinessException,UnsupportedEncodingException {
        SymmetricEncryptUtil util = SymmetricEncryptUtil.getInstance();
        String recv_account_no = record.getStr("recv_account_no");
        recv_account_no = util.encrypt(recv_account_no);
        record.set("recv_account_no", recv_account_no);
        SqlPara sqlPara = Db.getSqlPara("paycountercheck.paylist", Kv.by("map", record.getColumns()));
        Page<Record> paginate = Db.paginate(pageNum, pageSize, sqlPara);
        List<Record> list = paginate.getList();
        util.recvmask(list);
        return paginate;
    }

    /**
     * 查找交易流水
     * @param record
     * @return
     * @throws BusinessException
     */
    public List<Record> tradingListNoAuto(final Record record) {
        SqlPara sqlPara = Db.getSqlPara("paycountercheck.tradingList", Kv.by("map", record.getColumns()));
        return Db.find(sqlPara);
    }

    /**
     * 查找交易流水
     * @param record
     * @return
     * @throws BusinessException
     */
    public List<Record> tradingListAuto(final Record record) throws BusinessException, UnsupportedEncodingException {
        /**
         * (1)当业务流水记录行被选中时，银行流水表中按照以下先后顺序查询，如查到记录则完成匹配查询，把结果显示在银行流水表中
         *     (a) 按照指令码匹配查询。
         *     (b) 当(a)没有查到记录，按“付款银行+付款金额+对方银行账号+银行流水中的交易日期晚于等于业务流水操作日期”查询。
         * (2)当查询到单条记录时，流水银行表中该记录自动勾选（可手工撤销勾选）;
         * (3)当查询到多条记录，流水银行表中多条记录均不自动勾选（可手工勾选）;
         */
        //根据单据id获取对账码
        Record bill = Db.findById("gmf_bill","id",TypeUtils.castToInt(record.get("id")));
        List<Record> hisList = null;
        if(bill == null){
            throw new ReqDataException("未找到匹配的单据！");
        }
        String instructCode = TypeUtils.castToString(bill.get("instruct_code"));
        if(StringUtils.isNotEmpty(instructCode)){
            //根据指令码查询交易表数据
            hisList = Db.find(Db.getSql("paycountercheck.getHisByInstructCode"), bill.get("amount"),
                    instructCode, bill.get("send_on"));
            if(hisList!=null && hisList.size()>0){
                return hisList;
            }
        }
        SymmetricEncryptUtil util = SymmetricEncryptUtil.getInstance();
        bill.set("recv_account_no", new String(util.decrypt(bill.getStr("recv_account_no")), "utf-8"));
        return Db.find(Db.getSqlPara("paycountercheck.getHisByInfos", Kv.by("map", bill.getColumns())));
    }

    /**
     * 对账确认
     * @param record
     * @param userInfo
     * @return
     * @throws BusinessException
     */
    public Page<Record> confirm(final Record record, final UserInfo userInfo) throws BusinessException {
        //交易id
        final ArrayList<Integer> tradingNo = record.get("trading_no");
        //单据
        final Record bill = Db.findById("gmf_bill","id",TypeUtils.castToInt(record.get("id")));
        //获取勾选的交易
        final List<Record> tradList = Db.find(Db.getSqlPara("paycheck.findTradList", Kv.by("tradingNo", tradingNo)));

        if(bill==null || tradList.size()==0){
            throw new ReqDataException("请选择要核对的批次和交易！");
        }

        BigDecimal billAmount = TypeUtils.castToBigDecimal(bill.get("amount"));
        int isChecked = TypeUtils.castToInt(bill.get("is_checked"));
        if(isChecked == 1){
            throw new ReqDataException("已勾选的存在已核对的单据！");
        }
        List<Record> tradSize = Db.find(Db.getSqlPara("paycheck.findTradListBusiness",
                Kv.by("map", new Record().set("is_checked", 0).set("tradingNo", tradingNo).getColumns())));

        if(tradSize.size() != tradingNo.size()){
            throw new ReqDataException("存在已核对的交易再次进行核对！");
        }

        BigDecimal tradAmount = new BigDecimal(0);
        //交易付方向总金额-交易收方向总金额=批次成功付款金额，才可核对
        for(Record r : tradList){
            int direction = TypeUtils.castToInt(r.get("direction"));
            //1付 2收
            if(direction == 1){
                tradAmount = tradAmount.add(TypeUtils.castToBigDecimal(r.get("amount")));
            }else if(direction == 2){
                tradAmount = tradAmount.subtract(TypeUtils.castToBigDecimal(r.get("amount")));
            }
        }
        if(billAmount.compareTo(tradAmount) != 0){
            throw new ReqDataException("单据金额和交易金额不相同！");
        }

        //生成对账流水号
        final String checkSerialSeqNo = RedisSericalnoGenTool.genCheckSerialSeqNo();//生成十六进制流水号
        final List<Record> records = CommonService.genPayCounterConfirmRecords(TypeUtils.castToInt(record.get("id")), tradingNo, userInfo, checkSerialSeqNo);

        //进行数据新增操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //更新交易
                String seqnoOrstatmentCode = RedisSericalnoGenTool.genVoucherSeqNo();//生成十六进制序列号/凭证号
                for(Integer trad : tradingNo){
                    boolean s = CommonService.update("acc_his_transaction",
                                    new Record().set("is_checked", 1)
                                                .set("statement_code", seqnoOrstatmentCode)
                                                .set("check_service_number", checkSerialSeqNo)
                                                .set("check_user_id", userInfo.getUsr_id())
                                                .set("check_user_name", userInfo.getName())
                                                .set("check_date", new Date()),
                                    new Record().set("id", trad));
                    if(!s){
                        return false;
                    }
                }
                //更新单据
                boolean s = CommonService.update("gmf_bill",
                        new Record().set("is_checked", 1)
                                .set("persist_version", bill.getInt("persist_version")+1)
                                .set("statement_code", seqnoOrstatmentCode)
                                .set("check_service_number", checkSerialSeqNo)
                                .set("check_user_id", userInfo.getUsr_id())
                                .set("check_user_name", userInfo.getName())
                                .set("check_date", new Date()),
                        new Record().set("id", bill.getInt("id")).set("persist_version", bill.getInt("persist_version")));
                if(!s){
                    return false;
                }
                //存储关系
                for (int num=0; num<records.size(); num++) {
                    Record r = records.get(num);
                    boolean i = Db.save("pay_gmf_checked", r);
                    if (!i) {
                        return false;
                    }
                }
                try {
                    //生成凭证信息
                    return CheckVoucherService.sunVoucherData(bill, tradingNo, tradList, WebConstant.MajorBizType.GMF.getKey(), seqnoOrstatmentCode, userInfo);
                } catch (BusinessException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        });
        if (!flag) {
            throw new DbProcessException("结算对账失败！");
        } else {
            //返回批次列表
            Record rd = new Record();
            AccCommonService.setSftCheckStatus(record, "service_status");
            SqlPara sqlPara = Db.getSqlPara("paycountercheck.paylist", Kv.by("map", rd.getColumns()));
            return Db.paginate(1, 20, sqlPara);
        }
    }

    /**
     * 根据批次查询明细
     *
     * @param record
     * @return
     * @throws ReqDataException
     */
    public List<Record> getdetailbybaseid(Record record) throws BusinessException {
        int source_sys = TypeUtils.castToInt(record.get("source_sys"));
        String sql = "";
        if(source_sys == WebConstant.SftOsSource.LA.getKey()){
            sql = Db.getSql("paycheck.getladetailbybatchno");
        }else if(source_sys == WebConstant.SftOsSource.EBS.getKey()){
            sql = Db.getSql("paycheck.getebsdetailbybatchno");
        }
        long base_id = TypeUtils.castToLong(record.get("id"));
        List<Record> records = Db.find(sql, base_id);
        return records;
    }

    /**
     * 根据批次查询交易
     *
     * @param record
     * @return
     * @throws ReqDataException
     */
    public Record gettradbybatchno(Record record) throws BusinessException {
        long batchNo = TypeUtils.castToLong(record.get("batchno"));
        Record returnRecord = Db.findFirst(Db.getSql("paycheck.gettradbybatchno"), batchNo);
        return returnRecord;
    }

    /**
     * 查找所有银行账号
     */
    public List<Record> getallaccountno(Record record) {
        List<Record> records = Db.find(Db.getSql("channel_setting.getallaccountno"));
        return records;
    }

}
