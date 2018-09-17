<style lang="less" type="text/less">
    #electronicReceipt{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion {
            text-align: left;
            .line {
                text-align: center;
            }
        }
        
        .table-content{
            height: 325px;
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

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -10px;
        }
    }
</style>

<template>
    <div id="electronicReceipt">
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.pay_query_key" clearable placeholder="请输入付款方名称或账号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.recv_query_key" clearable
                                      placeholder="请输入收款方名称或账号"></el-input>
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
                <el-table-column prop="batchno" label="本方账户" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="total_num" label="交易日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="total_amount" label="方向" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="success_num" label="对方户名" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="success_num" label="对方账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="success_amount" label="金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="service_status" label="摘要" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="110"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.service_status != 1 && scope.row.service_status != 5">
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
        <!--待处理编辑弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="800px" title="调拨单信息查看 "
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">调拨单信息查看</h1>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "ElectronicReceipt",
        created: function(){
            this.$emit("transmitTitle", "电子回单");
            this.$emit("getCommTable", this.routerMessage);
        },
        mounted:function(){
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: {
                    optype: "wftrans_list",
                    params: {
                        page_size: 8,
                        page_num: 1
                    }
                },
                tableList: [],//列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                dialogVisible: false,
                dialogData: {},
                searchData: { //搜索条件
                    pay_query_key: "",
                    recv_query_key: "",
                    min: "",
                    max: "",
                },
            }
        },
        methods: {
            //换页后获取数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getCommTable", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.params = {
                    page_size: val,
                    page_num: 1
                };
                this.$emit("getCommTable", this.routerMessage);
            },
            //根据条件查询
            queryData:function (){

            },
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //查看
            lookBill: function (row) {
                this.dialogVisible = true;
            }
        },
        watch: {
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data;
                this.userData = val.ext;
            }
        }
    }
</script>

