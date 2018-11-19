<style scoped lang="less" type="text/less">
    #myExamineApprove{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        .approveTab{
            position: relative;
            height: 100%;
            .borderBom{
                height: 30px;
                border-bottom: 2px solid #e4e7ed;
            }
            .myTodo{
                margin-left: 15px;
                float: left;
                background: #98c9f1;
                border: 0;
                height: 30px;
                line-height: 30px;
                padding: 0 10px;
                color: #fff;
                border-radius: 5px 2px 0 0;
                vertical-align: top;
                cursor: pointer;
                position: relative;
                .tab-num{
                    font-style: normal;
                    font-size: 12px;
                }
            }
            .myTodo:after{
                content: " ";
                border-bottom: 30px solid #98c9f1;
                border-right: 10px solid transparent;
                position: absolute;
                right: -10px;
            }
        }
        .myTab{
            position: absolute;
            top: 30px;
            left: 0;
            width: 100%;
            text-align: inherit;
            height: 430px;
            .tab-num{
                color: #ccc;
                font-style: normal;
                font-size: 12px;
            }
        }
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
            height: 289px;
        }
        /*分隔栏*/
        .split-bar {
            width: 106%;
            height: 6px;
            margin-left: -20px;
            background-color: #E7E7E7;
            margin-bottom: 20px;
        }

        /*加签，同意*/
        .button-list{
            position: absolute;
            bottom: 40px;
            right: 20px;
        }
        .dialogTable{
            border: 1px solid #e2e2e2;
            box-sizing: border-box;
            border-right: 0;
            .el-row{
                >div{
                    border-right: 1px solid #e2e2e2;
                    border-bottom: 1px solid #e2e2e2;
                    padding: 3px 10px;
                    min-width: 60px;
                    white-space: nowrap;
                    text-overflow: ellipsis;
                    overflow: hidden;
                    min-height: 32px;
                }
            }
            .el-row:last-child{
                >div{
                   border-bottom: 0;
                }

            }
            .el-col:hover{
                background-color: #EEF7FE;
            }
            .height80{
                height: 80px;
            }
            .left{
                text-align: right;
                padding-right: 20px;
            }
            .center{
                text-align: center;
            }
            .left.height80{
                line-height: 80px;
            }
            .enclosure{
                padding: 0 10px;
                border-bottom: 1px solid #ccc;
            }
            .enclosureUp{
                position: relative;
                .left{
                    position: absolute;
                    top: 0;
                    left: 0;
                    right: 0;
                    bottom: 0;
                }
                .mw25{
                    margin-left: 25%;
                }
                .mw167{
                    margin-left: 16.66667%;
                }
                .file-list li{
                    margin-bottom: 0;
                }
                .textName{
                    display: flex;
                    justify-content: flex-end;
                    align-items: center;
                }
            }
        }

        /*----批量调拨，支付弹框样式*/
        //批量调拨查看弹出框
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
            >*{
                float: left;
            }
        }
        #batchDeatil{
            .tab-content{
                height: 292px;
            }
        }
        /*弹框表格-分页部分*/
        .inner-botton-pag {
            // position: absolute;
            width: 100%;
            height: 8%;
            // bottom: 56px;
            margin-top: 56px;
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
        .title-batchno{
            color: #ccc;
            font-size: 14px;
        }
        /*----批量调拨，支付弹框样式*/

        .blue{
            color: #409EFF;
        }
        .red{
            color: #f56c6c;
        }
        .green{
            color: #67c23a;
        }
        .agreeAdvice{
            margin: 10px 0 20px 6%;
        }
        .aomuntColor{
            color: #fc6e21;
        }
        .grey{
            color: #aaa;
        }
    }
</style>

<style lang="less">
    .myTab{
        .el-tabs__header {
            margin: 0;
            left: 140px;
            top: -38px;
            width:  -moz-calc(100% - 180px);
            width: -webkit-calc(100% - 180px);
            width: calc(100% - 180px);
        }
        .el-tabs__content {
            top: -18px;
            max-height: 360px;
        }
        .el-tab-pane{
            height: 300px;
        }
        .el-tab-pane:first-child{
            height: 360px;
        }
        #tab-0{//将第一项头部隐藏起来
            display:none;
        }
        #tab-1{
            // padding-left:0px;
        }
    }
    .myTab.isTop{
        top:70px!important;
    }
    .enclosureUp {
        .file-list li{
            margin-bottom: 0!important;
        }
        .el-textarea__inner{
            border:none;
        }
    }
    #myExamineApprove{
        .detailDialog.el-dialog__wrapper {
            .el-dialog__body {
                height: 400px;
                overflow-y: auto;
            }
        }
        .el-button-group .el-button:last-child {
            margin-left: -2px!important;
        }
    }
</style>

<template>
    <div id="myExamineApprove">
        <template v-if="!isPending">
            <!--搜索区-->
            <div class="search-setion">
                <el-form :inline="true" :model="searchData" size="mini">
                    <el-row>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.init_user_name" clearable  placeholder="请输入发起人"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.init_dept_name" clearable placeholder="请输入部门名称"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.init_org_name" clearable placeholder="请输入公司名称"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                            </el-form-item>
                        </el-col>
                    </el-row>
                    <el-row>
                        <el-col :span="8">
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
                                <el-select v-model="searchData.biz_type" placeholder="请选择业务种类" clearable >
                                    <el-option
                                        v-for="(item,k) in businessType"
                                        :key="k"
                                        :label="item"
                                        :value="k">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </div>
            <!--分隔栏-->
            <div class="split-bar" ></div>
            <!--已办数据展示区-->
            <section :class="['table-content']">
                <el-table :data="doneTableList"
                        border
                        height="100%"
                        size="mini">
                    <el-table-column prop="biz_type" label="业务种类"
                                    :formatter="transitionStatus"
                                    :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="bill_code" label="单据编号" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="start_time" label="办理日期" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="init_user_name" label="发起人" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="init_dept_name" label="部门" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="status" label="状态"
                                    :formatter="transitionStatus"
                                    :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="current[0].name" label="待办人" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column
                            label="操作" width="110"
                            fixed="right">
                        <template slot-scope="scope" class="operationBtn">
                            <el-tooltip content="查看" placement="bottom" effect="light"
                                        :enterable="false" :open-delay="500" v-show="!isPending">
                                <el-button type="primary" icon="el-icon-search" size="mini"
                                        @click="viewDetail(scope.row,scope.$index)"></el-button>
                            </el-tooltip>
                        </template>
                    </el-table-column>
                </el-table>
            </section>
        </template>
        <!-- 待办页面 -->
        <div class="approveTab" v-if="isPending">
            <div class="borderBom">
                <div class="myTodo" @click="comeBack">
                    <span>我的待办</span>
                    <i class="tab-num">[{{totalTabNum}}]</i>
                </div>
            </div>
            <el-tabs class="myTab" :class="classObject" v-model="activeName" @tab-click="handleClick" id="myTab">
                <el-tab-pane
                    v-for="(item) in editableTabsList"
                    :key="item.name"
                    :name="item.name"
                >
                    <!--tab标签-->
                    <span slot="label">{{item.title}}<i class="tab-num">[{{item.num}}]</i></span>
                    <!--数据展示-->
                    <el-table :data="item.tableList"
                        border
                        height="100%"
                        @selection-change="handleSelectionChange"
                        size="mini">
                        <el-table-column
                            v-if="activeName!='0'"
                            type="selection"
                            width="55">
                        </el-table-column>
                        <template v-for="head in item.tableHead">
                            <!-- 业务种类列，需要格式化数据-->
                            <el-table-column
                                v-if="head.prop=='biz_type' || head.prop=='service_status' || head.prop=='interactive_mode'"
                                :key="head.id"
                                :prop="head.prop"
                                :label="head.name"
                                :formatter="transitionStatus"
                                :show-overflow-tooltip="true"
                            >
                            </el-table-column>
                            <!-- 上级处理人取history最后一条 assignee -->
                            <el-table-column v-else-if="head.prop=='history'"
                                :key="head.id"
                                :prop="head.prop"
                                :label="head.name"
                                :formatter="getAssignee"
                                :show-overflow-tooltip="true"
                            >
                            </el-table-column>
                            <!-- 调拨通金额处理千分位 -->
                            <el-table-column v-else-if="head.prop=='payment_amount' || head.prop== 'total_amount' || head.prop== 'receipts_amount' || head.prop== 'success_amount'"
                                :key="head.id"
                                :prop="head.prop"
                                :label="head.name"
                                :formatter="changeThousandth"
                                :show-overflow-tooltip="true"
                            >
                            </el-table-column>
                            <!-- 归集通归集额度 -->
                            <el-table-column v-else-if="head.prop=='collect_type' || head.prop=='allocation_type' || head.prop=='gyl_allocation_type'"
                                             :key="head.id"
                                             :prop="head.prop"
                                             :label="head.name"
                                             :formatter="collectType"
                                             :show-overflow-tooltip="true"
                            >
                            </el-table-column>
                            <!-- 归集通归集频率 -->
                            <el-table-column v-else-if="head.prop=='collect_frequency' || head.prop=='gyl_allocation_frequency' || head.prop=='allocation_frequency'"
                                             :key="head.id"
                                             :prop="head.prop"
                                             :label="head.name"
                                             :formatter="collectFrequency"
                                             :show-overflow-tooltip="true"
                            >
                            </el-table-column>
                            <!-- 下级审批人处理 -->
                            <el-table-column v-else-if="head.prop=='nextUserList'"
                                             :key="head.id"
                                             :prop="head.prop"
                                             :label="head.name"
                                             :formatter="rename"
                                             :show-overflow-tooltip="true"
                            >
                            </el-table-column>
                            <!--付款方式-->
                            <el-table-column v-else-if="head.prop=='pay_mode'"
                                             :key="head.id"
                                             :prop="head.prop"
                                             :label="head.name"
                                             :formatter="payMode"
                                             :show-overflow-tooltip="true">
                            </el-table-column>
                            <!-- 公用列 -->
                            <el-table-column
                                v-else
                                :key="head.id"
                                :prop="head.prop"
                                :label="head.name"
                                :show-overflow-tooltip="true"
                            >
                            </el-table-column>
                        </template>

                        <el-table-column
                                label="操作" width="50"
                                fixed="right">
                            <template slot-scope="scope" class="operationBtn">
                                <el-tooltip content="查看" placement="bottom" effect="light"
                                            :enterable="false" :open-delay="500">
                                    <el-button type="primary" icon="el-icon-search" size="mini"
                                            @click="viewDetail(scope.row,scope.$index)"></el-button>
                                </el-tooltip>
                            </template>
                        </el-table-column>
                    </el-table>
                </el-tab-pane>
            </el-tabs>
        </div>
        <!--加签同意功能-->
        <div class="button-list" v-if="isPending" v-show="activeName!='0'">
            <el-button type="primary"
                plain
                size="small"
                @click="showThirdDialog('addLots','more')"
                icon="el-icon-circle-plus-outline">加签</el-button>
            <el-button type="warning"
                plain
                size="small"
                @click="showThirdDialog('agree','more')"
                icon="el-icon-circle-check-outline">同意</el-button>
        </div>
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
        <!-- 开户事项申请详情查看 -->
        <el-dialog :visible.sync="dialogVisible"
                   width="860px"
                   class="detailDialog"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <div class="dialogTable">
                <el-row>
                    <template v-for="detail in currentDetailDialog" >
                        <el-col v-if="detail.type"
                                :key="detail.id"
                                :span="detail.lspan" class="left center">{{detail.label}}</el-col>
                        <el-col v-else-if="detail.label"
                                :key="detail.id"
                                :span="detail.lspan" class="left">{{detail.label}}</el-col>
                        <el-col  v-else-if="detail.parent"
                                :key="detail.id"
                                :span="detail.pspan">{{hasParent(detail.prop)}}</el-col>
                        <el-col v-else-if="detail.prop=='interactive_mode'"
                                :key="detail.id"
                                :span="detail.pspan">{{interList[dialogData.interactive_mode]}}</el-col>
                        <el-col v-else-if="detail.prop=='collect_type' || detail.prop=='gyl_allocation_type'"
                                :key="detail.id"
                                :span="detail.pspan">{{poolTypeList[dialogData[detail.prop]]}}</el-col>
                        <el-col v-else-if="detail.prop=='collect_frequency' || detail.prop=='gyl_allocation_frequency'"
                                :key="detail.id"
                                :span="detail.pspan">{{frequencyList[dialogData[detail.prop]]}}</el-col>
                        <el-col v-else-if="detail.prop=='allocation_type'"
                                :key="detail.id"
                                :span="detail.pspan">{{poolTypeList[dialogData.allocation_type]}}</el-col>
                        <el-col v-else-if="detail.prop=='allocation_frequency'"
                                :key="detail.id"
                                :span="detail.pspan">{{frequencyList[dialogData.allocation_frequency]}}</el-col>
                        <el-col v-else-if="detail.prop=='deposits_mode'"
                                :key="detail.id"
                                :span="detail.pspan">{{depositsList[dialogData.deposits_mode]}}</el-col>
                        <el-col v-else-if="detail.prop=='payment_type'"
                                :key="detail.id"
                                :span="detail.pspan">{{dbtTypeList[dialogData.payment_type]}}</el-col>
                        <el-col v-else-if="detail.prop=='service_status'"
                                :key="detail.id"
                                :span="detail.pspan">{{billStatusList[dialogData.service_status]}}</el-col>
                        <el-col v-else-if="detail.prop=='pay_mode'"
                                :key="detail.id"
                                :span="detail.pspan">{{PayModeList[dialogData.pay_mode]}}</el-col>

                        <el-col v-else-if="detail.prop=='payment_amount' || detail.prop=='total_amount'"
                                :key="detail.id"
                                :span="detail.pspan">
                                <span>￥</span>
                                <span class="aomuntColor">{{tansss(dialogData[detail.prop])}}</span>
                                <span class="grey">(大写){{dialogData.payAmountUp}}</span></el-col>
                        <el-col  v-else
                                :key="detail.id"
                                :span="detail.pspan">{{dialogData[detail.prop]}}</el-col>
                    </template>
                </el-row>
                <el-row class="enclosureUp" v-if="dialogData.biz_type=='1' || dialogData.biz_type=='6'">
                    <el-col :span="4" class="left textName"><span>申请事由说明</span></el-col>
                    <el-col :span="20" class="mw167" :title="dialogData.detail">
                        <el-input v-model="dialogData.detail" readonly
                                    type="textarea" :rows="2"></el-input>
                    </el-col>
                </el-row>
                <el-row class="enclosureUp" v-if="dialogData.biz_type=='13'">
                    <el-col :span="4" class="left"><span>下拨时间</span></el-col>
                    <el-col :span="20" class="mw167">
                        <span v-html="translateFrequency(dialogData.frequency_detail)"></span>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="enclosureLWidth" class="left">附件</el-col>
                    <el-col :span="enclosurePWidth"><span class="enclosure">{{dialogData.encNumber}}</span>个</el-col>
                </el-row>
                <el-row class="enclosureUp">
                    <el-col :span="enclosureLWidth" class="left"></el-col>
                    <el-col :span="enclosurePWidth" :class="[enclosureLWidth==6 ? 'mw25' : 'mw167']">
                        <Upload
                            @currentFielList="setFileList"
                            :emptyFileList="emptyFileList"
                            :isPending="false"
                            :fileMessage="fileMessage"
                            :triggerFile="normalTriggerFile"></Upload>
                    </el-col>
                </el-row>
            </div>
            <BusinessTracking
                :businessParams="normalBusinessParams"
            ></BusinessTracking>
            <span slot="footer" class="dialog-footer" v-if="isPending">
                <el-button type="primary"
                    size="mini"
                    plain
                    @click="showThirdDialog('addLots')"
                    icon="el-icon-circle-plus-outline">加 签</el-button>
                <el-button type="danger"
                    size="mini"
                    plain
                    @click="showThirdDialog('reject')"
                    icon="el-icon-circle-close-outline">拒 绝</el-button>
                <el-button type="warning"
                    size="mini"
                    plain
                    @click="showThirdDialog('agree')"
                    icon="el-icon-circle-check-outline">同 意</el-button>
            </span>
        </el-dialog>
        <!--批量调拨弹出框-->
        <el-dialog title=""
                   :visible.sync="batchAllotVisible"
                   width="900px" top="76px"
                   class="detailDialog"
                   @close="closeLookDialog"
                   :close-on-click-modal="false">
            <h1 slot="title"  class="dialog-title">
                <span v-text="dialogTitle"></span>
                <span class="title-batchno">[{{dialogData.batchno}}]</span>
            </h1>
            <div class="view-mode">
                <el-button-group>
                    <el-button size="mini" :class="{viewSelect:curInnerTab}" round title="查看批次汇总" @click="changeTab(true)"><i class="tabBtn total"></i></el-button>
                    <el-button size="mini" :class="{viewSelect:!curInnerTab}" round title="查看批次明细" @click="changeTab(false)"><i class="tabBtn detail"></i></el-button>
                </el-button-group>
            </div>
            <section id="batchSummary" v-show="curInnerTab">
                <ul class="dialog-talbe">
                    <li class="borderRight bgColor">
                        <span class="table-title">总笔数</span>
                        <span class="tab-content">{{dialogData.total_num}}</span>
                    </li>
                    <li class="bgColor">
                        <span class="table-title">总金额</span>
                        <span class="tab-content font-red">{{dialogData.total_amount}}元</span>
                    </li>
                    <li v-if="curBiztype==19">
                        <span class="table-title">收款账号</span>
                        <span class="tab-content" :title="dialogData.recv_account_no">{{dialogData.recv_account_no}}</span>
                    </li>
                    <li v-if="curBiztype==19">
                        <span class="table-title">开户行</span>
                        <span class="tab-content" :title="dialogData.recv_account_bank">{{dialogData.recv_account_bank}}</span>
                    </li>
                    <li v-if="curBiztype!=19">
                        <span class="table-title">付款账号</span>
                        <span class="tab-content" :title="dialogData.pay_account_no">{{dialogData.pay_account_no}}</span>
                    </li>
                    <li v-if="curBiztype!=19">
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
                            :triggerFile="specialTriggerFile"
                            :isPending="false"></Upload>
                    </span>
                </div>
                <BusinessTracking :businessParams="specialBusinessParams"></BusinessTracking>
            </section>
            <section id="batchDeatil" v-show="!curInnerTab">
                <section class="tab-content">
                    <el-table :data="detailTableList"
                            height="100%"
                            border size="mini">
                        <el-table-column prop="pay_account_name" v-if="curBiztype==19" label="付款户名" :show-overflow-tooltip="true"></el-table-column>
                        <el-table-column prop="pay_account_no" v-if="curBiztype==19" label="付款账号" :show-overflow-tooltip="true"></el-table-column>
                        <el-table-column prop="pay_account_bank" v-if="curBiztype==19" label="付款行" :show-overflow-tooltip="true"></el-table-column>
                        <el-table-column prop="recv_account_name" v-if="curBiztype!=19" label="收款户名" :show-overflow-tooltip="true"></el-table-column>
                        <el-table-column prop="recv_account_no" v-if="curBiztype!=19" label="收款账号" :show-overflow-tooltip="true"></el-table-column>
                        <el-table-column prop="recv_account_bank" v-if="curBiztype!=19" label="收款行" :show-overflow-tooltip="true"></el-table-column>
                        <el-table-column prop="payment_amount" label="金额" :show-overflow-tooltip="true"
                                        :formatter="changeThousandth"></el-table-column>
                        <el-table-column prop="pay_status" label="业务状态" :show-overflow-tooltip="true"
                                        :formatter="transitionStatus"></el-table-column>
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
            <span slot="footer" class="dialog-footer" v-if="isPending">
                <el-button type="primary"
                    size="mini"
                    plain
                    @click="showThirdDialog('addLots')"
                    icon="el-icon-circle-plus-outline">加 签</el-button>
                <el-button type="danger"
                    size="mini"
                    plain
                    @click="showThirdDialog('reject')"
                    icon="el-icon-circle-close-outline">拒 绝</el-button>
                <el-button type="warning"
                    size="mini"
                    plain
                    @click="showThirdDialog('agree')"
                    icon="el-icon-circle-check-outline">同 意</el-button>
            </span>
        </el-dialog>
        <!-- 加签,拒绝，同意弹出框 -->
        <el-dialog :visible.sync="thirdFunVisible"
                   width="660px"
                   :close-on-click-modal="false"
                   top="120px">
            <h1 slot="title" class="dialog-title">
                <i class="el-icon-circle-plus-outline blue" v-if="thirdFunData.type=='addLots'"></i>
                <i class="el-icon-circle-close-outline red" v-else-if="thirdFunData.type=='reject'"></i>
                <i class="el-icon-circle-check-outline green" v-else></i>
                {{thirdFunTitle}}
            </h1>
            <el-form :model="thirdFunData" size="small"
                     :label-width="formLabelWidth">
                <el-row v-if="thirdFunData.type=='addLots'">
                    <el-col :span="24">
                        <el-form-item label="加签用户">
                            <el-select value-key="id" v-model="thirdFunData.user" filterable placeholder="请选择一个用户">
                                <el-option
                                    v-for="item in user_options"
                                    :key="item.id"
                                    :label="item.name"
                                    :value="item">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="处理意见">
                            <el-input v-model="thirdFunData.assignee_memo" type="textarea" :rows="3"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row v-else-if="thirdFunData.type=='reject'">
                    <el-col :span="24">
                        <!-- <el-form-item label="退回">
                            <el-select value-key="id" v-model="thirdFunData.back">
                                <el-option
                                    v-for="item in back_options"
                                    :key="item.id"
                                    :label="item.name"
                                    :value="item">
                                </el-option>
                            </el-select>
                        </el-form-item>-->
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="拒绝原因">
                            <el-input v-model="thirdFunData.assignee_memo" type="textarea" :rows="3"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row class="agreeAdvice" v-else>
                    <el-col :span="24">
                        <el-input v-model="thirdFunData.assignee_memo"
                            type="textarea"
                            :rows="3"
                            placeholder="再此可填写审批意见!"
                            ></el-input>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" @click="thirdFunVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="confirmThirdFun">确定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";
    import BusinessTracking from "../publicModule/BusinessTracking.vue"
    export default {
        name: "MyExamineApprove",
        created: function () {
            var params = this.$route.params;
            if(params.biz_type){
                var curTab = this.$route.params.biz_type;
                this.activeName = curTab+"";
                this.isFromHome = true;
            }
            this.$emit("transmitTitle", "我的审批平台");
            //获取tab数据
            if(this.isPending){
                this.getTabList();
            }else{
                this.$emit("getTableData", this.routerMessage);
            }
            //获取用户列表
            this.$axios({
                url: this.queryUrl + "commProcess",
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
                } else {
                    this.user_options = result.data.data;
                }

            })
            this.classParams = {
                "0":{//全部的开户详情
                    text:"开户事项",
                    detail:"openintent_detail",
                    list:"wfquery_pendingtasksall"
                },
                "1":{//开户列表的开户详情不调接口
                    text:"开户事项",
                    detail:"openintent_detail",
                    list:"openintent_pendingtasks",
                    addLots:"openintent_append",
                    agree:"openintent_agree",
                    reject:"openintent_reject",
                    'more-addLots':"openintent_batchappend",
                    'more-agree':"openintent_batchagree"
                },
                "2":{
                    text:"开户信息补录",
                    detail:"opencom_detail",
                    list:"opencom_pendingtasks",
                    addLots:"opencom_append",
                    agree:"opencom_agree",
                    reject:"opencom_reject",
                    'more-addLots':"opencom_batchappend",
                    'more-agree':"opencom_batchagree"
                },
                "3":{
                    text:"账户变更",
                    detail:"openchg_detail",
                    list:"openchg_pendingtasks",
                    addLots:"openchg_append",
                    agree:"openchg_agree",
                    reject:"openchg_reject",
                    'more-addLots':"openchg_batchappend",
                    'more-agree':"openchg_batchagree"
                },
                "4":{
                    text:"账户冻结",
                    detail:"accfreeze_detail",
                    list:"accfreeze_pendingtasks",
                    addLots:"accfreeze_append",
                    agree:"accfreeze_agree",
                    reject:"accfreeze_reject",
                    'more-addLots':"accfreeze_batchappend",
                    'more-agree':"accfreeze_batchagree"
                },
                "5":{
                    text:"账户解冻",
                    detail:"accdefreeze_detail",
                    list:"accdefreeze_pendingtasks",
                    addLots:"accdefreeze_append",
                    agree:"accdefreeze_agree",
                    reject:"accdefreeze_reject",
                    'more-addLots':"accdefreeze_batchappend",
                    'more-agree':"accdefreeze_batchagree"
                },
                "6":{
                    text:"销户事项",
                    detail:"closeacc_detail",
                    list:"closeacc_pendingtasks",
                    addLots:"closeacc_append",
                    agree:"closeacc_agree",
                    reject:"closeacc_reject",
                    'more-addLots':"closeacc_batchappend",
                    'more-agree':"closeacc_batchagree"
                },
                "7":{
                    text:"销户信息补录",
                    detail:"closeacccomple_detail",
                    list:"closeacccomple_pendingtasks",
                    addLots:"closeacccomple_append",
                    agree:"closeacccomple_agree",
                    reject:"closeacccomple_reject",
                    'more-addLots':"closeacccomple_batchappend",
                    'more-agree':"closeacccomple_batchagree"
                },
                "8":{
                    text:"内部调拨",
                    detail:"dbt_detail",
                    list:"dbt_pendingtasks",
                    addLots:"dbt_append",
                    agree:"dbt_agree",
                    reject:"dbt_reject",
                    'more-addLots':"dbt_batchappend",
                    'more-agree':"dbt_batchagree"
                },
                "9":{
                    text:"支付通",
                    detail:"zft_detail",
                    list:"zft_pendingtasks",
                    addLots:"zft_append",
                    agree:"zft_agree",
                    reject:"zft_reject",
                    'more-addLots':"zft_batchappend",
                    'more-agree':"zft_batchagree"
                },
                "10":{
                    text:"内部调拨-批量",
                    detail:"dbtbatch_viewbill",
                    list:"dbtbatch_pendingtasks",
                    addLots:"dbtbatch_append",
                    agree:"dbtbatch_agree",
                    reject:"dbtbatch_reject",
                    'more-addLots':"dbtbatch_batchappend",
                    'more-agree':"dbtbatch_batchagree"
                },
                "11":{
                    text:"支付通-批量",
                    detail:"zftbatch_billdetail",
                    list:"zftbatch_pendingtasks",
                    addLots:"zftbatch_append",
                    agree:"zftbatch_agree",
                    reject:"zftbatch_reject",
                    'more-addLots':"zftbatch_batchappend",
                    'more-agree':"zftbatch_batchagree"
                },
                "12":{
                    text:"归集通",
                    detail:"collectsetting_detail",
                    list:"collectsetting_pendingtasks",
                    addLots:"collectsetting_append",
                    agree:"collectsetting_agree",
                    reject:"collectsetting_reject",
                    'more-addLots':"collectsetting_batchappend",
                    'more-agree':"collectsetting_batchagree"
                },
                "13":{
                    text:"资金下拨",
                    detail:"allocset_detail",
                    list:"allocset_pendingtasks",
                    addLots:"allocset_append",
                    agree:"allocset_agree",
                    reject:"allocset_reject",
                    'more-addLots':"allocset_batchappend",
                    'more-agree':"allocset_batchagree"
                },
                "14":{
                    text:"广银联备付金",
                    detail:"gylsetting_detail",
                    list:"gylsetting_pendingtasks",
                    addLots:"gylsetting_append",
                    agree:"gylsetting_agree",
                    reject:"gylsetting_reject",
                    'more-addLots':"gylsetting_batchappend",
                    'more-agree':"gylsetting_batchagree"
                },
                "15":{
                    text:"收款通",
                    detail:"skt_billdetail",
                    list:"skt_pendingtasks",
                    addLots:"skt_append",
                    agree:"skt_agree",
                    reject:"skt_reject",
                    'more-addLots':"skt_batchappend",
                    'more-agree':"skt_batchagree"
                },
                "19":{
                    text:"非直连归集",
                    detail:"ndc_detail",
                    list:"ndc_pendingtasks",
                    addLots:"ndc_append",
                    agree:"ndc_agree",
                    reject:"ndc_reject",
                    'more-addLots':"ndc_batchappend",
                    'more-agree':"ndc_batchagree"
                },
                "20":{
                    text:"OA数据总公司付款",
                    detail:"headorgoa_detail",
                    list:"headorgoa_pendingtasks",
                    addLots:"headorgoa_append",
                    agree:"headorgoa_agree",
                    reject:"headorgoa_reject",
                    'more-addLots':"headorgoa_batchappend",
                    'more-agree':"headorgoa_batchagree"
                },
                "21":{
                    text:"OA数据分公司付款",
                    detail:"branchorgoa_detail",
                    list:"branchorgoa_pendingtasks",
                    addLots:"branchorgoa_append",
                    agree:"branchorgoa_agree",
                    reject:"branchorgoa_reject",
                    'more-addLots':"branchorgoa_batchappend",
                    'more-agree':"branchorgoa_batchagree"
                },
            };

            this.detailDialog ={
                "1":[
                    {id:"1", lspan:4, label:"编号"},
                    {id:"2", pspan:20, prop:"service_serial_number"},
                    {id:"3", lspan:4, label:"事由摘要"},
                    {id:"4", pspan:8, prop:"memo"},//事由说明字段显示特殊，未写在这里
                    {id:"5", lspan:4, label:"开户行"},
                    {id:"6", pspan:8, prop:"bank_name"},
                    {id:"7", lspan:4, label:"账户性质"},
                    {id:"8", pspan:8, prop:"acc_attr_name"},
                    {id:"9", lspan:4, label:"账户法人"},
                    {id:"10", pspan:8, prop:"lawfull_man"},
                    {id:"11", lspan:4, label:"账户模式"},
                    {id:"12", pspan:8, prop:"interactive_mode"},
                    {id:"13", lspan:4, label:"账户用途"},
                    {id:"14", pspan:8, prop:"acc_attr_purpose"},
                    {id:"15", lspan:4, label:"币种"},
                    {id:"16", pspan:8, prop:"curr_name"},
                    {id:"17", lspan:4, label:"存款类型"},
                    {id:"18", pspan:8, prop:"deposits_mode"}
                ],
                "2":[
                    {id:"1", lspan:4, label:"编号"},
                    {id:"2",pspan:20, prop:"service_serial_number"},
                    {id:"3", lspan:4, label:"账户号"},
                    {id:"4", pspan:8, prop:"acc_no"},
                    {id:"5", lspan:4, label:"账户名称"},
                    {id:"6", pspan:8, prop:"acc_name"},
                    {id:"7", lspan:4, label:"所属机构"},
                    {id:"8", pspan:8, prop:"org_name"},
                    {id:"9", lspan:4, label:"账户法人"},
                    {id:"10", pspan:8, prop:"lawfull_man"},
                    {id:"11", lspan:4, label:"开户行"},
                    {id:"12", pspan:8, prop:"bank_name"},
                    {id:"13", lspan:4, label:"开户行地址"},
                    {id:"14", pspan:8, prop:"bank_address"},
                    {id:"15", lspan:4, label:"开户行联系人"},
                    {id:"16", pspan:8, prop:"bank_contact"},
                    {id:"17", lspan:4, label:"联系电话"},
                    {id:"18", pspan:8, prop:"bank_contact_phone"},
                    {id:"19", lspan:4, label:"币种"},
                    {id:"20", pspan:8, prop:"curr_name"},
                    {id:"21", lspan:4, label:"开户日期"},
                    {id:"22", pspan:8, prop:"apply_on"},
                    {id:"23", lspan:4, label:"账户性质"},
                    {id:"24", pspan:8, prop:"acc_attr_name"},
                    {id:"25", lspan:4, label:"账户用途"},
                    {id:"26", pspan:8, prop:"acc_purpose_name"},
                    {id:"27", lspan:4, label:"账户模式"},
                    {id:"28", pspan:8, prop:"interactive_mode"},
                    {id:"29", lspan:4, label:"存款类型"},
                    {id:"30", pspan:8, prop:"deposits_mode"},
                    {id:"31", lspan:4, label:"预留印鉴"},
                    {id:"32", pspan:20, prop:"reserved_seal"},
                    {id:"33", lspan:4, label:"备注"},
                    {id:"34", pspan:20, prop:"memo"}

                ],
                "3":[
                    {id:"1", lspan:4, label:"编号"},
                    {id:"2",pspan:20, prop:"service_serial_number"},
                    {id:"3", lspan:4, label:"账户号"},
                    {id:"4", pspan:8, prop:"acc_no"},
                    {id:"5", lspan:4, label:"开户时间"},
                    {id:"6", pspan:8, prop:"apply_on"},
                    {id:"7", lspan:12, label:"变更前信息", type:'1'},
                    {id:"8", lspan:12, label:"变更后信息", type:'1'},
                    {id:"9", lspan:4, label:"账户名称"},
                    {id:"10", pspan:8, prop:"old_acc_name"},
                    {id:"11", lspan:4, label:"账户名称"},
                    {id:"12", pspan:8, prop:"new_acc_name"},
                    {id:"13", lspan:4, label:"所属机构"},
                    {id:"14", pspan:8, prop:"old_org_name"},
                    {id:"15", lspan:4, label:"所属机构"},
                    {id:"16", pspan:8, prop:"new_org_name"},
                    {id:"17", lspan:4, label:"开户行"},
                    {id:"18", pspan:8, prop:"old_bank_name"},
                    {id:"19", lspan:4, label:"开户行"},
                    {id:"20", pspan:8, prop:"new_bank_name"},
                    {id:"21", lspan:4, label:"账户法人"},
                    {id:"22", pspan:8, prop:"old_lawfull_man"},
                    {id:"23", lspan:4, label:"账户法人"},
                    {id:"24", pspan:8, prop:"new_lawfull_man"},
                    {id:"25", lspan:4, label:"币种"},
                    {id:"26", pspan:8, prop:"old_curr_name"},
                    {id:"27", lspan:4, label:"币种"},
                    {id:"28", pspan:8, prop:"new_curr_name"},
                    {id:"29", lspan:4, label:"账户性质"},
                    {id:"30", pspan:8, prop:"old_acc_attr_name"},
                    {id:"31", lspan:4, label:"账户性质"},
                    {id:"32", pspan:8, prop:"new_acc_attr_name"},
                    {id:"33", lspan:4, label:"账户模式"},
                    {id:"34", pspan:8, prop:"old_interactive_mode"},
                    {id:"35", lspan:4, label:"账户模式"},
                    {id:"36", pspan:8, prop:"new_interactive_mode"},
                    {id:"37", lspan:4, label:"账户用途"},
                    {id:"38", pspan:8, prop:"old_acc_purpose_name"},
                    {id:"39", lspan:4, label:"账户用途"},
                    {id:"40", pspan:8, prop:"new_acc_purpose_name"},
                    {id:"41", lspan:4, label:"备注"},
                    {id:"42", pspan:20, prop:"memo"}
                ],
                "4":[
                    {id:"1", lspan:4, label:"编号"},
                    {id:"2",pspan:20, prop:"service_serial_number"},
                    {id:"3", lspan:4, label:"账户号"},
                    {id:"4", pspan:8, prop:"acc_no"},
                    {id:"5", lspan:4, label:"账户名称"},
                    {id:"6", pspan:8, prop:"acc_name"},
                    {id:"7", lspan:4, label:"所属机构"},
                    {id:"8", pspan:8, prop:"org_name"},
                    {id:"9", lspan:4, label:"账户法人"},
                    {id:"10", pspan:8, prop:"lawfull_man"},
                    {id:"11", lspan:4, label:"开户行"},
                    {id:"12", pspan:20, prop:"bank_name"},
                    {id:"13", lspan:4, label:"币种"},
                    {id:"14", pspan:20, prop:"curr_name"},
                    {id:"15", lspan:4, label:"账户用途"},
                    {id:"16", pspan:8, prop:"acc_purpose_name"},
                    {id:"17", lspan:4, label:"账户模式"},
                    {id:"18", pspan:8, prop:"interactive_mode"},
                    {id:"19", lspan:4, label:"账户性质"},
                    {id:"20", pspan:8, prop:"acc_attr_name"},
                    {id:"21", lspan:4, label:"存款类型"},
                    {id:"22", pspan:8, prop:"deposits_mode"},
                    {id:"23", lspan:4, label:"备注"},
                    {id:"24", pspan:20, prop:"memo"}
                ],
                "5":[
                    {id:"1", lspan:4, label:"编号"},
                    {id:"2",pspan:20, prop:"service_serial_number"},
                    {id:"3", lspan:4, label:"账户号"},
                    {id:"4", pspan:8, prop:"acc_no"},
                    {id:"5", lspan:4, label:"账户名称"},
                    {id:"6", pspan:8, prop:"acc_name"},
                    {id:"7", lspan:4, label:"所属机构"},
                    {id:"8", pspan:8, prop:"org_name"},
                    {id:"9", lspan:4, label:"账户法人"},
                    {id:"10", pspan:8, prop:"lawfull_man"},
                    {id:"11", lspan:4, label:"开户行"},
                    {id:"12", pspan:20, prop:"bank_name"},
                    {id:"13", lspan:4, label:"币种"},
                    {id:"14", pspan:20, prop:"curr_name"},
                    {id:"15", lspan:4, label:"账户用途"},
                    {id:"16", pspan:8, prop:"acc_purpose_name"},
                    {id:"17", lspan:4, label:"账户模式"},
                    {id:"18", pspan:8, prop:"interactive_mode"},
                    {id:"19", lspan:4, label:"账户性质"},
                    {id:"20", pspan:8, prop:"acc_attr_name"},
                    {id:"21", lspan:4, label:"存款类型"},
                    {id:"22", pspan:8, prop:"deposits_mode"},
                    {id:"23", lspan:4, label:"备注"},
                    {id:"24", pspan:20, prop:"memo"}
                ],
                "6":[
                    {id:"1", lspan:4, label:"编号"},
                    {id:"2", pspan:20, prop:"service_serial_number"},
                    {id:"3", lspan:4, label:"事由摘要"},
                    {id:"4", pspan:20, prop:"memo"},
                    {id:"5", lspan:4, label:"账户号"},
                    {id:"6", pspan:8, prop:"acc_no", parent:'account_info'},
                    {id:"7", lspan:4, label:"账户名称"},
                    {id:"8", pspan:8, prop:"acc_name", parent:'account_info'},
                    {id:"9", lspan:4, label:"所属机构"},
                    {id:"10", pspan:8, prop:"org_name", parent:'account_info'},
                    {id:"11", lspan:4, label:"账户法人"},
                    {id:"12", pspan:8, prop:"lawfull_man", parent:'account_info'},
                    {id:"13", lspan:4, label:"币种"},
                    {id:"14", pspan:8, prop:"curr_name", parent:'account_info'},
                    {id:"15", lspan:4, label:"开户行"},
                    {id:"16", pspan:8, prop:"bank_name", parent:'account_info'},
                    {id:"17", lspan:4, label:"账户模式"},
                    {id:"18", pspan:8, prop:"interactive_mode", parent:'account_info'},
                    {id:"19", lspan:4, label:"账户用途"},
                    {id:"20", pspan:8, prop:"acc_purpose_name", parent:'account_info'},
                    {id:"21", lspan:4, label:"账户性质"},
                    {id:"22", pspan:8, prop:"acc_attr_name", parent:'account_info'},
                    {id:"23", lspan:4, label:"存款类型"},
                    {id:"24", pspan:8, prop:"deposits_mode", parent:'account_info'}
                ],
                "7":[
                    {id:"1", lspan:4, label:"编号"},
                    {id:"2",pspan:20, prop:"service_serial_number"},
                    {id:"3", lspan:4, label:"账户号"},
                    {id:"4", pspan:8, prop:"acc_no"},
                    {id:"5", lspan:4, label:"账户名称"},
                    {id:"6", pspan:8, prop:"acc_name"},
                    {id:"7", lspan:4, label:"所属机构"},
                    {id:"8", pspan:8, prop:"org_name"},
                    {id:"9", lspan:4, label:"账户法人"},
                    {id:"10", pspan:8, prop:"lawfull_man"},
                    {id:"11", lspan:4, label:"开户行"},
                    {id:"12", pspan:20, prop:"bank_name"},
                    {id:"13", lspan:4, label:"币种"},
                    {id:"14", pspan:20, prop:"curr_name"},
                    {id:"15", lspan:4, label:"账户用途"},
                    {id:"16", pspan:8, prop:"acc_purpose_name"},
                    {id:"17", lspan:4, label:"账户模式"},
                    {id:"18", pspan:8, prop:"interactive_mode"},
                    {id:"19", lspan:4, label:"账户性质"},
                    {id:"20", pspan:8, prop:"acc_attr_name"},
                    {id:"21", lspan:4, label:"存款类型"},
                    {id:"22", pspan:8, prop:"deposits_mode"},
                    {id:"23", lspan:4, label:"销户交易"},
                    {id:"24", pspan:20, prop:"salesTransaction"},
                    {id:"25", lspan:4, label:"备注"},
                    {id:"26", pspan:20, prop:"memo"}
                ],
                "8":[//调拨通
                    {id:"1", lspan:4, label:"编号"},
                    {id:"2",pspan:20, prop:"service_serial_number"},
                    {id:"3", lspan:4, label:"转出公司"},
                    {id:"4", pspan:8, prop:"pay_account_name"},
                    {id:"5", lspan:4, label:"转入公司"},
                    {id:"6", pspan:8, prop:"recv_account_name"},
                    {id:"7", lspan:4, label:"付款账号"},
                    {id:"8", pspan:8, prop:"pay_account_no"},
                    {id:"9", lspan:4, label:"收款账号"},
                    {id:"10", pspan:8, prop:"recv_account_no"},
                    {id:"11", lspan:4, label:"开户行"},
                    {id:"12", pspan:8, prop:"pay_account_bank"},
                    {id:"13", lspan:4, label:"开户行"},
                    {id:"14", pspan:8, prop:"recv_account_bank"},
                    {id:"15", lspan:4, label:"金额"},
                    {id:"16", pspan:20, prop:"payment_amount"},
                    {id:"17", lspan:4, label:"摘要"},
                    {id:"18", pspan:8, prop:"payment_summary"},
                    {id:"19", lspan:4, label:"调拨类型"},
                    {id:"20", pspan:8, prop:"payment_type"}
                ],
                "9":[
                    {id:"1", lspan:4, label:"编号"},
                    {id:"2",pspan:20, prop:"service_serial_number"},
                    {id:"3", lspan:4, label:"付款账号"},
                    {id:"4", pspan:8, prop:"pay_account_no"},
                    {id:"5", lspan:4, label:"收款人户名"},
                    {id:"6", pspan:8, prop:"recv_account_name"},
                    {id:"7", lspan:4, label:"收款人账号"},
                    {id:"8", pspan:8, prop:"recv_account_no"},
                    {id:"9", lspan:4, label:"开户行"},
                    {id:"10", pspan:8, prop:"recv_account_bank"},
                    {id:"11", lspan:4, label:"金额"},
                    {id:"12", pspan:8, prop:"payment_amount"},
                    {id:"13", lspan:4, label:"摘要"},
                    {id:"14", pspan:8, prop:"payment_summary"}
                ],
                "10":[//调拨通-批量
                    {id:"1", lspan:4, label:"批次号"},
                    {id:"2",pspan:20, prop:"batchno"},
                    {id:"3", lspan:4, label:"总笔数"},
                    {id:"4", pspan:8, prop:"total_num"},
                    {id:"5", lspan:4, label:"付款方户名"},
                    {id:"6", pspan:8, prop:"pay_account_name"},
                    {id:"7", lspan:4, label:"付款方账号"},
                    {id:"8", pspan:8, prop:"pay_account_no"},
                    {id:"9", lspan:4, label:"付款行"},
                    {id:"10", pspan:8, prop:"pay_account_bank"},
                    {id:"11", lspan:4, label:"金额"},
                    {id:"12", pspan:8, prop:"total_amount"},
                    {id:"13", lspan:4, label:"备注"},
                    {id:"14", pspan:8, prop:"payment_summary"},
                ],
                "11":[
                    {id:"1", lspan:4, label:"批次号"},
                    {id:"2",pspan:20, prop:"batchno"},
                    {id:"3", lspan:4, label:"总笔数"},
                    {id:"4", pspan:8, prop:"total_num"},
                    {id:"5", lspan:4, label:"总金额"},
                    {id:"6", pspan:8, prop:"total_amount"},
                    {id:"7", lspan:4, label:"付款账号"},
                    {id:"8", pspan:8, prop:"pay_account_no"},
                    {id:"9", lspan:4, label:"开户行"},
                    {id:"10", pspan:8, prop:"pay_account_bank"},
                ],
                "12":[
                    {id:"1", lspan:4, label:"归集主题"},
                    {id:"2",pspan:8, prop:"topic"},
                    {id:"3", lspan:4, label:"归集额度"},
                    {id:"4", pspan:8, prop:"collect_type"},
                    {id:"5", lspan:4, label:"归集金额"},
                    {id:"6", pspan:8, prop:"collect_amount"},
                    {id:"7", lspan:4, label:"归集频率"},
                    {id:"8", pspan:8, prop:"collect_frequency"},
                    {id:"9", lspan:4, label:"归集时间"},
                    {id:"10", pspan:8, prop:"time_settings"},
                    {id:"11", lspan:4, label:"归集集户(个)"},
                    {id:"12", pspan:8, prop:"collect_main_account_count"},
                ],
                "13":[
                    {id:"1", lspan:4, label:"归集主题"},
                    {id:"2",pspan:20, prop:"topic"},
                    {id:"3", lspan:4, label:"归集额度"},
                    {id:"4", pspan:8, prop:"allocation_type"},
                    {id:"5", lspan:4, label:"归集金额"},
                    {id:"6", pspan:8, prop:"allocation_amount"},
                    {id:"7", lspan:4, label:"归集频率"},
                    {id:"8", pspan:8, prop:"allocation_frequency"},
                    {id:"9", lspan:4, label:"归集集户(个)"},
                    {id:"10", pspan:8, prop:"allocation_child_account_count"},
                    // {id:"11", lspan:4, label:"归集时间"},
                    // {id:"12", pspan:20, prop:"frequency_detail"},
                ],
                "14":[
                    {id:"1", lspan:4, label:"主题"},
                    {id:"2",pspan:20, prop:"topic"},
                    {id:"3", lspan:4, label:"下拨额度"},
                    {id:"4", pspan:8, prop:"gyl_allocation_type"},
                    {id:"5", lspan:4, label:"下拨金额"},
                    {id:"6", pspan:8, prop:"gyl_allocation_amount"},
                    {id:"7", lspan:4, label:"下拨频率"},
                    {id:"8", pspan:8, prop:"gyl_allocation_frequency"},
                    {id:"9", lspan:4, label:"下拨时间"},
                    {id:"10", pspan:8, prop:"time_settings"},
                ],
                "15":[
                    {id:"1", lspan:4, label:"编号"},
                    {id:"2",pspan:20, prop:"service_serial_number"},
                    {id:"3", lspan:4, label:"收款账号"},
                    {id:"4", pspan:8, prop:"recv_account_no"},
                    {id:"5", lspan:4, label:"付款人户名"},
                    {id:"6", pspan:8, prop:"pay_account_name"},
                    {id:"7", lspan:4, label:"付款人账号"},
                    {id:"8", pspan:8, prop:"pay_account_no"},
                    {id:"9", lspan:4, label:"开户行"},
                    {id:"10", pspan:8, prop:"pay_account_bank"},
                    {id:"11", lspan:4, label:"金额"},
                    {id:"12", pspan:8, prop:"receipts_amount"},
                    {id:"13", lspan:4, label:"摘要"},
                    {id:"14", pspan:8, prop:"receipts_summary"}
                ],
                "19":[
                    {id:"1", lspan:4, label:"批次号"},
                    {id:"2",pspan:20, prop:"batchno"},
                    {id:"3", lspan:4, label:"总笔数"},
                    {id:"4", pspan:8, prop:"total_num"},
                    {id:"5", lspan:4, label:"总金额"},
                    {id:"6", pspan:8, prop:"total_amount"},
                    {id:"7", lspan:4, label:"收款账号"},
                    {id:"8", pspan:8, prop:"recv_account_no"},
                    {id:"9", lspan:4, label:"开户行"},
                    {id:"10", pspan:8, prop:"recv_account_bank"},
                    {id:"11", lspan:4, label:"已失败笔数"},
                    {id:"12", pspan:8, prop:"failed_num"},
                    {id:"13", lspan:4, label:"已失败金额"},
                    {id:"14", pspan:8, prop:"failed_amount"},
                    {id:"15", lspan:4, label:"已成功笔数"},
                    {id:"16", pspan:8, prop:"success_num"},
                    {id:"17", lspan:4, label:"已成功金额"},
                    {id:"18", pspan:8, prop:"success_amount"}
                ],
                "20":[
                    {id:"1", lspan:4, label:"单据号"},
                    {id:"2", pspan:8, prop:"service_serial_number"},
                    {id:"3", lspan:4, label:"付款账户号"},
                    {id:"4",pspan:8, prop:"pay_account_no"},
                    {id:"5", lspan:4, label:"付款账户名称"},
                    {id:"6", pspan:8, prop:"pay_account_name"},
                    {id:"7", lspan:4, label:"付款银行名称"},
                    {id:"8", pspan:8, prop:"pay_account_bank"},
                    {id:"9", lspan:4, label:"收款账户号"},
                    {id:"10", pspan:8, prop:"recv_account_no"},
                    {id:"11", lspan:4, label:"收款人名称"},
                    {id:"12", pspan:8, prop:"recv_account_name"},
                    {id:"13", lspan:4, label:"收款银行名称"},
                    {id:"14", pspan:8, prop:"recv_account_bank"},
                    {id:"15", lspan:4, label:"付款方式"},
                    {id:"16", pspan:8, prop:"pay_mode"},
                    {id:"17", lspan:4, label:"付款金额"},
                    {id:"18", pspan:8, prop:"payment_amount"},
                    {id:"19", lspan:4, label:"单据状态"},
                    {id:"20", pspan:8, prop:"service_status"},
                    {id:"21", lspan:4, label:"摘要"},
                    {id:"22", pspan:20, prop:"payment_summary"}
                ],
                "21":[
                    {id:"1", lspan:4, label:"单据号"},
                    {id:"2", pspan:8, prop:"service_serial_number"},
                    {id:"3", lspan:4, label:"资金池账户"},
                    {id:"4",pspan:8, prop:"pool_account_no"},
                    {id:"5", lspan:4, label:"付款方账号"},
                    {id:"6", pspan:8, prop:"pay_account_no"},
                    {id:"7", lspan:4, label:"收款人姓名"},
                    {id:"8", pspan:8, prop:"recv_account_name"},
                    {id:"9", lspan:4, label:"收款方账号"},
                    {id:"10", pspan:8, prop:"recv_account_no"},
                    {id:"11", lspan:4, label:"收款方银行"},
                    {id:"12", pspan:8, prop:"recv_account_bank"},
                    {id:"13", lspan:4, label:"收款金额"},
                    {id:"14", pspan:8, prop:"payment_amount"},
                    {id:"15", lspan:4, label:"付款方式"},
                    {id:"16", pspan:8, prop:"pay_mode"},
                    {id:"17", lspan:4, label:"状态"},
                    {id:"18", pspan:8, prop:"service_status"},
                    {id:"19", lspan:4, label:"摘要"},
                    {id:"20",pspan:8, prop:"payment_summary"}
                ],
            }
        },
        mounted:function(){
            //账户模式
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.InactiveMode) {
                this.interList = constants.InactiveMode;
            }
            //存款类型
            if (constants.DepositsMode) {
                this.depositsList = constants.DepositsMode;
            }
            //调拨类型
            if (constants.ZjdbType) {
                this.dbtTypeList = constants.ZjdbType;
            }
            //归集额度
            if(constants.CollOrPoolType){
                this.poolTypeList = constants.CollOrPoolType;
            }
            //归集频率
            if(constants.CollOrPoolFrequency){
                this.frequencyList = constants.CollOrPoolFrequency;
            }
            //状态
            if (constants.BillStatus) {
                this.billStatusList = constants.BillStatus;
            }
            //付款方式
            if (constants.PayMode) {
                this.PayModeList = constants.PayMode;
            }
            //业务类型下拉
            if(constants.MajorBizType){
                this.businessType = constants.MajorBizType;
            }
            
        },
        props: ["isPending", "tableData"],
        components:{
            Upload:Upload,
            BusinessTracking:BusinessTracking
        },
        data:function(){
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    todo: {
                        optype: "wfquery_pendingtasksall",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    },
                    done: {
                        optype: "wfquery_processtasksall",
                        params: {
                            page_size: 7,
                            page_num: 1,
                            assignee_id:this.$store.state.user.usr_id
                        }
                    }
                },
                doneTableList: [], //已办列表数据
                pagSize: 7, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                formLabelWidth:"120px",
                activeName: '0',
                tableHeadList:{//不同业务种类待办列表要显示的字段
                    "0":[
                        {id:'1',prop:"biz_type",name:'业务种类'},
                        {id:'2',prop:"bill_code",name:'单据编号'},
                        {id:'3',prop:"start_time",name:'申请日期'},
                        {id:'4',prop:"init_user_name",name:'发起人'},
                        {id:'5',prop:"init_dept_name",name:'部门'},
                        {id:'6',prop:"history",name:'上级处理人'},
                        {id:'7',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "1":[
                        {id:'1',prop:"apply_on",name:'申请日期'},
                        {id:'2',prop:"init_dept_name",name:'申请部门'},
                        {id:'3',prop:"init_org_name",name:'公司'},
                        {id:'4',prop:"memo",name:'事由摘要'},
                        {id:'5',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "2":[
                        {id:'1',prop:"apply_on",name:'申请日期'},
                        {id:'2',prop:"acc_no",name:'账户号'},
                        {id:'3',prop:"acc_name",name:'账户名称'},
                        {id:'4',prop:"curr_name",name:'币种'},
                        {id:'5',prop:"acc_attr",name:'账户性质'},
                        {id:'6',prop:"interactive_mode",name:'账户模式'},
                        {id:'7',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "3":[
                        {id:'1',prop:"apply_on",name:'申请日期'},
                        {id:'2',prop:"acc_no",name:'账户号'},
                        {id:'3',prop:"acc_name",name:'账户名称'},
                        {id:'4',prop:"curr_name",name:'币种'},
                        {id:'5',prop:"acc_attr",name:'账户性质'},
                        {id:'6',prop:"interactive_mode",name:'账户模式'},
                        {id:'7',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "4":[
                        {id:'1',prop:"apply_on",name:'申请日期'},
                        {id:'2',prop:"acc_no",name:'账户号'},
                        {id:'3',prop:"acc_name",name:'账户名称'},
                        {id:'4',prop:"curr_name",name:'币种'},
                        {id:'5',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "5":[
                        {id:'1',prop:"apply_on",name:'申请日期'},
                        {id:'2',prop:"acc_no",name:'账户号'},
                        {id:'3',prop:"acc_name",name:'账户名称'},
                        {id:'4',prop:"curr_name",name:'币种'},
                        {id:'5',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "6":[
                        {id:'1',prop:"apply_on",name:'申请日期'},
                        {id:'2',prop:"init_org_name",name:'公司'},
                        {id:'4',prop:"memo",name:'事由摘要'},
                        {id:'5',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "7":[
                        {id:'1',prop:"apply_on",name:'申请日期'},
                        {id:'2',prop:"acc_no",name:'账户号'},
                        {id:'3',prop:"acc_name",name:'账户名称'},
                        {id:'4',prop:"curr_name",name:'币种'},
                        {id:'5',prop:"acc_attr",name:'账户性质'},
                        {id:'6',prop:"interactive_mode",name:'账户模式'},
                        {id:'7',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "8":[
                        {id:'1',prop:"create_on",name:'申请日期'},
                        {id:'2',prop:"pay_account_no",name:'转出账户'},
                        {id:'3',prop:"recv_account_name",name:'收款公司'},
                        {id:'4',prop:"recv_account_no",name:'收款账号'},
                        {id:'5',prop:"payment_amount",name:'金额'},
                        {id:'6',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "9":[
                        {id:'1',prop:"recv_account_name",name:'收款方名称'},
                        {id:'2',prop:"recv_account_no",name:'收款方账号'},
                        {id:'3',prop:"recv_account_bank",name:'收款方银行'},
                        {id:'4',prop:"payment_amount",name:'金额'},
                        {id:'5',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "10":[//调拨通-批量
                        {id:'1',prop:"batchno",name:'批次号'},
                        {id:'2',prop:"pay_account_no",name:'付款方账号'},
                        {id:'3',prop:"pay_account_name",name:'付款方名称'},
                        {id:'4',prop:"pay_account_bank",name:'付款方银行'},
                        {id:'5',prop:"total_num",name:'总笔数'},
                        {id:'6',prop:"total_amount",name:'总金额'},
                        // {id:'7',prop:"success_num",name:'成功笔数'},
                        // {id:'8',prop:"success_amount",name:'成功金额'},
                        {id:'9',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "11":[
                        {id:'1',prop:"batchno",name:'批次号'},
                        {id:'2',prop:"pay_account_no",name:'付款方账号'},
                        {id:'3',prop:"pay_account_name",name:'付款方名称'},
                        {id:'4',prop:"pay_account_bank",name:'付款方银行'},
                        {id:'5',prop:"total_num",name:'总笔数'},
                        {id:'6',prop:"total_amount",name:'总金额'},
                        {id:'7',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "12":[
                        {id:'1',prop:"topic",name:'归集主题'},
                        {id:'2',prop:"collect_type",name:'归集额度'},
                        {id:'3',prop:"collect_amount",name:'归集金额'},
                        {id:'4',prop:"collect_frequency",name:'归集频率'},
                        {id:'5',prop:"collect_time",name:'归集时间'},
                        {id:'6',prop:"collect_main_account_count",name:'归集集户(个)'},
                        {id:'7',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "13":[
                        {id:'1',prop:"create_on",name:'申请日期'},
                        {id:'2',prop:"topic",name:'下拨主题'},
                        {id:'3',prop:"allocation_type",name:'下拨类型'},
                        {id:'4',prop:"allocation_frequency",name:'下拨频率'},
                        {id:'5',prop:"summary",name:'摘要'},
                        {id:'6',prop:"allocation_child_account_count",name:'下拨集户(个)'},
                        {id:'7',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "14":[
                        {id:'1',prop:"topic",name:'主题'},
                        {id:'2',prop:"gyl_allocation_type",name:'下拨额度'},
                        {id:'3',prop:"gyl_allocation_amount",name:'下拨金额'},
                        {id:'4',prop:"gyl_allocation_frequency",name:'下拨频率'},
                        {id:'5',prop:"gyl_allocation_time",name:'下拨时间'},
                        {id:'6',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "15":[
                        {id:'1',prop:"recv_account_name",name:'收款方名称'},
                        {id:'2',prop:"pay_account_no",name:'付款方账号'},
                        {id:'3',prop:"pay_account_bank",name:'付款方银行'},
                        {id:'4',prop:"receipts_amount",name:'金额'},
                        {id:'6',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "19":[
                        {id:'1',prop:"batchno",name:'批次号'},
                        {id:'2',prop:"recv_account_no",name:'收款方账号'},
                        {id:'3',prop:"recv_account_name",name:'收款方名称'},
                        {id:'4',prop:"recv_account_bank",name:'收款方银行'},
                        {id:'5',prop:"total_num",name:'总笔数'},
                        {id:'6',prop:"total_amount",name:'总金额'},
                        {id:'7',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "20":[
                        {id:'1',prop:"bill_no",name:'报销单申请号'},
                        {id:'2',prop:"org_name",name:'申请单位'},
                        {id:'3',prop:"pay_account_no",name:'付款方账号'},
                        {id:'4',prop:"pay_account_bank",name:'付款方银行'},
                        {id:'5',prop:"recv_account_name",name:'收款人姓名'},
                        {id:'6',prop:"recv_account_no",name:'收款方账号'},
                        {id:'7',prop:"recv_account_bank",name:'收款方银行'},
                        {id:'8',prop:"payment_amount",name:'收款金额'},
                        {id:'9',prop:"payment_summary",name:'摘要'},
                        {id:'10',prop:"nextUserList",name:'下级审批人'}
                    ],
                    "21":[
                        {id:'1',prop:"bill_no",name:'报销单申请号'},
                        {id:'2',prop:"org_name",name:'申请单位'},
                        {id:'3',prop:"pay_account_no",name:'付款方账号'},
                        {id:'4',prop:"pay_account_name",name:'付款方银行'},
                        {id:'5',prop:"recv_account_name",name:'收款人姓名'},
                        {id:'6',prop:"recv_account_no",name:'收款方账号'},
                        {id:'7',prop:"recv_account_bank",name:'收款方银行'},
                        {id:'8',prop:"payment_amount",name:'收款金额'},
                        {id:'9',prop:"payment_summary",name:'摘要'},
                        {id:'10',prop:"nextUserList",name:'下级审批人'}
                    ],
                },
                editableTabsList: {},
                totalTabNum: "",//我的待办条数
                bizType:{},//业务种类
                dialogVisible:false,
                dialogTitle:"开户事项申请",
                dialogData:{
                    encNumber:"",
                    account_info:{}
                },
                emptyFileList: [], //附件
                fileMessage: {
                    bill_id: "",
                    biz_type: 1
                },
                normalTriggerFile: false,
                specialTriggerFile: false,
                currentData:{},//当前查看的该条数据
                thirdFunVisible:false,//加签,同意，拒绝弹出框
                thirdFunData:{},
                thirdFunTitle:"",
                user_options:[],
                back_options:[
                    {id:"1",name:"返回提交人"},
                    {id:"2",name:"上级审批人"}
                ],
                searchData:{},
                businessType:[],
                normalBusinessParams:{},//业务状态追踪参数
                specialBusinessParams:{},//批量调拨等特殊业务状态追踪参数
                detailDialog:{},//详情弹出框
                currentDetailDialog:[],//当前详情弹出框
                classParams:{},// 待办列表optype
                interList:{},
                enclosureLWidth:4,
                enclosurePWidth:20,
                dbtTypeList:{},//调拨类型
                payAmountUp:"",//查看详情金额大写
                depositsList:[],//存款类型
                poolTypeList: {}, //归集金额
                frequencyList: {}, //归集频率
                billStatusList: {}, //状态
                PayModeList: {}, //付款方式
                isFromHome: false,//是否从首页进入
                isTop:false,//只有一个tab加样式
                selectionData: [],//选择加签或者同意的数据
                batchAllotVisible: false,
                curInnerTab: true,//调拨当前显示的tab
                detailTableList: [],
                detailTotal:{},//弹窗的表格汇总
                pagDeSize: 7, //弹窗分页数据
                pagDeTotal: 1,
                pagDeCurrent: 1,
                searchDetailData: {},//弹窗的表格查询条件
                curBiztype: "",//当前的biztype
            }
        },
        methods:{
            //获取tab集合
            getTabList:function(type){
                
                    this.$axios({
                        url: this.queryUrl + "commProcess",
                        method:"post",
                        data:{
                            optype: "wfquery_pendingtaskallnum"
                        }
                    }).then((result) =>{
                        if (result.data.error_msg) {
                            this.$message({
                                type: "error",
                                message: result.data.error_msg,
                                duration: 2000
                            });
                        }else{
                            var constantsBiz= this.businessType;
                            var data = result.data.data;
                            var list = data.pending_list;
                            var tableHead = this.tableHeadList;
                            this.totalTabNum = data.total_num;
                            var _tab_index = "";//为了tab样式做处理
                            //默认我的待办
                            var arrObject = {
                                "0":{
                                    title: '我的待办',
                                    name: '0',
                                    tableHead:tableHead[0],
                                    tableList:[],
                                    num: data.total_num
                                }
                            };
                            if (list.length>0) {
                                list.forEach((element,index) =>{
                                    var type = element.biz_type;
                                    if(type == this.activeName){
                                        _tab_index = index + 1;
                                    }
                                    arrObject[type]={
                                        title: constantsBiz[type],
                                        name: type +"",
                                        tableHead:tableHead[type],
                                        tableList:[],
                                        num: element.num,
                                    };
                                })
                            }
                            this.editableTabsList = arrObject;

                            if(this.isFromHome){//从home页跳过了
                                let rowBar = document.getElementsByClassName("el-tabs__active-bar")[0];
                                if(_tab_index==1){
                                    rowBar.style.left = "20px";
                                }
                                this.routerMessage.todo.params.page_num = 1;
                                this.routerMessage.todo.optype = this.classParams[this.activeName].list;
                                this.routerMessage.todo.params.biz_type = this.activeName;
                                this.$emit("getTableData", this.routerMessage);
                            }else if(!arrObject[this.activeName] || this.activeName == "0"){//如果没找到上次停留的tab，回到我的待办
                                this.comeBack();
                            }else if(type =='third'){
                                //同意，加签，拒绝后刷新列表
                                this.$emit("getTableData", this.routerMessage);
                            }
                            console.log(arrObject);
                        }
                    })
                
            },
            //展示格式转换
            transitionStatus: function (row, column, cellValue, index) {
                if(column.property === "biz_type"){//转换业务种类
                    if (this.businessType) {
                        return this.businessType[cellValue];
                    }
                }
                if(column.property === "service_status" || column.property === "status"){//转换业务状态
                    var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                    if (constants.BillStatus) {
                        return constants.BillStatus[cellValue];
                    }
                }
                if (column.property === "interactive_mode") {
                    return this.interList[cellValue];
                }
                if(column.property == 'pay_status'){
                     var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                    if (constants.PayStatus) {
                        return constants.PayStatus[cellValue];
                    }
                }
            },
            getAssignee: function (row, column, cellValue, index) {
                let len = cellValue.length;
                if(len>0){
                    return cellValue[len-1].assignee;
                }else{
                    return "";
                }
            },
            //处理表格千分位
            changeThousandth: function (row, column, cellValue, index) {
                if(cellValue){
                    return this.$common.transitSeparator(cellValue);
                }
            },
            //展示格式转换-归集额度
            collectType: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.CollOrPoolType) {
                    return constants.CollOrPoolType[cellValue];
                }
            },
            //展示格式转换-归集频率
            collectFrequency: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.CollOrPoolFrequency) {
                    return constants.CollOrPoolFrequency[cellValue];
                }
            },
            //处理弹出表格的金额展示问题
            tansss:function(value){
                if(value){
                    this.dialogData.payAmountUp = this.$common.transitText(value);
                    return this.$common.transitSeparator(value);
                }
            },
            //下级审批人的展示
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
            //付款方式展示
            payMode: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.PayMode) {
                    return constants.PayMode[cellValue];
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
                this.routerMessage.todo.params.page_size = val;
                this.routerMessage.todo.params.page_num = 1;
                this.routerMessage.done.params.page_size = val;
                this.routerMessage.done.params.page_num = 1;
                this.routerMessage.done.params.assignee_id = this.$store.state.user.usr_id;
                this.$emit("getTableData", this.routerMessage);
            },
            comeBack:function(){
                this.activeName = "0";
                document.getElementsByClassName("el-tabs__active-bar")[0].style.display="none";
                //请求全部数据
                this.routerMessage.todo.params.page_num = 1;
                this.routerMessage.todo.optype = this.classParams[0].list;
                this.$emit("getTableData", this.routerMessage);
            },
            handleClick:function(tab,event){
                //由于将我的待办写入了tab里，却又要隐藏掉，所以处理一下横线样式
                let id = tab.name;
                let rowBar = document.getElementsByClassName("el-tabs__active-bar")[0];
                if(tab.index == "1"){
                    rowBar.style.left = "20px";
                }else if(rowBar.style.left != "0px"){
                    rowBar.style.left="0px";
                }
                if(rowBar.style.display === "none")
                    rowBar.style.display="inline-block";
                //在这里tab.name就是biztype的值
                //待办列表的optype参数
                this.activeName = id;
                this.routerMessage.todo.optype = this.classParams[id].list;
                this.routerMessage.todo.params.page_num = 1;
                this.routerMessage.todo.params.biz_type = id;
                this.$emit("getTableData", this.routerMessage);
            },
            //加签
            showThirdDialog:function(type,number){
                this.thirdFunData = {};
                this.thirdFunData.number = number ? 'more' : '';
                if(type == 'addLots'){
                    this.thirdFunTitle = "加签";
                }else if(type == 'reject'){
                    this.thirdFunTitle = "拒绝";
                }else{
                    this.thirdFunTitle = "同意";
                }
                this.thirdFunData.type = type;
                this.thirdFunVisible = true;
            },
            viewDetail:function(row,index){
                let bizType = row.biz_type;
                let id = row.bill_id;
                let _index = this.activeName;
                if(bizType == 10 || bizType == 11 || bizType == 19){
                    this.specialBusinessParams = {};//清空数据
                    this.specialBusinessParams.biz_type = bizType;
                    this.specialBusinessParams.id = id;
                }else{
                    this.normalBusinessParams = {};
                    this.normalBusinessParams.biz_type = bizType;
                    this.normalBusinessParams.id = id;
                }
                
                this.curBiztype = bizType;
                this.dialogTitle = this.classParams[bizType].text;
                this.currentDetailDialog = this.detailDialog[bizType];
                //组装开户同意接口的参数
                this.currentData.index = index;
                this.currentData.id = id;
                this.currentData.define_id = row.define_id;

                

                //附件所占宽度
                // if( bizType === 1 || bizType === 6){
                // if(bizType === 6){
                //     this.enclosureLWidth = 6;
                //     this.enclosurePWidth = 18;
                // }else{
                //     this.enclosureLWidth = 4;
                //     this.enclosurePWidth = 20;
                // }
                this.currentData.wf_inst_id = _index == '0' ? row.id : row.inst_id;
                // let optype = this.classParams[bizType].detail;
                //详情接口参数
                var params = {};
                params.id = id;
                if(bizType == '10' || bizType == '11' || bizType == '19'){
                    params.batchno = row.batchno ? row.batchno : row.bill_code;
                }
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method:"post",
                    data:{
                        optype:this.classParams[bizType].detail,
                        params:params
                    }
                }).then((result) =>{
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
                        let data = result.data.data;
                        //组装开户同意接口的参数
                        this.currentData.service_status = data.service_status;
                        this.currentData.persist_version = data.persist_version;

                        if(bizType === 3){//账户变更的时候打平数据
                            let content = data.change_content;
                            let len = content.length;
                            for(let i = 0;i<len; i++){
                                if(!content[i]){
                                    continue;
                                }
                                else if(content[i].type == 1){
                                    data.old_acc_name = content[i].old_value;
                                    data.new_acc_name = content[i].new_value;
                                }else if(content[i].type == 2){
                                    data.old_org_name = content[i].old_value;
                                    data.new_org_name = content[i].new_value;
                                }else if(content[i].type == 3){
                                    data.old_bank_name = content[i].old_value;
                                    data.new_bank_name = content[i].new_value;
                                }else if(content[i].type == 4){
                                    data.old_lawfull_man = content[i].old_value;
                                    data.new_lawfull_man = content[i].new_value;
                                }else if(content[i].type == 5){
                                    data.old_curr_name = content[i].old_value;
                                    data.new_curr_name = content[i].new_value;
                                }else if(content[i].type == 6){
                                    data.old_acc_attr_name = content[i].old_value;
                                    data.new_acc_attr_name = content[i].new_value;
                                }else if(content[i].type == 7){
                                    data.old_interactive_mode = content[i].old_value;
                                    data.new_interactive_mode = content[i].new_value;
                                }else if(content[i].type == 8){
                                    data.old_acc_purpose_name = content[i].old_value;
                                    data.new_acc_purpose_name = content[i].new_value;
                                }
                            }
                        }else if(bizType === 7){//销户补录的时候处理销户交易数据
                            let content = data.additionals;
                            let len = content.length;
                            let str = "";
                            data.salesTransaction = "";
                            content.forEach((element,index) => {
                                str = str + element.comments + ":" + element.amount;
                                if(index!=len-1){
                                    str = str + ";";
                                }
                            });
                            data.salesTransaction = str;
                        }else if(bizType === 10 || bizType == 11 || bizType == 19){//批量调拨和支付or非直连归集
                            data.cancel_amount = this.$common.transitSeparator(data.cancel_amount);
                            data.failed_amount = this.$common.transitSeparator(data.failed_amount);
                            data.process_amount = this.$common.transitSeparator(data.process_amount);
                            data.saved_amount = this.$common.transitSeparator(data.saved_amount);
                            data.success_amount = this.$common.transitSeparator(data.success_amount);
                            data.total_amount = this.$common.transitSeparator(data.total_amount);
                        }
                        //组装查看弹出框数据
                        Object.assign(this.dialogData,data)
                        // this.dialogData = data;
                        this.dialogData.biz_type = bizType;
                        // this.dialogData.detail = data.detail;
                        // this.dialogData.memo = data.memo;
                        // this.dialogData.bill_code = data.service_serial_number;
                        //拿附件数据
                        console.log(this.dialogData)
                        this.fileMessage.bill_id = id;
                        this.fileMessage.biz_type = bizType;
                        
                        if(bizType == 10 || bizType == 11 || bizType == 19){
                            this.specialTriggerFile = !this.specialTriggerFile;
                            this.batchAllotVisible = true;
                            this.searchDetailData.batchno = data.batchno;
                            this.searchDetailData.biz_type = bizType;
                            this.getDetailTable(this.searchDetailData);
                        }else{
                            this.normalTriggerFile = !this.normalTriggerFile;
                            this.dialogVisible = true;
                        }

                    }
                })
            },
            //设置当前附件个数
            setFileList: function ($event) {
                let len = $event.length;
                this.dialogData.encNumber = len ? len : "";
            },
            //确认加签或同意或拒绝
            confirmThirdFun:function(){
                let type = this.thirdFunData.type;//加签？拒绝？同意？
                let number = this.thirdFunData.number;//批量？单笔？
                let _index = this.activeName == '0'? this.dialogData.biz_type : this.activeName;
                let message = "";
                let optype = "";
                let selData = this.selectionData;
                let batch_list = [];
                let memo = this.thirdFunData.assignee_memo;
                var lotParams = {};
                if(type == "addLots"){
                    //组装加签参数
                    let user = this.thirdFunData.user;
                    if(number){//批量同意参数
                        selData.forEach(ele =>{
                            let obj = {};
                            obj.define_id = ele.define_id;
                            obj.id= ele.bill_id;
                            obj.persist_version = ele.persist_version;
                            obj.service_status = ele.service_status;
                            obj.wf_inst_id = ele.inst_id;
                            obj.assignee_memo = memo;
                            obj.shadow_user_id = user.id;
                            obj.shadow_user_name = user.name;
                            batch_list.push(obj);
                        })
                        lotParams.batch_list = batch_list;
                        optype = this.classParams[_index]['more-addLots'];
                    }else{
                        this.currentData.shadow_user_id = user.id;
                        this.currentData.shadow_user_name = user.name;
                        this.currentData.assignee_memo = memo;
                        optype = this.classParams[_index].addLots;
                    }
                    message = "加签成功";
                }else if(type == "reject"){
                    this.currentData.assignee_memo = memo;
                    optype = this.classParams[_index].reject;
                    message = "拒绝成功";
                }else{
                    if(number){//批量同意参数
                        selData.forEach(ele =>{
                            let obj = {};
                            obj.define_id = ele.define_id;
                            obj.id= ele.bill_id;
                            obj.persist_version = ele.persist_version;
                            obj.service_status = ele.service_status;
                            obj.wf_inst_id = ele.inst_id;
                            obj.assignee_memo = memo;
                            batch_list.push(obj);
                        })
                        lotParams.batch_list = batch_list;
                        optype = this.classParams[_index]['more-agree'];
                    }else{
                        this.currentData.assignee_memo = memo;
                        optype = this.classParams[_index].agree;
                    }
                    message = "同意成功";
                }
                let paramsObj = number ? lotParams : this.currentData;
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method:"post",
                    data:{
                        optype:optype,
                        params:paramsObj
                    }
                }).then((result) =>{
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        });
                    }else{
                        this.$message({
                            type: "success",
                            message: message,
                            duration: 2000
                        });
                        this.getTabList('third');
                        if(this.curBiztype==10 || this.curBiztype==11 || this.curBiztype==19){
                            this.batchAllotVisible = false;
                        }else{
                            this.dialogVisible = false;
                        }
                        this.thirdFunVisible = false;
                    }
                })
            },
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                for (var k in searchData) {
                    this.routerMessage.done.params[k] = searchData[k];
                    this.routerMessage.done.params.page_num = 1;
                }
                this.$emit("getTableData", this.routerMessage);
            },
            hasParent:function(attr){
                var obj = this.dialogData.account_info;
                var result = "";
                if(obj){
                    result = obj[attr];
                    if(attr == 'deposits_mode'){
                        return this.depositsList[result];
                    }else if(attr == 'interactive_mode'){
                        return this.interList[result];
                    }else{
                        return result;
                    }
                }else{
                    return result;
                }

            },
            //转换下拨时间
            translateFrequency: function(list){
                var str = "";
                if(list && list.length>0){
                    list.forEach(element => {
                        if(element.allocation_frequency_detail){
                            str = str + element.allocation_frequency_detail + "," + element.allocation_time +";<br/>";
                        }else{
                            str = str + element.allocation_time +";<br/>";
                        }
                    });
                }
                return str;
            },
            //选择加签的或者同意的数据
            handleSelectionChange: function (val) {
                this.selectionData = val;
            },
            //切换弹框内tab
            changeTab: function (type) {
                // this.dialogTitle = type ? '批次汇总查看' : '批次汇总明细';
                this.curInnerTab = type;
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
            getDetailTable: function (params) {
                params.page_size = params.page_size ? params.page_size : 7;
                params.page_num = params.page_num ? params.page_num : 1;
                var optype = "";
                var bizType = params.biz_type;
                if(bizType == 10){
                    optype = "dbtbatch_detaillist";
                }else if(bizType == 11){
                    optype = "zftbatch_billdetaillist";
                }else if(bizType == 19){
                    optype = "ndc_billdetaillist";
                }
                this.$axios({
                    url: this.queryUrl + "normalProcess",
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
            //批量调拨查看弹框弹框关闭
            closeLookDialog: function(){
                this.pagDeSize = 7;
                this.pagDeTotal = 1;
                this.pagDeCurrent = 1;
                this.detailTableList = [];
                this.curInnerTab = true;
                this.searchDetailData = {};
            },
        },
        computed:{
            classObject:function (){
                return{
                    isTop: Object.keys(this.editableTabsList).length ==1? true : false
                }
            }
        },
        watch: {
            isPending: function (val, oldVal) {
                if(val){//已办
                    this.activeName = "0";
                    this.getTabList();
                }else{
                   this.routerMessage.todo.optype = "wfquery_pendingtasksall";
                }
            },
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                if(this.isPending){
                    var curTab = this.editableTabsList[this.activeName];
                    if(curTab)
                        curTab.tableList=val.data;
                    if(this.isFromHome){
                        this.isFromHome = false;
                    }
                }else{
                    this.doneTableList = val.data;
                }
            }
        }
    }
</script>
