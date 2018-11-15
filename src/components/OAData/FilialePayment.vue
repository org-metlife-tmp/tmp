<style scoped lang="less" type="text/less">
    #filialePayment {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion {
            text-align: left;

            /*时间控件*/
            .el-date-editor {
                width: 96%;
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

        /*撤回样式*/
        .withdraw {
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
            background-position: -48px 0;
        }

        /*查看弹框*/
        .dialog-talbe {
            width: 100%;
            height: 320px;

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
            }

            .table-two-row {
                width: 88%;
                margin-left: -3px;
                border-left: none;
            }
        }
        //顶部按钮
        .button-list-right {
            position: absolute;
            top: -60px;
            right: -18px;
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
    #filialePayment {
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 480px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="filialePayment">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="exportFun">导出</el-button>
        </div>
        <!--搜索区-->
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
                            <el-input v-model="searchData.bill_no" clearable
                                      placeholder="请输入报销单申请号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.recv_query_key" clearable
                                      placeholder="请输入收款方账户号或名称"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.org_name" clearable placeholder="请输入申请单位"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="5">
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
                    <el-col :span="10"></el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList" border
                      size="mini"
                      highlight-current-row>
                <el-table-column prop="bill_no" label="报销单申请号" :show-overflow-tooltip="true"
                                 width="120"></el-table-column>
                <el-table-column prop="org_name" label="申请单位" :show-overflow-tooltip="true"
                                 width="120"></el-table-column>
                <el-table-column prop="create_on" label="申请日期" :show-overflow-tooltip="true"
                                 width="100"></el-table-column>
                <el-table-column prop="update_on" label="发送日期" :show-overflow-tooltip="true"
                                 width="100" v-if="!isPending"></el-table-column>
                <el-table-column prop="pool_account_no" label="资金池账户" :show-overflow-tooltip="true"
                                 width="120"></el-table-column>
                <el-table-column prop="pay_pay_account_no" label="付款方账号" width="120"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_name" label="收款人" :show-overflow-tooltip="true"
                                 width="100"></el-table-column>
                <el-table-column prop="recv_account_no" label="收款方账号" :show-overflow-tooltip="true"
                                 width="120"></el-table-column>
                <el-table-column prop="recv_account_bank" label="收款方银行" :show-overflow-tooltip="true"
                                 width="120"></el-table-column>
                <el-table-column prop="payment_amount" label="收款金额" :show-overflow-tooltip="true"
                                 width="100"></el-table-column>
                <el-table-column prop="payment_summary" label="摘要" :show-overflow-tooltip="true"
                                 width="110"></el-table-column>
                <el-table-column label="状态" :show-overflow-tooltip="true" width="80">
                    <template slot-scope="scope">
                        <el-popover placement="top" title="失败原因"
                                    width="200" trigger="hover"
                                    :disabled="scope.row.service_status != 8"
                                    :content="scope.row.feed_back">
                            <span slot="reference" style="cursor:default">{{ constants[scope.row.service_status] }}</span>
                        </el-popover>
                    </template>
                </el-table-column>
                <el-table-column label="操作" width="110" fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="!isPending">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookData(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="isPending">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editData(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="作废" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="isPending || (!isPending && scope.row.service_status == 8)">
                            <el-button size="mini" icon="el-icon-circle-close-outline" type="danger"
                                       style="font-size:16px;padding: 1px;vertical-align: bottom;line-height: 15px;"
                                       @click="cancellationData(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="撤回" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="!isPending && scope.row.service_status == '2'">
                            <el-button size="mini" class="withdraw"
                                       @click="withdrawData(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
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
        <el-dialog title="分公司付款信息"
                   :visible.sync="dialogVisible"
                   width="900px" top="76px"
                   :close-on-click-modal="false">
            <div class="serial-number">
                [编号:
                <span v-text="dialogData.service_serial_number"></span>
                ]
            </div>
            <ul class="dialog-talbe">
                <li class="table-li-title">报销单申请号</li>
                <li class="table-li-content" v-text="dialogData.bill_no"></li>
                <li class="table-li-title">申请公司</li>
                <li class="table-li-content" v-text="dialogData.org_name"></li>

                <li class="table-li-title">付款账户号</li>
                <li class="table-li-content" v-text="dialogData.pay_account_no"></li>
                <li class="table-li-title">付款账户名称</li>
                <li class="table-li-content" v-text="dialogData.pay_account_name"></li>

                <li class="table-li-title">付款银行名称</li>
                <li class="table-li-content" v-text="dialogData.pay_account_bank"></li>
                <li class="table-li-title">CNAPS号</li>
                <li class="table-li-content" v-text="dialogData.pay_bank_cnaps"></li>

                <li class="table-li-title">银行所在省</li>
                <li class="table-li-content" v-text="dialogData.pay_bank_prov"></li>
                <li class="table-li-title">银行所在市</li>
                <li class="table-li-content" v-text="dialogData.pay_bank_city"></li>

                <li class="table-li-title">收款账户号</li>
                <li class="table-li-content" v-text="dialogData.recv_account_no"></li>
                <li class="table-li-title">收款账户名称</li>
                <li class="table-li-content" v-text="dialogData.recv_account_name"></li>

                <li class="table-li-title">收款银行名称</li>
                <li class="table-li-content" v-text="dialogData.recv_account_bank"></li>
                <li class="table-li-title">CNAPS号</li>
                <li class="table-li-content" v-text="dialogData.recv_bank_cnaps"></li>

                <li class="table-li-title">银行所在省</li>
                <li class="table-li-content" v-text="dialogData.recv_bank_prov"></li>
                <li class="table-li-title">银行所在市</li>
                <li class="table-li-content" v-text="dialogData.recv_bank_city"></li>

                <li class="table-li-title">付款方式</li>
                <li class="table-li-content" v-text="dialogData.pay_mode"></li>
                <li class="table-li-title">付款金额</li>
                <li class="table-li-content" style="color:#fd7d2f">￥{{ dialogData.payment_amount }}</li>

                <li class="table-li-title">单据状态</li>
                <li class="table-li-content" v-text="statusList[dialogData.service_status]"></li>
                <li class="table-li-title"></li>
                <li class="table-li-content"></li>

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
        <el-dialog title="分公司付款编辑"
                   :visible.sync="editVisible"
                   width="900px" top="76px"
                   :close-on-click-modal="false">
            <el-form :model="eidtData" size="small"
                     :label-width="formLabelWidth"
                     :rules="rules" ref="dialogForm">
                <el-row>
                    <el-col :span="12" v-show="eidtData.pay_account_id">
                        <el-form-item label="下拨账号">
                            <el-input v-model="eidtData.pool_account_no" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" v-show="eidtData.pay_account_id">
                        <el-form-item label="下拨金额">
                            <el-input v-model="eidtData.payment_amount" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="付款账号"  prop="pay_account_id">
                            <el-select v-model="eidtData.pay_account_id" placeholder="请选择付款账号"
                                       filterable @change="setPayInfo">
                                <el-option v-for="payItem in payList"
                                           :key="payItem.acc_id"
                                           :label="payItem.acc_no"
                                           :value="payItem.acc_id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="收款账号">
                            <el-input v-model="eidtData.recv_account_no" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="付款方">
                            <el-input v-model="eidtData.pay_account_name" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="收款方">
                            <el-input v-model="eidtData.recv_account_name" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行">
                            <el-input v-model="eidtData.pay_account_bank" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行">
                            <el-input v-model="eidtData.recv_account_bank" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="金额">
                            <el-input v-model="eidtData.payment_amount" placeholder="" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="摘要">
                            <el-input v-model="eidtData.payment_summary" placeholder="" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="附件">
                            <Upload :emptyFileList="emptyFileList"
                                    :fileMessage="fileMessage"
                                    :triggerFile="triggerFile"
                                    @currentFielList="setFileList"
                                    :isPending="true"></Upload>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <BusinessTracking
                v-show="eidtData.service_status==5"
                :businessParams="businessParams"
            ></BusinessTracking>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="editVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="subCurrent">保 存</el-button>
                <el-button type="warning" size="mini" @click="subFlow">提 交</el-button>
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
        name: "FilialePayment",
        created: function () {
            //设置当前页基本信息
            this.$emit("transmitTitle", "分公司付款");
            this.$emit("tableText", {
                leftTab: "未处理",
                rightTab: "已处理"
            });

            if(!this.isPending){
                this.statusList = {
                    2: "已提交",
                    3: "审批中",
                    4: "审批通过",
                    6: "处理中",
                    7: "已成功",
                    8: "已失败",
                    9: "已作废"
                }
            }

            this.$emit("getTableData", this.routerMessage);
        },
        mounted: function () {
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.BillStatus) {
                this.constants = constants.BillStatus;
            }
        },
        components: {
            Upload: Upload,
            BusinessTracking: BusinessTracking,
            WorkFlow: WorkFlow
        },
        props: ["isPending", "tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    todo: {
                        optype: "branchorgoa_oaTodoList",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    },
                    done: {
                        optype: "branchorgoa_oaDoneList",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    }
                },
                tableList: [],
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                searchData: { //搜索条件
                    bill_no: "",
                    recv_query_key: "",
                    org_name: "",
                    min: "",
                    max: "",
                    service_status: []
                },
                statusList: { //常量数据
                    1: "已保存",
                    5: "审批拒绝"
                },
                constants: {},
                dialogVisible: false, //弹框数据
                dialogData: {
                    pay_account_no: "",
                    pay_account_name: "",
                    pay_account_bank: "",
                    pay_bank_cnaps: "",
                    pay_bank_prov: "",
                    pay_bank_city: "",
                    recv_account_no: "",
                    recv_account_name: "",
                    recv_account_bank: "",
                    recv_bank_cnaps: "",
                    recv_bank_prov: "",
                    recv_bank_city: "",
                    pay_mode: "",
                    payment_amount: "",
                    service_status: "",
                    payment_summary: ""
                },
                //校验规则设置
                rules: {
                    pay_account_id: {
                        required: true,
                        message: "请选择付款账号",
                        trigger: "change"
                    }
                },
                editVisible: false,
                eidtData: {
                    pool_account_no: "",
                    payment_amount: "",
                    pay_account_id: "",
                    recv_account_no: "",
                    pay_account_name: "",
                    recv_account_name: "",
                    pay_account_bank: "",
                    recv_account_bank: "",
                    payment_summary: "",
                    poll_acc_id: "",
                    files: [],
                    service_status: ""
                },
                attrList: {},
                innerVisible: false, //提交弹框
                selectWorkflow: "",
                workflows: [],
                formLabelWidth: "120px",
                payList: [], // 下拉框数据
                emptyFileList: [], //附件
                fileMessage: {
                    bill_id: "",
                    biz_type: 21
                },
                triggerFile: false,
                businessParams: { //业务状态追踪参数
                },
                currData: {}, //
                flowList: {},//查看流程
                isEmptyFlow: false,//
                lookFlowDialogVisible: false,
                dateValue: "", //时间控件
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
            }
        },
        methods: {
            //导出
            exportFun: function(){
                if(this.tableList.length == 0){
                    this.$message({
                        type: "warning",
                        message: "当前数据为空",
                        duration: 2000
                    });
                    return;
                }
                var user = JSON.parse(window.sessionStorage.getItem("user"));
                var params = this.isPending ? this.routerMessage.todo.params : this.routerMessage.done.params;
                params.org_id = user.curUodp.org_id;
                this.$emit("exportData",{
                    optype: this.isPending ? "branchorgoa_todolistexport" : "branchorgoa_donelistexport",
                    params: params
                });
            },
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                searchData.apply_start_date = this.dateValue ? this.dateValue[0] : "";
                searchData.apply_end_date = this.dateValue ? this.dateValue[1] : "";
                for (var k in searchData) {
                    if (this.isPending) {
                        this.routerMessage.todo.params[k] = searchData[k];
                        this.routerMessage.todo.params.page_num = 1;
                    } else {
                        this.routerMessage.done.params[k] = searchData[k];
                        this.routerMessage.done.params.page_num = 1;
                    }
                }
                this.$emit("getTableData", this.routerMessage);
            },
            //换页后获取数据
            getCurrentPage: function (currPage) {
                if (this.isPending) {
                    this.routerMessage.todo.params.page_num = currPage;
                } else {
                    this.routerMessage.done.params.page_num = currPage;
                }
                this.$emit("getTableData", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.todo.params.page_size = val;
                this.routerMessage.todo.params.page_num = 1;

                this.routerMessage.done.params.page_size = val;
                this.routerMessage.done.params.page_num = 1;

                this.$emit("getTableData", this.routerMessage);
            },
            //展示格式转换-付款方式
            transitPayMode: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.PayMode) {
                    return constants.PayMode[cellValue];
                }
            },
            //展示格式转换-处理状态
            transitStatus: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.BillStatus) {
                    return constants.BillStatus[cellValue];
                }
            },
            //撤回
            withdrawData: function (row) {
                this.$confirm('确认撤回当前单据吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "branchorgoa_revoke",
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
                            });
                            return;
                        } else {
                            this.$message({
                                type: "success",
                                message: "撤回成功",
                                duration: 2000
                            });
                            this.$emit("getTableData", this.routerMessage);
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }).catch(() => {
                });
            },
            //作废
            cancellationData: function (row) {
                this.$prompt('请输入作废原因', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    title: "作废原因",
                    inputValidator: function(value){
                        if(!value){
                            return false;
                        }else{
                            return true;
                        }
                    },
                    inputErrorMessage: '请输入作废原因'
                }).then(({ value }) => {
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "branchorgoa_payOff",
                            params: {
                                ids: [row.id],
                                persist_version: [row.persist_version],
                                feed_back: value
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {
                            this.$message({
                                type: "error",
                                message: result.data.error_msg,
                                duration: 2000
                            });
                            return;
                        } else {
                            this.$message({
                                type: "success",
                                message: "作废成功",
                                duration: 2000
                            });
                            this.$emit("getTableData", this.routerMessage);
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }).catch(() => {

                });
            },
            //查看
            lookData: function (row) {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "branchorgoa_detail",
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
                        });
                        return;
                    } else {
                        var data = result.data.data;
                        var dialogData = this.dialogData;
                        for (var k in data) {
                            if (k == "pay_mode") {
                                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                                dialogData[k] = constants.PayMode[row[k]];
                            } else {
                                dialogData[k] = data[k];
                            }
                        }
                        this.dialogVisible = true;
                        //获取附件列表
                        this.emptyFileList = [];
                        this.fileMessage.bill_id = row.id;
                        this.triggerFile = !this.triggerFile;

                        //业务状态跟踪
                        this.businessParams = {};
                        this.businessParams.biz_type = 21;
                        this.businessParams.id = row.id;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //编辑
            editData: function (row) {
                //清空数据
                var dialogData = this.eidtData;
                for(var k in dialogData){
                    if(k == "files"){
                        dialogData[k]=[];
                    }else{
                        dialogData[k] = "";
                    }
                }
                this.getPayList(row.id);
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "branchorgoa_detail",
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
                        });
                        return;
                    } else {
                        var data = result.data.data;
                        this.editVisible = true;
                        var dialogData = this.eidtData;
                        for (var k in data) {
                            dialogData[k] = data[k];
                        }
                        //获取附件列表
                        this.emptyFileList = [];
                        this.fileMessage.bill_id = row.id;
                        this.triggerFile = !this.triggerFile;

                    }
                }).catch(function (error) {
                    console.log(error);
                })
                //审批拒绝显示业务追踪
                if(row.service_status == 5){
                    this.businessParams = {};//清空数据
                    this.businessParams.biz_type = 21;
                    this.businessParams.id = row.id;
                }
            },
            //获取付款账号列表
            getPayList: function(rowId) {
                //获取付款账号列表
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "branchorgoa_accListByOrg",
                        params: {
                            id: rowId
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        });
                        return;
                    } else {
                        var data = result.data.data;
                        this.payList = data;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //选择账号后设置其对应信息
            setPayInfo: function (payId) {
                if (payId) {
                    var payList = this.payList;
                    for (var i = 0; i < payList.length; i++) {
                        var item = payList[i];
                        if (item.acc_id == payId) {
                            this.$axios({
                                url: this.queryUrl + "normalProcess",
                                method: "post",
                                data: {
                                    optype: "branchorgoa_poolAccListByBankType",
                                    params: {
                                        bank_type: item.bank_type
                                    }
                                }
                            }).then((result) => {
                                if (result.data.error_msg) {
                                    this.$message({
                                        type: "error",
                                        message: result.data.error_msg,
                                        duration: 2000
                                    });
                                    return;
                                } else {
                                    var data = result.data.data;
                                    this.eidtData.pool_account_no = data.acc_no;
                                    this.eidtData.pool_account_id = data.acc_id;
                                }
                            }).catch(function (error) {
                                console.log(error);
                            });
                            this.eidtData.pay_account_name = item.acc_name;
                            this.eidtData.pay_account_bank = item.bank_name;
                            return false;
                        }
                    }
                }
            },
            //保存
            subCurrent: function () {
                this.$refs.dialogForm.validate((valid, object) => {
                    if (valid) {
                        var params = this.eidtData;
                        this.$axios({
                            url: this.queryUrl + "normalProcess",
                            method: "post",
                            data: {
                                optype: "branchorgoa_chgBranchPayment",
                                params: params
                            }
                        }).then((result) => {
                            if (result.data.error_msg) {
                                this.$message({
                                    type: "error",
                                    message: result.data.error_msg,
                                    duration: 2000
                                });
                                return;
                            } else {
                                var data = result.data.data;
                                this.$message({
                                    type: "success",
                                    message: "保存成功",
                                    duration: 2000
                                });
                                this.editVisible = false;
                                this.$emit("getTableData", this.routerMessage);
                            }
                        }).catch(function (error) {
                            console.log(error);
                        });
                    } else {
                        return false;
                    }
                });
            },
            //提交
            subFlow: function () {
                this.$refs.dialogForm.validate((valid, object) => {
                    if (valid) {
                        var params = this.eidtData;
                        this.$axios({
                            url: this.queryUrl + "normalProcess",
                            method: "post",
                            data: {
                                optype: "branchorgoa_presubmit",
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
                                this.eidtData.id = data.brand_payment.id;
                                this.eidtData.service_status = data.brand_payment.service_status;
                                this.eidtData.persist_version = data.brand_payment.persist_version;
                                //设置流程数据
                                this.selectWorkflow = "";
                                this.workflows = data.workflows;
                                this.innerVisible = true;
                                this.editVisible = false;
                                this.$emit("getTableData", this.routerMessage);
                            }
                        }).catch(function (error) {
                            console.log(error);
                        });
                    } else {
                        return false;
                    }
                });
            },
            //提交流程
            submitFlow: function () {
                var workflowData = this.eidtData;
                var params = {
                    define_id: this.selectWorkflow,
                    id: workflowData.id,
                    service_serial_number : workflowData.service_serial_number,
                    service_status: workflowData.service_status,
                    persist_version: workflowData.persist_version
                };

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "branchorgoa_submit",
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
                        var data = result.data.data;
                        this.innerVisible = false;
                        this.$message({
                            type: "success",
                            message: "提交成功",
                            duration: 2000
                        });
                        this.$emit("getTableData", this.routerMessage);
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //设置当前项上传附件
            setFileList: function ($event) {
                this.eidtData.files = [];
                if ($event.length > 0) {
                    $event.forEach((item) => {
                        this.eidtData.files.push(item.id);
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
            isPending: function (val, oldVal) {
                if (val) {
                    this.statusList = {
                        1: "已保存",
                        5: "审批拒绝"
                    }
                } else {
                    this.statusList = {
                        2: "已提交",
                        3: "审批中",
                        4: "审批通过",
                        6: "处理中",
                        7: "已成功",
                        8: "已失败",
                        9: "已作废"
                    }
                }
                var searchData = this.searchData;
                for (var k in searchData) {
                    if (k == "service_status") {
                        searchData[k] = [];
                    } else {
                        searchData[k] = "";
                    }
                }
                this.dateValue = "";
            },
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data;
            }
        }
    }
</script>


