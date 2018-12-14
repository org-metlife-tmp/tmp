package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.exceptions.ReqValidateException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.constant.WebConstant;

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

    private static String ZJZF_QT = "a0cabdb210f848cda9a6e1d090f4a783";        //资金支付-其他
    private static String ZJZF_BDYWJSF = "a0cabdb210f848cda9a6e1d090f4a793";   //资金支付-保单业务结算费
    private static String ZJZF_YHSXF = "a0cabdb210f848cda9a6e1d090f4a893";      //资金支付-保单业务结算费

    private static String PLDB_ZJHB = "d50f3048cee34c6684ff36635353955a";      //内部调部-批量-资金调拨
    private static String PLDB_TLHZ = "b8439d8f56a74da9a6bad8ca04711e04";      //内部调部-批量-投连划转

    private static String PLZF_QT = "982e09f355ea488dbd434d9f8be20365";        //资金支付-批量-其他
    private static String PLZF_BDYWJSF = "982e09f355ea488dbd434d9f8be20465";   //资金支付-批量-保单业务结算费
    private static String PLZF_YHSXF = "982e09f355ea488dbd434d9f8be20565";      //资金支付-批量-保单业务结算费

    private static String SK_LXSU = "d50f3048cee34c6684ff36634343955a";        //收款-利息收入
    private static String SK_LXSUMY = "d50f3048cee34c6684ff36634343944a";   //收款-利息收入(美金)
    private static String SK_QT = "d50f3048cee34c6684ff36634343933a";      //收款-其他


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
        } else {
            throw new ReqValidateException("未定义的业务类型!");
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
                    //判断该交易是  1收 or 2付
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
