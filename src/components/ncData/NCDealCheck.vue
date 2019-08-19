<style scoped lang="less" type="text/less">
    #dealCheck {
        .table-content {
            height: 100%;
        }

        /*分页部分*/
        .botton-pag {
            width: 100%;

            .el-button {
                float: right;
                margin-top: -30px;
            }

            .check-select {
                float: right;
                margin-top: -26px;
                margin-right: 70px;
            }
        }
    }
</style>

<template>
    <el-container id="dealCheck">
        <el-header>
            <div class="button-list-right" v-show="showDataNum">
                <el-select v-model="dataNum" filterable style="width:100%" size="mini"
                           @change="setParams">
                    <el-option value="1" :label="selectText.oneText"></el-option>
                </el-select>
            </div>
            <div class="search-setion">
                <el-form :inline="true" :model="searchData" size="mini">
                    <el-row>
                        <el-col :span="5">
                            <el-form-item>
                                <el-date-picker
                                        v-model="dateValue"
                                        type="daterange"
                                        range-separator="至"
                                        start-placeholder="开始日期"
                                        end-placeholder="结束日期"
                                        value-format="yyyy-MM-dd"
                                        clearable
                                        unlink-panels
                                        :picker-options="pickerOptions"
                                        @change="">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
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
                                    <el-input v-model="searchData.min" @blur="numberRule('min')" placeholder="最小金额"></el-input>
                                </el-col>
                                <el-col class="line" :span="1" style="text-align:center">-</el-col>
                                <el-col :span="11">
                                    <el-input v-model="searchData.max" @blur="numberRule('max')" placeholder="最大金额"></el-input>
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
            <div class="split-bar"></div>
        </el-header>
        <el-main>
            <section class="table-content" v-if="isPending">
                <el-table :data="tableList" border
                          height="100%" size="mini"
                          highlight-current-row
                          @current-change="getCheckData">
                    <el-table-column prop="pay_account_no" label="付款方账号"
                                     :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="pay_account_bank" label="付款银行"
                                     :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="recv_account_no" label="收款方账号"
                                     :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="recv_account_name" label="收款方公司名称"
                                     :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="item_type" label="支付类型" :show-overflow-tooltip="true"
                                     v-if="routerMessage.todo.optype == 'branchorgoacheck_checkbillList'"></el-table-column>
                    <el-table-column prop="payment_amount" label="金额" :show-overflow-tooltip="true"
                                     :formatter="transitAmount"></el-table-column>
                    <el-table-column v-if="curBizType==9||curBizType==12" prop="apply_on" label="日期"
                                     :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column v-else prop="create_on" label="日期" :show-overflow-tooltip="true"></el-table-column>
                </el-table>
            </section>
            <div class="table-content" v-if="!isPending">
                <el-table :data="tableList" border
                          height="100%"
                          size="mini" ref="validateTable"
                          @expand-change="getValidated">
                    <el-table-column type="expand">
                        <template slot-scope="props">
                            <el-table :data="validatedList" border
                                      size="mini">
                                <el-table-column prop="acc_no" label="账户号"
                                                 :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="acc_name" label="账户名称"
                                                 :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="bank_name" label="开户行"
                                                 :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="direction" label="收付方向"
                                                 :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="opp_acc_no" label="对方账户号"
                                                 :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="opp_acc_name" label="对方账户名称"
                                                 :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="amount" label="交易金额"  :formatter="transitAmount"
                                                 :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="summary" label="摘要"
                                                 :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="trans_date" label="交易时间"
                                                 :show-overflow-tooltip="true"></el-table-column>
                            </el-table>
                        </template>
                    </el-table-column>
                    <el-table-column prop="pay_account_no" label="付款方账号"
                                     :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="pay_account_bank" label="付款银行"
                                     :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="recv_account_no" label="收款方账号"
                                     :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="recv_account_name" label="收款方公司名称"
                                     :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="payment_amount" label="金额" :show-overflow-tooltip="true"
                                     :formatter="transitAmount"></el-table-column>
                    <el-table-column v-if="curBizType==9||curBizType==12" prop="apply_on" label="日期"
                                     :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column v-else prop="create_on" label="日期" :show-overflow-tooltip="true"></el-table-column>
                </el-table>
            </div>
        </el-main>
        <el-footer>
            <div class="botton-pag">
                <el-pagination
                        background
                        layout="sizes, prev, pager, next, jumper"
                        :page-size="pagSize"
                        :total="pagTotal"
                        :page-sizes="[5, 7, 50, 100, 500]"
                        :pager-count="5"
                        :current-page="pagCurrent"
                        @current-change="getCurrentPage"
                        @size-change="sizeChange">
                </el-pagination>
                <el-button type="warning" size="mini" @click="confirmCheck" v-show="isPending">确认</el-button>
                <div class="check-select" v-show="isPending">
                    <el-checkbox v-model="dateCheck" @change="getCheckData(currentData)">日期校验</el-checkbox>
                    <el-checkbox v-model="recvCheck" @change="getCheckData(currentData)">收款方校验</el-checkbox>
                </div>
            </div>
            <section style="margin-top:14px;height:180px" v-if="isPending">
                <el-table :data="childList" border
                          height="100%" size="mini"
                          @selection-change="selectChange">
                    <el-table-column type="selection" width="40"></el-table-column>
                    <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="bank_name" label="开户行" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="direction" label="收付方向" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="opp_acc_no" label="对方账户号" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="opp_acc_name" label="对方账户名称" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="amount" label="交易金额" :formatter="transitAmount" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="summary" label="摘要" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="trans_date" label="交易时间" :show-overflow-tooltip="true"></el-table-column>
                </el-table>
            </section>
        </el-footer>
    </el-container>
</template>

<script>
    export default {
        name: "NCDealCheck",
        beforeRouteUpdate(to, from, next) {
            this.setOptypes();
            this.dateCheck = true;
            this.recvCheck = true;
            this.childList = [];
            this.dataNum = "1";
            next();
        },
        created: function () {
            this.setOptypes();
            //设置当前页基本信息
            this.$emit("transmitTitle", "交易核对");
            this.$emit("tableText", {
                leftTab: "未核对",
                rightTab: "已核对"
            });
        },
        mounted: function () {
        },
        props: ["isPending", "tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    todo: {
                        optype: "",
                        params: {
                            page_size: 5,
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
                checkOptype: "",
                confirmOptype: "",
                validatedOptype: "",
                dateValue: "", //时间选择
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
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
                dataNum: "1",
                showDataNum: true,
                selectText: {
                },
                curBizType: "",
                dateCheck: true,
                recvCheck: true,
                currentData: ""
            }
        },
        methods: {
            //设置页面相关optype
            setOptypes: function () {

                    this.routerMessage.todo.optype = "headorgnccheck_checkbillList";
                    this.routerMessage.done.optype = "headorgnccheck_checkbillList";
                    this.checkOptype = "headorgnccheck_checkTradeList";
                    this.confirmOptype = "headorgnccheck_confirmCheck";
                    this.validatedOptype = "headorgnccheck_checkAlreadyTradeList";

                    //设置下拉框文字
                    this.selectText.oneText = "总公司付款";
                this.$emit("getTableData", this.routerMessage);
            },
            numberRule: function (target) {
                var targetVal = this.searchData[target];
                if (targetVal && (typeof targetVal != "number" || targetVal < 0)) {
                    this.$message({
                        type: "warning",
                        message: "请输入大于零的数字",
                        duration: 2000
                    });
                    this.searchData[target] = "";
                }
            },
            //选择操作类型后调整接口
            setParams: function (val) {
                var queryData = this.$router.currentRoute.query;
                if (val == "1") {
                    this.setOptypes();
                }
                this.dateCheck = true;
                this.recvCheck = true;
                this.childList = [];
            },
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                searchData.start_date = this.dateValue ? this.dateValue[0] : "";
                searchData.end_date = this.dateValue ? this.dateValue[1] : "";
                for (var k in searchData) {
                    if (this.isPending) {
                        this.routerMessage.todo.params[k] = searchData[k];
                        this.routerMessage.todo.params.page_num = 1;
                    } else {
                        this.routerMessage.done.params[k] = searchData[k];
                        this.routerMessage.done.params.page_num = 1;
                    }
                }
                this.$emit("getTableData", this.routerMessage);
                this.childList = [];
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
                this.routerMessage.todo.params.page_size = val;
                this.routerMessage.params.page_num = 1;
                this.routerMessage.todo.params.is_checked = 0;

                this.routerMessage.done.params.page_size = val;
                this.routerMessage.done.params.page_num = 1;
                this.routerMessage.done.params.is_checked = 1;

                this.$emit("getTableData", this.routerMessage);
            },
            //获取当前数据对应的核对数据
            getCheckData: function (val) {
                if (this.tableList.length === 0 || !val) {
                    this.currentData = "";
                    return;
                }
                this.currentData = val;
                this.currSelectData = val;
                this.childList = [];
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: this.checkOptype,
                        params: {
                            id: val.id,
                            date_validate: this.dateCheck ? 1 : 0,
                            recv_validate: this.recvCheck ? 1 : 0,
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
                        url: this.queryUrl + "normalProcess",
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
                            this.$message({
                                type: "success",
                                message: "确认成功",
                                duration: 2000
                            })
                        }
                    }).catch(function (error) {
                        console.log(error);
                    });
                }).catch(() => {
                });
            },
            //获取已核对数据的关联数据
            getValidated: function (row, expandedRows) {
                /*
                * 1、展开某一行
                *   1.1 只有一行展开(expandedRows.length == 1 && row == expandedRows[0])
                *       获取当前行数据
                *   2.1 展开行为第二个(expandedRows.length > 1)
                *       2.1.1 关闭之前展开行
                *       2.1.2 获取当前行数据
                * 2、关闭某一行
                *   2.1 手动关闭某一行(expandedRows.length ==0)
                *   2.2 打开第二行导致原展开行关闭(expandedRows.length == 1 && row != expandedRows[0])
                * */
                if (expandedRows.length == 0 || (expandedRows.length == 1 && row != expandedRows[0])) { //关闭行
                    return;
                }
                if (expandedRows.length == 2) { //展开第二行
                    for (var i = 0; i < expandedRows.length; i++) {
                        var item = expandedRows[i];
                        if (row != item) {
                            this.$refs.validateTable.toggleRowExpansion(item, false);
                            break;
                        }
                    }
                }

                this.validatedList = [];
                this.$axios({
                    url: this.queryUrl + "normalProcess",
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

