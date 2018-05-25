<style>
    #whiteContent {
        width: 80%;
        height: 100%;
        margin: 0 auto;
        min-width: 800px;
    }
    .content {
        width: 100%;
        height: 90%;
        background-color: #fff;
        min-height: 500px;
        box-sizing: border-box;
        position: relative;
        padding: 20px;
        background-color: #fff;
    }
    /*按钮样式*/
    .content .button-list-left,
    .content .button-list-right {
        position: absolute;
        top: -40px;
    }

    .content .button-list-left {
        left: 0;
    }

    .content .button-list-right {
        right: 0;
    }
    .el-button+.el-button {
        margin-left: 4px;
    }

    /*表格部分*/
    .table-setion {
        position: absolute;
        width: 100%;
        height: 44%;
        transition: top 1s, height 1s;
        background-color: #fff;
    }
    .table-up {
        top: 0;
        height: 92%;
    }

    .table-down {
        top: 48%;
        height: 44%;
    }

    .table-setion img {
        cursor: pointer;
        vertical-align: top;
    }
</style>

<template>
    <div id="whiteContent">
        <header>
            <h1 v-text="currentTitle"></h1>
        </header>
        <section class="content" v-loading="loading">
            <div class="button-list-left">
                <el-button type="primary" plain size="mini">全部公司</el-button>
                <el-button type="primary" plain size="mini">全部银行</el-button>
                <el-button type="primary" plain size="mini">全部属性</el-button>
                <el-button type="primary" plain size="mini">账户模式</el-button>
            </div>
            <div class="button-list-right">
                <el-button type="warning" size="mini">打印</el-button>
                <el-button type="warning" size="mini">下载</el-button>
            </div>
            <router-view @transmitTitle="currentTitle= $event"
                         @getTableData="getRouterData"
                         :tableData="childData"></router-view>
        </section>
    </div>
</template>

<script>

    export default {
        name: "WhiteContent",
        data: function () {
            return {
                currentTitle: "",
                childData: {},
                loading: true
            }
        },
        methods: {
            getRouterData: function (routerData) {
                this.loading = true;
                var currParams = {};
                for(var k in routerData){
                    currParams[k] = routerData[k];
                }
                var temporaryThis = this;
                this.$axios({
                    url: "/cfm/process",
                    method: "post",
                    params: currParams
                }).then(function (result) {
                    var currentData = result.data;
                    temporaryThis.childData = currentData;
                    temporaryThis.loading = false;
                }).catch(function (error) {
                    console.log(error);
                })
            }
        },
        computed: {}
    }
</script>
