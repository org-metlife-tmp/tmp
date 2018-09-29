<style scoped lang="less" type="text/less">
    #openAccountMatter {
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
        .search-setion {
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

        /*按钮样式*/
        .distribute,.withdraw{
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
        }
        /*按钮-设置状态*/
        .distribute {
            background-position: -440px -62px;
        }
        /*按钮-撤回*/
        .withdraw{
            background-position: -48px 0;
        }

        /*已处理查看分发人样式*/
        .dist-border {
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
        margin-top: -16px;
        .el-radio {
            display: block;
            margin-left: 30px;
            margin-bottom: 10px;
        }
    }
</style>
<style lang="less" type="text/less">
    #openAccountMatter {
        .el-dialog__wrapper {
            .el-dialog__body {
                height: 400px;
                overflow-y: scroll;
            }
        }
    }
</style>

<template>
    <div id="openAccountMatter">
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
                                <el-date-picker
                                        v-model="searchData.start_date"
                                        type="date"
                                        placeholder="起始日期"
                                        value-format="yyyy-MM-dd"
                                        style="width: 100%;">
                                </el-date-picker>
                            </el-col>
                            <el-col class="line" :span="1" style="text-align:center">-</el-col>
                            <el-col :span="11">
                                <el-date-picker
                                        v-model="searchData.end_date"
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
                            <el-input v-model="searchData.query_key" clearable placeholder="请输入事由摘要关键字"></el-input>
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
                        label="操作" width="110"
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
                                    :enterable="false" :open-delay="500"
                                    v-show="!isPending && (scope.row.service_status =='4')">
                            <el-button size="mini" @click="distMatter(scope.row)" class="distribute"></el-button>
                        </el-tooltip>
                        <el-tooltip content="办结" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-show="!isPending && (scope.row.service_status =='4')">
                            <el-button type="success" icon="el-icon-check" size="mini"
                                       @click="concludeMatter(scope.row)"></el-button>
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
        <!--待处理新增&修改弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="860px"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth">
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
                        <el-form-item label="事由摘要">
                            <el-input v-model="dialogData.memo" placeholder="请输入事由摘要(15字以内)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="事由说明">
                            <el-input v-model="dialogData.detail"
                                      type="textarea" :rows="3"
                                      placeholder="请输入事由说明(100字以内)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="银行大类">
                            <el-select v-model="dialogData.bank_type" placeholder="请选择银行大类"
                                       clearable filterable
                                       :filter-method="filterBankType"
                                       @visible-change="clearSearch"
                                       @change="bankIsSelect">
                                <el-option v-for="bankType in bankTypeList"
                                           :key="bankType.name"
                                           :label="bankType.display_name"
                                           :value="bankType.code">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" style="height:51px"></el-col>
                    <el-col :span="12">
                        <el-form-item label="地区">
                            <el-select v-model="dialogData.areaCode"
                                       filterable remote clearable
                                       placeholder="请输入地区关键字"
                                       :remote-method="getAreaList"
                                       :loading="loading"
                                       @change="bankIsSelect">
                                <el-option
                                        v-for="area in areaList"
                                        :key="area.name + '-' + area.top_super"
                                        :value="area.name + '-' + area.top_super">
                                    <span>{{ area.name }}</span><span style="margin-left:10px;color:#bbb">{{ area.top_super }}</span>
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行">
                            <el-select v-model="dialogData.bank_cnaps_code" placeholder="请选择银行"
                                       clearable filterable
                                       @visible-change="getBankList"
                                       :disabled="bankSelect">
                                <el-option v-for="bankType in bankList"
                                           :key="bankType.cnaps_code"
                                           :label="bankType.name"
                                           :value="bankType.cnaps_code">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户性质">
                            <el-select v-model="dialogData.acc_attr" placeholder="请选择账户性质"
                                       clearable>
                                <el-option v-for="(name,k) in attrList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户法人">
                            <el-input v-model="dialogData.lawfull_man"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式">
                            <el-select v-model="dialogData.interactive_mode" placeholder="请选择账户模式">
                                <el-option v-for="(name,k) in interList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户用途">
                            <el-select v-model="dialogData.acc_purpose" placeholder="请选择账户用途">
                                <el-option v-for="(name,k) in purposeList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <el-select v-model="dialogData.curr_id" placeholder="请选择币种"
                                       filterable>
                                <el-option v-for="currency in currencyList"
                                           :key="currency.id"
                                           :label="currency.name"
                                           :value="currency.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="存款类型">
                            <el-select v-model="dialogData.deposits_mode" filterable>
                                <el-option v-for="(name,k) in depositsList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="附件">
                            <Upload @currentFielList="setFileList"
                                    :emptyFileList="emptyFileList"
                                    :isPending="isPending"
                                    :fileMessage="fileMessage"
                                    :triggerFile="triggerFile"></Upload>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="subCurrent">保 存</el-button>
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
                   width="810px" title="新增"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">开户申请查看</h1>
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
                        <el-form-item label="开户行">
                            <el-input v-model="lookDialogData.bank_name" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户性质">
                            <el-input v-model="lookDialogData.acc_attr_name" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户法人">
                            <el-input v-model="lookDialogData.lawfull_man" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式">
                            <el-input v-model="interList[lookDialogData.interactive_mode]" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户用途">
                            <el-input v-model="lookDialogData.acc_attr_purpose" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <el-input v-model="lookDialogData.curr_name" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="存款类型">
                            <el-input v-model="depositsList[lookDialogData.deposits_mode]" :readonly="true"></el-input>
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
                :businessParams="businessParams"
            ></BusinessTracking>
        </el-dialog>
        <!--已处理分发弹框-->
        <el-dialog :visible.sync="distDialog"
                   width="600px" title="新增"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">开户申请分发</h1>
            <el-form :model="distDialogData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="编号">
                            <el-input v-model="distDialogData.service_serial_number" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="申请日期">
                            <el-input v-model="distDialogData.apply_on" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="事由摘要">
                            <el-input v-model="distDialogData.memo" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="分发人">
                            <el-select v-model="distDialogData.user_ids" multiple filterable placeholder="请选择">
                                <el-option
                                        v-for="item in distList"
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
                <el-button type="warning" size="mini" plain @click="distDialog = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="subDist">确 定</el-button>
            </span>
        </el-dialog>
        <!--已处理办结弹框-->
        <el-dialog :visible.sync="concludeDialog"
                   width="600px" title="新增"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">开户申请办结</h1>
            <el-form :model="concludeDialogData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="编号">
                            <el-input v-model="concludeDialogData.service_serial_number" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="申请日期">
                            <el-input v-model="concludeDialogData.apply_on" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="事由摘要">
                            <el-input v-model="concludeDialogData.memo" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <div class="split-form">
                            <h4>办结确认</h4>
                        </div>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注信息">
                            <el-input v-model="concludeDialogData.finally_memo"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="concludeDialog = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="subConclude">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";
    import BusinessTracking from "../publicModule/BusinessTracking.vue"
    export default {
        name: "OpenAccountMatter",
        created: function () {
            this.$emit("transmitTitle", "开户事项申请");
            this.$emit("getTableData", this.routerMessage);

            //查询分发人
            this.$axios({
                url: "/cfm/commProcess",
                method: "post",
                data: {
                    optype: "user_list"
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
                    this.distList = data;
                }
            })
        },
        mounted:function(){
            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
                this.bankTypeList = bankTypeList;
            }
            var bankAllTypeList = JSON.parse(window.sessionStorage.getItem("bankAllTypeList"));
            if(bankAllTypeList){
                this.bankAllTypeList = bankAllTypeList;
            }
            //币种
            var currencyList = JSON.parse(window.sessionStorage.getItem("selectCurrencyList"));
            if (currencyList) {
                this.currencyList = currencyList;
            }
            //账户性质&账户用途
            var catgList = JSON.parse(window.sessionStorage.getItem("catgList"));
            for (var i = 0; i < catgList.length; i++) {
                if (catgList[i].code == "acc_attr") {
                    this.attrList = catgList[i].items;
                }else if(catgList[i].code == "acc_purpose"){
                    this.purposeList = catgList[i].items;
                }
            }
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
        props: ["isPending", "tableData"],
        components: {
            Upload: Upload,
            BusinessTracking:BusinessTracking
        },
        data: function () {
            return {
                routerMessage: {
                    todo: {
                        optype: "openintent_todolist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    },
                    done: {
                        optype: "openintent_donelist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    }
                },
                searchData: { //搜索数据
                    service_status: []
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                currentMatter: {},
                dialogVisible: false, //待处理新增弹框数据
                dialogData: {
                    bank_type: "",
                    areaCode: "",
                    bank_cnaps_code: "",
                    acc_attr: "",
                    interactive_mode: "",
                    acc_purpose: "",
                    curr_id: "",
                    deposits_mode: "",
                    files: []
                },
                formLabelWidth: "120px",
                dialogTitle: "新增",
                innerVisible: false, //提交弹出框
                lookDialog: false, //已处理查看弹出框
                lookDialogData: {},
                distDialog: false, //已处理分发弹出框
                distDialogData: {
                    user_ids: []
                },
                distList: [], //分发人数据
                currentDoneMatter: {},
                issueList: [],
                concludeDialog: false, //已处理办结弹出框
                concludeDialogData: {},
                currentConclude: {},
                emptyFileList: [], //附件
                fileMessage: {
                    bill_id: "",
                    biz_type: 1
                },
                triggerFile: false,
                selectWorkflow: "", //流程选择
                workflows: [],
                workflowData: {},
                businessParams:{},//业务状态追踪参数
                currencyList:[],//币种
                interList:[],//账户模式
                attrList:[],//账户性质
                depositsList:[],//存款类型
                bankAllList: [], //银行大类全部
                bankAllTypeList: [], //银行大类全部(不重复)
                bankTypeList: [], //银行大类
                areaList: [], //地区
                loading: false, //地区加载状态
                bankList: [], //银行
                bankSelect: true, //银行可选控制
                purposeList:[],//账户用途
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
            queryData: function () {
                var searchData = this.searchData;
                for (var k in searchData) {
                    if (this.isPending) {
                        this.routerMessage.todo.params[k] = searchData[k];
                        this.routerMessage.todo.params.page_num = 1;
                    } else {
                        this.routerMessage.done.params[k] = searchData[k];
                        this.routerMessage.done.params.page_num = 1;
                    }
                }
                this.$emit("getTableData", this.routerMessage);
            },
            //获取当前用户部门和公司
            getDeptOrg: function () {
                var userUodp = this.$store.state.user.uodp;
                for (var i = 0; i < userUodp.length; i++) {
                    var item = userUodp[i];
                    if (item.is_default == "1") {
                        this.dialogData.dept_name = item.dept_name;
                        this.dialogData.org_name = item.org_name;
                        var curData = new Date();
                        this.dialogData.apply_on = curData.getFullYear() + "-" + (curData.getMonth() + 1) + "-" + curData.getDate();
                        break;
                    }
                }
            },
            //添加开户事项
            addAccountMatter: function () {
                this.dialogTitle = "新增";
                this.dialogVisible = true;
                this.bankSelect = true;
                //清空数据
                for (var k in this.dialogData) {
                    if (k == "files") {
                        this.dialogData[k] = [];
                    } else {
                        this.dialogData[k] = "";
                    }
                }
                this.fileMessage.bill_id = "";
                this.emptyFileList = [];
                //设置当前用户的部门和公司
                this.getDeptOrg();
            },
            //编辑当前事项申请
            editMerch: function (row) {
                //清空数据
                this.addAccountMatter();
                //设置弹框数据
                this.dialogTitle = "编辑";
                //查询详情
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "openintent_detail",
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
                        let data = result.data.data;
                        this.dialogData = data;
                        this.dialogData['deposits_mode'] = data['deposits_mode']+"";
                        this.dialogData['interactive_mode'] = data['interactive_mode']+"";
                        //设置银行大类和地区
                        this.dialogData.areaCode = data.city + "-" + data.province;
                        this.dialogData.area_code = data.area_code;
                        this.areaList = [];
                        this.areaList.push({
                            name: data.city,
                            top_super: data.province,
                            code: data.area_code
                        })
                        this.bankSelect = false;

                        //添加开户行下拉数据
                        if(row.bank_cnaps_code){
                            this.bankList = [];
                            this.$set(this.bankList, 0, {
                                cnaps_code: data.bank_cnaps_code,
                                name: data.bank_name
                            })
                        }
                    }
                })
                //获取附件列表
                this.fileMessage.bill_id = row.id;
                this.triggerFile = !this.triggerFile;
            },
            //提交当前修改或新增
            subCurrent: function () {
                var params = this.dialogData;
                var optype = "";
                if (!params.id) {
                    debugger;
                    optype = "openintent_add";
                    var area_code = this.dialogData.areaCode.split("-");
                    var areaList = this.areaList;
                    for(var i = 0; i < areaList.length; i++){
                        if(areaList[i].name == area_code[0]){
                            params.area_code = areaList[i].code;
                        }
                    }
                } else {
                    optype = "openintent_chg";
                }
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: optype,
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
                        if (!params.id) {
                            // if (this.tableList.length < this.routerMessage.todo.params.page_size) {
                            //     this.tableList.push(data);
                            // }
                            // this.pagTotal++;
                            var message = "新增成功";
                        } else {
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
                }).catch(function (error) {
                    console.log(error);
                })

            },
            //删除当前事项申请
            removeMatter: function (row, index, rows) {
                this.$confirm('确认删除当前事项申请吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/normalProcess",
                        method: "post",
                        data: {
                            optype: "openintent_del",
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
                            message: "删除成功",
                            duration: 2000
                        })
                    }).catch(function (error) {
                        console.log(error);
                    })
                }).catch(() => {
                });
            },
            //点击页数 获取当前页数据
            getCurrentPage: function (currPage) {
                if (this.isPending) {
                    this.routerMessage.todo.params.page_num = currPage;
                } else {
                    this.routerMessage.done.params.page_num = currPage;
                }
                this.$emit("getTableData", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
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
            //已处理事项查看
            lookMatter: function (row) {
                this.businessParams = {};//清空数据
                this.businessParams.biz_type = 1;
                this.businessParams.id = row.id;
                this.lookDialog = true;
                for (var k in this.lookDialogData) {
                    this.lookDialogData[k] = "";
                }
                //查询详情
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "openintent_detail",
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
                        this.lookDialogData = data;
                        this.lookDialogData.user_name = row.user_name;
                    }
                })
                if (row.issue) {
                    this.issueList = row.issue.split(",");
                } else {
                    this.issueList = [];
                }
                //附件数据
                this.emptyFileList = [];
                this.fileMessage.bill_id = row.id;
                this.triggerFile = !this.triggerFile;
            },
            //已处理事项分发
            distMatter: function (row) {
                this.currentDoneMatter = row;
                this.distDialog = true;
                this.distDialogData.user_ids = [];
                for (var key in row) {
                    this.distDialogData[key] = row[key];
                }
            },
            //已处理事项撤回
            withdrawMatter: function(row){
                this.$confirm('确认撤回当前事项申请吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/normalProcess",
                        method: "post",
                        data: {
                            optype: "openintent_revoke",
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
            //已处理事项分发确定
            subDist: function () {
                var distData = this.distDialogData;
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "openintent_issue",
                        params: {
                            id: distData.id,
                            iss_users: distData.user_ids
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
                        var data = result.data.data;
                        this.distDialog = false;
                        this.currentDoneMatter.issue = data.issue;
                        this.$message({
                            type: "success",
                            message: "分发成功",
                            duration: 2000
                        })
                    }
                })
            },
            //已处理事项办结
            concludeMatter: function (row) {
                this.concludeDialog = true;
                this.currentConclude = row;
                for (var k in row) {
                    this.concludeDialogData[k] = row[k];
                }
            },
            //已处理事项办结确定
            subConclude: function () {
                var concludeData = this.concludeDialogData;
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "openintent_finish",
                        params: {
                            id: concludeData.id,
                            persist_version: concludeData.persist_version,
                            finally_memo: concludeData.finally_memo
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
                        var data = result.data.data;
                        this.concludeDialog = false;
                        this.currentConclude.finally_memo = data.finally_memo;
                        this.currentConclude.service_status = data.service_status;
                        this.$message({
                            type: "success",
                            message: "办结成功",
                            duration: 2000
                        })
                    }
                })
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
                var params = this.dialogData;
                var area_code = this.dialogData.areaCode.split("-");
                var areaList = this.areaList;
                for(var i = 0; i < areaList.length; i++){
                    if(areaList[i].name == area_code[0]){
                        params.area_code = areaList[i].code;
                    }
                }

                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "openintent_presubmit",
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
                        this.selectWorkflow = "";
                        this.workflowData = data;
                        this.workflows = data.workflows;
                        this.dialogData.persist_version = data.persist_version;
                        this.dialogData.apply_on = data.apply_on;
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
                        optype: "openintent_submit",
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
            //提交审批流的弹框关闭的时候刷新列表
            beforeCloseDialog:function(){
                this.$emit('getTableData', this.routerMessage);
            },
            //银行大类搜索筛选
            filterBankType: function (value) {
                if (value && value.trim()) {
                    this.bankTypeList = this.bankAllList.filter(item => {
                        var chineseReg = /^[\u0391-\uFFE5]+$/; //判断是否为中文
                        var englishReg = /^[a-zA-Z]+$/; //判断是否为字母
                        var quanpinReg = /(a[io]?|ou?|e[inr]?|ang?|ng|[bmp](a[io]?|[aei]ng?|ei|ie?|ia[no]|o|u)|pou|me|m[io]u|[fw](a|[ae]ng?|ei|o|u)|fou|wai|[dt](a[io]?|an|e|[aeio]ng|ie?|ia[no]|ou|u[ino]?|uan)|dei|diu|[nl][gh]ei|[jqx](i(ao?|ang?|e|ng?|ong|u)?|u[en]?|uan)|([csz]h?|r)([ae]ng?|ao|e|i|ou|u[ino]?|uan)|[csz](ai?|ong)|[csz]h(ai?|uai|uang)|zei|[sz]hua|([cz]h|r)ong|y(ao?|[ai]ng?|e|i|ong|ou|u[en]?|uan))/; //判断是否为全拼

                        if (chineseReg.test(value)) {
                            return item.name.toLowerCase().indexOf(value.toLowerCase()) > -1;
                        } else if (englishReg.test(value)) {
                            if (quanpinReg.test(value)) {
                                return item.pinyin.toLowerCase().indexOf(value.toLowerCase()) > -1;
                            } else {
                                return item.jianpin.toLowerCase().indexOf(value.toLowerCase()) > -1;
                            }
                        }
                    });
                    this.bankTypeList = this.bankTypeList.filter((item,index,arr) => {
                        for(var i = index+1; i < arr.length; i++){
                            if(item.display_name == arr[i].display_name){
                                return false;
                            }
                        }
                        return true;
                    })
                } else {
                    this.bankTypeList = this.bankAllTypeList;
                }
            },
            //银行大类展开时重置数据
            clearSearch: function (val) {
                if (this.bankTypeList != this.bankAllList && val) {
                    this.bankTypeList = this.bankAllTypeList;
                }
            },
            //银行大类/地址变化后判断银行是否可选
            bankIsSelect: function (value) {
                this.bankList = [];
                this.dialogData.bank_cnaps_code = "";
                if (this.dialogData.areaCode && this.dialogData.bank_type) {
                    this.bankSelect = false;
                } else {
                    this.bankSelect = true;
                }
            },
            //地区数据
            getAreaList: function (query) {
                if (query && query.trim()) {
                    this.loading = true;
                    this.$axios({
                        url: "/cfm/commProcess",
                        method: "post",
                        data: {
                            optype: "area_list",
                            params: {
                                query_key: query.trim()
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {

                        } else {
                            this.loading = false;
                            this.areaList = result.data.data;
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                } else {
                    this.areaList = [];
                }
            },
            //获取银行列表
            getBankList: function (status) {
                if (status) {
                    var area_code = this.dialogData.areaCode.split("-");
                    var bank_type = this.dialogData.bank_type;

                    this.$axios({
                        url: "/cfm/commProcess",
                        method: "post",
                        data: {
                            optype: "bank_list",
                            params: {
                                province: area_code[1],
                                city: area_code[0],
                                bank_type: bank_type
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {
                        } else {
                            this.bankList = result.data.data;
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }
            }
        },
        computed: {
            getCurrentSearch: function () {
                if (this.isPending) {
                    return 5;
                } else {
                    return 8;
                }
            }
        },
        watch: {
            isPending: function (val, oldVal) {
                for (var k in this.searchData) {
                    if (k == "service_status") {
                        this.searchData[k] = [];
                    } else {
                        this.searchData[k] = "";
                    }
                }
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
