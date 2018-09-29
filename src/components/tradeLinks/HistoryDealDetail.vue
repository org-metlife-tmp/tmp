<style scoped lang="less" type="text/less">
    #historyDetail{
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

        /*时间控件*/
        .el-date-editor {
            position: absolute;
            top: -18px;
            right: -18px;
            width: 210px;
            z-index: 1;
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
    <div id="historyDetail">
        <el-date-picker
                v-model="dateValue"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="yyyy-MM-dd"
                size="mini" clearable
                unlink-panels
                :picker-options="pickerOptions"
                @change="getDateData">
        </el-date-picker>
        <!--柱状图-->
        <Histogram :barData="barData"></Histogram>
        <!--表格-->
        <div :class="['table-setion',{'table-up':!tableSite},{'table-down':tableSite}]">
            <img src="../../assets/icon_arrow_up.jpg" alt="" v-show="tableSite" @click="tableSite=!tableSite"/>
            <img src="../../assets/icon_arrow_down.jpg" alt="" v-show="!tableSite" @click="tableSite=!tableSite"/>
            <el-table :data="tableList"
                      border
                      size="mini"
                      height="90%"
                      max-height="397px">
                <el-table-column prop="org_name" label="公司名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_attr_name" label="账户性质" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_type_name" label="银行大类" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_name" label="开户行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="direction" label="收付方向" :show-overflow-tooltip="true">
                </el-table-column>
                <el-table-column prop="opp_acc_no" label="对方账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="opp_acc_name" label="对方账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="amount" label="交易金额" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="summary" label="摘要" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="trans_date" label="交易日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="trans_time" label="交易时间" :show-overflow-tooltip="true"></el-table-column>
            </el-table>
            <div class="allData">
                <span>收入合计：</span>
                <span v-text="recvAll" class="numText"></span>
                <span>支出合计：</span>
                <span v-text="payAll" class="numText"></span>
                <span>净收支合计：</span>
                <span v-text="netrecvAll" class="numText"></span>
            </div>
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
    import Histogram from "../echarts/Histogram.vue";

    export default {
        name: "HistoryDealDetail",
        created: function () {
            let curDate = new Date();
	    let yesterDay = new Date();
            let oldDate = new Date();
	    yesterDay.setFullYear(curDate.getFullYear());
	    yesterDay.setMonth(curDate.getMonth());
	    yesterDay.setDate(curDate.getDate()-1);
            oldDate.setFullYear(curDate.getFullYear());
            oldDate.setMonth(curDate.getMonth());
            oldDate.setDate(curDate.getDate()-8);

            this.dateValue = [oldDate,yesterDay];

            this.routerMessage.params.start_date = this.dateValue[0];
            this.routerMessage.params.end_date = this.dateValue[1];

            //向父组件发送自己的信息
            this.$emit('transmitTitle', '历史交易明细');
            this.$emit('getTableData', this.routerMessage);
            this.$emit('exportOptype','jyt_histransexport');
        },
        props: ["tableData"],
        components: {
            Histogram: Histogram
        },
        data: function(){
            return {
                routerMessage: { //获取自身数据信息
                    optype: "jyt_hisdetaillist",
                    params:{
                        page_num: 1,
                        page_size: 10
                    }
                },
                tableSite: true, //滑动面板控制
                tableList: [], //表格数据
                recvAll: "", //汇总数据
                payAll: "",
                netrecvAll: "",
                pagSize: 10, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                dateValue: "", //时间选择
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
                barData: [], //柱状图数据
            }
        },
        methods: {
            //展示格式转换-收付方向
            // transitionDirection: function (row, column, cellValue, index) {
            //     var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            //     if (constants.PayOrRecv) {
            //         return constants.PayOrRecv[cellValue];
            //     }
            // },
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
            //获取柱状图数据
            getBarData: function(){
                var currentData = this.tableList;
                var barData = [];
                for(var i = 0; i < currentData.length; i++){
                    var item = currentData[i];
                    var barColor = item.direction == "收"? "#A1D331" : "#33B2F2";
                    var currentValue = item.direction == "收"? item.amount * 1 : item.amount * -1;

                    barData.push({
                        direction : item.direction,
                        value : parseFloat(currentValue).toFixed(2),
                        itemStyle : {
                            normal : {
                                color : barColor
                            }
                        }
                    })
                };
                this.barData = barData;
            },
            //选择时间后设置数据
            getDateData: function (val) {
                this.routerMessage.params.start_date = val[0];
                this.routerMessage.params.end_date = val[1];
                this.$emit('getTableData', this.routerMessage);
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
                this.recvAll = val.ext ? val.ext.totalrecv : "";
                this.payAll = val.ext ? val.ext.totalpay : "";
                this.netrecvAll = val.ext ? val.ext.totalnetrecv : "";

                //获取柱状图数据
                this.getBarData();
            }
        }
    }
</script>

