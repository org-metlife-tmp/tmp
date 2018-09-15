<style lang="less" type="text/less">
    #lotMakeBill{
        min-width: 980px;
        width: 80%;
        height: 100%;
        margin: 0 auto;
        .top-edit{
            position: relative;
            .tip-box{
                width: 24px;
                height: 24px;
                background-image: url(../../assets/icon_common.png);
                background-repeat: no-repeat;
                background-position: -190px 0;
                cursor: pointer;
                z-index: 5;
            }
            /*标题*/
            button{
                position: absolute;
                top: -40px;
                right: 0;
            }
            /*顶部下拉*/
            .head-select1{
                position: absolute;
                top: -38px;
                left: 0;
                >*{
                    float: left;
                }
            }
            .head-select2{
                position: absolute;
                top: -38px;
                right: 76px;
            }
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

        .bills-container{
            white-space: nowrap;
            overflow: hidden;
            display: inline-block;
            vertical-align: top;
            position: relative;
            z-index: 1;
            margin-top: 30px;
            width: 100%;
            // text-align: left;
            height: 100px;
            .bills-source{
                overflow-x: auto;
                width: 100%;
                .bill-item{
                    // float: left;
                    display: inline-block;
                    vertical-align: top;
                    text-overflow: ellipsis;
                    overflow: hidden;
                    white-space: nowrap;
                    margin: 0 10px;
                    cursor: pointer;
                    // text-align: center;
                    min-width: 140px;

                    .icon-container span{
                        margin-top: 5px;
                        width: 38px;
                        height: 42px;
                        display: inline-block;
                        background-image: url("../../assets/icon_common.png");
                        background-repeat:  no-repeat; 
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
                    .icons {
                        display: inline-block;
                        position: relative;
                    }
                    .icon-container {
                        width: 52px;
                        height: 52px;
                        background-color: #fff;
                        border: 1px solid #dfdfdf;
                        display: inline-block;
                        margin-top: 7px;
                        .mainicon {
                            background-position: -44px -103px;
                            width: 38px;
                            height: 42px;
                            margin-top: 5px;
                        }
                    }
                    .bill-icon {
                        background-image: url(../../assets/icon_common.png);
                        background-repeat: no-repeat;
                        display: inline-block;
                        vertical-align: top;
                    }
                    .icon-mask {
                        position: absolute;
                        width: 54px;
                        height: 54px;
                        top: 7px;
                        left: 0;
                        background-color: rgba(0, 0, 0, 0.5);
                        display: none;
                        text-align: center;
                        color: #fff;
                    }
                    .eyeicon {
                        background-position: -270px 0;
                        width: 38px;
                        height: 22px;
                        display: block;
                        margin: 5px auto 0;
                    }
                    .bill-delete {
                        background-image: url(../../assets/icon_common.png);
                        background-repeat: no-repeat;
                        width: 18px;
                        height: 18px;
                        background-position: -248px -2px;
                        position: absolute;
                        top: 0;
                        left: 44px;
                        display: none;
                    }
                }

                .icon-items:hover{
                    .icon-mask{
                        display: inline-block;
                    }
                    .bill-delete{
                        display: inline-block;
                    }
                }
                .bill-totalnum {
                    color: #868686;
                }
            }
            
        }
        .bills-page {
            background-color: transparent;
            background-image: url(../../assets/icon_common.png);
            background-repeat: no-repeat;
            width: 30px;
            height: 50px;
            position: absolute;
            top: 90px;
            border: 0;
            display: none;
            cursor: pointer;
        }
        .bills-page.left{
            left: 5px;
            background-position: -185px -26px; 
        }
        .bills-page.right{
            right: 5px;
            background-position: -224px -26px;
        }

//弹框内部样式
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
        /*弹框表格-分页部分*/
        .inner-botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: 6px;
            left: 0;
            .el-pagination{
                text-align: center;
            }
        }
        .tab-content{
            height: 400px;
            padding: 0 0 60px;
        }
        .dialogTitle{
            font-size: 12px;
        }
        /*导入模板*/
        .dialog-title{
            text-align: left;
        }
        .modeUpload{
            width: 90%;
            display: flex;/*设为伸缩容器*/  
            flex-flow: row;/*伸缩项目单行排列*/
            line-height: 40px;
            margin: 60px 0;
            .title-text{
                font-size: 14px;
                width: 100px;
                text-align: right;
                padding-right: 15px;
            }
            >*:last-child{
                flex: 1;
            }
        }

        .upload-demo {
            .el-upload-list {
                display: none;
            }
        }
        
    }
</style>
<style lang="less" type="text/less">
    #lotMakeBill {
        .upload-input {
            position: relative;
            padding: 0 40px;

            .el-input{
                position: absolute;
                top: 10px;
                left: 70px;
                width: 80%;

                input{
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

                .upload-button{
                    background: url(../../assets/icon_common.png) -307px 2px;
                    padding: 12px 18px;
                    height: 30px;
                }
            }
        }
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 440px;
                overflow-y: auto;
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
</style>

<template>
    <div id="lotMakeBill">
        <!--顶部标题-按钮-->
        <header><h1>批量调拨-制单</h1></header>
        <!-- 顶部按钮 下拉-->
        <div class="top-edit">
            <div class="head-select1">
                <el-select v-model="billData.pay_account_id " placeholder="请选择付款方"
                           filterable clearable size="mini">
                    <el-option v-for="item in accOptions"
                                :key="item.acc_id"
                                :label="item.acc_no"
                                :value="item.acc_id">
                    </el-option>
                </el-select>
                <el-tooltip class="tip-box" effect="dark" content="Right Top 提示文字" placement="right-start">
                    <div class="tipImg"></div>
                </el-tooltip>
            </div>
            <el-select class="head-select2" v-model="billData.pay_mode" placeholder="请选择付款方式"
                           filterable clearable size="mini">
                <el-option v-for="(name,k) in payModeList"
                            :key="k"
                            :label="name"
                            :value="k">
                </el-option>
            </el-select>
            <el-button type="warning" size="small">打印</el-button>
        </div>
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
                <el-select v-model="billData.bizObj" placeholder="请选择业务类型"
                           filterable clearable size="mini" value-key="biz_id">
                    <el-option v-for="payItem in payStatList"
                               :key="payItem.biz_id"
                               :label="payItem.biz_name"
                               :value="payItem">
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
                        <!--<td class="title-small" rowspan="3">户名</td>-->
                        <td rowspan="3" colspan="5" class="upload-input">
                            <el-input class="input-with-select" readonly
                                      size="small" v-model="billData.searchData" clearable>
                                <el-button type="primary" slot="append" class="upload-button" @click="openTemplateUpload"></el-button>
                            </el-input>
                            <div class="bills-container">
                                <div class="bills-source">
                                    <div class="bill-item" v-for="file in fileModeList" :key="file.id">
                                        <div class="icons icon-items">
                                            <span class="icon-container">
                                                <span :class="file.file_extension_suffix"></span>
                                            </span>
                                            <p class="icon-mask" @click="lookDetail(file.id)" v-show="saveParams.id">
                                                <span class="bill-icon eyeicon"></span>
                                                <span>查看</span>
                                            </p>
                                            <span class="bill-delete" title="点击删除" @click="delUpFile(file)"></span>
                                        </div>
                                        <p class="bill-totalnum">{{file.number}}笔</p>
                                        <span class="bill-totalamount">{{tansitionAmount(file.amount)}}元</span>
                                    </div>
                                </div>
                            </div>
                            <button class="bills-page left"></button>
                            <button class="bills-page right"></button>
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
                            <input type="text" @blur="setMoney" v-model="billData.total_amount">
                            <span>(大写)</span>
                            <span v-text="moneyText"></span>
                        </td>
                        <td style="width:80px">总笔数</td>
                        <td class="empty-input" style="width:100px">
                            <input type="text" v-model="billData.total_num">
                        </td>
                    </tr>
                    <tr>
                        <td class="set-space">摘要</td>
                        <td class="empty-input" colspan="5">
                            <input type="text" placeholder="请在此处填写摘要" v-model="billData.payment_summary">
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
                    <el-button type="warning" size="small" @click="deleteBill" v-show="saveParams.id">删除</el-button>
                    <el-button type="warning" size="small" @click="clearBill" v-show="!saveParams.id">清空</el-button>
                    <el-button type="warning" size="small" @click="saveBill">保存</el-button>
                    <el-button type="warning" size="small" @click="submitBill">提交</el-button>
                </div>
            </div>
        </section>
        <!--查看导入模板详情弹框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="900px" title=""
                   top="76px" :close-on-click-modal="false">
                <h1 slot="title" class="dialog-title">{{saveParams.batchno}}</h1>
                <section class="tab-content">
                    <el-table :data="detailTableList"
                            height="100%"
                            border size="mini">
                        <el-table-column prop="recv_account_name" label="收款方名称" :show-overflow-tooltip="true"></el-table-column>
                        <el-table-column prop="recv_account_no" label="收款方账号" :show-overflow-tooltip="true"></el-table-column>
                        <el-table-column prop="recv_account_bank" label="收款方开户行" :show-overflow-tooltip="true"></el-table-column>
                        <el-table-column prop="payment_amount" label="金额" :show-overflow-tooltip="true"
                                        :formatter="transitAmount"></el-table-column>
                        <el-table-column prop="memo" label="摘要"
                                        :show-overflow-tooltip="true"></el-table-column>
                    </el-table>
                    <div class="allData">
                        <span>总笔数：</span>
                        <span v-text="detailTotal.total_num" class="numText"></span>
                        <span>总金额：</span>
                        <span v-text="detailTotal.total_amount" class="numText"></span>
                    </div>
                </section>
                <!--分页部分-->
                <div class="inner-botton-pag">
                    <el-pagination
                            background
                            layout="sizes, prev, pager, next, jumper"
                            :page-size="pagDeSize"
                            :total="pagDeTotal"
                            :page-sizes="[7, 50, 100, 500]"
                            :pager-count="5"
                            @current-change="getCurrentDePage"
                            @size-change="sizeDeChange"
                            :current-page="pagDeCurrent">
                    </el-pagination>
                </div>
        </el-dialog>
        <!--导入模板-->
        <el-dialog :visible.sync="tempalteDailogVisible"
                   width="800px" title=""
                   top="76px" :close-on-click-modal="false">
            <h1 slot="title" class="dialog-title">批量上传</h1>
            <div class="modeUpload">
            <div class="title-text">模板上传</div>
                <el-input v-model="currentUpload.original_file_name" readonly>
                    <template slot="append">
                        <el-upload
                                class="upload-demo"
                                action="/cfm/normal/excel/upload"
                                :headers="{pk:'2',Authorization:currToken}"
                                multiple
                                accept=".xlsx,.xls"
                                :on-success="uploadSuccess"
                                :before-upload="beforeUpload">
                            <span class="">浏览</span>
                        </el-upload>
                    </template>
                </el-input>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="templateDownLoad">模板下载</el-button>
                <el-button type="warning" size="mini" plain @click="tempalteDailogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" plain @click="addCurUpload" :disabled="addExcel">确 定</el-button>
            </span>
        </el-dialog>
        <el-dialog :visible.sync="commitVisible"
                    width="50%" title="提交审批流程" top="76px"
                    :close-on-click-modal="false">
            <h1 slot="title" class="dialog-title">提交审批流程</h1>
            <el-radio-group v-model="selectWorkflow">
                <el-radio v-for="workflow in workflows"
                            :key="workflow.define_id"
                            :label="workflow.define_id"
                >{{ workflow.workflow_name }}
                </el-radio>
            </el-radio-group>
            <span slot="footer" class="dialog-footer" style="text-align:center">
                <el-button type="warning" size="mini" plain @click="commitVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="confirmWorkflow">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";
    export default {
        name: "LotMakeBill",
        created: function(){
            this.currToken = this.$store.state.token;
            var params = this.$route.params;
            this.fileMessage.bill_id = params.id;
            this.triggerFile = !this.triggerFile;
            params.uuid = JSON.parse(window.sessionStorage.getItem("uuid"));
            this.saveParams = params;
            if(params.id){
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "dbtbatch_detail",
                        params: {
                            id: params.id,
                            batchno: params.batchno
                        }
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
                        //设置数字加千分符和转汉字
                        this.moneyText = this.$common.transitText(data.total_amount);
                        data.total_amount = this.$common.transitSeparator(data.total_amount);
                        data.pay_mode = data.pay_mode+"";
                        data.bizObj ={
                            biz_id: data.biz_id,
                            biz_name: data.biz_name
                        }
                        this.billData = data;
                        // //调拨类型
                        // this.allotType = JSON.parse(window.sessionStorage.getItem("constants")).ZjdbType[data.payment_type];
                        this.saveParams.persist_version = data.persist_version;
                        //获取附件列表
                        this.fileModeList = data.attach_info;
                        
                    }
                }).catch(function (error) {
                    console.log(error);
                });
                
                
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "dbtbatch_initchgtemp",
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
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            }
            //获取付款方账户列表
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
            //业务类型
            this.$axios({
                url:"/cfm/commProcess",
                method:"post",
                data:{
                    optype:"biztype_biztypes",
                    params: {
                        p_id: 10
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
        mounted:function(){
        },
        components: {
            Upload: Upload
        },
        data: function () {
            return {
                dateValue: new Date(), //申请时间
                billData: {
                    pay_mode: "8", //付款方式
                    service_serial_number: "", //单据编号
                    pay_account_name: "", //付款方
                    pay_account_id: "",
                    pay_account_bank: "",
                    payment_amount: "", //金额
                    payment_summary: "" //摘要
                },
                moneyText: "", //金额-大写
                fileMessage: { //附件
                    bill_id: "",
                    biz_type: 10
                },
                emptyFileList: [],
                fileList: [],
                fileLength: "",
                dialogVisible: false, //弹框数据
                dialogData: {},
                formLabelWidth: "100px",
                loading: false,
                payModeList:{ 8:"线下补录"}, //下拉框数据
                accOptions: [], //付款方账号列表
                saveParams:{},
                payStatList: [],
                fileModeList: [],
                detailTableList: [],
                detailTotal: {},
                pagDeSize: 8, //弹窗分页数据
                pagDeTotal: 1,
                pagDeCurrent: 1,
                queryDetailParams:{
                    page_size: 7,
                    page_num: 1
                },
                tempalteDailog: {},
                tempalteDailogVisible: false,//模板弹框数据
                input:"",
                currToken:"",
                currentUpload: {},//当前上传的模板信息
                addExcel: true, //上传文件数据，未上传禁止确定按钮
                commitVisible: false,//提交弹出框
                workflows: [],//审批流程级别
                workflowData: {},
                selectWorkflow: "", //流程选择
                
            }
        },
        methods: {
            //输入金额后进行格式化
            setMoney: function () {
                var moneyNum = this.billData.total_amount.replace(/,/gi, "").trim();
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
                    this.billData.total_amount = "";
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
                this.billData.total_amount = this.$common.transitSeparator(moneyNum);
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
                this.$router.push("/allot/lot-more-bills");
            },
            //保存
            saveBill: function(){
                // var params = this.setParams();
                // if(!params){
                //     return;
                // }
                var billData = this.billData;
                this.saveParams.pay_account_id = billData.pay_account_id;
                this.saveParams.biz_id = billData.bizObj.biz_id;
                this.saveParams.biz_name = billData.bizObj.biz_name;

                this.saveParams.pay_mode = billData.pay_mode;
                this.saveParams.payment_summary = billData.payment_summary;
                this.saveParams.files = this.fileList;
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: this.saveParams.id ? "dbtbatch_chg" : "dbtbatch_add",
                        params: this.saveParams
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
                        this.saveParams.persist_version = data.persist_version;
                        this.saveParams.id = data.id;
                        this.$message({
                            type: "success",
                            message: "保存成功",
                            duration: 2000
                        });
                        this.$axios({
                            url: "/cfm/normalProcess",
                            method: "post",
                            data: {
                                optype: "dbtbatch_initchgtemp",
                                params: this.saveParams
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
                            }
                        }).catch(function (error) {
                            console.log(error);
                        });
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
                        // params[k] = billData[k].split(",").join("");
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
            //amount小数位
            tansitionAmount: function (val) {
                debugger
                if(val){
                    if(Number.isNaN(Number(val))){
                        return val;
                    }
                    return this.$common.transitSeparator(val);
                }
            },
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            // 查看导入模板详情
            lookDetail:function (id) {
                this.queryDetailParams.id = id;
                this.queryDetailParams.batchno = this.saveParams.batchno;
                this.getDetailTable(this.queryDetailParams);
            },
            getDetailTable: function (params) {
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "dbtbatch_batchbillattlist",
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
                        var val = result.data;
                        var data = result.data.data;
                        this.detailTableList = data;
                        this.detailTotal = val.ext;
                        this.dialogVisible = true;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //换页后获取数据(弹窗表格)
            getCurrentDePage: function (currPage) {
                this.queryDetailParams.page_num = currPage;
                this.getDetailTable(this.this.queryDetailParams);
            },
            //当前页数据条数发生变化(弹窗表格)
            sizeDeChange: function (val) {
                this.queryDetailParams.page_size = val;
                this.queryDetailParams.page_num = 1;
                this.getDetailTable(this.queryDetailParams);
            },
            //打开上传模板的弹窗
            openTemplateUpload:function () {
                this.addExcel = true;
                this.currentUpload = {};
                this.tempalteDailogVisible = true;
            },
            //上传成功
            uploadSuccess:function(response, file, fileList){
                this.currentUpload = response;
                this.addExcel = false;
                this.$message({
                    type: "success",
                    message: "上传成功",
                    duration: 2000
                });
            },
            beforeUpload: function(){
                
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
                if(this.saveParams.batchno){
                    params.batchno = this.saveParams.batchno;
                }

                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "dbtbatch_imports",
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
                        var singleFile = data.files;
                        singleFile.amount = this.$common.transitSeparator(singleFile.amount);
                        this.fileModeList.push(singleFile);

                        this.saveParams.batchno = data.batchno;

                        //设置汇总数据
                        this.billData.total_num = data.total_num;
                        this.billData.total_amount = this.$common.transitSeparator(data.total_amount);
                        this.moneyText = this.$common.transitText(data.total_amount);

                        this.tempalteDailogVisible = false;
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
                var params = this.saveParams;
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "dbtbatch_removetemp",
                        params: {
                            id: file.id,
                            uuid: this.saveParams.uuid,
                            batchno: this.saveParams.batchno
                        }
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
                        var fileModeList = this.fileModeList;
                        //设置附件数据
                        var index = fileModeList.indexOf(file);
                        fileModeList.splice(index, 1);
                        //设置汇总数据
                        this.billData.total_num = data.total_num;
                        this.billData.total_amount = this.$common.transitSeparator(data.total_amount);
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
                this.saveParams = {};
                this.fileModeList = [];
                this.moneyText = "";
                this.emptyFileList = [];
                this.saveParams.uuid = JSON.parse(window.sessionStorage.getItem("uuid"));
            },
            //删除单据
            deleteBill: function () {
                var params = this.saveParams;
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "dbtbatch_del",
                        params: {
                            id: params.id,
                            persist_version: params.persist_version
                        }
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
                        this.clearBill();

                        this.$message({
                            type: "success",
                            message: "删除单据成功",
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //提交
            submitBill: function(){
                // var params = this.setParams();
                // if(!params){
                //     return;
                // }
                var billData = this.billData;
                var params = this.saveParams;
                params.pay_account_id = billData.pay_account_id;
                params.biz_id = billData.bizObj.biz_id;
                params.biz_name = billData.bizObj.biz_name;
                params.pay_mode = billData.pay_mode;
                params.payment_summary = billData.payment_summary;
                params.files = this.fileList;
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "dbtbatch_presubmit",
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
                        this.selectWorkflow = "";
                        this.workflowData = data;
                        this.workflows = data.workflows;
                        this.saveParams.persist_version = data.persist_version;
                        this.saveParams.id = data.id;
                        this.commitVisible = true;
                        this.$axios({
                            url: "/cfm/normalProcess",
                            method: "post",
                            data: {
                                optype: "dbtbatch_initchgtemp",
                                params: this.saveParams
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
                            }
                        }).catch(function (error) {
                            console.log(error);
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //审批流程弹框-确定
            confirmWorkflow: function(){
                var workflowData = this.workflowData;
                var params = {
                    define_id: this.selectWorkflow,
                    id: workflowData.id,
                    service_serial_number: this.saveParams.batchno,
                    service_status: workflowData.service_status,
                    persist_version: workflowData.persist_version
                };
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "dbtbatch_submit",
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
                        this.commitVisible = false;
                        this.clearBill();
                        this.$message({
                            type: "success",
                            message: "操作成功",
                            duration: 2000
                        })
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            templateDownLoad:function (){
                this.$axios({
                    url: "/cfm/normal/excel/downExcel",
                    method: "post",
                    data:{
                        params:{
                            pk:'2'
                        }
                    },
                    responseType: 'blob'
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else {
                        var fileName = decodeURI(result.headers["content-disposition"]).split("=")[1];
                        //ie兼容
                        if (window.navigator && window.navigator.msSaveOrOpenBlob) {
                            window.navigator.msSaveOrOpenBlob(new Blob([result.data]), fileName);
                        } else {
                            let url = window.URL.createObjectURL(new Blob([result.data]));
                            let link = document.createElement('a');
                            link.style.display = 'none';
                            link.href = url;
                            link.setAttribute('download', fileName);
                            document.body.appendChild(link);
                            link.click();
                        }
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            }
        }
    }
</script>

