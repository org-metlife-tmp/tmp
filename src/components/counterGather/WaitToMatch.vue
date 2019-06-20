<style scoped lang="less" type="text/less">
    #waitToMatch {

    }
</style>
<style>
    .el-popover {
        min-width: 10px;
    }
</style>

<template>
    <el-container id="waitToMatch">
        <el-header>
            <div class="button-list-left">

            </div>
            <div class="button-list-right">
                <el-button type="warning" size="mini" @click="addData">新增</el-button>
                <el-button type="warning" size="mini" @click="exportFun">导出</el-button>
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
                                <el-select v-model="searchData.recv_org_id" placeholder="请选择收款机构"
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
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.recv_mode" placeholder="请选择收款方式"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="(recvmode,key) in recvmodeList"
                                               :key="key"
                                               :label="recvmode"
                                               :value="key">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.recv_bank_name" clearable
                                          placeholder="请输入收款银行"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.bill_status" placeholder="请选择票据状态"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="(billStatus,key) in billStatusList"
                                               :key="key"
                                               :label="billStatus"
                                               :value="key">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="clearData" size="mini">清空筛选</el-button>
                            </el-form-item>
                        </el-col>

                        <el-col :span="5">
                            <el-form-item>
                                <el-col :span="11">
                                    <el-input v-model="searchData.min" clearable placeholder="最小金额"></el-input>
                                </el-col>
                                <el-col class="line" :span="1" style="text-align:center">-</el-col>
                                <el-col :span="11">
                                    <el-input v-model="searchData.max" clearable placeholder="最大金额"></el-input>
                                </el-col>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.terminal_no" clearable placeholder="请输入终端机编号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item style="margin-bottom:0px">
                                <el-checkbox-group v-model="searchData.match_status">
                                    <el-checkbox v-for="(name,k) in statusList"
                                                 :label="k" name="name" :key="k">
                                        {{ name }}
                                    </el-checkbox>
                                </el-checkbox-group>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </div>
        </el-header>
        <el-main>
            <el-table :data="tableList" border
                      highlight-current-row
                      size="mini" height="100%"
                      @current-change="saveCurrent">
                <el-table-column prop="recv_date" label="收款日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="batch_process_no" label="批处理号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_mode" label="收款方式" :show-overflow-tooltip="true"
                                 :formatter="transitMode"></el-table-column>
                <el-table-column prop="recv_bank_name" label="收款银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_acc_no" label="收款账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bill_status" label="票据状态" :show-overflow-tooltip="true"
                                 :formatter="transitBill"></el-table-column>
                <el-table-column prop="bill_number" label="票据编号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bill_date" label="票据日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="terminal_no" label="终端机编号" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="amount" label="金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="name" label="客户银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="consumer_acc_no" label="客户账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="create_user_name" label="操作人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_org_name" label="收款机构" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="match_status" label="状态" :show-overflow-tooltip="true"
                                 :formatter="transitStatus"></el-table-column>
                <el-table-column prop="bill_type" label="业务类型" :show-overflow-tooltip="true"
                                 :formatter="transitBillType"></el-table-column>
                <el-table-column prop="source_sys" label="核心系统" :show-overflow-tooltip="true"
                                 :formatter="transitSource"></el-table-column>
                <el-table-column prop="batch_no" label="批单号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="preinsure_bill_no" label="投保单号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="insure_bill_no" label="保单号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bill_org_name" label="保单机构" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="use_funds" label="资金用途" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="insure_acc_no" label="投保人客户号" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="insure_name" label="投保人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="business_acc_no" label="业务所属客户号" width="120px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="business_acc" label="业务所属客户" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="consumer_no" label="客户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="consumer_acc_name" label="客户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="third_payment" label="第三方缴费" width="100px"
                                 :show-overflow-tooltip="true" :formatter="transitPayment"></el-table-column>
                <el-table-column prop="payer" label="缴费人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_code" label="缴费人编码" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="match_user_name" label="匹配人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="match_on" label="匹配时间" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="refund_user_name" label="退款人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="fefund_bank_name" label="退款银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="refund_acc_no" label="退款银行账号" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="50"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookCurrent(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>
        <el-footer>
            <div class="allData">
                <div class="btn-right">
                    <el-popover
                            placement="top"
                            trigger="click">
                        <el-button-group>
                            <el-button size="mini" type="primary" @click="matching('SingleGather')">个单</el-button>
                            <el-button type="primary" size="mini" @click="matching('MassSingleGather')">团单</el-button>
                        </el-button-group>
                        <el-button slot="reference" type="warning" size="mini" :disabled="!hasSelect">匹配</el-button>
                    </el-popover>

                    <el-button type="warning" size="mini" :disabled="!hasSelect"
                               @click="refundData">回退
                    </el-button>
                </div>
            </div>
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

            <!--新增/修改 弹出框-->
            <el-dialog :visible.sync="dialogVisible"
                       width="860px" top="100px"
                       :close-on-click-modal="false">
                <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
                <el-form :model="dialogData" size="small"
                         :label-width="formLabelWidth">
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="收款日期">
                                <el-date-picker
                                        v-model="dialogData.recv_date"
                                        type="date" disabled
                                        placeholder="请选择日期"
                                        value-format="yyyy-MM-dd"
                                        style="width:100%">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="批处理号">
                                <el-input v-model="dialogData.batch_process_no" disabled></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="币种">
                                <el-select v-model="dialogData.currency" placeholder="请选择币种"
                                           filterable clearable>
                                    <el-option v-for="currency in currencyList"
                                               :key="currency.id"
                                               :label="currency.name"
                                               :value="currency.id">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="收款方式">
                                <el-select v-model="dialogData.recv_mode" placeholder="请选择收款方式"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="(recvmode,key) in recvmodeList"
                                               :key="key"
                                               :label="recvmode"
                                               :value="key">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="票据状态">
                                <el-select v-model="dialogData.bill_status" placeholder="请选择票据状态"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="(billStatus,key) in billStatusList"
                                               :key="key"
                                               :label="billStatus"
                                               :value="key">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="票据编号">
                                <el-input v-model="dialogData.bill_number" placeholder="请输入票据编号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="票据日期">
                                <el-date-picker
                                        v-model="dialogData.bill_date"
                                        type="date"
                                        placeholder="请选择票据日期"
                                        value-format="yyyy-MM-dd"
                                        style="width:100%">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="收款银行">
                                <el-select v-model="dialogData.recv_bank_name" placeholder="请选择收款银行"
                                           filterable clearable>
                                    <el-option v-for="bank in recvBankList"
                                               :key="bank.bankcode"
                                               :label="bank.bankcode"
                                               :value="bank.bankcode">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="银行账号">
                                <el-input v-model="dialogData.recv_acc_no" placeholder="请输入银行账号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="客户银行">
                                <el-select v-model="dialogData.consumer_bank_name" placeholder="请选择银行大类"
                                           clearable filterable
                                           style="width:100%"
                                           :filter-method="filterBankType"
                                           :loading="bankLongding"
                                           @visible-change="clearSearch">
                                    <el-option v-for="bankType in bankTypeList"
                                               :key="bankType.name"
                                               :label="bankType.display_name"
                                               :value="bankType.code">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="客户账号">
                                <el-input v-model="dialogData.consumer_acc_no" placeholder="请输入客户账号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="终端机编号">
                                <el-input v-model="dialogData.terminal_no" placeholder="请输入终端机编号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="金额">
                                <el-input v-model="dialogData.amount" placeholder="请输入金额"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="缴费人">
                                <el-input v-model="dialogData.payer" placeholder="请输入缴费人"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="缴费人证件号">
                                <el-input v-model="dialogData.payer_cer_no" placeholder="请输入缴费人证件号"></el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
                <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="saveData">确 定</el-button>
            </span>
            </el-dialog>
        </el-footer>
    </el-container>
</template>

<script>
    export default {
        name: "WaitToMatch",
        created: function () {
            this.$emit("transmitTitle", "待匹配收款");
            // this.$emit("getCommTable", this.routerMessage);

            /*获取常量数据*/
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            //来源系统
            if (constants.SftOsSource) {
                this.sourceList = constants.SftOsSourceCounter;
            }
            //票据状态
            if (constants.SftRecvCounterBillStatus) {
                this.billStatusList = constants.SftRecvCounterBillStatus;
            }
            //收款方式
            if (constants.Sft_RecvPersonalCounter_Recvmode) {
                this.recvmodeList = constants.Sft_RecvPersonalCounter_Recvmode;
            }
            //资金用途
            if (constants.SftRecvPersonalCounterUseFunds) {
                this.useFundList = constants.SftRecvGroupCounterUseFunds;
            }
            //币种
            var currencyList = JSON.parse(window.sessionStorage.getItem("selectCurrencyList"));
            if (currencyList) {
                this.currencyList = currencyList;
            }
            //机构列表
            this.getOrgList();
            //收款银行
            this.getRecvBank();
            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
            }
            var bankAllTypeList = JSON.parse(window.sessionStorage.getItem("bankAllTypeList"));
            if (bankAllTypeList) {
                this.bankAllTypeList = bankAllTypeList;
            }
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "recvcounterwaitingformatch_list",
                    params: {
                        page_size: 20,
                        page_num: 1
                    }
                },
                searchData: { //搜索条件
                    recv_org_id: "",
                    recv_mode: "",
                    recv_bank_name: "",
                    bill_status: "",
                    terminal_no: "",
                    min: "",
                    max: "",
                    match_status: []
                },
                dateValue: "", //时间控件
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                sourceList: {}, //常量数据
                useFundList: {},
                statusList: {
                    0: "待匹配",
                    1: "已撤销",
                    2: "已匹配",
                    3: "退费中",
                    4: "已退费",
                },
                orgList: [],
                billStatusList: {},
                recvmodeList: {},
                currencyList: [],
                recvBankList: [],
                outTime: "", //银行大类搜索控制
                bankLongding: false,
                bankAllList: [],
                bankTypeList: [],
                bankAllTypeList: [], //银行大类全部(不重复)
                dialogVisible: false, //弹框
                dialogTitle: "新增",
                dialogData: {
                    recv_date: "",
                    batch_process_no: "",
                    currency: "",
                    recv_mode: "",
                    bill_status: "",
                    bill_number: "",
                    bill_date: "",
                    recv_bank_name: "",
                    recv_acc_no: "",
                    consumer_bank_name: "",
                    consumer_acc_no: "",
                    terminal_no: "",
                    amount: "",
                    payer: "",
                    payer_cer_no: "",
                },
                formLabelWidth: "120px",
                currentData: "",
                hasSelect: false
            }
        },
        methods: {
            //清空搜索条件
            clearData: function () {
                var searchData = this.searchData;
                for (var k in searchData) {
                    if (k == "match_status") {
                        searchData[k] = [];
                    } else {
                        searchData[k] = "";
                    }
                }
                this.dateValue = "";
            },
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                var params = this.routerMessage.params;
                for (var k in searchData) {
                    params[k] = searchData[k];
                }
                var val = this.dateValue;
                params.start_date = val ? val[0] : "";
                params.end_date = val ? val[1] : "";
                params.page_num = 1;
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
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //展示格式转换-收款方式
            transitMode: function (row, column, cellValue, index) {
                return this.recvmodeList[cellValue];
            },
            //展示格式转换-票据状态
            transitBill: function (row, column, cellValue, index) {
                return this.billStatusList[cellValue];
            },
            //展示格式转换-状态
            transitStatus: function (row, column, cellValue, index) {
                let constants = JSON.parse(window.sessionStorage.getItem("constants"));
                return constants.SftRecvCounterMatchStatus[cellValue];
            },
            //展示格式转换-来源系统
            transitSource: function (row, column, cellValue, index) {
                return this.sourceList[cellValue];
            },
            //展示格式转换-业务类型
            transitBillType: function (row, column, cellValue, index) {
                let constants = JSON.parse(window.sessionStorage.getItem("constants"));
                return constants.SftRecvType[cellValue];
            },
            //展示格式转换-第三方缴费
            transitPayment: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                return constants.YesOrNo[cellValue];
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
            //收款银行
            getRecvBank: function () {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "recvgroupcounter_getBankcode",
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
                        this.recvBankList = data;
                    }

                }).catch(function (error) {
                    console.log(error);
                });
            },
            //银行大类搜索筛选
            filterBankType: function (value) {
                this.bankLongding = true;
                clearTimeout(this.outTime);
                this.outTime = setTimeout(() => {
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
                        });
                        this.bankTypeList = this.bankTypeList.filter((item, index, arr) => {
                            for (var i = index + 1; i < arr.length; i++) {
                                if (item.display_name == arr[i].display_name) {
                                    return false;
                                }
                            }
                            return true;
                        });
                    } else {
                        this.bankTypeList = this.bankAllTypeList.slice(0, 200);
                    }
                    this.bankLongding = false;
                }, 1200);
            },
            //银行大类展开时重置数据
            clearSearch: function (val) {
                if (this.bankTypeList != this.bankAllTypeList && val) {
                    this.bankTypeList = this.bankAllTypeList.slice(0, 200);
                }
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
                        optype: "recvcounterwaitingformatch_listexport",
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
            //新增
            addData: function () {
                this.dialogTitle = "新增";
                let dialogData = this.dialogData;
                for (let k in dialogData) {
                    if (k == "recv_date") {
                        let curDate = new Date();
                        dialogData[k] = curDate.getFullYear() + "-" + (curDate.getMonth() + 1) + "-" + curDate.getDate();
                    } else if (k == "batch_process_no") {
                        this.$axios({
                            url: this.queryUrl + "normalProcess",
                            method: "post",
                            data: {
                                optype: "recvcounter_getBatchProcessno",
                                params: {
                                    recv_type: 0
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
                                dialogData[k] = result.data.data.batch_process_no;
                            }
                        }).catch(function (error) {
                            console.log(error);
                        });
                    } else if (k == "currency") {
                        dialogData[k] = 1;
                    } else {
                        dialogData[k] = "";
                    }
                }

                this.dialogVisible = true;
            },
            //保存新增数据
            saveData: function () {
                let dialogData = this.dialogData;

                let params = {};
                for (let k in dialogData) {
                    params[k] = dialogData[k];
                }

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "recvcounterwaitingformatch_add",
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
                        this.dialogVisible = false;
                        this.$message({
                            type: "success",
                            message: "保存成功",
                            duration: 2000
                        });
                        this.$emit("getCommTable", this.routerMessage);
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //保存当前数据
            saveCurrent: function (row) {
                if (row) {
                    this.currentData = row;
                    this.hasSelect = true;
                } else {
                    this.currentData = "";
                    this.hasSelect = false;
                }
            },
            //匹配
            matching: function (pushName) {
                if (this.currentData.id) {
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "recvcounterwaitingformatch_match",
                            params: {
                                id: this.currentData.id
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
                            let data = result.data.data;
                            this.$router.push({
                                name: pushName,
                                params: {
                                    matchData: data
                                }
                            });
                        }
                    }).catch(function (error) {
                        console.log(error);
                    });
                }
            },
            //回退
            refundData: function () {
                if (this.currentData.id) {
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "recvcounterwaitingformatch_refund",
                            params: {
                                id: this.currentData.id
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
                            let data = result.data.data;
                            this.$router.push({
                                name: "PaymentWorkbench",
                                params: {
                                    matchData: data
                                }
                            });
                        }
                    }).catch(function (error) {
                        console.log(error);
                    });
                }
            },

            //查看
            lookCurrent: function (row) {
                this.dialogTitle = "查看";
                this.currentData = row;

                let dialogData = this.dialogData;
                for (let k in dialogData) {
                    dialogData[k] = "";
                }
                this.items = [
                    {
                        insure_bill_no: "",
                        amount: "",
                        bill_org_name: "",
                        source_sys: "",
                        insure_name: "",
                        insure_cer_no: "",
                        isnot_electric_pay: "",
                        isnot_bank_transfer_premium: "",
                        third_payment: "0",
                        payer: "",
                        payer_cer_no: "",
                        $id: 1
                    }
                ];

                this.dialogVisible = true;

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "recvcounter_detail",
                        params: {
                            id: row.id
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
                        let data = result.data.data;
                        let dialogData = this.dialogData;
                        for (let k in data) {
                            if (k == "policy_infos") {
                                let infoList = data[k];
                                infoList.forEach((item) => {
                                    item.$id = new Date();
                                });
                                this.items = infoList;
                            } else {
                                dialogData[k] = data[k];
                            }
                        }
                    }
                }).catch(function (error) {
                    console.log(error);
                });

                this.isLook = true;
                this.fileMessage.bill_id = row.id;
                this.triggerFile = !this.triggerFile;
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
