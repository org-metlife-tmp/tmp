<style scoped lang="less" type="text/less">
    #payMoreBills {
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

        /*列表数据*/
        .table-content{
            height: 289px;
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
            height: 180px;
            margin-bottom: 30px;

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
</style>
<style lang="less" type="text/less">
    #payMoreBills {
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
    <div id="payMoreBills">
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
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      border size="mini"
                      height="100%">
                <el-table-column prop="recv_account_name" label="收款方名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_no" label="收款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_bank" label="收款方银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_mode" label="付款方式" :show-overflow-tooltip="true"
                                 :formatter="transitPayMode"></el-table-column>
                <el-table-column prop="payment_amount" label="金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="service_status" label="业务状态" :show-overflow-tooltip="true"
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
                <li class="table-li-title">付款账号</li>
                <li class="table-li-content table-two-row" v-text="dialogData.pay_account_no"></li>

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
        <!--编辑弹出框-->
        <el-dialog title="编辑支付单"
                   :visible.sync="editVisible"
                   width="900px" top="76px"
                   :close-on-click-modal="false">
            <div class="serial-number">
                [编号:
                <span v-text="editDialogData.service_serial_number"></span>
                ]
            </div>
            <ul class="dialog-talbe">
                <li class="table-li-title">付款方式</li>
                <li class="table-li-content table-select">
                    <el-select v-model="editDialogData.pay_mode" placeholder="请选择付款方式"
                               filterable size="mini"
                               @change="clearPayAcc">
                        <el-option v-for="(item,k) in payModeList"
                                   :key="k"
                                   :label="item"
                                   :value="k">
                        </el-option>
                    </el-select>
                </li>
                <li class="table-li-title">业务类型</li>
                <li class="table-li-content table-select">
                    <el-select v-model="editDialogData.biz_id" placeholder="请选择业务类型"
                               filterable clearable size="mini" @change="setBizName">
                        <el-option v-for="payItem in payStatList"
                                   :key="payItem.biz_id"
                                   :label="payItem.biz_name"
                                   :value="payItem.biz_id">
                        </el-option>
                    </el-select>
                </li>

                <li class="table-li-title">付款账号</li>
                <li class="table-li-content table-select">
                    <el-select v-model="editDialogData.pay_account_id" placeholder="请选择付款方"
                               filterable size="mini"
                               @visible-change="getOptions">
                        <el-option v-for="item in accOptions"
                                   :key="item.acc_id"
                                   :label="item.acc_no"
                                   :value="item.acc_id">
                        </el-option>
                    </el-select>
                </li>
                <li class="table-li-title">收款人户名</li>
                <li class="table-li-content table-select">
                    <el-select v-model="editDialogData.recv_account_name"
                               filterable allow-create default-first-option
                               placeholder="请输入或选择户名"
                               @change="setPayer($event,'acc_no')">
                        <el-option
                                v-for="item in payerList"
                                :key="item.id"
                                :label="item.acc_name"
                                :value="item.acc_name">
                        </el-option>
                    </el-select>
                </li>

                <li class="table-li-title">收款人账号</li>
                <li class="table-li-content table-select">
                    <el-select v-model="editDialogData.recv_account_no"
                               filterable allow-create
                               default-first-option
                               placeholder="请输入或选择账号"
                               @change="setPayer($event,'acc_name')">
                        <el-option
                                v-for="item in payerList"
                                :key="item.id"
                                :label="item.acc_no"
                                :value="item.acc_no">
                        </el-option>
                    </el-select>
                </li>
                <li class="table-li-title">开户行</li>
                <li class="table-li-content">
                    <input type="text" placeholder="请选择开户行" class="table-input"
                           v-model="editDialogData.bank_name"
                           @focus="clearBankDialog">
                </li>

                <li class="table-li-title">金额</li>
                <li class="table-li-content">
                    <input type="text" @blur="setMoney" class="table-input" style="color:#fd7d2f"
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
                :businessParams="businessParams"
            ></BusinessTracking>
            <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" @click="saveBill">保 存</el-button>
                    <el-button type="warning" size="mini" @click="submitBill">提 交</el-button>
                </span>
        </el-dialog>
        <!--开户行选择弹框-->
        <el-dialog :visible.sync="bankVisible"
                   width="40%" title="选择开户行"
                   top="140px" :close-on-click-modal="false">

            <el-form :model="bankDialogData" size="small">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="银行大类" :label-width="formLabelWidth">
                            <el-select v-model="bankDialogData.bankTypeName" placeholder="请选择银行大类"
                                       clearable filterable
                                       style="width:100%"
                                       :filter-method="filterBankType"
                                       @visible-change="clearSearch"
                                       @change="bankIsSelect">
                                <el-option v-for="bankType in bankTypeList"
                                           :key="bankType.name"
                                           :label="bankType.display_name"
                                           :value="bankType.code">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="地区" :label-width="formLabelWidth">
                            <el-select v-model="bankDialogData.area"
                                       filterable remote clearable
                                       style="width:100%"
                                       placeholder="请输入地区关键字"
                                       :remote-method="getAreaList"
                                       :loading="loading"
                                       @change="bankIsSelect">
                                <el-option
                                        v-for="area in areaList"
                                        :key="area.name + '-' + area.top_super"
                                        :value="area.name + '-' + area.top_super">
                                    <span>{{ area.name }}</span><span style="margin-left:10px;color:#bbb">{{ area.top_super }}</span>
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="开户行" :label-width="formLabelWidth">
                            <el-select v-model="bankDialogData.bank_name" placeholder="请选择银行"
                                       clearable filterable style="width:100%"
                                       @visible-change="getBankList"
                                       @change="setCNAPS"
                                       :disabled="bankSelect">
                                <el-option v-for="bankType in bankList"
                                           :key="bankType.cnaps_code"
                                           :label="bankType.name"
                                           :value="bankType.name">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="CNAPS" :label-width="formLabelWidth">
                            <el-input v-model="bankDialogData.cnaps_code" readonly></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>

            <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="bankVisible = false">取 消</el-button>
                    <el-button type="warning" size="mini" @click="confirmBank">确 定</el-button>
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
        name: "PayMoreBills",
        created: function () {
            this.$emit("transmitTitle", "资金支付-更多单据");
            this.$emit("getCommTable", this.routerMessage);

            //获取付款方账户列表
            this.$axios({
                url:"/cfm/commProcess",
                method:"post",
                data:{
                    optype:"account_normallist",
                    params:{
                        status:1,
                        acc_id:""
                    }
                }
            }).then((result) =>{
                if (result.data.error_msg) {

                } else {
                    this.accOptions = result.data.data;
                }
            });

            //业务类型
            this.$axios({
                url:"/cfm/commProcess",
                method:"post",
                data:{
                    optype:"biztype_biztypes",
                    params: {
                        p_id: 9
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
            })

            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
                this.bankTypeList = bankTypeList;
            }
            var bankAllTypeList = JSON.parse(window.sessionStorage.getItem("bankAllTypeList"));
            if(bankAllTypeList){
                this.bankAllTypeList = bankAllTypeList;
            }
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.PayMode) {
                this.payModeList = constants.PayMode;
            }
        },
        components: {
            Upload: Upload,
            BusinessTracking:BusinessTracking,
            WorkFlow: WorkFlow
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: {
                    optype: "zft_morebills",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                searchData: { //搜索条件
                    pay_query_key: "",
                    recv_query_key: "",
                    pay_mode: "",
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
                dialogVisible: false, //弹框数据
                dialogData: {},
                emptyFileList: [], //附件
                fileMessage: {
                    bill_id: "",
                    biz_type: 9
                },
                triggerFile: false,
                editVisible: false, //编辑弹框数据
                editDialogData: {
                    id:"",
                    biz_id: "",
                    biz_name: "",
                    pay_mode: "",
                    persist_version: "",
                    rev_persist_version: "",
                    pay_account_id: "",
                    recv_account_id: "",
                    recv_account_no: "",
                    recv_account_name: "",
                    recv_bank_cnaps: "",
                    payment_amount: "",
                    payment_summary: "",
                    service_serial_number: "",
                    bank_name: "",
                    numText: "",
                    service_status: ""
                },
                currentBill: {},
                editEmptyFile: [], //编辑附件
                eidttrigFile: false,
                fileList: [],
                accOptions: [],//编辑弹框下拉框数据
                payStatList: [],
                payerList: [],
                bankVisible: false, //银行选择弹框
                bankDialogData: {
                    bankTypeName: "",
                    area: "",
                    bank_name: "",
                    cnaps_code: ""
                },
                formLabelWidth: "100px",
                bankAllList: [],
                bankTypeList: [],
                bankAllTypeList: [], //银行大类全部(不重复)
                areaList: [],
                loading: false,
                bankList: [],
                bankSelect: true,
                innerVisible: false, //提交弹框
                selectWorkflow: "",
                workflows: [],
                businessParams:{ //业务状态追踪参数
                },
                flowList: {},//查看流程
                isEmptyFlow: false,//
                lookFlowDialogVisible: false,
            }
        },
        methods: {
            //同时保存biztype和name
            setBizName: function(val){
                var payStatList = this.payStatList;
                for(var i = 0; i < payStatList.length; i++){
                    if(payStatList[i].biz_id == val){
                        this.billData.biz_name = payStatList[i].biz_name;
                    }
                }
            },
            //清空付款方账号
            clearPayAcc: function(){
                this.editDialogData.pay_account_id = "";
            },
            //获取付款方账号列表
            getOptions: function(status){
                if(status){
                    var pay_mode = this.editDialogData.pay_mode;
                    //获取付款方账户列表
                    this.$axios({
                        url:"/cfm/commProcess",
                        method:"post",
                        data:{
                            optype:"account_normallist",
                            params:{
                                status:1,
                                interactive_mode: pay_mode ? (pay_mode == "1" ? "1" : "2") : ""
                            }
                        }
                    }).then((result) =>{
                        if (result.data.error_msg) {

                        } else {
                            this.accOptions = result.data.data;
                        }
                    });
                }
            },
            //获取户名和账号的下拉列表值
            getPayerSelect: function(){
                //获取收款方户名列表
                this.$axios({
                    url:"/cfm/normalProcess",
                    method:"post",
                    data:{
                        optype:"zft_payacclist",
                        params:{
                            page_num:1,
                            page_size: 10000
                        }
                    }
                }).then((result) =>{
                    if (result.data.error_msg) {

                    } else {
                        this.payerList = result.data.data;
                    }
                });
            },
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
            //换页后获取数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getCommTable", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.params = {
                    page_size: val,
                    page_num: 1
                };
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
            //复制
            copyMakeBill: function (current) {
                this.$router.push({
                    name: "PayMakeBill",
                    query: {
                        id: current.id
                    }
                });
            },
            //查看
            lookBill: function (row) {
                for (var k in row) {
                    if(k == "payment_amount"){
                        this.dialogData[k] = "￥" + this.$common.transitSeparator(row.payment_amount);
                    }else{
                        this.dialogData[k] = row[k];
                    }
                }
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
            //删除
            removeBill: function (row, index, rows) {
                this.$confirm('确认删除当前单据吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/normalProcess",
                        method: "post",
                        data: {
                            optype: "zft_delbill",
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
                        url: "/cfm/normalProcess",
                        method: "post",
                        data: {
                            optype: "zft_revoke",
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
            //编辑
            editBill: function (row) {
                this.getPayerSelect();
                this.currentBill = row;

                var editDialogData = this.editDialogData;
                for(var k in editDialogData){
                    if(k == "bank_name"){
                        editDialogData[k] = row.recv_account_bank;
                    } else if (k == "pay_mode"){
                        if(row[k]){
                            editDialogData[k] = row[k] + "";
                        }else{
                            editDialogData[k] = "";
                        }
                    } else {
                        editDialogData[k] = row[k];
                    }
                }
                this.getOptions(true);
                //设置数字加千分符和转汉字
                editDialogData.numText = this.$common.transitText(row.payment_amount);
                editDialogData.payment_amount = this.$common.transitSeparator(row.payment_amount);
                //附件数据
                this.editEmptyFile = [];
                this.fileMessage.bill_id = row.id;
                this.eidttrigFile = !this.eidttrigFile;

                //审批拒绝显示业务追踪
                if(row.service_status == 5){
                    this.businessParams = {};//清空数据
                    this.businessParams.biz_type = 9;
                    this.businessParams.id = row.id;
                }
                this.editVisible = true;
            },
            /*编辑弹框内功能*/
            //修改户名和账号后改变对应的值
            setPayer: function(val,target){
                var payerList = this.payerList;
                var editDialogData = this.editDialogData;
                var flag = true;
                var key = target == "acc_no" ? "acc_name" : "acc_no";

                for(var i = 0; i < payerList.length; i++){
                    if(payerList[i][key] == val){
                        editDialogData.recv_account_id = payerList[i].id;
                        editDialogData.recv_bank_cnaps = payerList[i].cnaps_code;
                        editDialogData.rev_persist_version = payerList[i].persist_version;
                        if(target == "acc_no"){
                            editDialogData.recv_account_no = payerList[i][target];
                        }else{
                            editDialogData.recv_account_name = payerList[i][target];
                        }
                        editDialogData.bank_name = payerList[i].bank_name;
                        flag = false;
                        continue;
                    }
                    if(editDialogData.recv_account_no == payerList[i].acc_no){
                        flag = false;
                    }
                }
                if(flag){
                    editDialogData.recv_account_id = "";
                    editDialogData.rev_persist_version = "";
                }
            },
            //清空开户行选择弹框数据
            clearBankDialog: function(){
                this.bankVisible = true;
                this.bankSelect = true;
                var bankDialogData = this.bankDialogData;
                for(var k in bankDialogData){
                    bankDialogData[k] = "";
                }
            },
            //银行大类搜索筛选
            filterBankType: function (value) {
                if (value && value.trim()) {
                    this.bankTypeList = this.bankAllList.filter(item => {
                        var chineseReg = /^[\u0391-\uFFE5]+$/; //判断是否为中文
                        var englishReg = /^[a-zA-Z]+$/; //判断是否为字母
                        var quanpinReg = /(a[io]?|ou?|e[inr]?|ang?|ng|[bmp](a[io]?|[aei]ng?|ei|ie?|ia[no]|o|u)|pou|me|m[io]u|[fw](a|[ae]ng?|ei|o|u)|fou|wai|[dt](a[io]?|an|e|[aeio]ng|ie?|ia[no]|ou|u[ino]?|uan)|dei|diu|[nl][gh]ei|[jqx](i(ao?|ang?|e|ng?|ong|u)?|u[en]?|uan)|([csz]h?|r)([ae]ng?|ao|e|i|ou|u[ino]?|uan)|[csz](ai?|ong)|[csz]h(ai?|uai|uang)|zei|[sz]hua|([cz]h|r)ong|y(ao?|[ai]ng?|e|i|ong|ou|u[en]?|uan))/; //判断是否为全拼

                        if (chineseReg.test(value)) {
                            return item.name.toLowerCase().indexOf(value.toLowerCase()) > -1;
                        } else if (englishReg.test(value)) {
                            if (quanpinReg.test(value)) {
                                return item.pinyin.toLowerCase().indexOf(value.toLowerCase()) > -1;
                            } else {
                                return item.jianpin.toLowerCase().indexOf(value.toLowerCase()) > -1;
                            }
                        }
                    });
                    this.bankTypeList = this.bankTypeList.filter((item,index,arr) => {
                        for(var i = index+1; i < arr.length; i++){
                            if(item.display_name == arr[i].display_name){
                                return false;
                            }
                        }
                        return true;
                    });
                } else {
                    this.bankTypeList = this.bankAllTypeList;
                }
            },
            //重置银行大类数据
            clearSearch: function (val) {
                if (this.bankTypeList != this.bankAllTypeList && val) {
                    this.bankTypeList = this.bankAllTypeList;
                }
            },
            //银行大类/地址变化后判断银行是否可选
            bankIsSelect: function (value) {
                this.bankList = [];
                if (this.bankDialogData.area && this.bankDialogData.bankTypeName) {
                    this.bankSelect = false;
                } else {
                    this.bankSelect = true;
                }
            },
            //地区数据
            getAreaList: function (query) {
                if (query && query.trim()) {
                    this.loading = true;
                    this.$axios({
                        url: "/cfm/commProcess",
                        method: "post",
                        data: {
                            optype: "area_list",
                            params: {
                                query_key: query.trim()
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {

                        } else {
                            this.loading = false;
                            this.areaList = result.data.data;
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                } else {
                    this.areaList = [];
                }
            },
            //获取银行列表
            getBankList: function (status) {
                if (status) {
                    var area_code = this.bankDialogData.area.split("-");
                    var bank_type = this.bankDialogData.bankTypeName;

                    this.$axios({
                        url: "/cfm/commProcess",
                        method: "post",
                        data: {
                            optype: "bank_list",
                            params: {
                                province: area_code[1],
                                city: area_code[0],
                                bank_type: bank_type
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {
                        } else {
                            this.bankList = result.data.data;
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }
            },
            //设置CNPAS
            setCNAPS: function(value){
                var bankList = this.bankList;
                for(var i = 0; i < bankList.length; i++){
                    if(bankList[i].name == value){
                        this.bankDialogData.cnaps_code = bankList[i].cnaps_code;
                        return;
                    }
                }
            },
            //设置开户行
            confirmBank: function(){
                this.editDialogData.recv_bank_cnaps = this.bankDialogData.cnaps_code;
                this.editDialogData.bank_name = this.bankDialogData.bank_name;
                this.bankVisible = false;
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
            //设置当前项上传附件
            setFileList: function ($event) {
                this.fileList = [];
                if ($event.length > 0) {
                    $event.forEach((item) => {
                        this.fileList.push(item.id);
                    })
                }
            },
            //设置params
            setParams: function () {
                var params = {};
                var editDialogData = this.editDialogData;
                //校验
                var validater = {
                    pay_account_id: "请选择付款方",
                    pay_mode: "请选择付款方式",
                    biz_id: "请选择业务类型",
                    recv_account_name: "请选择户名",
                    recv_account_no: "请选择账号",
                    bank_name: "请选择开户行",
                    payment_amount: "请输入金额",
                }
                for(var k in validater){
                    if(!editDialogData[k]){
                        this.$message({
                            type: "warning",
                            message: validater[k],
                            duration: 2000
                        });
                        return false;
                    }
                }
                for(var k in editDialogData){
                    if(k == "payment_amount"){
                        params[k] = editDialogData[k].split(",").join("");
                    }else{
                        params[k] = editDialogData[k];
                    }
                }
                params.files = this.fileList;

                return params;
            },
            //保存
            saveBill: function () {
                var params = this.setParams();
                if (!params) {
                    return;
                }

                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "zft_chgbill",
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
                        for (var k in data) {
                            this.currentBill[k] = data[k];
                        }
                        this.editVisible = false;
                        //更新版本号
                        var tableList = this.tableList;
                        tableList.forEach((item) => {
                            if(item.recv_account_id == data.recv_account_id){
                                item.rev_persist_version = data.rev_persist_version;
                            }
                        });

                        this.getPayerSelect();
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
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "zft_presubmit",
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
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "zft_submit",
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
            //展示提交流程详情
            showFlowDialog:function(workflow){
                this.lookFlowDialogVisible = true;
                this.$axios({
                    url: "/cfm/commProcess",
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


