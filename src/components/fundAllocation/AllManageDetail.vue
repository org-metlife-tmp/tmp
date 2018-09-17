<style scoped lang="less" type="text/less">
    #allManageDetail{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }
</style>
<style lang="less">
    #allManageDetail {
        .el-dialog__wrapper {
            .el-dialog__body {
                height: 300px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="allManageDetail">
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
    </div>
</template>

<script>
    export default {
        name: "AllManageDetail",
        created: function () {
            this.$emit("transmitTitle", "自动归集管理-查看明细");
        },
        mounted: function () {
            //下拨频率
            // var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            // if (constants.PollingFrequency) {
            //     this.pollFrequencyList = constants.PollingFrequency;
            // }
            // //下拨额度
            // if (constants.PoolingType) {
            //     this.poolTypeList = constants.PoolingType;
            // }
            
        },
        props:["tableData"],
        data: function () {
            return {
            }
        },
        methods: {
        },
        watch:{
            tableData: function (val, oldVal) {
                
                // this.tableList = val.data;
            }
        }
    }
</script>

