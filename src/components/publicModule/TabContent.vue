<style lang="less" type="text/less">
    #tabContent {
        width: 90%;
        height: 100%;
        margin: 0 auto;
        min-width: 920px;
        position: relative;

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
        }

        .el-button + .el-button {
            margin-left: 4px;
        }

        //标签页按钮
        .tab-left {
            position: absolute;
            top: 12px;
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
    }
</style>

<template>
    <div id="tabContent">
        <header>
            <h1 v-text="currentTitle"></h1>
        </header>
        <!--标签页按钮-->
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
        <section class="content" v-loading="loading">
            <router-view @transmitTitle="currentTitle= $event"
                         @getTableData="setRouterMessage"
                         :tableData="childData"
                         :isPending="isPending"
                         ></router-view>
        </section>
    </div>
</template>

<script>
    export default {
        name: "TabContent",
        data: function () {
            return {
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
                this.loading = true;
                this.$axios({
                    url: "/cfm/normalProcess",
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
                        for(var k in params){
                            if(k != "page_size" && k != "page_num"){
                                delete params[k];
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
