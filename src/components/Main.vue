<style lang="less" type="text/less">
    #main {
        height: 100%;

        .header-none {
            height: 0 !important;
        }
        .padding-none {
            padding-top: 0;
        }
    }

    /**********
    修改框架样式-开始
    ***********/
    .el-table th {
        background: #E9F2F9;
        text-align: center;
    }

    .el-table td {
        text-align: left;
    }

    .el-table {
        font-size: 14px;
    }

    .el-header {
        background-color: #B3C0D1;
        color: #333;
        text-align: center;
        height: 52px !important;
        padding: 0;
        width: 100%;
        position: fixed;
        overflow: hidden;
        transition: height 0.5s;
    }

    .el-aside {
        color: #333;
        text-align: center;
        line-height: 200px;
        height: 100%;
        padding-bottom: 40px;
        background-color: rgba(255, 255, 255, 0.1);
    }

    .el-main {
        background-color: #E7E7E7;
        text-align: center;
        height: 100%;
    }

    h1 {
        margin-top: 0;
        font-size: 18px;
        font-weight: 400;
    }

    #main .el-container {
        height: 100%;
        transition: padding-top 0.5s;
    }

    .el-container:nth-child(5) .el-aside,
    .el-container:nth-child(6) .el-aside {
        line-height: 260px;
    }

    .el-container:nth-child(7) .el-aside {
        line-height: 320px;
    }

    .el-aside {
        overflow: hidden;
    }

    /**********
     修改框架样式-结束
     ***********/

    #contented {
        padding-top: 52px;
        #plugin {
            position: fixed;
            width: 60px;
            height: 150px;
            bottom: 15px;
            right: 15px;
            div {
                width: 100%;
                box-sizing: border-box;
                background-color: #fff;
                padding: 5px;
                height: 56px;
                cursor: pointer;
                font-size: 12px;
                padding-bottom: 54px;
                text-align: center;
            }
            div:hover {
                background-color: #00B3ED;
                color: #fff;
            }

            .show-header {
                margin-top: 10px;
            }
            .phone-version:before, .show-header:before {
                content: "";
                display: block;
                width: 50px;
                height: 34px;
                background: url(../assets/icon_nav.png) no-repeat;
            }
            .phone-version:before {
                background-position: 10px -180px;
            }
            .phone-version:hover:before {
                background-position: 10px -145px;
            }
            .show-header:before {
                background-position: -32px -178px;
            }
            .show-header:hover:before {
                background-position: -32px -144px;
            }
            .icon-position:before {
                background-position: -78px -178px;
            }
            .icon-position:hover:before {
                background-position: -78px -144px;
            }
        }
    }

    /*首页背景设置*/
    .home-bgc {
        background: url("../assets/index_bg_max.png") no-repeat;
        background-size: 100% 100%;
    }

    .home-bgc-none {
        background-color: rgba(0, 0, 0, 0);
    }
</style>

<template>
    <div id="main">
        <el-container>
            <el-header :class="{'header-none':!headerShow}">
                <Top></Top>
            </el-header>
            <el-container id="contented" :class="{'home-bgc':showBgc,'padding-none':!headerShow}">
                <el-aside width="70px">
                    <Left></Left>
                </el-aside>
                <el-main :class="{'home-bgc-none':showBgc}">
                    <router-view></router-view>
                </el-main>
                <div id="plugin" v-show="!showBgc">
                    <div class="phone-version">手机版</div>
                    <div :class="['show-header',{'icon-position':!headerShow}]" @click="setHeader"
                         v-text="topSet"></div>
                </div>
            </el-container>
        </el-container>
    </div>
</template>

<script>
    import Top from "./publicModule/Top.vue"
    import Left from "./publicModule/Left.vue"

    export default {
        name: "",
        data: function () {
            return {
                headerShow: true,
                topSet: "隐藏顶部"
            }
        },
        methods: {
            setHeader: function () {
                if (this.headerShow) {
                    this.headerShow = false;
                    this.topSet = "显示顶部";
                } else {
                    this.headerShow = true;
                    this.topSet = "隐藏顶部";
                }
            }
        },
        computed: {
            showBgc: function () {
                var pathName = this.$route.name;
                if (!pathName || pathName == "Home") {
                    return true;
                } else {
                    return false;
                }
            }
        },
        components: {
            Top: Top,
            Left: Left
        }
    }
</script>
