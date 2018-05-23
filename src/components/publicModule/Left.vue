<style lang='less'>
    /*框架样式修改*/
    .el-menu-vertical-demo:not(.el-menu--collapse) {
        width: 100%;
        min-height: 400px;
    }

    .el-menu-vertical-demo {
        width: 100%;
    }

    .el-submenu__title {
        height: 70px;
        padding-top: 12px;
    }

    .el-menu--vertical .el-menu {
        margin: 0;
        background-color: #48AEDF;
        min-width: 150px;
        width: 150px;
    }

    .el-menu--vertical .el-menu .el-menu-item-group__title {
        padding: 0;
    }

    .el-menu--vertical .el-menu ul li {
        height: 30px;
        line-height: 30px;
        color: rgba(255, 255, 255, 0.8);
    }

    .el-menu--vertical .el-menu ul li:hover {
        background-color: rgba(23, 144, 201, 1);
        color: #fff;
    }

    .menuList .el-submenu__title:hover {
        background-color: #48AEDF;
    }

    /*框架样式修改结束*/

    /*整体样式*/
    #menuContent {
        width: 87px;
        height: 100%;
        overflow-y: scroll;
        overflow-x: hidden;
    }

    .menuList {
        background-color: rgba(255, 255, 255, 0);
    }

    .menuList i {
        content: "";
        width: 26px;
        height: 26px;
        margin: 3px auto 0;
        display: block;
        background-image: url("../../assets/icon_nav.png");
    }

    .menuList .el-submenu__title p {
        color: rgba(255, 255, 255, 0.5);
        height: 20px;
        line-height: 20px;
        margin-top: 4px;
        margin-left: -6px;
        font: 14px "Microsoft YaHei";
    }

    .menuList .el-submenu__title .mg-four {
        margin-left: -14px;
    }

    /*整体样式结束*/

    /*首页背景图定位*/
    .menuList .icon-sjgl {
        background-position: -30px -60px;
    }

    .menuList .icon-yet {
        background-position: -30px 0;
    }

    .menuList .icon-jyt {
        background-position: -30px -29px;
    }

    .menuList .icon-dbt {
        background-position: -170px -180px;
    }

    .menuList .icon-zft {
        background-position: -168px -220px;
    }

    .menuList .icon-gjt {
        background-position: -103px -116px;
    }

    .menuList .icon-ywfk {
        background-position: -31px -219px;
    }

    .menuList .icon-zht {
        background-position: -249px -193px;
    }

    .menuList .icon-zjys {
        background-position: -32px 0px;
    }

    .menuList .icon-sppt {
        background-position: -102px -86px;
    }

    .menuList .icon-bb {
        background-position: -30px -55px;
    }

    .menuList .icon-oa {
        background-position: -506px -190px;
    }

    /*首页背景图定位结束*/

    /*标签页样式设置*/
    .noBgc .icon-sjgl {
        background-position: 0 -60px;
    }

    .noBgc .icon-yet {
        background-position: 0px 0;
    }

    .noBgc .icon-jyt {
        background-position: 0 -29px;
    }

    .noBgc .icon-dbt {
        background-position: -138px -180px;
    }

    .noBgc .icon-zft {
        background-position: -137px -220px;
    }

    .noBgc .icon-gjt {
        background-position: -69px -116px;
    }

    .noBgc .icon-ywfk {
        background-position: 0px -219px;
    }

    .noBgc .icon-zht {
        background-position: -213px -193px;
    }

    .noBgc .icon-zjys {
        background-position: 0px 0px;
    }

    .noBgc .icon-sppt {
        background-position: -68px -86px;
    }

    .noBgc .icon-bb {
        background-position: 0 -55px;
    }

    .noBgc .icon-oa {
        background-position: -506px -158px;
    }

    .noBgc .el-submenu__title p {
        color: rgba(0, 0, 0, 0.5);
    }

    .noBgc .el-submenu__title:hover .icon-sjgl{
        background-position: -30px -60px;
    }

    .noBgc .el-submenu__title:hover .icon-yet {
        background-position: -30px 0;
    }

    .noBgc .el-submenu__title:hover .icon-jyt {
        background-position: -30px -29px;
    }

    .noBgc .el-submenu__title:hover .icon-dbt {
        background-position: -170px -180px;
    }

    .noBgc .el-submenu__title:hover .icon-zft {
        background-position: -168px -220px;
    }

    .noBgc .el-submenu__title:hover .icon-gjt {
        background-position: -103px -116px;
    }

    .noBgc .el-submenu__title:hover .icon-ywfk {
        background-position: -31px -219px;
    }

    .noBgc .el-submenu__title:hover .icon-zht {
        background-position: -249px -193px;
    }

    .noBgc .el-submenu__title:hover .icon-zjys {
        background-position: -32px 0px;
    }

    .noBgc .el-submenu__title:hover .icon-sppt {
        background-position: -102px -86px;
    }

    .noBgc .el-submenu__title:hover .icon-bb {
        background-position: -30px -55px;
    }

    .noBgc .el-submenu__title:hover .icon-oa {
        background-position: -506px -190px;
    }

    .noBgc .el-submenu__title:hover p {
        color: #fff;
    }

</style>

<template>
    <div id="menuContent">
        <el-menu default-active="1-12-1" :class="['el-menu-vertical-demo', 'menuList', {'noBgc':showBgc}]"
                 :collapse="isCollapse" :router="true">
            <el-submenu index="1">
                <template slot="title" height="200px">
                    <i class="icon-sjgl"></i>
                    <p class="mg-four">数据管理</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/data-manage/basic-data">基础数据维护</el-menu-item>
                    <el-menu-item index="/data-manage/bank-account">银行账户管理</el-menu-item>
                    <el-menu-item index="1-3">接口管理</el-menu-item>
                    <el-menu-item index="1-4">清算通道管理</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="2">
                <template slot="title" height="200px">
                    <i class="icon-yet"></i>
                    <p>余额通</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/whiteContent/today-detail">当日余额明细</router-link></el-menu-item>
                    <el-menu-item index="/whiteContent/today-all">当日余额汇总</el-menu-item>
                    <el-menu-item index="/whiteContent/today-fluctuate">当日余额波动</el-menu-item>
                    <el-menu-item index="/whiteContent/history-detail">历史余额明细</el-menu-item>
                    <el-menu-item index="/whiteContent/history-all">历史余额汇总</el-menu-item>
                    <el-menu-item index="/whiteContent/history-fluctuate">历史余额波动</el-menu-item>
                    <el-menu-item index="1-8">余额数据导入</el-menu-item>
                </el-menu-item-group>
                <!--<el-submenu index="1-4">
                    <span slot="title">选项4</span>
                    <el-menu-item index="1-4-1">选项1</el-menu-item>
                </el-submenu>-->
            </el-submenu>
            <el-submenu index="3">
                <template slot="title" height="200px">
                    <i class="icon-jyt"></i>
                    <p>交易通</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="1-1">选项1</el-menu-item>
                    <el-menu-item index="1-2">选项2</el-menu-item>
                    <el-menu-item index="1-3">选项3</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="4">
                <template slot="title" height="200px">
                    <i class="icon-dbt"></i>
                    <p>调拨通</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="/makeBill">内部调拨-制单</el-menu-item>
                    <el-menu-item index="1-2">内部调拨-支付</el-menu-item>
                    <el-menu-item index="1-3">内部调拨-查看</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="5">
                <template slot="title" height="200px">
                    <i class="icon-zft"></i>
                    <p>支付通</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="1-1">选项1</el-menu-item>
                    <el-menu-item index="1-2">选项2</el-menu-item>
                    <el-menu-item index="1-3">选项3</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="6">
                <template slot="title" height="200px">
                    <i class="icon-gjt"></i>
                    <p>归集通</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="1-1">选项1</el-menu-item>
                    <el-menu-item index="1-2">选项2</el-menu-item>
                    <el-menu-item index="1-3">选项3</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="7">
                <template slot="title" height="200px">
                    <i class="icon-ywfk"></i>

                    <p class="mg-four">业务付款</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="1-1">选项1</el-menu-item>
                    <el-menu-item index="1-2">选项2</el-menu-item>
                    <el-menu-item index="1-3">选项3</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="8">
                <template slot="title" height="200px">
                    <i class="icon-zht"></i>

                    <p>账户通</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="1-1">选项1</el-menu-item>
                    <el-menu-item index="1-2">选项2</el-menu-item>
                    <el-menu-item index="1-3">选项3</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="9">
                <template slot="title" height="200px">
                    <i class="icon-zjys"></i>

                    <p class="mg-four">资金预算</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="1-1">选项1</el-menu-item>
                    <el-menu-item index="1-2">选项2</el-menu-item>
                    <el-menu-item index="1-3">选项3</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="10">
                <template slot="title" height="200px">
                    <i class="icon-sppt"></i>

                    <p class="mg-four">审批平台</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="1-1">选项1</el-menu-item>
                    <el-menu-item index="1-2">选项2</el-menu-item>
                    <el-menu-item index="1-3">选项3</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="11">
                <template slot="title" height="200px">
                    <i class="icon-bb"></i>

                    <p>报表</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="1-1">选项1</el-menu-item>
                    <el-menu-item index="1-2">选项2</el-menu-item>
                    <el-menu-item index="1-3">选项3</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
            <el-submenu index="12">
                <template slot="title" height="200px">
                    <i class="icon-oa"></i>

                    <p class="mg-four">OA审批</p>
                </template>
                <el-menu-item-group>
                    <el-menu-item index="1-1">选项1</el-menu-item>
                    <el-menu-item index="1-2">选项2</el-menu-item>
                    <el-menu-item index="1-3">选项3</el-menu-item>
                </el-menu-item-group>
            </el-submenu>
        </el-menu>
    </div>
</template>

<script>
    export default {
        name: "Left",
        data: function () {
            return {
                isCollapse: true
            }
        },
        methods: {},
        computed: {
            showBgc: function () {
                var currentPath = this.$route.name;
                if (!currentPath || currentPath == "Home") {
                    return false;
                } else {
                    return true;
                }
            }
        }
    }
</script>
