<style scoped lang="less" type="text/less">
    #refundDispose {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion {
            text-align: left;

            /*时间控件*/
            .el-date-editor {
                width: 96%;
            }
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
            top: 258px;

            .el-button {
                float: right;
                margin-top: 2px;
            }

            .el-select{
                float: left;
                margin-top: 2px;
                width: 160px;
            }
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
    <div id="refundDispose">
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="5">
                        <el-date-picker
                                v-model="dateValue"
                                type="daterange"
                                range-separator="至"
                                start-placeholder="开始日期"
                                end-placeholder="结束日期"
                                value-format="yyyy-MM-dd"
                                size="mini" clearable
                                unlink-panels
                                :picker-options="pickerOptions"
                                @change="">
                        </el-date-picker>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.acc_no" clearable placeholder="请输入账户号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
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
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList" border
                      height="100%" size="mini"
                      highlight-current-row
                      @current-change="getCheckData">
                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="direction" label="收付方向" :show-overflow-tooltip="true"
                                 :formatter="transitDirection"></el-table-column>
                <el-table-column prop="opp_acc_no" label="对方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_name" label="对方账户名" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_bank" label="对方银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="trans_date" label="交易日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="trans_time" label="交易时间" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="summary" label="摘要" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="amount" label="金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
            </el-table>
        </section>
        <!--分页部分-->
        <div class="botton-pag botton-pag-center">
            <el-select v-model="bizType" placeholder="请选择业务类型"
                       filterable size="mini"
                       @change="getSelectData">
                <el-option v-for="(bizType,key) in bizTypeList"
                           :key="key"
                           :label="bizType"
                           :value="key">
                </el-option>
            </el-select>
            <el-button type="warning" size="mini" @click="confirmCheck">确认退票</el-button>
            <div style="float:right;display:inline-block;width:60px;height:12px"></div>
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
        </div>
        <!--主数据关联数据-->
        <section class="table-content" style="margin-top:40px">
            <el-table :data="childList" border
                      highlight-current-row
                      height="100%" size="mini"
                      @current-change="selectData = $event || {}">
                <el-table-column prop="service_serial_number" label="单据编号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_account_no" label="付款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_account_name" label="付款方账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_account_bank" label="付款方开户行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_no" label="收款方账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_name" label="收款方账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_bank" label="收款方开户行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="payment_summary" label="摘要" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="payment_amount" label="金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
            </el-table>
        </section>
    </div>
</template>

<script>
    export default {
        name: "RefundDispose",
        created: function () {
            //设置当前页基本信息
            this.$emit("transmitTitle", "主动退票");
            this.$emit("getCommTable", this.routerMessage);
        },
        mounted: function () {
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "refund_tradeList",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                dateValue: new Date(), //时间选择
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
                searchData: {
                    acc_no: "",
                    min: "",
                    max: "",
                    start_date: "",
                    end_date: ""
                },
                currSelectData: {}, //当前选中的数据
                childList: [], //底部数据
                bizTypeList: {
                    "8": "调拨通-内部调拨",
                    "10": "调拨通-批量调拨",
                    "9": "支付通-单笔支付",
                    "11": "支付通-批量支付",
                    "12": "归集通-直联归集",
                    "19": "归集通-非直联归集",
                    "13": "资金下拨",
                    "15": "收款通",
                    "14": "广银联备付金",
                    "20": "OA总公司付款",
                    "21": "OA分公司付款"
                }, //业务类型
                bizType: "",
                selectData: {}, //底部数据选中数据
            }
        },
        methods: {
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //展示格式转换-收付方向
            transitDirection: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                return constants.PayOrRecv[cellValue];
            },
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                searchData.start_date = this.dateValue ? this.dateValue[0] : "";
                searchData.end_date = this.dateValue ? this.dateValue[1] : "";
                for (var k in searchData) {
                    this.routerMessage.params[k] = searchData[k];
                }
                this.routerMessage.params.page_num = 1;
                this.$emit("getCommTable", this.routerMessage);
            },
            //点击页数获取当前页数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getCommTable", this.routerMessage);
                this.childList = [];
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.params.page_num = 1;
                this.routerMessage.params.page_size = val;
                this.$emit("getCommTable", this.routerMessage);
                this.childList = [];
            },
            //获取当前数据对应的单据数据
            getCheckData: function (val) {
                if (!val) {
                    this.currSelectData = {};
                    return;
                }
                this.currSelectData = val;
                if(!this.bizType){
                    this.$message({
                        type: "warning",
                        message: "请选择业务类型",
                        duration: 2000
                    });
                    return;
                }
                this.childList = [];
                this.queryBillData();
            },
            //获取当前业务类型对应的单据数据
            getSelectData: function(val){
                if(!val){
                    return;
                }
                if(JSON.stringify(this.currSelectData) == "{}"){
                    this.$message({
                        type: "warning",
                        message: "请选择数据",
                        duration: 2000
                    });
                    return;
                };
                this.childList = [];
                this.queryBillData();
            },
            //查询单据数据
            queryBillData: function(){
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "refund_billList",
                        params: {
                            page_num: 1,
                            page_size: 1000,
                            biz_type: this.bizType,
                            trade_id: this.currSelectData.trade_id
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

            //确认退票
            confirmCheck: function () {
                var currSelectData = this.currSelectData;
                var selectData = this.selectData;

                //校验是否选择退票数据
                if(JSON.stringify(currSelectData) == "{}" || JSON.stringify(selectData) == "{}"){
                    this.$message({
                        type: "warning",
                        message: "请选择要退票的数据",
                        duration: 2000
                    });
                    return;
                }
                var params = {
                    biz_type: this.bizType,
                    trade_id: currSelectData.trade_id,
                    bill_id: selectData.bill_id,
                    bill_persist_version: selectData.bill_persist_version,
                    detail_id: selectData.detail_id,
                    detail_persist_version: selectData.detail_persist_version
                }

                this.$confirm('确认对当前数据进行退票处理吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "refund_confirm",
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
                            this.$message({
                                type: "success",
                                message: "退票成功",
                                duration: 2000
                            });
                            this.$emit("getCommTable", this.routerMessage);
                            this.childList = [];
                        }
                    }).catch(function (error) {
                        console.log(error);
                    });
                }).catch(() => {
                });
            }
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


