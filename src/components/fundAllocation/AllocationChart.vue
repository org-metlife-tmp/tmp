<style scoped lang="less" type="text/less">
    #allocationChart {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*顶部选择按钮*/
        .button-list-left {
            position: absolute;
            top: -56px;
            left: -20px;
        }

        /*弹框高度控制*/
        .dialog-content{
            overflow-y: auto;
            max-height: 440px;
        }

        /*用户-用户组切换*/
        .company-bank {
            position: absolute;
            top: -20px;
            right: -48px;
            width: 28px;
            height: 140px;

            li {
                width: 88%;
                line-height: 22px;
                height: 46px;
                font-size: 14px;
                border: 1px solid #00B3ED;
                background-color: #fff;
                color: #00B3ED;
                cursor: pointer;
                margin-bottom: 6px;
                position: relative;
            }

            .current-select {
                background-color: #00B3ED;
                color: #fff;
            }

            .current-select:before {
                border: 5px solid #00B3ED;
                border-top-color: transparent;
                border-bottom-color: transparent;
                border-left: none;
            }

            li:hover {
                background-color: #00B3ED;
                color: #fff;
            }

            li:before {
                content: "";
                display: block;
                position: absolute;
                border: none;
                top: 26px;
                left: -5px;
            }

            li:hover:before {
                border: 5px solid #00B3ED;
                border-top-color: transparent;
                border-bottom-color: transparent;
                border-left: none;
            }
        }

        /*时间控件*/
        .el-date-editor {
            width: 300px;
            float: left;
        }

        /*图表/列表切换按钮*/
        .switchover {
            position: absolute;
            bottom: -12px;
            left: 10px;
            text-align: left;
            margin: -22px 0 20px 0;
            z-index: 10;
            .el-button {
                background-color: #fff;
                border-color: #e8e8e8;
                padding: 4px 15px;
            }
            .el-button:nth-child(1) {
                border-radius: 10px 0 0 10px;
            }
            .el-button:nth-child(2) {
                border-radius: 0 10px 10px 0;
            }
            .get-img, .get-list {
                display: inline-block;
                width: 20px;
                height: 20px;
                background-image: url(../../assets/icon_common.png);
                border: none;
                padding: 0;
                vertical-align: middle;
            }
            .get-img {
                background-position: -476px -2px;
            }
            /*弹框-列表按钮*/
            .get-list {
                background-position: -393px -2px;
            }

            .active {
                background-color: #fafafa;
            }
        }

        /*图表内容*/
        .img-content{
            width: 100%;
            height: 100%;
            border: 1px solid #f1f1f1;

            .img-left {
                position: relative;
                float: left;
                height: 100%;
                width: 30%;
                background-color: #F7F7F6;

                h2 {
                    color: #C0BBB5;
                    margin-bottom: 140px;
                }

                .left-money {
                    font-size: 18px;
                    color: #FF4800;
                }

                .left-pie {
                    position: absolute;
                    top: 0;
                    left: 0;
                    width: 100%;
                    height: 100%;
                    overflow: hidden;

                    > div {
                        width: 100%;
                        height: 600px;
                        margin: 80px 0 0 2px;
                    }
                }
            }
            .img-right{
                float:right;
                height: 100%;
                width: 70%;
                ul {
                    width: 100%;
                    height: 100%;

                    .pie-list {
                        position: relative;
                        box-sizing: border-box;
                        width: 100%;
                        height: 20%;
                        border-top: 1px solid #ccc;

                        /*序号*/
                        .corner-mark {
                            width: 0;
                            height: 0;
                            border: 25px solid #69DAFF;
                            position: absolute;
                            top: 0;
                            left: 0px;
                            border-right-color: rgba(0, 0, 0, 0);
                            border-bottom-color: rgba(0, 0, 0, 0);
                            color: #fff;
                            text-indent: -24px;
                            line-height: 0px;
                        }

                        /*名称和金额*/
                        .left-text {
                            float: left;
                            width: 60%;
                            height: 100%;
                            text-align: left;
                            padding: 20px 0 0 60px;
                            box-sizing: border-box;

                            span {
                                display: inline-block;
                                font-size: 18px;
                                color: #FF4800;
                                margin-top: 6px;
                            }
                        }

                        /*饼图*/
                        .right-echart {
                            float: right;
                            width: 40%;
                            height: 100%;
                        }
                    }
                }
            }
        }

        /*列表内容*/
        .list-content{
            /*汇总数据*/
            .allData {
                height: 36px;
                line-height: 36px;
                width: 100%;
                background-color: #F8F8F8;
                border: 1px solid #ebeef5;
                border-top: none;
                box-sizing: border-box;
                text-align: right;

                /*汇总数字*/
                .numText {
                    color: #FF5800;
                    margin-right: 10px;
                }
            }
        }

    }
</style>
<template>
    <div id="allocationChart">
        <el-date-picker v-show="!showImg" style="margin-bottom:10px"
                v-model="dateValue"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
                value-format="yyyy-MM-dd"
                size="mini" clearable
                unlink-panels
                :picker-options="pickerOptions"
                @change="getDateData">
        </el-date-picker>
        <!--顶部选择按钮-->
        <div class="button-list-left">
            <el-button type="primary" plain size="mini" @click="showDialog('dialogVisible')">全部公司</el-button>
            <el-button type="primary" plain size="mini" @click="showDialog('bankDialogVisible')">全部银行</el-button>
        </div>
        <!-- 公司/账号 选择-->
        <div class="company-bank">
            <ul>
                <li :class="{'current-select':btActive}"
                    @click="isCompany">公司
                </li>
                <li :class="{'current-select':!btActive}"
                    @click="isNumber">账号
                </li>
            </ul>
        </div>
        <!--图表/列表切换按钮-->
        <div class="switchover">
            <el-button-group>
                <el-button type="primary" size="mini" :class="{active:showImg}"
                           @click="changeListOrChart">
                    <i class="get-img"></i>
                </el-button>
                <el-button type="primary" size="mini" :class="{active:!showImg}"
                           @click="changeListOrChart">
                    <i class="get-list"></i>
                </el-button>
            </el-button-group>
        </div>
        <!--图表内容-->
        <section class="img-content" v-show="showImg">
            <div class="img-left">
                <h2>TOP5</h2>
                <span>汇总金额</span>
                <div class="left-money">￥{{ pieTotalAmount }}</div>
                <!--饼图-->
                <div class="left-pie">
                    <div>
                        <CakePicture :pieData="pieData"></CakePicture>
                    </div>
                </div>
            </div>
            <div class="img-right">
                <ul>
                    <li v-for="(item,index) in pieList" :key="item.pay_account_org_id"
                        class="pie-list">
                        <span class="corner-mark">{{ index + 1 }}</span>
                        <div class="left-text">
                            <p v-show="btActive">{{ item.pay_account_org_name }}</p>
                            <p v-show="!btActive">{{ item.pay_account_no }}({{ item.pay_account_name }})</p>
                            <span>￥{{ transitionMoney(item.amount) }}</span>
                        </div>
                        <div class="right-echart">
                            <MoreCake :pieData="item.$pieData" :classIndex="index"></MoreCake>
                        </div>
                    </li>
                </ul>
            </div>
        </section>
        <!--列表内容-->
        <section class="list-content" v-show="!showImg">
            <el-table :data="tableList" height="325"
                      border size="mini">
                <el-table-column prop="pay_account_org_name" label="所属公司" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_account_no" label="被下拨账户" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_account_bank" label="开户行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="amount" label="下拨总金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="percentage" label="金额占比" :show-overflow-tooltip="true"></el-table-column>
            </el-table>
            <div class="allData">
                <span>总金额：</span>
                <span v-text="totalAmount" class="numText"></span>
                <span>总占比：</span>
                <span class="numText">100%</span>
            </div>
        </section>
        <!--选择公司弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   class="comDialog"
                   width="810px" title="请选择公司"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">请选择公司</h1>
            <div class="dialog-content">
                <el-tree :data="orgTreeList"
                         node-key="org_id"
                         :check-strictly="true"
                         highlight-current
                         accordion show-checkbox
                         :expand-on-click-node="false"
                         :default-expanded-keys="expandData"
                         ref="orgTree">
                <span class="custom-tree-node" slot-scope="{ node, data }">
                    <span>{{ node.data.name }}</span>
                </span>
                </el-tree>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="dialogVisible=false">取 消</el-button>
                <el-button type="warning" size="mini" @click="queryByOrg">确 定</el-button>
            </span>
        </el-dialog>
        <!--选择银行弹出框-->
        <el-dialog :visible.sync="bankDialogVisible"
                   width="600px" title="请选择账户性质"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">请选择银行</h1>
            <div class="dialog-content">
                <el-checkbox :indeterminate="insureIndeter" v-model="insureAll"
                             @change="insureAllChange">全选
                </el-checkbox>
                <el-checkbox-group v-model="selectBank" @change="insureChange">
                    <el-checkbox v-for="bank in bankTypeList"
                                 :key="bank.name"
                                 :label="bank.code"
                                 style="display:block;margin-left:15px">
                        {{bank.name}}
                    </el-checkbox>
                </el-checkbox-group>
            </div>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" plain @click="bankDialogVisible=false">取 消</el-button>
                <el-button type="warning" size="mini" @click="queryByBank">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import CakePicture from "../echarts/CakePicture.vue";
    import MoreCake from "../echarts/MoreCake.vue";
    export default {
        name: "AllocationChart",
        created: function () {
            this.$emit("transmitTitle", "自动下拨图表");
            // this.$emit("getCommTable", this.routerMessage);

            //机构树
            var orgTreeList = JSON.parse(window.sessionStorage.getItem("orgTreeList"));
            if (orgTreeList) {
                this.orgTreeList.push(orgTreeList);
            }
            //银行
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankTypeList = bankTypeList;
            }
            this.getChartAxios('org');
        },
        props: ["tableData"],
        components: {
            CakePicture: CakePicture,
            MoreCake: MoreCake
        },
        data: function () {
            return {
                routerMessage: {
                    optype: "allocreport_orglist",
                    params: {
                        start_date:"",
                        end_date: "",  
                        pay_account_org_id:[],
                        pay_bank_cnaps:[]
                    }
                },
                tableList: [], //列表数据
                totalAmount: "0.00",
                pieTotalAmount: "0.00",
                dialogVisible: false, //弹框数据
                orgTreeList: [],
                expandData: [],
                bankDialogVisible: false,
                bankTypeList:[], //弹框-银行
                selectBank: [],
                insureIndeter: false,
                insureAll: false,
                btActive: true, //右侧按钮状态控制
                dateValue: "", //时间选择器
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
                showImg: true, //图表/列表显示控制
                pieData: [],
                pieList: [],
            }
        },
        methods: {
            //筛选条件弹框弹出
            showDialog: function (type) {
                this[type] = true;
            },
            //银行全选
            insureAllChange: function (value) {
                var insureTypeList = [];
                var bankTypeList = this.bankTypeList;
                bankTypeList.forEach((bankItem) => {
                    insureTypeList.push(bankItem.code);
                })
                this.selectBank = value ? insureTypeList : [];
                this.insureIndeter = false;
            },
            //选择单个银行
            insureChange: function (value) {
                var allLength = this.bankTypeList.length;
                let checkedCount = value.length;
                this.insureAll = checkedCount === allLength;
                this.insureIndeter = checkedCount > 0 && checkedCount < allLength;
            },
            //切换到公司
            isCompany: function(){
                if(this.btActive){
                    return;
                }else{
                    if(this.showImg){
                        this.getChartAxios('org');
                    }else{
                        this.routerMessage.optype = "allocreport_orglist";
                        this.$emit("getCommTable", this.routerMessage);
                        this.clearAll();
                    }
                    this.btActive = true;
                    
                }
            },
            //切换到账号
            isNumber: function(){
                if(!this.btActive){
                    return;
                }else{
                    if(this.showImg){
                       this.getChartAxios('acc'); 
                    }else{
                        this.routerMessage.optype = "allocreport_acclist";
                        this.$emit("getCommTable", this.routerMessage);
                        this.clearAll();
                    }
                    this.btActive = false;
                }
            },
            //选择时间后设置数据
            getDateData: function (val) {
                this.routerMessage.params.start_date = val[0];
                this.routerMessage.params.end_date = val[1];
                this.$emit("getCommTable", this.routerMessage);
            },
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //图表金额格式转换
            transitionMoney: function (num) {
                return this.$common.transitSeparator(num);
            },
            //查询
            queryByOrg: function () {
                this.routerMessage.params.pay_account_org_id = this.$refs.orgTree.getCheckedKeys();
                if(this.showImg){//当前是图表
                    if(this.btActive){
                        this.getChartAxios('org');
                    }else{
                        this.getChartAxios('acc');
                    }
                }else{
                    this.routerMessage.optype = this.btActive ? "allocreport_orglist" : "allocreport_acclist";
                    this.$emit("getCommTable", this.routerMessage);
                }
                this.dialogVisible = false;
            },
            queryByBank: function(){
                this.routerMessage.params.pay_bank_cnaps = this.selectBank;
                if(this.showImg){//当前是图表
                    if(this.btActive){
                        this.getChartAxios('org',this.routerMessage.params);
                    }else{
                        this.getChartAxios('acc',this.routerMessage.params);
                    }
                }else{
                    this.routerMessage.optype = this.btActive ? "allocreport_orglist" : "allocreport_acclist";
                    
                    this.$emit("getCommTable", this.routerMessage);
                }
                this.bankDialogVisible = false;
            },
            //获取饼图数据
            getPieData:function(currentData){
                currentData = currentData.accounts;
                var pieData = [];
                for (var i = 0; i < currentData.length; i++) {
                    var item = currentData[i];
                    var currPieItem = {
                        value: item.amount,
                        code: item.percentage,
                        type: '下拨'
                    }
                    if (this.btActive) {
                        currPieItem.name = item.pay_account_org_name;
                    } else {
                        currPieItem.name = item.pay_account_name;
                    }
                    pieData.push(currPieItem);
                }
                this.pieData = {
                    $noElse: true,
                    pieData: pieData
                };
                //设置右侧饼图列表
                this.pieList = currentData;
                this.pieList.forEach((item) => {
                    var obj = this.countMoney(item.amount,item.percentage)
                    var currPieList = [
                        {
                            value: item.amount,
                            code: item.percentage,
                            name: this.btActive ? "本公司下拨金额" : "当前账号下拨金额",
                            type: "下拨"
                        },
                        {
                            value: obj.amount,
                            code: obj.percentage,
                            name: "其它",
                            type: "下拨"
                        }
                    ];
                    item.$pieData = currPieList;
                });
            },
            //获取图表请求
            getChartAxios:function (type){
                var optype = type === "org" ? 'allocreport_orgtopchar' : 'allocreport_acctopchar';
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data:{
                        optype: optype,
                        params: this.routerMessage.params
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else {
                        var data =result.data.data;
                        this.getPieData(data);
                        //设置图表总金额
                        if (data.total_amount) {
                            this.pieTotalAmount = this.$common.transitSeparator(data.total_amount);
                        }
                        this.clearAll();
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            changeListOrChart:function (){
                if(this.showImg){//当前是图表
                    this.routerMessage.optype = this.btActive ? "allocreport_orglist" : "allocreport_acclist";
                    this.$emit("getCommTable", this.routerMessage);
                    this.clearAll();
                }else{
                    if(this.btActive){
                        this.getChartAxios('org');
                    }else{
                        this.getChartAxios('acc');
                    }
                }
                this.showImg = !this.showImg;
            },
            //算钱
            countMoney:function(money,percentage){
                money = money * 1;
                percentage = percentage.split("%")[0]*1;
                var obj = {};
                obj.percentage = (100 -percentage).toFixed(2);
                obj.amount = ((money / (percentage / 100)) * (obj.percentage/100)).toFixed(2);
                obj.percentage = obj.percentage + "%";
                return obj;
            },
            //清除参数
            clearAll:function(){
                //清空参数
                if (this.$refs.orgTree)
                    this.$refs.orgTree.setCheckedKeys([]);
                this.selectBank = [];
                this.pieTotalAmount = "0.00";
                this.routerMessage.params = {
                    start_date:"",
                    end_date: "",  
                    pay_account_org_id:[],
                    pay_bank_cnaps:[]
                }
            }
        },
        watch: {
            tableData: function (val, oldVal) {
                if(val.data.total_amount){
                    this.totalAmount = this.$common.transitSeparator(val.data.total_amount);
                }
                this.tableList = val.data.accounts;
            }
        }
    }
</script>
