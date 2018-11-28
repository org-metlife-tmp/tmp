<style scoped lang="less" type="text/less">
    #balanceAdjust {
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

        /*搜索区*/
        .search-setion {
            text-align: left;

            .line {
                text-align: center;
            }

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
            margin-bottom: 20px;
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

        /*数据列表*/
        .table-content {
            height: 340px;
        }

        /*弹框余额表*/
        .build-table {
            width: 100%;
            padding: 0 10px;
            box-sizing: border-box;

            ul {
                width: 50%;
                float: left;
                box-sizing: border-box;
                border-top: 1px solid #E7E7E7;
                border-right: 1px solid #E7E7E7;

                li {
                    float: left;
                    box-sizing: border-box;
                    border: 1px solid #E7E7E7;
                    height: 30px;
                    border-right: none;
                    border-top: none;
                    line-height: 30px;
                    text-indent: 8px;
                }

                li:nth-child(odd) {
                    width: 70%;
                }

                li:nth-child(even) {
                    width: 30%;
                }

                /*表格标题*/
                .table-title {
                    background-color: #E9F2F9;
                    height: 32px;
                    line-height: 32px;
                    text-align: center;
                    text-indent: 0px;
                }

                /*表格底部*/
                .table-bottom {
                    background-color: #F8F8F8;
                }
            }
        }
    }
</style>
<style lang="less" type="text/less">
    #balanceAdjust {
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 440px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="balanceAdjust">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addBill">新增</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="4">
                        <el-form-item>
                            <el-date-picker v-model="searchData.year"
                                            type="year" placeholder="请选择年份"
                                            format="yyyy 年" value-format="yyyy">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-date-picker v-model="searchData.month"
                                            type="month" placeholder="请选择月份"
                                            format="M 月" value-format="M">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" clearable
                                      placeholder="请输入账户名或账号"></el-input>
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
            <el-table :data="tableList"
                      height="100%"
                      border size="mini">
                <el-table-column prop="month" label="月份" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="org_name" label="所属机构" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="50"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookBill(scope.row)"></el-button>
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
        <!--新增/查看弹出框-->
        <el-dialog title=""
                   :visible.sync="dialogVisible"
                   width="860px" top="76px"
                   :close-on-click-modal="false">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="11">
                        <el-form-item label="账号">
                            <el-select v-model="dialogData.acc_id" placeholder="请选择账号"
                                       clearable :disabled="dialogTitle != '新增余额调节表'">
                                <el-option v-for="accItem in accList"
                                           :key="accItem.acc_id"
                                           :label="accItem.acc_no"
                                           :value="accItem.acc_id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="11">
                        <el-form-item label="日期">
                            <el-date-picker v-model="dialogData.cdate"
                                            style="width:100%"
                                            type="month" placeholder="请选择日期"
                                            value-format="yyyy-MM"
                                            :disabled="dialogTitle != '新增余额调节表'">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2" style="text-align:right">
                        <el-button type="primary" plain @click="getBuild" size="small">查询</el-button>
                    </el-col>
                </el-row>
            </el-form>
            <div class="build-table" v-show="hasBuild">
                <ul>
                    <li class="table-title">项目</li>
                    <li class="table-title">金额 (元)</li>
                    <li>企业银行存款日记账余额</li>
                    <li v-text="dialogTable.voucher_bal"></li>
                    <li>加：银行已收,企业未收款</li>
                    <li v-text="dialogTable.ysAll"></li>
                    <template v-for="(item,index) in ysList">
                        <li>{{ item.$empty ? "" : (index + 1 ) + "、" + item.memo }}</li>
                        <li>{{ item.amount }}</li>
                    </template>


                    <li>减：银行已付,企业未付款</li>
                    <li v-text="dialogTable.yfAll"></li>
                    <template v-for="(item,index) in yfList">
                        <li>{{ item.$empty ? "" : (index + 1 ) + "、" + item.memo }}</li>
                        <li>{{ item.amount }}</li>
                    </template>
                    <li class="table-bottom">调节后的存款余额</li>
                    <li class="table-bottom" v-text="dialogTable.voucher_adjust_bal"></li>
                </ul>
                <ul>
                    <li class="table-title">项目</li>
                    <li class="table-title">金额 (元)</li>
                    <li>银行对账单余额</li>
                    <li v-text="dialogTable.acc_bal"></li>
                    <li>加：企业已收,银行未收款</li>
                    <li v-text="dialogTable.qsAll"></li>
                    <template v-for="(item,index) in qsList">
                        <li>{{ item.$empty ? "" : (index + 1 ) + "、" + item.memo }}</li>
                        <li>{{ item.amount }}</li>
                    </template>

                    <li>减：企业已付,银行未付款</li>
                    <li v-text="dialogTable.qfAll"></li>
                    <template v-for="(item,index) in qfList">
                        <li>{{ item.$empty ? "" : (index + 1 ) + "、" + item.memo }}</li>
                        <li>{{ item.amount }}</li>
                    </template>
                    <li class="table-bottom">调节后的存款余额</li>
                    <li class="table-bottom" v-text="dialogTable.acc_adjust_bal"></li>
                </ul>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="createTable"
                           v-show="dialogTitle == '新增余额调节表'">生成余额调节表</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>

    export default {
        name: "BalanceAdjust",
        created: function () {
            this.$emit("transmitTitle", "余额调节表");
            this.$emit("getCommTable", this.routerMessage);

            //账号列表
            this.$axios({
                url: this.queryUrl + "normalProcess",
                method: "post",
                data: {
                    optype: "account_list",
                    params: {
                        service_status: [1]
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
                    this.accList = data;
                }
            }).catch(function (error) {
                console.log(error);
            });
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "dztadjust_list",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                searchData: { //搜索条件
                    year: "",
                    month: "",
                    query_key: ""
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                accList: [], //下拉框数据
                dialogVisible: false, //弹框数据
                dialogData: {
                    acc_id: "",
                    cdate: "",
                    id: "",
                    persist_version: "",
                },
                dialogTitle: "新增余额调节表",
                formLabelWidth: "100px",
                dialogTable: {  //余额调节表汇总金额数据
                    voucher_bal: "",
                    voucher_adjust_bal: "",
                    acc_bal: "",
                    acc_adjust_bal: "",
                    ysAll: "",
                    yfAll: "",
                    qsAll: "",
                    qfAll: "",
                },
                ysList: [], //各个分项金额总数据
                yfList: [],
                qsList: [],
                qfList: [],
                hasBuild: false, //余额调节表显示控制
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
            //新增
            addBill: function () {
                this.dialogTitle = "新增余额调节表"
                this.hasBuild = false;
                var dialogData = this.dialogData;
                for (var k in dialogData) {
                    dialogData[k] = "";
                }
                var dialogTable = this.dialogTable;
                for (var k in dialogTable) {
                    dialogTable[k] = "";
                }
                this.ysList = [];
                this.yfList = [];
                this.qsList = [];
                this.qfList = [];
                this.dialogVisible = true;
            },
            //获取调节表
            getBuild: function (val) {
                var dialogData = this.dialogData;
                if (dialogData.acc_id && dialogData.cdate) {
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "dztadjust_build",
                            params: dialogData
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
                            data.checkout_date = this.dialogData.cdate;
                            data.acc_id = this.dialogData.acc_id;
                            this.setTableData(data);
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }else{
                    this.$message({
                        type: "warning",
                        message: "请选择账号和日期",
                        duration: 2000
                    });
                }
            },
            //查看
            lookBill: function (row) {
                this.addBill(); //使用新增来清空数据

                this.dialogTitle = "查看余额调节表";
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "dztadjust_detail",
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
                        this.setTableData(data);
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //设置调节表数据
            setTableData: function (data) {
                this.hasBuild = true;
                //设置账号和日期
                var dialogData = this.dialogData;
                for (var k in dialogData) {
                    if (k == "cdate") {
                        dialogData.cdate = data.checkout_date;
                    } else {
                        dialogData[k] = data[k];
                    }
                }

                var allData = {
                    ysAll: 0,
                    yfAll: 0,
                    qsAll: 0,
                    qfAll: 0,
                }
                //转换金额格式
                data.ysqws.forEach((item) => {
                    allData.ysAll += item.amount;
                    item.amount = this.$common.transitSeparator(item.amount);
                });
                data.qsyws.forEach((item) => {
                    allData.qsAll += item.amount;
                    item.amount = this.$common.transitSeparator(item.amount);
                });
                data.yfqwf.forEach((item) => {
                    allData.yfAll += item.amount;
                    item.amount = this.$common.transitSeparator(item.amount);
                });
                data.qfywf.forEach((item) => {
                    allData.qfAll += item.amount;
                    item.amount = this.$common.transitSeparator(item.amount);
                });

                //设置余额表汇总数据
                var dialogTable = this.dialogTable;
                for (var k in dialogTable) {
                    if (data[k]) {
                        dialogTable[k] = this.$common.transitSeparator(data[k]);
                    }
                }
                for (var k in allData) {
                    dialogTable[k] = this.$common.transitSeparator(allData[k]);
                }

                //银行已收和企业已收数据设置
                var emptyLength = Math.abs(data.ysqws.length - data.qsyws.length);
                var pushData = "";
                if (data.ysqws.length > data.qsyws.length) {
                    pushData = data.qsyws;
                } else {
                    pushData = data.ysqws;
                }

                for (var i = 0; i < emptyLength; i++) {
                    pushData.push({
                        $empty: true
                    })
                }
                this.ysList = data.ysqws;
                this.qsList = data.qsyws;
                //银行已付和企业已付数据设置
                var emptyLength = Math.abs(data.yfqwf.length - data.qfywf.length);
                var pushData = "";
                if (data.yfqwf.length > data.qfywf.length) {
                    pushData = data.qfywf;
                } else {
                    pushData = data.yfqwf;
                }

                for (var i = 0; i < emptyLength; i++) {
                    pushData.push({
                        $empty: true
                    })
                }
                this.yfList = data.yfqwf;
                this.qfList = data.qfywf;
            },
            //生成余额调节表
            createTable: function () {
                var dialogData = this.dialogData;
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "dztadjust_confirm",
                        params: {
                            id: dialogData.id,
                            persist_version: dialogData.persist_version,
                            cdate: dialogData.cdate
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

                        if (this.tableList.length < this.routerMessage.params.page_size) {
                            this.tableList.push(data);
                        }
                        this.pagTotal++;

                        this.$message({
                            type: "success",
                            message: "已成功生成余额调节表",
                            duration: 2000
                        });
                        this.dialogVisible = false;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
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



