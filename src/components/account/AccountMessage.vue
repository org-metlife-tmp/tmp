<style scoped lang="less" type="text/less">
    #accountMessage{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion{
            text-align: left;
        }

        /*数据展示区*/
        .table-content {
            height: 289px;
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

        //弹框
        .form-small-title {
            font-weight: bold;
            span {
                display: inline-block;
                width: 4px;
                height: 16px;
                background-color: orange;
                margin-right: 6px;
                vertical-align: middle;
            }
        }
    }
</style>
<style lang="less" type="text/less">
    #accountMessage {
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 400px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="accountMessage">
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" clearable placeholder="请输入账户号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.org_name" placeholder="请选择账户所属机构"
                                       filterable clearable>
                                <el-option v-for="org in orgList"
                                           :key="org.name"
                                           :label="org.name"
                                           :value="org.name">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.acc_attr" placeholder="请选择账户性质"
                                       clearable>
                                <el-option v-for="(name,k) in attrList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.interactive_mode" placeholder="请选择账户模式"
                                       clearable>
                                <el-option v-for="(name,k) in interList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
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
                                <el-checkbox label="1" name="type">正常</el-checkbox>
                                <el-checkbox label="3" name="type">已冻结</el-checkbox>
                                <el-checkbox label="2" name="type">已销户</el-checkbox>
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
                      border
                      height="100%"
                      size="mini">
                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_name" label="开户银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="curr_id" label="币种" :show-overflow-tooltip="true"
                                 :formatter="transitCurrency"></el-table-column>
                <el-table-column prop="acc_attr_name" label="账户性质" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="interactive_mode" label="账户模式" :show-overflow-tooltip="true"
                                 :formatter="transitInteract"></el-table-column>
                <el-table-column prop="status" label="账户状态" :show-overflow-tooltip="true"
                                 :formatter="transitStatus"></el-table-column>
                <el-table-column prop="is_close_confirm" label="销户确认" :show-overflow-tooltip="true"
                                 :formatter="transitStatus"></el-table-column>
                <!--SetAccAndMerchStatus-->
                <el-table-column
                        label="操作" width="80"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookMessage(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editMessage(scope.row)"></el-button>
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
        <el-dialog title="详情"
                   :visible.sync="dialogVisible"
                   width="860px" top="76px"
                   :close-on-click-modal="false">
            <el-form :model="dialogData" size="mini"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="账户号">
                            <el-input v-model="dialogData.acc_no" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称">
                            <el-input v-model="dialogData.acc_name" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属机构">
                            <el-input v-model="dialogData.org_name" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户法人">
                            <el-input v-model="dialogData.lawfull_man" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行">
                            <el-input v-model="dialogData.bank_name" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行地址">
                            <el-input v-model="dialogData.bank_address" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行联系人">
                            <el-input v-model="dialogData.bank_contact" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="联系电话">
                            <el-input v-model="dialogData.bank_contact_phone" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <el-input v-model="dialogData.currency_name" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户时间">
                            <el-input v-model="dialogData.open_date" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户性质">
                            <el-input v-model="dialogData.acc_attr_name" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户用途">
                            <el-input v-model="dialogData.acc_purpose_name" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式">
                            <el-input v-model="getInter" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="存款类型">
                            <el-input v-model="depositsList[dialogData.deposits_mode]" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="预留印鉴">
                            <el-input v-model="dialogData.reserved_seal" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="会计科目代码">
                            <el-input v-model="dialogData.subject_code" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" class="form-small-title"><span></span>账户开户</el-col>
                    <el-col :span="12">
                        <el-form-item label="摘要">
                            <el-input v-model="dialogData.intention.memo" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="说明">
                            <el-input v-model="dialogData.intention.detail" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>

                    <el-col :span="24" class="form-small-title"><span></span>账户变更</el-col>
                    <template v-for="item in this.dialogData.change">
                        <el-col :span="12">
                            <el-form-item :label="getName(item.type)">
                                <el-input v-model="item.old_value" :readonly="true"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="变更为">
                                <el-input v-model="item.new_value" :readonly="true"></el-input>
                            </el-form-item>
                        </el-col>
                    </template>

                    <el-col :span="24" class="form-small-title"><span></span>销户申请</el-col>
                    <el-col :span="12">
                        <el-form-item label="摘要">
                            <el-input v-model="dialogData.close.memo" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="说明">
                            <el-input v-model="dialogData.close.detail" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini"
                           @click="dialogVisible = false">取 消</el-button>
            </span>
        </el-dialog>
        <!--编辑弹出框-->
        <el-dialog title="详情"
                   :visible.sync="editDialogVisible"
                   width="860px" top="76px"
                   :close-on-click-modal="false">
            <el-form :model="editDialogData" size="mini"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="账户号">
                            <el-input v-model="editDialogData.acc_no" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称">
                            <el-input v-model="editDialogData.acc_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属机构">
                            <el-input v-model="editDialogData.org_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户法人">
                            <el-input v-model="editDialogData.lawfull_man" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行">
                            <el-input v-model="editDialogData.bank_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行地址">
                            <el-input v-model="editDialogData.bank_address"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行联系人">
                            <el-input v-model="editDialogData.bank_contact"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="联系电话">
                            <el-input v-model="editDialogData.bank_contact_phone"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <el-input v-model="editDialogData.currency_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户时间">
                            <el-input v-model="editDialogData.open_date" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户性质">
                            <el-input v-model="editDialogData.acc_attr_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户用途">
                            <el-input v-model="editDialogData.acc_purpose_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式">
                            <el-input v-model="getEidtInter" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="存款类型">
                            <el-input v-model="depositsList[editDialogData.deposits_mode]" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="预留印鉴">
                            <el-input v-model="editDialogData.reserved_seal" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="会计科目代码">
                            <el-input v-model="editDialogData.subject_code" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" @click="editDialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="subEdit">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "AccountMessage",
        created: function () {
            this.$emit("transmitTitle", "账户信息维护");
            this.$emit("getCommTable", this.routerMessage);
        },
        mounted:function(){
            //账户模式
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.InactiveMode) {
                this.interList = constants.InactiveMode;
            }
            //存款类型 
            if (constants.DepositsMode) {
                this.depositsList = constants.DepositsMode;
            }
            //账户性质&账户用途
            var catgList = JSON.parse(window.sessionStorage.getItem("catgList"));
            var flag = 2;
            for(var i = 0; i < catgList.length; i++){
                if(flag == 0){
                    break;
                }
                if(catgList[i].code == "acc_attr"){
                    this.attrList = catgList[i].items;
                    flag--;
                    continue;
                }
                if(catgList[i].code == "acc_purpose"){
                    this.purposeList = catgList[i].items;
                    flag--;
                    continue;
                }
            }
            //所属机构
            var orgList = JSON.parse(window.sessionStorage.getItem("selectOrgList"));
            if (orgList) {
                this.orgList = orgList;
            }
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: { //本页数据获取参数
                    optype: "account_list",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                searchData:{ //搜索区数据
                    service_status:[]
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                dialogVisible: false, //弹框数据
                dialogData: {
                    acc_no: "",
                    acc_name: "",
                    org_name: "",
                    lawfull_man: "",
                    bank_name: "",
                    bank_address: "",
                    bank_contact: "",
                    bank_contact_phone: "",
                    currency_name: "",
                    open_date: "",
                    acc_attr_name: "",
                    acc_purpose_name: "",
                    interactive_mode: "",
                    intention: {
                        memeo: "",
                        detail: ""
                    },
                    close: {
                        memeo: "",
                        detail: ""
                    },
                    change: []
                },
                formLabelWidth: "120px",
                editDialogVisible: false,
                editDialogData: {
                    acc_no: "",
                    acc_name: "",
                    org_name: "",
                    lawfull_man: "",
                    bank_name: "",
                    bank_address: "",
                    bank_contact: "",
                    bank_contact_phone: "",
                    currency_name: "",
                    open_date: "",
                    acc_attr_name: "",
                    acc_purpose: "",
                    interactive_mode: ""
                },
                /*下拉框数据*/
                interList: {}, //账户模式
                attrList: {}, //账户性质
                orgList: [], // 所属机构
                purposeList: {}, //账户用途
                depositsList:[],//存款类型
            }
        },
        methods: {
            //展示格式转换-币种
            transitCurrency: function (row, column, cellValue, index) {
                var currencyList = JSON.parse(window.sessionStorage.getItem("selectCurrencyList"));
                for (var i = 0; i < currencyList.length; i++) {
                    if (currencyList[i].id == cellValue) {
                        return currencyList[i].name;
                        break;
                    }
                }
            },
            //展示格式转换-账户模式
            transitInteract:function (row, column, cellValue, index) {
                var inactiveMode = JSON.parse(window.sessionStorage.getItem("constants")).InactiveMode;
                return inactiveMode[cellValue];
            },
            //展示格式转换-账户状态
            transitStatus: function (row, column, cellValue, index) {
                if(column.property === 'is_close_confirm'){
                    cellValue = cellValue ? '已确认': '未确认';
                    return cellValue;
                }
                var accountStatus = JSON.parse(window.sessionStorage.getItem("constants")).AccountStatus;
                return accountStatus[cellValue];
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
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                for (var key in searchData) {
                    this.routerMessage.params[key] = searchData[key];
                }
                this.$emit("getCommTable", this.routerMessage);
            },
            //查看
            lookMessage: function(row){
                this.getCurrentData("look",row);
            },
            //编辑
            editMessage: function(row){
                this.getCurrentData("edit",row);
            },
            //编辑确定
            subEdit:function(){
                var editDialogData = this.editDialogData;
                this.$axios({
                    url:"/cfm/normalProcess",
                    method: "post",
                    data:{
                        optype: "account_chg",
                        params: editDialogData
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
                        this.editDialogVisible = false;
                        this.$message({
                            type: "success",
                            message: "修改成功",
                            duration: 2000
                        })
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //获取当前项数据
            getCurrentData: function(setData,row){
                if(setData == "look"){
                    this.dialogVisible = true;
                    var dialogData = this.dialogData;
                    for(var k in dialogData){
                        if(k == "intention"){
                            var item = dialogData[k];
                            for(var key in item){
                                item[key] = "";
                            }
                        }else{
                            dialogData[k] = "";
                        }
                    }
                }else{
                    this.editDialogVisible = true;
                    var editDialogData = this.editDialogData;
                    for(var k in editDialogData){
                        editDialogData[k] = "";
                    }
                }
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "account_detail",
                        params: {
                            acc_id: row.acc_id
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
                        if(setData == "look"){
                            for (var key in data) {
                                if(key == "intention" || key == "close"){
                                    if(data[key]){
                                        this.dialogData[key] = data[key];
                                    }
                                }else{
                                    this.dialogData[key] = data[key];
                                }
                            }
                            console.log(this.dialogData);
                        }else{
                            for(var k in data){
                                this.editDialogData[k] = data[k];
                            }
                        }
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //将type装换为name
            getName:function(type){
                var accountChangeField = JSON.parse(window.sessionStorage.getItem("constants")).AccountChangeField
                return accountChangeField[type];
            }
        },
        computed:{
            getInter:function(){
                var inactiveMode = JSON.parse(window.sessionStorage.getItem("constants")).InactiveMode;
                return inactiveMode[this.dialogData.interactive_mode + ""];
            },
            getEidtInter:function(value){
                var inactiveMode = JSON.parse(window.sessionStorage.getItem("constants")).InactiveMode;
                return inactiveMode[this.editDialogData.interactive_mode + ""];
            },
        },
        watch: {
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.tableList = val.data;
                this.pagCurrent = val.page_num;
            }
        }
    }
</script>
