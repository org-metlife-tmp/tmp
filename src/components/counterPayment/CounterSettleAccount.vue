<style scoped lang="less" type="text/less">
    #counterSettleAccount {
        /*分页部分*/
        .botton-pag {
            width: 100%;
            height: 8%;
            margin-top: 6px;

            .el-button {
                float: right;
                margin-top: -30px;
            }
        }

        /*分割线*/
        .split-bar{
            width: 100%;
            margin: 0 0 10px 0;
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
    <el-container id="counterSettleAccount">
        <el-header>

        </el-header>
        <el-main>
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
                                <el-select v-model="searchData.source_sys"
                                           clearable filterable size="mini"
                                           placeholder="请选择来源系统">
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
                                <el-input v-model="searchData.preinsure_bill_no" clearable placeholder="请输入投保单号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.insure_bill_no" clearable placeholder="请输入保单号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.recv_acc_name" clearable placeholder="请输入客户名称"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="clearData" size="mini">清空筛选</el-button>
                            </el-form-item>
                        </el-col>

                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.recv_account_no" clearable placeholder="请输入收款银行账号"></el-input>
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
                        <el-col :span="5">
                            <el-form-item style="margin-bottom:0px">
                                <el-checkbox-group v-model="searchData.is_checked">
                                    <el-checkbox :label="0" name="未核对">未核对</el-checkbox>
                                    <el-checkbox :label="1" name="已核对">已核对</el-checkbox>
                                </el-checkbox-group>
                            </el-form-item>
                        </el-col>
                        <el-col :span="7">
                            <el-form-item style="margin-bottom:0px">
                                <el-checkbox v-model="selfMotion">自动匹配</el-checkbox>
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
            <el-table :data="tableList"
                      @current-change="selectChange"
                      highlight-current-row
                      height="200px" border size="mini">
                <el-table-column prop="check_date" label="操作日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="org_name" label="机构名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_mode" label="支付方式" :show-overflow-tooltip="true"
                                 :formatter="transitMode"></el-table-column>
                <el-table-column prop="biz_code" label="业务号码" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="preinsure_bill_no" label="投保单号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="insure_bill_no" label="保单号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="biz_type" label="业务类型" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="amount" label="金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="recv_acc_name" label="客户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_cert_code" label="客户号码" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_name" label="收款账号户名" width="120px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_no" label="收款银行账号" width="120px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_bank_name" label="开户行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_account_no" label="付款账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_bank_name" label="付款银行":show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="is_checked" label="状态" :show-overflow-tooltip="true"
                                 :formatter="transitStatus"></el-table-column>
                <el-table-column prop="check_service_number" label="对账流水号" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="check_user_name" label="操作人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="check_date" label="操作日期" :show-overflow-tooltip="true"></el-table-column>
            </el-table>
            <!--分页部分-->
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
                <el-button type="warning" size="mini" @click="affirm">确认对账</el-button>
            </div>
            <!--分隔栏-->
            <div class="split-bar"></div>
            <!--搜索区-->
            <div class="search-setion">
                <el-form :inline="true" :model="childSearch" size="mini">
                    <el-row>
                        <el-col :span="5">
                            <el-form-item>
                                <el-date-picker
                                        v-model="ChildDateValue"
                                        type="daterange"
                                        range-separator="至"
                                        start-placeholder="交易日期起"
                                        end-placeholder="交易日期止"
                                        value-format="yyyy-MM-dd"
                                        size="mini" clearable
                                        unlink-panels
                                        :picker-options="pickerOptions">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                        <el-col :span="5">
                            <el-form-item>
                                <el-col :span="11">
                                    <el-input v-model="childSearch.min" clearable placeholder="最小金额"></el-input>
                                </el-col>
                                <el-col class="line" :span="1" style="text-align:center">-</el-col>
                                <el-col :span="11">
                                    <el-input v-model="childSearch.max" clearable placeholder="最大金额"></el-input>
                                </el-col>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="childSearch.summary" clearable placeholder="请输入摘要"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="6">
                            <el-form-item style="margin-bottom:0px">
                                <el-checkbox-group v-model="childSearch.is_checked">
                                    <el-checkbox :label="0" name="未核对">未核对</el-checkbox>
                                    <el-checkbox :label="1" name="已核对">已核对</el-checkbox>
                                </el-checkbox-group>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="clearChildData" size="mini">清空筛选</el-button>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain size="mini"
                                           :disabled="selfMotion"
                                           @click="queryChildData">搜索</el-button>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </div>
            <!--主数据关联数据-->
            <el-table :data="childList" border
                      ref="tableRef"
                      @selection-change="childChange"
                      @select-all="childChange"
                      height="200px" size="mini">
                <el-table-column type="selection" width="40"></el-table-column>
                <el-table-column prop="trans_date" label="交易日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bankcode" label="BankCode" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_no" label="银行账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_name" label="开户行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="direction" label="收付方向" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_no" label="对方银行账号" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_name" label="对方账户名称" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_bank" label="对方开户行" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="amount" label="交易金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="summary" label="摘要" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="is_checked" label="状态" :show-overflow-tooltip="true"
                                 :formatter="transitStatus"></el-table-column>
                <el-table-column prop="check_service_number" label="对账流水号" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="check_user_name" label="操作人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="check_date" label="操作日期" :show-overflow-tooltip="true"></el-table-column>
            </el-table>
            <div class="allData">
                <div class="btn-right">
                    <span>交易金额(收)：</span>
                    <span v-text="childTotalData.recvAmount" class="numText"></span>
                    <span>交易金额(付)：</span>
                    <span v-text="childTotalData.payAmount" class="numText"></span>
                    <span>合计金额：</span>
                    <span v-show="!isZero && !isPay">收</span>
                    <span v-show="!isZero && isPay">付</span>
                    <span v-show="isZero">-</span>
                    <span v-text="childTotalData.totalAmount" class="numText"></span>
                </div>
            </div>
        </el-main>
        <el-footer>

        </el-footer>
    </el-container>
</template>

<script>
    export default {
        name: "CounterSettleAccount",
        created: function () {
            this.$emit("transmitTitle", "结算对账");
            this.$emit("getCommTable", this.routerMessage);

            /*获取常量数据*/
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            //来源系统
            if (constants.SftOsSource) {
                this.sourceList = constants.SftOsSource;
            }
            //支付方式
            if(constants.SftDoubtPayMode){
                this.paymodeList = constants.SftDoubtPayMode;
            }
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "sftpaycountercheck_batchlist",
                    params: {
                        page_size: 20,
                        page_num: 1,
                        // source_sys: "0"
                    }
                },
                searchData: { //搜索条件
                    source_sys: "",
                    preinsure_bill_no: "",
                    insure_bill_no: "",
                    recv_acc_name: "",
                    recv_account_no: "",
                    min: "",
                    max: "",
                    is_checked: []
                },
                childSearch: {
                    min: "",
                    max: "",
                    summary: "",
                    is_checked: [],
                },
                dateValue: "", //时间控件
                ChildDateValue: "",
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
                tableList: [], //列表数据
                childList: [],
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                selfMotion: true, //自动匹配
                sourceList: {}, //常量数据
                paymodeList: {},
                totalData: { //汇总数据
                    total_amount: ""
                },
                childTotalData: {
                    payAmount: "",
                    recvAmount: "",
                    totalAmount: ""
                },
                isPay: false,
                isZero: true,
                tradingList: [], //选中数据
                currentId: "",

                channelList: [],
                bankcodeList: [],
                bankList: [],
                batchidList: [], //选中数据
                versionList: [],
            }
        },
        methods: {
            //清空搜索条件
            clearData: function(){
                var searchData = this.searchData;
                for (var k in searchData) {
                    if(k == "is_checked"){
                        searchData[k] = [];
                    }else{
                        searchData[k] = "";
                    }
                }
                this.dateValue = "";
            },
            clearChildData: function(){
                var searchData = this.childSearch;
                for (var k in searchData) {
                    if(k == "is_checked"){
                        searchData[k] = [];
                    }else{
                        searchData[k] = "";
                    }
                }
                this.ChildDateValue = "";
            },
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
                this.childList = [];
            },
            //查询交易流水
            queryChildData: function(){
                var searchData = this.childSearch;
                var params = {};
                for (var k in searchData) {
                    params[k] = searchData[k];
                }
                var val = this.ChildDateValue;
                params.start_date = val ? val[0] : "";
                params.end_date = val ? val[1] : "";


                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "sftpaycountercheck_tradingListNoAuto",
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
                        this.childList = data;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
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
            //列表选择框改变后
            selectChange: function (row,oldRow) {
                this.totalData.total_amount = row.amount;
                this.currentId = row.id;

                //自动匹配时触发自动搜索
                if(this.selfMotion){
                    this.childList = [];
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "sftpaycountercheck_tradingListAuto",
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
                            })
                        } else {
                            var data = result.data.data;
                            this.childList = data;
                            if(data.length === 1){
                                setTimeout(() => {
                                    this.childSelect(this.childList[0]);
                                }, 100);
                            }
                        }
                    }).catch(function (error) {
                        console.log(error);
                    });
                }
            },
            childChange: function (val) {
                this.tradingList = [];
                var payAmount = 0;
                var recvAmount = 0;
                for (var i = 0; i < val.length; i++) {
                    var item = val[i];
                    if(item.direction == "收"){
                        recvAmount += item.amount;
                    }else{
                        payAmount += item.amount;
                    }
                    this.tradingList.push(item.id);
                }

                this.childTotalData.payAmount = this.$common.transitSeparator(payAmount);
                this.childTotalData.recvAmount = this.$common.transitSeparator(recvAmount);
                if(payAmount > recvAmount){
                    this.isPay = true;
                    this.isZero = false;
                    this.childTotalData.totalAmount = this.$common.transitSeparator(payAmount - recvAmount);
                }else if(payAmount == recvAmount){
                    this.isPay = false;
                    this.isZero = true;
                    this.childTotalData.totalAmount = "";
                }else{
                    this.isPay = false;
                    this.isZero = false;
                    this.childTotalData.totalAmount = this.$common.transitSeparator(recvAmount - payAmount);
                }
            },
            //交易流水自动勾选
            childSelect: function(selectItem) {
                this.$refs.tableRef.toggleRowSelection(selectItem,true);
            },
            //对账确认
            affirm: function () {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "sftpaycountercheck_confirm",
                        params: {
                            id: this.currentId,
                            trading_no: this.tradingList
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
                        this.$message({
                            type: "success",
                            message: "确认成功",
                            duration: 2000
                        });
                        this.$emit("getCommTable", this.routerMessage);
                        this.childList = [];
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //展示格式转换-状态
            transitStatus: function (row, column, cellValue, index) {
                return cellValue == 0 ? "未核对" : "已核对";
            },
            //展示格式转换-支付方式
            transitMode: function (row, column, cellValue, index) {
                return this.paymodeList[cellValue];
            },


            //获取通道编码
            getChannelList: function () {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "sftchannel_getallchannel",
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
                        this.channelList = data;
                    }

                }).catch(function (error) {
                    console.log(error);
                });
            },
            //获取bankcode
            getBankcode: function () {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "sftchannel_getallbankcode",
                        params: {
                            channel_id: this.searchData.channel_id_one
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
                        this.bankcodeList = data;
                    }

                }).catch(function (error) {
                    console.log(error);
                });
            },
            //获取银行账号
            getBankList: function(){
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "sftpaycheck_getallaccountno",
                        params: {

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
                        this.bankList = data;
                    }

                }).catch(function (error) {
                    console.log(error);
                });
            },
            //选择通道编码后设置bankcode
            setBankcode: function(val){
                var channelList = this.channelList;
                var bankcodeList = this.bankcodeList;
                this.childSearch.bankcode = "";
                for(var i = 0; i < channelList.length; i++){
                    var item = channelList[i];
                    if(item.channel_id == val){
                        var curBankcode = item.bankcode;
                        for(var j = 0; j < bankcodeList.length; j++){
                            if(bankcodeList[j].bankcode == curBankcode){
                                this.childSearch.bankcode = bankcodeList[j].bankcode;
                                break;
                            }
                        }
                        break;
                    }
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












