<style scoped lang="less" type="text/less">
    #todayFluctuate {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

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
    <div id="todayFluctuate">
        <div class="cake-picture"></div>
        <div :class="['table-setion',{'table-up':!tableSite},{'table-down':tableSite}]">
            <img src="../../assets/icon_arrow_up.jpg" alt="" v-show="tableSite" @click="tableSite=!tableSite"/>
            <img src="../../assets/icon_arrow_down.jpg" alt="" v-show="!tableSite" @click="tableSite=!tableSite"/>
            <el-table :data="tableList"
                      border show-summary
                      :sum-text="''"
                      size="mini"
                      height="86%"
                      max-height="362px">
                <el-table-column prop="acc_no" label="账户号"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称"></el-table-column>
                <el-table-column prop="acc_attr_name" label="账户属性"></el-table-column>
                <el-table-column prop="bal" label="当前余额"></el-table-column>
                <el-table-column prop="import_time" label="同步时间"></el-table-column>
            </el-table>
        </div>
        <!--分页-->
        <div class="botton-pag">
            <el-pagination
                    background
                    layout="prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    :pager-count="5"
                    :current-page="pagCurrent"
                    @current-change="getCurrentPage">
            </el-pagination>
        </div>
    </div>
</template>

<script>
    export default {
        name: "TodayFluctuate",
        created: function () {
            this.$emit('transmitTitle', '当日余额波动');
            this.$emit('getTableData', this.routerMessage);
        },
        props: ["tableData"],
        data: function () {
            return {
                tableSite: true,
                routerMessage: {
                    optype: "yet_curdetaillist",
                    params:{
                        page_num: 1,
                        page_size: 8,
                        org_ids: "",
                        cnaps_codes: "",
                        acc_attrs: "",
                        interactive_modes: ""
                    }
                },
                tableList: [],
                //分页数据
                pagSize: 8,
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
        },
        watch: {
            tableData: function (val, oldValue) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data; 
            }
        }
    }
</script>
