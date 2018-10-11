<style scoped lang="less" type="text/less">
    #accountAlteration {
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

        //弹框
        .form-small-title {
            line-height: 16px;
            border-bottom: 1px solid #e3e3e3;
            padding-bottom: 6px;
            margin-bottom: 10px;

            span {
                display: inline-block;
                width: 4px;
                height: 16px;
                background-color: orange;
                margin-right: 6px;
                vertical-align: middle;
            }
        }
        .form-two-title {
            margin-bottom: 10px;
            span {
                display: inline-block;
                width: 49%;
                height: 16px;
                line-height: 16px;
                border-bottom: 1px solid #e3e3e3;
                padding-bottom: 6px;

                i {
                    display: inline-block;
                    width: 4px;
                    height: 16px;
                    background-color: orange;
                    margin-right: 6px;
                    vertical-align: middle;
                }
            }
            span:nth-child(1) {
                margin-right: 10px;
            }
        }
    }
    .el-radio-group {

        .el-radio {
            display: block;
            margin-left: 30px;
            margin-bottom: 10px;
        }
    }
</style>
<style lang="less" type="text/less">
    #accountAlteration {
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 400px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="accountAlteration">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addAlteration" v-show="isPending">新增</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="7" v-if="!isPending">
                        <el-form-item>
                            <el-col :span="11">
                                <el-date-picker type="date" placeholder="起始日期" v-model="searchData.start_date"
                                                style="width: 100%;"></el-date-picker>
                            </el-col>
                            <el-col class="line" :span="1" style="text-align:center">-</el-col>
                            <el-col :span="11">
                                <el-date-picker type="date" placeholder="结束日期" v-model="searchData.end_date"
                                                style="width: 100%;"></el-date-picker>
                            </el-col>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" placeholder="请输入事由摘要关键字" clearable></el-input>
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
                        label="操作" width="80"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="isPending">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editAlteration(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="isPending">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeAlterat(scope.row,scope.$index,tableList)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="!isPending">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookAlterat(scope.row)"></el-button>
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
                   width="860px" title="新增"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth"
                     :rules="rules" ref="dialogForm">
                <el-row>
                    <el-col :span="24" class="form-small-title"><span></span>申请日期</el-col>
                    <el-col :span="12">
                        <el-form-item label="账户号" prop="acc_id">
                            <el-select v-model="dialogData.acc_id" @change="changeAccId" clearable filterable>
                                <el-option
                                        v-for="item in accList"
                                        :key="item.acc_id"
                                        :label="item.acc_no"
                                        :value="item.acc_id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户时间">
                            <el-input v-model="dialogData.open_date" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" class="form-two-title">
                        <span><i></i>变更前信息</span>
                        <span><i></i>变更后信息</span>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称">
                            <el-input v-model="dialogData.acc_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称" prop="$1">
                            <el-input v-model="dialogData.$1" clearable></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属机构">
                            <el-input v-model="dialogData.org_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属机构" prop="$2">
                            <el-select v-model="dialogData.$2" placeholder="请选择所属机构"
                                       filterable clearable>
                                <el-option v-for="org in orgList"
                                           :key="org.org_id"
                                           :label="org.name"
                                           :value="org.org_id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行">
                            <el-input v-model="dialogData.bank_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行" prop="bankName">
                            <el-input v-model="dialogData.bankName" placeholder="请选择银行" clearable
                                      @focus="clearBankDialog">
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户法人">
                            <el-input v-model="dialogData.lawfull_man" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户法人" prop="$4">
                            <el-input v-model="dialogData.$4" clearable></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <el-input v-model="dialogData.curr_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种" prop="$5">
                            <el-select v-model="dialogData.$5" placeholder="请选择币种"
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
                        <el-form-item label="账户性质">
                            <el-input v-model="dialogData.acc_attr_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户性质" prop="$6">
                            <el-select v-model="dialogData.$6" placeholder="请选择账户性质"
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
                        <el-form-item label="账户模式">
                            <el-input v-model="getInteract" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式"  prop="$7">
                            <el-select v-model="dialogData.$7" placeholder="请选择账户模式"
                                       clearable>
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
                            <el-input v-model="dialogData.acc_purpose_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户用途"  prop="$8">
                            <el-select v-model="dialogData.$8" placeholder="请选择账户用途"
                                       clearable>
                                <el-option v-for="(name,k) in purposeList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" class="form-small-title"><span></span>备注与附件</el-col>
                    <el-col :span="24">
                        <el-form-item label="备注">
                            <el-input v-model="dialogData.memo"
                                      type="textarea" :rows="3"
                                      placeholder="请输入事由说明(100字以内)"></el-input>
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
                    </el-radio>
                </el-radio-group>
                <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="innerVisible = false">取 消</el-button>
                    <el-button type="warning" size="mini" @click="confirmWorkflow">确 定</el-button>
                </span>
            </el-dialog>
        </el-dialog>
        <!--已处理查看弹出框-->
        <el-dialog :visible.sync="lookDialogVisible"
                   width="860px" title="新增"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">详情</h1>
            <el-form :model="lookDialogData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="24" class="form-small-title"><span></span>申请日期</el-col>
                    <el-col :span="12">
                        <el-form-item label="账户号">
                            <el-input v-model="lookDialogData.acc_no" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户时间">
                            <el-input v-model="lookDialogData.open_date" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" class="form-two-title">
                        <span><i></i>变更前信息</span>
                        <span><i></i>变更后信息</span>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称">
                            <el-input v-model="lookDialogData.old_1" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称">
                            <el-input v-model="lookDialogData.$1" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属机构">
                            <el-input v-model="lookDialogData.old_2" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属机构">
                            <el-input v-model="lookDialogData.$2" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行">
                            <el-input v-model="lookDialogData.old_3" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行">
                            <el-input v-model="lookDialogData.$3" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户法人">
                            <el-input v-model="lookDialogData.old_4" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户法人">
                            <el-input v-model="lookDialogData.$4" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <el-input v-model="lookDialogData.old_5" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <el-input v-model="lookDialogData.$5" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户性质">
                            <el-input v-model="lookDialogData.old_6" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户性质">
                            <el-input v-model="lookDialogData.$6" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式">
                            <el-input v-model="lookDialogData.old_7" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式">
                            <el-input v-model="lookDialogData.$7" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户用途">
                            <el-input v-model="lookDialogData.old_8" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户用途">
                            <el-input v-model="lookDialogData.$8" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" class="form-small-title"><span></span>备注与附件</el-col>
                    <el-col :span="24">
                        <el-form-item label="备注">
                            <el-input v-model="lookDialogData.memo"
                                      type="textarea" :rows="3" :disabled="true"></el-input>
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
        <!--开户行选择弹框-->
        <el-dialog :visible.sync="bankDialogVisible"
                   width="40%" title="选择开户行"
                   top="140px" :close-on-click-modal="false">

            <el-form :model="bankDialogData" size="small">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="银行大类" :label-width="formLabelWidth">
                            <el-select v-model="bankDialogData.bank_type" placeholder="请选择银行大类"
                                       clearable filterable
                                       style="width:100%"
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
                    <el-col :span="24">
                        <el-form-item label="地区" :label-width="formLabelWidth">
                            <el-select v-model="bankDialogData.area"
                                       filterable remote clearable
                                       style="width:100%"
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
                    <el-col :span="24">
                        <el-form-item label="开户行" :label-width="formLabelWidth">
                            <el-select v-model="bankDialogData.bank_name" placeholder="请选择银行"
                                       clearable filterable style="width:100%"
                                       @visible-change="getBankList"
                                       @change="setCNAPS"
                                       :disabled="bankSelect">
                                <el-option v-for="bankType in bankList"
                                           :key="bankType.cnaps_code"
                                           :label="bankType.name"
                                           :value="bankType.name">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="CNAPS" :label-width="formLabelWidth">
                            <el-input v-model="bankDialogData.cnaps_code" readonly></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>

            <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="bankDialogVisible = false">取 消</el-button>
                    <el-button type="warning" size="mini" @click="confirmBank">确 定</el-button>
                </span>
        </el-dialog>
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";
    import BusinessTracking from "../publicModule/BusinessTracking.vue"
    export default {
        name: "AccountAlteration",
        created: function () {
            this.$emit("transmitTitle", "账户变更申请");
            this.$emit("getTableData", this.routerMessage);
        },
        mounted: function () {
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
            //所属机构
            var orgList = JSON.parse(window.sessionStorage.getItem("selectOrgList"));
            if (orgList) {
                this.orgList = orgList;
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
        },
        components: {
            Upload: Upload,
            BusinessTracking:BusinessTracking
        },
        props: ["isPending", "tableData"],
        data: function () {
            return {
                routerMessage: { //路由数据参数信息
                    todo: {
                        optype: "openchg_todolist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    },
                    done: {
                        optype: "openchg_donelist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    }
                },
                searchData: { //搜索区数据
                    service_status: []
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                dialogVisible: false, //弹框数据
                dialogData: {
                    acc_id: "",
                    open_date: "",
                    acc_name: "",
                    org_name: "",
                    bank_name: "",
                    lawfull_man: "",
                    curr_name: "",
                    acc_attr_name: "",
                    acc_purpose_name:"",
                    interactive_mode: "",
                    $1: "",
                    $2: "",
                    $3: "",
                    $4: "",
                    $5: "",
                    $6: "",
                    $7: "",
                    $8: "",
                    bankTypeName: "",
                    area: "",
                    files: []
                },
                //校验规则设置
                rules: {
                    acc_id: {
                        required: true,
                        message: "请选择账户号",
                        trigger: "change"
                    },
                    $1: {
                        required: true,
                        message: "请输入账户名称",
                        trigger: "blur"
                    },
                    $2: {
                        required: true,
                        message: "请选择所属机构",
                        trigger: "change"
                    },
                    bankName: {
                        required: true,
                        message: "请选择开户行",
                        trigger: "blur"
                    },
                    $4: {
                        required: true,
                        message: "请输入账户法人",
                        trigger: "blur"
                    },
                    $5: {
                        required: true,
                        message: "请选择币种",
                        trigger: "change"
                    },
                    $6: {
                        required: true,
                        message: "请选择账户性质",
                        trigger: "change"
                    },
                    $7: {
                        required: true,
                        message: "请选择账户模式",
                        trigger: "change"
                    },
                    $8: {
                        required: true,
                        message: "请选择账户用途",
                        trigger: "change"
                    }
                },
                formLabelWidth: "120px",
                dialogTitle: "新增",
                lookDialogVisible: false,
                lookDialogData: {
                    acc_no: "",
                    open_date: "",
                    acc_name: "",
                    org_name: "",
                    bank_name: "",
                    lawfull_man: "",
                    curr_name: "",
                    acc_attr_name: "",
                    acc_purpose_name:"",
                    interactive_mode: "",
                    $1: "",
                    $2: "",
                    $3: "",
                    $4: "",
                    $5: "",
                    $6: "",
                    $7: "",
                    $8: "",
                    bankTypeName: "",
                    area: ""
                },
                bankDialogVisible: false, //选择银行弹框
                bankDialogData: {},
                currentAltera: {}, //当前数据
                /*下拉框数据*/
                accList: [], //账户号
                orgList: [], //所属机构
                currencyList: [], //币种
                attrList: {}, //账户性质
                interList: {}, //账户模式
                bankAllList: [], //银行大类全部
                bankAllTypeList: [], //银行大类全部(不重复)
                bankTypeList: [], //银行大类
                areaList: [], //地区
                loading: false, //地区加载状态
                bankList: [], //银行
                bankSelect: true, //银行可选控制
                emptyFileList: [], //附件
                fileMessage: {
                    bill_id: "",
                    biz_type: 3
                },
                triggerFile: false,
                innerVisible: false, //提交弹出框
                selectWorkflow: "", //流程选择
                workflows: [],
                workflowData: {},
                businessParams:{},//业务状态追踪参数
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
            //新增
            addAlteration: function () {
                this.dialogTitle = "新增";
                this.dialogVisible = true;
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
                this.fileMessage.bill_id = "";
                this.emptyFileList = [];
                this.setAccsList({acc_id:""});
            },
            //编辑
            editAlteration: function (row) {
                this.dialogTitle = "编辑";
                this.dialogVisible = true;
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

                this.currentAltera = row;
                this.setAccsList(row);
                //获取附件列表
                this.fileMessage.bill_id = row.id;
                this.triggerFile = !this.triggerFile;
                //获取当前项详细数据
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "openchg_detail",
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
                        for (var key in data) {
                            //转换变更后数据
                            if(key == "change_content"){
                                var item = data[key];
                                for(var i = 0; i < item.length; i++){
                                    var current = item[i];
                                    //添加开户行下拉数据
                                    if(current.type == "3"){
                                        this.dialogData.bankName = current.new_value;
                                    }
                                    //存在id时为下拉框数据 赋值为id
                                    if(current.new_id){
                                        if(current.type == 2 || current.type == 5){ //后台此处id返回为字符串 下拉框为数字 转换格式
                                            dialogData["$"+current.type] = current.new_id * 1;
                                        }else{
                                            dialogData["$"+current.type] = current.new_id;
                                        }
                                    }else{
                                        dialogData["$"+current.type] = current.new_value;
                                    }
                                }
                            }else{
                                dialogData[key] = data[key];
                            }
                        }
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //提交当前新增或修改
            subCurrent: function () {
                this.$refs.dialogForm.validate((valid, object) => {
                    if (valid) {
                        var dialogData = this.dialogData;
                        //设置传递参数
                        var reg = /^\$.*/;
                        var paramsData = [];
                        for (var k in dialogData) {
                            if (reg.test(k)) {
                                var item = {
                                    type: k.slice(1, k.length),
                                    value: dialogData[k]
                                };
                                paramsData.push(item);
                            }
                        }
                        var optype = "";
                        var params = {
                            acc_id: dialogData.acc_id,
                            memo: dialogData.memo,
                            files: dialogData.files,
                            change_content: paramsData
                        };

                        if (!dialogData.id) {
                            optype = "openchg_add";
                        } else {
                            optype = "openchg_chg";
                            params.id = dialogData.id;
                            params.persist_version = dialogData.persist_version;
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
                                if(!dialogData.id){
                                    // if (this.tableList.length < this.routerMessage.todo.params.page_size) {
                                    //     this.tableList.push(data);
                                    // }
                                    // this.pagTotal++;
                                    var message = "新增成功"
                                }else{
                                    // for (var k in data) {
                                    //     this.currentAltera[k] = data[k];
                                    // }
                                    var message = "修改成功"
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
                    } else {
                        return false;
                    }
                });
            },
            //删除当前变更
            removeAlterat: function (row, index, rows) {
                this.$confirm('确认删除当前变更申请吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/normalProcess",
                        method: "post",
                        data: {
                            optype: "openchg_del",
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
            //查看
            lookAlterat: function(row){
                this.businessParams = {};//清空数据
                this.businessParams.biz_type = 3;
                this.businessParams.id = row.id;
                this.lookDialogVisible = true;
                var dialogData = this.lookDialogData;
                for(var k in dialogData){
                    dialogData[k] = "";
                }

                //附件数据
                this.emptyFileList = [];
                this.fileMessage.bill_id = row.id;
                this.triggerFile = !this.triggerFile;

                //当前项详细数据
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "openchg_detail",
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
                        var content = data.change_content;
                        var reflect = {
                            1:"acc_name",
                            2:"org_name",
                            3:"bank_name",
                            4:"lawfull_man",
                            5:"curr_name",
                            6:"acc_attr_name",
                            7:"interactive_mode",
                            8:"acc_attr_purpose"
                        }
                        for(var i = 0; i < content.length; i++){
                            var current = content[i];
                            var type = current.type;
                            data["$" + type] = current.new_value;
                            data["old_" + type] = current.old_value;
                            delete reflect[type];
                        }
                        var inactiveMode = JSON.parse(window.sessionStorage.getItem("constants")).InactiveMode;
                        //没变更的值
                        for (var key in reflect){
                            var field = reflect[key];//字段
                            var value = data[field];
                            if(field == 'interactive_mode'){//从外面取需要转值
                                value = inactiveMode[value];
                            }
                            data["$" + key] = "";
                            data["old_" + key] = value;
                        }
                        this.lookDialogData = data;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
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
            /*下拉框相关设置*/
            //设置账户号下拉数据
            setAccsList:function(row){
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "account_accs",
                        params: {
                            status: 1,
                            acc_id: row.acc_id
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
                        this.accList = data;
                    }
                });
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
                    });
                } else {
                    this.bankTypeList = this.bankAllTypeList;
                }
            },
            //银行大类展开时重置数据
            clearSearch: function (val) {
                if (this.bankTypeList != this.bankAllTypeList && val) {
                    this.bankTypeList = this.bankAllTypeList;
                }
            },
            //银行大类/地址变化后判断银行是否可选
            bankIsSelect: function (value) {
                this.bankList = [];
                if (this.bankDialogData.area && this.bankDialogData.bank_type) {
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
                    var area_code = this.bankDialogData.area.split("-");
                    var bank_type = this.bankDialogData.bank_type;

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
            },
            //设置CNPAS
            setCNAPS: function(value){
                var bankList = this.bankList;
                for(var i = 0; i < bankList.length; i++){
                    if(bankList[i].name == value){
                        this.bankDialogData.cnaps_code = bankList[i].cnaps_code;
                        return;
                    }
                }
            },
            //清空开户行选择弹框数据
            clearBankDialog: function(){
                this.bankDialogVisible = true;
                this.bankSelect = true;
                var bankDialogData = this.bankDialogData;
                for(var k in bankDialogData){
                    bankDialogData[k] = "";
                }
            },
            //设置开户行
            confirmBank: function(){
                this.dialogData.$3 = this.bankDialogData.cnaps_code;
                this.dialogData.bankName = this.bankDialogData.bank_name;
                this.bankDialogVisible = false;
                this.$refs.dialogForm.validateField("bankName");
            },
            //选择账户号带出相关数据
            changeAccId: function (val) {
                var accList = this.accList;
                for (var i = 0; i < accList.length; i++) {
                    var item = accList[i];
                    if (item.acc_id == val) {
                        for (var k in item) {
                            this.dialogData[k] = item[k];
                        }
                    }
                }
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
                        var reg = /^\$.*/;
                        var params = this.dialogData;
                        var paramsData = [];
                        for (var k in params) {
                            if (reg.test(k)) {
                                var item = {
                                    type: k.slice(1, k.length),
                                    value: params[k]
                                };
                                paramsData.push(item);
                            }
                        }
                        params.change_content = paramsData;
                        this.$axios({
                            url: "/cfm/normalProcess",
                            method: "post",
                            data: {
                                optype: "openchg_presubmit",
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
                        optype: "openchg_submit",
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
                        //     var index = this.tableList.indexOf(this.currentAltera);
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
                            optype: "openchg_revoke",
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
        computed: {
            getInteract: function () {
                var inactiveMode = JSON.parse(window.sessionStorage.getItem("constants")).InactiveMode;
                return inactiveMode[this.dialogData.interactive_mode];
            },
            getLookInteract: function () {
                var inactiveMode = JSON.parse(window.sessionStorage.getItem("constants")).InactiveMode;
                return inactiveMode[this.lookDialogData.interactive_mode];
            }
        },
        watch: {
            isPending: function (val, oldVal) {
                var searchData = this.searchData;
                for(var k in searchData){
                    if(k == "service_status"){
                        searchData[k] = [];
                    }else{
                        searchData[k] = "";
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
