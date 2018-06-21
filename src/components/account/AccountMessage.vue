<style scoped lang="less" type="text/less">
    #accountMessage{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }
</style>

<template>
    <div id="accountMessage">
    </div>
</template>

<script>
    export default {
        name: "AccountMessage",
        created: function () {
            this.$emit("transmitTitle", "账户信息维护");
        },
        data: function () {
            return {}
        },
        methods: {}
    }
</script>
