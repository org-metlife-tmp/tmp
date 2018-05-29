<style scoped lang="less" type="text/less">
    #routerSet {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        //顶部按钮
        .button-list-right {
            position: absolute;
            top: -60px;
            right: -18px;
        }
        /*搜索区*/
        .search-setion {
            text-align: right;
        }
        @media (max-width: 1340px) {
            .search-setion {
                text-align: left;
            }
        }
        /*分隔栏*/
        .split-bar {
            width: 106%;
            height: 6px;
            margin-left: -20px;
            background-color: #E7E7E7;
            margin-bottom: 20px;
        }
        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }
        .split-form{
            width: 100%;
            height: 20px;
            border-top: 1px solid #eee;
        }
    }
</style>

<template>
    <div id="routerSet">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addRouter">新增</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="serachData" size="mini" :label-position="'left'">
                <el-row>
                    <el-col :span="7">
                        <el-form-item label="来源系统">
                            <el-select v-model="serachData.region" placeholder="活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="机构">
                            <el-select v-model="serachData.region" placeholder="活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="支付方式">
                            <el-select v-model="serachData.region" placeholder="活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="7">
                        <el-form-item label="支付子项">
                            <el-select v-model="serachData.region" placeholder="活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="业务类型">
                            <el-select v-model="serachData.region" placeholder="活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="险种大类">
                            <el-select v-model="serachData.region" placeholder="活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
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
                <el-table-column prop="source_code" label="来源系统" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_recv_mode" label="支付方式"
                                 :show-overflow-tooltip="true"
                                 :formatter="getPay"></el-table-column>
                <el-table-column prop="pay_item" label="支付子项" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="memo" label="备注" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="is_activate" label="状态"
                                 :formatter="getStatus"
                                 width="100"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作"
                        width="80">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="编辑" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editRouter(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeRouter(scope.row,scope.$index,tableList)"></el-button>
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
                    @current-change="getPageData"
                    :pager-count="5">
            </el-pagination>
        </div>
        <!--新增/修改 弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="860px" top="76px"
                   :close-on-click-modal="false">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="来源系统" :label-width="formLabelWidth">
                            <el-select v-model="dialogData.region" placeholder="请选择活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="支付方式" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.name" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="支付子项" :label-width="formLabelWidth">
                            <el-select v-model="dialogData.region" placeholder="请选择活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="状态" :label-width="formLabelWidth">
                            <el-select v-model="dialogData.region" placeholder="请选择活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.name" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <div class="split-form"></div>
                    </el-col>


                    <el-col :span="12">
                        <el-form-item label="支付渠道" :label-width="formLabelWidth">
                            <el-select v-model="dialogData.region" placeholder="请选择活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item :label-width="formLabelWidth">
                            <div slot="label" style="line-height:16px">支付渠道<br>原子接口</div>
                            <el-select v-model="dialogData.region" placeholder="请选择活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item :label-width="formLabelWidth">
                            <div slot="label" style="line-height:16px">结算账户<br>/商户号</div>
                            <el-select v-model="dialogData.region" placeholder="请选择活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="优先级" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.name" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false" size="mini">取 消</el-button>
                <el-button type="primary" @click="" size="mini">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "RouterSet",
        created: function () {
            this.$emit("transmitTitle", "路由设置");
            this.$emit("getTableData", this.routerMessage);
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: { //本页数据获取参数
                    optype: "handleroute_list",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                pagSize: 1,
                pagTotal: 1,
                serachData: {

                },
                tableList:[],
                dialogVisible:false,
                dialogTitle: "新增",
                dialogData: {},
                formLabelWidth: "120px",
                currentRouter: ""
            }
        },
        methods: {
            //展示格式转换-支付方式
            getPay: function(row, column, cellValue, index){
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.PayOrRecvMode) {
                    return constants.PayOrRecvMode[cellValue];
                }
            },
            //展示格式转换-状态
            getStatus: function(row, column, cellValue, index){
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if(cellValue == "0"){
                    return "（无效）";
                }else{
                    return "（有效）";
                }
            },
            //换页后获取数据
            getPageData:function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getTableData", this.routerMessage);
            },
            //新增路由
            addRouter:function () {
                this.dialogTitle = "新增";
                this.dialogData = {};
                this.dialogVisible = true;
            },
            //编辑当前路由
            editRouter:function (row) {
                this.dialogTitle = "编辑";
                this.dialogVisible = true;
                this.currentRouter = row;
                this.dialogData = {};
                for(var k in row){
                    this.dialogData[k] = row[k];
                }
            },
            //删除当前路由
            removeRouter:function (row, index, rows) {
                this.$confirm('确认删除当前公司吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/adminProcess",
                        method: "post",
                        data: {
                            optype: "handleroute_del",
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
