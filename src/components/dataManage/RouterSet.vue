<style scoped lang="less" type="text/less">
    #routerSet {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        //顶部按钮
        .button-list-right {
            position: absolute;
            top: -60px;
            right: -18px;
        }
        /*搜索区*/
        .search-setion {
            text-align: right;
        }
        @media (max-width: 1340px) {
            .search-setion {
                text-align: left;
            }
        }
        /*分隔栏*/
        .split-bar {
            width: 106%;
            height: 6px;
            margin-left: -20px;
            background-color: #E7E7E7;
            margin-bottom: 20px;
        }
        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

        /*弹框样式调整*/
        /*分割线*/
        .split-form {
            width: 100%;
            height: 26px;
            border-bottom: 1px solid #eee;
            margin-top: 20px;
            margin-bottom: 10px;
            padding-bottom: 1px;
            text-align: right;
        }

        .small-title {
            margin: 0;
            position: absolute;
            top: 18px;
        }

        /*中间树内容*/
        .tree-content {
            height: 200px;

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
                .org-tree, .biz-type-list, .insure-type-list {
                    width: 90%;
                    height: 88%;
                    border: 1px solid #eee;
                    margin-left: 20px;
                    overflow: auto;
                }

                .biz-type-list, .insure-type-list {
                    box-sizing: border-box;
                    padding-left: 20px;
                }
            }
        }

        /*按钮-设置状态*/
        .on-off {
            width: 22px;
            height: 22px;
            background-image: url(../../assets/icon_common.png);
            background-position: -273px -62px;
            border: none;
            padding: 0;
            vertical-align: middle;
        }
    }
</style>
<style lang="less" type="text/less">
    #routerSet {
        .el-dialog__wrapper {
            .el-dialog__body {
                height: 400px;
                overflow-y: scroll;
            }
        }
    }
</style>

<template>
    <div id="routerSet">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addRouter">新增</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="serachData" size="mini" :label-position="'left'">
                <el-row>
                    <el-col :span="7">
                        <el-form-item label="来源系统">
                            <el-select v-model="serachData.source_code" placeholder="请选择来源系统" clearable>
                                <el-option label="个险" value="GX"></el-option>
                                <el-option label="团险" value="TX"></el-option>
                                <el-option label="移动展业" value="YD"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="机构">
                            <el-select v-model="serachData.org_exp" placeholder="请选择机构"
                                       filterable clearable>
                                <el-option v-for="org in orgList"
                                           :key="org.org_id"
                                           :label="org.name"
                                           :value="org.org_id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="支付方式">
                            <el-select v-model="serachData.pay_recv_mode" placeholder="请选择支付方式"
                                       clearable>
                                <el-option v-for="(name,k) in PayOrRecvMode"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="7">
                        <el-form-item label="支付子项">
                            <el-select v-model="serachData.pay_item" placeholder="请选择支付子项" clearable>
                                <el-option label="微信" value="WX"></el-option>
                                <el-option label="支付宝" value="ZFB"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="业务类型">
                            <el-select v-model="serachData.biz_tyep_exp" placeholder="请选择业务类型"
                                       clearable>
                                <el-option v-for="(name,k) in bizTypeList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="险种大类">
                            <el-select v-model="serachData.insurance_type_exp" placeholder="请选择险种大类"
                                       clearable>
                                <el-option v-for="(name,k) in insureTypeList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="3">
                        <el-form-item>
                            <el-button type="primary" plain @click="queryData">搜索</el-button>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      border
                      size="mini">
                <el-table-column prop="source_code" label="来源系统" :show-overflow-tooltip="true"
                                 :formatter="transiSource"></el-table-column>
                <el-table-column prop="pay_recv_mode" label="支付方式"
                                 :show-overflow-tooltip="true"
                                 :formatter="getPay"></el-table-column>
                <el-table-column prop="pay_item" label="支付子项"
                                 :show-overflow-tooltip="true"
                                 :formatter="transiPayitem"></el-table-column>
                <el-table-column prop="memo" label="备注" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="is_activate" label="是否激活"
                                 :formatter="getStatus"
                                 width="80"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作"
                        width="110">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="设置状态" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button size="mini"
                                       @click="setStatus(scope.row)"
                                       class="on-off"></el-button>
                        </el-tooltip>
                        <el-tooltip content="编辑" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editRouter(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeRouter(scope.row,scope.$index,tableList)"></el-button>
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
                    @current-change="getPageData"
                    :pager-count="5">
            </el-pagination>
        </div>
        <!--新增/修改 弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="860px" top="76px"
                   :close-on-click-modal="false">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="来源系统" :label-width="formLabelWidth">
                            <el-select v-model="dialogData.source_code" placeholder="请选择来源系统" clearable>
                                <el-option label="个险" value="GX"></el-option>
                                <el-option label="团险" value="TX"></el-option>
                                <el-option label="移动展业" value="YD"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="支付方式" :label-width="formLabelWidth">
                            <el-select v-model="dialogData.pay_recv_mode"
                                       placeholder="请选择支付方式"
                                       clearable filterable
                                       @change="payChange">
                                <el-option v-for="(name,k) in PayOrRecvMode"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="支付子项" :label-width="formLabelWidth">
                            <el-select v-model="dialogData.pay_item"
                                       placeholder="请选择支付子项"
                                       clearable :disabled="hasPayRecv">
                                <el-option label="微信" value="WX"></el-option>
                                <el-option label="支付宝" value="ZFB"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.memo" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" class="tree-content">
                        <el-row>
                            <el-col :span="10">
                                <h4>机构</h4>
                                <div class="org-tree">
                                    <el-tree :data="orgTreeList"
                                             node-key="org_id"
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
                                <h4>业务类型</h4>
                                <div class="biz-type-list">
                                    <el-checkbox :indeterminate="isIndeterminate" v-model="biztypeAll"
                                                 @change="biztypeAllChange">全选
                                    </el-checkbox>
                                    <el-checkbox-group v-model="biztypeSelect" @change="biztypeChange">
                                        <el-checkbox v-for="(name,k) in bizTypeList"
                                                     :label="k"
                                                     :key="k"
                                                     style="display:block;margin-left:0">{{name}}
                                        </el-checkbox>
                                    </el-checkbox-group>
                                </div>
                            </el-col>
                            <el-col :span="7">
                                <h4>险种大类</h4>
                                <div class="insure-type-list">
                                    <el-checkbox :indeterminate="insureIndeter" v-model="insureAll"
                                                 @change="insureAllChange">全选
                                    </el-checkbox>
                                    <el-checkbox-group v-model="insureSelect" @change="insureChange">
                                        <el-checkbox v-for="(name,k) in insureTypeList"
                                                     :label="k"
                                                     :key="k"
                                                     style="display:block;margin-left:0">{{name}}
                                        </el-checkbox>
                                    </el-checkbox-group>
                                </div>
                            </el-col>
                        </el-row>
                    </el-col>
                    <el-col :span="24" style="position:relative">
                        <h4 class="small-title">渠道账户</h4>
                    </el-col>
                </el-row>
                <el-row v-for="item in items"
                        :key="item.$id">
                    <el-col :span="24">
                        <div class="split-form">
                            <el-button-group>
                                <el-button size="mini" @click="removeAccount(item)"
                                           v-show="showDel">删除
                                </el-button>
                                <el-button size="mini" style="margin-left:0"
                                           @click="addAccount">新增
                                </el-button>
                            </el-button-group>
                        </div>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="支付渠道" :label-width="formLabelWidth">
                            <el-select v-model="item.channel_code" placeholder="请选择支付渠道"
                                       clearable>
                                <el-option v-for="channel in channelList"
                                           :key="channel.code"
                                           :label="channel.desc"
                                           :value="channel.code">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item :label-width="formLabelWidth">
                            <div slot="label" style="line-height:16px">支付渠道<br>原子接口</div>
                            <el-select v-model="item.channel_interface_code" placeholder="请选择活动区域"
                                       clearable :disabled="item.channel_code?false:true"
                                       @visible-change="getSalestList(item.channel_code)">
                                <el-option v-for="(name,k) in salesChannelList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item :label-width="formLabelWidth">
                            <div slot="label" style="line-height:16px">结算账户<br>/商户号</div>
                            <el-select v-model="item.settle_or_merchant_acc_id" placeholder="请选择账户"
                                       clearable filterable :disabled="item.channel_code?false:true"
                                       @visible-change="getAccountList(item.channel_code)">
                                <el-option v-for="closeAccount in closeAccountList"
                                           :key="closeAccount.id"
                                           :label="closeAccount.acc_no"
                                           :value="closeAccount.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="优先级" :label-width="formLabelWidth">
                            <el-input v-model="item.level" auto-complete="off"></el-input>
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
        name: "RouterSet",
        created: function () {
            this.$emit("transmitTitle", "路由设置");
            this.$emit("getTableData", this.routerMessage);

            /*获取下拉框数据*/
            //机构
            var orgList = JSON.parse(window.sessionStorage.getItem("orgList"));
            if (orgList) {
                this.orgList = orgList;
            }
            //支付方式
            var PayOrRecvMode = JSON.parse(window.sessionStorage.getItem("constants")).PayOrRecvMode;
            if (PayOrRecvMode) {
                this.PayOrRecvMode = PayOrRecvMode;
            }
            //常量里获取下拉框数据
            var catgList = JSON.parse(window.sessionStorage.getItem("catgList"));
            var bizType, insureType, salesChannel;
            for (var i = 0; i < catgList.length; i++) {
                if (catgList[i].code == "biz_type") {
                    bizType = catgList[i];
                    continue;
                }
                if (catgList[i].code == "insure_type") {
                    insureType = catgList[i];
                    continue;
                }
                if (catgList[i].code == "sales_channel") {
                    salesChannel = catgList[i];
                    continue;
                }
            }
            //业务类型
            if (bizType) {
                this.bizTypeList = bizType.items;
            }
            //险种大类
            if (insureType) {
                this.insureTypeList = insureType.items;
            }

            //机构树
            var orgTreeList = JSON.parse(window.sessionStorage.getItem("orgTreeList"));
            if (orgTreeList) {
                this.orgTreeList.push(orgTreeList);
                console.log(this.orgTreeList);
            }
            //支付渠道
            var channelList = JSON.parse(window.sessionStorage.getItem("channelList"));
            if (channelList) {
                this.channelList = channelList;
            }
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: { //本页数据获取参数
                    optype: "handleroute_list",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                pagSize: 1, //分页
                pagTotal: 1,
                serachData: {}, //搜索区
                tableList: [], //表格
                currentRouter: "",
                PayOrRecvMode: {}, //展示数据格式
                orgList: [], //下拉框数据
                bizTypeList: {},
                insureTypeList: {},
                channelList: [],
                salesChannelList: {},
                closeAccountList: [],
                dialogVisible: false, //弹框
                dialogTitle: "新增",
                dialogData: {
                    source_code: "",
                    pay_recv_mode: "",
                    pay_item: "",
                    memo: ""
                },
                items: [
                    {
                        channel_code: "",
                        channel_interface_code: "",
                        settle_or_merchant_acc_id: "",
                        level: "",
                        $id: 1
                    }
                ],
                formLabelWidth: "120px",
                orgTreeList: [], //弹框-机构
                biztypeSelect: [], //弹框-业务类型
                isIndeterminate: false,
                biztypeAll: false,
                insureIndeter: false, //弹框-险种大类
                insureAll: false,
                insureSelect: [],
                isCloseAccount: false, //结算账户的展开状态
                isCloseSale: false, //原子接口的展开状态
                expandData: [],
                hasPayRecv: true
            }
        },
        methods: {
            //展示格式转换-支付方式
            getPay: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.PayOrRecvMode) {
                    return constants.PayOrRecvMode[cellValue];
                }
            },
            //展示格式转换-状态
            getStatus: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.YesOrNo) {
                    return constants.YesOrNo[cellValue];
                } else {
                    return "";
                }
            },
            //换页后获取数据
            getPageData: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getTableData", this.routerMessage);
            },
            //新增路由
            addRouter: function () {
                this.dialogTitle = "新增";
                this.clearDialogData();
                this.dialogVisible = true;
            },
            //编辑当前路由
            editRouter: function (row) {
                this.dialogTitle = "编辑";
                this.dialogVisible = true;
                this.currentRouter = row;

                var dialogData = this.dialogData;
                row.pay_recv_mode += "";
                for (var k in dialogData) {
                    dialogData[k] = row[k];
                }
                dialogData.id = row.id;

                var orgExp = row.org_exp;
                if (orgExp) {
                    orgExp = orgExp.split("@");
                    orgExp.pop();
                    orgExp.shift();
                    setTimeout(() => {
                        console.log(orgExp);
                        this.$refs.orgTree.setCheckedKeys(orgExp);
                        this.expandData = orgExp;
                    }, 100)
                }
                var bizTypeExp = row.biz_type_exp;
                if (bizTypeExp) {
                    bizTypeExp = bizTypeExp.split("@");
                    bizTypeExp.pop();
                    bizTypeExp.shift();
                    this.biztypeSelect = bizTypeExp;
                }
                var insuranceTypeExp = row.insurance_type_exp;
                if (insuranceTypeExp) {
                    insuranceTypeExp = insuranceTypeExp.split("@");
                    insuranceTypeExp.pop();
                    insuranceTypeExp.shift();
                    this.insureSelect = insuranceTypeExp;
                }
                if (!row.items) {
                    this.$axios({
                        url: "/cfm/adminProcess",
                        method: "post",
                        data: {
                            optype: "handleroute_detail",
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
                        } else {
                            var itemList = result.data.data.items;
                            itemList.forEach((item) => {
                                for (var key in item) {
                                    if (key == "id") {
                                        item.$id = item[key];
                                    }
                                }
                                var itemAccId = item.settle_or_merchant_acc_id;
                                var pushData = true;
                                this.closeAccountList.forEach((current) => {
                                    if (current.id == itemAccId) {
                                        pushData = false;
                                    }
                                })
                                if (pushData) {
                                    this.closeAccountList.push({
                                        id: item.settle_or_merchant_acc_id,
                                        acc_no: item.settle_or_merchant_acc_no
                                    })
                                }
                            })
                            this.items = itemList;
                            row.items = itemList;
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                } else {
                    var itemList = row.items;
                    itemList.forEach((item) => {
                        for (var key in item) {
                            if (key == "id") {
                                item.$id = item[key];
                            }
                        }
                    })
                    this.items = itemList;
                }
            },
            //删除当前路由
            removeRouter: function (row, index, rows) {
                this.$confirm('确认删除当前路由吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/adminProcess",
                        method: "post",
                        data: {
                            optype: "handleroute_del",
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
            //根据条件查询数据
            queryData: function () {
                var serachData = this.serachData;
                for (var key in serachData) {
                    this.routerMessage.params[key] = serachData[key];
                }
                this.$emit("getTableData", this.routerMessage);
            },
            /*弹出框相关*/
            //业务类型选择
            biztypeAllChange: function (value) {
                var bizTypeList = Object.keys(this.bizTypeList);
                this.biztypeSelect = value ? bizTypeList : [];
                this.isIndeterminate = false;
            },
            biztypeChange: function (value) {
                var allLength = Object.keys(this.bizTypeList).length;
                let checkedCount = value.length;
                this.biztypeAll = checkedCount === allLength;
                this.isIndeterminate = checkedCount > 0 && checkedCount < allLength;
            },
            //险种大类选择
            insureAllChange: function (value) {
                var insureTypeList = Object.keys(this.insureTypeList);
                this.insureSelect = value ? insureTypeList : [];
                this.insureIndeter = false;
            },
            insureChange: function (value) {
                var allLength = Object.keys(this.insureTypeList).length;
                let checkedCount = value.length;
                this.insureAll = checkedCount === allLength;
                this.insureIndeter = checkedCount > 0 && checkedCount < allLength;
            },
            //新增渠道账户
            addAccount: function () {
                var item = {
                    channel_code: "",
                    channel_interface_code: "",
                    settle_or_merchant_acc_id: "",
                    level: "",
                    $id: Date.now()
                };
                this.items.push(item);
            },
            //删除渠道账户
            removeAccount: function (item) {
                var index = this.items.indexOf(item);
                if (index != -1) {
                    this.items.splice(index, 1);
                }
            },
            //获取结算账户数据
            getAccountList: function (code) {
                this.isCloseAccount = !this.isCloseAccount;
                if (this.isCloseAccount) {
                    this.closeAccountList = [];
                }
                if (this.isCloseAccount && code) {
                    this.$axios({
                        url: "/cfm/adminProcess",
                        method: "post",
                        data: {
                            optype: "handleroute_setormeracc",
                            params: {
                                code: code
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {
                        } else {
                            this.closeAccountList = result.data.data;
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }
            },
            //设置原子接口数据
            getSalestList:function(code){
                this.isCloseSale = !this.isCloseSale;
                if(this.isCloseSale){
                    this.salesChannelList = {};
                }
                if(this.isCloseSale && code){
                    var channelList = JSON.parse(window.sessionStorage.getItem("channelList"));
                    for(var i = 0; i < channelList.length; i++){
                        if(channelList[i].code = code){
                            this.salesChannelList = channelList[i].items;
                        }
                    }
                }
            },
            //提交数据
            subCurrent: function () {
                var params = this.transitionData();
                var optype = "";
                if (this.dialogTitle == "新增") {
                    optype = "handleroute_add";
                } else {
                    optype = "handleroute_chg";
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
                            var message = "添加成功";
                        } else {
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
            //提交前转换数据格式
            transitionData: function () {
                var params = {};
                var dialogData = this.dialogData;
                for (var key in dialogData) {
                    params[key] = dialogData[key];
                }

                var org_exp = this.$refs.orgTree.getCheckedKeys();
                if (org_exp.length) {
                    org_exp = org_exp.join("@");
                    org_exp = "@" + org_exp + "@";
                    params.org_exp = org_exp;
                }
                var biz_type_exp = this.biztypeSelect;
                if (biz_type_exp.length) {
                    biz_type_exp = biz_type_exp.join("@");
                    biz_type_exp = "@" + biz_type_exp + "@";
                    params.biz_type_exp = biz_type_exp;
                }
                var insurance_type_exp = this.insureSelect;
                if (insurance_type_exp.length) {
                    insurance_type_exp = insurance_type_exp.join("@");
                    insurance_type_exp = "@" + insurance_type_exp + "@";
                    params.insurance_type_exp = insurance_type_exp;
                }
                // console.log(this.dialogData);
                var items = [];
                this.items.forEach(function (item) {
                    var current = {};
                    for (var k in item) {
                        if (k != "$id") {
                            current[k] = item[k];
                        }
                    }
                    items.push(current);
                })
                params.items = items;
                return params;
            },
            //清空弹框数据
            clearDialogData: function () {
                var dialogData = this.dialogData;
                for (var k in dialogData) {
                    dialogData[k] = "";
                }
                if (this.$refs.orgTree) {
                    this.$refs.orgTree.setCheckedKeys([]);
                }
                this.biztypeSelect = [];
                this.isIndeterminate = false;
                this.insureSelect = [];
                this.insureIndeter = false;
                this.items.splice(1, this.items.length - 1);
                var itemOne = this.items[0];
                for (var key in itemOne) {
                    if (key != "$id") {
                        itemOne[key] = "";
                    } else {
                        itemOne[key] = "1"
                    }
                }
            },
            //选择支付方式
            payChange: function (value) {
                if (value == "3") {
                    this.hasPayRecv = false;
                } else {
                    this.hasPayRecv = true;
                    this.dialogData.pay_item = "";
                }
            },
            //展示格式转换-来源系统
            transiSource: function (row, column, cellValue, index) {
                var data = {
                    GX: "个险",
                    TX: "团险",
                    YD: "移动展业",
                }
                return data[cellValue];
            },
            //展示格式转换-支付子项
            transiPayitem:function (row, column, cellValue, index) {
                if(cellValue == "WX"){
                    return "微信";
                }else{
                    return "支付宝";
                }
            },
            //设置状态
            setStatus:function (row) {
                this.$axios({
                    url:"/cfm/adminProcess",
                    method: "post",
                    data: {
                        optype: "handleroute_setstatus",
                        params: {
                            id: row.id
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
                        row.is_activate = result.data.data.is_activate;
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
        },
        watch: {
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.tableList = val.data;
            }
        },
        computed: {
            showDel: function () {
                if (this.items.length > 1) {
                    return true;
                } else {
                    return false;
                }
            }
        }
    }
</script>
