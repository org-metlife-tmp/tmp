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
        <!--<div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="7">
                        <el-form-item>
                            <el-col :span="11">
                                <el-date-picker type="date" placeholder="起始日期" v-model="searchData.date1" style="width: 100%;"></el-date-picker>
                            </el-col>
                            <el-col class="line" :span="1" style="text-align:center">-</el-col>
                            <el-col :span="11">
                                <el-date-picker type="date" placeholder="结束日期" v-model="searchData.date2" style="width: 100%;"></el-date-picker>
                            </el-col>
                        </el-form-item>
                    </el-col>
                    <el-col :span="getCurrentSearch" style="text-align:center">
                        <el-form-item>
                            <el-checkbox-group v-model="searchData.type" v-if="isPending">
                                <el-checkbox-button label="已保存" name="type"></el-checkbox-button>
                                <el-checkbox-button label="审批拒绝" name="type"></el-checkbox-button>
                            </el-checkbox-group>
                            <el-checkbox-group v-model="searchData.type" v-else>
                                <el-checkbox-button label="已提交" name="type"></el-checkbox-button>
                                <el-checkbox-button label="审批中" name="type"></el-checkbox-button>
                                <el-checkbox-button label="审批通过" name="type"></el-checkbox-button>
                                <el-checkbox-button label="已完结" name="type"></el-checkbox-button>
                            </el-checkbox-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="事由摘要">
                            <el-input v-model="searchData.name" placeholder="请输入关键字"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            &lt;!&ndash;<el-button type="primary" plain @click="" size="mini">清空</el-button>&ndash;&gt;
                            <el-button type="primary" plain @click="" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>-->
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="7" v-if="!isPending">
                        <el-form-item>
                            <el-col :span="11">
                                <el-date-picker type="date" placeholder="起始日期" v-model="searchData.date1" style="width: 100%;"></el-date-picker>
                            </el-col>
                            <el-col class="line" :span="1" style="text-align:center">-</el-col>
                            <el-col :span="11">
                                <el-date-picker type="date" placeholder="结束日期" v-model="searchData.date2" style="width: 100%;"></el-date-picker>
                            </el-col>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.name" placeholder="请输入事由摘要关键字"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <!--<el-button type="primary" plain @click="" size="mini">清空</el-button>-->
                            <el-button type="primary" plain @click="" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item style="margin-bottom:0px">
                            <el-checkbox-group v-model="searchData.type" v-if="isPending">
                                <el-checkbox label="待补录" name="type"></el-checkbox>
                                <el-checkbox label="已保存" name="type"></el-checkbox>
                                <el-checkbox label="审批拒绝" name="type"></el-checkbox>
                            </el-checkbox-group>
                            <el-checkbox-group v-model="searchData.type" v-else>
                                <el-checkbox label="已提交" name="type"></el-checkbox>
                                <el-checkbox label="审批中" name="type"></el-checkbox>
                                <el-checkbox label="审批通过" name="type"></el-checkbox>
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
                      size="mini">
                <el-table-column prop="apply_on" label="申请日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="memo" label="事由摘要" :show-overflow-tooltip="true"></el-table-column>
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
                                       @click="lookMessage(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="isPending">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeMatter(scope.row,scope.$index,tableList)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="!isPending">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookMatter(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="分发" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="!isPending && scope.row.service_status!=11">
                            <el-button size="mini" @click="distribute(scope.row)" class="distribute"></el-button>
                        </el-tooltip>
                        <el-tooltip content="办结" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="!isPending && scope.row.service_status!=11">
                            <el-button type="success" icon="el-icon-check" size="mini"
                                       @click="lookParticular(scope.row)"></el-button>
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
                    :page-sizes="[10, 50, 100, 500]"
                    :pager-count="5"
                    :current-page="pagCurrent">
            </el-pagination>
        </div>
        <!--补录申请弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="810px" title="销户信息补录申请"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">销户信息补录申请</h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="24" class="form-small-title">
                        <span></span>
                        <span>申请日期:</span>
                        <span>{{dialogData.apply_on}}</span>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="事由摘要">
                            <el-input v-model="dialogData.memo" :disabled="true" placeholder="请输入事由摘要(15字以内)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="事由说明">
                            <el-input v-model="dialogData.detail"
                                      :disabled="true"
                                      type="textarea" :rows="3"
                                      placeholder="请输入事由说明(100字以内)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" class="form-small-title">
                        <span></span>
                        <span>信息补录</span>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名">
                            <el-input v-model="dialogData.dept_name" placeholder="请输入账户号(必输)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称">
                            <el-input v-model="dialogData.dept_name" placeholder="请输入账户名称(必输)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属机构">
                            <el-input v-model="dialogData.org_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户法人">
                            <el-input v-model="dialogData.dept_name" placeholder="请输入账户法人(必输)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="开户行">
                            <el-input v-model="dialogData.dept_name"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行地址">
                            <el-input v-model="dialogData.dept_name" placeholder="请输入开户行地址(必输)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span=12 style="height:51px"></el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行联系人">
                            <el-input v-model="dialogData.dept_name" placeholder="请输入联系人(必输)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="联系地址">
                            <el-input v-model="dialogData.dept_name" placeholder="请输入联系地址(必输)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <el-input v-model="dialogData.dept_name"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户日期">
                            <el-date-picker
                                :disabled="true"
                                v-model="dialogData.apply_on"
                                format="yyyy-MM-dd"
                                type="date">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户属性">
                            <el-input v-model="dialogData.dept_name"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户用途">
                            <el-input v-model="dialogData.dept_name"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式">
                            <el-input v-model="dialogData.dept_name"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" class="form-small-title">
                        <span></span>
                        <span>备注与附件</span>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注"> 
                            <el-input type="textarea" v-model="dialogData.dept_name"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="">清 空</el-button>
                <el-button type="warning" size="mini" @click="">保 存</el-button>
                <el-button type="warning" size="mini" @click="">提 交</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "CloseAccountMessage",
        created: function () {
            this.$emit("transmitTitle", "销户信息补录");
            this.$emit("getTableData", this.routerMessage);
        },
        props:["isPending","tableData"],
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
                tableList:[],
                pagSize: 8,
                pagTotal: 1,
                pagCurrent: 1,
                formLabelWidth: "120px",
                dialogVisible:false,
                dialogData:{}
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
            //查看
            lookMessage:function(row){
                debugger;
                row.apply_on=row.apply_on?row.apply_on.split(" ")[0]:"";
                this.dialogVisible = true;
                this.dialogData = row;
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
            tableData: function (val, oldVal) { //props里的字段
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data;
            }
        }
    }
</script>
