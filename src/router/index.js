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
//余额通
import TodayDetail from '@/components/balance/TodayDetail'
import TodayAll from '@/components/balance/TodayAll'
import TodayFluctuate from '@/components/balance/TodayFluctuate'
import HistoryDetail from '@/components/balance/HistoryDetail'
import HistoryAll from '@/components/balance/HistoryAll'
import HistoryFluctuate from '@/components/balance/HistoryFluctuate'
//调拨通
import MakeBill from '@/components/allot/MakeBill'
import BankAccount from '@/components/allot/BankAccount'

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
                        {path: 'today-detail', name: 'TodayDetail', component: TodayDetail},
                        {path: 'today-all', name: 'TodayAll', component: TodayAll},
                        {path: 'today-fluctuate', name: 'TodayFluctuate', component: TodayFluctuate},
                        {path: 'history-detail', name: 'HistoryDetail', component: HistoryDetail},
                        {path: 'history-all', name: 'HistoryAll', component: HistoryAll},
                        {path: 'history-fluctuate', name: 'HistoryFluctuate', component: HistoryFluctuate}
                    ]
                },
                //基础样式二（中心内容-无按钮）
                {path: '/empty-content', name: 'EmptyContent', component: EmptyContent,
                    children: [
                        //数据管理
                        {path: '/data-manage/basic-data', name: 'BasicData', component: BasicData, meta: {requireAuth: true}},
                        {path: '/data-manage/bank-account', name: 'BankAccount', component: BankAccount, meta: {requireAuth: true}},
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
                    ]
                },
                //基础样式三（tab页）
                {
                    path:'/tab-content',name:'TabContent',component:TabContent,
                    children:[
                        //账户管理
                        {path: '/account/open-ccount-matter', name: 'OpenAccountMatter', component: OpenAccountMatter, meta: {requireAuth: true}},
                    ]
                },
                //调拨通
                {path: '/makeBill', name: 'MakeBill', component: MakeBill}
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
