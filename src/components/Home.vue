<style>
    #homeContent {
        position: relative;
        height: 100%;
        min-width: 1080px;
        line-height: 0;
        color: #fff;
    }

    /*地址 时间-开始*/
    .location-time, .backlog-incident, .centre, .content-panel, .footer-text {
        position: absolute;
    }

    .location-time {
        top: 20px;
        left: 20px;
        width: 202px;
        height: 40px;
        color: #ffffff;
        line-height: 40px;
    }

    .location-time .address {
        font-size: 22px;
        border-right: 1px solid rgba(255, 255, 255, 0.5);
        padding: 0 10px 0 0;
    }

    .location-time .c-data {
        padding: 0 10px;
        vertical-align: top;
    }

    /*地址 时间-结束*/

    /*待办事项-开始*/
    .backlog-incident {
        top: 200px;
        left: 20px;
        width: 210px;
        height: 300px;
        text-align: left;
    }

    .backlog-incident h3 {
        margin-bottom: 26px;
    }

    .backlog-incident article {
        margin-top: 20px;
        height: 140px;
        padding-top: 20px;
    }

    .backlog-incident .backlog-content {
        position: relative;
        height: 110px;
    }

    .backlog-content, .backlog-content:after, .backlog-content:before {
        background-color: rgba(255, 255, 255, 0.1);
    }

    .backlog-content:after, .backlog-content:before {
        content: "";
        width: 100%;
        height: 15px;
        display: block;
        position: absolute;
        left: 0;
    }

    .backlog-content:after {
        background: linear-gradient(135deg, transparent 10px, rgba(255, 255, 255, 0.1) 0);
        top: -15px;
    }

    .backlog-content:before {
        background: linear-gradient(-45deg, transparent 10px, rgba(255, 255, 255, 0.1) 0);
        bottom: -15px;
    }
    /*待办事项-结束*/

    /*快捷面板*/
    .centre {
        left: 50%;
        width: 400px;
        height: 400px;
        background-color: salmon;
        margin: 20px 0 0 -240px;
    }

    /*快捷面板*/

    /*金额信息-开始*/
    .content-panel {
        top: 20px;
        right: 30px;
        width: 320px;
        height: 380px;
    }

    .content-panel header {
        font-size: 16px;
        line-height: 22px;;
    }

    .content-panel header span:before, .content-panel header span:after {
        content: "";
        display: inline-block;
        width: 64px;
        height: 10px;
        background-image: url("../assets/icon_nav.png");
    }

    .content-panel header span:before {
        background-position: -84px -53px;
        margin-right: 5px;
    }

    .content-panel header span:after {
        background-position: -84px -63px;
        margin-left: 5px;
    }

    .content-panel article {
        background: rgba(255, 255, 255, 0.1);
        position: relative;
        margin-top: 30px;
        min-height: 335px;
    }

    .content-panel article:before, .content-panel article:after {
        content: "";
        width: 100%;
        height: 15px;
        position: absolute;
        left: 0;
    }

    .content-panel article:before {
        background: linear-gradient(135deg, transparent 10px, rgba(255, 255, 255, 0.1) 0);
        top: -15px;
    }

    .content-panel article:after {
        background: linear-gradient(-45deg, transparent 10px, rgba(255, 255, 255, 0.1) 0);
        bottom: -15px;

    }

    /*金额信息-结束*/

    /*公司信息*/
    .footer-text {
        bottom: 0px;
        left: 50%;
        margin-left: -223px;
        color: rgba(255, 255, 255, 0.5);
        font-size: 14px;
    }
</style>
<template>
    <div id="homeContent">
        <!-- 地址 时间-->
        <div class="location-time">
            <span class="address" v-text="currentAddress"></span>
            <span class="c-data" v-text="getDate"></span>
        </div>
        <!-- 待办事项-->
        <div class="backlog-incident">
            <h3><span v-text="userName"></span>,<span v-text="getAmPm"></span></h3>

            <div>您的待办事项如下:</div>
            <article>
                <div class="backlog-content"></div>
            </article>
        </div>
        <!-- 快捷面板-->
        <div class="centre"></div>
        <!-- 金额信息-->
        <div class="content-panel">
            <header>
                <span v-text="getDay"></span>
            </header>
            <article></article>
        </div>
        <!-- 底部公司信息-->
        <div class="footer-text">Copyright © 北京企航金服科技有限公司 京ICP备17016903号</div>
    </div>
</template>
<script>
    export default {
        name: "Home",
        data: function () {
            return {
                currentAddress: "(北京)",
                userName: "(圈圈)",
                currentDate: new Date()
            }
        },
        methods: {
        },
        computed: {
            //获取具体时间
            getDate: function () {
                var currentDate = this.currentDate;
                var weekDay = ["星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"];
                var showDate = currentDate.getFullYear() + "-" + (currentDate.getMonth() + 1) + "-" + currentDate.getDate() + " " + weekDay[currentDate.getDay()];
                return showDate;
            },
            //获取日期
            getDay: function () {
                var currentDate = this.currentDate;
                var showDate = currentDate.getFullYear() + "年" + (currentDate.getMonth() + 1) + "月" + currentDate.getDate() + "日";
                return showDate;
            },
            //设置时间问候
            getAmPm: function () {
                var amOrPm = new Date().getHours();
                if (amOrPm < 6) {
                    return "晚上好";
                } else if (amOrPm < 12) {
                    return "上午好";
                } else if (amOrPm < 14) {
                    return "中午好";
                } else if (amOrPm < 18) {
                    return "下午好";
                } else if (amOrPm < 24) {
                    return "晚上好";
                }
            }
        }
    }
</script>
