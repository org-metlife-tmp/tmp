<style scoped lang="less" type="text/less">
    #historyAll {
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
        .allData {
            height: 28px;
            line-height: 28px;
            width: 100%;
            background-color: #F8F8F8;
            border: 1px solid #ebeef5;
            border-top: none;
            box-sizing: border-box;
            text-align: right;

            .numText {
                color: #FF5800;
                margin-right: 10px;
            }
        }

        /*时间控件*/
        .el-date-editor {
            position: absolute;
            top: -18px;
            right: -18px;
            width: 210px;
            z-index: 1;
        }

        /*公司-银行切换*/
        .company-bank {
            position: absolute;
            top: -20px;
            right: -48px;
            width: 28px;
            height: 140px;

            li {
                width: 88%;
                line-height: 26px;
                height: 56px;
                font-size: 14px;
                border: 1px solid #00B3ED;
                background-color: #fff;
                color: #00B3ED;
                cursor: pointer;
                margin-bottom: 6px;
                position: relative;
            }

            .current-select {
                background-color: #00B3ED;
                color: #fff;
            }

            .current-select:before {
                border: 5px solid #00B3ED;
                border-top-color: transparent;
                border-bottom-color: transparent;
                border-left: none;
            }

            li:hover {
                background-color: #00B3ED;
                color: #fff;
            }

            li:before {
                content: "";
                display: block;
                position: absolute;
                border: none;
                top: 26px;
                left: -5px;
            }

            li:hover:before {
                border: 5px solid #00B3ED;
                border-top-color: transparent;
                border-bottom-color: transparent;
                border-left: none;
            }
        }
    }
</style>

<template>
    <div id="historyAll">
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
        <Histogram :barData="barData" :showLegend="showLegend"></Histogram>
        <!--表格-->
        <div :class="['table-setion',{'table-up':!tableSite},{'table-down':tableSite}]">
            <img src="../../assets/icon_arrow_up.jpg" alt="" v-show="tableSite" @click="tableSite=!tableSite"/>
            <img src="../../assets/icon_arrow_down.jpg" alt="" v-show="!tableSite" @click="tableSite=!tableSite"/>
            <el-table :data="tableList"
                      border
                      size="mini"
                      height="90%"
                      max-height="397px">
                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"
                                 v-if="btActive.accActive"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"
                                 v-if="btActive.accActive"></el-table-column>
                <el-table-column prop="name" label="公司名称" :show-overflow-tooltip="true"
                                 v-if="btActive.comActive"></el-table-column>
                <el-table-column prop="name" label="银行名称" :show-overflow-tooltip="true"
                                 v-if="btActive.bankActive"></el-table-column>

                <el-table-column prop="totalrecv" label="收入" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="totalpay" label="支出" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="totalnetrecv" label="净收支" :show-overflow-tooltip="true"></el-table-column>
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
        <div class="company-bank">
            <ul>
                <li :class="{'current-select':btActive.accActive}"
                    @click="currActive('3')">账户
                </li>
                <li :class="{'current-select':btActive.comActive}"
                    @click="currActive('1')">公司
                </li>
                <li :class="{'current-select':btActive.bankActive}"
                    @click="currActive('2')">银行
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
    import Histogram from "../echarts/Histogram.vue";

    export default {
        name: "HistoryDealAll",
        created: function () {
            //向父组件发送自己的信息
            this.$emit('transmitTitle', '历史交易汇总');
            this.$emit('getTableData', this.routerMessage);
        },
        props: ["tableData"],
        components: {
            Histogram: Histogram
        },
        data: function () {
            return {
                routerMessage: { //获取自身数据信息
                    optype: "jyt_hiscollectlist",
                    params: {
                        page_num: 1,
                        page_size: 10,
                        type: "3"
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
                btActive: { //账户/公司/银行激活状态
                    accActive: true,
                    comActive: false,
                    bankActive: false,
                },
                barData: [], //柱状图数据
                showLegend: true
            }
        },
        methods: {
            //点击页数 获取当前页数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getTableData", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.params.page_size = val;
                this.routerMessage.params.page_num = 1;
                this.$emit("getTableData", this.routerMessage);
            },
            //公司-账户-银行 切换
            currActive: function (type) {
                if (type == "3") { //账户
                    if (this.btActive.accActive) {
                        return;
                    } else {
                        this.btActive.comActive = false;
                        this.btActive.bankActive = false;
                        this.btActive.accActive = true;
                    }
                } else if (type == "1") { //公司
                    if (this.btActive.comActive) {
                        return;
                    } else {
                        this.btActive.comActive = true;
                        this.btActive.bankActive = false;
                        this.btActive.accActive = false;
                    }
                } else if (type == "2") { //银行
                    if (this.btActive.bankActive) {
                        return;
                    } else {
                        this.btActive.comActive = false;
                        this.btActive.bankActive = true;
                        this.btActive.accActive = false;
                    }
                }
                ;
                //获取表格数据
                this.routerMessage.params.page_num = 1;
                this.routerMessage.params.type = type;
                this.$emit("getTableData", this.routerMessage);
            },
            //获取柱状图数据
            getBarData: function () {
                var currentData = this.tableList;
                var barData = [];
                for (var i = 0; i < currentData.length; i++) {
                    var item = currentData[i];
                    barData.push({
                        name: item.acc_name ? item.acc_name : item.name,
                        totalrecv: parseFloat(item.totalrecv).toFixed(2),
                        totalpay: parseFloat(item.totalpay).toFixed(2),
                        totalnetrecv: parseFloat(item.totalnetrecv).toFixed(2)
                    })
                }
                ;
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


