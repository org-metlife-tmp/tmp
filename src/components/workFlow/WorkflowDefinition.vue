<style lang="less" scoped>
    #workflowDefinition{
        /*搜索区*/
        .search-setion{
            margin-bottom: 20px; 
            /*搜索区按钮*/
            .el-button--primary {
                color: #fff;
                background-color: #409EFF;
                border-color: #409EFF;
                border-radius: 0;
            }
        }
        
        /*顶部按钮*/
        .button-list-right {
            position: absolute;
            top: -42px;
            right: 0px;
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

        /*数据展示区*/
        .table-content {
            height: 289px;
        }
        /*按钮-复制*/
        .on-copy {
            width: 22px;
            height: 22px;
            background-image: url(../../assets/icon_common.png);
            background-position: -22px 1px;
            border: none;
            padding: 0;
            vertical-align: middle;
        }
    }
</style>
<template>
    <div id="workflowDefinition">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="createProcess">新建流程</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="12" :offset="6">
                        <el-input placeholder="请输入内容" v-model="searchData.query_key" class="input-with-select">
                            <el-button slot="append" icon="el-icon-search" type="primary" @click="queryData"></el-button>
                        </el-input>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!--数据展示区-->
        <section :class="['table-content']">
            <el-table :data="tableList"
                      border
                      height="100%"
                      size="mini">
                <el-table-column prop="workflow_name" label="流程名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="create_on" label="创建时间" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="user_name" label="创建人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="150"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookMatter(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="复制" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button class="on-copy" size="mini"
                                       @click="lookMatter(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editMerch(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeMatter(scope.row,scope.$index,tableList)"></el-button>
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
                    :current-page="pagCurrent"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange">
            </el-pagination>
        </div>
        <!--新建流程弹出框-->
        <el-dialog :visible.sync="createDialogVisible"
                   width="500px" title="新建流程"
                   :close-on-click-modal="false"
                   top="156px">
            <h1 slot="title" class="dialog-title">新建流程</h1>
            <el-form :model="createDialogData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="流程组名称">
                            <el-input v-model="createDialogData.apply_on"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="审批退回">
                            <el-select v-model="createDialogData.user_ids">
                                <el-option
                                    v-for="item in back_options"
                                    :key="item.id"
                                    :label="item.name"
                                    :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="初始级次">
                            <el-select v-model="createDialogData.user_ids">
                                <el-option
                                    v-for="item in gradation_options"
                                    :key="item.id"
                                    :label="item.name"
                                    :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="createDialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="">下 一 步</el-button>
            </span>
        </el-dialog>
    </div>
</template>
<script>
export default {
    name:"WorkflowDefinition",
    created:function(){
        this.$emit("transmitTitle","流程定义");
        this.$emit("getTableData", this.routerMessage);
    },
    props:["tableData"],
    data:function(){
        return {
            routerMessage: {
                optype:"wfdefine_list",
                params: {
                    page_size: 10,
                    page_num: 1
                }
            },
            formLabelWidth: "120px",
            searchData: {},
            tableList: [],
            pagSize: 7,
            pagTotal: 1,
            pagCurrent: 1,
            createDialogVisible: false,
            createDialogData: {},
            back_options:[
                {id:'1',name:'回退到上一级'},
                {id:'2',name:'回退到提交人'},
                {id:'3',name:'自定义'}
            ],
            gradation_options:[
                {id:'1',name:'1'},
                {id:'2',name:'2'},
                {id:'3',name:'3'},
                {id:'4',name:'4'},
                {id:'5',name:'5'}
            ]
        }
    },
    methods:{
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
        //查询数据
        queryData:function(){
            this.routerMessage.params.query_key = this.searchData.query_key;
            this.$emit("getTableData", this.routerMessage);
        },
        //新建流程
        createProcess:function(){
            this.createDialog = {};
            this.createDialogVisible = true;
        }
    },
    watch: {
        tableData: function (val, oldVal) {
            this.pagSize = val.page_size;
            this.pagTotal = val.total_line;
            this.pagCurrent = val.page_num;
            debugger
            this.tableList = val.data;
        }
    }
}
</script>


