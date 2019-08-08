<style scoped lang="less" type="text/less">
    #refundInquiry {
    }
</style>
<style lang="less">
    #refundInquiry {
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
    <el-container id="operation">
        <el-header>
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
            <div class="split-bar"></div>
        </el-header>
        <el-main>
            <el-table :data="tableList"
                      border
                      height="100%"
                      highlight-current-row
                      @expand-change="getExpandData"
                      :expand-row-keys="expandKeys"
                      :row-key="getRowKeys"
                      size="mini">
                <el-table-column type="expand" prop="list">
                    <template slot-scope="scope">
                        <section class="childTable">
                            <el-table :data="scope.row.list"
                                      border
                                      size="mini">
                                <el-table-column prop="pay_account_no" label="付款方账号" :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="pay_account_bank" label="付款银行" :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="recv_account_no" label="收款方账号" :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="recv_account_name" label="收款方公司名称" :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="payment_amount" label="金额" :show-overflow-tooltip="true"
                                                 :formatter="transitAmount"></el-table-column>
                                <el-table-column prop="create_on" label="日期" :show-overflow-tooltip="true"></el-table-column>
                            </el-table>
                        </section>
                    </template>
                </el-table-column>
                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_name" label="开户行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="direction" label="收付方向" :show-overflow-tooltip="true" width="80"
                                 :formatter="transitionStatus"></el-table-column>
                <el-table-column prop="opp_acc_no" label="对方账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_name" label="对方账户号名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="amount" label="交易金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="summary" label="摘要" :show-overflow-tooltip="true" width="80"></el-table-column>
                <el-table-column prop="trans_date" label="交易时间" :show-overflow-tooltip="true"></el-table-column>
            </el-table>
        </el-main>
        <el-footer>
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
        </el-footer>
    </el-container>
</template>

<script>
    export default {
        name: "RefundInquiry",
        created: function () {
            this.$emit("transmitTitle", "退票查询");
            this.$emit("getCommTable", this.routerMessage);

        },
        mounted: function () {
            //账户模式
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.PayOrRecv) {
                this.payOrRecv = constants.PayOrRecv;
            }
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "refundview_alreadyRefundTradeList",
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
                expandKeys:[],//存放展开的id
                // 获取row的key值
                getRowKeys(row) {
                    return row.id;
                },
                payOrRecv: {},//收付方向
            }
        },
        methods: {
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //展示格式转换
            transitionStatus: function (row, column, cellValue, index) {
                if(column.property === "direction"){//转换收付方向
                    if (this.payOrRecv) {
                        return this.payOrRecv[cellValue];
                    }
                }
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
                this.expandKeys = [];
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
            //点击获取当前展开表格数据
            getExpandData: function (row, expandedRows) {
                var isPush = true;
                var esList = this.expandKeys;
                var esLen = esList.length;
                for(let i =0 ;i<esLen;i++){
                    if(esList[i] == row.id){
                        isPush = false;
                        break;
                    }
                }
                if(isPush){
                    this.expandKeys = [];
                    this.expandKeys.push(row.id);
                }else{
                    let index = esList.indexOf(row.id);
                    esList.splice(index,1);
                }
                if(!row.list){
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method:"post",
                        data:{
                            optype: "refundview_billList",
                            params:{
                                trade_id: row.trade_id,
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
                            var arr = [];
                            arr.push(data);
                            this.$set(row,'list',arr);
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }
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





