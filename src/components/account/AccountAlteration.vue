<style scoped lang="less" type="text/less">
    #accountAlteration {
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

        /*数据展示区*/
        .table-content {
            height: 289px;
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

        //弹框
        .form-small-title {
            line-height: 16px;
            border-bottom: 1px solid #e3e3e3;
            padding-bottom: 6px;
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
        .form-two-title {
            margin-bottom: 10px;
            span {
                display: inline-block;
                width: 49%;
                height: 16px;
                line-height: 16px;
                border-bottom: 1px solid #e3e3e3;
                padding-bottom: 6px;

                i {
                    display: inline-block;
                    width: 4px;
                    height: 16px;
                    background-color: orange;
                    margin-right: 6px;
                    vertical-align: middle;
                }
            }
            span:nth-child(1) {
                margin-right: 10px;
            }
        }
    }
</style>
<style lang="less" type="text/less">
    #accountAlteration {
        .el-dialog__wrapper {
            .el-dialog__body {
                height: 400px;
                overflow-y: scroll;
            }
        }
    }
</style>

<template>
    <div id="accountAlteration">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addAlteration" v-show="isPending">新增</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="7" v-if="!isPending">
                        <el-form-item>
                            <el-col :span="11">
                                <el-date-picker type="date" placeholder="起始日期" v-model="searchData.date1"
                                                style="width: 100%;"></el-date-picker>
                            </el-col>
                            <el-col class="line" :span="1" style="text-align:center">-</el-col>
                            <el-col :span="11">
                                <el-date-picker type="date" placeholder="结束日期" v-model="searchData.date2"
                                                style="width: 100%;"></el-date-picker>
                            </el-col>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.name" placeholder="请输入事由摘要关键字"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <!--<el-button type="primary" plain @click="" size="mini">清空</el-button>-->
                            <el-button type="primary" plain @click="" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item style="margin-bottom:0px">
                            <el-checkbox-group v-model="searchData.type" v-if="isPending">
                                <el-checkbox label="已保存" name="type"></el-checkbox>
                                <el-checkbox label="审批拒绝" name="type"></el-checkbox>
                            </el-checkbox-group>
                            <el-checkbox-group v-model="searchData.type" v-else>
                                <el-checkbox label="已提交" name="type"></el-checkbox>
                                <el-checkbox label="审批中" name="type"></el-checkbox>
                                <el-checkbox label="审批通过" name="type"></el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section :class="['table-content']">
            <el-table :data="tableList"
                      border
                      height="100%"
                      size="mini">
                <el-table-column prop="apply_on" label="申请日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="service_status" label="业务状态"
                                 :formatter="transitionStatus"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="80"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="isPending">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editAlteration(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="isPending">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removeMatter(scope.row,scope.$index,tableList)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500" v-show="!isPending">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookMatter(scope.row)"></el-button>
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
                    :current-page="pagCurrent"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange">
            </el-pagination>
        </div>
        <!--待处理新增弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="860px" title="新增"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="24" class="form-small-title"><span></span>申请日期</el-col>
                    <el-col :span="12">
                        <el-form-item label="账户号">
                            <el-select v-model="dialogData.acc_id" @change="changeAccId" clearable>
                                <el-option
                                        v-for="item in accList"
                                        :key="item.acc_id"
                                        :label="item.acc_no"
                                        :value="item.acc_id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户时间">
                            <el-input v-model="dialogData.open_date" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" class="form-two-title">
                        <span><i></i>变更前信息</span>
                        <span><i></i>变更后信息</span>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称">
                            <el-input v-model="dialogData.acc_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称">
                            <el-input v-model="dialogData.$1" clearable></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属机构">
                            <el-input v-model="dialogData.org_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="所属机构">
                            <el-select v-model="dialogData.$2" placeholder="请选择所属机构"
                                       filterable clearable>
                                <el-option v-for="org in orgList"
                                           :key="org.org_id"
                                           :label="org.name"
                                           :value="org.org_id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" style="height:51px"></el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行">
                            <el-select v-model="dialogData.bankTypeName" placeholder="请选择银行大类"
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
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" style="height:51px"></el-col>
                    <el-col :span="12">
                        <el-form-item label="开户地址">
                            <el-select v-model="dialogData.area"
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
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行">
                            <el-input v-model="dialogData.bank_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="开户行">
                            <el-select v-model="dialogData.$3" placeholder="请选择银行"
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
                        <el-form-item label="账户法人">
                            <el-input v-model="dialogData.lawfull_man" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户法人">
                            <el-input v-model="dialogData.$4" clearable></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <el-input v-model="dialogData.curr_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种">
                            <el-select v-model="dialogData.$5" placeholder="请选择币种"
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
                        <el-form-item label="账户属性">
                            <el-input v-model="dialogData.acc_attr_name" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户属性">
                            <el-select v-model="dialogData.$6" placeholder="请选择账户属性"
                                       clearable>
                                <el-option v-for="(name,k) in attrList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式">
                            <el-input v-model="getInteract" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户模式">
                            <el-select v-model="dialogData.$7" placeholder="请选择账户模式"
                                       clearable>
                                <el-option v-for="(name,k) in interList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" class="form-small-title"><span></span>备注与附件</el-col>
                    <el-col :span="24">
                        <el-form-item label="备注">
                            <el-input v-model="dialogData.memo"
                                      type="textarea" :rows="3"
                                      placeholder="请输入事由说明(100字以内)"></el-input>
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
        name: "AccountAlteration",
        created: function () {
            this.$emit("transmitTitle", "账户变更申请");
            this.$emit("getTableData", this.routerMessage);
        },
        mounted: function () {
            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
                this.bankTypeList = bankTypeList;
            }
            //所属机构
            var orgList = JSON.parse(window.sessionStorage.getItem("selectOrgList"));
            if (orgList) {
                this.orgList = orgList;
            }
            //币种
            var currencyList = JSON.parse(window.sessionStorage.getItem("selectCurrencyList"));
            if (currencyList) {
                this.currencyList = currencyList;
            }
            //账户属性&账户用途
            var catgList = JSON.parse(window.sessionStorage.getItem("catgList"));
            for (var i = 0; i < catgList.length; i++) {
                if (catgList[i].code == "acc_attr") {
                    this.attrList = catgList[i].items;
                    break;
                }
            }
            //账户模式
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.InactiveMode) {
                this.interList = constants.InactiveMode;
            }
        },
        props: ["isPending", "tableData"],
        data: function () {
            return {
                routerMessage: { //路由数据参数信息
                    todo: {
                        optype: "openchg_todolist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    },
                    done: {
                        optype: "openchg_donelist",
                        params: {
                            page_size: 7,
                            page_num: 1
                        }
                    }
                },
                searchData: { //搜索区数据
                    type: []
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                dialogVisible: false, //弹框数据
                dialogData: {
                    acc_id: "",
                    open_date: "",
                    acc_name: "",
                    org_name: "",
                    bank_name: "",
                    lawfull_man: "",
                    curr_name: "",
                    acc_attr_name: "",
                    interactive_mode: "",
                    $1: "",
                    $2: "",
                    $3: "",
                    $4: "",
                    $5: "",
                    $6: "",
                    $7: "",
                    bankTypeName: "",
                    area: ""
                },
                formLabelWidth: "120px",
                dialogTitle: "新增",
                currentAltera: {}, //当前数据
                /*下拉框数据*/
                accList: [], //账户号
                orgList: [], //所属机构
                currencyList: [], //币种
                attrList: {}, //账户属性
                interList: {}, //账户模式
                bankAllList: [], //银行大类全部
                bankTypeList: [], //银行大类
                areaList: [], //地区
                loading: false, //地区加载状态
                bankList: [], //银行
                bankSelect: true, //银行可选控制
            }
        },
        methods: {
            //展示格式转换-业务状态
            transitionStatus: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.BillStatus) {
                    return constants.BillStatus[cellValue];
                }
            },
            //点击页数 获取当前页数据
            getCurrentPage: function (currPage) {
                if (this.isPending) {
                    this.routerMessage.todo.params.page_num = currPage;
                } else {
                    this.routerMessage.done.params.page_num = currPage;
                }
                this.$emit("getTableData", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.todo.params = {
                    page_size: val,
                    page_num: 1
                };
                this.routerMessage.done.params = {
                    page_size: val,
                    page_num: 1
                };
                this.$emit("getTableData", this.routerMessage);
            },
            //新增
            addAlteration: function () {
                this.dialogTitle = "新增";
                this.dialogVisible = true;
                var dialogData = this.dialogData;
                for (var k in dialogData) {
                    dialogData[k] = "";
                }
                this.setAccsList({acc_id:""});
            },
            //编辑
            editAlteration: function (row) {
                this.dialogTitle = "编辑";
                this.currentAltera = row;
                this.dialogVisible = true;
                var dialogData = this.dialogData;
                for (var k in dialogData) {
                    dialogData[k] = "";
                }

                this.setAccsList(row);

                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "openchg_detail",
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
                        var data = result.data.data;
                        for (var key in data) {
                            //转换变更后数据
                            if(key == "change_content"){
                                var item = data[key];
                                for(var i = 0; i < item.length; i++){
                                    var current = item[i];
                                    //添加开户行下拉数据
                                    if(current.type == "3"){
                                        this.$set(this.bankList, 0, {
                                            cnaps_code: current.new_id,
                                            name: current.new_value
                                        })
                                    }
                                    //存在id时为下拉框数据 赋值为id
                                    if(current.new_id){
                                        if(current.type == 2 || current.type == 5){ //后台此处id返回为字符串 下拉框为数字 转换格式
                                            dialogData["$"+current.type] = current.new_id * 1;
                                        }else{
                                            dialogData["$"+current.type] = current.new_id;
                                        }
                                    }else{
                                        dialogData["$"+current.type] = current.new_value;
                                    }
                                }
                            }else{
                                dialogData[key] = data[key];
                            }
                        }
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //提交当前新增或修改
            subCurrent: function () {
                var dialogData = this.dialogData;
                //设置传递参数
                var reg = /^\$.*/;
                var paramsData = [];
                for (var k in dialogData) {
                    if (reg.test(k)) {
                        var item = {
                            type: k.slice(1, k.length),
                            value: dialogData[k]
                        };
                        paramsData.push(item);
                    }
                }
                var optype = "";
                var params = {
                    acc_id: dialogData.acc_id,
                    memo: dialogData.memo,
                    change_content: paramsData
                };
                if (this.dialogTitle == "新增") {
                    optype = "openchg_add";
                } else {
                    optype = "openchg_chg";
                    params.id = dialogData.id;
                    params.persist_version = dialogData.persist_version;
                }

                this.$axios({
                    url: "/cfm/normalProcess",
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
                        if(this.dialogTitle == "新增"){
                            if (this.tableList.length < this.routerMessage.todo.params.page_size) {
                                this.tableList.push(data);
                            }
                            this.pagTotal++;
                            var message = "新增成功"
                        }else{
                            for (var k in data) {
                                this.currentAltera[k] = data[k];
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
            },
            /*下拉框相关设置*/
            //设置账户号下拉数据
            setAccsList:function(row){
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "account_accs",
                        params: {
                            status: 1,
                            acc_id: row.acc_id
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
                        this.accList = data;
                    }
                });
            },
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
            //银行大类/地址变化后判断银行是否可选
            bankIsSelect: function (value) {
                this.bankList = [];
                if (this.dialogData.area && this.dialogData.bankTypeName) {
                    this.bankSelect = false;
                } else {
                    this.bankSelect = true;
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
                    var area_code = this.dialogData.area;
                    var bank_type = this.dialogData.bankTypeName;

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
            //选择账户号带出相关数据
            changeAccId: function (val) {
                console.log(val);
                var accList = this.accList;
                for (var i = 0; i < accList.length; i++) {
                    var item = accList[i];
                    if (item.acc_id == val) {
                        for (var k in item) {
                            this.dialogData[k] = item[k];
                        }
                    }
                }
            }
        },
        computed: {
            getInteract: function () {
                var inactiveMode = JSON.parse(window.sessionStorage.getItem("constants")).InactiveMode;
                return inactiveMode[this.dialogData.interactive_mode];
            }
        },
        watch: {
            isPending: function (val, oldVal) {
                this.searchData.type = [];
            },
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data;
            }
        }
    }
</script>
