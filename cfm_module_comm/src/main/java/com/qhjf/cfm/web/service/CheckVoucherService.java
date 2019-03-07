package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.ReqValidateException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.constant.WebConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Auther: zhangyuanyuan
 * @Date: 2018/11/2
 * @Description: 交易核对凭证生成
 */
public class CheckVoucherService {

    /**
     * -- 8 内部调拨   9 支付通   10 内部调部-批量  11 支付通-批量   12 归集通   13 资金下拨 15  收款通
     * <p>
     * INSERT [dbo].[cfm_biz_type_setting] ([biz_id], [p_id], [biz_name], [is_delete], [is_activity], [memo]) VALUES
     * (N'03bead791d6244a9B513bb789799fa3a', 8, N'资金调拨', 0, 1, N'1')
     * ,(N'2b4ccc1daf0a4b44b50c10dffb7b3e54', 8, N'投连划转', 0, 1, N'1')
     * ,(N'a0cabdb210f848cda9a6e1d090f4a783', 9, N'其他', 0, 1, N'1')
     * ,(N'a0cabdb210f848cda9a6e1d090f4a793', 9, N'保单业务结算费', 0, 1, N'1')
     * ,(N'a0cabdb210f848cda9a6e1d090f4a893', 9, N'银行手续费结算', 0, 1, N'1')
     * <p>
     * ,(N'd50f3048cee34c6684ff36635353955a', 10, N'资金调拨', 0, 1, N'1')
     * ,(N'b8439d8f56a74da9a6bad8ca04711e04',10, N'投连划转', 1, 1, N'1')
     * <p>
     * ,(N'982e09f355ea488dbd434d9f8be20365', 11, N'其他', 0, 1, N'1')
     * ,(N'982e09f355ea488dbd434d9f8be20465', 11, N'保单业务结算费', 0, 1, N'1')
     * ,(N'982e09f355ea488dbd434d9f8be20565', 11, N'银行手续费结算', 0, 1, N'1')
     * <p>
     * ,(N'd50f3048cee34c6684ff36634343955a', 15, N'利息收入', 0, 1, N'1')
     * ,(N'd50f3048cee34c6684ff36634343944a', 15, N'利息收入(美金)', 0, 1, N'1')
     * ,(N'd50f3048cee34c6684ff36634343933a', 15, N'其他', 0, 1, N'1')
     * ;
     */
    private static String NBDB_ZJHB = "03bead791d6244a9B513bb789799fa3a";      //内部调拨-资金调拨
    private static String NBDB_TLHZ = "2b4ccc1daf0a4b44b50c10dffb7b3e54";      //内部调拨-投连划转
    private static String NBDB_TLHH = "2b4ccc1daf0a4b44b50c10dffb7b3e55";      //内部调拨-投连划回
    private static String NBDB_DSF = "11e1d8f2dbe14c41a241e0a430743c6b";      //内部调拨-批量付第三方

    private static String ZJZF_PLF_DSF = "801dbfb1bfb34817b9e61ce29d86b47b";   //资金支付-批量付/第三方
    private static String ZJZF_QT = "a0cabdb210f848cda9a6e1d090f4a783";        //资金支付-其他
    private static String ZJZF_BDYWJSF = "a0cabdb210f848cda9a6e1d090f4a793";   //资金支付-保单业务结算费
    private static String ZJZF_YHSXF = "a0cabdb210f848cda9a6e1d090f4a893";      //资金支付-保单业务结算费
    private static String ZJZF_BDYWSGFK = "a0cabdb210f848cda9a6e1d090f4a993";   //资金支付-保单业务手工付款

    private static String PLDB_ZJHB = "d50f3048cee34c6684ff36635353955a";      //内部调部-批量-资金调拨
    private static String PLDB_TLHZ = "b8439d8f56a74da9a6bad8ca04711e04";      //内部调部-批量-投连划转
    private static String PLDB_TLHH = "d50f3048cee34c6684ff36635353955b";      //内部调拨-批量-投连划回

    private static String PLZF_QT = "982e09f355ea488dbd434d9f8be20365";        //资金支付-批量-其他
    private static String PLZF_BDYWJSF = "982e09f355ea488dbd434d9f8be20465";   //资金支付-批量-保单业务结算费
    private static String PLZF_YHSXF = "982e09f355ea488dbd434d9f8be20565";      //资金支付-批量-保单业务结算费
    private static String PLZF_BDYWSGFK = "982e09f355ea488dbd434d9f8be20665";   //资金支付-批量-保单业务手工付款

    private static String SK_LXSU = "d50f3048cee34c6684ff36634343955a";        //收款-利息收入
    private static String SK_LXSUMY = "d50f3048cee34c6684ff36634343944a";   //收款-利息收入(美金)
    private static String SK_QT = "d50f3048cee34c6684ff36634343933a";      //收款-其他

    private static SimpleDateFormat formatTrans = new SimpleDateFormat("ddMMyyyy");//交易时间规则
    private static SimpleDateFormat formatTransRefer = new SimpleDateFormat("YYMM");//交易标识规则
    private static SimpleDateFormat formatdsfTransRefer = new SimpleDateFormat("yyMMdd");//交易标识规则

    private static Logger log = LoggerFactory.getLogger(CheckVoucherService.class);


    public static void sunVoucherData(List<Integer> recordList, long billId, int biz_type, Map<Integer, Date> transDateMap) throws BusinessException {
        WebConstant.MajorBizType majorBizType = WebConstant.MajorBizType.getBizType(biz_type);
        switch (majorBizType) {
            case INNERDB://内部调拨 - 单笔
            case INNERDB_BATCH://内部调拨 - 批量
                dbtCheckVoucher(recordList, billId, majorBizType, transDateMap);
                break;
            case ZFT://支付通 - 单笔
            case OBP://支付通 - 批量
                zftCheckVoucher(recordList, billId, majorBizType, transDateMap);
                break;
            case GJT://归集通
                gjtCheckVoucher(recordList, billId, transDateMap);
                break;
            case CBB://非直连归集
                nonGjtCheckVoucher(recordList, billId, transDateMap);
                break;
            case OA_HEAD_PAY://oa总公司付款
                oaHeadCheckVoucher(recordList, billId, transDateMap);
                break;
            case OA_BRANCH_PAY://oa分公司付款
                oaBranchCheckVoucher(recordList, billId, transDateMap);
                break;
            case SKT://收款通
                sktCheckVoucher(recordList, billId, transDateMap);
                break;
            default:
                throw new ReqDataException(majorBizType.getDesc() + "，未实现生成凭证方法！");
        }
    }

    public static boolean sunVoucherData(List<Integer> batchList, List<Integer> tradList,
                                      List<Record> batchRecordList, List<Record> tradRecordList,
                                      int biz_type, String seqnoOrstatmentCode) throws BusinessException {
        WebConstant.MajorBizType majorBizType = WebConstant.MajorBizType.getBizType(biz_type);
        boolean flag = false;
        switch (majorBizType) {
            case SFF_PLF_DSF://收付费批量付款第三方/内部调拨
            case SFF_PLF_INNER://收付费批量付款第三方/内部调拨
                flag = plfCheckVoucher(batchList, tradList, batchRecordList, tradRecordList, majorBizType, seqnoOrstatmentCode);
                break;
            case PLS:
                flag = plsCheckVoucher(batchList, tradList, batchRecordList, tradRecordList, majorBizType, seqnoOrstatmentCode);
                break;
            default:
                throw new ReqDataException(majorBizType.getDesc() + "，未实现生成凭证方法！");
        }
        return flag;
    }

    /**
     * 调拨通 核对生成凭证
     *
     * @param transIdList
     * @param billId
     * @return
     * @throws BusinessException
     */
    private static boolean dbtCheckVoucher(List<Integer> transIdList, long billId, WebConstant.MajorBizType bizType, Map<Integer, Date> transDateMap) throws BusinessException {
        Record payRec = null;
        Record recvRec = null;
        Record set = null;
        Record where = null;
        Record billRec = null;
        String tableName = "";
        String primartKey = "";

        //根据单据id查询单据信息
        if (WebConstant.MajorBizType.INNERDB == bizType) {
            tableName = "inner_db_payment";
            primartKey = "id";
            billRec = Db.findById(tableName, primartKey, billId);
        } else {
            tableName = "inner_batchpay_bus_attach_detail";
            primartKey = "detail_id";
            billRec = Db.findFirst(Db.getSql("batch.findBatchSendInfoByDetailId"), billId);
        }

        String seqnoOrstatmentCode = RedisSericalnoGenTool.genVoucherSeqNo();//生成十六进制序列号/凭证号

        Record[] infos = processTranIds(transIdList, seqnoOrstatmentCode);
        payRec = infos[0];
        recvRec = infos[1];

        String bizId = TypeUtils.castToString(billRec.get("biz_id"));
        List<Record> list = new ArrayList<>();

        if (NBDB_ZJHB.equals(bizId) || PLDB_ZJHB.equals(bizId)) {//资金调拨
            //============ 付方向 凭证数据组装 begin ============
            //凭证1、2
            list.add(CommonService.dbtPayVorcher(payRec, billRec, seqnoOrstatmentCode, 1, bizType, transDateMap));
            list.add(CommonService.dbtPayVorcher(payRec, billRec, seqnoOrstatmentCode, 2, bizType, transDateMap));
            //============ 付方向 凭证数据组装 end ============

            //============ 收方向 凭证数据组装 begin ============
            //凭证1、2
            list.add(CommonService.dbtRecvVorcher(recvRec, billRec, seqnoOrstatmentCode, 1, bizType, transDateMap));
            list.add(CommonService.dbtRecvVorcher(recvRec, billRec, seqnoOrstatmentCode, 2, bizType, transDateMap));
            //============ 收方向 凭证数据组装 end ============
        } else if (NBDB_TLHZ.equals(bizId) || PLDB_TLHZ.equals(bizId)) {//投连划转
            //============ 付方向 凭证数据组装 begin ============
            list.add(CommonService.dbtPayVorcher(payRec, billRec, seqnoOrstatmentCode, 3, bizType, transDateMap));
            list.add(CommonService.dbtPayVorcher(payRec, billRec, seqnoOrstatmentCode, 4, bizType, transDateMap));
            //============ 付方向 凭证数据组装 end ============
        } else if (NBDB_TLHH.equals(bizId) || PLDB_TLHH.equals(bizId)) {
            list.add(CommonService.dbtPayVorcher(payRec, billRec, seqnoOrstatmentCode, 5, bizType, transDateMap));
            list.add(CommonService.dbtPayVorcher(payRec, billRec, seqnoOrstatmentCode, 6, bizType, transDateMap));
        } else if (NBDB_DSF.equals(bizId)) {
            /**
             * 批量付自动生成的调拨单，判断付款方和收款方是否为同一机构，
             *      1，如果为同一机构，则生成一借一贷，
             *      2，如果为不同机构，则需要内部往来的
             */
            //根据账户id查询账户所属机构
            Record payAcc = Db.findById("account", "acc_id", TypeUtils.castToLong(billRec.get("pay_account_id")));
            Record recvAcc = Db.findById("account", "acc_id", TypeUtils.castToLong(billRec.get("recv_account_id")));

            if(payAcc.getInt("org_id") == recvAcc.getInt("org_id")){
                //生成一借一贷
                list.add(CommonService.dbtPlfPayVorcher(payRec, billRec, seqnoOrstatmentCode, 1, bizType, transDateMap));
                list.add(CommonService.dbtPlfPayVorcher(recvRec, billRec, seqnoOrstatmentCode, 2, bizType, transDateMap));
            }else{
                //按原来的生成，但是个别字段不一样
                list.add(CommonService.dbtPlfPayVorcher(payRec, billRec, seqnoOrstatmentCode, 3, bizType, transDateMap));
                list.add(CommonService.dbtPlfPayVorcher(payRec, billRec, seqnoOrstatmentCode, 4, bizType, transDateMap));
                list.add(CommonService.dbtPlfPayVorcher(recvRec, billRec, seqnoOrstatmentCode, 3, bizType, transDateMap));
                list.add(CommonService.dbtPlfPayVorcher(recvRec, billRec, seqnoOrstatmentCode, 4, bizType, transDateMap));
            }
        } else {
//            throw new ReqValidateException("未定义的业务类型!");
            return true;
        }


        if (!CommonService.saveCheckVoucher(list)) {
            throw new ReqValidateException("生成凭证失败!");
        }

        //反写单据凭证码
        set = new Record();
        where = new Record();

        int version = TypeUtils.castToInt(billRec.get("persist_version"));
        set.set("statement_code", seqnoOrstatmentCode);
        set.set("persist_version", (version + 1));

        where.set(primartKey, billId);
        where.set("persist_version", version);

        return CommonService.update(tableName, set, where);
    }

    /**
     * 支付通 核对生成凭证
     *
     * @param transIdList
     * @param billId
     * @return
     * @throws BusinessException
     */
    private static boolean zftCheckVoucher(List<Integer> transIdList, long billId, WebConstant.MajorBizType bizType, Map<Integer, Date> transDateMap) throws BusinessException {
        Record payRec = null;
        Record recvRec = null;
        Record set = null;
        Record where = null;
        Record billRec = null;
        String tableName = "";
        String primartKey = "";

        //根据单据id查询单据信息
        if (WebConstant.MajorBizType.ZFT == bizType) {
            tableName = "outer_zf_payment";
            primartKey = "id";
            billRec = Db.findById(tableName, primartKey, billId);
        } else {
            tableName = "outer_batchpay_bus_attach_detail";
            primartKey = "detail_id";
            billRec = Db.findFirst(Db.getSql("zftbatch.findBatchSendInfoByDetailId"), billId);
        }

        String seqnoOrstatmentCode = RedisSericalnoGenTool.genVoucherSeqNo();//生成十六进制序列号/凭证号

        Record[] infos = processTranIds(transIdList, seqnoOrstatmentCode);
        payRec = infos[0];
        recvRec = infos[1];


        String bizId = TypeUtils.castToString(billRec.get("biz_id"));
        List<Record> list = new ArrayList<>();


        if (ZJZF_QT.equals(bizId) || PLZF_QT.equals(bizId)) {//其他
            //============ 付方向 凭证数据组装 begin ============
            //凭证1
            list.add(CommonService.zftPayVorcher(payRec, billRec, seqnoOrstatmentCode, 1, bizType, transDateMap));
            //凭证2
            list.add(CommonService.zftPayVorcher(payRec, billRec, seqnoOrstatmentCode, 2, bizType, transDateMap));
            //============ 付方向 凭证数据组装 end ============
        } else if (ZJZF_BDYWJSF.equals(bizId) || PLZF_BDYWJSF.equals(bizId)) {//保单业务结算费
            //============ 付方向 凭证数据组装 begin ============
            //凭证3
            list.add(CommonService.zftPayVorcher(payRec, billRec, seqnoOrstatmentCode, 3, bizType, transDateMap));
            //凭证4
            list.add(CommonService.zftPayVorcher(payRec, billRec, seqnoOrstatmentCode, 4, bizType, transDateMap));
            //============ 付方向 凭证数据组装 end ============
        } else if (ZJZF_YHSXF.equals(bizId) || PLZF_YHSXF.equals(bizId)) {//银行手续费结算
            //============ 付方向 凭证数据组装 begin ============
            //凭证5
            list.add(CommonService.zftPayVorcher(payRec, billRec, seqnoOrstatmentCode, 5, bizType, transDateMap));
            //凭证6
            list.add(CommonService.zftPayVorcher(payRec, billRec, seqnoOrstatmentCode, 6, bizType, transDateMap));
            //============ 付方向 凭证数据组装 end ============
        } else if (ZJZF_PLF_DSF.equals(bizId)) {    //资金支付-批量付/第三方
            //============ 付方向 凭证数据组装 begin ============
            //凭证5
            list.add(CommonService.zftPayVorcher(payRec, billRec, seqnoOrstatmentCode, 7, bizType, transDateMap));
            //凭证6
            list.add(CommonService.zftPayVorcher(payRec, billRec, seqnoOrstatmentCode, 8, bizType, transDateMap));
            //============ 付方向 凭证数据组装 end ============ else {
        }else if(ZJZF_BDYWSGFK.equals(bizId) || PLZF_BDYWSGFK.equals(bizId)){
            return true;
        } else {
            throw new ReqDataException("未定义的业务类型!");
        }


        if (!CommonService.saveCheckVoucher(list)) {
            throw new ReqDataException("生成凭证失败!");
        }

        //反写单据凭证码
        set = new Record();
        where = new Record();

        int version = TypeUtils.castToInt(billRec.get("persist_version"));
        set.set("statement_code", seqnoOrstatmentCode);
        set.set("persist_version", (version + 1));

        where.set(primartKey, billId);
        where.set("persist_version", version);

        return CommonService.update(tableName, set, where);

    }

    /**
     * 批量付 核对生成凭证
     *
     * @param batchList
     * @param tradList
     * @return
     * @throws BusinessException
     */
    private static boolean plfCheckVoucher(List<Integer> batchList, List<Integer> tradList,
                                           List<Record> batchRecordList, List<Record> tradRecordList,
                                           WebConstant.MajorBizType bizType, String seqnoOrstatmentCode) throws BusinessException {

        //获取所有的流水
        List<Record> detailLsit = Db.find(
                Db.getSqlPara("paycheck.findbatchdetail", Kv.by("map", new Record().set("batchid",batchList).getColumns())));
        String curr="",orgcode="";
        List<Record> list = new ArrayList<>();
        //三个日期，一个是付方向最晚的日期，一个是收方向最晚的日期，一个是所有交易的最晚日期
        Date allLastDate = TypeUtils.castToDate(tradRecordList.get(0).get("trans_date")), payLastDate=null, recvLastDate=null, periodDate=null;
        Date tempDate = null;
        for(Record r : tradRecordList){
            /**
             * 1、有收款记录 取收款记录较晚对账单日期作为会计日期。2、没有收款记录 取对账操作日期作为会计日期
             */
            int direction = TypeUtils.castToInt(r.get("direction"));
            tempDate = TypeUtils.castToDate(r.get("trans_date"));
            //1付 2收
            if(direction == 1){
                if(payLastDate == null){
                    payLastDate = TypeUtils.castToDate(r.get("trans_date"));
                }
                if(tempDate.after(payLastDate)){
                    payLastDate = tempDate;
                }
            }else if(direction == 2){
                if(recvLastDate == null){
                    recvLastDate = TypeUtils.castToDate(r.get("trans_date"));
                }
                if(tempDate.after(recvLastDate)){
                    recvLastDate = tempDate;
                }
            }
            if(tempDate.after(allLastDate)){
                allLastDate = tempDate;
            }
        }
        if(bizType == WebConstant.MajorBizType.SFF_PLF_INNER){
            Record chann=null, acc=null;
            String description=null, code6=null, transactionReference=null;
            //根据通道找对应的bankcode和channel_code，然后根据channel_code找对应的账号
            if(detailLsit!=null && detailLsit.size()>0){
                chann = Db.findById("channel_setting", "id", detailLsit.get(0).getInt("channel_id"));
                acc = Db.findFirst(Db.getSql("paycheck.findaccount"), chann.getStr("bankcode"));
                //根据机构id查询机构信息
                curr = TypeUtils.castToString(
                        Db.findById("currency","id", TypeUtils.castToLong(acc.get("curr_id")))
                                .get("iso_code"));
                orgcode = TypeUtils.castToString(
                        Db.findById("organization","org_id", TypeUtils.castToLong(acc.get("org_id")))
                                .get("code"));
                Record sftcheck = Db.findById("sftcheck_org_mapping", "tmp_org_code", orgcode);
                transactionReference = "SS" + sftcheck.getStr("code_abbre") + formatTransRefer.format(new Date()) + seqnoOrstatmentCode;
            }
            //内部调拨的分全额模式和净额模式两种 batchList 净额模式 0--净额模式 1--全额模式
            int netMode = TypeUtils.castToInt(detailLsit.get(0).get("net_mode"));
            if(netMode == 0){
                /**
                 * 会计日期 取当日银行对账单最晚日期  所有交易最晚的
                 */
                periodDate = CommonService.getPeriodByCurrentDay(allLastDate);

                for(Record detailRecord : detailLsit){
                    int status = TypeUtils.castToInt(detailRecord.get("status"));
                    if(status == WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_S.getKey()){
                        description = formatdsfTransRefer.format(allLastDate) + chann.getStr("bankcode") + "批量付款-银行净额模式-付款成功";
                        //凭证1
                        list.add(CommonService.plfPayVorcher(acc, description, chann.getStr("channel_code")+chann.getStr("bankcode"), transactionReference, 1, periodDate, allLastDate, detailRecord, curr, orgcode));
                        //凭证2
                        list.add(CommonService.plfPayVorcher(acc, description, "SYS", transactionReference, 2, periodDate, allLastDate, detailRecord, curr, orgcode));
                    }
                }

            }else if(netMode == 1){
                /**
                 * 1、付款：会计日期取付款银行对账单较晚的日期 2、收款：会计日期取收款银行对账单较晚日期  各取方向最晚日期
                 */
                Date periodS=null, periodF=null;
                periodF = CommonService.getPeriodByCurrentDay(allLastDate);
                if(recvLastDate != null){
                    periodS = CommonService.getPeriodByCurrentDay(recvLastDate);
                }

                for(Record detailRecord : detailLsit){
                    int status = TypeUtils.castToInt(detailRecord.get("status"));
                    if(status == WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_F.getKey()){
                        description = formatdsfTransRefer.format(recvLastDate) + chann.getStr("bankcode") + "批量付款-银行全额模式-资金退回";
                        //凭证5
                        list.add(CommonService.plfPayVorcher(acc, description, "SYS", transactionReference, 5, periodS, recvLastDate, detailRecord, curr, orgcode));
                        //凭证6
                        list.add(CommonService.plfPayVorcher(acc, description, chann.getStr("channel_code")+chann.getStr("bankcode"), transactionReference, 6, periodS, recvLastDate, detailRecord, curr, orgcode));
                    }
                    description = formatdsfTransRefer.format(allLastDate) + chann.getStr("bankcode") + "批量付款-银行全额模式-付款成功";
                    //凭证3
                    list.add(CommonService.plfPayVorcher(acc, description, chann.getStr("channel_code")+chann.getStr("bankcode"), transactionReference, 3, periodF, allLastDate, detailRecord, curr, orgcode));
                    //凭证4
                    list.add(CommonService.plfPayVorcher(acc, description, "SYS", transactionReference, 4, periodF, allLastDate, detailRecord, curr, orgcode));
                }

            }
        }else if(bizType == WebConstant.MajorBizType.SFF_PLF_DSF){
            /**
             * 1、有收款记录 取收款记录较晚对账单日期作为会计日期（所有收付记录的最晚的）。2、没有收款记录 取对账操作日期作为会计日期
             */
            Date periodS=null, periodF=null;
            periodF = CommonService.getPeriodByCurrentDay(allLastDate);
            if(recvLastDate != null){
                periodS = CommonService.getPeriodByCurrentDay(new Date());
            }

            Record chann=null, acc=null;
            String description=null, code6=null, transactionReference=null;
            //根据通道找对应的bankcode和channel_code，然后根据channel_code找对应的账号
            if(detailLsit!=null && detailLsit.size()>0){
                chann = Db.findById("channel_setting", "id", detailLsit.get(0).getInt("channel_id"));
                acc = Db.findFirst(Db.getSql("paycheck.findaccount"), chann.getStr("bankcode"));
                //根据机构id查询机构信息
                curr = TypeUtils.castToString(
                        Db.findById("currency","id", TypeUtils.castToLong(acc.get("curr_id")))
                                .get("iso_code"));
                orgcode = TypeUtils.castToString(
                        Db.findById("organization","org_id", TypeUtils.castToLong(acc.get("org_id")))
                                .get("code"));
                Record sftcheck = Db.findById("sftcheck_org_mapping", "tmp_org_code", orgcode);
                transactionReference = "SS" + sftcheck.getStr("code_abbre") + formatTransRefer.format(new Date()) + seqnoOrstatmentCode;
            }
            for(Record detailRecord : detailLsit){
                int status = TypeUtils.castToInt(detailRecord.get("status"));

                if(status == WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_S.getKey()){
                    description = formatdsfTransRefer.format(allLastDate) + chann.getStr("bankcode") + "第三方结算-批量付款-付款成功";
                    code6 = chann.getStr("channel_code")+chann.getStr("bankcode");
                    //处理成功的
                    //凭证1
                    list.add(CommonService.plfPayVorcher(acc, description, code6, transactionReference, 7, periodF, allLastDate, detailRecord, curr, orgcode));
                    //凭证2
                    list.add(CommonService.plfPayVorcher(acc, description, code6, transactionReference, 8, periodF, allLastDate, detailRecord, curr, orgcode));
                }else if(status == WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_F.getKey()){
                    //处理失败的
                    //凭证1
                    description = formatdsfTransRefer.format(new Date()) + chann.getStr("bankcode") + "第三方结算-批量付款-付款失败退款";
                    list.add(CommonService.plfPayVorcher(acc, description, "SYS", transactionReference, 9, periodS, new Date(), detailRecord, curr, orgcode));
                    //凭证2
                    list.add(CommonService.plfPayVorcher(acc, description, chann.getStr("channel_code")+chann.getStr("bankcode"), transactionReference, 10, periodS, new Date(), detailRecord, curr, orgcode));
                }
            }
        }

        if (!CommonService.saveCheckVoucher(list)) {
            throw new ReqDataException("生成凭证失败!");
        }


        return true;
    }

    /**
     * 批量收 核对生成凭证
     *
     * @param batchList
     * @param tradList
     * @return
     * @throws BusinessException
     */
    private static boolean plsCheckVoucher(List<Integer> batchList, List<Integer> tradList,
                                           List<Record> batchRecordList, List<Record> tradRecordList,
                                           WebConstant.MajorBizType bizType, String seqnoOrstatmentCode) throws BusinessException {

        //获取所有的流水
        List<Record> detailLsit = Db.find(
                Db.getSqlPara("recvcheck.findbatchdetail", Kv.by("map", new Record().set("batchid",batchList).getColumns())));
        String curr="",orgcode="";
        List<Record> list = new ArrayList<>();
        //所有交易的最晚日期
        Date allLastDate = TypeUtils.castToDate(tradRecordList.get(0).get("trans_date")), periodDate=null;
        Date tempDate = null;
        for(Record r : tradRecordList){
            tempDate = TypeUtils.castToDate(r.get("trans_date"));
            if(tempDate.after(allLastDate)){
                allLastDate = tempDate;
            }
        }
        /**
         * 记录较晚对账单日期作为会计日期（所有收付记录的最晚的）。
         */
        periodDate = CommonService.getPeriodByCurrentDay(allLastDate);

        Record chann=null, acc=null;
        String description=null, code6=null, transactionReference=null;
        //根据通道找对应的bankcode和channel_code，然后根据channel_code找对应的账号
        if(detailLsit!=null && detailLsit.size()>0){
            chann = Db.findById("channel_setting", "id", detailLsit.get(0).getInt("channel_id"));

            acc = Db.findById("account", "acc_id", TypeUtils.castToLong(tradRecordList.get(0).get("acc_id")));
            //根据机构id查询机构信息
            curr = TypeUtils.castToString(
                    Db.findById("currency","id", TypeUtils.castToLong(acc.get("curr_id")))
                            .get("iso_code"));
            orgcode = TypeUtils.castToString(
                    Db.findById("organization","org_id", TypeUtils.castToLong(acc.get("org_id")))
                            .get("code"));
            Record sftcheck = Db.findById("sftcheck_org_mapping", "tmp_org_code", orgcode);
            transactionReference = "SS" + sftcheck.getStr("code_abbre") + formatTransRefer.format(new Date()) + seqnoOrstatmentCode;
        }
        for(Record detailRecord : detailLsit){
            description = formatdsfTransRefer.format(allLastDate) + chann.getStr("bankcode") + "批量收款资金到帐";
            //凭证1
            list.add(CommonService.plsPayVorcher(acc, description, "SYS", transactionReference, 1, periodDate, allLastDate, detailRecord, curr, orgcode));
            //凭证2
            list.add(CommonService.plsPayVorcher(acc, description, chann.getStr("channel_code")+chann.getStr("bankcode"), transactionReference, 2, periodDate, allLastDate, detailRecord, curr, orgcode));
        }

        if (!CommonService.saveCheckVoucher(list)) {
            throw new ReqDataException("生成凭证失败!");
        }


        return true;
    }

    /**
     * 批量付LA/EBS回显TMP 核对生成凭证
     * @param source
     * @param payLegalRecord
     * @param periodDate
     * @return
     * @throws BusinessException
     */
    public static boolean plfLaEbsBackCheckVoucher(String source, Record payLegalRecord, Date periodDate) throws BusinessException {

        String seqnoOrstatmentCode = RedisSericalnoGenTool.genVoucherSeqNo();//生成十六进制序列号/凭证号
        List<Record> list = new ArrayList<>();
        if("LA".equals(source)){
            //凭证1
            list.add(CommonService.plfLaBackCheckVoucher(payLegalRecord, 1, periodDate, seqnoOrstatmentCode));
            //凭证2
            list.add(CommonService.plfLaBackCheckVoucher(payLegalRecord, 2, periodDate, seqnoOrstatmentCode));

        }else if("EBS".equals(source)){
            //凭证1
            list.add(CommonService.plfLaBackCheckVoucher(payLegalRecord, 3, periodDate, seqnoOrstatmentCode));
            //凭证2
            list.add(CommonService.plfLaBackCheckVoucher(payLegalRecord, 4, periodDate, seqnoOrstatmentCode));
        }

        if (!CommonService.saveCheckVoucher(list)) {
            throw new ReqDataException("生成凭证失败!");
        }

        return true;
    }

    /**
     * 批量收LA回显TMP 核对生成凭证
     * @param source
     * @param recvLegalRecord
     * @param periodDate
     * @return
     * @throws BusinessException
     */
    public static boolean plsLaBackCheckVoucher(String source, Record recvLegalRecord, Date periodDate) throws BusinessException {

        String seqnoOrstatmentCode = RedisSericalnoGenTool.genVoucherSeqNo();//生成十六进制序列号/凭证号
        List<Record> list = new ArrayList<>();
        if("LA".equals(source)){
            //凭证1
            list.add(CommonService.plfLaBackCheckVoucher(recvLegalRecord, 1, periodDate, seqnoOrstatmentCode));
            //凭证2
            list.add(CommonService.plfLaBackCheckVoucher(recvLegalRecord, 2, periodDate, seqnoOrstatmentCode));

        }

        if (!CommonService.saveCheckVoucher(list)) {
            throw new ReqDataException("生成凭证失败!");
        }

        return true;
    }

    /**
     * 归集通 核对生成凭证
     *
     * @param transIdList
     * @param billId
     * @return
     * @throws BusinessException
     */
    private static boolean gjtCheckVoucher(List<Integer> transIdList, long billId, Map<Integer, Date> transDateMap) throws BusinessException {
        Record payRec = null;
        Record recvRec = null;
        Record set = null;
        Record where = null;
        Record billRec = null;
        String tableName = "collect_execute_instruction";
        String primartKey = "id";

        //根据单据id查询单据信息
        billRec = Db.findById(tableName, primartKey, billId);

        String seqnoOrstatmentCode = RedisSericalnoGenTool.genVoucherSeqNo();//生成十六进制序列号/凭证号


        Record[] infos = processTranIds(transIdList, seqnoOrstatmentCode);
        payRec = infos[0];
        recvRec = infos[1];


        List<Record> list = new ArrayList<>();
        //============ 付方向 凭证数据组装 begin ============
        //凭证1、2
        list.add(CommonService.gjtPayVorcher(payRec, billRec, seqnoOrstatmentCode, 1, transDateMap));
        list.add(CommonService.gjtPayVorcher(payRec, billRec, seqnoOrstatmentCode, 2, transDateMap));
        //============ 付方向 凭证数据组装 end ============

        //============ 收方向 凭证数据组装 begin ============
        //凭证3、4
        list.add(CommonService.gjtRecvVorcher(recvRec, billRec, seqnoOrstatmentCode, 3, transDateMap));
        list.add(CommonService.gjtRecvVorcher(recvRec, billRec, seqnoOrstatmentCode, 4, transDateMap));
        //============ 收方向 凭证数据组装 end ============

        if (!CommonService.saveCheckVoucher(list)) {
            throw new ReqValidateException("生成凭证失败!");
        }

        //反写单据凭证码
        set = new Record();
        where = new Record();

        int version = TypeUtils.castToInt(billRec.get("persist_version"));
        set.set("statement_code", seqnoOrstatmentCode);
        set.set("persist_version", (version + 1));

        where.set(primartKey, billId);
        where.set("persist_version", version);

        return CommonService.update(tableName, set, where);
    }

    /**
     * 非直连归集通 核对生成凭证
     *
     * @param transIdList
     * @param billId
     * @return
     * @throws BusinessException
     */
    private static boolean nonGjtCheckVoucher(List<Integer> transIdList, long billId, Map<Integer, Date> transDateMap) throws BusinessException {
        Record payRec = null;
        Record recvRec = null;
        Record set = null;
        Record where = null;
        Record billRec = null;
        String tableName = "collect_batch_bus_attach_detail";
        String primartKey = "detail_id";

        //根据单据id查询单据信息
        billRec = Db.findById(tableName, primartKey, billId);

        String seqnoOrstatmentCode = RedisSericalnoGenTool.genVoucherSeqNo();//生成十六进制序列号/凭证号


        Record[] infos = processTranIds(transIdList, seqnoOrstatmentCode);
        payRec = infos[0];
        recvRec = infos[1];


        List<Record> list = new ArrayList<>();
        //============ 付方向 凭证数据组装 begin ============
        //凭证1、2
        list.add(CommonService.nonGjtPayVorcher(payRec, billRec, seqnoOrstatmentCode, 1, transDateMap));
        list.add(CommonService.nonGjtPayVorcher(payRec, billRec, seqnoOrstatmentCode, 2, transDateMap));
        //============ 付方向 凭证数据组装 end ============

        //============ 收方向 凭证数据组装 begin ============
        //凭证3、4
        list.add(CommonService.nonGjtRecvVorcher(recvRec, billRec, seqnoOrstatmentCode, 3, transDateMap));
        list.add(CommonService.nonGjtRecvVorcher(recvRec, billRec, seqnoOrstatmentCode, 4, transDateMap));
        //============ 收方向 凭证数据组装 end ============

        if (!CommonService.saveCheckVoucher(list)) {
            throw new ReqDataException("生成凭证失败!");
        }

        //反写单据凭证码
        set = new Record();
        where = new Record();

        int version = TypeUtils.castToInt(billRec.get("persist_version"));
        set.set("statement_code", seqnoOrstatmentCode);
        set.set("persist_version", (version + 1));

        where.set(primartKey, billId);
        where.set("persist_version", version);

        return CommonService.update(tableName, set, where);
    }

    /**
     * OA总公司付款 核对生成凭证
     *
     * @param transIdList
     * @param billId
     * @return
     * @throws BusinessException
     */
    private static boolean oaHeadCheckVoucher(List<Integer> transIdList, long billId, Map<Integer, Date> transDateMap) throws BusinessException {
        Record payRec = null;
        Record set = null;
        Record where = null;
        Record billRec = null;
        String tableName = "oa_head_payment";
        String primartKey = "id";

        //根据单据id查询单据信息
        billRec = Db.findById(tableName, primartKey, billId);

        String seqnoOrstatmentCode = RedisSericalnoGenTool.genVoucherSeqNo();//生成十六进制序列号/凭证号

        Record[] infos = processTranIds(transIdList, seqnoOrstatmentCode);
        payRec = infos[0];

        List<Record> list = new ArrayList<>();

        //============ 付方向 凭证数据组装 begin ============
        //凭证1
        list.add(CommonService.oaHeadPayVorcher(payRec, billRec, seqnoOrstatmentCode, 1, transDateMap));
        //凭证2
        list.add(CommonService.oaHeadPayVorcher(payRec, billRec, seqnoOrstatmentCode, 2, transDateMap));
        //============ 付方向 凭证数据组装 end ============


        Long billOrgId = TypeUtils.castToLong(billRec.get("org_id"));// oa_head_payment org_id

        Record applyOrg = Db.findById("organization", "org_id", billOrgId);
        if (applyOrg != null && applyOrg.getInt("level_num") > 1) { // 总公司帐户代分公司对外付款(如果申请机构的level_num > 1 ,即不为总公司)

            //============ 付方向 凭证数据组装 begin ============
            //凭证3
            list.add(CommonService.oaHeadPayVorcher(payRec, billRec, seqnoOrstatmentCode, 3, transDateMap));
            //凭证4
            list.add(CommonService.oaHeadPayVorcher(payRec, billRec, seqnoOrstatmentCode, 4, transDateMap));
            //============ 付方向 凭证数据组装 end ============
        }


        if (!CommonService.saveCheckVoucher(list)) {
            throw new ReqDataException("生成凭证失败!");
        }

        //反写单据凭证码
        set = new Record();
        where = new Record();

        int version = TypeUtils.castToInt(billRec.get("persist_version"));
        set.set("statement_code", seqnoOrstatmentCode);
        set.set("persist_version", (version + 1));

        where.set(primartKey, billId);
        where.set("persist_version", version);

        return CommonService.update(tableName, set, where);
    }

    /**
     * OA分公司付款 核对生成凭证
     *
     * @param transIdList
     * @param billId
     * @return
     * @throws BusinessException
     */
    private static boolean oaBranchCheckVoucher(List<Integer> transIdList, long billId, Map<Integer, Date> transDateMap) throws BusinessException {
        Record payRec = null;
        Record recvRec = null;
        Record set = null;
        Record where = null;
        Record billRec = null;
        String tableName = "oa_branch_payment_item";
        String primartKey = "id";

        //根据单据id查询单据信息
        billRec = Db.findFirst(Db.getSql("branch_org_oa.findOaBranchItemToList"), billId);

        int itemType = TypeUtils.castToInt(billRec.get("item_type"));//1：调拨  2：支付

        String seqnoOrstatmentCode = RedisSericalnoGenTool.genVoucherSeqNo();//生成十六进制序列号/凭证号


        Record[] infos = processTranIds(transIdList, seqnoOrstatmentCode);
        payRec = infos[0];
        recvRec = infos[1];


        List<Record> list = new ArrayList<>();
        if (itemType == 2) {//支付
            //============ 付方向 凭证数据组装 begin ============
            //凭证1
            list.add(CommonService.oaBrHeadPayVorcher(payRec, billRec, seqnoOrstatmentCode, 1, transDateMap));
            //凭证2
            list.add(CommonService.oaBrHeadPayVorcher(payRec, billRec, seqnoOrstatmentCode, 2, transDateMap));
            //============ 付方向 凭证数据组装 end ============
        } else {//下拨
            //============ 收方向 凭证数据组装 begin ============
            //凭证3
            list.add(CommonService.oaBrHeadRecvVorcher(recvRec, billRec, seqnoOrstatmentCode, 3, transDateMap));
            //凭证4
            list.add(CommonService.oaBrHeadRecvVorcher(recvRec, billRec, seqnoOrstatmentCode, 4, transDateMap));
            //============ 收方向 凭证数据组装 end ============
            //============ 付方向 凭证数据组装 begin ============
            //凭证5
            list.add(CommonService.oaBranchPayVorcher(payRec, billRec, seqnoOrstatmentCode, 5, transDateMap));
            //凭证6
            list.add(CommonService.oaBranchPayVorcher(payRec, billRec, seqnoOrstatmentCode, 6, transDateMap));
            //============ 付方向 凭证数据组装 end ============
        }

        if (!CommonService.saveCheckVoucher(list)) {
            throw new ReqDataException("生成凭证失败!");
        }

        //反写单据凭证码
        set = new Record();
        where = new Record();

        int version = TypeUtils.castToInt(billRec.get("persist_version"));
        set.set("statement_code", seqnoOrstatmentCode);
        set.set("persist_version", (version + 1));

        where.set(primartKey, billId);
        where.set("persist_version", version);

        return CommonService.update(tableName, set, where);
    }

    /**
     * 收款通 核对生成凭证
     *
     * @param transIdList
     * @param billId
     * @return
     * @throws BusinessException
     */
    private static boolean sktCheckVoucher(List<Integer> transIdList, long billId, Map<Integer, Date> transDateMap) throws BusinessException {
        Record recvRec = null;
        Record set = null;
        Record where = null;
        Record billRec = null;
        String tableName = "outer_sk_receipts";
        String primartKey = "id";

        //根据单据id查询单据信息
        billRec = Db.findById(tableName, primartKey, billId);

        String seqnoOrstatmentCode = RedisSericalnoGenTool.genVoucherSeqNo();//生成十六进制序列号/凭证号

        Record[] infos = processTranIds(transIdList, seqnoOrstatmentCode);
        recvRec = infos[1];


        String bizId = TypeUtils.castToString(billRec.get("biz_id"));
        List<Record> list = new ArrayList<>();

        if (SK_LXSU.equals(bizId)) {//收款-利息收入
            //============ 收方向 凭证数据组装 begin ============
            list.add(CommonService.sktRecvVorcher(recvRec, billRec, seqnoOrstatmentCode, 1, transDateMap));
            list.add(CommonService.sktRecvVorcher(recvRec, billRec, seqnoOrstatmentCode, 3, transDateMap));
            //============ 收方向 凭证数据组装 end ============
        } else if (SK_LXSUMY.equals(bizId)) { // 收款-利息收入-美元
            //============ 收方向 凭证数据组装 begin ============
            list.add(CommonService.sktRecvVorcher(recvRec, billRec, seqnoOrstatmentCode, 1, transDateMap));
            list.add(CommonService.sktRecvVorcher(recvRec, billRec, seqnoOrstatmentCode, 4, transDateMap));
            //============ 收方向 凭证数据组装 end ============
        } else {
            //============ 付方向 凭证数据组装 begin ============
            list.add(CommonService.sktRecvVorcher(recvRec, billRec, seqnoOrstatmentCode, 1, transDateMap));
            list.add(CommonService.sktRecvVorcher(recvRec, billRec, seqnoOrstatmentCode, 2, transDateMap));
            //============ 付方向 凭证数据组装 end ============
        }


        if (!CommonService.saveCheckVoucher(list)) {
            throw new ReqDataException("生成凭证失败!");
        }

        //反写单据凭证码
        set = new Record();
        where = new Record();

        int version = TypeUtils.castToInt(billRec.get("persist_version"));
        set.set("statement_code", seqnoOrstatmentCode);
        set.set("persist_version", (version + 1));

        where.set(primartKey, billId);
        where.set("persist_version", version);

        return CommonService.update(tableName, set, where);
    }


    /**
     * 处理交易ids    传入交易列表信息，结算码， 返回付款交易和收款交易
     *
     * @param transIdList    交易id 列表
     * @param statement_code 结算码，存入交易表和单据表
     * @return
     * @throws ReqDataException
     */
    private static Record[] processTranIds(List<Integer> transIdList, String statement_code) throws ReqDataException {
        Record payRec = null;
        Record recvRec = null;

        if (transIdList != null && transIdList.size() > 0) {
            for (Integer transId : transIdList) {
                //根据transId查询历史交易数据明细(收/付)
                Record transRec = Db.findById("acc_his_transaction", "id", transId);
                if (transRec != null) {
                    //判断该交易是  1付 or 2收
                    int direction = TypeUtils.castToInt(transRec.get("direction"));
                    if (WebConstant.PayOrRecv.PAYMENT.getKey() == direction) {
                        payRec = transRec;
                    } else {
                        recvRec = transRec;
                    }
                } else {
                    throw new ReqDataException("未找到有效的交易信息!");
                }

                //反写历史交易凭证码
                Record set = new Record()
                        .set("statement_code", statement_code);
                Record where = new Record()
                        .set("id", transId);
                boolean flag = CommonService.update("acc_his_transaction", set, where);
                if (!flag) {
                    throw new ReqDataException("生成凭证失败!");
                }
            }
        }
        return new Record[]{payRec, recvRec};
    }

    public static void main(String[] args) {
        SimpleDateFormat format = new SimpleDateFormat("0MMyyyy");
        System.out.println(format.format(new Date()));
    }
}
