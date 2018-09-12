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
            }

            /*归集频率日期选择*/
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
    }

    /*时间选择弹框*/
    .set-date{
        text-align: center;
        margin-top: 10px;
        margin-bottom: 30px;

        h5{
            width: 60%;
            margin: 0 auto 10px;
            background: #3dbaf0;
            color: #fff;
        }

        .el-checkbox-group{
            margin-bottom: 30px;
        }

        .month-day{
            width: 60%;
            height: 120px;
            margin: 0 auto 30px;

            li{
                float: left;
                width: 14%;
                cursor: pointer;
            }

            li:hover{
                .active;
            }

            .active{
                background-color: #3dbaf0;
                border-radius: 6px;
                color: #fff;
            }
        }
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
            <el-button type="warning" size="mini" @click="">打印</el-button>
        </div>
        <!--中间内容-->
        <section class="table-content">
            <el-form :model="collectionData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="14">
                        <el-form-item label="归集主题">
                            <el-input v-model="collectionData.acc_no" placeholder="请为本次归集主题命名以方便识别"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="14" style="text-align:left">
                        <el-form-item label="归集额度">
                            <el-radio-group v-model="collectionData.resource">
                                <el-radio label="1">全额归集</el-radio>
                                <el-radio label="2">定额归集</el-radio>
                                <el-radio label="3">留存余额归集</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="14" style="text-align:left">
                        <el-form-item label=" ">
                            <el-row>
                                <el-col :span="6">
                                    <el-input v-model="collectionData.acc_name" placeholder="请填写归集金额"></el-input>
                                </el-col>
                                <el-col :span="1" style="height:1px"></el-col>
                                <el-col :span="16" style="color:#676767">
                                    将归集账户内所有余额转入归集主账户
                                </el-col>
                            </el-row>
                        </el-form-item>
                    </el-col>
                    <el-col :span="20">
                        <el-form-item label="归集关系">
                            <el-tabs v-model="collectionData.activeName" type="card"
                                     @tab-click="handleClick">
                                <el-tab-pane v-for="(item, index) in editableTabs"
                                             :key="item.name"
                                             :label="item.title"
                                             :name="item.name">
                                    <div class="tab-content">
                                        <div class="account-select">
                                            归集主账户
                                            <el-input v-model="collectionData.acc_name"></el-input>
                                        </div>
                                        <div class="tab-add-collect">
                                            <span>添加被归集账户</span>
                                            <i class="el-icon-circle-plus-outline" @click="addTheCollect"></i>
                                            <div>
                                                <span>全部清除</span>
                                                <span>被归集账户：</span>
                                                <span>0</span>
                                                <span>个</span>
                                            </div>
                                        </div>
                                    </div>
                                </el-tab-pane>
                            </el-tabs>
                        </el-form-item>
                    </el-col>
                    <el-col :span="14">
                        <el-form-item label="归集频率" style="text-align:left">
                            <el-radio-group v-model="collectionData.resource">
                                <el-radio label="1">每日</el-radio>
                                <el-radio label="2">每周</el-radio>
                                <el-radio label="3">每月</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="21" style="text-align:left">
                        <el-form-item label=" ">
                            <el-row>
                                <el-col :span="6" class="date-select" v-for="item in datePickList" :key="item">
                                    <el-input placeholder="请输入内容" prefix-icon="el-icon-date"
                                              v-model="collectionData.input21"
                                              @focus="dateDialog = true">
                                    </el-input>
                                </el-col>
                                <el-col :span="2" class="date-select">
                                    <i class="el-icon-circle-plus-outline" @click="addDatePicker"
                                       v-show="datePickList.length < 3"></i>
                                    <i class="el-icon-remove-outline" @click="delDatePicker"
                                       v-show="datePickList.length > 1"></i>
                                </el-col>
                            </el-row>
                        </el-form-item>
                    </el-col>
                    <el-col :span="14">
                        <el-form-item label="摘要">
                            <el-input v-model="collectionData.acc_no"></el-input>
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
                <el-button type="warning" size="small" @click="">清空</el-button>
                <el-button type="warning" size="small" @click="">保存</el-button>
                <el-button type="warning" size="small" @click="">提交</el-button>
            </div>
        </div>
        <!--添加被归集账户弹框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="860px" title="添加被归集账户"
                   top="140px" :close-on-click-modal="false">

            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="6">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" clearable placeholder="请输入账户号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" clearable placeholder="请输入账户号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" clearable placeholder="请输入账户号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <el-button type="primary" plain @click="" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>

            <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                    <el-button type="warning" size="mini" @click="">确 定</el-button>
                </span>
        </el-dialog>
        <!--选择时间弹框-->
        <el-dialog :visible.sync="dateDialog"
                   width="600px" title="归集时间选择"
                   top="140px" :close-on-click-modal="false">
            <div class="set-date">
                <h5>请选择日期</h5>
                <ul class="month-day">
                    <li v-for="item in monthDay" :key="item.day"
                        :class="{active:item.isActive}"
                        @click="item.isActive = !item.isActive">{{ item.day }}</li>
                </ul>
                <el-checkbox-group v-model="dateObj.checkboxGroup2" size="small">
                    <el-checkbox-button v-for="(week,k) in weeks"
                                        :label="k"
                                        :key="k">
                        {{week}}
                    </el-checkbox-button>
                </el-checkbox-group>
                <el-time-picker
                        arrow-control
                        v-model="dateObj.value3"
                        :format="'HH:mm'"
                        placeholder="请选择时间">
                </el-time-picker>
            </div>
            <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="dateDialog = false">取 消</el-button>
                    <el-button type="warning" size="mini" @click="">确 定</el-button>
                </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "CollectionSet",
        created: function () {
            this.$emit("transmitTitle", "自动归集设置");
        },
        data: function () {
            return {
                collectionData: { //表单数据
                    activeName: "1",
                },
                formLabelWidth: "100px",
                editableTabs: [{ //控制标签页数据
                    title: '01',
                    name: '1'
                }, {
                    title: '+',
                    name: '2'
                }],
                dialogVisible: false, //弹框数据
                searchData: {},
                dateDialog: false,
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
                datePickList:[1]
            }
        },
        methods: {
            //点击tab标签
            handleClick: function(tab,event){
                if(tab.label == "+"){
                    var editableTabs = this.editableTabs;
                    if(editableTabs.length < 3){
                        editableTabs[1].title = "02";
                        editableTabs.push({
                            title: '+',
                            name: '3'
                        });
                        this.collectionData.activeName = "2";
                    }else{
                        editableTabs[2].title = "03";
                        this.collectionData.activeName = "3";
                    }
                }
            },
            //添加被归集账号
            addTheCollect: function(){
                this.dialogVisible = true;
            },
            //添加时间选择器
            addDatePicker: function(){
                var datePickList = this.datePickList;
                if(datePickList.length < 3){
                    datePickList.push(datePickList[datePickList.length-1] + 1);
                }
            },
            //删除时间选择器
            delDatePicker: function(){
                var datePickList = this.datePickList;
                if(datePickList.length > 1){
                    datePickList.pop();
                }
            }
        }
    }
</script>
