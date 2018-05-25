<style scoped>
    #userMenu {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }
</style>

<template>
    <div id="userMenu"></div>
</template>

<script>
    export default {
        name: "UserMenu",
        created:function(){
            this.$emit("transmitTitle","用户菜单设置");
        },
        data: function () {
            return {}
        },
        methods: {}
    }
</script>
