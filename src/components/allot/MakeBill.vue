<style lang="less" type="text/less">
    #makeBill {
        min-width: 980px;
        width: 80%;
        height: 100%;
        margin: 0 auto;

        /*标题*/
        header button {
            float: right;
            margin-top: -40px;
        }

        /*内容*/
        section {
            width: 100%;
            height: 512px;
            background-color: #fff;
            box-sizing: border-box;
            padding: 20px 20px 60px;
            margin-bottom: 10px;
        }

        /*表格顶部内容*/
        .title-date {
            color: #ccc;
            height: 40px;
            text-align: left;

            > span {
                margin-right: 10px;
            }

            .serial-number {
                float: right;
                margin-top: 10px;
            }
        }

        /*表格*/
        .bill-content {
            width: 100%;
            height: 388px;
            color: #363636;
            box-sizing: border-box;
            border: 3px solid #dde0e0;
            background: url(../../assets/slice_bg.png) repeat;

            table {
                width: 100%;
                height: 100%;
                border-collapse: collapse;
            }

            td {
                border: 1px solid #dde0e0;
            }

            .title-erect {
                width: 60px;
                text-align: center;
                padding: 0 16px;
                box-sizing: border-box;
                line-height: 20px;
            }

            .title-small {
                width: 82px;
                text-align: center;
                letter-spacing: 1em;
                text-indent: 1em;
                text-align: center;
            }

            .title-space {
                height: 2px;
            }

            .set-space {
                letter-spacing: 2em;
                text-indent: 2em;
            }

            .select-height {
                height: 56px;
                padding: 0;

                .el-select {
                    height: 100%;

                    .el-input {
                        height: 100%;

                        input {
                            height: 100%;
                        }
                    }
                }
            }

            .el-select {
                width: 100%;

                input {
                    height: 56px;
                    border: none;
                }
            }

            .text-left {
                text-align: left;
                text-indent: 1em;
            }

            /*金额*/
            .money-input {
                text-align: left;
                padding-left: 16px;

                input {
                    border: none;
                    outline: none;
                    color: #fd7d2f;
                    border-bottom: 2px solid #eee;
                    font-size: 15px;
                    font-weight: bold;
                    background-color: rgba(255, 255, 255, 0);
                    width: 120px;
                    margin-right: 8px;
                }
            }

            /*摘要-调拨类型*/
            .empty-input {
                text-align: left;
                padding-left: 16px;

                input {
                    border: none;
                    outline: none;
                    font-size: 14px;
                    background-color: rgba(255, 255, 255, 0);
                    width: 100%;
                }
            }
        }

        /*底部按钮组*/
        .bill-operation {
            text-align: left;
            margin-top: 12px;

            .arrows {
                font-size: 20px;
                vertical-align: middle;
                font-family: initial;
                margin-left: 10px;
            }

            .btnGroup {
                float: right;
            }
        }

        /*附件*/
        .upload-content{
            height: 77px;
            padding-left: 16px;

            #upload{
                height: 77px;
                padding-top: 16px;
                box-sizing: border-box;
                overflow-y: auto;
            }
        }
        .upload-num{
            display: inline-block;
            width: 18px;
            height: 18px;
            border-bottom: 1px solid #aaa;
            vertical-align: middle;
            margin: 0 10px 0 26px;
            text-indent: 0;
            padding-left: 15px;
        }
    }


</style>

<template>
    <div id="makeBill">
        <header>
            <h1>内部调拨-制单</h1>
            <el-button type="warning" size="small">打印</el-button>
        </header>
        <section>
            <div class="title-date">
                <el-date-picker
                        v-model="dateValue"
                        type="date"
                        placeholder="请选择申请日期"
                        value-format="yyyy-MM-dd"
                        size="mini">
                </el-date-picker>
                <el-select v-model="payMode" placeholder="请选择业务类型"
                           filterable clearable size="mini">
                    <el-option v-for="(name,k) in payStatList"
                               :key="k"
                               :label="name"
                               :value="k">
                    </el-option>
                </el-select>
                <el-select v-model="lala" placeholder="请选择付款方式"
                           filterable clearable size="mini">
                    <el-option v-for="(name,k) in payModeList"
                               :key="k"
                               :label="name"
                               :value="k">
                    </el-option>
                </el-select>
                <div class="serial-number">
                    <span>单据编号:</span>
                    <span v-text="serial"></span>
                </div>
            </div>
            <div class="bill-content">
                <table>
                    <tr>
                        <td rowspan="3" class="title-erect">付款方</td>
                        <td class="title-small">户名</td>
                        <td class="text-left" v-text="paymentName"></td>
                        <td rowspan="3" class="title-erect">收款方</td>
                        <td class="title-small">户名</td>
                        <td class="text-left" v-text="gatherName"></td>
                    </tr>
                    <tr>
                        <td class="title-small">账号</td>
                        <td class="select-height">
                            <el-select v-model="paymentNumber"
                                       clearable filterable remote
                                       placeholder="请选择账号"
                                       :loading="payLoading"
                                       :remote-method="getPayList"
                                       @change="selectNumber($event,'payNumber')"
                                       @visible-change="selectVisible($event,'payNumber')"
                                       @clear="clearSelect('payNumber')">
                                <el-option
                                        v-for="payItem in payList"
                                        :key="payItem.acc_id"
                                        :label="payItem.acc_no"
                                        :value="payItem.acc_id">
                                </el-option>
                            </el-select>
                        </td>
                        <td class="title-small">账号</td>
                        <td>
                            <el-select v-model="gatherNumber"
                                       clearable filterable remote
                                       placeholder="请选择账号"
                                       :loading="gatherLoading"
                                       :remote-method="getGatherList"
                                       @change="selectNumber($event,'gatherNumber')"
                                       @visible-change="selectVisible($event,'gatherNumber')"
                                       @clear="clearSelect('gatherNumber')">
                                <el-option
                                        v-for="gatherItem in gatherList"
                                        :key="gatherItem.acc_id"
                                        :label="gatherItem.acc_no"
                                        :value="gatherItem.acc_id">
                                </el-option>
                            </el-select>
                        </td>
                    </tr>
                    <tr>
                        <td>开户行</td>
                        <td v-text="paymentAddress" class="text-left"></td>
                        <td>开户行</td>
                        <td v-text="gatherAddress" class="text-left"></td>
                    </tr>
                    <tr>
                        <td colspan="2" class="title-space"></td>
                        <td colspan="4"></td>
                    </tr>
                    <tr>
                        <td colspan="2">金额（元）</td>
                        <td colspan="4" class="money-input">
                            <span>￥</span>
                            <input type="text" @blur="setMoney" v-model="moneyNum">
                            <span>(大写)</span>
                            <span v-text="moneyText"></span>
                        </td>
                    </tr>
                    <tr>
                        <td colspan="2" class="set-space">摘要</td>
                        <td class="empty-input">
                            <input type="text" placeholder="请在此处填写摘要">
                        </td>
                        <td colspan="2">调拨类型</td>
                        <td v-text="allotType"></td>
                    </tr>
                    <tr>
                        <td colspan="2" class="set-space">附件<br/>
                            <span class="upload-num" v-text="fileLength"></span>个
                        </td>
                        <td colspan="4" class="upload-content">
                            <Upload @currentFielList="setFileList"
                                    :fileMessage="fileMessage"
                                    :isPending="true">
                            </Upload>
                        </td>
                    </tr>
                </table>
            </div>
            <div class="bill-operation">
                <el-button type="warning" plain size="medium">更多单据<span
                        class="arrows">></span></el-button>
                <div class="btnGroup">
                    <el-button type="warning" size="small">清空</el-button>
                    <el-button type="warning" size="small">保存</el-button>
                    <el-button type="warning" size="small">提交</el-button>
                </div>
            </div>
        </section>
    </div>
</template>

<script>
    import Upload from "../publicModule/Upload.vue";

    export default {
        name: "MakeBill",
        created: function () {
            //付款方式 PayMode
            //业务类型
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            //付款方式
            if(constants.PayMode){
                this.payModeList = constants.PayMode;
            }
        },
        components: {
            Upload: Upload
        },
        data: function () {
            return {
                dateValue: "", //申请时间
                serial: "(1232132332)", //单据编号
                paymentNumber: "", //付款方数据
                payLoading: false,
                payList: [],
                paymentName: "",
                paymentAddress: "",
                gatherNumber: "", //收款方数据
                gatherLoading: false,
                gatherList: [],
                gatherName: "",
                gatherAddress: "",
                moneyNum: "", //金额
                moneyText: "",
                allotType: "", //调拨类型
                fileMessage: {
                    bill_id: "",
                    biz_type: 1
                },//附件
                fileList: [],
                fileLength: "",
                payMode: "", //付款方式
                payModeList:{},

                payStatList: [],
                lala: ""
            }
        },
        methods: {
            //获取付款方账号
            getPayList: function (value) {
                this.payLoading = true;
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "dbt_payacclist",
                        params: {
                            query_key: value.trim(),
                            exclude_ids: this.gatherNumber,
                            page_size: 10000,
                            page_num: 1
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {

                    } else {
                        this.payLoading = false;
                        this.payList = result.data.data;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //获取收款方账号
            getGatherList: function (value) {
                this.gatherLoading = true;
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "dbt_recvacclist",
                        params: {
                            query_key: value.trim(),
                            exclude_ids: this.paymentNumber,
                            page_size: 10000,
                            page_num: 1
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {

                    } else {
                        this.gatherLoading = false;
                        this.gatherList = result.data.data;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //选择账号时设置户名和开户行
            selectNumber: function (value, target) {
                var payList = this.payList;
                var gatherList = this.gatherList;

                if (target == "payNumber") {
                    for (var i = 0; i < payList.length; i++) {
                        if (payList[i].acc_id == value) {
                            this.paymentName = payList[i].acc_name;
                            this.paymentAddress = payList[i].bank_name;

                            if (this.gatherNumber) {
                                for (var j = 0; j < gatherList.length; j++) {
                                    if (gatherList[j].acc_id == this.gatherNumber) {
                                        var payLevel = payList[j].level_num;
                                        var gatherLevel = gatherList[j].level_num;
                                    }
                                }
                            }
                        }
                    }
                }
                if (target == "gatherNumber") {
                    for (var i = 0; i < gatherList.length; i++) {
                        if (gatherList[i].acc_id == value) {
                            this.gatherName = gatherList[i].acc_name;
                            this.gatherAddress = gatherList[i].bank_name;

                            if (this.paymentNumber) {
                                for (var j = 0; j < payList.length; j++) {
                                    if (payList[j].acc_id == this.paymentNumber) {
                                        var payLevel = payList[j].level_num;
                                        var gatherLevel = gatherList[j].level_num;
                                    }
                                }
                            }
                        }
                    }
                }

                if (payLevel && gatherLevel) {
                    this.allotType = payLevel == gatherLevel ? "本公司内部账户调拨" : (payLevel > gatherLevel ? "本公司拨给下级公司" : "本公司拨给上级公司");
                }
            },
            //账号下拉框显示时更新数据
            selectVisible: function (curStatus, target) {
                if (curStatus) {
                    if (target == "payNumber") {
                        this.getPayList("");
                    } else {
                        this.getGatherList("");
                    }
                }
            },
            //清空账号时清空户名和开户行
            clearSelect: function (target) {
                if (target == "payNumber") {
                    this.paymentName = "";
                    this.paymentAddress = "";
                }
                if (target == "gatherNumber") {
                    this.gatherName = "";
                    this.gatherAddress = "";
                }
                this.allotType = "";
            },
            //输入金额后进行格式化
            setMoney: function () {
                var moneyNum = this.moneyNum.replace(/,/gi, "").trim();
                /*校验数据格式是否正确*/
                if (moneyNum == "") {
                    return;
                }
                if (isNaN(moneyNum)) {
                    this.$message({
                        type: "warning",
                        message: "请输入正确的金额",
                        duration: 2000
                    });
                    this.moneyNum = "";
                    return;
                }
                var verify = moneyNum.split(".");
                if (verify[0].length > 10) {
                    this.$message({
                        type: "warning",
                        message: "整数位不能超过10位数",
                        duration: 2000
                    });
                    verify[0] = verify[0].slice(0, 10);
                    moneyNum = verify.join(".");
                }
                if (verify[1] && verify[1].length > 2) {
                    this.$message({
                        type: "warning",
                        message: "小数点后只能保留两位小数",
                        duration: 2000
                    });
                    verify[1] = verify[1].slice(0, 2);
                    moneyNum = verify.join(".");
                }

                //设置数字部分格式
                var stringValue = (moneyNum * 1).toLocaleString();
                var value = stringValue.split(".");
                if (value.length == 1) {
                    this.moneyNum = value[0] + ".00";
                } else {
                    if (value[1].length == 1) {
                        this.moneyNum = stringValue + "0";
                    } else {
                        this.moneyNum = stringValue;
                    }
                }

                /*
                * 1、将数字转换成汉字
                * 2、根据其数位添加其计数单位
                * 3、判断0在不同位置的读法
                *   3.0 个位：不转汉字 加计数单位
                *   3.1 十位 转汉字 不加计数单位
                *   3.2 百位 转汉字 不加计数单位
                *   3.3 千位 转汉字 不加计数单位
                *   3.4 万位 不转汉字 加计数单位
                *   3.5 十万位 转汉字 不加计数单位
                *   3.6 百万位 转汉字 不加计数单位
                *   3.7 千万位 转汉字 不加计数单位
                *   3.8 亿位 不转汉字 加计数单位
                *   3.9 十亿位 转汉字 不加计数单位
                * 4、去掉多余的汉字
                *   4.1 去掉重复的零
                *   4.2 去掉后面是计数单位的零
                *   4.3 去掉结尾的零
                *   4.4 判断重复的计数单位：只有零在万位时会出现此情况
                *
                * */

                /*设置中文大写*/
                var numIndex = ['', '拾', '佰', '仟', '万', '拾', '佰', '仟', '亿', '拾'];
                var numTextList = {0: "零", 1: "壹", 2: "贰", 3: "叁", 4: "肆", 5: "伍", 6: "陆", 7: "柒", 8: "捌", 9: "玖",};
                var textValue = moneyNum.split("."); //整数和小数分开
                var textArray = textValue[0].split(""); //整数部分
                var numText = "";
                /*遍历进行汉字转换和添加计数单位*/
                for (var i = 0; i < textArray.length; i++) {
                    var indexLength = textArray.length - 1 - i; //当前数字的数位

                    if (textArray[i] == 0) { //判断为0的情况
                        if (indexLength == 0 || indexLength == 4 || indexLength == 8) { //不转汉字 加计数单位
                            numText += numIndex[indexLength];
                        } else { //转汉字 不加计数单位
                            numText += numTextList[textArray[i]];
                        }
                    } else {
                        numText += numTextList[textArray[i]] + numIndex[indexLength];
                    }
                }
                /*去掉多余的汉字*/
                var resultTextArr = numText.split("");
                for (var i = 0; i < resultTextArr.length; i++) {
                    //去掉重复的零和后面是计数单位的零
                    if (resultTextArr[i] == "零" && (resultTextArr[i + 1] == "零" || numIndex.indexOf(resultTextArr[i + 1]) != -1)) {
                        resultTextArr.splice(i, 1);
                        i--;
                    }
                    //判断计数单位是否重复
                    if (numIndex.indexOf(resultTextArr[i]) == 4 && numIndex.indexOf(resultTextArr[i - 1]) >= 8) {
                        resultTextArr.splice(i, 1);
                        i--;
                    }
                }
                numText = resultTextArr.join("");
                //去掉末尾的零
                if (numText[numText.length - 1] == "零") {
                    numText = numText.slice(0, numText.length - 1);
                }

                //判断是否需要有小数位
                if (value.length == 1) {
                    this.moneyText = numText + "元整";
                } else {
                    numText += "点";
                    var decimal = textValue[1].split("");
                    for (var i = 0; i < decimal.length; i++) {
                        numText += numTextList[decimal[i]];
                    }
                    this.moneyText = numText + "元";
                }
            },
            //设置当前项上传附件
            setFileList: function ($event) {
                this.fileLength = "";
                this.fileList = [];
                if ($event.length > 0) {
                    this.fileLength = $event.length;
                    $event.forEach((item) => {
                        this.fileList.push(item.id);
                    })
                }
            },
        }
    }
</script>
