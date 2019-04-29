<style scoped lang="less" type="text/less">
    #gatherSettleAccounts {
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
    <el-container id="gatherSettleAccounts">
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
                                <el-select v-model="searchData.channel_id_one" placeholder="请选择通道编码"
                                           clearable filterable
                                           @change="setBankcode"
                                           style="width:100%">
                                    <el-option v-for="channel in channelList"
                                               :key="channel.channel_id"
                                               :label="channel.channel_code"
                                               :value="channel.channel_id">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.channel_id_one" placeholder="请选择通道描述"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="channel in channelList"
                                               :key="channel.channel_id"
                                               :label="channel.channel_desc"
                                               :value="channel.channel_id">
                                    </el-option>
                                </el-select>
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
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="clearData" size="mini">清空筛选</el-button>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="queryData" size="mini"
                                           :disabled="!searchData.channel_id_one && !searchData.channel_id_two">搜索</el-button>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </div>
            <!--数据展示区-->
            <el-table :data="tableList" ref="tableRef"
                      @select="selectChange"
                      @select-all="selectChange"
                      height="200px" border size="mini">
                <el-table-column type="selection" width="40" :selectable="isSelect"></el-table-column>
                <el-table-column prop="send_on" label="出盘日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="master_batchno" label="主批次号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="child_batchno" label="子批次号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="channel_code" label="通道编码" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="channel_desc" label="通道描述" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="total_amount" label="总金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="total_num" label="总笔数" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="success_amount" label="成功金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="success_num" label="成功笔数" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="fail_amount" label="失败金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="fail_num" label="失败笔数" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="is_checked" label="状态" :show-overflow-tooltip="true"
                                 :formatter="transitStatus"></el-table-column>
                <el-table-column prop="check_service_number" label="对账流水号" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="check_user_name" label="操作人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="check_date" label="操作日期" :show-overflow-tooltip="true"></el-table-column>
            </el-table>
            <div class="allData">
                <div class="btn-right">
                    <span>总笔数：</span>
                    <span v-text="totalData.total_num" class="numText"></span>
                    <span>总金额：</span>
                    <span v-text="totalData.total_amount" class="numText"></span>
                    <span>成功笔数：</span>
                    <span v-text="totalData.success_num" class="numText"></span>
                    <span>成功金额：</span>
                    <span v-text="totalData.success_amount" class="numText"></span>
                    <span>失败笔数：</span>
                    <span v-text="totalData.fail_num" class="numText"></span>
                    <span>失败金额：</span>
                    <span v-text="totalData.fail_amount" class="numText"></span>
                </div>
            </div>
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
                                <el-select v-model="childSearch.bankcode" placeholder="请选择bankcode"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="channel in bankcodeList"
                                               :key="channel.bankcode"
                                               :label="channel.bankcode"
                                               :value="channel.bankcode">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="childSearch.acc_no" placeholder="请选择银行账号"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="bank in bankList"
                                               :key="bank.acc_no"
                                               :label="bank.acc_no"
                                               :value="bank.acc_no">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="childSearch.summary" clearable placeholder="请输入摘要"></el-input>
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
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="queryChildData" size="mini"
                                           :disabled="tableList.length == 0">搜索</el-button>
                            </el-form-item>
                        </el-col>
                        <el-col :span="21">
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
                    </el-row>
                </el-form>
            </div>
            <!--主数据关联数据-->
            <el-table :data="childList" border
                      @selection-change="childChange"
                      @select-all="childChange"
                      height="200px" size="mini">
                <el-table-column type="selection" width="40" :selectable="isSelect"></el-table-column>
                <el-table-column prop="trans_date" label="交易日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bankcode" label="BankCode" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_no" label="银行账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_nameacc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
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
        name: "GatherSettleAccounts",
        created: function () {
            this.$emit("transmitTitle", "结算对账");
            // this.$emit("getCommTable", this.routerMessage);

            /*获取常量数据*/
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            //来源系统
            if (constants.SftOsSource) {
                this.sourceList = constants.SftOsSource;
            }
            //通道编码
            this.getChannelList();
            //bankcode
            this.getBankcode();
            //银行账号
            this.getBankList();
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "sftrecvcheck_batchlist",
                    params: {
                        page_size: 20,
                        page_num: 1,
                        // source_sys: "0"
                    }
                },
                searchData: { //搜索条件
                    // source_sys: "0",
                    channel_id_one: "",
                    channel_id_two: "",
                    is_checked: []
                },
                childSearch: {
                    bankcode: "",
                    acc_no: "",
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
                sourceList: {}, //常量数据
                channelList: [],
                bankcodeList: [],
                bankList: [],
                batchidList: [], //选中数据
                versionList: [],
                tradingList: [],
                totalData: { //汇总数据
                    total_amount: "",
                    total_num: "",
                    success_amount: "",
                    success_num: "",
                    fail_amount: "",
                    fail_num: "",
                },
                childTotalData: {
                    payAmount: "",
                    recvAmount: "",
                    totalAmount: ""
                },
                isPay: false,
                isZero: true
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
                        optype: "sftrecvcheck_tradingList",
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
            //获取通道编码
            getChannelList: function () {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "sftchannel_getallchannel",
                        params: {
                            pay_attr: 0
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
            //当前列是否可以勾选
            isSelect: function(row, index){
                return !(row.is_checked == 1);
            },
            //列表选择框改变后
            selectChange: function (val,row) {
                //计算汇总数据
                var allAmount = 0;
                var allNum = 0;
                var sucAmount = 0;
                var sucNum = 0;
                var filAmount = 0;
                var filNum = 0;

                this.batchidList = [];
                this.versionList = [];
                for (var i = 0; i < val.length; i++) {
                    var item = val[i];
                    allAmount += item.total_amount;
                    allNum += item.total_num;
                    sucAmount += item.success_amount;
                    sucNum += item.success_num;
                    filAmount += item.fail_amount;
                    filNum += item.fail_num;
                    this.batchidList.push(item.id);
                    this.versionList.push(item.persist_version);
                }
                this.totalData.total_amount = this.$common.transitSeparator(allAmount);
                this.totalData.total_num = allNum;
                this.totalData.success_amount = this.$common.transitSeparator(sucAmount);
                this.totalData.success_num = sucNum;
                this.totalData.fail_amount = this.$common.transitSeparator(filAmount);
                this.totalData.fail_num = filNum;
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
            //对账确认
            affirm: function () {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "sftrecvcheck_confirm",
                        params: {
                            batchid: this.batchidList,
                            persist_version: this.versionList,
                            trading_no: this.tradingList,
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
                        this.queryChildData();
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //展示格式转换-结算模式
            transtMode: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                return constants.SftNetMode[cellValue];
            },
            //展示格式转换-状态
            transitStatus: function (row, column, cellValue, index) {
                return cellValue == 0 ? "未核对" : "已核对";
            },
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
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











