<style scoped lang="less" type="text/less">
    #bankAccount {
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
        //顶部按钮
        .button-list-right {
            position: absolute;
            top: -60px;
            right: -18px;
        }
    }
</style>

<template>
    <div id="bankAccount">
        <div class="button-list-right">
            <el-button type="warning" size="mini">新增</el-button>
            <el-button type="warning" size="mini">下载</el-button>
        </div>
        <el-table :data="tableList"
                  border
                  size="mini"
                  max-height="90%">
            <el-table-column prop="name" label="账户号"></el-table-column>
            <el-table-column prop="city" label="账户名称"></el-table-column>
            <el-table-column prop="province" label="账户属性"></el-table-column>
            <el-table-column prop="date" label="开户日期"></el-table-column>
            <el-table-column
                    label="操作"
                    width="100">
                <template slot-scope="scope" class="operationBtn">
                    <el-button type="success" icon="el-icon-plus" size="mini"></el-button>
                    <el-button type="primary" icon="el-icon-edit" size="mini"></el-button>
                    <el-button type="danger" icon="el-icon-delete" size="mini"></el-button>
                </template>
            </el-table-column>
        </el-table>
        <div class="botton-pag">
            <el-pagination
                    background
                    layout="prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    @current-change=""
                    :pager-count="5">
            </el-pagination>
        </div>
    </div>
</template>

<script>
    export default {
        name: "BankAccount",
        created: function () {
            this.$emit('transmitTitle', '银行账户管理');
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: {
                    optype: "org_list"
                },
                tableList: [
                    {
                        name: "啦啦",
                        city: "哈哈",
                        province: "啊啊",
                        date: "哎哎"
                    }
                ],
                //分页数据
                pagTotal: 10,
                pagSize: 8
            }
        },
        methods: {},
        watch: {
            //根据父组件返回的信息进行设置
            tableData: function (val, oldValue) {
                var data = val.data;
                this.tableList = data;
            }
        }
    }
</script>
