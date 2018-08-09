<style scoped lang="less" type="text/less">
    #todayDetail{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }
</style>

<template>
    <div id="todayDetail">

    </div>
</template>

<script>
    export default {
        name: "TodayDetail",
        created: function () {
            this.$emit('transmitTitle', '当日交易明细');
        },
    }
</script>
