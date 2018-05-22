<style>
    #historyAll {
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

    /*公司-银行切换*/
    .company-bank {
        position: absolute;
        top: -20px;
        right: -48px;
        width: 28px;
        height: 140px;
    }

    .company-bank li {
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

    .company-bank .current-select {
        background-color: #00B3ED;
        color: #fff;
    }

    .company-bank .current-select:before {
        border: 5px solid #00B3ED;
        border-top-color: transparent;
        border-bottom-color: transparent;
        border-left: none;
    }

    .company-bank li:hover {
        background-color: #00B3ED;
        color: #fff;
    }

    .company-bank li:before {
        content: "";
        display: block;
        position: absolute;
        border: none;
        top: 26px;
        left: -5px;
    }

    .company-bank li:hover:before {
        border: 5px solid #00B3ED;
        border-top-color: transparent;
        border-bottom-color: transparent;
        border-left: none;
    }

    #historyAll .el-date-editor{
        position: absolute;
        top: -18px;
        right: -18px;
        width: 210px;
    }
</style>

<template>
    <div id="historyAll">
        <el-date-picker
                v-model="value6"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                size="mini">
        </el-date-picker>
        <!--饼图-->
        <CakePicture :pieData="pieData"></CakePicture>
        <!-- 表格数据-->
        <div :class="['table-setion',{'table-up':!tableSite},{'table-down':tableSite}]">
            <img src="../../assets/icon_arrow_up.jpg" alt="" v-show="tableSite" @click="tableSite=!tableSite"/>
            <img src="../../assets/icon_arrow_down.jpg" alt="" v-show="!tableSite" @click="tableSite=!tableSite"/>
            <el-table :data="tableList"
                      border show-summary
                      :sum-text="''"
                      size="mini"
                      height="86%"
                      max-height="362px">
                <el-table-column prop="name" label="公司名称" v-if="btActive"></el-table-column>
                <el-table-column prop="name" label="银行名称" v-else></el-table-column>
                <el-table-column prop="balance" label="余额"></el-table-column>
            </el-table>
        </div>
        <!-- 分页-->
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
        <!-- 公司/银行 选择-->
        <div class="company-bank">
            <ul>
                <li :class="{'current-select':btActive}"
                    @click="isCompany">公司</li>
                <li :class="{'current-select':!btActive}"
                    @click="isBank">银行</li>
            </ul>
        </div>
    </div>
</template>

<script>
    import CakePicture from "./CakePicture.vue";

    export default {
        name: "HistoryAll",
        created:function(){
            this.$emit('transmitTitle','历史余额汇总');
            this.$emit('getTableData', this.routerMessage);

            //获取饼图数据
            this.getPieData("qhb_org_topchart");
        },
        props: ["tableData"],
        data: function () {
            return {
                //获取数据的信息
                routerMessage: {
                    optype: "qhb_org_list",
                    pageno: 1,
                    pagesize: 8,
                    mode: "",
                    bank: "",
                    org: "",
                    acctType: ""
                },
                //滑动面板的控制
                tableSite: true,
                //表格数据
                tableList: [],
                //分页数据
                pagTotal: 0,
                pagSize: 0,
                //饼图数据
                pieData: [],
                //公司/银行激活状态
                btActive: true,
                value6:""
            }
        },
        components: {
            CakePicture: CakePicture
        },
        methods: {
            //换页
            pageChange: function (page) {
                this.routerMessage.pageno = page;
                this.$emit("getTableData", this.routerMessage);
            },
            //点击公司
            isCompany: function () {
                if(this.btActive){
                    return;
                }else{
                    this.btActive = true;
                    //获取表格数据
                    this.routerMessage.pageno = 1;
                    this.routerMessage.optype = "qhb_org_list";
                    this.$emit("getTableData", this.routerMessage);
                    //获取饼图数据
                    this.getPieData("qhb_org_topchart");
                }
            },
            //点击银行
            isBank: function () {
                if(!this.btActive){
                    return;
                }else{
                    this.btActive = false;
                    this.routerMessage.pageno = 1;
                    this.routerMessage.optype = "qhb_bank_list";
                    //获取表格数据
                    this.$emit("getTableData", this.routerMessage);
                    //获取饼图数据
                    this.getPieData("qhb_bank_topchart");
                }
            },
            //获取饼图数据
            getPieData: function(optype){
                var routeThis = this;
                this.$axios({
                    url: "/cfm/process",
                    method: "post",
                    params: {
                        optype: optype,
                        pageno: 1,
                        pagesize: 100
                    }
                }).then(function (result) {
                    var currentData = result.data.data.list;
                    var pieData = [];
                    for (var i = 0; i < currentData.length; i++) {
                        var item = currentData[i];
                        pieData.push({value: item.balance, name: item.name, code: item.code});
                    }
                    routeThis.pieData = pieData;
                }).catch(function (error) {
                    console.log(error);
                })
            }
        },
        watch: {
            //根据父组件返回的信息进行设置
            tableData: function (val, oldValue) {
                var data = val.data;
                this.tableList = data.list;
                this.pagSize = data.pagesize * 1;
                this.pagTotal = data.total * 1;
            }
        }
    }
</script>
