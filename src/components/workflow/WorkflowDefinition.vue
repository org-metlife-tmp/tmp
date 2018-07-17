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
            bottom: -6px;
        }

        /*数据展示区*/
        .table-content {
            height: 289px;
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
            left: 700px;
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
                .left-icon, .right-icon {
                    width: 10px;
                    height: 15px;
                    cursor: pointer;
                    border: 0;
                    position: absolute;
                    top: 4px;
                    display: inline-block;
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
        .borderHover {
            border: 1px solid #7bb9f2!important;
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
                                       @click="lookMatter(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="复制" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button class="on-copy" size="mini"
                                       @click="lookMatter(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editMerch(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeMatter(scope.row,scope.$index,tableList)"></el-button>
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
                            <el-select v-model="createDialogData.initalGradation">
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
                   width="800px" title="新建流程"
                   :close-on-click-modal="false"
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
                <div id="root_box" class="firstEnd" @click="creatFirst"><span class="iconBg"></span></div>
                <template  v-for="(item,index) in design_data" >
                    <div :id="'item_'+item.item_id" class="child_item" :key="item.item_id" :class="{borderHover:item.isHover}"  @mouseover="item.isHover=true" @mouseout="item.isHover=false">
                        <span class="child_no"><i class="child_num">{{item.item_id}}</i></span>
                        <div class="child_org" v-show="item.isOrg">
                            <el-select v-model="org">
                                <el-option
                                    v-for="org in org_options"
                                    :key="org.id"
                                    :label="org.name"
                                    :value="org.id">
                                </el-option>
                            </el-select>
                        </div>
                        <span class="child_delete iconBg" @click="deleteNode(design_data,item,index)"></span>
                        <div class="child_user">
                            <el-select v-model="item.curUser" @change="selectUsers(item)">
                                <el-option
                                    v-for="user in item.options"
                                    :key="user.id"
                                    :label="user.name"
                                    :value="user.id"
                                    :disabled="user.disabled">
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
                                        @close="handleClose(tag.id,item)">
                                        {{tag.name}}
                                    </el-tag>
                                </div>
                            </div> 
                            <button class="iconBg left-icon" @click="leftMove($event)"></button>     
                            <button class="iconBg right-icon" @click="rightMove($event)"></button> 
                        </div>
                        <div class="child_add iconBg" v-show="item.isHover" @click="addChild(item.n_column,item.axis_x,item.item_id)"></div>
                        <div class="check_redirect iconBg" @click="selectFlow(item.n_column,item.item_id)"></div>
                    </div>
                </template>
                <div id="end_box" class="firstEnd"><span class="iconBg"></span></div>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="nextStepDialogVisible = false">取 消</el-button>
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
                    <el-input class="ruler-input" v-model="addRuleCurData.min" placeholder="大于等于"></el-input>
                    -
                    <el-input class="ruler-input" v-model="addRuleCurData.max" placeholder="小于"></el-input>
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
                            :key="item"
                            :label="item"
                            :value="item">
                        </el-option>
                    </el-select>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" @click="connectFlow">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>
<script>
import "../../js/jsPlumb-2.2.6.js"

export default {
    name:"WorkflowDefinition",
    created:function(){
        this.$emit("transmitTitle","流程定义");
        this.$emit("getTableData", this.routerMessage);

        this.design_data = [{
                axis_x:200,			
                axis_y:0,				
                n_column:"1",			
                n_row:"1",			
                item_id:"1",
                isHover:false, 
                isOrg:false,
                options:[
                    {id:'3',name:'用户1'},
                    {id:'2',name:'用户2'},
                    {id:'1',name:'用户3'},
                    {id:'0',name:'用户4'},
                    {id:'-1',name:'用户5'},
                    {id:'-2',name:'用户6'},
                    {id:'-3',name:'用户7'}
                ]
            }
        ];
        this.line_data = [
            {
                d_source_id:"-1",	
                d_target_id:"1",		
                rule:""
            }
        ]
        this.matrixArr = {
            1:[1]
        }

        this.work_options = [
            {id:'3',name:'职业1'},
            {id:'2',name:'职业2'},
            {id:'1',name:'职业3'},
            {id:'0',name:'职业4'},
            {id:'-1',name:'职业5'},
            {id:'-2',name:'职业6'},
            {id:'-3',name:'职业7'}
        ];
    },
    props:["tableData"],
    data:function(){
        return {
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
            pagSize: 7,
            pagTotal: 1,
            pagCurrent: 1,
            createDialogVisible: false,
            createDialogData: {},
            back_options:[
                {id:'1',name:'初始提交人'},
                {id:'2',name:'上级审批人'},
                {id:'3',name:'自定义'}
            ],
            gradation_options:[
                {id:'1',name:'1'},
                {id:'2',name:'2'},
                {id:'3',name:'3'},
                {id:'4',name:'4'},
                {id:'5',name:'5'}
            ],
            nextStepDialogVisible:false,
            workflowData:{},
            design_data:[{}],
            line_data:[],
            input23:"123",
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
                {id:'3',name:'上三级'},
                {id:'2',name:'上二级'},
                {id:'1',name:'上一级'},
                {id:'0',name:'本公司'},
                {id:'-1',name:'下一级'},
                {id:'-2',name:'下二级'},
                {id:'-3',name:'下三级'}
            ],
            user_options:[
                {id:'3',name:'用户1'},
                {id:'2',name:'用户2'},
                {id:'1',name:'用户3'},
                {id:'0',name:'用户4'},
                {id:'-1',name:'用户5'},
                {id:'-2',name:'用户6'},
                {id:'-3',name:'用户7'}
            ],
            // addUsers:[],//已选择用户
            org:"1",
            addRuleDialogVisible:false,
            addRuleCurData:{},//当前规则弹框的数据
            matrixArr:{}, //存放每列的数据
            selectFlowDialogVisible:false,//选择后续流程走向
            selectFlowData:{},
            select_flow:[]//可以选择的流程走向
        }
    },
    methods:{
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
        //查询数据
        queryData:function(){
            this.routerMessage.params.query_key = this.searchData.query_key;
            this.$emit("getTableData", this.routerMessage);
        },
        //新建流程
        createProcess:function(){
            this.createDialog = {};
            this.createDialogVisible = true;
        },
        //打开下一步
        showNext:function(curdata){
            if(curdata.workflow_name){
                // var flag = false;
                // this.$axios({
                //     url:"/cfm/adminProcess",
                //     method:"post",
                //     data:{
                //         optype:"wfdefine_add",
                //         params:{
                //             workflow_name:workflow_name
                //         }
                //     }
                // }).then((result) =>{
                //     debugger
                //     if (result.data.error_msg) {
                //         this.$message({
                //             type: "error",
                //             message: result.data.error_msg,
                //             duration: 2000
                //         })
                //         return;
                //     }else{
                //         flag = true;
                //     }
                // })
            }else{
                return ;
            }

            var initalNum = curdata.initalGradation;

            // if(flag){
                this.nextStepDialogVisible = true;
                var _this=this;
                var newLength = 0 ;
                setTimeout(function(){
                    //初始化显示默认节点的位置
                    // var firseChild = document.getElementById("item_1");
                    // firseChild.style.top="0px";
                    // firseChild.style.left="200px";

                    jsPlumb.ready(function(){
                        _this.jsplumb = jsPlumb.getInstance(_this.lineStyle);
                        jsPlumb.connect({
                            source: 'root_box',
                            target: 'item_1',
                            overlays: [
                                ["Custom", {
                                    create: function (component) {
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
                                        elementChild.setAttribute("nodeId","item_1");
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
                        document.getElementById("addRule_"+newLength).onclick=function(){
                            //清空弹出框数据
                            _this.addRuleCurData = {};
                            _this.addRuleDialogVisible = true;
                            _this.addRuleCurData.item_id = "1";
                        }
                        jsPlumb.draggable('item_1');
                    })
                },0)   
            // }         
        },
        //点击创建第一列的节点
        creatFirst:function(){
            debugger
            var _this=this;
            //每点击一次该列数据增加一条
            var top = this.matrixArr[1].length * 180;
            
            var list = this.design_data;
            var len = list.length;
            len = len == 0 ? 0 : list[len-1].item_id*1;
            var newId = "item_" + (len+1); //取最后一个元素的item_id
            this.matrixArr[1].push(len+1);
            //组织点数据
            list.push({axis_x: 200,			
                axis_y: top,				
                n_column: "1",			
                n_row: this.matrixArr[1].length,			
                item_id: len + 1 + "",	
                isHover:false,
                isOrg:false,
                options:[
                    {id:'3',name:'用户1'},
                    {id:'2',name:'用户2'},
                    {id:'1',name:'用户3'},
                    {id:'0',name:'用户4'},
                    {id:'-1',name:'用户5'},
                    {id:'-2',name:'用户6'},
                    {id:'-3',name:'用户7'}
                ]
            });
            //组织线数据
            this.line_data.push(
                {
                    d_source_id:"-1",	
                    d_target_id:len + 1+"",		
                    rule:""
                }
            )

            setTimeout(function(){
                //初始化显示默认节点的位置
                var newChild = document.getElementById(newId);
                newChild.style.top = top + "px";
                newChild.style.left = "200px" ;

                var newLength = 0 ;
                this.jsplumb = jsPlumb.getInstance(this.lineStyle);
                jsPlumb.connect({
                    source: 'root_box',
                    target: newId,
                    overlays: [
                            ["Custom", {
                                create: function (component) {
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
                                    elementChild.setAttribute("nodeId","item_1");
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
                jsPlumb.connect({
                    source: 'root_box',
                    target: newId
                })
                
                
                document.getElementById("addRule_"+newLength).onclick=function(){
                    //清空弹出框数据
                    _this.addRuleCurData = {};
                    _this.addRuleDialogVisible = true;
                    _this.addRuleCurData.item_id = len + 1 + "";
                }
                jsPlumb.draggable(newId);
                
            },0)
            
            

        },
        //添加其他列的节点
        addChild:function(column,x,id){
            var _this=this;
            //如果该列有数据，增加该列数据的个数
            //如果该列没有数据，增加this.matrixArr新push一列
            column = Number(column);
            var top = 0;
            var left = x + 300;
            var list = this.design_data;
            var len = list.length;
            len = len == 0 ? 0 : list[len-1].item_id*1;
            var curId = "item_" + id;
            var newId = "item_" + (len+1); 
            this.matrixArr[column+1] = this.matrixArr[column+1] ? this.matrixArr[column+1] : [];
            if(this.matrixArr[column+1] && this.matrixArr[column+1].length>0){//如果新增列有数据(不可能为0)
                top = this.matrixArr[column+1].length * 180;
            }
            this.matrixArr[column+1].push(len+1);
            //组织点数据
            list.push({axis_x: left,			
                axis_y: top,				
                n_column: column + 1,			
                n_row: this.matrixArr[column+1].length,			
                item_id: len + 1 + "",	
                isHover:false,
                isOrg:false,
                options:[
                    {id:'3',name:'用户1'},
                    {id:'2',name:'用户2'},
                    {id:'1',name:'用户3'},
                    {id:'0',name:'用户4'},
                    {id:'-1',name:'用户5'},
                    {id:'-2',name:'用户6'},
                    {id:'-3',name:'用户7'}
                ]	
            });
            //组织线数据
            this.line_data.push(
                {
                    d_source_id:id,	
                    d_target_id:len + 1+"",		
                    rule:""
                }
            )
            setTimeout(function(){
                //初始化显示默认节点的位置
                var newChild = document.getElementById(newId);
                newChild.style.top = top + "px";
                newChild.style.left = left + "px" ;
                this.jsplumb = jsPlumb.getInstance(this.lineStyle);

                var newLength = 0 ;
                jsPlumb.connect({
                    source: curId,
                    target: newId,
                    overlays: [
                        ["Custom", {
                            create: function (component) {
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
                                elementChild.setAttribute("nodeId","item_1");
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
                
                
                document.getElementById("addRule_"+newLength).onclick=function(){
                    //清空弹出框数据
                    _this.addRuleCurData = {};

                    _this.addRuleDialogVisible = true;
                    _this.addRuleCurData.item_id = len + 1 + "" ;
                }
                jsPlumb.draggable(newId);
            })
        },
        //保存规则
        saveRules:function(curData){
            if(curData.min && curData.max){
                var arrList = this.line_data;
                var len = arrList.length;
                var str = "";
                for(var i = 0; i<len;i++){
                    if(arrList[i].d_target_id === curData.item_id){
                        str = curData.min + "~" + curData.max;
                        arrList[i].rule = str;
                        break;
                    }
                }
                this.addRuleDialogVisible = false;
                var _this = this;
                var curShowEle = document.getElementById("ruleChange_"+curData.item_id);
                curShowEle.innerText = str;
                curShowEle.style.display = "inline-block";
                curShowEle.previousElementSibling.className = "";
                curShowEle.onclick=function(){
                    _this.addRuleDialogVisible = true;
                    _this.addRuleCurData.item_id =  curData.item_id;
                    _this.addRuleCurData.min =  curData.min;
                    _this.addRuleCurData.max =  curData.max;
                }
            }
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
        //选择用户
        selectUsers:function(item){

            debugger
            let id = item.curUser;
            let obj = {};
            let arrList = item.options;
            let len = arrList.length;
            for(let i=0;i<len;i++){
                if(arrList[i].id === id){
                    obj = arrList[i];
                    arrList[i].disabled = true;
                    break;
                }
            }
            item.addUsers = item.addUsers ? item.addUsers :[];
            item.addUsers.push(obj);
            item.data = item.data ? item.data : {};
            item.data.user_type = item.isOrg ? "1" : "0";
            item.isOrg ? item.data.push_org = "0" : "";
            item.data.users= item.data.users ? item.data.users : [];
            item.data.users.push(id);
        },
        //删除用户
        handleClose:function(tag,item) {
            let arrList = item.options;
            let len1 = arrList.length;
            for(let i=0;i<len1;i++){
                if(arrList[i].id === tag){
                    arrList[i].disabled = false;
                    break;
                }
            }
            var list = item.addUsers;
            var len = list.length;
            for(let j=0;j<len;j++){
                if(list[j].id === tag){
                   list.splice(j,1);
                   break;
                }
            }
            item.data.users.splice(item.data.users.indexOf(tag), 1);
        },
        //切换机构或用户
        changeOrgUser:function(item){
            item.isOrg = !item.isOrg;
            if(item.isOrg){//切换为机构了
                item.options = [
                    {id:'3',name:'职业1'},
                    {id:'2',name:'职业2'},
                    {id:'1',name:'职业3'},
                    {id:'0',name:'职业4'},
                    {id:'-1',name:'职业5'},
                    {id:'-2',name:'职业6'},
                    {id:'-3',name:'职业7'}
                ];
                item.addUsers = [];
                item.data = {};
                item.curUser = "";
            }else{
                item.options = [
                    {id:'3',name:'用户1'},
                    {id:'2',name:'用户2'},
                    {id:'1',name:'用户3'},
                    {id:'0',name:'用户4'},
                    {id:'-1',name:'用户5'},
                    {id:'-2',name:'用户6'},
                    {id:'-3',name:'用户7'}
                ];
                item.addUsers = [];
                item.data = {};
                item.curUser = "";
            }
        },
        //删除节点
        deleteNode:function(list,item,index){
            debugger;
            list.splice(index,1);
            var column = item.n_column * 1;
            this.matrixArr[column].splice(this.matrixArr[column].indexOf(item.item_id), 1);
            //当前删除元素id
            var id = event.currentTarget.parentNode.getAttribute("id");
            jsPlumb.remove(id);
        },
        //选择后续流程
        selectFlow:function(column,id){
            this.selectFlowData = {};
            this.select_flow = [];
            column = Number(column);
            this.selectFlowData.item_id = id;
            
            if(this.matrixArr[column+1]){
                for (let i in this.matrixArr){
                    if(i !== id){
                        this.select_flow = this.select_flow.concat(this.matrixArr[i]); 
                    }
                }
            }
            this.select_flow.push("-2");
            this.selectFlowDialogVisible = true;
        },
        //连线后续流程走向
        connectFlow:function(){
            var curId = "item_" + this.selectFlowData.item_id;
            if(this.selectFlowData.target_id === "-2"){
                var newId = "end_box";
            }else{
                var newId = "item_" + this.selectFlowData.target_id;
            }
            var newLength = 0;
            var _this = this;
            jsPlumb.connect({
                source: curId,
                target: newId,
                overlays: [
                    ["Custom", {
                        create: function (component) {
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
                            elementChild.setAttribute("nodeId","item_1");
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
            document.getElementById("addRule_"+newLength).onclick=function(){
                //清空弹出框数据
                _this.addRuleCurData = {};

                _this.addRuleDialogVisible = true;
                _this.addRuleCurData.item_id = _this.selectFlowData.target_id+"";
            }
            // jsPlumb.draggable(newId);
            this.selectFlowDialogVisible = false;
        },
        saveWorkflow:function(){
           let commitObj = {}
           commitObj.design_data = {};
           commitObj.design_data.lines = this.line_data;
           commitObj.design_data.nodes = this.design_data; 
        }
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


