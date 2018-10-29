<style scoped lang="less" type="text/less">
    #closeAccountMatter{
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

        /*数据展示区*/
        .table-content {
            height: 289px;
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }
        /*按钮-设置状态*/
        .distribute,.withdraw {
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
        }
        .distribute{
            background-position: -440px -62px;
        }
        /*按钮-撤回*/
        .withdraw{
            background-position: -48px 0;
        }
        /*已处理查看分发人样式*/
        .dist-border{
            border: 1px solid #ccc;
            border-radius: 22%;
            margin-right: 10px;
            padding: 0px 6px;
            float: left;
            line-height: 26px;
        }

        /*分割线*/
        .split-form {
            width: 100%;
            height: 26px;
            border-bottom: 1px solid #ccc;
            margin-bottom: 10px;
            h4 {
                margin: 0;
                float: left;
            }
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
    #closeAccountMatter {
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 400px;
                overflow-y: auto;
            }
        }
    }
</style>
<template>
    <div id="closeAccountMatter">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addAccountMatter" v-show="isPending">新增</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="7" v-if="!isPending">
                        <el-form-item>
                            <el-col :span="11">
                                <el-date-picker type="date" placeholder="起始日期" v-model="searchData.start_date" style="width: 100%;" value-format="yyyy-MM-dd"></el-date-picker>
                            </el-col>
                            <el-col class="line" :span="1" style="text-align:center">-</el-col>
                            <el-col :span="11">
                                <el-date-picker type="date" placeholder="结束日期" v-model="searchData.end_date" style="width: 100%;" value-format="yyyy-MM-dd"></el-date-picker>
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
                                <el-checkbox label="11" name="type">已完结</el-checkbox>
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
                <el-table-column prop="memo" label="事由摘要" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="service_status" label="业务状态"
                                 :formatter="transitionStatus"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="140"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="isPending">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editMerch(scope.row)"></el-button>
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
                                    :enterable="false" :open-delay="500" v-show="!isPending && scope.row.service_status ==4">
                            <el-button size="mini" @click="distribute(scope.row)" class="distribute"></el-button>
                        </el-tooltip>
                        <el-tooltip content="办结" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="!isPending && scope.row.service_status ==4">
                            <el-button type="success" icon="el-icon-check" size="mini"
                                       @click="concludeMatter(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="撤回" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-show="!isPending && (scope.row.service_status ==2)">
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
                   width="810px" title="新增"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth"
                     :rules="rules" ref="dialogForm">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="申请日期">
                            <el-input v-model="dialogData.apply_on" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span=12 style="height:51px"></el-col>
                    <el-col :span="12">
                        <el-form-item label="申请部门">
                            <el-input v-model="dialogData.dept_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属公司">
                            <el-input v-model="dialogData.org_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="事由摘要" prop="memo">
                            <el-input v-model="dialogData.memo" placeholder="请输入事由摘要(15字以内)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="事由说明" prop="detail">
                            <el-input v-model="dialogData.detail"
                                      type="textarea" :rows="3"
                                      placeholder="请输入事由说明(100字以内)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户号" prop="acc_id">
                            <el-select v-model="dialogData.acc_id" @change="changeAccount" clearable>
                                <el-option
                                    v-for="item in accOptions"
                                    :key="item.acc_no"
                                    :label="item.acc_no"
                                    :value="item.acc_id">
                                    <span>{{ item.acc_no }}</span>
                                    <span style="margin-left:10px;color:#bbb">{{ item.acc_name }}</span>
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span=12 style="height:51px"></el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称">
                            <div class="height30">{{dialogData.account_info.acc_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属机构">
                            <div class="height30">{{dialogData.account_info.org_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户法人">
                            <div class="height30">{{dialogData.account_info.lawfull_man}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <div class="height30">{{dialogData.account_info.curr_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="开户行">
                            <div class="height30">{{dialogData.account_info.bank_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式">
                            <div class="height30">{{interList[dialogData.account_info.interactive_mode]}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户用途">
                            <div class="height30">{{dialogData.account_info.acc_purpose_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户性质">
                            <div class="height30">{{dialogData.account_info.acc_attr_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="存款类型">
                            <div class="height30">{{depositsList[dialogData.account_info.deposits_mode]}}</div>
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
                v-show="dialogData.service_status==5"
                :businessParams="businessParams"
            ></BusinessTracking>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="subCurrent">确 定</el-button>
                <el-button type="warning" size="mini" @click="subFlow">提 交</el-button>
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
        <!--已处理查看弹出框-->
        <el-dialog :visible.sync="lookDialog"
                   width="810px" title="查看"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">销户申请查看</h1>
            <el-form :model="lookDialogData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="编号">
                            <el-input v-model="lookDialogData.service_serial_number" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span=12 style="height:51px"></el-col>
                    <el-col :span="12">
                        <el-form-item label="申请日期">
                            <el-input v-model="lookDialogData.apply_on" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="申请人">
                            <el-input v-model="lookDialogData.user_name" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="申请部门">
                            <el-input v-model="lookDialogData.dept_name" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属公司">
                            <el-input v-model="lookDialogData.org_name" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="分发人">
                            <ul>
                                <li v-for="item in issueList" class="dist-border">{{ item }}</li>
                            </ul>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="办结摘要">
                            <el-input v-model="lookDialogData.finally_memo"
                                      :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="事由摘要">
                            <el-input v-model="lookDialogData.memo"
                                      placeholder="请输入事由摘要(15字以内)"
                                      :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="事由说明">
                            <el-input v-model="lookDialogData.detail"
                                      type="textarea" :rows="3"
                                      :readonly="true"
                                      placeholder="请输入事由说明(100字以内)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户号">
                            <div class="height30">{{lookDialogData.account_info.acc_no}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span=12 style="height:51px"></el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称">
                            <div class="height30">{{lookDialogData.account_info.acc_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属机构">
                            <div class="height30">{{lookDialogData.account_info.org_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户法人">
                            <div class="height30">{{lookDialogData.account_info.lawfull_man}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <div class="height30">{{lookDialogData.account_info.curr_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="开户行">
                            <div class="height30">{{lookDialogData.account_info.bank_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式">
                            <div class="height30">{{interList[lookDialogData.account_info.interactive_mode]}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户用途">
                            <div class="height30">{{lookDialogData.account_info.acc_purpose_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户性质">
                            <div class="height30">{{lookDialogData.account_info.acc_attr_name}}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="存款类型">
                            <div class="height30">{{depositsList[lookDialogData.account_info.deposits_mode]}}</div>
                        </el-form-item>
                    </el-col>
                    <el-form-item label="附件">
                        <Upload @currentFielList="setFileList"
                                :emptyFileList="emptyFileList"
                                :fileMessage="fileMessage"
                                :triggerFile="triggerFile"
                                :isPending="isPending"></Upload>
                    </el-form-item>
                </el-row>
            </el-form>
            <BusinessTracking
                :businessParams="businessParams"
            ></BusinessTracking>
        </el-dialog>
        <!--分发弹出框-->
        <el-dialog :visible.sync="distributeDialogVisible"
                   width="600px" title="通用事项"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">通用事项</h1>
            <el-form :model="distributeData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="编号">
                            <el-input v-model="distributeData.service_serial_number" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="申请日期">
                            <el-date-picker
                                style="width:100%"
                                :disabled="true"
                                v-model="distributeData.apply_on"
                                format="yyyy-MM-dd"
                                type="date">
                            </el-date-picker>
                            <!-- <el-input v-model="distributeData.apply_on" :disabled="true"></el-input> -->
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="事由摘要">
                            <el-input v-model="distributeData.memo" :disabled="true" placeholder="请输入事由摘要(15字以内)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="分发人">
                            <el-select v-model="distributeData.user_ids" multiple filterable placeholder="请选择">
                                <el-option
                                    v-for="item in user_options"
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
                <el-button type="warning" size="mini" plain @click="distributeDialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="distriConfirm">确 定</el-button>
            </span>
        </el-dialog>
        <!--办结弹出框-->
        <el-dialog :visible.sync="handleDialogVisible"
                   width="600px" title="通用事项办结"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">通用事项办结</h1>
            <el-form :model="handleData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="编号">
                            <el-input v-model="handleData.service_serial_number" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="申请日期">
                            <el-date-picker
                                :disabled="true"
                                v-model="handleData.apply_on"
                                format="yyyy-MM-dd"
                                type="date">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span=12 style="height:51px"></el-col>
                    <el-col :span="24">
                        <el-form-item label="事由摘要">
                            <el-input v-model="handleData.memo" placeholder="请输入事由摘要(15字以内)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <div class="split-form">
                            <h4>办结确认</h4>
                        </div>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注信息">
                            <el-input v-model="handleData.finally_memo"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="handleDialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="handleConfirm">确 定</el-button>
            </span>
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
    import BusinessTracking from "../publicModule/BusinessTracking.vue";
    import WorkFlow from "../publicModule/WorkFlow.vue";
    export default {
        name: "CloseAccountMatter",
        created: function () {
            this.$emit("transmitTitle", "销户事项申请");
            this.$emit("getTableData", this.routerMessage);

            //查询分发人
            this.$axios({
                url:"/cfm/commProcess",
                method:"post",
                data:{
                    optype:"user_list"
                }
            }).then((result) =>{
                if (result.data.error_msg) {
                    this.$message({
                        type: "error",
                        message: result.data.error_msg,
                        duration: 2000
                    })
                    return;
                }
                if(result.data.data.length>0){
                    this.user_options = result.data.data;
                }
            })
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
            BusinessTracking:BusinessTracking,
            WorkFlow: WorkFlow
        },
        data: function () {
            return {
                routerMessage: {
                    todo:{
                        optype: "closeacc_todolist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    },
                    done:{
                        optype: "closeacc_donelist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    }
                },
                searchData:{
                    service_status:[]
                },
                user_options: [],
                tableList:[],
                pagSize: 8,//一页多少条数据
                pagTotal: 1,//共多少条数据
                pagCurrent: 1,//当前页码
                currentMatter: {},
                dialogVisible: false, //弹框数据
                distributeDialogVisible: false,//分发弹框
                handleDialogVisible: false,//办结弹框
                lookDialog: false,//已处理查看弹框
                dialogData: {
                    files: [],
                    account_info:{}
                },
                //校验规则设置
                rules: {
                    memo: {
                        required: true,
                        message: "请输入事由摘要",
                        trigger: "blur"
                    },
                    detail: {
                        required: true,
                        message: "请输入事由说明",
                        trigger: "blur"
                    },
                    acc_id: {
                        required: true,
                        message: "请选择账户号",
                        trigger: "change"
                    }
                },
                formLabelWidth: "120px",
                dialogTitle: "新增",
                distributeData: {},
                handleData: {},
                lookDialogData: {
                    account_info:{}
                },
                issueList:[],
                currentDoneMatter:{},
                emptyFileList: [], //附件
                fileMessage: {
                    bill_id: "",
                    biz_type: 6
                },
                triggerFile: false,
                innerVisible: false, //提交弹出框
                selectWorkflow: "", //流程选择
                workflows: [],
                workflowData: {},
                businessParams:{},//业务状态追踪参数
                accOptions:[],//账户号下拉数据,
                interList:[],//账户模式
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
            //根据条件查询数据
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
            //获取当前用户公司和部门
            getDeptOrg:function(){
                // var userUodp = JSON.parse(window.sessionStorage.getItem("user")).uodp;
                var userUodp = this.$store.state.user.uodp;
                for (var i = 0; i < userUodp.length; i++) {
                    var item = userUodp[i];
                    if (item.is_default == "1") {
                        this.dialogData.dept_id = item.dept_id;
                        this.dialogData.org_id = item.org_id;
                        this.dialogData.dept_name = item.dept_name;
                        this.dialogData.org_name = item.org_name;
                        var curData = new Date();
                        this.dialogData.apply_on = curData.getFullYear() + "-" + (curData.getMonth() + 1) + "-" + curData.getDate();
                        break;
                    }
                }
            },
            //添加销户事项申请
            addAccountMatter:function(){
                this.dialogTitle = "新增";
                this.dialogVisible = true;
                //清空数据
                var dialogData = this.dialogData;
                for (var k in dialogData) {
                    if(k == "files"){
                        dialogData[k] = [];
                    }else{
                        dialogData[k] = "";
                    }
                }
                //清空校验信息
                if (this.$refs.dialogForm) {
                    this.$refs.dialogForm.clearValidate();
                }
                this.fileMessage.bill_id = ""; //清空附件
                this.emptyFileList = [];
                //设置当前用户的公司和部门
                this.getDeptOrg();
                this.accOptions = [];
                this.$axios({
                    url:"/cfm/normalProcess",
                    method:"post",
                    data:{
                        optype:"account_accs",
                        params:{
                            status:1,
                            acc_id:""
                        }
                    }
                }).then((result) =>{
                    this.accOptions = result.data.data;
                });
            },
            //编辑当前事项申请
            editMerch:function(row){
                this.dialogTitle = "编辑";
                this.accOptions = [];

                //清空数据和校验信息
                var dialogData = this.dialogData;
                for(var k in dialogData){
                    if(k == "files"){
                        dialogData[k] = [];
                    }else{
                        dialogData[k] = "";
                    }
                }
                if (this.$refs.dialogForm) {
                    this.$refs.dialogForm.clearValidate();
                }
                this.currentMatter = row;//保存当前数据
                //设置当前用户的部门和公司
                this.getDeptOrg();
                //设置弹框数据
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "closeacc_detail",
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
                    } else {
                        let data = result.data.data
                        data.org_name = row.org_name;
                        data.dept_name = row.dept_name;
                        this.dialogData = data;
                        this.dialogVisible = true;
                    }
                })

                //获取附件列表
                this.fileMessage.bill_id = row.id;
                this.triggerFile = !this.triggerFile;
                this.$axios({
                    url:"/cfm/normalProcess",
                    method:"post",
                    data:{
                        optype:"account_accs",
                        params:{
                            status:1,
                            acc_id:row.acc_id
                        }
                    }
                }).then((result) =>{
                    this.accOptions = result.data.data;
                });
                //审批拒绝显示业务追踪
                if(row.service_status == 5){
                    this.businessParams = {};//清空数据
                    this.businessParams.biz_type = 6;
                    this.businessParams.id = row.id;
                }
            },
            //提交当前修改或新增
            subCurrent:function(){
                this.$refs.dialogForm.validate((valid, object) => {
                    if (valid) {
                        var params = this.dialogData;
                        var optype = "";
                        optype = params.id ? "closeacc_todochg" : "closeacc_todoadd";
                        this.$axios({
                            url:"/cfm/normalProcess",
                            method:"post",
                            data: {
                                optype: optype,
                                params: params
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
                                if(!params.id){
                                    // if (this.tableList.length < this.routerMessage.todo.params.page_size) {
                                    //     this.tableList.push(data);
                                    // }
                                    // this.pagTotal++;
                                    var message = "新增成功";
                                }else{
                                    // for (var k in data) {
                                    //     this.currentMatter[k] = data[k];
                                    // }
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
                        });
                    } else {
                        return false;
                    }
                });
            },
            //删除当前事项申请
            removeMatter:function(row,index,rows){
                this.$confirm('确认删除当前事项申请吗?','提示',{
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url:"/cfm/normalProcess",
                        method:"post",
                        data:{
                            optype:"closeacc_tododel",
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
            //分发
            distribute:function(row){
                this.distributeDialogVisible = true;
                //设置当前分发内容
                this.distributeData.apply_on = row.apply_on;
                this.distributeData.memo = row.memo;
                this.distributeData.service_serial_number = row.service_serial_number;
                this.distributeData.id = row.id;
                this.currentDoneMatter = row;
            },
            //确认分发
            distriConfirm:function(){
                this.$axios({
                    url:"/cfm/normalProcess",
                    method:"post",
                    data:{
                        optype:"closeacc_doneissue",
                        params:{
                            id:this.distributeData.id,
                            user_ids:this.distributeData.user_ids
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
                        var data = result.data.data;
                        this.distributeDialogVisible = false;
                        this.distributeData.user_ids = [];
                        this.currentDoneMatter.issues = data;
                        this.$message({
                            type: "success",
                            message: "分发成功",
                            duration: 2000
                        })
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //办结
            concludeMatter:function(row){
                this.currentMatter = row;
                this.handleDialogVisible = true;
                //设置当前办结内容
                this.handleData.apply_on = row.apply_on;
                this.handleData.memo = row.memo;
                this.handleData.service_serial_number = row.service_serial_number;
                this.handleData.id = row.id;
            },
            //办结确认
            handleConfirm:function(){
                this.$axios({
                    url:"/cfm/normalProcess",
                    method:"post",
                    data:{
                        optype:"closeacc_doneend",
                        params:{
                            id:this.handleData.id,
                            finally_memo:this.handleData.finally_memo
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
                    if(result.data.state == "ok"){
                       this.currentMatter.service_status = result.data.data;
                       this.handleDialogVisible = false;
                       this.handleDialogVisible.finally_memo = "";
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //已处理事项查看
            lookMatter:function(row){
                this.businessParams = {};//清空数据
                this.businessParams.biz_type = 6;
                this.businessParams.id = row.id;

                for(var k in this.lookDialogData){
                    this.lookDialogData[k] = "";
                }
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "closeacc_detail",
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
                    } else {
                        let data = result.data.data
                        data.org_name = row.org_name;
                        data.dept_name = row.dept_name;
                        data.user_name = row.user_name;
                        this.lookDialogData = data;
                        this.lookDialog = true;
                    }
                })
                if(row.issues){
                    this.issueList = row.issues.split(",");
                }else{
                    this.issueList = [];
                }
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
                            url: "/cfm/normalProcess",
                            method: "post",
                            data: {
                                optype: "closeacc_presubmit",
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
                        });
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
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "closeacc_submit",
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

                        // if(this.dialogTitle == "编辑"){
                        //     var rows = this.tableList;
                        //     var index = this.tableList.indexOf(this.currentMatter);
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
                        url: "/cfm/normalProcess",
                        method: "post",
                        data: {
                            optype: "closeacc_revoke",
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
            //切换账户号
            changeAccount:function(cur){
                this.dialogData.account_info = {};
                var temp = this.dialogData.account_info;
                var item = this.accOptions;
                for(let i=0;i<item.length;i++){
                    if(item[i].acc_id === cur){
                        var obj = item[i];
                        for(let j in obj){
                            temp[j] = obj[j];
                        }
                        break;
                    }
                }
            },
            //展示提交流程详情
            showFlowDialog:function(workflow){
                this.lookFlowDialogVisible = true;
                this.$axios({
                    url: "/cfm/commProcess",
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
