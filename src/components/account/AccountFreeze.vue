<style scoped lang="less" type="text/less">
    #accountFreeze{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }
</style>

<template>
    <div id="accountFreeze">
    </div>
</template>

<script>
    export default {
        name: "AccountFreeze",
        created: function () {
            this.$emit("transmitTitle", "账户冻结申请");
        },
        data: function () {
            return {}
        },
        methods: {}
    }
</script>
