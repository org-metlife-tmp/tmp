package com.qhjf.cfm.web.constant;

import com.qhjf.cfm.exceptions.WorkflowException;

import java.util.*;

/**
 * 系统常量
 */
public interface WebConstant {

    int getKey();

    String getDesc();

    enum UserRole {
        admin("管理员"), normal("普通用户");
        String desc;

        UserRole(String desc) {
            this.desc = desc;
        }
    }

    /**
     * 是与否
     */
    @ConstantAnnotation("是与否")
    enum YesOrNo implements WebConstant {
        YES(1, "是"), NO(0, "否");
        protected int key;
        String desc;

        YesOrNo(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    /**
     * 用户访问UA
     */
    enum UserAgent implements WebConstant {
        IPHONE_BS(1, "CFMiPhoneBS"), IPHONE_CS(2, "CFMiPhoneCS"), ANDROID_BS(3, "CFMAndroidBS"),
        ANDROID_CS(4, "CFMAndroidCS");

        int key;
        String desc;

        UserAgent(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

    }

    /**
     * 终端类型
     */
    enum Terminal implements WebConstant {
        PC(1, "PC机"), ANDROID(2, "Android设备"), IOS(3, "IOS设备");
        int key;
        String desc;

        Terminal(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    /**
     * 收或付
     */
    @ConstantAnnotation("收或付")
    enum PayOrRecv implements WebConstant {
        PAYMENT(1, "付"), RECEIPT(2, "收");
        int key;
        String desc;

        PayOrRecv(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    /**
     * 借或贷
     */
    @ConstantAnnotation("借或贷")
    enum CredirOrDebit implements WebConstant {
        DEBIT(1, "借"), CREDIT(2, "贷");
        int key;
        String desc;

        CredirOrDebit(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    @ConstantAnnotation("对账期初数据类型")
    enum InitDataType implements WebConstant {
        QYWD(1, "企业未达"), YHWD(2, "银行未达");
        int key;
        String desc;

        InitDataType(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

    }

    /**
     * 机构/部门状态
     */
    @ConstantAnnotation("机构/部门状态")
    enum OrgDeptStatus implements WebConstant {
        NORMAL(1, "正常"), DISUSE(2, "停用"), DELETE(3, "删除");
        int key;
        String desc;

        OrgDeptStatus(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    /**
     * 用户状态
     */
    @ConstantAnnotation("用户状态")
    enum UserStatus implements WebConstant {
        NORMAL(1, "正常"), FROZEN(2, "冻结"), DELETE(3, "删除");
        int key;
        String desc;

        UserStatus(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static UserStatus getByKey(int key) {
            for (UserStatus userStatus : UserStatus.values()) {
                if (userStatus.key == key) {
                    return userStatus;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    /**
     * 结算账户与商户号的状态
     */
    @ConstantAnnotation("结算账户与商户号的状态")
    enum SetAccAndMerchStatus implements WebConstant {
        NORMAL(1, "正常"), FROZEN(2, "冻结"), DELETE(3, "删除");
        int key;
        String desc;

        SetAccAndMerchStatus(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static SetAccAndMerchStatus getByKey(int key) {
            for (SetAccAndMerchStatus setAccAndMerchStatus : SetAccAndMerchStatus.values()) {
                if (setAccAndMerchStatus.key == key) {
                    return setAccAndMerchStatus;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    /**
     * 结算账户与商户号的收支属性
     */
    @ConstantAnnotation("结算账户与商户号的收支属性")
    enum AccPayOrRecvAttr implements WebConstant {
        RECV(1, "收"), PAY(2, "支"), INTEGRATION(3, "收支一体");
        int key;
        String desc;

        AccPayOrRecvAttr(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static AccPayOrRecvAttr getByKey(int key) {
            for (AccPayOrRecvAttr accPayOrRecvAttr : AccPayOrRecvAttr.values()) {
                if (accPayOrRecvAttr.key == key) {
                    return accPayOrRecvAttr;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    @ConstantAnnotation("支付方式")
    enum PayOrRecvMode implements WebConstant {

        SSDF(1, "实时代付"), SSDK(2, "实时代扣"), SMSK(3, "扫描收款"), PLDF(4, "批量代付"), PLDK(5, "批量代扣");

        int key;
        String desc;

        PayOrRecvMode(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static PayOrRecvMode getByKey(int key) {
            for (PayOrRecvMode payOrRecvMode : PayOrRecvMode.values()) {
                if (payOrRecvMode.key == key) {
                    return payOrRecvMode;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

    }

    @ConstantAnnotation("支付结算方式")
    enum PayMode implements WebConstant {

        DIRECTCONN(1, "直联"), NETSILVER(2, "网银"), ADDITIONAL(8, "自动扣款");
        // , OFFER(3, "报盘"), CASH(4, "现金"), CHECK(5, "支票 "), POS(6, "POS "), OTHER(7,
        // "其它");

        int key;
        String desc;

        PayMode(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static PayMode getByKey(int key) {
            for (PayMode payMode : PayMode.values()) {
                if (payMode.key == key) {
                    return payMode;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

    }

    @ConstantAnnotation("支付结算方式")
    enum ZjdbType implements WebConstant {

        FORSELF(1, "本公司内部账户调拨"), FORJUNIOR(2, "本公司拨给下级公司"), FORSUPPER(3, "本公司拨给上级公司");

        int key;
        String desc;

        ZjdbType(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static ZjdbType getByKey(int key) {
            for (ZjdbType zjdbType : ZjdbType.values()) {
                if (zjdbType.key == key) {
                    return zjdbType;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

    }

    /**
     * 平台交易状态
     */
    enum TradeStatus implements WebConstant {
        INIT(0, "初始化"), SUCCESS(1, "成功"), FAILED(2, "失败"), PROCESSING(3, "处理中");

        int key;
        String desc;

        TradeStatus(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static TradeStatus getByKey(int key) {
            for (TradeStatus tradeStatus : TradeStatus.values()) {
                if (tradeStatus.key == key) {
                    return tradeStatus;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    /**
     * 支付状态
     */
    @ConstantAnnotation("支付状态")
    enum PayStatus implements WebConstant {
        INIT(0, "已保存"), SUCCESS(1, "成功"), FAILD(2, "失败"), HANDLE(3, "处理中"), CANCEL(4, "已作废");
        int key;
        String desc;

        PayStatus(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    @ConstantAnnotation("单据状态")
    enum BillStatus implements WebConstant {
        SAVED(1, "已保存"), SUBMITED(2, "已提交"), AUDITING(3, "审批中"), PASS(4, "审批通过"), REJECT(5, "审批拒绝"),
        PROCESSING(6, "处理中"), SUCCESS(7, "已成功"), FAILED(8, "已失败"), CANCEL(9, "已作废"), NOCOMPLETION(10, "未完结"),
        COMPLETION(11, "已完结"), WAITPROCESS(12, "待处理");

        int key;
        String desc;

        BillStatus(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static BillStatus getBillStatus(int status) {
            for (BillStatus b : BillStatus.values()) {
                if (status == b.getKey()) {
                    return b;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

    }

    @ConstantAnnotation("主要业务类型")
    enum MajorBizType implements WebConstant {
    	ACC_OPEN_INT(1, "开户事项申请", "AOI"), ACC_OPEN_COM(2, "开户信息补录申请", "AOC"), ACC_CHG_APL(3, "账户变更申请", "ACA"),
        ACC_FREEZE_APL(4, "账户冻结申请", "AFA"), ACC_DEFREEZE_APL(5, "账户解冻申请", "ADA"), ACC_CLOSE_INT(6, "销户事项申请", "ACI"),
        ACC_CLOSE_COM(7, "销户信息补录申请", "ACC"), INNERDB(8, "内部调拨", "IDB"), ZFT(9, "支付通", "ZFT"),
        INNERDB_BATCH(10, "内部调部-批量", "IBP"), OBP(11, "支付通-批量", "OBP"), GJT(12, "归集通", "GJT"),
        ALLOCATION(13, "资金下拨", "ALT"), GYL(14, "广银联备付金", "GYL"), SKT(15, "收款通", "SKT"), DZT(16, "对账通", "DZT"),
        MONITOR(17, "资金监控", "MNT"), ELECTRONIC(18, "电子回单", "ELE"), CBB(19, "非直连归集", "CBB"),
        OA_HEAD_PAY(20, "OA总公司付款", "OHDP"), OA_BRANCH_PAY(21, "OA分公司付款", "OBRP"), REFUND(22, "主动退票", "ZDTP"),
        DOUBTFUL_REFUND(23, "可疑退票", "KYTP"), PLF(24, "组批审批", "SFF_PLF"), PLS(25, "批量收款", "PLS"),
        PLF_EXCEPT_BACK(26, "申请退回", "PLF_EXCEPT_BACK"), SFF_PLF_DSF(29, "收付费批量付款第三方", "SFF_PLF_DSF"),
        SFF_PLF_INNER(30, "收付费批量付款内部调拨", "SFF_PLF_INNER"), PLS_EXCEPT_BACK(31, "批量收申请退回", "PLS_EXCEPT_BACK"),
        GMF(32, "柜面付", "GMF"), CWYTJ(33, "财务预提交", "CWYTJ"), CWCX(34, "财务冲销", "CWCX"),
        VOUCHER_QUERY(35, "凭证查询", "VOUCHER_QUERY"),PLF_HD_VOUCHER(36, "批量付回调", "PLF_HD_VOUCHER"),
		PLS_HD_VOUCHER(37, "批量收回调", "PLS_HD_VOUCHER"),
        GMF_HD_VOUCHER(38, "柜面付回调", "GMF_HD_VOUCHER"),GMSGD(39, "柜面收个单", "GMSGD"),
		GMSTD(40, "柜面收团单", "GMSTD"),GMSCG_HD_VOUCHER(41, "柜面收常规回调", "GMSCG_HD_VOUCHER"),GMSWPP_HD_VOUCHER(42, "柜面收未匹配回调", "GMSWPP_HD_VOUCHER"),
        GMS(43, "柜面收撤销审批", "GMS"),NC_HEAD_PAY(44,"NC总公司付款","NCHP");
        int key;
        String desc;
        String prefix;

        MajorBizType(int key, String desc, String prefix) {
            this.key = key;
            this.desc = desc;
            this.prefix = prefix;
        }

        public static MajorBizType getBizType(int bizType) throws WorkflowException {
            for (MajorBizType b : MajorBizType.values()) {
                if (bizType == b.getKey()) {
                    return b;
                }
            }
            throw new WorkflowException("未定义的biz_type!");
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

        public String getPrefix() {
            return prefix;
        }

        public String getTableName() throws WorkflowException {
            switch (this) {
                case ACC_OPEN_INT:// 开户事项
                    return "acc_open_intention_apply";
                case ACC_OPEN_COM:// 开户补录
                    return "acc_open_complete_apply";
                case ACC_CHG_APL:// 账户变更
                    return "acc_change_apply";
                case ACC_CLOSE_INT:// 销户事项
                    return "acc_close_intertion_apply";
                case ACC_CLOSE_COM:// 销户补录
                    return "acc_close_complete_apply";
                case ACC_FREEZE_APL:// 冻结
                case ACC_DEFREEZE_APL:// 解冻
                    return "acc_freeze_and_defreeze_apply";
                case INNERDB:// 内部调拨
                    return "inner_db_payment";
                case ZFT:
                    return "outer_zf_payment";
                case OBP:// 内部调拨单笔
                    return "outer_batchpay_baseinfo";
                case INNERDB_BATCH:// 内部调拨批量
                    return "inner_batchpay_baseinfo";
                case GJT:
                    return "collect_topic";
                case GYL:
                    return "gyl_allocation_topic";
                case ALLOCATION:// 资金下拨
                    return "allocation_topic";
                case SKT:// 收款通
                    return "outer_sk_receipts";
                case DZT:// 对账通
                    return "";
                case MONITOR:// 资金监控
                    return "";
                case ELECTRONIC:
                    return "";
                case OA_HEAD_PAY:
                    return "oa_head_payment";
                case OA_BRANCH_PAY:
                    return "oa_branch_payment";
                case CBB:
                    return "collect_batch_baseinfo";
                case REFUND: // 主动退票
                    return "acc_refundable_trans";
                case DOUBTFUL_REFUND: // 可疑退票
                    return "acc_refundable_trans";
                case PLF: // 批量付
                    return "pay_batch_total_master";
                case PLF_EXCEPT_BACK: // 批量付异常处理回退流程
                    return "pay_batch_total";
                case PLS_EXCEPT_BACK: // 批量收异常处理回退流程
                    return "recv_batch_total";
                case GMF: // 柜面付单据表
                    return "gmf_bill";
                case GMS:
                    return "recv_counter_bill";
                default:
                    throw new WorkflowException("未定义" + this.name() + "的数据库表对应关系！");
            }
        }

        public String getBillTableName() throws WorkflowException {
            switch (this) {
                case ACC_OPEN_INT:// 开户事项
                    return "acc_open_intention_apply";
                case ACC_OPEN_COM:// 开户补录
                    return "acc_open_complete_apply";
                case ACC_CHG_APL:// 账户变更
                    return "acc_change_apply";
                case ACC_CLOSE_INT:// 销户事项
                    return "acc_close_intertion_apply";
                case ACC_CLOSE_COM:// 销户补录
                    return "acc_close_complete_apply";
                case ACC_FREEZE_APL:// 冻结
                case ACC_DEFREEZE_APL:// 解冻
                    return "acc_freeze_and_defreeze_apply";
                case INNERDB:// 内部调拨
                    return "inner_db_payment";
                case ZFT: // 支付通
                    return "outer_zf_payment";
                case OBP:// 支付通_批量
                    return "outer_batchpay_baseinfo,outer_batchpay_bus_attach_detail";
                case INNERDB_BATCH:// 内部调拨_批量
                    return "inner_batchpay_baseinfo,inner_batchpay_bus_attach_detail";
                case GJT:
                    return "collect_execute_instruction";
                case GYL:
                    return "gyl_allocation_execute_instruction";
                case ALLOCATION:// 资金下拨
                    return "allocation_execute_instruction";
                case SKT:// 收款通
                    return "outer_sk_receipts";
                case DZT:// 对账通
                    return "";
                case MONITOR:// 资金监控
                    return "";
                case ELECTRONIC:
                    return "";
                case OA_HEAD_PAY: // OA 总公司支付
                    return "oa_head_payment";
                case OA_BRANCH_PAY:
                    return "oa_branch_payment_item";
                case CBB: // 非直连归集
                    return "collect_batch_baseinfo,collect_batch_bus_attach_detail";
                case REFUND: // 主动退票
                    return "acc_refundable_trans";
                case DOUBTFUL_REFUND: // 可疑退票
                    return "acc_refundable_trans";
                default:
                    throw new WorkflowException("未定义" + this.name() + "的数据库表对应关系！");
            }
        }

        /**
         * 根据业务类型获取对应的权限串
         *
         * @param type
         * @return
         */
        public static String[] getCorrespondingForces(MajorBizType type) {
            switch (type) {
                case ACC_OPEN_INT:
                    return new String[]{"AccOpenIntAppl"};
                case ACC_OPEN_COM:
                    return new String[]{"AccOpenComAppl"};
                case ACC_CHG_APL:
                    return new String[]{"AccChgAppl"};
                case ACC_FREEZE_APL:
                    return new String[]{"AccFreezeAppl"};
                case ACC_DEFREEZE_APL:
                    return new String[]{"AccDefreezeAppl"};
                case ACC_CLOSE_INT:
                    return new String[]{"AccCloseIntAppl"};
                case ACC_CLOSE_COM:
                    return new String[]{"AccCloseComAppl"};
                case INNERDB:
                    return new String[]{"DbtViewBill", "DbtPayBill"};
                case INNERDB_BATCH:
                    return new String[]{"DbtBatchPay", "DbtBatchView"};
                case ZFT:
                    return new String[]{"ZFTPayBill", "ZFTViewBill"};
                case OBP:
                    return new String[]{"ZFTBatchPay", "ZFTBatchView"};
                case GJT:
                    return new String[]{"GJView"};
                case ALLOCATION:
                    return new String[]{"ZJXBView"};
                case GYL:
                    return new String[]{"GYLBFJView"};
                case SKT:
                    return new String[]{"SKTViewBill"};
                case CBB:
                    return new String[]{"GJBatchView"};
                case OA_HEAD_PAY:
                    return new String[]{"OAHeadPay"};
                case OA_BRANCH_PAY:
                    return new String[]{"OABranchPay"};
                case PLF:
                    return new String[]{"PayBatchSend"};
                case GMF:   //柜面付审批流
                    return new String[]{"PayCounterPlat"};
                case GMS:   //柜面收审批流
                    return new String[]{"RECVCOUNTERGROUP","RECVCOUNTERPERSON"};
                case DZT:
                case MONITOR:
                case ELECTRONIC:
                case REFUND:
                case DOUBTFUL_REFUND:
                case PLS:
                case PLF_EXCEPT_BACK:
                    return new String[]{"PayBatchDoExcp"};
                case PLS_EXCEPT_BACK:
                case SFF_PLF_DSF:
                case SFF_PLF_INNER:
                default:
                    return null;
            }

        }
    }

    @ConstantAnnotation("账户操作类型")
    enum AccOperType implements WebConstant {
        OPEN(1, "开户"), FREEZE(2, "冻结"), DEFREEZE(3, "解冻"), CLOSE(4, "销户");

        int key;
        String desc;

        AccOperType(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static AccOperType getAccOperType(int key) {
            for (AccOperType anEnum : AccOperType.values()) {
                if (anEnum.getKey() == key) {
                    return anEnum;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    @ConstantAnnotation("账户维护锁定表类型")
    enum AccProcessLockType implements WebConstant {
        CHANGE(1, "变更"), FREORDEFRE(2, "冻结/解冻"), CLOSE(3, "销户");

        int key;
        String desc;

        AccProcessLockType(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static AccProcessLockType getAccProcessLockType(int key) {
            for (AccProcessLockType type : AccProcessLockType.values()) {
                if (type.getKey() == key) {
                    return type;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

    }

    @ConstantAnnotation("直联模式")
    enum InactiveMode implements WebConstant {
        DIRECTCONN(1, "直联"), MANUAL(2, "人工");

        int key;
        String desc;

        InactiveMode(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

    }

    @ConstantAnnotation("账户变更字段")
    enum AccountChangeField implements WebConstant {
        ACCNAME(1, "账户名称"), ORG(2, "所属机构"), BANK(3, "开户行"), LAWFULLMAN(4, "账户法人"), CURRENCY(5, "币种"),
        ACCATTR(6, "账户属性"), INACTIVEMODE(7, "账户模式"), ACCPURPOSE(8, "账户用途");

        int key;
        String desc;

        AccountChangeField(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static AccountChangeField get(int key) {
            for (AccountChangeField accountChangeField : values()) {
                if (accountChangeField.key == key) {
                    return accountChangeField;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    @ConstantAnnotation("存款模式")
    enum DepositsMode implements WebConstant {
        REGULAR(1, "定期"), CURRENT(2, "活期");

        int key;
        String desc;

        DepositsMode(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return this.key;
        }

        @Override
        public String getDesc() {
            return this.desc;
        }
    }

    @ConstantAnnotation("账户状态")
    enum AccountStatus implements WebConstant {
        NORAMAL(1, "正常"), CLOSED(2, "销户"), FREEZE(3, "冻结");

        int key;
        String desc;

        AccountStatus(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static AccountStatus getByKey(int key) {
            for (AccountStatus as : values()) {
                if (as.getKey() == key) {
                    return as;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    @ConstantAnnotation("工作流表达式类型")
    enum WfExpressType implements WebConstant {
        AMOUNT(1, "金额"), STATUS(2, "单据状态");

        int key;
        String desc;

        WfExpressType(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static WfExpressType getTypeByName(String name) {
            if (name != null) {
                try {
                    return Enum.valueOf(WfExpressType.class, name.trim());
                } catch (IllegalArgumentException e) {

                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

    }

    @ConstantAnnotation("数据来源")
    enum DataSource implements WebConstant {
        DIRECTCONN(1, "银企直连"), MANUALIMP(2, "人工导入"), SYSTEMIMP(3, "系统导入");

        int key;
        String desc;

        DataSource(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    @ConstantAnnotation("业务类型类别")
    enum BizType implements WebConstant {
        ACC_MGR(1, "账户管理"), OA_MGR(2, "OA审批"), DBT_MGR(3, "调拨通"), ZFT_MGR(4, "支付通"), GJT_MGR(5, "归集通"),
        GYL_MGR(6, "广银联备付金"), ZJXB_MGR(7, "资金下拨"), SKT_MGR(8, "收款通"), DZT_MGR(9, "对账通"), MONITOR_MGR(10, "资金监控"),
        OADATA_MGR(11, "OA付款管理"), ELECTRONIC_MGR(12, "电子回单"), REFUND_MGR(13, "退票"), SFF_MGR(14, "收付费")
        , VOUCHER_QUERY(15, "凭证查询");

        int key;
        String desc;

        BizType(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static Map<String, List<MajorBizType>> getAllMajorBizType() {
            Map<String, List<MajorBizType>> map = new HashMap<>();
            for (BizType bizType : values()) {
                if (bizType.equals(ACC_MGR)) {
                    map.put(bizType.name(), Arrays.asList(MajorBizType.ACC_OPEN_INT, MajorBizType.ACC_OPEN_COM,
                            MajorBizType.ACC_CHG_APL, MajorBizType.ACC_FREEZE_APL, MajorBizType.ACC_DEFREEZE_APL,
                            MajorBizType.ACC_CLOSE_INT, MajorBizType.ACC_CLOSE_COM));
                } else if (bizType.equals(OA_MGR)) {
                    map.put(bizType.name(), new ArrayList<MajorBizType>());
                } else if (bizType.equals(DBT_MGR)) {
                    map.put(bizType.name(), Arrays.asList(MajorBizType.INNERDB, MajorBizType.INNERDB_BATCH));
                } else if (bizType.equals(ZFT_MGR)) {
                    map.put(bizType.name(), Arrays.asList(MajorBizType.OBP, MajorBizType.ZFT));
                } else if (bizType.equals(GJT_MGR)) {
                    map.put(bizType.name(), Arrays.asList(MajorBizType.GJT, MajorBizType.CBB));
                } else if (bizType.equals(GYL_MGR)) {
                    map.put(bizType.name(), Arrays.asList(MajorBizType.GYL));
                } else if (bizType.equals(ZJXB_MGR)) {
                    map.put(bizType.name(), Arrays.asList(MajorBizType.ALLOCATION));
                } else if (bizType.equals(SKT_MGR)) {
                    map.put(bizType.name(), Arrays.asList(MajorBizType.SKT));
                } else if (bizType.equals(DZT_MGR)) {
                    map.put(bizType.name(), Arrays.asList(MajorBizType.DZT));
                } else if (bizType.equals(MONITOR_MGR)) {
                    map.put(bizType.name(), Arrays.asList(MajorBizType.MONITOR));
                } else if (bizType.equals(OADATA_MGR)) {
                    map.put(bizType.name(), Arrays.asList(MajorBizType.OA_HEAD_PAY, MajorBizType.OA_BRANCH_PAY));
                } else if (bizType.equals(ELECTRONIC_MGR)) {
                    map.put(bizType.name(), Arrays.asList(MajorBizType.ELECTRONIC));
                } else if (bizType.equals(REFUND_MGR)) {
                    map.put(bizType.name(), Arrays.asList(MajorBizType.REFUND, MajorBizType.DOUBTFUL_REFUND));
                } else if (bizType.equals(SFF_MGR)) {
                    map.put(bizType.name(),
                            Arrays.asList(MajorBizType.PLF, MajorBizType.PLS, MajorBizType.PLF_EXCEPT_BACK, MajorBizType.PLS_EXCEPT_BACK,
                                    MajorBizType.GMF, MajorBizType.GMS)
                    );
                } else if (bizType.equals(VOUCHER_QUERY)) {
                    map.put(bizType.name(), Arrays.asList(MajorBizType.VOUCHER_QUERY, MajorBizType.VOUCHER_QUERY));
                }
            }
            return map;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    @ConstantAnnotation("临时表状态")
    enum TempTableStatus implements WebConstant {
        ORIGINAL(1, "原有"), NEW(2, "新增"), DELETED(3, "删除");

        int key;
        String desc;

        TempTableStatus(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    @ConstantAnnotation("归集/资金下拨类型")
    enum CollOrPoolType implements WebConstant {
        FIXED(1, "定额"), RESERVE(2, "留存余额"), ALL(3, "全额");

        int key;
        String desc;

        CollOrPoolType(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static CollOrPoolType getByKey(int key) {
            for (CollOrPoolType cp : values()) {
                if (cp.getKey() == key) {
                    return cp;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    @ConstantAnnotation("归集/资金下拨频率")
    enum CollOrPoolFrequency implements WebConstant {
        DAILY(1, "每日"), WEEKLY(2, "每周"), MONTHLY(3, "每月");

        int key;
        String desc;

        CollOrPoolFrequency(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static CollOrPoolFrequency getByKey(int key) {
            for (CollOrPoolFrequency cp : values()) {
                if (cp.getKey() == key) {
                    return cp;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    @ConstantAnnotation("归集/资金下拨任务运行状态")
    enum CollOrPoolRunStatus implements WebConstant {
        SAVED(1, "已保存"), SENDING(2, "已发送"), SUCCESS(3, "已成功"), FAILED(4, "已失败"), CANCEL(5, "已作废");

        int key;
        String desc;

        CollOrPoolRunStatus(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    @ConstantAnnotation("周期任务组类型")
    enum CronTaskGroup implements WebConstant {
        BALQUEYR(1, "balQ_", "余额查询", ""), TRANSQUEYR(2, "transQ_", "交易查询", ""),
        COLLECT(3, "collect_", "资金归集", "com.qhjf.cfm.web.quartzs.jobs.CollectJob"),
        ALLOCATION(4, "allocation_", "资金下拨", ""), GYL_ALLOCATION(5, "gyl_allcatione_", "广银联备付金", "");

        int key;
        String prefix;
        String desc;
        String classPath;

        CronTaskGroup(int key, String prefix, String desc, String classPath) {
            this.key = key;
            this.prefix = prefix;
            this.desc = desc;
            this.classPath = classPath;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getClassPath() {
            return classPath;
        }
    }

    @ConstantAnnotation("OA付款类型")
    enum OaPayOrgType implements WebConstant {
        OA_HEAD_PAY(1, "总公司付款"), OA_BRANCH_PAY(2, "分公司付款");

        int key;
        String desc;

        OaPayOrgType(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    @ConstantAnnotation("OA接口状态")
    enum OaInterfaceStatus implements WebConstant {
        OA_INTER_RECV_SUCCESS(1, "接收成功"), OA_INTER_PROCEEING(2, "处理中"), OA_INTER_PROCESS_S(3, "处理成功"),
        OA_INTER_PROCESS_F(4, "处理失败");

        int key;
        String desc;

        OaInterfaceStatus(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

        public static OaInterfaceStatus getOaInterfaceStatus(int key) {
            for (OaInterfaceStatus value : OaInterfaceStatus.values()) {
                if (value.getKey() == key) {
                    return value;
                }
            }
            return null;
        }

    }

    @ConstantAnnotation("OA内部处理状态")
    enum OaProcessStatus implements WebConstant {
        OA_CONVERT_SUCCESS(1, "转换成功"), OA_CONVERT_FAILED(2, "转换失败"), OA_TRADE_SUCCESS(3, "交易成功"),
        OA_TRADE_FAILED(4, "交易失败"), OA_TRADE_CANCEL(5, "交易作废");

        int key;
        String desc;

        OaProcessStatus(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

        public static OaProcessStatus getOaProcessStatus(int key) {
            for (OaProcessStatus value : OaProcessStatus.values()) {
                if (value.getKey() == key) {
                    return value;
                }
            }
            return null;
        }

    }

    enum COMMONUser {
        JZUser(-101l, "机制用户");

        Long id;
        String name;

        COMMONUser(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    enum COMMONUodp {
        JZUodp(-101l, -101l, -101l, -101l, "机制机构", "机制部门", "机制岗位", 1);

        Long id;
        Long orgId;
        Long deptId;
        Long posId;
        String orgName;
        String deptName;
        String posName;
        Integer isDefault;

        COMMONUodp(Long id, Long orgId, Long deptId, Long posId, String orgName, String deptName, String posName,
                   Integer isDefault) {
            this.id = id;
            this.orgId = orgId;
            this.deptId = deptId;
            this.posId = posId;
            this.orgName = orgName;
            this.deptName = deptName;
            this.posName = posName;
            this.isDefault = isDefault;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getOrgId() {
            return orgId;
        }

        public void setOrgId(Long orgId) {
            this.orgId = orgId;
        }

        public Long getDeptId() {
            return deptId;
        }

        public void setDeptId(Long deptId) {
            this.deptId = deptId;
        }

        public Long getPosId() {
            return posId;
        }

        public void setPosId(Long posId) {
            this.posId = posId;
        }

        public String getOrgName() {
            return orgName;
        }

        public void setOrgName(String orgName) {
            this.orgName = orgName;
        }

        public String getDeptName() {
            return deptName;
        }

        public void setDeptName(String deptName) {
            this.deptName = deptName;
        }

        public String getPosName() {
            return posName;
        }

        public void setPosName(String posName) {
            this.posName = posName;
        }

        public Integer getIsDefault() {
            return isDefault;
        }

        public void setIsDefault(Integer isDefault) {
            this.isDefault = isDefault;
        }

    }

    /**
     * 收付费管理_交互方式
     */
    @ConstantAnnotation("收付费管理_交互方式")
    enum SftInteractiveMode implements WebConstant {
        DIRECTCON(0, "直连"), DOCUMENT(1, "报盘");
        int key;
        String desc;

        SftInteractiveMode(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static SftInteractiveMode getSftInteractiveMode(int key) {
            for (SftInteractiveMode value : SftInteractiveMode.values()) {
                if (value.getKey() == key) {
                    return value;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    /**
     * 收付费管理_支付方式
     */
    @ConstantAnnotation("收付费管理_支付方式")
    enum SftPayMode implements WebConstant {
        PLSF(0, "0", "批量收付"), SSSF(1, "1", "实时收付"), DSF(2, "2", "第三方");
        int key;
        String keyc;
        String desc;

        SftPayMode(int key, String keyc, String desc) {
            this.key = key;
            this.keyc = keyc;
            this.desc = desc;
        }

        public static SftPayMode getSftPayModeByKeyc(String keyc) {
            for (SftPayMode value : SftPayMode.values()) {
                if (keyc.equals(value.getKeyc())) {
                    return value;
                }
            }
            return null;
        }

        public static SftPayMode getSftPayModeByKey(int key) {
            for (SftPayMode value : SftPayMode.values()) {
                if (key == value.getKey()) {
                    return value;
                }
            }
            return null;
        }

        public String getKeyc() {
            return keyc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    /**
     * 收付费管理_可疑数据支付方式
     */
    @ConstantAnnotation("收付费管理_批量付_支付方式")
    enum SftDoubtPayMode implements WebConstant {
        PLSF(0, "C", "批量收付"), SSSF(1, "Q", "实时收付"), DSF(2, "H", "第三方"), WY(3, "0", "网银");
        int key;
        String keyc;
        String desc;

        SftDoubtPayMode(int key, String keyc, String desc) {
            this.key = key;
            this.keyc = keyc;
            this.desc = desc;
        }

        public static SftDoubtPayMode getSftDoubtPayModeByKeyc(String keyc) {
            for (SftDoubtPayMode value : SftDoubtPayMode.values()) {
                if (keyc.equals(value.getKeyc())) {
                    return value;
                }
            }
            return null;
        }

        public static SftDoubtPayMode getSftDoubtPayModeByKey(int key) {
            for (SftDoubtPayMode value : SftDoubtPayMode.values()) {
                if (key == value.getKey()) {
                    return value;
                }
            }
            return null;
        }

        public String getKeyc() {
            return keyc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }


    /**
     * 收付费管理_可疑数据支付方式
     */
    @ConstantAnnotation("收付费管理_批量收_支付方式")
    enum SftDoubtRecvMode implements WebConstant {
        PLSF(0, "D", "批量收付"), WY(1, "C", "网银");
        int key;
        String keyc;
        String desc;

        SftDoubtRecvMode(int key, String keyc, String desc) {
            this.key = key;
            this.keyc = keyc;
            this.desc = desc;
        }

        public static SftDoubtRecvMode getSftDoubtRecvModeByKeyc(String keyc) {
            for (SftDoubtRecvMode value : SftDoubtRecvMode.values()) {
                if (keyc.equals(value.getKeyc())) {
                    return value;
                }
            }
            return null;
        }

        public static SftDoubtRecvMode getSftDoubtRecvModeByKey(int key) {
            for (SftDoubtRecvMode value : SftDoubtRecvMode.values()) {
                if (key == value.getKey()) {
                    return value;
                }
            }
            return null;
        }

        public String getKeyc() {
            return keyc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    /**
     * 收付费管理_收付属性
     */
    @ConstantAnnotation("收付费管理_收付属性")
    enum SftPayAttr implements WebConstant {
        RECV(0, "收"), PAY(1, "付");
        int key;
        String desc;

        SftPayAttr(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static SftPayAttr getSftPayAttr(int key) {
            for (SftPayAttr value : SftPayAttr.values()) {
                if (value.getKey() == key) {
                    return value;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    /**
     * 收付费管理_系统来源
     */
    @ConstantAnnotation("收付费管理_系统来源")
    enum SftOsSource implements WebConstant {
        LA(0, "LA"), EBS(1, "EBS");
        int key;
        String desc;

        SftOsSource(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static SftOsSource getSftOsSource(int key) {
            for (SftOsSource value : SftOsSource.values()) {
                if (value.getKey() == key) {
                    return value;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    /**
     * 收付费管理_系统来源
     */
    @ConstantAnnotation("收付费管理_柜面收_来源系统")   //使用SftOsSource会导致批量收/付下拉框多数据
    enum SftOsSourceCounter implements WebConstant {
        LA(0, "LA"), EBS(1, "EBS") , NB(2, "NB");
        int key;
        String desc;

        SftOsSourceCounter(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static SftOsSourceCounter getSftOsSource(int key) {
            for (SftOsSourceCounter value : SftOsSourceCounter.values()) {
                if (value.getKey() == key) {
                    return value;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }
    
    /**
     * 收付费管理_所属渠道
     */
    @ConstantAnnotation("收付费管理_所属渠道")
    enum SftSubordinateChannel implements WebConstant {
        ALL(0, "全部"), DLR(1, "代理人"), YB(2, "银保"), WXKF(3, "网销客服"), DHYX(4, "电话行销");
        int key;
        String desc;

        SftSubordinateChannel(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static SftSubordinateChannel getSftSubordinateChannel(int key) {
            for (SftSubordinateChannel value : SftSubordinateChannel.values()) {
                if (value.getKey() == key) {
                    return value;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    /**
     * 收付费管理_结算模式
     */
    @ConstantAnnotation("收付费管理_结算模式")
    enum SftNetMode implements WebConstant {
        JEMS(0, "净额模式"), QEMS(1, "全额模式");
        int key;
        String desc;

        SftNetMode(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static SftNetMode getSftNetMode(int key) {
            for (SftNetMode value : SftNetMode.values()) {
                if (value.getKey() == key) {
                    return value;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    /**
     * 收付费_渠道
     */
    @ConstantAnnotation("收付费管理_渠道")
    enum Channel implements WebConstant {
        ZP(1, "招行批量"), RP(2, "融汇通批量"), GP(3, "广银联批量"), GX(4, "广银联信用卡"), GS(5, "广银联实时"), TP(6, "通联批量"), JP(7, "建行批量");
        int key;
        String desc;

        Channel(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

    }

    /**
     * 收付费_盘片类型
     */
    @ConstantAnnotation("收付费管理_盘片类型")
    enum DocumentType implements WebConstant {
        FB(1, "付款报盘"), FH(2, "付款回盘"), SB(3, "收款报盘"), SH(4, "收款回盘");
        int key;
        String desc;

        DocumentType(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

        public static DocumentType getByKey(int key) {
            for (DocumentType dt : values()) {
                if (dt.getKey() == key) {
                    return dt;
                }
            }
            return null;
        }
    }

    /**
     * 收付费管理_合法数据状态
     */
    @ConstantAnnotation("收付费管理_付款信息合法数据状态")
    enum SftLegalData implements WebConstant {
        NOGROUP(0, "未提交"), GROUPBATCH(1, "已提交"), REVOKE(2, "已拒绝");
        int key;
        String desc;

        SftLegalData(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }

    @ConstantAnnotation("收付通接口状态")
    enum SftInterfaceStatus implements WebConstant {
        SFT_INTER_PROCESS_S(1, "处理成功"), SFT_INTER_PROCESS_F(2, "处理失败"), SFT_INTER_PROCEEING(3, "处理中"),
        SFT_INTER_PROCESS_N(4, "未上线通道不做处理");

        int key;
        String desc;

        SftInterfaceStatus(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

        public static SftInterfaceStatus getSftInterfaceStatus(int key) {
            for (SftInterfaceStatus value : SftInterfaceStatus.values()) {
                if (value.getKey() == key) {
                    return value;
                }
            }
            return null;
        }

    }

    @ConstantAnnotation("收付通回调状态")
    enum SftCallbackStatus implements WebConstant {
        SFT_NO_CALLBACK(1, "未回调"), SFT_CALLBACK_S(2, "回调成功"), SFT_CALLBACK_F(3, "回调失败"), SFT_CALLBACKING(4, "回调中");

        int key;
        String desc;

        SftCallbackStatus(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

        public static SftCallbackStatus getSftCallbackStatus(int key) {
            for (SftCallbackStatus value : SftCallbackStatus.values()) {
                if (value.getKey() == key) {
                    return value;
                }
            }
            return null;
        }

    }

    /**
     * 收付费_盘片类型
     */
    @ConstantAnnotation("收付费管理_子批次状态")
    enum SftCheckBatchStatus implements WebConstant {
        SPZ(1, "审批中"), SPWFS(2, "已审批未发送"), SPJJ(7, "审批拒绝"), FSWHP(4, "已发送未回盘"), HPCG(5, "回盘成功"), HPYC(6, "回盘异常"),
        HTSPZ(3, "回退审批中"), YHT(8, "已回退"), ZPWFS(9, "已组批未发送 ");
        int key;
        String desc;

        SftCheckBatchStatus(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

        public static SftCheckBatchStatus getByKey(int key) {
            for (SftCheckBatchStatus dt : values()) {
                if (dt.getKey() == key) {
                    return dt;
                }
            }
            return null;
        }
    }

    /**
     * 收付费_批量收_主要业务类型
     */
    @ConstantAnnotation("收付费_批量收_主要业务类型")
    enum Sft_BizType implements WebConstant {
        SQ(0, "首期"), XQ(1, "续期");
        int key;
        String desc;

        Sft_BizType(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

        public static Sft_BizType getByKey(int key) {
            for (Sft_BizType sbt : values()) {
                if (sbt.getKey() == key) {
                    return sbt;
                }
            }
            return null;
        }
    }

    /**
     * 收付费_批量收_主要业务类型
     */
    @ConstantAnnotation("收付费_柜面付_补录状态")
    enum Sft_supplyStatus implements WebConstant {
        WBL(0, "未补录"), YBL(1, "已补录");
        int key;
        String desc;

        Sft_supplyStatus(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

        public static Sft_supplyStatus getByKey(int key) {
            for (Sft_supplyStatus sbt : values()) {
                if (sbt.getKey() == key) {
                    return sbt;
                }
            }
            return null;
        }
    }

    /**
     * 收付费_批量收_主要业务类型
     */
    @ConstantAnnotation("收付费_柜面付_补录状态")
    enum Sft_Billstatus implements WebConstant {
        WJF(0, "未给付"), SPZ(1, "审批中"), SPJJ(2, "审批拒绝"), SPTG(3, "审批通过,给付中"), JFCG(4, "给付成功"), JFSB(5, "给付失败");
        int key;
        String desc;

        Sft_Billstatus(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

        public static Sft_Billstatus getByKey(int key) {
            for (Sft_Billstatus sbt : values()) {
                if (sbt.getKey() == key) {
                    return sbt;
                }
            }
            return null;
        }
    }

    @ConstantAnnotation("收付费_柜面收_团单收款方式")
	enum Sft_RecvGroupCounter_Recvmode  implements WebConstant {
		XJJKD(1, "2", "现金解款单"), ZP(2, "3", "支票"), WYHK(3, "4", "网银/汇款");
		int key;
		String keyc;
		String desc;

		Sft_RecvGroupCounter_Recvmode(int key, String keyc, String desc) {
			this.key = key;
			this.keyc = keyc;
			this.desc = desc;
		}

		@Override
		public int getKey() {
			return key;
		}

		public String getKeyc() {
			return keyc;
		}

		@Override
		public String getDesc() {
			return desc;
		}

		public static Sft_RecvGroupCounter_Recvmode getSft_RecvGroupCounter_RecvmodeKeyc(String keyc) {
			for (Sft_RecvGroupCounter_Recvmode value : Sft_RecvGroupCounter_Recvmode.values()) {
				if (keyc.equals(value.getKeyc())) {
					return value;
				}
			}
			return null;
		}

		public static Sft_RecvGroupCounter_Recvmode getSft_RecvGroupCounter_RecvmodeByKey(int key) {
			for (Sft_RecvGroupCounter_Recvmode value : Sft_RecvGroupCounter_Recvmode.values()) {
				if (key == value.getKey()) {
					return value;
				}
			}
			return null;
		}
	}
	@ConstantAnnotation("收付费_柜面收_个单收款方式")
	enum Sft_RecvPersonalCounter_Recvmode  implements WebConstant {
		POS(0,"POS机" ,"1"), XJJKD(1 ,"现金解款单","E"), ZP(2, "支票","I"), WYHK(3 , "网银/汇款", "D");
		int key;
		String desc;
		String prefix;

		Sft_RecvPersonalCounter_Recvmode(int key, String desc, String prefix) {
			this.key = key;
			this.desc = desc;
			this.prefix = prefix;
		}

		@Override
		public int getKey() {
			return key;
		}

		public String getPrefix() {
			return prefix;
		}
		@Override
		public String getDesc() {
			return desc;
		}

		public static Sft_RecvPersonalCounter_Recvmode getByKey(int key) {
			for (Sft_RecvPersonalCounter_Recvmode sbt : values()) {
				if (sbt.getKey() == key) {
					return sbt;
				}
			}
			return null;
		}
	}
	@ConstantAnnotation("收付费_柜面收_团单资金用途")
	enum SftRecvGroupCounterUseFunds  implements WebConstant {
		KHZH(0, "0", "客户账户"), XDQF(1, "1", "新单签发"), BQSF(2, "2", "保全收费"),
		DQJSSF(3, "3", "定期结算收费"), XQSF(4, "4", "续期收费"), BDQSF(5, "5", "不定期收费");
		int key;
		String keyc;
		String desc;

		SftRecvGroupCounterUseFunds(int key, String keyc, String desc) {
			this.key = key;
			this.keyc = keyc;
			this.desc = desc;
		}

		@Override
		public int getKey() {
			return key;
		}

		public String getKeyc() {
			return keyc;
		}

		@Override
		public String getDesc() {
			return desc;
		}

		public static SftRecvGroupCounterUseFunds getSftRecvGroupCounterUseFundsKeyc(String keyc) {
			for (SftRecvGroupCounterUseFunds value : SftRecvGroupCounterUseFunds.values()) {
				if (keyc.equals(value.getKeyc())) {
					return value;
				}
			}
			return null;
		}

		public static SftRecvGroupCounterUseFunds getSftRecvGroupCounterUseFundsByKey(int key) {
			for (SftRecvGroupCounterUseFunds value : SftRecvGroupCounterUseFunds.values()) {
				if (key == value.getKey()) {
					return value;
				}
			}
			return null;
		}
	}
	@ConstantAnnotation("收付费_柜面收_个单资金用途")
	enum SftRecvPersonalCounterUseFunds  implements WebConstant {
		BDZJ(6, "保单暂记"), ZJTZXZ(7, "追加投资悬账");
		int key;
		String desc;

		SftRecvPersonalCounterUseFunds(int key, String desc) {
			this.key = key;
			this.desc = desc;
		}

		@Override
		public int getKey() {
			return key;
		}

		@Override
		public String getDesc() {
			return desc;
		}

		public static SftRecvPersonalCounterUseFunds getByKey(int key) {
			for (SftRecvPersonalCounterUseFunds sbt : values()) {
				if (sbt.getKey() == key) {
					return sbt;
				}
			}
			return null;
		}
	}
    @ConstantAnnotation("收付费_柜面收_团单个单资金用途")
    enum SftRecvCounterUseFunds  implements WebConstant {
        KHZH(0, "客户账户"), XDQF(1, "新单签发"), BQSF(2, "保全收费"), DQJSSF(3, "定期结算收费"),
        XQSF(4, "续期收费"), BDQSF(5, "不定期收费"), BDZJ(6, "保单暂记"), ZJTZXZ(7, "追加投资悬账");
        int key;
        String desc;

        SftRecvCounterUseFunds(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

        public static SftRecvCounterUseFunds getByKey(int key) {
            for (SftRecvCounterUseFunds sbt : values()) {
                if (sbt.getKey() == key) {
                    return sbt;
                }
            }
            return null;
        }
    }

	@ConstantAnnotation("收付费_柜面收_票据状态")
	enum SftRecvCounterBillStatus  implements WebConstant {

		YDZ(0, "已到账"), YTP(1, "已退票");
		int key;
		String desc;

		SftRecvCounterBillStatus(int key, String desc) {
			this.key = key;
			this.desc = desc;
		}

		@Override
		public int getKey() {
			return key;
		}

		@Override
		public String getDesc() {
			return desc;
		}

		public static SftRecvCounterBillStatus getByKey(int key) {
			for (SftRecvCounterBillStatus sbt : values()) {
				if (sbt.getKey() == key) {
					return sbt;
				}
			}
			return null;
		}
	}
    /**
     * 预提凭证页面，预提状态
     */
    @ConstantAnnotation("收付费_柜面付_补录状态")
    enum PreSubmitStatus implements WebConstant {

        WYT(0, "未预提"), YTFHZ(1, "预提复核中"), YYT(2, "已预提"), CXFHZ(3, "撤销复核中"), YCHEX(4, "已撤销"), YCHONGX(5, "已冲销");
        int key;
        String desc;

        PreSubmitStatus(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }

        public static PreSubmitStatus getByKey(int key) {
            for (PreSubmitStatus sbt : values()) {
                if (sbt.getKey() == key) {
                    return sbt;
                }
            }
            return null;
        }
    }
    
    /**
	 * 预提凭证页面，预提状态
	 */
	@ConstantAnnotation("收付费_柜面收_单据类型")
	enum SftRecvType implements WebConstant {
		GDSK(0, "个单收款"), TDSK(1, "团单收款");
		int key;
		String desc;

		SftRecvType(int key, String desc) {
			this.key = key;
			this.desc = desc;
		}

		public static SftRecvType getByKey(int key) {
			for (SftRecvType sbt : values()) {
				if (sbt.getKey() == key) {
					return sbt;
				}
			}
			return null;
		}

		@Override
		public int getKey() {
			return key;
		}

		@Override
		public String getDesc() {
			return desc;
		}
	}

    @ConstantAnnotation("前端历史交易导入下拉框列表常量")
    enum HisTransImportBankList implements WebConstant {
        DEFAULT(6, "自带模板"),
        CBC(10, "建设银行"),
        BOC(11, "中国银行"),
        CNCB(12, "中信银行"),
        CITI(13, "花旗银行");
        int key;
        String desc;

        HisTransImportBankList(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static HisTransImportBankList getBank(int key) {
            for (HisTransImportBankList value : HisTransImportBankList.values()) {
                if (value.getKey() == key) {
                    return value;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }
    /**
	 * 收付费,柜面收_支付状态
	 */
	@ConstantAnnotation("收付费_柜面收_支付状态")
	enum SftRecvCounterPayStatus implements WebConstant {
		CX(0, "撤销"), QR(1, "确认");
		int key;
		String desc;

		SftRecvCounterPayStatus(int key, String desc) {
			this.key = key;
			this.desc = desc;
		}

		public static SftRecvCounterPayStatus getByKey(int key) {
			for (SftRecvCounterPayStatus sbt : values()) {
				if (sbt.getKey() == key) {
					return sbt;
				}
			}
			return null;
		}

		@Override
		public int getKey() {
			return key;
		}

		@Override
		public String getDesc() {
			return desc;
		}
	}
	
	/**
	 * 收付费,柜面收_支付状态
	 */
	@ConstantAnnotation("收付费_柜面收_匹配状态")
	enum SftRecvCounterMatchStatus implements WebConstant {
		DPP(0, "待匹配"), YCX(1, "已撤销"), YPP(2, "已匹配"), TFZ(3, "退费中"), YTF(4, "已退费");
		int key;
		String desc;

		SftRecvCounterMatchStatus(int key, String desc) {
			this.key = key;
			this.desc = desc;
		}

		public static SftRecvCounterMatchStatus getByKey(int key) {
			for (SftRecvCounterMatchStatus sbt : values()) {
				if (sbt.getKey() == key) {
					return sbt;
				}
			}
			return null;
		}

		@Override
		public int getKey() {
			return key;
		}

		@Override
		public String getDesc() {
			return desc;
		}
	}

    /**
     * 卡种类型
     */
	@ConstantAnnotation("卡种类型")
	enum CardType implements WebConstant{
	    CRADALL(0,"全部"),
        DEBITCARD(1,"借记卡"),
        CREDITCARD(2,"贷记卡"),
        DCRADPASS(3,"借记卡+存折");
	    int key;
	    String desc;

        CardType(int key, String desc) {
            this.key = key;
            this.desc = desc;
        }

        public static CardType getByKey(int key) {
            for (CardType cardType : values()) {
                if (cardType.getKey() == key) {
                    return cardType;
                }
            }
            return null;
        }

        @Override
        public int getKey() {
            return key;
        }

        @Override
        public String getDesc() {
            return desc;
        }
    }
}
