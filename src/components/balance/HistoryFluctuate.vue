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
        }
    }
</style>

<template>
    <div id="historyFluctuate">
        <el-date-picker
                v-model="value6"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                size="mini">
        </el-date-picker>
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
                <el-table-column prop="code" label="账户号"></el-table-column>
                <el-table-column prop="name" label="账户名称"></el-table-column>
                <el-table-column prop="bankName" label="公司" width="300px"></el-table-column>
                <el-table-column prop="type" label="账户属性"></el-table-column>
                <el-table-column prop="balance" label="当前余额"></el-table-column>
            </el-table>
        </div>
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
    export default {
        name: "HistoryFluctuate",
        created: function () {
            this.$emit('transmitTitle', '历史余额波动');
            this.$emit('getTableData', this.routerMessage);
        },
        props: ["tableData"],
        data: function () {
            return {
                tableSite: true,
                routerMessage: {
                    optype: "qhb_acct_average",
                    pageno: 1,
                    pagesize: 8
                },
                tableList: [],
                pagTotal: 0,
                pagSize: 0,
                value6: ""
            }
        },
        methods: {
            pageChange: function (page) {
                this.routerMessage.pageno = page;
                this.$emit("getTableData", this.routerMessage);
            }
        },
        watch: {
            tableData: function (val, oldValue) {
                var data = val.data;
                this.tableList = data.list;
                this.pagSize = data.pagesize * 1;
                this.pagTotal = data.total * 1;
            }
        }
    }
</script>
