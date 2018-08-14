<style scoped lang="less" type="text/less">
    #historyFluctuate{
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
                <el-table-column prop="" label="公司名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="totalrecv" label="收入" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="totalpay" label="支出" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="totalnetrecv" label="净收支" :show-overflow-tooltip="true"></el-table-column>
            </el-table>
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
        name: "HistoryFluctuate",
        created: function () {
            //向父组件发送自己的信息
            this.$emit('transmitTitle', '历史交易波动');
            this.$emit('getTableData', this.routerMessage);
        },
        props: ["tableData"],
        data: function(){
            return {
                routerMessage: { //获取自身数据信息
                    optype: "jyt_hiswavelist",
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
                dateValue: "", //时间选择
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                }
            }
        },
        methods: {
            //点击页数 获取当前页数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getTableData", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function(val){
                this.routerMessage.params = {
                    page_size: val,
                    page_num: 1
                };
                this.$emit("getTableData", this.routerMessage);
            },
            //选择时间后设置数据
            getDateData: function(val){
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
            }
        }
    }
</script>



