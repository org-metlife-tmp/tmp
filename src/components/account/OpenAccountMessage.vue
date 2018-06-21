<style scoped lang="less" type="text/less">
    #openAccountMessage{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }
</style>

<template>
    <div id="openAccountMessage">
    </div>
</template>

<script>
    export default {
        name: "OpenAccountMessage",
        created: function () {
            this.$emit("transmitTitle", "开户信息补录");
        },
        data: function () {
            return {}
        },
        methods: {}
    }
</script>
