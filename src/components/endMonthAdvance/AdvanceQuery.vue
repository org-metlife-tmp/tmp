<style scoped lang="less" type="text/less">
    #advanceQuery {

    }
</style>

<template>
    <el-container id="advanceQuery">
        <el-header>
            <div class="button-list-right">
                <el-button type="warning" size="mini" @click="exportFun">导出业务明细</el-button>
                <el-button type="warning" size="mini" @click="exportFun1">导出财务账</el-button>
            </div>
            <div class="search-setion">
                <el-form :inline="true" :model="searchData" size="mini">
                    <el-row>
                        <el-col :span="5">
                            <el-form-item>
                                <el-date-picker
                                        v-model="dateValue"
                                        type="daterange"
                                        range-separator="至"
                                        start-placeholder="开始日期"
                                        end-placeholder="结束日期"
                                        value-format="yyyy-MM-dd"
                                        size="mini" clearable
                                        unlink-panels
                                        :picker-options="pickerOptions">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.org_id" placeholder="请选择开户机构"
                                           clearable filterable
                                           @change="getAccData"
                                           style="width:100%">
                                    <el-option v-for="item in orgList"
                                               :key="item.org_id"
                                               :label="item.name"
                                               :value="item.org_id">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.acc_no" placeholder="请选择银行账号"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="accItem in accList"
                                               :key="accItem.acc_id"
                                               :label="accItem.acc_no"
                                               :value="accItem.acc_id">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.acc_no" placeholder="请选择bankcode"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="accItem in accList"
                                               :key="accItem.acc_id"
                                               :label="accItem.bankcode"
                                               :value="accItem.acc_id">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-date-picker
                                        v-model="searchData.period_date"
                                        type="month"
                                        value-format="yyyy-MM"
                                        placeholder="请选择财务月">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                            </el-form-item>
                        </el-col>

                        <el-col :span="5">
                            <el-form-item>
                                <el-col :span="11">
                                    <el-input v-model="searchData.min" placeholder="最小金额"></el-input>
                                </el-col>
                                <el-col class="line" :span="1" style="text-align:center">-</el-col>
                                <el-col :span="11">
                                    <el-input v-model="searchData.max" placeholder="最大金额"></el-input>
                                </el-col>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.is_checked" placeholder="请选择对账状态"
                                           clearable filterable style="width:100%">
                                    <el-option label="未核对" value="0"></el-option>
                                    <el-option label="已核对" value="1"></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.presubmit_user_name" clearable
                                          placeholder="请输入操作人"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="8">
                            <el-form-item style="margin-bottom:0px">
                                <el-checkbox-group v-model="searchData.precondition">
                                    <el-checkbox v-for="(name,k) in statusList"
                                                 :label="k" name="name" :key="k">
                                        {{ name }}
                                    </el-checkbox>
                                </el-checkbox-group>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
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
            <el-table :data="tableList" height="100%" border size="mini"
                      @selection-change="saveId">
                <el-table-column type="selection" width="40"></el-table-column>
                <el-table-column prop="trans_date" label="对账单日期" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bankcode" label="BankCode" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_no" label="银行账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_name" label="开户行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="org_name" label="开户机构" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="direction" label="收付方向" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_no" label="对方银行账号" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_name" label="对方账户名称" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_bank" label="对方开户行" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="period_date" label="财务月" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="summary" label="摘要" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="amount" label="金额" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="is_checked" label="对账状态" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="precondition" label="预提状态" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="presubmit_confirm_user_name" label="操作人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="presubmit_date" label="预提日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="chargeoff_date" label="冲销日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="presubmit_code" label="预提凭证号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="chargeoff_code" label="冲销凭证号" :show-overflow-tooltip="true"></el-table-column>
                <!--<el-table-column
                        label="操作" width="50"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        &lt;!&ndash;<el-tooltip content="撤回" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-show="scope.row.status == '未提交'">
                            <el-button size="mini" class="withdraw"
                                       @click="withdrawBill(scope.row)"></el-button>
                        </el-tooltip>&ndash;&gt;
                        <el-tooltip content="拒绝" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-show="scope.row.status == '未提交'">
                            <el-button type="danger" icon="el-icon-close" size="mini"
                                       @click="withdrawBill(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>-->
            </el-table>
        </el-main>
        <el-footer>
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
        </el-footer>
    </el-container>
</template>

<script>
    export default {
        name: "AdvanceQuery",
        created: function () {
            this.$emit("transmitTitle", "查询");
            this.$emit("getCommTable", this.routerMessage);

            //机构列表
            this.getOrgList();
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "sftvoucherlist_voucherlist",
                    params: {
                        page_size: 20,
                        page_num: 1,
                        page_flag: 0
                    }
                },
                searchData: { //搜索条件
                    start_date: "",
                    end_date: "",
                    org_id: "",
                    acc_no: "",
                    bankcode: "",
                    period_date: "",
                    min: "",
                    max: "",
                    is_checked: "",
                    presubmit_user_name: "",
                    precondition: []
                },
                dateValue: "",
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                statusList: { //常量数据
                    0: "未预提",
                    1: "预提复核中",
                    2: "已预提",
                    3: "撤销复核中",
                    4: "已撤销",
                    5: "已冲销"
                },
                orgList: [], //常量数据
                accList: [],
                selectId: [], //选中数据
            }
        },
        methods: {
            //清空搜索条件
            clearData: function () {
                var searchData = this.searchData;
                for (var k in searchData) {
                    if (k == "precondition") {
                        searchData[k] = [];
                    } else {
                        searchData[k] = "";
                    }
                }
            },
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                let params = this.routerMessage.params;
                params.page_num = 1;

                for (var k in searchData) {
                    params[k] = searchData[k];
                }

                let dateValue = this.dateValue;
                params.start_date = dateValue ? dateValue[0] : "";
                params.end_date = dateValue ? dateValue[1] : "";

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
            //获取机构列表
            getOrgList: function () {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "sftbankkey_getorg",
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
            //获取银行账号
            getAccData: function(val){
                if(val){
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "sftvoucherlist_getaccbyorg",
                            params: {
                                org_id: val
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
                            this.accList = data;
                        }

                    }).catch(function (error) {
                        console.log(error);
                    });
                }else{
                    this.accList = [];
                }
            },
            //设置当前勾选的id
            saveId: function (selection) {
                this.selectId = [];

                selection.forEach((row) => {
                    this.selectId.push(row.id);
                })
            },

            //导出
            exportFun: function () {
                if (!this.tableList.length) {
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
                        optype: "checkbatch_listexport",
                        params: params
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
            },
            //导出
            exportFun1: function () {
                if (!this.tableList.length) {
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
                        optype: "sftvoucherlist_voucherexport",
                        params: params
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
