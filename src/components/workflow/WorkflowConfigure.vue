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
        height: 420px;
        margin-bottom: 20px;

        h4 {
            margin: 0;
            text-align: center;
            font-weight: 100;
        }

        /*机构*/
        .org-tree, .dept-list, .major-list {
            width: 90%;
            height: 44%;
            margin-bottom: 10px;
            border: 1px solid #eee;
            margin-left: 20px;
            overflow: auto;
        }

        .dept-list {
            padding-top: 10px;
            height: 19%;
            box-sizing: border-box;
            padding-left: 20px;
        }

    }

    /*弹框标题*/
    .dialog-title {
        margin-top: 0;
        font-size: 18px;
        font-weight: 400;
    }

    /**/
    .formflot {
        display: inline-block;
        .el-input {
            width: 60%;
        }
        .el-input__inner {
            height: 30px;
            line-height: 30px;
        }
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

    .workflow-dialog {
        .el-dialog__body {
            height: 400px;
            overflow-y: auto;
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
                                       @click="lookFlow(scope.row)"
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
                       class="workflow-dialog"
                       append-to-body top="76px"
                       :close-on-click-modal="false">
                <h1 slot="title" v-text="innerTitle" class="dialog-title"></h1>
                <el-form :model="innerData" size="small" :label-width="formLabelWidth">
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

                        <h4 class="set-required">绑定部门</h4>
                        <div class="dept-list">
                            <el-checkbox :indeterminate="isIndeterminate" v-model="deptAll"
                                         @change="deptAllChange">全选
                            </el-checkbox>
                            <el-checkbox-group v-model="deptSelect" @change="deptChange">
                                <el-checkbox v-for="dept in deptList"
                                             :label="dept.dept_id"
                                             :key="dept.dept_id">{{dept.name}}
                                </el-checkbox>
                            </el-checkbox-group>
                        </div>

                        <h4 class="set-required">绑定业务</h4>
                        <div class="org-tree">
                            <el-tree :data="majorList"
                                     node-key="biz_id"
                                     highlight-current
                                     accordion show-checkbox
                                     :expand-on-click-node="false"
                                     :default-expanded-keys="expandBizData"
                                     ref="bizTree">
                                        <span class="custom-tree-node" slot-scope="{ node, data }">
                                            <span>{{ node.data.biz_name }}</span>
                                        </span>
                            </el-tree>
                        </div>
                    </el-col>
                </el-form>
                <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="reset">重 置</el-button>
                    <el-button type="warning" size="mini" @click="subInnerWorkflow">保 存</el-button>
                </span>
            </el-dialog>
        </el-dialog>
        <!--查看工作流弹出框-->
        <el-dialog :visible.sync="lookFlowDialogVisible"
                   width="800px" title="新建流程"
                   :close-on-click-modal="false"
                   :before-close="cancelLookFlow"
                   top="56px">
            <h1 slot="title" class="dialog-title">查看流程</h1>
            <div>
                <div class="formflot" style="margin-bottom:15px">
                    <span>流程名称</span>
                    <el-input v-model="createDialogData.workflow_name" disabled size="mini"></el-input>
                </div>
                <div class="formflot">
                    <span>审批退回</span>
                    <el-input v-model="createDialogData.reject_strategy" disabled size="mini"></el-input>
                </div>
            </div>
            <WorkFlow
                    :flowList="flowList"
                    :isEmptyFlow="isEmptyFlow"
            ></WorkFlow>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="cancelLookFlow">取 消</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import WorkFlow from "../publicModule/WorkFlow.vue";

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
            //业务  biztype_list
            this.$axios({
                url: this.queryUrl + "adminProcess",
                method: "post",
                data: {
                    optype: "biztype_list"
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
                    data.forEach(element => {//一级菜单不能选
                        element.disabled = true;
                        let sec = element.children;
                        if (sec.length > 0) {
                            sec.forEach(secEle => {//二级菜单有子项不让选
                                secEle.secFlag = true;//增加是否二级属性
                                // if(secEle.children.length>0){
                                //     secEle.disabled = true;
                                // }
                            })
                        }
                    });
                    this.majorList = data;
                }
            })
        },
        props: ["tableData"],
        components: {
            WorkFlow: WorkFlow
        },
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
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
                majorList: [], //弹框-业务
                expandBizData: [],
                majorSelect: [],
                majorIndeter: false,
                majorAll: false,
                lookFlowDialogVisible: false,
                createDialogData: {},
                flowList: {},
                isEmptyFlow: false
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
                this.routerMessage.params.page_num = 1;
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
                    url: this.queryUrl + "adminProcess",
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
                    url: this.queryUrl + "adminProcess",
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
            reset: function () {
                //清空数据
                var innerData = this.innerData;
                for (var k in innerData) {
                    innerData[k] = "";
                }
                if (this.$refs.orgTree) {
                    this.$refs.orgTree.setCheckedKeys([]);
                }
                if (this.$refs.bizTree) {
                    this.$refs.bizTree.setCheckedKeys([]);
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
            editSetting: function (row) {
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
                var majorTypeExp = row.biz_exp + row.biz_setting_exp.substr(1);
                if (majorTypeExp) {
                    majorTypeExp = majorTypeExp.split("@");
                    majorTypeExp.pop();
                    majorTypeExp.shift();
                    if (this.$refs.bizTree) {
                        this.$refs.bizTree.setCheckedKeys(majorTypeExp);
                        this.expandBizData = majorTypeExp;
                    } else {
                        setTimeout(() => {
                            this.$refs.bizTree.setCheckedKeys(majorTypeExp);
                            this.expandBizData = majorTypeExp;
                        }, 800)
                    }
                }
            },
            //提交业务配置流程
            subInnerWorkflow: function () {
                var params = this.transitionData();
                if (!params) {
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
                    url: this.queryUrl + "adminProcess",
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
            transitionData: function () {
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
                var major_exp = this.$refs.bizTree.getCheckedNodes();
                if (major_exp.length) {
                    let biz_exp = "@";//存二级
                    let biz_setting_exp = "@";//存三级
                    major_exp.forEach(element => {
                        if (element.secFlag) {//如果是二级
                            biz_exp = biz_exp + element.biz_id + "@";
                        } else {
                            biz_setting_exp = biz_setting_exp + element.biz_id + "@";
                        }
                    })
                    params.biz_exp = biz_exp;
                    params.biz_setting_exp = biz_setting_exp;
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
                        url: this.queryUrl + "adminProcess",
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

                        if (this.settingCurPag < (this.settingTotal / 10)) { //存在下一页
                            this.getSettingData(this.settingCurPag);
                        } else {
                            if (rows.length == "1") { //是当前页最后一条
                                this.settingCurPag--;
                                this.getSettingData(this.settingCurPag);
                            } else {
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
            //查看工作流
            lookFlow: function (row) {
                if (row.id) {
                    this.$axios({
                        url: this.queryUrl + "commProcess",
                        method: "post",
                        data: {
                            optype: "wfquery_wfdetail",
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
                        } else {
                            let getData = result.data.data;
                            let define = getData.define;
                            this.createDialogData.workflow_name = getData.workflow_name;
                            this.createDialogData.reject_strategy = define.reject_strategy;
                            this.lookFlowDialogVisible = true;
                            //将数据传递给子组件
                            this.flowList = define;
                            this.isEmptyFlow = false;
                        }
                    })
                }
            },
            //关闭查看工作流弹框
            cancelLookFlow: function () {
                this.isEmptyFlow = true;
                this.lookFlowDialogVisible = false;
                this.flowList = {};
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

