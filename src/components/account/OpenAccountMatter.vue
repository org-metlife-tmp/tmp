<style scoped lang="less" type="text/less">
    #openAccountMatter{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
    }

</style>

<template>
    <div id="openAccountMatter">
        <el-tabs type="card">
            <el-tab-pane label="用户管理"></el-tab-pane>
            <el-tab-pane label="配置管理"></el-tab-pane>
            <el-tab-pane label="角色管理"></el-tab-pane>
            <el-tab-pane label="定时任务补偿"></el-tab-pane>
        </el-tabs>
    </div>
</template>

<script>
    export default {
        name: "OpenAccountMatter",
        created: function () {
            this.$emit("transmitTitle", "开户事项补录");
        },
        data: function () {
            return {}
        },
        methods: {}
    }
</script>
