<style scoped>
    #userGroup {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }
</style>

<template>
    <div id="userGroup"></div>
</template>

<script>
    export default {
        name: "UserGroup",
        created:function(){
            this.$emit("transmitTitle","用户组设置");
        },
        data: function () {
            return {}
        },
        methods: {}
    }
</script>
