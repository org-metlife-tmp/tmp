<style scoped lang="less" type="text/less">
    #theOffer{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }
</style>

<template>
    <div id="theOffer">

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
        },
        data: function () {
            return {
                workdayData: [], //日历数据
                yearList: [],
                currentYear: ""
            }
        },
    }
</script>

