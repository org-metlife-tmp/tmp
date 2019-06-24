package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.*;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.DateFormatThreadLocal;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * 批量付 结算对账
 *
 * @author GJF
 * @date 2018年12月28日
 */
public class PayCheckService {
    private static Logger log = LoggerFactory.getLogger(ChannelSettingService.class);
    /**
     * 查询所有批次
     *
     * @param pageNum
     * @param pageSize
     * @param record
     * @return
     */
    public Page<Record> batchlist(int pageNum, int pageSize, final Record record) throws BusinessException {
        if(StringUtils.isEmpty(record.getStr("channel_id_one")) &&
                StringUtils.isEmpty(record.getStr("channel_id_two"))){
            throw new ReqDataException("通道编码或通道描述不能为空!");
        }
        SqlPara sqlPara = Db.getSqlPara("paycheck.paylist", Kv.by("map", record.getColumns()));
        return Db.paginate(pageNum, pageSize, sqlPara);
    }

    /**
     * 查找交易流水
     * @param record
     * @return
     * @throws BusinessException
     */
    public List<Record> tradingList(final Record record){
        /**
         * 如果有bankcode，根据bankcode找对应的通道，如果是第三方的走business_check否则走is_checked
         * 如果没有bankcode，看传过来的is_inner 根据上面的通道来判断应该是走第三方的business_check还是is_checked
         */
        String bankcode = TypeUtils.castToString(record.get("bankcode"));
        if(StringUtils.isNotEmpty(bankcode)){
            Record chan = Db.findFirst(Db.getSql("channel_setting.getchannelbybankcode"), record.get("bankcode"));
            if(chan == null){
                return null;
            }
            if(chan.getInt("is_inner") == 0){
                record.set("business_check", record.get("is_checked"));
                record.remove("is_checked");
            }
        }else{
            int isInner = TypeUtils.castToInt(record.get("is_inner"));
            if(isInner == 0){
                record.set("business_check", record.get("is_checked"));
                record.remove("is_checked");
            }
        }
        record.remove("is_inner");
        SqlPara sqlPara = Db.getSqlPara("paycheck.tradingList", Kv.by("map", record.getColumns()));
        return Db.find(sqlPara);
    }

    /**
     * 对账确认
     * @param record
     * @param userInfo
     * @return
     * @throws BusinessException
     */
    public Page<Record> confirm(final Record record, final UserInfo userInfo) throws BusinessException {
        long startTime = System.currentTimeMillis();
        log.info("批量付开始核对---【begin】");
        //交易id
        final ArrayList<Integer> tradingNo = record.get("trading_no");
        //批次号
        final ArrayList<Integer> batchNo = record.get("batchid");
        final ArrayList<Integer> persistVersion = record.get("persist_version");

        //获取勾选的交易和批次
        final List<Record> batchList = Db.find(Db.getSqlPara("paycheck.findBatchList", Kv.by("batchNo", batchNo)));
        final List<Record> tradList = Db.find(Db.getSqlPara("paycheck.findTradList", Kv.by("tradingNo", tradingNo)));

        if(batchList.size()==0 || tradList.size()==0){
            throw new ReqDataException("请选择要核对的批次和交易！");
        }

        //判断勾选的批次是否是同一通道
        HashSet<String> codeSet = new HashSet<>();
        //判断是否已核对过
        HashSet<Integer> checkedSet = new HashSet<>();
        BigDecimal batchAmount = new BigDecimal(0);
        for(Record r : batchList){
            codeSet.add(TypeUtils.castToString(r.get("channel_code")));
            int isChecked = TypeUtils.castToInt(r.get("is_checked"));
            if(isChecked == 1){
                checkedSet.add(isChecked);
            }
            batchAmount = batchAmount.add(TypeUtils.castToBigDecimal(r.get("success_amount")));
        }
        if(codeSet.size() != 1){
            throw new ReqDataException("请选择同一通道！");
        }
        if(checkedSet.size() >= 1){
            throw new ReqDataException("已勾选的存在已核对的批次！");
        }
        //0--第三方付款 1--内部调拨付款
        int isInner = TypeUtils.castToInt(batchList.get(0).get("is_inner"));
        List<Record> tradSize = null;
        if(isInner == 0){
            //第三方的根据business_check判断是否核对
            tradSize = Db.find(Db.getSqlPara("paycheck.findTradListBusiness",
                    Kv.by("map", new Record().set("business_check", 0).set("tradingNo", tradingNo).getColumns())));
        }else if(isInner == 1){
            //内部调拨的根据is_checked判断是否核对
            tradSize = Db.find(Db.getSqlPara("paycheck.findTradListBusiness",
                    Kv.by("map", new Record().set("is_checked", 0).set("tradingNo", tradingNo).getColumns())));
        }else{
            throw new ReqDataException("需要传对应的业务类型！");
        }
        if(tradSize.size() != tradingNo.size()){
            throw new ReqDataException("存在已核对的交易再次进行核对！");
        }

        BigDecimal tradAmount = new BigDecimal(0);
        //收款记录的会记日期确认标记位
        boolean directionFlag = false;
        //交易付方向总金额-交易收方向总金额=批次成功付款金额，才可核对
        for(Record r : tradList){
            /**
             * 1、有收款记录 取收款记录较晚对账单日期作为会计日期。2、没有收款记录 取对账操作日期作为会计日期
             */
            int direction = TypeUtils.castToInt(r.get("direction"));
            //1付 2收
            if(direction == 1){
                tradAmount = tradAmount.add(TypeUtils.castToBigDecimal(r.get("amount")));
            }else if(direction == 2){
                directionFlag = true;
                tradAmount = tradAmount.subtract(TypeUtils.castToBigDecimal(r.get("amount")));
            }
        }
        if(batchAmount.compareTo(tradAmount) != 0){
            throw new ReqDataException("核对批次金额和交易金额不相同！");
        }

        final int inner = isInner;
        //生成对账流水号
        final String checkSerialSeqNo = RedisSericalnoGenTool.genCheckSerialSeqNo();//生成十六进制流水号

        final List<Record> records = CommonService.genPayConfirmRecords(batchNo, tradingNo, userInfo, checkSerialSeqNo);

        //进行数据新增操作
        boolean flag = Db.tx(new IAtom() {
            @Override
            public boolean run() throws SQLException {
                //更新交易
                String checkName = "";
                int biz_type;
                if(inner == 0){
                    checkName = "business_check";
                    biz_type = WebConstant.MajorBizType.SFF_PLF_DSF.getKey();
                }else{
                    checkName = "is_checked";
                    biz_type = WebConstant.MajorBizType.SFF_PLF_INNER.getKey();
                }
                String seqnoOrstatmentCode = DateFormatThreadLocal.format("yyyyMMddhhmmss", new Date()) +
                        RedisSericalnoGenTool.genVoucherSeqNo();//生成十六进制序列号/凭证号
                for(Integer trad : tradingNo){

                    boolean s = CommonService.update("acc_his_transaction",
                                    new Record().set(checkName, 1)
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
                //更新批次
                for(int num=0; num<batchNo.size(); num++){
                    boolean s = CommonService.update("pay_batch_total",
                            new Record().set("is_checked", 1)
                                        .set("persist_version", persistVersion.get(num)+1)
                                        .set("statement_code", seqnoOrstatmentCode)
                                        .set("check_service_number", checkSerialSeqNo)
                                        .set("check_user_id", userInfo.getUsr_id())
                                        .set("check_user_name", userInfo.getName())
                                        .set("check_date", new Date()),
                            new Record().set("id", batchNo.get(num)).set("persist_version", persistVersion.get(num)));
                    if(!s){
                        return false;
                    }
                }
                //存储关系
                for (int num=0; num<records.size(); num++) {
                    Record r = records.get(num);
                    boolean i = Db.save("pay_batch_checked", r);
                    if (!i) {
                        return false;
                    }
                }
                try {
                    //生成凭证信息
                    return CheckVoucherService.sunVoucherData(batchNo, tradingNo, batchList, tradList, biz_type, seqnoOrstatmentCode, userInfo);
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
            rd.set("channel_id_one", batchList.get(0).get("channel_id"));
            AccCommonService.setSftCheckStatus(record, "service_status");
            SqlPara sqlPara = Db.getSqlPara("paycheck.paylist", Kv.by("map", rd.getColumns()));
            long endTime = System.currentTimeMillis();
            log.info("批量付核对结束---【end】，耗时【"+(endTime-startTime)/1000+"】秒");
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
