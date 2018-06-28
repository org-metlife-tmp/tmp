<style scoped lang="less" type="text/less">
    #openAccountMatter {
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
            background-color: #E7E7E7;
            margin-bottom: 20px;
        }

        /*数据展示区*/
        .table-content {
            transition: height 1s;
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

        /*按钮-设置状态*/
        .distribute {
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            background-position: -440px -62px;
            border: none;
            padding: 0;
            vertical-align: middle;
        }
    }
</style>

<template>
    <div id="openAccountMatter">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addAccountMatter" v-show="isPending">新增</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="7" v-if="!isPending">
                        <el-form-item>
                            <el-col :span="11">
                                <el-date-picker
                                        v-model="searchData.start_date"
                                        type="date"
                                        placeholder="起始日期"
                                        value-format="yyyy-MM-dd"
                                        style="width: 100%;">
                                </el-date-picker>
                            </el-col>
                            <el-col class="line" :span="1" style="text-align:center">-</el-col>
                            <el-col :span="11">
                                <el-date-picker
                                        v-model="searchData.end_date"
                                        type="date"
                                        placeholder="结束日期"
                                        value-format="yyyy-MM-dd"
                                        style="width: 100%;">
                                </el-date-picker>
                            </el-col>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" placeholder="请输入事由摘要关键字"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <!--<el-button type="primary" plain @click="" size="mini">清空</el-button>-->
                            <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item style="margin-bottom:0px">
                            <el-checkbox-group v-model="searchData.service_status" v-if="isPending">
                                <el-checkbox label="1" name="type">已保存</el-checkbox>
                                <el-checkbox label="5" name="type">审批拒绝</el-checkbox>
                            </el-checkbox-group>
                            <el-checkbox-group v-model="searchData.service_status" v-else>
                                <el-checkbox label="2" name="type">已提交</el-checkbox>
                                <el-checkbox label="3" name="type">审批中</el-checkbox>
                                <el-checkbox label="4" name="type">审批通过</el-checkbox>
                                <el-checkbox label="11" name="type">已完结</el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section :class="['table-content']">
            <el-table :data="tableList"
                      border
                      size="mini">
                <el-table-column prop="apply_on" label="申请日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="memo" label="事由摘要" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="service_status" label="业务状态"
                                 :formatter="transitionStatus"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="110"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="isPending">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editMerch(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="isPending">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeMatter(scope.row,scope.$index,tableList)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="!isPending">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookParticular(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="分发" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="!isPending">
                            <el-button size="mini" @click="" class="distribute"></el-button>
                        </el-tooltip>
                        <el-tooltip content="办结" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="!isPending">
                            <el-button type="success" icon="el-icon-check" size="mini"
                                       @click="lookParticular(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </section>
        <!--分页部分-->
        <div class="botton-pag">
            <el-pagination
                    background
                    layout="sizes, prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    :page-sizes="[7, 50, 100, 500]"
                    :pager-count="5"
                    :current-page="pagCurrent"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange">
            </el-pagination>
        </div>
        <!--弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="810px" title="新增"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="申请日期">
                            <el-input v-model="dialogData.apply_on" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span=12 style="height:51px"></el-col>
                    <el-col :span="12">
                        <el-form-item label="申请部门">
                            <el-input v-model="dialogData.dept_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属公司">
                            <el-input v-model="dialogData.org_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="事由摘要">
                            <el-input v-model="dialogData.memo" placeholder="请输入事由摘要(15字以内)"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="事由说明">
                            <el-input v-model="dialogData.detail"
                                      type="textarea" :rows="3"
                                      placeholder="请输入事由说明(100字以内)"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="subCurrent">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "OpenAccountMatter",
        created: function () {
            this.$emit("transmitTitle", "开户事项申请");
            this.$emit("getTableData", this.routerMessage);
        },
        props: ["isPending", "tableData"],
        data: function () {
            return {
                routerMessage: {
                    todo: {
                        optype: "openintent_todolist",
                        params: {
                            page_size: 7,
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
                searchData: { //搜索数据
                    service_status: []
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                currentMatter: {},
                dialogVisible: false, //弹框数据
                dialogData: {},
                formLabelWidth: "120px",
                dialogTitle: "新增"
            }
        },
        methods: {
            //展示格式转换-业务状态
            transitionStatus: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.BillStatus) {
                    return constants.BillStatus[cellValue];
                }
            },
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                for (var k in searchData) {
                    if (this.isPending) {
                        this.routerMessage.todo.params[k] = searchData[k];
                    } else {
                        this.routerMessage.done.params[k] = searchData[k];
                    }
                }
                this.$emit("getTableData", this.routerMessage);
            },
            //获取当前用户部门和公司
            getDeptOrg:function(){
                var userUodp = JSON.parse(window.sessionStorage.getItem("user")).uodp;
                for (var i = 0; i < userUodp.length; i++) {
                    var item = userUodp[i];
                    if (item.is_default == "1") {
                        this.dialogData.dept_name = item.dept_name;
                        this.dialogData.org_name = item.org_name;
                        var curData = new Date();
                        this.dialogData.apply_on = curData.getFullYear() + "-" + (curData.getMonth() + 1) + "-" + curData.getDate();
                        break;
                    }
                }
            },
            //添加开户事项
            addAccountMatter: function () {
                this.dialogTitle = "新增";
                this.dialogVisible = true;
                //清空数据
                for (var k in this.dialogData) {
                    this.dialogData[k] = "";
                }
                //设置当前用户的部门和公司
                this.getDeptOrg();
            },
            //编辑当前事项申请
            editMerch:function (row) {
                this.dialogTitle = "编辑";
                this.dialogVisible = true;
                //清空数据和校验信息
                for(var k in this.dialogData){
                    this.dialogData[k] = "";
                }
                this.currentMatter = row; //保存当前数据
                //设置当前用户的部门和公司
                this.getDeptOrg();
                //设置弹框数据
                for(var k in row){
                    this.dialogData[k] = row[k];
                }
            },
            //提交当前修改或新增
            subCurrent: function () {
                var params = this.dialogData;
                var optype = "";
                if (this.dialogTitle == "新增") {
                    optype = "openintent_add";
                } else {
                    optype = "openintent_chg";
                }

                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: optype,
                        params: params
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else {
                        var data = result.data.data;
                        if (this.dialogTitle == "新增") {
                            if (this.tableList.length < this.routerMessage.todo.params.page_size) {
                                this.tableList.push(data);
                            }
                            this.pagTotal++;
                            var message = "新增成功"
                        } else {
                            for (var k in data) {
                                this.currentMatter[k] = data[k];
                            }
                            var message = "修改成功"
                        }
                        this.dialogVisible = false;
                        this.$message({
                            type: 'success',
                            message: message,
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                })

            },
            //删除当前事项申请
            removeMatter:function (row, index, rows) {
                this.$confirm('确认删除当前事项申请吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/normalProcess",
                        method: "post",
                        data: {
                            optype: "openintent_del",
                            params: {
                                id: row.id,
                                persist_version: row.persist_version
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

                        if(this.pagCurrent < (this.pagTotal/this.pagSize)){ //存在下一页
                            this.$emit('getTableData', this.routerMessage);
                        }else{
                            if(rows.length == "1"){ //是当前页最后一条
                                this.routerMessage.params.page_num--;
                                this.$emit('getTableData', this.routerMessage);
                            }else{
                                rows.splice(index, 1);
                                this.pagTotal--;
                            }
                        }

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
            },
            //点击页数 获取当前页数据
            getCurrentPage: function (currPage) {
                if(this.isPending){
                    this.routerMessage.todo.params.page_num = currPage;
                }else{
                    this.routerMessage.done.params.page_num = currPage;
                }
                this.$emit("getTableData", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange:function(val){
                this.routerMessage.todo.params = {
                    page_size: val,
                    page_num: 1
                };
                this.routerMessage.done.params = {
                    page_size: val,
                    page_num: 1
                };
                this.$emit("getTableData", this.routerMessage);
            },
        },
        computed: {
            getCurrentSearch: function () {
                if (this.isPending) {
                    return 5;
                } else {
                    return 8;
                }
            }
        },
        watch: {
            isPending: function (val, oldVal) {
                this.searchData.query_key = "";
                this.searchData.service_status = [];
            },
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data;
            }
        }
    }
</script>
