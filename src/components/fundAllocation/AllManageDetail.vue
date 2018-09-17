<style scoped lang="less" type="text/less">
    #allManageDetail{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*主体内容*/
        .table-content {
            max-height: 94%;
            overflow-y: auto;

            
            /*标签页*/
            .tab-content {
                width: 100%;
                border: 1px solid #e4e7ed;
                box-sizing: border-box;
                border-top: none;

                /*下拨主账户选择*/
                .account-select {
                    text-align: left;
                    padding-left: 20px;
                    padding-top: 15px;
                    color: #676767;
                    >*{
                        margin-right: 10px;
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

                    div {
                        float: right;
                        span:nth-child(2) {
                            color: #FF4900;
                        }
                    }
                }
                /*添加被下拨账户*/
                .aolloct-acc-list{
                    height: 159px;
                    overflow-y: auto;
                    .acc-item{
                        height: 32px;
                        border-bottom: 1px solid #e2e2e2; 
                        box-sizing: border-box;
                        >span{
                            display: inline-block;
                            overflow: hidden;
                            white-space: nowrap;
                            text-overflow: ellipsis;
                            padding: 0  10px;
                        }
                        >span:first-child{
                            border-right: 1px solid #e2e2e2; 
                            width: 150px;
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

            .tacticsTime{
                color: #FF4900;
            }
            .eidtLeft{
                float: left;
                margin-left: 30px;
            }
            .eidtRight{
                float: right;
                margin-right: 20px;
            }
        }
        #showbox{
            position: fixed;
            right: -500px;
            z-index: 999;
            transition: all 1.5s;
        } 
        .policyNo{
            position: absolute;
            top: -45px;
            left: -15px;
            color: #999999;
        }
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
    <div id="allManageDetail">
        <div class="policyNo">
            <span>策略编号[</span>
            <span>GJT201809120000001</span>
            <span>]</span>
        </div>
        <!--中间内容-->
        <section class="table-content">
            <el-form :model="allocationData" size="small"
                     :label-width="formLabelWidth">
                <el-row  style="text-align:left">
                    <el-col :span="14">
                        <el-form-item label="下拨主题">
                            <div v-text="">test</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="定额下拨">
                            <div v-text="">全额归集</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="22">
                        <el-form-item label="下拨关系">
                            <el-tabs v-model="activeName" type="card">
                                <el-tab-pane v-for="(item, index) in editableTabs"
                                             :key="item.name"
                                             :label="item.title"
                                             :name="item.name">
                                    <div class="tab-content">
                                        <div class="account-select">
                                            <span>下拨主账户</span>
                                            <span>11087363445533344</span>
                                            <span>中国建设银行股份有限公司深圳振华支行</span>
                                        </div>
                                        <div class="tab-add-collect">
                                            <span>被下拨账户</span>
                                            <div>
                                                <span>被下拨账户：</span>
                                                <span>1</span>
                                                <span>个</span>
                                            </div>
                                        </div>
                                        <div class="aolloct-acc-list">
                                            <div class="acc-item">
                                                <span>11087363445533344</span>
                                                <span>中国建设银行股份有限公司深圳振华支行</span>
                                            </div>
                                            <div class="acc-item">
                                                <span>11087363445533344</span>
                                                <span>中国建设银行股份有限公司深圳振华支行</span>
                                            </div>
                                            <div class="acc-item">
                                                <span>11087363445533344</span>
                                                <span>中国建设银行股份有限公司深圳振华支行</span>
                                            </div>
                                        </div>
                                    </div>
                                </el-tab-pane>
                            </el-tabs>
                        </el-form-item>
                    </el-col>
                    <el-col :span="14">
                        <el-form-item label="下拨频率" style="text-align:left">
                            <span>固定时点</span>
                            <span class="tacticsTime">01:05</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="14">
                        <el-form-item label="摘要">
                            <div>111</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="22">
                        <el-form-item label="附件">
                            <div>111</div>
                        </el-form-item>
                    </el-col>
                </el-row>
                <div class="eidtLeft">
                    <el-button type="warning" plain size="medium" @click="showRightFlow">
                        审批记录<span class="arrows">></span>
                    </el-button>
                    <el-button type="warning" plain size="medium" @click="goMoreBills">
                        更多单据<span class="arrows">></span>
                    </el-button>
                </div>
                <div class="eidtRight">
                    <el-button type="warning" size="medium" @click="showRightFlow">
                        策略暂停<span class="arrows">></span>
                    </el-button>
                </div>
            </el-form>
        </section>
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
    import BusinessTracking from "../publicModule/BusinessTracking.vue"
    export default {
        name: "AllManageDetail",
        created: function () {
            this.$emit("transmitTitle", "自动下拨管理-查看明细");
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
        components:{
            BusinessTracking:BusinessTracking
        },
        props:["tableData"],
        data: function () {
            return {
                allocationData: {},
                activeName:"1",
                formLabelWidth:"120px",
                editableTabs: [{ //控制标签页数据
                    title: '01',
                    name: '1'
                }],
                businessParams:{},//业务状态追踪参数
            }
        },
        methods: {
            addTheCollect:function  () {
                
            },
            selectDate:function (){

            },
            goMoreBills:function (){
                this.$router.push("/allocation/allocation-manage");
            },
            showRightFlow:function (row) {
                this.businessParams = {};
                this.businessParams.id = row.bill_id;
                this.businessParams.biz_type = row.biz_type;
                this.businessParams.type = 1;
                document.getElementById("showbox").style.right="0px";
            },
            closeRightFlow:function(){
                this.businessParams = {};
                document.getElementById("showbox").style.right="-500px";
            }
        },
        watch:{
            tableData: function (val, oldVal) {
                
                // this.tableList = val.data;
            }
        }
    }
</script>

