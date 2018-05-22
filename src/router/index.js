import Vue from 'vue'
import Router from 'vue-router'
//主页面
import Login from '@/components/Login'
import Main from '@/components/Main'
//首页
import Home from '@/components/Home'
//样式结构页面
import WhiteContent from '@/components/WhiteContent'
//余额通部分页面
import TodayDetail from '@/components/balance/TodayDetail'
import TodayAll from '@/components/balance/TodayAll'
import TodayFluctuate from '@/components/balance/TodayFluctuate'
import HistoryDetail from '@/components/balance/HistoryDetail'
import HistoryAll from '@/components/balance/HistoryAll'
import HistoryFluctuate from '@/components/balance/HistoryFluctuate'
//调拨通
import MakeBill from '@/components/allot/MakeBill'

Vue.use(Router)

export default new Router({
    routes: [
        //登录页面
        {path: '/login', name: 'Login', component: Login},
        {path: '/', redirect: {name:'Home'}},
        {path: '/', component: Main,
            children:[
                //首页
                {path: '/home', name: 'Home', component: Home},
                //余额通
                {path: '/whiteContent', name: 'WhiteContent', component: WhiteContent,
                    children: [
                        {path: 'today-detail', name: 'TodayDetail', component: TodayDetail},
                        {path: 'today-all', name: 'TodayAll', component: TodayAll},
                        {path: 'today-fluctuate', name: 'TodayFluctuate', component: TodayFluctuate},
                        {path: 'history-detail', name: 'HistoryDetail', component: HistoryDetail},
                        {path: 'history-all', name: 'HistoryAll', component: HistoryAll},
                        {path: 'history-fluctuate', name: 'HistoryFluctuate', component: HistoryFluctuate}
                    ]
                },
                //调拨通
                {path: '/makeBill', name: 'MakeBill', component: MakeBill}
            ]
        }
    ]
})
