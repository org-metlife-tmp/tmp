<style scoped lang="less" type="text/less">
    #bankkeySet {
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

<template>
    <div id="bankkeySet">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addBankkey">新增</el-button>
            <el-button type="warning" size="mini" @click="exportFun">导出</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.os_source" placeholder="请选择来源系统"
                                       clearable filterable
                                       style="width:100%">
                                <el-option v-for="(source,key) in sourceList"
                                           :key="key"
                                           :label="source"
                                           :value="key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.channel_code" placeholder="请选择通道编码"
                                       clearable filterable
                                       style="width:100%">
                                <el-option v-for="channel in allChannelList"
                                           :key="channel.channel_id"
                                           :label="channel.channel_code"
                                           :value="channel.channel_id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.channel_desc" placeholder="请选择通道描述"
                                       clearable filterable
                                       style="width:100%">
                                <el-option v-for="channel in allChannelList"
                                           :key="channel.channel_id"
                                           :label="channel.channel_desc"
                                           :value="channel.channel_id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.bankkey" clearable placeholder="请输入bankkey"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.bankkey_desc" clearable placeholder="请输入bankkey描述"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item style="margin-bottom:0px">
                            <el-checkbox-group v-model="searchData.bankkey_status">
                                <el-checkbox :label="1" name="启用">启用</el-checkbox>
                                <el-checkbox :label="0" name="停用">停用</el-checkbox>
                            </el-checkbox-group>
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
                <el-table-column prop="os_source" label="来源系统" :show-overflow-tooltip="true"
                                 :formatter="transitSource"></el-table-column>
                <el-table-column prop="bankkey" label="bankkey" width="90px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bankkey_desc" label="bankkey描述" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="channel_code" label="通道编码" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="channel_desc" label="通道描述" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="orgname" label="机构名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bankcode" label="bankcode" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_no" label="银行账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="subordinate_channel" label="所属渠道" :show-overflow-tooltip="true"
                                 :formatter="transitChannel"></el-table-column>
                <el-table-column prop="is_source_back" label="原通道退款" :show-overflow-tooltip="true"
                                 width="96px" :formatter="transitSurce"></el-table-column>
                <el-table-column prop="bankkey_status" label="状态" :show-overflow-tooltip="true"
                                 width="60px" :formatter="transitStatus"></el-table-column>
                <el-table-column
                        label="操作" width="80"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookBankkey(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editBankkey(scope.row)"></el-button>
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
        <!--新增、修改弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="860px"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth"
                     :rules="rules" ref="dialogForm">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="来源系统" prop="os_source">
                            <el-select v-model="dialogData.os_source" placeholder="请选择来源系统"
                                       clearable filterable :disabled="isLook"
                                       style="width:100%">
                                <el-option v-for="(source,key) in sourceList"
                                           :key="key"
                                           :label="source"
                                           :value="key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="机构名称" prop="org_id">
                            <el-select v-model="dialogData.org_id" placeholder="请选择机构"
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
                    <el-col :span="12">
                        <el-form-item label="bankkey" prop="bankkey">
                            <el-input v-model="dialogData.bankkey" placeholder="请输入bankkey" :disabled="isLook"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="bankkey描述" prop="bankkey_desc">
                            <el-input v-model="dialogData.bankkey_desc" placeholder="请输入bankkey描述" :disabled="isLook"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="收付属性" prop="pay_mode">
                            <el-select v-model="dialogData.pay_mode" placeholder="请选择收付属性"
                                       clearable filterable :disabled="isLook"
                                       @change="dialogData.channel_id = ''"
                                       style="width:100%">
                                <el-option v-for="(payAttr,key) in payAttrList"
                                           :key="key"
                                           :label="payAttr"
                                           :value="key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" style="height:50.4px">
                        <el-form-item label="bankkey状态">
                            <el-switch v-model="dialogData.bankkey_status"
                                       active-value="1" :disabled="isLook"
                                       inactive-value="0"></el-switch>
                            <span v-show="dialogData.bankkey_status == 1">启用</span><span
                                v-show="dialogData.bankkey_status == 0">禁用</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="通道编码" prop="channel_id">
                            <el-select v-model="dialogData.channel_id" placeholder="请选择通道编码"
                                       clearable filterable
                                       @change="setChannel"
                                       @visible-change="getChannelList"
                                       :disabled="isLook || !dialogData.pay_mode"
                                       style="width:100%">
                                <el-option v-for="channel in channelList"
                                           :key="channel.channel_id"
                                           :label="channel.channel_code"
                                           :value="channel.channel_id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="通道描述">
                            <el-input v-model="dialogData.channel_desc" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="原通道退款">
                            <el-switch v-model="dialogData.is_source_back"
                                       active-value="1" :disabled="isLook"
                                       inactive-value="0"></el-switch>
                            <span v-show="dialogData.is_source_back == 1">是</span>
                            <span v-show="dialogData.is_source_back == 0">否</span>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="银行名称" prop="bank_type">
                            <el-select v-model="dialogData.bank_type" placeholder="请选择银行"
                                       clearable filterable :disabled="isLook"
                                       style="width:100%"
                                       :filter-method="filterBankType"
                                       @visible-change="clearSearch"
                                       :loading="bankLongding">
                                <el-option v-for="bankType in bankTypeList"
                                           :key="bankType.name"
                                           :label="bankType.display_name"
                                           :value="bankType.code">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属渠道" prop="subordinate_channel">
                            <el-select v-model="dialogData.subordinate_channel" placeholder="请选择渠道"
                                       clearable filterable :disabled="isLook"
                                       style="width:100%">
                                <el-option v-for="(item,key) in subordinateList"
                                           :key="key"
                                           :label="item"
                                           :value="key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注">
                            <el-input type="textarea" v-model="dialogData.remark" :disabled="isLook"
                                      :rows="4" placeholder="请输入文字"></el-input>
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
        name: "BankkeySet",
        created: function () {
            this.$emit("transmitTitle", "bankkey设置");
            this.$emit("getCommTable", this.routerMessage);

            /*获取常量数据*/
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            //来源系统
            if (constants.SftOsSource) {
                this.sourceList = constants.SftOsSource;
            }
            //收付属性
            if (constants.SftPayAttr) {
                this.payAttrList = constants.SftPayAttr;
            }
            //渠道列表
            if (constants.SftSubordinateChannel) {
                this.subordinateList = constants.SftSubordinateChannel;
            }
            //机构列表
            this.getOrgList();
            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
            }
            var bankAllTypeList = JSON.parse(window.sessionStorage.getItem("bankAllTypeList"));
            if (bankAllTypeList) {
                this.bankAllTypeList = bankAllTypeList;
            }
            //通道
            this.getAllChannelList();
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "sftbankkey_bankkeylist",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                searchData: { //搜索条件
                    os_source: "",
                    channel_code: "",
                    channel_desc: "",
                    bankkey: "",
                    bankkey_desc: "",
                    bankkey_status: []
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                sourceList: {}, //常量数据
                channelList: [],
                allChannelList: [],
                orgList: [],
                payAttrList: {},
                subordinateList: {},
                bankAllList: [], //银行大类全部
                bankAllTypeList: [], //银行大类全部(不重复)
                bankTypeList: [], //银行大类
                bankLongding: false,
                dialogVisible: false, //弹框数据
                dialogTitle: "新增",
                dialogData: {
                    os_source: 1,
                    org_id: "",
                    bankkey: "",
                    bankkey_desc: "",
                    pay_mode: "",
                    bankkey_status: "",
                    channel_id: "",
                    channel_desc: "",
                    bank_type: "",
                    is_source_back: "",
                    subordinate_channel: "0",
                    remark: ""
                },
                formLabelWidth: "110px",
                currentBank: {},
                isLook: false,
                //校验规则设置
                rules: {
                    os_source: {
                        required: true,
                        message: "请选择来源系统",
                        trigger: "change"
                    },
                    org_id: {
                        required: true,
                        message: "请选择机构名称",
                        trigger: "change"
                    },
                    subordinate_channel: {
                        required: true,
                        message: "请选择渠道",
                        trigger: "change"
                    },
                    bank_type: {
                        required: true,
                        message: "请选择银行",
                        trigger: "change"
                    },
                    channel_id: {
                        required: true,
                        message: "请选择通道编码",
                        trigger: "change"
                    },
                    pay_mode: {
                        required: true,
                        message: "请选择收付属性",
                        trigger: "change"
                    },
                    bankkey_desc: {
                        required: true,
                        message: "请输入bankkey描述",
                        trigger: "blur"
                    },
                    bankkey: {
                        required: true,
                        message: "请输入bankkey",
                        trigger: "blur"
                    }
                },
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
            //获取通道编码
            getChannelList: function (val,payMode) {
                if(val){
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "sftbankkey_getchanbypaymode",
                            params: {
                                pay_mode: payMode ? payMode : this.dialogData.pay_mode
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
                            this.channelList = data;
                        }

                    }).catch(function (error) {
                        console.log(error);
                    });
                }
            },
            getAllChannelList: function () {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "sftchannel_getallchannel",
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
                        this.allChannelList = data;
                    }

                }).catch(function (error) {
                    console.log(error);
                });
            },
            //银行大类搜索筛选
            filterBankType: function (value) {
                this.bankLongding = true;
                clearTimeout(this.outTime);
                this.outTime = setTimeout(() => {
                    if (value && value.trim()) {
                        this.bankTypeList = this.bankAllList.filter(item => {
                            var chineseReg = /^[\u0391-\uFFE5]+$/; //判断是否为中文
                            var englishReg = /^[a-zA-Z]+$/; //判断是否为字母
                            var quanpinReg = /(a[io]?|ou?|e[inr]?|ang?|ng|[bmp](a[io]?|[aei]ng?|ei|ie?|ia[no]|o|u)|pou|me|m[io]u|[fw](a|[ae]ng?|ei|o|u)|fou|wai|[dt](a[io]?|an|e|[aeio]ng|ie?|ia[no]|ou|u[ino]?|uan)|dei|diu|[nl][gh]ei|[jqx](i(ao?|ang?|e|ng?|ong|u)?|u[en]?|uan)|([csz]h?|r)([ae]ng?|ao|e|i|ou|u[ino]?|uan)|[csz](ai?|ong)|[csz]h(ai?|uai|uang)|zei|[sz]hua|([cz]h|r)ong|y(ao?|[ai]ng?|e|i|ong|ou|u[en]?|uan))/; //判断是否为全拼

                            if (chineseReg.test(value)) {
                                return item.name.toLowerCase().indexOf(value.toLowerCase()) > -1;
                            } else if (englishReg.test(value)) {
                                if (quanpinReg.test(value)) {
                                    return item.pinyin.toLowerCase().indexOf(value.toLowerCase()) > -1;
                                } else {
                                    return item.jianpin.toLowerCase().indexOf(value.toLowerCase()) > -1;
                                }
                            }
                        });
                        this.bankTypeList = this.bankTypeList.filter((item, index, arr) => {
                            for (var i = index + 1; i < arr.length; i++) {
                                if (item.display_name == arr[i].display_name) {
                                    return false;
                                }
                            }
                            return true;
                        });
                    } else {
                        this.bankTypeList = this.bankAllTypeList.slice(0, 200);
                    }
                    this.bankLongding = false;
                }, 1200);
            },
            //银行大类展开时重置数据
            clearSearch: function (val) {
                if (this.bankTypeList != this.bankAllTypeList && val) {
                    this.bankTypeList = this.bankAllTypeList.slice(0, 200);
                }
            },
            //选择通道
            setChannel: function (val) {
                if (val) {
                    var channelList = this.channelList;
                    for (var i = 0; i < channelList.length; i++) {
                        if (channelList[i].channel_id == val) {
                            this.dialogData.channel_desc = channelList[i].channel_desc;
                            break;
                        }
                    }
                } else {
                    this.dialogData.channel_desc = "";
                }
            },
            //新增
            addBankkey: function () {
                if (this.$refs.dialogForm) {
                    this.$refs.dialogForm.clearValidate();
                }
                this.isLook = false;
                this.dialogVisible = true;
                this.dialogTitle = "新增";

                var dialogData = this.dialogData;
                for (var k in dialogData) {
                    if (k == "bankkey_status" || k == "is_source_back" || k == "subordinate_channel") {
                        dialogData[k] = "0";
                    } else {
                        dialogData[k] = "";
                    }
                }
            },
            //保存新增或修改
            subCurrent: function () {
                this.$refs.dialogForm.validate((valid, object) => {
                    if (valid) {
                        var dialogData = this.dialogData;
                        var optype = "";

                        if (this.dialogTitle == "新增") {
                            optype = "sftbankkey_addbankkey";
                        } else {
                            optype = "sftbankkey_chgbankkey";
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

                                if (this.dialogTitle == "新增") {
                                    if (this.tableList.length < this.routerMessage.params.page_size) {
                                        this.tableList.push(data);
                                    }
                                    this.pagTotal++;
                                    var message = "新增成功";
                                } else {
                                    var currentBank = this.currentBank;
                                    for (var k in currentBank) {
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
                    } else {
                        return false;
                    }
                });
            },
            //展示格式转换-来源系统
            transitSource: function (row, column, cellValue, index) {
                return this.sourceList[cellValue];
            },
            //展示格式转换-渠道
            transitChannel: function (row, column, cellValue, index) {
                return this.subordinateList[cellValue];
            },
            //展示格式转换-状态
            transitStatus: function (row, column, cellValue, index) {
                return cellValue == 1 ? "启用" : "禁用";
            },
            //展示格式转换-原通道退款
            transitSurce: function (row, column, cellValue, index) {
                return cellValue == 1 ? "是" : "否";
            },
            //编辑
            editBankkey: function (row) {
                this.currentBank = row;
                //清空数据
                this.addBankkey();
                this.dialogTitle = "编辑";
                //设置数据
                var dialogData = this.dialogData;
                for (var k in dialogData) {
                    if (k == "os_source" || k == "pay_mode" || k == "bankkey_status" || k == "is_source_back" || k == "subordinate_channel") {
                        dialogData[k] = row[k] + "";
                    } else {
                        dialogData[k] = row[k];
                    }
                }
                this.getChannelList(true,dialogData.pay_mode);

                var channelList = this.channelList;
                for(var i = 0; i < channelList.length; i++){
                    if(channelList[i].channel_id == row.channel_id){
                        dialogData.channel_desc = channelList[i].channel_desc;
                        break;
                    }
                }
            },
            //查看
            lookBankkey: function (row) {
                this.editBankkey(row);
                this.dialogTitle = "查看";
                this.isLook = true;
            },
            //导出
            exportFun:function () {
                if(!this.tableList.length){
                    this.$message({
                        type: "warning",
                        message: "当前数据为空",
                        duration: 2000
                    });
                    return;
                }
                var params = this.routerMessage.params;
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype:"sftbankkey_listexport",
                        params:params
                    },
                    responseType: 'blob'
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else {
                        var fileName = decodeURI(result.headers["content-disposition"]).split("=")[1];
                        //ie兼容
                        if (window.navigator && window.navigator.msSaveOrOpenBlob) {
                            window.navigator.msSaveOrOpenBlob(new Blob([result.data]), fileName);
                        } else {
                            let url = window.URL.createObjectURL(new Blob([result.data]));
                            let link = document.createElement('a');
                            link.style.display = 'none';
                            link.href = url;
                            link.setAttribute('download', fileName);
                            document.body.appendChild(link);
                            link.click();
                        }
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            }
        },
        watch: {
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.tableList = val.data;
                this.pagCurrent = val.page_num;
            }
        }
    }
</script>



