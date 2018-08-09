<style scoped lang="less" type="text/less">
    #historyFluctuate{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }
</style>

<template>
    <div id="historyFluctuate"></div>
</template>

<script>
    export default {
        name: "HistoryFluctuate",
        created: function () {
            //向父组件发送自己的信息
            this.$emit('transmitTitle', '历史交易波动');
        },
    }
</script>



