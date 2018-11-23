<style scoped lang="less" type="text/less">
    #initialBalance {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*顶部按钮*/
        .button-list-right {
            position: absolute;
            top: -60px;
            right: -18px;
        }
        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

        /*数据列表*/
        .table-content {
            height: 400px;
        }

        /*弹框*/
        .form-small-title{
            font-weight: bold;
            padding-bottom: 6px;

            span{
                display:inline-block;
                width: 4px;
                height: 16px;
                background-color: orange;
                margin-right: 6px;
                vertical-align: middle;
            }
        }
        .split-form {
            width: 100%;
            height: 26px;
            border-bottom: 1px solid #eee;
            margin-bottom: 10px;
            padding-bottom: 1px;
            text-align: right;
        }
    }
</style>
<style lang="less" type="text/less">
    #initialBalance {
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 440px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="initialBalance">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addSign">新增</el-button>
        </div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      height="100%"
                      border size="mini">
                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="balance" label="期初余额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="bank_fail_achieve" label="银行未达账项" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="enter_fail_achieve" label="企业未达账项" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="is_enabled" label="状态" :show-overflow-tooltip="true"
                                :formatter="transitionStatus"></el-table-column>
                <el-table-column
                        label="操作" width="80"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="scope.row.is_enabled == 1">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookSign(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="scope.row.is_enabled == 0">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editSign(scope.row)"></el-button>
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
        <!--新增弹出框-->
        <el-dialog title=""
                   :visible.sync="dialogVisible"
                   width="860px" top="76px"
                   :close-on-click-modal="false">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="账户号">
                            <el-select v-model="dialogData.acc_id" clearable placeholder="请选择账户号" :disabled="lookDisabled">
                                <el-option
                                    v-for="item in accOptions"
                                    :key="item.acc_no"
                                    :label="item.acc_no"
                                    :value="item.acc_id">
                                    <span>{{ item.acc_no }}</span>
                                    <span style="margin-left:10px;color:#bbb">{{ item.acc_name }}</span>
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="期初余额">
                            <el-input v-model="dialogData.balance" :disabled="lookDisabled" clearable></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="年">
                            <el-date-picker
                                v-model="dialogData.year"
                                type="year"
                                placeholder="选择年"
                                value-format="yyyy"
                                style="width: 100%;" :disabled="lookDisabled">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="月">
                            <el-date-picker
                                v-model="dialogData.month"
                                type="month"
                                placeholder="选择月"
                                value-format="M"
                                style="width: 100%;" :disabled="lookDisabled">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row v-for="item in items"
                        :key="item.$id">
                    <el-col :span="24">
                        <div class="split-form">
                            <el-button-group v-show="!lookDisabled">
                                <el-button size="mini" @click="removeAccount(item)"
                                           v-show="showDel">删除</el-button>
                                <el-button size="mini" style="margin-left:0"
                                           @click="addAccount">新增
                                </el-button>
                            </el-button-group>
                        </div>
                    </el-col>
                    <el-col :span="12" required>
                        <el-form-item label="类型">
                            <el-select v-model="item.data_type" placeholder="请选择类型" clearable :disabled="lookDisabled">
                                <el-option
                                    v-for="(item,k) in initDataType"
                                    :key="k"
                                    :label="item"
                                    :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="借贷方向">
                            <el-select v-model="item.credit_or_debit" placeholder="请选择借贷方向" clearable  :disabled="lookDisabled">
                                <el-option
                                    v-for="(item,k) in credirOrDebit"
                                    :key="k"
                                    :label="item"
                                    :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="金额">
                            <el-input v-model="item.amount" :disabled="lookDisabled" clearable></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="摘要">
                            <el-input v-model="item.memo" :disabled="lookDisabled" clearable></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer" v-show="!lookDisabled">
                <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="saveBalance">保 存</el-button>
                <el-button type="warning" size="mini" @click="enableBalance">启 用</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>

    export default {
        name: "InitialBalance",
        created: function () {
            this.$emit("transmitTitle", "期初余额");
            this.$emit("getCommTable", this.routerMessage);
        },
        components: {},
        mounted: function () {
            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
            }
            var bankAllTypeList = JSON.parse(window.sessionStorage.getItem("bankAllTypeList"));
            if (bankAllTypeList) {
                this.bankAllTypeList = bankAllTypeList;
            }
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            //借贷方向
            if(constants.CredirOrDebit){
                this.credirOrDebit = constants.CredirOrDebit;
            }
            //类型
            if(constants.InitDataType){
                this.initDataType = constants.InitDataType;
            }

            this.$axios({
                url:this.queryUrl + "normalProcess",
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
            this.messageTips = {
                acc_id: "请选择账号！",
                balance: "请填写正确的金额！",
                year: "请选择年！",
                month: "请选择月！"
            }
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "dztinit_list",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                payModeList: {
                    "1": "银行承兑汇票"
                },
                accOptions: [],
                dialogVisible: false, //弹框数据
                dialogData: {
                    acc_id: "",
                    year: "",
                    month: "",
                    balance: ""
                },
                dialogTitle: "查看",
                items: [
                    {
                        data_type: "",
                        credit_or_debit: "",
                        memo: "",
                        amount:"",
                        $id: 1
                    }
                ],
                formLabelWidth: "100px",
                credirOrDebit: {},//借贷方向
                initDataType: {},//类型
                isEnabled:{
                    1: "已启用",
                    0: "未启用",
                },
                lookDisabled: false,
                messageTips: {},//校验提示
            }
        },
        methods: {
            //展示格式转换-业务状态
            transitionStatus: function (row, column, cellValue, index) {
                if(column.property === "is_enabled"){//转换启用状态
                    if (this.isEnabled) {
                        return this.isEnabled[cellValue];
                    }
                }
            },
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //根据条件查询数据
            queryData: function () {
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
            //新增票据信息
            addAccount: function () {
                var item = {
                    data_type: "",
                    credit_or_debit: "",
                    memo: "",
                    amount: "",
                    $id: Date.now()
                };
                this.items.push(item);
            },
            //删除票据信息
            removeAccount: function (item) {
                var index = this.items.indexOf(item);
                if (index != -1) {
                    this.items.splice(index, 1);
                }
            },
            //新增
            addSign: function (row) {
                this.dialogTitle = "新增";
                this.lookDisabled = false;
                var dialogData = this.dialogData;
                for(var k in dialogData){
                    dialogData[k] = "";
                }
                this.items = [
                    {
                        data_type: "",
                        credit_or_debit: "",
                        amount: "",
                        memo: "",
                        $id: 1
                    }
                ];
                this.dialogVisible = true;
            },
            editSign: function (row) {
                this.lookSign(row,'edit');
            },
            //查看
            lookSign: function (row,type) {
                this.lookDisabled = type ? false : true;
                this.dialogTitle = type ? "修改" : "查看";
                var dialogData = this.dialogData;
                this.$axios({
                    url:this.queryUrl + "normalProcess",
                    method:"post",
                    data:{
                        optype: "dztinit_detail",
                        params:  {
                            acc_id: row.acc_id,
                            year: row.year,
                            month: row.month
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
                        var data = result.data.data;
                        for(var k in dialogData){
                            dialogData[k] = row[k];
                        }
                        this.items = data;
                        data.forEach(ele=>{
                            ele.data_type = ele.data_type + "";
                            ele.credit_or_debit = ele.credit_or_debit + "";
                        })
                        if(type == 'edit' && data.length==0){//编辑
                            this.items = [
                                {
                                    data_type: "",
                                    credit_or_debit: "",
                                    amount: "",
                                    memo: "",
                                    $id: 1
                                }
                            ];
                        }
                        this.dialogVisible = true;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //保存
            saveBalance: function(){
                var params = this.setParams();
                if(!params){
                    return;
                }
                var optype = this.dialogTitle=="新增" ? "dztinit_add" : "dztinit_chg";
                this.$axios({
                    url:this.queryUrl + "normalProcess",
                    method:"post",
                    data:{
                        optype: optype,
                        params: params
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
                        var data = result.data.data;
                        this.$emit("getCommTable", this.routerMessage);
                        this.dialogVisible = false;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //启用
            enableBalance: function(){
                var params = this.setParams();
                if(!params){
                    return;
                }
                this.$axios({
                    url:this.queryUrl + "normalProcess",
                    method:"post",
                    data:{
                        optype: "dztinit_enable",
                        params: params
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
                        var data = result.data.data;
                        this.$emit("getCommTable", this.routerMessage);
                        this.dialogVisible = false;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //设置参数
            setParams:function(){
                // var obj = {
                //     "zh":{
                //         aaa:1,
                //         bbb:2
                //     },
                //     "bs":{
                //         bbb:111,
                //         ccc:111
                //     }
                // }
                var flag = false;
                var params = this.dialogData;
                var messageTips = this.messageTips;
                for(var k in messageTips){
                    if(!params[k]){
                        var message = messageTips[k];
                        this.$message({
                            type: "warning",
                            message: message,
                            duration: 2000
                        });
                        return ;
                    }
                }
                //校验子类型
                var items = [];
                this.items.forEach(element =>{
                    if(!element.data_type && !element.amount && !element.credit_or_debit){

                    }else if(!element.data_type || !element.amount || !element.credit_or_debit){
                        this.$message({
                            type: "warning",
                            message: "请完善子类型！",
                            duration: 2000
                        })
                        flag = true;
                        return ;
                    }else{
                        items.push(element);
                    }
                })
                if (flag) {
                    return ;
                }
                params.list = items;
                return params;
            }
        },
        computed: {
            showDel: function () {
                if (this.items.length > 1) {
                    return true;
                } else {
                    return false;
                }
            }
        },
        watch: {
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data;
            }
        }
    }
</script>


