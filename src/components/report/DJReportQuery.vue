<style scoped lang="less" type="text/less">
    #aisleSet {
        .form-small-title {
            font-weight: bold;
            border-bottom: 1px solid #e2e2e2;
            padding-bottom: 4px;
            margin-bottom: 10px;

            span {
                display: inline-block;
                width: 4px;
                height: 16px;
                background-color: orange;
                margin-right: 6px;
                vertical-align: middle;
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
    <el-container id="aisleSet">
        <el-header>
            <div class="button-list-right">
                <el-button type="warning" size="mini" @click="exportFun">导出</el-button>
            </div>
            <!--搜索区-->
            <div class="search-setion">
                <el-form :inline="true" :model="searchData" size="mini">
                    <el-row>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.collect_status" placeholder="请选择状态"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="(colStatus,key) in colStatusList"
                                               :key="key"
                                               :label="colStatus"
                                               :value="key">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.pay_account_no" clearable placeholder="请输入被归集账号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.recv_account_no" clearable placeholder="请输入归集主账号"></el-input>
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
                        <el-col :span="6">
                            <el-form-item>
                                <el-col :span="11">
                                    <el-input v-model="searchData.min" clearable placeholder="最小金额"></el-input>
                                </el-col>
                                <el-col class="line" :span="2">-</el-col>
                                <el-col :span="11">
                                    <el-input v-model="searchData.max" clearable placeholder="最大金额"></el-input>
                                </el-col>
                            </el-form-item>
                        </el-col>
                        <el-col :span="20">
                            <el-form-item>
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
                      border size="mini" height="100%">
                <el-table-column prop="pay_account_no" label="被归集账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_account_org_name" label="被归集账户所属分公司" :show-overflow-tooltip="true" ></el-table-column>
                <el-table-column prop="recv_account_bank" label="归集账号开户行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_no" label="归集主账号" :show-overflow-tooltip="true" :formatter="transitSendOn"></el-table-column>
                <el-table-column prop="collect_amount" label="归集金额" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="collect_status" label="状态" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="execute_time" label="日期" :show-overflow-tooltip="true"></el-table-column>
            </el-table>
        </el-main>
        <el-footer>
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
        </el-footer>
    </el-container>
</template>

<script>
    export default {
        name: "GJTReportQuery",
        created: function () {
            this.$emit("transmitTitle", "归集通查询报表");
            this.$emit("getCommTable", this.routerMessage);
        },
        mounted:function(){
            //付款方式
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.CollOrPoolRunStatus) {
                this.colStatusList = constants.CollOrPoolRunStatus;
            }
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "gjtReport_GjtReportList",
                    params: {
                        page_size: 20,
                        page_num: 1
                    }
                },
                searchData: { //搜索条件
                    collect_status: "",
                    pay_account_no: "",
                    recv_account_no: "",
                    min: "",
                    max: "",
                },
                dateValue: "", //时间控件
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                payModeList: {}, //支付方式
                colStatusList: {}, //下拉框数据
            }
        },
        methods: {
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                for (var k in searchData) {
                    this.routerMessage.params[k] = searchData[k];
                }
                let dateValue = this.dateValue;
                let start_date =  dateValue ? dateValue[0] : "";
                this.routerMessage.params.start_date = start_date;
                let end_date=end_date = dateValue ? dateValue[1] : "";
                this.routerMessage.params.end_date = end_date.slice(0,4)+end_date.slice(5,7)+end_date.slice(8,10);
                this.routerMessage.params.page_num = 1;
                if (this.routerMessage.params.start_date==""||this.routerMessage.params.end_date=="") {
                    this.$message({
                        type: "error",
                        message: "请输入付款日期",
                        duration: 2000
                    });
                    return;
                }
                this.$emit("getCommTable", this.routerMessage);
            },
            //清空搜索条件
            clearData: function(){
                var searchData = this.searchData;
                for (var k in searchData) {
                    if(k == "is_checkout"){
                        searchData[k] = [];
                    }else{
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
            transitProStatus: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                return constants.OaProcessStatus[cellValue];
            },
            transitSendOn: function (row, column, cellValue, index) {
                return cellValue.slice(0,4)+"-"+cellValue.slice(4,6)+"-"+cellValue.slice(6,8);
            },
            //导出
            exportFun:function () {
                if(!this.tableList.length){
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
                        optype:"gjtReport_listexport",
                        params:params
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
            }
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


