<style scoped lang="less" type="text/less">
    #openAccountMatter{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion{
            text-align: left;
        }

        /*分隔栏*/
        .split-bar {
            width: 106%;
            height: 6px;
            margin-left: -20px;
            background-color: #E7E7E7;
            margin-bottom: 20px;
        }
    }
</style>

<template>
    <div id="openAccountMatter">
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="7">
                        <el-form-item>
                            <el-col :span="11">
                                <el-date-picker type="date" placeholder="起始日期" v-model="searchData.date1" style="width: 100%;"></el-date-picker>
                            </el-col>
                            <el-col class="line" :span="1" style="text-align:center">-</el-col>
                            <el-col :span="11">
                                <el-date-picker type="date" placeholder="结束日期" v-model="searchData.date2" style="width: 100%;"></el-date-picker>
                            </el-col>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8" style="text-align:center">
                        <el-form-item>
                            <el-checkbox-group v-model="searchData.type" v-if="isPending">
                                <el-checkbox-button label="已保存" name="type"></el-checkbox-button>
                                <el-checkbox-button label="审批拒绝" name="type"></el-checkbox-button>
                            </el-checkbox-group>
                            <el-checkbox-group v-model="searchData.type" v-else>
                                <el-checkbox-button label="已提交" name="type"></el-checkbox-button>
                                <el-checkbox-button label="审批中" name="type"></el-checkbox-button>
                                <el-checkbox-button label="审批通过" name="type"></el-checkbox-button>
                                <el-checkbox-button label="已完结" name="type"></el-checkbox-button>
                            </el-checkbox-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="事由摘要">
                            <el-input v-model="searchData.name" placeholder="请输入关键字"></el-input>
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
        <!--分隔栏-->
        <div class="split-bar"></div>
    </div>
</template>

<script>
    export default {
        name: "OpenAccountMatter",
        created: function () {
            this.$emit("transmitTitle", "开户事项申请");
        },
        data: function () {
            return {
                searchData:{
                    type:[]
                },
                isPending: false
            }
        },
        methods: {}
    }
</script>
