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
            <el-button type="warning" size="mini" @click="addBatchPayee">批量新增</el-button>
            <el-button type="warning" size="mini" @click="">下载</el-button>
        </div>

        <!--搜索区-->
        <div class="search-setion">
            <el-input placeholder="请输入流程名查询" class="input-with-select"
                      size="small" v-model="searchData" clearable>
                <el-button type="primary" slot="append" icon="el-icon-search" @click=""></el-button>
            </el-input>
        </div>

        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      border
                      height="100%"
                      size="mini">
                <el-table-column prop="workflow_name" label="收款方账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="workflow_name" label="收款方账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="workflow_name" label="开户行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="workflow_name" label="属性" :show-overflow-tooltip="true"></el-table-column>

                <el-table-column
                        label="操作"
                        width="80">
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

        <!--新增 弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="860px" top="76px"
                   :close-on-click-modal="false">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="收款方账号" :label-width="formLabelWidth" prop="acc_no">
                            <el-input v-model="dialogData.acc_no" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="收款方名称" :label-width="formLabelWidth" prop="acc_name">
                            <el-input v-model="dialogData.acc_name" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
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
                    <el-col :span="24">
                        <el-form-item label=" " :label-width="formLabelWidth">
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
                        <el-form-item label="属性" :label-width="formLabelWidth">
                            <el-select v-model="dialogData.org_id" placeholder="请选择机构"
                                       filterable clearable>
                                <el-option value="1">公司</el-option>
                                <el-option value="2">个人</el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain
                           @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="">确 定</el-button>
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
        },
        mounted: function(){
            /*下拉框数据*/
            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
                this.bankTypeList = bankTypeList;
            }
        },
        props: ["tableData"],
        data: function(){
            return {
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
                dialogVisible: false, //弹框相关数据
                dialogTitle: "新增",
                dialogData: {},
                formLabelWidth: "120px",
                batchDialog: false, //批量弹框相关数据
                bankAllList: [], //银行相关数据
                bankTypeList: [],
                areaList: [],
                loading: false,
                bankList: [],
                bankSelect: true,
                bankCorrelation: {
                    area: "",
                    bankTypeName: ""
                },
            }
        },
        methods: {
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
            },
            //新增
            addPayee: function(){
                this.dialogVisible = true;
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
