<style scoped lang="less" type="text/less">
    #initialBalance {
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

            .line {
                text-align: center;
            }

            /*时间控件*/
            .el-date-editor {
                width: 100%;
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

        /*数据列表*/
        .table-content {
            height: 340px;
        }

        /*弹框*/
        .form-small-title{
            font-weight: bold;
            padding-bottom: 6px;

            span{
                display:inline-block;
                width: 4px;
                height: 16px;
                background-color: orange;
                margin-right: 6px;
                vertical-align: middle;
            }
        }
        .split-form {
            width: 100%;
            height: 26px;
            border-bottom: 1px solid #eee;
            margin-bottom: 10px;
            padding-bottom: 1px;
            text-align: right;
        }
    }
</style>
<style lang="less" type="text/less">
    #initialBalance {
        .el-dialog__wrapper {
            .el-dialog__body {
                max-height: 440px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="initialBalance">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addSign">新增</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.pay_mode" placeholder="银行承兑汇票"
                                       filterable clearable>
                                <el-option v-for="(name,k) in payModeList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input size="mini" v-model="searchData.pay_query_key" clearable
                                      placeholder="请输入被背书人"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.bank_type" placeholder="请选择开票银行"
                                       clearable filterable
                                       :filter-method="filterBankType"
                                       :loading="bankLongding"
                                       @visible-change="clearSearch">
                                <el-option v-for="bankType in bankTypeList"
                                           :key="bankType.name"
                                           :label="bankType.name"
                                           :value="bankType.code">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.recv_query_key" clearable
                                      placeholder='输入票据编号以“|”分隔'></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
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
                      height="100%"
                      border size="mini">
                <el-table-column prop="bill_number" label="票据编号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bill_type" label="票据类型" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="date_draft" label="出票日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="money" label="金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="sign_date" label="签收日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="due_date" label="票据到期日" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="endorse_people" label="被背书人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="endorse_type" label="背书类型" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="endorse_date" label="背书日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="50"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookBill(scope.row)"></el-button>
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
        <!--新增弹出框-->
        <el-dialog title=""
                   :visible.sync="dialogVisible"
                   width="860px" top="76px"
                   :close-on-click-modal="false">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="24" class="form-small-title"
                            style="border-bottom: 1px solid #eee;margin-bottom: 10px;">
                        <span></span>被背书人信息
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="被背书人">
                            <el-input v-model="dialogData.endorse_people"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="背书类型">
                            <el-select v-model="dialogData.endorse_type" placeholder="请选择背书类型" clearable>
                                <el-option label="转让背书" value="转让背书"></el-option>
                                <el-option label="非转让背书" value="非转让背书"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>

                    <el-col :span="24" class="form-small-title" style="margin-bottom:-22px"><span></span>票据信息</el-col>
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
                    <el-col :span="12" required>
                        <el-form-item label="单据编号">
                            <el-select v-model="item.bill_number" placeholder="请选择单据编号" clearable>
                                <el-option label="G234234234" value="G234234234"></el-option>
                                <el-option label="J324324577" value="J324324577"></el-option>
                                <el-option label="Q090680234" value="Q090680234"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="出票日">
                            <el-input v-model="item.date_draft"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="到期日">
                            <el-input v-model="item.due_date"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="金额">
                            <el-input v-model="item.money"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="签发行">
                            <el-input v-model="item.issued"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24" class="form-small-title"
                            style="border-bottom: 1px solid #eee;margin-bottom: 10px;">
                        <span></span>背书事项
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label-width="10px">
                            <el-input v-model="dialogData.memo"
                                      type="textarea"
                                      :rows="3"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="affirmEndorse">背 书</el-button>
            </span>
        </el-dialog>
        <!--查看弹出框-->
        <el-dialog title=""
                   :visible.sync="lookDialogVisible"
                   width="860px" top="76px"
                   :close-on-click-modal="false">
            <h1 slot="title" v-text="lookDialogTitle" class="dialog-title"></h1>
            <el-form :model="lookDialog" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="24" class="form-small-title"
                            style="border-bottom: 1px solid #eee;margin-bottom: 10px;">
                        <span></span>被背书人信息
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="被背书人">
                            <el-input v-model="lookDialog.endorse_people" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="背书类型">
                            <el-select v-model="lookDialog.endorse_type" placeholder="请选择背书类型"
                                       disabled clearable>
                                <el-option label="转让背书" value="转让背书"></el-option>
                                <el-option label="非转让背书" value="非转让背书"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>

                    <el-col :span="24" class="form-small-title"
                            style="border-bottom: 1px solid #eee;margin-bottom: 10px;">
                        <span></span>票据信息
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="12" required>
                        <el-form-item label="单据编号">
                            <el-select v-model="lookDialog.bill_number" placeholder="请选择单据编号"
                                       clearable disabled>
                                <el-option label="G234234234" value="G234234234"></el-option>
                                <el-option label="J324324577" value="J324324577"></el-option>
                                <el-option label="Q090680234" value="Q090680234"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="出票日">
                            <el-input v-model="lookDialog.date_draft" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="到期日">
                            <el-input v-model="lookDialog.due_date" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="金额">
                            <el-input v-model="lookDialog.money" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="签发行">
                            <el-input v-model="lookDialog.issued" disabled></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="24" class="form-small-title"
                            style="border-bottom: 1px solid #eee;margin-bottom: 10px;">
                        <span></span>背书事项
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label-width="10px">
                            <el-input v-model="lookDialog.memo" type="textarea" :rows="3" disabled></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
            </span>
        </el-dialog>
    </div>
</template>

<script>

    export default {
        name: "InitialBalance",
        created: function () {
            this.$emit("transmitTitle", "期初余额");
            // this.$emit("getCommTable", this.routerMessage);
        },
        components: {},
        mounted: function () {
            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
            }
            var bankAllTypeList = JSON.parse(window.sessionStorage.getItem("bankAllTypeList"));
            if (bankAllTypeList) {
                this.bankAllTypeList = bankAllTypeList;
            }
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "dbtbatch_viewlist",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                searchData: { //搜索条件
                    pay_mode: "",
                    pay_query_key: "",
                    bank_type: "",
                    recv_query_key: ""
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                payModeList: {
                    "1": "银行承兑汇票"
                },
                bankAllList: [], //银行大类全部
                bankAllTypeList: [], //银行大类全部(不重复)
                bankTypeList: [], //银行大类
                outTime: "", //银行大类搜索控制
                bankLongding: false,
                dialogVisible: false, //弹框数据
                dialogData: {
                    endorse_people: "",
                    endorse_type: "",
                    memo: "",
                },
                dialogTitle: "新增",
                items: [
                    {
                        bill_number: "",
                        date_draft: "",
                        due_date: "",
                        money: "",
                        issued: "",
                        $id: 1
                    }
                ],
                formLabelWidth: "100px",
                lookDialogVisible: false, //查看弹框
                lookDialog: {
                    endorse_people: "",
                    endorse_type: "",
                    memo: "",
                    bill_number: "",
                    date_draft: "",
                    due_date: "",
                    money: "",
                    issued: "",
                },
                lookDialogTitle: "查看",
            }
        },
        methods: {
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
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;

                for (var k in searchData) {
                    this.routerMessage.params[k] = searchData[k];
                }
                this.routerMessage.params.page_num = 1;
                return;
                this.$emit("getCommTable", this.routerMessage);
            },
            //换页后获取数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                return;
                this.$emit("getCommTable", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.params.page_size = val;
                this.routerMessage.params.page_num = 1;
                return;
                this.$emit("getCommTable", this.routerMessage);
            },
            //新增
            addSign: function () {
                var dialogData = this.dialogData;
                for(var k in dialogData){
                    dialogData[k] = "";
                }
                this.items = [
                    {
                        bill_number: "",
                        date_draft: "",
                        due_date: "",
                        money: "",
                        issued: "",
                        $id: 1
                    }
                ];
                this.dialogVisible = true;
            },
            //新增票据信息
            addAccount: function () {
                var item = {
                    bill_number: "",
                    date_draft: "",
                    due_date: "",
                    money: "",
                    issued: "",
                    $id: Date.now()
                };
                this.items.push(item);
            },
            //删除票据信息
            removeAccount: function (item) {
                var index = this.items.indexOf(item);
                if (index != -1) {
                    this.items.splice(index, 1);
                }
            },
            //确认背书
            affirmEndorse: function(){
                var dialogData = this.dialogData;
                var items = this.items;
                items.forEach((item) => {
                    var currItem = {};
                    for(var k in dialogData){
                        currItem[k] = dialogData[k];
                    }
                    for(var key in item){
                        if(key != "$id"){
                            currItem[key] = item[key];
                        }
                    }
                    this.tableList.push(currItem);
                });
                this.dialogVisible = false;
            },
            //查看
            lookBill: function (row) {
                var lookDialog = this.lookDialog;
                for(var k in row){
                    lookDialog[k] = row[k];
                }
                this.lookDialogVisible = true;
            },

            //编辑
            editBill: function (row) {
                this.$router.push({
                    name: "LotMakeBill",
                    params: {
                        id: row.id,
                        batchno: row.batchno
                    }
                });
            },
        },
        computed: {
            showDel: function () {
                if (this.items.length > 1) {
                    return true;
                } else {
                    return false;
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


