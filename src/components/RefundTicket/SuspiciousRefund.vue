<style scoped lang="less" type="text/less">
    #suspiciousRefund {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion {
            text-align: left;

            .line {
                text-align: center;
            }

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

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

        /*查看弹框*/
        .dialog-talbe {
            width: 100%;
            height: 180px;

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
                overflow: hidden;
                text-overflow: ellipsis;
                white-space: nowrap;
            }

            .table-two-row {
                width: 88%;
                margin-left: -3px;
                border-left: none;
            }
        }

        .serial-number {
            color: #ccc;
            margin-bottom: 2px;
            margin-top: -15px;
        }
    }
</style>
<style lang="less">
    #suspiciousRefund {
        .el-form--inline .el-form-item {
            width: calc(100% - 10px);
            width: -moz-calc(100% - 10px);
            width: -webkit-calc(100% - 10px);
        }
        .el-form--inline .el-form-item__content {
            width: 100%;
        }
    }
</style>

<template>
    <div id="suspiciousRefund">
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
            <el-table :data="tableList"
                      border size="mini">
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
                <el-table-column
                        label="操作" width="80"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="确认正常" placement="bottom" effect="light" :enterable="false"
                                    :open-delay="500" v-show="scope.row">
                            <el-button type="success" icon="el-icon-check" size="mini"
                                       @click="notarizeNormal(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="确认为退票" placement="bottom" effect="light" :enterable="false"
                                    :open-delay="500" v-show="scope.row">
                            <el-button type="danger" icon="el-icon-back" size="mini"
                                       @click="notarizeRefund(scope.row)"></el-button>
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
        <el-dialog title="可疑退票信息"
                   :visible.sync="dialogVisible"
                   width="900px" top="76px"
                   :close-on-click-modal="false">
            <el-select v-model="bizType" placeholder="请选择业务类型"
                       filterable size="mini"
                       @change="getSelectData">
                <el-option v-for="(bizType,key) in bizTypeList"
                           :key="key"
                           :label="bizType"
                           :value="key">
                </el-option>
            </el-select>
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
            <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" @click="conrifmRefund">确定退票</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "SuspiciousRefund",
        created: function () {
            this.$emit("transmitTitle", "可疑退票");
            this.$emit("getCommTable", this.routerMessage);
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "doubtfulrefund_tradeList",
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
                dialogVisible: false, //弹框数据
                currSelectData: {}, //当前选中的数据
                childList: [], //可疑退票数据
                selectData: {}, //可疑退票数据选中项
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
            //换页后获取数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getCommTable", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.params.page_num = 1;
                this.routerMessage.params.page_size = val;
                this.$emit("getCommTable", this.routerMessage);
            },
            //确认正常
            notarizeNormal: function (row) {
                this.$confirm('确认当前数据为正常吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "doubtfulrefund_normalTrade",
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
                            this.$message({
                                type: "success",
                                message: "已确认为正常数据",
                                duration: 2000
                            });
                            var rows = this.tableList;
                            var index = this.tableList.indexOf(row);
                            if (this.pagCurrent < (this.pagTotal / this.pagSize)) { //存在下一页
                                this.$emit('getCommTable', this.routerMessage);
                            } else {
                                if (rows.length == "1" && (this.routerMessage.params.page_num != 1)) { //是当前页最后一条
                                    this.routerMessage.params.page_num--;
                                    this.$emit('getCommTable', this.routerMessage);
                                } else {
                                    rows.splice(index, 1);
                                    this.pagTotal--;
                                }
                            }
                        }
                    }).catch(function (error) {
                        console.log(error);
                    });
                }).catch(() => {
                });
            },
            //确认为退票
            notarizeRefund: function (row) {
                this.currSelectData = row;
                this.childList = [];
                this.bizType = "";
                this.dialogVisible = true;
            },
            //获取当前业务类型对应的可疑数据
            getSelectData: function (val) {
                if (!val) {
                    return;
                }
                var currSelectData = this.currSelectData;
                this.childList = [];
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "doubtfulrefund_billList",
                        params: {
                            page_num: 1,
                            page_size: 1000,
                            biz_type: val,
                            id: currSelectData.id
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
                });
            },
            //确认退票
            conrifmRefund: function () {
                var selectData = this.selectData;
                //校验是否选择退票数据
                if (JSON.stringify(selectData) == "{}") {
                    this.$message({
                        type: "warning",
                        message: "请选择要退票的数据",
                        duration: 2000
                    });
                    return;
                }
                var params = {
                    biz_type: this.bizType,
                    id: this.currSelectData.id,
                    bill_id: selectData.bill_id,
                    bill_persist_version: selectData.bill_persist_version,
                    detail_id: selectData.detail_id,
                    detail_persist_version: selectData.detail_persist_version
                }

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "doubtfulrefund_confirm",
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
                        this.dialogVisible = false;
                        this.$emit("getCommTable", this.routerMessage);
                    }
                }).catch(function (error) {
                    console.log(error);
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





