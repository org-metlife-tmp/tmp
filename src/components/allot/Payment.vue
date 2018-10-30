<style scoped lang="less" type="text/less">
    #payment {
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

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
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

            /*汇总数字*/
            .numText {
                color: #FF5800;
                margin-right: 10px;
            }
        }
        //发送
        .transmit-icon {
            position: relative;
            display: inline-block;
            width: 16px;
            height: 10px;
            vertical-align: middle;
            margin-right: 4px;

            i {
                position: absolute;
                top: -5px;
                left: -3px;
                width: 18px;
                height: 18px;
                background: url(../../assets/icon_common.png) no-repeat;
                background-position: -49px -80px;
            }
        }

        /*查看弹框*/
        .bill-status {
            height: 50px;
            margin-bottom: 22px;
            background: #fafafa;
            line-height: 50px;
            display: flex;
            flex-flow: row;
            >.i-status i {
                font-size: 30px;
                vertical-align: middle;
                flex: 1;
            }
            >.i-status span {
                font-size: 22px;
                vertical-align: middle;
            }
            >.i-time{
                flex: 1;
                text-align: center;
                >span{
                    display: inline-block;
                    overflow: hidden;
                }
                .feed-back{
                    margin-left: 25px;
                }
            }
            .success-color {
                color: #44c62b;
            }
            .defeated-color {
                color: red;
            }
        }

        .dialog-talbe {
            width: 100%;
            height: 230px;

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
        /*数据展示部分*/
        .table-content{
            height: 279px;
        }

        /*失败原因图标*/
        .flow-tip-box{
            display: inline-block;
            width: 24px;
            height: 20px;
            vertical-align: middle;
            background-image: url(../../assets/icon_common.png);
            background-repeat: no-repeat;
            background-position: -410px -166px;
            cursor: pointer;
            z-index: 5;
            background-color: #fff;
            border: 0;
            padding: 0;
        }
    }
</style>
<style lang="less">
    #payment{
        .el-form--inline .el-form-item{
            width: calc(100% - 10px);
            width: -moz-calc(100% - 10px);
            width: -webkit-calc(100% - 10px);
        }
        .el-form--inline .el-form-item__content{
            width: 100%;
        }
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 440px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="payment">
        <!--顶部按钮-->
        <div class="button-list-left">
            <el-select v-model="searchData.payment_type" placeholder="请选择调拨类型"
                       filterable clearable size="mini" @change="queryByPayType">
                <el-option v-for="(name,k) in paymentTypeList"
                           :key="k"
                           :label="name"
                           :value="k">
                </el-option>
            </el-select>
            <el-select v-model="searchData.pay_mode" placeholder="请选择付款方式"
                    filterable size="mini" @change="queryByPayMode">
                <el-option v-for="(name,k) in payModeList"
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

                    <el-col :span="24">
                        <el-form-item style="margin-bottom:0px">
                            <el-checkbox-group v-model="searchData.service_status">
                                <el-checkbox label="4" name="type">审批通过</el-checkbox>
                                <el-checkbox label="8" name="type">已失败</el-checkbox>
                            </el-checkbox-group>
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
                      border size="mini"
                      height="100%"
                      @selection-change="selectChange">
                <el-table-column type="selection" width="38"></el-table-column>
                <el-table-column prop="pay_mode" label="付款方式" :show-overflow-tooltip="true"
                                 :formatter="transitPayMode"></el-table-column>
                <el-table-column prop="pay_account_no" label="付款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_account_bank" label="付款银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_no" label="收款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_name" label="收款方公司名称"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="payment_amount" label="金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="service_status" label="处理状态" :show-overflow-tooltip="true"
                                 :formatter="transitStatus"></el-table-column>
                <el-table-column
                        label="操作" width="50"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookBill(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
            <div class="allData">
                <div class="btn-left">
                    <el-button type="warning" plain size="mini" @click="goLookOver">
                        更多单据<i class="el-icon-arrow-right el-icon--right"></i>
                    </el-button>
                    <el-button type="warning" plain size="mini" icon="el-icon-delete"  v-show="searchData.pay_mode==1"
                               @click="cancellation('more')">支付作废
                    </el-button>
                    <el-button type="warning" size="mini" @click="confirmPay('more')" v-show="searchData.pay_mode==2">
                        <span class="transmit-icon"><i></i></span>支付确认
                    </el-button>
                    <el-button type="warning" size="mini" @click="sendBill('more')" v-show="searchData.pay_mode==1">
                        <span class="transmit-icon"><i></i></span>发送
                    </el-button>
                </div>
                <span>总笔数：</span>
                <span v-text="totalData.total_num" class="numText"></span>
                <span>总金额：</span>
                <span v-text="totalData.total_amount" class="numText"></span>
            </div>
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
        <el-dialog title="调拨单信息"
                   :visible.sync="dialogVisible"
                   width="900px" top="76px"
                   :close-on-click-modal="false">
            <div class="bill-status">
                <div class="i-status">
                    <i :class="{'success-color':dialogData.service_status == 4,
                            'defeated-color':dialogData.service_status != 4,
                            'el-icon-circle-check-outline':dialogData.service_status == 4,
                            'el-icon-circle-close-outline':dialogData.service_status != 4}">
                    </i>
                    <span v-text="currentStatus"
                        :class="{'success-color':dialogData.service_status == 4,'defeated-color':dialogData.service_status != 4}"></span>
                </div>
                <div class="i-time">
                    <span v-text="dialogData.create_on"></span>
                    <span class="feed-back" v-show="dialogData.feed_back">失败原因</span>
                    <el-popover
                        v-show="dialogData.feed_back"
                        placement="top-start"
                        title="失败原因"
                        width="200"
                        trigger="hover"
                        :content="dialogData.feed_back">
                        <el-button class="flow-tip-box" slot="reference"></el-button>
                    </el-popover>
                </div>
                <div class="i-btn">
                    <el-button type="warning" plain size="mini" icon="el-icon-delete" v-show="searchData.pay_mode==1"
                           @click="cancellation">支付作废
                    </el-button>
                    <el-button type="warning" size="mini" @click="sendBill" v-show="searchData.pay_mode==1">
                        <span class="transmit-icon"><i></i></span>发送
                    </el-button>
                    <el-button type="warning" size="mini" @click="confirmPay" v-show="searchData.pay_mode==2">
                        <span class="transmit-icon"><i></i></span>支付确认
                    </el-button>
                </div>
            </div>
            <div class="serial-number">
                [编号:
                <span v-text="dialogData.service_serial_number"></span>
                ]
            </div>
            <ul class="dialog-talbe">
                <li class="table-li-title">业务类型</li>
                <li class="table-li-content" v-text="dialogData.biz_name"></li>
                <li class="table-li-title">付款方式</li>
                <li class="table-li-content" v-text="dialogData.pay_mode"></li>

                <li class="table-li-title">付款单位</li>
                <li class="table-li-content" v-text="dialogData.pay_account_name"></li>
                <li class="table-li-title">收款单位</li>
                <li class="table-li-content" v-text="dialogData.recv_account_name"></li>

                <li class="table-li-title">账号</li>
                <li class="table-li-content" v-text="dialogData.pay_account_no"></li>
                <li class="table-li-title">账号</li>
                <li class="table-li-content" v-text="dialogData.recv_account_no"></li>

                <li class="table-li-title">开户行</li>
                <li class="table-li-content" :title="dialogData.pay_account_bank" v-text="dialogData.pay_account_bank"></li>
                <li class="table-li-title">开户行</li>
                <li class="table-li-content" :title="dialogData.recv_account_bank" v-text="dialogData.recv_account_bank"></li>

                <li class="table-li-title">调拨金额</li>
                <li class="table-li-content" v-text="dialogData.payment_amount" style="color:#fd7d2f"></li>
                <li class="table-li-title">大写</li>
                <li class="table-li-content" v-text="dialogData.numText"></li>

                <li class="table-li-title">摘要</li>
                <li class="table-li-content table-two-row" v-text="dialogData.payment_summary"></li>

                <li class="table-li-title" style="height:60px;line-height:60px">附件</li>
                <li class="table-li-content table-two-row" style="height:60px;padding-top:6px;overflow-y:auto">
                    <Upload :emptyFileList="emptyFileList"
                            :fileMessage="fileMessage"
                            :triggerFile="triggerFile"
                            :isPending="false"></Upload>
                </li>
            </ul>
            <BusinessTracking :businessParams="businessParams"></BusinessTracking>
        </el-dialog>
        <!--支付作废弹出框-->
        <el-dialog title="作废"
                   :visible.sync="innerVisible"
                   width="600px" top="76px"
                   :close-on-click-modal="false">
            <div style="margin-bottom:6px">请输入作废原因：</div>
            <el-input
                    type="textarea"
                    :autosize="{ minRows: 3,maxRows: 16}"
                    placeholder="请输入作废原因(必填)"
                    v-model="paymentData.feed_back">
            </el-input>
            <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="innerVisible = false">取 消</el-button>
                    <el-button type="warning" size="mini" @click="confirmcancell">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";
    import BusinessTracking from "../publicModule/BusinessTracking.vue"

    export default {
        name: "Payment",
        created: function () {
            this.$emit("transmitTitle", "内部调拨-支付处理");
            this.$emit("getCommTable", this.routerMessage);
        },
        components: {
            Upload: Upload,
            BusinessTracking:BusinessTracking
        },
        mounted: function () {
            //调拨类型
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.ZjdbType) {
                this.paymentTypeList = constants.ZjdbType;
            }
            //付款方式
            if(constants.PayMode){
                var data = constants.PayMode;
                delete data['8'];//删除线下补录
                this.payModeList = data;
            }
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: {
                    optype: "dbt_paylist",
                    params: {
                        page_size: 7,
                        page_num: 1,
                        pay_mode: 1
                    }
                },
                searchData: { //搜索条件
                    pay_mode: "1",
                    payment_type: "",
                    pay_query_key: "",
                    recv_query_key: "",
                    min: "",
                    max: "",
                    service_status: [],
                    start_date: "",
                    end_date: ""
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                totalData: { //汇总数据
                    total_amount: "",
                    total_num: ""
                },
                dateValue: "", //时间选择
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
                paymentTypeList: {}, //下拉框数据
                dialogVisible: false, //弹框数据
                dialogData: {},
                currentStatus: "",
                innerVisible: false,
                selectData: [], //列表选中数据
                paymentData: { //支付作废参数
                    ids: [],
                    feed_back: ""
                },
                emptyFileList: [], //附件
                fileMessage: {
                    bill_id: "",
                    biz_type: 8
                },
                triggerFile: false,
                businessParams:{ //业务状态追踪参数
                },
                payModeList: {},//付款方式
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
                this.routerMessage.params.page_num = 1;
                this.$emit("getCommTable", this.routerMessage);
            },
            //根据调拨类型查询
            queryByPayType:function(val){
                var searchData = this.searchData;
                searchData.start_date = this.dateValue ? this.dateValue[0] : "";
                searchData.end_date = this.dateValue ? this.dateValue[1] : "";
                for (var k in searchData) {
                    if(k=='payment_type'){
                        this.routerMessage.params[k] = val;
                    }else{
                         this.routerMessage.params[k] = searchData[k];
                    }
                }
                this.routerMessage.params.page_num = 1;
                this.$emit("getCommTable", this.routerMessage);
            },
            //根据付款方式查询
            queryByPayMode:function(val){
                var searchData = this.searchData;
                searchData.start_date = this.dateValue ? this.dateValue[0] : "";
                searchData.end_date = this.dateValue ? this.dateValue[1] : "";
                for (var k in searchData) {
                    if(k=='pay_mode'){
                        this.routerMessage.params[k] = val;
                    }else{
                         this.routerMessage.params[k] = searchData[k];
                    }
                }
                this.routerMessage.params.page_num = 1;
                this.$emit("getCommTable", this.routerMessage);
            },
            //列表选择框改变后
            selectChange: function (val) {
                this.selectData = [];
                for (var i = 0; i < val.length; i++) {
                    this.selectData.push(val[i].id);
                }
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
                this.routerMessage.params.pay_mode = 1;
                this.$emit("getCommTable", this.routerMessage);
            },
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //展示格式转换-付款方式
            transitPayMode: function (row, column, cellValue, index){
                return this.payModeList[cellValue];
            },
            //更多单据
            goLookOver: function () {
                this.$router.push("/allot/more-bills");
            },
            //展示格式转换-处理状态
            transitStatus: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.BillStatus) {
                    return constants.BillStatus[cellValue];
                }
            },
            //查看单据详情
            lookBill: function (row) {
                for (var k in row) {
                    this.dialogData[k] = row[k];
                }
                this.dialogData.numText = this.$common.transitText(row.payment_amount);
                this.dialogData.payment_amount = "￥" + this.$common.transitSeparator(row.payment_amount);
                this.currentStatus = JSON.parse(window.sessionStorage.getItem("constants")).BillStatus[row.service_status];
                this.dialogData.pay_mode = JSON.parse(window.sessionStorage.getItem("constants")).PayMode[row.pay_mode];

                //附件数据
                this.emptyFileList = [];
                this.fileMessage.bill_id = row.id;
                this.triggerFile = !this.triggerFile;

                //业务状态跟踪
                this.businessParams = {};
                this.businessParams.biz_type = 8;
                this.businessParams.id = row.id;

                this.dialogVisible = true;
            },
            //发送
            sendBill: function (number) {
                var params = {
                    ids: []
                };
                if (number == "more") { //发送多条
                    params.ids = this.selectData;
                } else { //发送一条
                    params.ids.push(this.dialogData.id);
                }

                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "dbt_sendPayList",
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
                        this.$message({
                            type: "success",
                            message: "发送成功",
                            duration: 2000
                        });
                        if (number != "more") {
                            this.dialogVisible = false;
                        }
                        this.$emit("getCommTable", this.routerMessage);
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //支付确认
            confirmPay: function(number){
                var ids = [];
                if(number == 'more'){
                    if(this.selectData.length<=0){
                            this.$message({
                                type:"warning",
                                message:"请选择要确认的数据！",
                                duration:2000
                            });
                            return ;
                        }
                        ids= this.selectData;
                }else{
                    ids.push(this.dialogData.id);
                }
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "dbt_payconfirm",
                        params: {
                            ids:ids
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
                            message: "数据已确认！",
                            duration: 2000
                        });
                        if (number != "more") {
                            this.dialogVisible = false;
                        }
                        this.$emit("getCommTable", this.routerMessage);
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //支付作废
            cancellation: function (number) {
                this.paymentData.ids = [];
                this.paymentData.feed_back = "";
                if (number == "more") {
                    if(this.selectData.length<=0){
                        this.$message({
                            type:"warning",
                            message:"请选择要作废的数据！",
                            duration:2000
                        });
                        return ;
                    }
                    this.paymentData.ids = this.selectData;
                } else {
                    this.paymentData.ids.push(this.dialogData.id);
                }
                this.innerVisible = true;
            },
            //确定作废
            confirmcancell: function () {
                if(!this.paymentData.feed_back){
                    this.$message({
                        type:"warning",
                        message:"请输入作废原因！",
                        duration:2000
                    });
                    return;
                }
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "dbt_cancel",
                        params: this.paymentData
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
                            message: "数据已作废",
                            duration: 2000
                        });
                        this.innerVisible = false;
                        if(this.dialogVisible){
                            this.dialogVisible = false;
                        }
                        this.$emit("getCommTable", this.routerMessage);
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            }
        },
        computed: {},
        watch: {
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.tableList = val.data;
                this.pagCurrent = val.page_num;
                this.totalData = val.ext;
            }
        }
    }
</script>
