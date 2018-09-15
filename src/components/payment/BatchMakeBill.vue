<style lang="less" type="text/less">
    #batchMakeBill {
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
                width: 116px;
                text-align: center;
                padding: 0 44px;
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

            .pay-file-list{
                height: 50px;
                width: 80%;
                margin: 10px auto 0px;

                li{
                    float: left;
                    position: relative;
                    cursor: pointer;
                    margin-right: 40px;
                    border: 1px solid #ccc;
                    padding: 6px;

                    span{
                        width: 38px;
                        height: 42px;
                        display: inline-block;
                        background: url("../../assets/icon_common.png") no-repeat;
                        background-position: -85px -103px;
                        vertical-align: middle;
                    }
                    .txt{
                        background-position: -85px -103px;
                    }
                    .doc,.docx{
                        background-position: 0px -103px;
                    }
                    .xls,.xlsx{
                        background-position: -44px -103px;
                    }
                    .png,.jpg{
                        background-position: -132px -103px;
                    }

                    i{
                        position: absolute;
                        width: 21px;
                        height: 21px;
                        background: url("../../assets/icon_common.png") no-repeat;
                        background-position: -248px 0;
                        top: -8px;
                        left: 40px;
                        display: none;
                    }

                    div{
                        position: absolute;
                        bottom: -22px;
                        width: 60px;
                        left: -6px;
                    }

                    div:nth-child(4){
                        bottom: -42px;
                        width: 300px;
                        left: -124px;
                    }
                }

                li:hover{
                    i{
                        display: inline-block;
                    }
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
    }

    /*设置弹出框样式*/
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
    }

    .el-radio-group {
        margin-top: -16px;
        .el-radio {
            display: block;
            margin-left: 30px;
            margin-bottom: 10px;
        }
    }

    .dialog-upload-input {
        width: 90%;
        margin: 10px auto 40px;

        .el-input {
            width: 80%;
            vertical-align: middle;
            margin-left: 10px;

            input {
                cursor: default;
            }

            div {
                cursor: pointer;
            }
        }
    }

    .upload-demo {
        .el-upload-list {
            display: none;
        }
    }

    .upload-input {
        position: relative;

        .el-input {
            position: absolute;
            top: 10px;
            left: 90px;
            width: 80%;

            input {
                border-radius: 16px 0 0 16px;
                cursor: default;
            }

            .el-input-group__append {
                border-radius: 16px;
                left: -16px;
                background-color: #409EFF;
                color: #fff;
                width: 14px;
            }

            .upload-button {
                background: url(../../assets/icon_common.png) -307px 2px;
                padding: 12px 18px;
                height: 30px;
            }
        }
    }
</style>

<template>
    <div id="batchMakeBill">
        <!--顶部标题-按钮-->
        <header>
            <h1>批量支付-制单</h1>
            <el-button type="warning" size="small">打印</el-button>
        </header>
        <!--表单部分-->
        <section>
            <!--表单顶部-->
            <div class="title-date">
                <el-date-picker
                        v-model="dateValue"
                        type="date" :readonly="true"
                        placeholder="请选择申请日期"
                        value-format="yyyy-MM-dd"
                        size="mini">
                </el-date-picker>
                <el-select v-model="billData.pay_acc_id" placeholder="请选择付款方"
                           filterable clearable size="mini">
                    <el-option v-for="item in accOptions"
                               :key="item.acc_id"
                               :label="item.acc_no"
                               :value="item.acc_id">
                    </el-option>
                </el-select>
                <el-select v-model="billData.biz_id" placeholder="请选择业务类型"
                           filterable clearable size="mini"
                           @change="setBizName">
                    <el-option v-for="payItem in payStatList"
                               :key="payItem.biz_id"
                               :label="payItem.biz_name"
                               :value="payItem.biz_id">
                    </el-option>
                </el-select>
                <div class="serial-number">
                    <span>批次号:</span>
                    <span v-text="billData.batchno"></span>
                </div>
            </div>
            <!--表单-->
            <div class="bill-content">
                <table>
                    <tr>
                        <td rowspan="3" class="title-erect">收款信息</td>
                        <td rowspan="3" colspan="5" class="upload-input">
                            <el-input class="input-with-select" readonly
                                      size="small" v-model="billData.uploadName" clearable>
                                <el-button type="primary" slot="append" class="upload-button"
                                           @click="showUpload"></el-button>
                            </el-input>
                            <ul class="pay-file-list">
                                <li v-for="file in saveUploadList">
                                    <span :class="file.file_extension_suffix"></span>
                                    <i title="点击删除" @click="delUpFile(file)"></i>
                                    <div>{{ file.number }}笔</div>
                                    <div>{{ file.amount }}元</div>
                                </li>
                            </ul>
                        </td>
                    </tr>
                    <tr></tr>
                    <tr></tr>
                    <tr>
                        <td class="title-space"></td>
                        <td colspan="6"></td>
                    </tr>
                    <tr>
                        <td>金额合计（元）</td>
                        <td colspan="3" class="money-input">
                            <span>￥</span>
                            <input type="text" v-model="billData.payment_amount" readonly>
                            <span>(大写)</span>
                            <span v-text="moneyText"></span>
                        </td>
                        <td style="width:80px">总笔数</td>
                        <td class="empty-input" style="width:100px">
                            <input type="text" v-model="billData.allList" readonly>
                        </td>
                    </tr>
                    <tr>
                        <td class="set-space">摘要</td>
                        <td class="empty-input" colspan="5">
                            <input type="text" placeholder="请在此处填写摘要" v-model="billData.memo">
                        </td>
                    </tr>
                    <tr>
                        <td class="set-space">附件<br/>
                            <span class="upload-num" v-text="fileLength"></span>个
                        </td>
                        <td colspan="6" class="upload-content">
                            <Upload @currentFielList="setFileList"
                                    :fileMessage="fileMessage"
                                    :emptyFileList="emptyFileList"
                                    :isPending="true"
                                    :triggerFile="eidttrigFile">
                            </Upload>
                        </td>
                    </tr>
                </table>
            </div>
            <!--表单底部按钮-->
            <div class="bill-operation">
                <el-button type="warning" plain size="medium" @click="goMoreBills">
                    更多单据<span class="arrows">></span>
                </el-button>
                <div class="btnGroup">
                    <el-button type="warning" size="small" @click="clearBill">清空</el-button>
                    <el-button type="warning" size="small" @click="saveBill">保存</el-button>
                    <el-button type="warning" size="small" @click="submitBill">提交</el-button>
                </div>
            </div>
        </section>
        <!--开户行选择弹框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="800px" title="批量上传"
                   top="140px" :close-on-click-modal="false">
            <div class="dialog-upload-input">
                模板上传
                <el-input size="small" readonly v-model="currentUpload.original_file_name">
                    <template slot="append">
                        <el-upload
                                class="upload-demo"
                                action="/cfm/normal/excel/upload"
                                :headers="{pk:'2',Authorization:currToken}"
                                :on-success="uploadSuccess"
                                multiple>
                            <span class="">浏览</span>
                        </el-upload>
                    </template>
                </el-input>
            </div>
            <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="">模板下载</el-button>
                    <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                    <el-button type="warning" size="mini" @click="addCurUpload" :disabled="addExcel">确 定</el-button>
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
                </el-radio>
            </el-radio-group>
            <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="innerVisible = false">取 消</el-button>
                    <el-button type="warning" size="mini" @click="submitFlow">确 定</el-button>
                </span>
        </el-dialog>
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";

    export default {
        name: "PayMakeBill",
        created: function () {
            //获取付款方账户列表
            this.$axios({
                url: "/cfm/normalProcess",
                method: "post",
                data: {
                    optype: "account_accs",
                    params: {
                        status: 1,
                        acc_id: ""
                    }
                }
            }).then((result) => {
                this.accOptions = result.data.data;
            });

            //获取单据数据
            var hash = window.location.hash.split("?")[1];
            if(hash){
                var paramsList = hash.split("&");
                var params = {
                    uuid: JSON.parse(window.sessionStorage.getItem("uuid"))
                };
                paramsList.forEach((item) => {
                    var keyValue = item.split("=");
                    params[keyValue[0]] = keyValue[1];
                })

                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "zftbatch_prechgbill",
                        params: params
                    }
                }).then((result) => {
                    if (result.data.error_msg) {

                    } else {
                        var data = result.data.data;
                        var billData = this.billData;

                        billData.id = data.id;
                        billData.batchno = data.batchno;
                        billData.pay_acc_id = data.pay_account_id;
                        billData.version = data.persist_version;
                        billData.memo = data.payment_summary;
                        billData.allList = data.total_num;
                        this.saveUploadList = data.obbaiTempList;
                        billData.payment_amount = this.$common.transitSeparator(data.total_amount);
                        this.moneyText = this.$common.transitText(data.total_amount);
                        //附件数据
                        this.fileMessage.bill_id = data.id;
                        this.eidttrigFile = !this.eidttrigFile;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            }

            //业务类型
            this.$axios({
                url:"/cfm/commProcess",
                method:"post",
                data:{
                    optype:"biztype_biztypes",
                    params: {
                        p_id: 11
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

            //设置token数据
            this.currToken = this.$store.state.token;
        },
        components: {
            Upload: Upload
        },
        data: function () {
            return {
                dateValue: new Date(), //申请时间
                billData: {
                    uploadName: "", //上传的文件名
                    allList: "", //总笔数
                    pay_acc_id: "", //付款方式
                    payment_amount: "", //金额
                    memo: "",
                    files: [],
                    batchno: "",
                    pay_mode: 8,
                    version: "",
                    id: "",
                    biz_id: "",
                    biz_name: ""
                },
                moneyText: "", //金额-大写
                fileMessage: { //附件
                    bill_id: "",
                    biz_type: 11
                },
                eidttrigFile: false,
                emptyFileList: [],
                fileList: [],
                fileLength: "",
                accOptions: {}, //下拉框数据
                payStatList: [],
                dialogVisible: false, //弹框数据
                innerVisible: false, //提交弹框
                selectWorkflow: "",
                workflows: [],
                addExcel: true, //上传文件数据
                currentUpload: {},
                saveUploadList: [],
                currToken: "", //token信息
            }
        },
        methods: {
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
            //弹出上传弹框
            showUpload: function () {
                this.currentUpload = {};
                this.addExcel = true;
                this.dialogVisible = true;
            },
            //上传成功
            uploadSuccess: function (response, file, fileList) {
                if(response.error_message){
                    this.$message({
                        type: "error",
                        message: response.error_message,
                        duration: 2000
                    });
                    return;
                }
                this.currentUpload = response;
                this.addExcel = false;
                this.$message({
                    type: "success",
                    message: "上传成功",
                    duration: 2000
                });
            },
            //添加上传成功的文件
            addCurUpload: function () {
                this.addExcel = true;
                var currentUpload = this.currentUpload;
                var params = {
                    uuid: JSON.parse(window.sessionStorage.getItem("uuid")),
                    object_id: currentUpload.object_id,
                    document_id: currentUpload.document_id,
                    origin_name: currentUpload.original_file_name
                }
                if(this.billData.batchno){
                    params.batchno = this.billData.batchno;
                }

                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "zftbatch_addbillexcel",
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
                        //设置附件数据
                        this.saveUploadList.push(data);
                        this.billData.batchno = data.batchno;
                        //设置汇总数据
                        this.billData.allList = data.total_num;
                        this.billData.payment_amount = this.$common.transitSeparator(data.total_amount);
                        this.moneyText = this.$common.transitText(data.total_amount);

                        this.dialogVisible = false;
                        this.$message({
                            type: "success",
                            message: "添加附件成功",
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //删除
            delUpFile: function(file){
                console.log(file);
                var params = {
                    batchno: file.batchno,
                    uuid: file.uuid,
                    info_id: file.id
                }
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "zftbatch_delbillexcel",
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
                        var saveUploadList = this.saveUploadList;
                        //设置附件数据
                        var index = saveUploadList.indexOf(file);
                        saveUploadList.splice(index, 1);
                        //设置汇总数据
                        this.billData.allList = data.total_num;
                        this.billData.payment_amount = this.$common.transitSeparator(data.total_amount);
                        this.moneyText = this.$common.transitText(data.total_amount);

                        this.$message({
                            type: "success",
                            message: "删除附件成功",
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //清空
            clearBill: function () {
                var billData = this.billData;
                for (var k in billData) {
                    billData[k] = "";
                }
                this.saveUploadList = [];
                this.moneyText = "";
                this.emptyFileList = [];
            },
            //更多单据
            goMoreBills: function () {
                this.$router.push("/payment/batch-more-bills");
            },
            //设置params
            setParams: function () {
                //校验数据是否完善 并设置发送给后台的数据
                var params = this.billData;
                params.files = this.fileList;
                params.pay_mode = 8;
                params.uuid = JSON.parse(window.sessionStorage.getItem("uuid"));
                return params;
            },
            //保存
            saveBill: function () {
                var params = this.setParams();
                if (!params) {
                    return;
                }

                var billData = this.billData;
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: billData.id ? "zftbatch_chgbill" : "zftbatch_addbill",
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
                        this.$message({
                            type: "success",
                            message: billData.id ? "修改成功" : "保存成功",
                            duration: 2000
                        });
                        this.billData.id = data.id;
                        this.billData.version = data.persist_version;
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
                        optype: "zftbatch_presubmit",
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
                        this.billData.id = data.id;
                        this.billData.service_status = data.service_status;
                        this.billData.version = data.persist_version;
                        //设置流程数据
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
                    service_serial_number : workflowData.batchno,
                    service_status: workflowData.service_status,
                    persist_version: workflowData.version
                };

                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "zftbatch_submit",
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
                        this.$router.push({
                            name: "BatchMakeBill"
                        });
                        this.clearBill();
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
            //同时保存biztype和name
            setBizName: function(val){
                var payStatList = this.payStatList;
                for(var i = 0; i < payStatList.length; i++){
                    if(payStatList[i].biz_id == val){
                        this.billData.biz_name = payStatList[i].biz_name;
                    }
                }
            }
        }
    }
</script>

