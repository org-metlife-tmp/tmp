<style scoped lang="less" type="text/less">
    #suspiciousData {
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

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

        /*查看弹框*/
        .dialog-talbe {
            width: 100%;
            height: 260px;

            li {
                float: left;
                box-sizing: border-box;
                border: 1px solid #e2e2e2;
                margin-left: -1px;
                margin-top: -1px;
                height: 30px;
                line-height: 30px;
            }

            .table-li-title {
                width: 12%;
                text-align: right;
                padding-right: 10px;
                font-weight: bold;
            }
            .table-li-content {
                width: 38%;
                padding-left: 10px;
            }

            .table-two-row {
                width: 88%;
                margin-left: -3px;
                border-left: none;
            }
        }

        /*详情弹出框区域分割样式*/
        .form-small-title {
            // font-weight: bold;
            border-bottom: 1px solid #e3e3e3;
            padding-bottom: 8px;
            margin-bottom: 15px;
            span:first-child {
                display: inline-block;
                width: 4px;
                height: 16px;
                background-color: orange;
                margin-right: 6px;
                vertical-align: middle;
            }
        }

        /*撤回样式*/
        .withdraw {
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
            background-position: -48px 0;
        }
        /*通过样式*/
        .pass{
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
            background-position: -465px -62px;
        }
    }
</style>
<style lang="less" type="text/less">
    #suspiciousData {
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 400px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="suspiciousData">
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
                            <el-input v-model="searchData.org_name" clearable placeholder="请输入申请单位"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.recv_acc_no" clearable placeholder="请输入收款人账号或名称"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.pay_org_type" placeholder="请选择付款机构类型"
                                       clearable filterable
                                       style="width:100%">
                                <el-option v-for="(payOrg,key) in payOrgList"
                                           :key="key"
                                           :label="payOrg"
                                           :value="key">
                                </el-option>
                            </el-select>
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

                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      border size="mini">
                <el-table-column prop="bill_no" label="报销单申请号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_org_type" label="付款机构类型" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="org_name" label="申请单位" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_acc_name" label="收款人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_acc_no" label="收款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_bank" label="收款方银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="amount" label="金额" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="memo" label="摘要" :show-overflow-tooltip="true"></el-table-column>

                <el-table-column
                        label="操作" width="110"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookData(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="通过" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button class="pass" size="mini"
                                       @click="passData(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="撤回" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button size="mini" class="withdraw"
                                       @click="withdrawData(scope.row)"></el-button>
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
        <!--查看弹出框-->
        <el-dialog title="单据信息"
                   :visible.sync="dialogVisible"
                   width="900px" top="76px"
                   :close-on-click-modal="false">
            <ul class="dialog-talbe">
                <li class="table-li-title">报销单申请号</li>
                <li class="table-li-content" v-text="dialogData.bill_no"></li>
                <li class="table-li-title">申请公司</li>
                <li class="table-li-content" v-text="dialogData.org_name"></li>

                <li class="table-li-title">申请部门</li>
                <li class="table-li-content" v-text="dialogData.apply_dept"></li>
                <li class="table-li-title">金额</li>
                <li class="table-li-content" style="color:#fd7d2f">￥{{ dialogData.amount }}</li>

                <li class="table-li-title">申请用户</li>
                <li class="table-li-content" v-text="dialogData.apply_user"></li>
                <li class="table-li-title">申请日期</li>
                <li class="table-li-content" v-text="dialogData.apply_date"></li>

                <li class="table-li-title">收款人账户</li>
                <li class="table-li-content" v-text="dialogData.recv_acc_no"></li>
                <li class="table-li-title">收款人账户名</li>
                <li class="table-li-content" v-text="dialogData.recv_acc_name"></li>

                <li class="table-li-title">收款人银行</li>
                <li class="table-li-content" v-text="dialogData.recv_bank"></li>
                <li class="table-li-title">银行所在省</li>
                <li class="table-li-content" v-text="dialogData.recv_bank_prov"></li>

                <li class="table-li-title">银行所在市</li>
                <li class="table-li-content" v-text="dialogData.recv_bank_city"></li>
                <li class="table-li-title">开户行地址</li>
                <li class="table-li-content" v-text="dialogData.recv_bank_addr"></li>

                <li class="table-li-title">开户行类型</li>
                <li class="table-li-content" v-text="dialogData.recv_bank_type"></li>
                <li class="table-li-title">CNAPS号</li>
                <li class="table-li-content" v-text="dialogData.recv_bank_cnaps"></li>

                <li class="table-li-title">付款机构类型</li>
                <li class="table-li-content" v-text="dialogData.pay_org_type"></li>
                <li class="table-li-title">摘要</li>
                <li class="table-li-content" v-text="dialogData.memo"></li>
            </ul>
            <div class="form-small-title">
                <span></span>
                <span>可疑数据</span>
            </div>
            <el-table :data="dialogList"
                      border size="mini">
                <el-table-column prop="bill_no" label="报销单申请号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_org_type" label="付款机构类型" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="ref_service_serial_number" label="单据编号" :show-overflow-tooltip="true"></el-table-column>
            </el-table>
            <span slot="footer" class="dialog-footer">
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "SuspiciousData",
        created: function () {
            //设置当前页基本信息
            this.$emit("transmitTitle", "可疑数据管理");
            this.$emit("getCommTable", this.routerMessage);
        },
        mounted: function(){
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if(constants.OaPayOrgType){
                this.payOrgList = constants.OaPayOrgType;
            }
        },
        data: function () {
            return {
                routerMessage: {
                    optype: "checkdoubtfuloa_list",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                tableList: [],
                dialogList: [],
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                searchData:{ //搜索条件
                    bill_no: "",
                    org_name: "",
                    recv_acc_no: "",
                    pay_org_type: "",
                    min_amount: "",
                    max_amount: ""
                },
                dialogVisible: false, //弹框数据
                dialogData: {},
                payOrgList: {} //常量数据
            }
        },
        props: ["tableData"],
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
                this.routerMessage.params = {
                    page_size: val,
                    page_num: 1
                };
                this.$emit("getCommTable", this.routerMessage);
            },

            //查看
            lookData: function(row){
                this.dialogVisible = true;
                var dialogData = this.dialogData;
                for(var k in row){
                    dialogData[k] = row[k];
                }

                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "checkdoubtfuloa_doubtlist",
                        params: {
                            bill_no: row.bill_no,
                            identification: row.identification
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                        return;
                    } else {
                        var data = result.data.data;
                        this.dialogList = data;
                        console.log(data);
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //通过
            passData: function(row){
                this.$confirm('确认通过当前单据吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/normalProcess",
                        method: "post",
                        data: {
                            optype: "checkdoubtfuloa_pass",
                            params: {
                                id: row.id,
                                persist_version: row.persist_version
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {
                            this.$message({
                                type: "error",
                                message: result.data.error_msg,
                                duration: 2000
                            })
                            return;
                        } else {
                            this.$message({
                                type: "success",
                                message: "通过成功",
                                duration: 2000
                            });
                            this.$emit("getCommTable", this.routerMessage);
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }).catch(() => {
                });
            },
            //通过
            withdrawData: function(row){
                this.$prompt('请输入撤回原因', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    title: "撤回原因",
                    inputValidator: function(value){
                        if(!value){
                            return false;
                        }else{
                            return true;
                        }
                    },
                    inputErrorMessage: '请输入撤回原因'
                }).then(({ value }) => {
                    this.$axios({
                        url: "/cfm/normalProcess",
                        method: "post",
                        data: {
                            optype: "checkdoubtfuloa_payoff",
                            params: {
                                ids: [row.id],
                                persist_version: [row.persist_version],
                                feed_back: value
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
                                message: "撤回成功",
                                duration: 2000
                            });
                            this.$emit("getCommTable", this.routerMessage);
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }).catch(() => {

                });
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

