<style lang="less" scoped type="text/less">
    #workflowTrace {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
        overflow: hidden;

        /*搜索区*/
        .search-setion {
            text-align: left;
        }
        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

        /*已办数据展示区*/
        .table-content {
            height: 336px;
        }
        /*分隔栏*/
        .split-bar {
            width: 106%;
            height: 6px;
            margin-left: -20px;
            background-color: #E7E7E7;
            margin-bottom: 20px;
        }
        /*按钮样式*/
        .withdraw,.look-work-flow,.iconTime{
            width: 22px;
            height: 22px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
        }
        /*按钮-撤回*/
        .withdraw{
            background-position: -48px 0px;
        }
        /*按钮-查看*/
        .look-work-flow {
            background-position: -296px -62px;
        }
        /*按钮-流程*/
        .iconTime {
            background-position: -369px -62px;
        }
        
    }
    /*弹框标题*/
    .dialog-title {
        margin-top: 0;
        font-size: 18px;
        font-weight: 400;
    }
    /**/
    .formflot{
        display: inline-block;
        .el-input{
            width:60%;
        }
        .el-input__inner {
            height: 30px;
            line-height: 30px;
        }
    }
    #showbox{
        position: absolute;
        right: -500px;
        transition: all 1.5s;
    } 
</style>

<template>
    <div id="workflowTrace">
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.biz_type" placeholder="请选择业务种类" clearable >
                                <el-option
                                    v-for="item in businessType"
                                    :key="item.id"
                                    :label="item.name"
                                    :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item>
                            <el-col :span="11">
                                <el-date-picker
                                        v-model="searchData.start_time"
                                        type="date"
                                        placeholder="起始日期"
                                        value-format="yyyy-MM-dd"
                                        style="width: 100%;">
                                </el-date-picker>
                            </el-col>
                            <el-col class="line" :span="1" style="text-align:center">-</el-col>
                            <el-col :span="11">
                                <el-date-picker
                                        v-model="searchData.end_time"
                                        type="date"
                                        placeholder="结束日期"
                                        value-format="yyyy-MM-dd"
                                        style="width: 100%;">
                                </el-date-picker>
                            </el-col>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.bill_code"
                                      clearable
                                      placeholder="请输入事单据号"></el-input>
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
        <div class="split-bar" ></div>
        <!--已办数据展示区-->
        <section :class="['table-content']">
            <el-table :data="tableList"
                    border
                    height="100%"
                    size="mini">
                <el-table-column prop="bill_code" label="单据编号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="start_time" label="申请日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="biz_type" label="业务种类" 
                                :formatter="transitionStatus"
                                :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="init_user_name" label="发起人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="current" label="当前处理人"
                                :formatter="rename" 
                                :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="history_name" label="上级处理人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="future" label="下级审批人"   
                                :formatter="rename"
                                :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="110"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button size="mini"
                                       @click="lookFlow(scope.row)"
                                       class="look-work-flow"></el-button>
                        </el-tooltip>
                         <el-tooltip content="流程" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button size="mini" class="iconTime" @click="showRightFlow(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="撤回" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button size="mini" class="withdraw" @click="widthDrawBill(scope.row)"></el-button>
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
                :page-sizes="[8, 50, 100, 500]"
                :pager-count="5"
                :current-page="pagCurrent"
                @current-change="getCurrentPage"
                @size-change="sizeChange">
            </el-pagination>
        </div> 
        <!--查看工作流弹出框-->
        <el-dialog :visible.sync="lookFlowDialogVisible"
                   width="800px" title="新建流程"
                   :close-on-click-modal="false"
                   :before-close="cancelLookFlow"
                   top="56px">
            <h1 slot="title" class="dialog-title">查看流程</h1>
            <div>
                <div class="formflot" style="margin-bottom:15px">
                    <span>流程名称</span>
                    <el-input v-model="createDialogData.workflow_name" disabled size="mini"></el-input>
                </div>
                <div class="formflot">
                    <span>审批退回</span>
                    <el-input v-model="createDialogData.reject_strategy" disabled size="mini"></el-input>
                </div>
            </div>
            <WorkFlow
                :flowList="flowList"
                :isEmptyFlow="isEmptyFlow"
            ></WorkFlow>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="cancelLookFlow">取 消</el-button>
            </span>
        </el-dialog>
        <!-- 右侧流程图 -->
        <div id="showbox">
            <BusinessTracking
                :businessParams="businessParams"
                @closeRightDialog="closeRightFlow"
            ></BusinessTracking>
        </div>
        
    </div>
</template>

<script>
import WorkFlow from "../publicModule/WorkFlow.vue";
import BusinessTracking from "../publicModule/BusinessTracking.vue"
    export default {
        name: "WorkflowTrace",
        created: function () {
            this.$emit("transmitTitle", "业务跟踪管理");
            this.$emit("getTableData", this.routerMessage);

            let constants = JSON.parse(window.sessionStorage.getItem("constants"));
            this.bizType = constants.MajorBizType;
            let length = Object.keys(this.bizType).length + 1;
            let businessTypeArr = [];
            for(let i = 1; i<length; i++){
                businessTypeArr.push({
                    id:i,
                    name:this.bizType[i]
                })
            }
            this.businessType = businessTypeArr;
            
        },
        props: ["tableData"],
        components:{
            WorkFlow:WorkFlow,
            BusinessTracking:BusinessTracking
        },
        data: function () {
            return {
                routerMessage: { //本页数据获取参数
                    optype: "wftrace_list",
                    params: {
                        page_size: 8,
                        page_num: 1
                    }
                },
                tableList:[],
                pagSize: 8,
                pagTotal: 1,
                pagCurrent: 1,
                bizType:{},
                searchData: {},
                businessType:[],
                lookFlowDialogVisible:false,
                createDialogData:{},
                flowList:{},
                isEmptyFlow:false,
                businessParams:{},//业务状态追踪参数
                rightFlow:false
            }
        },
        methods:{
            //展示格式转换
            transitionStatus: function (row, column, cellValue, index) {
                if(column.property === "biz_type"){//转换业务种类
                    if (this.bizType) {
                        return this.bizType[cellValue];
                    }
                }
            },
            //审批人的展示
            rename: function(row, column, cellValue, index){
                var new_name = "";
                var len = cellValue.length;
                if(len < 1){
                    return "";
                }
                cellValue.forEach((element,index) =>{
                    if(element.name){
                        new_name = index===len-1 ? new_name + element.name : new_name + element.name + "|";
                    }
                })
                return new_name;
            },
            //根据条件查询
            queryData:function(){
                var searchData = this.searchData;
                for(var k in searchData){
                    this.routerMessage.params[k] = searchData[k];
                }
                this.routerMessage.params.page_num = 1;
                this.$emit("getTableData", this.routerMessage);
            },
            //点击页数获取当前页数据
            getCurrentPage:function(currPage){
                this.routerMessage.params.page_num = currPage;
                this.$emit("getTableData", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange:function(val){
                this.routerMessage.params = {
                    page_size: val,
                    page_num: 1
                };
                this.$emit("getTableData", this.routerMessage);
            },
            //单据撤回
            widthDrawBill:function(row){
                this.$confirm('确认撤回当前单据吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/adminProcess",
                        method: "post",
                        data: {
                            optype: "wftrace_approvrevoke",
                            params:{
                            wf_inst_id: row.id,
                            id: row.bill_id,
                            service_status: row.service_status,
                            persist_version: row.persist_version,
                            biz_type: row.biz_type
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
                        var rows = this.tableList;
                        var index = rows.indexOf(row);
                        if (this.pagCurrent < (this.pagTotal / this.pagSize)) { //存在下一页
                            this.$emit('getTableData', this.routerMessage);
                        } else {
                            if (rows.length == "1" && (this.routerMessage.params.page_num != 1)) { //是当前页最后一条
                                this.routerMessage.params.page_num--;
                                this.$emit('getTableData', this.routerMessage);
                            } else {
                                rows.splice(index, 1);
                                this.pagTotal--;
                            }
                        }
                        this.$message({
                            type: "success",
                            message: "撤回成功",
                            duration: 2000
                        })
                    }).catch(function (error) {
                        console.log(error);
                    })
                }).catch(() => {
                });
            },
            //查看工作流
            lookFlow:function(row){
                if(row.id){
                    this.$axios({
                            url:"/cfm/commProcess",
                            method:"post",
                            data:{
                                optype:"wfquery_wfdetail",
                                params:{
                                    id:row.base_id
                                }
                            }
                        }).then((result) =>{
                            if (result.data.error_msg) {
                                this.$message({
                                    type: "error",
                                    message: result.data.error_msg,
                                    duration: 2000
                                })
                                return;
                            }else{
                                let getData = result.data.data;
                                let define = getData.define;
                                this.createDialogData.workflow_name = getData.workflow_name;
                                this.createDialogData.reject_strategy = define.reject_strategy;
                                this.lookFlowDialogVisible = true;
                                //将数据传递给子组件
                                this.flowList = define;
                                this.isEmptyFlow = false;
                            }
                        })
                }
            },
            //关闭查看工作流弹框
            cancelLookFlow:function(){
                this.isEmptyFlow = true;
                this.lookFlowDialogVisible = false;
                this.flowList = {};
            },
            showRightFlow:function (row) {
                this.businessParams = {};
                this.businessParams.id = row.bill_id;
                this.businessParams.biz_type = row.biz_type;
                this.businessParams.type = 1;
                // if(!this.rightFlow){
                //     this.rightFlow = true;
                // }
                document.getElementById("showbox").style.right="0px";
            },
            closeRightFlow:function(){
                this.businessParams = {};
                document.getElementById("showbox").style.right="-500px";
            }
        },
        watch:{
            tableData: function (val,oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                let data = val.data;
                data.forEach(element => {
                    let historyList = element.history;
                    let hL = historyList.length;
                    element.history_name = hL ? historyList[hL-1].assignee : "";
                });
                this.tableList = data;
            }
        }
    }
</script>
