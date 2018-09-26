<style scoped lang="less" type="text/less">
    #taskLook{
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
    <div id="taskLook">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addCollect">新增</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.collect_type" placeholder="请选择归集额度"
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
                            <el-select v-model="searchData.collect_frequency" placeholder="请选择归集频率"
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
                            <el-input v-model="searchData.topic" clearable placeholder="请输入归集关键字"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.main_acc_query_key" clearable placeholder="请输入归集主账号关键字"></el-input>
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
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      border size="mini">
                <el-table-column prop="topic" label="主题" width="180px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column label="下拨额度" :show-overflow-tooltip="true" width="200px">
                    <template slot-scope="scope">
                        <span>{{ scope.row.gyl_allocation_type }}</span>
                        <span style="margin-left: 10px;color:#fd7d2f">{{ transitNum(scope.row.gyl_allocation_amount) }}</span>
                    </template>
                </el-table-column>
                <el-table-column label="下拨频率" :show-overflow-tooltip="true">
                    <template slot-scope="scope">
                        <span>{{ scope.row.gyl_allocation_frequency }} - </span>
                        <span>{{ scope.row.gyl_allocation_time }}</span>
                    </template>
                </el-table-column>
                <el-table-column prop="service_status" label="业务状态" :show-overflow-tooltip="true"
                                 width="100px" :formatter="transitStatus"></el-table-column>
                <el-table-column
                        label="操作" width="50"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookCollect(scope.row)"></el-button>
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
    </div>
</template>

<script>
    export default {
        name: "taskLook",
        created: function () {
            this.$emit("transmitTitle", "任务查看");
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
                routerMessage: {
                    optype: "gylview_collections",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                searchData:{ //搜索条件
                    collect_type: "",
                    collect_frequency: "",
                    topic: "",
                    main_acc_query_key: "",
                    service_status: [],
                    is_activity: []
                },
                statusList: {
                    2: "已提交",
                    3: "审批中",
                    4: "审批通过",
                    9: "已作废"
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
            //展示格式转换-处理状态
            transitStatus: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.BillStatus) {
                    return constants.BillStatus[cellValue];
                }
            },
            //展示格式转换-金额
            transitNum: function(num){
                return "￥" + this.$common.transitSeparator(num);
            },
            //新增归集单据
            addCollect: function(){
                this.$router.push({
                    name: "StrategySet"
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
            }
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



