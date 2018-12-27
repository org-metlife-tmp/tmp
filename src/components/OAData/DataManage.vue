<style scoped lang="less" type="text/less">
    #dataManage {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion{
            text-align: left;
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
        .button-list-left{
            position: absolute;
            top: -56px;
            left: -20px;

            /*时间控件*/
            .el-date-editor {
                width: 260px;
            }
        }
        .button-list-right {
            position: absolute;
            top: -60px;
            right: -18px;
        }
    }
</style>

<template>
    <div id="dataManage">
        <!-- 顶部按钮-->
        <div class="button-list-left">
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
                    @change="getDateData">
            </el-date-picker>
        </div>
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="exportFun">导出</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.bill_no" clearable placeholder="请输入报销单申请号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.org_name" clearable placeholder="请输入申请单位"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.recv_acc_query_key" clearable placeholder="请输入收款人账号或名称"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.pay_org_type" placeholder="请选择付款机构类型"
                                       clearable filterable
                                       style="width:100%">
                                <el-option v-for="(payOrg,key) in payOrgList"
                                           :key="key"
                                           :label="payOrg"
                                           :value="key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item>
                            <el-col :span="11">
                                <el-input v-model="searchData.min_amount" clearable placeholder="最小金额"></el-input>
                            </el-col>
                            <el-col class="line" :span="1" style="text-align:center">-</el-col>
                            <el-col :span="11">
                                <el-input v-model="searchData.max_amount" clearable placeholder="最大金额"></el-input>
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
                            <el-checkbox-group v-model="searchData.status">
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
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      border size="mini">
                <el-table-column prop="bill_no" label="报销单申请号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_org_type" label="付款机构类型" :show-overflow-tooltip="true"
                                 :formatter="transitPayType"></el-table-column>
                <!-- <el-table-column prop="apply_user" label="申请用户" :show-overflow-tooltip="true"></el-table-column> -->
                <el-table-column prop="org_name" label="申请单位" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="apply_date" label="申请日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_acc_name" label="收款人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_acc_no" label="收款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_bank" label="收款方银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="amount" label="金额" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="interface_status" label="接口状态" :show-overflow-tooltip="true"
                                 :formatter="transitStatus"></el-table-column>
                <el-table-column prop="process_status" label="处理状态" :show-overflow-tooltip="true"
                                 :formatter="transitProStatus"></el-table-column>
                <el-table-column
                        label="操作" width="50"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookData(scope.row)"></el-button>
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
        <el-dialog title="单据信息"
                   :visible.sync="dialogVisible"
                   width="900px" top="76px"
                   :close-on-click-modal="false">
            <ul class="dialog-talbe">
                <li class="table-li-title">报销单申请号</li>
                <li class="table-li-content" v-text="dialogData.bill_no"></li>
                <li class="table-li-title">申请公司</li>
                <li class="table-li-content" v-text="dialogData.org_name"></li>

                <li class="table-li-title">申请部门</li>
                <li class="table-li-content" v-text="dialogData.apply_dept"></li>
                <li class="table-li-title">金额</li>
                <li class="table-li-content" style="color:#fd7d2f">￥{{ dialogData.amount }}</li>

                <li class="table-li-title">申请用户</li>
                <li class="table-li-content" v-text="dialogData.apply_user"></li>
                <li class="table-li-title">申请日期</li>
                <li class="table-li-content" v-text="dialogData.apply_date"></li>

                <li class="table-li-title">收款人账户</li>
                <li class="table-li-content" v-text="dialogData.recv_acc_no"></li>
                <li class="table-li-title">收款人账户名</li>
                <li class="table-li-content" v-text="dialogData.recv_acc_name"></li>

                <li class="table-li-title">收款人银行</li>
                <li class="table-li-content" v-text="dialogData.recv_bank"></li>
                <li class="table-li-title">银行所在省</li>
                <li class="table-li-content" v-text="dialogData.recv_bank_prov"></li>

                <li class="table-li-title">银行所在市</li>
                <li class="table-li-content" v-text="dialogData.recv_bank_city"></li>
                <li class="table-li-title">开户行地址</li>
                <li class="table-li-content" v-text="dialogData.recv_bank_addr"></li>

                <li class="table-li-title">开户行类型</li>
                <li class="table-li-content" v-text="dialogData.recv_bank_type"></li>
                <li class="table-li-title">CNAPS号</li>
                <li class="table-li-content" v-text="dialogData.recv_bank_cnaps"></li>

                <li class="table-li-title">付款机构类型</li>
                <li class="table-li-content" v-text="payOrgList[dialogData.pay_org_type]"></li>
                <li class="table-li-title">状态</li>
                <li class="table-li-content" v-text="statusList[dialogData.status]"></li>

                <li class="table-li-title">摘要</li>
                <li class="table-li-content table-two-row" v-text="dialogData.memo"></li>
            </ul>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "DataManage",
        created: function () {
            //设置当前页基本信息
            this.$emit("transmitTitle", "接口数据管理");
            this.$emit("getCommTable", this.routerMessage);
        },
        mounted: function(){
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if(constants.OaInterfaceStatus){
                this.statusList = constants.OaInterfaceStatus;
            }
            if(constants.OaPayOrgType){
                this.payOrgList = constants.OaPayOrgType;
            }
        },
        data: function () {
            return {
                routerMessage: {
                    optype: "origindataoa_list",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                tableList: [],
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                searchData:{ //搜索条件
                    apply_start_date: "",
                    apply_end_date: "",
                    bill_no: "",
                    org_name: "",
                    recv_acc_query_key: "",
                    pay_org_type: "",
                    min_amount: "",
                    max_amount: "",
                    status: []
                },
                dialogVisible: false, //弹框数据
                dialogData: {},
                statusList: {}, //常量数据
                payOrgList: {},
                dateValue: "", //时间控件
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
            }
        },
        props: ["tableData"],
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
                var params = this.routerMessage.params;
                params.org_id = user.curUodp.org_id;
                this.$emit("exportData",{
                    optype: "origindataoa_listexport",
                    params: params
                });
            },
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                for (var k in searchData) {
                    this.routerMessage.params[k] = searchData[k];
                }
                var val = this.dateValue;
                this.routerMessage.params.apply_start_date = val ? val[0] : "";
                this.routerMessage.params.apply_end_date = val ? val[1] : "";
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
            //选择时间后设置数据
            getDateData: function (val) {
                this.routerMessage.params.apply_start_date = val ? val[0] : "";
                this.routerMessage.params.apply_end_date = val ? val[1] : "";
                this.$emit('getCommTable', this.routerMessage);
            },
            //展示格式转换-接口状态
            transitStatus: function (row, column, cellValue, index) {
                return this.statusList[cellValue];
            },
            //展示格式转换-处理状态
            transitProStatus: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                return constants.OaProcessStatus[cellValue];
            },
            //展示格式转换
            transitPayType: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                return constants.OaPayOrgType[cellValue];
            },
            //查看
            lookData: function(row){
                console.log(row);
                this.dialogVisible = true;
                var dialogData = this.dialogData;
                for(var k in row){
                    dialogData[k] = row[k];
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
