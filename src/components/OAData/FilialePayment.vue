<style scoped lang="less" type="text/less">
    #filialePayment {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion {
            text-align: left;
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
    }
</style>

<template>
    <div id="filialePayment">
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.bill_no" clearable placeholder="请输入报销单申请号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.apply_user" clearable placeholder="请输入申请用户"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.recv_acc_query_key" clearable placeholder="请输入收款人账号或名称"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item>
                            <el-col :span="11">
                                <el-input v-model="searchData.min_amount" clearable placeholder="最小金额"></el-input>
                            </el-col>
                            <el-col class="line" :span="1" style="text-align:center">-</el-col>
                            <el-col :span="11">
                                <el-input v-model="searchData.max_amount" clearable placeholder="最大金额"></el-input>
                            </el-col>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item style="margin-bottom:0px">
                            <el-checkbox-group v-model="searchData.status">
                                <el-checkbox v-for="(name,k) in statusList"
                                             :label="k" name="type" :key="k">
                                    {{ name }}
                                </el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="10"></el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList" border
                      height="100%" size="mini"
                      highlight-current-row>
                <el-table-column prop="pay_account_no" label="付款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_account_bank" label="付款银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_no" label="收款方账号" :show-overflow-tooltip="true"></el-table-column>

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
    </div>
</template>

<script>
    export default {
        name: "FilialePayment",
        created: function () {
            //设置当前页基本信息
            this.$emit("transmitTitle", "分公司付款");
            this.$emit("tableText", {
                leftTab: "未处理",
                rightTab: "已处理"
            });
        },
        mounted: function(){
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
        },
        props: ["isPending", "tableData"],
        data: function () {
            return {
                routerMessage: {
                    todo: {
                        optype: "",
                        params: {
                            page_size: 7,
                            page_num: 1,
                            is_checked: 0
                        }
                    },
                    done: {
                        optype: "",
                        params: {
                            page_size: 7,
                            page_num: 1,
                            is_checked: 1
                        }
                    }
                },
                tableList: [],
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                searchData:{ //搜索条件
                    bill_no: "",
                    apply_user: "",
                    recv_acc_query_key: "",
                    pay_org_type: "",
                    min_amount: "",
                    max_amount: "",
                    status: []
                },
                statusList: { //常量数据
                    1: "已保存",
                    2: "已提交",
                    3: "审批中",
                    4: "审批通过",
                    5: "审批拒绝",
                    6: "处理中",
                    7: "已成功",
                    8: "已失败",
                    9: "已作废"
                },
            }
        },
        methods: {
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;

                for (var k in searchData) {
                    this.routerMessage.params[k] = searchData[k];
                }
                this.$emit("getCommTable", this.routerMessage);
            },
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
        },
        watch: {
            isPending: function (val, oldVal) {
                var searchData = this.searchData;
                for (var k in searchData) {
                    searchData[k] = "";
                }
            },
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data;
            }
        }
    }
</script>

