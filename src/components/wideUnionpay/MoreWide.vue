<style scoped lang="less" type="text/less">
    #moreWide{
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
    <el-container id="moreWide">
        <el-header>
            <div class="button-list-right">
                <el-button type="warning" size="mini" @click="addCollect">新增</el-button>
            </div>
            <div class="search-setion">
                <el-form :inline="true" :model="searchData" size="mini">
                    <el-row>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.gyl_allocation_type" placeholder="请选择下拨额度"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="(collType,key) in collTypeList"
                                               :key="key"
                                               :label="collType"
                                               :value="key">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.gyl_allocation_frequency" placeholder="请选择下拨频率"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option v-for="(frequency,key) in frequencyList"
                                               :key="key"
                                               :label="frequency"
                                               :value="key">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.topic" clearable placeholder="请输入下拨关键字"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.pay_acc_query_key" clearable placeholder="请输入下拨主账号关键字"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item style="margin-bottom:0px">
                                <el-checkbox-group v-model="searchData.service_status">
                                    <el-checkbox v-for="(name,k) in statusList"
                                                 :label="k" name="type" :key="k">
                                        {{ name }}
                                    </el-checkbox>
                                </el-checkbox-group>
                            </el-form-item>
                            <el-form-item label="|" style="margin-bottom:0px;margin-left:10px" v-show="showActivate">
                                <el-checkbox-group v-model="searchData.is_activity">
                                    <el-checkbox v-for="(name,k) in activatList"
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
            <div class="split-bar"></div>
        </el-header>
        <el-main>
            <el-table :data="tableList"
                      height="100%"
                      border size="mini">
                <el-table-column prop="topic" label="主题" width="180px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column label="下拨额度" :show-overflow-tooltip="true" width="200px">
                    <template slot-scope="scope">
                        <span>{{ scope.row.gyl_allocation_type_name }}</span>
                        <span style="margin-left: 10px;color:#fd7d2f">{{ scope.row.gyl_allocation_amount_new }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="下拨频率" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span>{{ scope.row.gyl_allocation_frequency_name }} - </span>
                        <span>{{ scope.row.gyl_allocation_frequenc_time }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="service_status" label="业务状态" :show-overflow-tooltip="true"
                                 width="100px" :formatter="transitStatus"></el-table-column>
                <el-table-column
                        label="操作" width="80"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.service_status != 1 && scope.row.service_status != 5">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookCollect(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.service_status == 1 || scope.row.service_status == 5">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editCollect(scope.row)"></el-button>
                        </el-tooltip>
                        <!-- <el-tooltip content="复制" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.service_status == 1 || scope.row.service_status == 5 || scope.row.service_status == 2">
                            <el-button class="on-copy" size="mini"
                                       @click="copyCollect(scope.row)"></el-button>
                        </el-tooltip> -->
                        <el-tooltip content="撤回" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.service_status == 2">
                            <el-button size="mini" class="withdraw"
                                       @click="withdrawBill(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.service_status == 1">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeBill(scope.row,scope.$index,tableList)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>
        <el-footer>
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
        </el-footer>
    </el-container>
</template>

<script>
    export default {
        name: "MoreWide",
        created: function () {
            this.$emit("transmitTitle", "广银联-更多单据");
            this.$emit("getCommTable", this.routerMessage);

            /*获取常量数据*/
            //获取归集额度
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.CollOrPoolType) {
                this.collTypeList = constants.CollOrPoolType;
            }
            //归集频率
            if (constants.CollOrPoolFrequency) {
                this.frequencyList = constants.CollOrPoolFrequency;
            }
        },
        props: ["tableData"],
        data:function(){
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "gylsetting_morebill",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                searchData:{ //搜索条件
                    gyl_allocation_type: "",
                    gyl_allocation_frequency: "",
                    topic: "",
                    pay_acc_query_key: "",
                    service_status: [],
                    is_activity: []
                },
                statusList: {
                    "1": "已保存",
                    "2": "已提交",
                    "3": "审批中",
                    "5": "审批拒绝",
                    "9": "已作废",
                    "4": "审批通过",
                },
                activatList: {
                    0: "未激活",
                    1: "已激活"
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                collTypeList: {}, //常量数据
                frequencyList: {},
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
            //展示格式转换-处理状态
            transitStatus: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.BillStatus) {
                    return constants.BillStatus[cellValue];
                }
            },
            //删除
            removeBill: function (row, index, rows) {
                this.$confirm('确认删除当前单据吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "gylsetting_del",
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
                        }

                        if (this.pagCurrent < (this.pagTotal / this.pagSize)) { //存在下一页
                            this.$emit("getCommTable", this.routerMessage);
                        } else {
                            if (rows.length == "1" && (this.routerMessage.params.page_num != 1)) { //是当前页最后一条
                                this.routerMessage.params.page_num--;
                                this.$emit("getCommTable", this.routerMessage);
                            } else {
                                rows.splice(index, 1);
                                this.pagTotal--;
                            }
                        }

                        this.$message({
                            type: "success",
                            message: "删除成功",
                            duration: 2000
                        })
                    }).catch(function (error) {
                        console.log(error);
                    })
                }).catch(() => {
                });
            },
            //新增
            addCollect: function(){
                this.$router.push({
                    name: "StrategySet"
                });
            },
            //复制
            copyCollect: function(row){
                this.$router.push({
                    name: "StrategySet",
                    query: {
                        copyId: row.id
                    }
                });
            },
            //编辑
            editCollect: function(row){
                this.$router.push({
                    name: "StrategySet",
                    query: {
                        id: row.id
                    }
                });
            },
            //查看
            lookCollect: function(row){
                this.$router.push({
                    name: "StrategySet",
                    query: {
                        viewId: row.id
                    }
                });
            },
            //撤回
            withdrawBill: function (row) {
                this.$confirm('确认撤回当前单据吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url:this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "gylsetting_revoke",
                            params: {
                                id: row.id,
                                persist_version: row.persist_version,
                                service_status: row.service_status
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
            },
        },
        computed: {
            showActivate: function(){
                if(this.searchData.service_status.indexOf("4") == -1){
                    this.searchData.is_activity = [];
                }
                return this.searchData.service_status.indexOf("4") != -1;
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

