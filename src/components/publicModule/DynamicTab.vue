<style lang="less" type="text/less">
    #dynamicTab {
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

        //标签页按钮
        .tab-left {
            position: absolute;
            top: 22px;
            height: 30px;
            color: #b1b1b1;

            ul{
                li{
                    float: left;
                    height: 100%;
                    line-height: 30px;
                    width: 100px;
                    background-color: #f2f2f2;
                    border-radius: 3px 3px 0 0;
                    cursor: pointer;

                    i{
                        font-size: 20px;
                        vertical-align: middle;
                    }
                }
                li:nth-child(1){
                    margin-right: 4px;
                }
                .tab-active{
                    color: #00b3ed;
                    background-color: #fff;
                }
            }
        }
    }
</style>

<template>
    <el-container id="dynamicTab" v-loading="loading"
                  element-loading-background="rgba(230, 230, 230, 0.5)">
        <el-header>
            <h1 v-text="currentTitle"></h1>
            <div class="tab-left">
                <ul>
                    <li :class="{'tab-active':isActive}" @click="activeCurrentTab(true)">
                        {{ tabText.leftTab }}
                    </li>
                    <li :class="{'tab-active':!isActive}" @click="activeCurrentTab(false)">
                        {{ tabText.rightTab }}
                    </li>
                </ul>
            </div>
        </el-header>
        <el-main>
            <router-view @transmitTitle="currentTitle= $event"
                         @getTableData="setRouterMessage"
                         @tableText="setTablText"
                         @exportData="exportFun"
                         :tableData="childData"
                         :isPending="isPending"
            ></router-view>
        </el-main>
    </el-container>
</template>

<script>
    export default {
        name: "DynamicTab",
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                currentTitle: "标题错误",
                loading: false,
                childData:{},
                isActive: true, //tab标签激活状态
                isPending: true, //tab激活状态
                todoMessage:{ //待处理数据请求信息
                },
                doneMessage:{ //已处理数据请求信息
                },
                tabText:{
                    leftTab: "",
                    rightTab: ""
                }
            }
        },
        methods: {
            //设置tab标题
            setTablText: function(text){
                this.tabText.leftTab = text.leftTab;
                this.tabText.rightTab = text.rightTab;
            },
            //设置当前message信息
            setRouterMessage:function(routerData){
                this.todoMessage = routerData.todo;
                this.doneMessage = routerData.done;
                if(this.isActive){
                    this.getRouterData(routerData.todo);
                }else{
                    this.getRouterData(routerData.done);
                }
            },
            //获取数据
            getRouterData:function(params){
                this.loading = true;
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: params
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
                        var currentData = result.data;
                        this.childData = currentData;
                        this.loading = false;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //切换tab页
            activeCurrentTab:function(currentStatus){
                if(currentStatus){
                    if(!this.isActive){
                        this.isActive = true;
                        this.isPending = true;
                        this.getRouterData(this.todoMessage);
                        var params = this.doneMessage.params;
                        if(this.doneMessage.optype =='wfquery_processtasksall'){
                            for(var k in params){
                                if(k != "page_size" && k != "page_num" && k != "assignee_id"){
                                    delete params[k];
                                }
                            }
                        }else{
                            for(var k in params){
                                if(k != "page_size" && k != "page_num" && k != "is_checked"){
                                    delete params[k];
                                }
                            }
                        }
                    }
                }else{
                    if(this.isActive){
                        this.isActive = false;
                        this.isPending = false;
                        this.getRouterData(this.doneMessage);
                        var params = this.todoMessage.params;
                        for(var k in params){
                            if(k != "page_size" && k != "page_num" && k != "is_checked"){
                                delete params[k];
                            }
                        }
                    }
                }
            },
            //导出
            exportFun: function(routerMessage){
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

