<style scoped lang="less" type="text/less">
    #workSection {
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
            min-width: 1100px;

            .month {
                float: left;
                width: 336px;
                height: 200px;
                box-sizing: border-box;
                padding-left: 5px;
                margin: 0 20px 30px 10px;
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
                    background-color: #fff;
                }

                /*日*/
                li {
                    float: left;
                    width: 46px;
                    box-sizing: border-box;
                    margin-top: 4px;
                    cursor: pointer;
                    background-color: #DDEFF9;

                    span {
                        display: inline-block;
                        width: 24px;
                    }
                }

                /*判断不同日期*/
                .noHoliday {
                    color: #1781B5;
                }

                .holiday {
                    color: #FF3A03;
                }

                /*鼠标经过*/
                .noHoliday:hover, .holiday:hover {
                    .end-date;
                }

                /*起点*/
                .start-date {
                    background-clip: content-box;
                    box-sizing: border-box;
                    padding: 0 0 0 10px;
                    border-radius: 50% 0 0 50%;
                    color: #fff;
                }

                .start-date span {
                    margin-right: 10px;
                    background: #4FB1E0;
                    border-radius: 50%;
                }

                .start-date:hover {
                    .start-date;

                    span {
                        background: #4FB1E0;
                        margin-left: 0;
                    }
                }

                /*结束点*/
                .end-date {
                    background-clip: content-box;
                    box-sizing: border-box;
                    padding: 0 10px 0 0;
                    border-radius: 0 50% 50% 0;
                    color: #fff;

                    span {
                        margin-left: 10px;
                        background: #1BD2B4;
                        border-radius: 50%;
                    }
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
    <div id="workSection">
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
                        @click="setEndDate(month,workday)">
                        <span>{{ month.cdate }}</span>
                    </li>
                </el-tooltip>
            </ul>

            <!--底部按钮-->
            <div class="botton-pag">
                <el-button type="warning" size="small" @click="startUsing">启用</el-button>
                <el-button type="warning" size="small" @click="saveWorkWeek(false)">保存</el-button>
                <el-button type="warning" size="small" @click="cancelSet">取消</el-button>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "WorkSection",
        created: function () {
            this.$emit("transmitTitle", "工作区间设置");

            //设置年份列表
            var newYear = new Date().getFullYear();
            this.yearList.push(newYear + "");
            this.yearList.push(newYear + 1 + "");
            this.currentYear = newYear + "";

            //获取日历表
            this.getWorkday(newYear + "");
        },
        data: function () {
            return {
                workdayData: [], //日历数据
                yearList: [],
                currentYear: "",
                weekList: []
            }
        },
        methods: {
            //获取日历
            getWorkday: function (getYear) {
                this.$axios({
                    url: "/cfm/normalProcess",
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

                        //获取工作周数据
                        this.getWorkWeek(this.currentYear);
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //设置每月一号的位置
            oneDayClass: function (month) {
                if (month.cdate == "01") {
                    return {'margin-left': ((month.day_of_week - 1) * 46) + "px"};
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
                if (month.$start) {
                    classList['start-date'] = true;
                }
                if (month.$end) {
                    classList['end-date'] = true;
                }
                return classList;
            },
            //获取起点和结束点
            getWorkWeek: function (getYear) {
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "workweek_list",
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
                        this.weekList = data.weeks;

                        //将起始点数据放进日历
                        this.setFullWork();
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //将起始点数据和日历结合
            setFullWork: function () {
                var workdayData = this.workdayData;
                var weekList = this.weekList;

                workdayData.forEach((workDay) => {
                    for (var i = 0; i < workDay.value.length; i++) {
                        var workDayItem = workDay.value[i];
                        var workDayValue = new Date(this.currentYear + "-" + workDayItem.month + "-" + workDayItem.cdate).valueOf();

                        for (var j = 0; j < weekList.length; j++) {
                            var weekItem = weekList[j];
                            var startValue = new Date(weekItem.start_date).valueOf();
                            var endValue = new Date(weekItem.end_date).valueOf();

                            if (workDayValue == startValue) {
                                this.$set(workDayItem, '$start', true);
                                continue;
                            }
                            if (workDayValue == endValue) {
                                this.$set(workDayItem, '$end', true);
                                continue;
                            }
                        }
                    }
                });
            },
            //控制提示框是否显示
            showTooltip: function (month) {
                var $start = month.$start;
                var $end = month.$end;
                return $start || false;
            },
            //设置鼠标经过时的提示框
            tooltipText: function (month) {
                if (month.$start) { //开始点
                    return "";
                } else if (month.$end) { //结束点
                    return "取消此结束点";
                } else { //普通日期
                    return "设置为结束点";
                }
            },
            //设置结束点
            setEndDate: function (month, workDay) {
                var workdayData = this.workdayData;
                var workList = workDay.value;
                var $start = month.$start;
                var $end = month.$end;

                if ($start) {
                    return;
                }

                var flag = $end ? false : true; //正常日期变为结束点 结束点变为正常日期
                this.$set(month, '$end', flag); //设置当天

                //设置后一天为起点
                var index = workList.indexOf(month);
                if (index == (workList.length - 1)) {
                    var nextDay = workdayData[workdayData.indexOf(workDay) + 1].value[0];
                } else {
                    var nextDay = workList[workList.indexOf(month) + 1];
                }
                this.$set(nextDay, '$start', flag);

                //判断后一天是否为结束点
                if(nextDay.$end){
                    //取消后一天的结束点标记
                    this.$set(nextDay, '$end', false);

                    //取消大后天的开始点标记
                    if(index < (workList.length - 2)){ //大后天在本月
                        var nextTwoDay = workList[workList.indexOf(month) + 2];
                    }else{ //大后天在次月
                        var nextTwoDay = workdayData[workdayData.indexOf(workDay) + 1].value[ 2 - (workList.length - index)];
                    }

                    this.$set(nextTwoDay, '$start', false);
                }
            },
            //选择年份
            selectYear: function(currYear){
                this.getWorkday(currYear);
            },
            //取消
            cancelSet: function(){
                this.getWorkday(this.currentYear);
            },
            //保存
            saveWorkWeek: function(startUsing){
                var workdayData = this.workdayData;
                var params = {
                    endpoint: []
                }
                workdayData.forEach((month) => {
                    month.value.forEach((dayDate) => {
                        if(dayDate.$end){
                            params.endpoint.push(this.currentYear + "-" + dayDate.month + "-" + dayDate.cdate);
                        }
                    })
                });

                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "workweek_set",
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

                        //启用
                        if(startUsing){
                            this.$axios({
                                url: "/cfm/normalProcess",
                                method: "post",
                                data: {
                                    optype: "workweek_set",
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
                                    this.$message({
                                        message: '启用成功',
                                        type: 'success',
                                        duration: 2000
                                    });
                                }
                            }).catch(function (error) {
                                console.log(error);
                            });
                        }
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //启用
            startUsing: function(){
                this.$confirm('启用之后将不可再进行修改，确定启用吗？', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    this.saveWorkWeek(true);
                }).catch(() => {
                });
            }
        }
    }
</script>
