<style lang="less" scoped>
    #workflowDefinition{
        /*搜索区*/
        .search-setion{
            margin-bottom: 20px;
            /*搜索区按钮*/
            .el-button--primary {
                color: #fff;
                background-color: #409EFF;
                border-color: #409EFF;
                border-radius: 0;
            }
        }

        /*顶部按钮*/
        .button-list-right {
            position: absolute;
            top: -42px;
            right: 0px;
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: 8px;
        }

        /*数据展示区*/
        .table-content {
            height: 362px;
        }
        /*按钮-复制*/
        .on-copy {
            width: 22px;
            height: 22px;
            background-image: url(../../assets/icon_common.png);
            background-position: -22px 1px;
            border: none;
            padding: 0;
            vertical-align: middle;
        }
        /*增加规则弹出框的样式*/
        .dialog-svg{
            width: 50px;
            height: 10px;
        }
        .svg-line{
            stroke: #ccc;
            stroke-width: 1;
            z-index: -1;
        }
        .rule-source{
            width: 30px;
            height: 30px;
            border-radius: 30px;
            background-color: #7bb9f2;
            color: #fff;
            display: inline-block;
            text-align: center;
            line-height: 30px;
            margin: 0 5px;
        }
        .ruler-input.el-input {
            width: 34%;
        }

        .showBtn{
            display: inline-block;
        }
        .scrBtn{
            display: none
        }
    }
</style>
<style lang="less">
    #diagramContainer {
        // border: 1px solid gray;
        width: 100%;
        height: 400px;
        position: relative;
        overflow: auto;
        .iconBg{
            background:url("../../assets/icon_common.png");
            background-repeat: no-repeat;
            display: inline-block;
        }
        .firstEnd{
            width: 50px;
            height: 50px;
            padding: 13px;
            border-radius: 30px;
            box-sizing: border-box;
            cursor: pointer;
            display: inline-block;
            vertical-align: top;
            position: absolute;
            span{
                width:25px;
                height: 25px;
                background-position: -18px -167px;
            }
        }
        #end_box{
            top: 100px;
            right: 0;
            border: 1px solid orange;
            span{
                background-position: -36px -369px;
            }
        }
        #root_box{
            border: 1px solid #80b2ea;
            top: 100px;
            span{
                background-position: -18px -167px;
            }
        }
        #root_box:hover{
            background-color: #80b2ea;
        }
        #root_box:hover span{
            background-position: -58px -168px;
            background-color: #80b2ea;
        }
        #item_1{
            top: 0px;
            left: 200px;
        }
        .child_item:hover{
            border: 1px solid #7bb9f2;
            .child_add{
                display: inline-block;
            }
        }
        .child_item{
            width: 200px;
            height: 150px;
            border-radius: 3px;
            display: inline-block;
            border: 1px solid #dadada;
            vertical-align: top;
            position: absolute;
            box-sizing: border-box;
            cursor: move;
            .child_no{
                content: ' ';
                border: 40px solid transparent;
                border-left: 0;
                border-top-color: #7bb9f2;
                display: inline-block;
                position: absolute;
                top: 0;
                left: 0;
                i{
                    font-style: initial;
                    color: #fff;
                    position: absolute;
                    top: -40px;
                    left: 4px;
                }
            }
            .child_delete{
                width: 16px;
                height: 18px;
                position: absolute;
                top: 7px;
                right: 7px;
                background-position: -47px -53px;
                cursor: pointer;
            }
            .child_user{
                width:100%;
                height: 30px;
                margin-top: 60px;
                border-radius: 3px;
                padding: 0 5px 0px 15px;
                box-sizing: border-box;
                vertical-align: top;
                position: relative;
                .el-input{
                    width:85%;
                }
                .el-input__inner {
                    height: 30px;
                    line-height: 30px;
                }
                i{
                    background-position: -185px -160px;
                }
                .userCheck{
                    width: 15px;
                    height: 20px;
                    background-position: -214px -164px;
                    vertical-align: top;
                    cursor: pointer;
                    position: absolute;
                    top: 3px;
                    right: 9px;
                }
            }
            .child_add{
                width: 25px;
                height: 40px;
                position: absolute;
                top: -2px;
                right: -24px;
                background-position: -95px -158px;
                cursor: pointer;
                display: none;
            }
            .check_redirect{
                width: 22px;
                height: 22px;
                background-position: -308px -164px;
                cursor: pointer;
                bottom: 5px;;
                right: 10px;
                position: absolute;
            }
            .child_org{
                width: 100%;
                padding-left: 24px;
                top: 20px;
                position: absolute;
                .el-input{
                    width:70%;
                }
                .el-input__inner {
                    height: 30px;
                    line-height: 30px;
                }
            }
            .child-select{
                margin-top: 4px;
                padding: 0 16px;
                position: relative;
                .child-users-container{
                    height: 25px;
                    overflow: hidden;
                    white-space: nowrap;
                    overflow: hidden;
                    display: inline-block;
                    position: relative;
                    z-index: 1;
                    width: 100%;
                    .child-users-source{
                        transition: all 1s;
                        -moz-transition: all 1s; /* Firefox 4 */
                        -webkit-transition: all 1s; /* Safari and Chrome */
                        -o-transition: all 1s; /* Opera */
                        display: inline-block;
                        .el-tag{
                            border-radius: 13px;
                            margin-right:5px;
                        }
                        .el-tag .el-icon-close {
                            height: 12px;
                            width: 12px;
                            line-height: 12px;
                            right: 0px;
                        }
                    }
                }
                .showBtn{
                    display: inline-block!important;
                }
                .scrBtn{
                    display: none;
                }
                .left-icon, .right-icon {
                    width: 10px;
                    height: 15px;
                    cursor: pointer;
                    border: 0;
                    position: absolute;
                    top: 4px;
                    outline: none;
                    // display: inline-block;
                }
                .left-icon {
                    left: 4px;
                    background-position: -326px -120px;
                }
                .right-icon {
                    right: 4px;
                    background-position: -363px -120px;
                }
            }
        }
        .displaynone{
            visibility: hidden!important;
        }
        .rule-icon{
            width: 20px;
            height: 20px;
            background-position: -253px -165px;
            cursor: pointer;
            display: none;
        }
        .item-label{
            cursor: pointer;
            width: 20px;
            height: 20px;
        }
        .jtk-hover .rule-icon {
            display: inline-block;
        }
        .rule-div{
            display: none;
            margin-left: -18px;
        }
        .rule-hasValue{
            margin-left: -18px;
        }
    }
    .ruler-input .el-input__inner {
        height: 30px;
        line-height: 30px;
    }
    .ruler-form{
        padding:30px 12px;
    }
    .ruler-form .el-select {
        width: 65% !important;
        margin-left: 15px;
    }
    .formflot{
        display: inline-block;
        .el-input{
            width:60%;
        }
        .el-input__inner {
            height: 30px;
            line-height: 30px;
        }
    }
</style>

<template>
    <div id="workflowDefinition">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="createProcess">新建流程</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="12" :offset="6">
                        <el-input placeholder="请输入内容" v-model="searchData.query_key" class="input-with-select">
                            <el-button slot="append" icon="el-icon-search" type="primary" @click="queryData"></el-button>
                        </el-input>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!--数据展示区-->
        <section :class="['table-content']">
            <el-table :data="tableList"
                      border
                      height="100%"
                      size="mini">
                <el-table-column prop="workflow_name" label="流程名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="create_on" label="创建时间" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="user_name" label="创建人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="150"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookFlow(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="复制" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button class="on-copy" size="mini"
                                       @click="editFlow(scope.row,'copy')"></el-button>
                        </el-tooltip>
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editFlow(scope.row,'edit')"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="delFlow(scope.row,scope.$index,tableList)"></el-button>
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
                    :current-page="pagCurrent"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange">
            </el-pagination>
        </div>
        <!--新建流程弹出框-->
        <el-dialog :visible.sync="createDialogVisible"
                   width="500px" title="新建流程"
                   :close-on-click-modal="false"
                   top="156px">
            <h1 slot="title" class="dialog-title">新建流程</h1>
            <el-form :model="createDialogData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="流程组名称">
                            <el-input v-model="createDialogData.workflow_name"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="审批退回">
                            <el-select v-model="createDialogData.reject_strategy">
                                <el-option
                                    v-for="item in back_options"
                                    :key="item.id"
                                    :label="item.name"
                                    :value="item.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="初始级次">
                            <el-select v-model="createDialogData.lanes">
                                <el-option
                                    v-for="item in gradation_options"
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
                <el-button type="warning" size="mini" plain @click="createDialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="showNext(createDialogData)">下 一 步</el-button>
            </span>
        </el-dialog>
        <!--下一步弹出框-->
        <el-dialog :visible.sync="nextStepDialogVisible"
                   width="960px" title="新建流程"
                   :close-on-click-modal="false"
                   :before-close="handleClose"
                   top="56px">
            <div slot="title" class="dialog-title">
                <div class="formflot">
                    <span>流程名称</span>
                    <el-input v-model="createDialogData.workflow_name"></el-input>
                </div>
                <div class="formflot">
                    <span>审批退回</span>
                    <el-select v-model="createDialogData.reject_strategy">
                        <el-option
                            v-for="item in back_options"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id">
                        </el-option>
                    </el-select>
                </div>
            </div>
            <div id="diagramContainer">
                <template  v-for="(item,index) in design_data">
                    <div v-if="item.item_id=='-1'" id="root_box" :key="item.item_id" class="firstEnd" @click="creatFirst">
                        <span class="iconBg"></span>
                    </div>
                    <div v-if="item.item_id=='-2'" id="end_box" :key="item.item_id" class="firstEnd">
                        <span class="iconBg"></span>
                    </div>
                    <div v-if="isFirstEnd(item)" :id="'item_'+item.item_id" draggable="true" class="child_item" :key="item.item_id">
                        <span class="child_no"><i class="child_num">{{item.item_id}}</i></span>
                        <div class="child_org" v-show="item.isOrg">
                            <el-select v-model="item.push_org" @change="selectPushOrg(item)">
                                <el-option
                                    v-for="org in org_options"
                                    :key="org.id"
                                    :label="org.name"
                                    :value="org.id">
                                </el-option>
                            </el-select>
                        </div>
                        <span class="child_delete iconBg" @click="deleteNode(design_data,item,index,$event)"></span>
                        <div class="child_user">
                            <el-select v-model="item['curUser']" @change="selectUsers(item,'user')" v-show="!item.isOrg">
                                <el-option
                                    v-for="user in user_list"
                                    :key="user.usr_id"
                                    :label="user.name"
                                    :value="user.usr_id">
                                </el-option>
                            </el-select>
                            <el-select v-model="item['curUser']" @change="selectUsers(item,'pos')" v-show="item.isOrg">
                                <el-option
                                    v-for="pos in position_list"
                                    :key="pos.pos_id"
                                    :label="pos.name"
                                    :value="pos.pos_id">
                                </el-option>
                            </el-select>
                            <span class="userCheck iconBg" @click="changeOrgUser(item)"></span>
                        </div>
                        <div class="child-select">
                            <div class="child-users-container">
                                <div class="child-users-source">
                                    <el-tag
                                        size="small"
                                        type="info"
                                        :key="tag.id"
                                        v-for="tag in item.addUsers"
                                        closable
                                        :disable-transitions="false"
                                        @close="deleteUser(tag.id,item,$event)">
                                        {{tag.name}}
                                    </el-tag>
                                </div>
                            </div>
                            <button class="iconBg left-icon scrBtn" @click="leftMove($event)"></button>
                            <button class="iconBg right-icon scrBtn" @click="rightMove($event)"></button>
                        </div>
                        <div class="child_add iconBg" @click="addChild(item.n_column,item.axis_x,item.item_id)"></div>
                        <div class="check_redirect iconBg" @click="selectFlow(item.n_column,item.item_id)"></div>
                    </div>
                    <!-- <div id="end_box" class="firstEnd"><span class="iconBg"></span></div> -->
                </template>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="cancelWorkflow">取 消</el-button>
                <el-button type="warning" size="mini" @click="saveWorkflow">下 一 步</el-button>
            </span>
        </el-dialog>
        <!--添加规则弹出框-->
        <el-dialog :visible.sync="addRuleDialogVisible"
                   width="420px" title="添加规则"
                   :close-on-click-modal="false"
                   top="156px">
            <h1 slot="title" class="dialog-title">流程走向条件[单据金额]</h1>
            <el-form :model="addRuleCurData" size="small"
                     :label-width="formLabelWidth"
                     class="ruler-form">
                     <svg class="dialog-svg">
                        <line x1="0" y1="5" x2="50" y2="5" class="svg-line" stroke-dasharray="3"></line>
                        <path d="M50 5 L 42 0 L 42 10 L 50 5 z" fill="#ccc"></path>
                    </svg>
                    <span class="rule-source">{{addRuleCurData.item_id}}</span>
                    <el-input class="ruler-input" v-model="addRuleCurData.min" placeholder="大于等于" @blur="validateNum(addRuleCurData.min,'min')" clearable></el-input>
                    -
                    <el-input class="ruler-input" v-model="addRuleCurData.max" placeholder="小于" @blur="validateNum(addRuleCurData.max,'max')" clearable></el-input>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" @click="saveRules(addRuleCurData)">确 定</el-button>
            </span>
        </el-dialog>
        <!--选择流程后续走向弹出框-->
        <el-dialog :visible.sync="selectFlowDialogVisible"
                   width="420px" title="选择流程后续走向"
                   :close-on-click-modal="false"
                   top="156px">
            <h1 slot="title" class="dialog-title">选择流程后续走向</h1>
            <el-form :model="selectFlowData" size="small"
                     :label-width="formLabelWidth"
                     class="ruler-form">
                     <span class="rule-source">{{selectFlowData.item_id}}</span>
                     <svg class="dialog-svg">
                        <line x1="0" y1="5" x2="50" y2="5" class="svg-line" stroke-dasharray="3"></line>
                        <path d="M50 5 L 42 0 L 42 10 L 50 5 z" fill="#ccc"></path>
                    </svg>
                    <el-select v-model="selectFlowData.target_id">
                        <el-option
                            v-for="item in select_flow"
                            :key="item.id"
                            :label="item.name"
                            :value="item.id">
                        </el-option>
                    </el-select>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" @click="connectFlow">确 定</el-button>
            </span>
        </el-dialog>
        <!--查看工作流弹出框-->
        <el-dialog :visible.sync="lookFlowDialogVisible"
                   width="800px" title="新建流程"
                   :close-on-click-modal="false"
                   :before-close="cancelLookFlow"
                   top="56px">
            <h1 slot="title" class="dialog-title">查看流程</h1>
            <div>
                <div class="formflot" style="margin-bottom:15px">
                    <span>流程名称</span>
                    <el-input size="mini" v-model="createDialogData.workflow_name" disabled></el-input>
                </div>
                <div class="formflot">
                    <span>审批退回</span>
                    <el-input size="mini" v-model="createDialogData.reject_strategy" disabled></el-input>
                </div>
            </div>
            <WorkFlow
                :flowList="flowList"
                :isEmptyFlow="isEmptyFlow"
            ></WorkFlow>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="cancelLookFlow">取 消</el-button>
            </span>
        </el-dialog>
    </div>
</template>
<script>
import "../../js/jsPlumb-2.2.6.js"
import WorkFlow from "../publicModule/WorkFlow.vue";

export default {
    name:"WorkflowDefinition",
    created:function(){
        this.$emit("transmitTitle","流程定义");
        this.$emit("getTableData", this.routerMessage);

        this.$axios({
            url: this.queryUrl + "adminProcess",
            method:"post",
            data:{
                optype:"usr_list",
                params:{
                    page_size:10000,
                    page_num:1
                }
            }
        }).then((result) =>{
            this.user_list = result.data.data;
        });
        this.$axios({
            url: this.queryUrl + "adminProcess",
            method:"post",
            data:{
                optype:"position_list",
                params:{
                    page_size:10000,
                    page_num:1
                }
            }
        }).then((result) =>{
           this.position_list = result.data.data;
        })

    },
    props:["tableData"],
    components: {
        WorkFlow: WorkFlow
    },
    data:function(){
        return {
            queryUrl: this.$store.state.queryUrl,
            routerMessage: {
                optype:"wfdefine_list",
                params: {
                    page_size: 10,
                    page_num: 1
                }
            },
            formLabelWidth: "120px",
            searchData: {},
            tableList: [],
            pagSize: 10,
            pagTotal: 1,
            pagCurrent: 1,
            createDialogVisible: false,
            createDialogData: {
                reject_strategy:"",
                lanes:"",
                workflow_name:""
            },
            back_options:[
                {id:1,name:'初始提交人'},
                {id:2,name:'上级审批人'},
                //{id:3,name:'自定义'}
            ],
            gradation_options:[
                {id:1,name:'1'},
                {id:2,name:'2'},
                {id:3,name:'3'},
                {id:4,name:'4'},
                {id:5,name:'5'}
            ],
            nextStepDialogVisible:false,
            design_data:[],
            line_data:[],
            lineStyle:{
                Anchor: ["Right", "Left"],
                PaintStyle: {
                    strokeWidth: 1,
                    outlineWidth: 10,
                    outlineStroke: "transparent",
                    "stroke-dasharray": [3, 1],
                    stroke: '#7bb9f2'
                },
                HoverPaintStyle: {
                    strokeWidth: 1,
                    stroke: '#7bb9f2'
                },
                Endpoint: ["Dot", {cssClass: "displaynone" }],
                Connector: ["Straight"],
                Container: "diagramContainer",
                ConnectionOverlays: [
                    ["PlainArrow", {
                        location: 1,
                        width: 10,
                        length: 10
                    }]
                ]
            },
            org_options:[
                {id:'+3',name:'上三级'},
                {id:'+2',name:'上二级'},
                {id:'+1',name:'上一级'},
                {id:'0',name:'本公司'},
                {id:'-1',name:'下一级'},
                {id:'-2',name:'下二级'},
                {id:'-3',name:'下三级'}
            ],
            position_list:[],
            user_list:[],
            // addUsers:[],//已选择用户
            org:"1",
            addRuleDialogVisible:false,
            addRuleCurData:{},//当前规则弹框的数据
            matrixArr:{}, //存放每列的数据
            selectFlowDialogVisible:false,//选择后续流程走向
            selectFlowData:{},
            select_flow:[],//可以选择的流程走向
            selectLineId:"",//当前连线的id，因为弹框只有一个拿不到线的数字框的id
            flowBase:{},//代表是新增工作流还是修改工作流
            lookFlowDialogVisible:false,
            flowList:{},//查看详情的工作流数据
            isEmptyFlow:false,//是否清空子组件的数据
            curRow: {},//当前列表的一条数据
            validateFlag:true
        }
    },
    methods:{
        //点击页数获取当前页数据
        getCurrentPage:function(currPage){
            if(this.isPending){
                this.routerMessage.params.page_num = currPage;
            }else{
                this.routerMessage.params.page_num = currPage;
            }
            this.$emit("getTableData", this.routerMessage);
        },
        //当前页数据条数发生变化
        sizeChange:function(val){
            this.routerMessage.params.page_size = val;
            this.routerMessage.params.page_num = 1;
            this.$emit("getTableData", this.routerMessage);
        },
        //查询数据
        queryData:function(){
            this.routerMessage.params.page_num = 1;
            this.routerMessage.params.query_key = this.searchData.query_key;
            this.$emit("getTableData", this.routerMessage);
        },
        //新建流程
        createProcess:function(){
            this.curRow = {};
            //清空流程名
            this.createDialogData.workflow_name = "";
            //设置默认值
            this.createDialogData.reject_strategy = 1;
            this.createDialogData.lanes = 1;
            this.createDialogVisible = true;
        },
        //打开下一步
        showNext:function(curdata){

            if(curdata.workflow_name){
                //关闭新建弹框
                this.createDialogVisible = false;

                var initalNum = curdata.lanes * 1;
                for(let i =0;i<initalNum;i++){
                    let obj_design ={
                        axis_y:0,
                        n_row:"1",
                        isOrg:false,
                        curUser:"",
                        push_org:"",
                        addUsers:[]
                    }
                    let obj_line ={
                        rule:""
                    }
                    obj_design["axis_x"] = 200 + i*300;
                    obj_design["n_column"] = i + 1 + "";
                    obj_design["item_id"] = i + 1 + "";
                    obj_line["d_source_id"] = i + "";
                    if(i===0){
                        obj_line["d_source_id"] = "-1" ;
                    }
                    this.matrixArr[i+1] = [];
                    this.matrixArr[i+1].push(i+1);
                    obj_line["d_target_id"] = i + 1 + "";
                    this.design_data.push(obj_design);
                    this.line_data.push(obj_line);
                }
                //初始化开始节点和结束节点,追加到数据结尾，减少影响
                this.design_data.push({item_id: "-1"});
                this.design_data.push({item_id: "-2"});

                this.nextStepDialogVisible = true;

                setTimeout(() =>{
                    this.jsplumb = jsPlumb.getInstance(this.lineStyle);
                    //初始化显示默认节点的位置
                    for (let j = 0;j<initalNum;j++){
                        // var newLength = 0 ;
                        var curId = "item_" + this.design_data[j].item_id;
                        var firstChild = document.getElementById(curId);
                        firstChild.style.top = this.design_data[j].axis_y+ "px";
                        firstChild.style.left = this.design_data[j].axis_x + "px";
                        if(j === 0){
                            var sourceId = "root_box";
                        }else{
                            var sourceId = "item_" + j ;
                        }
                        var targetId = "item_" + (j+1);
                        this.jsplumb.connect({
                            source: sourceId,
                            target: targetId,
                            overlays: [
                                ["Custom", {
                                    create:  (component)=> {
                                        var element = document.createElement("div");
                                        var elementChild = document.createElement("span");
                                        var elementChildSe = document.createElement("div");
                                        //首次初始化线的id值为循环变量j+1
                                        // var svg = document.getElementById("diagramContainer").getElementsByTagName("svg");
                                        // var length = svg.length;
                                        // for(var n = 0 ;n < length ;n ++){
                                        //     if( svg[n].parentNode.id === "diagramContainer"){
                                        //         newLength ++;
                                        //     }
                                        // }
                                        elementChild.className = "iconBg rule-icon";
                                        // elementChild.setAttribute("nodeId","item_1");
                                        elementChild.setAttribute("id",'addRule_'+ (j+1));
                                        elementChildSe.setAttribute("id",'ruleChange_'+ (j+1));
                                        elementChildSe.className = "rule-div";
                                        element.appendChild(elementChild);
                                        element.appendChild(elementChildSe);
                                        return element;
                                    },
                                    location: 0.5,
                                    cssClass: "item-label"
                                }]
                            ]
                        })
                        document.getElementById("addRule_"+(j+1)).onclick=(event)=>{
                            //清空弹出框数据
                            this.addRuleCurData = {};
                            this.addRuleDialogVisible = true;
                            if(j===0){
                                this.addRuleCurData.source_id = "-1";
                            }else{
                                this.addRuleCurData.source_id = j + "";
                            }

                            this.addRuleCurData.item_id = this.design_data[j].item_id;
                            this.selectLineId = event.target.id;
                        }
                        // document.getElementById(curId).onmouseup = function(event) {
                        //     // 存储拖拽数据和拖拽效果...
                        //     // event.dataTransfer.setData("Text",ev.target.id);
                        //     console.log("111")
                        // }
                        // document.getElementById(curId).onmousedown = function(event) {
                        //     // 存储拖拽数据和拖拽效果...
                        //     // event.dataTransfer.setData("Text",ev.target.id);
                        //     console.log(event.target.offsetLeft,event.target.offsetTop);
                        // }
                        this.jsplumb.draggable(targetId);
                        this.jsplumb.draggable("end_box");
                    }
                },0)
            }else{
                this.$message({
                    type: "warning",
                    message: "请输入流程名称",
                    duration: 2000
                })
                return ;
            }
        },
        //点击创建第一列的节点
        creatFirst:function(){

            //每点击一次该列数据增加一条
            var top = this.matrixArr[1].length * 180;

            var list = this.design_data;
            var len = list.length - 2;
            var item_id = (len == 0) ? 1 : list[len-1].item_id*1 + 1;
            var newId = "item_" + item_id; //取最后一个元素的item_id
            this.matrixArr[1].push(item_id);
            //组织点数据,因为多了两条首尾数据，所以要在指定位置增加点数据
            list.splice(len, 0, {axis_x: 200,
                axis_y: top,
                n_column: "1",
                n_row: this.matrixArr[1].length,
                item_id: item_id + "",
                isOrg:false,
                curUser:"",
                push_org:"",
                addUsers:[]
            });
            //组织线数据
            this.line_data.push(
                {
                    d_source_id: "-1",
                    d_target_id: item_id+"",
                    rule:""
                }
            )
            setTimeout(() =>{
                //初始化显示默认节点的位置
                var newChild = document.getElementById(newId);
                newChild.style.top = top + "px";
                newChild.style.left = "200px" ;

                var newLength = 0 ;
                this.jsplumb.connect({
                    source: 'root_box',
                    target: newId,
                    overlays: [
                            ["Custom", {
                                create:  (component)=> {
                                    var element = document.createElement("div");
                                    var elementChild = document.createElement("span");
                                    var elementChildSe = document.createElement("div");
                                    var svg = document.getElementById("diagramContainer").getElementsByTagName("svg");
                                    var length = svg.length;
                                    for(var n = 0 ;n < length ;n ++){
                                        if( svg[n].parentNode.id === "diagramContainer"){
                                            newLength ++;
                                        }
                                    }
                                    elementChild.className = "iconBg rule-icon";
                                    // elementChild.setAttribute("nodeId","item_1");
                                    elementChild.setAttribute("id",'addRule_'+newLength);
                                    elementChildSe.setAttribute("id",'ruleChange_'+newLength);
                                    elementChildSe.className = "rule-div";
                                    element.appendChild(elementChild);
                                    element.appendChild(elementChildSe);
                                    return element;
                                },
                                location: 0.5,
                                cssClass: "item-label"
                            }]
                    ]
                })
                document.getElementById("addRule_"+newLength).onclick=(event)=>{
                    //清空弹出框数据
                    this.addRuleCurData = {};
                    this.addRuleDialogVisible = true;
                    this.addRuleCurData.source_id = "-1";
                    this.addRuleCurData.item_id = item_id + "";
                    this.selectLineId = event.target.id;
                }
                this.jsplumb.draggable(newId);
            },0)
        },
        //添加其他列的节点
        addChild:function(column,x,id){
            //如果该列有数据，增加该列数据的个数
            //如果该列没有数据，增加this.matrixArr新push一列
            column = Number(column);
            var top = 0;
            var left = x + 300;
            var list = this.design_data;
            var len = list.length - 2;//因为多了首尾两条数据
            var item_id = list[len-1].item_id*1 + 1;
            var curId = "item_" + id;
            var newId = "item_" + item_id;
            this.matrixArr[column+1] = this.matrixArr[column+1] ? this.matrixArr[column+1] : [];
            if(this.matrixArr[column+1] && this.matrixArr[column+1].length>0){//如果新增列有数据(不可能为0)
                top = this.matrixArr[column+1].length * 180;
            }
            this.matrixArr[column+1].push(item_id);
            //组织点数据,因为多了两条首尾数据，所以要在指定位置增加点数据
            list.splice(len, 0, {axis_x: left,
                axis_y: top,
                n_column: column + 1,
                n_row: this.matrixArr[column+1].length,
                item_id: item_id + "",
                isOrg: false,
                curUser: "",
                push_org: "",
                addUsers: []
            });
            //组织线数据
            this.line_data.push(
                {
                    d_source_id: id,
                    d_target_id: item_id+"",
                    rule:""
                }
            )
            setTimeout(() =>{
                //初始化显示默认节点的位置
                var newChild = document.getElementById(newId);
                newChild.style.top = top + "px";
                newChild.style.left = left + "px" ;

                var newLength = 0 ;
                this.jsplumb.connect({
                    source: curId,
                    target: newId,
                    overlays: [
                        ["Custom", {
                            create:  (component)=> {
                                var element = document.createElement("div");
                                var elementChild = document.createElement("span");
                                var elementChildSe = document.createElement("div");
                                var svg = document.getElementById("diagramContainer").getElementsByTagName("svg");
                                var length = svg.length;
                                for(var n = 0 ;n < length ;n ++){
                                    if( svg[n].parentNode.id === "diagramContainer"){
                                        newLength ++;
                                    }
                                }
                                elementChild.className = "iconBg rule-icon";
                                // elementChild.setAttribute("nodeId","item_1");
                                elementChild.setAttribute("id",'addRule_'+newLength);
                                elementChildSe.setAttribute("id",'ruleChange_'+newLength);
                                elementChildSe.className = "rule-div";
                                element.appendChild(elementChild);
                                element.appendChild(elementChildSe);
                                return element;
                            },
                            location: 0.5,
                            cssClass: "item-label"
                        }]
                    ]
                })


                document.getElementById("addRule_"+newLength).onclick=(event)=>{
                    //清空弹出框数据
                    this.addRuleCurData = {};

                    this.addRuleDialogVisible = true;
                    this.addRuleCurData.source_id = id + "";
                    this.addRuleCurData.item_id = item_id + "" ;
                    this.selectLineId = event.target.id;
                }
                this.jsplumb.draggable(newId);
            },100)
        },
        //保存规则
        saveRules:function(curData){
            if(!this.validateFlag){
                this.validateFlag = true;
                return ;
            }
            var min = curData.min;
            var max = curData.max;
            var id = this.selectLineId.split("_")[1];
            var curShowEle = document.getElementById("ruleChange_" + id);
            if((min || min === '0') || (max || max ==='0')){
                if((min || min === '0') && (max || max ==='0') && Number(max)<Number(min)){
                    this.$message({
                        message: '最大金额不能小于最小金额！',
                        type: 'warning'
                    });
                    return ;
                }
                var arrList = this.line_data;
                var len = arrList.length;
                var str = "";
                var dealMoney = this.dealRuleMoney(min,max);
                for(var i = 0; i<len;i++){
                    if(arrList[i].d_target_id === curData.item_id && arrList[i].d_source_id === curData.source_id){
                        str = dealMoney.showStr;
                        arrList[i].rule = dealMoney.saveStr;
                        break;
                    }
                }
                this.addRuleDialogVisible = false;
                curShowEle.innerText = str;
                if(!curData.isEdit){//新建工作流时，添加新规则时，要隐藏加号，显示规则，修改的时候如果原来有规则则不需要
                    curShowEle.style.display = "inline-block";
                    curShowEle.previousElementSibling.className = "";
                    curShowEle.onclick = (event)=>{
                        this.addRuleDialogVisible = true;
                        this.addRuleCurData.item_id =  curData.item_id;
                        this.addRuleCurData.source_id = curData.source_id;
                        this.addRuleCurData.min =  min ? min : '';
                        this.addRuleCurData.max =  max ? max : '';
                        this.selectLineId = event.target.id;
                    }
                }
            }else{
                var arrList = this.line_data;
                var len = arrList.length;
                for(var i = 0; i<len;i++){
                    if(arrList[i].d_target_id === curData.item_id && arrList[i].d_source_id === curData.source_id){
                        arrList[i].rule = "";
                        break;
                    }
                }
                curShowEle.style.display = "none";
                curShowEle.innerText = "";
                curShowEle.previousElementSibling.className = "iconBg rule-icon";
                curShowEle.previousElementSibling.onclick = (event)=>{
                    this.addRuleCurData.min =  "";
                    this.addRuleCurData.max =  "";
                    this.selectLineId = event.target.id;
                    this.addRuleCurData.isEdit = false;
                    this.addRuleDialogVisible = true;
                }
                this.addRuleDialogVisible = false;
                return;
            }
        },
        //处理保存金额规则
        dealRuleMoney:function(min,max){
            let obj = {};
            if((min || min === 0) && (max || max === 0)){
                obj.showStr = min + '~' + max;
                obj.saveStr = '${AMOUNT}~${'+ min + ',' + max + '}';
            }else if(min || min === 0){
                obj.showStr = '>=' + min;
                obj.saveStr = '${AMOUNT}>=${'+ min + '}';
            }else{
                obj.showStr = '<' + max;
                obj.saveStr = '${AMOUNT}<${' + max + '}';
            }
            // ${AMOUNT}~${0,10000}
            // ${AMOUNT}>=${1000}
            // ${AMOUNT}<${100}
            return obj;
        },
        //反显金额规则
        translateRuleMoney:function(str){
            let result = {};
            if(str){
                let f_index = str.lastIndexOf('{') + 1;
                let e_index = str.lastIndexOf('}');
                let str_re= str.substring(f_index, e_index);
                if(str.indexOf('~') > 0){
                    let arr = str_re.split(',');
                    result.str = arr[0] + '~' + arr[1];
                    result.min = arr[0];
                    result.max = arr[1];
                }else if(str.indexOf('>') > 0){
                    result.str = '>=' + str_re;
                    result.min = str_re;
                    result.max = "";
                }else{
                    result.str = '<' + str_re;
                    result.max = str_re;
                    result.min = "";
                }
            }else{
                result.str = "";
                result.min = "";
                result.max = "";
            }
            return result;
        },
        //左移
        leftMove:function(event){
            var moveWidth = 166;
            var newWidth = 0;
            var curMove = event.currentTarget.parentNode.firstElementChild.firstChild;
            var curWidth = curMove.offsetWidth;
            var hasMove = curMove.style.marginLeft;
            hasMove = hasMove.split("p")[0]*1;
            var num = Math.floor(curWidth / moveWidth);
            if(hasMove < -moveWidth){
                newWidth = hasMove + moveWidth;
                curMove.style.marginLeft = newWidth + "px";
            }else if(hasMove>-moveWidth && hasMove<0){
                curMove.style.marginLeft = "0px";
            }else{
                return ;
            }
        },
        //右移
        rightMove:function(event){
            var moveWidth = 166;
            var newWidth = 0;
            var curMove = event.currentTarget.parentNode.firstElementChild.firstChild;
            var curWidth = curMove.offsetWidth;
            var hasMove = curMove.style.marginLeft;
            hasMove = hasMove.split("p")[0]*1;
            var num = Math.floor(curWidth / moveWidth) - 1;
            var rest = curWidth % moveWidth;
            if(curWidth>moveWidth){
                if(Math.abs(hasMove)<num*moveWidth){
                    newWidth = hasMove - moveWidth;
                    curMove.style.marginLeft = newWidth + "px";
                }else if(Math.abs(hasMove)===num*moveWidth && rest){
                    newWidth = hasMove - rest;
                    curMove.style.marginLeft = newWidth + "px";
                }else{
                    return ;
                }
            }else{
                return ;
            }
        },
        //修改机构
        selectPushOrg: function(item){
            item.data.push_org = item.push_org;
        },
        //选择用户or机构
        selectUsers:function(item,type){
            let id = item.curUser;
            let obj = {};
            let arrList,key,index;
            let flag = false;
            //重复选择的判断
            if(item.data){
                if(item.data.users && item.data.users.indexOf(id) > -1){
                    flag = true;
                }else if(item.data.position && item.data.position.indexOf(id) > -1){
                    flag = true;
                }
            }

            if(flag){
                this.$message({
                    message: '不能重复选择！',
                    type: 'warning'
                });
                item.curUser = "";
                return ;
            }

            if(type === 'user'){
                arrList = this.user_list;
                key = "usr_id";
            }else{
                arrList = this.position_list;
                key = "pos_id";
            }

            let len = arrList.length;
            for(let i=0;i<len;i++){
                if(arrList[i][key] === id){
                    obj.name = arrList[i].name;
                    obj.id = arrList[i][key];
                    break;
                }
            }
            item.addUsers = item.addUsers ? item.addUsers :[];
            index = item.addUsers.length;
            // this.$set(item.addUsers,0,obj);
            item.addUsers.push(obj);

            setTimeout(() =>{
               //是否显示左右按钮(这里的时候，新添加的dom还没有追加上，所以写在settimeout中)
                let curDom = document.getElementById("item_"+item.item_id);
                let conDomW = curDom.getElementsByClassName("child-users-container")[0].offsetWidth;
                let scrDomW = curDom.getElementsByClassName("child-users-source")[0].offsetWidth;
                if(scrDomW > conDomW){
                    let btn = curDom.getElementsByClassName("scrBtn");
                    btn[0].classList.add("showBtn");
                    btn[1].classList.add("showBtn");
                }
            },0)
            item.data = item.data ? item.data : {};
            if(item.isOrg){
                item.data.user_type = "1";
                item.data.push_org = item.push_org;
                item.data.position= item.data.position ? item.data.position : [];
                item.data.position.push(id);
            }else{
                item.data.user_type = "0";
                item.data.users= item.data.users ? item.data.users : [];
                item.data.users.push(id);
            }
            item.curUser = "";
        },
        //删除用户
        deleteUser:function(tag,item,event) {
            let list = item.addUsers;
            let len = list.length;
            for(let j=0;j<len;j++){
                if(list[j].id === tag){
                   item.addUsers.splice(j,1);
                   break;
                }
            }
            if(item.isOrg){
                item.data.position.splice(item.data.position.indexOf(tag), 1);
            }else{
                item.data.users.splice(item.data.users.indexOf(tag), 1);
            }

            let curWidth = event.currentTarget.parentNode.offsetWidth;
            let curDom = document.getElementById("item_"+item.item_id);
            let conDomW = curDom.getElementsByClassName("child-users-container")[0].offsetWidth;
            let scrDomW = curDom.getElementsByClassName("child-users-source")[0].offsetWidth;
            if(scrDomW - curWidth < conDomW){
                let btn = curDom.getElementsByClassName("scrBtn");
                btn[0].classList.remove("showBtn");
                btn[1].classList.remove("showBtn");
            }
            if(conDomW - scrDomW + curWidth > 0){
                curDom.getElementsByClassName("child-users-source")[0].style.marginLeft = 0 +"px";
            }else{
                curDom.getElementsByClassName("child-users-source")[0].style.marginLeft = conDomW - scrDomW + curWidth +"px";
            }
        },
        //切换机构或用户
        changeOrgUser:function(item){
            item.isOrg = !item.isOrg;
            item.addUsers = [];
            item.data = {};

            //组织点数据的时候得声明这两个变量，否则会造成选不上的诡异现象
            //这段代码造成选值有延迟，待解决
            item.curUser = "";
            if(item.isOrg){//切换为机构了
                item.push_org = "0";
            }
        },
        //删除节点
        deleteNode:function(list,item,index,event){
            //删除后重组点数据
            list.splice(index,1);
            //删除后重组点数据
            let itemId = item.item_id;
            let lines = this.line_data;
            let i = lines.length;
            while(i--){
                if(itemId === lines[i].d_source_id || itemId === lines[i].d_target_id){
                    lines.splice(i,1);
                }
            }

            let column = item.n_column * 1;
            this.matrixArr[column].splice(this.matrixArr[column].indexOf(Number(item.item_id)), 1);
            //当前删除元素id
            let id = event.currentTarget.parentNode.getAttribute("id");
            this.jsplumb.remove(id);
        },
        //选择后续流程
        selectFlow:function(column,id){
            this.selectFlowData = {};
            this.select_flow = [];
            column = Number(column);
            this.selectFlowData.item_id = id;
            if(this.matrixArr[column+1]){
                for (let i in this.matrixArr){
                    if(Number(i) > column){
                        let arr = [];
                        this.matrixArr[i].forEach(element => {
                            arr.push({id:element+"",name:element});
                        });
                        // this.select_flow = this.select_flow.concat(this.matrixArr[i]);
                        this.select_flow = this.select_flow.concat(arr);
                    }
                }
            }
            this.select_flow.push({id:"-2",name:"结束节点"});
            this.selectFlowDialogVisible = true;
        },
        //连线后续流程走向(添加连线，)
        connectFlow:function(){
            if(!this.selectFlowData.target_id){
                this.$message({
                    type: "warning",
                    message: "请选择后续流程",
                    duration: 2000
                })
                return ;
            }
            var curId = "item_" + this.selectFlowData.item_id;
            var targetId ;
            var isConnectFlag = true;
            if(this.selectFlowData.target_id === "-2"){
                var newId = "end_box";
                targetId = "-2";
            }else{
                var newId = "item_" + this.selectFlowData.target_id;
                targetId = this.selectFlowData.target_id;
            }
            this.line_data.forEach(element => {
                if(element.d_source_id == this.selectFlowData.item_id && element.d_target_id == targetId){
                    isConnectFlag = false;
                }
            });
            if(!isConnectFlag){
                this.$message({
                    type: "warning",
                    message: "不允许重复连接！",
                    duration: 2000
                });
                return ;
            }
            //组织线数据
            this.line_data.push(
                {
                    d_source_id: this.selectFlowData.item_id,
                    d_target_id: targetId,
                    rule:""
                }
            )
            var newLength = 0;
            this.jsplumb.connect({
                source: curId,
                target: newId,
                overlays: [
                    ["Custom", {
                        create: (component) => {
                            var element = document.createElement("div");
                            if(newId !== 'end_box'){
                                var elementChild = document.createElement("span");
                                var elementChildSe = document.createElement("div");
                                var svg = document.getElementById("diagramContainer").getElementsByTagName("svg");
                                var length = svg.length;
                                for(var n = 0 ;n < length ;n ++){
                                    if( svg[n].parentNode.id === "diagramContainer"){
                                        newLength ++;
                                    }
                                }
                                elementChild.className = "iconBg rule-icon";
                                elementChild.setAttribute("id",'addRule_'+newLength);
                                elementChildSe.setAttribute("id",'ruleChange_'+newLength);
                                elementChildSe.className = "rule-div";
                                element.appendChild(elementChild);
                                element.appendChild(elementChildSe);
                            }
                            return element;
                        },
                        location: 0.5,
                        cssClass: "item-label"
                    }]
                ]
            })
            if(newId !== 'end_box'){
                document.getElementById("addRule_"+newLength).onclick=(event) =>{
                    //清空弹出框数据
                    this.addRuleCurData = {};
                    this.addRuleDialogVisible = true;
                    this.addRuleCurData.source_id = this.selectFlowData.item_id;
                    this.addRuleCurData.item_id = this.selectFlowData.target_id+"";
                    this.selectLineId = event.target.id;
                }
            }
            // jsPlumb.draggable(newId);
            this.selectFlowDialogVisible = false;
        },
        //保存或修改工作流的定义
        saveWorkflow:function(){
            let commitObj = {};
            var message ="";
            var type = this.flowBase.type;
            if(!type){
                commitObj.lanes = this.createDialogData.lanes;
                message = "保存成功";
            }else if(type === 'edit'){//代表修改
                commitObj.lanes = this.flowBase.lanes;
                commitObj.base_id = this.flowBase.base_id;
                message = "修改成功";
            }else if(type === 'copy'){
                commitObj.lanes = this.flowBase.lanes;
                message = "复制成功";
            }
            commitObj.workflow_name = this.createDialogData.workflow_name;
            commitObj.reject_strategy = this.createDialogData.reject_strategy;

            commitObj.design_data = {};
            commitObj.design_data.lines = this.line_data;
            commitObj.design_data.nodes = this.design_data.slice(0,this.design_data.length-2); //去除最后两个元素

            let nodes = commitObj.design_data.nodes;
            let len = nodes.length;
            for(let i = 0; i<len ;i++){
                let id = "item_" + nodes[i].item_id;
                let dom = document.getElementById(id);
                nodes[i].axis_x = dom.offsetLeft;
                nodes[i].axis_y = dom.offsetTop;
            }

            this.$axios({
               url: this.queryUrl + "adminProcess",
                method:"post",
                data:{
                    optype:"wfchart_add",
                    params:commitObj
                }
            }).then((result) =>{
                if (result.data.error_msg) {
                    this.$message({
                        type: "error",
                        message: result.data.error_msg,
                        duration: 2000
                    });

                    return;
                }else{
                    var data = result.data.data;
                    if(type !== 'edit'){
                        if (this.tableList.length < this.routerMessage.params.page_size) {
                            this.tableList.push(data);
                        }
                        this.pagTotal++;
                    }
                    //清空工作流相关数据
                    this.jsplumb.empty("diagramContainer");
                    this.matrixArr = [];
                    this.line_data = [];
                    this.design_data = [];
                    this.flowBase = {};
                    this.nextStepDialogVisible = false;
                    this.createDialogData.reject_strategy = "";
                    this.createDialogData.lanes = "";
                    this.curRow.workflow_name = data.workflow_name;

                    this.$message({
                        type: "success",
                        message: message,
                        duration: 2000
                    })

                }
           })
        },
        //修改工作流(修改，复制)
        editFlow:function(row,type){
            if(row.id){
                this.curRow = row;
                this.$axios({
                    url: this.queryUrl + "commProcess",
                    method:"post",
                    data:{
                        optype:"wfquery_wfdetail",
                        params:{
                            id:row.id
                        }
                    }
                }).then((result) =>{
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                        return;
                    }else{
                        var getData = result.data.data;
                        if(getData){
                            var define = getData.define;
                            if(type ==='copy'){
                                this.flowBase.type = 'copy';
                            }else if(type ==='edit'){
                                this.flowBase.type = 'edit';
                            }
                            //修改工作流需要的参数
                            this.flowBase.base_id = define.base_id;
                            this.flowBase.lanes = define.lanes;
                            this.flowBase.laster_version = getData.laster_version;
                            this.flowBase.persist_version = getData.persist_version;

                            this.line_data = define.lines;//线数据
                            /**
                             * 声明nodes这个变量的原因是因为
                               this.design_data = nodes;在这里赋值的话，
                               页面就已经在渲染了，之后在添加的值会被认为没有初始化
                               即将在for循环中初始化的变量不会被渲染到页面上，curUser、push_org
                               会造成数据无法及时相应的情况
                             */

                            var nodes = define.nodes;

                            this.createDialogData.workflow_name = getData.workflow_name;
                            this.createDialogData.reject_strategy = define.reject_strategy;

                            var len = nodes.length;
                            for(let i =0;i<len;i++){
                                //还需要补充数据
                                let column = nodes[i].n_column * 1;
                                this.matrixArr[column] = this.matrixArr[column] ? this.matrixArr[column] : [];
                                this.matrixArr[column].push(nodes[i].item_id);

                                let selOptions = JSON.parse(nodes[i].data);
                                nodes[i].data = {};
                                nodes[i].curUser = "";
                                nodes[i].push_org = "";
                                if(selOptions["users"]){//选的是用户
                                    nodes[i].data.user_type = "0";
                                    nodes[i].isOrg = false;
                                    nodes[i].data["users"]= selOptions["users"];
                                }else{
                                    nodes[i].isOrg = true;
                                    nodes[i].data.user_type = "1";
                                    nodes[i].push_org = selOptions["push_org"];
                                    nodes[i].data.push_org = selOptions["push_org"];
                                    nodes[i].data["position"]= selOptions["position"];
                                }
                            }
                            this.design_data = nodes;//点数据
                            //初始化开始节点和结束节点,追加到数据结尾，减少影响
                            this.design_data.push({item_id: "-1"});
                            this.design_data.push({item_id: "-2"});

                            this.nextStepDialogVisible = true;
                            setTimeout(() =>{
                                this.jsplumb = jsPlumb.getInstance(this.lineStyle);
                                //初始化显示默认节点的位置
                                for (let j=0;j<len;j++){
                                    let curId = "item_"+this.design_data[j].item_id;
                                    let firstChild = document.getElementById(curId);
                                    firstChild.style.top = this.design_data[j].axis_y+ "px";
                                    firstChild.style.left = this.design_data[j].axis_x + "px";
                                    let conDomW = firstChild.getElementsByClassName("child-users-container")[0].offsetWidth;
                                    let scrDomW = firstChild.getElementsByClassName("child-users-source")[0].offsetWidth;
                                    if(scrDomW > conDomW){
                                        let btn = firstChild.getElementsByClassName("scrBtn");
                                        btn[0].classList.add("showBtn");
                                        btn[1].classList.add("showBtn");
                                    }
                                }
                                //初始化线的位置，因为线可能比点多，所以不能共用循环
                                let lineLen = this.line_data.length;
                                for (let q = 0; q< lineLen; q++){
                                    var newLength = 0;//记录这是第几根线
                                    var clickObjId = "";//点击对象的id，是加号还是规则框
                                    var sourceId ,targetId;
                                    var oldSId =this.line_data[q]["d_source_id"];
                                    var oldTId =this.line_data[q]["d_target_id"];
                                    if(oldSId === "-1"){
                                        sourceId = "root_box";
                                    }else{
                                        sourceId = "item_" + oldSId;
                                    }
                                    if(oldTId === "-2"){
                                        targetId = "end_box";
                                    }else{
                                        targetId = "item_" + oldTId;
                                    }
                                    var ruleMoney = this.translateRuleMoney(this.line_data[q].rule);
                                    this.jsplumb.connect({
                                        source: sourceId,
                                        target: targetId,
                                        overlays: [
                                            ["Custom", {
                                                create:  (component)=> {
                                                    var element = document.createElement("div");
                                                    if(targetId !=='end_box'){
                                                        var elementChild = document.createElement("span");//加号
                                                        var elementChildSe = document.createElement("div");
                                                        var svg = document.getElementById("diagramContainer").getElementsByTagName("svg");
                                                        var length = svg.length;
                                                        for(var n = 0 ;n < length ;n ++){
                                                            if( svg[n].parentNode.id === "diagramContainer"){
                                                                newLength ++;
                                                            }
                                                        }
                                                        elementChild.className = "";
                                                        elementChild.setAttribute("id",'addRule_' + newLength);
                                                        element.appendChild(elementChild);
                                                        clickObjId = 'addRule_' + newLength;
                                                        if(!this.line_data[q].rule){//没有规则时要创建加号
                                                            elementChild.className = "iconBg rule-icon";
                                                        }else{
                                                            elementChildSe.className = "rule-hasValue";
                                                            elementChildSe.innerText = ruleMoney.str;
                                                            clickObjId = 'ruleChange_' + newLength;
                                                        }
                                                        elementChildSe.setAttribute("id",'ruleChange_' + newLength);
                                                        element.appendChild(elementChildSe);
                                                    }
                                                    return element;
                                                },
                                                location: 0.5,
                                                cssClass: "item-label"
                                            }]
                                        ]
                                    })
                                    if(targetId !=='end_box'){
                                        document.getElementById(clickObjId).onclick=(event)=>{
                                            //清空弹出框数据
                                            this.addRuleCurData = {};
                                            this.addRuleCurData.source_id = this.line_data[q].d_source_id;
                                            this.addRuleCurData.item_id = this.line_data[q].d_target_id;
                                            if(this.line_data[q].rule){//非加号时
                                                var money = this.translateRuleMoney(this.line_data[q].rule);
                                                this.addRuleCurData.min = money.min;
                                                this.addRuleCurData.max = money.max;
                                                this.addRuleCurData.isEdit = true;
                                            }
                                            this.selectLineId = event.target.id;
                                            this.addRuleDialogVisible = true;
                                        }
                                    }
                                    this.jsplumb.draggable(targetId);
                                }
                            },0)
                        }

                    }
                })
            }

        },
        //删除工作流
        delFlow:function(row,index,rows){
            this.$confirm("确认删除当前工作流吗","提示",{
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() =>{
                this.$axios({
                    url: this.queryUrl + "adminProcess",
                    method:"post",
                    data:{
                        optype:"wfdefine_del",
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
        //关闭流程图弹框
        handleClose:function(){
            //清空工作流相关数据
            this.jsplumb.empty("diagramContainer");
            this.matrixArr = [];
            this.line_data = [];
            this.design_data = [];
            this.flowBase = {};
            this.nextStepDialogVisible = false;
            this.createDialogData.reject_strategy = "";
            this.createDialogData.lanes = "";
        },
        //清空工作流相关数据，由于不能再打开弹出框的时候删除，所以在关闭的时候删除
        cancelWorkflow:function(){
            this.jsplumb.empty("diagramContainer");
            this.matrixArr = [];
            this.line_data = [];
            this.design_data = [];
            this.flowBase = {};
            this.nextStepDialogVisible = false;
            this.createDialogData.reject_strategy = "";
            this.createDialogData.lanes = "";
        },
        firstEndId: function(item){
            if(item.item_id == "-1"){
                return "root_box";
            }else if(item.item_id == "-2"){
                return "end_box";
            }else{
                return;
            }
        },
        isFirstEnd:function(item){
            if(item.item_id == "-1" || item.item_id == "-2"){
                return false;
            }else{
                return true;
            }
        },
        //查看工作流
        lookFlow:function(row){
            if(row.id){
               this.$axios({
                    url: this.queryUrl + "commProcess",
                    method:"post",
                    data:{
                        optype:"wfquery_wfdetail",
                        params:{
                            id:row.id
                        }
                    }
                }).then((result) =>{
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                        return;
                    }else{
                        let getData = result.data.data;
                        let define = getData.define;
                        this.createDialogData.workflow_name = getData.workflow_name;
                        this.createDialogData.reject_strategy = define.reject_strategy;
                        this.lookFlowDialogVisible = true;
                        //将数据传递给子组件
                        this.flowList = define;
                        this.isEmptyFlow = false;
                    }
                })
            }
        },
        //关闭查看工作流弹框
        cancelLookFlow:function(){
            this.isEmptyFlow = true;
            this.lookFlowDialogVisible = false;
            this.flowList = {};
        },
        validateNum:function(val,type){
            if(/^\d+(\.\d{1,2})?$/.test(val) || !val){
                this.validateFlag = true;
                return ;
            }else{
                this.validateFlag = false;
                if(type =='min'){
                    this.addRuleCurData.min = "";
                }else{
                    this.addRuleCurData.max = "";
                }
                this.$message({
                    type: "warning",
                    message: "请输入小数位2位以内的数字！",
                    duration: 2000
                })
            }
        }
    },
    computed: {

    },
    watch: {
        tableData: function (val, oldVal) {
            this.pagSize = val.page_size;
            this.pagTotal = val.total_line;
            this.pagCurrent = val.page_num;
            this.tableList = val.data;
        }
    }
}
</script>


