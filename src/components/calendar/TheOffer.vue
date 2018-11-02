<style scoped lang="less" type="text/less">
    #theOffer{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*顶部按钮*/
        .button-list-left {
            position: absolute;
            top: -40px;
            left: -21px;
        }

        //日历部分
        .workday-content {
            position: relative;
            height: 960px;
            margin: -20px 0 20px -20px;
            width: 104%;
            background: #fff;
            padding-top: 20px;
            padding-left: 20px;
            min-width: 1076px;

            .month {
                float: left;
                width: 30%;
                margin: 0 20px 30px 10px;
                height: 200px;
                border: 1px solid #4FB1E2;
                border-radius: 6px;
                box-shadow: 0px 2px 12px 0 #ccc;

                /*月份*/
                .month-title {
                    margin-top: 6px;
                    font-weight: bold;
                }

                /*周*/
                .month-week {
                    font-weight: bold;
                    margin-top: 6px;
                    margin-bottom: 2px;
                }

                /*日*/
                li {
                    float: left;
                    width: 46px;
                    box-sizing: border-box;
                    margin-top: 4px;
                    cursor: pointer;
                }

                /*判断不同日期*/
                .noHoliday {
                    color: #1781B5;
                }

                .holiday {
                    color: #FF3A03;
                }

                .noHoliday:hover, .holiday:hover {
                    .isEdit;
                }

                /*修改后的日期*/
                .isEdit {
                    background: #50B0E2;
                    background-clip: content-box;
                    box-sizing: border-box;
                    padding: 0 10px;
                    border-radius: 50%;
                    color: #fff;
                }
            }

            .month:hover {
                box-shadow: 0px 2px 30px 0 #ccc;
            }
        }
        /*底部按钮*/
        .botton-pag {
            position: absolute;
            height: 32px;
            bottom: 10px;
            right: 34px;
            width: 100%;
            text-align: right;
        }
    }
</style>

<template>
    <div id="theOffer">
        <!--顶部按钮-->
        <div class="button-list-left">
            <el-select v-model="bankType" placeholder="请选择银行大类"
                       clearable filterable size="mini"
                       :filter-method="filterBankType"
                       @change="bankIsSelect">
                <el-option v-for="bankType in bankTypeList"
                           :key="bankType.name"
                           :label="bankType.display_name"
                           :value="bankType.name">
                </el-option>
            </el-select>
            <el-select v-model="currentYear" placeholder="请选择年份"
                       filterable clearable size="mini"
                       @change="selectYear">
                <el-option v-for="year in yearList"
                           :key="year"
                           :label="year"
                           :value="year">
                </el-option>
            </el-select>
        </div>

        <!--日历部分-->
        <div class="workday-content">
            <ul v-for="workday in workdayData" class="month">
                <div class="month-title">{{ workday.month }}月</div>
                <li class="month-week">一</li>
                <li class="month-week">二</li>
                <li class="month-week">三</li>
                <li class="month-week">四</li>
                <li class="month-week">五</li>
                <li class="month-week">六</li>
                <li class="month-week">日</li>

                <el-tooltip placement="bottom" v-for="month in workday.value"
                            :key="month.month + month.cdate"
                            :disabled="showTooltip(month)"
                            :open-delay="500">
                    <div slot="content">{{ tooltipText(month) }}</div>
                    <li :style="oneDayClass(month)"
                        :class="dayStatus(month)"
                        @click="setOfferday(month)">
                        {{ month.cdate }}
                    </li>
                </el-tooltip>
            </ul>

            <!--底部按钮-->
            <div class="botton-pag" v-if="!isActive">
                <el-button type="warning" size="small" @click="startUsing">启用</el-button>
                <el-button type="warning" size="small" @click="saveOfferday(false)">保存</el-button>
                <el-button type="warning" size="small" @click="cancelSet">取消</el-button>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "TheOffer",
        created: function () {
            this.$emit("transmitTitle", "报盘日设置");

            //设置年份列表
            var newYear = new Date().getFullYear();
            this.yearList.push(newYear + "");
            this.yearList.push(newYear + 1 + "");
            this.currentYear = newYear + "";

            //获取日历表
            this.getWorkday(newYear + "");
        },
        mounted: function(){
            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
                this.bankTypeList = bankTypeList;
            }
            var bankAllTypeList = JSON.parse(window.sessionStorage.getItem("bankAllTypeList"));
            if(bankAllTypeList){
                this.bankAllTypeList = bankAllTypeList;
            }
        },
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                workdayData: [], //日历数据
                yearList: [],
                currentYear: "",
                bankType: "", //银行大类
                bankTypeCode: "",
                bankAllList: [],
                bankTypeList: [],
                bankAllTypeList: [], //银行大类全部(不重复)
                isActive: false //激活状态
            }
        },
        methods: {
            //获取日历
            getWorkday: function(getYear){
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "workcal_list",
                        params: {
                            year: getYear
                        }
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
                        this.workdayData = data.detail;

                        //获取报盘日
                        this.getWorkWeek();
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //设置每月一号的位置
            oneDayClass: function (month) {
                if (month.cdate == "01") {
                    return {'margin-left': ((month.day_of_week - 1) * 46) + "px"};
                } else {

                }
            },
            //设置每日的颜色
            dayStatus: function (month) {
                var classList = {};

                if (month.is_holiday == 0) { //工作日
                    classList.noHoliday = true;
                } else { //休息日
                    classList.holiday = true;
                }
                if (month.$offerDay) {
                    classList.isEdit = true;
                }

                return classList;
            },
            //获取报盘日
            getWorkWeek: function () {
                var params = {
                    year: this.currentYear,
                    bank_type: this.bankTypeCode
                }

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "workoffer_list",
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
                        //设置报盘日
                        var workdayData = this.workdayData;
                        workdayData.forEach((month) => {
                            month.value.forEach((item) => {
                                data.dates.forEach((offerItem) => {
                                    var currentDate = new Date(this.currentYear + "-" + item.month + "-" + item.cdate).valueOf();
                                    var offerDate = new Date(offerItem).valueOf();
                                    if(currentDate == offerDate){
                                        this.$set(item,"$offerDay",true);
                                    }
                                })
                            })
                        });
                        //设置激活状态
                        this.isActive = data.is_active == 1 ? true : false;
                        //设置银行大类
                        this.bankType = data.bank_type;

                        var bankAllList = this.bankAllList;
                        for(var i = 0; i < bankAllList.length; i++){
                            if(bankAllList[i].name == data.bank_type){
                                this.bankTypeCode = bankAllList[i].code;
                                break;
                            }
                        }
                    }
                }).catch(function (error) {
                    console.log(error);
                });
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
                    this.bankTypeList = this.bankAllTypeList;
                }
            },
            //选择年份
            selectYear: function(currYear){
                if(currYear){
                    this.getWorkday(currYear);
                }
            },
            //银行大类变化后
            bankIsSelect: function (value) {
                if(value){
                    this.getWorkday(this.currentYear);

                    var bankAllList = this.bankAllList;
                    for(var i = 0; i < bankAllList.length; i++){
                        if(bankAllList[i].name == value){
                            this.bankTypeCode = bankAllList[i].code;
                            break;
                        }
                    }
                }else{
                    this.bankTypeCode = "";
                }

            },
            //设置报盘日
            setOfferday: function(month){
                if(this.isActive){
                    return;
                }
                this.$set(month, "$offerDay", !month.$offerDay);
                this.$set(month, "$edit", !month.$edit);
            },
            //控制提示框是否显示
            showTooltip: function (month) {
                if(this.isActive){
                    return true;
                }else{
                    return false;
                }
            },
            //设置鼠标经过时的提示框
            tooltipText: function (month) {
                if (month.$offerDay) { //报盘日
                    return "取消此报盘日";
                } else {
                    return "设置为报盘日";
                }
            },
            //取消
            cancelSet: function(){
                var workdayData = this.workdayData;
                workdayData.forEach((month) => {
                    month.value.forEach((item) => {
                        if(item.$edit){
                            this.setOfferday(item);
                        }
                    })
                })
            },
            //保存
            saveOfferday: function(startUsing){
                var workdayData = this.workdayData;
                var params = {
                    year: this.currentYear,
                    bank_type: this.bankTypeCode,
                    offer_date: []
                }
                workdayData.forEach((month) => {
                    month.value.forEach((item) => {
                        if(item.$offerDay){
                            params.offer_date.push(this.currentYear + "-" + item.month + "-" + item.cdate)
                        }
                    })
                });

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "workoffer_add",
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
                        if(!startUsing){
                            this.$message({
                                message: '保存成功',
                                type: 'success',
                                duration: 2000
                            });
                        }
                        var workdayData = this.workdayData;
                        workdayData.forEach((month) => {
                            month.value.forEach((item) => {
                                if(item.$edit){
                                    this.$set(item, "$edit", false);
                                }
                            })
                        });

                        //启用
                        if(startUsing){
                            this.startUsingAxios();
                        }
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //启用
            startUsing: function(){
                var workdayData = this.workdayData;
                var flag = false;
                workdayData.forEach((month) => {
                    month.value.forEach((item) => {
                        if(item.$edit){
                            flag = true;
                        }
                    })
                });

                if(flag){
                    this.$confirm('是否默认保存当前修改并启用?启用之后将不可修改', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).then(() => {
                        this.saveWorkday(true);
                    }).catch(() => {
                    });
                }else{
                    this.$confirm('启用之后将不可修改，是否启用？', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).then(() => {
                        this.startUsingAxios();
                    }).catch(() => {
                    });
                }
            },
            //启用请求
            startUsingAxios: function(){
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "workoffer_activity",
                        params: {
                            year: this.currentYear,
                            bank_type: this.bankTypeCode
                        }
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
                        this.$message({
                            message: '启用成功',
                            type: 'success',
                            duration: 2000
                        });
                        this.isActive = true;

                        var workdayData = this.workdayData;
                        workdayData.forEach((month) => {
                            month.value.forEach((item) => {
                                if(item.$edit){
                                    this.$set(item, "$edit", false);
                                }
                            })
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
        }
    }
</script>

