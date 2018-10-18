<style scoped lang="less" type="text/less">
    #collectionManage{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion{
            text-align: left;
        }

        /*分隔栏*/
        .split-bar {
            width: 106%;
            height: 6px;
            margin-left: -20px;
            background-color: #E7E7E7;
            margin-bottom: 20px;
        }

        /*数据展示区*/
        .table-content{
            text-align: left;
            height: 76%;
            overflow-y: auto;

            .el-card{
                width: 92%;
                margin-bottom: 20px;

                /*顶部按钮*/
                .title-btn{
                    float: right;
                    margin-top: -4px;

                    .icon-img{
                        width: 24px;
                        height: 28px;
                        display: inline-block;
                        cursor: pointer;
                        background: url(../../assets/icon_common.png);
                    }
                    i:nth-child(1){
                        background-position: -89px -74px;
                    }
                    i:nth-child(2){
                        background-position: -187px -74px;
                    }
                    i:nth-child(3){
                        background-position: -43px -48px;
                    }
                    i:nth-child(4){
                        background-position: -140px -74px;
                    }

                    .icon-log{
                        width: 24px;
                        height: 28px;
                        vertical-align: top;
                        font-size: 19px;
                        cursor: pointer;
                        line-height: 28px;
                    }
                }

                /*卡片内容*/
                .card-content{
                    text-align: center;

                    .content-top{
                        height: 110px;
                        background-color: #F7F7F7;

                        .collect-money{
                            padding: 20px 0 15px 0;
                            color: #FF5800;
                            font-size: 18px;
                        }

                        .collect-status{
                            width: 56px;
                            height: 56px;
                            margin: 10px auto;
                            border-radius: 50%;
                            background-color: #fff;
                            padding-top: 2px;

                            div{
                                width: 52px;
                                height: 52px;
                                box-sizing: border-box;
                                border: 1px solid #F1F1F1;
                                border-radius: 50%;
                                background: #F7F7F7;
                                margin: 0 auto;

                                .status-icon{
                                    display: inline-block;
                                    width: 52px;
                                    height: 52px;
                                    background: url(../../assets/icon_common.png);
                                    background-position: -388px -308px;
                                }
                                .coll-active{
                                    background-position: -292px -308px;
                                }
                            }
                        }
                    }

                    .content-center{
                        height: 60px;
                        width: 90%;
                        margin: 50px auto 20px;
                        /*border-bottom: 1px solid #ccc;*/

                        span{
                            display: block;
                            margin-top: 6px;
                        }
                    }

                    .content-bottom{
                        width: 90%;
                        margin: 0 auto;
                        text-align: left;
                        padding-bottom: 60px;
                    }
                }
            }

        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

        /*日志弹框*/
        .dialog-title{
            border-bottom: 1px solid #ccc;
            margin-bottom: 10px;

            span{
                margin-left: 10px;
                color: #ccc;
            }
        }

        .table-all-data{
            border: 1px solid #ebeef5;
            border-bottom: none;
            padding: 0 6px;
            box-sizing: border-box;

            div{
                float: right;
            }
        }

        /*按钮样式*/
        .send {
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            background-position: -47px -78px;
            vertical-align: middle;
        }
    }
</style>
<style lang="less" type="text/less">
    #collectionManage{
        .el-card{
            .el-card__header{
                padding: 8px 14px;
            }
            .el-card__body{
                padding: 0;
            }
        }
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 400px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="collectionManage">
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.collect_type" placeholder="请选择归集额度"
                                       clearable filterable
                                       style="width:100%">
                                <el-option v-for="(collType,key) in collTypeList"
                                           :key="key"
                                           :label="collType"
                                           :value="key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.collect_frequency" placeholder="请选择归集频率"
                                       clearable filterable
                                       style="width:100%">
                                <el-option v-for="(frequency,key) in frequencyList"
                                           :key="key"
                                           :label="frequency"
                                           :value="key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.topic" clearable placeholder="请输入归集关键字"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.main_acc_query_key" clearable placeholder="请输入归集主账号关键字"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item style="margin-bottom:0px">
                            <el-checkbox-group v-model="searchData.is_activity">
                                <el-checkbox v-for="(name,k) in statusList"
                                             :label="k" name="type" :key="k">
                                    {{ name }}
                                </el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section class="table-content">
            <el-row>
                <el-col :span="6" v-for="(card,index) in tableList" :key="card.id">
                    <el-card class="box-card">
                        <div slot="header">
                            <span>{{ card.topic }}</span>
                            <div class="title-btn">
                                <i class="icon-img" title="激活" v-show="card.is_activity == 0"
                                   @click="activeCollect(card)"></i>
                                <i class="icon-img" title="暂停" v-show="card.is_activity == 1"
                                   @click="activeCollect(card)"></i>
                                <i class="icon-img" title="作废" v-show="card.is_activity == 0"
                                   @click=removeCollect(card,index,tableList)></i>
                                <i class="icon-img" title="查看" @click="lookCollect(card)"></i>
                                <i class="icon-log el-icon-tickets" title="日志" @click="lookLog(card)"></i>
                            </div>
                        </div>
                        <div class="card-content">
                            <div class="content-top">
                                <div class="collect-money">￥{{ transitionMoney(card.collect_amount) }}</div>
                                <div class="collect-way">{{ card.collect_type_name }}</div>
                                <div class="collect-status">
                                    <div>
                                        <i class="status-icon" :class="{'coll-active':card.is_activity == 1}"></i>
                                    </div>
                                </div>
                            </div>
                            <div class="content-center">
                                <span>{{ card.collect_frequency_name }}</span>
                                <span>{{ card.collect_time }}</span>
                            </div>
                            <!--<div class="content-bottom">
                                11087363445533344 (子账户 1个)
                            </div>-->
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </section>
        <!--日志 弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="1000px" top="76px"
                   title="日志"
                   :close-on-click-modal="false">
            <div class="dialog-title">
                {{ dialogData.topic }}
                <span>[ {{ dialogData.service_serial_number }} ]</span>
            </div>

            <!--上半部分表格-->
            <div class="table-all-data" v-show="topLogList.length > 0">
                执行时间: <span>{{ topLogData.execute_time }}</span>
                <div>
                    共 <span style="color:#FF5800">{{ topLogData.total_num }}</span> 笔,
                    成功 <span>{{ topLogData.success_count }}</span> 笔,
                    归集总额 <span style="color:#FF5800">￥{{ $common.transitSeparator(topLogData.total_amount) }}</span>
                </div>
            </div>
            <el-table :data="topLogList"
                      border size="mini"
                      highlight-current-row>
                <el-table-column prop="recv_account_no" label="被归集账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_bank" label="归集账号开户行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_account_no" label="归集主账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="collect_amount" label="归集金额" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="collect_status" label="状态" :show-overflow-tooltip="true"
                                 :formatter="transitionStatus"></el-table-column>
                <el-table-column
                        label="操作"
                        width="80">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="发送" placement="bottom" effect="light" :enterable="false"
                                    :open-delay="500" v-show="scope.row.collect_status == 4">
                            <el-button class="send" size="mini" type="warning"
                                       @click="sendData(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="作废" placement="bottom" effect="light" :enterable="false"
                                    :open-delay="500" v-show="scope.row.collect_status == 4">
                            <el-button size="mini" class="withdraw" icon="el-icon-circle-close-outline" type="danger"
                                       style="font-size:16px;padding: 1px;vertical-align: bottom;line-height: 15px;"
                                       @click="cancelData(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>

            <!--下半部分表格-->
            <div class="table-all-data" style="margin-top:20px" v-show="bottomLogList.length > 0">
                执行时间: <span>{{ bottomLogData.execute_time }}</span>
                <div>
                    共 <span style="color:#FF5800">{{ bottomLogData.total_num }}</span> 笔,
                    成功 <span>{{ bottomLogData.success_count }}</span> 笔,
                    归集总额 <span style="color:#FF5800">￥{{ $common.transitSeparator(bottomLogData.total_amount) }}</span>
                </div>
            </div>
            <el-table :data="bottomLogList"
                      border size="mini"
                      v-show="bottomLogList.length > 0"
                      highlight-current-row>
                <el-table-column prop="recv_account_no" label="被归集账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_bank" label="归集账号开户行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_account_no" label="归集主账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="collect_amount" label="归集金额" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="collect_status" label="状态" :show-overflow-tooltip="true"
                                 :formatter="transitionStatus"></el-table-column>
            </el-table>
            <span slot="footer" class="dialog-footer" style="text-align:center"></span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "CollectionManage",
        created: function () {
            this.$emit("transmitTitle", "自动归集管理");
            this.$emit("getCommTable", this.routerMessage);
        },
        mounted: function(){
            /*获取常量数据*/
            //获取归集额度
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.CollOrPoolType) {
                this.collTypeList = constants.CollOrPoolType;
            }
            //归集频率
            if (constants.CollOrPoolFrequency) {
                this.frequencyList = constants.CollOrPoolFrequency;
            }
        },
        props: ["tableData"],
        data:function(){
            return {
                routerMessage: {
                    optype: "collectmanage_list",
                    params: {

                    }
                },
                searchData:{ //搜索条件
                    collect_type: "",
                    collect_frequency: "",
                    topic: "",
                    main_acc_query_key: "",
                    is_activity: []
                },
                statusList: {
                    1: "已激活",
                    0: "未激活"
                },
                tableList: [], //列表数据
                collTypeList: {}, //常量数据
                frequencyList: {},
                dialogVisible: false, //日志弹框
                dialogData: {},
                topLogData: {
                    execute_time: "",
                    total_num: "",
                    success_count: "",
                    total_amount: ""
                },
                topLogList: [],
                bottomLogData: {
                    execute_time: "",
                    total_num: "",
                    success_count: "",
                    total_amount: ""
                },
                bottomLogList: [],
                formLabelWidth: "120px",
            }
        },
        methods: {
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;

                for (var k in searchData) {
                    this.routerMessage.params[k] = searchData[k];
                }
                this.$emit("getCommTable", this.routerMessage);
            },
            //展示格式转换-金额
            transitionMoney: function(num){
                return this.$common.transitSeparator(num);
            },
            //展示格式转换-状态
            transitionStatus:  function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.CollOrPoolRunStatus) {
                    return constants.CollOrPoolRunStatus[cellValue];
                }
            },
            //查看
            lookCollect: function(row){
                this.$router.push({
                    name: "CollectionSet",
                    query: {
                        viewId: row.id
                    }
                });
            },
            //激活
            activeCollect: function(row){
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "collectmanage_setstate",
                        params: {
                            id: row.id,
                            persist_version: row.persist_version
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                        return;
                    }
                    var data = result.data.data;
                    row.id = data.id;
                    row.persist_version = data.persist_version;
                    row.is_activity = data.is_activity;

                    this.$message({
                        type: "success",
                        message: data.is_activity == "0" ? "已暂停" : "激活成功",
                        duration: 2000
                    })
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //作废
            removeCollect: function (row, index, rows) {
                this.$confirm('确认作废当前单据吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/normalProcess",
                        method: "post",
                        data: {
                            optype: "collectmanage_cancel",
                            params: {
                                id: row.id,
                                persist_version: row.persist_version
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {
                            this.$message({
                                type: "error",
                                message: result.data.error_msg,
                                duration: 2000
                            })
                            return;
                        }

                        rows.splice(index, 1);
                        this.$message({
                            type: "success",
                            message: "删除成功",
                            duration: 2000
                        })
                    }).catch(function (error) {
                        console.log(error);
                    })
                }).catch(() => {
                });
            },
            //弹框-发送
            sendData: function(row){
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "collectmanage_sendPayList",
                        params: {
                            ids: [row.id]
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                        return;
                    }else{
                        row.collect_status = 2;
                        this.$message({
                            type: "success",
                            message: "发送成功",
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //弹框-作废
            cancelData: function(row){
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "collectmanage_cancelinstruction",
                        params: {
                            id: row.id,
                            repeat_count: row.repeat_count
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                        return;
                    }else{
                        var data = result.data.data;
                        row.collect_status = data.collect_status;
                        this.$message({
                            type: "success",
                            message: "作废成功",
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //查看日志
            lookLog: function(row){
                //清空数据
                var topLogData = this.topLogData;
                for(var k in topLogData){
                    topLogData[k] = "";
                }
                this.topLogList = [];
                var bottomLogData = this.bottomLogData;
                for(var k in bottomLogData){
                    bottomLogData[k] = "";
                }
                this.bottomLogList = [];

                //获取列表数据
                this.dialogData = row;
                //获取当前数据
                this.dialogVisible = true;
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "collectmanage_instruction",
                        params: {
                            collect_id: row.id
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        });
                    }else{
                        var data = result.data.data;
                        if(data.length >= 1){
                            var oneData = data[0];
                            var topLogData = this.topLogData;
                            for(var k in topLogData){
                                topLogData[k] = oneData[k];
                            }
                            this.topLogList = oneData.detail;
                        }
                        if(data.length >= 2){
                            var towData = data[1];
                            var bottomLogData = this.bottomLogData;
                            for(var k in bottomLogData){
                                bottomLogData[k] = towData[k];
                            }
                            this.bottomLogList = towData.detail;
                        }
                    }

                }).catch(function (error) {
                    console.log(error);
                })
            }
        },
        watch: {
            tableData: function (val, oldVal) {
                this.tableList = val.data;
            }
        }
    }
</script>

