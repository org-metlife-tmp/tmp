<style scoped lang="less" type="text/less">
    #settleAccount {

        /*数据展示区*/
        .table-content {
            height: 325px;
        }
    }
</style>

<template>
    <el-container id="settleAccount">
        <el-header>

        </el-header>
        <el-main>
            <el-form :model="ruleForm" status-icon  ref="ruleForm" label-width="100px">
                <el-form-item label="请输入报文" prop="request">
                    <el-input v-model.number="ruleForm.request"/>
                </el-form-item>
            </el-form>
            <el-button type="warning" size="mini" @click="bankPay">确 定</el-button>
        </el-main>
        <el-footer>

        </el-footer>
    </el-container>
</template>

<script>

    export default {
        data() {
            return {
                ruleForm: {
                    request: ''
                },
                queryUrl: this.$store.state.queryUrl,
            };
        },
        methods: {
            //提交
            bankPay: function () {

                this.$axios({
                    url: this.queryUrl + "adminProcess",
                    method: "post",
                    data: {
                        optype: "payBank_send",
                        params: {
                            request: this.ruleForm.request,
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        });
                        return;
                    } else {
                        this.$message({
                            type: "success",
                            message: "提交成功",
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
        }
    }
</script>
