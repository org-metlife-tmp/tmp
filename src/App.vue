<style>
    #app {
        height: 100%;
    }
</style>
<template>
    <div id="app">
        <router-view></router-view>
    </div>
</template>

<script>
    //    axios.defaults.headers.post['Content-Type'] = 'application/x-www-form-urlencoded';

    import Vue from 'vue'
    import Vuex from 'vuex'
    Vue.use(Vuex);
    const store = new Vuex.Store({
        state: {
            count: 0
        },
        mutations: {
            increment (state) {
                state.count++
            }
        }
    })

    export default {
        name: 'App',
        data: function () {
            return {}
        },
        beforeCreate: function () {
            this.$axios({
                url: "/cfm/login",
                method: "post",
                params: {
                    "passwd": "1f4e40efba9a66f163bb26033af857dd26acb8609697282fccc7316dedaf6dab6d96748b915fbae42829f70522474f56d86cd75b358c431f5c8a9b215651296a0804f241ad3b951cd50bdb4544ee5c333e59933e61305f80a9c9cf2e9abc8bc92613d3cd4fdda91aa7019bd2b618a90d7be545dad04bfd2107ffb611c7d8f160",
                    useridentify: "zhangyy",
                    "hid_exponent": "010001",
                    "hid_modulus": "00ba3b4a0e80f0c334c0d664598ee1674366f0bee390a92df0e1ffef7d4016f05f332e556459b5d46ed8533b61fc9b23f55cf1f7c466c775a6adf7cc973690764ff9e9cbdd00b1dd93e756b29094f745c5139cbc1d94b3db0e3436e910fb31f102f3cc41581b34a80294fe25f458460b8ed154e9862b5c4abdc1fb165903306a15"

                }
            }).then(function (response) {
//                console.log(response.data);
            });
        },
        created: function () {
            /*axios({
             url: "/cfm/process",
             method: "post",
             params: {
             "optype": "acc_data_report",
             }
             }).then(function (response) {
             //                console.log(response.data);
             });*/
        },
        methods: {
            setCookie: function (key, value, iDay) {
                var oDate = new Date();
                oDate.setDate(oDate.getDate() + iDay);
                document.cookie = key + '=' + value + ';expires=' + oDate;
            },
            removeCookie: function (key) {
                this.setCookie(key, '', -1);
            },
            getCookie: function (key) {
                var cookieArr = document.cookie.split('; ');
                for (var i = 0; i < cookieArr.length; i++) {
                    var arr = cookieArr[i].split('=');
                    if (arr[0] === key) {
                        return arr[1];
                    }
                }
                return false;
            }
        },
        computed: {
        },
        store
    }
</script>

