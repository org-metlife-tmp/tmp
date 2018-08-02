<style scoped lang="less" type="text/less">
    #openAccountMessage {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

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

        //弹框附件联系按钮
        .upload-icon{
            display: inline-block;
            padding: 6px 0 0 6px;
            color: #ccc;
            cursor: pointer;

            i{
                display: inline-block;
                width: 24px;
                height: 24px;
                background: url(../../assets/icon_common.png) no-repeat -52px -477px;
                vertical-align: middle;
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
    #openAccountMessage {
        .el-dialog__wrapper {
            .el-dialog__body {
                height: 400px;
                overflow-y: scroll;
            }
        }
    }
</style>

<template>
    <div id="openAccountMessage">
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="7" v-if="!isPending">
                        <el-form-item>
                            <el-col :span="11">
                                <el-date-picker type="date" placeholder="起始日期"
                                                v-model="searchData.start_date"
                                                value-format="yyyy-MM-dd"
                                                style="width: 100%;"></el-date-picker>
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
                                <el-checkbox label="12" name="type">待处理</el-checkbox>
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
                <el-table-column prop="up_memo" label="事由摘要" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="memo" label="备注" :show-overflow-tooltip="true" v-if="!isPending"></el-table-column>
                <el-table-column prop="service_status" label="业务状态"
                                 :formatter="transitionStatus"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="80"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="isPending">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editMerch(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="showDele(scope.row.service_status)">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeMessage(scope.row,scope.$index,tableList)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="!isPending">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookMessage(scope.row)"></el-button>
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
        <!--待处理编辑弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="860px" title="销户信息补录申请"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">开户信息补录申请</h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="24" class="form-small-title">
                        <span></span>
                        <span>申请日期:</span>
                        <span>{{dialogData.apply_on}}</span>
                    </el-col>
                    <el-col :span="22">
                        <el-form-item label="事由摘要">
                            <el-input v-model="dialogData.up_memo" :disabled="true"
                                      placeholder="请输入事由摘要(15字以内)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2" style="height:52px">
                        <span class="upload-icon" @click="showRelationFile = !showRelationFile">
                            <i></i>{{ fileLength }}
                        </span>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="事由说明">
                            <el-input v-model="dialogData.detail"
                                      :disabled="true"
                                      type="textarea" :rows="3"
                                      placeholder="请输入事由说明(100字以内)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-show="showRelationFile">
                        <el-form-item label="附件">
                            <Upload @currentFielList="setFileList"
                                    :fileMessage="relationFile"
                                    :triggerFile="relationTrigger"
                                    :emptyFileList="emptyFileList"
                                    :isPending="false"></Upload>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" class="form-small-title">
                        <span></span>
                        <span>信息补录</span>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户号">
                            <el-input v-model="dialogData.acc_no"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称">
                            <el-input v-model="dialogData.acc_name"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属机构">
                            <el-input v-model="dialogData.org_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户法人">
                            <el-input v-model="dialogData.lawfull_man"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="18">
                        <el-form-item label="开户行" :label-width="formLabelWidth">
                            <el-col :span="14">
                                <el-select v-model="bankCorrelation.bankTypeName" placeholder="请选择银行大类"
                                           clearable filterable
                                           :filter-method="filterBankType"
                                           @visible-change="clearSearch"
                                           @change="bankIsSelect">
                                    <el-option v-for="bankType in bankTypeList"
                                               :key="bankType.name"
                                               :label="bankType.name"
                                               :value="bankType.code">
                                    </el-option>
                                </el-select>
                            </el-col>
                            <el-col :span="1" style="height:1px"></el-col>
                            <el-col :span="9">
                                <el-select v-model="bankCorrelation.area"
                                           filterable remote clearable
                                           placeholder="请输入地区关键字"
                                           :remote-method="getAreaList"
                                           :loading="loading"
                                           @change="bankIsSelect">
                                    <el-option
                                            v-for="area in areaList"
                                            :key="area.name"
                                            :label="area.name"
                                            :value="area.code">
                                    </el-option>
                                </el-select>
                            </el-col>
                        </el-form-item>
                    </el-col>
                    <el-col :span="18">
                        <el-form-item label=" " :label-width="formLabelWidth" prop="cnaps_code">
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
                        <el-form-item label="开户行地址">
                            <el-input v-model="dialogData.bank_address"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行联系人">
                            <el-input v-model="dialogData.bank_contact"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="联系电话">
                            <el-input v-model="dialogData.bank_contact_phone"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <el-select v-model="dialogData.curr_id" placeholder="请选择币种"
                                       filterable clearable>
                                <el-option v-for="currency in currencyList"
                                           :key="currency.id"
                                           :label="currency.name"
                                           :value="currency.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户日期">
                            <el-date-picker
                                    v-model="dialogData.open_date"
                                    placeholder="请选择日期"
                                    style="width:100%"
                                    format="yyyy-MM-dd"
                                    value-format="yyyy-MM-dd"
                                    type="date">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户属性">
                            <el-select v-model="dialogData.acc_attr" placeholder="请选择账户属性"
                                       filterable clearable>
                                <el-option v-for="attr in attrList"
                                           :key="attr.key"
                                           :label="attr.value"
                                           :value="attr.key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户用途">
                            <el-select v-model="dialogData.acc_purpose" placeholder="请选择账户用途"
                                       filterable clearable>
                                <el-option v-for="purpose in purposeList"
                                           :key="purpose.key"
                                           :label="purpose.value"
                                           :value="purpose.key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式">
                            <el-select v-model="dialogData.interactive_mode" placeholder="请选择账户模式"
                                       filterable clearable>
                                <el-option v-for="(name,k) in interList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" class="form-small-title">
                        <span></span>
                        <span>备注与附件</span>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注">
                            <el-input type="textarea" v-model="dialogData.memo"></el-input>
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
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" plain size="mini" @click="dialogVisible=false">取 消</el-button>
                <el-button type="warning" size="mini" @click="subTodoChange">确 定</el-button>
                <el-button type="warning" size="mini" @click="subFlow">提 交</el-button>
            </span>
            <el-dialog :visible.sync="innerVisible"
                       width="50%" title="提交审批流程"
                       append-to-body top="76px"
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
            <h1 slot="title" class="dialog-title">补录信息查看</h1>
            <el-form :model="lookDialogData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="24" class="form-small-title">
                        <span></span>
                        <span>申请日期:</span>
                        <span>{{lookDialogData.apply_on}}</span>
                    </el-col>
                    <el-col :span="22">
                        <el-form-item label="事由摘要">
                            <el-input v-model="lookDialogData.up_memo" :readonly="true"
                                      placeholder="请输入事由摘要(15字以内)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2" style="height:52px">
                        <span class="upload-icon" @click="showRelationFile = !showRelationFile">
                            <i></i>{{ fileLength }}
                        </span>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="事由说明">
                            <el-input v-model="lookDialogData.detail"
                                      :readonly="true"
                                      type="textarea" :rows="3"
                                      placeholder="请输入事由说明(100字以内)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" v-show="showRelationFile">
                        <el-form-item label="附件">
                            <Upload @currentFielList="setFileList"
                                    :fileMessage="relationFile"
                                    :triggerFile="relationTrigger"
                                    :emptyFileList="emptyFileList"
                                    :isPending="false"></Upload>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" class="form-small-title">
                        <span></span>
                        <span>信息补录</span>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户号">
                            <el-input v-model="lookDialogData.acc_no" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称">
                            <el-input v-model="lookDialogData.acc_name" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属机构">
                            <el-input v-model="lookDialogData.org_name" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户法人">
                            <el-input v-model="lookDialogData.lawfull_man" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="18">
                        <el-form-item label="开户行">
                            <el-col :span="14">
                                <el-select v-model="bankCorrelation.bankTypeName" placeholder="请选择银行大类"
                                           clearable filterable
                                           :disabled="true"
                                           :filter-method="filterBankType"
                                           @visible-change="clearSearch"
                                           @change="bankIsSelect">
                                    <el-option v-for="bankType in bankTypeList"
                                               :key="bankType.name"
                                               :label="bankType.name"
                                               :value="bankType.code">
                                    </el-option>
                                </el-select>
                            </el-col>
                            <el-col :span="1" style="height:1px"></el-col>
                            <el-col :span="9">
                                <el-select v-model="bankCorrelation.area"
                                           filterable remote clearable
                                           placeholder="请输入地区关键字"
                                           :remote-method="getAreaList"
                                           :loading="loading"
                                           :disabled="true"
                                           @change="bankIsSelect">
                                    <el-option
                                            v-for="area in areaList"
                                            :key="area.name"
                                            :label="area.name"
                                            :value="area.code">
                                    </el-option>
                                </el-select>
                            </el-col>
                        </el-form-item>
                    </el-col>
                    <el-col :span="18">
                        <el-form-item label=" " prop="cnaps_code">
                            <el-select v-model="lookDialogData.bank_cnaps_code" placeholder="请选择银行"
                                       clearable filterable
                                       :readonly="true"
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
                        <el-form-item label="开户行地址">
                            <el-input v-model="lookDialogData.bank_address" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行联系人">
                            <el-input v-model="lookDialogData.bank_contact" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="联系电话">
                            <el-input v-model="lookDialogData.bank_contact_phone" :readonly="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <el-select v-model="lookDialogData.curr_id" placeholder="请选择币种"
                                       :disabled="true" filterable clearable>
                                <el-option v-for="currency in currencyList"
                                           :key="currency.id"
                                           :label="currency.name"
                                           :value="currency.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户日期">
                            <el-date-picker
                                    v-model="lookDialogData.open_date"
                                    placeholder="请选择日期"
                                    style="width:100%"
                                    :readonly="true"
                                    format="yyyy-MM-dd"
                                    value-format="yyyy-MM-dd"
                                    type="date">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户属性">
                            <el-select v-model="lookDialogData.acc_attr" placeholder="请选择账户属性"
                                       :disabled="true" filterable clearable>
                                <el-option v-for="attr in attrList"
                                           :key="attr.key"
                                           :label="attr.value"
                                           :value="attr.key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户用途">
                            <el-select v-model="lookDialogData.acc_purpose" placeholder="请选择账户用途"
                                       :disabled="true" filterable clearable>
                                <el-option v-for="purpose in purposeList"
                                           :key="purpose.key"
                                           :label="purpose.value"
                                           :value="purpose.key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式">
                            <el-select v-model="lookDialogData.interactive_mode" placeholder="请选择账户模式"
                                       :disabled="true" filterable clearable>
                                <el-option v-for="(name,k) in interList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" class="form-small-title">
                        <span></span>
                        <span>备注与附件</span>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注">
                            <el-input type="textarea" v-model="lookDialogData.memo" :readonly="true"></el-input>
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
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" @click="lookDialog=false">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";

    export default {
        name: "OpenAccountMessage",
        created: function () {
            this.$emit("transmitTitle", "开户信息补录");
            this.$emit("getTableData", this.routerMessage);

            //获取账户属性下拉数据
            this.$axios({
                url: "/cfm/commProcess",
                method: "post",
                data: {
                    optype: "category_list",
                    params: {
                        code: "acc_attr"
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
                    this.attrList = data;
                }
            }).catch(function (error) {
                console.log(error);
            })

            //获取账户用途下拉数据
            this.$axios({
                url: "/cfm/commProcess",
                method: "post",
                data: {
                    optype: "category_list",
                    params: {
                        code: "acc_purpose"
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
                    this.purposeList = data;
                }
            }).catch(function (error) {
                console.log(error);
            })
        },
        mounted: function () {
            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
                this.bankTypeList = bankTypeList;
            }
            //币种
            var currencyList = JSON.parse(window.sessionStorage.getItem("selectCurrencyList"));
            if (currencyList) {
                this.currencyList = currencyList;
            }
            //账户模式
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.InactiveMode) {
                this.interList = constants.InactiveMode;
            }
        },
        props: ["isPending", "tableData"],
        components: {
            Upload: Upload
        },
        data: function () {
            return {
                routerMessage: {
                    todo: {
                        optype: "opencom_todolist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    },
                    done: {
                        optype: "opencom_donelist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    }
                },
                searchData: {
                    service_status: []
                },
                tableList: [],
                pagSize: 8,
                pagTotal: 1,
                pagCurrent: 1,
                dialogVisible: false,
                dialogData: {
                    interactive_mode: "",
                    bank_cnaps_code: "",
                    curr_id: "",
                    open_date: "",
                    acc_attr: "",
                    acc_purpose: "",
                    interactive_mode: "",
                    files: []
                },
                formLabelWidth: "120px",
                bankCorrelation: {},
                bankAllList: [], //银行大类全部
                bankTypeList: [], //银行大类
                loading: false, //地区加载数据时状态显示
                areaList: [], //地区
                bankSelect: true, //银行可选控制
                bankList: [], //银行
                currencyList: [], //币种
                interList: {}, //账户模式
                attrList: [], //账户属性
                purposeList: [], //账户用途
                currentTodo: {},
                lookDialog: false,
                lookDialogData: {},
                emptyFileList: [], //附件
                fileMessage: {
                    bill_id: "",
                    biz_type: 2
                },
                triggerFile: false,
                relationFile:{
                    bill_id: "",
                    biz_type: 2
                },
                relationTrigger: false,
                fileLength: 0,
                showRelationFile: false,
                innerVisible: false, //提交弹出框
                selectWorkflow: "", //流程选择
                workflows: [],
                workflowData: {}
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
            //删除按钮的显示控制
            showDele: function (status) {
                if (this.isPending && status != "12") {
                    return true;
                } else {
                    return false;
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
            //待处理-编辑
            editMerch: function (row) {
                this.dialogVisible = true;
                this.currentTodo = row;

                //清空弹框数据
                for (var k in this.dialogData) {
                    if(k == "files"){
                        this.dialogData[k] = [];
                    }else{
                        this.dialogData[k] = '';
                    }
                }
                for(var k in this.bankCorrelation){
                    this.bankCorrelation[k] = "";
                }
                this.bankSelect = true;
                this.emptyFileList = [];
                this.fileLength = 0;
                this.showRelationFile = false;

                //获取其事项申请的附件
                this.relationFile.bill_id = row.relation_id;
                this.relationTrigger = !this.relationTrigger;

                //设置显示数据
                if (!row.id) {
                    for (var k in row) {
                        this.dialogData[k] = row[k];
                    }
                } else {
                    //获取当前项详细信息
                    this.$axios({
                        url: "/cfm/normalProcess",
                        method: "post",
                        data: {
                            optype: "opencom_detail",
                            params: {
                                id: row.id
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
                            for (var k in row) {
                                this.dialogData[k] = row[k];
                            }
                            for (var key in data) {
                                if (key == "bank_cnaps_code") {
                                    this.$set(this.bankList, 0, {
                                        cnaps_code: data.bank_cnaps_code,
                                        name: data.bank_name
                                    })
                                }
                                if (key == "interactive_mode") {
                                    data[key] += "";
                                }
                                this.dialogData[key] = data[key];
                            }
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                    //获取附件列表
                    this.fileMessage.bill_id = row.id;
                    this.triggerFile = !this.triggerFile;
                }
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
                    })
                } else {
                    this.bankTypeList = this.bankAllList;
                }
            },
            //银行大类展开时重置数据
            clearSearch: function () {
                if (this.bankTypeList != this.bankAllList) {
                    this.bankTypeList = this.bankAllList;
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
            //银行大类/地址变化后判断银行是否可选
            bankIsSelect: function (value) {
                this.bankList = [];
                if (this.bankCorrelation.area && this.bankCorrelation.bankTypeName) {
                    this.bankSelect = false;
                } else {
                    this.bankSelect = true;
                }
            },
            //获取银行列表
            getBankList: function (status) {
                if (status) {
                    var area_code = this.bankCorrelation.area;
                    var bank_type = this.bankCorrelation.bankTypeName;

                    this.$axios({
                        url: "/cfm/commProcess",
                        method: "post",
                        data: {
                            optype: "bank_list",
                            params: {
                                area_code: area_code,
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
            },
            //代办修改-提交
            subTodoChange: function () {
                var dialogData = this.dialogData;
                var optype = "";
                if (dialogData.id) {
                    optype = "opencom_chg"
                } else {
                    optype = "opencom_add"
                }
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: optype,
                        params: dialogData
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
                        for(var k in data){
                            this.currentTodo[k] = data[k];
                        }
                        this.dialogVisible = false;
                        this.$message({
                            type: "success",
                            message: "修改成功",
                            duration: 2000
                        })
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //删除当前信息补录
            removeMessage:function (row, index, rows) {
                this.$confirm('确认删除当前事项申请吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/normalProcess",
                        method: "post",
                        data: {
                            optype: "opencom_del",
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
                        this.$message({
                            type: "success",
                            message: "删除成功",
                            duration: 2000
                        })
                        this.$emit("getTableData", this.routerMessage);
                    }).catch(function (error) {
                        console.log(error);
                    })
                }).catch(() => {
                });
            },
            //已处理事项查看
            lookMessage:function(row){
                this.lookDialog = true;
                for(var k in this.bankCorrelation){
                    this.bankCorrelation[k] = "";
                }
                for (var k in this.lookDialogData) {
                    this.lookDialogData[k] = '';
                }
                this.fileLength = 0;
                this.showRelationFile = false;
                this.bankSelect = true;

                //当前项详细信息
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "opencom_detail",
                        params: {
                            id: row.id
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
                        for (var k in row) {
                            this.lookDialogData[k] = row[k];
                        }
                        for (var key in data) {
                            if (key == "bank_cnaps_code") {
                                this.$set(this.bankList, 0, {
                                    cnaps_code: data.bank_cnaps_code,
                                    name: data.bank_name
                                })
                            }
                            if (key == "interactive_mode") {
                                data[key] += "";
                            }
                            this.lookDialogData[key] = data[key];
                        }
                    }
                }).catch(function (error) {
                    console.log(error);
                })
                //附件数据
                this.emptyFileList = [];
                //获取其事项申请的附件
                this.relationFile.bill_id = row.relation_id;
                this.relationTrigger = !this.relationTrigger;
                this.fileMessage.bill_id = row.id;
                this.triggerFile = !this.triggerFile;
            },
            //点击页数 获取当前页数据
            getCurrentPage: function (currPage) {
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
            //设置当前项上传附件
            setFileList: function($event){
                if($event.length > 0 && $event[0].biz_type == 2){
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
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "opencom_presubmit",
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
                        optype: "opencom_submit",
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

                        var rows = this.tableList;
                        var index = this.tableList.indexOf(this.currentTodo);
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
                            optype: "opencom_revoke",
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
            }
        },
        watch: {
            isPending: function (val, oldVal) {
                for(var k in this.searchData){
                    if(k == "service_status"){
                        this.searchData[k] = [];
                    }else{
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
