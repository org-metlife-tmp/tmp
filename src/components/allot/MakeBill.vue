<style>
    #makeBill {
        min-width: 800px;
        width: 70%;
        height: 100%;
        margin: 0 auto;
    }

    /*标题*/

    #makeBill header button {
        float: right;
        margin-top: -40px;
    }

    /*内容*/
    #makeBill section {
        width: 100%;
        height: 512px;
        background-color: #fff;
        box-sizing: border-box;
        padding: 20px 20px 60px;
        margin-bottom: 10px;
    }

    #makeBill .title-date {
        color: #ccc;
        height: 40px;
        text-align: left;
    }

    #makeBill .title-date > span {
        margin-right: 10px;
    }

    #makeBill .title-date .serial-number {
        float: right;
    }

    /*表格*/
    #makeBill .bill-content {
        width: 100%;
        height: 388px;
        color: #363636;
        box-sizing: border-box;
        border: 3px solid #dde0e0;
        background: url(../../assets/slice_bg.png) repeat;
    }

    #makeBill .bill-content table {
        width: 100%;
        height: 100%;
        border-collapse: collapse;
    }

    #makeBill .bill-content td {
        border: 1px solid #dde0e0;
    }

    #makeBill .bill-content .title-erect {
        width: 60px;
        text-align: center;
        padding: 0 16px;
        box-sizing: border-box;
        line-height: 20px;
    }

    #makeBill .bill-content .title-small {
        width: 82px;
        text-align: center;
        letter-spacing: 1em;
        text-indent: 1em;
        text-align: center;
    }

    #makeBill .bill-content .title-space {
        height: 2px;
    }

    #makeBill .bill-content .set-space {
        letter-spacing: 2em;
        text-indent: 2em;
    }

    #makeBill .bill-content .select-height {
        height: 56px;
    }

    #makeBill .bill-content .el-select {
        width: 100%;
    }

    #makeBill .bill-content .el-select input {
        height: 56px;
        border: none;
    }

    #makeBill .bill-content .text-left {
        text-align: left;
        text-indent: 1em;
    }

    /*底部按钮组*/
    #makeBill .bill-operation {
        text-align: left;
        margin-top: 12px;
    }

    #makeBill .bill-operation .arrows {
        font-size: 20px;
        vertical-align: middle;
        font-family: initial;
        margin-left: 10px;
    }

    #makeBill .bill-operation .btnGroup {
        float: right;
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
                <span>申请日期:</span>
                <el-date-picker
                        v-model="dateValue"
                        type="date"
                        placeholder="选择日期"
                        value-format="yyyy-MM-dd"
                        size="mini">
                </el-date-picker>
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
                            <el-select v-model="paymentAccountNumber"
                                       clearable filterable
                                       placeholder="请选择账号"
                                       @change="selectPayNumber($event,'payNumber')"
                                       @clear="clearPayNumber('payNumber')">
                                <el-option
                                        v-for="item in options"
                                        :key="item.value"
                                        :label="item.value"
                                        :value="item.value">
                                </el-option>
                            </el-select>
                        </td>
                        <td class="title-small">账号</td>
                        <td>
                            <el-select v-model="gatherAccountNumber"
                                       clearable filterable
                                       placeholder="请选择账号"
                                       @change="selectPayNumber($event,'gatherNumber')"
                                       @clear="clearPayNumber">
                                <el-option
                                        v-for="item in options"
                                        :key="item.value"
                                        :label="item.value"
                                        :value="item.value">
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
                        <td colspan="4"></td>
                    </tr>
                    <tr>
                        <td colspan="2" class="set-space">摘要</td>
                        <td></td>
                        <td colspan="2">调拨类型</td>
                        <td></td>
                    </tr>
                    <tr>
                        <td colspan="2" class="set-space">附件 <br/> <span></span>个</td>
                        <td colspan="4"></td>
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
    export default {
        name: "MakeBill",
        data: function () {
            return {
                dateValue: "",
                serial: "(1232132332)",
                options: [{
                    value: '666666',
                    name: '建设银行',
                    address: "北京支行"
                }, {
                    value: '888888',
                    name: '民生银行',
                    address: "上海总行"
                }, {
                    value: '333333',
                    name: '招商银行',
                    address: "中国分行"
                }],
                paymentAccountNumber: "",
                gatherAccountNumber: "",
                paymentName: "",
                paymentAddress: "",
                gatherName: "",
                gatherAddress: ""
            }
        },
        methods: {
            selectPayNumber: function (value, target) {
                var options = this.options;
                for (var i = 0; i < options.length; i++) {
                    var thisNumber = options[i];
                    if (value == thisNumber.value) {
                        if (target == "payNumber") {
                            this.paymentName = thisNumber.name;
                            this.paymentAddress = thisNumber.address;
                        } else {
                            this.gatherName = thisNumber.name;
                            this.gatherAddress = thisNumber.address;
                        }
                    }
                }
            },
            clearPayNumber: function (target) {
                if (target == "payNumber") {
                    this.paymentName = "";
                    this.gatherName = "";
                }
            }
        }
    }
</script>
