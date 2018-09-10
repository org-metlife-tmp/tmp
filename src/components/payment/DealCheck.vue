<style scoped lang="less" type="text/less">
    #dealCheck{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion{
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

        /*数据展示区*/
        .table-content {
            height: 181px;
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;

            .el-button {
                float: right;
                margin-top: -30px;
            }
        }
        .botton-pag-center{
            bottom: 184px;
        }

        /*分割线*/
        .split-form {
            width: 100%;
            height: 26px;
            border-bottom: 1px solid #ccc;
            margin-bottom: 10px;
            h4 {
                margin: 0;
                float: left;
            }
        }
    }
</style>

<template>
    <div id="dealCheck">
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" placeholder="请输入付款方账号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4" v-if="isPending">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" placeholder="请输入收款方账号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item>
                            <el-col :span="11">
                                <el-input v-model="searchData.query_key" placeholder="最小金额"></el-input>
                            </el-col>
                            <el-col class="line" :span="1" style="text-align:center">-</el-col>
                            <el-col :span="11">
                                <el-input v-model="searchData.query_key" placeholder="最大金额"></el-input>
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
        <section class="table-content" v-if="isPending">
            <el-table :data="tableList"
                      border
                      height="100%"
                      size="mini">
                <el-table-column prop="apply_on" label="付款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="memo" label="付款银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="service_status" label="收款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="service_status" label="收款方公司名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="service_status" label="金额" :show-overflow-tooltip="true"></el-table-column>

                <el-table-column
                        label="操作" width="50"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="!isPending">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookMatter(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </section>
        <!--分页部分-->
        <div class="botton-pag" :class="{'botton-pag-center':isPending}">
            <el-pagination
                    background
                    layout="sizes, prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    :page-sizes="[7, 50, 100, 500]"
                    :pager-count="5"
                    :current-page="pagCurrent"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange">
            </el-pagination>
            <el-button type="warning" size="mini" @click="" v-show="isPending">确认</el-button>
        </div>
        <!--主数据关联数据-->
        <section class="table-content" style="margin-top:40px" v-if="isPending">
            <el-table :data="childList"
                      border
                      height="100%"
                      size="mini">
                <el-table-column type="selection" width="38"></el-table-column>
                <el-table-column prop="apply_on" label="付款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="memo" label="付款银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="service_status" label="收款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="service_status" label="收款方公司名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="service_status" label="金额" :show-overflow-tooltip="true"></el-table-column>

                <el-table-column
                        label="操作" width="50"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="!isPending">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookMatter(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </section>
    </div>
</template>

<script>
    export default {
        name: "DealCheck",
        created: function () {
            this.$emit("transmitTitle", "交易核对");
            this.$emit("tableText", {
                leftTab: "未核对",
                rightTab: "已核对"
            });
            this.$emit("getTableData", this.routerMessage);
        },
        mounted: function () {
        },
        props:["isPending","tableData"],
        data: function () {
            return {
                routerMessage: {
                    todo:{
                        optype: "closeacc_todolist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    },
                    done:{
                        optype: "closeacc_donelist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    }
                },
                searchData:{
                    service_status:[]
                },
                tableList:[],
                childList: [],
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
            }
        },
        methods: {
            //根据条件查询数据
            queryData:function(){
                var searchData = this.searchData;
                for(var k in searchData){
                    if(this.isPending){
                        this.routerMessage.todo.params[k] = searchData[k];
                    }else{
                        this.routerMessage.done.params[k] = searchData[k];
                    }
                }
                this.$emit("getTableData", this.routerMessage);
            },
            //点击页数获取当前页数据
            getCurrentPage:function(currPage){
                if(this.isPending){
                    this.routerMessage.todo.params.page_num = currPage;
                }else{
                    this.routerMessage.done.params.page_num = currPage;
                }
                this.$emit("getTableData", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange:function(val){
                this.routerMessage.todo.params = {
                    page_size: val,
                    page_num: 1
                };
                this.routerMessage.done.params = {
                    page_size: val,
                    page_num: 1
                };
                this.$emit("getTableData", this.routerMessage);
            },
            //已处理事项查看
            lookMatter:function(row){
                this.businessParams = {};//清空数据
                this.businessParams.biz_type = 6;
                this.businessParams.id = row.id;

                for(var k in this.lookDialogData){
                    this.lookDialogData[k] = "";
                }
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "closeacc_detail",
                        params:{
                            id:row.id
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
                        let data = result.data.data
                        data.org_name = row.org_name;
                        data.dept_name = row.dept_name;
                        data.user_name = row.user_name;
                        this.lookDialogData = data;
                        this.lookDialog = true;
                    }
                })
                if(row.issues){
                    this.issueList = row.issues.split(",");
                }else{
                    this.issueList = [];
                }
                //附件数据
                this.emptyFileList = [];
                this.fileMessage.bill_id = row.id;
                this.triggerFile = !this.triggerFile;
            },
        },
        watch:{
            isPending:function(val,oldVal){
                this.searchData.query_key = "";
                this.searchData.service_status = [];
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

