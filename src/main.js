// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'

//引入elementUI库
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
Vue.use(ElementUI);
//引入axios
import axios from "axios";
axios.defaults.withCredentials = true
Vue.prototype.$axios = axios;
//引入echarts
var echarts = require('echarts/lib/echarts');
require("echarts/lib/chart/pie");
require('echarts/lib/component/tooltip');
require("echarts/lib/component/legendScroll");
Vue.prototype.$echarts = echarts;
//import echarts from "echarts"
//Vue.prototype.$echarts = echarts

//Vue.use(Element, { size: 'small' });
Vue.config.productionTip = false
/* eslint-disable no-new */

new Vue({
    el: '#app',
    router,
    components: {App},
    template: '<App/>'
})
