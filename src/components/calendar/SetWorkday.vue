<style scoped lang="less" type="text/less">
    #setWorkday {
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
            margin-bottom: 20px;
            width: 104%;
            margin-left: -20px;
            margin-top: -20px;
            background: #fff;
            padding-top: 20px;
            padding-left: 20px;
            min-width: 1076px;

            .month {
                float: left;
                width: 30%;
                margin-right: 20px;
                margin-left: 10px;
                margin-bottom: 30px;
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
                .disabled-li {
                    color: #ccc;
                    cursor: no-drop;
                    /*context-menu*/
                }

                .noHoliday {
                    color: #1781B5;
                }

                .holiday {
                    color: #FF3A03;
                }

                .noHoliday:hover, .holiday:hover {
                    background: #EBEBEB;
                    background-clip: content-box;
                    box-sizing: border-box;
                    padding: 0 10px;
                    border-radius: 50%;
                }

                /*修改后的日期*/
                .isEdit {
                    background: #EBEBEB;
                    background-clip: content-box;
                    box-sizing: border-box;
                    padding: 0 10px;
                    border-radius: 50%;
                }
            }

            .month:hover {
                box-shadow: 0px 2px 30px 0 #ccc;
            }

            /*不可用的月份*/
            .noActive {
                border-color: #ccc;

                .month-title {
                    color: #ccc;
                }

                .month-week {
                    color: #ccc;
                }
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
    <div id="setWorkday">
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
            <ul v-for="workday in workdayData" class="month"
                :class="weekStatus(workday.month)">
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
                        @click="setHoliday(month)">
                        {{ month.cdate }}
                    </li>
                </el-tooltip>
            </ul>

            <!--底部按钮-->
            <div class="botton-pag" v-if="!isActive">
                <el-button type="warning" size="small" @click="startUsing">启用</el-button>
                <el-button type="warning" size="small" @click="saveWorkday(false)">保存</el-button>
                <el-button type="warning" size="small" @click="cancelSet">取消</el-button>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "SetWorkday",
        created: function () {
            this.$emit("transmitTitle", "工作日设置");

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
                isActive: false
            }
        },
        methods: {
            //获取日历
            getWorkday: function(getYear){
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "workcal_init",
                        params: {
                            year: getYear
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {

                    } else {
                        var data = result.data.data;
                        this.workdayData = data.detail;
                        this.isActive = data.is_active == 1 ? true : false;
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
                var isActive = this.dayIsActive(month);
                if (!isActive) { //不能设置的时间
                    month.$isDontEdit = true;
                    return "disabled-li";
                } else if (month.is_holiday == 0) { //工作日
                    return {
                        "isEdit": month.$isEdit,
                        "noHoliday": true
                    }
                } else { //休息日
                    return {
                        "isEdit": month.$isEdit,
                        "holiday": true
                    }
                }
            },
            //设置周的颜色
            weekStatus: function(month){
                var workMonth = new Date(this.currentYear + "-" + month).valueOf();
                var currentMonth = new Date(new Date().getFullYear() + "-" + (new Date().getMonth() + 1)).valueOf();
                return {
                    noActive: workMonth < currentMonth
                }
            },
            //设置鼠标经过时的提示框
            tooltipText: function (month) {
                var isActive = this.dayIsActive(month);
                if (!isActive) { //不能设置的时间

                } else if (month.$isEdit) {
                    return "取消设置";
                } else if (month.is_holiday == 0) { //工作日
                    return "设置为休息日";
                } else { //休息日
                    return "设置为工作日";
                }
            },
            //控制提示框是否显示
            showTooltip: function (month) {
                var isActive = this.dayIsActive(month);
                return this.isActive ? true : !isActive;
            },
            //判断当前时间是否可用
            dayIsActive: function (month) {
                var currentDate = new Date().valueOf();
                var thisDate = new Date(this.currentYear + "-" + month.month + "-" + month.cdate + " 23:59:59").valueOf();

                if (thisDate < currentDate) { //不可用
                    return false;
                } else { //可用
                    return true;
                }
            },
            //设置当前日期
            setHoliday: function (month) {
                if (month.$isDontEdit || this.isActive) {
                    return;
                } else {
                    month.is_holiday = month.is_holiday == 1 ? 0 : 1;
                    month.$isEdit = !month.$isEdit;
                }
            },
            //选择年份
            selectYear: function(currYear){
                this.getWorkday(currYear);
            },
            //取消
            cancelSet: function () {
                var workdayData = this.workdayData;
                for (var i = 0; i < workdayData.length; i++) {
                    var workday = workdayData[i].value;
                    for (var j = 0; j < workday.length; j++) {
                        var item = workday[j];
                        if (item.$isEdit) {
                            item.is_holiday = item.is_holiday == 1 ? 0 : 1;
                            item.$isEdit = false;
                        }
                    }
                }
            },
            //保存
            saveWorkday: function(startUsing){
                var params = this.setParams();
                if(params.holiday.length == 0 && params.workingday.length == 0){
                    this.$message({
                        message: '请对日历进行修改',
                        type: 'warning',
                        duration: 2000
                    });
                    return;
                }

                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "workcal_holiday",
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
                        var workdayData = this.workdayData;
                        for (var i = 0; i < workdayData.length; i++) {
                            var workday = workdayData[i].value;
                            for (var j = 0; j < workday.length; j++) {
                                var item = workday[j];
                                if (item.$isEdit) {
                                    //每日颜色的样式是根据is_holiday判断的 所以必须修改is_holiday才能触发样式改变
                                    item.is_holiday = item.is_holiday == 1 ? 0 : 1;
                                    item.$isEdit = false;
                                    item.is_holiday = item.is_holiday == 1 ? 0 : 1;
                                }
                            }
                        }
                        this.$message({
                            message: '保存成功',
                            type: 'success',
                            duration: 2000
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
                var params = this.setParams();
                if(params.holiday.length != 0 || params.workingday.length != 0){
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
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "workcal_activity",
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
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //设置params
            setParams: function(){
                var workdayData = this.workdayData;
                var params = {
                    year: this.currentYear,
                    holiday: [],
                    workingday: []
                }
                workdayData.forEach((monthData) => {
                    monthData.value.forEach((item) => {
                        if(item.$isEdit){
                            if(item.is_holiday == 1){
                                params.holiday.push(this.currentYear + "-" + item.month + "-" + item.cdate);
                            }else{
                                params.workingday.push(this.currentYear + "-" + item.month + "-" + item.cdate);
                            }
                        }
                    })
                });
                return params;
            }
        },
        computed: {}
    }
</script>
