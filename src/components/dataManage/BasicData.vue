<style scoped lang="less" type="text/less">
    #basicData {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*公司-银行切换*/
        .company-bank {
            position: absolute;
            top: -20px;
            right: -48px;
            width: 28px;
            height: 140px;
            li {
                width: 88%;
                line-height: 26px;
                height: 56px;
                font-size: 14px;
                border: 1px solid #00B3ED;
                background-color: #fff;
                color: #00B3ED;
                cursor: pointer;
                margin-bottom: 6px;
                position: relative;
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

        //顶部按钮
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

        /*树结构*/
        .tree-content {
            width: 96%;
            margin: 0 auto;

            ul {
                width: 100%;
                height: 30px;
                background-color: #E9F2F9;
                line-height: 30px;
                color: #848484;
                li {
                    float: left;
                    width: 30%;
                    text-align: left;
                    padding-left: 30px;
                    box-sizing: border-box;
                    border-left: 1px solid #e2e2e2;
                }
                li:nth-child(4) {
                    width: 10%;
                    border-right: 1px solid #e2e2e2;
                    text-align: center;
                    padding-left: 0;
                }
            }
        }

        .custom-tree-node {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding-right: 8px;
            /*position: relative;*/
        }
        .custom-tree-city, .custom-tree-provice {
            position: absolute;
            left: 32%;
        }
        .custom-tree-provice {
            left: 62%;
        }

        .el-tree .el-button {
            padding: 3px 3px;
        }

        /*按钮-开关*/
        .on-off {
            width: 22px;
            height: 22px;
            background-image: url(../../assets/icon_common.png);
            background-position: -273px -62px;
            border: none;
            padding: 0;
            vertical-align: middle;
        }

        /*按钮-设为默认*/
        .text-button {
            color: #848484;
        }
        .text-button:hover {
            color: #409EFF;
        }
    }
</style>

<template>
    <div id="basicData">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" v-if="btActive.department" @click="addDept">新增</el-button>
            <el-button type="warning" size="mini">下载</el-button>
        </div>
        <!--公司内容-->
        <div class="tree-content" v-if="btActive.company">
            <ul class="tree-title">
                <li>公司内容</li>
                <li>公司地址</li>
                <li>地区（省）</li>
                <li>操作</li>
            </ul>
            <el-tree :data="treeList"
                     node-key="org_id"
                     default-expand-all
                     highlight-current
                     accordion
                     :expand-on-click-node="false">
            <span class="custom-tree-node" slot-scope="{ node, data }">
                <span>{{ node.data.name }}</span>
                <span class="custom-tree-city">{{ node.data.city }}</span>
                <span class="custom-tree-provice">{{ node.data.province }}</span>
                <span>
                    <el-tooltip content="添加下级公司" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                        <el-button type="success" icon="el-icon-plus" size="mini"
                                   @click="addCompany(node,data,'add')"></el-button>
                    </el-tooltip>
                    <el-tooltip content="编辑" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                        <el-button type="primary" icon="el-icon-edit" size="mini"
                                   v-if="data.level_num != 1"
                                   @click="addCompany(node,data,'alter')"></el-button>
                    </el-tooltip>
                    <el-tooltip content="删除" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                        <el-button type="danger" icon="el-icon-delete" size="mini"
                                   v-if="data.level_num != 1"
                                   @click="removeCompany(node,data)"></el-button>
                    </el-tooltip>
                </span>
            </span>
            </el-tree>
        </div>
        <!--币种/部门 表格-->
        <el-table :data="tableList"
                  border
                  size="mini"
                  max-height="100%"
                  v-else>
            <el-table-column prop="iso_code" label="币种编号" v-if="btActive.currency"></el-table-column>
            <el-table-column prop="name" label="部门名称" v-else-if="btActive.department"></el-table-column>

            <el-table-column prop="name" label="币种名称" v-if="btActive.currency"></el-table-column>
            <el-table-column prop="status" :formatter="transition" label="状态"
                             v-else-if="btActive.department"></el-table-column>

            <el-table-column prop="symbol" label="币种符号" v-if="btActive.currency"></el-table-column>
            <el-table-column
                    label="操作"
                    width="100">
                <template slot-scope="scope" class="operationBtn">
                    <el-button type="text" v-if="btActive.currency"
                               @click="setCurrency(scope.row,tableList)"
                               :class="{'text-button':!scope.row.is_default}">设为默认
                    </el-button>
                    <el-tooltip content="开关" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                        <el-button size="mini"
                                   @click="setStatus(scope.row)"
                                   v-if="btActive.department"
                                   class="on-off"></el-button>
                    </el-tooltip>
                    <el-tooltip content="编辑" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                        <el-button type="primary" icon="el-icon-edit" size="mini"
                                   v-if="btActive.department"
                                   @click="redact(scope.row)"></el-button>
                    </el-tooltip>
                    <el-tooltip content="删除" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                        <el-button type="danger" icon="el-icon-delete" size="mini"
                                   v-if="btActive.department"
                                   @click="delDept(scope.row,scope.$index,tableList)"></el-button>
                    </el-tooltip>
                </template>
            </el-table-column>
        </el-table>
        <!-- 公司/币种/部门 选择-->
        <div class="company-bank">
            <ul>
                <li :class="{'current-select':btActive.company}"
                    @click="isActive('company')">公司
                </li>
                <li :class="{'current-select':btActive.currency}"
                    @click="isActive('currency')">币种
                </li>
                <li :class="{'current-select':btActive.department}"
                    @click="isActive('department')">部门
                </li>
            </ul>
        </div>
        <!--分页部分-->
        <div class="botton-pag" v-if="!btActive.company">
            <el-pagination
                    background
                    layout="prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    @current-change="pageChange"
                    :pager-count="5">
            </el-pagination>
        </div>
        <!--弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="30%"
                   :close-on-click-modal="false">
            <span slot="title" v-text="dialogTitle"></span>
            <el-form :model="form" :label-width="formLabelWidth">
                <el-form-item label="公司名称">
                    <el-input v-model="form.name"></el-input>
                </el-form-item>
                <el-form-item label="公司编号">
                    <el-input v-model="form.code"></el-input>
                </el-form-item>
                <el-form-item label="公司所在省">
                    <el-select v-model="form.province" placeholder="请选择省份">
                        <el-option label="北京" value="北京"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="公司所在市">
                    <el-select v-model="form.city" placeholder="请选择市">
                        <el-option label="朝阳区" value="朝阳区"></el-option>
                        <el-option label="海淀区" value="海淀区"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="公司地址">
                    <el-input v-model="form.address"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="setCompany">确 定</el-button>
            </span>
        </el-dialog>
        <el-dialog :visible.sync="deptDialog"
                   width="30%"
                   :close-on-click-modal="false">
            <span slot="title" v-text="deptDialogTitle"></span>
            <el-form :model="deptForm" :label-width="formLabelWidth">
                <el-form-item label="公司名称">
                    <el-input v-model="deptForm.name"></el-input>
                </el-form-item>
                <el-form-item label="部门描述">
                    <el-input v-model="deptForm.desc"
                              type="textarea"
                              :rows="3"></el-input>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="deptDialog = false">取 消</el-button>
                <el-button type="primary" @click="setDept">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import router from "../../router";

    export default {
        name: "BasicData",
        props: ["tableData"],
        created: function () {
            this.$emit('transmitTitle', '基础数据维护');
            this.$emit('getTableData', this.routerMessage);
        },
        data: function () {
            return {
                routerMessage: {
                    optype: "org_list"
                },
                tableList: [], //表格数据
                treeList: [], //公司数据
                pagSize: 0, //分页数据
                pagTotal: 0,
                //右侧按钮控制
                btActive: {
                    company: true,
                    currency: false,
                    department: false
                },
                dialogVisible: false, //弹框-公司
                form: {},
                currentTree: {},
                dialogTitle: "编辑",
                deptDialog: false, //弹框-部门
                deptForm: {},
                currentDept: {},
                deptDialogTitle: "编辑",
                formLabelWidth: '100px' //弹框表单的标签宽度
            }
        },
        methods: {
            //切换数据内容
            isActive: function (active) {
                //清空原先数据
                this.tableList = [];
                this.treeList = [];

                var btActive = this.btActive;
                for (var k in btActive) {
                    btActive[k] = false;
                }
                //点击公司
                if (active == "company") {
                    btActive.company = true;
                    this.routerMessage.optype = "org_list";
                    this.routerMessage.params = null;
                    this.$emit("getTableData", this.routerMessage);
                    return;
                }
                //点击币种
                if (active == "currency") {
                    btActive.currency = true;
                    this.routerMessage.optype = "currency_list";
                    this.routerMessage.params = {
                        page_size: "10",
                        page_num: "1"
                    };
                    this.$emit("getTableData", this.routerMessage);
                    return;
                }
                //点击部门
                if (active == "department") {
                    btActive.department = true;
                    this.routerMessage.optype = "dept_list";
                    this.routerMessage.params = {
                        page_size: "10",
                        page_num: "1"
                    }
                    this.$emit("getTableData", this.routerMessage);
                    return;
                }
            },
            //点击换页
            pageChange: function (currentPage) {
                this.routerMessage.params.page_num = currentPage;
                this.$emit("getTableData", this.routerMessage);
            },
            //添加下级公司 编辑当前公司
            addCompany: function (node, data, operation) {
                this.form = {};
                this.currentTree = data;
                if (operation == "add") {
                    this.dialogTitle = "添加下级公司";
                    this.dialogVisible = true;
                    this.form.parent_id = data.org_id;
                } else {
                    this.dialogTitle = "编辑";
                    this.dialogVisible = true;
                    for (var k in data) {
                        this.form[k] = data[k];
                    }
                }
            },
            //删除当前公司
            removeCompany: function (node, data) {
                this.$confirm('确认删除当前公司吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/adminProcess",
                        method: "post",
                        data: {
                            optype: "org_del",
                            params: {
                                org_id: data.org_id
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {
                            this.$message({
                                type: "error",
                                message: result.data.error_msg,
                                duration: 2000
                            })
                        }
                        if (result.data.state == "ok") {
                            const parent = node.parent;
                            const children = parent.data.children || parent.data;
                            const index = children.findIndex(d => d.org_id === data.org_id);
                            children.splice(index, 1);
                            this.$message({
                                type: 'success',
                                message: '删除成功!',
                                duration: 2000
                            });
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }).catch(() => {
                });
            },
            //弹出框-确定
            setCompany: function () {
                var params = this.form;
                var currentTree = this.currentTree;
                if (params.children) {
                    delete params.children;
                }
                if (this.dialogTitle == "添加下级公司") {
                    this.$axios({
                        url: "/cfm/adminProcess",
                        method: "post",
                        data: {
                            optype: "org_add",
                            params: params
                        }
                    }).then(function (result) {
                        if (result.data) {
                            result.data.data.children = [];
                            currentTree.children.push(result.data.data);
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                } else { //编辑当前公司
                    this.$axios({
                        url: "/cfm/adminProcess",
                        method: "post",
                        data: {
                            optype: "org_chg",
                            params: params
                        }
                    }).then(function (result) {
                        if (result.data) {
                            var data = result.data.data;
                            for (var k in data) {
                                currentTree[k] = data[k];
                            }
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }
                this.dialogVisible = false;
            },
            //币种-设为默认币种
            setCurrency: function (currentRow,rows) {
                this.$axios({
                    url: "/cfm/adminProcess",
                    method: "post",
                    data: {
                        optype: "currency_setdefault",
                        params: {
                            id: currentRow.id
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }
                    if (result.data.state == "ok") {
                        rows.forEach(function(item){
                            if(item.is_default){
                                item.is_default = 0;
                                return;
                            }
                        })
                        currentRow.is_default = 1;
                        this.$message({
                            type: 'success',
                            message: '设置成功!',
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            transition: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.OrgDeptStatus) {
                    return constants.OrgDeptStatus[cellValue];
                }
            },
            //设置部门状态
            setStatus: function (row) {
                this.$axios({
                    url: "/cfm/adminProcess",
                    method: "post",
                    data: {
                        optype: "dept_setstatus",
                        params: {
                            dept_id: row.dept_id,
                            status: row.status
                        }
                    }
                }).then((result) => {
                    if (result.data.state == "ok") {
                        row.status = result.data.data.status;
                        this.$message({
                            type: 'success',
                            message: '状态更改成功',
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //编辑当前部门
            redact: function (row) {
                this.deptDialogTitle = "编辑";
                this.currentDept = row;
                this.deptForm = {};
                for (var k in row) {
                    this.deptForm[k] = row[k];
                }
                this.deptDialog = true;
            },
            //当前部门弹框-确定
            setDept: function () {
                if (this.deptDialogTitle == "新增") {
                    this.$axios({
                        url: "/cfm/adminProcess",
                        method: "post",
                        data: {
                            optype: "dept_add",
                            params: this.deptForm
                        }
                    }).then((result) => {
                        this.tableList.push(result.data.data);
                        this.deptDialog = false;
                        this.$message({
                            type: "success",
                            message: '新增成功',
                            duration: 2000
                        })
                    }).catch(function (error) {
                        console.log(error);
                    })
                } else {
                    var currentDept = this.currentDept
                    this.$axios({
                        url: "/cfm/adminProcess",
                        method: "post",
                        data: {
                            optype: "dept_chg",
                            params: this.deptForm
                        }
                    }).then((result) => {
                        if (result.data.errorMessage) {
                            return;
                        }
                        var data = result.data.data;
                        for (var key in data) {
                            currentDept[key] = data[key];
                        }
                        this.deptDialog = false;
                        this.$message({
                            type: "success",
                            message: '修改成功',
                            duration: 2000
                        })
                    }).catch(function (error) {
                        console.log(error);
                    })
                }
            },
            //删除当前部门
            delDept: function (row, index, rows) {
                this.$confirm('确认删除当前公司吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/adminProcess",
                        method: "post",
                        data: {
                            optype: "dept_del",
                            params: {
                                dept_id: row.dept_id
                            }
                        }
                    }).then((result) => {
                        if (result.errorMessage) {
                            return;
                        }
                        rows.splice(index, 1);
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
            //新增部门
            addDept: function () {
                this.deptDialogTitle = "新增";
                this.deptForm = {};
                this.deptDialog = true;
            }
        },
        computed: {},
        watch: {
            //根据父组件返回的信息进行设置
            tableData: function (val, oldValue) {
                var data = val.data;
                if (val.page_size) { //币种 部门
                    this.pagSize = val.page_size * 1;
                    this.pagTotal = val.total_line * 1;
                    this.tableList = data;
                } else { //公司
                    /*
                    *将后台返回的数据转换为树结构数据
                    *
                    *  1、创建树结构的数据 并保存根节点
                    *  2、递归将数据添加进根节点
                    *     2.1 遍历获取当前层级的下一层数据 并根据parentId添加到其父节点下面
                    *     2.2 保存当前层级数据 作为下一次遍历时的父节点
                    *  3、终止条件：当前data中无数据
                    *
                    * */
                    //设置根节点
                    var treeData = {};
                    var oneItem = data[0];
                    for (var k in oneItem) {
                        treeData[k] = oneItem[k];
                    }
                    treeData.children = [];

                    //遍历设置子节点
                    function setTreeData(data, tier, currentDatas) {
                        var allDatas = []; //未使用全部数据
                        //第二级数据的设置
                        if (!currentDatas) {
                            var newCurrentDatas = []; //当前层级数据
                            for (var i = 0; i < data.length; i++) {
                                if (i > 0) {
                                    var item = data[i];
                                    if (item.level_num == 2) {
                                        item.children = [];
                                        treeData.children.push(item);
                                        newCurrentDatas.push(item);
                                    } else {
                                        allDatas.push(item);
                                    }
                                }
                            }
                            if (allDatas.length > 0) {
                                setTreeData(allDatas, ++tier, newCurrentDatas);
                            }
                        }
                        //第三级及以后层级数据设置
                        if (currentDatas) {
                            var newCurrentDatas = [];
                            for (var i = 0; i < data.length; i++) {
                                var item = data[i];
                                if (item.level_num == tier) {
                                    var thisParentId = item.parent_id;
                                    currentDatas.forEach(function (value) {
                                        if (value.org_id == thisParentId) {
                                            item.children = [];
                                            value.children.push(item);
                                            newCurrentDatas.push(item);
                                        }
                                    })
                                } else {
                                    allDatas.push(item);
                                }
                            }
                            if (allDatas.length > 0) {
                                setTreeData(allDatas, ++tier, newCurrentDatas);
                            }
                        }
                    };
                    setTreeData(data, 2);
                    this.treeList.push(treeData);
                    this.treeList = JSON.parse(JSON.stringify(this.treeList));
                }
            }
        }
    }
</script>
