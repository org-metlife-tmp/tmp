<style lang="less" type="text/less">
    #OAMakeBill {
        min-width: 980px;
        width: 80%;
        height: 100%;
        margin: 0 auto;

        /*标题*/
        header button {
            float: right;
            margin-top: -40px;
        }

        /*内容*/
        section {
            width: 100%;
            height: 512px;
            background-color: #fff;
            box-sizing: border-box;
            padding: 20px 20px 60px;
            margin-bottom: 10px;
        }

        /*表格顶部内容*/
        .title-date {
            color: #ccc;
            height: 40px;
            text-align: left;

            > span {
                margin-right: 10px;
            }

            .serial-number {
                float: right;
                margin-top: 10px;
            }
        }

        /*表格*/
        .bill-content {
            width: 100%;
            height: 388px;
            color: #363636;
            box-sizing: border-box;
            border: 3px solid #dde0e0;
            background: url(../../assets/slice_bg.png) repeat;

            table {
                width: 100%;
                height: 100%;
                border-collapse: collapse;
            }

            td {
                border: 1px solid #dde0e0;
            }

            .title-erect {
                width: 60px;
                text-align: center;
                padding: 0 16px;
                box-sizing: border-box;
                line-height: 20px;
            }

            .title-small {
                width: 82px;
                text-align: center;
                letter-spacing: 1em;
                text-indent: 1em;
                text-align: center;
            }

            .title-space {
                height: 2px;
            }

            .set-space {
                letter-spacing: 2em;
                text-indent: 2em;
            }

            .select-height {
                height: 56px;
                padding: 0;

                .el-select {
                    height: 100%;

                    .el-input {
                        height: 100%;

                        input {
                            height: 100%;
                        }
                    }
                }
            }

            .el-select {
                width: 100%;

                input {
                    height: 56px;
                    border: none;
                }
            }

            .text-left {
                text-align: left;
                text-indent: 1em;
            }

            /*金额*/
            .money-input {
                text-align: left;
                padding-left: 16px;

                input {
                    border: none;
                    outline: none;
                    color: #fd7d2f;
                    border-bottom: 2px solid #eee;
                    font-size: 15px;
                    font-weight: bold;
                    background-color: rgba(255, 255, 255, 0);
                    width: 120px;
                    margin-right: 8px;
                }
            }

            /*摘要-调拨类型*/
            .empty-input {
                text-align: left;
                padding-left: 16px;

                input {
                    border: none;
                    outline: none;
                    font-size: 14px;
                    background-color: rgba(255, 255, 255, 0);
                    width: 100%;
                }
            }
        }

        /*底部按钮组*/
        .bill-operation {
            text-align: left;
            margin-top: 12px;

            .arrows {
                height: 16px;
                display: inline-block;
                line-height: 13px;
                font-size: 20px;
                vertical-align: middle;
                font-family: initial;
                margin-left: 10px;
            }

            .btnGroup {
                float: right;
            }
        }

        /*附件*/
        .upload-content {
            height: 77px;
            padding-left: 16px;

            #upload {
                height: 77px;
                padding-top: 16px;
                box-sizing: border-box;
                overflow-y: auto;
            }
        }
        .upload-num {
            display: inline-block;
            width: 18px;
            height: 18px;
            border-bottom: 1px solid #aaa;
            vertical-align: middle;
            margin: 0 10px 0 26px;
            text-indent: 0;
            padding-left: 15px;
        }

        /*设置弹出框公共样式*/
        .el-dialog {
            text-align: left;
            margin-bottom: 10px;
            /*设置标题*/
            .dialog-title {
                margin-bottom: 0;
            }
            .el-dialog__body {
                padding-top: 10px;
                padding-bottom: 0;
            }
            .el-form {
                width: 94%;
                .el-select {
                    width: 100%;
                }
            }

            .el-radio-group {

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

        #showbox{
            position: fixed;
            right: -500px;
            z-index: 999;
            transition: all 1.5s;
        }
    }
</style>

<template>
    <div id="OAMakeBill">
        <!--顶部标题-按钮-->
        <header>
            <h1>总公司付款-编辑</h1>
        </header>
        <!--表单部分-->
        <section>
            <!--表单顶部-->
            <div class="title-date">
                <el-select v-model="billData.pay_mode" placeholder="请选择付款方式"
                           filterable size="mini">
                    <el-option v-for="(item,key) in payModeList"
                               :key="key"
                               :label="item"
                               :value="key">
                    </el-option>
                </el-select>
                <div class="serial-number">
                    <span>单据编号:</span>
                    <span v-text="billData.service_serial_number"></span>
                </div>
            </div>
            <!--表单-->
            <div class="bill-content">
                <table>
                    <tr>
                        <td rowspan="3" class="title-erect">付款方</td>
                        <td class="title-small">户名</td>
                        <td class="text-left" v-text="billData.pay_account_name"></td>
                        <td rowspan="3" class="title-erect">收款方</td>
                        <td class="title-small">户名</td>
                        <td class="text-left" v-text="billData.recv_account_name"></td>
                    </tr>
                    <tr>
                        <td class="title-small">账号</td>
                        <td class="select-height">
                            <el-select v-model="billData.pay_account_id"
                                       filterable remote
                                       placeholder="请选择账号"
                                       @change="selectNumber"
                                       @clear="selectNumber">
                                <el-option
                                        v-for="payItem in payList"
                                        :key="payItem.acc_id"
                                        :label="payItem.acc_no"
                                        :value="payItem.acc_id">
                                </el-option>
                            </el-select>
                        </td>
                        <td class="title-small">账号</td>
                        <td v-text="billData.recv_account_no" style="width:37%" class="text-left"></td>
                    </tr>
                    <tr>
                        <td>开户行</td>
                        <td v-text="billData.pay_account_bank" class="text-left"></td>
                        <td>开户行</td>
                        <td v-text="billData.recv_account_bank" class="text-left"></td>
                    </tr>
                    <tr>
                        <td colspan="2" class="title-space"></td>
                        <td colspan="4"></td>
                    </tr>
                    <tr>
                        <td colspan="2">金额（元）</td>
                        <td colspan="4" class="money-input">
                            <span>￥</span>
                            <input type="text" v-model="billData.payment_amount" readonly>
                            <span>(大写)</span>
                            <span v-text="moneyText"></span>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2" class="set-space">摘要</td>
                        <td class="empty-input" colspan="4">
                            <input type="text" placeholder="请在此处填写摘要" v-model="billData.payment_summary">
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2" class="set-space">附件<br/>
                            <span class="upload-num" v-text="fileLength"></span>个
                        </td>
                        <td colspan="4" class="upload-content">
                            <Upload @currentFielList="setFileList"
                                    :fileMessage="fileMessage"
                                    :emptyFileList="emptyFileList"
                                    :triggerFile="eidttrigFile"
                                    :isPending="true">
                            </Upload>
                        </td>
                    </tr>
                </table>
            </div>
            <!--表单底部按钮-->
            <div class="bill-operation">
                <el-button type="warning" plain size="medium" @click="showRightFlow" v-show="billData.service_status==5">
                    审批记录<span class="arrows">></span>
                </el-button>
                <el-button type="warning" plain size="medium" @click="goMoreBills">
                    更多单据<span class="arrows">></span>
                </el-button>
                <div class="btnGroup">
                    <el-button type="warning" size="small" @click="saveBill">保存</el-button>
                    <el-button type="warning" size="small" @click="submitBill">提交</el-button>
                </div>
            </div>
        </section>
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
        <!-- 右侧流程图 -->
        <div id="showbox">
            <BusinessTracking
                :businessParams="businessParams"
                @closeRightDialog="closeRightFlow"
            ></BusinessTracking>
        </div>
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";
    import WorkFlow from "../publicModule/WorkFlow.vue";
    import BusinessTracking from "../publicModule/BusinessTracking.vue";
    export default {
        name: "OAMakeBill",
        created: function () {
            //获取单据数据
            var params = window.location.hash.split("?")[1];
            if (params) {
                params = params.split("=");
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "headorgoa_detail",
                        params: {
                            id: params[1]
                        }
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

                        var billData = this.billData;
                        for (var k in data) {
                            if(k == "pay_mode"){
                                billData[k] = data[k] + "";
                            }else{
                                billData[k] = data[k];
                            }
                        }
                        //设置数字加千分符和转汉字
                        this.moneyText = this.$common.transitText(data.payment_amount);
                        billData.payment_amount = this.$common.transitSeparator(data.payment_amount);

                        //附件数据
                        this.fileMessage.bill_id = data.id;
                        this.eidttrigFile = !this.eidttrigFile;

                        //获取付款方账号
                        this.$axios({
                            url: this.queryUrl + "normalProcess",
                            method: "post",
                            data: {
                                optype: "poolacc_getpoolaccinfo",
                                params: {
                                    bank_type: data.pay_bank_cnaps.slice(0, 3)
                                }
                            }
                        }).then((result) => {
                            if (result.data.error_msg) {
                                return;
                            } else {
                                var data = result.data.data;
                                this.payList = data;
                            }
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            }
        },
        mounted: function(){
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if(constants.PayMode){
                this.payModeList = constants.PayMode;
            }
        },
        components: {
            Upload: Upload,
            WorkFlow: WorkFlow,
            BusinessTracking: BusinessTracking
        },
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                billData: {
                    pay_mode: "",
                    service_serial_number: "", //单据编号
                    pay_account_name: "", //付款方
                    pay_account_id: "",
                    pay_account_bank: "",
                    recv_account_name: "", //收款方
                    recv_account_no: "",
                    recv_account_bank: "",
                    payment_amount: "", //金额
                    payment_summary: "", //摘要
                    service_status: ""
                },
                payList: [], //下拉框数据
                payModeList: {},
                moneyText: "", //金额-大写
                fileMessage: { //附件
                    bill_id: "",
                    biz_type: 20
                },
                eidttrigFile: false,
                emptyFileList: [],
                fileList: [],
                fileLength: "",
                innerVisible: false, //弹框数据
                selectWorkflow: "",
                workflows: [],
                flowList: {},//查看流程
                isEmptyFlow: false,//
                lookFlowDialogVisible: false,
                businessParams: {},//业务追踪
            }
        },
        methods: {
            //选择账号时设置户名等数据
            selectNumber: function (value) {
                var payList = this.payList;
                var params = {
                    pay_account_id: "acc_id",
                    pay_account_no: "acc_no",
                    pay_account_name: "acc_name",
                    pay_account_cur: "iso_code",
                    pay_account_bank: "bank_name",
                    pay_bank_cnaps: "bank_cnaps_code",
                    pay_bank_prov: "province",
                    pay_bank_city: "city"
                }
                if(value){
                    for(var i = 0; i < payList.length; i++){
                        if(payList[i].acc_id == value){
                            var item = payList[i];
                            for(var k in params){
                                this.billData[k] = item[params[k]];
                            }
                        }
                    }
                }else{
                    for(var k in params){
                        this.billData[k] = "";
                    }
                }

            },
            //设置当前项上传附件
            setFileList: function ($event) {
                this.fileLength = "";
                this.fileList = [];
                if ($event.length > 0) {
                    this.fileLength = $event.length;
                    $event.forEach((item) => {
                        this.fileList.push(item.id);
                    })
                }
            },
            //更多单据
            goMoreBills: function () {
                this.$router.push("/OA-data/head-office-pay");
            },

            //保存
            saveBill: function () {
                var params = this.billData;
                if(!params.payment_summary){
                    this.$message({
                        type: "warning",
                        message: "请填写摘要",
                        duration: 2000
                    });
                    return;
                }
                params.files = this.fileList;

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "headorgoa_chg",
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

                        var billData = this.billData;
                        for (var k in data) {
                            billData[k] = data[k] + "";
                        }
                        //设置数字加千分符和转汉字
                        billData.payment_amount = this.$common.transitSeparator(data.payment_amount);
                        this.$message({
                            type: "success",
                            message: "修改成功",
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //提交
            submitBill: function () {
                var params = this.billData;

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "headorgoa_presubmit",
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
                        var billData = this.billData;
                        for (var k in data) {
                            billData[k] = data[k] + "";
                        }
                        //设置数字加千分符和转汉字
                        billData.payment_amount = this.$common.transitSeparator(data.payment_amount);
                        //设置弹框数据
                        this.selectWorkflow = "";
                        this.workflows = data.workflows;
                        this.innerVisible = true;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //提交流程
            submitFlow: function () {
                var workflowData = this.billData;
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
                        optype: "headorgoa_submit",
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
                        this.goMoreBills();
                        this.$message({
                            type: "success",
                            message: "提交成功",
                            duration: 2000
                        })
                    }
                }).catch(function (error) {
                    console.log(error);
                })
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
            },
            //业务追踪显示
            showRightFlow:function (row) {
                this.businessParams = {};
                this.businessParams.id = this.billData.id;
                this.businessParams.biz_type = "10";
                this.businessParams.type = 1;
                document.getElementById("showbox").style.right="0px";
            },
            closeRightFlow:function(){
                this.businessParams = {};
                document.getElementById("showbox").style.right="-500px";
            },
        }
    }
</script>


