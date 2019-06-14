<style scoped lang="less" type="text/less">
    #POSQuery {

    }
</style>

<template>
    <el-container id="POSQuery">
        <el-header>
            <div class="button-list-right">
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
                                        start-placeholder="清算日期始"
                                        end-placeholder="清算日期止"
                                        value-format="yyyy-MM-dd"
                                        size="mini" clearable
                                        unlink-panels
                                        :picker-options="pickerOptions">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                        <el-col :span="5">
                            <el-form-item>
                                <el-date-picker
                                        v-model="tradeDateValue"
                                        type="daterange"
                                        range-separator="至"
                                        start-placeholder="交易日期始"
                                        end-placeholder="交易日期止"
                                        value-format="yyyy-MM-dd"
                                        size="mini" clearable
                                        unlink-panels
                                        :picker-options="pickerOptions">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.terminal_no" clearable placeholder="请输入终端号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.serial_number" clearable placeholder="请输入流水号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.card_no" clearable placeholder="请输入卡号"></el-input>
                            </el-form-item>
                        </el-col>

                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.card_issue_bank" clearable placeholder="请输入发卡行"></el-input>
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
                        <el-col :span="9">
                            <el-form-item style="margin-bottom:0px">
                                <el-checkbox-group v-model="searchData.bill_checked">
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
        </el-header>
        <el-main>
            <el-table :data="tableList"
                      border size="mini" height="100%">
                <el-table-column prop="liquidation_date" label="清算日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="trade_date" label="交易日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="trade_time" label="交易时间" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="terminal_no" label="终端号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="trade_amount" label="交易金额" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="procedures_amount" label="手续费" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="entry_account_amount" label="入账金额" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="system_track_number" label="系统跟踪号" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="retrieval_reference_number" label="检索参考号" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="serial_number" label="流水号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="trade_type" label="交易类型" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="card_no" label="卡号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="card_type" label="卡类型" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="card_issue_bank" label="发卡行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="no_identity_mark" label="非接标识" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bill_checked" label="状态" :show-overflow-tooltip="true"></el-table-column>
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
    import Upload from "../publicModule/Upload.vue";

    export default {
        name: "POSQuery",
        created: function () {
            this.$emit("transmitTitle", "POS机明细导入");
            // this.$emit("getCommTable", this.routerMessage);
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "recvcounterimportpos_list",
                    params: {
                        page_size: 20,
                        page_num: 1
                    }
                },
                searchData: { //搜索条件
                    terminal_no: "",
                    serial_number: "",
                    card_no: "",
                    card_issue_bank: "",
                    min: "",
                    max: "",
                    bill_checked: []
                },
                dateValue: "", //时间控件
                tradeDateValue: "",
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                statusList: {  //常量数据
                    0: "未核对",
                    1: "已核对",
                },
            }
        },
        methods: {
            //清空搜索条件
            clearData: function(){
                var searchData = this.searchData;
                for (var k in searchData) {
                    if(k == "bill_checked"){
                        searchData[k] = [];
                    }else{
                        searchData[k] = "";
                    }
                }
                this.dateValue = "";
                this.tradeDateValue = "";
            },
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                var params = this.routerMessage.params;
                for (var k in searchData) {
                    params[k] = searchData[k];
                }
                var val = this.dateValue;
                params.liquidation_start_date = val ? val[0] : "";
                params.liquidation_end_date = val ? val[1] : "";
                let tradeDateValue = this.tradeDateValue;
                params.trade_start_date = tradeDateValue ? tradeDateValue[0] : "";
                params.trade_end_date = tradeDateValue ? tradeDateValue[1] : "";

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
                        optype: "recvcounterimportpos_listexport",
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
