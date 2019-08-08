<style lang="less" type="text/less">
    #emptyContent {
        width: 90%;
        height: 100%;
        margin: 0 auto;
        min-width: 960px;
        min-height: 552px;
        position: relative;

        .el-header {
            background-color: #fff;
            height: auto !important;
        }
        .el-main {
            width: 100%;
            background-color: #fff;
            box-sizing: border-box;
            padding: 0;

            > .el-container{
                height: 100%;

                > .el-header {
                    overflow: visible;
                }
            }
        }
        .el-footer {
            background-color: #fff;
            padding: 0;
            height: auto !important;

            .el-pagination {
                margin-top: 14px;
            }
        }

        > .el-header {
            line-height: 52px;
            background-color: #E7E7E7;

            h1 {
                margin-bottom: 0;
            }
        }

        > .el-main {
            padding: 20px;
            padding-bottom: 14px;
            overflow: hidden;
        }
    }
</style>

<template>
    <el-container id="emptyContent" v-loading="loading"
                  element-loading-background="rgba(230, 230, 230, 0.5)">
        <el-header>
            <h1 v-text="currentTitle"></h1>
        </el-header>
        <el-main>
            <router-view @transmitTitle="currentTitle= $event"
                         @getTableData="getRouterData"
                         @getCommTable="commRouterData"
                         @getGatherData="getGatherData"
                         @downLoadData="downLoad"
                         @exportData="exportFun"
                         :tableData="childData"
                         :gatherData="childGatherData"></router-view>
        </el-main>
    </el-container>
</template>

<script>
    export default {
        name: "EmptyContent",
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
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
                    url: this.queryUrl + "adminProcess",
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
                        temporaryThis.childData = currentData;
                        temporaryThis.loading = false;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //获取normal接口数据
            commRouterData: function (routerData) {
                this.loading = true;
                var currParams = {};
                for (var k in routerData) {
                    currParams[k] = routerData[k];
                }
                var temporaryThis = this;
                this.$axios({
                    url: this.queryUrl + "normalProcess",
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
                        temporaryThis.childData = currentData;
                        temporaryThis.loading = false;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //部分页面获取汇总数据接口
            getGatherData: function (routerData) {
                var currParams = {};
                for (var k in routerData) {
                    currParams[k] = routerData[k];
                }
                this.$axios({
                    url: this.queryUrl + "normalProcess",
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
            //下载
            downLoad: function (loadData) {
                var currParams = {};
                for (var k in loadData) {
                    currParams[k] = loadData[k];
                }
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: currParams,
                    responseType: 'blob'
                }).then((result) => {
                    if (result.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else {
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
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //导出
            exportFun: function (routerMessage) {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: routerMessage.optype,
                        params: routerMessage.params
                    },
                    responseType: 'blob'
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else {
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
                }).catch(function (error) {
                    console.log(error);
                })
            }
        }
    }
</script>
