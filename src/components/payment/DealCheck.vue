<style scoped lang="less" type="text/less">
    #dealCheck {
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

        /*数据展示区*/
        .table-content {
            height: 181px;
        }

        .validated-content {
            height: 75%;
            overflow-y: auto;
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;

            .el-button {
                float: right;
                margin-top: -30px;
            }
        }
        .botton-pag-center {
            top: 258px;
        }

        /*分割线*/
        .split-form {
            width: 100%;
            height: 26px;
            border-bottom: 1px solid #ccc;
            margin-bottom: 10px;
            h4 {
                margin: 0;
                float: left;
            }
        }
    }
</style>

<template>
    <div id="dealCheck">
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.pay_account_no" placeholder="请输入付款方账号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4" v-if="isPending">
                        <el-form-item>
                            <el-input v-model="searchData.recv_account_no" placeholder="请输入收款方账号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item>
                            <el-col :span="11">
                                <el-input v-model="searchData.min" placeholder="最小金额"></el-input>
                            </el-col>
                            <el-col class="line" :span="1" style="text-align:center">-</el-col>
                            <el-col :span="11">
                                <el-input v-model="searchData.max" placeholder="最大金额"></el-input>
                            </el-col>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section class="table-content" v-if="isPending">
            <el-table :data="tableList" border
                      height="100%" size="mini"
                      highlight-current-row
                      @current-change="getCheckData">
                <el-table-column prop="pay_account_no" label="付款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_account_bank" label="付款银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_no" label="收款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_name" label="收款方公司名称"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="payment_amount" label="金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
            </el-table>
        </section>
        <div class="validated-content" v-if="!isPending">
            <el-table :data="tableList" border
                      size="mini"
                      @expand-change="getValidated">
                <el-table-column type="expand">
                    <template slot-scope="props">
                        <el-table :data="validatedList" border
                                  size="mini">
                            <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column prop="acc_name" label="账户名称"
                                             :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column prop="bank_name" label="所属银行"
                                             :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column prop="direction" label="收付方向"
                                             :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column prop="opp_acc_no" label="对方账户号"
                                             :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column prop="opp_acc_name" label="对方账户名称"
                                             :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column prop="amount" label="交易金额" :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column prop="summary" label="摘要" :show-overflow-tooltip="true"></el-table-column>
                        </el-table>
                    </template>
                </el-table-column>
                <el-table-column prop="pay_account_no" label="付款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_account_bank" label="付款银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_no" label="收款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_name" label="收款方公司名称"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="payment_amount" label="金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
            </el-table>
        </div>
        <!--分页部分-->
        <div class="botton-pag" :class="{'botton-pag-center':isPending}">
            <el-pagination
                    background
                    layout="sizes, prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    :page-sizes="[7, 50, 100, 500]"
                    :pager-count="5"
                    :current-page="pagCurrent"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange">
            </el-pagination>
            <el-button type="warning" size="mini" @click="confirmCheck" v-show="isPending">确认</el-button>
        </div>
        <!--主数据关联数据-->
        <section class="table-content" style="margin-top:40px" v-if="isPending">
            <el-table :data="childList" border
                      height="100%" size="mini"
                      @selection-change="selectChange">
                <el-table-column type="selection" width="38"></el-table-column>
                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_name" label="所属银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="direction" label="收付方向" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_no" label="对方账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_name" label="对方账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="amount" label="交易金额" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="summary" label="摘要" :show-overflow-tooltip="true"></el-table-column>
            </el-table>
        </section>
    </div>
</template>

<script>
    export default {
        name: "DealCheck",
        created: function () {
            //设置当前optype信息
            var queryData = this.$router.currentRoute.query;
            if (queryData.bizType == "9") { //支付通
                this.routerMessage.todo.optype = "zft_checkbillList";
                this.routerMessage.done.optype = "zft_checkbillList";
                this.checkOptype = "zft_checkTradeList";
                this.confirmOptype = "zft_confirmCheck";
                this.validatedOptype = "zft_checkAlreadyTradeList";
            }

            //设置当前页基本信息
            this.$emit("transmitTitle", "交易核对");
            this.$emit("tableText", {
                leftTab: "未核对",
                rightTab: "已核对"
            });
            this.$emit("getTableData", this.routerMessage);
        },
        mounted: function () {
        },
        props: ["isPending", "tableData"],
        data: function () {
            return {
                routerMessage: {
                    todo: {
                        optype: "",
                        params: {
                            page_size: 7,
                            page_num: 1,
                            is_checked: 0
                        }
                    },
                    done: {
                        optype: "",
                        params: {
                            page_size: 7,
                            page_num: 1,
                            is_checked: 1
                        }
                    }
                },
                checkOptype:"",
                confirmOptype: "",
                validatedOptype: "",
                searchData: {
                    pay_account_no: "",
                    recv_account_no: "",
                    min: "",
                    max: ""
                },
                tableList: [],
                childList: [],
                validatedList: [],
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                currSelectData: {},
                selectData: [], //待管理数据选中数据
            }
        },
        methods: {
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
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
            //点击页数获取当前页数据
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
                    page_num: 1,
                    is_checked: 0
                };
                this.routerMessage.done.params = {
                    page_size: val,
                    page_num: 1,
                    is_checked: 1
                };
                this.$emit("getTableData", this.routerMessage);
            },
            //获取当前数据对应的核对数据
            getCheckData: function (val) {
                if (!val) {
                    return;
                }
                this.currSelectData = val;
                this.childList = [];
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: this.checkOptype,
                        params: {
                            id: val.id
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else {
                        var data = result.data.data;
                        this.childList = data;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //列表选择框改变后
            selectChange: function (val) {
                this.selectData = [];
                for (var i = 0; i < val.length; i++) {
                    this.selectData.push(val[i].id);
                }
            },
            //确认核对
            confirmCheck: function () {
                //校验是否选择核对数据
                if (this.selectData.length == 0) {
                    this.$message({
                        type: "warning",
                        message: "请选择核对数据",
                        duration: 2000
                    });
                    return;
                }
                var currSelectData = this.currSelectData;
                var params = {
                    bill_id: currSelectData.id,
                    persist_version: currSelectData.persist_version,
                    trade_id: this.selectData
                }

                this.$confirm('确认核对当前选择数据吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/normalProcess",
                        method: "post",
                        data: {
                            optype: this.confirmOptype,
                            params: params
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {
                            this.$message({
                                type: "error",
                                message: result.data.error_msg,
                                duration: 2000
                            })
                        } else {
                            var data = result.data.data;
                            this.$emit("getTableData", this.routerMessage);
                            this.childList = [];
                        }
                    }).catch(function (error) {
                        console.log(error);
                    });
                }).catch(() => {
                });
            },
            //获取已核对数据的关联数据
            getValidated: function (row) {
                this.validatedList = [];
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: this.validatedOptype,
                        params: {
                            id: row.id
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else {
                        var data = result.data.data;
                        this.validatedList = data;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
        },
        watch: {
            isPending: function (val, oldVal) {
                var searchData = this.searchData;
                for (var k in searchData) {
                    searchData[k] = "";
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

