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
//数据管理
import BasicData from '@/components/dataManage/BasicData'
import BankAccount from '@/components/dataManage/BankAccount'
//余额通
import TodayDetail from '@/components/balance/TodayDetail'
import TodayAll from '@/components/balance/TodayAll'
import TodayFluctuate from '@/components/balance/TodayFluctuate'
import HistoryDetail from '@/components/balance/HistoryDetail'
import HistoryAll from '@/components/balance/HistoryAll'
import HistoryFluctuate from '@/components/balance/HistoryFluctuate'
//调拨通
import MakeBill from '@/components/allot/MakeBill'

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
                        {path: '/data-manage/bank-account', name: 'BankAccount', component: BankAccount, meta: {requireAuth: true}}
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
