<style scoped lang="less" type="text/less">
    #todayDetail {
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
        /*汇总数据*/
        .allData{
            height: 28px;
            line-height: 28px;
            width: 100%;
            background-color: #F8F8F8;
            border: 1px solid #ebeef5;
            border-top: none;
            box-sizing: border-box;
            text-align: right;

            .numText{
                color: #FF5800;
                margin-right: 10px;
            }
        }
    }
</style>

<template>
    <div id="todayDetail">
        <!--饼图-->
        <CakePicture :pieData="pieData"></CakePicture>
        <!--表格-->
        <div :class="['table-setion',{'table-up':!tableSite},{'table-down':tableSite}]">
            <img src="../../assets/icon_arrow_up.jpg" alt="" v-show="tableSite" @click="tableSite=!tableSite"/>
            <img src="../../assets/icon_arrow_down.jpg" alt="" v-show="!tableSite" @click="tableSite=!tableSite"/>
            <el-table :data="tableList"
                      border
                      size="mini"
                      height="81%"
                      max-height="362px">
                <el-table-column prop="org_name" label="公司名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_attr_name" label="账户性质" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_type_name" label="银行大类" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_name" label="开户行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bal" label="当前余额" :show-overflow-tooltip="true" width="100px"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="import_time" label="同步时间" :show-overflow-tooltip="true"></el-table-column>
            </el-table>
            <div class="allData">
                <span>合计：</span>
                <span v-text="this.$common.transitSeparator(recvAll)" class="numText"></span>
            </div>
        </div>
        <!--分页-->
        <div class="botton-pag">
            <el-pagination
                    background
                    layout="sizes, prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    :page-sizes="[10, 50, 100, 500]"
                    :pager-count="5"
                    :current-page="pagCurrent"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange">
            </el-pagination>
        </div>
    </div>
</template>

<script>
    import CakePicture from "../echarts/CakePicture.vue";
    // import func from './vue-temp/vue-editor-bridge';

    export default {
        name: "TodayDetail",
        created: function () {

            this.$emit('transmitTitle', '当日余额明细');
            this.$emit('getTableData', this.routerMessage);
            this.$emit('exportOptype','yet_currtransexport');

            //获取饼图数据
            this.getPieData();
        },
        props: ["tableData"],
        components: {
            CakePicture: CakePicture
        },
        data: function () {
            return {
                //获取自身数据信息
                routerMessage: {
                    optype: "yet_curdetaillist",
                    params:{
                        page_num: 1,
                        page_size: 10,
                        org_ids: "",
                        cnaps_codes: "",
                        acc_attrs: "",
                        interactive_modes: ""
                    }
                },
                //滑动面板控制
                tableSite: true,
                //表格数据
                tableList: [],
                //分页数据
                pagSize: 8,
                pagTotal: 1,
                pagCurrent: 1,
                //饼图数据
                pieData: [],
                recvAll:""
            }
        },
        methods: {
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
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
            //获取饼图数据
            getPieData:function(){
                var currentData = this.tableList;
                var pieData = [];
                for (var i = 0; i < currentData.length; i++) {
                    var item = currentData[i];
                    pieData.push({value: item.bal, name: item.acc_name, code: item.acc_no});
                }
                this.pieData = pieData;
            }
        },
        watch: {
            //设置数据
            tableData: function (val, oldValue) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data;
                //设置汇总数据
                this.recvAll = val.ext ? val.ext.bal : "";
                //获取饼图数据
                this.getPieData();
            }
        }
    }
</script>
