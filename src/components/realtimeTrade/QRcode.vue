<style scoped lang="less" type="text/less">
    #QRcode{
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
            height: 76%;
            transition: height 1s;
        }
        .is-small {
            height: 44%;
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
    #QRcode {
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
    <div id="QRcode">
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
                      max-height="100%">
                <el-table-column prop="serial_no" label="流水号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="biz_type" :formatter="transitionType"
                                 label="业务类型" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="settle_or_merchant_acc_no" label="对方账号"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="trade_status" label="状态"
                                 :formatter="transitionStatus"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作"
                        width="50">
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
                    background
                    layout="prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    @current-change="getPageData"
                    :pager-count="5">
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
                        <el-form-item label="保单号：" :label-width="formLabelWidth">
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
        name: "QRcode",
        created:function(){
            this.$emit("transmitTitle", "移动展业二维码");
            this.$emit("getCommTable", this.routerMessage);
        },
        mounted:function(){
            /*获取下拉框数据*/
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
                    optype: "ydsmsk_list",
                    params: {
                        page_size: 8,
                        page_num: 1
                    }
                },
                searchData: {}, //搜索数据
                showMore: false,
                pagSize: 1, //分页数据
                pagTotal: 1,
                tableList: [], //表格数据
                bankAllList: [], //下拉框数据
                bankTypeList: [],
                channelList: [],
                payStatList: {},
                dialogVisible:false, //弹框数据
                dialogData: {},
                formLabelWidth: "140px",

                businessMessage: [],
                payMessage: []
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
            getPageData: function (currPage) {
                this.routerMessage.params.page_num = currPage;
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
                //支付信息数据设置
                var payLabel = [
                    {key:"create_date",value:"创建日期"},
                    {key:"bank_serial_no",value:"银行交互流水号"},
                    {key:"trade_status",value:"支付状态"},
                    {key:"channel_code",value:"支付渠道编码"},
                    {key:"channel_interface_code",value:"支付渠道原子接口"},
                    {key:"trade_status",value:"平台响应码"},
                    {key:"trade_msg",value:"平台响应信息"},
                    {key:"channel_status",value:"渠道响应码"},
                    {key:"channel_msg",value:"渠道响应信息"},
                    {key:"real_date",value:"实付日期"},
                    {key:"memo",value:"备注"}
                ];
                var payMessage = [];
                payLabel.forEach((item) => {
                    var current = {};
                    current.title = item.value;
                    current.content = row[item.key];
                    payMessage.push(current);
                })
                this.payMessage = payMessage;

                //业务信息数据设置
                var businessLabel = [
                    {key:"biz_type",value:"业务类型"},
                    {key:"bill_no",value:"单据号"},
                    {key:"serial_no",value:"流水号"},
                    {key:"preinsure_bill_no",value:"投保单号"},
                    {key:"insure_bill_no",value:"保单号"},
                    {key:"cert_type",value:"证件类型"},
                    {key:"cert_no",value:"证件号"},
                    {key:"insure_type",value:"险种大类"},
                    {key:"insure_code",value:"险种代码"},
                    {key:"insure_name",value:"险种名称"},
                    {key:"op_name",value:"操作员"},
                    {key:"op_org",value:"操作员所属机构"},
                    {key:"sales_channel",value:"销售渠道"},
                    {key:"customer_acc",value:"客户账号"},
                    {key:"customer_name",value:"客户姓名"},
                    {key:"customer_bank",value:"开户银行"},
                    {key:"amount",value:"金额"},
                    {key:"business_no",value:"业务单号"},
                    {key:"org_seg",value:"机构段"},
                    {key:"detail_seg",value:"明细段"},
                    {key:"repet_count",value:"重发次数"},
                    {key:"settle_or_merchant_acc_name",value:"账户名称"},
                    {key:"settle_or_merchant_acc_no",value:"账户编号"},
                    {key:"due_date",value:"应付日期"}
                ];
                var businessMessage = [];
                businessLabel.forEach((item) => {
                    var current = {};
                    current.title = item.value;
                    current.content = row[item.key];
                    businessMessage.push(current);
                })
                this.businessMessage = businessMessage;

                this.dialogData = row;
                this.dialogVisible = true;
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
