<style scoped lang="less" type="text/less">
    #collectionStatement {
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

            .img-left{
                float:left;
                height: 100%;
                width: 30%;
                background-color: #F7F7F6;

                h2{
                    color: #C0BBB5;
                    margin-bottom: 120px;
                }

                div{
                    font-size: 18px;
                    color: #FF4800;
                }
            }
            .img-right{
                float:right;
                height: 100%;
                width: 70%;
            }
        }

        /*列表内容*/
        .list-content{
        }
    }
</style>

<template>
    <div id="collectionStatement">
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
                           @click="showImg=!showImg">
                    <i class="get-img"></i>
                </el-button>
                <el-button type="primary" size="mini" :class="{active:!showImg}"
                           @click="showImg=!showImg">
                    <i class="get-list"></i>
                </el-button>
            </el-button-group>
        </div>
        <!--图表内容-->
        <section class="img-content" v-show="showImg">
            <div class="img-left">
                <h2>TOP5</h2>
                <span>汇总金额</span>
                <div>￥0.00</div>
            </div>
            <div class="img-right"></div>
        </section>
        <!--列表内容-->
        <section class="list-content" v-show="!showImg">
            <el-table :data="tableList"
                      border size="mini">
                <el-table-column prop="pay_account_bank" label="归集主题" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_no" label="归集额度" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_name" label="归集频率" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_name" label="归集集户(个)" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_name" label="归集金额" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="recv_account_name" label="业务状态" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="50"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                       @click=""></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
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
                <el-button type="warning" size="mini" @click="">确 定</el-button>
            </span>
        </el-dialog>
        <!--选择银行弹出框-->
        <el-dialog :visible.sync="bankDialogVisible"
                   width="600px" title="请选择账户属性"
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
                <el-button type="warning" size="mini" @click="">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "CollectionStatement",
        created: function () {
            this.$emit("transmitTitle", "自动归集图表");

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
        },
        data: function () {
            return {
                tableList: [], //列表数据
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
                    this.btActive = true;
                }
            },
            //切换到账号
            isNumber: function(){
                if(!this.btActive){
                    return;
                }else{
                    this.btActive = false;
                }
            },
            //选择时间后设置数据
            getDateData: function (val) {
                this.routerMessage.params.start_date = val[0];
                this.routerMessage.params.end_date = val[1];
            }
        }
    }
</script>
