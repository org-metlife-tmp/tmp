<style scoped lang="less" type="text/less">
    #userMaintain {
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

        /*按钮-冻结/解冻*/
        .on-off {
            width: 22px;
            height: 22px;
            background-image: url(../../assets/icon_nav.png);
            background-position: -375px -100px;
            border: none;
            padding: 0;
            vertical-align: middle;
        }
    }
</style>

<template>
    <div id="userMaintain">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addStaff">添加员工</el-button>
            <el-button type="warning" size="mini" @click="">下载</el-button>
        </div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      border
                      size="mini">
                <el-table-column prop="login_name" label="登录名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="name" label="姓名" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pos_name" label="职位" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="org_name" label="所属机构" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="dept_name" label="所属部门" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="phone" label="手机号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="email" label="邮箱" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="register_date" label="注册日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作"
                        width="110">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="编辑" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editStaff(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="冻结/解冻" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button size="mini"
                                       @click="setUserStatus(scope.row)"
                                       class="on-off"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeUser(scope.row,scope.$index,tableList)"></el-button>
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
        <!--添加/编辑员工 弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="800px" top="76px"
                   :close-on-click-modal="false">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="姓名" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.name" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="职位" :label-width="formLabelWidth">
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
                        <el-form-item label="所属部门" :label-width="formLabelWidth">
                            <el-select v-model="dialogData.region" placeholder="请选择活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="手机号" :label-width="formLabelWidth">
                            <el-col :span="10">
                                <el-input v-model="dialogData.name" auto-complete="off"></el-input>
                            </el-col>
                            <el-col :span="2" style="height:1px"></el-col>
                            <el-col :span="5">
                                <el-button size="small" type="info" disabled>获取手机验证码</el-button>
                            </el-col>
                            <el-col :span="7">
                                <el-input v-model="dialogData.name" auto-complete="off"></el-input>
                            </el-col>
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
        name: "UserMaintain",
        created: function () {
            this.$emit("transmitTitle", "用户维护");
            this.$emit("getTableData", this.routerMessage);
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: {
                    optype: "usr_list",
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
                currentUser: ""
            }
        },
        methods: {
            //添加员工
            addStaff:function () {
                this.dialogTitle = "新增";
                this.dialogData = {};
                this.dialogVisible = true;
            },
            //编辑员工
            editStaff:function (row) {
                this.dialogTitle = "编辑";
                this.dialogData = {};
                this.dialogVisible = true;
                this.currentUser = row;
                for(var k in row){
                    this.dialogData[k] = row[k];
                }
            },
            //修改用户状态
            setUserStatus: function (row) {
                this.$axios({
                    url:"/cfm/adminProcess",
                    method: "post",
                    data: {
                        optype: "usr_setstatus",
                        params: {
                            usr_id:row.usr_id,
                            status: row.status
                        }
                    }
                }).then((result) => {
                    if(result.data.error_msg){
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
                        row.status = result.data.data;
                        this.$message({
                            type: 'success',
                            message: '操作成功!',
                            duration: 2000
                        });
                    }
                }).catch(function(error){
                    console.log(error);
                })
            },
            //删除当前用户
            removeUser:function (row, index, rows) {
                this.$confirm('确认删除当前员工吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/adminProcess",
                        method: "post",
                        data: {
                            optype: "usr_del",
                            params: {
                                usr_id: row.usr_id
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
