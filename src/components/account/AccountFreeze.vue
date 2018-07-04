<style scoped lang="less" type="text/less">
    #accountFreeze{
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
</style>

<template>
    <div id="accountFreeze">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addAccountFreeze" v-show="isPending">新增</el-button>
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
                            <el-input v-model="searchData.query_key" placeholder="请输入关键字"></el-input>
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
                                       @click="editFreeze(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="isPending && scope.row.service_status!=12">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeFreeze(scope.row,scope.$index,tableList)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="!isPending">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookFreeze(scope.row)"></el-button>
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
                   width="810px" title="账户冻结申请"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
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
                    <el-col :span="12">
                        <el-form-item label="账户号">
                            <el-select v-model="dialogData.acc_no" @change="changeAccount" clearable :disabled="lookDisabled">
                                <el-option
                                v-for="item in accOptions"
                                :key="item.acc_no"
                                :label="item.acc_name"
                                :value="item.acc_no">
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
                        <el-form-item label="开户行">
                            <div class="height30">{{dialogData.bank_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <div class="height30">{{dialogData.curr_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span=12 style="height:51px"></el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式">
                            <div class="height30" v-text="getInactiveMode"></div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户用途">
                            <div class="height30">{{dialogData.acc_purpose}}</div>
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
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" @click="dialogVisible = false" :disabled="lookDisabled">取 消</el-button>
                <el-button type="warning" size="mini" @click="saveFreeze" :disabled="lookDisabled">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "AccountFreeze",
        created: function () {
            this.$emit("transmitTitle", "账户冻结申请");
            this.$emit("getTableData",this.routerMessage);

            this.$axios({
                url:"/cfm/normalProcess",
                method:"post",
                data:{
                    optype:"account_accs",
                    params:{
                        status:1
                    }
                }
            }).then((result) =>{
               this.accOptions = result.data.data;
            });
        },
        props:["isPending","tableData"],
        data: function () {
            return {
                routerMessage:{
                    todo:{
                        optype:"accfreeze_todolist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    },
                    done:{
                        optype:"accfreeze_donelist",
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
                dialogData:{},
                dialogTitle:"账户冻结申请",
                accOptions:[],//账户号下拉数据,
                currentFreeze:{}
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
            //新增冻结申请
            addAccountFreeze:function(){
                this.dialogData = {};
                this.currentFreeze = {};
                this.lookDisabled = false;
                this.dialogVisible = true;
            },
            //编辑
            editFreeze:function(row){
                //清空数据
                this.dialogData = {};
                this.dialogTitle = "账户冻结查看";
                this.dialogData = row;
                this.lookDisabled = false;
                this.dialogVisible = true;
                this.currentFreeze = row;

            },
            //切换账户号
            changeAccount:function(cur){
                var temp = this.dialogData;
                this.accOptions.forEach(function(item,index){
                    if(item.acc_no === cur){
                        temp.acc_name = item.acc_name;
                        temp.org_name = item.org_name;
                        temp.lawfull_man = item.lawfull_man;
                        temp.bank_name = item.bank_name;
                        temp.curr_name = item.curr_name;
                        temp.interactive_mode = item.interactive_mode;
                        temp.acc_purpose = item.acc_purpose;
                        temp.acc_id = item.acc_id;
                    }
                })
                
            },
            //删除冻结
            removeFreeze:function(row,index,rows){
                this.$confirm("确认删除当前事项申请吗","提示",{
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() =>{
                    this.$axios({
                        url:"/cfm/normalProcess",
                        method:"post",
                        data:{
                            optype:"accfreeze_tododel",
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
            saveFreeze:function(){
                var optype;
                var data = {
                    acc_id:this.dialogData.acc_id,
                    memo:this.dialogData.memo
                };
                if(this.currentFreeze.id == null || this.currentFreeze.id == "" || this.currentFreeze.id == undefined){
                    optype = "accfreeze_todoadd";
                }else{
                    optype = "accfreeze_todochg";
                    data.id = this.currentFreeze.id;
                }
                this.$axios({
                    url:"/cfm/normalProcess",
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
                        if(!this.currentFreeze.id){
                            if (this.tableList.length < this.routerMessage.todo.params.page_size) {
                                this.tableList.push(data);
                            }
                            this.pagTotal++;
                            var message = "新增成功";
                        }else{
                            var message = "修改成功"; 
                        }
            
                        this.dialogVisible = false;
                        this.$message({
                            type: 'success',
                            message: message,
                            duration: 2000
                        });
                    }
                    
                })
            },
            //查看已处理列表详情
            lookFreeze:function(row){
                this.dialogData = {};
                this.dialogTitle = "账户冻结";
                this.dialogData = row;
                this.lookDisabled = true;
                this.dialogVisible = true;
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
