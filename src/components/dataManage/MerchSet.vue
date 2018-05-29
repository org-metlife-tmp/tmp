<style scoped lang="less" type="text/less">
    #merchSet {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*顶部按钮*/
        .button-list-right {
            position: absolute;
            top: -60px;
            right: -18px;
        }
        /*搜索区*/
        .search-setion {
            text-align: left;
        }
        /*分隔栏*/
        .split-bar {
            width: 106%;
            height: 6px;
            margin-left: -20px;
            margin-bottom: 20px;
            background-color: #E7E7E7;
        }
        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }
    }
</style>

<template>
    <div id="merchSet">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addMerch">新增</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="serachData" size="mini">
                <el-row>
                    <el-col :span="7">
                        <el-form-item label="机构">
                            <el-select v-model="serachData.region" placeholder="活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="支付渠道">
                            <el-select v-model="serachData.region" placeholder="活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="商户号">
                            <el-input v-model="serachData.user" placeholder="审批人"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="3">
                        <el-form-item>
                            <el-button type="primary" plain @click="">搜索</el-button>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      border
                      size="mini">
                <el-table-column prop="acc_no" label="商户号" width="180px"></el-table-column>
                <el-table-column prop="acc_name" label="商户名称"></el-table-column>
                <el-table-column prop="channel_name" label="支付渠道"></el-table-column>
                <el-table-column prop="org_name" label="所属机构"></el-table-column>
                <el-table-column prop="pay_recv_attr" :formatter="setAccPay" label="账户属性"></el-table-column>
                <el-table-column prop="curr_name" label="币种"></el-table-column>
                <el-table-column
                        label="操作"
                        width="70">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="编辑" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editMerch(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeMerch(scope.row,scope.$index,tableList)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </section>
        <!--分页部分-->
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
        <!--弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="810px"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="diaLogData" :label-width="formLabelWidth" size="small">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="商户号">
                            <el-input v-model="diaLogData.name"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="商户名称">
                            <el-input v-model="diaLogData.name"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="支付渠道">
                            <el-select v-model="diaLogData.city" placeholder="请选择市">
                                <el-option label="朝阳区" value="朝阳区"></el-option>
                                <el-option label="海淀区" value="海淀区"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属机构">
                            <el-select v-model="diaLogData.city" placeholder="请选择市">
                                <el-option label="朝阳区" value="朝阳区"></el-option>
                                <el-option label="海淀区" value="海淀区"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <el-select v-model="diaLogData.city" placeholder="请选择市">
                                <el-option label="朝阳区" value="朝阳区"></el-option>
                                <el-option label="海淀区" value="海淀区"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="收付属性">
                            <el-select v-model="diaLogData.city" placeholder="请选择市">
                                <el-option label="朝阳区" value="朝阳区"></el-option>
                                <el-option label="海淀区" value="海淀区"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户日期">
                            <el-date-picker
                                    v-model="diaLogData.data"
                                    type="date"
                                    placeholder="选择日期"
                                    style="width:100%">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="状态">
                            <el-select v-model="diaLogData.city" placeholder="请选择市">
                                <el-option label="朝阳区" value="朝阳区"></el-option>
                                <el-option label="海淀区" value="海淀区"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="明细段">
                            <el-select v-model="diaLogData.city" placeholder="请选择市">
                                <el-option label="朝阳区" value="朝阳区"></el-option>
                                <el-option label="海淀区" value="海淀区"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="机构段">
                            <el-input v-model="diaLogData.name"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="结算账号">
                            <el-input v-model="diaLogData.name"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注">
                            <el-input v-model="diaLogData.name" width="100%"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false" size="small">取 消</el-button>
                <el-button type="primary" @click="" size="small">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "MerchSet",
        created: function () {
            this.$emit("transmitTitle", "商户号设置");
            this.$emit("getTableData", this.routerMessage);
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: {
                    optype: "merchacc_list",
                    params: {
                        page_size: 10,
                        page_num: 1
                    }
                },
                pagSize: 1, //分页数据
                pagTotal: 1,
                serachData: {}, //搜索域数据
                tableList: [], //表格数据
                dialogVisible: false, //弹框数据
                dialogTitle: "新增",
                diaLogData: {},
                formLabelWidth: '120px', //弹框表单的标签宽度
                currentRouter: ""
            }
        },
        methods: {
            //展示格式转换-账户属性
            setAccPay: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.AccPayOrRecvAttr) {
                    return constants.AccPayOrRecvAttr[cellValue];
                }
            },
            //添加商户号
            addMerch: function () {
                this.dialogTitle = "新增";
                this.dialogVisible = true;
                this.diaLogData = {};
            },
            //编辑商户号
            editMerch:function (row) {
                this.dialogTitle = "编辑";
                this.dialogVisible = true;
                this.dialogData = {};
                this.currentRouter = row;
                for(var k in row){
                    this.dialogData[k] = row[k];
                }
            },
            //删除当前商户号
            removeMerch:function (row, index, rows) {
                this.$confirm('确认删除当前商户号吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/adminProcess",
                        method: "post",
                        data: {
                            optype: "merchacc_del",
                            params: {
                                id: row.id
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {
                            this.$message({
                                type: "error",
                                message: result.data.error_msg,
                                duration: 2000
                            })
                            return;
                        }
                        rows.splice(index, 1);
                        this.pagTotal--;
                        this.$message({
                            type: "success",
                            message: "删除成功",
                            duration: 2000
                        })
                    }).catch(function (error) {
                        console.log(error);
                    })
                }).catch(() => {
                });
            }
        },
        watch: {
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.tableList = val.data;
            }
        }
    }
</script>
