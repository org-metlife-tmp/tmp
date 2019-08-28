<style scoped lang="less" type="text/less">
    #headOfficePay {
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
        /*通过样式*/
        .pass{
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
            background-position: -417px -62px;
        }
    }
</style>

<template>
    <el-container id="headOfficePay">
        <el-header>
            <div class="button-list-right">
                <el-button type="warning" size="mini" @click="exportFun">导出</el-button>
            </div>
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
                                <el-input v-model="searchData.org_name" clearable placeholder="请输入申请单位"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.flow_id" clearable
                                          placeholder="请输入结算号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.recv_account_query_key" clearable
                                          placeholder="请输入收款方账户号或名称"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="5">
                            <el-form-item>
                                <el-col :span="11">
                                    <el-input v-model.number="searchData.min" @blur="numberRule('min')" clearable placeholder="最小金额"></el-input>
                                </el-col>
                                <el-col class="line" :span="1" style="text-align:center">-</el-col>
                                <el-col :span="11">
                                    <el-input v-model.number="searchData.max" @blur="numberRule('max')" clearable placeholder="最大金额"></el-input>
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
            <el-table :data="tableList" border
                      size="mini" height="100%"
                      highlight-current-row>
                <el-table-column prop="flow_id" label="结算号" :show-overflow-tooltip="true"
                                 width="120"></el-table-column>
                <el-table-column prop="send_count" label="推送次数" :show-overflow-tooltip="true"
                                 width="80"></el-table-column>
                <el-table-column prop="apply_user" label="申请人" :show-overflow-tooltip="true"
                                 width="120"></el-table-column>
                <el-table-column prop="org_name" label="申请单位" :show-overflow-tooltip="true"
                                 width="120"></el-table-column>
                <el-table-column prop="apply_date" label="支付日期" :show-overflow-tooltip="true"
                                 width="100"></el-table-column>
                <el-table-column prop="pay_account_no" label="付款方账号" :show-overflow-tooltip="true"
                                 width="120"></el-table-column>
                <el-table-column prop="pay_account_bank" label="付款方银行" :show-overflow-tooltip="true"
                                 width="120"></el-table-column>
                <el-table-column prop="payment_amount" label="收款金额" :show-overflow-tooltip="true"
                                 width="100" :formatter="transitAmount" ></el-table-column>
                <el-table-column prop="recv_account_name" label="收款人" :show-overflow-tooltip="true"
                                 width="100"></el-table-column>
                <el-table-column prop="recv_account_no" label="收款方账号" :show-overflow-tooltip="true"
                                 width="120"></el-table-column>
                <el-table-column prop="recv_account_bank" label="收款方开户行" :show-overflow-tooltip="true"
                                 width="120"></el-table-column>
                <el-table-column prop="payment_summary" label="摘要" :show-overflow-tooltip="true"
                                 width="100"></el-table-column>
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
                    <template slot-scope="scope" class="operationBtn" >
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    >
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookData(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="提交" placement="bottom" effect="light"
                                     :enterable="false" :open-delay="500" v-show="scope.row.service_status == 0||scope.row.service_status == 10"
                        >
                            <el-button type="primary"  size="mini" :disabled="scope.row.service_status == 10"
                                      class="pass"  @click="submitBill(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="作废" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-show="scope.row.service_status == 0||scope.row.service_status == 10">
                            <el-button size="mini" icon="el-icon-circle-close-outline" type="danger"
                                       style="font-size:16px;padding: 1px;vertical-align: bottom;line-height: 15px;"
                                       :disabled="scope.row.service_status == 10"
                                       @click="cancellationData(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>
        <el-footer>
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
            <!--查看弹出框-->
            <el-dialog title="总公司付款信息"
                       :visible.sync="dialogVisible"
                       width="900px" top="76px"
                       :close-on-click-modal="false">
                <div class="serial-number">
                    [编号:<span v-text="dialogData.service_serial_number"></span>]
                </div>
                <ul class="dialog-talbe">
                    <li class="table-li-title">结算号</li>
                    <li class="table-li-content" v-text="dialogData.flow_id"></li>
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
                    <li class="table-li-content" style="color:#fd7d2f" >{{ dialogData.payment_amount }}</li>

                    <li class="table-li-title">单据状态</li>
                    <li class="table-li-content" v-text="statusList[dialogData.service_status]"></li>
                    <li class="table-li-title"></li>
                    <li class="table-li-content"></li>

                    <li class="table-li-title">摘要</li>
                    <li class="table-li-content table-two-row" v-text="dialogData.payment_summary"></li>
                </ul>
                <span slot="footer" class="dialog-footer"  v-show="dialogData.service_status == 0">
                     <el-button type="warning"  size="mini" :disabled="dialogData.service_status == 10"
                                 @click="submitBill1(dialogData)" icon="el-icon-circle-check-outline">提 交</el-button>
                    </span>
            </el-dialog>
        </el-footer>
    </el-container>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";
    import BusinessTracking from "../publicModule/BusinessTracking.vue"

    export default {
        name: "NCHeadOfficePay",
        created: function () {
            //设置当前页基本信息
            this.$emit("transmitTitle", "总公司付款");
            this.$emit("tableText", {
            });
            this.statusList = {
                0: "未提交",
                6: "处理中",
                7: "已成功",
                8: "已失败",
                9: "已作废"
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
            BusinessTracking:BusinessTracking
        },
        props: ["isPending", "tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    todo: {
                        optype: "headorgnc_todolist",
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
                    recv_account_query_key: "",
                    flow_id: "",
                    org_name: "",
                    min: "",
                    max: "",
                    service_status: []
                },
                statusList: { //常量数据
                },
                constants: {},
                dialogVisible: false, //弹框数据
                btnDisabled:false,
                dialogData: {},
                emptyFileList: [], //附件
                fileMessage: {
                    bill_id: "",
                    biz_type: 20
                },
                triggerFile: false,
                businessParams:{ //业务状态追踪参数
                },
                dateValue: "", //时间控件
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
            }
        },
        methods: {
            //转换日期
            transitionDate: function (row, column, cellValue, index) {
                if(cellValue){
                    return cellValue.slice(0,4)+"-"+cellValue.slice(4,6)+"-"+cellValue.slice(6,8);
                }
            },
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
                    optype: "headorgnc_todolistexport",
                    params: params
                });
            },
            //提交
            submitBill1: function (row) {
                row.service_status='10';
                this.dialogVisible=false;
                var params = {
                    id:row.id,
                };
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "headorgnc_submit",
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
                        this.queryData();
                        this.dialogVisible = false;
                        this.$message({
                            type: "success",
                            message: result.data.data,
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                });
                this.btnDisabled=false;
            },
            //提交
            submitBill: function (row) {
                row.service_status='10';
                this.btnDisabled=true;
                var params = {
                    id:row.id,
                };
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "headorgnc_submit",
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
                        this.queryData();
                        this.dialogVisible = false;
                        this.$message({
                        type: "success",
                        message: result.data.data,
                        duration: 2000
                    });
                    }
                }).catch(function (error) {
                    console.log(error);
                });
                this.btnDisabled=false;
            },
            numberRule: function (target) {
                var targetVal = this.searchData[target];
                if (targetVal && (typeof targetVal != "number" || targetVal < 0)) {
                    this.$message({
                        type: "warning",
                        message: "请输入大于零的数字",
                        duration: 2000
                    });
                    this.searchData[target] = "";
                }
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
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.todo.params.page_size = val;
                this.routerMessage.todo.params.page_num = 1;

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
            //查看
            lookData: function(row){
                this.dialogVisible = true;
                this.dialogData = {};

                for(var k in row){
                    if(k == "pay_mode"){
                        var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                        this.dialogData[k] = constants.PayMode[row[k]];
                    }else{
                        this.dialogData[k] = row[k];
                    }
                }
                this.dialogData.payment_amount=this.$common.transitSeparator(row.payment_amount);
                //获取附件列表
                this.emptyFileList = [];
                this.fileMessage.bill_id = row.id;
                this.triggerFile = !this.triggerFile;

                //业务状态跟踪
                this.businessParams = {};
                this.businessParams.biz_type = 20;
                this.businessParams.id = row.id;
            },
            //编辑
            editData: function(row){
                this.$router.push({
                    name: "NCMakeBill",
                    query: {
                        id: row.id
                    }
                });
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
                            optype: "headorgnc_revoke",
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
                            optype: "headorgnc_payOff",
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
        },
        watch: {
            isPending: function (val, oldVal) {
                if (val) {
                    this.statusList = {
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
