<style scoped lang="less" type="text/less">
    #initialBalance {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

        /*数据列表*/
        .table-content {
            height: 400px;
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
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      height="100%"
                      border size="mini">
                <el-table-column prop="bill_number" label="账户" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bill_type" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="money" label="期初余额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="sign_date" label="银行未达账项" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="due_date" label="企业未达账项" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="endorse_people" label="状态" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="80"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="addSign(scope.row)"></el-button>
                        </el-tooltip>
                        <!-- <el-tooltip content="新增" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-plus" size="mini"
                                       @click="addSign(scope.row)"></el-button>
                        </el-tooltip> -->
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
                    <el-col :span="12">
                        <el-form-item label="账户号">
                            <el-select v-model="dialogData.endorse_type" placeholder="请选择账户号" clearable>
                                <el-option label="转让背书" value="转让背书"></el-option>
                                <el-option label="非转让背书" value="非转让背书"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="日期">
                            <el-date-picker
                                    v-model="dialogData.start_date"
                                    type="date"
                                    placeholder="起始日期"
                                    value-format="yyyy-MM-dd"
                                    style="width: 100%;">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="期初余额">
                            <el-input v-model="dialogData.endorse_people"></el-input>
                        </el-form-item>
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
                    <el-col :span="12" required>
                        <el-form-item label="类型">
                            <el-select v-model="item.bill_number" placeholder="请选择单据编号" clearable>
                                <el-option label="G234234234" value="G234234234"></el-option>
                                <el-option label="J324324577" value="J324324577"></el-option>
                                <el-option label="Q090680234" value="Q090680234"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="借贷方向">
                            <el-select v-model="item.bill_number" placeholder="请选择借贷方向" clearable>
                                <el-option label="收" value="1"></el-option>
                                <el-option label="付" value="2"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="金额">
                            <el-input v-model="item.money"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="摘要">
                            <el-input v-model="item.issued"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="affirmEndorse">保 存</el-button>
                <el-button type="warning" size="mini" @click="affirmEndorse">启 用</el-button>
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
                tableList: [
                    {id:"1",money:"10000"}
                ], //列表数据
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
                dialogTitle: "期初余额",
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
            }
        },
        methods: {
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //根据条件查询数据
            queryData: function () {
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
                return;
                this.$emit("getCommTable", this.routerMessage);
            },
            //新增
            addSign: function (row) {
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


