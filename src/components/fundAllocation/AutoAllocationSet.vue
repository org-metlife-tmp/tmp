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
            }

            /*下拨频率日期选择*/
            .date-select {
                .el-input{
                    width: 80%;
                }

                i {
                    font-size: 22px;
                    vertical-align: middle;
                    color: #bbb;
                    cursor: pointer;
                }

                i:nth-child(3) {
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
</style>
<style lang="less" type="text/less">
    #autoAllocationSet {
        .el-tabs__header {
            margin-bottom: 0;
        }
        .el-tabs__item {
            height: 32px;
            line-height: 32px;
        }
    }
</style>

<template>
    <div id="autoAllocationSet">
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
                        <el-form-item label="下拨主题">
                            <el-input v-model="collectionData.acc_no" placeholder="请为本次下拨主题命名以方便识别"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" style="text-align:left">
                        <el-form-item label="定额下拨">
                            <el-input style="width:200px" v-model="collectionData.acc_name" placeholder="请填写下拨金额"></el-input>
                            <span style="color:#676767">（将下拨账户内所有余额转入下拨主账户）</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="20">
                        <el-form-item label="下拨关系">
                            <el-tabs v-model="collectionData.activeName" type="card" closable
                                     @tab-remove="removeTab">
                                <el-tab-pane v-for="(item, index) in editableTabs"
                                             :key="item.name"
                                             :label="item.title"
                                             :name="item.name">
                                    <div class="tab-content">
                                        <div class="account-select">
                                            下拨主账户
                                            <el-input v-model="collectionData.acc_name"></el-input>
                                        </div>
                                        <div class="tab-add-collect">
                                            <span>添加被下拨账户</span>
                                            <i class="el-icon-circle-plus-outline" @click="addTheCollect"></i>
                                            <div>
                                                <span>全部清除</span>
                                                <span>被下拨账户：</span>
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
                        <el-form-item label="下拨频率" style="text-align:left">
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
                                <el-col :span="8" class="date-select">
                                    <el-input placeholder="请输入内容" prefix-icon="el-icon-date"
                                              v-model="collectionData.input21"
                                              @focus="selectDate">
                                    </el-input>
                                    <i class="el-icon-circle-plus-outline"></i>
                                    <i class="el-icon-remove-outline"></i>
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
            <el-button type="warning" plain size="mini" @click="gomoreBills">
                更多单据<span class="arrows">></span>
            </el-button>
            <div class="btnGroup">
                <el-button type="warning" size="small" @click="">清空</el-button>
                <el-button type="warning" size="small" @click="">保存</el-button>
                <el-button type="warning" size="small" @click="">提交</el-button>
            </div>
        </div>
        <!--添加被下拨账户弹框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="860px" title="添加被下拨账户"
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
                   width="600px" title="选择时间"
                   top="140px" :close-on-click-modal="false">

            <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="dateDialog = false">取 消</el-button>
                    <el-button type="warning" size="mini" @click="">确 定</el-button>
                </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "AutoAllocationSet",
        created: function () {
            this.$emit("transmitTitle", "自动下拨设置");
            // 
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
                }],
                dialogVisible: false, //弹框数据
                searchData: {},
                dateDialog: false,
            }
        },
        methods: {
            //删除tab页
            removeTab:function(targetName) {
                let tabs = this.editableTabs;
                let activeName = this.collectionData.activeName;
                if (activeName === targetName) {
                    tabs.forEach((tab, index) => {
                        if (tab.name === targetName) {
                            let nextTab = tabs[index + 1] || tabs[index - 1];
                            if (nextTab) {
                                activeName = nextTab.name;
                            }
                        }
                    });
                }
                this.collectionData.activeName = activeName;
                this.editableTabs = tabs.filter(tab => tab.name !== targetName);
            },
            //添加被下拨账号
            addTheCollect: function(){
                this.dialogVisible = true;
            },
            //选择时间
            selectDate: function(){
                this.dateDialog = true;
            },
            gomoreBills: function(){
                this.$router.push("/allocation/allocation-more-bills");
            }
        }
    }
</script>
