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
        /*数据展示区*/
        .table-content {
            height: 408px;
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
            background-image: url(../../assets/icon_common.png);
            background-position: -273px -62px;
            border: none;
            padding: 0;
            vertical-align: middle;
        }

        /*分割线*/
        .split-form {
            width: 100%;
            height: 26px;
            padding-bottom: 1px;
            h4 {
                margin: 0;
                float: left;
            }
            .el-button {
                float: right;
            }
        }
    }
</style>
<style lang="less" type="text/less">
    #userMaintain {
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 400px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="userMaintain">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addStaff">添加员工</el-button>
            <!--<el-button type="warning" size="mini" @click="">下载</el-button>-->
        </div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      border height="100%"
                      size="mini">
                <el-table-column prop="login_name" label="登录名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="name" label="姓名" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pos_name" label="职位" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="org_name" label="所属机构" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="dept_name" label="所属部门" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="phone" label="手机号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="email" label="邮箱" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="register_date" label="注册日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="status" label="状态" width="80"
                                 :formatter="transitionStatus"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作"
                        width="110">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="编辑" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editStaff(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="冻结/解冻" placement="bottom" effect="light" :enterable="false"
                                    :open-delay="500">
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
                    background :pager-count="5"
                    layout="sizes , prev, pager, next, jumper"
                    :page-size="pagSize" :total="pagTotal"
                    :page-sizes="[10, 50, 100, 500]"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange">
            </el-pagination>
        </div>
        <!--添加/编辑员工 弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="800px" top="76px"
                   :close-on-click-modal="false">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth"
                     :rules="rules" ref="dialogForm">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="姓名" prop="name">
                            <el-input v-model="dialogData.name" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="邮箱" prop="email">
                            <el-input v-model="dialogData.email" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="登录名称" prop="login_name">
                            <el-input v-model="dialogData.login_name" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="密码" :label-width="formLabelWidth">
                            <el-input type="password"
                                      v-model="dialogData.password"
                                      auto-complete="off"
                                      placeholder="密码默认为123456"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="手机号" prop="phone">
                            <el-input v-model="dialogData.phone" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <div class="split-form">
                            <h4>所属机构部门</h4>
                            <el-button size="mini" style="margin-left:0"
                                       @click="showPosDialog">新增
                            </el-button>
                        </div>
                    </el-col>
                    <el-col :span="24" style="margin-bottom:24px">
                        <el-table :data="udopsList"
                                  border size="mini"
                                  style="width:96%;float:right"
                                  empty-text="请点击新增添加数据"
                                  highlight-current-row
                                  @current-change="udopsCurrentChange">
                            </el-table-column>
                            <el-table-column label="默认" width="60">
                                <template slot-scope="scope">
                                    <el-radio v-model="scope.row.is_default" label="1">{{emptyData}}</el-radio>
                                </template>
                            </el-table-column>
                            <el-table-column prop="org_id" label="所属机构"
                                             :formatter="transitionOrg"
                                             :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column prop="dept_id" label="所属部门"
                                             :formatter="transitionDept"
                                             :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column prop="post_id" label="所属职位"
                                             :formatter="transitionPos"
                                             :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column
                                    label="删除"
                                    width="50">
                                <template slot-scope="scope" class="operationBtn">
                                    <el-tooltip content="删除" placement="bottom" effect="light" :enterable="false"
                                                :open-delay="500">
                                        <el-button type="danger" icon="el-icon-delete" size="mini"
                                                   @click.stop="removePos(scope.row,scope.$index,udopsList)"></el-button>
                                    </el-tooltip>
                                </template>
                            </el-table-column>
                        </el-table>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="subCurrent">确 定</el-button>
            </span>
            <el-dialog :visible.sync="innerVisible"
                       width="38%" title="添加职位"
                       append-to-body
                       :close-on-click-modal="false">
                <el-form :model="posDialogData" size="small"
                         :label-width="formLabelWidth"
                         :rules="posRules" ref="innerDialogForm">
                    <el-row>
                        <el-col :span="24">
                            <el-form-item label="所属机构" prop="org_id">
                                <el-select v-model="posDialogData.org_id" placeholder="请选择机构" clearable>
                                    <el-option v-for="org in orgList"
                                               :key="org.org_id"
                                               :label="org.name"
                                               :value="org.org_id">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="所属部门" prop="dept_id">
                                <el-select v-model="posDialogData.dept_id" placeholder="请选择部门" clearable>
                                    <el-option v-for="dept in deptList"
                                               :key="dept.dept_id"
                                               :label="dept.name"
                                               :value="dept.dept_id">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="所属职位" prop="post_id">
                                <el-select v-model="posDialogData.post_id" placeholder="请选择职位" clearable>
                                    <el-option v-for="position in positionList"
                                               :key="position.pos_id"
                                               :label="position.name"
                                               :value="position.pos_id">
                                    </el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="24">
                            <el-form-item label="是否默认" :label-width="formLabelWidth">
                                <el-switch v-model="posDialogData.is_default"
                                           active-value="1"
                                           inactive-value="0">
                                </el-switch>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
                <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="innerVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="addUdops">确 定</el-button>
            </span>
            </el-dialog>
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
        mounted: function () {
            /*获取下拉框数据*/
            //机构
            var orgList = JSON.parse(window.sessionStorage.getItem("orgList"));
            if (orgList) {
                this.orgList = orgList;
            }
            //部门
            var deptList = JSON.parse(window.sessionStorage.getItem("deptList"));
            if (deptList) {
                this.deptList = deptList;
            }
            //职位
            this.$axios({
                url: "/cfm/adminProcess",
                method: "post",
                data: {
                    optype: "position_list",
                    params: {
                        page_size: 1000,
                        page_num: 1,
                        status: 1
                    }
                }
            }).then((result) => {
                var data = result.data.data;
                if (result.data.error_msg) {
                    this.$message({
                        type: "error",
                        message: result.data.error_msg,
                        duration: 2000
                    })
                } else {
                    this.positionList = data;
                }
            }).catch(function (error) {
                console.log(error);
            })
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
                tableList: [], //表格数据
                dialogVisible: false, //新增/修改弹框
                dialogTitle: "新增",
                dialogData: {},
                udopsList: [],
                formLabelWidth: "120px",
                innerVisible: false, //添加职位弹框
                posDialogData: {
                    is_default: "0"
                },
                currentUser: "", //表格当期数据
                orgList: [], //下拉框数据
                deptList: [],
                positionList: [],
                emptyData: "", //为了展示数据为空
                //校验规则设置
                rules: {
                    name: {
                        required: true,
                        message: "请输入姓名",
                        trigger: "blur",
                        transform: function (value) {
                            if (value) {
                                return value.trim();
                            }
                        }
                    },
                    login_name: {
                        required: true,
                        message: "请输入登录名称",
                        trigger: "blur",
                        transform: function (value) {
                            if (value) {
                                return value.trim();
                            }
                        }
                    },
                    email: {
                        type: "email",
                        message: "请输入正确的邮箱格式",
                        trigger: "blur"
                    },
                    phone: {
                        validator: function (rule, value, callback, source, options) {
                            if(!value){
                                callback();
                            }
                            var reg = /^[1][0-9]{10}$/;
                            if (reg.test(value)) {
                                callback();
                            } else {
                                var errors = [];
                                callback(new Error("请输入正确的手机号"));
                            }
                        },
                        trigger: "blur"
                    }
                },
                posRules: {
                    org_id: {
                        required: true,
                        message: "请选择所属机构",
                        trigger: "change"
                    },
                    dept_id: {
                        required: true,
                        message: "请选择所属部门",
                        trigger: "change"
                    },
                    post_id: {
                        required: true,
                        message: "请选择所属职位",
                        trigger: "change"
                    }
                }
            }
        },
        methods: {
            //添加员工
            addStaff: function () {
                this.dialogTitle = "新增";
                this.dialogData = {};
                if (this.$refs.dialogForm) {
                    this.$refs.dialogForm.clearValidate();
                }
                this.udopsList.splice(0, this.udopsList.length);
                this.dialogVisible = true;
            },
            //编辑员工
            editStaff: function (row) {
                this.dialogTitle = "编辑";
                this.dialogData = {};
                if (this.$refs.dialogForm) {
                    this.$refs.dialogForm.clearValidate();
                }
                this.udopsList.splice(0, this.udopsList.length);
                this.dialogVisible = true;
                this.currentUser = row;

                for (var k in row) {
                    if (k != "udops") {
                        this.dialogData[k] = row[k];
                    } else {
                        row[k].forEach((current) => {
                            var udopsItem = {};
                            for (var k in current) {
                                if (k == "is_default") {
                                    udopsItem[k] = current[k] + "";
                                } else {
                                    udopsItem[k] = current[k];
                                }
                            }
                            this.udopsList.push(udopsItem);
                        })
                    }
                }

            },
            //修改用户状态
            setUserStatus: function (row) {
                this.$axios({
                    url: "/cfm/adminProcess",
                    method: "post",
                    data: {
                        optype: "usr_setstatus",
                        params: {
                            usr_id: row.usr_id,
                            status: row.status
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else {
                        row.status = result.data.data;
                        console.log(row.status);
                        this.$message({
                            type: 'success',
                            message: '修改成功!',
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //删除当前用户
            removeUser: function (row, index, rows) {
                this.$confirm('确认删除当前用户吗?', '提示', {
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
                        if ((this.pagTotal / this.pagSize) > 1) {
                            this.$emit('getTableData', this.routerMessage);
                        } else {
                            rows.splice(index, 1);
                            this.pagTotal--;
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
                this.routerMessage.params.page_num = currPage;
                this.$emit("getTableData", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.params = {
                    page_size: val,
                    page_num: "1"
                };
                this.$emit("getTableData", this.routerMessage);
            },
            //展示格式转换-状态
            transitionStatus:function(row, column, cellValue, index){
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.UserStatus) {
                    return constants.UserStatus[cellValue];
                }
            },
            //展示格式转换-机构
            transitionOrg: function (row, column, cellValue, index) {
                var orgList = this.orgList;
                for (var i = 0; i < orgList.length; i++) {
                    if (cellValue == orgList[i].org_id) {
                        return orgList[i].name;
                    }
                }
            },
            //展示格式转换-部门
            transitionDept: function (row, column, cellValue, index) {
                var deptList = this.deptList;
                for (var i = 0; i < deptList.length; i++) {
                    if (cellValue == deptList[i].dept_id) {
                        return deptList[i].name;
                    }
                }
            },
            //展示格式转换-职位
            transitionPos: function (row, column, cellValue, index) {
                var positionList = this.positionList;
                for (var i = 0; i < positionList.length; i++) {
                    if (cellValue == positionList[i].pos_id) {
                        return positionList[i].name;
                    }
                }
            },
            /*弹框事件*/
            //添加所属机构部门
            addUdops: function () {
                this.$refs.innerDialogForm.validate((valid, object) => {
                    if(valid){
                        var tableItem = {};
                        for (var k in this.posDialogData) {
                            tableItem[k] = this.posDialogData[k];
                        }
                        if (tableItem.is_default == "1") {
                            this.udopsList.forEach((item) => {
                                item.is_default = "0";
                            })
                        }
                        this.udopsList.push(tableItem);
                        this.innerVisible = false;
                    }else{
                        return false;
                    }
                })
            },
            //添加职位弹框-显示
            showPosDialog: function () {
                this.innerVisible = true;
                if (this.$refs.innerDialogForm) {
                    this.$refs.innerDialogForm.clearValidate();
                }
                for (var k in this.posDialogData) {
                    if (k == "is_default") {
                        this.posDialogData[k] = '0';
                    } else {
                        this.posDialogData[k] = '';
                    }
                }
            },
            //删除当前职位
            removePos: function (row, index, rows) {
                this.$confirm('确认删除当前部门吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    rows.splice(index, 1);
                    this.$message({
                        type: "success",
                        message: "删除成功",
                        duration: 2000
                    })
                }).catch(() => {
                });
            },
            //提交当前修改或新增
            subCurrent: function () {
                this.$refs.dialogForm.validate((valid, object) => {
                    if(valid){
                        console.log(this.udopsList);
                        var udopsList = this.udopsList;
                        if(!udopsList.length){
                            this.$message({
                                type: "warning",
                                message: "请选择所属机构",
                                duration: 2000
                            });
                            return;
                        }else{
                            var hasDefault = false;
                            for(var i = 0; i<udopsList.length; i++){
                                if(udopsList[i].is_default == "1"){
                                    hasDefault = true;
                                    break;
                                }
                            }
                            if(!hasDefault){
                                this.$message({
                                    type: "warning",
                                    message: "请选择一条默认所属机构",
                                    duration: 2000
                                });
                                return;
                            }
                        }

                        var params = this.dialogData;
                        params.udops = this.udopsList;
                        var optype = "";
                        if (this.dialogTitle == "新增") {
                            optype = "usr_add";
                        } else {
                            optype = "usr_chg";
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
                                    if (this.tableList.length < this.routerMessage.params.page_size) {
                                        this.tableList.push(data);
                                    }
                                    this.pagTotal++;
                                    var message = "新增成功";
                                } else {
                                    for (var k in data) {
                                        this.currentUser[k] = data[k];
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
                    }else{
                        return false;
                    }
                })
            },
            //职位当前项改变
            udopsCurrentChange: function (val) {
                if (val) {
                    this.udopsList.forEach((item) => {
                        item.is_default = "0";
                    })
                    val.is_default = "1";
                }
            }
        },
        watch: {
            tableData: function (val, oldVal) {
                console.log(val);
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.tableList = val.data;
            }
        }
    }
</script>
