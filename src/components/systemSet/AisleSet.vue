<style scoped lang="less" type="text/less">
    #aisleSet {
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

        /*搜索区*/
        .search-setion {
            text-align: left;
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

        .form-small-title {
            font-weight: bold;
            border-bottom: 1px solid #e2e2e2;
            padding-bottom: 4px;
            margin-bottom: 10px;

            span {
                display: inline-block;
                width: 4px;
                height: 16px;
                background-color: orange;
                margin-right: 6px;
                vertical-align: middle;
            }
        }
        /*按钮样式*/
        .on-copy, .withdraw {
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
        }
        /*复制按钮*/
        .on-copy {
            background-position: -24px 1px;
        }
        /*撤回按钮*/
        .withdraw {
            background-position: -48px 0;
        }
    }
</style>
<style lang="less" type="text/less">
    #aisleSet {
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 400px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="aisleSet">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addAisle">新增</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.channel_code" clearable placeholder="请选择通道编码"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.pay_mode" placeholder="请选择支付方式"
                                       clearable filterable
                                       style="width:100%">
                                <el-option v-for="(payMode,key) in payModeList"
                                           :key="key"
                                           :label="payMode"
                                           :value="key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.bankcode" clearable placeholder="请输入bankcode"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.interactive_mode" placeholder="请选择交互方式"
                                       clearable filterable
                                       style="width:100%">
                                <el-option v-for="(interact,key) in interactiveList"
                                           :key="key"
                                           :label="interact"
                                           :value="key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item style="margin-bottom:0px">
                            <el-radio-group v-model="searchData.is_checkout">
                                <el-radio :label="1">启用</el-radio>
                                <el-radio :label="0">停用</el-radio>
                            </el-radio-group>
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
                      border size="mini">
                <el-table-column prop="channel_code" label="通道编码" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="channel_desc" label="通道描述" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_mode" label="支付方式" width="80px"
                                 :show-overflow-tooltip="true" :formatter="transitPayMode"></el-table-column>
                <el-table-column prop="pay_attr" label="收付属性" width="80px"
                                 :show-overflow-tooltip="true" :formatter="transitPayAttr"></el-table-column>
                <el-table-column prop="single_amount_limit" label="单笔金额限制" width="120px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="single_file_limit" label="单批次文件笔数限制" width="150px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bankcode" label="bankcode" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="org_name" label="文件生成归属地" width="120px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="interactive_mode" label="交互方式" width="80px"
                                 :show-overflow-tooltip="true" :formatter="transitInteract"></el-table-column>
                <el-table-column
                        label="操作" width="80"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookBankcode(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="编辑" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editBankcode(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </section>
        <!--分页部分-->
        <div class="botton-pag">
            <el-pagination
                    background
                    layout="sizes, prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    :page-sizes="[7, 50, 100, 500]"
                    :pager-count="5"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange"
                    :current-page="pagCurrent">
            </el-pagination>
        </div>
        <!--日志 弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="860px"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="交互方式">
                            <el-radio-group v-model="dialogData.interactive_mode" @change="setMode" :disabled="isLook">
                                <el-radio :label="0">直连</el-radio>
                                <el-radio :label="1">报盘</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="!dialogData.interactive_mode">
                        <el-form-item label="直连通道">
                            <el-select v-model="dialogData.direct_channel" placeholder="请选择通道"
                                       clearable filterable @change="dialogData.direct_child_channel = ''"
                                       style="width:100%" :disabled="isLook">
                                <el-option v-for="(direct,key) in directList"
                                           :key="key"
                                           :label="direct"
                                           :value="key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="!dialogData.interactive_mode">
                        <el-form-item label="子接口">
                            <el-select v-model="dialogData.direct_child_channel" placeholder="请选择子接口"
                                       clearable filterable :disabled="isLook || !dialogData.direct_channel"
                                       @visible-change="getDirectSubChannel"
                                       style="width:100%">
                                <el-option v-for="(directsub,key) in directsubList"
                                           :key="key"
                                           :label="directsub"
                                           :value="key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" v-if="dialogData.interactive_mode">
                        <el-form-item label="报盘模板">
                            <el-select v-model="dialogData.document_moudle" placeholder="请选择模板"
                                       clearable filterable
                                       style="width:100%" :disabled="isLook">
                                <el-option v-for="(moudle,key) in moudleList"
                                           :key="key"
                                           :label="moudle"
                                           :value="key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="通道编码">
                            <el-input v-model="dialogData.channel_code" placeholder="请输入通道编码"
                                      :disabled="isLook || dialogTitle == '编辑'"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="通道描述">
                            <el-input v-model="dialogData.channel_desc" placeholder="请输入体系名称"
                                      :disabled="isLook"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="支付方式">
                            <el-select v-model="dialogData.pay_mode" placeholder="请选择支付方式"
                                       clearable filterable :disabled="isLook"
                                       style="width:100%">
                                <el-option v-for="(payMode,key) in payModeList"
                                           :key="key"
                                           :label="payMode"
                                           :value="key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="收付属性">
                            <el-select v-model="dialogData.pay_attr" placeholder="请选择收付属性"
                                       clearable filterable :disabled="isLook"
                                       style="width:100%">
                                <el-option v-for="(payAttr,key) in payAttrList"
                                           :key="key"
                                           :label="payAttr"
                                           :value="key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="文件生成归属地">
                            <el-select v-model="dialogData.org_id" placeholder="请选择文件生成归属地"
                                       clearable filterable :disabled="isLook"
                                       style="width:100%">
                                <el-option v-for="item in orgList"
                                           :key="item.org_id"
                                           :label="item.name"
                                           :value="item.org_id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>

                    <el-col :span="24" class="form-small-title"><span></span>限制</el-col>
                    <el-col :span="12">
                        <el-form-item label="单笔金额限制">
                            <el-input v-model="dialogData.single_amount_limit" placeholder="请输入限制"
                                      :disabled="isLook">
                                <i slot="suffix" style="color: #000000;">元</i>
                            </el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item>
                            <div slot="label" style="line-height:16px">单批次文件<br>笔数限制</div>
                            <el-input v-model="dialogData.single_file_limit" placeholder="请输入限制"
                                      :disabled="isLook">
                                <i slot="suffix" style="color: #000000;">条</i>
                            </el-input>
                        </el-form-item>
                    </el-col>

                    <el-col :span="24" class="form-small-title"><span></span>手续费<i style="color:#ccc">（非必填）</i></el-col>
                    <el-col :span="12">
                        <el-form-item label-width="20px">
                            <el-radio :label="0" v-model="dialogData.charge_mode" @change="setMoney" :disabled="isLook">
                                按金额百分比收取
                                <el-input style="width:60px;vertical-align:middle;margin:0 16px 0 6px"
                                          v-model="chargeMoney.percentage" :disabled="isLook"></el-input>
                                %元/每笔金额
                            </el-radio>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label-width="20px">
                            <el-radio :label="1" v-model="dialogData.charge_mode" @change="setMoney" :disabled="isLook">
                                按笔数收取
                                <el-input style="width:60px;vertical-align:middle;margin:0 16px 0 6px"
                                          v-model="chargeMoney.number" :disabled="isLook"></el-input>
                                元/每笔
                            </el-radio>
                        </el-form-item>
                    </el-col>

                    <el-col :span="24" class="form-small-title"><span></span>账户</el-col>
                    <el-col :span="12">
                        <el-form-item label="bankcode">
                            <el-select v-model="dialogData.bankcode" placeholder="请选择bankcode"
                                       clearable filterable :disabled="isLook"
                                       @change="setBankcode"
                                       style="width:100%">
                                <el-option v-for="bankcode in bankcodeList"
                                           :key="bankcode.bankcode"
                                           :label="bankcode.bankcode"
                                           :value="bankcode.bankcode">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账号">
                            <el-input v-model="dialogData.acc_no" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="户名">
                            <el-input v-model="dialogData.acc_name" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行">
                            <el-input v-model="dialogData.bank_name" disabled></el-input>
                        </el-form-item>
                    </el-col>

                    <el-col :span="24" class="form-small-title"><span></span>通道指定调拨/支付账号<i style="color:#ccc">（非必填）</i>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账号">
                            <el-input v-model="dialogData.op_acc_no" placeholder="请输入账号" :disabled="isLook"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="户名">
                            <el-input v-model="dialogData.op_acc_name" placeholder="请输入户名" :disabled="isLook"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行">
                            <el-input v-model="dialogData.op_bank_name" placeholder="请输入开户行" :disabled="isLook"></el-input>
                        </el-form-item>
                    </el-col>

                    <el-col :span="24">
                        <el-form-item label="通道">
                            <el-switch v-model="dialogData.is_checkout"
                                       active-value="1" :disabled="isLook"
                                       inactive-value="0"></el-switch>
                            <span v-show="dialogData.is_checkout == 1">启用</span><span
                                v-show="dialogData.is_checkout == 0">禁用</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注">
                            <el-input type="textarea" v-model="dialogData.remark"
                                      :rows="4" placeholder="请输入文字"
                                      :disabled="isLook"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="subCurrent" v-show="!isLook">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "AisleSet",
        created: function () {
            this.$emit("transmitTitle", "通道设置");
            this.$emit("getCommTable", this.routerMessage);

            /*获取常量数据*/
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            //支付方式
            if (constants.SftPayMode) {
                this.payModeList = constants.SftPayMode;
            }
            //交互方式
            if (constants.SftInteractiveMode) {
                this.interactiveList = constants.SftInteractiveMode;
            }
            //报盘模板
            if (constants.SftDocumentMoudle) {
                this.moudleList = constants.SftDocumentMoudle;
            }
            //收付属性
            if (constants.SftPayAttr) {
                this.payAttrList = constants.SftPayAttr;
            }
            //交互方式
            if (constants.SftInteractiveMode) {
                this.interactList = constants.SftInteractiveMode;
            }
            //省
            this.provinceList = JSON.parse(window.sessionStorage.getItem("provinceList"));
            //直连通道
            this.getDirectChannel();
            //机构列表
            this.getOrgList();
            //bankcode
            this.getBankcode();
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "sftchannel_channellist",
                    params: {
                        page_size: 7,
                        page_num: 1,
                        is_checkout: 1
                    }
                },
                searchData: { //搜索条件
                    channel_code: "",
                    pay_mode: "",
                    bankcode: "",
                    interactive_mode: "",
                    is_checkout: 1
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                payModeList: {}, //常量数据
                interactiveList: {},
                moudleList: {},
                payAttrList: {},
                directList: {},
                directsubList: {},
                provinceList: {},
                interactList: {},
                orgList: [],
                bankcodeList: [],
                dialogVisible: false, //弹框数据
                dialogTitle: "新增",
                dialogData: {
                    interactive_mode: 0,
                    direct_channel: "",
                    direct_child_channel: "",
                    document_moudle: "",
                    channel_code: "",
                    channel_desc: "",
                    pay_mode: "",
                    pay_attr: "",
                    org_id: "",
                    single_amount_limit: "",
                    single_file_limit: "",
                    charge_mode: "",
                    charge_amount: "",
                    acc_id: "",
                    acc_no: "",
                    acc_name: "",
                    bank_name: "",
                    bankcode: "",
                    op_acc_no: "",
                    op_acc_name: "",
                    op_bank_name: "",
                    is_checkout: "",
                    remark: "",
                },
                formLabelWidth: "110px",
                chargeMoney: {
                    percentage: "",
                    number: ""
                },
                currentBank: {}, //编辑的当前项
                isLook: false,
            }
        },
        methods: {
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;

                for (var k in searchData) {
                    this.routerMessage.params[k] = searchData[k];
                }
                this.routerMessage.params.page_num = 1;
                this.$emit("getCommTable", this.routerMessage);
            },
            //换页后获取数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getCommTable", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.params.page_size = val;
                this.routerMessage.params.page_num = 1;
                this.$emit("getCommTable", this.routerMessage);
            },
            //新增归集单据
            addAisle: function () {
                this.dialogVisible = true;
                this.isLook = false;
                this.dialogTitle = "新增";

                var dialogData = this.dialogData;
                for (var k in dialogData) {
                    if (k == "interactive_mode") {
                        dialogData[k] = 0;
                    } else {
                        dialogData[k] = "";
                    }
                }

                var chargeMoney = this.chargeMoney;
                for (var k in chargeMoney) {
                    chargeMoney[k] = "";
                }
            },
            //获取直连通道数据
            getDirectChannel: function () {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "sftchannel_getdirectchannel",
                        params: {}
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        });
                    } else {
                        var data = result.data.data;
                        this.directList = data;
                    }

                }).catch(function (error) {
                    console.log(error);
                });
            },
            //获取子接口
            getDirectSubChannel: function (val) {
                if (val) {
                    this.directsubList = {};
                    var directChannel = this.dialogData.direct_channel
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "sftchannel_getdirectsubchannel",
                            params: {
                                direct_channel: directChannel
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {
                            this.$message({
                                type: "error",
                                message: result.data.error_msg,
                                duration: 2000
                            });
                        } else {
                            var data = result.data.data;
                            this.directsubList = data;
                        }

                    }).catch(function (error) {
                        console.log(error);
                    });
                }
            },
            //获取机构列表
            getOrgList: function () {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "sftbankkey_getorg",
                        params: {}
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        });
                    } else {
                        var data = result.data.data;
                        this.orgList = data;
                    }

                }).catch(function (error) {
                    console.log(error);
                });
            },
            //获取bankcode
            getBankcode: function () {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "sftchannel_getallbankcode",
                        params: {}
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        });
                    } else {
                        var data = result.data.data;
                        // this.bankcodeList.push(data[16]);

                        this.bankcodeList = data;
                    }

                }).catch(function (error) {
                    console.log(error);
                });
            },
            //选择bankcode后设置相关值
            setBankcode: function (val) {
                var dialogData = this.dialogData;
                if (val) {
                    var bankcodeList = this.bankcodeList;
                    for (var i = 0; i < bankcodeList.length; i++) {
                        var item = bankcodeList[i];
                        if (item.acc_id = val) {
                            dialogData.acc_id = item.acc_id;
                            dialogData.acc_no = item.acc_no;
                            dialogData.acc_name = item.acc_name;
                            dialogData.bank_name = item.bank_name;
                        }
                        break;
                    }
                } else {
                    dialogData.acc_id = "";
                    dialogData.acc_no = "";
                    dialogData.acc_name = "";
                    dialogData.bank_name = "";
                }
            },
            //改变手续费方式
            setMoney: function (val) {
                var chargeMoney = this.chargeMoney;
                if (val == 1) {
                    chargeMoney.percentage = "";
                } else {
                    chargeMoney.number = "";
                }
            },
            //改变交互方式
            setMode: function (val) {
                if (val == 0) {
                    this.dialogData.document_moudle = "";
                } else {
                    this.dialogData.direct_channel = "";
                    this.dialogData.direct_child_channel = "";
                }
            },
            //保存新增或修改
            subCurrent: function () {
                var dialogData = this.dialogData;
                var optype = "";

                if (dialogData.charge_mode == 0) {
                    dialogData.charge_amount = this.chargeMoney.percentage;
                } else if (dialogData.charge_mode == 1) {
                    dialogData.charge_amount = this.chargeMoney.number;
                }
                if(this.dialogTitle == "新增"){
                    optype = "sftchannel_addchannel";
                }else{
                    optype = "sftchannel_chgchannel";
                    dialogData.id = this.currentBank.id;
                    dialogData.persist_version = this.currentBank.persist_version;
                }

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: optype,
                        params: dialogData
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        });
                    } else {
                        var data = result.data.data;
                        var orgList = this.orgList;
                        for(var i = 0; i < orgList.length; i++){
                            if(orgList[i].org_id == data.org_id){
                                data.org_name = orgList[i].name;
                                break;
                            }
                        }

                        if (this.dialogTitle == "新增") {
                            if (this.tableList.length < this.routerMessage.params.page_size) {
                                this.tableList.push(data);
                            }
                            this.pagTotal++;
                            var message = "新增成功";
                        }else{
                            var currentBank = this.currentBank;
                            for(var k in currentBank){
                                currentBank[k] = data[k];
                            }
                            var message = "修改成功";
                        }

                        this.dialogVisible = false;
                        this.$message({
                            type: "success",
                            message: message,
                            duration: 2000
                        })
                    }

                }).catch(function (error) {
                    console.log(error);
                });
            },
            //展示格式转换-支付方式
            transitPayMode: function (row, column, cellValue, index) {
                return this.payModeList[cellValue];
            },
            //展示格式转换-金额
            transitPayAttr: function (row, column, cellValue, index) {
                return this.payAttrList[cellValue];
            },
            //展示格式转换-状态
            transitInteract: function (row, column, cellValue, index) {
                return this.interactList[cellValue];
            },

            //查看
            lookBankcode: function (row) {
                this.editBankcode(row);
                this.dialogTitle = "查看";
                this.isLook = true;
            },
            //编辑
            editBankcode: function (row) {
                this.currentBank = row;
                //清空数据
                this.addAisle();
                this.dialogTitle = "编辑";
                //设置数据
                var dialogData = this.dialogData;
                for (var k in dialogData) {
                    if (k == "pay_mode" || k == "pay_attr" || k == "is_checkout" || k == "document_moudle") {
                        dialogData[k] = row[k] + "";
                    } else {
                        dialogData[k] = row[k];
                    }
                }

                switch (dialogData.charge_mode) {
                    case 0:
                        this.chargeMoney.percentage = dialogData.charge_amount;
                        break;
                    case 1:
                        this.chargeMoney.number = dialogData.charge_amount;
                        break;
                    default:
                        break;
                }

                if(dialogData.interactive_mode == 0){
                    this.getDirectSubChannel(true);
                }
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


