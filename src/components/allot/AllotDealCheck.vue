<style scoped lang="less" type="text/less">
    #allotDealCheck{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion{
            text-align: left;
            .line {
                text-align: center;
            }
            /*时间控件*/
            .el-date-editor {
                width: 100%;
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
        .table-content{
            height: 181px;
        }
        .table-content.height1 {
            height: 325px;
        }
        .childTable{
            // min-height: 96px;
            // max-height: 192px;
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

        /*顶部按钮*/
        .button-list-right {
            position: absolute;
            top: -56px;
            right: -21px;
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
        .el-form--inline .el-form-item{
            width: calc(100% - 10px);
            width: -moz-calc(100% - 10px);
            width: -webkit-calc(100% - 10px);
        }
        .el-form--inline .el-form-item__content{
            width: 100%;
        }
    }
</style>

<template>
    <div id="allotDealCheck">
        <!--顶部按钮-->
        <div class="button-list-right" v-show="isAllot">
            <el-select v-model="searchData.payment_type" placeholder="请选择调拨类型" size="mini"
                       @change="queryByPayType">
                <el-option v-for="(name,k) in paymentTypeList"
                           :key="k"
                           :label="name"
                           :value="k">
                </el-option>
            </el-select>
        </div>
        <!--搜索区-->
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
                                    :picker-options="pickerOptions"
                                    @change="">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
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
                      :expand-row-keys="expandKeys"
                      :row-key="getRowKeys"
                      size="mini">
                <el-table-column type="expand" prop="list" v-if="!isPending">
                    <template slot-scope="scope">
                        <section class="childTable">
                            <el-table :data="scope.row.list"
                                    border
                                    size="mini">
                                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                                <el-table-column prop="bank_name" label="开户行" :show-overflow-tooltip="true"></el-table-column>
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
                <el-table-column prop="create_on" label="日期" :show-overflow-tooltip="true"></el-table-column>
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
                <el-table-column type="selection" width="40"></el-table-column>
                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_name" label="开户行" :show-overflow-tooltip="true"></el-table-column>
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
            this.paymentTypeList = {
                8: '内部调拨',
                10: '批量调拨'
            }
        },
        props:["isPending","tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
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
                searchData:{
                    payment_type: "8",
                },
                tableList:[],
                childList: [],
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                selectionList: [],//选中的交易确认
                currenrRow: {},//当前选中行
                checkOptype: "",
                confirmOptype: "",
                validatedOptype: "",
                dateValue: "", //时间选择
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
                paymentTypeList:{},//调拨类型
                isAllot: false,//是否是调拨
                expandKeys:[],//存放展开的id
                // 获取row的key值
                getRowKeys(row) {
                    return row.id;
                },
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
                searchData.start_date = this.dateValue ? this.dateValue[0] : "";
                searchData.end_date = this.dateValue ? this.dateValue[1] : "";
                for(var k in searchData){
                    if(this.isPending){
                        this.routerMessage.todo.params[k] = searchData[k];
                    }else{
                        this.routerMessage.done.params[k] = searchData[k];
                    }
                }
                this.routerMessage.todo.params.page_num = 1;
                this.routerMessage.done.params.page_num = 1;
                this.childList = [];
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
                this.routerMessage.todo.params.page_size = val;
                this.routerMessage.todo.params.page_num = 1;
                this.routerMessage.done.params.page_size = val;
                this.routerMessage.done.params.page_num = 1;
                this.$emit("getTableData", this.routerMessage);
            },
            //获取未核对下第二个表格数据
            getCurRowData: function (row, event, column) {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method:"post",
                    data:{
                        optype:this.checkOptype,
                        params:{
                            pay_account_no: row.pay_account_no,
                            recv_account_no: row.recv_account_no,
                            payment_amount: row.payment_amount,
                            create_on: row.create_on
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
                    this.$confirm('确认核对当前选择数据吗?', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).then(() => {
                        var row = this.currenrRow;
                        this.$axios({
                            url: this.queryUrl + "normalProcess",
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
                    }).catch(() => {

                    });
                }
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
                    this.isAllot = false;
                }else if(type == '8'){//内部调拨
                    this.routerMessage.todo.optype = "dbttrad_billList";
                    this.routerMessage.done.optype = "dbttrad_confirmbillList";
                    this.checkOptype = "dbttrad_tradingList";
                    this.confirmOptype = "dbttrad_confirm";
                    this.validatedOptype = "dbttrad_confirmTradingList";
                    this.isAllot = true;
                }else if(type == '15'){
                    this.routerMessage.todo.optype = "skttrad_billList";
                    this.routerMessage.done.optype = "skttrad_confirmbillList";
                    this.checkOptype = "skttrad_tradingList";
                    this.confirmOptype = "skttrad_confirm";
                    this.validatedOptype = "skttrad_confirmTradingList";
                    this.isAllot = false;
                }
                this.$emit("getTableData", this.routerMessage);
            },
            //根据调拨类型查询
            queryByPayType:function(val){
                var searchData = this.searchData;
                for (var k in searchData) {
                    if(k!=='payment_type')
                        searchData[k] = "";
                }
                this.tableList = [];
                this.childList = [];
                this.routerMessage.todo.params = {
                    page_size: 7,
                    page_num: 1
                }
                this.routerMessage.done.params = {
                    page_size: 7,
                    page_num: 1
                }
                if(val == '10'){
                    this.routerMessage.todo.optype = "dbtbatchtrad_billList";
                    this.routerMessage.done.optype = "dbtbatchtrad_confirmbillList";
                    this.checkOptype = "dbtbatchtrad_tradingList";
                    this.confirmOptype = "dbtbatchtrad_confirm";
                    this.validatedOptype = "dbtbatchtrad_confirmTradingList";
                }else{
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
                    if(k!=='payment_type')
                        searchData[k] = "";
                }
                this.childList = [];

            },
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data;
                if(!this.isPending){
                     this.expandKeys = [];
                }
            },
            '$route' (to, from) {
                // 对路由变化作出响应...
                this.getRouterParamsChange(to.query.bizType);
            }
        }
    }
</script>

