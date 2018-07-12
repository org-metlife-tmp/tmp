<style lang="less" scoped type="text/less">
    #workflowConfigure {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion {
            text-align: left;
        }
    }
</style>

<template>
    <div id="workflowConfigure">
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="5">
                        <el-form-item>
                            <el-input v-model="searchData.settle_or_merchant_acc_name" clearable></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
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
                            <el-input v-model="searchData.query_ey"
                                      clearable
                                      placeholder="请输入事单据号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <!--<el-button type="primary" plain @click="" size="mini">清空</el-button>-->
                            <el-button type="primary" plain @click="" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
    </div>
</template>

<script>
    export default {
        name: "WorkflowConfigure",
        created: function () {
            this.$emit("transmitTitle", "业务跟踪管理");
        },
        data: function () {
            return {
                routerMessage: { //本页数据获取参数
                    optype: "handleroute_list",
                    params: {
                        page_size: 8,
                        page_num: 1
                    }
                },
                searchData: {}
            }
        }
    }
</script>

