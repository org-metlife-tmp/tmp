<style scoped lang="less" type="text/less">
    #historyAll{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }
</style>

<template>
    <div id="historyAll"></div>
</template>

<script>
    export default {
        name: "HistoryAll",
        created: function () {
            //向父组件发送自己的信息
            this.$emit('transmitTitle', '历史交易汇总');
        },
    }
</script>


