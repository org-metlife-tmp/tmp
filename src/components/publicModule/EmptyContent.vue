<style lang="less" type="text/less">
    #emptyContent {
        width: 90%;
        height: 100%;
        margin: 0 auto;
        min-width: 960px;
        .content {
            width: 100%;
            height: 90%;
            min-height: 500px;
            box-sizing: border-box;
            position: relative;
            padding: 20px;
            background-color: #fff;
        }

        //表格内部操作按钮
        .el-table .el-button {
            padding: 3px 3px;
            + .el-button {
                margin-left: 4px;
            }
        }
    }

    /*设置弹出框公共样式*/
    .el-dialog {
        text-align: left;
        margin-bottom: 10px;
        /*设置标题*/
        .dialog-title {
            margin-bottom: 0;
        }
        .el-dialog__body {
            padding-top: 10px;
            padding-bottom: 0;
        }
        .el-form {
            width: 94%;
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
        <section class="content" v-loading="loading">
            <router-view @transmitTitle="currentTitle= $event"
                         @getTableData="getRouterData"
                         @getCommTable="commRouterData"
                         @getGatherData="getGatherData"
                         @downLoadData="downLoad"
                         :tableData="childData"
                         :gatherData="childGatherData"></router-view>
        </section>
    </div>
</template>

<script>
    export default {
        name: "EmptyContent",
        data: function () {
            return {
                currentTitle: "",
                childData: {},
                childGatherData: {},
                loading: false
            }
        },
        methods: {
            getRouterData: function (routerData) {
                this.loading = true;
                var currParams = {};
                for (var k in routerData) {
                    currParams[k] = routerData[k];
                }
                var temporaryThis = this;
                this.$axios({
                    url: "/cfm/adminProcess",
                    method: "post",
                    data: currParams
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
                        var currentData = result.data;
                        temporaryThis.childData = currentData;
                        temporaryThis.loading = false;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            commRouterData:function(routerData){
                this.loading = true;
                var currParams = {};
                for (var k in routerData) {
                    currParams[k] = routerData[k];
                }
                var temporaryThis = this;
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: currParams
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else{
                        var currentData = result.data;
                        temporaryThis.childData = currentData;
                        temporaryThis.loading = false;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            getGatherData: function(routerData){
                var currParams = {};
                for (var k in routerData) {
                    currParams[k] = routerData[k];
                }
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: currParams
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else {
                        var currentData = result.data;
                        this.childGatherData = currentData;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            downLoad:function(loadData){
                var currParams = {};
                for (var k in loadData) {
                    currParams[k] = loadData[k];
                }
                this.$axios({
                    url:"/cfm/normalProcess",
                    method: "post",
                    data: currParams,
                    responseType: 'blob'
                }).then((result) => {
                    if(result.error_msg){
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
                        var fileName = decodeURI(result.headers["content-disposition"]).split("=")[1];
                        //ie兼容
                        if (window.navigator && window.navigator.msSaveOrOpenBlob) {
                            window.navigator.msSaveOrOpenBlob(new Blob([result.data]), fileName);
                        } else {
                            let url = window.URL.createObjectURL(new Blob([result.data]));
                            let link = document.createElement('a');
                            link.style.display = 'none';
                            link.href = url;
                            link.setAttribute('download', fileName);
                            document.body.appendChild(link);
                            link.click();
                        }
                    }
                }).catch(function(error){
                    console.log(error);
                })
            }
        }
    }
</script>
