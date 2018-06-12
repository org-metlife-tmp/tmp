<style scoped lang="less" type="text/less">
    #userMenu {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*用户-用户组切换*/
        .company-bank {
            position: absolute;
            top: -20px;
            right: -48px;
            width: 28px;
            height: 140px;

            li {
                width: 88%;
                line-height: 18px;
                height: 74px;
                font-size: 14px;
                border: 1px solid #00B3ED;
                background-color: #fff;
                color: #00B3ED;
                cursor: pointer;
                margin-bottom: 6px;
                position: relative;
            }
            li:nth-child(2) {
                height: 92px;
            }

            .current-select {
                background-color: #00B3ED;
                color: #fff;
            }

            .current-select:before {
                border: 5px solid #00B3ED;
                border-top-color: transparent;
                border-bottom-color: transparent;
                border-left: none;
            }

            li:hover {
                background-color: #00B3ED;
                color: #fff;
            }

            li:before {
                content: "";
                display: block;
                position: absolute;
                border: none;
                top: 26px;
                left: -5px;
            }

            li:hover:before {
                border: 5px solid #00B3ED;
                border-top-color: transparent;
                border-bottom-color: transparent;
                border-left: none;
            }
        }

        /*数据展示区*/
        .table-content{
            height: 397px;
        }
        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

        /*分割线*/
        .split-form {
            width: 100%;
            height: 26px;
            padding-bottom: 1px;
            h4 {
                margin: 0;
            }
        }
    }
</style>
<style lang="less" type="text/less">
    #userMenu {
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 400px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="userMenu">
        <!-- 用户/用户组 选择-->
        <div class="company-bank">
            <ul>
                <li :class="{'current-select':btActive}"
                    @click="isUser">用户权限
                </li>
                <li :class="{'current-select':!btActive}"
                    @click="isGroup">用户组权限
                </li>
            </ul>
        </div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      border height="100%"
                      size="mini">
                <!--用户信息-->
                <el-table-column prop="name" label="姓名" :show-overflow-tooltip="true" v-if="btActive"></el-table-column>
                <el-table-column prop="org_name" label="机构名称" :show-overflow-tooltip="true"
                                 v-if="btActive"></el-table-column>
                <el-table-column prop="dept_name" label="部门名称" :show-overflow-tooltip="true"
                                 v-if="btActive"></el-table-column>
                <el-table-column prop="pos_name" label="职位" :show-overflow-tooltip="true"
                                 v-if="btActive"></el-table-column>
                <el-table-column prop="is_default" label="是否默认"
                                 :show-overflow-tooltip="true"
                                 :formatter="transitDefault"
                                 width="100" v-if="btActive">
                </el-table-column>
                <!--用户组信息-->
                <el-table-column prop="is_builtin" label="是否内置" :show-overflow-tooltip="true"
                                 v-if="!btActive"></el-table-column>
                <el-table-column prop="memo" label="用户组描述" :show-overflow-tooltip="true"
                                 v-if="!btActive"></el-table-column>
                <el-table-column prop="name" label="用户组名称" :show-overflow-tooltip="true"
                                 v-if="!btActive"></el-table-column>
                <el-table-column
                        label="操作"
                        width="50">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="编辑" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editUser(scope.row)"></el-button>
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
                    @size-change="sizeChange"
                    :current-page="pagCurrent">
            </el-pagination>
        </div>
        <!--用户弹出框-->
        <el-dialog title="编辑"
                   :visible.sync="dialogVisible"
                   width="800px" top="76px"
                   :close-on-click-modal="false">
            <el-form :model="dialogData" size="small">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="姓名" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.name" auto-complete="off" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="机构名称" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.org_name" auto-complete="off" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="部门名称" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.dept_name" auto-complete="off" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="职位" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.pos_name" auto-complete="off" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="是否默认" :label-width="formLabelWidth">
                            <el-switch v-model="dialogData.is_default" disabled
                                       active-value="1"
                                       inactive-value="0"></el-switch>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <div class="split-form">
                            <h4>用户组分配</h4>
                        </div>
                    </el-col>
                    <el-col :span="24" style="margin-bottom:24px">
                        <el-table :data="usrgroupList"
                                  border size="mini"
                                  style="width:96%;float:right"
                                  empty-text="无可分配数据"
                                  ref="usergroupTable"
                                  @selection-change="usergroupChange">
                            <el-table-column type="selection" width="46"></el-table-column>
                            <el-table-column prop="name" label="用户组名称"
                                             :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column prop="memo" label="用户组说明"
                                             :show-overflow-tooltip="true"></el-table-column>
                        </el-table>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="subCurrent">确 定</el-button>
            </span>
        </el-dialog>
        <!--用户组弹出框-->
        <el-dialog title="编辑"
                   :visible.sync="groupDialog"
                   width="800px" top="76px"
                   :close-on-click-modal="false">
            <el-form :model="groupDiaData" size="small">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="用户组名称" :label-width="formLabelWidth">
                            <el-input v-model="groupDiaData.name" auto-complete="off" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="用户组描述" :label-width="formLabelWidth">
                            <el-input v-model="groupDiaData.memo" auto-complete="off" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="是否内置" :label-width="formLabelWidth">
                            <el-switch v-model="groupDiaData.is_default" disabled
                                       active-value="1"
                                       inactive-value="0"></el-switch>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <div class="split-form">
                            <h4>用户管理</h4>
                            <el-button size="mini" style="float:right"
                                       @click="innerVisible = true">添加用户
                            </el-button>
                        </div>
                    </el-col>
                    <el-col :span="24" style="margin-bottom:24px">
                        <el-table :data="userList"
                                  border size="mini"
                                  style="width:96%;float:right"
                                  empty-text="请添加用户">
                            <el-table-column prop="name" label="姓名" :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column prop="org_name" label="机构名称" :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column prop="dept_name" label="部门名称" :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column prop="pos_name" label="职位" :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column
                                    label="删除"
                                    width="50">
                                <template slot-scope="scope" class="operationBtn">
                                    <el-button type="danger" icon="el-icon-delete" size="mini"
                                               @click="delSeleUser(scope.row,scope.$index,userList)"></el-button>
                                </template>
                            </el-table-column>
                        </el-table>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="subGroup">确 定</el-button>
            </span>
            <el-dialog :visible.sync="innerVisible"
                       width="50%" title="添加用户"
                       append-to-body top="76px"
                       :close-on-click-modal="false">
                <el-table :data="selectUserList"
                          size="mini">
                    <!--用户信息-->
                    <el-table-column prop="name" label="姓名" width="100" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="org_name" label="机构名称" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="dept_name" label="部门名称" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column prop="pos_name" label="职位" :show-overflow-tooltip="true"></el-table-column>
                    <el-table-column
                            label=""
                            width="60">
                        <template slot-scope="scope" class="operationBtn">
                            <el-button type="text" style="padding-top:6px;padding-bottom:6px"
                                       @click="addSelectUser(scope.row)">添加</el-button>
                        </template>
                    </el-table-column>
                </el-table>

                <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-pagination
                            background
                            layout="prev, pager, next, jumper"
                            :page-size="8"
                            :total="selectTotal"
                            @current-change="getPageUser"
                            :pager-count="5">
                    </el-pagination>
                </span>
            </el-dialog>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "UserMenu",
        created: function () {
            this.$emit("transmitTitle", "用户菜单设置");
            this.$emit("getTableData", this.routerMessage);
        },
        mounted:function(){
            /*获取下拉框数据*/
            //用户列表
            if (!window.sessionStorage.getItem("userList")) {
                this.$axios({
                    url:"/cfm/adminProcess",
                    method: "post",
                    data: {
                        optype: "usrmenu_list",
                        params: {
                            page_num: 1,
                            page_size: 10000
                        }
                    }
                }).then((result) => {
                    var data = result.data.data;
                    if(result.data.error_msg){

                    }else{
                        window.sessionStorage.setItem("userList", JSON.stringify(data));
                    }
                }).catch(function(error){
                    console.log(error);
                })
            }
            //用户组
            var usrgroupList = JSON.parse(window.sessionStorage.getItem("usrgroupList"));
            if (usrgroupList) {
                this.usrgroupList = usrgroupList;
            }
        },
        destroyed: function () {
            window.sessionStorage.removeItem("userList");
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: {
                    optype: "usrmenu_list",
                    params: {
                        page_size: 10,
                        page_num: 1
                    }
                },
                pagSize: 1, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                tableList: [], //表格数据
                btActive: true, //右侧按钮状态控制
                dialogVisible: false, //用户弹框数据
                dialogData: {},
                formLabelWidth: "120px",
                userAllocation: [],
                usrgroupList: [],
                selectIds: [],
                groupDialog: false, //用户组弹框数据
                groupDiaData: {},
                userList: [],
                innerVisible: false,
                selectUserList: [],
                selectTotal: 8,
                currentUser: {}, //当前用户
            }
        },
        methods: {
            //点击页数 获取当前页数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getTableData", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange:function(val){
                this.routerMessage.params = {
                    page_size: val,
                    page_num: "1"
                };
                this.$emit("getTableData", this.routerMessage);
            },
            //右侧按钮点击
            isUser: function () {
                if (this.btActive) {
                    return;
                } else {
                    this.btActive = true;
                    //获取表格数据
                    this.routerMessage.params.page_num = 1;
                    this.routerMessage.optype = "usrmenu_list";
                    this.$emit("getTableData", this.routerMessage);
                }
            },
            isGroup: function () {
                if (!this.btActive) {
                    return;
                } else {
                    this.btActive = false;
                    //获取表格数据
                    this.routerMessage.params.page_num = 1;
                    this.routerMessage.optype = "usrgroup_list2";
                    this.$emit("getTableData", this.routerMessage);
                    //设置添加用户列表
                    var userAllList = JSON.parse(window.sessionStorage.getItem("userList"));
                    if(userAllList.length){
                        this.selectTotal = userAllList.length;
                        if(userAllList.length >= 8){
                            for(var i = 0; i<8; i++){
                                this.selectUserList.push(userAllList[i]);
                            }
                        }else{
                            userAllList.forEach((item) => {
                                this.selectUserList.push(item);
                            })
                        }
                    }
                }
            },
            //编辑单签用户
            editUser: function (row) {
                this.currentUser = row; //保存当前项
                //编辑用户
                if (row.usr_id) {
                    this.dialogVisible = true;
                    this.dialogData = {};
                    setTimeout(() => {
                        this.$refs.usergroupTable.clearSelection();
                        if (row.group_ids.length > 0) {
                            row.group_ids.forEach(item => {
                                var usrgroupList = this.usrgroupList;
                                for (var i = 0; i < usrgroupList.length; i++) {
                                    if (item == usrgroupList[i].group_id) {
                                        this.$refs.usergroupTable.toggleRowSelection(usrgroupList[i]);
                                        break;
                                    }
                                }
                            })
                        }
                    }, 100)

                    row.is_default += "";
                    this.dialogData = row;
                } else { //编辑用户组
                    this.groupDialog = true;
                    this.groupDiaData = {};
                    this.userList = [];
                    row.is_builtin += "";
                    this.groupDiaData = row;
                    if(row.uodp_ids.length){
                        var uodpIdList = row.uodp_ids;
                        var userAllList = JSON.parse(window.sessionStorage.getItem("userList"));
                        for(var j = 0; j<uodpIdList.length; j++){
                            for(var k = 0; k<userAllList.length; k++){
                                if(uodpIdList[j] == userAllList[k].uodp_id){
                                    this.userList.push(userAllList[k]);
                                    break;
                                }
                            }
                        }
                    }
                }
            },
            //展示格式转换-是否默认
            transitDefault: function (row, column, cellValue, index) {
                if (cellValue) {
                    return "是";
                } else {
                    return "否";
                }
            },
            //提交当前修改
            subCurrent: function () {
                this.$axios({
                    url: "/cfm/adminProcess",
                    method: "post",
                    data: {
                        optype: "usrmenu_allot",
                        params: {
                            uodp_id: this.dialogData.uodp_id,
                            group_ids: this.selectIds
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
                        this.currentUser.group_ids = data.group_ids;
                        this.dialogVisible = false;
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
            //用户设置用户组改变后
            usergroupChange: function (val) {
                var groupIds = [];
                val.forEach((item) => {
                    groupIds.push(item.group_id);
                })
                this.selectIds = groupIds;
            },
            //添加用户列表换页
            getPageUser:function(currPage){
                var userAllList = JSON.parse(window.sessionStorage.getItem("userList"));
                var actionOne = (currPage-1)*8;
                var endOne = actionOne + 8;
                this.selectUserList = [];
                for(var i=actionOne; i<=endOne; i++){
                    if(userAllList[i]){
                        this.selectUserList.push(userAllList[i]);
                    }
                }
            },
            //添加用户
            addSelectUser: function(row){
                var userList = this.userList;
                for(var i=0; i<userList.length; i++){
                    if(userList[i].uodp_id == row.uodp_id){
                        this.$message({
                            type: "warning",
                            message: "此用户已添加",
                            duration: 2000
                        });
                        return;
                    }
                }
                this.userList.push(row);
                this.$message({
                    message: '添加成功',
                    type: 'success',
                    duration: 2000
                });
            },
            //删除用户
            delSeleUser: function(row, index, rows){
                rows.splice(index, 1);
                this.$message({
                    type: "success",
                    message: "删除成功",
                    duration: 2000
                })
            },
            //提交用户组信息
            subGroup: function(){
                var idList = [];
                this.userList.forEach((item) => {
                    idList.push(item.uodp_id);
                })
                this.$axios({
                    url:"/cfm/adminProcess",
                    method: "post",
                    data: {
                        optype: "usrgroup_allot",
                        params: {
                            group_id: this.currentUser.group_id,
                            uodp_ids: idList
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
                        this.currentUser.uodp_ids = result.data.data.uodp_ids;
                        this.groupDialog = false;
                        this.$message({
                            type: 'success',
                            message: '修改成功!',
                            duration: 2000
                        });
                    }
                }).catch(function(error){
                    console.log(error);
                })
            }
        },
        watch: {
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data;
            }
        }
    }
</script>
