<style lang="less" type="text/less">
    #whiteContent {
        width: 80%;
        height: 100%;
        margin: 0 auto;
        min-width: 800px;

        .content {
            width: 100%;
            height: 90%;
            background-color: #fff;
            min-height: 500px;
            box-sizing: border-box;
            position: relative;
            padding: 20px;
            background-color: #fff;
        }
        /*按钮样式*/
        .content .button-list-left,
        .content .button-list-right {
            position: absolute;
            top: -40px;
        }

        .content .button-list-left {
            left: 0;
        }

        .content .button-list-right {
            right: 0;
        }
        .el-button+.el-button {
            margin-left: 4px;
        }

        /*表格部分*/
        .table-setion {
            position: absolute;
            width: 100%;
            height: 44%;
            transition: top 1s, height 1s;
            background-color: #fff;
        }
        .table-up {
            top: 0;
            height: 92%;
        }

        .table-down {
            top: 48%;
            height: 44%;
        }

        .table-setion img {
            cursor: pointer;
            vertical-align: top;
        }

        /*设置弹出框公共样式*/
        .el-dialog {
            text-align: left;
            margin-bottom: 10px;
            /*设置标题*/
            .dialog-title {
                margin-bottom: 0;
            }
            .el-dialog__body {
                padding-top: 10px;
                padding-bottom: 0;
            }
            .el-form {
                width: 94%;
                .el-select {
                    width: 100%;
                }
            }
        }
        .el-checkbox{
            display: block;
            margin-left: 50px;
            line-height: 35px;

        }
        .comDialog.el-dialog__wrapper {
            .el-dialog__body {
                height: 400px;
                overflow-y: scroll;
            }
        }
    }

</style>

<template>
    <div id="whiteContent">
        <header>
            <h1 v-text="currentTitle"></h1>
        </header>
        <section class="content" v-loading="loading">
            <div class="button-list-left">
                <el-button type="primary" plain size="mini" @click="showDialog('dialogVisible')">全部公司</el-button>
                <!-- <el-button type="primary" plain size="mini">全部银行</el-button> -->
                <el-button type="primary" plain size="mini" @click="showDialog('accDialogVisible')">账户属性</el-button>
                <el-button type="primary" plain size="mini" @click="showDialog('inactiveDialogVisible')">账户模式</el-button>
            </div>
            <div class="button-list-right">
                <el-button type="warning" size="mini">打印</el-button>
                <el-button type="warning" size="mini">下载</el-button>
            </div>
            <router-view @transmitTitle="currentTitle= $event"
                         @getTableData="getRouterData"
                         :tableData="childData"></router-view>
        </section>
        <!--请选择公司弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                    class="comDialog"
                   width="810px" title="请选择公司"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">请选择公司</h1>
            <el-tree
                :data="treeList"
                node-key="org_id"
                show-checkbox
                ref="tree"
                default-expand-all
                :expand-on-click-node="false">
                <span class="custom-tree-node" slot-scope="{ node, data }">
                    <span>{{ node.data.name }}</span>
                </span>
            </el-tree>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="dialogVisible=false">取 消</el-button>
                <el-button type="warning" size="mini" @click="queryByOrg">确 定</el-button>
            </span>
        </el-dialog>
        <!--账户属性弹出框-->
        <el-dialog :visible.sync="accDialogVisible"
                   width="600px" title="请选择账户属性"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">请选择账户属性</h1>
            <el-checkbox-group v-model="checkAccAttrList">
                <el-checkbox v-for="acc in accAttrList" :key="acc.id" :label="acc.id">{{acc.name}}</el-checkbox>
            </el-checkbox-group>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="accDialogVisible=false">取 消</el-button>
                <el-button type="warning" size="mini" @click="queryByAcc">确 定</el-button>
            </span>
        </el-dialog>
        <!--账户模式弹出框-->
        <el-dialog :visible.sync="inactiveDialogVisible"
                   width="600px" title="请选择账户模式"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">请选择账户模式</h1>
            <el-checkbox-group v-model="checkModeList">
                <el-checkbox v-for="mode in modeList" :key="mode.id" :label="mode.id">{{mode.name}}</el-checkbox>
            </el-checkbox-group>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="inactiveDialogVisible=false">取 消</el-button>
                <el-button type="warning" size="mini" @click="queryByMode">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>

    export default {
        name: "WhiteContent",
        created:function(){
            this.$axios({
                url:"/cfm/commProcess",
                method:"post",
                data:{
                    optype:"org_curlist"
                }
            }).then((result) =>{
                if (result.data.error_msg) {
                    this.$message({
                        type: "error",
                        message: result.data.error_msg,
                        duration: 2000
                    })
                } else {
                    var data = result.data.data;
                    this.treeList.push(this.setTreeData(data));
                }
            }).catch(function (error) {
                console.log(error);
            })

            let constants = JSON.parse(window.sessionStorage.getItem("constants"));
            let mode = constants.InactiveMode;
            let modeArr = [];
            for(let i in mode){
                modeArr.push({
                    id:i,
                    name:mode[i]
                })
            }
            this.modeList = modeArr;

            let accAttrObj = JSON.parse(window.sessionStorage.getItem("catgList"))[0].items;
            let accAttrArr = [];
            for(let i in accAttrObj){
                accAttrArr.push({
                    id:i,
                    name:accAttrObj[i]
                })
            }
            this.accAttrList = accAttrArr;
        },
        data: function () {
            return {
                currentTitle: "",
                childData: {},
                loading: false,
                dialogVisible: false,
                treeList:[],
                curRouterParam:{},
                accDialogVisible:false,
                inactiveDialogVisible:false,
                modeList: [],
                checkModeList: [],
                accAttrList: [],
                checkAccAttrList: []
            }
        },
        methods: {
            getRouterData: function (routerData,type) {
                if(!type)
                   this.curRouterParam = routerData;

                this.loading = true;
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: routerData
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
                        var currentData = result.data;
                        this.childData = currentData;
                        this.loading = false;
                        //机构查询后关闭弹框
                        if(type){
                            this[type] = false;
                        }
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //显示公司弹出框
            showDialog:function (type) {
                this[type] = true;
            },
            //设置树数据的转换
            setTreeData: function (data) {
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
                function setTree(data, tier, currentDatas) {
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
                            setTree(allDatas, ++tier, newCurrentDatas);
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
                            setTree(allDatas, ++tier, newCurrentDatas);
                        }
                    }
                };
                setTree(data, 2);
                return treeData;
            },
            queryByOrg:function () {
                this.curRouterParam.params.org_ids = this.$refs.tree.getCheckedKeys()
                this.curRouterParam.params.page_size = 8;
                this.curRouterParam.params.page_num = 1;
                this.getRouterData(this.curRouterParam,'dialogVisible');
            },
            queryByMode:function () {
                this.curRouterParam.params.interactive_modes = this.checkModeList;
                this.curRouterParam.params.page_size = 8;
                this.curRouterParam.params.page_num = 1;
                this.getRouterData(this.curRouterParam,'inactiveDialogVisible');
            },
            queryByAcc:function () {
                this.curRouterParam.params.acc_attrs = this.checkAccAttrList;
                this.curRouterParam.params.page_size = 8;
                this.curRouterParam.params.page_num = 1;
                this.getRouterData(this.curRouterParam,'accDialogVisible');
            }
        },
        computed: {}
    }
</script>