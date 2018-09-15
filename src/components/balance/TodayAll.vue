<style scoped lang="less" type="text/less">
    #todayAll {
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
    <div id="todayAll">
        <!--饼图-->
        <CakePicture :pieData="pieData"></CakePicture>
        <!-- 表格数据-->
        <div :class="['table-setion',{'table-up':!tableSite},{'table-down':tableSite}]">
            <img src="../../assets/icon_arrow_up.jpg" alt="" v-show="tableSite" @click="tableSite=!tableSite"/>
            <img src="../../assets/icon_arrow_down.jpg" alt="" v-show="!tableSite" @click="tableSite=!tableSite"/>
            <el-table :data="tableList"
                      border
                      size="mini"
                      height="81%"
                      max-height="362px">
                <el-table-column prop="name" label="公司名称" v-if="btActive" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="name" label="银行名称" v-else :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bal" label="余额" :show-overflow-tooltip="true"></el-table-column>
            </el-table>
            <div class="allData">
                <span>合计：</span>
                <span v-text="recvAll" class="numText"></span>
            </div>
        </div>
        <!-- 分页-->
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
        <!-- 公司/银行 选择-->
        <div class="company-bank">
            <ul>
                <li :class="{'current-select':btActive}"
                    @click="isCompany">公司
                </li>
                <li :class="{'current-select':!btActive}"
                    @click="isBank">银行
                </li>
            </ul>
        </div>
    </div>
</template>

<script>
    import CakePicture from "../echarts/CakePicture.vue";

    export default {
        name: "TodayAll",
        created: function () {
            //向父组件发送自己的信息
            this.$emit('transmitTitle', '当日余额汇总');
            this.$emit('getTableData', this.routerMessage);
            this.$emit('exportOptype','yet_curcollectlistorgexport');

            //获取饼图数据
            this.getPieData();
        },
        props: ["tableData"],
        data: function () {
            return {
                //获取数据的信息
                routerMessage: {
                    optype: "yet_curcollectlist",
                    params:{
                        page_num: 1,
                        page_size: 10,
                        org_ids: "",
                        cnaps_codes: "",
                        acc_attrs: "",
                        interactive_modes: "",
                        type:1
                    }
                },
                //滑动面板的控制
                tableSite: true,
                //表格数据
                tableList: [],
                //分页数据
                pagSize: 10,
                pagTotal: 1,
                pagCurrent: 1,
                //饼图数据
                pieData: [],
                //公司/银行激活状态
                btActive: true,
                recvAll:""
            }
        },
        components: {
            CakePicture: CakePicture
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
            //点击公司
            isCompany: function () {
                if (this.btActive) {
                    return;
                } else {
                    this.btActive = true;
                    //获取表格数据
                    this.routerMessage.params.page_num = 1;
                    this.routerMessage.params.type = 1;
                    this.$emit("getTableData", this.routerMessage);
                    this.$emit('exportOptype','yet_curcollectlistorgexport');
                    //获取饼图数据
                    this.getPieData();
                }
            },
            //点击银行
            isBank: function () {
                if (!this.btActive) {
                    return;
                } else {
                    this.btActive = false;
                    this.routerMessage.params.page_num = 1;
                    this.routerMessage.params.type = 2;
                    //获取表格数据
                    this.$emit("getTableData", this.routerMessage);
                    this.$emit('exportOptype','yet_curcollectlistbankexport');
                    //获取饼图数据
                    this.getPieData();
                }
            },
            //获取饼图数据
            getPieData: function (optype) {
                var currentData = this.tableList;
                var pieData = [];
                for (var i = 0; i < currentData.length; i++) {
                    var item = currentData[i];
                    pieData.push({value: item.bal, name: item.name});
                }
                this.pieData = pieData;
            }
        },
        watch: {
            //根据父组件返回的信息进行设置
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
