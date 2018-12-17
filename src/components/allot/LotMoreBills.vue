<style scoped lang="less" type="text/less">
    #lotMoreBills {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*顶部按钮*/
        .button-list-left {
            position: absolute;
            top: -56px;
            left: -21px;
        }

        /*搜索区*/
        .search-setion {
            text-align: left;

            .line {
                text-align: center;
            }

            /*时间控件*/
            .el-date-editor {
                width: 100%;
            }
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
        /*弹框表格-分页部分*/
        .inner-botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: 6px;
            left: 0;
            .el-pagination{
                text-align: center;
            }
        }

        /*汇总数据*/
        .allData {
            height: 36px;
            line-height: 36px;
            width: 100%;
            background-color: #F8F8F8;
            border: 1px solid #ebeef5;
            border-top: none;
            box-sizing: border-box;
            text-align: right;

            /*左侧按钮*/
            .btn-left {
                float: left;
                margin-left: 16px;

                .transmit-icon {
                    position: relative;
                    display: inline-block;
                    width: 16px;
                    height: 10px;
                    vertical-align: middle;
                    margin-right: 4px;

                    i {
                        position: absolute;
                        top: -5px;
                        left: -3px;
                        width: 18px;
                        height: 18px;
                        background: url(../../assets/icon_common.png) no-repeat;
                        background-position: -49px -80px;
                    }
                }
            }

            /*汇总数字*/
            .numText {
                color: #FF5800;
                margin-right: 10px;
            }
        }

        /*按钮样式*/
        .withdraw {
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
        }

        /*撤回按钮*/
        .withdraw {
            background-position: -48px 0;
        }

        /*编辑弹框*/
        .table-input {
            width: 100%;
            height: 100%;
            border: none;
            outline: none;
        }

        .el-radio-group {
            margin-top: 0;
            .el-radio {
                display: block;
                margin-left: 30px;
                margin-bottom: 10px;
            }
        }
        .table-content{
            height: 279px;
        }

        //查看弹出框
        .view-mode{
            position: absolute;
            margin: auto;
            top: 15px;
            left: 400px;
            height: 30px;
            line-height: 30px;
            text-align: center;
            .tabBtn{
                width: 20px;
                height: 20px;
                background-image: url(../../assets/icon_common.png);
                border: none;
                padding: 0;
                display: inline-block;
                background-repeat: no-repeat;
            }
            .total{
                background-position: -344px -2px
            }
            .detail{
                background-position: -393px -2px;
            }
            .viewSelect{
                .total{
                    background-position: -366px -2px;
                }
                .detail{
                    background-position: -420px -2px;
                }
            }
        }
        /*查看弹框*/
        .dialog-talbe {
            width: 100%;
            overflow: hidden;
            li {
                float: left;
                box-sizing: border-box;
                border-top: 1px solid #e2e2e2;
                height: 30px;
                line-height: 30px;
                width: 50%;
                display:flex;/*设为伸缩容器*/
                flex-flow:row;/*伸缩项目单行排列*/
                span{
                    display: inline-block;
                    padding: 0 5px;
                }
                .table-title{
                    width: 100px;
                    text-align: right;
                }
                .tab-content{
                    flex:1;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                }
                .font-red{
                    color: #fd7d2f;
                    font-weight: bold;
                }
                .money-box{
                    flex: 1;
                    display:flex;/*设为伸缩容器*/
                    flex-flow:row;/*伸缩项目单行排列*/
                    .num{
                        width: 100px;
                        text-align:right;;
                    }
                    .amount{
                        flex: 1;
                        text-align: center;
                    }
                }
            }
            .font-red{
                color: #fd7d2f;
            }
            .payTitle{
                    width: 100%;
                }
            .borderRight{
                border-right: 1px solid #e2e2e2;
            }
            .bgColor{
                background-color: #fafafa;
            }
        }
        .table-memo{
            height: 30px;
            line-height: 30px;
        }
        .memo-title{
            width: 60px;
            display: inline-block;
            padding: 0 5px;
            text-align: right;
        }
        .table-file{
            overflow: hidden;
            >*{
                float: left;
            }
        }
        #batchDeatil{
            .tab-content{
                height: 300px;
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
        .title-batchno{
            color: #ccc;
            font-size: 14px;
        }
    }
</style>
<style lang="less" type="text/less">
    #lotMoreBills {
        /*编辑弹框*/
        .table-select {

            .el-select {
                height: 95%;
                width: 100%;

                .el-input {
                    height: 100%;

                    input {
                        height: 94%;
                        padding-left: 0px;
                        border: none;
                    }
                }
            }
        }
        .el-button-group .el-button:not(:last-child) {
            // margin-right: -6px;
        }
        .el-dialog__wrapper {
            .el-dialog__body {
                height: 440px;
                overflow-y: auto;
            }
        }
        .el-form--inline .el-form-item{
            width: calc(100% - 10px);
            width: -moz-calc(100% - 10px);
            width: -webkit-calc(100% - 10px);
        }
        .el-form--inline .el-form-item__content{
            width: 100%;
        }
    }
</style>

<template>
    <div id="lotMoreBills">
        <!--顶部按钮-->
        <div class="button-list-left">
            <el-select v-model="searchData.pay_mode" placeholder="请选择付款方式"
                    filterable clearable size="mini" @change="queryByPayMode">
                <el-option v-for="(name,k) in payModeList"
                        :key="k"
                        :label="name"
                        :value="k">
                </el-option>
            </el-select>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="5">
                        <el-form-item>
                            <el-date-picker
                                    v-model="dateValue"
                                    type="daterange"
                                    range-separator="至"
                                    start-placeholder="开始日期"
                                    end-placeholder="结束日期"
                                    value-format="yyyy-MM-dd"
                                    size="mini" clearable
                                    unlink-panels
                                    :picker-options="pickerOptions"
                                    @change="">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.pay_query_key" clearable placeholder="请输入付款方名称或账号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.recv_query_key" clearable
                                      placeholder="请输入收款方名称或账号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item>
                            <el-col :span="11">
                                <el-input v-model="searchData.min" clearable placeholder="最小金额"></el-input>
                            </el-col>
                            <el-col class="line" :span="2">-</el-col>
                            <el-col :span="11">
                                <el-input v-model="searchData.max" clearable placeholder="最大金额"></el-input>
                            </el-col>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item style="margin-bottom:0px">
                            <el-checkbox-group v-model="searchData.service_status">
                                <el-checkbox v-for="(name,k) in statusList"
                                             :label="k" name="type" :key="k">
                                    {{ name }}
                                </el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      height="100%"
                      border size="mini">
                <el-table-column prop="pay_mode" label="付款方式" :show-overflow-tooltip="true"
                                 :formatter="transitPayMode"></el-table-column>
                <el-table-column prop="batchno" label="批次号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="total_num" label="总笔数" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="total_amount" label="总金额" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="success_num" label="成功笔数"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="success_amount" label="成功金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="service_status" label="批次状态" :show-overflow-tooltip="true"
                                 :formatter="transitStatus"></el-table-column>
                <el-table-column
                        label="操作" width="110"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.service_status != 1 && scope.row.service_status != 5">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookBill(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.service_status == 1 || scope.row.service_status == 5">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editBill(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="撤回" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.service_status == 2">
                            <el-button size="mini" class="withdraw"
                                       @click="withdrawBill(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.service_status == 1">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeBill(scope.row,scope.$index,tableList)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
            <div class="allData">
                <div class="btn-left">
                    <el-button type="warning" plain size="mini" @click="goMakeBill">制单</el-button>
                    <el-button type="warning" plain size="mini" @click="goPayment">支付处理</el-button>
                </div>
                <span>总金额：</span>
                <span v-text="total_amount" class="numText"></span>
                <span>成功金额：</span>
                <span v-text="success_amount" class="numText"></span>
            </div>
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
                    @current-change="getCurrentPage"
                    @size-change="sizeChange"
                    :current-page="pagCurrent">
            </el-pagination>
        </div>
        <!--查看弹出框-->
        <el-dialog title=""
                   :visible.sync="dialogVisible"
                   width="900px" top="76px"
                   :close-on-click-modal="false">
            <h1 slot="title"  class="dialog-title">
                <span v-text="dialogTitle"></span>
                <span class="title-batchno">[{{dialogData.batchno}}]</span>
            </h1>
            <div class="view-mode">
                <el-button-group>
                    <el-button size="mini" :class="{viewSelect:curTab}" round title="查看批次汇总" @click="changeTab(true)"><i class="tabBtn total"></i></el-button>
                    <el-button size="mini" :class="{viewSelect:!curTab}" round title="查看批次明细" @click="changeTab(false)"><i class="tabBtn detail"></i></el-button>
                </el-button-group>
            </div>
            <section id="batchSummary" v-show="curTab">
                <ul class="dialog-talbe">
                    <li class="borderRight bgColor">
                        <span class="table-title">总笔数</span>
                        <span class="tab-content">{{dialogData.total_num}}</span>
                    </li>
                    <li class="bgColor">
                        <span class="table-title">总金额</span>
                        <span class="tab-content font-red">{{dialogData.total_amount}}元</span>
                    </li>
                    <li>
                        <span class="table-title">付款账号</span>
                        <span class="tab-content" :title="dialogData.pay_account_no">{{dialogData.pay_account_no}}</span>
                    </li>
                    <li>
                        <span class="table-title">开户行</span>
                        <span class="tab-content" :title="dialogData.pay_account_bank">{{dialogData.pay_account_bank}}</span>
                    </li>
                    <li class="payTitle">
                        <span class="table-title">支付结果</span>
                    </li>
                    <li class="font-red">
                        <span class="table-title">已失败</span>
                        <div class="money-box">
                            <span class="num">{{dialogData.failed_num}}笔</span>
                            <span class="amount">{{dialogData.failed_amount}}元</span>
                        </div>
                    </li>
                    <li>
                        <span class="table-title">处理中</span>
                        <div class="money-box">
                            <span class="num">{{dialogData.process_num}}笔</span>
                            <span class="amount">{{dialogData.process_amount}}元</span>
                        </div>
                    </li>
                    <li>
                        <span class="table-title">已保存</span>
                        <div class="money-box">
                            <span class="num">{{dialogData.saved_num}}笔</span>
                            <span class="amount">{{dialogData.saved_amount}}元</span>
                        </div>
                    </li>
                    <li>
                        <span class="table-title">已作废</span>
                        <div class="money-box">
                            <span class="num">{{dialogData.cancel_num}}笔</span>
                            <span class="amount">{{dialogData.cancel_amount}}元</span>
                        </div>
                    </li>
                    <li>
                        <span class="table-title">已成功</span>
                        <div class="money-box">
                            <span class="num">{{dialogData.success_num}}笔</span>
                            <span class="amount">{{dialogData.success_amount}}元</span>
                        </div>
                    </li>
                    <li></li>
                </ul>
                <div class="table-memo">
                    <span class="memo-title">备注</span>
                    <span class="memo-content">{{dialogData.payment_summary}}</span>
                </div>
                <div class="table-file">
                    <span class="memo-title" style="height:60px;line-height:60px">附件</span>
                    <span class="memo-content" style="height:60px;padding-top:6px;overflow-y:auto">
                        <Upload :emptyFileList="emptyFileList"
                            :fileMessage="fileMessage"
                            :triggerFile="triggerFile"
                            :isPending="false"></Upload>
                    </span>
                </div>
                <BusinessTracking :businessParams="businessParams"></BusinessTracking>
            </section>
            <section id="batchDeatil" v-show="!curTab">
                <!--搜索区-->
                <div class="search-setion">
                    <el-form :inline="true" :model="searchDetailData" size="mini">
                        <el-row>
                            <el-col :span="6">
                                <el-form-item>
                                    <el-input v-model="searchDetailData.recv_query_key" clearable
                                            placeholder="请输入收款方名称或账号"></el-input>
                                </el-form-item>
                            </el-col>
                            <el-col :span="6">
                                <el-form-item>
                                    <el-col :span="11">
                                        <el-input v-model="searchDetailData.min" clearable placeholder="最小金额"></el-input>
                                    </el-col>
                                    <el-col class="line" :span="2">-</el-col>
                                    <el-col :span="11">
                                        <el-input v-model="searchDetailData.max" clearable placeholder="最大金额"></el-input>
                                    </el-col>
                                </el-form-item>
                            </el-col>
                            <el-col :span="2">
                                <el-form-item>
                                    <el-button type="primary" plain @click="queryDetailData" size="mini">搜索</el-button>
                                </el-form-item>
                            </el-col>
                        </el-row>
                    </el-form>
                </div>
                <section class="tab-content">
                    <el-table :data="detailTableList"
                            height="100%"
                            border size="mini">
                        <el-table-column prop="recv_account_name" label="收款户名" :show-overflow-tooltip="true"></el-table-column>
                        <el-table-column prop="recv_account_no" label="收款账号" :show-overflow-tooltip="true"></el-table-column>
                        <el-table-column prop="recv_account_bank" label="收款行" :show-overflow-tooltip="true"></el-table-column>
                        <el-table-column prop="payment_amount" label="金额" :show-overflow-tooltip="true"
                                        :formatter="transitAmount"></el-table-column>
                        <el-table-column prop="pay_status" label="业务状态" :show-overflow-tooltip="true"
                                        :formatter="transitStatus"></el-table-column>
                        <el-table-column prop="feed_back" label="反馈信息"
                                        :show-overflow-tooltip="true"></el-table-column>
                    </el-table>
                    <div class="allData">
                        <span>总笔数：</span>
                        <span v-text="detailTotal.total_num" class="numText"></span>
                        <span>总金额：</span>
                        <span v-text="detailTotal.total_amount" class="numText"></span>
                    </div>
                </section>
                <!--分页部分-->
                <div class="inner-botton-pag">
                    <el-pagination
                            background
                            layout="sizes, prev, pager, next, jumper"
                            :page-size="pagDeSize"
                            :total="pagDeTotal"
                            :page-sizes="[7, 50, 100, 500]"
                            :pager-count="5"
                            @current-change="getCurrentDePage"
                            @size-change="sizeDeChange"
                            :current-page="pagDeCurrent">
                    </el-pagination>
                </div>
            </section>

        </el-dialog>
        <!--编辑弹出框-->
        <el-dialog title="编辑调拨单"
                   :visible.sync="editVisible"
                   width="900px" top="76px"
                   :close-on-click-modal="false">
            <div class="serial-number">
                [编号:
                <span v-text="editDialogData.service_serial_number"></span>
                ]
            </div>
            <ul class="dialog-talbe">
                <li class="table-li-title">业务类型</li>
                <li class="table-li-content table-select">
                    <el-select v-model="editDialogData.biz_id" placeholder="请选择业务类型"
                               filterable clearable size="mini">
                        <el-option v-for="payItem in payStatList"
                                   :key="payItem.biz_id"
                                   :label="payItem.biz_name"
                                   :value="payItem.biz_id">
                        </el-option>
                    </el-select>
                </li>
                <li class="table-li-title">付款方式</li>
                <li class="table-li-content table-select">
                    <el-select v-model="editDialogData.pay_mode" placeholder="请选择付款方式"
                               filterable clearable size="mini">
                        <el-option v-for="(name,k) in payModeList"
                                   :key="k"
                                   :label="name"
                                   :value="k">
                        </el-option>
                    </el-select>
                </li>

                <li class="table-li-title">付款单位</li>
                <li class="table-li-content" v-text="editDialogData.pay_account_name"></li>
                <li class="table-li-title">收款单位</li>
                <li class="table-li-content" v-text="editDialogData.recv_account_name"></li>

                <li class="table-li-title">账号</li>
                <li class="table-li-content table-select">
                    <el-select v-model="editDialogData.pay_account_id"
                               clearable filterable remote
                               placeholder="请选择账号"
                               :loading="payLoading"
                               :remote-method="getPayList"
                               @change="selectNumber($event,'payNumber')"
                               @visible-change="selectVisible($event,'payNumber')"
                               @clear="clearSelect('payNumber')">
                        <el-option
                                v-for="payItem in payList"
                                :key="payItem.acc_id"
                                :label="payItem.acc_no"
                                :value="payItem.acc_id">
                        </el-option>
                    </el-select>
                </li>
                <li class="table-li-title">账号</li>
                <li class="table-li-content table-select">
                    <el-select v-model="editDialogData.recv_account_id"
                               clearable filterable remote
                               placeholder="请选择账号"
                               :loading="gatherLoading"
                               :remote-method="getGatherList"
                               @change="selectNumber($event,'gatherNumber')"
                               @visible-change="selectVisible($event,'gatherNumber')"
                               @clear="clearSelect('gatherNumber')">
                        <el-option
                                v-for="gatherItem in gatherList"
                                :key="gatherItem.acc_id"
                                :label="gatherItem.acc_no"
                                :value="gatherItem.acc_id">
                        </el-option>
                    </el-select>
                </li>

                <li class="table-li-title">开户行</li>
                <li class="table-li-content" :title="editDialogData.pay_account_bank" v-text="editDialogData.pay_account_bank"></li>
                <li class="table-li-title">开户行</li>
                <li class="table-li-content" :title="editDialogData.recv_account_bank" v-text="editDialogData.recv_account_bank"></li>

                <li class="table-li-title">调拨金额</li>
                <li class="table-li-content">
                    <input type="text" @blur="setMoney" class="table-input"
                           v-model="editDialogData.payment_amount">
                </li>
                <li class="table-li-title">大写</li>
                <li class="table-li-content" v-text="editDialogData.numText"></li>

                <li class="table-li-title">摘要</li>
                <li class="table-li-content table-two-row">
                    <input type="text" class="table-input" v-model="editDialogData.payment_summary">
                </li>

                <li class="table-li-title" style="height:60px;line-height:60px">附件</li>
                <li class="table-li-content table-two-row" style="height:60px;padding-top:6px;overflow-y:auto">
                    <Upload @currentFielList="setFileList"
                            :emptyFileList="editEmptyFile"
                            :isPending="true"
                            :fileMessage="fileMessage"
                            :triggerFile="eidttrigFile"></Upload>
                </li>
            </ul>
            <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" @click="saveBill">保 存</el-button>
                    <el-button type="warning" size="mini" @click="submitBill">提 交</el-button>
                </span>
        </el-dialog>
        <!--提交弹框-->
        <el-dialog :visible.sync="innerVisible"
                   width="50%" title="提交审批流程"
                   top="76px"
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
                    <el-button type="warning" size="mini" @click="submitFlow">确 定</el-button>
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
    import BusinessTracking from "../publicModule/BusinessTracking.vue"
    import WorkFlow from "../publicModule/WorkFlow.vue";

    export default {
        name: "LotMoreBills",
        created: function () {
            this.$emit("transmitTitle", "批量调拨制单-更多单据");
            this.$emit("getCommTable", this.routerMessage);

            //业务类型
            this.$axios({
                url: this.queryUrl + "commProcess",
                method:"post",
                data:{
                    optype:"biztype_biztypes",
                    params: {
                        p_id: 8
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
                    this.payStatList = data;
                }
            }).catch(function (error) {
                console.log(error);
            })
        },
        components: {
            Upload: Upload,
            BusinessTracking: BusinessTracking,
            WorkFlow: WorkFlow
        },
        mounted: function () {
            //付款方式
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if(constants.PayMode){
                var data = constants.PayMode;
                delete data['8'];//删除自动扣款
                this.payModeList = data;
                // this.payModeList = constants.PayMode;
            }
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "dbtbatch_list",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                searchData: { //搜索条件
                    pay_mode: "",
                    pay_query_key: "",
                    recv_query_key: "",
                    min: "",
                    max: "",
                    service_status: [],
                    start_date: "",
                    end_date: ""
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                pagDeSize: 8, //弹窗分页数据
                pagDeTotal: 1,
                pagDeCurrent: 1,
                //汇总数据
                total_amount: "",
                success_amount: "",
                dateValue: "", //时间选择
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
                statusList: {
                    1: "已保存",
                    2: "已提交",
                    3: "审批中",
                    4: "审批通过",
                    5: "审批拒绝",
                    7: "已成功",
                    8: "已失败",
                    10: "未完结",
                    11: "已完结"
                },
                dialogVisible: false, //弹框数据
                dialogData: {},
                currentBill: {},
                editVisible: false,
                innerVisible: false, //提交弹框
                selectWorkflow: "",
                workflows: [],
                editDialogData: {
                    pay_account_name: "",
                    recv_account_name: "",
                    pay_account_no: "",
                    recv_account_no: "",
                    pay_account_bank: "",
                    recv_account_bank: "",
                    payment_amount: "",
                    numText: "",
                    payment_summary: "",
                    pay_mode: "",
                    biz_id: ""
                },
                payLoading: false, //付款方数据
                payList: [],
                gatherLoading: false, //收款方数据
                gatherList: [],
                payModeList: {}, //下拉框数据
                payStatList: [],
                emptyFileList: [], //附件
                fileMessage: {
                    bill_id: "",
                    biz_type: 10
                },
                triggerFile: false,
                editEmptyFile: [],
                eidttrigFile: false,
                fileList: [],
                businessParams:{ //业务状态追踪参数
                },
                payStatList: [],
                dialogTitle:"批次汇总查看",
                curTab: true,//当前显示的tab
                detailTableList: [],//弹窗的表格
                detailTotal:{},//弹窗的表格汇总
                searchDetailData: {},//弹窗的表格的搜索条件
                flowList: {},//查看流程
                isEmptyFlow: false,//
                lookFlowDialogVisible: false,
            }
        },
        methods: {
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                searchData.start_date = this.dateValue ? this.dateValue[0] : "";
                searchData.end_date = this.dateValue ? this.dateValue[1] : "";

                for (var k in searchData) {
                    this.routerMessage.params[k] = searchData[k];
                }
                this.routerMessage.params.page_num = 1;
                this.$emit("getCommTable", this.routerMessage);
            },
            //根据付款方式查询
            queryByPayMode:function(val){
                var searchData = this.searchData;
                searchData.start_date = this.dateValue ? this.dateValue[0] : "";
                searchData.end_date = this.dateValue ? this.dateValue[1] : "";
                for (var k in searchData) {
                    if(k=='pay_mode'){
                        this.routerMessage.params[k] = val;
                    }else{
                         this.routerMessage.params[k] = searchData[k];
                    }
                }
                this.routerMessage.params.page_num = 1;
                this.$emit("getCommTable", this.routerMessage);
            },
            //换页后获取数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getCommTable", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.params.page_size = val;
                this.routerMessage.params.page_num = 1;
                this.$emit("getCommTable", this.routerMessage);
            },
            //根据条件查询数据(弹窗表格)
            queryDetailData: function () {
                this.searchDetailData.page_num = 1;
                this.getDetailTable(this.searchDetailData);
            },
            //换页后获取数据(弹窗表格)
            getCurrentDePage: function (currPage) {
                this.searchDetailData.page_num = currPage;
                this.getDetailTable(this.searchDetailData);
            },
            //当前页数据条数发生变化(弹窗表格)
            sizeDeChange: function (val) {
                this.searchDetailData.page_size = val;
                this.searchDetailData.page_num = 1;
                this.getDetailTable(this.searchDetailData);
            },
            //展示格式转换-处理状态
            transitStatus: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if(column.property == 'pay_status'){
                    if (constants.PayStatus) {
                        return constants.PayStatus[cellValue];
                    }
                }else{
                    if (constants.BillStatus) {
                        return constants.BillStatus[cellValue];
                    }
                }
            },
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //展示格式转换-付款方式
            transitPayMode: function (row, column, cellValue, index){
                return this.payModeList[cellValue];
            },
            //制单
            goMakeBill: function () {
                this.$router.push("/allot/lot-make-bill");
            },
            //支付处理
            goPayment: function () {
                this.$router.push("/allot/lot-payment");
            },
            //查看
            lookBill: function (row) {
                this.searchDetailData.batchno = row.batchno;
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "dbtbatch_viewbill",
                        params: {
                            batchno: row.batchno
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
                        data.cancel_amount = this.$common.transitSeparator(data.cancel_amount);
                        data.failed_amount = this.$common.transitSeparator(data.failed_amount);
                        data.process_amount = this.$common.transitSeparator(data.process_amount);
                        data.saved_amount = this.$common.transitSeparator(data.saved_amount);
                        data.success_amount = this.$common.transitSeparator(data.success_amount);
                        data.total_amount = this.$common.transitSeparator(data.total_amount);
                        this.dialogData = data;
                        this.dialogVisible = true;

                        this.getDetailTable({batchno:row.batchno});
                    }
                }).catch(function (error) {
                    console.log(error);
                });

                //附件数据
                this.emptyFileList = [];
                this.fileMessage.bill_id = row.id;
                this.triggerFile = !this.triggerFile;

                //业务状态跟踪
                this.businessParams = {};
                this.businessParams.biz_type = 10;
                this.businessParams.id = row.id;
            },
            getDetailTable: function (params) {
                params.page_size = params.page_size ? params.page_size : 7;
                params.page_num = params.page_num ? params.page_num : 1;
                this.$axios({
                    url:  this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "dbtbatch_detaillist",
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
                        var val = result.data;
                        var data = val.data;
                        this.detailTotal = val.ext;
                        this.pagDeSize = val.page_size;
                        this.pagDeTotal = val.total_line;
                        this.pagDeCurrent = val.page_num;
                        this.detailTableList = data;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //编辑
            editBill: function (row) {
                this.$router.push({
                    name:"LotMakeBill",
                    params:{
                        id: row.id,
                        batchno: row.batchno
                    }
                });
            },

            /*编辑弹框内功能*/
            //获取付款方账号
            getPayList: function (value) {
                this.payLoading = true;
                var billData = this.editDialogData;

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "dbt_payacclist",
                        params: {
                            query_key: value.trim(),
                            exclude_ids: billData.recv_account_id,
                            page_size: 10000,
                            page_num: 1
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {

                    } else {
                        this.payLoading = false;
                        this.payList = result.data.data;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //获取收款方账号
            getGatherList: function (value) {
                this.gatherLoading = true;
                var billData = this.editDialogData;

                this.$axios({
                    url:  this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "dbt_recvacclist",
                        params: {
                            query_key: value.trim(),
                            exclude_ids: billData.pay_account_id,
                            page_size: 10000,
                            page_num: 1
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {

                    } else {
                        this.gatherLoading = false;
                        this.gatherList = result.data.data;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //选择账号时设置户名和开户行
            selectNumber: function (value, target) {
                var payList = this.payList;
                var gatherList = this.gatherList;
                var billData = this.editDialogData;

                if (target == "payNumber") {
                    for (var i = 0; i < payList.length; i++) {
                        if (payList[i].acc_id == value) {
                            billData.pay_account_name = payList[i].acc_name;
                            billData.pay_account_bank = payList[i].bank_name;
                        }
                    }
                }
                if (target == "gatherNumber") {
                    for (var i = 0; i < gatherList.length; i++) {
                        if (gatherList[i].acc_id == value) {
                            billData.recv_account_name = gatherList[i].acc_name;
                            billData.recv_account_bank = gatherList[i].bank_name;
                        }
                    }
                }
            },
            //账号下拉框显示时更新数据
            selectVisible: function (curStatus, target) {
                if (curStatus) {
                    if (target == "payNumber") {
                        this.getPayList("");
                    } else {
                        this.getGatherList("");
                    }
                }
            },
            //清空账号时清空户名和开户行
            clearSelect: function (target) {
                var billData = this.editDialogData;
                if (target == "payNumber") {
                    billData.pay_account_name = "";
                    billData.pay_account_bank = "";
                }
                if (target == "gatherNumber") {
                    billData.recv_account_name = "";
                    billData.recv_account_bank = "";
                }
            },
            //输入金额后进行格式化
            setMoney: function () {
                var moneyNum = this.editDialogData.payment_amount.replace(/,/gi, "").trim();
                /*校验数据格式是否正确*/
                if (moneyNum == "") {
                    return;
                }
                if (isNaN(moneyNum)) {
                    this.$message({
                        type: "warning",
                        message: "请输入正确的金额",
                        duration: 2000
                    });
                    this.editDialogData.payment_amount = "";
                    return;
                }
                var verify = moneyNum.split(".");
                if (verify[0].length > 10) {
                    this.$message({
                        type: "warning",
                        message: "整数位不能超过10位数",
                        duration: 2000
                    });
                    verify[0] = verify[0].slice(0, 10);
                    moneyNum = verify.join(".");
                }
                if (verify[1] && verify[1].length > 2) {
                    this.$message({
                        type: "warning",
                        message: "小数点后只能保留两位小数",
                        duration: 2000
                    });
                    verify[1] = verify[1].slice(0, 2);
                    moneyNum = verify.join(".");
                }

                //设置数字部分格式
                this.editDialogData.payment_amount = this.$common.transitSeparator(moneyNum);
                //设置汉字
                this.editDialogData.numText = this.$common.transitText(moneyNum);
            },
            //保存
            saveBill: function () {
                var params = this.setParams();
                if (!params) {
                    return;
                }

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "dbt_chg",
                        params: params
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        });
                    } else {
                        var data = result.data.data;
                        data.pay_mode += "";

                        for (var k in data) {
                            this.currentBill[k] = data[k];
                        }
                        this.editVisible = false;
                        this.$message({
                            type: "success",
                            message: "保存成功",
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //提交
            submitBill: function () {
                var params = this.setParams();
                if (!params) {
                    return;
                }

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "dbt_presubmit",
                        params: params
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        });
                    } else {
                        var data = result.data.data;
                        //设置表单数据
                        data.pay_mode += "";

                        for (var k in data) {
                            this.currentBill[k] = data[k];
                        }
                        //设置弹框数据
                        this.selectWorkflow = "";
                        this.workflows = data.workflows;

                        this.innerVisible = true;
                        this.editVisible = false;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //提交流程
            submitFlow: function () {
                var workflowData = this.currentBill;
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
                        optype: "dbt_submit",
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
                        this.innerVisible = false;
                        this.$message({
                            type: "success",
                            message: "提交成功",
                            duration: 2000
                        });
                        this.$emit("getCommTable", this.routerMessage);
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //设置params
            setParams: function () {
                //校验数据是否完善 并设置发送给后台的数据
                var billData = this.editDialogData;
                var params = {
                    pay_account_id: "",
                    recv_account_id: "",
                    payment_amount: "",
                    pay_mode: "",
                    payment_summary: "",
                    files: [],
                    biz_id: ""
                }
                for (var k in params) {
                    if (k != "payment_summary" && k != "files" && !billData[k]) {
                        this.$message({
                            type: "warning",
                            message: "请完善单据信息",
                            duration: 2000
                        });
                        return false;
                    }
                    if (k == "payment_amount") {  //金额
                        params[k] = billData[k].split(",").join("");
                    } else if (k == "files") {  //附件
                        params[k] = this.fileList;
                    } else if(k == "biz_id"){
                        params[k] = billData[k];
                        var payStatList = this.payStatList;
                        for(var i = 0; i < payStatList.length; i++){
                            if(billData[k] == payStatList[i].biz_id){
                                params.biz_name = payStatList[i].biz_name;
                            }
                        }
                    } else {
                        params[k] = billData[k];
                    }
                }
                if (billData.id) {
                    params.id = billData.id;
                    params.persist_version = billData.persist_version;
                }
                return params;
            },

            /*附件*/
            //设置当前项上传附件
            setFileList: function ($event) {
                this.fileList = [];
                if ($event.length > 0) {
                    $event.forEach((item) => {
                        this.fileList.push(item.id);
                    })
                }
            },
            //切换弹框内tab
            changeTab: function (type) {
                this.dialogTitle = type ? '批次汇总查看' : '批次汇总明细';
                this.curTab = type;
            },
             //删除
            removeBill: function (row, index, rows) {
                this.$confirm('确认删除当前单据吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "dbtbatch_del",
                            params: {
                                id: row.id,
                                persist_version: row.version
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
                            if (this.pagCurrent < (this.pagTotal / this.pagSize)) { //存在下一页
                                this.$emit("getCommTable", this.routerMessage);
                            } else {
                                if (rows.length == "1" && (this.routerMessage.params.page_num != 1)) { //是当前页最后一条
                                    this.routerMessage.params.page_num--;
                                    this.$emit("getCommTable", this.routerMessage);
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
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }).catch(() => {
                });
            },
            //已处理撤回
            withdrawBill:function(row){
                this.$confirm('确认撤回当前事项申请吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "dbtbatch_revoke",
                            params: {
                                id: row.id,
                                persist_version: row.version,
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
                        this.$emit('getCommTable', this.routerMessage);

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
        watch: {
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.success_amount = val.success_amount;
                this.total_amount = val.total_amount;
                this.tableList = val.data;
            }
        }
    }
</script>

