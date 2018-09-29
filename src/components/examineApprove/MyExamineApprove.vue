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
                overflow-y: scroll;
            }
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
            <el-tabs class="myTab" v-model="activeName" @tab-click="handleClick" id="myTab">
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
                            <el-table-column v-else-if="head.prop=='payment_amount' || head.prop== 'total_amount' || head.prop== 'receipts_amount'"
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
                                label="操作" width="110"
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
        <!-- <div class="button-list" v-if="isPending" v-show="activeName!='0'">
            <el-button type="primary"
                plain
                size="small"
                @click=""
                icon="el-icon-circle-plus-outline">加签</el-button>
            <el-button type="warning"
                plain
                size="small"
                @click=""
                icon="el-icon-circle-check-outline">同意</el-button>
        </div> -->
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
                            :triggerFile="triggerFile"></Upload>
                    </el-col>
                </el-row>
            </div>
            <BusinessTracking
                :businessParams="businessParams"
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
            this.$emit("transmitTitle", "我的审批平台");
            // this.$emit("getTableData", this.routerMessage);
            //获取tab数据
            this.getTabList();
            //获取用户列表
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
                    reject:"openintent_reject"
                },
                "2":{
                    text:"开户信息补录",
                    detail:"opencom_detail",
                    list:"opencom_pendingtasks",
                    addLots:"opencom_append",
                    agree:"opencom_agree",
                    reject:"opencom_reject"
                },
                "3":{
                    text:"账户变更",
                    detail:"openchg_detail",
                    list:"openchg_pendingtasks",
                    addLots:"openchg_append",
                    agree:"openchg_agree",
                    reject:"openchg_reject"
                },
                "4":{
                    text:"账户冻结",
                    detail:"accfreeze_detail",
                    list:"accfreeze_pendingtasks",
                    addLots:"accfreeze_append",
                    agree:"accfreeze_agree",
                    reject:"accfreeze_reject"
                },
                "5":{
                    text:"账户解冻",
                    detail:"accdefreeze_detail",
                    list:"accdefreeze_pendingtasks",
                    addLots:"accdefreeze_append",
                    agree:"accdefreeze_agree",
                    reject:"accdefreeze_reject"
                },
                "6":{
                    text:"销户事项",
                    detail:"closeacc_detail",
                    list:"closeacc_pendingtasks",
                    addLots:"closeacc_append",
                    agree:"closeacc_agree",
                    reject:"closeacc_reject"
                },
                "7":{
                    text:"销户信息补录",
                    detail:"closeacccomple_detail",
                    list:"closeacccomple_pendingtasks",
                    addLots:"closeacccomple_append",
                    agree:"closeacccomple_agree",
                    reject:"closeacccomple_reject"
                },
                "8":{
                    text:"内部调拨",
                    detail:"dbt_detail",
                    list:"dbt_pendingtasks",
                    addLots:"dbt_append",
                    agree:"dbt_agree",
                    reject:"dbt_reject"
                },
                "9":{
                    text:"支付通",
                    detail:"zft_detail",
                    list:"zft_pendingtasks",
                    addLots:"zft_append",
                    agree:"zft_agree",
                    reject:"zft_reject"
                },
                "10":{
                    text:"内部调拨-批量",
                    detail:"dbtbatch_detail",
                    list:"dbtbatch_pendingtasks",
                    addLots:"dbtbatch_append",
                    agree:"dbtbatch_agree",
                    reject:"dbtbatch_reject"
                },
                "11":{
                    text:"支付通-批量",
                    detail:"zftbatch_detail",
                    list:"zftbatch_pendingtasks",
                    addLots:"zftbatch_append",
                    agree:"zftbatch_agree",
                    reject:"zftbatch_reject"
                },
                "12":{
                    text:"归集通",
                    detail:"collectsetting_detail",
                    list:"collectsetting_pendingtasks",
                    addLots:"collectsetting_append",
                    agree:"collectsetting_agree",
                    reject:"collectsetting_reject"
                },
                "13":{
                    text:"资金下拨",
                    detail:"allocset_detail",
                    list:"allocset_pendingtasks",
                    addLots:"allocset_append",
                    agree:"allocset_agree",
                    reject:"allocset_reject"
                },
                "14":{
                    text:"广银联备付金",
                    detail:"gylsetting_detail",
                    list:"gylsetting_pendingtasks",
                    addLots:"gylsetting_append",
                    agree:"gylsetting_agree",
                    reject:"gylsetting_reject"
                },
                "15":{
                    text:"收款通",
                    detail:"skt_billdetail",
                    list:"skt_pendingtasks",
                    addLots:"skt_append",
                    agree:"skt_agree",
                    reject:"skt_reject"
                }
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
                    {id:"11", lspan:4, label:"已失败笔数"},
                    {id:"12", pspan:8, prop:"failed_num"},
                    {id:"13", lspan:4, label:"已失败金额"},
                    {id:"14", pspan:8, prop:"failed_amount"},
                    {id:"15", lspan:4, label:"已成功笔数"},
                    {id:"16", pspan:8, prop:"sucess_num"},
                    {id:"17", lspan:4, label:"已成功金额"},
                    {id:"18", pspan:8, prop:"sucess_amount"},
                ],
                "12":[
                    {id:"1", lspan:4, label:"归集主题"},
                    {id:"2",pspan:20, prop:"topic"},
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
                    {id:"13", lspan:4, label:"业务状态"},
                    {id:"14", pspan:8, prop:"service_status"}
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
                    {id:"11", lspan:4, label:"业务状态"},
                    {id:"12", pspan:8, prop:"service_status"}
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
        },
        props: ["isPending", "tableData"],
        components:{
            Upload:Upload,
            BusinessTracking:BusinessTracking
        },
        data:function(){
            return {
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
                        {id:'1',prop:"pay_account_bank",name:'收款方名称'},
                        {id:'2',prop:"recv_account_no",name:'收款方账号'},
                        {id:'3',prop:"recv_account_name",name:'收款方银行'},
                        {id:'4',prop:"payment_amount",name:'金额'},
                        {id:'5',prop:"service_status",name:'处理状态'}
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
                        {id:'9',prop:"service_status",name:'批次状态'}
                    ],
                    "11":[
                        {id:'1',prop:"batchno",name:'批次号'},
                        {id:'2',prop:"total_num",name:'总笔数'},
                        {id:'3',prop:"total_amount",name:'总金额'},
                        {id:'4',prop:"sucess_num",name:'成功笔数'},
                        {id:'5',prop:"sucess_amount",name:'成功金额'},
                        {id:'6',prop:"service_status",name:'批次状态'}
                    ],
                    "12":[
                        {id:'1',prop:"topic",name:'归集主题'},
                        {id:'2',prop:"collect_type",name:'归集额度'},
                        {id:'3',prop:"collect_amount",name:'归集金额'},
                        {id:'4',prop:"collect_frequency",name:'归集频率'},
                        {id:'5',prop:"collect_time",name:'归集时间'},
                        {id:'6',prop:"collect_main_account_count",name:'归集集户(个)'},
                        {id:'7',prop:"service_status",name:'业务状态'}
                    ],
                    "13":[
                        {id:'1',prop:"create_on",name:'申请日期'},
                        {id:'2',prop:"topic",name:'下拨主题'},
                        {id:'3',prop:"allocation_type",name:'下拨类型'},
                        {id:'4',prop:"allocation_frequency",name:'下拨频率'},
                        {id:'5',prop:"summary",name:'摘要'},
                        {id:'6',prop:"allocation_child_account_count",name:'下拨集户(个)'}
                    ],
                    "14":[
                        {id:'1',prop:"topic",name:'主题'},
                        {id:'2',prop:"gyl_allocation_type",name:'下拨额度'},
                        {id:'3',prop:"gyl_allocation_amount",name:'下拨金额'},
                        {id:'4',prop:"gyl_allocation_frequency",name:'下拨频率'},
                        {id:'5',prop:"gyl_allocation_time",name:'下拨时间'},
                        {id:'6',prop:"service_status",name:'业务状态'}
                    ],
                    "15":[
                        {id:'1',prop:"recv_account_bank",name:'收款方名称'},
                        {id:'2',prop:"pay_account_no",name:'付款方账号'},
                        {id:'3',prop:"pay_account_name",name:'付款方银行'},
                        {id:'4',prop:"receipts_amount",name:'金额'},
                        {id:'5',prop:"service_status",name:'处理状态'}
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
                triggerFile: false,
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
                businessParams:{},//业务状态追踪参数
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
            }
        },
        methods:{
            //获取tab集合
            getTabList:function(){
                var _this = this;
                this.$axios({
                    url:"/cfm/commProcess",
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
                        var constantsBiz = JSON.parse(window.sessionStorage.getItem("constants")).MajorBizType;
                        this.businessType = constantsBiz;//业务类型下拉
                        var data = result.data.data;
                        var list = data.pending_list;
                        var tableHead = this.tableHeadList;
                        this.totalTabNum = data.total_num;
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
                                arrObject[type]={
                                    title: constantsBiz[type],
                                    name: type +"",
                                    tableHead:tableHead[type],
                                    tableList:[],
                                    num: element.num,
                                };
                            })
                        }else{
                            document.getElementById("myTab").style.top = "70px";
                            // _this.$refs.myTab.style.top = "70px";
                        }
                        if(!arrObject[this.activeName] || this.activeName == "0"){//如果没找到上次停留的tab，回到我的待办
                            this.comeBack();
                        }
                        this.editableTabsList = arrObject;
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
                    page_num: 1,
                    assignee_id:this.$store.state.user.usr_id
                };
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
                if(id == "1"){
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
            showThirdDialog:function(type){
                this.thirdFunData = {};
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
                this.businessParams = {};//清空数据
                let bizType = row.biz_type;
                this.dialogTitle = this.classParams[bizType].text;
                this.currentDetailDialog = this.detailDialog[bizType];
                let id = row.bill_id;
                let _index = this.activeName;
                //组装开户同意接口的参数
                this.currentData.index = index;
                this.currentData.id = id;
                this.currentData.define_id = row.define_id;

                this.businessParams.biz_type = bizType;
                this.businessParams.id = id;

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
                if(bizType == '10'){
                    params.batchno = row.batchno ? row.batchno : row.bill_code;
                }
                this.$axios({
                    url:"/cfm/normalProcess",
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
                        }
                        if(bizType === 7){//销户补录的时候处理销户交易数据
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
                        this.triggerFile = !this.triggerFile;
                        this.dialogVisible = true;
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
                let type = this.thirdFunData.type;
                let _index = this.dialogData.biz_type;
                let message = "";
                let optype = "";
                if(type == "addLots"){
                    let user = this.thirdFunData.user;
                    //组装加签参数
                    this.currentData.shadow_user_id = user.id;
                    this.currentData.shadow_user_name = user.name;
                    optype = this.classParams[_index].addLots;
                    message = "加签成功";
                }else if(type == "reject"){
                    optype = this.classParams[_index].reject;
                    message = "拒绝成功";
                }else{
                     optype = this.classParams[_index].agree;
                    message = "同意成功";
                }

                this.currentData.assignee_memo = this.thirdFunData.assignee_memo;
                let paramsObj = this.currentData;
                this.$axios({
                    url:"/cfm/normalProcess",
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

                        //同意，加签，拒绝后刷新列表
                        this.$emit("getTableData", this.routerMessage);

                        this.getTabList();
                        this.dialogVisible = false;
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
            }
        },
        watch: {
            isPending: function (val, oldVal) {
                if(val){//已办
                    this.activeName = "0";
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
                    curTab.tableList=val.data;
                    // curTab.num = val.total_line;
                    // if(val.total_line === 0){
                    //     delete this.editableTabsList[this.activeName];
                    //     this.activeName = "0";
                    //     var keysLen = Object.keys(this.editableTabsList).length;
                    //     if(keysLen === 1){
                    //         this.editableTabsList[0].num = 0;
                    //         this.editableTabsList[0].tableList = [];
                    //         document.getElementById("myTab").style.top = "70px";
                    //     }
                    // }
                }else{
                    this.doneTableList = val.data;
                }
            }
        }
    }
</script>
