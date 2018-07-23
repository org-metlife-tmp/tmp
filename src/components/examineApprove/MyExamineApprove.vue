<style scoped lang="less" type="text/less">
    #myExamineApprove{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }

</style>

<template>
    <div id="myExamineApprove">

    </div>
</template>

<script>
    export default {
        name: "MyExamineApprove",
        created: function () {
            this.$emit("transmitTitle", "我的审批平台");
            this.$emit("getTableData", this.routerMessage);
        },
        props: ["isPending", "tableData"],
        data:function(){
            return {
                routerMessage: {
                    todo: {
                        optype: "wfquery_pendingtasksall",
                        params: {
                            page_size: 8,
                            page_num: 1
                        }
                    },
                    done: {
                        optype: "openintent_donelist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    }
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
            }
        },
        watch: {
            isPending: function (val, oldVal) {

            },
            tableData: function (val, oldVal) {
                console.log(val);
                return;
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data;
            }
        }
    }
</script>
