<style lang="less" type="text/less">
    #makeBill {
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
        .upload-content{
            height: 77px;
            padding-left: 16px;

            #upload{
                height: 77px;
                padding-top: 16px;
                box-sizing: border-box;
                overflow-y: auto;
            }
        }
        .upload-num{
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
    }
    .el-radio-group {
        // margin-top: -16px;
        .el-radio {
            display: block;
            margin-left: 30px;
            margin-bottom: 10px;
        }
    }
</style>
<style lang="less" type="text/less">
    #makeBill {
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 400px;
                overflow-y: auto;
            }
        }
    }
</style>
<template>
    <div id="makeBill">
        <!--顶部标题-按钮-->
        <header>
            <h1>内部调拨-制单</h1>
            <!-- <el-button type="warning" size="small">打印</el-button> -->
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
                <el-select v-model="billData.biz_id" placeholder="请选择业务类型"
                           filterable clearable size="mini">
                    <el-option v-for="payItem in payStatList"
                               :key="payItem.biz_id"
                               :label="payItem.biz_name"
                               :value="payItem.biz_id">
                    </el-option>
                </el-select>
                <el-select v-model="billData.pay_mode" placeholder="请选择付款方式"
                           filterable clearable size="mini">
                    <el-option v-for="(name,k) in payModeList"
                               :key="k"
                               :label="name"
                               :value="k">
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
                        </td>
                        <td class="title-small">账号</td>
                        <td style="width:36%">
                            <el-select v-model="billData.recv_account_id"
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
                        </td>
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
                            <input type="text" @blur="setMoney" v-model="billData.payment_amount">
                            <span>(大写)</span>
                            <span v-text="moneyText"></span>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2" class="set-space">摘要</td>
                        <td class="empty-input">
                            <input type="text" placeholder="请在此处填写摘要" v-model="billData.payment_summary">
                        </td>
                        <td colspan="2">调拨类型</td>
                        <td v-text="allotType"></td>
                    </tr>
                    <tr>
                        <td colspan="2" class="set-space">附件<br/>
                            <span class="upload-num" v-text="fileLength"></span>个
                        </td>
                        <td colspan="4" class="upload-content">
                            <Upload @currentFielList="setFileList"
                                    :fileMessage="fileMessage"
                                    :emptyFileList="emptyFileList"
                                    :isPending="true">
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
        name: "MakeBill",
        created: function () {
            //获取单据数据
            var params = window.location.hash.split("?")[1];
            if(params){
                params = params.split("=");
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "dbt_detail",
                        params: {
                            id: params[1]
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {

                    } else {
                        var data = result.data.data;
                        //设置数字加千分符和转汉字
                        this.moneyText = this.$common.transitText(data.payment_amount);
                        data.payment_amount = this.$common.transitSeparator(data.payment_amount);
                        data.pay_mode += ""; //下拉框数据 需为字符串
                        //下拉框列表
                        this.payList.push({
                            acc_id: data.pay_account_id,
                            acc_no: data.pay_account_no
                        });
                        this.gatherList.push({
                            acc_id: data.recv_account_id,
                            acc_no: data.recv_account_no
                        });

                        this.billData = data;
                        //调拨类型
                        this.allotType = JSON.parse(window.sessionStorage.getItem("constants")).ZjdbType[data.payment_type];
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            }
            //付款方式
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if(constants.PayMode){
                this.payModeList = constants.PayMode;
            }

            //业务类型
            this.$axios({
                url:"/cfm/commProcess",
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
            })

        },
        components: {
            Upload: Upload
        },
        data: function () {
            return {
                dateValue: new Date(), //申请时间
                billData: {
                    biz_id: "", //业务类型
                    pay_mode: "", //付款方式
                    service_serial_number: "", //单据编号
                    pay_account_name: "", //付款方
                    pay_account_id: "",
                    pay_account_bank: "",
                    recv_account_name: "", //收款方
                    recv_account_id: "",
                    recv_account_bank: "",
                    payment_amount: "", //金额
                    payment_summary: "" //摘要
                },
                payLoading: false, //付款方数据
                payList: [],
                gatherLoading: false, //收款方数据
                gatherList: [],
                moneyText: "", //金额-大写
                allotType: "", //调拨类型
                fileMessage: { //附件
                    bill_id: "",
                    biz_type: 1
                },
                emptyFileList: [],
                fileList: [],
                fileLength: "",
                innerVisible: false, //弹框数据
                selectWorkflow: "",
                workflows: [],

                payModeList:{}, //下拉框数据
                payStatList: [],
            }
        },
        methods: {
            //获取付款方账号
            getPayList: function (value) {
                this.payLoading = true;
                var billData = this.billData;

                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "dbt_payacclist",
                        params: {
                            query_key: value.trim(),
                            exclude_ids: billData.recv_account_id,
                            page_size: 10000,
                            page_num: 1
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
                var billData = this.billData;

                this.$axios({
                    url: "/cfm/normalProcess",
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
                var billData = this.billData;

                if (target == "payNumber") {
                    for (var i = 0; i < payList.length; i++) {
                        if (payList[i].acc_id == value) {
                            billData.pay_account_name = payList[i].acc_name;
                            billData.pay_account_bank = payList[i].bank_name;

                            if (billData.recv_account_id) {
                                for (var j = 0; j < gatherList.length; j++) {
                                    if (gatherList[j].acc_id == billData.recv_account_id) {
                                        var payLevel = payList[j].level_num;
                                        var gatherLevel = gatherList[j].level_num;
                                    }
                                }
                            }
                        }
                    }
                }
                if (target == "gatherNumber") {
                    for (var i = 0; i < gatherList.length; i++) {
                        if (gatherList[i].acc_id == value) {
                            billData.recv_account_name = gatherList[i].acc_name;
                            billData.recv_account_bank = gatherList[i].bank_name;

                            if (billData.pay_account_id) {
                                for (var j = 0; j < payList.length; j++) {
                                    if (payList[j].acc_id == billData.pay_account_id) {
                                        var payLevel = payList[j].level_num;
                                        var gatherLevel = gatherList[j].level_num;
                                    }
                                }
                            }
                        }
                    }
                }

                if (payLevel && gatherLevel) {
                    this.allotType = payLevel == gatherLevel ? "本公司内部账户调拨" : (payLevel > gatherLevel ? "本公司拨给下级公司" : "本公司拨给上级公司");
                }
            },
            //账号下拉框显示时更新数据
            selectVisible: function (curStatus, target) {
                if (curStatus) {
                    if (target == "payNumber") {
                        this.getPayList("");
                    } else {
                        this.getGatherList("");
                    }
                }
            },
            //清空账号时清空户名和开户行
            clearSelect: function (target) {
                var billData = this.billData;
                if (target == "payNumber") {
                    billData.pay_account_name = "";
                    billData.pay_account_bank = "";
                }
                if (target == "gatherNumber") {
                    billData.recv_account_name = "";
                    billData.recv_account_bank = "";
                }
                this.allotType = "";
            },
            //输入金额后进行格式化
            setMoney: function () {
                var moneyNum = this.billData.payment_amount.replace(/,/gi, "").trim();
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
                    this.billData.payment_amount = "";
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
                this.billData.payment_amount = this.$common.transitSeparator(moneyNum);
                //设置汉字
                this.moneyText = this.$common.transitText(moneyNum);
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
            //清空
            clearBill: function(){
                var billData = this.billData;
                for(var k in billData){
                    billData[k] = "";
                }
                this.moneyText = "";
                this.allotType = "";
                this.emptyFileList = [];
            },
            //更多单据
            goMoreBills: function(){
                this.$router.push("/allot/more-bills");
            },
            //保存
            saveBill: function(){
                var params = this.setParams();
                if(!params){
                    return;
                }

                var billData = this.billData;

                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: billData.id ? "dbt_chg" : "dbt_add",
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
                        data.payment_amount = this.$common.transitSeparator(data.payment_amount);
                        data.pay_mode += "";

                        this.billData = data;
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
            submitBill: function(){
                var params = this.setParams();
                if(!params){
                    return;
                }

                this.$axios({
                    url: "/cfm/normalProcess",
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
                        data.payment_amount = this.$common.transitSeparator(data.payment_amount);
                        data.pay_mode += "";
                        this.billData = data;
                        //设置弹框数据
                        this.selectWorkflow = "";
                        this.workflows = data.workflows;
                        this.innerVisible = true;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //设置params
            setParams: function(){
                //校验数据是否完善 并设置发送给后台的数据
                var billData = this.billData;
                var params = {
                    pay_account_id: "",
                    recv_account_id: "",
                    payment_amount: "",
                    pay_mode: "",
                    payment_summary: "",
                    files: [],
                    biz_id: ""
                }
                for(var k in params){
                    if(k != "payment_summary" && k != "files" && !billData[k]){
                        this.$message({
                            type: "warning",
                            message: "请完善单据信息",
                            duration: 2000
                        });
                        return false;
                    }
                    if(k == "payment_amount"){  //金额
                        params[k] = billData[k].split(",").join("");
                    }else if(k == "files"){  //附件
                        params[k] = this.fileList;
                    }else if(k == "biz_id"){
                        params[k] = billData[k];
                        var payStatList = this.payStatList;
                        for(var i = 0; i < payStatList.length; i++){
                            if(billData[k] == payStatList[i].biz_id){
                                params.biz_name = payStatList[i].biz_name;
                            }
                        }
                    }else{
                        params[k] = billData[k];
                    }
                }
                if(billData.id){
                    params.id = billData.id;
                    params.persist_version = billData.persist_version;
                }
                return params;
            },
            //提交流程
            submitFlow: function(){
                var workflowData = this.billData;
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
                        var data = result.data.data;
                        this.innerVisible = false;
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
            }
        }
    }
</script>
