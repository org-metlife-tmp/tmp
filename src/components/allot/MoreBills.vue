<style scoped lang="less" type="text/less">
    #moreBills {
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

        /*数据展示部分*/
        .table-content{
            height: 279px;
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
        .on-copy, .withdraw {
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
        }
        /*复制按钮*/
        .on-copy {
            background-position: -24px 1px;
        }
        /*撤回按钮*/
        .withdraw {
            background-position: -48px 0;
        }

        /*查看弹框*/
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

        /*编辑弹框*/
        .table-input {
            width: 100%;
            height: 100%;
            border: none;
            outline: none;
        }

        .el-radio-group {
            margin-top: 0;
            .el-radio {
                display: block;
                margin-left: 30px;
                margin-bottom: 10px;
            }
        }
        //提交流程查看按钮
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
<style lang="less" type="text/less">
    #moreBills {
        /*编辑弹框*/
        .table-select {

            .el-select {
                height: 95%;
                width: 100%;

                .el-input {
                    height: 100%;

                    input {
                        height: 94%;
                        padding-left: 0px;
                        border: none;
                    }
                }
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
    <div id="moreBills">
        <!--顶部按钮-->
        <div class="button-list-left">
            <!-- <el-select v-model="searchData.payment_type" placeholder="请选择调拨类型"
                       filterable clearable size="mini" @change="queryByPayType">
                <el-option v-for="(name,k) in paymentTypeList"
                           :key="k"
                           :label="name"
                           :value="k">
                </el-option>
            </el-select> -->
            <el-select v-model="searchData.biz_id" placeholder="请选择业务类型"
                        filterable clearable size="mini" @change="queryByPayType">
                <el-option v-for="payItem in payStatList"
                            :key="payItem.biz_id"
                            :label="payItem.biz_name"
                            :value="payItem.biz_id">
                </el-option>
            </el-select>
            <el-select v-model="searchData.pay_mode" placeholder="请选择付款方式"
                    filterable clearable size="mini" @change="queryByPayMode">
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
                       height="100%"
                      border size="mini">
                <el-table-column prop="biz_name" label="业务类型" :show-overflow-tooltip="true"></el-table-column>
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
                        label="操作" width="80"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.service_status != 1 && scope.row.service_status != 5">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookBill(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.service_status == 1 || scope.row.service_status == 5">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editBill(scope.row)"></el-button>
                        </el-tooltip>
                        <!-- <el-tooltip content="复制" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.service_status == 1 || scope.row.service_status == 5 || scope.row.service_status == 2">
                            <el-button class="on-copy" size="mini"
                                       @click="copyMakeBill(scope.row)"></el-button>
                        </el-tooltip> -->
                        <el-tooltip content="撤回" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.service_status == 2">
                            <el-button size="mini" class="withdraw"
                                       @click="withdrawBill(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.service_status == 1">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeBill(scope.row,scope.$index,tableList)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
            <div class="allData">
                <div class="btn-left">
                    <el-button type="warning" plain size="mini" @click="goMakeBill">制单</el-button>
                    <el-button type="warning" plain size="mini" @click="goPayment">支付处理</el-button>
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
            <BusinessTracking :businessParams="lookBusinessParams"></BusinessTracking>
        </el-dialog>
        <!--编辑弹出框-->
        <el-dialog title="编辑调拨单"
                   :visible.sync="editVisible"
                   width="900px" top="76px"
                   :close-on-click-modal="false">
            <div class="serial-number">
                [编号:
                <span v-text="editDialogData.service_serial_number"></span>
                ]
            </div>
            <ul class="dialog-talbe">
                <li class="table-li-title">业务类型</li>
                <li class="table-li-content table-select">
                    <el-select v-model="editDialogData.biz_id" placeholder="请选择业务类型"
                               filterable clearable size="mini">
                        <el-option v-for="payItem in payStatList"
                                   :key="payItem.biz_id"
                                   :label="payItem.biz_name"
                                   :value="payItem.biz_id">
                        </el-option>
                    </el-select>
                </li>
                <li class="table-li-title">付款方式</li>
                <li class="table-li-content table-select">
                    <el-select v-model="editDialogData.pay_mode" placeholder="请选择付款方式"
                               filterable size="mini" @change="changePayMode">
                        <el-option v-for="(name,k) in payModeList"
                                   :key="k"
                                   :label="name"
                                   :value="k">
                        </el-option>
                    </el-select>
                </li>

                <li class="table-li-title">付款单位</li>
                <li class="table-li-content" v-text="editDialogData.pay_account_name"></li>
                <li class="table-li-title">收款单位</li>
                <li class="table-li-content" v-text="editDialogData.recv_account_name"></li>

                <li class="table-li-title">账号</li>
                <li class="table-li-content table-select">
                    <el-select v-model="editDialogData.pay_account_id"
                               clearable filterable remote
                               placeholder="请选择账号"
                               :loading="payLoading"
                               :remote-method="getPayList"
                               @change="selectNumber($event,'payNumber')"
                               @visible-change="selectVisible($event,'payNumber')"
                               @clear="clearSelect('payNumber')">
                        <el-option
                                v-for="payItem in payList"
                                :key="payItem.acc_id"
                                :label="payItem.acc_no"
                                :value="payItem.acc_id">
                        </el-option>
                    </el-select>
                </li>
                <li class="table-li-title">账号</li>
                <li class="table-li-content table-select">
                    <el-select v-model="editDialogData.recv_account_id"
                               clearable filterable remote
                               placeholder="请选择账号"
                               :loading="gatherLoading"
                               :remote-method="getGatherList"
                               @change="selectNumber($event,'gatherNumber')"
                               @visible-change="selectVisible($event,'gatherNumber')"
                               @clear="clearSelect('gatherNumber')">
                        <el-option
                                v-for="gatherItem in gatherList"
                                :key="gatherItem.acc_id"
                                :label="gatherItem.acc_no"
                                :value="gatherItem.acc_id">
                        </el-option>
                    </el-select>
                </li>

                <li class="table-li-title">开户行</li>
                <li class="table-li-content" v-text="editDialogData.pay_account_bank"></li>
                <li class="table-li-title">开户行</li>
                <li class="table-li-content" v-text="editDialogData.recv_account_bank"></li>

                <li class="table-li-title">调拨金额</li>
                <li class="table-li-content">
                    <input type="text" @blur="setMoney" class="table-input"
                           v-model="editDialogData.payment_amount">
                </li>
                <li class="table-li-title">大写</li>
                <li class="table-li-content" v-text="editDialogData.numText"></li>

                <li class="table-li-title">摘要</li>
                <li class="table-li-content table-two-row">
                    <input type="text" class="table-input" v-model="editDialogData.payment_summary">
                </li>

                <li class="table-li-title" style="height:60px;line-height:60px">附件</li>
                <li class="table-li-content table-two-row" style="height:60px;padding-top:6px;overflow-y:auto">
                    <Upload @currentFielList="setFileList"
                            :emptyFileList="editEmptyFile"
                            :isPending="true"
                            :fileMessage="fileMessage"
                            :triggerFile="eidttrigFile"></Upload>
                </li>
            </ul>
            <BusinessTracking
                v-show="editDialogData.service_status==5"
                :businessParams="editBusinessParams"
            ></BusinessTracking>
            <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" @click="saveBill">保 存</el-button>
                    <el-button type="warning" size="mini" @click="submitBill">提 交</el-button>
                </span>
        </el-dialog>
        <!--提交弹框-->
        <el-dialog :visible.sync="innerVisible"
                   width="50%" title="提交审批流程"
                   top="76px"
                   :close-on-click-modal="false">
            <el-radio-group v-model="selectWorkflow">
                <el-radio v-for="workflow in workflows"
                          :key="workflow.define_id"
                          :label="workflow.define_id"
                >{{ workflow.workflow_name }}
                    <el-button class="flow-tip-box" @click="showFlowDialog(workflow)"></el-button>
                </el-radio>
            </el-radio-group>
            <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="innerVisible = false">取 消</el-button>
                    <el-button type="warning" size="mini" @click="submitFlow">确 定</el-button>
                </span>
        </el-dialog>
        <!--查看工作流弹出框-->
        <el-dialog :visible.sync="lookFlowDialogVisible"
                   width="800px" title="查看流程"
                   :close-on-click-modal="false"
                   :before-close="cancelLookFlow"
                   top="120px">
            <WorkFlow
                    :flowList="flowList"
                    :isEmptyFlow="isEmptyFlow"
            ></WorkFlow>
        </el-dialog>
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";
    import BusinessTracking from "../publicModule/BusinessTracking.vue";
    import WorkFlow from "../publicModule/WorkFlow.vue";

    export default {
        name: "MoreBills",
        created: function () {
            this.$emit("transmitTitle", "内部调拨-更多单据");
            this.$emit("getCommTable", this.routerMessage);
            //业务类型
            this.$axios({
                url: this.queryUrl + "commProcess",
                method:"post",
                data:{
                    optype:"biztype_biztypes",
                    params: {
                        p_id: 8
                    }
                }
            }).then((result) =>{
                if (result.data.error_msg) {
                    this.$message({
                        type: "error",
                        message: result.data.error_msg,
                        duration: 2000
                    })
                } else {
                    var data = result.data.data;
                    this.payStatList = data;
                }
            }).catch(function (error) {
                console.log(error);
            });
            this.messageTips = {
                pay_mode: "请选择付款方式！",
                pay_account_id: "请选择付款方账号！",
                recv_account_id: "请选择收款方账号！",
                payment_amount: "请填写金额！"
            }
        },
        components: {
            Upload: Upload,
            BusinessTracking:BusinessTracking,
            WorkFlow: WorkFlow
        },
        mounted: function () {
            //调拨类型
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.ZjdbType) {
                this.paymentTypeList = constants.ZjdbType;
            }
            //付款方式
            if(constants.PayMode){
                this.payModeList = constants.PayMode;
            }
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "dbt_morelist",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                searchData: { //搜索条件
                    pay_mode: "",
                    payment_type: "",
                    biz_id: "",
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
                statusList: {
                    1: "已保存",
                    2: "已提交",
                    3: "审批中",
                    4: "审批通过",
                    5: "审批拒绝",
                    6: "处理中",
                    7: "已成功",
                    8: "已失败",
                    9: "已作废"
                },
                paymentTypeList: {}, //下拉框数据
                dialogVisible: false, //弹框数据
                dialogData: {},
                currentBill: {},
                editVisible: false,
                innerVisible: false, //提交弹框
                selectWorkflow: "",
                workflows: [],
                editDialogData: {
                    pay_account_name: "",
                    recv_account_name: "",
                    pay_account_no: "",
                    recv_account_no: "",
                    pay_account_bank: "",
                    recv_account_bank: "",
                    payment_amount: "",
                    numText: "",
                    payment_summary: "",
                    pay_mode: "",
                    biz_id: ""
                },
                payLoading: false, //付款方数据
                payList: [],
                gatherLoading: false, //收款方数据
                gatherList: [],
                payModeList: {}, //下拉框数据
                payStatList: [],
                emptyFileList: [], //附件
                fileMessage: {
                    bill_id: "",
                    biz_type: 8
                },
                triggerFile: false,
                editEmptyFile: [],
                eidttrigFile: false,
                fileList: [],
                lookBusinessParams:{},//业务状态追踪参数
                editBusinessParams:{}, //业务状态追踪参数
                payModeList:{}, //下拉框数据
                payStatList: [],
                messageTips: {},
                flowList: {},//查看流程
                isEmptyFlow: false,//
                lookFlowDialogVisible: false,
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
            //根据业务类型查询
            queryByPayType:function(val){
                var searchData = this.searchData;
                searchData.start_date = this.dateValue ? this.dateValue[0] : "";
                searchData.end_date = this.dateValue ? this.dateValue[1] : "";
                for (var k in searchData) {
                    if(k=='biz_id'){
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
            //展示格式转换-付款方式
            transitPayMode: function (row, column, cellValue, index){
                return this.payModeList[cellValue];
            },
            //制单
            goMakeBill: function () {
                this.$router.push("/allot/make-bill");
            },
            //支付处理
            goPayment: function () {
                this.$router.push("/allot/payment");
            },
            //复制
            copyMakeBill: function (current) {
                this.$router.push({
                    name: "MakeBill",
                    query: {
                        id: current.id
                    }
                });
            },
            //查看
            lookBill: function (row) {
                for (var k in row) {
                    this.dialogData[k] = row[k];
                }

                this.dialogData.numText = this.$common.transitText(row.payment_amount);
                this.dialogData.payment_amount = "￥" + this.$common.transitSeparator(row.payment_amount);
                this.dialogData.pay_mode = JSON.parse(window.sessionStorage.getItem("constants")).PayMode[row.pay_mode];
                this.dialogVisible = true;

                //附件数据
                this.emptyFileList = [];
                this.fileMessage.bill_id = row.id;
                this.triggerFile = !this.triggerFile;

                //业务状态跟踪
                this.lookBusinessParams = {};
                this.lookBusinessParams.biz_type = 8;
                this.lookBusinessParams.id = row.id;
            },
            //切换付款方式
            changePayMode: function(val){
                this.editDialogData.pay_account_name = "";
                this.editDialogData.pay_account_id = "";
                this.editDialogData.pay_account_bank = "";
            },
            //编辑
            editBill: function (row) {
                for (var k in row) {
                    this.editDialogData[k] = row[k];
                }
                this.currentBill = row;
                //下拉框列表
                this.payList = [];
                this.payList.push({
                    acc_id: row.pay_account_id,
                    acc_no: row.pay_account_no
                });
                this.gatherList = [];
                this.gatherList.push({
                    acc_id: row.recv_account_id,
                    acc_no: row.recv_account_no
                });
                this.editDialogData.numText = this.$common.transitText(row.payment_amount);
                this.editDialogData.payment_amount = this.$common.transitSeparator(row.payment_amount);
                this.editDialogData.pay_mode += "";

                //附件数据
                this.editEmptyFile = [];
                this.fileMessage.bill_id = row.id;
                this.eidttrigFile = !this.eidttrigFile;

                //审批拒绝显示业务追踪
                if(row.service_status == 5){
                    this.editBusinessParams = {};//清空数据
                    this.editBusinessParams.biz_type = 8;
                    this.editBusinessParams.id = row.id;
                }
                this.editVisible = true;
            },
            //删除
            removeBill: function (row, index, rows) {
                this.$confirm('确认删除当前单据吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url:  this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "dbt_del",
                            params: {
                                id: row.id,
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
                            return;
                        }

                        if (this.pagCurrent < (this.pagTotal / this.pagSize)) { //存在下一页
                            this.$emit("getCommTable", this.routerMessage);
                        } else {
                            if (rows.length == "1" && (this.routerMessage.todo.params.page_num != 1)) { //是当前页最后一条
                                this.routerMessage.todo.params.page_num--;
                                this.$emit("getCommTable", this.routerMessage);
                            } else {
                                rows.splice(index, 1);
                                this.pagTotal--;
                            }
                        }

                        this.$message({
                            type: "success",
                            message: "删除成功",
                            duration: 2000
                        })
                    }).catch(function (error) {
                        console.log(error);
                    })
                }).catch(() => {
                });
            },
            //撤回
            withdrawBill: function (row) {
                this.$confirm('确认撤回当前单据吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "dbt_revoke",
                            params: {
                                id: row.id,
                                persist_version: row.persist_version,
                                service_status: row.service_status
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {
                            this.$message({
                                type: "error",
                                message: result.data.error_msg,
                                duration: 2000
                            })
                            return;
                        } else {
                            this.$message({
                                type: "success",
                                message: "撤回成功",
                                duration: 2000
                            });
                            this.$emit("getCommTable", this.routerMessage);
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }).catch(() => {
                });
            },

            /*编辑弹框内功能*/
            //获取付款方账号
            getPayList: function (value) {
                this.payLoading = true;
                var billData = this.editDialogData;
                var interactive_mode = billData.pay_mode !='1' ? '2' : '1';
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "dbt_payacclist",
                        params: {
                            query_key: value.trim(),
                            exclude_ids: billData.recv_account_id,
                            page_size: 10000,
                            page_num: 1,
                            interactive_mode: interactive_mode
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {

                    } else {
                        this.payLoading = false;
                        this.payList = result.data.data;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //获取收款方账号
            getGatherList: function (value) {
                this.gatherLoading = true;
                var billData = this.editDialogData;

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "dbt_recvacclist",
                        params: {
                            query_key: value.trim(),
                            exclude_ids: billData.pay_account_id,
                            page_size: 10000,
                            page_num: 1
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {

                    } else {
                        this.gatherLoading = false;
                        this.gatherList = result.data.data;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //选择账号时设置户名和开户行
            selectNumber: function (value, target) {
                var payList = this.payList;
                var gatherList = this.gatherList;
                var billData = this.editDialogData;

                if (target == "payNumber") {
                    for (var i = 0; i < payList.length; i++) {
                        if (payList[i].acc_id == value) {
                            billData.pay_account_name = payList[i].acc_name;
                            billData.pay_account_bank = payList[i].bank_name;
                        }
                    }
                }
                if (target == "gatherNumber") {
                    for (var i = 0; i < gatherList.length; i++) {
                        if (gatherList[i].acc_id == value) {
                            billData.recv_account_name = gatherList[i].acc_name;
                            billData.recv_account_bank = gatherList[i].bank_name;
                        }
                    }
                }
            },
            //账号下拉框显示时更新数据
            selectVisible: function (curStatus, target) {
                if (curStatus) {
                    if (target == "payNumber") {
                        if(!this.editDialogData.pay_mode){
                            this.$message({
                                type:"warning",
                                message:"请先选择付款方式！",
                                duration:2000
                            });
                            return;
                        }
                        this.getPayList("");
                    } else {
                        this.getGatherList("");
                    }
                }else{
                    if (target == "payNumber") {
                        if(!this.editDialogData.pay_mode){
                            this.$message({
                                type:"warning",
                                message:"请先选择付款方式！",
                                duration:2000
                            });
                            return;
                        }
                    }
                }
            },
            //清空账号时清空户名和开户行
            clearSelect: function (target) {
                var billData = this.editDialogData;
                if (target == "payNumber") {
                    billData.pay_account_name = "";
                    billData.pay_account_bank = "";
                }
                if (target == "gatherNumber") {
                    billData.recv_account_name = "";
                    billData.recv_account_bank = "";
                }
            },
            //输入金额后进行格式化
            setMoney: function () {
                var moneyNum = this.editDialogData.payment_amount.replace(/,/gi, "").trim();
                /*校验数据格式是否正确*/
                if (moneyNum == "") {
                    return;
                }
                if (isNaN(moneyNum)) {
                    this.$message({
                        type: "warning",
                        message: "请输入正确的金额",
                        duration: 2000
                    });
                    this.editDialogData.payment_amount = "";
                    return;
                }
                var verify = moneyNum.split(".");
                if (verify[0].length > 10) {
                    this.$message({
                        type: "warning",
                        message: "整数位不能超过10位数",
                        duration: 2000
                    });
                    verify[0] = verify[0].slice(0, 10);
                    moneyNum = verify.join(".");
                }
                if (verify[1] && verify[1].length > 2) {
                    this.$message({
                        type: "warning",
                        message: "小数点后只能保留两位小数",
                        duration: 2000
                    });
                    verify[1] = verify[1].slice(0, 2);
                    moneyNum = verify.join(".");
                }

                //设置数字部分格式
                this.editDialogData.payment_amount = this.$common.transitSeparator(moneyNum);
                //设置汉字
                this.editDialogData.numText = this.$common.transitText(moneyNum);
            },
            //保存
            saveBill: function () {
                var params = this.setParams();
                if (!params) {
                    return;
                }

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "dbt_chg",
                        params: params
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        });
                    } else {
                        var data = result.data.data;
                        data.pay_mode += "";

                        for (var k in data) {
                            this.currentBill[k] = data[k];
                        }
                        this.editVisible = false;
                        this.$message({
                            type: "success",
                            message: "保存成功",
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //提交
            submitBill: function () {
                var params = this.setParams();
                if (!params) {
                    return;
                }

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "dbt_presubmit",
                        params: params
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        });
                    } else {
                        var data = result.data.data;
                        //设置表单数据
                        data.pay_mode += "";

                        for (var k in data) {
                            this.currentBill[k] = data[k];
                        }
                        //设置弹框数据
                        this.selectWorkflow = "";
                        this.workflows = data.workflows;

                        this.innerVisible = true;
                        this.editVisible = false;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //提交流程
            submitFlow: function () {
                var workflowData = this.currentBill;
                var params = {
                    define_id: this.selectWorkflow,
                    id: workflowData.id,
                    service_serial_number: workflowData.service_serial_number,
                    service_status: workflowData.service_status,
                    persist_version: workflowData.persist_version
                };

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "dbt_submit",
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
                        this.innerVisible = false;
                        this.$message({
                            type: "success",
                            message: "提交成功",
                            duration: 2000
                        });
                        this.$emit("getCommTable", this.routerMessage);
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //设置params
            setParams: function () {
                //校验数据是否完善 并设置发送给后台的数据
                var billData = this.editDialogData;
                var params = {
                    biz_id: "",
                    pay_mode: "",
                    pay_account_id: "",
                    recv_account_id: "",
                    payment_amount: "",
                    payment_summary: "",
                    files: []
                }
                for (var k in params) {
                    if (k != "payment_summary" && k != "files" && !billData[k]  && k != "biz_id") {
                        this.$message({
                            type: "warning",
                            message: this.messageTips[k],
                            duration: 2000
                        });
                        return false;
                    }
                    if (k == "payment_amount") {  //金额
                        params[k] = billData[k].split(",").join("");
                    } else if (k == "files") {  //附件
                        params[k] = this.fileList;
                    } else if(k == "biz_id"){
                        params[k] = billData[k];
                        var payStatList = this.payStatList;
                        for(var i = 0; i < payStatList.length; i++){
                            if(billData[k] == payStatList[i].biz_id){
                                params.biz_name = payStatList[i].biz_name;
                            }
                        }
                    } else {
                        params[k] = billData[k];
                    }
                }
                if (billData.id) {
                    params.id = billData.id;
                    params.persist_version = billData.persist_version;
                }
                return params;
            },

            /*附件*/
            //设置当前项上传附件
            setFileList: function ($event) {
                this.fileList = [];
                if ($event.length > 0) {
                    $event.forEach((item) => {
                        this.fileList.push(item.id);
                    })
                }
            },
            //展示提交流程详情
            showFlowDialog:function(workflow){
                this.lookFlowDialogVisible = true;
                this.$axios({
                    url: this.queryUrl + "commProcess",
                    method: "post",
                    data: {
                        optype: "wfquery_wfdetail",
                        params: {
                            id: workflow.id
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                        return;
                    } else {
                        let getData = result.data.data;
                        let define = getData.define;
                        //将数据传递给子组件
                        this.flowList = define;
                        this.isEmptyFlow = false;
                        
                    }
                })
            },
            cancelLookFlow:function(){
                this.isEmptyFlow = true;
                this.lookFlowDialogVisible = false;
                this.flowList = {};
            }
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

