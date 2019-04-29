<style scoped lang="less" type="text/less">
    #payLookOver {
        /*查看弹框*/
        .dialog-talbe {
            width: 100%;
            border-top: 1px solid #e2e2e2;
            border-left: 1px solid #e2e2e2;
            overflow: hidden;

            li {
                float: left;
                box-sizing: border-box;
                border-right: 1px solid #e2e2e2;
                border-bottom: 1px solid #e2e2e2;
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
   #payLookOver{
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
    <el-container id="payLookOver">
        <el-header>
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
                                <el-input v-model="searchData.pay_query_key" clearable placeholder="请输入付款方名称或账号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.recv_query_key" clearable
                                          placeholder="请输入收款方名称或账号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.pay_mode" placeholder="请选择付款方式"
                                           filterable size="mini">
                                    <el-option v-for="(item,k) in payModeList"
                                               :key="k"
                                               :label="item"
                                               :value="k">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="5">
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
            <div class="split-bar"></div>
        </el-header>
        <el-main>
            <el-table :data="tableList"
                      height="100%"
                      border size="mini">
                <el-table-column prop="recv_account_name" label="收款方名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_no" label="收款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_bank" label="收款方银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_mode" label="付款方式" :show-overflow-tooltip="true"
                                 :formatter="transitPayMode"></el-table-column>
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
        </el-main>
        <el-footer>
            <div class="allData">
                <div class="btn-left">
                    <el-button type="warning" plain size="mini" @click="goMakeBill">制单</el-button>
                    <el-button type="warning" plain size="mini" @click="goPayment">支付处理</el-button>
                </div>
                <div class="btn-right">
                    <span>总笔数：</span>
                    <span v-text="totalData.total_num" class="numText"></span>
                    <span>总金额：</span>
                    <span v-text="totalData.total_amount" class="numText"></span>
                </div>
            </div>
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
            <el-dialog title="支付单信息"
                       :visible.sync="dialogVisible"
                       width="900px" top="76px"
                       :close-on-click-modal="false">
                <div class="serial-number">
                    [编号:
                    <span v-text="dialogData.service_serial_number"></span>
                    ]
                </div>
                <ul class="dialog-talbe">
                    <li class="table-li-title">申请日期</li>
                    <li class="table-li-content" v-text="dialogData.apply_on"></li>
                    <li class="table-li-title">业务类型</li>
                    <li class="table-li-content" v-text="dialogData.biz_name"></li>
                    <li class="table-li-title">付款方式</li>
                    <li class="table-li-content" v-text="dialogData.pay_mode"></li>
                    <li class="table-li-title">付款账号</li>
                    <li class="table-li-content" v-text="dialogData.pay_account_no"></li>

                    <li class="table-li-title">收款人户名</li>
                    <li class="table-li-content" v-text="dialogData.recv_account_name"></li>
                    <li class="table-li-title">收款人账号</li>
                    <li class="table-li-content" v-text="dialogData.recv_account_no"></li>

                    <li class="table-li-title">开户行</li>
                    <li class="table-li-content" :title="dialogData.recv_account_bank" v-text="dialogData.recv_account_bank"></li>
                    <li class="table-li-title">金额</li>
                    <li class="table-li-content" v-text="dialogData.payment_amount" style="color:#fd7d2f"></li>

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
        </el-footer>
    </el-container>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";
    import BusinessTracking from "../publicModule/BusinessTracking.vue"

    export default {
        name: "PayLookOver",
        created: function () {
            this.$emit("transmitTitle", "资金支付-查看");
            this.$emit("getCommTable", this.routerMessage);
        },
        mounted: function () {
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.PayMode) {
                this.payModeList = constants.PayMode;
            }
        },
        props: ["tableData"],
        components: {
            Upload: Upload,
            BusinessTracking:BusinessTracking
        },
        data: function () {
            return {
                routerMessage: {
                    optype: "zft_bills",
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
                payModeList: {}, //付款方式选择列表
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
                statusList: {
                    2: "已提交",
                    3: "审批中",
                    4: "审批通过",
                    6: "处理中",
                    7: "已成功",
                    8: "已失败",
                    9: "已作废"
                },
                dialogVisible: false, //弹框数据
                dialogData: {},
                emptyFileList: [], //附件
                fileMessage: {
                    bill_id: "",
                    biz_type: 9
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
            //展示格式转换-付款方式
            transitPayMode: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.PayMode) {
                    return constants.PayMode[cellValue];
                }
            },
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //展示格式转换-处理状态
            transitStatus: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.BillStatus) {
                    return constants.BillStatus[cellValue];
                }
            },
            //制单
            goMakeBill: function () {
                this.$router.push("/payment/pay-make-bill");
            },
            //支付处理
            goPayment: function () {
                this.$router.push("/payment/pay-payment");
            },

            //查看单据详情
            lookBill: function (row) {
                for (var k in row) {
                    if(k == "payment_amount"){
                        this.dialogData[k] = "￥" + this.$common.transitSeparator(row[k]);
                    }else{
                        this.dialogData[k] = row[k];
                    }
                }
                this.dialogData.pay_mode = JSON.parse(window.sessionStorage.getItem("constants")).PayMode[row.pay_mode];
                this.dialogData['apply_on'] = row.apply_on.split(' ')[0];
                this.dialogVisible = true;

                //附件数据
                this.emptyFileList = [];
                this.fileMessage.bill_id = row.id;
                this.triggerFile = !this.triggerFile;

                //业务状态跟踪
                this.businessParams = {};
                this.businessParams.biz_type = 9;
                this.businessParams.id = row.id;
            },
        },
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

