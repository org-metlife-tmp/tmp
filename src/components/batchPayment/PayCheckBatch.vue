<style scoped lang="less" type="text/less">
    #payCheckBatch {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*顶部按钮*/
        .button-list-right {
            position: absolute;
            top: -60px;
            right: -18px;
        }
        .button-list-left {
            position: absolute;
            top: -50px;
            left: -20px;

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

        /*搜索区*/
        .search-setion {
            text-align: left;

            /*时间控件*/
            .el-date-editor {
                width: 100%;
                max-width: 210px;
            }
        }

        /*分隔栏*/
        .split-bar {
            width: 106%;
            height: 6px;
            margin-left: -20px;
            background-color: #E7E7E7;
            margin-bottom: 20px;
        }

        /*数据展示区*/
        .table-content {
            height: 60%;
        }

        /*汇总数据*/
        .allData {
            height: 36px;
            line-height: 36px;
            width: 100%;
            background-color: #F8F8F8;
            border: 1px solid #ebeef5;
            border-top: none;
            box-sizing: border-box;
            text-align: right;

            /*左侧按钮*/
            .btn-left {
                float: left;
                margin-left: 16px;
            }

            /*汇总数字*/
            .numText {
                color: #FF5800;
                margin-right: 10px;
            }
        }

        .transmit-icon {
            position: relative;
            display: inline-block;
            width: 16px;
            height: 10px;
            vertical-align: middle;
            margin-right: 4px;

            i {
                position: absolute;
                top: -5px;
                left: -3px;
                width: 18px;
                height: 18px;
                background: url(../../assets/icon_common.png) no-repeat;
                background-position: -49px -80px;
            }
        }

        /*撤回按钮*/
        .withdraw {
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            background-position: -48px 0;
            border: none;
            padding: 0;
            vertical-align: middle;
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

    }
</style>

<template>
    <div id="payCheckBatch">
        <!-- 顶部按钮-->
        <div class="button-list-left">
            <!--<el-select v-model="searchData.source_sys"
                       filterable size="mini">
                <el-option v-for="(item,key) in sourceList"
                           :key="key"
                           :label="item"
                           :value="key">
                </el-option>
            </el-select>-->
            <ul>
                <li :class="{'active': searchData.source_sys == '0'}" @click="switchTab('0')">LA</li>
                <li :class="{'active': searchData.source_sys == '1'}" @click="switchTab('1')">EBS</li>
            </ul>
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
                            <el-select v-model="searchData.channel_id" placeholder="请选择通道编码"
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
                            <el-select v-model="searchData.channel_id" placeholder="请选择通道描述"
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
                            <el-input v-model="searchData.recv_acc_no" clearable placeholder="请输入客户账号"></el-input>
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
                            <el-input v-model="searchData.bank_key" clearable placeholder="请输入bankkey"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="11">
                        <el-form-item style="margin-bottom:0px">
                            <el-checkbox-group v-model="searchData.status">
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
                    <el-col :span="2">
                        <el-form-item>
                            <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList" ref="contentTable"
                      height="100%" border size="mini"
                      @select="setId"
                      @select-all="allChange">
                <el-table-column type="selection" width="40" :selectable="isSelect"></el-table-column>
                <el-table-column prop="push_date" label="日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="channel_code" label="通道编码" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="channel_desc" label="通道描述" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_key" label="bankkey" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bankkey_desc" label="bankkey描述" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="name" label="机构名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="preinsure_bill_no" label="投保单号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="insure_bill_no" label="保单号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="amount" label="金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="recv_acc_name" label="客户姓名" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_cert_code" label="证件号码" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_acc_no" label="客户账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_code" label="支付号码" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="status" label="状态" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="op_user_name" label="操作人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="op_date" label="操作日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="50"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <!--<el-tooltip content="撤回" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-show="scope.row.status == '未提交'">
                            <el-button size="mini" class="withdraw"
                                       @click="withdrawBill(scope.row)"></el-button>
                        </el-tooltip>-->
                        <el-tooltip content="拒绝" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-show="scope.row.status == '未提交'">
                            <el-button type="danger" icon="el-icon-close" size="mini"
                                       @click="withdrawBill(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
            <div class="allData">
                <div class="btn-left">
                    <el-button type="warning" size="mini" @click="affirm"
                               :disabled="mayAffirm">
                        <span class="transmit-icon"><i></i></span>确认
                    </el-button>
                </div>
                <span>总笔数：</span>
                <span v-text="totalData.total_num" class="numText"></span>
                <span>总金额：</span>
                <span v-text="totalData.total_amount" class="numText"></span>
            </div>
        </section>
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
        </div>
        <!--拒绝弹框-->
        <el-dialog title="拒绝理由"
                   top="200px" width="310px"
                   :visible.sync="dialogVisible"
                   :close-on-click-modal="false">
            <el-select v-model="rejectMessage" placeholder="请选择拒绝理由" clearable size="small">
                <el-option label="TMP:变更支付方式" value="TMP:变更支付方式"></el-option>
                <el-option label="TMP:拒绝支付" value="TMP:拒绝支付"></el-option>
            </el-select>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" plain @click="dialogVisible = false" size="mini">取 消</el-button>
                <el-button type="warning" @click="submitReject" size="mini">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "PayCheckBatch",
        created: function () {
            this.$emit("transmitTitle", "核对组批");

            /*获取常量数据*/
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            //组批状态
            if (constants.SftLegalData) {
                this.statusList = constants.SftLegalData;
            }
            //来源系统
            if (constants.SftOsSource) {
                this.sourceList = constants.SftOsSource;
            }
            //机构列表
            this.getOrgList();
            //通道编码
            this.getChannelList();
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "checkbatch_list",
                    params: {
                        page_size: 20,
                        page_num: 1,
                        source_sys: "0",
                    }
                },
                searchData: { //搜索条件
                    source_sys: "0",
                    channel_id: "",
                    channel_desc: "",
                    preinsure_bill_no: "",
                    insure_bill_no: "",
                    org_id: "",
                    recv_acc_no: "",
                    bank_key: "",
                    status: ["0"],
                    visit_time: ""
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
                orgList: [], //常量数据
                channelList: [],
                statusList: {},
                sourceList: {},
                totalData: { //汇总数据
                    total_amount: "",
                    total_num: ""
                },
                currentData: "",
                selectId: [], //选中数据
                resetAllData: true,
                dialogVisible:false, //拒绝弹框
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
                this.selectId = [];
                this.resetAllData = true;
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
                this.resetAllData = false;
                this.routerMessage.params.page_num = currPage;
                this.$emit("getCommTable", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.resetAllData = false;
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
                this.tableList = [];
                this.selectId = [];
            },
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
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
            //获取通道编码
            getChannelList: function () {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "sftchannel_getallchannel",
                        params: {
                            pay_attr: 1
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
            //设置未保存的id
            setId: function (selection, row) {
                let selectId = this.selectId;
                let totalData = this.totalData;
                for (let i = 0; i < selection.length; i++) {
                    if (row.pay_id == selection[i].pay_id) {
                        for (let j = 0; j < selectId.length; j++) {
                            if (selectId[j] === row.pay_id) {
                                selectId.splice(j, 1);
                                totalData.total_num++;
                                totalData.total_amount = this.$common.transitSeparator(this.$common.transitNumber(totalData.total_amount) + row.amount);
                                break;
                            }
                        }
                        return;
                    }
                }
                totalData.total_num--;
                totalData.total_amount = this.$common.transitSeparator(this.$common.transitNumber(totalData.total_amount) - row.amount);
                selectId.push(row.pay_id);
            },
            //点击全选时设置取消勾选的id
            allChange: function (selection, val) {
                let tableList = this.tableList;
                let selectId = this.selectId;
                let totalData = this.totalData;

                if (selection.length === 0) {
                    tableList.forEach((item) => {
                        let flag = true;
                        if (item.status == "已提交" || item.status == "已拒绝") {
                            flag = false;
                        } else {
                            for (let i = 0; i < selectId.length; i++) {
                                let idItem = selectId[i];
                                if (item.pay_id === idItem) {
                                    flag = false;
                                    break;
                                }
                            }
                        }

                        if (flag) {
                            totalData.total_num--;
                            totalData.total_amount = this.$common.transitSeparator(this.$common.transitNumber(totalData.total_amount) - item.amount);
                            selectId.push(item.pay_id);
                        }
                        ;
                    });
                } else {
                    tableList.forEach((item) => {
                        for (let i = 0; i < selectId.length; i++) {
                            let idItem = selectId[i];
                            if (item.pay_id === idItem) {
                                totalData.total_num++;
                                totalData.total_amount = this.$common.transitSeparator(this.$common.transitNumber(totalData.total_amount) + item.amount);
                                selectId.splice(i, 1);
                                break;
                            }
                        }
                    });
                }
            },
            //当前列是否可以勾选
            isSelect: function (row, index) {
                return !(row.status == "已提交" || row.status == "已拒绝");
            },
            //确认
            affirm: function () {
                if (!this.routerMessage.params.channel_id) {
                    this.$message({
                        type: "warning",
                        message: "请选择通道编码",
                        duration: 2000
                    });
                    return;
                }
                let params = {
                    remove_ids: this.selectId
                };
                let searchData = this.routerMessage.params;
                for (let k in searchData) {
                    params[k] = searchData[k];
                }

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "checkbatch_confirm",
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
                            message: "确认成功",
                            duration: 2000
                        });
                        this.$emit("getCommTable", this.routerMessage);
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //拒绝
            withdrawBill: function (row) {
                this.dialogVisible = true;
                this.rejectMessage = "";
                this.currentData = row;
            },
            //确定拒绝
            submitReject: function(){
                let currentData = this.currentData;
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "checkbatch_revokeToLaOrEbs",
                        params: {
                            id: currentData.pay_id,
                            persist_version: currentData.persist_version,
                            source_sys: this.routerMessage.params.source_sys,
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
                        this.dialogVisible = false;
                        this.$emit("getCommTable", this.routerMessage);
                    }
                }).catch(function (error) {
                    console.log(error);
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
            //设置当前页勾选情况
            setSelect: function () {
                if (this.tableList.length > 0) {
                    let selectId = this.selectId;
                    let tableList = this.tableList;
                    tableList.forEach((row) => {
                        let flag = true;
                        if (row.status == "已提交" || row.status == "已拒绝") {
                            flag = false;
                        } else {
                            for (let i = 0; i < selectId.length; i++) {
                                if (row.pay_id == selectId[i]) {
                                    flag = false;
                                    break;
                                }
                            }
                        }
                        this.$refs.contentTable.toggleRowSelection(row, flag);
                    })
                }
            }
        },
        computed: {
            mayAffirm: function () {
                return this.tableList.length === 0;
            }
        },
        watch: {
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.tableList = val.data;
                this.pagCurrent = val.page_num;
                this.searchData.visit_time = val.data.length > 0 ? val.data[0].visit_time : "";
                if (this.resetAllData) {
                    this.totalData.total_num = val.total_num;
                    this.totalData.total_amount = this.$common.transitSeparator(val.total_amount);
                }

                setTimeout(() => {
                    this.setSelect();
                }, 100);
            }
        }
    }
</script>





