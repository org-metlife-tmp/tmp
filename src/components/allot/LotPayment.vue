<style scoped lang="less" type="text/less">
    #lotPayment {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*顶部按钮*/
        .button-list-left {
            position: absolute;
            top: -56px;
            left: -21px;
        }

        /*搜索区*/
        .search-setion {
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

        /*底部操作按钮*/
        .edit-btn{
            text-align: left;
            position: absolute;
            z-index: 9;
            bottom: 0;
            .arrows {
                // height: 10px;
                display: inline-block;
                // line-height: 8px;
                // font-size: 12px;
                // vertical-align: middle;
                font-family: initial;
                margin-left: 10px;
            }
        }
        .table-content{
            height: 350px;
            overflow-y: auto;
            .el-card:hover{
                border-color: #409EFF;
                .right-btn{
                    display: inline-block;
                }
                .blue{
                    color: #409EFF;
                }
            }
            .el-card{
                width: 92%;
                margin: 0 auto;
                margin-bottom: 20px;
                text-align: left;
                //顶部
                .head-box{
                    overflow: hidden;
                    line-height: 30px;
                }
                .headline{
                    width: 65%;
                    float: left;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                    text-align: left;
                    color: #8e8e8e;
                }
                .right-btn{
                    display: none;
                    float: right;
                    margin-top: 4px;
                }
                .content-check-box{
                    position: absolute;
                    bottom: 10px;
                    right: 10px;
                }
                .right-btn.el-button--small{
                    padding: 3px 8px;
                }
                //卡片内容
                .card-content{
                    position: relative;
                    text-align: center;
                    padding: 0px 14px;
                    .content-box{
                        display: flex;
                        flex-flow: row;
                        color: #949494;
                        >div{
                            padding: 10px 0;
                            span{
                                font-size: 16px;
                                line-height: 28px;
                            }
                            p{
                                font-size: 13px;
                            }
                        }
                        .numBox{
                            width: 88px;
                        }
                        .amountBox{
                            flex: 1;
                        }
                    }
                    .content-box:first-child{
                        border-bottom: 1px dotted #dbdbdb;
                    }

                }
            }
        }
        .dialog-section{
            padding-bottom: 15px;
            /*弹框表格-分页部分*/
            .inner-botton-pag {
                width: 100%;
                height: 8%;
                margin-top: 51px;
                .el-pagination{
                    text-align: center;
                }
            }
            .tab-content{
                height:292px;
            }
            /*汇总数据*/
            .allData {
                height: 36px;
                line-height: 36px;
                width: 100%;
                background-color: #F8F8F8;
                border: 1px solid #ebeef5;
                border-top: none;
                box-sizing: border-box;
                text-align: right;

                /*左侧按钮*/
                .btn-left {
                    float: left;
                    margin-left: 16px;
                } 
            }
        }
        
    }
</style>
<style lang="less" type="text/less">
    #lotPayment {
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 440px;
                overflow-y: auto;
            }
        }
        .el-card{
            .el-card__header{
                padding: 0px 14px;
            }
            .el-card__body{
                padding: 0;
            }
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
    <div id="lotPayment">
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
                            <el-input v-model="searchData.pay_query_key" clearable placeholder="请输入付款方名称或账号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.recv_query_key" clearable
                                      placeholder="请输入收款方名称或账号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item>
                            <el-col :span="11">
                                <el-input v-model="searchData.min" clearable placeholder="最小金额"></el-input>
                            </el-col>
                            <el-col class="line" :span="2">-</el-col>
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
        <section class="table-content" @scroll="paperScroll($event)">
            <el-row>
                <el-col :span="8" v-for="(card,index) in tableList" :key="card.batchno">
                    <el-card class="box-card">
                        <div slot="header" class="head-box">
                            <span class="headline" :title="card.batchno">{{card.batchno}}</span>
                            <el-button class="right-btn" type="primary" icon="el-icon-view" size="small" @click="lookCard(card)">查看</el-button>
                        </div>
                        <div class="card-content">
                            <div class="content-box">
                                <div class="numBox"><span class="blue">{{card.pending_num}}</span><p>待处理</p></div>
                                <div class="amountBox"><span class="blue">{{transitionMoney(card.pending_amount)}}</span><p>待处理金额</p></div>
                            </div>
                            <div class="content-box">
                                <div class="numBox"><span>{{card.total_num}}</span><p>总笔数</p></div>
                                <div class="amountBox"><span>{{transitionMoney(card.total_amount)}}</span><p>总金额</p></div>
                            </div>
                            <el-checkbox class="content-check-box" @change="setCurrentCard($event,card)" v-model="card.isChecked"></el-checkbox>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </section>
        <div class="edit-btn">
            <!-- <el-button type="warning" size="mini" @click="">
                <span class="transmit-icon"><i></i></span>发送
            </el-button> -->
            <el-button type="warning" plain size="mini" icon="el-icon-delete"
                           @click="cancellation">支付作废
            </el-button>
            <el-button type="warning" plain size="mini" @click="goMoreBills">
                更多单据<span class="arrows">></span>
            </el-button>
        </div>
        <!--查看弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="810px" title=""
                   :close-on-click-modal="false"
                   @close="closeLookDialog"
                   top="56px">
            <h1 slot="title" class="dialog-title">{{searchDetailData.batchno}}</h1>
            <section class="dialog-section">
                <div class="search-setion">
                    <el-form :inline="true" :model="searchDetailData" size="mini">
                        <el-row>
                            <el-col :span="6">
                                <el-form-item>
                                    <el-input v-model="searchDetailData.recv_query_key" clearable
                                            placeholder="请输入收款方名称或账号"></el-input>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item>
                                    <el-col :span="11">
                                        <el-input v-model="searchDetailData.min" clearable placeholder="最小金额"></el-input>
                                    </el-col>
                                    <el-col class="line" :span="2">-</el-col>
                                    <el-col :span="11">
                                        <el-input v-model="searchDetailData.max" clearable placeholder="最大金额"></el-input>
                                    </el-col>
                                </el-form-item>
                            </el-col>
                            <el-col :span="2">
                                <el-form-item>
                                    <el-button type="primary" plain @click="queryDetailData" size="mini">搜索</el-button>
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </el-form>
                </div>
                <section class="tab-content">
                    <el-table :data="detailTableList"
                            height="100%"
                            border size="mini"
                            @selection-change="selectChange">
                        <el-table-column type="selection" width="38"></el-table-column>
                        <el-table-column prop="recv_account_name" label="收款户名" :show-overflow-tooltip="true"></el-table-column>
                        <el-table-column prop="recv_account_no" label="收款账号" :show-overflow-tooltip="true"></el-table-column>
                        <el-table-column prop="recv_account_bank" label="收款行" :show-overflow-tooltip="true"></el-table-column>
                        <el-table-column prop="payment_amount" label="金额" :show-overflow-tooltip="true"
                                        :formatter="transitAmount"></el-table-column>
                        <el-table-column prop="pay_status" label="业务状态" :show-overflow-tooltip="true"
                                        :formatter="transitStatus"></el-table-column>
                        <el-table-column prop="feed_back" label="反馈信息"
                                        :show-overflow-tooltip="true"></el-table-column>
                    </el-table>
                    <div class="allData">
                        <div class="btn-left">
                            <el-button type="warning" plain size="mini" icon="el-icon-delete"
                                    @click="cancellation('more')">支付作废
                            </el-button>
                            <el-button type="warning" size="mini" @click="">
                                <span class="transmit-icon"><i></i></span>发送
                            </el-button>
                        </div>
                    </div>
                </section>
                <!--分页部分-->
                <div class="inner-botton-pag">
                    <el-pagination
                            background
                            layout="sizes, prev, pager, next, jumper"
                            :page-size="pagDeSize"
                            :total="pagDeTotal"
                            :page-sizes="[7, 50, 100, 500]"
                            :pager-count="5"
                            @current-change="getCurrentDePage"
                            @size-change="sizeDeChange"
                            :current-page="pagDeCurrent">
                    </el-pagination>
                </div>
            </section>
        </el-dialog>
        <!--支付作废弹出框-->
        <el-dialog title="作废"
                   :visible.sync="payVisible"
                   width="600px" top="76px"
                   :close-on-click-modal="false">
            <div style="margin-bottom:16px">请输入作废原因：</div>
            <el-input
                    type="textarea"
                    :autosize="{ minRows: 3,maxRows: 16}"
                    placeholder="请输入作废原因(必填)"
                    v-model="paymentData.feed_back">
            </el-input>
            <span slot="footer" class="dialog-footer" style="text-align:center">
                <el-button type="warning" size="mini" @click="confirmcancell">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";
    import BusinessTracking from "../publicModule/BusinessTracking.vue"

    export default {
        name: "LotPayment",
        created: function () {
            this.$emit("transmitTitle", "批量调拨制单-支付");
            this.$emit("getCommTable", this.routerMessage);
        },
        mounted: function () {
        },
        components: {
            Upload: Upload,
            BusinessTracking:BusinessTracking
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: {
                    optype: "dbtbatch_paylist",
                    params: {
                        page_size: 9,
                        page_num: 1
                    }
                },
                searchData: { //搜索条件
                    pay_query_key: "",
                    recv_query_key: "",
                    min: "",
                    max: "",
                    service_status: [],
                    start_date: "",
                    end_date: ""
                },
                dateValue: "", //时间选择
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
                tableList: [],
                dialogVisible: false,
                detailTableList:[],
                pagDeSize: 8, //弹窗分页数据
                pagDeTotal: 1,
                pagDeCurrent: 1,
                searchDetailData: {},//弹窗的表格的搜索条件
                payVisible: false,//支付作废
                paymentData: {},
                selectData: [], //要作废的数据
                currentData:{},//当前选中的一条数据
                pagCurrent:1,//当前列表页
                pagTotal:"",//列表总数
            }
        },
        methods: {
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                searchData.start_date = this.dateValue ? this.dateValue[0] : "";
                searchData.end_date = this.dateValue ? this.dateValue[1] : "";
                for (var k in searchData) {
                    this.routerMessage.params[k] = searchData[k];
                }
                this.pagCurrent = 1;
                this.routerMessage.params.page_size = 9;
                this.$emit("getCommTable", this.routerMessage);
            },
            //展示格式转换-金额
            transitionMoney: function(num){
                return this.$common.transitSeparator(num);
            },
            //查看
            lookCard: function(row){
                this.searchDetailData.batchno = row.batchno;
                this.getDetailTable(this.searchDetailData);
                this.currentData = row;
                this.dialogVisible = true;
            },
            getDetailTable: function (params) {
                params.page_size = params.page_size ? params.page_size : 7;
                params.page_num = params.page_num ? params.page_num : 1;
                params.pay_status = [0,2];
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "dbtbatch_detaillist",
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
                        var val = result.data;
                        var data = val.data;
                        this.pagDeSize = val.page_size;
                        this.pagDeTotal = val.total_line;
                        this.pagDeCurrent = val.page_num;
                        this.detailTableList = data;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            setCurrentCard: function(val,row){
                if(val){
                    this.tableList.forEach(element=>{
                        if(element.batchno !== row.batchno){
                            element.isChecked = false;
                        }
                    });
                    this.paymentData.id = row.id;
                    this.paymentData.persist_version = row.persist_version;
                }else{
                    this.paymentData.id = "";
                    this.paymentData.persist_version = "";
                }
                //解决响应式问题
                var index = this.tableList.indexOf(row);
                this.$set(this.tableList,index,row);
            },
            //展示格式转换-处理状态
            transitStatus: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.PayStatus) {
                    return constants.PayStatus[cellValue];
                }
            },
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //根据条件查询数据(弹窗表格)
            queryDetailData: function () {
                this.getDetailTable(this.searchDetailData);
            },
            //换页后获取数据(弹窗表格)
            getCurrentDePage: function (currPage) {
                this.searchDetailData.page_num = currPage;
                this.getDetailTable(this.searchDetailData);
            },
            //当前页数据条数发生变化(弹窗表格)
            sizeDeChange: function (val) {
                this.searchDetailData.page_size = val;
                this.searchDetailData.page_num = 1;
                this.getDetailTable(this.searchDetailData);
            },
            //支付作废
            cancellation:function(number){
                this.paymentData.detail_ids = [];
                this.paymentData.feed_back = "";
                if (number == "more") {
                    var selData = this.selectData;
                    if(selData.length<1){
                        this.$message({
                            type:"warning",
                            message:"请选择要作废的数据！",
                            duration:2000
                        });
                        return;
                    }
                    this.paymentData.id = this.currentData.id;
                    this.paymentData.persist_version = this.currentData.persist_version;
                    selData.forEach(element =>{
                        this.paymentData.detail_ids.push(element.detail_id);
                    });
                    this.paymentData.number = "more";
                }else{
                    if(!this.paymentData.id){
                        this.$message({
                            type:"warning",
                            message:"请选择一条作废数据！",
                            duration:2000
                        });
                        return;
                    }
                } 
                this.payVisible = true;
            },
            //确认支付作废
            confirmcancell: function(){
                if(!this.paymentData.feed_back){
                    this.$message({
                        type:"warning",
                        message:"请输入作废原因！",
                        duration:2000
                    });
                    return;
                }
                var optype = this.paymentData.number ? 'dbtbatch_cancelids' : 'dbtbatch_cancel';
                var lala = this.paymentData;
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: optype,
                        params: lala
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
                            message: "数据已作废",
                            duration: 2000
                        });
                        this.payVisible = false;
                        if(optype=='dbtbatch_cancel'){//批次作废
                            this.pagCurrent = 1;
                            this.routerMessage.params.page_size = 9;
                            this.$emit("getCommTable", this.routerMessage);
                            this.paymentData = [];
                        } 
                        else{//批量作废
                            this.selectData = [];
                            var detail_ids= this.paymentData.detail_ids;
                            if(detail_ids.length === this.pagDeTotal){//批量全部作废
                                this.dialogVisible = false;
                                this.paymentData = [];
                            }else{
                                this.getDetailTable(this.searchDetailData);
                                this.paymentData.detail_ids = [];
                                this.currentData.persist_version = data.persist_version;
                            }
                        }
                        
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //多选作废单据
            selectChange: function (selectVal) {
                this.selectData = selectVal;
            },
            //查看弹框关闭
            closeLookDialog: function(){
                this.pagCurrent = 1;
                this.routerMessage.params.page_size = 9;
                this.$emit("getCommTable", this.routerMessage);
                this.paymentData = {};
                this.searchDetailData = {};
            },
            //更多单据
            goMoreBills:function(){
                this.$router.push("/allot/lot-look-over");
            },
            paperScroll: function(e){
                var target = e.target;
                if(target.scrollTop + target.offsetHeight >= target.scrollHeight){
                    this.pagCurrent ++;
                    this.routerMessage.params.page_size = this.pagCurrent * 9;
                    if(this.pagTotal >= this.pagCurrent * 9){//总数大于等于查询数，继续查询
                        //滚动加搜索条件
                        var searchData = this.searchData;
                        searchData.start_date = this.dateValue ? this.dateValue[0] : "";
                        searchData.end_date = this.dateValue ? this.dateValue[1] : "";
                        for (var k in searchData) {
                            this.routerMessage.params[k] = searchData[k];
                        }
                         this.$emit("getCommTable", this.routerMessage);
                    }else{
                        this.$message({
                            type:"warning",
                            message:"没有可加载的数据！",
                            duration:2000
                        });
                        this.pagCurrent --;
                    }
                }
            }
        },
        watch: {
            tableData: function (val, oldVal) {
                // this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                // this.pagCurrent = val.page_num;
                this.tableList = val.data;
                
            }
        }
    }
</script>

