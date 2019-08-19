<style scoped lang="less" type="text/less">
    #singleGather {
        /*弹框样式*/
        .split-form {
            width: 100%;
            height: 26px;
            border-bottom: 1px solid #eee;
            margin-top: 20px;
            margin-bottom: 10px;
            padding-bottom: 1px;
            text-align: right;
        }

        .small-title {
            margin: 0;
            position: absolute;
            top: 18px;
        }

        /*按钮样式*/
        .withdraw {
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
            background-position: -48px 0;
        }
    }
</style>

<template>
    <el-container id="singleGather">
        <el-header>
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
                        :picker-options="pickerOptions">
                </el-date-picker>
            </div>
            <div class="button-list-right">
                <el-button type="warning" size="mini" @click="addData">新增</el-button>
                <el-button type="warning" size="mini" @click="exportFun">导出</el-button>
            </div>
            <div class="search-setion">
                <el-form :inline="true" :model="searchData" size="mini">
                    <el-row>
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
                                <el-select v-model="searchData.source_sys"
                                           clearable filterable size="mini"
                                           placeholder="请选择核心系统">
                                    <el-option v-for="(item,key) in sourceList"
                                               :key="key"
                                               :label="item"
                                               :value="key">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.insure_bill_no" clearable placeholder="请输入保单号"></el-input>
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
                                <el-input v-model="searchData.recv_bank_name" clearable placeholder="请输入收款银行"></el-input>
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

                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.terminal_no" clearable placeholder="请输入终端机编号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.use_funds" placeholder="请选择资金用途"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="(useFund,key) in useFundList"
                                               :key="key"
                                               :label="useFund"
                                               :value="key">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.third_payment" placeholder="是否第三方缴费"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="(item,key) in YesOrNo"
                                               :key="key"
                                               :label="item"
                                               :value="key">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="5">
                            <el-form-item>
                                <el-col :span="11">
                                    <el-input v-model.number="searchData.min" clearable placeholder="最小金额"></el-input>
                                </el-col>
                                <el-col class="line" :span="1" style="text-align:center">-</el-col>
                                <el-col :span="11">
                                    <el-input v-model.number="searchData.max" clearable placeholder="最大金额"></el-input>
                                </el-col>
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

                        <el-col :span="21">
                            <el-form-item style="margin-bottom:0px">
                                <el-checkbox-group v-model="searchData.pay_status">
                                    <el-checkbox v-for="(name,k) in statusList"
                                                 :label="k" name="name" :key="k">
                                        {{ name }}
                                    </el-checkbox>
                                </el-checkbox-group>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </div>
        </el-header>
        <el-main>
            <el-table :data="tableList"
                      border size="mini" height="100%">
                <el-table-column prop="recv_date" label="收款日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="batch_process_no" label="批处理号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="source_sys" label="核心系统" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="insure_bill_no" label="保单号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bill_org_name" label="保单机构" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_mode" label="收款方式" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_bank_name" label="收款银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_acc_no" label="收款账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="use_funds" label="资金用途" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bill_status" label="票据状态" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bill_number" label="票据票号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bill_date" label="票据日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="terminal_no" label="终端机编号" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="amount" label="金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="consumer_bank_name" label="客户银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="consumer_acc_no" label="客户账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="insure_name" label="投保人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="insure_cer_no" label="投保人证件号" width="120px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="third_payment" label="第三方缴费" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="payer" label="缴费人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="payer_cer_no" label="缴费人证件号" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="create_user_name" label="操作人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_org_name" label="收款机构" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_status" label="状态" :show-overflow-tooltip="true"
                                 :formatter="transitStatus"></el-table-column>
                <el-table-column
                        label="操作" width="80"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookCurrent(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="撤销" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.pay_status == 1">
                            <el-button size="mini" class="withdraw"
                                       @click="revocationData(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
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
                            <el-form-item label="资金用途">
                                <el-select v-model="dialogData.use_funds" placeholder="请选择资金用途"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="(useFund,key) in useFundList"
                                               :key="key"
                                               :label="useFund"
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
                                           filterable clearable @change="setAccNo">
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

                        <el-col :span="24">
                            <el-form-item label="附件">
                                <Upload @currentFielList="setFileList"
                                        :fileMessage="fileMessage"
                                        :emptyFileList="emptyFileList"
                                        :triggerFile="eidttrigFile"
                                        :isPending="!isLook">
                                </Upload>
                            </el-form-item>
                        </el-col>

                        <el-col :span="24" style="position:relative">
                            <h4 class="small-title">保单</h4>
                        </el-col>
                    </el-row>
                    <el-row v-for="item in items"
                            :key="item.$id">
                        <el-col :span="24">
                            <div class="split-form">
                                <el-button-group>
                                    <el-button size="mini" @click="removeAccount(item)"
                                               v-show="showDel">删除
                                    </el-button>
                                    <el-button size="mini" style="margin-left:0"
                                               @click="addAccount">新增
                                    </el-button>
                                </el-button-group>
                            </div>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="保单号">
                                <el-input v-model="item.insure_bill_no" placeholder="请输入保单号"
                                          @change="setBillInfo(item)"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="金额">
                                <el-input v-model="item.amount" placeholder="请输入金额"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="保单机构">
                                <el-input v-model="item.bill_org_name" disabled></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="核心系统">
                                <el-select v-model="item.source_sys" disabled>
                                    <el-option v-for="(item,key) in sourceList"
                                               :key="key"
                                               :label="item"
                                               :value="key">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="投保人">
                                <el-input v-model="item.insure_name" disabled></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="投保人证件号">
                                <el-input v-model="item.insure_cer_no" disabled></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="允许垫交">
                                <el-radio-group  v-model="item.isnot_electric_pay">
                                    <el-radio class="radio" label="yes">是</el-radio>
                                    <el-radio class="radio" label="no">否</el-radio>
                                </el-radio-group>
                            </el-form-item>
                                <!--<el-select v-model="item.isnot_electric_pay" disabled>
                                    <el-option v-for="(item,key) in YesOrNo"-->
                                               <!--:key="key"-->
                                               <!--:label="item"-->
                                               <!--:value="key">-->
                                    <!--</el-option>
                                     </el-select>-->
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="是否银行转账">
                                <el-select v-model="item.isnot_bank_transfer_premium" disabled>
                                    <el-option v-for="(item,key) in YesOrNo"
                                               :key="key"
                                               :label="item"
                                               :value="key">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="是否第三方缴费">
                                <el-switch
                                        v-model="item.third_payment"
                                        active-value="1"
                                        inactive-value="0"
                                        @change="setPayInfo(item)">
                                </el-switch>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="缴费人">
                                <el-input v-model="item.payer" placeholder="请输入缴费人"
                                          :disabled="item.third_payment == '0'"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="缴费人证件号">
                                <el-input v-model="item.payer_cer_no" placeholder="请输入缴费人证件号"
                                          :disabled="item.third_payment == '0'"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="与投保人关系">
                                <el-input v-model="dialogData.payer_relation_insured" placeholder="请输入与投保人关系"
                                          :disabled="item.third_payment == '0'"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="代缴费原因">
                                <el-input v-model="dialogData.pay_reason" placeholder="请输入代缴费原因"
                                          :disabled="item.third_payment == '0'"></el-input>
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
    import Upload from "../publicModule/Upload.vue";

    export default {
        name: "SingleGather",
        created: function () {
            this.$emit("transmitTitle", "个单收款");
            // this.$emit("getCommTable", this.routerMessage);

            /*获取常量数据*/
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            //来源系统
           /* if (constants.SftOsSource) {
                this.sourceList = constants.SftOsSourceCounter;
            }*/
            if (constants.SftOsSource) {
                this.sourceList = {
                    0: "LA",
                    1: "EBS"
                };
                // this.sourceList = constants.SftOsSource;
            }
            //票据状态
            if(constants.SftRecvCounterBillStatus){
                this.billStatusList = constants.SftRecvCounterBillStatus;
            }
            //资金用途
            if(constants.SftRecvPersonalCounterUseFunds){
                this.useFundList = constants.SftRecvPersonalCounterUseFunds;
            }
            //收款方式
            if(constants.Sft_RecvPersonalCounter_Recvmode){
                this.recvmodeList = constants.Sft_RecvPersonalCounter_Recvmode;
            }
            //是否第三方
            if(constants.YesOrNo){
                this.YesOrNo = constants.YesOrNo;
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
            if(bankAllTypeList){
                this.bankAllTypeList = bankAllTypeList;
                this.bankTypeList = this.bankAllTypeList.slice(0, 200);
            }

            //从待匹配页面进入反写数据
            let matchData = this.$route.params.matchData;
            if(matchData){
                this.setMatchData(matchData);
            }
        },
        props: ["tableData"],
        components: {
            Upload: Upload
        },
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "recvcounter_list",
                    params: {
                        page_size: 20,
                        page_num: 1
                    }
                },
                searchData: { //搜索条件
                    recv_org_id: "",
                    source_sys: "",
                    insure_bill_no: "",
                    recv_mode: "",
                    recv_bank_name: "",
                    bill_status: "",
                    terminal_no: "",
                    use_funds: "",
                    third_payment: "",
                    min: "",
                    max: "",
                    pay_status: []
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
                statusList: {
                    0: "撤销",
                    1: "确认",
                },
                orgList: [],
                billStatusList: {},
                useFundList: {},
                recvmodeList: {},
                YesOrNo: {},
                currencyList: [],
                recvBankList: [],
                dialogVisible: false, //弹框
                dialogTitle: "新增",
                dialogData: {
                    recv_date: "",
                    batch_process_no: "",
                    currency: "",
                    recv_mode: "",
                    use_funds: "",
                    bill_status: "",
                    bill_number: "",
                    bill_date: "",
                    recv_bank_name: "",
                    recv_acc_no: "",
                    consumer_bank_name: "",
                    consumer_acc_no: "",
                    terminal_no: "",
                    amount: "",
                    payer_relation_insured: "",
                    pay_reason: "",
                },
                items: [
                    {
                        insure_bill_no: "",
                        amount: "",
                        bill_org_name: "",
                        bill_org_id: "",
                        source_sys: "",
                        insure_name: "",
                        insure_cer_no: "",
                        isnot_electric_pay: "",
                        isnot_bank_transfer_premium: "",
                        third_payment: "",
                        payer: "",
                        bank_code: "",
                        payer_cer_no: "",
                        $id: 1
                    }
                ],
                formLabelWidth: "120px",
                fileMessage: { //附件
                    bill_id: "",
                    biz_type: 35
                },
                emptyFileList: [],
                eidttrigFile: false,
                isLook: false,
                fileList: [],
                currentData: "",
                outTime: "", //银行大类搜索控制
                bankLongding: false,
                bankAllList: [],
                bankTypeList: [],
                bankAllTypeList: [], //银行大类全部(不重复)
            }
        },
        methods: {
            //清空搜索条件
            clearData: function(){
                var searchData = this.searchData;
                for (var k in searchData) {
                    if(k == "pay_status"){
                        searchData[k] = [];
                    }else{
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
                    if(k == "min" || k == "max"){
                        params[k] = searchData[k] ? (typeof(searchData[k]) == "number" ? searchData[k] : "") : searchData[k];
                    }else{
                        params[k] = searchData[k];
                    }
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
            //展示格式转换-状态
            transitStatus: function (row, column, cellValue, index) {
                return this.statusList[cellValue];
            },
            //是否第三方缴费状态改变后设置缴费人信息
            setPayInfo: function(item){
                if(item.third_payment == "0"){
                    item.payer_cer_no = "";
                    item.payer = "";
                    item.payer_relation_insured="";
                    item.payer = "";
                }
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
            //设置银行账号
            setAccNo: function(val){
                let recvBankList = this.recvBankList;
                if(recvBankList.length > 0){
                    for(let i = 0; i < recvBankList.length; i++){
                        if(recvBankList[i].bankcode == val){
                            this.dialogData.recv_acc_no = recvBankList[i].acc_no;
                            break;
                        }
                    }
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
                        optype: "recvcounter_listexport",
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
                        this.bankTypeList = this.bankTypeList.filter((item,index,arr) => {
                            for(var i = index+1; i < arr.length; i++){
                                if(item.display_name == arr[i].display_name){
                                    return false;
                                }
                            }
                            return true;
                        });
                    } else {
                        this.bankTypeList = this.bankAllTypeList.slice(0,200);
                    }
                    this.bankLongding = false;
                }, 1200);
            },
            //银行大类展开时重置数据
            clearSearch: function (val) {
                if (this.bankTypeList != this.bankAllTypeList && val) {
                    this.bankTypeList = this.bankAllTypeList.slice(0,200);
                }
            },
            //新增
            addData: function () {
                this.dialogTitle = "新增";
                this.currentData = "";
                let dialogData = this.dialogData;
                for(let k in dialogData){
                    if(k == "recv_date"){
                        let curDate = new Date();
                        dialogData[k] = curDate.getFullYear() + "-" + (curDate.getMonth() + 1) + "-" + curDate.getDate();
                    }else if(k == "batch_process_no"){
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
                    }else if(k == "currency"){
                        dialogData[k] = 1;
                    }else{
                        dialogData[k] = "";
                    }
                }

                this.items = [
                    {
                        insure_bill_no: "",
                        amount: "",
                        bill_org_name: "",
                        bill_org_id: "",
                        source_sys: "",
                        insure_name: "",
                        insure_cer_no: "",
                        isnot_electric_pay: "",
                        isnot_bank_transfer_premium: "",
                        third_payment: "0",
                        payer: "",
                        bank_code: "",
                        payer_cer_no: "",
                        $id: 1
                    }
                ];

                this.isLook = false;
                this.fileMessage.bill_id = "";
                this.emptyFileList = [];

                this.dialogVisible = true;
            },
            //新增保单
            addAccount: function () {
                var item = {
                    insure_bill_no: "",
                    amount: "",
                    bill_org_name: "",
                    bill_org_id: "",
                    source_sys: "",
                    insure_name: "",
                    insure_cer_no: "",
                    isnot_electric_pay: "",
                    isnot_bank_transfer_premium: "",
                    third_payment: "0",
                    payer: "",
                    bank_code: "",
                    payer_cer_no: "",
                    $id: Date.now()
                };
                this.items.push(item);
            },
            //删除保单
            removeAccount: function (item) {
                var index = this.items.indexOf(item);
                if (index != -1) {
                    this.items.splice(index, 1);
                }
            },
            //根据保单号设置相关信息
            setBillInfo: function(item){
                if(item.insure_bill_no){
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "recvcounter_getPolicyInfo",
                            params: {
                                insure_bill_no: item.insure_bill_no
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
                            for(let k in item){
                                if(data[k]){
                                    item[k] = data[k];
                                }
                            }
                        }
                    }).catch(function (error) {
                        console.log(error);
                    });
                }
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
            //保存新增数据
            saveData: function(){
                this.$confirm('是否确认完成当前业务收款?', '提示', {
                    confirmButtonText: '确认',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    let dialogData = this.dialogData;
                    let currentData = this.currentData;
                    let items = this.items;

                    let params = {
                        files: this.fileList,
                        policy_infos: items,
                        wait_match_flag: currentData.wait_match_flag ? currentData.wait_match_flag : 0,
                        wait_match_id: currentData.wait_match_id
                    };

                    if(this.dialogTitle == "查看"){
                        params.id = this.currentData.id;
                    }else{
                        for(let k in dialogData){
                            params[k] = dialogData[k];
                        }
                    }

                    let optype = this.dialogTitle == "查看" ? "recvcounter_detailConfirm" : "recvcounter_add";

                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: optype,
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
                }).catch(() => {
                });
            },
            //查看
            lookCurrent: function(row){
                this.dialogTitle = "查看";
                this.currentData = row;

                let dialogData = this.dialogData;
                for(let k in dialogData){
                    dialogData[k] = "";
                }
                this.items = [
                    {
                        insure_bill_no: "",
                        amount: "",
                        bill_org_name: "",
                        bill_org_id: "",
                        source_sys: "",
                        insure_name: "",
                        insure_cer_no: "",
                        isnot_electric_pay: "",
                        isnot_bank_transfer_premium: "",
                        third_payment: "0",
                        payer: "",
                        bank_code: "",
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
                        for(let k in data){
                            if(k == "policy_infos"){
                                let infoList = data[k];
                                infoList.forEach((item) => {
                                    item.third_payment += "";
                                    item.$id = new Date();
                                });
                                this.items = infoList;
                            } else if(k == "currency" || k == "recv_mode" || k == "bill_status"){
                                dialogData[k] = data[k] + "";
                            } else if (k == "consumer_bank_name"){
                                let bankTypeList = this.bankTypeList;
                                let flag = false;
                                for(let i = 0; i < bankTypeList.length; i++){
                                    if(bankTypeList.code == data.code){
                                        flag = true;
                                        continue;
                                    }
                                }
                                if(flag){
                                    this.bankTypeList = this.bankAllTypeList.slice(0, 200);
                                }else{
                                    this.bankTypeList.push({
                                        code: data.code,
                                        name: data.name,
                                        display_name: data.display_name,
                                    })
                                }
                                dialogData[k] = data[k];
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
                this.eidttrigFile = !this.eidttrigFile;
            },
            //撤销
            revocationData: function(row){
                this.$confirm('是否确认撤销当前业务收款?', '提示', {
                    confirmButtonText: '确认',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "recvcounter_revoke",
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
                            this.dialogVisible = false;
                            this.$message({
                                type: "success",
                                message: "撤销成功",
                                duration: 2000
                            });
                            this.$emit("getCommTable", this.routerMessage);
                        }
                    }).catch(function (error) {
                        console.log(error);
                    });
                }).catch(() => {
                });
            },
            //反写待匹配数据
            setMatchData: function(matchData){
                this.dialogTitle = "新增";
                this.currentData = matchData;

                let dialogData = this.dialogData;
                this.dialogVisible = true;

                for(let k in matchData){
                    if(k == "currency" || k == "recv_mode" || k == "bill_status" || k == "consumer_bank_name"){
                        dialogData[k] = matchData[k] + "";
                    }else{
                        dialogData[k] = matchData[k];
                    }
                }
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
                this.tableList = val.data;
                this.pagCurrent = val.page_num;
            }
        }
    }
</script>
