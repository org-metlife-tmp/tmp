<style scoped lang="less" type="text/less">
    #openAccountConfirm{
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
    #openAccountConfirm {
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 400px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="openAccountConfirm">
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
                            <el-select v-model="searchData.acc_attr" placeholder="请选择账户属性"
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
                <el-table-column prop="acc_attr_name" label="账户属性" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="interactive_mode" label="账户模式" :show-overflow-tooltip="true"
                                 :formatter="transitInteract"></el-table-column>
                <el-table-column prop="status" label="账户状态" :show-overflow-tooltip="true"
                                 :formatter="transitStatus"></el-table-column>
                <!--SetAccAndMerchStatus-->
                <el-table-column
                        label="操作" width="80"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
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
        <!--编辑弹出框-->
        <el-dialog title="详情"
                   :visible.sync="dialogVisible"
                   width="860px" top="76px"
                   :close-on-click-modal="false">
            <el-form :model="dialogData" size="mini"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="申请公司">
                            <el-input v-model="dialogData.org_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户日期">
                            <el-input v-model="dialogData.open_date" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="银行账号">
                            <el-input v-model="dialogData.acc_no" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称">
                            <el-input v-model="dialogData.acc_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行">
                            <el-input v-model="dialogData.bank_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户属性">
                            <el-input v-model="dialogData.acc_attr_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户法人">
                            <el-input v-model="dialogData.lawfull_man" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户用途">
                            <el-input v-model="dialogData.acc_purpose_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式">
                            <el-input v-model="interList[dialogData.interactive_mode]" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <el-input v-model="dialogData.curr_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="存款类型">
                            <el-input v-model="depositsList[dialogData.deposits_mode]" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="开户行地址">
                            <el-input v-model="dialogData.bank_address" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行联系人">
                            <el-input v-model="dialogData.bank_contact" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="联系人电话">
                            <el-input v-model="dialogData.bank_contact_phone" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="预留印鉴">
                            <el-input v-model="dialogData.reserved_seal" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注">
                            <el-input type="textarea" v-model="dialogData.memo" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="会计科目代码">
                            <el-input v-model="dialogData.subject_code"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="subEdit">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "OpenAccountConfirm",
        created: function () {
            this.$emit("transmitTitle", "开户确认");
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
            //账户属性&账户用途
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
                    optype: "accconfirm_list",
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
                },
                formLabelWidth: "120px",
                /*下拉框数据*/
                interList: {}, //账户模式
                attrList: {}, //账户属性
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
            //编辑
            editMessage: function(row){
                this.getCurrentData("edit",row);
            },
            //编辑确定
            subEdit:function(){
                debugger
                var row = this.dialogData;
                if(row.acc_id && row.subject_code){
                    this.$axios({
                        url:"/cfm/normalProcess",
                        method: "post",
                        data:{
                            optype: "accconfirm_setstatus",
                            params: {
                                acc_id:row.acc_id,
                                subject_code:row.subject_code
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
                            this.dialogVisible = false;
                            var rows = this.tableList;
                            var index = rows.indexOf(row);
                            if (this.pagCurrent < (this.pagTotal / this.pagSize)) { //存在下一页
                                this.$emit('getTableData', this.routerMessage);
                            } else {
                                if (rows.length == "1" && (this.routerMessage.params.page_num != 1)) { //是当前页最后一条
                                    this.routerMessage.params.page_num--;
                                    this.$emit('getTableData', this.routerMessage);
                                } else {
                                    rows.splice(index, 1);
                                    this.pagTotal--;
                                }
                            }
                            this.$message({
                                type: "success",
                                message: "确认成功",
                                duration: 2000
                            })
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }else{
                    this.$message({
                        type: "warning",
                        message: "会计科目代码不能为空！",
                        duration: 2000
                    })
                }

            },
            //获取当前项数据
            getCurrentData: function(setData,row){
                debugger
                var dialogData = this.dialogData;
                this.dialogVisible = true;
                for(var k in dialogData){
                    dialogData[k] = "";
                }
                for(var i in row){
                    dialogData[i] = row[i];
                }
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
