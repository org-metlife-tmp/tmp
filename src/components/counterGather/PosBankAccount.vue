<style scoped lang="less" type="text/less">
    #posBankAccount {
        /*分页部分*/
        .botton-pag {
            width: 100%;
            height: 8%;
            margin-top: 6px;

            .el-button {
                float: right;
                margin-top: -30px;
            }
        }

        /*分割线*/
        .split-bar{
            width: 100%;
            margin: 0 0 10px 0;
        }
    }
</style>

<template>
    <el-container id="posBankAccount">
        <el-header>

        </el-header>
        <el-main>
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
                                        size="mini" clearable
                                        unlink-panels
                                        :picker-options="pickerOptions">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.terminal_no" clearable placeholder="请输入终端机编号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.serial_number" clearable placeholder="请输入流水号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.trade_type" clearable placeholder="请输入交易类型"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="5">
                            <el-form-item>
                                <el-col :span="11">
                                    <el-input v-model="searchData.min" clearable placeholder="最小金额"></el-input>
                                </el-col>
                                <el-col class="line" :span="1" style="text-align:center">-</el-col>
                                <el-col :span="11">
                                    <el-input v-model="searchData.max" clearable placeholder="最大金额"></el-input>
                                </el-col>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                            </el-form-item>
                        </el-col>

                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.card_no" clearable placeholder="请输入卡号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.card_issue_bank" clearable placeholder="请输入发卡行"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="14">
                            <el-form-item style="margin-bottom:0px">
                                <el-checkbox-group v-model="searchData.is_checked">
                                    <el-checkbox :label="0" name="未核对">未核对</el-checkbox>
                                    <el-checkbox :label="1" name="已核对">已核对</el-checkbox>
                                </el-checkbox-group>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="clearData" size="mini">清空筛选</el-button>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </div>
            <el-table :data="tableList" ref="tableRef"
                      @select="selectChange"
                      @select-all="selectChange"
                      height="200px" border size="mini">
                <el-table-column type="selection" width="40" ></el-table-column>
                <el-table-column prop="liquidation_date" label="清算日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="trade_date" label="交易日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="trade_time" label="交易时间" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="terminal_no" label="终端机编号" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="trade_amount" label="交易金额" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="procedures_amount" label="手续费" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="entry_account_amount" label="入账金额" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="system_track_number" label="系统跟踪号" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="retrieval_reference_number" label="检索参考号" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="serial_number" label="流水号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="trade_type" label="交易类型" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="card_no" label="卡号"  width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="card_type" label="卡类型" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="card_issue_bank" label="发卡行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="no_identity_mark" label="非接标识" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="import_date" label="导入日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="trade_check_service_number" label="对账流水号" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
            </el-table>
            <div class="botton-pag">
                <el-pagination
                        background
                        layout="sizes, prev, pager, next, jumper"
                        :page-size="pagSize"
                        :total="pagTotal"
                        :page-sizes="[20, 50, 100, 500]"
                        :pager-count="5"
                        @current-change="getCurrentPage"
                        @size-change="sizeChange"
                        :current-page="pagCurrent">
                </el-pagination>
                <el-button type="warning" size="mini" @click="affirm">确认对账</el-button>
            </div>
            <div class="split-bar"></div>
            <div class="search-setion">
                <el-form :inline="true" :model="childSearch" size="mini">
                    <el-row>
                        <el-col :span="5">
                            <el-form-item>
                                <el-date-picker
                                        v-model="ChildDateValue"
                                        type="daterange"
                                        range-separator="至"
                                        start-placeholder="开始日期"
                                        end-placeholder="结束日期"
                                        value-format="yyyy-MM-dd"
                                        size="mini" clearable
                                        unlink-panels
                                        :picker-options="pickerOptions">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="childSearch.summary" clearable placeholder="请输入摘要"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="5">
                            <el-form-item>
                                <el-col :span="11">
                                    <el-input v-model="childSearch.min" clearable placeholder="最小金额"></el-input>
                                </el-col>
                                <el-col class="line" :span="1" style="text-align:center">-</el-col>
                                <el-col :span="11">
                                    <el-input v-model="childSearch.max" clearable placeholder="最大金额"></el-input>
                                </el-col>
                            </el-form-item>
                        </el-col>
                        <el-col :span="5">
                            <el-form-item style="margin-bottom:0px">
                                <el-checkbox-group v-model="childSearch.is_checked">
                                    <el-checkbox :label="0" name="未核对">未核对</el-checkbox>
                                    <el-checkbox :label="1" name="已核对">已核对</el-checkbox>
                                </el-checkbox-group>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="clearChildData" size="mini">清空筛选</el-button>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="queryChildData" size="mini"
                                           :disabled="tableList.length == 0">搜索</el-button>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </div>
            <el-table :data="childList" border
                      @selection-change="childChange"
                      @select-all="childChange"
                      height="200px" size="mini">
                <el-table-column type="selection" width="40"></el-table-column>
                <el-table-column prop="trans_date" label="交易日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bankcode" label="BankCode" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_no" label="银行账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_name" label="开户行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="direction" label="收付方向" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_no" label="对方银行账号" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_name" label="对方账户名称" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_bank" label="对方开户行" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="amount" label="交易金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="summary" label="摘要" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="is_checked" label="状态" :show-overflow-tooltip="true"
                                 :formatter="transitStatus"></el-table-column>
                <el-table-column prop="check_service_number" label="对账流水号" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="check_user_name" label="操作人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="check_date" label="操作日期" :show-overflow-tooltip="true"></el-table-column>
            </el-table>
            <div class="allData">
                <div class="btn-right">
                    <span>交易金额(收)：</span>
                    <span v-text="childTotalData.recvAmount" class="numText"></span>
                    <span>交易金额(付)：</span>
                    <span v-text="childTotalData.payAmount" class="numText"></span>
                    <span>合计金额：</span>
                    <span v-show="!isZero && !isPay">收</span>
                    <span v-show="!isZero && isPay">付</span>
                    <span v-show="isZero">-</span>
                    <span v-text="childTotalData.totalAmount" class="numText"></span>
                </div>
            </div>
        </el-main>
        <el-footer>

        </el-footer>
    </el-container>
</template>

<script>
    export default {
        name: "PosBankAccount",
        created: function () {
            this.$emit("transmitTitle", "POS明细与银行流水对账");
            this.$emit("getCommTable", this.routerMessage);
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "recvcounterpostranscheck_list",
                    params: {
                        page_size: 20,
                        page_num: 1
                    }
                },
                searchData: { //搜索条件
                    terminal_no: "",
                    serial_number: "",
                    trade_type: "",
                    min: "",
                    max: "",
                    card_no: "",
                    card_issue_bank: "",
                    is_checked: []
                },
                childSearch: {
                    min: "",
                    max: "",
                    summary: "",
                    is_checked: [],
                },
                dateValue: "", //时间控件
                ChildDateValue: "",
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
                tableList: [], //列表数据
                childList: [],
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                batchidList: [], //选中数据
                versionList: [],
                tradingList: [],
                childTotalData: {
                    payAmount: "",
                    recvAmount: "",
                    totalAmount: ""
                },
                isPay: false,
                isZero: true
            }
        },
        methods: {
            //清空搜索条件
            clearData: function(){
                var searchData = this.searchData;
                for (var k in searchData) {
                    if(k == "is_checked"){
                        searchData[k] = [];
                    }else{
                        searchData[k] = "";
                    }

                }
            },
            clearChildData: function(){
                var searchData = this.childSearch;
                for (var k in searchData) {
                    if(k == "is_checked"){
                        searchData[k] = [];
                    }else{
                        searchData[k] = "";
                    }

                }
            },
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                for (var k in searchData) {
                    this.routerMessage.params[k] = searchData[k];
                }
                var val = this.dateValue;
                this.routerMessage.params.start_date = val ? val[0] : "";
                this.routerMessage.params.end_date = val ? val[1] : "";
                this.routerMessage.params.page_num = 1;
                this.$emit("getCommTable", this.routerMessage);
            },
            //查询交易流水
            queryChildData: function() {
                var searchData = this.childSearch;
                var params = {};
                for (var k in searchData) {
                    params[k] = searchData[k];
                }
                var val = this.ChildDateValue;
                params.start_date = val ? val[0] : "";
                params.end_date = val ? val[1] : "";


                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "recvcounterpostranscheck_tradingList",
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
                        this.childList = data;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //换页后获取数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getCommTable", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.params.page_size = val;
                this.routerMessage.params.page_num = 1;
                this.$emit("getCommTable", this.routerMessage);
            },
            //展示格式转换-状态
            transitStatus: function (row, column, cellValue, index) {
                return cellValue == 0 ? "未核对" : "已核对";
            },
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //列表选择框改变后
            selectChange: function (val,row) {
                //计算汇总数据
                this.batchidList = [];
                this.versionList = [];
                for (var i = 0; i < val.length; i++) {
                    var item = val[i];
                    this.batchidList.push(item.id);
                    this.versionList.push(item.persist_version);
                }
            },
            childChange: function (val) {
                this.tradingList = [];
                var payAmount = 0;
                var recvAmount = 0;
                for (var i = 0; i < val.length; i++) {
                    var item = val[i];
                    if(item.direction == "收"){
                        recvAmount += item.amount;
                    }else{
                        payAmount += item.amount;
                    }
                    this.tradingList.push(item.id);
                }

                this.childTotalData.payAmount = this.$common.transitSeparator(payAmount);
                this.childTotalData.recvAmount = this.$common.transitSeparator(recvAmount);
                if(payAmount > recvAmount){
                    this.isPay = true;
                    this.isZero = false;
                    this.childTotalData.totalAmount = this.$common.transitSeparator(payAmount - recvAmount);
                }else if(payAmount == recvAmount){
                    this.isPay = false;
                    this.isZero = true;
                    this.childTotalData.totalAmount = "";
                }else{
                    this.isPay = false;
                    this.isZero = false;
                    this.childTotalData.totalAmount = this.$common.transitSeparator(recvAmount - payAmount);
                }
            },
            //对账确认
            affirm: function () {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "recvcounterpostranscheck_confirm",
                        params: {
                            batchid: this.batchidList,
                            persist_version: this.versionList,
                            trading_no: this.tradingList,
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        });
                    } else {
                        var data = result.data.data;
                        this.$message({
                            type: "success",
                            message: "确认成功",
                            duration: 2000
                        });
                        this.$emit("getCommTable", this.routerMessage);
                        this.queryChildData();
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
        },
        watch: {
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.tableList = val.data;
                this.pagCurrent = val.page_num;
            }
        }
    }
</script>
