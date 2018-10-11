<style scoped lang="less" type="text/less">
    #closeAccountMessage{
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

        /*数据展示区*/
        .table-content {
            height: 289px;
        }

        /*按钮样式*/
        .withdraw{
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
        }
        /*按钮-撤回*/
        .withdraw{
            background-position: -48px 0;
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


        .height30 {
            height: 33px;
        }
        /*增加销户交易按钮*/
        .addSales{
            padding: 5px;
            margin-left: 10px;
        }
        .width40{
            width: 40%;
            float: left;
            margin-right: 5px;
            margin-bottom: 5px;
        }
    }
    .el-radio-group {
        // margin-top: -16px;
        .el-radio {
            display: block;
            margin-left: 30px;
            margin-bottom: 10px;
        }
    }
</style>
<style lang="less" type="text/less">
    #closeAccountMessage {
        .el-dialog__wrapper {
            .el-dialog__body {
                height: 400px;
                overflow-y: scroll;
            }
        }
    }
</style>

<template>
    <div id="closeAccountMessage">
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="7" v-if="!isPending">
                        <el-form-item>
                            <el-col :span="11">
                                <el-date-picker type="date" placeholder="起始日期" v-model="searchData.start_date" style="width: 100%;"></el-date-picker>
                            </el-col>
                            <el-col class="line" :span="1" style="text-align:center">-</el-col>
                            <el-col :span="11">
                                <el-date-picker type="date" placeholder="结束日期" v-model="searchData.end_date" style="width: 100%;"></el-date-picker>
                            </el-col>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" placeholder="请输入事由摘要关键字"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <!--<el-button type="primary" plain @click="" size="mini">清空</el-button>-->
                            <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item style="margin-bottom:0px">
                            <el-checkbox-group v-model="searchData.service_status" v-if="isPending">
                                <el-checkbox label="12" name="type">待补录</el-checkbox>
                                <el-checkbox label="1" name="type">已保存</el-checkbox>
                                <el-checkbox label="5" name="type">审批拒绝</el-checkbox>
                            </el-checkbox-group>
                            <el-checkbox-group v-model="searchData.service_status" v-else>
                                <el-checkbox label="2" name="type">已提交</el-checkbox>
                                <el-checkbox label="3" name="type">审批中</el-checkbox>
                                <el-checkbox label="4" name="type">审批通过</el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section :class="['table-content']">
            <el-table :data="tableList"
                      border
                      height="100%"
                      size="mini">
                <el-table-column prop="apply_on" label="申请日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="aci_memo" label="事由摘要" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="service_status" label="业务状态"
                                 :formatter="transitionStatus"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="110"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="isPending">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editMessage(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="isPending && scope.row.service_status!=12">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeMessage(scope.row,scope.$index,tableList)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="!isPending">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="editMessage(scope.row,'look')"></el-button>
                        </el-tooltip>
                        <el-tooltip content="撤回" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-show="!isPending && (scope.row.service_status =='2')">
                            <el-button size="mini" class="withdraw" @click="withdrawMatter(scope.row)"></el-button>
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
        <!--补录申请弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="810px" title="销户信息补录"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title" v-text="dialogTitle"></h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="24" class="form-small-title">
                        <span></span>
                        <span>申请日期:</span>
                        <span>{{dialogData.apply_on}}</span>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="编号">
                            <span>{{dialogData.service_serial_number}}</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" style="height:51px"></el-col>
                    <el-col :span="24">
                        <el-form-item label="事由摘要">
                            <span>{{dialogData.aci_memo}}</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="事由说明">
                            <span>{{dialogData.detail}}</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" class="form-small-title">
                        <span></span>
                        <span>信息补录</span>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户号">
                            <div class="height30">{{dialogData.acc_no}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称">
                            <div class="height30">{{dialogData.acc_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属机构">
                            <div class="height30">{{dialogData.org_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户法人">
                            <div class="height30">{{dialogData.lawfull_man}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="开户行">
                            <div class="height30">{{dialogData.bank_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <div class="height30">{{dialogData.curr_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式">
                            <div class="height30">{{interList[dialogData.interactive_mode]}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户用途">
                            <div class="height30">{{dialogData.acc_purpose_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户性质">
                            <div class="height30">{{dialogData.acc_attr_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="存款类型">
                            <div class="height30">{{depositsList[dialogData.deposits_mode]}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="销户交易">
                            <el-button class="addSales" type="primary" icon="el-icon-plus" circle
                                       :disabled="lookDisabled"
                                       @click="addSales(dialogData.additionals)"></el-button>
                            <template v-for="item in salesList">
                                <el-input class="width40" v-model="item.comments" :disabled="lookDisabled" placeholder="请输入交易摘要"></el-input>
                                <el-input class="width40" v-model="item.amount" :disabled="lookDisabled" placeholder="请输入交易金额"></el-input>
                            </template>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="销户时间">
                            <el-date-picker
                                :disabled="lookDisabled"
                                style="width:100%"
                                v-model="dialogData.close_date"
                                format="yyyy-MM-dd"
                                value-format="yyyy-MM-dd"
                                type="date">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" class="form-small-title">
                        <span></span>
                        <span>备注与附件</span>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注">
                            <el-input type="textarea" v-model="dialogData.memo" :disabled="lookDisabled"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="附件">
                            <Upload @currentFielList="setFileList"
                                    :emptyFileList="emptyFileList"
                                    :fileMessage="fileMessage"
                                    :triggerFile="triggerFile"
                                    :isPending="isPending"></Upload>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <BusinessTracking
                v-show="businessTrack"
                :businessParams="businessParams"
            ></BusinessTracking>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" @click="dialogVisible = false" v-show="!lookDisabled">取 消</el-button>
                <el-button type="warning" size="mini" @click="saveAppliation" v-show="!lookDisabled">确定</el-button>
                <el-button type="warning" size="mini" @click="subFlow" v-show="!lookDisabled">提 交</el-button>
            </span>
            <el-dialog :visible.sync="innerVisible"
                       width="50%" title="提交审批流程"
                       append-to-body top="76px"
                       @close="beforeCloseDialog"
                       :close-on-click-modal="false">
                <el-radio-group v-model="selectWorkflow">
                    <el-radio v-for="workflow in workflows"
                              :key="workflow.define_id"
                              :label="workflow.define_id"
                    >{{ workflow.workflow_name }}
                    </el-radio>
                </el-radio-group>
                <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="innerVisible = false">取 消</el-button>
                    <el-button type="warning" size="mini" @click="confirmWorkflow">确 定</el-button>
                </span>
            </el-dialog>
        </el-dialog>
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";
    import BusinessTracking from "../publicModule/BusinessTracking.vue"
    export default {
        name: "CloseAccountMessage",
        created: function () {
            this.$emit("transmitTitle", "销户信息补录");
            this.$emit("getTableData", this.routerMessage);

        },
        mounted: function () {
            //账户模式
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.InactiveMode) {
                this.interList = constants.InactiveMode;
            }
            //存款类型
            if (constants.DepositsMode) {
                this.depositsList = constants.DepositsMode;
            }
        },
        props:["isPending","tableData"],
        components: {
            Upload: Upload,
            BusinessTracking:BusinessTracking
        },
        data: function () {
            return {
                routerMessage: {
                    todo:{
                        optype: "closeacccomple_todolist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    },
                    done:{
                        optype: "closeacccomple_donelist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    }
                },
                searchData:{
                    service_status:[]
                },
                lookDisabled:false,
                tableList:[],
                pagSize: 7,
                pagTotal: 1,
                pagCurrent: 1,
                formLabelWidth: "120px",
                dialogVisible:false,
                dialogData:{
                    files: []
                },
                dialogTitle:"销户信息补录申请",
                currentMessage:{},
                accOptions:[],//账户号下拉数据,
                salesList:[{comments:"",amount:""}], //销户交易,
                emptyFileList: [], //附件
                fileMessage: {
                    bill_id: "",
                    biz_type: 7
                },
                triggerFile: false,
                innerVisible: false, //提交弹出框
                selectWorkflow: "", //流程选择
                workflows: [],
                workflowData: {},
                businessParams:{},//业务状态追踪参数
                businessTrack:false,
                interList:[],//账户模式
                depositsList:[],//存款类型
            }
        },
        methods: {
            //展示格式转换-业务状态
            transitionStatus: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.BillStatus) {
                    return constants.BillStatus[cellValue];
                }
            },
            //根据条件查询
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
            //编辑
            editMessage:function(row,type){
                this.businessTrack = false;
                //清空数据
                this.lookDisabled = false;
                this.dialogData = {close_date:"",interactive_mode:"",acc_no:""};
                this.salesList = [{comments:"",amount:""}];
                this.accOptions = [];
                this.emptyFileList = [];
                //带出原有值
                row.apply_on = row.apply_on?row.apply_on.split(" ")[0]:"";
                this.currentMessage = row;
                for(var i in row){
                    this.dialogData[i] = row[i];
                }
                // this.dialogData.apply_on = row.apply_on;
                // this.dialogData.aci_memo = row.aci_memo;
                // this.dialogData.relation_id = row.relation_id;
                // this.dialogData.service_serial_number = row.service_serial_number;
                // this.dialogData.detail = row.detail;
                // this.dialogData.id = row.id;
                if(type){
                    this.businessTrack = true;
                    this.businessParams = {};//清空数据
                    this.businessParams.biz_type = 7;
                    this.businessParams.id = row.id;
                }
                if(row.service_status != "12" && row.id){//修改
                    this.dialogTitle = type == 'look' ? '销户信息补录查看': '销户信息补录修改'
                    this.lookDisabled = type == 'look' ? true : false;

                    this.dialogData.close_date = row.close_date;
                    this.dialogData.memo = row.memo;
                    this.$axios({
                        url:"/cfm/normalProcess",
                        method:"post",
                        data:{
                            optype:"closeacccomple_detail",
                            params:{
                                id:row.id
                            }
                        }
                    }).then((result) =>{
                        if (result.data.error_msg) {
                            this.$message({
                                type: "error",
                                message: result.data.error_msg,
                                duration: 2000
                            })
                        } else {
                            var data = result.data.data;
                            Object.assign(this.dialogData,data);
                            //未刷新表格数据时，手动给当前数据赋值
                            this.salesList = data.additionals;
                            this.dialogVisible = true;
                        }
                    })
                    //获取附件列表
                    this.fileMessage.bill_id = row.id;
                    this.triggerFile = !this.triggerFile;

                }else{//待处理状态下为新增功能
                    this.dialogVisible = true;
                }
            },
            //添加销户交易
            addSales:function(){
                this.salesList.push({comments:"",amount:""});
            },
            //保存或修改申请
            saveAppliation:function(){
                var data = {};
                var optype = "";
                if(this.dialogData.id){//修改
                    data.id = this.dialogData.id;
                    optype = "closeacccomple_todochg";
                    data.files = this.dialogData.files;
                }else{
                    optype = "closeacccomple_todoadd";
                }
                data.relation_id = this.dialogData.relation_id;
                data.additionals = this.salesList;
                data.close_date = this.dialogData.close_date;
                data.acc_id = this.dialogData.acc_id;
                data.memo = this.dialogData.memo;
                data.apply_on = this.dialogData.apply_on;
                this.$axios({
                    url:"/cfm/normalProcess",
                    method:"post",
                    data:{
                        optype:optype,
                        params:data
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
                    //    this.currentMessage.service_status = result.data.data.service_status;
                    //    this.currentMessage.id = result.data.data.id;
                    //    this.currentMessage.close_date = result.data.data.close_date;
                    //    this.currentMessage.memo = result.data.data.memo;
                        this.$emit('getTableData', this.routerMessage);
                        this.dialogVisible = false;
                        this.dialogData = {};
                        this.$message({
                            type: 'success',
                            message: '保存成功',
                            duration: 2000
                        });
                    }
                })
            },
            //删除申请
            removeMessage:function(row,index,rows){
                this.$confirm("确认删除当前事项申请吗","提示",{
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() =>{
                    this.$axios({
                        url:"/cfm/normalProcess",
                        method:"post",
                        data:{
                            optype:"closeacccomple_tododel",
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
                            return;
                        }
                        this.$message({
                            type: "success",
                            message: "删除成功",
                            duration: 2000
                        })
                        this.$emit("getTableData", this.routerMessage);
                    }).catch(function(error){
                        console.log(error)
                    })
                }).catch(() => {

                });
            },
            //设置当前项上传附件
            setFileList: function($event){
                if($event.length > 0 && $event[0].biz_type == 7){
                    this.fileLength = $event.length;
                }
                if(this.isPending){
                    this.dialogData.files = [];
                    $event.forEach((item) => {
                        this.dialogData.files.push(item.id);
                    })
                }else{

                }
            },
            //提交审批流程
            subFlow: function () {
                this.dialogData.additionals = this.salesList;
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "closeacccomple_presubmit",
                        params: this.dialogData
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
                        this.selectWorkflow = "";
                        this.workflowData = data;
                        this.workflows = data.workflows;
                        this.dialogData.persist_version = data.persist_version;
                        this.dialogData.id = data.id;
                        this.innerVisible = true;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
             //审批流程弹框-确定
            confirmWorkflow: function(){
                var workflowData = this.workflowData;
                var params = {
                    define_id: this.selectWorkflow,
                    id: workflowData.id,
                    service_serial_number: workflowData.service_serial_number,
                    service_status: workflowData.service_status,
                    persist_version: workflowData.persist_version
                };
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "closeacccomple_submit",
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
                        this.innerVisible = false;
                        this.dialogVisible = false;

                        // var rows = this.tableList;
                        // var index = this.tableList.indexOf(this.currentMessage);
                        // if (this.pagCurrent < (this.pagTotal / this.pagSize)) { //存在下一页
                        //     this.$emit('getTableData', this.routerMessage);
                        // } else {
                        //     if (rows.length == "1" && (this.routerMessage.todo.params.page_num != 1)) { //是当前页最后一条
                        //         this.routerMessage.todo.params.page_num--;
                        //         this.$emit('getTableData', this.routerMessage);
                        //     } else {
                        //         rows.splice(index, 1);
                        //         this.pagTotal--;
                        //     }
                        // }
                        this.$message({
                            type: "success",
                            message: "操作成功",
                            duration: 2000
                        })
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //已处理事项撤回
            withdrawMatter:function(row){
                this.$confirm('确认撤回当前事项申请吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/normalProcess",
                        method: "post",
                        data: {
                            optype: "closeacccomple_revoke",
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
                        }

                        var rows = this.tableList;
                        var index = rows.indexOf(row);
                        if (this.pagCurrent < (this.pagTotal / this.pagSize)) { //存在下一页
                            this.$emit('getTableData', this.routerMessage);
                        } else {
                            if (rows.length == "1" && (this.routerMessage.todo.params.page_num != 1)) { //是当前页最后一条
                                this.routerMessage.todo.params.page_num--;
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
            //提交审批流的弹框关闭的时候刷新列表
            beforeCloseDialog:function(){
                this.$emit('getTableData', this.routerMessage);
            }
        },
        computed:{
            getCurrentSearch:function(){
                if(this.isPending){
                    return 5;
                }else{
                    return 8;
                }
            }
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
