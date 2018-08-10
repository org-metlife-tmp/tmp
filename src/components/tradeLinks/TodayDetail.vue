<style scoped lang="less" type="text/less">
    #todayDetail{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -12px;
        }

        /*汇总数据*/
        .allData{
            height: 28px;
            width: 100%;
            background-color: #cccccc;
        }
    }
</style>

<template>
    <div id="todayDetail">
        <!--表格-->
        <div :class="['table-setion',{'table-up':!tableSite},{'table-down':tableSite}]">
            <img src="../../assets/icon_arrow_up.jpg" alt="" v-show="tableSite" @click="tableSite=!tableSite"/>
            <img src="../../assets/icon_arrow_down.jpg" alt="" v-show="!tableSite" @click="tableSite=!tableSite"/>
            <el-table :data="tableList"
                      border
                      size="mini"
                      height="90%"
                      max-height="397px">
                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_attr_name" label="账户属性" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="direction" label="收付方向" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_no" label="对方账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_name" label="对方账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="amount" label="交易金额" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="summary" label="摘要" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="trans_date" label="交易时间" :show-overflow-tooltip="true"></el-table-column>
            </el-table>
            <div class="allData"></div>
        </div>
        <!--分页部分-->
        <div class="botton-pag">
            <el-pagination
                    background
                    layout="sizes, prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    :page-sizes="[10, 50, 100, 500]"
                    :pager-count="5"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange"
                    :current-page="pagCurrent">
            </el-pagination>
        </div>
    </div>
</template>

<script>
    export default {
        name: "TodayDetail",
        created: function () {
            this.$emit('transmitTitle', '当日交易明细');
            this.$emit('getTableData', this.routerMessage);
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: { //获取自身数据信息
                    optype: "jyt_curdetaillist",
                    params:{
                        page_num: 1,
                        page_size: 10
                    }
                },
                tableSite: true, //滑动面板控制
                tableList: [], //表格数据
                pagSize: 10, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
            }
        },
        methods: {
            //点击页数 获取当前页数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getTableData", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange:function(val){
                this.routerMessage.params = {
                    page_size: val,
                    page_num: 1
                };
                this.$emit("getTableData", this.routerMessage);
            },
        },
        watch: {
            //设置数据
            tableData: function (val, oldValue) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data;
            }
        }
    }
</script>
