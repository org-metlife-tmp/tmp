<style scoped lang="less" type="text/less">
    #todayAll{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }
</style>

<template>
    <div id="todayAll"></div>
</template>

<script>
    export default {
        name: "TodayAll",
        created: function () {
            //向父组件发送自己的信息
            this.$emit('transmitTitle', '当日交易汇总');
        },
    }
</script>
