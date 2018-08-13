// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'

//引入elementUI库
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.use(ElementUI);

//引入echarts
var echarts = require('echarts/lib/echarts');
require("echarts/lib/chart/pie");
require('echarts/lib/chart/bar');
require('echarts/lib/component/tooltip');
require("echarts/lib/component/legendScroll");
Vue.prototype.$echarts = echarts;

require("babel-polyfill");

/*引入axios*/
import axios from 'axios';

axios.defaults.withCredentials = true;
//设置axios请求头附带token
import store from './js/store.js';

axios.defaults.headers.common['Authorization'] = store.state.token;
axios.interceptors.request.use(config => {
        //判断是否存在token 如果存在将每个页面header都添加token
        if (store.state.token) {
            config.headers.common['Authorization'] = store.state.token;
        }
        return config;
    },
    err => {
        //请求错误的做法
        return Promise.reject(err);
    }
);
//response拦截器
axios.interceptors.response.use(
    response => {
        return response;
    },
    error => {
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    store.commit('del_store');
                    ElementUI.Message({
                        type: 'warning',
                        message: "部分数据无请求权限",
                        duration: 2000
                    });
                    router.replace({
                        path: "/login",
                        query: {redirect: router.currentRoute.fullPath}//登录成功后跳入浏览的当前页面
                    });
                    break;
                case 500:
                    ElementUI.Message({
                        type: 'error',
                        message: "数据请求错误,请检查服务器",
                        duration: 2000
                    });
                    break;
            }
        }
        return Promise.reject(error.response.data);
    }
)
Vue.prototype.$axios = axios;


Vue.config.productionTip = false
/* eslint-disable no-new */

new Vue({
    el: '#app',
    router,
    components: {App},
    template: '<App/>'
})
