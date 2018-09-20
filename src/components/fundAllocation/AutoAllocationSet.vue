<style scoped lang="less" type="text/less">
    #autoAllocationSet {
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

        /*主体内容*/
        .table-content {
            max-height: 94%;
            overflow-y: auto;

            /*标签页*/
            .tab-content {
                width: 100%;
                height: 240px;
                border: 1px solid #e4e7ed;
                box-sizing: border-box;
                border-top: none;

                /*下拨主账户选择*/
                .account-select {
                    text-align: left;
                    padding-left: 20px;
                    padding-top: 15px;
                    color: #676767;

                    .el-input {
                        width: 40%;
                        margin-left: 10px;
                    }
                }

                /*添加被下拨账户*/
                .tab-add-collect {
                    text-align: left;
                    height: 30px;
                    line-height: 30px;
                    padding: 0 20px;
                    background-color: #E9F2F9;
                    margin-top: 10px;
                    border-top: 1px solid #e4e7ed;
                    color: #848484;

                    i {
                        color: #00B3EC;
                        font-size: 22px;
                        vertical-align: middle;
                        background-color: #fff;
                        border-radius: 50%;
                        cursor: pointer;
                    }

                    div {
                        float: right;

                        span:nth-child(1) {
                            margin-right: 20px;
                            color: #0084A7;
                            cursor: pointer;
                        }
                        span:nth-child(3) {
                            color: red;
                        }
                    }
                }
                /*被下拨账户列表*/
                .content-list {
                    height: 151px;
                }
            }

            /*下拨频率日期选择*/
            .date-select {
                .el-input{
                    width: 90%;
                }

                i {
                    font-size: 22px;
                    vertical-align: middle;
                    color: #00B3EC;
                    cursor: pointer;
                }

                i:nth-child(2) {
                    color: #F9B32C;
                }
            }
        }

        /*底部按钮组*/
        .btn-bottom {
            text-align: left;
            margin-top: 10px;

            .arrows {
                height: 16px;
                display: inline-block;
                line-height: 13px;
                font-size: 20px;
                vertical-align: middle;
                font-family: initial;
                margin-left: 10px;
            }

            .btnGroup {
                float: right;
            }
        }
        /*时间选择弹框*/
        .set-date {
            text-align: center;
            margin-top: 10px;
            margin-bottom: 30px;

            h5 {
                width: 60%;
                margin: 0 auto 20px;
                background: #3dbaf0;
                color: #fff;
            }

            .el-checkbox-group {
                margin-bottom: 30px;
            }

            .month-day {
                width: 60%;
                height: 120px;
                margin: 0 auto 30px;

                li {
                    float: left;
                    width: 14%;
                    cursor: pointer;
                }

                li:hover {
                    .active;
                }

                .active {
                    background-color: #3dbaf0;
                    border-radius: 6px;
                    color: #fff;
                }
            }
        }

        #showbox{
            position: fixed;
            right: -500px;
            z-index: 999;
            transition: all 1.5s;
        }
        
    }
</style>
<style lang="less" type="text/less">
    #autoAllocationSet {
        .el-table th {
            text-align: left;
        }
        .el-tabs__header {
            margin-bottom: 0;
        }
        .el-tabs__item {
            height: 32px;
            line-height: 32px;
        }
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 400px;
                overflow-y: auto;
            }
        }
        /*弹框表格样式*/
        .dialogTable{
            // max-height: 200px;
            margin-bottom: 20px;
        }
        .tableDialog.el-dialog__wrapper {
            .el-dialog__body {
                height: 400px;
            }
        }
        .workFlow{
            .el-radio-group {
                margin-top: -16px;
                .el-radio {
                    display: block;
                    margin-left: 30px;
                    margin-bottom: 10px;
                }
            }
        }
    }
</style>

<template>
    <div id="autoAllocationSet">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <!-- <el-button type="warning" size="mini" @click="">打印</el-button>-->
        </div>
        <!--中间内容-->
        <section class="table-content">
            <el-form :model="allocationData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="14">
                        <el-form-item label="下拨主题">
                            <el-input v-model="allocationData.topic" placeholder="请为本次下拨主题命名以方便识别" :disabled="viewReadonly"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" style="text-align:left">
                        <el-form-item label="定额下拨">
                            <el-input style="width:200px" :disabled="viewReadonly" v-model="allocationData.allocation_amount" placeholder="请填写下拨金额"></el-input>
                            <span style="color:#676767">（将下拨账户内所有余额转入下拨主账户）</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="20">
                        <el-form-item label="下拨关系">
                            <el-tabs v-model="tabActive" type="card">
                                <el-tab-pane v-for="(item, index) in editableTabs"
                                             :key="item.name"
                                             :label="item.title"
                                             :name="item.name">
                                    <div class="tab-content">
                                        <div class="account-select">
                                            下拨主账户
                                            <!-- :remote-method="getMainAccListByKey" -->
                                            <el-select v-model="item.main_acc_id" filterable remote clearable
                                                        placeholder="请输入关键字"
                                                        style="width:42%;margin-left:6px"
                                                        @visible-change="getMainAccList"
                                                        :disabled="viewReadonly"  
                                                        :loading="loading">
                                                <el-option
                                                        v-for="acc in mainAccOptions"
                                                        :key="acc.acc_id"
                                                        :label="acc.acc_name"
                                                        :value="acc.acc_id">
                                                </el-option>
                                            </el-select>
                                        </div>
                                        <div class="tab-add-collect">
                                            <span>添加被下拨账户</span>
                                            <i class="el-icon-circle-plus-outline"
                                               @click="addTheAllocate(item,false)" v-show="!viewReadonly"></i>
                                            <div>
                                                <span @click="clearAllocate(item)" v-show="!viewReadonly">全部清除</span>
                                                <span>被下拨账户：</span>
                                                <span>{{ item.child_accounts.length }}</span>
                                                <span>个</span>
                                            </div>
                                        </div>
                                        <div class="content-list">
                                            <el-table :data="item.child_accounts"
                                                      size="mini" height="100%"
                                                      :show-header="false"
                                                      :empty-text="' '"
                                                      @selection-change="selectChange">
                                                <el-table-column prop="child_acc_no" label="收款方账号"
                                                                 :show-overflow-tooltip="true"></el-table-column>
                                                <el-table-column prop="child_acc_bank_name" label="收款方开户行"
                                                                 :show-overflow-tooltip="true"></el-table-column>
                                                <el-table-column
                                                        label="操作" width="50"
                                                        fixed="right">
                                                    <template slot-scope="scope" class="operationBtn">
                                                        <el-tooltip content="删除" placement="bottom" effect="light" v-show="!viewReadonly"
                                                                    :enterable="false" :open-delay="500">
                                                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                                                       @click="removeAllocate(scope.row,scope.$index,item.child_accounts)"></el-button>
                                                        </el-tooltip>
                                                    </template>
                                                </el-table-column>
                                            </el-table>
                                        </div>
                                    </div>
                                </el-tab-pane>
                            </el-tabs>
                        </el-form-item>
                    </el-col>
                    <el-col :span="14">
                        <el-form-item label="下拨频率" style="text-align:left">
                            <el-radio-group v-model="allocationData.allocation_frequency"
                                            :disabled="viewReadonly"
                                            @change="clearDate">
                                <el-radio v-for="(frequency,key) in frequencyList"
                                          :key="key" :label="key">{{ frequency }}
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="21" style="text-align:left">
                        <el-form-item label=" ">
                            <el-row>
                                <el-col :span="6" class="date-select" v-for="datePicker in timesetting_list"
                                        :key="datePicker.id">
                                    <el-input placeholder="请选择时间" prefix-icon="el-icon-date"
                                              v-model="datePicker.show" clearable
                                              :disabled="viewReadonly"
                                              @focus="setCurrDate(datePicker)">
                                    </el-input>
                                </el-col>
                                <el-col :span="2" class="date-select" v-show="!viewReadonly">
                                    <i class="el-icon-circle-plus-outline" @click="addDatePicker"
                                       v-show="timesetting_list.length < 3"></i>
                                    <i class="el-icon-remove-outline" @click="delDatePicker"
                                       v-show="timesetting_list.length > 1"></i>
                                </el-col>
                            </el-row>
                        </el-form-item>
                    </el-col>
                    <el-col :span="14">
                        <el-form-item label="摘要">
                            <el-input v-model="allocationData.summary" :disabled="viewReadonly"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="附件">
                            <Upload @currentFielList="setFileList"
                                    :fileMessage="fileMessage"
                                    :emptyFileList="emptyFileList"
                                    :triggerFile="eidttrigFile"
                                    :isPending="!viewReadonly">
                            </Upload>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </section>
        <!--底部按钮-->
        <div class="btn-bottom">
            <el-button type="warning" plain size="mini" @click="showRightFlow" v-show="viewReadonly">
                审批记录<span class="arrows">></span>
            </el-button>
            <el-button type="warning" plain size="mini" @click="gomoreBills">
                更多单据<span class="arrows">></span>
            </el-button>
            <div class="btnGroup" v-show="!viewReadonly">
                <el-button type="warning" size="small" @click="deleteBill" v-show="allocationData.id">删除</el-button>
                <el-button type="warning" size="small" @click="clearAll" v-show="!allocationData.id">清空</el-button>
                <el-button type="warning" size="small" @click="saveAllocate">保存</el-button>
                <el-button type="warning" size="small" @click="submitBill">提交</el-button>
            </div>
        </div>
        <!--添加被下拨账户弹框-->
        <el-dialog :visible.sync="dialogVisible"
                    class="tableDialog"
                   width="860px" title="添加被下拨账户"
                   top="76px" :close-on-click-modal="false">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="6">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" clearable placeholder="请输入收款方名称或账号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item>
                            <el-select v-model="searchData.acc_attr" placeholder="请选择账户类型"
                                       clearable filterable
                                       style="width:100%">
                                <el-option v-for="(purpose,key) in purposeList"
                                           :key="key"
                                           :label="purpose"
                                           :value="key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item>
                            <el-select v-model="searchData.bank_type" placeholder="请选择银行大类"
                                       clearable filterable
                                       style="width:100%"
                                       :filter-method="filterBankType"
                                       @visible-change="clearSearch">
                                <el-option v-for="bankType in bankTypeList"
                                           :key="bankType.name"
                                           :label="bankType.name"
                                           :value="bankType.code">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <el-button type="primary" plain @click="queryDialogData" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <div class="dialog-content">
                <template v-for="table in dialogTableList" >  
                    <el-table :data="table.accounts" :key="table.org_id"
                             size="mini" class="dialogTable"
                             max-height="250"
                            @selection-change="selectChange($event,table)">
                        <el-table-column type="selection" width="38"></el-table-column>
                        <el-table-column prop="child_acc_no" :label="table.org_name" :show-overflow-tooltip="true"></el-table-column>
                        <el-table-column prop="child_acc_bank_name" label=""
                                        :show-overflow-tooltip="true"></el-table-column>
                    </el-table>
                </template> 
            </div>
            <span slot="footer" class="dialog-footer" style="text-align:center">
                <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="addAllocate">确 定</el-button>
            </span>
        </el-dialog>
        <!--选择时间弹框-->
        <el-dialog :visible.sync="dateDialog"
                   width="600px" title="下拨时间选择"
                   top="140px" :close-on-click-modal="false">
            <div class="set-date">
                <h5 v-show="allocationData.allocation_frequency == 3">请选择日期</h5>
                <ul class="month-day" v-show="allocationData.allocation_frequency == 3">
                    <li v-for="item in monthDay" :key="item.day"
                        :class="{active:item.isActive}"
                        @click="item.isActive = !item.isActive">{{ item.day }}
                    </li>
                </ul>
                <el-checkbox-group v-model="dateSelect.weekDate" size="small"
                                   v-show="allocationData.allocation_frequency == 2">
                    <el-checkbox-button v-for="(week,k) in weeks"
                                        :label="k"
                                        :key="k">
                        {{week}}
                    </el-checkbox-button>
                </el-checkbox-group>
                <el-time-picker
                    arrow-control
                    v-model="dateSelect.timeDate"
                    :format="'HH:mm'"
                    value-format="HH:mm"
                    placeholder="请选择时间">
                </el-time-picker>
            </div>
            <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="dateDialog = false">取 消</el-button>
                    <el-button type="warning" size="mini" @click="comfirmCurrDate">确 定</el-button>
                </span>
        </el-dialog>
        <el-dialog :visible.sync="commitVisible"
                    width="50%" title="提交审批流程" top="76px"
                    :close-on-click-modal="false" class="workFlow">
            <h1 slot="title" class="dialog-title">提交审批流程</h1>
            <el-radio-group v-model="selectWorkflow">
                <el-radio v-for="workflow in workflows"
                            :key="workflow.define_id"
                            :label="workflow.define_id"
                >{{ workflow.workflow_name }}
                </el-radio>
            </el-radio-group>
            <span slot="footer" class="dialog-footer" style="text-align:center">
                <el-button type="warning" size="mini" plain @click="commitVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="confirmWorkflow">确 定</el-button>
            </span>
        </el-dialog>
        <!-- 右侧流程图 -->
        <div id="showbox">
            <BusinessTracking
                :businessParams="businessParams"
                @closeRightDialog="closeRightFlow"
            ></BusinessTracking>
        </div>
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";
    import BusinessTracking from "../publicModule/BusinessTracking.vue";
    export default {
        name: "AutoAllocationSet",
        created: function () {
            this.$emit("transmitTitle", "自动下拨设置");
            /*获取常量数据*/
            //获取下拨额度
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.CollOrPoolType) {
                let poolType = constants.CollOrPoolType;
                for(let i in poolType){
                    if(poolType[i] == "定额"){
                        this.allocation_type = i;
                    }
                }
            }
            //下拨频率
            if (constants.CollOrPoolFrequency) {
                this.frequencyList = constants.CollOrPoolFrequency;
            }
            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
                this.bankTypeList = bankTypeList;
            }
            //账户类型
            var catgList = JSON.parse(window.sessionStorage.getItem("catgList"));
            for (var i = 0; i < catgList.length; i++) {
                if (catgList[i].code == "acc_purpose") {
                    this.purposeList = catgList[i].items;
                    break;
                }
            }
        },
        mounted: function(){
            var params = this.$route.params;
            if(params.id){
                if(params.type==='view'){
                    this.viewReadonly = true;
                }
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "allocset_detail",
                        params: {
                            id: params.id
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
                        var frequency_detail = data.frequency_detail;
                        frequency_detail.forEach(element=>{
                            let obj = {};
                            if(data.allocation_frequency === 1){
                                element.show = element.allocation_time;
                            }else{
                                element.show =  element.allocation_frequency_detail + "-" + element.allocation_time;
                            }
                        })
                        this.timesetting_list = frequency_detail;
                        var main_accounts = data.main_accounts;
                        main_accounts.forEach(element=>{
                            element.name = element.tab;
                            element.title = "0" + element.tab;
                        })
                        //查询主账户列表
                        this.mainAccOptions = [{
                            acc_id: main_accounts[0].main_acc_id,
                            acc_name: main_accounts[0].main_acc_name
                        }]
                        this.editableTabs = main_accounts;
                        data.allocation_frequency = data.allocation_frequency+"";
                        
                        if(params.type==='copy'){
                            data.id = "";
                            data.persist_version = "";
                            data.topic = data.topic+"_copy";
                        }

                        this.allocationData = data;
                        
                        //查询附件
                        this.fileMessage.bill_id = params.id;
                        this.eidttrigFile = !this.eidttrigFile;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            }
        },
        data: function () {
            return {
                allocationData: { //表单数据
                    allocation_frequency: "1"
                },
                tabActive: "1",
                formLabelWidth: "100px",
                editableTabs: [{ //控制标签页数据
                    title: '01',
                    name: '1',
                    main_acc_id: "",
                    child_accounts: [],
                },
                // { //控制标签页数据
                //     title: '02',
                //     name: '2',
                //     main_acc_id: "",
                //     child_accounts: []
                // }
                ],
                searchData: {//子账户搜索条件
                    query_key: "",
                    bank_type: "",
                    acc_attr: ""
                },
                dateDialog: false,
                dialogVisible: false, //弹框数据
                dateObj:{
                    checkboxGroup2: []
                },
                weeks: { //周选择
                    1: "星期一",
                    2: "星期二",
                    3: "星期三",
                    4: "星期四",
                    5: "星期五",
                    6: "星期六",
                    7: "星期日",
                },
                monthDay: [ //月份选择
                    {day:"01",isActive:false},
                    {day:"02",isActive:false},
                    {day:"03",isActive:false},
                    {day:"04",isActive:false},
                    {day:"05",isActive:false},
                    {day:"06",isActive:false},
                    {day:"07",isActive:false},
                    {day:"08",isActive:false},
                    {day:"09",isActive:false},
                    {day:"10",isActive:false},
                    {day:"11",isActive:false},
                    {day:"12",isActive:false},
                    {day:"13",isActive:false},
                    {day:"14",isActive:false},
                    {day:"15",isActive:false},
                    {day:"16",isActive:false},
                    {day:"17",isActive:false},
                    {day:"18",isActive:false},
                    {day:"19",isActive:false},
                    {day:"20",isActive:false},
                    {day:"21",isActive:false},
                    {day:"22",isActive:false},
                    {day:"23",isActive:false},
                    {day:"24",isActive:false},
                    {day:"25",isActive:false},
                    {day:"26",isActive:false},
                    {day:"27",isActive:false},
                    {day:"28",isActive:false},
                    {day:"29",isActive:false},
                    {day:"30",isActive:false},
                    {day:"31",isActive:false}
                ],
                frequencyList: [],//下拨频率
                timesetting_list: [ //时间选择器内容
                    {allocation_time: "", allocation_frequency_detail: "", show:""}
                ],
                dateSelect: {
                    weekDate: [],
                    timeDate: ""
                },
                currTimeSetting: {},
                purposeList: [],//账户类型
                bankTypeList: [],
                dialogTableList: [], //弹框表格数据
                loading: false, //主账户列表加载数据时状态显示
                mainAccOptions:[],//主账户列表
                selectList: [],//选择要添加的子账户
                allocation_type:"",
                fileMessage: { //附件
                    bill_id: "",
                    biz_type: 13
                },
                emptyFileList: [],
                eidttrigFile: false,
                commitVisible: false,
                workflows: [],//审批流程级别
                workflowData: {},
                selectWorkflow: "", //流程选择
                viewReadonly: false,//查看只读
                businessParams: {},//业务追踪
            }
        },
        components: {
            Upload: Upload,
            BusinessTracking:BusinessTracking
        },
        methods: {
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
            //重置银行大类数据
            clearSearch: function () {
                if (this.bankTypeList != this.bankAllList) {
                    this.bankTypeList = this.bankAllList;
                }
            },
            //查询被下拨子账户
            queryDialogData: function () {
                var tabActive = this.tabActive;
                var tabList = this.editableTabs;
                for (var i = 0; i < tabList.length; i++) {
                    if (tabList[i].name == tabActive) {
                        this.addTheAllocate(tabList[i], true);
                        break;
                    }
                }
            },
            //点击添加被下拨账号，查询子账户列表
            addTheAllocate: function(currTab, addParams){
                if (!currTab.main_acc_id) {
                    this.$message({
                        type: "warning",
                        message: "请选择下拨主账户",
                        duration: 2000
                    });
                    return;
                }

                //组织params
                var params = {};
                var searchData = this.searchData;
                if (!addParams) {
                    //清空查询条件，数据
                    for (var k in searchData) {
                        searchData[k] = "";
                    }
                    this.dialogTableList = [];
                } else {
                    //点击弹框的搜索查询参数
                    for (var k in searchData) {
                        params[k] = searchData[k];
                    }
                }
                var childList = currTab.child_accounts;//当前tab下选择过的账户
                var excludeInstIds = [];
                childList.forEach((childItem) => {
                    excludeInstIds.push(childItem.child_acc_id);
                });
                params.excludeInstIds = excludeInstIds;
                //查询子账户列表
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "allocset_childacclist",
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
                        this.dialogTableList = data;
                        this.dialogVisible = true;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //选择被下拨弹框选择列表后
            selectChange: function (selectVal,curTable) {
                curTable.selectList = selectVal;
            },
            //添加被下拨账户
            addAllocate: function () {
                var list = this.dialogTableList;
                var hasSelList = [];
                list.forEach(element => {
                    var selList = element.selectList;
                    if(selList && selList.length>0){
                        hasSelList = hasSelList.concat(selList);
                    }
                });
                if (hasSelList.length == 0) {
                    this.$message({
                        type: "warning",
                        message: "请选择要添加的下拨账户",
                        duration: 2000
                    });
                    return;
                }
                var tabActive = this.tabActive;
                var tabList = this.editableTabs;
                for (var i = 0; i < tabList.length; i++) {
                    var tabItem = tabList[i];
                    if (tabItem.name == tabActive) {
                        tabItem.child_accounts = tabItem.child_accounts.concat(hasSelList);
                        this.dialogVisible = false;
                        break;
                    }
                }
            },
            //删除被下拨账户
            removeAllocate: function (row, index, rows) {
                rows.splice(index, 1);
            },
            //清楚被下拨账户
            clearAllocate: function (currTab) {
                currTab.child_accounts = [];
            },
            //确认设置时间
            comfirmCurrDate: function () {
                var frequency = this.allocationData.allocation_frequency;
                var dateSelect = this.dateSelect;
                if (frequency == 1) { //只设置时间
                    this.currTimeSetting.show = dateSelect.timeDate;
                } else if (frequency == 2) { //设置周
                    var str = dateSelect.weekDate.join(",");
                    this.currTimeSetting.show = str + "-" + dateSelect.timeDate;
                    this.currTimeSetting.allocation_frequency_detail = str;
                } else { //设置月
                    var monthDay = this.monthDay;
                    var activeDay = "";
                    monthDay.forEach((dayItem) => {
                        if (dayItem.isActive) {
                            activeDay += dayItem.day + ",";
                        }
                    });
                    var str = activeDay.slice(0, activeDay.length - 1);
                    this.currTimeSetting.show = str + "-" + dateSelect.timeDate;
                    this.currTimeSetting.allocation_frequency_detail = str;
                }
                this.currTimeSetting.allocation_time = dateSelect.timeDate;
                this.dateDialog = false;
            },
            //添加时间选择器
            addDatePicker: function () {
                var datePickList = this.timesetting_list;
                if (datePickList.length < 3) {
                    datePickList.push({allocation_time: "", allocation_frequency_detail: "", show:""});
                }
            },
            //删除时间选择器
            delDatePicker: function () {
                var datePickList = this.timesetting_list;
                if (datePickList.length > 1) {
                    datePickList.pop();
                }
            },
            //设置时间
            setCurrDate: function (currDate) {
                //清空数据
                var frequency = this.allocationData.allocation_frequency;
                var dateSelect = this.dateSelect;
                dateSelect.timeDate = "";
                if (frequency == 2) { //周
                    dateSelect.weekDate = [];
                } else { //月
                    this.monthDay.forEach((dayItem) => {
                        if (dayItem.isActive) {
                            dayItem.isActive = false;
                        }
                    });
                }
                //保存当前数据
                this.currTimeSetting = currDate;
                this.dateDialog = true;
            },
            //改变下拨频率后清空其时间选择
            clearDate: function () {
                this.timesetting_list = [{allocation_time: "", allocation_frequency_detail: "", show:""}];
            },
            gomoreBills: function(){
                this.$router.push("/allocation/allocation-more-bills");
            },
            selectDate: function(){
                this.dateDialog = true;
            },
            //设置当前项上传附件
            setFileList: function ($event) {
                this.fileList = [];
                if ($event.length > 0) {
                    $event.forEach((item) => {
                        this.fileList.push(item.id);
                    })
                }
            },
            //下拉展开查询主账户列表方法
            getMainAccList: function(query){
                this.mainAccOptions = [];
                var tabActive = this.tabActive;
                var tabList = this.editableTabs;
                var excludeInstIds = [];
                tabList.forEach((tabItem) => {
                    if (tabItem.name != tabActive && tabItem.main_acc_id) {
                        excludeInstIds.push(tabItem.main_acc_id);
                    }
                });
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "allocset_mainacclist",
                        params: {
                            query_key: query,
                            excludeInstIds:excludeInstIds
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
                        this.loading = false;
                        var data = result.data.data;
                        this.mainAccOptions = data;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //关键字查询主账户列表
            getMainAccListByKey: function(query){
                if (query && query.trim()) {
                    this.loading = true;
                    this.getMainAccList(query);
                } 
            },
            //组织保存参数
            setParams: function(){
                var data = this.allocationData;
                data.frequency_detail = this.timesetting_list;
                data.main_accounts = [];
                var editableTabs = this.editableTabs;
                editableTabs.forEach((tab)=>{
                    var obj = {};
                    obj.tab = tab.name
                    obj.main_acc_id = tab.main_acc_id;
                    obj.child_accounts = [];
                    var list = tab.child_accounts;
                    list.forEach((item)=>{
                        obj.child_accounts.push(item.acc_id);
                    })
                    data.main_accounts.push(obj);
                })
                data.allocation_type = this.allocation_type;
                data.files = this.fileList;
                return data;
            },
            //清空
            clearAll: function () {
                var data = this.allocationData;
                for (var k in data) {
                    if (k == "frequency_detail" && k == "files" && k == "main_accounts") {
                        data[k] = [];
                    }else{
                        if (k == "allocation_frequency") {
                            data[k] = "1";
                        } else {
                            data[k] = "";
                        }
                    }
                }
                this.editableTabs.forEach((tabItem) => {
                    this.clearAllocate(tabItem);
                    tabItem.main_acc_id = "";
                });
                this.clearDate();
                this.emptyFileList = [];
            },
            //删除单据
            deleteBill:function (){
                var param = this.allocationData;
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "allocset_del",
                        params: {
                            id: param.id,
                            persist_version: param.persist_version
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
                        this.clearAll();
                        this.$message({
                            type: "success",
                            message: "删除成功",
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //保存
            saveAllocate:function (){
                // var query = this.$route.params;
                var params = this.setParams();
                var optype = params.id ? 'allocset_chg' : 'allocset_add';
                var message = params.id ? '修改成功' : '保存成功';
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
                        this.allocationData.id = data.id;
                        this.allocationData.persist_version = data.persist_version;
                        this.$message({
                            type: "success",
                            message: message,
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //提交
            submitBill: function(){
                var params = this.setParams();
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "allocset_presubmit",
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
                        this.allocationData.persist_version = data.persist_version;
                        this.allocationData.id = data.id;
                        this.commitVisible = true;
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
                        optype: "allocset_submit",
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
                        this.commitVisible = false;
                        this.clearAll();
                        this.$message({
                            type: "success",
                            message: "提交成功",
                            duration: 2000
                        })
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //业务追踪显示
            showRightFlow:function (row) {
                this.businessParams = {};
                this.businessParams.id = this.allocationData.id;
                this.businessParams.biz_type = "13";
                this.businessParams.type = 1;
                document.getElementById("showbox").style.right="0px";
            },
            closeRightFlow:function(){
                this.businessParams = {};
                document.getElementById("showbox").style.right="-500px";
            }
        }
    }
</script>
