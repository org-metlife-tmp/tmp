<style scoped lang="less" type="text/less">
    #settleAccount {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        //顶部按钮
        .button-list-right {
            position: absolute;
            top: -60px;
            right: -18px;
        }
        /*搜索区*/
        .search-setion {
            text-align: left;
        }
        /*分隔栏*/
        .split-bar {
            width: 106%;
            height: 6px;
            margin-left: -20px;
            background-color: #E7E7E7;
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
    <div id="settleAccount">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini">新增</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="serachData" size="mini">
                <el-row>
                    <el-col :span="7">
                        <el-form-item label="机构">
                            <el-select v-model="serachData.region" placeholder="活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="银行大类">
                            <el-select v-model="serachData.region" placeholder="活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="账户">
                            <el-input v-model="serachData.user" placeholder="审批人"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="3">
                        <el-form-item>
                            <el-button type="primary" plain @click="">搜索</el-button>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section class="table-content"></section>
        <!--分页部分-->
        <div class="botton-pag">
            <el-pagination
                    background
                    layout="prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    @current-change=""
                    :pager-count="5">
            </el-pagination>
        </div>
    </div>
</template>

<script>
    export default {
        name: "SettleAccount",
        created:function(){
            this.$emit("transmitTitle","结算账户设置");
        },
        data: function () {
            return {
                pagSize: 1,
                pagTotal: 1,
                serachData: {

                }
            }
        },
        methods: {}
    }
</script>
