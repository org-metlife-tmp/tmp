<style scoped lang="less" type="text/less">
    #refundTicket {
        .table-content {
            height: 100%;
        }

        /*分页部分*/
        .botton-pag {
            width: 100%;

            .el-button {
                float: right;
                margin-top: -30px;
            }

            .check-select {
                float: right;
                margin-top: -26px;
                margin-right: 70px;
            }
        }
    }
</style>

<template>
    <el-container id="refundTicket">
        <el-header>
            <div class="button-list-right">

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
                                        start-placeholder="开始日期"
                                        end-placeholder="结束日期"
                                        value-format="yyyy-MM-dd"
                                        clearable
                                        unlink-panels
                                        :picker-options="pickerOptions"
                                        @change="">
                                </el-date-picker>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.acc_no" placeholder="请输入账户号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.opp_acc_no" placeholder="请输入对方账号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="7">
                            <el-form-item>
                                <el-col :span="11">
                                    <el-input v-model="searchData.min" placeholder="最小金额"></el-input>
                                </el-col>
                                <el-col class="line" :span="1" style="text-align:center">-</el-col>
                                <el-col :span="11">
                                    <el-input v-model="searchData.max" placeholder="最大金额"></el-input>
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
            <div class="split-bar"></div>
        </el-header>
        <el-main>
            <section class="table-content">
                <el-table :data="tableList" border
                          height="100%" size="mini"
                          highlight-current-row
                          @current-change="getCheckData">
                    <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="bank_type_name" label="开户行" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="direction" label="收付方向" :show-overflow-tooltip="true"
                                     :formatter="transitDirection"></el-table-column>
                    <el-table-column prop="opp_acc_no" label="对方账户号" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="opp_acc_name" label="对方账户名称" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="amount" label="交易金额" :show-overflow-tooltip="true"
                                     :formatter="transitAmount"></el-table-column>
                    <el-table-column prop="summary" label="摘要" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="trans_date" label="日期" :show-overflow-tooltip="true"></el-table-column>
                </el-table>
            </section>
        </el-main>
        <el-footer>
            <div class="botton-pag">
                <el-pagination
                        background
                        layout="sizes, prev, pager, next, jumper"
                        :page-size="pagSize"
                        :total="pagTotal"
                        :page-sizes="[5, 7, 50, 100, 500]"
                        :pager-count="5"
                        :current-page="pagCurrent"
                        @current-change="getCurrentPage"
                        @size-change="sizeChange">
                </el-pagination>
                <el-button type="warning" size="mini" @click="confirmCheck"
                           :disabled="!currentData || !childList.biz_id">确认</el-button>
            </div>
            <section style="margin-top:14px;height:140px">
                <el-form :model="childList" size="mini"
                         :label-width="formLabelWidth"
                         ref="dialogForm">
                    <el-row>
                        <el-col :span="7">
                            <el-form-item label="业务类型">
                                <el-select v-model="childList.biz_id" placeholder="请选择业务类型"
                                           filterable size="mini" >
                                    <el-option v-for="payItem in biztypeList"
                                               :key="payItem.biz_id"
                                               :label="payItem.biz_name"
                                               :value="payItem.biz_id">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="16" style="height:50px"></el-col>

                        <el-col :span="3" style="text-align:left">
                            <el-form-item label=" " label-width="30px">
                                <el-checkbox v-model="hasOpp">覆盖对方账户</el-checkbox>
                            </el-form-item>
                        </el-col>
                        <el-col :span="7">
                            <el-form-item label="对方账户号">
                                <el-select v-model="childList.opp_acc_no"
                                           filterable allow-create
                                           default-first-option
                                           :disabled="!hasOpp"
                                           placeholder="请输入或选择账号"
                                           @change="setPayer">
                                    <el-option
                                            v-for="item in payerList"
                                            :key="item.acc_no"
                                            :label="item.acc_no"
                                            :value="item.acc_no">
                                        <span>{{ item.acc_no }}</span>
                                        <span style="margin-left:10px;color:#bbb">{{ item.acc_name }}</span>
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="7">
                            <el-form-item label="对方账户名称">
                                <el-input v-model="childList.opp_acc_name" :disabled="!hasOpp"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="7">
                            <el-form-item label="对方开户行">
                                <SetBanktype :fillinBankName="childList.opp_cnaps_name"
                                            @bankInfo="setBank" :isDisabled="!hasOpp"></SetBanktype>
                            </el-form-item>
                        </el-col>

                        <el-col :span="3" style="text-align:left">
                            <el-form-item label=" " label-width="30px">
                                <el-checkbox v-model="hasSummary">覆盖摘要</el-checkbox>
                            </el-form-item>
                        </el-col>
                        <el-col :span="7">
                            <el-form-item label="摘要">
                                <el-input v-model="childList.summary" :disabled="!hasSummary"></el-input>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </section>
        </el-footer>
    </el-container>
</template>

<script>
    import SetBanktype from "../publicModule/SetBanktype.vue"

    export default {
        name: "RefundTicket",
        created: function () {
            //设置当前页基本信息
            this.$emit("transmitTitle", "退票支付");
            this.$emit("getCommTable", this.routerMessage);
        },
        mounted: function () {
            //业务类型
            this.getBiztype();
            //账号
            this.getPayerSelect();
        },
        props: ["tableData"],
        components: {
            SetBanktype: SetBanktype
        },
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "zftrefund_list",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                dateValue: "", //时间选择
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
                searchData: {
                    acc_no: "",
                    opp_acc_no: "",
                    min: "",
                    max: ""
                },
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                tableList: [],
                childList: {
                    biz_id: "",
                    opp_acc_id: "",
                    opp_acc_no: "",
                    opp_acc_name: "",
                    opp_cnaps_code: "",
                    opp_cnaps_name: "",
                    summary: ""
                },
                hasOpp: false,
                hasSummary: false,
                formLabelWidth: "120px",
                currentData: "", //当前项
                biztypeList: [], //常量
                payerList: [],
            }
        },
        methods: {
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                searchData.start_trans_date = this.dateValue ? this.dateValue[0] : "";
                searchData.end_trans_date = this.dateValue ? this.dateValue[1] : "";
                var params = this.routerMessage.params;
                for (var k in searchData) {
                    params[k] = searchData[k];
                }
                params.page_num = 1;
                this.$emit("getCommTable", this.routerMessage);
            },
            //点击页数获取当前页数据
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
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //展示格式转换-收付方向
            transitDirection: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                return constants.PayOrRecv[cellValue];
            },
            //业务类型
            getBiztype: function(){
                this.$axios({
                    url: this.queryUrl + "commProcess",
                    method: "post",
                    data: {
                        optype: "biztype_biztypes",
                        params: {
                            p_id: 9
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
                        this.biztypeList = data;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //账号
            getPayerSelect: function(){
                //获取收款方户名列表
                this.$axios({
                    url:this.queryUrl + "normalProcess",
                    method:"post",
                    data:{
                        optype:"zft_recvacclist",
                        params:{
                            page_num:1,
                            page_size: 10000
                        }
                    }
                }).then((result) =>{
                    if (result.data.error_msg) {

                    } else {
                        this.payerList = result.data.data;
                    }
                });
            },
            //判断账号时录入还是选择
            setPayer: function(val){
                if(val){
                    var payerList = this.payerList;
                    var flag = false;
                    for(var i = 0; i < payerList.length; i++){
                        var item = payerList[i];
                        if(item.acc_no == val){
                            var childList = this.childList;
                            childList.opp_acc_id = item.id;
                            childList.opp_acc_name = item.acc_name;
                            childList.opp_cnaps_name = item.bank_name;
                            flag = true;
                            break;
                        }
                    }
                    if(!flag){
                        this.childList.opp_acc_id = "";
                    }
                }
            },
            //设置银行信息
            setBank:function(val){
                if(val){
                    this.childList.opp_cnaps_code = val.cnaps_code;
                }
            },
            //保存当前项
            getCheckData: function (val) {
                if (!val) {
                    this.currentData = "";
                    return;
                }
                this.currentData = val;
            },
            //确认
            confirmCheck: function () {
                var childData = this.childList;
                var params = {
                    trans_id: this.currentData.id
                }
                for(var k in childData){
                    params[k] = childData[k];
                }

                this.$confirm('确认进行当前操作吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "zftrefund_confirm",
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
                            this.$emit("getCommTable", this.routerMessage);
                            for(var k in childData){
                                childData[k] = "";
                            }
                            this.$message({
                                type: "success",
                                message: "确认成功",
                                duration: 2000
                            });
                        }
                    }).catch(function (error) {
                        console.log(error);
                    });
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


