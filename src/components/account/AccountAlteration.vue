<style scoped lang="less" type="text/less">
    #accountAlteration{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }
</style>

<template>
    <div id="accountAlteration">
    </div>
</template>

<script>
    export default {
        name: "AccountAlteration",
        created: function () {
            this.$emit("transmitTitle", "账户变更申请");
        },
        data: function () {
            return {}
        },
        methods: {}
    }
</script>
