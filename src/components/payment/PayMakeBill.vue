<style lang="less" type="text/less">
    #payMakeBill{
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
</style>

<template>
    <div id="payMakeBill">
        <!--顶部标题-按钮-->
        <header>
            <h1>资金支付-制单</h1>
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
                <el-select v-model="billData.pay_account_id " placeholder="请选择付款方"
                           filterable clearable size="mini">
                    <el-option v-for="item in accOptions"
                               :key="item.acc_id"
                               :label="item.acc_no"
                               :value="item.acc_id">
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
                        <td rowspan="3" class="title-erect">收款人</td>
                        <td class="title-small">户名</td>
                        <td class="empty-input" colspan="4">
                            <input type="text" placeholder="请输入户名" v-model="billData.acc_name">
                        </td>
                    </tr>
                    <tr>
                        <td class="title-small">账号</td>
                        <td class="empty-input" colspan="4">
                            <input type="text" placeholder="请输入账号" v-model="billData.acc_no">
                        </td>
                    </tr>
                    <tr>
                        <td>开户行</td>
                        <td class="empty-input" colspan="4">
                            <input type="text" placeholder="请选择开户行"
                                   v-model="billData.bank_type_name"
                                   @focus="dialogVisible = true">
                        </td>
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
        <!--开户行选择弹框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="40%" title="选择开户行"
                   top="140px" :close-on-click-modal="false">

            <el-form :model="dialogData" size="small">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="银行名称" :label-width="formLabelWidth">
                            <el-select v-model="dialogData.bankTypeName" placeholder="请选择银行大类"
                                       clearable filterable
                                       style="width:100%"
                                       :filter-method="filterBankType"
                                       @visible-change="clearSearch"
                                       @change="bankIsSelect">
                                <el-option v-for="bankType in bankTypeList"
                                           :key="bankType.name"
                                           :label="bankType.name"
                                           :value="bankType.code">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="开户地址" :label-width="formLabelWidth">
                            <el-select v-model="dialogData.area"
                                       filterable remote clearable
                                       style="width:100%"
                                       placeholder="请输入地区关键字"
                                       :remote-method="getAreaList"
                                       :loading="loading"
                                       @change="bankIsSelect">
                                <el-option
                                        v-for="area in areaList"
                                        :key="area.name"
                                        :label="area.name"
                                        :value="area.code">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="开户行" :label-width="formLabelWidth">
                            <el-select v-model="dialogData.cnaps_code" placeholder="请选择银行"
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
                            <el-input v-model="dialogData.bankCNAPS"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>

            <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                    <el-button type="warning" size="mini" @click="confirmBank">确 定</el-button>
                </span>
        </el-dialog>
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";

    export default {
        name: "PayMakeBill",
        created: function(){
            this.$axios({
                url:"/cfm/normalProcess",
                method:"post",
                data:{
                    optype:"account_accs",
                    params:{
                        status:1,
                        acc_id:""
                    }
                }
            }).then((result) =>{
                this.accOptions = result.data.data;
            });
        },
        mounted: function(){
            /*获取下拉框数据*/
            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
                this.bankTypeList = bankTypeList;
            }
        },
        components: {
            Upload: Upload
        },
        data: function () {
            return {
                dateValue: new Date(), //申请时间
                billData: {
                    acc_name: "",
                    acc_no: "",
                    cnaps_code: "",
                    bank_type_name: "",
                    payment_amount: "",
                    payment_summary: "",
                    pay_account_id: ""
                },
                moneyText: "", //金额-大写
                fileMessage: { //附件
                    bill_id: "",
                    biz_type: 1
                },
                emptyFileList: [],
                fileList: [],
                fileLength: "",
                dialogVisible: false, //弹框数据
                dialogData: {
                    bankTypeName: "",
                    area: "",
                    cnaps_code:"",
                    bankCNAPS: "",
                },
                formLabelWidth: "100px",
                bankSelect: true, //银行可选控制
                bankAllList: [], //弹框下拉框数据
                bankTypeList: [],
                areaList: [],
                loading: false,
                bankList: [],
                payModeList:{}, //下拉框数据
                accOptions: []
            }
        },
        methods: {
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
                    })
                } else {
                    this.bankTypeList = this.bankAllList;
                }
            },
            //重置银行大类数据
            clearSearch: function () {
                if (this.bankTypeList != this.bankAllList) {
                    this.bankTypeList = this.bankAllList;
                }
            },
            //银行大类/地址变化后判断银行是否可选
            bankIsSelect: function (value) {
                this.bankList = [];
                if (this.dialogData.area && this.dialogData.bankTypeName) {
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
                    var area_code = this.dialogData.area;
                    var bank_type = this.dialogData.bankTypeName;

                    this.$axios({
                        url: "/cfm/commProcess",
                        method: "post",
                        data: {
                            optype: "bank_list",
                            params: {
                                area_code: area_code,
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
                        this.dialogData.bankCNAPS = bankList[i].cnaps_code;
                        return;
                    }
                }
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
            //设置开户行
            confirmBank: function(){
                this.billData.cnaps_code = this.dialogData.bankCNAPS;
                this.billData.bank_type_name = this.dialogData.cnaps_code;
                this.dialogVisible = false;
            },
            //清空
            clearBill: function(){
                var billData = this.billData;
                for(var k in billData){
                    if(k != "id" && k != "service_serial_number"){
                        billData[k] = "";
                    }
                }
                this.moneyText = "";
                this.emptyFileList = [];
            },
            //设置params
            setParams: function(){
                //校验数据是否完善 并设置发送给后台的数据
                var billData = this.billData;
                for(var k in billData){
                    if(k != "payment_summary" && k != "files" && !billData[k]){
                        this.$message({
                            type: "warning",
                            message: "请完善单据信息",
                            duration: 2000
                        });
                        return false;
                    }else if(k == "payment_amount"){  //金额
                        billData[k] = billData[k].split(",").join("");
                    }
                }
                billData.files= this.fileList;
                return billData;
            },
            //保存
            saveBill: function(){
                var params = this.setParams();
                if(!params){
                    return;
                }

                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: params.id ? "" : "zft_addbill",
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
                            message: "保存成功",
                            duration: 2000
                        });
                        this.billData.id = data.id;
                        this.billData.persist_version = data.persist_version;
                        this.billData.service_serial_number = data.service_serial_number;
                        console.log(data);
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //更多单据
            goMoreBills: function(){
                this.$router.push("/payment/pay-more-bills");
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
