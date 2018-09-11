import Vue from 'vue'
import Router from 'vue-router'
//主页面
import Login from '@/components/Login'
import Main from '@/components/Main'
//首页
import Home from '@/components/Home'
//样式结构页面
import WhiteContent from '@/components/publicModule/WhiteContent'
import EmptyContent from '@/components/publicModule/EmptyContent'
import TabContent from '@/components/publicModule/TabContent'
import ImportTabContent from '@/components/publicModule/ImportTabContent'
import DynamicTab from '@/components/publicModule/DynamicTab'
//数据管理
import BasicData from '@/components/dataManage/BasicData'
import SettleAccount from '@/components/dataManage/SettleAccount'
import ChannelSet from '@/components/dataManage/ChannelSet'
import RouterSet from '@/components/dataManage/RouterSet'
import MerchSet from '@/components/dataManage/MerchSet'
//用户管理
import UserMaintain from '@/components/userManage/UserMaintain'
import UserGroup from '@/components/userManage/UserGroup'
import UserMenu from '@/components/userManage/UserMenu'
//实时交易
import PersonalInsurance from '@/components/realtimeTrade/PersonalInsurance'
import GroupInsurance from '@/components/realtimeTrade/GroupInsurance'
import QRcode from '@/components/realtimeTrade/QRcode'
import MoveWithhold from '@/components/realtimeTrade/MoveWithhold'
//账户通
import OpenAccountMatter from '@/components/account/OpenAccountMatter'
import OpenAccountMessage from '@/components/account/OpenAccountMessage'
import OpenAccountConfirm from '@/components/account/OpenAccountConfirm'
import AccountAlteration from '@/components/account/AccountAlteration'
import AccountMessage from '@/components/account/AccountMessage'
import AccountUnfreeze from '@/components/account/AccountUnfreeze'
import AccountFreeze from '@/components/account/AccountFreeze'
import CloseAccountMatter from '@/components/account/CloseAccountMatter'
import CloseAccountMessage from '@/components/account/CloseAccountMessage'
import CloseAccountConfirm from '@/components/account/CloseAccountConfirm'
//余额通
import TodayDetail from '@/components/balance/TodayDetail'
import TodayAll from '@/components/balance/TodayAll'
import TodayFluctuate from '@/components/balance/TodayFluctuate'
import HistoryDetail from '@/components/balance/HistoryDetail'
import HistoryAll from '@/components/balance/HistoryAll'
import HistoryFluctuate from '@/components/balance/HistoryFluctuate'
import HistoryData from '@/components/balance/HistoryData'
//交易通
import TodayDealDetail from '@/components/tradeLinks/TodayDealDetail'
import TodayDealAll from '@/components/tradeLinks/TodayDealAll'
import HistoryDealDetail from '@/components/tradeLinks/HistoryDealDetail'
import HistoryDealAll from '@/components/tradeLinks/HistoryDealAll'
import HistoryDealFluctuate from '@/components/tradeLinks/HistoryDealFluctuate'
//调拨通
import MakeBill from '@/components/allot/MakeBill'
import Payment from '@/components/allot/Payment'
import LookOver from '@/components/allot/LookOver'
import MoreBills from '@/components/allot/MoreBills'
import AllotDealCheck from '@/components/allot/AllotDealCheck'
//审批流管理
import WorkflowDefinition from '@/components/workflow/WorkflowDefinition'
import WorkflowTrace from '@/components/workflow/WorkflowTrace'
import WorkflowConfigure from '@/components/workflow/WorkflowConfigure'
//审批平台
import MyExamineApprove from '@/components/examineApprove/MyExamineApprove'
//日历
import SetWorkday from '@/components/calendar/SetWorkday'
import WorkSection from '@/components/calendar/WorkSection'
import TheOffer from '@/components/calendar/TheOffer'
import ClosingDay from '@/components/calendar/ClosingDay'
//支付通
import PayMakeBill from '@/components/payment/PayMakeBill'
import PayPayment from '@/components/payment/PayPayment'
import PayLookOver from '@/components/payment/PayLookOver'
import BatchMakeBill from '@/components/payment/BatchMakeBill'
import BatchPayment from '@/components/payment/BatchPayment'
import BatchLookOver from '@/components/payment/BatchLookOver'
import PayeeMessage from '@/components/payment/PayeeMessage'
import DealCheck from '@/components/payment/DealCheck'
//归集通
import CollectionSet from '@/components/collection/CollectionSet'
import CollectionManage from '@/components/collection/CollectionManage'
import CollectionLook from '@/components/collection/CollectionLook'
import CollectionStatement from '@/components/collection/CollectionStatement'
import ColleDealCheck from '@/components/collection/ColleDealCheck'


Vue.use(Router)

const router = new Router({
    routes: [
        //登录页面
        {path: '/login', name: 'Login', component: Login},
        // {path: '/', redirect: {name:'Home'}},
        {path: '/', component: Main, meta: {requireAuth: true},
            children:[
                //首页
                {path: '/home', name: 'Home', component: Home, meta: {requireAuth: true}},
                //基础样式一（中心内容）
                {path: '/whiteContent', name: 'WhiteContent', component: WhiteContent,
                    children: [
                        //余额通
                        {path: '/balance/today-detail', name: 'TodayDetail', component: TodayDetail},
                        {path: '/balance/today-all', name: 'TodayAll', component: TodayAll},
                        {path: '/balance/today-fluctuate', name: 'TodayFluctuate', component: TodayFluctuate},
                        {path: '/balance/history-detail', name: 'HistoryDetail', component: HistoryDetail},
                        {path: '/balance/history-all', name: 'HistoryAll', component: HistoryAll},
                        {path: '/balance/history-fluctuate', name: 'HistoryFluctuate', component: HistoryFluctuate},
                        //交易通
                        {path: '/trade-links/today-detail', name: 'ToDayDealDetail', component: TodayDealDetail},
                        {path: '/trade-links/today-all', name: 'ToDayDealAll', component: TodayDealAll},
                        {path: '/trade-links/history-detail', name: 'HistoryDealDetail', component: HistoryDealDetail},
                        {path: '/trade-links/history-all', name: 'HistoryDealAll', component: HistoryDealAll},
                        {path: '/trade-links/history-fluctuate', name: 'HistoryDealFluctuate', component: HistoryDealFluctuate},
                    ]
                },
                //基础样式二（中心内容-无按钮）
                {path: '/empty-content', name: 'EmptyContent', component: EmptyContent,
                    children: [
                        //数据管理
                        {path: '/data-manage/basic-data', name: 'BasicData', component: BasicData, meta: {requireAuth: true}},
                        {path: '/data-manage/settle-account', name: 'SettleAccount', component: SettleAccount, meta: {requireAuth: true}},
                        {path: '/data-manage/channel-set', name: 'ChannelSet', component: ChannelSet, meta: {requireAuth: true}},
                        {path: '/data-manage/router-set', name: 'RouterSet', component: RouterSet, meta: {requireAuth: true}},
                        {path: '/data-manage/merch-set', name: 'MerchSet', component: MerchSet, meta: {requireAuth: true}},
                        //用户管理
                        {path: '/user-manage/user-maintain', name: 'UserMaintain', component: UserMaintain, meta: {requireAuth: true}},
                        {path: '/user-manage/user-group', name: 'UserGroup', component: UserGroup, meta: {requireAuth: true}},
                        {path: '/user-manage/user-menu', name: 'UserMenu', component: UserMenu, meta: {requireAuth: true}},
                        //实时交易
                        {path: '/realtime-trade/personal-insurance', name: 'PersonalInsurance', component: PersonalInsurance, meta: {requireAuth: true}},
                        {path: '/realtime-trade/group-insurance', name: 'GroupInsurance', component: GroupInsurance, meta: {requireAuth: true}},
                        {path: '/realtime-trade/QRcode', name: 'QRcode', component: QRcode, meta: {requireAuth: true}},
                        {path: '/realtime-trade/move-withhold', name: 'MoveWithhold', component: MoveWithhold, meta: {requireAuth: true}},
                        //账户通
                        {path: '/account/account-message', name: 'AccountMessage', component: AccountMessage, meta: {requireAuth: true}},
                        {path: '/account/open-account-confirm', name: 'OpenAccountConfirm', component: OpenAccountConfirm, meta: {requireAuth: true}},
                        {path: '/account/close-account-confirm', name: 'CloseAccountConfirm', component: CloseAccountConfirm, meta: {requireAuth: true}},
                        //审批流管理
                        {path: '/workflow/workflow-definition', name: 'WorkflowDefinition', component: WorkflowDefinition, meta: {requireAuth: true}},
                        {path: '/workflow/workflow-trace', name: 'WorkflowTrace', component: WorkflowTrace, meta: {requireAuth: true}},
                        {path: '/workflow/workflow-configure', name: 'WorkflowConfigure', component: WorkflowConfigure, meta: {requireAuth: true}},
                        //调拨通
                        {path: '/allot/payment', name: 'Payment', component: Payment, meta: {requireAuth: true}},
                        {path: '/allot/look-over', name: 'LookOver', component: LookOver, meta: {requireAuth: true}},
                        {path: '/allot/more-bills', name: 'MoreBills', component: MoreBills, meta: {requireAuth: true}},
                        //支付通
                        {path: '/payment/pay-payment', name: 'PayPayment', component: PayPayment, meta: {requireAuth: true}},
                        {path: '/payment/pay-look-over', name: 'PayLookOver', component: PayLookOver, meta: {requireAuth: true}},
                        {path: '/payment/batch-payment', name: 'BatchPayment', component: BatchPayment, meta: {requireAuth: true}},
                        {path: '/payment/batch-look-over', name: 'BatchLookOver', component: BatchLookOver, meta: {requireAuth: true}},
                        {path: '/payment/payee-message', name: 'PayeeMessage', component: PayeeMessage, meta: {requireAuth: true}},
                        //日历
                        {path: '/calendar/set-workday', name: 'SetWorkday', component: SetWorkday, meta: {requireAuth: true}},
                        {path: '/calendar/work-section', name: 'WorkSection', component: WorkSection, meta: {requireAuth: true}},
                        {path: '/calendar/the-offer', name: 'TheOffer', component: TheOffer, meta: {requireAuth: true}},
                        {path: '/calendar/closing-day', name: 'ClosingDay', component: ClosingDay, meta: {requireAuth: true}},
                        //归集通
                        {path: '/collection/collection-set', name: 'CollectionSet', component: CollectionSet, meta: {requireAuth: true}},
                        {path: '/collection/collection-manage', name: 'CollectionManage', component: CollectionManage, meta: {requireAuth: true}},
                        {path: '/collection/collection-look', name: 'CollectionLook', component: CollectionLook, meta: {requireAuth: true}},
                        {path: '/collection/collection-statement', name: 'CollectionStatement', component: CollectionStatement, meta: {requireAuth: true}},
                        {path: '/collection/colle-deal-check', name: 'ColleDealCheck', component: ColleDealCheck, meta: {requireAuth: true}},
                    ]
                },
                //基础样式三（tab页）
                {
                    path:'/tab-content',name:'TabContent',component:TabContent,
                    children:[
                        //账户通
                        {path: '/account/open-account-matter', name: 'OpenAccountMatter', component: OpenAccountMatter, meta: {requireAuth: true}},
                        {path: '/account/open-account-message', name: 'OpenAccountMessage', component: OpenAccountMessage, meta: {requireAuth: true}},
                        {path: '/account/account-alteration', name: 'AccountAlteration', component: AccountAlteration, meta: {requireAuth: true}},
                        {path: '/account/account-unfreeze', name: 'AccountUnfreeze', component: AccountUnfreeze, meta: {requireAuth: true}},
                        {path: '/account/account-freeze', name: 'AccountFreeze', component: AccountFreeze, meta: {requireAuth: true}},
                        {path: '/account/close-account-matter', name: 'CloseAccountMatter', component: CloseAccountMatter, meta: {requireAuth: true}},
                        {path: '/account/close-account-message', name: 'CloseAccountMessage', component: CloseAccountMessage, meta: {requireAuth: true}},
                        //审批平台
                        {path: '/examine-approve/my-examine-approve', name: 'MyExamineApprove', component: MyExamineApprove, meta: {requireAuth: true}},
                    ]
                },
                //基础样式四（导入的tab页）
                {
                    path:'/import-tab-content',name:'ImportTabContent',component:ImportTabContent,
                    children:[
                        //余额通
                        {path: '/balance/history-data', name: 'HistoryData', component: HistoryData, meta: {requireAuth: true}}
                    ]
                },
                //基础样式五（可切换tab文字的tab页）
                {
                    path:'/dynamic-tab',name:'DynamicTab',component:DynamicTab,
                    children:[
                        {path: '/payment/deal-check', name: 'DealCheck', component: DealCheck, meta: {requireAuth: true}},
                        {path: '/allot/allot-deal-check', name: 'AllotDealCheck', component: AllotDealCheck, meta: {requireAuth: true}}
                    ]
                },
                //调拨通
                {path: '/allot/make-bill', name: 'MakeBill', component: MakeBill,meta: {requireAuth: true}},
                //支付通
                {path: '/payment/pay-make-bill', name: 'PayMakeBill', component: PayMakeBill, meta: {requireAuth: true}},
                {path: '/payment/batch-make-bill', name: 'BatchMakeBill', component: BatchMakeBill, meta: {requireAuth: true}},
            ]
        }
    ]
});

import store from "../js/store"

//全局路由守卫 根据token判断是否进入路由
router.beforeEach((to,from,next) => {
    if(to.meta.requireAuth){
        if(store.state.token){
            next();
        }else{
            next({path:"/login"})
        }
    }else{
        next();
    }
})

//刷新页面时重新保存token和user信息
if(sessionStorage.getItem("token")){
    var data = {};
    data.token = sessionStorage.getItem("token");
    data.user = JSON.parse(sessionStorage.getItem("user"));
    store.commit("set_token", data);
}

export default router;
