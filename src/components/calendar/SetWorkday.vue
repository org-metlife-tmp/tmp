<style scoped lang="less" type="text/less">
    #setWorkday{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        //日历部分
        .workday-content{
            width: 100%;
            height: 95%;
            overflow: hidden;

            >div{
                width: 100%;
                transition: margin 1s;
            }

            .slide-down{
                margin-top: -460px;
            }

            .month{
                float: left;
                width: 30%;
                margin-right: 25px;
                margin-bottom: 30px;
                height: 200px;
                border: 1px solid #4FB1E2;
                border-radius: 6px;
                box-shadow: 0px 2px 12px 0 #ccc;


                /*月份*/
                .month-title{
                    margin-top: 6px;
                    font-weight: bold;
                }

                /*周*/
                .month-week{
                    font-weight: bold;
                    margin-top: 6px;
                    margin-bottom: 2px;
                }

                /*日*/
                li{
                    float: left;
                    width: 46px;
                    box-sizing: border-box;
                    margin-top: 4px;
                    cursor: pointer;
                }

                /*判断不同日期*/
                .disabled-li{
                    color: #ccc;
                    cursor: no-drop;
                    /*context-menu*/
                }

                .noHoliday{
                    color: #1781B5;
                }

                .holiday{
                    color: #FF3A03;
                }

                .noHoliday:hover,.holiday:hover{
                    background: #EBEBEB;
                    background-clip: content-box;
                    box-sizing: border-box;
                    padding: 0 10px;
                    border-radius: 50%;
                }

                /*修改后的日期*/
                .isEdit{
                    background: #EBEBEB;
                    background-clip: content-box;
                    box-sizing: border-box;
                    padding: 0 10px;
                    border-radius: 50%;
                }
            }

            .month:hover{
                box-shadow: 0px 2px 30px 0 #ccc;
            }

            /*不可用的月份*/
            .noActive{
                border-color: #ccc;

                .month-title{
                    color: #ccc;
                }

                .month-week{
                    color: #ccc;
                }
            }
        }

        //右侧滑块
        .slide-button{
            position: absolute;
            width: 30px;
            height: 90%;
            right: 0;
            top: 0;

            i{
                display: block;
                width: 36px;
                height: 36px;
                border-radius: 50%;
                line-height: 32px;
                font-size: 34px;
                font-weight: bold;
                cursor: pointer;
                background: #ccc;
                color: #fff;
            }

            .usable{
                background: #a4ff7f;
            }

            i:nth-child(2){
                margin-top: 366px;
                line-height: 36px;
            }
        }

        /*底部按钮*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -10px;
            text-align: right;
            box-sizing: border-box;
        }
    }
</style>

<template>
    <div id="setWorkday">
        <div class="workday-content">
            <div :class="{'slide-down':workdayIsDown}">
                <ul v-for="workday in workdayData" class="month"
                    :class="{noActive: workday.month * 1 < (new Date().getMonth() + 1)}">
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
            </div>
        </div>
        <div class="slide-button">
            <i class="el-icon-arrow-up" @click="workdayIsDown = false" :class="{'usable':workdayIsDown}"></i>
            <i class="el-icon-arrow-down" @click="workdayIsDown = true" :class="{'usable':!workdayIsDown}"></i>
        </div>
        <!--底部按钮-->
        <div class="botton-pag">
            <el-button type="warning" size="small">启用</el-button>
            <el-button type="warning" size="small">保存</el-button>
            <el-button type="warning" size="small" @click="cancelSet">取消</el-button>
        </div>
    </div>
</template>

<script>
    export default {
        name: "SetWorkday",
        created: function () {
            this.$emit("transmitTitle", "工作日设置");

            //获取日历表
            this.$axios({
                url: "/cfm/normalProcess",
                method: "post",
                data: {
                    optype: "workcal_init",
                    params: {
                        year: new Date().getFullYear() + ""
                    }
                }
            }).then((result) => {
                if (result.data.error_msg) {

                } else {
                    var data = result.data.data;
                    this.workdayData = data;
                }
            }).catch(function (error) {
                console.log(error);
            });

            //根据当前月份设置显示月份
            if((new Date().getMonth() + 1) >= 7){
                this.workdayIsDown = true;
            }
        },
        data:function(){
            return {
                workdayData: [], //日历数据
                workdayIsDown: false, //日历展示月份控制
            }
        },
        methods: {
            //设置每月一号的位置
            oneDayClass: function(month){
                if(month.cdate == "01"){
                    return {'margin-left': ((month.day_of_week - 1) *46) + "px"};
                }else{

                }
            },
            //设置每日的颜色
            dayStatus: function(month){
                var isActive = this.dayIsActive(month);
                if(!isActive){ //不能设置的时间
                    month.$isDontEdit = true;
                    return "disabled-li";
                }else if(month.is_holiday == 0){ //工作日
                    return {
                        "isEdit": month.$isEdit,
                        "noHoliday": true
                    }
                }else{ //休息日
                    return {
                        "isEdit": month.$isEdit,
                        "holiday": true
                    }
                }
            },
            //设置鼠标经过时的提示框
            tooltipText: function(month){
                var isActive = this.dayIsActive(month);
                if(!isActive){ //不能设置的时间

                }else if (month.$isEdit){
                    return "取消设置";
                }else if(month.is_holiday == 0){ //工作日
                    return "设置为休息日";
                }else{ //休息日
                    return "设置为工作日";
                }
            },
            //控制提示框是否显示
            showTooltip: function(month){
                var isActive = this.dayIsActive(month);
                return !isActive
            },
            //判断当前时间是否可用
            dayIsActive: function(month){
                var currentDate = new Date().valueOf();
                var thisDate = new Date("2018-" + month.month + "-" + month.cdate + " 23:59:59").valueOf();

                if(thisDate < currentDate){ //不可用
                    return false;
                }else{ //可用
                    return true;
                }
            },
            //设置当前日期
            setHoliday: function(month){
                if(month.$isDontEdit){
                    return;
                }else{
                    month.is_holiday = month.is_holiday == 1? 0 : 1;
                    month.$isEdit = !month.$isEdit;
                }
            },
            //取消
            cancelSet: function(){
                var workdayData = this.workdayData;
                for(var i = 0; i < workdayData.length; i++){
                    var workday = workdayData[i].value;
                    for(var j = 0; j < workday.length; j++){
                        var item = workday[j];
                        if(item.$isEdit){
                            item.is_holiday = item.is_holiday == 1? 0 : 1;
                            item.$isEdit = false;
                        }
                    }
                }
            }
        },
        computed: {

        }
    }
</script>
