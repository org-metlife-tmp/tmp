<style scoped lang="less" type="text/less">
    #voucherQuery {
        .form-small-title {
            font-weight: bold;
            border-bottom: 1px solid #e2e2e2;
            padding-bottom: 4px;
            margin-bottom: 10px;

            span {
                display: inline-block;
                width: 4px;
                height: 16px;
                background-color: orange;
                margin-right: 6px;
                vertical-align: middle;
            }
        }
        /*按钮样式*/
        .on-copy, .withdraw {
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
        }
        /*复制按钮*/
        .on-copy {
            background-position: -24px 1px;
        }
        /*撤回按钮*/
        .withdraw {
            background-position: -48px 0;
        }

    }
</style>

<template>
    <el-container id="voucherQuery">
        <el-header>
            <div class="button-list-right">
                <el-button type="warning" size="mini" @click="exportFun">导出</el-button>
            </div>
            <!--搜索区-->
            <div class="search-setion">
                <el-form :inline="true" :model="searchData" size="mini">
                    <el-row>
                        <el-col :span="8">
                            <el-form-item>
                                <el-col :span="11">
                                    <el-date-picker type="date" placeholder="交易开始" v-model="searchData.start_trans_date"
                                                    style="width: 100%;" value-format="yyyy-MM-dd"></el-date-picker>
                                </el-col>
                                <el-col class="line" :span="1" style="text-align:center">-</el-col>
                                <el-col :span="11">
                                    <el-date-picker type="date" placeholder="交易结束" v-model="searchData.end_trans_date"
                                                    style="width: 100%;" value-format="yyyy-MM-dd"></el-date-picker>
                                </el-col>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item>
                                <el-col :span="11">
                                    <el-date-picker type="month" placeholder="记账开始" v-model="searchData.start_accounting_period"
                                                    style="width: 100%;" value-format="yyyy-MM"></el-date-picker>
                                </el-col>
                                <el-col class="line" :span="1" style="text-align:center">-</el-col>
                                <el-col :span="11">
                                    <el-date-picker type="month" placeholder="记账结束" v-model="searchData.end_accounting_period"
                                                    style="width: 100%;" value-format="yyyy-MM"></el-date-picker>
                                </el-col>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item>
                                <el-col :span="11">
                                    <el-input v-model="searchData.min" placeholder="最小金额" clearable></el-input>
                                </el-col>
                                <el-col class="line" :span="1" style="text-align:center">-</el-col>
                                <el-col :span="11">
                                    <el-input v-model="searchData.max" placeholder="最大金额" clearable></el-input>
                                </el-col>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.statement_code" clearable placeholder="对账唯一识别号码"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.account_code" clearable placeholder="科目编号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.a_code10" placeholder="AnalysisCode10"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="item in orgList"
                                               :key="item.code"
                                               :label="item.name"
                                               :value="item.code">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.docking_status" placeholder="接口状态"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="(docking,key) in dockingList"
                                               :key="key"
                                               :label="docking"
                                               :value="key">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        
                        
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.operator" clearable placeholder="操作人"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.operator_org" placeholder="操作人所属机构"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="item in orgList"
                                               :key="item.org_id"
                                               :label="item.name"
                                               :value="item.org_id">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2" style="float:right">
                            <el-form-item>
                                <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2" style="float:right">
                            <el-form-item>
                                <el-button type="primary" plain @click="clearData" size="mini">清空筛选</el-button>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </div>
            <div class="split-bar"></div>
        </el-header>
        <el-main>
            <el-table :data="tableList"
                      border size="mini" height="100%">
                <el-table-column prop="statement_code" label="对账唯一识别号码" :show-overflow-tooltip="true" min-width="150px"></el-table-column>
                <el-table-column prop="business_ref_no" label="其他业务代码" :show-overflow-tooltip="true" min-width="120px"></el-table-column>
                <el-table-column prop="account_code" label="科目编号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="account_period" label="入账区间" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="base_amount" label="金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="debit_credit_name" label="借贷标识" :show-overflow-tooltip="true" ></el-table-column>
                <el-table-column prop="description" label="描述" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="journal_source" label="凭证来源" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="transaction_amount" label="交易金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="transaction_date" label="交易日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="transaction_reference" label="交易标识" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="operator_name" label="操作人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="operator_org_name" label="操作人所属机构" :show-overflow-tooltip="true" min-width="120px"></el-table-column>
                <el-table-column prop="docking_status_name" label="接口状态" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="a_code1" label="AnalysisCode1" :show-overflow-tooltip="true" min-width="130px"></el-table-column>
                <el-table-column prop="a_code2" label="AnalysisCode2" :show-overflow-tooltip="true" min-width="130px"></el-table-column>
                <el-table-column prop="a_code3" label="AnalysisCode3" :show-overflow-tooltip="true" min-width="130px"></el-table-column>
                <el-table-column prop="a_code5" label="AnalysisCode5" :show-overflow-tooltip="true" min-width="130px"></el-table-column>
                <el-table-column prop="a_code6" label="AnalysisCode6" :show-overflow-tooltip="true" min-width="130px"></el-table-column>
                <el-table-column prop="a_code7" label="AnalysisCode7" :show-overflow-tooltip="true" min-width="130px"></el-table-column>
                <el-table-column prop="a_code10_name" label="AnalysisCode10" :show-overflow-tooltip="true" min-width="140px"></el-table-column>
                <el-table-column prop="currency_code" label="币种编号" :show-overflow-tooltip="true"></el-table-column>
            </el-table>
        </el-main>
        <el-footer>
            <div class="botton-pag">
                <el-pagination
                        background
                        layout="sizes, prev, pager, next, jumper"
                        :page-size="pagSize"
                        :total="pagTotal"
                        :page-sizes="[20, 50, 100, 500]"
                        :pager-count="5"
                        @current-change="getCurrentPage"
                        @size-change="sizeChange"
                        :current-page="pagCurrent">
                </el-pagination>
            </div>
        </el-footer>
    </el-container>
</template>

<script>
    export default {
        name: "VoucherQuery",
        created: function () {
            this.$emit("transmitTitle", "凭证查询");
            this.$emit("getCommTable", this.routerMessage);
        },
        mounted:function(){
            /*获取常量数据*/
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            //支付方式
            if (constants.SftPayMode) {
                this.payModeList = constants.SftPayMode;
            }
            //交互方式
            if (constants.SftInteractiveMode) {
                this.interactiveList = constants.SftInteractiveMode;
            }
            //收付属性
            if (constants.SftPayAttr) {
                this.payAttrList = constants.SftPayAttr;
            }
            //交互方式
            if (constants.SftInteractiveMode) {
                this.interactList = constants.SftInteractiveMode;
            }
            //结算模式
            if(constants.SftNetMode){
                this.netModeList = constants.SftNetMode;
            }
            //机构列表
            this.getOrgList();
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "voucher_list",
                    params: {
                        page_size: 20,
                        page_num: 1
                    }
                },
                searchData: { //搜索条件
                    start_trans_date: "",
                    end_trans_date: "",
                    statement_code: "",
                    account_code: "",
                    a_code10: "",
                    min: "",
                    max: "",
                    docking_status: "",
                    start_accounting_period: "",
                    end_accounting_period: "",
                    operator: "",
                    operator_org: "",
                },
                tableList: [], //列表数据
                pagSize: 20, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                orgList: [],
                dockingList:{
                    1: "已生成",
                    0: "未生成"
                }
            }
        },
        methods: {
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                for (var k in searchData) {
                    this.routerMessage.params[k] = searchData[k];
                }
                this.routerMessage.params.page_num = 1;
                this.$emit("getCommTable", this.routerMessage);
            },
            //清空搜索条件
            clearData: function(){
                var searchData = this.searchData;
                for (var k in searchData) {
                    searchData[k] = "";

                }
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
            //获取机构列表
            getOrgList: function () {
                this.$axios({
                    url: this.queryUrl + "commProcess",
                    method: "post",
                    data: {
                        optype: "org_curlist",
                        params: {}
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
                        this.orgList = data;
                    }

                }).catch(function (error) {
                    console.log(error);
                });
            },
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //导出
            exportFun:function () {
                if(!this.tableList.length){
                    this.$message({
                        type: "warning",
                        message: "当前数据为空",
                        duration: 2000
                    });
                    return;
                }
                var params = this.routerMessage.params;
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype:"voucher_export",
                        params:params
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
                })
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


