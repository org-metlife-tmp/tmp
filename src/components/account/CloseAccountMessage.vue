<style scoped lang="less" type="text/less">
    #closeAccountMessage{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }
</style>

<template>
    <div id="closeAccountMessage">
    </div>
</template>

<script>
    export default {
        name: "CloseAccountMessage",
        created: function () {
            this.$emit("transmitTitle", "销户信息补录");
        },
        data: function () {
            return {}
        },
        methods: {}
    }
</script>
