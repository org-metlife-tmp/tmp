<style scoped lang="less" type="text/less">
    #settleAccounts {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        .dropdown-content{
            width: 100%;
            height: 100%;
            overflow-y: scroll;
            overflow-x: hidden;
        }

        /*顶部按钮*/
        .button-list-right {
            position: absolute;
            top: -60px;
            right: -18px;
        }
        .button-list-left {
            position: absolute;
            top: -56px;
            left: -21px;
        }

        /*搜索区*/
        .search-setion {
            text-align: left;

            /*时间控件*/
            .el-date-editor {
                width: 100%;
            }
        }

        /*分隔栏*/
        .split-bar {
            width: 106%;
            height: 6px;
            margin-left: -20px;
            background-color: #E7E7E7;
            margin-bottom: 10px;
        }

        /*数据展示区*/
        .table-content {
            height: 181px;
        }

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
    <div id="settleAccounts">
        <!-- 顶部按钮-->
        <div class="button-list-left">
            <el-select v-model="searchData.source_sys"
                       filterable size="mini"
                       @change="queryData">
                <el-option v-for="(item,key) in sourceList"
                           :key="key"
                           :label="item"
                           :value="key">
                </el-option>
            </el-select>
        </div>
        <div class="dropdown-content">
            <!--搜索区-->
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
                                <el-select v-model="searchData.channel_id_two" placeholder="请选择通道描述"
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
                                <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </div>
            <!--数据展示区-->
            <section class="table-content">
                <el-table :data="tableList"
                          @selection-change="selectChange"
                          height="100%" border size="mini">
                    <el-table-column type="selection" width="38"></el-table-column>
                    <el-table-column prop="create_on" label="出盘日期" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="pay_date" label="主批次号" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="pay_code" label="子批次号" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="pay_mode" label="通道编码" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="bank_key" label="通道描述" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="bankkey_desc" label="总金额" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="biz_type" label="总笔数" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="org_name" label="成功金额" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="preinsure_bill_no" label="成功笔数" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="insure_bill_no" label="失败金额" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="amount" label="失败笔数" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="recv_acc_name" label="状态" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="recv_cert_code" label="对账流水号" width="100px"
                                     :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="op_user_name" label="操作人" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="op_date" label="操作日期" :show-overflow-tooltip="true"></el-table-column>
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
                                    <el-option v-for="channel in channelList"
                                               :key="channel.channel_id"
                                               :label="channel.channel_desc"
                                               :value="channel.channel_id">
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
                                <el-button type="primary" plain @click="queryChildData" size="mini">搜索</el-button>
                            </el-form-item>
                        </el-col>
                        <el-col :span="5">
                            <el-form-item style="margin-bottom:0px">
                                <el-checkbox-group v-model="childSearch.is_checked">
                                    <el-checkbox :label="0" name="未核对">未核对</el-checkbox>
                                    <el-checkbox :label="1" name="已核对">已核对</el-checkbox>
                                </el-checkbox-group>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </div>
            <!--主数据关联数据-->
            <section class="table-content">
                <el-table :data="childList" border
                          @selection-change="childChange"
                          height="100%" size="mini">
                    <el-table-column type="selection" width="38"></el-table-column>
                    <el-table-column prop="acc_no" label="交易日期" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="acc_name" label="BankCode" width="100px"
                                     :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="bank_name" label="银行账号" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="direction" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="opp_acc_no" label="开户行" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="opp_acc_name" label="收付方向" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="amount" label="对方银行账号" width="110px"
                                     :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="summary" label="对方账户名称" width="110px"
                                     :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="trans_date" label="对方开户行" width="100px"
                                     :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="trans_date" label="交易金额" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="trans_date" label="摘要" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="trans_date" label="状态" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="trans_date" label="对账流水号" width="100px"
                                     :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="trans_date" label="操作人" :show-overflow-tooltip="true"></el-table-column>
                </el-table>
            </section>
        </div>
    </div>
</template>

<script>
    export default {
        name: "SettleAccounts",
        created: function () {
            this.$emit("transmitTitle", "结算对账");
            this.$emit("getCommTable", this.routerMessage);

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
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "sftpaycheck_batchlist",
                    params: {
                        page_size: 7,
                        page_num: 1,
                        source_sys: "0"
                    }
                },
                searchData: { //搜索条件
                    source_sys: "0",
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
                batchidList: [], //选中数据
                versionList: [],
                tradingList: [],
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
                        optype: "sftpaycheck_tradingList",
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
                        this.bankcodeList = data;
                    }

                }).catch(function (error) {
                    console.log(error);
                });
            },
            //列表选择框改变后
            selectChange: function (val) {
                //计算汇总数据
                this.batchidList = [];
                this.versionList = [];
                for (var i = 0; i < val.length; i++) {
                    var item = val[i];
                    this.batchidList.push(item.id);
                    this.versionList.push(item.persist_version);
                }
            },
            childChange: function (val) {
                this.tradingList = [];
                for (var i = 0; i < val.length; i++) {
                    var item = val[i];
                    this.tradingList.push(item.id);
                }
            },
            //对账确认
            affirm: function () {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "sftpaycheck_confirm",
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











