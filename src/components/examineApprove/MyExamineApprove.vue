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
                >div:last-child{
                    border-right: 0;
                }
            }
            .el-row:last-child{
                >div{
                   border-bottom: 0; 
                }
                
            }
            .el-row:hover{
                background-color: #EEF7FE;
            }
            .height80{
                height: 80px;
            }
            .left{
                text-align: right;
                padding-right: 20px;
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
                .right{
                    margin-left: 25%;
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

        /*业务追踪*/
        .busTrackContainer{
            margin-top: 20px;
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
                                <el-select v-model="searchData.biz_type" placeholder="请选择业务种类" clearable >
                                    <el-option
                                        v-for="item in businessType"
                                        :key="item.id"
                                        :label="item.name"
                                        :value="item.id">
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
                </div>
            </div>
            <el-tabs class="myTab" v-model="activeName" @tab-click="handleClick">
                <el-tab-pane
                    v-for="item in editableTabsList"
                    :key="item.name"
                    :label="item.title"
                    :name="item.name" 
                >   
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
                                v-if="head.prop=='biz_type' || head.prop=='service_status'"
                                :key="head.id"
                                :prop="head.prop"
                                :label="head.name"
                                :formatter="transitionStatus"
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
        <div class="button-list" v-if="isPending" v-show="activeName!='0'">
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
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <div class="dialogTable">
                <el-row>
                    <el-col :span="6" class="left">编号</el-col>
                    <el-col :span="18">{{dialogData.bill_code}}</el-col>
                </el-row>
                <el-row>
                    <el-col :span="6" class="left">事由摘要</el-col>
                    <el-col :span="18">{{dialogData.memo}}</el-col>
                </el-row>
                <el-row class="enclosureUp">
                    <el-col :span="6" class="left textName"><span>申请事由说明</span></el-col>
                    <el-col :span="18" class="right" :title="dialogData.detail">
                        <el-input v-model="dialogData.detail" readonly
                                      type="textarea" :rows="2"></el-input>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="6" class="left">附件</el-col>
                    <el-col :span="18"><span class="enclosure">{{dialogData.encNumber}}</span>个</el-col>
                </el-row>
                <el-row class="enclosureUp">
                    <el-col :span="6" class="left"></el-col>
                    <el-col :span="18" class="right">
                        <Upload 
                            @currentFielList="setFileList"
                            :emptyFileList="emptyFileList"
                            :isPending="false"
                            :fileMessage="fileMessage"
                            :triggerFile="triggerFile"></Upload>
                    </el-col>
                </el-row>
            </div>
            <div class="busTrackContainer">
                <el-row>
                    <el-col :span="24" class="form-small-title">
                        <span></span>
                        <span>业务状态跟踪</span>
                    </el-col>
                </el-row>
            </div>
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
                        <el-form-item label="退回">
                            <el-select value-key="id" v-model="thirdFunData.back">
                                <el-option
                                    v-for="item in back_options"
                                    :key="item.id"
                                    :label="item.name"
                                    :value="item">
                                </el-option>
                            </el-select>
                        </el-form-item>
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
    export default {
        name: "MyExamineApprove",
        created: function () {
            this.$emit("transmitTitle", "我的审批平台");
            this.$emit("getTableData", this.routerMessage);
            //获取tab数据
            this.editableTabsList = this.getTabList();

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
            
        },
        props: ["isPending", "tableData"],
        components:{
            Upload:Upload
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
                        {id:'6',prop:"submitter_name",name:'上级处理人'},
                        {id:'7',prop:"nextUserList[0].login_name",name:'下级审批人'}
                    ],
                    "1":[
                        {id:'1',prop:"apply_on",name:'申请日期'},
                        {id:'2',prop:"memo",name:'事由摘要'},
                        {id:'3',prop:"service_status",name:'业务状态'}
                    ],
                    "2":[
                        {id:'1',prop:"apply_on",name:'申请日期'},
                        {id:'2',prop:"memo",name:'事由摘要'},
                        {id:'3',prop:"service_status",name:'业务状态'}
                    ],
                    "3":[
                        {id:'1',prop:"apply_on",name:'申请日期'},
                        {id:'2',prop:"memo",name:'账户号'},
                        {id:'3',prop:"memo",name:'账户名称'},
                        {id:'4',prop:"service_status",name:'业务状态'}
                    ],
                    "4":[
                        {id:'1',prop:"apply_on",name:'申请日期'},
                        {id:'2',prop:"memo",name:'账户号'},
                        {id:'3',prop:"memo",name:'账户名称'},
                        {id:'4',prop:"service_status",name:'业务状态'}
                    ],
                    "5":[
                        {id:'1',prop:"apply_on",name:'申请日期'},
                        {id:'2',prop:"memo",name:'账户号'},
                        {id:'3',prop:"memo",name:'账户名称'},
                        {id:'4',prop:"service_status",name:'业务状态'}
                    ],
                    "6":[
                        {id:'1',prop:"apply_on",name:'申请日期'},
                        {id:'2',prop:"memo",name:'事由摘要'},
                        {id:'4',prop:"service_status",name:'业务状态'}
                    ],
                    "6":[
                        {id:'1',prop:"apply_on",name:'申请日期'},
                        {id:'2',prop:"memo",name:'事由摘要'},
                        {id:'4',prop:"service_status",name:'业务状态'}
                    ]
                },
                editableTabsList: [],
                bizType:{},//业务种类
                dialogVisible:false,
                dialogTitle:"开户事项申请",
                dialogData:{
                    encNumber:""
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
                businessType:[]
            }
        },
        methods:{
            //获取tab集合
            getTabList:function(){
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                this.bizType = constants.MajorBizType;
                if (this.bizType) {
                    let tableHead = this.tableHeadList;
                    //加一个我的待办
                    let arrList = [
                        {
                            title: '我的待办',
                            name: '0',
                            tableHead:tableHead['0'],
                            tableList:[]
                        }
                    ];
                    let length = Object.keys(this.bizType).length + 1;
                    let businessTypeArr = [];
                    for(let i = 1; i<length; i++){
                        businessTypeArr.push({
                            id:i,
                            name:this.bizType[i]
                        })
                        arrList.push({
                            title: this.bizType[i],
                            name: i+"",
                            tableHead:tableHead[i],
                            tableList:[]
                        })
                    }
                    // this.editableTabsList = arrList;
                    this.businessType = businessTypeArr;
                    return arrList;
                }else{
                    return [];
                }
            },
            //展示格式转换
            transitionStatus: function (row, column, cellValue, index) {
                if(column.property === "biz_type"){//转换业务种类
                    if (this.bizType) {
                        return this.bizType[cellValue];
                    }
                }
                if(column.property === "service_status" || column.property === "status"){//转换业务状态
                    var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                    if (constants.BillStatus) {
                        return constants.BillStatus[cellValue];
                    }
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
                    page_num: 1,
                    assignee_id:this.$store.state.user.usr_id
                };
                this.$emit("getTableData", this.routerMessage);
            },
            comeBack:function(){
                this.activeName = "0";
                document.getElementsByClassName("el-tabs__active-bar")[0].style.display="none";
                //请求全部数据
                this.routerMessage.todo.optype = "wfquery_pendingtasksall";
                this.$emit("getTableData", this.routerMessage);
            },
            handleClick:function(tab,event){
                //由于将我的待办写入了tab里，却又要隐藏掉，所以处理一下横线样式
                let rowBar = document.getElementsByClassName("el-tabs__active-bar")[0];
                if(tab.name == "1"){
                    rowBar.style.left = "20px";
                }else if(rowBar.style.left != "0px"){
                    rowBar.style.left="0px";
                }
                if(rowBar.style.display === "none")
                    rowBar.style.display="inline-block";

                //在这里tab.name就是biztype的值
                this.routerMessage.todo.optype = "openintent_pendingtasks";
                this.routerMessage.todo.params.biz_type = tab.name;
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
                let id = this.activeName == '0'? row.bill_id : row.aoi_id;
                //组装开户同意接口的参数
                this.currentData.index = index;
                this.currentData.id = id;
                this.currentData.define_id = row.define_id;

                if(this.activeName == '0'){
                    this.currentData.wf_inst_id = row.id;
                    this.$axios({
                        url:"/cfm/normalProcess",
                        method:"post",
                        data:{
                            optype:"openintent_detail",
                            params:{
                                id:id
                            }
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

                            //组装查看弹出框数据
                            this.dialogData.detail = data.detail;
                            this.dialogData.memo = data.memo;
                            this.dialogData.bill_code = data.service_serial_number;
                            //拿附件数据
                            this.fileMessage.bill_id = id;
                            this.fileMessage.biz_type = row.biz_type;
                            this.triggerFile = !this.triggerFile;
                            this.dialogVisible = true;
                        }
                    })
                }else{
                    //组装开户同意接口的参数
                    this.currentData.service_status = row.service_status;
                    this.currentData.persist_version = row.persist_version;
                    this.currentData.wf_inst_id = row.inst_id;

                    //组装查看弹出框数据
                    this.dialogData.detail = row.detail;
                    this.dialogData.memo = row.memo;
                    this.dialogData.bill_code = row.service_serial_number;
                    //拿附件数据
                    this.fileMessage.bill_id = id;
                    this.fileMessage.biz_type = row.biz_type;
                    this.triggerFile = !this.triggerFile;
                    this.dialogVisible = true;
                }
                // //加载业务跟踪状态数据
                // this.$axios({
                //     url:"/cfm/normalProcess",
                //     method:"post",
                //     data:{
                //         optype:"wfquery_approvedetail",
                //         params:{
                //             id:id,
                //             biz_type:row.biz_type
                //         }
                //     }
                // }).then((result) =>{
                //     if (result.data.error_msg) {
                //         this.$message({
                //             type: "error",
                //             message: result.data.error_msg,
                //             duration: 2000
                //         })
                //     }else{
                //         debugger;
                //     }
                // })
            },
            //设置当前附件个数
            setFileList: function ($event) {
                let len = $event.length;
                this.dialogData.encNumber = len ? len : "";
            },
            //确认加签或同意或拒绝
            confirmThirdFun:function(){
                let type = this.thirdFunData.type;
                let message = "";
                let optype = "";
                if(type == "addLots"){
                    let user = this.thirdFunData.user;
                    //组装加签参数
                    this.currentData.shadow_user_id = user.id;
                    this.currentData.shadow_user_name = user.name;
                    optype = "openintent_append";
                    message = "加签成功";
                }else if(type == "reject"){
                    optype = "openintent_reject";
                    message = "拒绝成功";
                }else{
                    optype = "openintent_agree";
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
                        let rows = this.editableTabsList[this.activeName].tableList;
                        let index = paramsObj.index;
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
                    this.editableTabsList[this.activeName].tableList=val.data;
                }else{
                    this.doneTableList = val.data;
                }
            }
        }
    }
</script>
