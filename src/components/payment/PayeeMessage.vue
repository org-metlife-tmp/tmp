<style scoped lang="less" type="text/less">
    #payeeMessage{
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
            width: 50%;
            margin: 0 auto 10px;
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -12px;
        }

        /*批量上传*/
        .upload-batch{
            width: 80%;
            margin: 20px auto;
            margin-bottom: 30px;
        }
    }
</style>
<style lang="less" type="text/less">
    #payeeMessage {
        .search-setion {
            .el-input-group__append {
                background-color: #409EFF;
                color: #fff;
            }
        }
    }
    .upload-batch{
        .el-input{
            input{
                cursor: default;
            }
        }
    }
</style>

<template>
    <div id="payeeMessage">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addPayee">新增</el-button>
            <!--<el-button type="warning" size="mini" @click="addBatchPayee">批量新增</el-button>-->
            <!--<el-button type="warning" size="mini" @click="">下载</el-button>-->
        </div>

        <!--搜索区-->
        <div class="search-setion">
            <el-input placeholder="请输入收款方名称或账号" class="input-with-select"
                      size="small" v-model="searchData" clearable>
                <el-button type="primary" slot="append" icon="el-icon-search" @click="queryData"></el-button>
            </el-input>
        </div>

        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      border
                      size="mini">
                <el-table-column prop="acc_name" label="收款方账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_no" label="收款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_name" label="开户行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="type" label="属性" :show-overflow-tooltip="true"
                                 :formatter="transitType"></el-table-column>

                <el-table-column
                        label="操作"
                        width="80">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="编辑" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-edit" size="mini"
                                       @click="editPayee(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click="removePayee(scope.row,scope.$index,tableList)"></el-button>
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

        <!--新增/修改 弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="860px" top="76px"
                   :close-on-click-modal="false">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth"
                     :rules="rules" ref="dialogForm">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="收款方账号" prop="acc_no">
                            <el-input v-model="dialogData.acc_no" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="收款方名称" prop="acc_name">
                            <el-input v-model="dialogData.acc_name" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="开户行">
                            <el-col :span="14">
                                <el-select v-model="bankCorrelation.bankTypeName" placeholder="请选择银行大类"
                                           clearable filterable
                                           :filter-method="filterBankType"
                                           @visible-change="clearSearch"
                                           :loading="bankLongding"
                                           @change="bankIsSelect">
                                    <el-option v-for="bankType in bankTypeList"
                                               :key="bankType.name"
                                               :label="bankType.display_name"
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
                                            :key="area.name + '-' + area.top_super"
                                            :value="area.name + '-' + area.top_super">
                                        <span>{{ area.name }}</span><span style="margin-left:10px;color:#bbb">{{ area.top_super }}</span>
                                    </el-option>
                                </el-select>
                            </el-col>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label=" " prop="cnaps_code">
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
                        <el-form-item label="属性" prop="type">
                            <el-select v-model="dialogData.type" placeholder="请选择属性"
                                       filterable clearable>
                                <el-option value="1" label="公司"></el-option>
                                <el-option value="2" label="个人"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="币种"  prop="curr_id">
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
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain
                           @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="confirm">确 定</el-button>
            </span>
        </el-dialog>

        <!--批量新增弹出框-->
        <el-dialog :visible.sync="batchDialog"
                   width="800px" top="180px" title="批量新增"
                   :close-on-click-modal="false">

            <div class="upload-batch">
                <el-input placeholder="请输入流程名查询" class="input-with-select"
                          size="small" readonly clearable>
                    <el-button type="primary" slot="append" @click="">浏览</el-button>
                </el-input>
            </div>

            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="">模板下载</el-button>
                <el-button type="warning" size="mini" plain
                           @click="batchDialog = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "PayeeMessage",
        created: function () {
            this.$emit("transmitTitle", "收款方信息管理");
            this.$emit("getCommTable", this.routerMessage);
        },
        mounted: function(){
            /*下拉框数据*/
            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
            }
            var bankAllTypeList = JSON.parse(window.sessionStorage.getItem("bankAllTypeList"));
            if(bankAllTypeList){
                this.bankAllTypeList = bankAllTypeList;
            }
            //币种
            var currencyList = JSON.parse(window.sessionStorage.getItem("selectCurrencyList"));
            if (currencyList) {
                this.currencyList = currencyList;
            }
        },
        props: ["tableData"],
        data: function(){
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: { //本页数据获取参数
                    optype: "supplier_list",
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
                dialogVisible: false, //弹框相关数据
                dialogTitle: "新增",
                dialogData: {
                    id: "",
                    persist_version: "",
                    acc_no: "",
                    acc_name: "",
                    curr_id: "",
                    type: "",
                    cnaps_code: ""
                },
                //校验规则设置
                rules: {
                    acc_no: {
                        required: true,
                        message: "请输入收款方账号",
                        trigger: "blur"
                    },
                    acc_name: {
                        required: true,
                        message: "请输入收款方名称",
                        trigger: "blur"
                    },
                    cnaps_code: {
                        required: true,
                        message: "请选择开户行",
                        trigger: "change"
                    },
                    type: {
                        required: true,
                        message: "请选择属性",
                        trigger: "change"
                    },
                    curr_id: {
                        required: true,
                        message: "请选择币种",
                        trigger: "change"
                    },
                },
                formLabelWidth: "100px",
                currPayee: {},
                batchDialog: false, //批量弹框相关数据
                bankAllList: [], //银行相关数据
                bankAllTypeList: [], //银行大类全部(不重复)
                bankTypeList: [],
                outTime: "", //银行大类搜索控制
                bankLongding: false,
                areaList: [],
                loading: false,
                bankList: [],
                bankSelect: true,
                bankCorrelation: {
                    area: "",
                    bankTypeName: ""
                },
                currencyList: [], //下拉框数据
            }
        },
        methods: {
            //点击页数 获取当前页数据
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
            //展示格式转换-金额
            transitType: function (row, column, cellValue, index) {
                if(cellValue){
                    return cellValue == 1 ? "公司" : "个人";
                }else{
                     return "";
                }
            },
            //搜索
            queryData: function(){
                this.routerMessage.params.query_key = this.searchData;
                this.routerMessage.params.page_num = 1;
                this.$emit("getCommTable", this.routerMessage);
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
                        this.bankTypeList = this.bankTypeList.filter((item,index,arr) => {
                            for(var i = index+1; i < arr.length; i++){
                                if(item.display_name == arr[i].display_name){
                                    return false;
                                }
                            }
                            return true;
                        });
                    } else {
                        this.bankTypeList = this.bankAllTypeList.slice(0,200);
                    }
                    this.bankLongding = false;
                }, 1200);
            },
            //银行大类展开时重置数据
            clearSearch: function (val) {
                if (this.bankTypeList != this.bankAllTypeList && val) {
                    this.bankTypeList = this.bankAllTypeList.slice(0,200);
                }
            },
            //地区数据
            getAreaList: function (query) {
                if (query && query.trim()) {
                    this.loading = true;
                    this.$axios({
                        url: this.queryUrl + "commProcess",
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
                    var area_code = this.bankCorrelation.area.split("-");
                    var bank_type = this.bankCorrelation.bankTypeName;

                    this.$axios({
                        url: this.queryUrl + "commProcess",
                        method: "post",
                        data: {
                            optype: "bank_list",
                            params: {
                                province: area_code[1],
                                city: area_code[0],
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
            },
            //新增
            addPayee: function(){
                var dialogData = this.dialogData;
                var bankCorrelation = this.bankCorrelation;
                for(var k in dialogData){
                    dialogData[k] = "";
                }
                for(var key in bankCorrelation){
                    bankCorrelation[key] = "";
                }
                //清空校验信息
                if (this.$refs.dialogForm) {
                    this.$refs.dialogForm.clearValidate();
                }
                this.dialogTitle = "新增";
                this.bankSelect = true;
                this.dialogVisible = true;
            },
            //编辑
            editPayee: function(row){
                this.addPayee();
                this.dialogTitle = "修改";
                this.currPayee = row;
                var dialogData = this.dialogData;
                for(var k in dialogData){
                    if(k == "type"){
                        dialogData[k] = row[k] ? row[k] + "" : "";
                    }else if(k == "cnaps_code"){
                        dialogData[k] = row[k];
                        this.bankList = [];
                        this.bankList.push({
                            cnaps_code: row[k],
                            name: row.bank_name
                        })
                    }else{
                        dialogData[k] = row[k];
                    }
                }
                this.bankCorrelation.bankTypeName = row.bank_type;
                this.bankCorrelation.area = row.city + "-" + row.province;
                this.areaList = [];
                this.areaList.push({
                    name: row.city,
                    top_super: row.province,
                    code: row.area_code
                });
                this.bankSelect = false;
            },
            //确认新增或修改
            confirm: function(){
                this.$refs.dialogForm.validate((valid, object) => {
                    if (valid) {
                        var params = this.dialogData;

                        this.$axios({
                            url: this.queryUrl + "normalProcess",
                            method: "post",
                            data: {
                                optype: this.dialogTitle == "新增" ? "supplier_add" : "supplier_chg",
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
                                        this.currPayee[k] = data[k];
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
                        });
                    } else {
                        return false;
                    }
                });
            },
            //删除
            removePayee: function (row, index, rows) {
                this.$confirm('确认删除当前数据吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "supplier_del",
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

                        if (this.pagCurrent < (this.pagTotal / this.pagSize)) { //存在下一页
                            this.$emit("getCommTable", this.routerMessage);
                        } else {
                            if (rows.length == "1" && (this.routerMessage.todo.params.page_num != 1)) { //是当前页最后一条
                                this.routerMessage.todo.params.page_num--;
                                this.$emit("getCommTable", this.routerMessage);
                            } else {
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
            //批量新增
            addBatchPayee: function(){
                this.batchDialog = true;
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
