<style scoped lang="less" type="text/less">
    #allotDealCheck{
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
            height: 181px;
        }
        .table-content.height1 {
            height: 325px;
        }
        .childTable{
            height: 109px;
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
        .botton-pag-center{
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
<style lang="less">
    #allotDealCheck {
        .el-dialog__wrapper {
            .el-dialog__body {
                height: 400px;
                overflow-y: scroll;
            }
        }
        .el-table__expanded-cell[class*=cell] {
            padding: 20px;
        }
    }
</style>

<template>
    <div id="allotDealCheck">
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.pay_query_key" placeholder="请输入付款方账号" clearable></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4" v-if="isPending">
                        <el-form-item>
                            <el-input v-model="searchData.recv_query_key" placeholder="请输入收款方账号" clearable></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item>
                            <el-col :span="11">
                                <el-input v-model="searchData.min" placeholder="最小金额" clearable></el-input>
                            </el-col>
                            <el-col class="line" :span="1" style="text-align:center">-</el-col>
                            <el-col :span="11">
                                <el-input v-model="searchData.max" placeholder="最大金额" clearable></el-input>
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
        <section class="table-content" :class="[isPending ? '' : 'height1']">
            <el-table :data="tableList"
                      border
                      height="100%"
                      highlight-current-row
                      @row-click="getCurRowData"
                      @expand-change="getExpandData"
                      size="mini">
                <el-table-column type="expand" v-if="!isPending"> 
                    <template slot-scope="props" >
                        <section class="childTable">
                            <el-table :data="props.row.list"
                                    border
                                    height="100%"
                                    size="mini">
                                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="direction" label="收付方向" :show-overflow-tooltip="true" width="80"></el-table-column>
                                <el-table-column prop="opp_acc_no" label="对方账户号" :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="opp_acc_name" label="对方账户号名称" :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="amount" label="交易金额" :show-overflow-tooltip="true"
                                                :formatter="transitAmount"></el-table-column>
                                <el-table-column prop="summary" label="摘要" :show-overflow-tooltip="true" width="80"></el-table-column>
                                <el-table-column prop="trans_date" label="交易时间" :show-overflow-tooltip="true"></el-table-column>
                            </el-table>
                        </section>
                    </template>
                </el-table-column>
                <el-table-column prop="pay_account_no" label="付款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_account_bank" label="付款银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_no" label="收款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_name" label="收款方公司名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="payment_amount" label="金额" :show-overflow-tooltip="true"
                                :formatter="transitAmount"></el-table-column>
            </el-table>
        </section>
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
            <el-button type="warning" size="mini" @click="transactionConfirm" v-show="isPending">确认</el-button>
        </div>
        <!--主数据关联数据-->
        <section class="table-content" style="margin-top:40px" v-if="isPending">
            <el-table :data="childList"
                      border
                      height="100%"
                      @selection-change="handleSelectionChange"
                      size="mini">
                <el-table-column type="selection" width="38"></el-table-column>
                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="direction" label="收付方向" :show-overflow-tooltip="true" width="80"></el-table-column>
                <el-table-column prop="opp_acc_no" label="对方账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_name" label="对方账户号名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="amount" label="交易金额" :show-overflow-tooltip="true"
                                :formatter="transitAmount"></el-table-column>
                <el-table-column prop="summary" label="摘要" :show-overflow-tooltip="true" width="80"></el-table-column>
                <el-table-column prop="trans_date" label="交易时间" :show-overflow-tooltip="true"></el-table-column>
            </el-table>
        </section>
    </div>
</template>

<script>
    export default {
        name: "AllotDealCheck",
        created: function () {
            //设置当前optype信息
            var queryData = this.$router.currentRoute.query;

            this.$emit("transmitTitle", "交易核对");
            this.$emit("tableText", {
                leftTab: "未核对",
                rightTab: "已核对"
            });
            this.getRouterParamsChange(queryData.bizType);
            // this.$emit("getTableData", this.routerMessage);
        },
        mounted: function () {
        },
        props:["isPending","tableData"],
        data: function () {
            return {
                routerMessage: {
                    todo:{
                        optype: "dbttrad_billList",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    },
                    done:{
                        optype: "dbttrad_confirmbillList",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    }
                },
                searchData:{},
                tableList:[],
                childList: [],
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                selectionList: [],//选中的交易确认
                currenrRow: {},//当前选中行
                checkOptype: "",
                confirmOptype: "",
                validatedOptype: ""
            }
        },
        methods: {
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //根据条件查询数据
            queryData:function(){
                var searchData = this.searchData;
                for(var k in searchData){
                    if(this.isPending){
                        this.routerMessage.todo.params[k] = searchData[k];
                    }else{
                        this.routerMessage.done.params[k] = searchData[k];
                    }
                }
                this.$emit("getTableData", this.routerMessage);
            },
            //点击页数获取当前页数据
            getCurrentPage:function(currPage){
                if(this.isPending){
                    this.routerMessage.todo.params.page_num = currPage;
                }else{
                    this.routerMessage.done.params.page_num = currPage;
                }
                this.$emit("getTableData", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange:function(val){
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
            //获取未核对下第二个表格数据
            getCurRowData: function (row, event, column) {
                this.$axios({
                    url:"/cfm/normalProcess",
                    method:"post",
                    data:{
                        optype:this.checkOptype,
                        params:{
                            pay_account_no: row.pay_account_no,
                            recv_account_no: row.recv_account_no,
                            payment_amount: row.payment_amount
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
                        this.currenrRow = row;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //选择确认数据
            handleSelectionChange: function (val) {
                this.selectionList = val;
            },
            //交易确认
            transactionConfirm: function () {
                let trading_no = [];
                this.selectionList.forEach(element => {
                    trading_no.push(element.id);
                });
                if(trading_no.length){
                    var row = this.currenrRow;
                    this.$axios({
                        url:"/cfm/normalProcess",
                        method:"post",
                        data:{
                            optype:this.confirmOptype,
                            params:{
                                bill_no: row.id,
                                trading_no: trading_no,
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
                        } else {
                            this.$emit("getTableData", this.routerMessage);
                            this.childList = [];
                            this.$message({
                                type: "success",
                                message: "交易成功！",
                                duration: 2000
                            })
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }
            },
            //点击获取当前展开表格数据
            getExpandData: function (row, expandedRows) {
                if(!row.list){
                    this.$axios({
                        url:"/cfm/normalProcess",
                        method:"post",
                        data:{
                            optype:this.validatedOptype,
                            params:{
                                bill_no: row.id,
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
                            this.$set(row,'list',data);
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }
            },
            //路由变化而导致的参数变化
            getRouterParamsChange:function(type){
                if(type == '13'){
                    this.routerMessage.todo.optype = "pooltrad_billList";
                    this.routerMessage.done.optype = "pooltrad_confirmbillList";
                    this.checkOptype = "pooltrad_tradingList";
                    this.confirmOptype = "pooltrad_confirm";
                    this.validatedOptype = "pooltrad_confirmTradingList";
                }else if(type == '8'){
                    this.routerMessage.todo.optype = "dbttrad_billList";
                    this.routerMessage.done.optype = "dbttrad_confirmbillList";
                    this.checkOptype = "dbttrad_tradingList";
                    this.confirmOptype = "dbttrad_confirm";
                    this.validatedOptype = "dbttrad_confirmTradingList";
                }
                this.$emit("getTableData", this.routerMessage);
            }  
        },
        watch:{
            isPending:function(val,oldVal){
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
            },
            '$route' (to, from) {
                // 对路由变化作出响应...
                this.getRouterParamsChange(to.query.bizType);
            }
        }
    }
</script>

