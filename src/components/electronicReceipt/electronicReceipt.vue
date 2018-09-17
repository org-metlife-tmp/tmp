<style lang="less" type="text/less">
    #electronicReceipt{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*分隔栏*/
        .split-bar {
            width: 106%;
            height: 6px;
            margin-left: -20px;
            background-color: #E7E7E7;
            margin-bottom: 20px;
        }
        /*列表部分*/
        .table-list{
            border: 1px solid #ebeef5;
            border-top: 0;
            height: 321px;
            overflow-y: auto;
            .title{
                background: #E9F2F9;
                height: 35px;
                line-height: 35px;
                border-bottom: 1px solid #ebeef5;
            }
            .list-body{
                height: 35px;
                line-height: 35px;
                text-align: left;
                .item{
                    padding: 0 10px;
                    border-bottom: 1px solid #ebeef5;
                    span{
                        width: 100%;
                        display: inline-block;
                    }
                }
            }
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -10px;
        }
        // 弹框表单
        .addDialog{
            margin-bottom: 100px;
        }
    }
</style>

<template>
    <div id="electronicReceipt">
        <!--搜索区-->
        <div class="search-setion">
            <el-form :model="userData" size="small" :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="5">
                        <el-form-item label="被授权人：">
                            <span v-text="userData.be_authorize_usr_name"></span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item label="被授权时间：">
                            <span v-text="userData.start_date"></span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="6">
                        <el-form-item label="授权结束时间：">
                            <span v-text="userData.end_date"></span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2" :offset="2">
                        <el-form-item>
                            <el-button type="primary" plain @click="editUser" size="mini">编辑</el-button>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section :class="['table-content']">
            <div class="table-list">
                <div class="title">授权记录</div>
                <div class="list-body">
                    <div class="item" v-for="item in tableList" :key="item.temprownumber">
                        <span v-text="item.memo"></span>
                    </div>
                </div>
            </div>
        </section>
        <!--分页部分-->
        <div class="botton-pag">
            <el-pagination
                    background
                    layout="sizes, prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    :page-sizes="[8, 50, 100, 500]"
                    :pager-count="5"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange"
                    :current-page="pagCurrent">
            </el-pagination>
        </div>
        <!--待处理编辑弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="800px" title="新增被授权人"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">新增被授权人</h1>
            <el-form :model="dialogData" size="small"
                     class="addDialog"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="被授权人">
                            <el-select v-model="dialogData.be_authorize"
                                        filterable remote clearable
                                        placeholder="请输入关键字"
                                        :remote-method="getUserListByKey"
                                        :loading="loading"
                                        value-key="usr_id">
                                <el-option
                                        v-for="user in be_authorize_list"
                                        :key="user.name"
                                        :label="user.name"
                                        :value="user">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="授权开始时间">
                            <el-date-picker
                                v-model="dialogData.start_date"
                                type="date"
                                placeholder="结束日期"
                                value-format="yyyy-MM-dd"
                                style="width: 100%;">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="授权结束时间">
                            <el-date-picker
                                v-model="dialogData.end_date"
                                type="date"
                                placeholder="结束日期"
                                value-format="yyyy-MM-dd"
                                style="width: 100%;">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" plain size="mini" @click="dialogVisible=false">取 消</el-button>
                <el-button type="warning" size="mini" @click="subConfirm">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "ElectronicReceipt",
        created: function(){
            this.$emit("transmitTitle", "电子回单");
            this.$emit("getCommTable", this.routerMessage);
        },
        mounted:function(){
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: {
                    optype: "wftrans_list",
                    params: {
                        page_size: 8,
                        page_num: 1
                    }
                },
                tableList: [],//列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                dialogVisible: false,
                dialogData: {},
            }
        },
        methods: {
            //换页后获取数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getCommTable", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.params = {
                    page_size: val,
                    page_num: 1
                };
                this.$emit("getCommTable", this.routerMessage);
            },
            //显示编辑弹出框
            editUser: function () {
                this.getBeUsrList();
                this.dialogVisible = true;
            }
        },
        watch: {
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data;
                this.userData = val.ext;
            }
        }
    }
</script>

