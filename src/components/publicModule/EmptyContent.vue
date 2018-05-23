<style lang="less" type="text/less">
    #emptyContent {
        width: 80%;
        height: 100%;
        margin: 0 auto;
        min-width: 800px;
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
    }

    //表格内部操作按钮
    .el-table .el-button {
        padding: 3px 3px;
    }

    .el-button + .el-button {
        margin-left: 4px;
    }

    .el-dialog {
        text-align: left;
        .el-form {
            width: 85%;
            .el-select {
                width: 100%;
            }
        }
    }
</style>

<template>
    <div id="emptyContent">
        <header>
            <h1 v-text="currentTitle"></h1>
        </header>
        <section class="content">
            <router-view @transmitTitle="currentTitle= $event"
                         @getTableData="getRouterData"
                         :tableData="childData"></router-view>
        </section>
    </div>
</template>

<script>
    export default {
        name: "",
        data: function () {
            return {
                currentTitle: "",
                childData: {}
            }
        },
        methods: {
            getRouterData: function (routerData) {
                var currParams = {};
                for (var k in routerData) {
                    currParams[k] = routerData[k];
                }
                var temporaryThis = this;
                this.$axios({
                    url: "/cfm/adminProcess",
                    method: "post",
                    data: currParams
                }).then(function (result) {
                    var currentData = result.data;
                    temporaryThis.childData = currentData;
                }).catch(function (error) {
                    console.log(error);
                })
            }
        }
    }
</script>
