<style lang='less' type="text/less">
    /*整体样式*/
    #menuContent {
        width: 87px;
        height: 100%;
        overflow-y: scroll;
        overflow-x: hidden;

        .menuList {
            background-color: rgba(255, 255, 255, 0);

            i:nth-child(1) {
                content: "";
                width: 26px;
                height: 26px;
                margin: 3px auto 0;
                display: block;
                background-image: url("../../assets/icon_nav.png");
            }

            /*修改框架样式*/
            .el-submenu__title p {
                color: rgba(255, 255, 255, 0.5);
                height: 20px;
                line-height: 20px;
                margin-top: 4px;
                // margin-left: -6px;
                width: 100%;
                text-align: center;
                font: 14px "Microsoft YaHei";
            }

            .el-submenu__title .mg-four {
                // margin-left: -14px;
            }

            .el-submenu__title:hover {
                background-color: #48AEDF;
            }
            /*修改框架样式结束*/

            /*首页背景图定位*/
            .icon-sjgl {
                background-position: -30px -60px;
            }

            .icon-yhgl {
                background-position: -170px -143px;
            }

            .icon-ssjy {
                background-position: -502px -95px;
            }

            .icon-yet {
                background-position: -30px 0;
            }

            .icon-jyt {
                background-position: -30px -29px;
            }

            .icon-dbt {
                background-position: -170px -180px;
            }

            .icon-zft {
                background-position: -168px -220px;
            }

            .icon-gjt {
                background-position: -103px -116px;
            }

            .icon-zjxb{
                background-position: -92px -254px;
            }

            .icon-skt{
                background-position: -152px -254px;
            }
            .icon-electb{
                background-position: -321px -194px;
            }
            .icon-gyl{
                background-position: -212px -254px;
            }

            .icon-ywfk {
                background-position: -31px -219px;
            }

            .icon-zht {
                background-position: -249px -193px;
            }
            .icon-splgl {
                background-position: -322px -222px;
            }
            .icon-zjys {
                background-position: -32px 0px;
            }

            .icon-sppt {
                background-position: -102px -86px;
            }

            .icon-bb {
                background-position: -30px -55px;
            }

            .icon-oa {
                background-position: -506px -190px;
            }

            .icon-gzrl {
                background-position: -32px -252px;
            }

            .icon-tpgl {
                background-position: -272px -254px;
            }

            .icon-xtsz {
                background-position: -31px -85px;
            }
            /*首页背景图定位结束*/
        }

        /*标签页样式设置*/
        .noBgc {
            .icon-sjgl {
                background-position: 0 -60px;
            }
            .icon-yhgl {
                background-position: -138px -143px;
            }
            .icon-ssjy {
                background-position: -470px -95px;
            }
            .icon-yet {
                background-position: 0px 0;
            }
            .icon-jyt {
                background-position: 0 -29px;
            }
            .icon-dbt {
                background-position: -138px -180px;
            }
            .icon-zft {
                background-position: -137px -220px;
            }
            .icon-gjt {
                background-position: -69px -116px;
            }
            .icon-zjxb{
                background-position: -62px -254px;
            }
            .icon-skt{
                background-position: -122px -254px;
            }
            .icon-electb{
                background-position: -287px -194px;
            }
            .icon-gyl{
                background-position: -182px -254px;
            }
            .icon-ywfk {
                background-position: 0px -219px;
            }
            .icon-zht {
                background-position: -213px -193px;
            }
            .icon-splgl {
                background-position: -287px -222px;
            }
            .icon-zjys {
                background-position: 0px 0px;
            }
            .icon-sppt {
                background-position: -68px -86px;
            }
            .icon-bb {
                background-position: 0 -55px;
            }
            .icon-oa {
                background-position: -506px -160px;
            }
            .icon-gzrl {
                background-position: -1px -252px;
            }
            .icon-tpgl {
                background-position: -242px -254px;
            }
            .icon-xtsz {
                background-position: 0px -85px;
            }

            .el-submenu__title p {
                color: rgba(0, 0, 0, 0.5);
            }

            .el-submenu__title:hover .icon-sjgl {
                background-position: -30px -60px;
            }

            .el-submenu__title:hover .icon-yhgl {
                background-position: -170px -143px;
            }
            .el-submenu__title:hover .icon-ssjy {
                background-position: -502px -95px;
            }

            .el-submenu__title:hover .icon-yet {
                background-position: -30px 0;
            }

            .el-submenu__title:hover .icon-jyt {
                background-position: -30px -29px;
            }

            .el-submenu__title:hover .icon-dbt {
                background-position: -170px -180px;
            }

            .el-submenu__title:hover .icon-zft {
                background-position: -168px -220px;
            }

            .el-submenu__title:hover .icon-gjt {
                background-position: -103px -116px;
            }

            .el-submenu__title:hover .icon-zjxb{
                background-position: -92px -254px;
            }

            .el-submenu__title:hover .icon-skt{
                background-position: -152px -254px;
            }
            .el-submenu__title:hover .icon-electb{
                background-position: -321px -194px;
            }
            .el-submenu__title:hover .icon-gyl{
                background-position: -212px -254px;
            }

            .el-submenu__title:hover .icon-ywfk {
                background-position: -31px -219px;
            }

            .el-submenu__title:hover .icon-zht {
                background-position: -249px -193px;
            }

            .el-submenu__title:hover .icon-splgl {
                background-position: -322px -222px;
            }

            .el-submenu__title:hover .icon-zjys {
                background-position: -32px 0px;
            }

            .el-submenu__title:hover .icon-sppt {
                background-position: -102px -86px;
            }

            .el-submenu__title:hover .icon-bb {
                background-position: -30px -55px;
            }

            .el-submenu__title:hover .icon-oa {
                background-position: -506px -190px;
            }

            .el-submenu__title:hover .icon-gzrl{
                background-position: -32px -252px;
            }

            .el-submenu__title:hover .icon-tpgl{
                background-position: -272px -254px;
            }

            .el-submenu__title:hover .icon-xtsz {
                background-position: -31px -85px;
            }

            .el-submenu__title:hover p {
                color: #fff;
            }
        }

        /*修改框架样式*/
        .el-menu-vertical-demo:not(.el-menu--collapse) {
            width: 100%;
            min-height: 400px;
        }

        .el-menu-vertical-demo {
            width: 100%;
        }

        .el-submenu__title {
            height: 70px;
            padding-left: 0px !important;
            padding-right: 0px;
            padding-top: 12px;

        }
    }

    /*框架样式修改*/
    .el-menu--vertical .el-menu {
        margin: 0;
        background-color: #48AEDF;
        min-width: 150px;
        width: 150px;
    }

    .el-menu--vertical .el-menu .el-menu-item-group__title {
        padding: 0;
    }

    .el-menu--vertical .el-menu ul li {
        height: 30px;
        line-height: 30px;
        color: rgba(255, 255, 255, 0.8);
    }

    .el-menu--vertical .el-menu ul li:hover {
        background-color: rgba(23, 144, 201, 1);
        color: #fff;
    }

    /*框架样式修改结束*/
</style>

<template>
    <div id="menuContent">
        <el-menu default-active="1-12-1" :class="['el-menu-vertical-demo', 'menuList', {'noBgc':showBgc}]"
                 :collapse="isCollapse" :router="true">
            <el-submenu index="1" v-if="menuList.DMMGT">
                <template slot="title" height="200px">
                    <i class="icon-sjgl"></i>
                    <p class="mg-four">数据管理</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/data-manage/basic-data" v-if="menuList.BasicDataMgt">基础数据维护</el-menu-item>
                    <el-menu-item index="/data-manage/settle-account" v-if="menuList.SettAccMgt">结算账户设置</el-menu-item>
                    <el-menu-item index="/data-manage/merch-set" v-if="menuList.MerchMgt">商户号设置</el-menu-item>
                    <el-menu-item index="/data-manage/channel-set" v-if="menuList.ChannelMgt">渠道设置</el-menu-item>
                    <el-menu-item index="/data-manage/router-set" v-if="menuList.RouteMgt">路由设置</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="2" v-if="menuList.UMMGT">
                <template slot="title" height="200px">
                    <i class="icon-yhgl"></i>
                    <p class="mg-four">用户管理</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/user-manage/user-maintain" v-if="menuList.UsrMgt">用户维护</el-menu-item>
                    <el-menu-item index="/user-manage/user-group" v-if="menuList.UsrGpMgt">用户组设置</el-menu-item>
                    <el-menu-item index="/user-manage/user-menu" v-if="menuList.UsrMenuMgt">用户菜单设置</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="3" v-if="menuList.RTPRMGT">
                <template slot="title" height="200px">
                    <i class="icon-ssjy"></i>
                    <p class="mg-four">实时交易</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/realtime-trade/personal-insurance" v-if="menuList.RTGXPayMgt">个险核心实时代付
                    </el-menu-item>
                    <el-menu-item index="/realtime-trade/group-insurance" v-if="menuList.RTTXPayMgt">团险核心实时代付
                    </el-menu-item>
                    <el-menu-item index="/realtime-trade/QRcode" v-if="menuList.RTYDQrCodeMgt">移动展业二维码</el-menu-item>
                    <el-menu-item index="/realtime-trade/move-withhold" v-if="menuList.RTYDRecvMgt">移动展业实时代扣
                    </el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="4" v-if="menuList.ACCMGT">
                <template slot="title" height="200px">
                    <i class="icon-zht"></i>
                    <p>账户通</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/account/open-account-matter" v-if="menuList.AccOpenIntAppl">开户事项申请
                    </el-menu-item>
                    <el-menu-item index="/account/open-account-message" v-if="menuList.AccOpenComAppl">开户信息补录申请
                    </el-menu-item>
                    <el-menu-item index="/account/open-account-confirm" v-if="menuList.AccOpenConfirm">开户确认
                    </el-menu-item>
                    <el-menu-item index="/account/account-alteration" v-if="menuList.AccChgAppl">账户变更申请</el-menu-item>
                    <el-menu-item index="/account/account-message" v-if="menuList.AccMtc">账户信息维护</el-menu-item>
                    <el-menu-item index="/account/account-unfreeze" v-if="menuList.AccFreezeAppl">账户解冻申请</el-menu-item>
                    <el-menu-item index="/account/account-freeze" v-if="menuList.AccDefreezeAppl">账户冻结申请</el-menu-item>
                    <el-menu-item index="/account/close-account-matter" v-if="menuList.AccCloseIntAppl">销户事项申请
                    </el-menu-item>
                    <el-menu-item index="/account/close-account-message" v-if="menuList.AccCloseComAppl">销户信息补录申请
                    </el-menu-item>
                    <el-menu-item index="/account/close-account-confirm" v-if="menuList.AccCloseConfirm">销户确认
                    </el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="5" v-if="menuList.WFMGT">
                <template slot="title" height="200px">
                    <i class="icon-splgl"></i>
                    <p>审批流管理</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/workflow/workflow-definition" v-if="menuList.WFDefine">审批流程定义</el-menu-item>
                    <el-menu-item index="/workflow/workflow-trace" v-if="menuList.WFTrace">业务跟踪管理</el-menu-item>
                    <el-menu-item index="/workflow/workflow-configure" v-if="menuList.WFLinK">业务配置审批流程</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="6" v-if="menuList.WFPLAT">
                <template slot="title" height="200px">
                    <i class="icon-sppt"></i>
                    <p class="mg-four">审批平台</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/examine-approve/my-examine-approve" v-if="menuList.MyWFPLAT">我的审批平台
                    </el-menu-item>
                    <el-menu-item index="/examine-approve/permission-transfer" v-if="menuList.ApproveTransfer">权限转移
                    </el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="7" v-if="menuList.YET">
                <template slot="title" height="200px">
                    <i class="icon-yet"></i>
                    <p>余额通</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/balance/today-detail" v-if="menuList.CurBal">当日余额明细</el-menu-item>
                    <el-menu-item index="/balance/today-all" v-if="menuList.CulBalSummary">当日余额汇总</el-menu-item>
                    <el-menu-item index="/balance/today-fluctuate" v-if="menuList.CulBalWave">当日余额波动</el-menu-item>
                    <el-menu-item index="/balance/history-detail" v-if="menuList.HisBal">历史余额明细</el-menu-item>
                    <el-menu-item index="/balance/history-all" v-if="menuList.HisBalSummary">历史余额汇总</el-menu-item>
                    <el-menu-item index="/balance/history-fluctuate" v-if="menuList.HisBalWave">历史余额波动</el-menu-item>
                    <el-menu-item index="/balance/history-data" v-if="menuList.BalImport">余额数据导入</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="8" v-if="menuList.JYT">
                <template slot="title" height="200px">
                    <i class="icon-jyt"></i>
                    <p>交易通</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/trade-links/today-detail" v-if="menuList.CurTrans">当日交易明细</el-menu-item>
                    <el-menu-item index="/trade-links/today-all" v-if="menuList.CurTransSummary">当日交易汇总</el-menu-item>
                    <el-menu-item index="/trade-links/history-detail" v-if="menuList.HisTrans">历史交易明细</el-menu-item>
                    <el-menu-item index="/trade-links/history-all" v-if="menuList.HisTransSummary">历史交易汇总</el-menu-item>
                    <el-menu-item index="/trade-links/history-fluctuate" v-if="menuList.HisTransWave">历史交易波动</el-menu-item>
                    <el-menu-item index="/trade-links/history-data" v-if="menuList.TransImport">交易数据导入</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="9" v-if="menuList.DBT">
                <template slot="title" height="200px">
                    <i class="icon-dbt"></i>
                    <p>调拨通</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/allot/make-bill" v-if="menuList.DbtMkBill">内部调拨-制单</el-menu-item>
                    <el-menu-item index="/allot/payment" v-if="menuList.DbtPayBill">内部调拨-支付</el-menu-item>
                    <el-menu-item index="/allot/look-over" v-if="menuList.DbtViewBill">内部调拨-查看</el-menu-item>
                    <el-menu-item index="/allot/lot-make-bill" v-if="menuList.DbtBatchBill">批量调拨-制单</el-menu-item>
                    <el-menu-item index="/allot/lot-payment" v-if="menuList.DbtBatchPay">批量调拨-支付</el-menu-item>
                    <el-menu-item index="/allot/lot-look-over" v-if="menuList.DbtBatchView">批量调拨-查看</el-menu-item>
                    <el-menu-item index="/allot/allot-deal-check?bizType=8" v-if="menuList.DbtCheck">交易核对</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="10" v-if="menuList.WDATESET || menuList.WCALSET">
                <template slot="title" height="200px">
                    <i class="icon-gzrl"></i>
                    <p>工作日历</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/calendar/set-workday" v-if="menuList.HolidaySet">工作日设置</el-menu-item>
                    <el-menu-item index="/calendar/work-section" v-if="menuList.SectionSet">工作区间设置</el-menu-item>
                    <el-menu-item index="/calendar/the-offer" v-if="menuList.OfferSet">报盘日设置</el-menu-item>
                    <el-menu-item index="/calendar/closing-day" v-if="menuList.CheckoutSet">结账日设置</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="11" v-if="menuList.ZFT">
                <template slot="title" height="200px">
                    <i class="icon-zft"></i>
                    <p class="mg-four">支付通</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/payment/pay-make-bill" v-if="menuList.ZFTMkBill">资金支付-制单</el-menu-item>
                    <el-menu-item index="/payment/pay-payment" v-if="menuList.ZFTPayBill">资金支付-支付</el-menu-item>
                    <el-menu-item index="/payment/pay-look-over" v-if="menuList.ZFTViewBill">资金支付-查看</el-menu-item>
                    <el-menu-item index="/payment/batch-make-bill" v-if="menuList.ZFTBatchBill">批量支付-制单</el-menu-item>
                    <el-menu-item index="/payment/batch-payment" v-if="menuList.ZFTBatchPay">批量支付-支付</el-menu-item>
                    <el-menu-item index="/payment/batch-look-over" v-if="menuList.ZFTBatchView">批量支付-查看</el-menu-item>
                    <el-menu-item index="/payment/payee-message" v-if="menuList.ZFTSuplierAcc">收款方信息管理</el-menu-item>
                    <el-menu-item index="/payment/deal-check?bizType=9" v-if="menuList.ZFTCheck">交易核对</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="12" v-if="menuList.GJT">
                <template slot="title" height="200px">
                    <i class="icon-gjt"></i>
                    <p>归集通</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/collection/collection-set" v-if="menuList.GJSetting">自动归集设置</el-menu-item>
                    <el-menu-item index="/collection/collection-manage" v-if="menuList.GJMgr">自动归集管理</el-menu-item>
                    <el-menu-item index="/collection/collection-look" v-if="menuList.GJView">自动归集查看</el-menu-item>
                    <el-menu-item index="/collection/collection-statement" v-if="menuList.GJReport">自动归集报表</el-menu-item>
                    <el-menu-item index="/collection/not-directly-set" v-if="menuList.GJBatchBill">非直连归集-制单</el-menu-item>
                    <el-menu-item index="/collection/no-directly-look" v-if="menuList.GJBatchView">非直连归集-查看</el-menu-item>
                    <el-menu-item index="/payment/deal-check?bizType=12" v-if="menuList.GJCheck">交易核对</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="13"  v-if="menuList.ZJXB">
                <template slot="title" height="200px">
                    <i class="icon-zjxb"></i>
                    <p>资金下拨</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/allocation/fundpool-acc-set" v-if="menuList.ZJXBApSetting">资金池账户设置</el-menu-item>
                    <el-menu-item index="/allocation/allocation-set" v-if="menuList.ZJXBSetting">自动下拨设置</el-menu-item>
                    <el-menu-item index="/allocation/allocation-manage" v-if="menuList.ZJXBMgr">自动下拨管理</el-menu-item>
                    <el-menu-item index="/allocation/allocation-view" v-if="menuList.ZJXBView">自动下拨查看</el-menu-item>
                    <el-menu-item index="/allocation/allocation-chart" v-if="menuList.ZJXBReport">自动下拨报表</el-menu-item>
                    <el-menu-item index="/allot/allot-deal-check?bizType=13" v-if="menuList.ZJXBChecked">交易核对</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="14"  v-if="menuList.SKT">
                <template slot="title" height="200px">
                    <i class="icon-skt"></i>
                    <p>收款通</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/receivables/receive-make-bill" v-if="menuList.SKTMkBill">资金收款-制单</el-menu-item>
                    <el-menu-item index="/receivables/receive-look-over" v-if="menuList.SKTViewBill">资金收款-查看</el-menu-item>
                    <el-menu-item index="/allot/allot-deal-check?bizType=15" v-if="menuList.SKTCheck">交易核对</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="15" v-if="menuList.ELECTB">
                <template slot="title" height="200px">
                    <i class="icon-electb"></i>
                    <p>电子回单</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/electronicReceipt/electronic-receipt" v-if="menuList.ElectronicBillMgr">电子回单</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="16" v-if="menuList.GYLBFJ">
                <template slot="title" height="200px">
                    <i class="icon-gyl"></i>
                    <p>广银联</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/wide-unionpay/strategy-set" v-if="menuList.GYLBFJSetting">广银联备付金设置</el-menu-item>
                    <el-menu-item index="/wide-unionpay/unionpay-manage" v-if="menuList.GYLBFJMgr">广银联备付金管理</el-menu-item>
                    <el-menu-item index="/wide-unionpay/task-look" v-if="menuList.GYLBFJView">广银联备付金查看</el-menu-item>
                    <el-menu-item index="/payment/deal-check?bizType=14" v-if="menuList.GYLBFJCheck">广银联备付金核对</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="17" v-if="menuList.OADATAMGT">
                <template slot="title" height="200px">
                    <i class="icon-oa"></i>
                    <p class="mg-four">OA数据</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/OA-data/data-manage" v-if="menuList.DataMgt">接口数据管理</el-menu-item>
                    <el-menu-item index="/OA-data/head-office-pay" v-if="menuList.OAHeadPay">总公司付款</el-menu-item>
                    <el-menu-item index="/OA-data/filiale-payment" v-if="menuList.OABranchPay">分公司付款</el-menu-item>
                    <el-menu-item index="/OA-data/suspicious-data" v-if="menuList.OACheckDoubtful">可疑数据管理</el-menu-item>
                    <el-menu-item index="/payment/deal-check?bizType=20" v-if="menuList.OACheck">交易核对</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="18" v-if="menuList.TPGL">
                <template slot="title" height="200px">
                    <i class="icon-tpgl"></i>
                    <p class="mg-four">退票管理</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/refund-ticket/refund-dispose" v-if="menuList.ActiveRefund">主动退票</el-menu-item>
                    <el-menu-item index="/refund-ticket/suspicious-refund" v-if="menuList.DoubtfulRefund">可疑退票</el-menu-item>
                    <el-menu-item index="/refund-ticket/refund-inquiry" v-if="menuList.QRefund">退票查询</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="19" v-if="menuList.SYSSET">
                <template slot="title" height="200px">
                    <i class="icon-xtsz"></i>
                    <p class="mg-four">系统设置</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/system-set/aisle-set" v-if="menuList.BankkeySet">通道设置</el-menu-item>
                    <el-menu-item index="/system-set/bankkey-set" v-if="menuList.ChannelSet">bankkey设置</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="20">
                <template slot="title" height="200px">
                    <i class="icon-plfk"></i>
                    <p class="mg-four">批量付款</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/batch-payment/prevent-repet">收付数据防重预警</el-menu-item>
                    <el-menu-item index="/batch-payment/pay-check-batch">批量付款-核对组批</el-menu-item>
                    <el-menu-item index="/batch-payment/pay-disk-sending">批量付款-盘片发送</el-menu-item>
                    <el-menu-item index="/batch-payment/settle-accounts">结算对账</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
        </el-menu>
    </div>
</template>

<script>
    export default {
        name: "Left",
        created: function () {
            this.setMenu();
        },
        data: function () {
            return {
                isCollapse: true,
                menuList: {
                    DMMGT: false, //数据管理
                    BasicDataMgt: false, //基础数据维护
                    SettAccMgt: false, //结算账户设置
                    MerchMgt: false, //商户号设置
                    ChannelMgt: false, //渠道设置
                    RouteMgt: false, //路由设置

                    UMMGT: false, //用户管理
                    UsrMgt: false, //用户维护
                    UsrGpMgt: false, //用户组设置
                    UsrMenuMgt: false, //用户菜单设置

                    RTPRMGT: false, //实时交易
                    RTGXPayMgt: false, //个险核心实时代付
                    RTTXPayMgt: false, //团险核心实时代付
                    RTYDQrCodeMgt: false, //移动展业二维码
                    RTYDRecvMgt: false, //移动展业实时代扣

                    ACCMGT: false, //账户通
                    AccOpenIntAppl: false, //开户事项申请
                    AccOpenComAppl: false, //开户信息补录申请
                    AccOpenConfirm: false, //开户确认
                    AccChgAppl: false, //账户变更申请
                    AccMtc: false, //账户信息维护
                    AccFreezeAppl: false, //账户解冻申请
                    AccDefreezeAppl: false, //账户冻结申请
                    AccCloseIntAppl: false, //销户事项申请
                    AccCloseComAppl: false, //销户信息补录申请
                    AccCloseConfirm: false, //销户确认

                    WFMGT: false, //审批流管理
                    WFDefine: false, //审批流程定义
                    WFTrace: false, //业务流程追踪
                    WFLinK: false, //业务配置审批流程

                    WFPLAT: false, //审批平台
                    MyWFPLAT: false, //我的审批平台
                    ApproveTransfer: false, //权限转移

                    YET: false, //余额通
                    CurBal: false, //当日余额明细
                    CulBalSummary: false, //当日余额汇总
                    CulBalWave: false, //当日余额波动
                    HisBal: false, //历史余额明细
                    HisBalSummary: false, //历史余额汇总
                    HisBalWave: false, //历史余额波动
                    BalImport: false, //历史余额波动

                    JYT: false, //交易通
                    CurTrans: false, //当日交易明细
                    CurTransSummary: false, //当日交易汇总
                    HisTrans: false, //历史交易明细
                    HisTransSummary: false, //历史交易汇总
                    HisTransWave: false, //历史交易波动
                    TransImport: false, //历史交易波动

                    DBT: false, //调拨通
                    DbtMkBill: false, //内部调拨-制单
                    DbtPayBill: false, //内部调拨-支付
                    DbtViewBill: false, //内部调拨-查看
                    DbtBatchBill: false, //批量调拨-制单
                    DbtBatchView: false, //批量调拨-查看
                    DbtBatchPay: false, //批量调拨-支付
                    DbtCheck: false, //内部调拨-交易核对

                    ZFT: false, //支付通
                    ZFTMkBill: false, //资金支付-制单
                    ZFTPayBill: false, //资金支付-支付
                    ZFTViewBill: false, //资金支付-查看
                    ZFTBatchBill: false, //资金支付-批量制单
                    ZFTBatchPay: false, //资金支付-批量支付
                    ZFTBatchView: false, //资金支付-批量查看
                    ZFTCheck: false, //资金支付-交易核对
                    ZFTSuplierAcc: false, //收款方账户维护

                    GJT: false, //归集通
                    GJSetting: false, //自动归集设置
                    GJMgr: false, //自动归集管理
                    GJView: false, //自动归集查看
                    GJReport: false, //自动归集报表
                    GJCheck: false, //自动归集核对
                    GJBatchBill: false, //自动归集核对
                    GJBatchView: false, //自动归集核对

                    WDATESET: false, //工作日历
                    WCALSET: false, //工作日历(管理员模块)
                    HolidaySet: false, //自动归集设置
                    SectionSet: false, //工作区间设置
                    OfferSet: false, //报盘日设置
                    CheckoutSet: false, //结账日设置

                    ZJXB: false, //资金下拨
                    ZJXBApSetting: false, //资金下拨账户设置
                    ZJXBSetting: false, //资金下拨设置
                    ZJXBMgr: false, //资金下拨管理
                    ZJXBView: false, //资金下拨查看
                    ZJXBReport: false, //资金下拨报表
                    ZJXBChecked: false, //资金下拨及交易核对

                    SKT: false, //收款通
                    SKTMkBill: false, //收款通账户设置
                    SKTViewBill: false, //收款通设置
                    SKTCheck: false, //收款通交易核对

                    GYLBFJ: false, //广银联备付金
                    GYLBFJSetting: false, //广银联备付金设置
                    GYLBFJMgr: false, //广银联备付金管理
                    GYLBFJView: false, //广银联备付金查看
                    GYLBFJCheck: false, //广银联备付金核对

                    OADATAMGT: false, //OA数据管理
                    DataMgt: false, //接口数据管理
                    OAHeadPay: false, //总公司付款
                    OABranchPay: false, //分公司付款
                    OACheckDoubtful: false, //分公司付款
                    OACheck: false, //交易核对

                    ELECTB: false, //电子回单
                    ElectronicBillMgr: false, //电子回单

                    TPGL: false, //退票管理
                    ActiveRefund: false, //主动退票
                    DoubtfulRefund: false, //可疑退票
                    QRefund: false, //退票查询

                    SYSSET: false, //系统设置
                    BankkeySet: false, //Bankkey设置
                    ChannelSet: false, //通道设置
                }
            }
        },
        methods: {
            //根据权限设置菜单栏
            setMenu: function(){
                var user = JSON.parse(window.sessionStorage.getItem("user"));
                var menuList = this.menuList;
                for(var k in menuList){
                    menuList[k] = false;
                }

                if (user.menu_info) {
                    var menuList = user.menu_info;
                    for (var i = 0; i < menuList.length; i++) {
                        var items = menuList[i].items;
                        for (var j = 0; j < items.length; j++) {
                            var item = items[j];
                            for (var k in this.menuList) {
                                if (item.code == k) {
                                    this.menuList[k] = true;
                                    break;
                                }
                            }
                        }
                        for (var key in this.menuList) {
                            if (menuList[i].code == key) {
                                this.menuList[key] = true;
                                break;
                            }
                        }
                    }
                }
            }
        },
        computed: {
            showBgc: function () {
                var currentPath = this.$route.name;
                if(currentPath == "Home" && this.$route.params.refreshUser){
                    this.setMenu();
                }
                if (!currentPath || currentPath == "Home") {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }
</script>
