<style scoped lang="less" type="text/less">
    #collectionSet {
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

                /*归集主账户选择*/
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

                /*添加被归集账户*/
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

                /*被归集账户列表*/
                .content-list {
                    height: 152px;
                }
            }

            /*归集频率日期选择*/
            .date-select {
                .el-input {
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

    /*被归集账户选择弹框*/
    .dialog-content {
        height: 360px;
        overflow-y: auto;
    }
</style>
<style lang="less" type="text/less">
    #collectionSet {
        .el-tabs__header {
            margin-bottom: 0;
        }
    }
</style>

<template>
    <div id="collectionSet">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <!--<el-button type="warning" size="mini" @click="">打印</el-button>-->
        </div>
        <!--中间内容-->
        <section class="table-content">
            <el-form :model="collectionData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="14">
                        <el-form-item label="归集主题">
                            <el-input v-model="collectionData.topic" placeholder="请为本次归集主题命名以方便识别"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="14" style="text-align:left">
                        <el-form-item label="归集额度">
                            <el-radio-group v-model="collectionData.collect_type">
                                <el-radio v-for="(collType,key) in collTypeList"
                                          :key="key" :label="key">{{ collType }}
                                </el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="14" style="text-align:left">
                        <el-form-item label=" ">
                            <el-row>
                                <el-col :span="6">
                                    <el-input v-model="collectionData.collect_amount" placeholder="请填写归集金额"
                                              :disabled="amountStatus"></el-input>
                                </el-col>
                                <el-col :span="1" style="height:1px"></el-col>
                                <el-col :span="16" style="color:#676767">
                                    <span v-show="collectionData.collect_type == 3">将归集账户内所有余额转入归集主账户</span>
                                    <span v-show="collectionData.collect_type == 1">从每个被归集账户内转出固定金额到归集主账户</span>
                                    <span v-show="collectionData.collect_type == 2">为每个被归集账户保留一个固定金额后的剩余金额转入归集主账户</span>
                                </el-col>
                            </el-row>
                        </el-form-item>
                    </el-col>
                    <el-col :span="20">
                        <el-form-item label="归集关系">
                            <el-tabs v-model="tabActive" type="card"
                                     @tab-click="handleClick">
                                <el-tab-pane v-for="(item, index) in editableTabs"
                                             :key="item.name"
                                             :label="item.tab"
                                             :name="item.name">
                                    <div class="tab-content">
                                        <div class="account-select">
                                            归集主账户
                                            <el-select v-model="item.main_acc_id" placeholder="请选择主账户"
                                                       style="width:42%;margin-left:6px"
                                                       filterable clearable size="mini"
                                                       @visible-change="getAccList"
                                                       @change="setMain($event,item)">
                                                <el-option v-for="item in accOptions"
                                                           :key="item.main_acc_id"
                                                           :label="item.main_acc_name"
                                                           :value="item.main_acc_id">
                                                </el-option>
                                            </el-select>
                                        </div>
                                        <div class="tab-add-collect">
                                            <span>添加被归集账户</span>
                                            <i class="el-icon-circle-plus-outline"
                                               @click="getTheCollect(item,false)"></i>
                                            <div>
                                                <span @click="clearCollect(item)">全部清除</span>
                                                <span>被归集账户：</span>
                                                <span>{{ item.child_list.length }}</span>
                                                <span>个</span>
                                            </div>
                                        </div>
                                        <div class="content-list">
                                            <el-table :data="item.child_list"
                                                      size="mini" height="100%"
                                                      :show-header="false"
                                                      :empty-text="' '"
                                                      @selection-change="selectChange">
                                                <el-table-column prop="child_acc_name" label="收款方名称"
                                                                 :show-overflow-tooltip="true"></el-table-column>
                                                <el-table-column prop="child_acc_no" label="收款方账号"
                                                                 :show-overflow-tooltip="true"></el-table-column>
                                                <el-table-column prop="child_acc_bank_name" label="收款方开户行"
                                                                 :show-overflow-tooltip="true"></el-table-column>
                                                <el-table-column
                                                        label="操作" width="50"
                                                        fixed="right">
                                                    <template slot-scope="scope" class="operationBtn">
                                                        <el-tooltip content="删除" placement="bottom" effect="light"
                                                                    :enterable="false" :open-delay="500">
                                                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                                                       @click="removeCollect(scope.row,scope.$index,item.child_list)"></el-button>
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
                        <el-form-item label="归集频率" style="text-align:left">
                            <el-radio-group v-model="collectionData.collect_frequency"
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
                                              v-model="datePicker.dateItem" clearable
                                              @focus="setCurrDate(datePicker)">
                                    </el-input>
                                </el-col>
                                <el-col :span="2" class="date-select">
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
                            <el-input v-model="collectionData.summary"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="附件">
                            <Upload @currentFielList="setFileList"
                                    :fileMessage="fileMessage"
                                    :emptyFileList="emptyFileList"
                                    :isPending="true">
                            </Upload>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </section>
        <!--底部按钮-->
        <div class="btn-bottom">
            <el-button type="warning" plain size="mini" @click="">
                更多单据<span class="arrows">></span>
            </el-button>
            <div class="btnGroup">
                <el-button type="warning" size="small" @click="clearAll">清空</el-button>
                <el-button type="warning" size="small" @click="saveCollect">保存</el-button>
                <el-button type="warning" size="small" @click="">提交</el-button>
            </div>
        </div>
        <!--添加被归集账户弹框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="860px" title="添加被归集账户"
                   top="80px" :close-on-click-modal="false">

            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="6">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" clearable placeholder="请输入收款方名称或账号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item>
                            <el-select v-model="searchData.acc_type" placeholder="请选择账户类型"
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
                <el-table :data="dialogList"
                          border size="mini"
                          @selection-change="selectChange">
                    <el-table-column type="selection" width="38"></el-table-column>
                    <el-table-column prop="child_acc_name" label="收款方名称"
                                     :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="child_acc_no" label="收款方账号" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="child_acc_bank_name" label="收款方开户行"
                                     :show-overflow-tooltip="true"></el-table-column>
                </el-table>
            </div>

            <span slot="footer" class="dialog-footer" style="text-align:center">
                <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="addCollect">确 定</el-button>
            </span>
        </el-dialog>
        <!--选择时间弹框-->
        <el-dialog :visible.sync="dateDialog"
                   width="600px" title="归集时间选择"
                   top="140px" :close-on-click-modal="false">
            <div class="set-date">
                <h5 v-show="collectionData.collect_frequency == 3">请选择日期</h5>

                <ul class="month-day" v-show="collectionData.collect_frequency == 3">
                    <li v-for="item in monthDay" :key="item.day"
                        :class="{active:item.isActive}"
                        @click="item.isActive = !item.isActive">{{ item.day }}
                    </li>
                </ul>

                <el-checkbox-group v-model="dateSelect.weekDate" size="small"
                                   v-show="collectionData.collect_frequency == 2">
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
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";

    export default {
        name: "CollectionSet",
        created: function () {
            this.$emit("transmitTitle", "自动归集设置");

            /*获取常量数据*/
            //获取归集方式
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.CollOrPoolType) {
                this.collTypeList = constants.CollOrPoolType;
            }
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
        data: function () {
            return {
                collectionData: { //表单数据
                    topic: "",
                    collect_type: "",
                    collect_amount: "",
                    main_list: [],
                    collect_frequency: "1",
                    timesetting_list: [],
                    summary: "",
                    files: []
                },
                editableTabs: [
                    { //控制标签页数据
                        tab: '01',
                        name: '1',
                        main_acc_id: "",
                        child_list: []
                    }, {
                        tab: '+',
                        name: '2',
                        main_acc_id: "",
                        child_list: []
                    }
                ],
                tabActive: "1",
                dialogVisible: false, //选择被归集账户弹框
                formLabelWidth: "100px",
                searchData: {
                    query_key: "",
                    bank_type: "",
                    acc_type: ""
                },
                dialogList: [],
                selectList: [],
                dateDialog: false, //选择时间弹框
                dateSelect: {
                    weekDate: [],
                    timeDate: ""
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
                    {day: "1", isActive: false},
                    {day: "2", isActive: false},
                    {day: "3", isActive: false},
                    {day: "4", isActive: false},
                    {day: "5", isActive: false},
                    {day: "6", isActive: false},
                    {day: "7", isActive: false},
                    {day: "8", isActive: false},
                    {day: "9", isActive: false},
                    {day: "10", isActive: false},
                    {day: "11", isActive: false},
                    {day: "12", isActive: false},
                    {day: "13", isActive: false},
                    {day: "14", isActive: false},
                    {day: "15", isActive: false},
                    {day: "16", isActive: false},
                    {day: "17", isActive: false},
                    {day: "18", isActive: false},
                    {day: "19", isActive: false},
                    {day: "20", isActive: false},
                    {day: "21", isActive: false},
                    {day: "22", isActive: false},
                    {day: "23", isActive: false},
                    {day: "24", isActive: false},
                    {day: "25", isActive: false},
                    {day: "26", isActive: false},
                    {day: "27", isActive: false},
                    {day: "28", isActive: false},
                    {day: "29", isActive: false},
                    {day: "30", isActive: false},
                    {day: "31", isActive: false}
                ],
                timesetting_list: [ //时间选择器内容
                    {dateItem: "", id: new Date().valueOf()}
                ],
                currTimeSetting: {},
                accOptions: [], //下拉框数据
                bankTypeList: [],
                purposeList: {},
                collTypeList: {}, //常量数据
                frequencyList: {},
                fileMessage: { //附件
                    bill_id: "",
                    biz_type: 1
                },
                emptyFileList: [],
                fileList: [],
            }
        },
        components: {
            Upload: Upload
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
            //点击tab标签
            handleClick: function (tab, event) {
                if (tab.label == "+") {
                    var editableTabs = this.editableTabs;
                    if (editableTabs.length < 3) {
                        editableTabs[1].tab = "02";
                        editableTabs.push({
                            tab: '+',
                            name: '3',
                            main_acc_id: "",
                            child_list: []
                        });
                        this.tabActive = "2";
                    } else {
                        editableTabs[2].tab = "03";
                        this.tabActive = "3";
                    }
                }
            },
            //获取归集主账户
            getAccList: function (status) {
                if (status) {
                    var tabActive = this.tabActive;
                    var tabList = this.editableTabs;
                    var exclude_ids = [];
                    tabList.forEach((tabItem) => {
                        if (tabItem.name != tabActive && tabItem.main_acc_id) {
                            exclude_ids.push(tabItem.main_acc_id);
                        }
                    });
                    this.$axios({
                        url: "/cfm/normalProcess",
                        method: "post",
                        data: {
                            optype: "collectsetting_accs",
                            params: {
                                status: 1,
                                acc_id: "",
                                exclude_ids: exclude_ids
                            }
                        }
                    }).then((result) => {
                        this.accOptions = result.data.data;
                    });
                }
            },
            //选择归集主账户
            setMain: function (val, currTab) {
                var accOptions = this.accOptions;
                for (var i = 0; i < accOptions.length; i++) {
                    var accItem = accOptions[i];
                    if (accItem.main_acc_id == val) {
                        for (var k in accItem) {
                            currTab[k] = accItem[k];
                        }
                    }
                }
                currTab.child_list = [];
            },
            //获取被归集账号
            getTheCollect: function (currTab, addParams) {
                if (!currTab.main_acc_id) {
                    this.$message({
                        type: "warning",
                        message: "请选择归集主账户",
                        duration: 2000
                    });
                    return;
                }

                //设置params
                var params = {};
                var searchData = this.searchData;
                if (!addParams) {
                    for (var k in searchData) {
                        searchData[k] = "";
                    }
                    this.dialogList = [];
                    this.dialogVisible = true;
                } else {
                    for (var k in searchData) {
                        params[k] = searchData[k];
                    }
                }
                var childList = currTab.child_list;
                var exclude_ids = [];
                childList.forEach((childItem) => {
                    exclude_ids.push(childItem.child_acc_id);
                });
                params.exclude_ids = exclude_ids;

                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "collectsetting_getchildacclist",
                        params: params
                    }
                }).then((result) => {
                    this.dialogList = result.data.data;
                });
                this.dialogVisible = true;
            },
            //查询被归集账户
            queryDialogData: function () {
                var tabActive = this.tabActive;
                var tabList = this.editableTabs;
                for (var i = 0; i < tabList.length; i++) {
                    if (tabList[i].name == tabActive) {
                        this.getTheCollect(tabList[i], true);
                        break;
                    }
                }
            },
            //选择被归集弹框选择列表后
            selectChange: function (val) {
                this.selectList = val;
            },
            //添加被归集账户
            addCollect: function () {
                if (this.selectList.length == 0) {
                    this.$message({
                        type: "warning",
                        message: "请选择归要添加的账户",
                        duration: 2000
                    });
                    return;
                }
                var tabActive = this.tabActive;
                var tabList = this.editableTabs;
                for (var i = 0; i < tabList.length; i++) {
                    var tabItem = tabList[i];
                    if (tabItem.name == tabActive) {
                        this.selectList.forEach((selectItem) => {
                            tabItem.child_list.push(selectItem);
                        })
                        this.dialogVisible = false;
                        break;
                    }
                }
            },
            //删除被归集账户
            removeCollect: function (row, index, rows) {
                rows.splice(index, 1);
            },
            //清楚被归集账户
            clearCollect: function (currTab) {
                currTab.child_list = [];
            },
            //改变归集频率后清空其时间选择
            clearDate: function () {
                this.timesetting_list = [{dateItem: "", id: new Date().valueOf()}];
            },
            //添加时间选择器
            addDatePicker: function () {
                var datePickList = this.timesetting_list;
                if (datePickList.length < 3) {
                    datePickList.push({dateItem: "", id: new Date().valueOf()});
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
                var frequency = this.collectionData.collect_frequency;
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
            //确认设置时间
            comfirmCurrDate: function () {
                var frequency = this.collectionData.collect_frequency;
                var dateSelect = this.dateSelect;
                if (frequency == 1) { //只设置时间
                    this.currTimeSetting.dateItem = dateSelect.timeDate;
                } else if (frequency == 2) { //设置周
                    this.currTimeSetting.dateItem = dateSelect.weekDate.join(",") + "-" + dateSelect.timeDate;
                } else { //设置月
                    var monthDay = this.monthDay;
                    var activeDay = "";
                    monthDay.forEach((dayItem) => {
                        if (dayItem.isActive) {
                            activeDay += dayItem.day + ",";
                        }
                    });
                    this.currTimeSetting.dateItem = activeDay.slice(0, activeDay.length - 1) + "-" + dateSelect.timeDate;
                }
                this.dateDialog = false;
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
            //保存
            saveCollect: function () {
                var params = this.seParams();

                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "collectsetting_add",
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
                        console.log(data);

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
            //设置params
            seParams: function () {
                var collectionData = this.collectionData;
                var editableTabs = this.editableTabs;
                collectionData.main_list = editableTabs.filter((item) => {
                    return item.main_acc_id;
                });
                collectionData.timesetting_list = [];
                this.timesetting_list.forEach((item) => {
                    collectionData.timesetting_list.push(item.dateItem);
                });
                collectionData.files = this.fileList;
                console.log(collectionData);
                return collectionData;
            },
            //清空
            clearAll: function (clearAll) {
                var collectionData = this.collectionData;
                for (var k in collectionData) {
                    if (k != "main_list" && k != "timesetting_list" && k != "files") {
                        if (k == "collect_frequency") {
                            collectionData[k] = "1";
                        } else {
                            collectionData[k] = "";
                        }
                    }else{
                        collectionData[k] = [];
                    }
                }
                this.editableTabs.forEach((tabItem) => {
                    this.clearCollect(tabItem);
                    tabItem.main_acc_id = "";
                });
                this.clearDate();
                this.emptyFileList = [];
            }
        },
        computed: {
            amountStatus: function () {
                return this.collectionData.collect_type == 3 ? true : false;
            }
        }
    }
</script>
