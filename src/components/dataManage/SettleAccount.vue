<style scoped lang="less" type="text/less">
    #settleAccount {
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
        /*数据展示区*/
        .table-content {
            height: 325px;
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
    }
</style>

<template>
    <div id="settleAccount">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addData">新增</el-button>
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
                        <el-form-item label="银行大类">
                            <el-select v-model="serachData.bank_type" placeholder="请选择银行"
                                       clearable filterable
                                       :filter-method="filterBankType"
                                       @visible-change="clearSearch">
                                <el-option v-for="bankType in bankTypeList"
                                           :key="bankType.name"
                                           :label="bankType.name"
                                           :value="bankType.code">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="账户">
                            <el-input v-model="serachData.query_key" clearable
                                      placeholder="输入账户号或账户名称"></el-input>
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
                <el-table-column prop="acc_no" label="账户编号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="org_name" label="所属机构" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_name" label="开户行" :show-overflow-tooltip="true"
                                 width="200"></el-table-column>
                <el-table-column prop="pay_recv_attr" label="收付属性"
                                 :formatter="setPayRecv" width="100"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="curr_name" label="币种" width="120"></el-table-column>
                <el-table-column
                        label="操作"
                        width="70">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="编辑" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editData(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeSettle(scope.row,scope.$index,tableList)"></el-button>
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
        <!--新增/修改 弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="860px" top="76px"
                   :close-on-click-modal="false">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small"
                     :rules="rules" ref="dialogForm">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="账户编号" :label-width="formLabelWidth" prop="acc_no">
                            <el-input v-model="dialogData.acc_no" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称" :label-width="formLabelWidth" prop="acc_name">
                            <el-input v-model="dialogData.acc_name" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="18">
                        <el-form-item label="开户行" :label-width="formLabelWidth">
                            <el-col :span="14">
                                <el-select v-model="bankCorrelation.bankTypeName" placeholder="请选择银行大类"
                                           clearable filterable
                                           :filter-method="filterBankType"
                                           @visible-change="clearSearch"
                                           @change="bankIsSelect">
                                    <el-option v-for="bankType in bankTypeList"
                                               :key="bankType.name"
                                               :label="bankType.name"
                                               :value="bankType.code">
                                    </el-option>
                                </el-select>
                            </el-col>
                            <el-col :span="1" style="height:1px"></el-col>
                            <el-col :span="9">
                                <el-select v-model="bankCorrelation.area"
                                           filterable remote clearable
                                           placeholder="请输入地区关键字"
                                           :remote-method="getAreaList"
                                           :loading="loading"
                                           @change="bankIsSelect">
                                    <el-option
                                            v-for="area in areaList"
                                            :key="area.name"
                                            :label="area.name"
                                            :value="area.code">
                                    </el-option>
                                </el-select>
                            </el-col>
                        </el-form-item>
                    </el-col>
                    <el-col :span="18">
                        <el-form-item label=" " :label-width="formLabelWidth" prop="cnaps_code">
                            <el-select v-model="dialogData.cnaps_code" placeholder="请选择银行"
                                       clearable filterable
                                       @visible-change="getBankList"
                                       :disabled="bankSelect">
                                <el-option v-for="bankType in bankList"
                                           :key="bankType.cnaps_code"
                                           :label="bankType.name"
                                           :value="bankType.cnaps_code">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属机构" :label-width="formLabelWidth" prop="org_id">
                            <el-select v-model="dialogData.org_id" placeholder="请选择机构"
                                       filterable clearable>
                                <el-option v-for="org in orgList"
                                           :key="org.org_id"
                                           :label="org.name"
                                           :value="org.org_id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种" :label-width="formLabelWidth" prop="curr_id">
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
                        <el-form-item label="收付属性" :label-width="formLabelWidth" prop="pay_recv_attr">
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
                        <el-form-item label="开户日期" :label-width="formLabelWidth" prop="open_date">
                            <el-date-picker type="date" placeholder="选择日期" v-model="dialogData.open_date"
                                            style="width: 100%;"
                                            format="yyyy 年 MM 月 dd 日"
                                            value-format="yyyy-MM-dd"></el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="机构段" :label-width="formLabelWidth" prop="org_seg">
                            <el-input v-model="dialogData.org_seg" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="明细段" :label-width="formLabelWidth" prop="detail_seg">
                            <el-input v-model="dialogData.detail_seg" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.memo" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain
                           @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="subCurrent">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "SettleAccount",
        created: function () {
            this.$emit("transmitTitle", "结算账户设置");
            this.$emit("getTableData", this.routerMessage);
        },
        mounted: function () {
            /*获取下拉框数据*/
            //机构
            var orgList = JSON.parse(window.sessionStorage.getItem("orgList"));
            if (orgList) {
                this.orgList = orgList;
            }
            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
                this.bankTypeList = bankTypeList;
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
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: { //本页数据获取参数
                    optype: "settacc_list",
                    params: {
                        page_size: 8,
                        page_num: 1
                    }
                },
                pagSize: 1, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                serachData: {}, //搜索区数据
                tableList: [], //表格数据
                dialogVisible: false, //弹框相关数据
                dialogTitle: "新增",
                dialogData: {
                    acc_no: "",
                    acc_name: "",
                    cnaps_code: "",
                    org_id: "",
                    curr_id: "",
                    pay_recv_attr: "",
                    open_date: "",
                    org_seg: "",
                    detail_seg: "",
                    memo: ""
                },
                formLabelWidth: "120px",
                btnText: "取消",
                currentSettle: {}, //表格当前数据
                /*下拉框数据*/
                orgList: [], //机构
                bankAllList: [], //银行大类全部
                bankTypeList: [], //银行大类
                areaList: [], //地区
                bankList: [], //银行
                currencyList: [], //币种
                accOrRecvList: {}, //收付属性
                bankCorrelation: {},
                loading: false, //地区加载数据时状态显示
                bankSelect: true, //银行可选控制
                //校验规则设置
                rules: {
                    acc_no: [
                        {
                            required: true,
                            message: "请输入账户编码",
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
                        message: "请输入账户名称",
                        trigger: "blur",
                        transform: function (value) {
                            if (value) {
                                return value.trim();
                            }
                        }
                    },
                    cnaps_code: {
                        required: true,
                        message: "请选择银行",
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
                        message: "请选择收支属性",
                        trigger: "change"
                    },
                    open_date: {
                        required: true,
                        message: "请选择时间",
                        trigger: "change"
                    },
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
                    ]
                },
            }
        },
        methods: {
            //表格展示数据格式转换
            setPayRecv: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.AccPayOrRecvAttr) {
                    return constants.AccPayOrRecvAttr[cellValue];
                }
            },
            //新增数据
            addData: function () {
                this.dialogTitle = "新增";
                this.dialogVisible = true;

                this.bankCorrelation = {};
                this.bankList = [];
                this.bankSelect = true;
                var dialogData = this.dialogData;
                for (var k in dialogData) {
                    dialogData[k] = "";
                }

                if (this.$refs.dialogForm) {
                    this.$refs.dialogForm.clearValidate();
                }
            },
            //编辑数据
            editData: function (row) {
                row.pay_recv_attr += "";
                this.dialogTitle = "编辑";
                this.dialogVisible = true;
                this.currentSettle = row;

                this.bankCorrelation = {};
                if (this.$refs.dialogForm) {
                    this.$refs.dialogForm.clearValidate();
                }
                this.bankSelect = true;
                var dialogData = this.dialogData;
                for (var k in dialogData) {
                    dialogData[k] = "";
                }

                if (row.cnaps_code) {
                    this.$set(this.bankList, 0, {
                        cnaps_code: row.cnaps_code,
                        name: row.bank_name
                    })
                }
                for (var k in row) {
                    this.dialogData[k] = row[k];
                }
            },
            //提交当前修改或新增
            subCurrent: function () {
                this.$refs.dialogForm.validate((valid, object) => {
                    if (valid) {
                        var params = this.dialogData;
                        var optype = "";
                        if (this.dialogTitle == "新增") {
                            optype = "settacc_add";
                        } else {
                            optype = "settacc_chg";
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
                                    var message = "新增成功"
                                } else {
                                    for (var k in data) {
                                        this.currentSettle[k] = data[k];
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
                    } else {
                        return false;
                    }
                })
            },
            //删除数据
            removeSettle: function (row, index, rows) {
                this.$confirm('确认删除当前数据吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: "/cfm/adminProcess",
                        method: "post",
                        data: {
                            optype: "settacc_del",
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
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.params = {
                    page_size: val,
                    page_num: "1"
                };
                this.$emit("getTableData", this.routerMessage);
            },
            //根据条件查询数据
            queryData: function () {
                var serachData = this.serachData;
                for (var key in serachData) {
                    this.routerMessage.params[key] = serachData[key];
                }

                this.$emit("getTableData", this.routerMessage);
            },

            /*下拉框相关设置*/
            //银行大类搜索筛选
            filterBankType: function (value) {
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
                    })
                } else {
                    this.bankTypeList = this.bankAllList;
                }
            },
            //银行大类展开时重置数据
            clearSearch: function () {
                if (this.bankTypeList != this.bankAllList) {
                    this.bankTypeList = this.bankAllList;
                }
            },
            //地区数据
            getAreaList: function (query) {
                if (query && query.trim()) {
                    this.loading = true;
                    this.$axios({
                        url: "/cfm/commProcess",
                        method: "post",
                        data: {
                            optype: "area_list",
                            params: {
                                query_key: query.trim()
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {

                        } else {
                            this.loading = false;
                            this.areaList = result.data.data;
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                } else {
                    this.areaList = [];
                }
            },
            //获取银行列表
            getBankList: function (status) {
                if (status) {
                    var area_code = this.bankCorrelation.area;
                    var bank_type = this.bankCorrelation.bankTypeName;

                    this.$axios({
                        url: "/cfm/commProcess",
                        method: "post",
                        data: {
                            optype: "bank_list",
                            params: {
                                area_code: area_code,
                                bank_type: bank_type
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {
                        } else {
                            this.bankList = result.data.data;
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }
            },
            //银行大类/地址变化后判断银行是否可选
            bankIsSelect: function (value) {
                this.bankList = [];
                if (this.bankCorrelation.area && this.bankCorrelation.bankTypeName) {
                    this.bankSelect = false;
                } else {
                    this.bankSelect = true;
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
