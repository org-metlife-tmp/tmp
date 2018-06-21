<style scoped lang="less" type="text/less">
    #accountUnfreeze{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }
</style>

<template>
    <div id="accountUnfreeze">
    </div>
</template>

<script>
    export default {
        name: "AccountUnfreeze",
        created: function () {
            this.$emit("transmitTitle", "账户解冻申请");
        },
        data: function () {
            return {}
        },
        methods: {}
    }
</script>
