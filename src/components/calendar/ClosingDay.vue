<style scoped lang="less" type="text/less">
    #closingDay{
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
                    background: #1BD2B4;
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
    <div id="closingDay">
        <!--顶部按钮-->
        <div class="button-list-left">
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
        name: "ClosingDay",
        created: function () {
            this.$emit("transmitTitle", "结账日设置");

            //设置年份列表
            var newYear = new Date().getFullYear();
            this.yearList.push(newYear + "");
            this.yearList.push(newYear + 1 + "");
            this.currentYear = newYear + "";

            //获取日历表
            this.getWorkday(newYear + "");
        },
        mounted: function(){
        },
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                workdayData: [], //日历数据
                yearList: [],
                currentYear: "",
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

                        //获取结账日
                        this.getClosingDay();
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
                if (month.$closingDay) {
                    classList.isEdit = true;
                }

                return classList;
            },
            //获取结账日
            getClosingDay: function () {
                var params = {
                    year: this.currentYear
                }

                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "workcal_checkoutlist",
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
                                        this.$set(item,"$closingDay",true);
                                    }
                                })
                            })
                        });
                        //设置激活状态
                        this.isActive = data.is_active == 1 ? true : false;
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //选择年份
            selectYear: function(currYear){
                if(currYear){
                    this.getWorkday(currYear);
                }
            },
            //设置结账日
            setOfferday: function(month){
                if(this.isActive){
                    return;
                }
                this.$set(month, "$closingDay", !month.$closingDay);
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
                if (month.$closingDay) { //报盘日
                    return "取消此结账日";
                } else {
                    return "设置为结账日";
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
                    dates: []
                }
                workdayData.forEach((month) => {
                    month.value.forEach((item) => {
                        if(item.$closingDay){
                            params.dates.push(this.currentYear + "-" + item.month + "-" + item.cdate)
                        }
                    })
                });
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "workcal_checkoutset",
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
                        this.saveOfferday(true);
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
                        optype: "workcal_checkoutactivity",
                        params: {
                            year: this.currentYear
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

