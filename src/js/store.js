/************
**状态管理部分
 * token: 保存后台返回的token值 用来管理登录状态
 * user: 登录用户的信息
 * set_token: 保存token和user信息
 * del_token: 清除token和user信息
************/
import Vue from 'vue'
import Vuex from 'vuex'

Vue.use(Vuex);

export default new Vuex.Store({
    state:{
        token: '',
        user: ''
    },
    mutations: {
        set_token:function(state,data){
            state.token = data.token;
            state.user = data.user;
            window.sessionStorage.setItem("token",data.token);
            window.sessionStorage.setItem("user",JSON.stringify(data.user));
        },
        del_token:function(state){
            state.token = '';
            state.user = '';
            window.sessionStorage.removeItem("token");
            window.sessionStorage.removeItem("uer");
        }
    }
})
