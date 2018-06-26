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

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }
    }
</style>

<template>
    <div id="openAccountMatter">
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="7" v-if="!isPending">
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
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.name" placeholder="请输入事由摘要关键字"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <!--<el-button type="primary" plain @click="" size="mini">清空</el-button>-->
                            <el-button type="primary" plain @click="" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item style="margin-bottom:0px">
                            <el-checkbox-group v-model="searchData.type" v-if="isPending">
                                <el-checkbox label="已保存" name="type"></el-checkbox>
                                <el-checkbox label="审批拒绝" name="type"></el-checkbox>
                            </el-checkbox-group>
                            <el-checkbox-group v-model="searchData.type" v-else>
                                <el-checkbox label="已提交" name="type"></el-checkbox>
                                <el-checkbox label="审批中" name="type"></el-checkbox>
                                <el-checkbox label="审批通过" name="type"></el-checkbox>
                                <el-checkbox label="已完结" name="type"></el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--分页部分-->
        <div class="botton-pag">
            <el-pagination
                    background
                    layout="sizes, prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    :page-sizes="[10, 50, 100, 500]"
                    :pager-count="5"
                    :current-page="pagCurrent">
            </el-pagination>
        </div>
    </div>
</template>

<script>
    export default {
        name: "OpenAccountMatter",
        created: function () {
            this.$emit("transmitTitle", "开户事项申请");
        },
        props:["isPending"],
        data: function () {
            return {
                searchData:{
                    type:[]
                },
                pagSize: 8,
                pagTotal: 1,
                pagCurrent: 1
            }
        },
        methods: {
        },
        computed:{
            getCurrentSearch:function(){
                if(this.isPending){
                    return 5;
                }else{
                    return 8;
                }
            }
        },
        watch:{
            isPending:function(val,oldVal){
                this.searchData.type=[];
            }
        }
    }
</script>
