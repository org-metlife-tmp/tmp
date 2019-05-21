<style scoped lang="less" type="text/less">
    #paymentWorkbench {
        /*顶部按钮*/
        .button-list-left {
            top: 22px;

            ul {
                font-size: 14px;
                color: #b1b1b1;
                text-align: center;
                height: 30px;
                line-height: 30px;

                li {
                    float: left;
                    margin-right: 4px;
                    height: 100%;
                    width: 100px;
                    border-radius: 3px 3px 0 0;
                    cursor: pointer;
                    background-color: #f2f2f2;
                }

                .active {
                    color: #00b3ed;
                    background-color: #fff;
                }
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
    <el-container id="paymentWorkbench">
        <el-header>
            <div class="button-list-left">
                <ul>
                    <li :class="{'active': searchData.source_sys == '0'}" @click="switchTab('0')">LA</li>
                    <li :class="{'active': searchData.source_sys == '1'}" @click="switchTab('1')">EBS</li>
                </ul>
            </div>
            <div class="button-list-right">
                <el-button type="warning" size="mini" @click="exportFun">导出</el-button>
            </div>
            <div class="search-setion">
                <el-form :inline="true" :model="searchData" size="mini">
                    <el-row>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.preinsure_bill_no" clearable
                                          placeholder="请输入投保单号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.insure_bill_no" clearable placeholder="请输入保单号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.org_id" placeholder="请选择机构"
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
                                <el-input v-model="searchData.recv_cert_code" clearable
                                          placeholder="请输入客户号码"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.recv_acc_name" clearable placeholder="请输入客户名称"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.pay_mode" placeholder="请选择支付方式"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="(payMode,key) in payModeList"
                                               :key="key"
                                               :label="payMode"
                                               :value="key">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
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
                                <el-input v-model="searchData.biz_code" clearable placeholder="请输入业务号码"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.service_status" placeholder="请选择支付结果"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="(billstatus,key) in billstatusList"
                                               :key="key"
                                               :label="billstatus"
                                               :value="key">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="7">
                            <el-form-item style="margin-bottom:0px">
                                <el-checkbox-group v-model="searchData.status">
                                    <el-checkbox v-for="(status,key) in statusList"
                                                 :key="key" :label="key">
                                        {{status}}
                                    </el-checkbox>
                                </el-checkbox-group>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="clearData" size="mini">清空筛选</el-button>
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
            <div class="split-bar"></div>
        </el-header>
        <el-main>
            <el-table :data="tableList"
                      @selection-change="setId"
                      border size="mini" height="100%">
                <el-table-column type="selection" width="40" :selectable="isSelect"></el-table-column>
                <el-table-column prop="push_date" label="业务操作日期" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="name" label="机构名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_mode" label="支付方式" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="biz_code" label="业务号码" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="preinsure_bill_no" label="投保单号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="insure_bill_no" label="保单号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="type_name" label="业务类型" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="amount" label="金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="recv_acc_name" label="客户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_cert_code" label="客户号码" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_acc_name" label="收款账号户名" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_acc_no" label="收款银行账号" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_bank_name" label="开户行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_code" label="支付号码" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="service_status" label="支付结果" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_acc_no" label="付款账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_bank_name" label="付款银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="status" label="状态" :show-overflow-tooltip="true"
                                 :formatter="transitStatus"></el-table-column>
                <el-table-column prop="op_user_name" label="操作人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="op_date" label="操作日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="actual_payment_date" label="实付日期"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="80"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="补录" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-show="scope.row.status == 0">
                            <el-button type="primary" icon="el-icon-edit-outline" size="mini"
                                       @click="addRecord(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-show="scope.row.status == 1">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="addRecord(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="拒绝" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="danger" icon="el-icon-close" size="mini"
                                       @click="rejectData(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>
        <el-footer>
            <div class="allData">
                <div class="btn-left">
                    <el-button type="warning" size="mini" @click="submit"
                               :disabled="selectId.length == 0">提交
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
            <!--补录弹出框-->
            <el-dialog :visible.sync="dialogVisible"
                       width="810px" title="补录"
                       :close-on-click-modal="false"
                       top="56px">
                <el-form :model="dialogData" size="small"
                         :label-width="formLabelWidth"
                         :rules="rules" ref="dialogForm">
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="收款账号户名" prop="recv_acc_name">
                                <el-input v-model="dialogData.recv_acc_name" clearable
                                          placeholder="请输入收款账号户名" :disabled="isLook"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="收款银行账号" prop="recv_acc_no">
                                <el-input v-model="dialogData.recv_acc_no" clearable
                                          placeholder="请输入收款银行账号" :disabled="isLook"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="16">
                            <el-form-item label="开户行" prop="recv_bank_name">
                                <el-input v-model="dialogData.recv_bank_name" :disabled="isLook"
                                          placeholder="请选择开户行" @focus="getBank"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="摘要">
                                <el-input v-model="dialogData.payment_summary" clearable
                                          placeholder="请输入摘要" :disabled="isLook"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="附件">
                                <Upload @currentFielList="setFileList"
                                        :emptyFileList="emptyFileList"
                                        :fileMessage="fileMessage"
                                        :triggerFile="triggerFile"
                                        :isPending="!isLook"></Upload>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
                <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="saveAddRecord">确 定</el-button>
            </span>
            </el-dialog>
            <!--开户行选择弹框-->
            <el-dialog :visible.sync="bankdialogVisible"
                       width="40%" title="选择开户行"
                       top="140px" :close-on-click-modal="false">

                <el-form :model="bankdialogData" size="small">
                    <el-row>
                        <el-col :span="24">
                            <el-form-item label="银行大类" :label-width="formLabelWidth">
                                <el-select v-model="bankdialogData.bankTypeName" placeholder="请选择银行大类"
                                           clearable filterable
                                           style="width:100%"
                                           :filter-method="filterBankType"
                                           :loading="bankLongding"
                                           @visible-change="clearSearch"
                                           @change="bankIsSelect">
                                    <el-option v-for="bankType in bankTypeList"
                                               :key="bankType.name"
                                               :label="bankType.display_name"
                                               :value="bankType.code">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="地区" :label-width="formLabelWidth">
                                <el-select v-model="bankdialogData.area"
                                           filterable remote clearable
                                           style="width:100%"
                                           placeholder="请输入地区关键字"
                                           :remote-method="getAreaList"
                                           :loading="loading"
                                           @change="bankIsSelect">
                                    <el-option
                                            v-for="area in areaList"
                                            :key="area.name + '-' + area.top_super"
                                            :value="area.name + '-' + area.top_super">
                                        <span>{{ area.name }}</span><span style="margin-left:10px;color:#bbb">{{ area.top_super }}</span>
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="开户行" :label-width="formLabelWidth">
                                <el-select v-model="bankdialogData.bank_name" placeholder="请选择银行"
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
                    </el-row>
                </el-form>

                <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="bankdialogVisible = false">取 消</el-button>
                    <el-button type="warning" size="mini" @click="saveBankinfo">确 定</el-button>
                </span>
            </el-dialog>

            <!--拒绝弹框-->
            <el-dialog title="拒绝理由"
                       top="200px" width="310px"
                       :visible.sync="rejectDialogVisible"
                       :close-on-click-modal="false">
                <el-select v-model="rejectMessage" placeholder="请选择拒绝理由" clearable size="small">
                    <el-option label="TMPPJ:变更支付方式" value="TMPPJ:变更支付方式"></el-option>
                    <el-option label="TMPPJ:拒绝支付" value="TMPPJ:拒绝支付"></el-option>
                </el-select>
                <span slot="footer" class="dialog-footer">
                <el-button type="warning" plain @click="rejectDialogVisible = false" size="mini">取 消</el-button>
                <el-button type="warning" @click="submitReject" size="mini">确 定</el-button>
            </span>
            </el-dialog>
        </el-footer>
    </el-container>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";

    export default {
        name: "PaymentWorkbench",
        created: function () {
            this.$emit("transmitTitle", "付款工作台");
            // this.$emit("getCommTable", this.routerMessage);

            /*获取常量数据*/
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            //来源系统
            if (constants.SftOsSource) {
                this.sourceList = constants.SftOsSource;
            }
            //支付方式
            if (constants.SftDoubtPayMode) {
                // this.payModeList = constants.SftDoubtPayMode;
                this.payModeList = {
                    "3": "网银"
                }
            }
            //支付结果
            if (constants.Sft_Billstatus) {
                this.billstatusList = constants.Sft_Billstatus;
            }
            //状态
            if (constants.SftLegalData) {
                this.statusList = constants.SftLegalData;
            }
            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
            }
            var bankAllTypeList = JSON.parse(window.sessionStorage.getItem("bankAllTypeList"));
            if (bankAllTypeList) {
                this.bankAllTypeList = bankAllTypeList;
            }
            //机构列表
            this.getOrgList();
        },
        props: ["tableData"],
        components: {
            Upload: Upload
        },
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "paycounter_list",
                    params: {
                        page_size: 20,
                        page_num: 1,
                        source_sys: "0",
                        status: ["0"]
                    }
                },
                searchData: { //搜索条件
                    source_sys: "0",
                    preinsure_bill_no: "",
                    insure_bill_no: "",
                    org_id: "",
                    recv_cert_code: "",
                    recv_acc_name: "",
                    pay_mode: "",
                    biz_code: "",
                    service_status: "",
                    status: ["0"]
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
                payModeList: {},
                orgList: [],
                billstatusList: [],
                statusList: {},
                dialogVisible: false, //弹框数据
                dialogData: {
                    recv_acc_name: "",
                    recv_acc_no: "",
                    recv_bank_name: "",
                    payment_summary: "",
                    recv_cnaps_code: "",
                },
                formLabelWidth: "120px",
                isLook: false,
                //校验规则设置
                rules: {
                    recv_acc_name: {
                        required: true,
                        message: "请输入收款账号户名",
                        trigger: "blur"
                    },
                    recv_acc_no: {
                        required: true,
                        message: "请输入收款银行账号",
                        trigger: "blur"
                    },
                    recv_bank_name: {
                        required: true,
                        message: "请选择开户行",
                        trigger: "change"
                    }
                },
                bankdialogVisible: false, //选择银行弹框
                bankdialogData: {
                    bankTypeName: "",
                    area: "",
                    cnaps_code: "",
                    bank_name: "",
                },
                bankSelect: true, //银行可选控制
                bankAllList: [], //弹框下拉框数据
                bankAllTypeList: [], //银行大类全部(不重复)
                bankTypeList: [],
                areaList: [],
                loading: false,
                bankList: [],
                outTime: "", //银行大类搜索控制
                bankLongding: false,
                emptyFileList: [], //附件
                fileMessage: {
                    bill_id: "",
                    biz_type: 32
                },
                triggerFile: false,
                fileList: [],
                currentData: "", //当前项
                selectId: [],
                selectVersion: [],
                rejectDialogVisible: false, //拒绝弹框
                rejectMessage: ""
            }
        },
        methods: {
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                for (var k in searchData) {
                    this.routerMessage.params[k] = searchData[k];
                }
                var val = this.dateValue;
                this.routerMessage.params.start_date = val ? val[0] : "";
                this.routerMessage.params.end_date = val ? val[1] : "";
                this.routerMessage.params.page_num = 1;
                this.$emit("getCommTable", this.routerMessage);
            },
            //清空搜索条件
            clearData: function () {
                var searchData = this.searchData;
                for (var k in searchData) {
                    if (k == "status") {
                        searchData[k] = [];
                    } else if (k != "source_sys") {
                        searchData[k] = "";
                    }
                }
                this.dateValue = "";
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
            //切换标签
            switchTab: function (tab) {
                var searchData = this.searchData;
                for (var k in searchData) {
                    if (k == "source_sys") {
                        searchData[k] = tab;
                    } else if (k == "status") {
                        searchData[k] = ["0"];
                    } else {
                        searchData[k] = "";
                    }
                }
                this.dateValue = "";
                this.queryData();
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
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //展示格式转换-状态
            transitStatus: function (row, column, cellValue, index) {
                return this.statusList[cellValue];
            },
            //补录
            addRecord: function (row) {
                this.dialogVisible = true;
                let dialogData = this.dialogData;
                for (let k in dialogData) {
                    dialogData[k] = row[k];
                }

                if (this.$refs.dialogForm) {
                    this.$refs.dialogForm.clearValidate();
                }

                this.currentData = row;

                this.isLook = row.status == 0 ? false : true;

                //附件数据
                this.emptyFileList = [];
                this.fileMessage.bill_id = row.gmf_id ? row.gmf_id : row.pay_id;
                this.triggerFile = !this.triggerFile;
            },
            //选择开户行
            getBank: function () {
                this.bankdialogVisible = true;
                this.bankSelect = true;
                var bankdialogData = this.bankdialogData;
                for (var k in bankdialogData) {
                    bankdialogData[k] = "";
                }
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
            //银行大类/地址变化后判断银行是否可选
            bankIsSelect: function (value) {
                this.bankList = [];
                this.bankdialogData.bank_name = "";
                this.bankdialogData.cnaps_code = "";
                if (this.bankdialogData.area && this.bankdialogData.bankTypeName) {
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
                        url: this.queryUrl + "commProcess",
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
                    var area_code = this.bankdialogData.area.split("-");
                    var bank_type = this.bankdialogData.bankTypeName;

                    this.$axios({
                        url: this.queryUrl + "commProcess",
                        method: "post",
                        data: {
                            optype: "bank_list",
                            params: {
                                province: area_code[1],
                                city: area_code[0],
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
            setCNAPS: function (value) {
                var bankList = this.bankList;
                for (var i = 0; i < bankList.length; i++) {
                    if (bankList[i].name == value) {
                        this.bankdialogData.cnaps_code = bankList[i].cnaps_code;
                        return;
                    }
                }
            },
            //保存选中的银行
            saveBankinfo: function () {
                this.dialogData.recv_bank_name = this.bankdialogData.bank_name;
                this.dialogData.recv_cnaps_code = this.bankdialogData.cnaps_code;
                this.bankdialogVisible = false;
            },
            //设置当前项上传附件
            setFileList: function ($event) {
                this.fileList = [];
                if ($event.length > 0) {
                    $event.forEach((item) => {
                        this.fileList.push(item.id);
                    })
                }
            },
            //保存补录信息
            saveAddRecord: function () {
                this.$refs.dialogForm.validate((valid, object) => {
                    if (valid) {
                        let dialogData = this.dialogData;
                        let params = {
                            files: this.fileList,
                            source_sys: this.routerMessage.params.source_sys,
                            pay_id: this.currentData.pay_id,
                            persist_version: this.currentData.persist_version
                        };
                        for (let k in dialogData) {
                            params[k] = dialogData[k];
                        }

                        this.$axios({
                            url: this.queryUrl + "normalProcess",
                            method: "post",
                            data: {
                                optype: "paycounter_supplement",
                                params: params
                            }
                        }).then((result) => {
                            if (result.data.error_msg) {
                                this.$message({
                                    type: "error",
                                    message: result.data.error_msg,
                                    duration: 2000
                                });
                                return;
                            } else {
                                this.$message({
                                    type: "success",
                                    message: "补录信息保存成功",
                                    duration: 2000
                                });
                                this.dialogVisible = false;
                                this.$emit("getCommTable", this.routerMessage);
                            }
                        }).catch(function (error) {
                            console.log(error);
                        })
                    } else {
                        return false;
                    }
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
                        optype: "paycounter_listexport",
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
            //拒绝
            rejectData: function (row) {
                this.rejectDialogVisible = true;
                this.rejectMessage = "";
                this.currentData = row;
            },
            //确定拒绝
            submitReject: function () {
                let currentData = this.currentData;
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "paycounter_revokeToLaOrEbs",
                        params: {
                            pay_id: currentData.pay_id,
                            source_sys: currentData.source_sys,
                            persist_version: currentData.persist_version,
                            feed_back: this.rejectMessage
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        });
                        return;
                    } else {
                        this.$message({
                            type: "success",
                            message: "拒绝成功",
                            duration: 2000
                        });
                        this.rejectDialogVisible = false;
                        this.$emit("getCommTable", this.routerMessage);
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //当前列是否可以勾选
            isSelect: function (row, index) {
                return !(row.status == "1" || row.status == "2");
            },
            //设置未保存的id
            setId: function (selection) {
                this.selectId = [];
                this.selectVersion = [];
                let selectId = this.selectId;
                let selectVersion = this.selectVersion;
                for (let i = 0; i < selection.length; i++) {
                    let row = selection[i];
                    selectId.push(row.pay_id);
                    selectVersion.push(row.persist_version);
                }
            },
            //提交
            submit: function () {
                let params = {
                    source_sys: this.routerMessage.params.source_sys,
                    pay_id: this.selectId,
                    persist_version: this.selectVersion,
                }
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "paycounter_confirm",
                        params: params
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        });
                        return;
                    } else {
                        this.$message({
                            type: "success",
                            message: "提交成功",
                            duration: 2000
                        });
                        this.$emit("getCommTable", this.routerMessage);
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
                this.tableList = val.data;
                this.pagCurrent = val.page_num;
                this.selectId = [];
                this.selectVersion = [];
            }
        }
    }
</script>





