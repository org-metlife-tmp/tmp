<style lang="less" type="text/less">
    #tabContent {
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
    <el-container id="tabContent" v-loading="loading"
                  element-loading-background="rgba(230, 230, 230, 0.5)">
        <el-header>
            <h1 v-text="currentTitle"></h1>
            <div class="tab-left">
                <ul>
                    <li :class="{'tab-active':isActive}" @click="activeCurrentTab(true)">
                        <i class="el-icon-time"></i>
                        待处理
                    </li>
                    <li :class="{'tab-active':!isActive}" @click="activeCurrentTab(false)">
                        <i class="el-icon-circle-check-outline"></i>
                        已处理
                    </li>
                </ul>
            </div>
        </el-header>
        <el-main>
            <router-view @transmitTitle="currentTitle= $event"
                         @getTableData="setRouterMessage"
                         :tableData="childData"
                         :isPending="isPending"
            ></router-view>
        </el-main>
    </el-container>
</template>

<script>
    export default {
        name: "TabContent",
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
                }
            }
        },
        methods: {
            setRouterMessage:function(routerData){
                this.todoMessage = routerData.todo;
                this.doneMessage = routerData.done;
                if(this.isActive){
                    this.getRouterData(routerData.todo);
                }else{
                    this.getRouterData(routerData.done);
                }
            },
            getRouterData:function(params){
                var url =this.queryUrl + "normalProcess";
                if(params.optype ==='wfquery_pendingtasksall'||params.optype ==='wfquery_processtasksall'){
                    url =this.queryUrl + "commProcess";
                }
                this.loading = true;
                this.$axios({
                    url: url,
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
                                if(k != "page_size" && k != "page_num"){
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
                            if(k != "page_size" && k != "page_num"){
                                delete params[k];
                            }
                        }
                    }
                }
            }
        }
    }
</script>
