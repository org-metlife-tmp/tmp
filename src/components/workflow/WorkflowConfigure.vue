<style lang="less" scoped type="text/less">
    #workflowConfigure {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion {
            width: 50%;
            margin: 0 auto 10px;
            /*搜索区按钮*/
            .el-button--primary {
                /*color: #fff;*/
                /*background-color: #409EFF;*/
            }
        }

        /*数据展示区*/
        .table-content{
            height: 407px;
        }

        /*按钮*/
        .on-off, .look-work-flow{
            width: 22px;
            height: 22px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
        }
        /*-设置状态*/
        .on-off{
            background-position: -273px -62px;
        }
        /*-查看*/
        .look-work-flow{
            background-position: -296px -61px;
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -12px;
        }

        /*配置流程弹框-新增*/
        .add-button{
            width: 100%;
            text-align: right;
            margin: -12px 0 6px 0;
            border-top: 1px solid #eee;
            padding-top: 6px;
        }
    }
</style>
<style lang="less" type="text/less">
    #workflowConfigure{
        .search-setion{
            .el-input-group__append{
                background-color: #409EFF;
                color: #fff;
            }
        }
    }
</style>

<template>
    <div id="workflowConfigure">
        <!--搜索区-->
        <div class="search-setion">
            <el-input placeholder="请输入流程名查询" class="input-with-select"
                      size="small" v-model="searchData" clearable>
                <el-button type="primary" slot="append" icon="el-icon-search" @click="queryData"></el-button>
            </el-input>
        </div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      border
                      height="100%"
                      size="mini">
                <el-table-column prop="workflow_name" label="流程名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="is_activity" label="配置状态" :formatter="transitionAct" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="配置处理"
                        width="110">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="设置状态" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button size="mini"
                                       @click="setStatus(scope.row)"
                                       class="on-off"></el-button>
                        </el-tooltip>
                        <el-tooltip content="查看" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button size="mini"
                                       @click=""
                                       class="look-work-flow"></el-button>
                        </el-tooltip>
                        <el-tooltip content="配置" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="warning" icon="el-icon-setting" size="mini"
                                       @click="settingWorkFlow(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </section>
        <!--分页部分-->
        <div class="botton-pag">
            <el-pagination
                    background :pager-count="5"
                    :current-page="pagCurrent"
                    layout="sizes , prev, pager, next, jumper"
                    :page-size="pagSize" :total="pagTotal"
                    :page-sizes="[10, 50, 100, 500]"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange">
            </el-pagination>
        </div>
        <!--配置弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="900px"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">流程配置</h1>
            <div class="add-button">
                <el-button type="warning" size="mini" @click="">新增</el-button>
            </div>
            <el-table :data="settingTableList"
                      border
                      size="mini">
                <el-table-column prop="name" label="名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="create_on" label="时间" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作"
                        width="80">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="编辑" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click=""></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click=""></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
            <span slot="footer" class="dialog-footer" style="text-align:center">
                <el-pagination
                        background :pager-count="5"
                        layout="prev, pager, next, jumper"
                        :page-size="10"
                        :total="settingTotal"
                        @current-change="getSettingData">
                    </el-pagination>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "WorkflowConfigure",
        created: function () {
            this.$emit("transmitTitle", "业务配置审批流程");
            this.$emit("getTableData", this.routerMessage);
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: { //本页数据获取参数
                    optype: "wfdefine_list",
                    params: {
                        page_size: 10,
                        page_num: 1
                    }
                },
                searchData: "", //搜索数据
                tableList: [], //列表数据
                pagSize: 1, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                dialogVisible: false, //弹框数据
                settingTotal: 1,
                settingTableList: [],
                currentBaseId: "",
            }
        },
        methods:{
            //展示格式转换-配置状态
            transitionAct: function (row, column, cellValue, index) {
                if(cellValue == "1"){
                    return "启用";
                }else{
                    return "禁用";
                }
            },
            //查询数据
            queryData: function(){
                this.routerMessage.params.query_key = this.searchData;
                this.$emit("getTableData", this.routerMessage);
            },
            //点击页数 获取当前页数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getTableData", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange:function(val){
                this.routerMessage.params = {
                    page_size: val,
                    page_num: "1"
                };
                this.$emit("getTableData", this.routerMessage);
            },
            //设置状态
            setStatus:function (row) {
                this.$axios({
                    url:"/cfm/adminProcess",
                    method: "post",
                    data: {
                        optype: "wfdefine_setstatus",
                        params: {
                            id: row.id,
                            persist_version: row.persist_version
                        }
                    }
                }).then((result) => {
                    if(result.data.error_msg){
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
                        var data = result.data.data;
                        for(var k in data){
                            row[k] = data[k];
                        }
                        this.$message({
                            type: 'success',
                            message: '修改成功!',
                            duration: 2000
                        });
                    }
                }).catch(function(error){
                    console.log(error);
                })
            },
            //配置流程
            settingWorkFlow:function(row){
                this.dialogVisible = true;
                this.currentBaseId = row.id;
                this.getSettingData(1);
            },
            //配置流程弹框数据换页
            getSettingData: function(currPag){
                this.$axios({
                    url: "/cfm/adminProcess",
                    method: "post",
                    data: {
                        optype: "wfrelation_list",
                        params: {
                            page_size: 10,
                            page_num: currPag,
                            base_id: this.currentBaseId
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
                        var data = result.data;
                        this.settingTableList = data.data;
                        this.settingTotal = data.total_line;
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

