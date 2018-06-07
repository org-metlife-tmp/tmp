<style scoped lang="less" type="text/less">
    #userGroup {
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
        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }
    }
</style>
<style lang="less" type="text/less">
    #userGroup {
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 400px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="userGroup">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addUserGroup">新增</el-button>
            <el-button type="warning" size="mini" @click="">下载</el-button>
        </div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      border
                      size="mini">
                <el-table-column prop="name" label="用户组名" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="memo" label="用户组描述" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作"
                        width="70">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="编辑" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editUserGroup(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeGroup(scope.row,scope.$index,tableList)"></el-button>
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
                    @current-change="getCurrentPage"
                    :pager-count="5">
            </el-pagination>
        </div>
        <!--添加/编辑 弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="800px" top="76px"
                   :close-on-click-modal="false">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="用户组名" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.name" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="用户组描述" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.memo" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">功能权限分配</el-col>
                    <el-col :span="24">
                        <el-tree :data="jurisdTreeList"
                                 node-key="code"
                                 highlight-current
                                 accordion show-checkbox
                                 :expand-on-click-node="false"
                                 ref="jurisdTree">
                                        <span class="custom-tree-node" slot-scope="{ node, data }">
                                            <span>{{ node.data.name }}</span>
                                        </span>
                        </el-tree>
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
        name: "UserGroup",
        created: function () {
            this.$emit("transmitTitle", "用户组设置");
            this.$emit("getTableData", this.routerMessage);

            //获取下拉框数据
            this.$axios({
                url:"/cfm/adminProcess",
                method: "post",
                data: {
                    optype: "usrgroup_busmenu"
                }
            }).then((result) => {
                var data = result.data.data;
                if(result.data.error_msg){
                }else{
                    data.forEach((item) => {
                        item.children = item.menus;
                        this.jurisdTreeList.push(item);
                    })
                    this.jurisdTreeList = JSON.parse(JSON.stringify(this.jurisdTreeList));
                }
            }).catch(function(error){
                console.log(error);
            })
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: {
                    optype: "usrgroup_list",
                    params: {
                        page_size: 10,
                        page_num: 1
                    }
                },
                pagSize: 1, //分页数据
                pagTotal: 1,
                tableList:[],
                dialogVisible: false,
                dialogTitle: "新增",
                dialogData: {},
                formLabelWidth: "120px",
                currentGroup: "",
                jurisdTreeList: []
            }
        },
        methods: {
            //新增用户组
            addUserGroup:function () {
                this.dialogTitle = "新增";
                this.dialogData = {};
                this.dialogVisible = true;
            },
            //编辑当前用户组
            editUserGroup: function (row) {
                this.dialogTitle = "编辑";
                this.dialogData = {};
                this.dialogVisible = true;

                this.currentGroup = row;
                for(var k in row){
                    if(k == "menus"){
                        var currentRow = row[k];
                        setTimeout(() => {
                            console.log(currentRow);
                            this.$refs.jurisdTree.setCheckedKeys(currentRow);
                        }, 100)
                    }else{
                        this.dialogData[k] = row[k];
                    }
                }
            },
            //删除当前用户组
            removeGroup:function (row, index, rows) {
                this.$confirm('确认删除当前用户组吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/adminProcess",
                        method: "post",
                        data: {
                            optype: "usrgroup_del",
                            params: {
                                group_id: row.group_id
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
            },
            //提交数据
            subCurrent: function () {
                var params = this.dialogData;
                params.menus = this.$refs.jurisdTree.getCheckedKeys();
                var optype = "";
                if (this.dialogTitle == "新增") {
                    optype = "usrgroup_add";
                } else {
                    optype = "usrgroup_chg";
                }

                this.$axios({
                    url: "/cfm/adminProcess",
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
                            if(this.tableList.length < this.routerMessage.params.page_size){
                                this.tableList.push(data);
                            }
                            this.pagTotal++;
                            var message = "添加成功";
                        } else {
                            console.log(data);
                            return;
                            for (var k in data) {
                                this.currentRouter[k] = data[k];
                            }
                            var message = "修改成功";
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
            //点击页数 获取当前页数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getTableData", this.routerMessage);
            },
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
