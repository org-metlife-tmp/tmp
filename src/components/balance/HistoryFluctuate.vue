<style scoped lang="less" type="text/less">
    #historyFluctuate {
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
        /* nodata展示样式*/
        .nodatapage{
            position: relative;
            height: 100%;
            .nodata-img{
                background-image: url(../../assets/no_value.png);
                background-repeat: no-repeat;
                width: 202px;
                height: 126px;
                position: absolute;
                left: 0; right: 0;
                top: 30%;
                margin: auto;
            }
            .nodatalabel {
                position: absolute;
                top: 60%;
                width: 100%;
            }
        }
    }
</style>

<template>
    <div id="historyFluctuate">
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
        <!--折线图-->
        <LineChart :lineData="lineData"></LineChart>
        <!-- 表格数据 -->
        <div :class="['table-setion',{'table-up':!tableSite},{'table-down':tableSite}]">
            <img src="../../assets/icon_arrow_up.jpg" alt="" v-show="tableSite" @click="tableSite=!tableSite"/>
            <img src="../../assets/icon_arrow_down.jpg" alt="" v-show="!tableSite" @click="tableSite=!tableSite"/>
            <el-table :data="tableList"
                      border
                      size="mini"
                      height="81%"
                      highlight-current-row
                      @row-click="getCurLineData"
                      max-height="362px">
                <el-table-column prop="org_name" label="公司名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_attr_name" label="账户性质" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_type_name" label="银行大类" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_name" label="开户行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bal" label="日均余额" :show-overflow-tooltip="true"></el-table-column>
            </el-table>
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
    import LineChart from "../echarts/LineChart.vue";
    export default {
        name: "HistoryFluctuate",
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

            this.$emit('transmitTitle', '历史余额波动');
            this.$emit('getTableData', this.routerMessage);
            this.$emit('exportOptype','yet_hiswavelistexport');
        },
        components:{
            LineChart:LineChart
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                tableSite: true,
                routerMessage: {
                    optype: "yet_hiswavelist",
                    params:{
                        page_num: 1,
                        page_size: 10,
                        org_ids: "",
                        cnaps_codes: "",
                        acc_attrs: "",
                        interactive_modes: ""
                    }
                },
                tableList: [],
                //折线图数据
                lineData: [],
                //分页数据
                pagSize: 10,
                pagTotal: 1,
                pagCurrent: 1,
                dateValue: "",
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
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
                this.routerMessage.params.page_size = val;
                this.routerMessage.params.page_num = 1;
                this.$emit("getTableData", this.routerMessage);
            },
            getCurLineData: function (row, event, column) {
                let acc_id;
                if(this.tableList.length <= 0){
                    this.lineData = [];
                    return false;
                }
                if( row === 'all'){
                    acc_id = this.tableList[0].acc_id;
                }else{
                    acc_id = row.acc_id;
                }
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "yet_hiswavetopchart",
                        params: {
                            acc_id:acc_id,
                            start_date:this.dateValue[0],
                            end_date:this.dateValue[1]
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else {
                        let data = result.data.data;
                        let obj ={
                            time:[],
                            y:[]
                        }
                        data.forEach(element => {
                            let time = element.bal_date.split(" ")[0];
                            obj.time.push(time);
                            obj.y.push(parseFloat(element.bal).toFixed(2));
                        });
                        //写两个子组件监听事件不管用
                        // this.lineData.xData = arrXList;
                        // this.lineData.yData = arrYList;
                        this.lineData = obj;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //选择时间后设置数据
            getDateData: function (val) {
                this.routerMessage.params.start_date = val[0];
                this.routerMessage.params.end_date = val[1];
                this.$emit('getTableData', this.routerMessage);
            }
        },
        watch: {
            tableData: function (val, oldValue) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data;
                this.getCurLineData('all');
            }
        }
    }
</script>
