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
            li:nth-child(2){
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

<template>
    <div id="userMenu">
        <!-- 公司/银行 选择-->
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
                      border
                      size="mini">
                <el-table-column prop="name" label="姓名" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="org_name" label="机构名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="dept_name" label="部门名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pos_name" label="职位" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="is_default" label="是否默认"
                                 :show-overflow-tooltip="true"
                                 :formatter="transitDefault"
                                 width="100"></el-table-column>
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
                    background
                    layout="prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    @current-change="getCurrentPage"
                    :pager-count="5">
            </el-pagination>
        </div>
        <!--弹出框-->
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
                            <h4>用户分配</h4>
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
    </div>
</template>

<script>
    export default {
        name: "UserMenu",
        created:function(){
            this.$emit("transmitTitle","用户菜单设置");
            this.$emit("getTableData", this.routerMessage);

            //用户组表格
            /*获取下拉框数据*/
            //机构
            var usrgroupList = JSON.parse(window.sessionStorage.getItem("usrgroupList"));
            if (usrgroupList) {
                this.usrgroupList = usrgroupList;
            }
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
                tableList: [], //表格数据
                btActive: true, //右侧按钮状态控制
                dialogVisible: false, //弹框数据
                dialogData: {},
                formLabelWidth: "120px",
                userAllocation: [],
                usrgroupList: [],
                selectIds: [],
                currentUser: {}, //当前用户
            }
        },
        methods: {
            //点击页数 获取当前页数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getTableData", this.routerMessage);
            },
            //右侧按钮点击
            isUser:function(){
                if (this.btActive) {
                    return;
                } else {
                    this.btActive = true;
                    //获取表格数据
                    this.routerMessage.pageno = 1;
                    this.routerMessage.optype = "usrmenu_list";
                    this.$emit("getTableData", this.routerMessage);
                }
            },
            isGroup:function(){
                if (!this.btActive) {
                    return;
                } else {
                    this.$message({
                        type:"error",
                        message: "功能暂无",
                        duration: 2000
                    })
                    return;
                    this.btActive = false;
                    //获取表格数据
                    this.routerMessage.pageno = 1;
                    this.routerMessage.optype = "usrmenu_allot";
                    this.$emit("getTableData", this.routerMessage);
                }
            },
            //编辑单签用户
            editUser:function(row){
                this.dialogVisible = true;
                this.dialogData = {};
                setTimeout(() => {
                    this.$refs.usergroupTable.clearSelection();
                    if(row.group_ids.length > 0){
                        row.group_ids.forEach(item => {
                            var usrgroupList = this.usrgroupList;
                            for(var i = 0; i < usrgroupList.length; i++){
                                if(item == usrgroupList[i].group_id){
                                    this.$refs.usergroupTable.toggleRowSelection(usrgroupList[i]);
                                    break;
                                }
                            }
                        })
                    }
                },100)
                this.currentUser = row;
                row.is_default += "";
                this.dialogData = row;
            },
            //展示格式转换-是否默认
            transitDefault:function(row, column, cellValue, index){
                if(cellValue){
                    return "是";
                }else{
                    return "否";
                }
            },
            //提交当前修改
            subCurrent:function(){
                this.$axios({
                    url:"/cfm/adminProcess",
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
                    if(result.data.error_msg){
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
                        this.currentUser.group_ids = data.group_ids;
                        this.dialogVisible = false;
                        this.$message({
                            type: 'success',
                            message: '修改成功!',
                            duration: 2000
                        });
                    }
                }).catch(function(error){
                    console.log(error);
                })
            },
            usergroupChange: function(val){
                var groupIds = [];
                val.forEach((item) => {
                    groupIds.push(item.group_id);
                })
                this.selectIds = groupIds;
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
