<style scoped lang="less" type="text/less">
    #historyDetail{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }
</style>

<template>
    <div id="historyDetail"></div>
</template>

<script>
    export default {
        name: "HistoryDetail",
        created: function () {
            //向父组件发送自己的信息
            this.$emit('transmitTitle', '历史交易明细');
        },
    }
</script>

