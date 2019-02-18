<style scoped lang="less" type="text/less">
    #noDirectlyLook {
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
            }

            /*汇总数字*/
            .numText {
                color: #FF5800;
                margin-right: 10px;
            }
        }

        /*按钮样式*/
        .get-all, .get-list {
            display: inline-block;
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
        }
        /*弹框-汇总按钮*/
        .get-all {
            background-position: -366px -2px;
        }
        /*弹框-列表按钮*/
        .get-list {
            background-position: -393px -2px;
        }

        /*查看弹框*/
        .switchover {
            text-align: center;
            position: absolute;
            top: 20px;
            left: 400px;

            .el-button {
                background-color: #fff;
                border-color: #e8e8e8;
                padding: 4px 15px;
            }
            .el-button:nth-child(1) {
                border-radius: 10px 0 0 10px;
            }
            .el-button:nth-child(2) {
                border-radius: 0 10px 10px 0;
            }
            .active {
                background-color: #fafafa;
            }
        }

        .list-table{
            padding-bottom: 20px;
        }

        .dialog-talbe {
            width: 100%;
            height: 230px;
            margin-bottom: 42px;

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

            .small-title {
                border-right: none;
            }
            .table-two-row {
                width: 88%;
                margin-left: -3px;
                border-left: none;
            }

            .partition{
                margin-left: 20px;
            }
        }

        .serial-number {
            color: #ccc;
            margin-bottom: 5px;
            margin-top: -10px;
        }
    }
</style>
<style lang="less">
    #noDirectlyLook{
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
                max-height:440px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="noDirectlyLook">
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
                            <el-input v-model="searchData.pay_account_no" clearable placeholder="请输入付款方名称或账号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.recv_account_no" clearable
                                      placeholder="请输入收款方名称或账号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item>
                            <el-col :span="11">
                                <el-input v-model="searchData.min_amount" clearable placeholder="最小金额"></el-input>
                            </el-col>
                            <el-col class="line" :span="2">-</el-col>
                            <el-col :span="11">
                                <el-input v-model="searchData.max_amount" clearable placeholder="最大金额"></el-input>
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
                                <el-checkbox v-for="(name,k) in statusList"
                                             :label="k" name="type" :key="k">
                                    {{ name }}
                                </el-checkbox>
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
                      border size="mini">
                <el-table-column prop="batchno" label="批次号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="total_num" label="总笔数" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="total_amount" label="总金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="success_num" label="成功笔数" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="success_amount" label="成功金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="service_status" label="批次状态" :show-overflow-tooltip="true"
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
                    <el-button type="warning" plain size="mini" @click="goMakeBill">制单</el-button>
                </div>
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
            <div class="switchover">
                <el-button-group>
                    <el-button type="primary" size="mini" :class="{active:showAll}"
                               @click="showAll=true">
                        <i class="get-all"></i>
                    </el-button>
                    <el-button type="primary" size="mini" :class="{active:!showAll}"
                               @click="showAll=false">
                        <i class="get-list"></i>
                    </el-button>
                </el-button-group>
            </div>
            <div class="all-table" v-show="showAll">
                <div class="serial-number">
                    [批次号:
                    <span v-text="dialogData.batchno"></span>
                    ]
                </div>
                <ul class="dialog-talbe">
                    <li class="table-li-title">总笔数</li>
                    <li class="table-li-content" v-text="dialogData.total_num"></li>
                    <li class="table-li-title">总金额</li>
                    <li class="table-li-content" v-text="dialogData.total_amount"></li>

                    <li class="table-li-title">收款账号</li>
                    <li class="table-li-content" v-text="dialogData.recv_account_no"></li>
                    <li class="table-li-title">开户行</li>
                    <li class="table-li-content" :title="dialogData.recv_account_bank" v-text="dialogData.recv_account_bank"></li>

                    <li class="table-li-title small-title">支付结果</li>
                    <li class="table-li-content table-two-row"></li>

                    <li class="table-li-title">已失败</li>
                    <li class="table-li-content"  style="color:#fd7d2f">
                        <span v-text="dialogData.failed_num"></span>笔
                        <span v-text="dialogData.failed_amount" class="partition"></span>元
                    </li>
                    <li class="table-li-title">处理中</li>
                    <li class="table-li-content">
                        <span v-text="dialogData.process_num"></span>笔
                        <span v-text="dialogData.process_amount" class="partition"></span>元
                    </li>

                    <li class="table-li-title">已保存</li>
                    <li class="table-li-content">
                        <span v-text="dialogData.saved_num"></span>笔
                        <span v-text="dialogData.saved_amount" class="partition"></span>元
                    </li>
                    <li class="table-li-title">已作废</li>
                    <li class="table-li-content">
                        <span v-text="dialogData.cancel_num"></span>笔
                        <span v-text="dialogData.cancel_amount" class="partition"></span>元
                    </li>

                    <li class="table-li-title">已成功</li>
                    <li class="table-li-content table-two-row">
                        <span v-text="dialogData.success_num"></span>笔
                        <span v-text="dialogData.success_amount" class="partition"></span>元
                    </li>

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
            </div>
            <div class="list-table" v-show="!showAll">
                <el-form :inline="true" :model="dialogSearch" size="mini">
                    <el-row>
                        <el-col :span="6">
                            <el-form-item>
                                <el-input v-model="dialogSearch.query_key" clearable
                                          placeholder="请输入付款方名称或账号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item>
                                <el-col :span="11">
                                    <el-input v-model="dialogSearch.min_amount" clearable placeholder="最小金额"></el-input>
                                </el-col>
                                <el-col class="line" :span="1" style="text-align:center">-</el-col>
                                <el-col :span="11">
                                    <el-input v-model="dialogSearch.max_amount" clearable placeholder="最大金额"></el-input>
                                </el-col>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="queryDialogData" size="mini">搜索</el-button>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
                <el-table :data="dialogList"
                          border size="mini">
                    <el-table-column prop="pay_account_name" label="付款户名" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="pay_account_no" label="付款账号" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="pay_account_bank" label="付款行" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="payment_amount" label="金额" :show-overflow-tooltip="true"
                                     :formatter="transitAmount"></el-table-column>
                    <el-table-column prop="pay_status" label="业务状态" :show-overflow-tooltip="true"
                                     :formatter="transitStatus"></el-table-column>
                    <el-table-column prop="feed_back" label="反馈信息" :show-overflow-tooltip="true"></el-table-column>
                </el-table>
                <div class="allData">
                    <span>总笔数：</span>
                    <span v-text="dialogTotal.total_num" class="numText"></span>
                    <span>总金额：</span>
                    <span v-text="dialogTotal.total_amount" class="numText"></span>
                </div>
                <el-pagination style="margin-top:20px;margin-left:280px"
                               background
                               layout="prev, pager, next, jumper"
                               :page-size="dialogPagSize"
                               :total="dialogPagTotal"
                               :pager-count="5"
                               @current-change="diaCurrentPage"
                               :current-page="dialogPagCurrent">
                </el-pagination>
            </div>
        </el-dialog>
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";
    import BusinessTracking from "../publicModule/BusinessTracking.vue"

    export default {
        name: "NoDirectlyLook",
        created: function () {
            this.$emit("transmitTitle", "非直连归集-查看");
            this.$emit("getCommTable", this.routerMessage);
        },
        props: ["tableData"],
        components: {
            Upload: Upload,
            BusinessTracking:BusinessTracking
        },
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "ndc_batchlist",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                searchData: { //搜索条件
                    pay_account_no: "",
                    recv_account_no: "",
                    min_amount: "",
                    max_amount: "",
                    service_status: [],
                    start_date: "",
                    end_date: ""
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                dateValue: "", //时间选择
                statusList: {
                    2: "已提交",
                    3: "审批中",
                    11: "已完结"
                },
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
                dialogVisible: false, //查看弹框
                dialogData: {},
                showAll: true,
                dialogSearch: {
                    query_key: "",
                    min_amount: "",
                    max_amount: ""
                },
                dialogList: [],
                dialogMessage: {
                    optype: "ndc_billdetaillist",
                    params: {
                        page_num: 1,
                        page_size: 8,
                        batchno: ""
                    }
                },
                dialogPagSize: 10,
                dialogPagTotal: 1,
                dialogPagCurrent: 1,
                dialogTotal: {
                    total_num: 0,
                    total_amount: 0
                },
                emptyFileList: [], //附件
                fileMessage: {
                    bill_id: "",
                    biz_type: 19
                },
                triggerFile: false,
                businessParams:{ //业务状态追踪参数
                },
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
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //展示格式转换-状态
            transitStatus: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.BillStatus) {
                    return constants.BillStatus[cellValue];
                }
            },
            //制单
            goMakeBill: function () {
                this.$router.push("/collection/not-directly-set");
            },
            //查看
            lookBill: function (row) {
                //获取汇总数据
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "ndc_detail",
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
                    }else{
                        var data = result.data.data;
                        this.dialogData = data;
                        this.dialogData.batchno = row.batchno;

                        //附件数据
                        this.emptyFileList = [];
                        this.fileMessage.bill_id = row.id;
                        this.triggerFile = !this.triggerFile;

                        //业务状态跟踪
                        this.businessParams = {};
                        this.businessParams.biz_type = 19;
                        this.businessParams.id = row.id;
                    }
                }).catch(function (error) {
                    console.log(error);
                });

                //获取详细列表
                this.dialogMessage.params.batchno = row.batchno;
                this.getDialogList(1);

                this.dialogVisible = true;
            },
            //查看-获取详细列表
            getDialogList: function(pageNum){
                var params = this.dialogMessage;
                params.params.page_num = pageNum;

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: params
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
                        var data = result.data;
                        this.dialogPagSize = data.page_size;
                        this.dialogPagTotal = data.total_line;
                        this.dialogList = data.data;
                        this.dialogPagCurrent = data.page_num;
                        this.dialogTotal = data.ext;
                        console.log(data);
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //查看-列表换页
            diaCurrentPage: function(currPage){
                this.getDialogList(currPage);
            },
            //查询弹框数据
            queryDialogData: function(){
                var dialogSearch = this.dialogSearch;
                for(var k in dialogSearch){
                    this.dialogMessage.params[k] = dialogSearch[k];
                }
                this.getDialogList(1);
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



