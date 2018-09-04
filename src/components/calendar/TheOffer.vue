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
            <el-select v-model="currentYear" placeholder="请选择年份"
                       filterable clearable size="mini"
                       @change="">
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
                            :open-delay="500">
                    <div slot="content">{{  }}</div>
                    <li :style="oneDayClass(month)"
                        :class="dayStatus(month)">
                        {{ month.cdate }}
                    </li>
                </el-tooltip>
            </ul>

            <!--底部按钮-->
            <div class="botton-pag">
                <el-button type="warning" size="small" @click="">启用</el-button>
                <el-button type="warning" size="small" @click="">保存</el-button>
                <el-button type="warning" size="small" @click="">取消</el-button>
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
            this.currentYear = newYear;

            //获取日历表
            this.getWorkday(newYear + "");
        },
        data: function () {
            return {
                workdayData: [], //日历数据
                yearList: [],
                currentYear: ""
            }
        },
        methods: {
            //获取日历
            getWorkday: function(getYear){
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
                if (month.is_holiday == 0) { //工作日
                    return {
                        "noHoliday": true
                    }
                } else { //休息日
                    return {
                        "holiday": true
                    }
                }
            },
        }
    }
</script>

