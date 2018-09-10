<style scoped lang="less" type="text/less">
    #openAccountConfirm {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

    }
</style>

<template>
    <div id="openAccountConfirm">
        开户确认
    </div>
</template>

<script>
    export default {
        name: "OpenAccountConfirm",
        created: function () {
            this.$emit("transmitTitle", "开户确认");
        },
        mounted:function(){
            // //银行大类
            // var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            // if (bankTypeList) {
            //     this.bankAllList = bankTypeList;
            //     this.bankTypeList = bankTypeList;
            // }
            // //币种
            // var currencyList = JSON.parse(window.sessionStorage.getItem("selectCurrencyList"));
            // if (currencyList) {
            //     this.currencyList = currencyList;
            // }
            // //账户属性&账户用途
            // var catgList = JSON.parse(window.sessionStorage.getItem("catgList"));
            // for (var i = 0; i < catgList.length; i++) {
            //     if (catgList[i].code == "acc_attr") {
            //         this.attrList = catgList[i].items;
            //     }else if(catgList[i].code == "acc_purpose"){
            //         this.purposeList = catgList[i].items;
            //     }
            // }
            // //账户模式
            // var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            // if (constants.InactiveMode) {
            //     this.interList = constants.InactiveMode;
            // }
            // //存款类型 
            // if (constants.DepositsMode) {
            //     this.depositsList = constants.DepositsMode;
            // }
        },
        props: [],
        data: function () {
            return {
            }
        },
        methods: {
            
        },
        watch: {
        }
    }
</script>
