<style scoped lang="less" type="text/less">
    #merchSet {
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
            margin-bottom: 20px;
            background-color: #E7E7E7;
        }
        /*数据展示区*/
        .table-content{
            height: 333px;
        }
        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
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

        /*当屏幕过小时整体样式调整*/
        @media screen and (max-width: 1340px){
            .split-bar{
                margin-bottom: 10px;
            }
        }
    }
</style>

<template>
    <div id="merchSet">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addMerch">新增</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="serachData" size="mini">
                <el-row>
                    <el-col :span="7">
                        <el-form-item label="机构">
                            <el-select v-model="serachData.org_id" placeholder="请选择机构" clearable>
                                <el-option v-for="org in orgList"
                                           :key="org.org_id"
                                           :label="org.name"
                                           :value="org.org_id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="支付渠道">
                            <el-select v-model="serachData.channel_code" placeholder="请选择支付渠道"
                                       clearable>
                                <el-option v-for="channel in channelList"
                                           :key="channel.code"
                                           :label="channel.desc"
                                           :value="channel.code">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="商户号">
                            <el-input v-model="serachData.query_key" placeholder="请输入商户号" clearable></el-input>
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
                      height="100%"
                      size="mini">
                <el-table-column prop="acc_no" label="商户号" width="180px" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="商户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="channel_name" label="支付渠道" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="org_name" label="所属机构" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_recv_attr" :formatter="setAccPay" label="账户性质"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="status" :formatter="setShowStatus" label="状态" width="80"></el-table-column>
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
                                       @click="editMerch(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeMerch(scope.row,scope.$index,tableList)"></el-button>
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
                    :page-sizes="[8, 50, 100, 500]"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange">
            </el-pagination>
        </div>
        <!--弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="810px"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth"
                     :rules="rules" ref="dialogForm">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="商户号" prop="acc_no">
                            <el-input v-model="dialogData.acc_no"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="商户名称" prop="acc_name">
                            <el-input v-model="dialogData.acc_name"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="支付渠道" prop="channel_code">
                            <el-select v-model="dialogData.channel_code" placeholder="请选择支付渠道"
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
                        <el-form-item label="所属机构" prop="org_id">
                            <el-select v-model="dialogData.org_id" placeholder="请选择机构" clearable>
                                <el-option v-for="org in orgList"
                                           :key="org.org_id"
                                           :label="org.name"
                                           :value="org.org_id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种" prop="curr_id">
                            <el-select v-model="dialogData.curr_id" placeholder="请选择币种"
                                       filterable clearable>
                                <el-option v-for="currency in currencyList"
                                           :key="currency.id"
                                           :label="currency.name"
                                           :value="currency.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="收付属性" prop="pay_recv_attr">
                            <el-select v-model="dialogData.pay_recv_attr" placeholder="请选择收付属性"
                                       filterable clearable>
                                <el-option v-for="(name,k) in accOrRecvList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户日期" prop="open_date">
                            <el-date-picker type="date" placeholder="选择日期" v-model="dialogData.open_date"
                                            style="width: 100%;"
                                            format="yyyy 年 MM 月 dd 日"
                                            value-format="yyyy-MM-dd"></el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="明细段" prop="detail_seg">
                            <el-input v-model="dialogData.detail_seg"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="机构段" prop="org_seg">
                            <el-input v-model="dialogData.org_seg"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="结算账号" prop="settle_acc_id">
                            <el-select v-model="dialogData.settle_acc_id" placeholder="请选择支付渠道"
                                       clearable>
                                <el-option v-for="settacc in settaccList"
                                           :key="settacc.id"
                                           :label="settacc.acc_no"
                                           :value="settacc.id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注">
                            <el-input v-model="dialogData.memo" width="100%"></el-input>
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
        name: "MerchSet",
        created: function () {
            this.$emit("transmitTitle", "商户号设置");
            this.$emit("getTableData", this.routerMessage);
        },
        mounted:function(){
            /*获取下拉框数据*/
            //机构
            var orgList = JSON.parse(window.sessionStorage.getItem("orgList"));
            if (orgList) {
                this.orgList = orgList;
            }
            //支付渠道
            var channelList = JSON.parse(window.sessionStorage.getItem("channelList"));
            if (channelList) {
                this.channelList = channelList;
            }
            //币种
            var currencyList = JSON.parse(window.sessionStorage.getItem("currencyList"));
            if (currencyList) {
                this.currencyList = currencyList;
            }
            //收付属性
            var AccPayOrRecvAttr = JSON.parse(window.sessionStorage.getItem("constants")).AccPayOrRecvAttr;
            if (AccPayOrRecvAttr) {
                this.accOrRecvList = AccPayOrRecvAttr;
            }
            //结算账号
            this.$axios({
                url: this.queryUrl + "adminProcess",
                method: "post",
                data: {
                    optype: "settacc_list",
                    params: {
                        page_size:10000,
                        page_num: 1
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
                    var data = result.data.data;
                    this.settaccList = data;
                }
            }).catch(function(error){
                console.log(error);
            })
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "merchacc_list",
                    params: {
                        page_size: 8,
                        page_num: 1
                    }
                },
                pagSize: 1, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                serachData: {}, //搜索域数据
                tableList: [], //表格数据
                dialogVisible: false, //弹框数据
                dialogTitle: "新增",
                dialogData: {
                    channel_code: "",
                    org_id: "",
                    curr_id: "",
                    pay_recv_attr: "",
                    open_date: ""
                },
                formLabelWidth: '120px', //弹框表单的标签宽度
                currentRouter: "",
                orgList: [], //下拉框数据
                channelList: [],
                currencyList: [],
                accOrRecvList: {},
                settaccList: [],
                //校验规则设置
                rules: {
                    acc_no: [
                        {
                            required: true,
                            message: "请输入商户号",
                            trigger: "blur",
                            transform: function (value) {
                                if (value) {
                                    return value.trim();
                                }
                            }
                        },
                        {
                            validator: function (rule, value, callback, source, options) {
                                var reg = /^[\w-]+$/;
                                if (reg.test(value)) {
                                    callback();
                                } else {
                                    var errors = [];
                                    callback(new Error("只能输入字母、数字和符号-"));
                                }
                            },
                            trigger: "blur"
                        }
                    ],
                    acc_name: {
                        required: true,
                        message: "请输入商户名称",
                        trigger: "blur",
                        transform: function (value) {
                            if (value) {
                                return value.trim();
                            }
                        }
                    },
                    channel_code: {
                        required: true,
                        message: "请选择支付渠道",
                        trigger: "change"
                    },
                    org_id: [{
                        required: true,
                        message: "请选择机构",
                        trigger: "change"
                    }],
                    curr_id: {
                        required: true,
                        message: "请选择币种",
                        trigger: "change"
                    },
                    pay_recv_attr: {
                        required: true,
                        message: "请选择收付属性",
                        trigger: "change"
                    },
                    open_date: {
                        required: true,
                        message: "请选择开户日期",
                        trigger: "change"
                    },
                    detail_seg: [
                        {
                            required: true,
                            message: "请输入明细段",
                            trigger: "blur",
                            transform: function (value) {
                                if (value) {
                                    return value.trim();
                                }
                            }
                        },
                        {
                            validator: function (rule, value, callback, source, options) {
                                var reg = /^[\w-]+$/;
                                if (reg.test(value)) {
                                    callback();
                                } else {
                                    var errors = [];
                                    callback(new Error("只能输入字母、数字和符号-"));
                                }
                            },
                            trigger: "blur"
                        }
                    ],
                    org_seg: [
                        {
                            required: true,
                            message: "请输入机构段",
                            trigger: "blur",
                            transform: function (value) {
                                if (value) {
                                    return value.trim();
                                }
                            }
                        },
                        {
                            validator: function (rule, value, callback, source, options) {
                                var reg = /^[\w-]+$/;
                                if (reg.test(value)) {
                                    callback();
                                } else {
                                    var errors = [];
                                    callback(new Error("只能输入字母、数字和符号-"));
                                }
                            },
                            trigger: "blur"
                        }
                    ],
                    settle_acc_id:[{
                        required: true,
                        message: "请选择结算账号",
                        trigger: "change"
                    }],
                },
            }
        },
        methods: {
            //展示格式转换-账户性质
            setAccPay: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.AccPayOrRecvAttr) {
                    return constants.AccPayOrRecvAttr[cellValue];
                }
            },
            //展示格式转换-状态
            setShowStatus:function(row, column, cellValue, index){
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.SetAccAndMerchStatus) {
                    return constants.SetAccAndMerchStatus[cellValue];
                }
            },
            //添加商户号
            addMerch: function () {
                this.dialogTitle = "新增";
                this.dialogVisible = true;
                //清空数据和校验信息
                for(var k in this.dialogData){
                    this.dialogData[k] = "";
                }
                if (this.$refs.dialogForm) {
                    this.$refs.dialogForm.clearValidate();
                }
            },
            //编辑商户号
            editMerch:function (row) {
                this.dialogTitle = "编辑";
                this.dialogVisible = true;
                //清空数据和校验信息
                for(var k in this.dialogData){
                    this.dialogData[k] = "";
                }
                if (this.$refs.dialogForm) {
                    this.$refs.dialogForm.clearValidate();
                }
                this.currentRouter = row;
                for(var k in row){
                    if(k == "pay_recv_attr"){
                        row[k] = row[k] + "";
                    }
                    this.dialogData[k] = row[k];
                }
            },
            //删除当前商户号
            removeMerch:function (row, index, rows) {
                this.$confirm('确认删除当前商户号吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: this.queryUrl + "adminProcess",
                        method: "post",
                        data: {
                            optype: "merchacc_del",
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

                        if(this.pagCurrent < (this.pagTotal/this.pagSize)){ //存在下一页
                            this.$emit('getTableData', this.routerMessage);
                        }else{
                            if(rows.length == "1"){ //是当前页最后一条
                                this.routerMessage.params.page_num--;
                                this.$emit('getTableData', this.routerMessage);
                            }else{
                                rows.splice(index, 1);
                                this.pagTotal--;
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
            //点击页数 获取当前页数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getTableData", this.routerMessage);
            },
            //根据条件查询数据
            queryData: function () {
                var serachData = this.serachData;
                for (var key in serachData) {
                    this.routerMessage.params[key] = serachData[key];
                }
                this.routerMessage.params.page_num = 1;
                this.$emit("getTableData", this.routerMessage);
            },
            //提交当前修改或新增
            subCurrent: function () {
                this.$refs.dialogForm.validate((valid, object) => {
                    if(valid){
                        var params = this.dialogData;
                        var optype = "";
                        if(this.dialogTitle == "新增"){
                            optype = "merchacc_add";
                        }else {
                            optype = "merchacc_chg";
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
                            }else {
                                var data = result.data.data;
                                if(this.dialogTitle == "新增"){
                                    if(this.tableList.length < this.routerMessage.params.page_size){
                                        this.tableList.push(data);
                                    }
                                    this.pagTotal++;
                                    var message = "新增成功"
                                }else{
                                    for(var k in data){
                                        this.currentRouter[k] = data[k];
                                    }
                                    var message = "修改成功"
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
            //设置状态
            setStatus:function (row) {
                this.$axios({
                    url: this.queryUrl + "adminProcess",
                    method: "post",
                    data: {
                        optype: "merchacc_setstatus",
                        params: row
                    }
                }).then((result) => {
                    if(result.data.error_msg){
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
                        row.status = result.data.data.status;
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
            //当前页数据条数发生变化
            sizeChange:function(val){
                this.routerMessage.params = {
                    page_size: val,
                    page_num: "1"
                };
                this.$emit("getTableData", this.routerMessage);
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
