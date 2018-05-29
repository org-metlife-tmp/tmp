<style scoped lang="less" type="text/less">
    #settleAccount {
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
            text-align: left;
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
    }
</style>

<template>
    <div id="settleAccount">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addData">新增</el-button>
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
                        <el-form-item label="银行大类">
                            <el-select v-model="serachData.region" placeholder="活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="账户">
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
                <el-table-column prop="acc_no" label="账户编号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="org_name" label="所属机构" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_name" label="开户行" :show-overflow-tooltip="true" width="200"></el-table-column>
                <el-table-column prop="pay_recv_attr" label="收付属性"
                                 :formatter="setPayRecv"
                                 width="100"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="curr_name" label="币种" :show-overflow-tooltip="true" width="120"></el-table-column>
                <el-table-column
                        label="操作"
                        width="70">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="编辑" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editData(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click=""></el-button>
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
        <!--新增/修改 弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="860px" top="76px"
                   :close-on-click-modal="false">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="账户编号" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.name" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.name" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="16">
                        <el-form-item label="开户行" :label-width="formLabelWidth">
                            <el-col :span="16">
                                <el-input v-model="dialogData.name" auto-complete="off"></el-input>
                            </el-col>
                            <el-col :span="1" style="height:1px"></el-col>
                            <el-col :span="7">
                                <el-input v-model="dialogData.name" auto-complete="off"></el-input>
                            </el-col>
                        </el-form-item>
                    </el-col>
                    <el-col :span="16">
                        <el-form-item label="" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.name" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属机构" :label-width="formLabelWidth">
                            <el-select v-model="dialogData.region" placeholder="请选择活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种" :label-width="formLabelWidth">
                            <el-select v-model="dialogData.region" placeholder="请选择活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="收付属性" :label-width="formLabelWidth">
                            <el-select v-model="dialogData.region" placeholder="请选择活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户日期" :label-width="formLabelWidth">
                            <el-date-picker type="date" placeholder="选择日期" v-model="dialogData.date1" style="width: 100%;"></el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="机构段" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.name" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="明细段" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.name" auto-complete="off"></el-input>
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
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false" size="mini">取 消</el-button>
                <el-button type="primary" @click="subCurrent" size="mini">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "SettleAccount",
        created:function(){
            this.$emit("transmitTitle","结算账户设置");
            this.$emit("getTableData", this.routerMessage);
            //获取下拉框数据
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: { //本页数据获取参数
                    optype: "settacc_list",
                    params: {
                        page_size: 10,
                        page_num: 1
                    }
                },
                pagSize: 1, //分页数据
                pagTotal: 1,
                serachData: {
                }, //搜索区数据
                tableList: [], //表格数据
                dialogVisible: false, //弹框相关数据
                dialogTitle: "新增",
                dialogData: {},
                formLabelWidth: "120px",
                currentSettle:"" //表格当前数据
            }
        },
        methods: {
            //表格展示数据格式转换
            setPayRecv:function(row, column, cellValue, index){
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.AccPayOrRecvAttr) {
                    return constants.AccPayOrRecvAttr[cellValue];
                }
            },
            //新增数据
            addData: function(){
                this.dialogTitle = "新增";
                this.dialogVisible = true;
                this.dialogData = {};
            },
            //编辑数据
            editData:function(row){
                this.dialogTitle = "编辑";
                this.dialogVisible = true;
                this.currentSettle = row;
                this.dialogData = {};
                for(var k in row){
                    this.dialogData[k] = row[k];
                }
            },
            //提交当前修改或新增
            subCurrent: function(){
                //新增数据
                if(this.dialogTitle == "新增"){

                }else{ //修改数据

                }
                this.dialogVisible = false;
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
