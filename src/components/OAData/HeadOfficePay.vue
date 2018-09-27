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
                                       @click="lookBill(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="isPending">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editBill(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="撤回" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="!isPending">
                            <el-button size="mini" class="withdraw"
                                       @click="withdrawBill(scope.row)"></el-button>
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
    </div>
</template>

<script>
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
