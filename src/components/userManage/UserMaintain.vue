<style scoped>
    #userMaintain {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }
</style>

<template>
    <div id="userMaintain"></div>
</template>

<script>
    export default {
        name: "UserMaintain",
        created:function(){
            this.$emit("transmitTitle","用户维护");
        },
        data: function () {
            return {}
        },
        methods: {}
    }
</script>
