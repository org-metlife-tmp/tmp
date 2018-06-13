<style scoped lang="less" type="text/less">
    #moveWithhold{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion {
            text-align: right;
            height: 42px;
            overflow: hidden;
            transition: height 1s;
        }
        @media (max-width: 1340px) {
            .search-setion {
                text-align: left;
            }
        }
        .search-setion.show-more {
            height: 140px;
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
            height: 340px;
            transition: height 1s;
        }
        .is-small {
            height: 50%;
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

        /*弹框*/
        .el-dialog{
            .el-form{
                .el-form-item{
                    margin-bottom: 0;
                }
            }
        }
        .form-small-title{
            font-weight: bold;
            span{
                display:inline-block;
                width: 4px;
                height: 16px;
                background-color: orange;
                margin-right: 6px;
                vertical-align: middle;
            }
        }
    }
</style>
<style lang="less" type="text/less">
    #moveWithhold {
        .search-setion {
            .el-form {
                .el-form-item__content {
                    width: 180px;
                }
            }
        }
        .el-dialog__wrapper {
            .el-dialog__body {
                height: 400px;
                overflow-y: scroll;
            }
        }
    }
</style>

<template>
    <div id="moveWithhold">
        <!--搜索区-->
        <div :class="['search-setion',{'show-more':showMore}]">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="7">
                        <el-form-item label="投保单号">
                            <el-input v-model="searchData.preinsure_bill_no" clearable></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="对方账号">
                            <el-input v-model="searchData.settle_or_merchant_acc_no" clearable></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="对方户名">
                            <el-input v-model="searchData.settle_or_merchant_acc_name" clearable></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="3">
                        <el-button type="primary" plain @click="showMore = !showMore" size="mini">
                            高级<i
                                :class="['el-icon--right',{'el-icon-arrow-down':!showMore},{'el-icon-arrow-right':showMore}]"></i>
                        </el-button>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="开户银行">
                            <el-select v-model="searchData.customer_bank" placeholder="请选择银行"
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
                        <el-form-item label="支付渠道">
                            <el-select v-model="searchData.channel_code" placeholder="请选择支付渠道"
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
                        <el-form-item label="开始日期">
                            <el-date-picker type="date" placeholder="选择日期" v-model="searchData.start_date"
                                            style="width: 100%;"
                                            format="yyyy 年 MM 月 dd 日"
                                            value-format="yyyy-MM-dd"></el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="结束日期">
                            <el-date-picker type="date" placeholder="选择日期" v-model="searchData.end_date"
                                            style="width: 100%;"
                                            format="yyyy 年 MM 月 dd 日"
                                            value-format="yyyy-MM-dd"></el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="交易状态">
                            <el-select v-model="searchData.trade_status" placeholder="请选择交易状态"
                                       filterable clearable>
                                <el-option v-for="(name,k) in payStatList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7" style="height:1px"></el-col>
                    <el-col :span="7" style="text-align:right">
                        <el-form-item>
                            <el-button type="primary" plain @click="clearSearData">清空</el-button>
                            <el-button type="primary" plain @click="queryData">搜索</el-button>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section :class="['table-content',{'is-small':showMore}]">
            <el-table :data="tableList"
                      border
                      size="mini"
                      height="100%">
                <el-table-column prop="bill_no" label="单据号" :show-overflow-tooltip="true" width="190"></el-table-column>
                <el-table-column prop="serial_no" label="流水号" :show-overflow-tooltip="true" width="190"></el-table-column>
                <el-table-column prop="biz_type" :formatter="transitionType"
                                 label="业务类型" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="settle_or_merchant_acc_no" label="对方账号" width="100"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="settle_or_merchant_acc_name" label="对方户名" width="100"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_type_name" label="开户银行"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="preinsure_bill_no" label="投保单号"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="insure_bill_no" label="保单号" width="190"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="channel_name" label="支付渠道"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="trade_status" label="交易状态"
                                 :formatter="transitionStatus"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="date_time" label="创建日期" width="120"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="real_date_time" label="支付日期" width="120"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="channel_msg" label="状态描述" width="100"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="50"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click="lookParticular(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </section>
        <!--分页部分-->
        <div class="botton-pag">
            <el-pagination
                    background :pager-count="5"
                    layout="sizes , prev, pager, next, jumper"
                    :page-size="pagSize" :total="pagTotal"
                    :page-sizes="[8, 50, 100, 500]"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange"
                    :current-page="pagCurrent">
            </el-pagination>
        </div>
        <!--详情弹出框-->
        <el-dialog title="详情"
                   :visible.sync="dialogVisible"
                   width="800px" top="76px"
                   :close-on-click-modal="false">
            <el-form :model="dialogData" size="mini">
                <el-row>
                    <el-col :span="24" class="form-small-title"><span></span>支付信息</el-col>
                    <el-col :span="12" v-for="payItem in payMessage" :key="payItem.title">
                        <el-form-item :label-width="formLabelWidth">
                            <label slot="label">{{ payItem.title }}：</label>
                            <div>{{ payItem.content }}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" style="height:28px"></el-col>
                    <el-col :span="12" v-for="payItem in payMessage2" :key="payItem.title">
                        <el-form-item :label-width="formLabelWidth">
                            <label slot="label">{{ payItem.title }}：</label>
                            <div>{{ payItem.content }}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24" class="form-small-title"><span></span>业务信息</el-col>
                    <el-col :span="12" v-for="business in businessMessage" :key="business.title">
                        <el-form-item label="保单号：" :label-width="formLabelWidth">
                            <label slot="label">{{ business.title }}：</label>
                            <div>{{ business.content }}</div>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" style="height:28px"></el-col>
                    <el-col :span="12" v-for="business in businessMessage2" :key="business.title">
                        <el-form-item label="保单号：" :label-width="formLabelWidth">
                            <label slot="label">{{ business.title }}：</label>
                            <div>{{ business.content }}</div>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain
                           @click="dialogVisible = false">取 消</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "MoveWithhold",
        created:function(){
            this.$emit("transmitTitle", "移动展业实时代扣");
            this.$emit("getCommTable", this.routerMessage);
        },
        mounted:function(){
            /*获取下拉框数据*/
            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
                this.bankTypeList = bankTypeList;
            }
            //支付渠道
            var channelList = JSON.parse(window.sessionStorage.getItem("channelList"));
            if (channelList) {
                this.channelList = channelList;
            }
            //交易状态
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.PayStatus) {
                this.payStatList = constants.PayStatus;
            }
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: { //本页数据获取参数
                    optype: "ydssdk_list",
                    params: {
                        page_size: 8,
                        page_num: 1
                    }
                },
                searchData: {}, //搜索数据
                showMore: false,
                pagSize: 1, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                tableList: [], //表格数据
                bankAllList: [], //下拉框数据
                bankTypeList: [],
                channelList: [],
                payStatList: {},
                dialogVisible:false, //弹框数据
                dialogData: {},
                formLabelWidth: "140px",

                businessMessage: [],
                payMessage: [],
                payMessage2: [],
                businessMessage2: []
            }
        },
        methods: {
            //展示格式转换-业务类型
            transitionType: function (row, column, cellValue, index) {
                var catgList = JSON.parse(window.sessionStorage.getItem("catgList"));
                for (var i = 0; i < catgList.length; i++) {
                    if (catgList[i].code == "biz_type") {
                        return catgList[i].items[cellValue];
                    }
                }
            },
            //展示格式转换-状态
            transitionStatus: function (row, column, cellValue, index) {
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if (constants.PayStatus) {
                    return constants.PayStatus[cellValue];
                }
            },
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                for (var key in searchData) {
                    this.routerMessage.params[key] = searchData[key];
                }
                this.$emit("getCommTable", this.routerMessage);
            },
            //清空查询条件
            clearSearData:function(){
                var searchData = this.searchData;
                for(var k in searchData){
                    searchData[k] = "";
                }
            },
            //换页后获取数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getCommTable", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange:function(val){
                this.routerMessage.params = {
                    page_size: val,
                    page_num: 1
                };
                this.$emit("getCommTable", this.routerMessage);
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
            /*弹框相关*/
            //查看详细信息
            lookParticular: function(row){
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "ydssdk_detail",
                        params: {
                            id: row.id
                        }
                    }
                }).then((result) => {
                    var data = result.data.data;
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else {
                        //支付信息数据设置
                        var payLabel = [
                            {key: "channel_code", value: "渠道名称"},
                            {key: "bank_serial_no", value: "交易流水号"},
                            {key: "haha", value: "支付方式"},
                            {key: "amount", value: "交易金额"},
                            {key: "date_time", value: "创建时间"},
                            {key: "real_date_time", value: "支付时间"},
                            {key: "trade_status", value: "支付状态"}
                        ];
                        var payMessage = [];
                        payLabel.forEach((item) => {
                            var current = {};
                            current.title = item.value;
                            //展示格式转换
                            if (item.key == "trade_status") {
                                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                                if (constants.PayStatus) {
                                    current.content = constants.PayStatus[data[item.key]];
                                } else {
                                    current.content = data[item.key];
                                }
                            } else if (item.key == "channel_code") {
                                current.content = data.channel_name;
                            } else if (item.key == "channel_interface_code") {
                                current.content = data.channel_interface_name;
                            }else if (item.key == "haha") {
                                current.content = "实时代付";
                            } else {
                                current.content = data[item.key];
                            }

                            payMessage.push(current);
                        })
                        this.payMessage = payMessage;

                        var payLabel2 = [
                            {key: "channel_status", value: "渠道响应码"},
                            {key: "channel_msg", value: "渠道响应信息"},
                            {key: "trade_status", value: "平台响应码"},
                            {key: "trade_msg", value: "平台响应信息"},
                            // {key: "channel_interface_code", value: "支付渠道原子接口"},
                            {key: "memo", value: "备注"}
                        ];
                        var payMessage2 = [];
                        payLabel2.forEach((item) => {
                            var current = {};
                            current.title = item.value;
                            //展示格式转换
                            if (item.key == "trade_status") {
                                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                                if (constants.PayStatus) {
                                    current.content = constants.PayStatus[data[item.key]];
                                } else {
                                    current.content = data[item.key];
                                }
                            } else if (item.key == "channel_code") {
                                current.content = data.channel_name;
                            } else if (item.key == "channel_interface_code") {
                                current.content = data.channel_interface_name;
                            }else if (item.key == "haha") {
                                current.content = "实时代付";
                            } else {
                                current.content = data[item.key];
                            }

                            payMessage2.push(current);
                        })
                        this.payMessage2 = payMessage2;

                        //业务信息数据设置
                        var businessLabel = [
                            {key: "lala", value: "系统来源"},
                            {key: "biz_type", value: "业务类型"},
                            {key: "bill_no", value: "单据号"},
                            {key: "serial_no", value: "流水号"},
                            // {key: "preinsure_bill_no", value: "投保单号"},
                            {key: "insure_bill_no", value: "保单号"},
                            {key: "business_no", value: "业务单号"},
                            {key: "bill_org_name", value: "保单所属机构"},
                            {key: "insure_type", value: "险种大类"},
                            {key: "insure_code", value: "险种代码"},
                            {key: "insure_name", value: "险种名称"},
                            {key: "op_code", value: "操作员编码"},
                            {key: "op_name", value: "操作员姓名"},
                            {key: "op_org_name", value: "操作员所属机构"},
                            {key: "sales_channel", value: "销售渠道"},
                            {key: "customer_name", value: "客户姓名"},
                            {key: "customer_acc", value: "客户账号"},
                            {key: "bank_type_name", value: "开户银行"}
                        ];
                        var businessMessage = [];
                        businessLabel.forEach((item) => {
                            var current = {};
                            current.title = item.value;
                            if (item.key == "biz_type") {
                                current.content = data.biz_type_name;
                            }else if(item.key == "insure_type"){
                                current.content = data.insure_type_name;
                            }else if(item.key == "sales_channel"){
                                current.content = data.sales_channel_name;
                            }else if(item.key == "cert_type"){
                                current.content = data.cert_type_name;
                            }else if(item.key == "lala"){
                                current.content = "个险核心";
                            }else{
                                current.content = data[item.key];
                            }
                            businessMessage.push(current);
                        })
                        this.businessMessage = businessMessage;

                        var businessLabel2 = [
                            {key: "cert_type", value: "证件类型"},
                            {key: "cert_no", value: "证件号"},
                            {key: "customer_phone", value: "手机号"}, //
                            // {key: "repet_count", value: "重发次数"},
                            // {key: "settle_or_merchant_acc_name", value: "账户名称"},
                            // {key: "settle_or_merchant_acc_no", value: "账户编号"},
                            // {key: "due_date", value: "应付日期"},
                            // {key: "org_seg", value: "机构段"},
                            // {key: "detail_seg", value: "明细段"}
                        ];
                        var businessMessage2 = [];
                        businessLabel2.forEach((item) => {
                            var current = {};
                            current.title = item.value;
                            if (item.key == "biz_type") {
                                current.content = data.biz_type_name;
                            }else if(item.key == "insure_type"){
                                current.content = data.insure_type_name;
                            }else if(item.key == "sales_channel"){
                                current.content = data.sales_channel_name;
                            }else if(item.key == "customer_bank"){
                                current.content = data.customer_name;
                            }else if(item.key == "cert_type"){
                                current.content = data.cert_type_name;
                            }else if(item.key == "lala"){
                                current.content = "个险核心";
                            }else{
                                current.content = data[item.key];
                            }
                            businessMessage2.push(current);
                        })
                        this.businessMessage2 = businessMessage2;

                        this.dialogData = data;
                        this.dialogVisible = true;
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
            }
        }
    }
</script>
