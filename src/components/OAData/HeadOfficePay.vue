<style scoped lang="less" type="text/less">
    #headOfficePay {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion {
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

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

        /*撤回样式*/
        .withdraw {
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
            background-position: -48px 0;
        }

        /*查看弹框*/
        .dialog-talbe {
            width: 100%;
            height: 320px;

            li {
                float: left;
                box-sizing: border-box;
                border: 1px solid #e2e2e2;
                margin-left: -1px;
                margin-top: -1px;
                height: 30px;
                line-height: 30px;
            }

            .table-li-title {
                width: 12%;
                text-align: right;
                padding-right: 10px;
                font-weight: bold;
            }
            .table-li-content {
                width: 38%;
                padding-left: 10px;
            }

            .table-two-row {
                width: 88%;
                margin-left: -3px;
                border-left: none;
            }
        }
    }
</style>
<style lang="less" type="text/less">
    #headOfficePay {
        .el-dialog__wrapper {
            .el-dialog__body {
                height: 480px;
                overflow-y: scroll;
            }
        }
    }
</style>

<template>
    <div id="headOfficePay">
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="5">
                        <el-form-item>
                            <el-input v-model="searchData.recv_account_query_key" clearable
                                      placeholder="请输入收款方账户号或名称"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item style="margin-bottom:0px">
                            <el-checkbox-group v-model="searchData.service_status">
                                <el-checkbox v-for="(name,k) in statusList"
                                             :label="k" name="type" :key="k">
                                    {{ name }}
                                </el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="10"></el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList" border
                      size="mini"
                      highlight-current-row>
                <el-table-column prop="pay_account_no" label="付款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_account_bank" label="付款方银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_no" label="收款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_bank" label="收款方银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="payment_amount" label="收款金额" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_mode" label="付款方式" :show-overflow-tooltip="true"
                                 :formatter="transitPayMode"></el-table-column>
                <el-table-column prop="service_status" label="状态" :show-overflow-tooltip="true"
                                 :formatter="transitStatus"></el-table-column>
                <el-table-column
                        label="操作" width="80"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="!isPending">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookData(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="isPending">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editData(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="作废" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="isPending">
                            <el-button size="mini" class="withdraw" icon="el-icon-circle-close-outline" type="danger"
                                       style="font-size:16px;padding: 1px;vertical-align: bottom;line-height: 15px;"
                                       @click="cancellationData(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="撤回" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="!isPending">
                            <el-button size="mini" class="withdraw"
                                       @click="withdrawData(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </section>
        <!--分页部分-->
        <div class="botton-pag">
            <el-pagination
                    background
                    layout="sizes, prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    :page-sizes="[7, 50, 100, 500]"
                    :pager-count="5"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange"
                    :current-page="pagCurrent">
            </el-pagination>
        </div>
        <!--查看弹出框-->
        <el-dialog title="总公司付款信息"
                   :visible.sync="dialogVisible"
                   width="900px" top="76px"
                   :close-on-click-modal="false">
            <div class="serial-number">
                [编号:
                <span v-text="dialogData.service_serial_number"></span>
                ]
            </div>
            <ul class="dialog-talbe">
                <li class="table-li-title">付款账户号</li>
                <li class="table-li-content" v-text="dialogData.pay_account_no"></li>
                <li class="table-li-title">付款账户名称</li>
                <li class="table-li-content" v-text="dialogData.pay_account_name"></li>

                <li class="table-li-title">付款银行名称</li>
                <li class="table-li-content" v-text="dialogData.pay_account_bank"></li>
                <li class="table-li-title">CNAPS号</li>
                <li class="table-li-content" v-text="dialogData.pay_bank_cnaps"></li>

                <li class="table-li-title">银行所在省</li>
                <li class="table-li-content" v-text="dialogData.pay_bank_prov"></li>
                <li class="table-li-title">银行所在市</li>
                <li class="table-li-content" v-text="dialogData.pay_bank_city"></li>

                <li class="table-li-title">收款账户号</li>
                <li class="table-li-content" v-text="dialogData.recv_account_no"></li>
                <li class="table-li-title">收款账户名称</li>
                <li class="table-li-content" v-text="dialogData.recv_account_name"></li>

                <li class="table-li-title">收款银行名称</li>
                <li class="table-li-content" v-text="dialogData.recv_account_bank"></li>
                <li class="table-li-title">CNAPS号</li>
                <li class="table-li-content" v-text="dialogData.recv_bank_cnaps"></li>

                <li class="table-li-title">银行所在省</li>
                <li class="table-li-content" v-text="dialogData.recv_bank_prov"></li>
                <li class="table-li-title">银行所在市</li>
                <li class="table-li-content" v-text="dialogData.recv_bank_city"></li>

                <li class="table-li-title">付款方式</li>
                <li class="table-li-content" v-text="dialogData.pay_mode"></li>
                <li class="table-li-title">付款金额</li>
                <li class="table-li-content" style="color:#fd7d2f">￥{{ dialogData.payment_amount }}</li>

                <li class="table-li-title">单据状态</li>
                <li class="table-li-content" v-text="statusList[dialogData.service_status]"></li>
                <li class="table-li-title"></li>
                <li class="table-li-content"></li>

                <li class="table-li-title">摘要</li>
                <li class="table-li-content table-two-row" v-text="dialogData.payment_summary"></li>

                <li class="table-li-title" style="height:60px;line-height:60px">附件</li>
                <li class="table-li-content table-two-row" style="height:60px;padding-top:6px;overflow-y:auto">
                    <Upload :emptyFileList="emptyFileList"
                            :fileMessage="fileMessage"
                            :triggerFile="triggerFile"
                            :isPending="false"></Upload>
                </li>
            </ul>
            <BusinessTracking :businessParams="businessParams"></BusinessTracking>
        </el-dialog>
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";
    import BusinessTracking from "../publicModule/BusinessTracking.vue"

    export default {
        name: "HeadOfficePay",
        created: function () {
            //设置当前页基本信息
            this.$emit("transmitTitle", "总公司付款");
            this.$emit("tableText", {
                leftTab: "未处理",
                rightTab: "已处理"
            });

            this.$emit("getTableData", this.routerMessage);
        },
        mounted: function () {
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
        },
        components: {
            Upload: Upload,
            BusinessTracking:BusinessTracking
        },
        props: ["isPending", "tableData"],
        data: function () {
            return {
                routerMessage: {
                    todo: {
                        optype: "headorgoa_todolist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    },
                    done: {
                        optype: "headorgoa_donelist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    }
                },
                tableList: [],
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                searchData: { //搜索条件
                    recv_account_query_key: "",
                    service_status: []
                },
                statusList: { //常量数据
                    1: "已保存",
                    5: "审批拒绝"
                },
                dialogVisible: false, //弹框数据
                dialogData: {},
                emptyFileList: [], //附件
                fileMessage: {
                    bill_id: "",
                    biz_type: 20
                },
                triggerFile: false,
                businessParams:{ //业务状态追踪参数
                }
            }
        },
        methods: {
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                for (var k in searchData) {
                    if (this.isPending) {
                        this.routerMessage.todo.params[k] = searchData[k];
                    } else {
                        this.routerMessage.done.params[k] = searchData[k];
                    }
                }
                this.$emit("getTableData", this.routerMessage);
            },
            //换页后获取数据
            getCurrentPage: function (currPage) {
                if (this.isPending) {
                    this.routerMessage.todo.params.page_num = currPage;
                } else {
                    this.routerMessage.done.params.page_num = currPage;
                }
                this.$emit("getTableData", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.todo.params = {
                    page_size: val,
                    page_num: 1
                };
                this.routerMessage.done.params = {
                    page_size: val,
                    page_num: 1
                };
                this.$emit("getTableData", this.routerMessage);
            },
            //展示格式转换-付款方式
            transitPayMode: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.PayMode) {
                    return constants.PayMode[cellValue];
                }
            },
            //展示格式转换-处理状态
            transitStatus: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.BillStatus) {
                    return constants.BillStatus[cellValue];
                }
            },
            //查看
            lookData: function(row){
                this.dialogVisible = true;
                var dialogData = this.dialogData;
                for(var k in row){
                    if(k == "pay_mode"){
                        var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                        dialogData[k] = constants.PayMode[row[k]];
                    }else{
                        dialogData[k] = row[k];
                    }
                }
            },
            //编辑
            editData: function(row){
                this.$router.push({
                    name: "OAMakeBill",
                    query: {
                        id: row.id
                    }
                });
            },
            //撤回
            withdrawData: function (row) {
                this.$confirm('确认撤回当前单据吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/normalProcess",
                        method: "post",
                        data: {
                            optype: "headorgoa_revoke",
                            params: {
                                id: row.id,
                                persist_version: row.persist_version,
                                service_status: row.service_status
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
                        } else {
                            this.$message({
                                type: "success",
                                message: "撤回成功",
                                duration: 2000
                            });
                            this.$emit("getTableData", this.routerMessage);
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }).catch(() => {
                });
            },
            //作废
            cancellationData: function (row) {
                this.$prompt('请输入作废原因', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    title: "作废原因",
                    inputValidator: function(value){
                        if(!value){
                            return false;
                        }else{
                            return true;
                        }
                    },
                    inputErrorMessage: '请输入作废原因'
                }).then(({ value }) => {
                    this.$axios({
                        url: "/cfm/normalProcess",
                        method: "post",
                        data: {
                            optype: "headorgoa_payOff",
                            params: {
                                ids: [row.id],
                                persist_version: [row.persist_version],
                                feed_back: value
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {
                            this.$message({
                                type: "error",
                                message: result.data.error_msg,
                                duration: 2000
                            });
                            return;
                        } else {
                            this.$message({
                                type: "success",
                                message: "作废成功",
                                duration: 2000
                            });
                            this.$emit("getTableData", this.routerMessage);
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }).catch(() => {

                });
            },
        },
        watch: {
            isPending: function (val, oldVal) {
                if (val) {
                    this.statusList = {
                        1: "已保存",
                        5: "审批拒绝"
                    }
                } else {
                    this.statusList = {
                        2: "已提交",
                        3: "审批中",
                        4: "审批通过",
                        6: "处理中",
                        7: "已成功",
                        8: "已失败",
                        9: "已作废",
                        10: "未完结",
                        11: "已完结",
                        12: "待处理"
                    }
                }
                var searchData = this.searchData;
                for (var k in searchData) {
                    if (k == "service_status") {
                        searchData[k] = [];
                    } else {
                        searchData[k] = "";
                    }
                }
            },
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data;
            }
        }
    }
</script>
