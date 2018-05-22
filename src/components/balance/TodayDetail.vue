<style>
    #todayDetail {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }

    /*分页部分*/
    .botton-pag {
        position: absolute;
        width: 100%;
        height: 8%;
        bottom: -6px;
    }
</style>

<template>
    <div id="todayDetail">
        <!--饼图-->
        <CakePicture :pieData="pieData" ></CakePicture>
        <!--表格-->
        <div :class="['table-setion',{'table-up':!tableSite},{'table-down':tableSite}]">
            <img src="../../assets/icon_arrow_up.jpg" alt="" v-show="tableSite" @click="tableSite=!tableSite"/>
            <img src="../../assets/icon_arrow_down.jpg" alt="" v-show="!tableSite" @click="tableSite=!tableSite"/>
            <el-table :data="tableList"
                      border show-summary
                      :sum-text="''"
                      size="mini"
                      height="86%"
                      max-height="362px">
                <el-table-column prop="code" label="账户号"></el-table-column>
                <el-table-column prop="name" label="账户名称"></el-table-column>
                <el-table-column prop="type" label="账户属性"></el-table-column>
                <el-table-column prop="balance" label="当前余额"></el-table-column>
                <el-table-column prop="time" label="同步时间"></el-table-column>
            </el-table>
        </div>
        <!--分页-->
        <div class="botton-pag">
            <el-pagination
                    background
                    layout="prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    @current-change="pageChange"
                    :pager-count="5">
            </el-pagination>
        </div>
    </div>
</template>

<script>
    import CakePicture from "./CakePicture.vue";

    export default {
        name: "TodayDetail",
        created: function () {
            this.$emit('transmitTitle', '当日余额明细');
            this.$emit('getTableData', this.routerMessage);

            //获取饼图数据
            var routeThis = this;
            this.$axios({
                url: "/cfm/process",
                method: "post",
                params: {
                    optype: "qcb_detail_topchart",
                    pageno: 1,
                    pagesize: 100
                }
            }).then(function (result) {
                var currentData = result.data.data.list;
                var pieData = [];
                for (var i = 0; i < currentData.length; i++) {
                    var item = currentData[i];
                    pieData.push({value: item.balance, name: item.name,code:item.code});
                }
                routeThis.pieData = pieData
            }).catch(function (error) {
                console.log(error);
            })
        },
        props: ["tableData"],
        components: {
            CakePicture: CakePicture
        },
        data: function () {
            return {
                //获取自身数据信息
                routerMessage: {
                    optype: "qcb_detail_list",
                    pageno: 1,
                    pagesize: 8
                },
                //滑动面板控制
                tableSite: true,
                //表格数据
                tableList: [],
                //分页数据
                pagTotal: 0,
                pagSize: 0,
                //饼图数据
                pieData: []
            }
        },
        methods: {
            //换页
            pageChange: function (page) {
                this.routerMessage.pageno = page;
                this.$emit("getTableData", this.routerMessage);
            }
        },
        watch: {
            //设置数据
            tableData: function (val, oldValue) {
                var data = val.data;
                this.tableList = data.list;
                this.pagSize = data.pagesize * 1;
                this.pagTotal = data.total * 1;
            }
        }
    }
</script>
