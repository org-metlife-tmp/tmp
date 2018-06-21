<style scoped lang="less" type="text/less">
    #closeAccountMatter{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }
</style>

<template>
    <div id="closeAccountMatter">
    </div>
</template>

<script>
    export default {
        name: "CloseAccountMatter",
        created: function () {
            this.$emit("transmitTitle", "销户事项申请");
        },
        data: function () {
            return {}
        },
        methods: {}
    }
</script>
