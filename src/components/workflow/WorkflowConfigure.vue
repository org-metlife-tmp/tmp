<style lang="less" scoped type="text/less">
    #workflowConfigure {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion {
            width: 50%;
            margin: 0 auto 10px;
            /*搜索区按钮*/
            .el-button--primary {
                /*color: #fff;*/
                /*background-color: #409EFF;*/
            }
        }

        /*数据展示区*/
        .table-content {
            height: 407px;
        }

        /*按钮*/
        .on-off, .look-work-flow {
            width: 22px;
            height: 22px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
        }
        /*-设置状态*/
        .on-off {
            background-position: -273px -62px;
        }
        /*-查看*/
        .look-work-flow {
            background-position: -296px -61px;
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -12px;
        }

        /*配置流程弹框-新增*/
        .add-button {
            width: 100%;
            text-align: right;
            margin: -12px 0 6px 0;
            border-top: 1px solid #eee;
            padding-top: 6px;
        }
    }

    /*中间树内容*/
    .tree-content {
        height: 350px;

        .el-row {
            height: 100%;

            .el-col {
                height: 100%;
            }

            h4 {
                margin: 0;
                text-align: center;
                font-weight: 100;
            }

            /*机构*/
            .org-tree, .dept-list, .major-list {
                width: 90%;
                height: 88%;
                border: 1px solid #eee;
                margin-left: 20px;
                overflow: auto;
            }

            .dept-list, .major-list {
                box-sizing: border-box;
                padding-left: 20px;
            }
        }
    }

    /*弹框标题*/
    .dialog-title {
        margin-top: 0;
        font-size: 18px;
        font-weight: 400;
    }
</style>
<style lang="less" type="text/less">
    #workflowConfigure {
        .search-setion {
            .el-input-group__append {
                background-color: #409EFF;
                color: #fff;
            }
        }
    }
</style>

<template>
    <div id="workflowConfigure">
        <!--搜索区-->
        <div class="search-setion">
            <el-input placeholder="请输入流程名查询" class="input-with-select"
                      size="small" v-model="searchData" clearable>
                <el-button type="primary" slot="append" icon="el-icon-search" @click="queryData"></el-button>
            </el-input>
        </div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      border
                      height="100%"
                      size="mini">
                <el-table-column prop="workflow_name" label="流程名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="is_activity" label="配置状态" :formatter="transitionAct"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="配置处理"
                        width="110">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="设置状态" placement="bottom" effect="light" :enterable="false"
                                    :open-delay="500">
                            <el-button size="mini"
                                       @click="setStatus(scope.row)"
                                       class="on-off"></el-button>
                        </el-tooltip>
                        <el-tooltip content="查看" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button size="mini"
                                       @click=""
                                       class="look-work-flow"></el-button>
                        </el-tooltip>
                        <el-tooltip content="配置" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="warning" icon="el-icon-setting" size="mini"
                                       @click="settingWorkFlow(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </section>
        <!--分页部分-->
        <div class="botton-pag">
            <el-pagination
                    background :pager-count="5"
                    :current-page="pagCurrent"
                    layout="sizes , prev, pager, next, jumper"
                    :page-size="pagSize" :total="pagTotal"
                    :page-sizes="[10, 50, 100, 500]"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange">
            </el-pagination>
        </div>
        <!--配置弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="900px"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">流程配置</h1>
            <div class="add-button">
                <el-button type="warning" size="mini" @click="addSettingData">新增</el-button>
            </div>
            <el-table :data="settingTableList"
                      border
                      size="mini">
                <el-table-column prop="name" label="名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="create_on" label="时间" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作"
                        width="80">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="编辑" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editSetting(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeSetting(scope.row,scope.$index,settingTableList)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
            <span slot="footer" class="dialog-footer" style="text-align:center">
                <el-pagination
                        background :pager-count="5"
                        :current-page="settingCurPag"
                        layout="prev, pager, next, jumper"
                        :page-size="10"
                        :total="settingTotal"
                        @current-change="getSettingData">
                    </el-pagination>
            </span>
            <el-dialog :visible.sync="innerVisible"
                       width="800px"
                       append-to-body top="76px"
                       :close-on-click-modal="false">
                <h1 slot="title" v-text="innerTitle" class="dialog-title"></h1>
                <el-form :model="innerData" size="small" :label-width="formLabelWidth">
                    <el-row>
                        <el-col :span="12">
                            <el-form-item label="关系名称">
                                <el-input v-model="innerData.relationName" auto-complete="off"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="12">
                            <el-form-item label="流程名称">
                                <el-input v-model="innerData.workflowName" auto-complete="off"
                                          :disabled="true"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="24" class="tree-content">
                            <el-row>
                                <el-col :span="10">
                                    <h4 class="set-required">绑定公司</h4>
                                    <div class="org-tree">
                                        <el-tree :data="orgTreeList"
                                                 node-key="org_id"
                                                 :check-strictly="true"
                                                 highlight-current
                                                 accordion show-checkbox
                                                 :expand-on-click-node="false"
                                                 :default-expanded-keys="expandData"
                                                 ref="orgTree">
                                        <span class="custom-tree-node" slot-scope="{ node, data }">
                                            <span>{{ node.data.name }}</span>
                                        </span>
                                        </el-tree>
                                    </div>
                                </el-col>
                                <el-col :span="7">
                                    <h4 class="set-required">绑定部门</h4>
                                    <div class="dept-list">
                                        <el-checkbox :indeterminate="isIndeterminate" v-model="deptAll"
                                                     @change="deptAllChange">全选
                                        </el-checkbox>
                                        <el-checkbox-group v-model="deptSelect" @change="deptChange">
                                            <el-checkbox v-for="dept in deptList"
                                                         :label="dept.dept_id"
                                                         :key="dept.dept_id"
                                                         style="display:block;margin-left:0">{{dept.name}}
                                            </el-checkbox>
                                        </el-checkbox-group>
                                    </div>
                                </el-col>
                                <el-col :span="7">
                                    <h4 class="set-required">绑定业务</h4>
                                    <div class="major-list">
                                        <el-checkbox :indeterminate="majorIndeter" v-model="majorAll"
                                                     @change="majorAllChange">全选
                                        </el-checkbox>
                                        <el-checkbox-group v-model="majorSelect" @change="majorChange">
                                            <el-checkbox v-for="(name,k) in majorList"
                                                         :label="k"
                                                         :key="k"
                                                         style="display:block;margin-left:0">{{name}}
                                            </el-checkbox>
                                        </el-checkbox-group>
                                    </div>
                                </el-col>
                            </el-row>
                        </el-col>
                    </el-row>
                </el-form>
                <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="reset">重 置</el-button>
                    <el-button type="warning" size="mini" @click="subInnerWorkflow">保 存</el-button>
                </span>
            </el-dialog>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "WorkflowConfigure",
        created: function () {
            this.$emit("transmitTitle", "业务配置审批流程");
            this.$emit("getTableData", this.routerMessage);
        },
        mounted: function () {
            //机构树
            var orgTreeList = JSON.parse(window.sessionStorage.getItem("orgTreeList"));
            if (orgTreeList) {
                this.orgTreeList.push(orgTreeList);
            }
            //部门
            var deptList = JSON.parse(window.sessionStorage.getItem("deptList"));
            if (deptList) {
                this.deptList = deptList;
            }
            //业务
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.MajorBizType) {
                this.majorList = constants.MajorBizType;
            }
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: { //本页数据获取参数
                    optype: "wfdefine_list",
                    params: {
                        page_size: 10,
                        page_num: 1
                    }
                },
                searchData: "", //搜索数据
                tableList: [], //列表数据
                pagSize: 1, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                dialogVisible: false, //弹框数据
                settingTotal: 1,
                settingCurPag: 1,
                settingTableList: [],
                currentBaseId: "",
                currentWorkName: "",
                currentInnerData: {},
                formLabelWidth: "120px",
                innerVisible: false, //新增业务配置弹框
                innerTitle: "新增业务配置",
                innerData: {
                    relationName: "",
                    workflowName: ""
                },
                orgTreeList: [], //弹框-机构
                expandData: [],
                deptList: [], //弹框-部门
                deptSelect: [],
                isIndeterminate: false,
                deptAll: false,
                majorList: {}, //弹框-业务
                majorSelect: [],
                majorIndeter: false,
                majorAll: false,
            }
        },
        methods: {
            //展示格式转换-配置状态
            transitionAct: function (row, column, cellValue, index) {
                if (cellValue == "1") {
                    return "启用";
                } else {
                    return "禁用";
                }
            },
            //查询数据
            queryData: function () {
                this.routerMessage.params.query_key = this.searchData;
                this.$emit("getTableData", this.routerMessage);
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
            //设置状态
            setStatus: function (row) {
                this.$axios({
                    url: "/cfm/adminProcess",
                    method: "post",
                    data: {
                        optype: "wfdefine_setstatus",
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
                    } else {
                        var data = result.data.data;
                        for (var k in data) {
                            row[k] = data[k];
                        }
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
            //配置流程
            settingWorkFlow: function (row) {
                this.dialogVisible = true;
                this.currentBaseId = row.id;
                this.currentWorkName = row.workflow_name;
                this.getSettingData(1);
            },
            //配置流程弹框数据换页
            getSettingData: function (currPag) {
                this.$axios({
                    url: "/cfm/adminProcess",
                    method: "post",
                    data: {
                        optype: "wfrelation_list",
                        params: {
                            page_size: 10,
                            page_num: currPag,
                            base_id: this.currentBaseId
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
                        var data = result.data;
                        this.settingTableList = data.data;
                        this.settingTotal = data.total_line;
                        this.settingCurPag = data.page_num;
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //重置业务配置数据
            reset:function(){
                //清空数据
                var innerData = this.innerData;
                for (var k in innerData) {
                    innerData[k] = "";
                }
                if (this.$refs.orgTree) {
                    this.$refs.orgTree.setCheckedKeys([]);
                }
                this.deptSelect = [];
                this.isIndeterminate = false;
                this.majorSelect = [];
                this.majorIndeter = false;

                //设置流程名称
                this.innerData.workflowName = this.currentWorkName;
            },
            //新增业务配置流程
            addSettingData: function () {
                this.innerTitle = "新增业务配置";
                this.innerVisible = true;
                this.reset();
            },
            //编辑业务配置流程
            editSetting: function(row){
                this.innerTitle = "编辑业务配置";
                this.innerVisible = true;
                this.currentInnerData = row;
                this.reset();

                this.innerData.relationName = row.name;
                //设置公司
                var orgExp = row.org_exp;
                if (orgExp) {
                    orgExp = orgExp.split("@");
                    orgExp.pop();
                    orgExp.shift();
                    for (var i = 0; i < orgExp.length; i++) {
                        orgExp[i] = orgExp[i] * 1;
                    }
                    if (this.$refs.orgTree) {
                        this.$refs.orgTree.setCheckedKeys(orgExp);
                        this.expandData = orgExp;
                    } else {
                        setTimeout(() => {
                            this.$refs.orgTree.setCheckedKeys(orgExp);
                            this.expandData = orgExp;
                        }, 800)
                    }
                }
                //设置部门
                var deptExp = row.dept_exp;
                if (deptExp) {

                    deptExp = deptExp.split("@");
                    deptExp.pop();
                    deptExp.shift();
                    for (var i = 0; i < deptExp.length; i++) {
                        deptExp[i] = deptExp[i] * 1;
                    }
                    this.deptSelect = deptExp;
                }
                //设置业务
                var majorTypeExp = row.biz_exp;
                if (majorTypeExp) {
                    majorTypeExp = majorTypeExp.split("@");
                    majorTypeExp.pop();
                    majorTypeExp.shift();
                    this.majorSelect = majorTypeExp;
                }
            },
            //提交业务配置流程
            subInnerWorkflow: function(){
                var params = this.transitionData();
                if(!params){
                    return;
                }

                var optype = "";
                if (this.innerTitle == "新增业务配置") {
                    optype = "wfrelation_add";
                } else {
                    optype = "wfrelation_chg";
                    params.id = this.currentInnerData.id;
                    params.persist_version = this.currentInnerData.persist_version;
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

                        if (this.innerTitle == "新增业务配置") {
                            if (this.settingTableList.length < 10) {
                                this.settingTableList.push(data);
                            }
                            this.settingTotal++;
                            var message = "添加成功";
                        } else {
                            for (var k in data) {
                                this.currentInnerData[k] = data[k];
                            }
                            var message = "修改成功";
                        }

                        this.innerVisible = false;
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
            //转换提交数据格式
            transitionData: function(){
                var params = {};
                params.base_id = this.currentBaseId;
                params.name = this.innerData.relationName;

                var org_exp = this.$refs.orgTree.getCheckedKeys();
                if (org_exp.length) {
                    org_exp = org_exp.join("@");
                    org_exp = "@" + org_exp + "@";
                    params.org_exp = org_exp;
                }
                var dept_exp = this.deptSelect;
                if (dept_exp.length) {
                    dept_exp = dept_exp.join("@");
                    dept_exp = "@" + dept_exp + "@";
                    params.dept_exp = dept_exp;
                }
                var major_exp = this.majorSelect;
                if (major_exp.length) {
                    major_exp = major_exp.join("@");
                    major_exp = "@" + major_exp + "@";
                    params.biz_exp = major_exp;
                }

                return params;
            },
            //删除业务配置
            removeSetting: function (row, index, rows) {
                this.$confirm('确认删除当前业务配置流程吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/adminProcess",
                        method: "post",
                        data: {
                            optype: "wfrelation_del",
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

                        if(this.settingCurPag < (this.settingTotal/10)){ //存在下一页
                            this.getSettingData(this.settingCurPag);
                        }else{
                            if(rows.length == "1"){ //是当前页最后一条
                                this.settingCurPag--;
                                this.getSettingData(this.settingCurPag);
                            }else{
                                rows.splice(index, 1);
                                this.settingTotal--;
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
            //部门选择
            deptAllChange: function (value) {
                var deptList = [];
                this.deptList.forEach((item) => {
                    deptList.push(item.dept_id);
                })
                this.deptSelect = value ? deptList : [];
                this.isIndeterminate = false;
            },
            deptChange: function (value) {
                var allLength = this.deptList.length;
                let checkedCount = value.length;
                this.deptAll = checkedCount === allLength;
                this.isIndeterminate = checkedCount > 0 && checkedCount < allLength;
            },
            //业务选择
            majorAllChange: function (value) {
                var majorList = Object.keys(this.majorList);
                this.majorSelect = value ? majorList : [];
                this.majorIndeter = false;
            },
            majorChange: function (value) {
                var allLength = Object.keys(this.majorList).length;
                let checkedCount = value.length;
                this.majorAll = checkedCount === allLength;
                this.majorIndeter = checkedCount > 0 && checkedCount < allLength;
            },
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

