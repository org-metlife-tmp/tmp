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
                width: 210px;
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

        /*弹框表格-分页部分*/
        .inner-botton-pag {

            width: 100%;
            height: 8%;
            margin: 15px 0;
            .el-pagination{
                text-align: center;
            }
        }
        .lookDialog{
            .tab-content{
                height:320px;
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
        <section class="table-content">
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
                            <el-checkbox class="content-check-box" @change="setCurrentCard($event,card)" v-model="card.checked"></el-checkbox>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </section>
        <div class="edit-btn">
            <el-button type="warning" size="mini" @click="">
                <span class="transmit-icon"><i></i></span>发送
            </el-button>
            <el-button type="warning" plain size="mini" icon="el-icon-delete"
                           @click="">支付作废
            </el-button>
            <el-button type="warning" plain size="mini" @click="">
                更多单据<span class="arrows">></span>
            </el-button>
        </div>
        <!--查看弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="810px" title=""
                   class="lookDialog"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">{{dialogData.batchno}}</h1>
            <section>
                <div class="search-setion">
                    <el-form :inline="true" :model="dialogData" size="mini">
                        <el-row>
                            <el-col :span="6">
                                <el-form-item>
                                    <el-input v-model="dialogData.recv_query_key" clearable
                                            placeholder="请输入收款方名称或账号"></el-input>
                                </el-form-item>
                            </el-col>
                            <el-col :span="8">
                                <el-form-item>
                                    <el-col :span="11">
                                        <el-input v-model="dialogData.min" clearable placeholder="最小金额"></el-input>
                                    </el-col>
                                    <el-col class="line" :span="2">-</el-col>
                                    <el-col :span="11">
                                        <el-input v-model="dialogData.max" clearable placeholder="最大金额"></el-input>
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
                            border size="mini">
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
                        page_size: 7,
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
                dialogData: {},
                detailTableList:[],
                pagDeSize: 8, //弹窗分页数据
                pagDeTotal: 1,
                pagDeCurrent: 1,
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
                this.$emit("getCommTable", this.routerMessage);
            },
            //展示格式转换-金额
            transitionMoney: function(num){
                return this.$common.transitSeparator(num);
            },
            //查看
            lookCard: function(row){
                this.dialogVisible = true;
                this.dialogData.batchno = row.batchno;
            },
            setCurrentCard: function(val,row){
                this.tableList.forEach(element=>{
                    if(element.batchno !== row.batchno){
                        element.checked = false;
                    }

                })
                // row.checked = val;
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
            //展示格式转换-处理状态
            transitStatus: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.BillStatus) {
                    return constants.BillStatus[cellValue];
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
        },
        watch: {
            tableData: function (val, oldVal) {
                // this.pagSize = val.page_size;
                // this.pagTotal = val.total_line;
                // this.pagCurrent = val.page_num;
                // this.success_amount = val.success_amount;
                // this.total_amount = val.total_amount;
                this.tableList = val.data;
            }
        }
    }
</script>

