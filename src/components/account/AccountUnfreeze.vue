<style scoped lang="less" type="text/less">
    #accountUnfreeze{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion{
            text-align: left;
        }

        /*顶部按钮*/
        .button-list-right {
            position: absolute;
            top: -60px;
            right: -18px;
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
    }
    .el-radio-group {
        // margin-top: -16px;
        .el-radio {
            display: block;
            margin-left: 30px;
            margin-bottom: 10px;
        }
    }
    //提交流程查看按钮
    .flow-tip-box{
        display: inline-block;
        width: 24px;
        height: 20px;
        vertical-align: middle;
        background-image: url(../../assets/icon_common.png);
        background-repeat: no-repeat;
        background-position: -410px -166px;
        cursor: pointer;
        z-index: 5;
        background-color: #fff;
        border: 0;
        padding: 0;
    }
</style>
<style lang="less" type="text/less">
    #accountUnfreeze {
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 400px;
                overflow-y: auto;
            }
        }
    }
</style>
<template>
    <div id="accountUnfreeze">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addAccountUnfreeze" v-show="isPending">新增</el-button>
        </div>
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
                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
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
                                       @click="editUnfreeze(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="isPending && scope.row.service_status!=12">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeUnfreeze(scope.row,scope.$index,tableList)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="!isPending">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookUnfreeze(scope.row)"></el-button>
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
        <!--待处理新增弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="810px" title="账户解冻申请"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth"
                     :rules="rules" ref="dialogForm">
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
                    <el-col :span="12">
                        <el-form-item label="账户号" prop="acc_no">
                            <el-select v-model="dialogData.acc_no" @change="changeAccount" clearable :disabled="lookDisabled">
                                <el-option
                                    v-for="item in accOptions"
                                    :key="item.acc_no"
                                    :label="item.acc_no"
                                    :value="item.acc_no">
                                    <span>{{ item.acc_no }}</span>
                                    <span style="margin-left:10px;color:#bbb">{{ item.acc_name }}</span>
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span=12 style="height:51px"></el-col>
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
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <div class="height30">{{dialogData.curr_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="开户行">
                            <div class="height30">{{dialogData.bank_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式">
                            <div class="height30" v-text="getInactiveMode"></div>
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
                    <el-col :span="24" class="form-small-title">
                        <span></span>
                        <span>备注与附件</span>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注" prop="memo">
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
                <el-button type="warning" size="mini" @click="saveUnfreeze" v-show="!lookDisabled">确 定</el-button>
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
                        <el-button class="flow-tip-box" @click="showFlowDialog(workflow)"></el-button>
                    </el-radio>
                </el-radio-group>
                <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="innerVisible = false">取 消</el-button>
                    <el-button type="warning" size="mini" @click="confirmWorkflow">确 定</el-button>
                </span>
            </el-dialog>
        </el-dialog>
        <!--查看工作流弹出框-->
        <el-dialog :visible.sync="lookFlowDialogVisible"
                   width="800px" title="查看流程"
                   :close-on-click-modal="false"
                   :before-close="cancelLookFlow"
                   top="120px">
            <WorkFlow
                    :flowList="flowList"
                    :isEmptyFlow="isEmptyFlow"
            ></WorkFlow>
        </el-dialog>
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";
    import BusinessTracking from "../publicModule/BusinessTracking.vue"
    import WorkFlow from "../publicModule/WorkFlow.vue";
    export default {
        name: "AccountUnfreeze",
        created: function () {
            this.$emit("transmitTitle", "账户解冻申请");
            this.$emit("getTableData",this.routerMessage);

        },
        mounted: function () {
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            //存款类型
            if (constants.DepositsMode) {
                this.depositsList = constants.DepositsMode;
            }
        },
        props:["isPending","tableData"],
        components: {
            Upload: Upload,
            BusinessTracking:BusinessTracking,
            WorkFlow: WorkFlow
        },
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage:{
                    todo:{
                        optype:"accdefreeze_todolist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    },
                    done:{
                        optype:"accdefreeze_donelist",
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
                pagSize: 7,
                pagTotal: 1,
                pagCurrent: 1,
                formLabelWidth: "120px",
                tableList:[],
                dialogVisible:false,
                dialogData:{
                    files: []
                },
                //校验规则设置
                rules: {
                    acc_no: {
                        required: true,
                        message: "请选择账户号",
                        trigger: "change"
                    },
                    memo: {
                        required: true,
                        message: "请输入备注",
                        trigger: "blur"
                    },
                },
                dialogTitle:"账户解冻申请",
                accOptions:[],//账户号下拉数据,
                currentUnfreeze:{},
                emptyFileList: [], //附件
                fileMessage: {
                    bill_id: "",
                    biz_type: 5
                },
                triggerFile: false,
                innerVisible: false, //提交弹出框
                selectWorkflow: "", //流程选择
                workflows: [],
                workflowData: {},
                businessParams:{},//业务状态追踪参数,
                businessTrack:false,
                depositsList:[],//存款类型
                flowList: {},//查看流程
                isEmptyFlow: false,//
                lookFlowDialogVisible: false,
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
                        this.routerMessage.todo.params.page_num = 1;

                    }else{
                        this.routerMessage.done.params[k] = searchData[k];
                        this.routerMessage.done.params.page_num = 1;
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
            //新增冻结申请
            addAccountUnfreeze:function(){
                this.businessTrack = false;
                this.dialogTitle = "账户解冻申请";
                this.dialogData = {};
                this.accOptions = [];
                this.currentUnfreeze = {};
                this.lookDisabled = false;
                this.dialogVisible = true;
                this.fileMessage.bill_id = "";
                this.emptyFileList = [];
                //清空校验信息
                if (this.$refs.dialogForm) {
                    this.$refs.dialogForm.clearValidate();
                }

                this.$axios({
                    url:this.queryUrl + "normalProcess",
                    method:"post",
                    data:{
                        optype:"account_accs",
                        params:{
                            status:3,
                            acc_id:""
                        }
                    }
                }).then((result) =>{
                    this.accOptions = result.data.data;
                });
            },
            //编辑
            editUnfreeze:function(row){
                this.businessTrack = false;
                //***遍历为弹框每个字段添加值 取消双向绑定
                //清空数据
                this.dialogData = {};
                this.accOptions = [];
                this.dialogTitle = "账户解冻查看";
                // for(var k in row){
                //     this.dialogData[k] = row[k];
                // }
                //清空校验信息
                if (this.$refs.dialogForm) {
                    this.$refs.dialogForm.clearValidate();
                }

                this.currentUnfreeze = row;
                this.$axios({
                    url:this.queryUrl + "normalProcess",
                    method:"post",
                    data:{
                        optype:"accdefreeze_detail",
                        params:{
                            status:3,
                            id:row.id
                        }
                    }
                }).then((result) =>{
                    this.dialogData = result.data.data;
                    this.lookDisabled = false;
                    this.dialogVisible = true;
                });

                //获取附件列表
                this.fileMessage.bill_id = row.id;
                this.triggerFile = !this.triggerFile;
                this.$axios({
                    url:this.queryUrl + "normalProcess",
                    method:"post",
                    data:{
                        optype:"account_accs",
                        params:{
                            status:3,
                            acc_id:row.acc_id
                        }
                    }
                }).then((result) =>{
                    this.accOptions = result.data.data;
                });

                //审批拒绝显示业务追踪
                if(row.service_status == 5){
                    this.businessTrack = true;
                    this.businessParams = {};//清空数据
                    this.businessParams.biz_type = 5;
                    this.businessParams.id = row.id;
                }
            },
            //切换账户号
            changeAccount:function(cur){
                var temp = this.dialogData;
                var item = this.accOptions;
                for(let i=0;i<item.length;i++){
                    if(item[i].acc_no === cur){
                        var obj = item[i];
                        for(let j in obj){
                            temp[j] = obj[j];
                        }
                        break;
                    }
                }
            },
            //删除冻结
            removeUnfreeze:function(row,index,rows){
                this.$confirm("确认删除当前事项申请吗","提示",{
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() =>{
                    this.$axios({
                        url:this.queryUrl + "normalProcess",
                        method:"post",
                        data:{
                            optype:"accdefreeze_tododel",
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
                        if(this.pagCurrent < (this.pagTotal/this.pagSize)){ //存在下一页
                            this.$emit('getTableData', this.routerMessage);
                        }else{
                            if(rows.length == "1" && this.routerMessage.todo.params.page_num!=1){ //是当前页最后一条
                                this.routerMessage.todo.params.page_num--;
                                this.$emit('getTableData', this.routerMessage);
                            }else{
                                rows.splice(index, 1);
                                this.pagTotal--;
                            }
                        }

                        this.$message({
                            type: "success",
                            message: "删除成功",
                            duration: 2000
                        })
                    }).catch(function(error){
                        console.log(error)
                    })
                }).catch(() => {

                });
            },
            //保存冻结
            saveUnfreeze:function(){
                this.$refs.dialogForm.validate((valid, object) => {
                    if (valid) {
                        var optype;
                        var data = {
                            acc_id:this.dialogData.acc_id,
                            memo:this.dialogData.memo,
                            files: this.dialogData.files
                        };
                        if(!this.dialogData.id){
                            optype = "accdefreeze_todoadd";
                        }else{
                            optype = "accdefreeze_todochg";
                            data.id = this.dialogData.id;
                        }
                        this.$axios({
                            url:this.queryUrl + "normalProcess",
                            method:"post",
                            data:{
                                optype:optype,
                                params:data
                            }
                        }).then((result) =>{
                            if(result.data.error_msg){
                                this.$message({
                                    type: "error",
                                    message: result.data.error_msg,
                                    duration: 2000
                                })
                            }else{
                                var data = result.data.data;
                                Object.assign(data,this.dialogData);
                                data.apply_on = data.apply_on ? data.apply_on.split(" ")[0] : '';
                                if(!this.dialogData.id){
                                    // if (this.tableList.length < this.routerMessage.todo.params.page_size) {
                                    //     this.tableList.push(data);
                                    // }
                                    // this.pagTotal++;
                                    var message = "新增成功";
                                }else{
                                    var message = "修改成功";
                                }
                                this.$emit('getTableData', this.routerMessage);
                                this.dialogVisible = false;
                                this.$message({
                                    type: 'success',
                                    message: message,
                                    duration: 2000
                                });
                            }

                        })
                    } else {
                        return false;
                    }
                });
            },
            //查看已处理列表详情
            lookUnfreeze:function(row){
                this.businessTrack = true;
                this.businessParams = {};//清空数据
                this.businessParams.biz_type = 5;
                this.businessParams.id = row.id;
                this.dialogData = {};
                this.dialogTitle = "账户解冻";
                this.dialogData = row;
                this.lookDisabled = true;
                this.dialogVisible = true;
                //附件数据
                this.emptyFileList = [];
                this.fileMessage.bill_id = row.id;
                this.triggerFile = !this.triggerFile;
            },
            //设置当前项上传附件
            setFileList: function ($event) {
                if (this.isPending) {
                    this.dialogData.files = [];
                    if ($event.length > 0) {
                        $event.forEach((item) => {
                            this.dialogData.files.push(item.id);
                        })
                    }
                } else {

                }
            },
            //提交审批流程
            subFlow: function () {
                this.$refs.dialogForm.validate((valid, object) => {
                    if (valid) {
                        this.$axios({
                            url: this.queryUrl + "normalProcess",
                            method: "post",
                            data: {
                                optype: "accdefreeze_presubmit",
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
                    } else {
                        return false;
                    }
                });
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
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "accdefreeze_submit",
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
                        // if(this.dialogTitle == "账户解冻查看"){
                        //     var rows = this.tableList;
                        //     var index = this.tableList.indexOf(this.currentUnfreeze);
                        //     if (this.pagCurrent < (this.pagTotal / this.pagSize)) { //存在下一页
                        //         this.$emit('getTableData', this.routerMessage);
                        //     } else {
                        //         if (rows.length == "1" && (this.routerMessage.todo.params.page_num != 1)) { //是当前页最后一条
                        //             this.routerMessage.todo.params.page_num--;
                        //             this.$emit('getTableData', this.routerMessage);
                        //         } else {
                        //             rows.splice(index, 1);
                        //             this.pagTotal--;
                        //         }
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
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "accdefreeze_revoke",
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
            },
            //展示提交流程详情
            showFlowDialog:function(workflow){
                this.lookFlowDialogVisible = true;
                this.$axios({
                    url: this.queryUrl + "commProcess",
                    method: "post",
                    data: {
                        optype: "wfquery_wfdetail",
                        params: {
                            id: workflow.id
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
                        let getData = result.data.data;
                        let define = getData.define;
                        //将数据传递给子组件
                        this.flowList = define;
                        this.isEmptyFlow = false;

                    }
                })
            },
            cancelLookFlow:function(){
                this.isEmptyFlow = true;
                this.lookFlowDialogVisible = false;
                this.flowList = {};
            }
        },
        computed:{
            getInactiveMode:function(){
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if(constants.InactiveMode && this.dialogData.interactive_mode){
                    return constants.InactiveMode[this.dialogData.interactive_mode];
                }else{
                    return "";
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
