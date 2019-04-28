<style lang="less" type="text/less">
    #main {
        height: 100%;

        /**********
        修改框架样式-开始
        ***********/
        /*表格*/
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

        /*表单*/
        .el-form-item .el-input input {
            vertical-align: top;
        }

        /*布局*/
        .el-header {
            color: #333;
            text-align: center;
            height: 52px !important;
            padding: 0;
            width: 100%;
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
            overflow: hidden;
        }

        .el-main {
            background-color: #E7E7E7;
            text-align: center;
        }

        h1 {
            margin-top: 0;
            font-size: 18px;
            font-weight: 400;
        }

        .el-table .el-button {
            padding: 3px 3px;
            + .el-button {
                margin-left: 4px;
            }
        }
        /**********
         修改框架样式-结束
         ***********/

        /**********
         公共样式设置
        **********/
        .search-setion {
            text-align: left;

            /*时间控件*/
            .el-date-editor {
                width: 100%;
                max-width: 210px;
            }
        }

        .split-bar {
            width: 106%;
            height: 6px;
            margin-left: -20px;
            background-color: #E7E7E7;
            margin-bottom: 20px;
        }

        .allData {
            height: 36px;
            line-height: 36px;
            width: 100%;
            background-color: #F8F8F8;
            border: 1px solid #ebeef5;
            border-top: none;
            box-sizing: border-box;

            .btn-left {
                float: left;
                margin-left: 16px;
            }

            .btn-right {
                float: right;
                margin-right: 16px;
            }

            .numText {
                color: #FF5800;
                margin-right: 10px;
            }
        }

        .button-list-right {
            position: absolute;
            top: 18px;
            right: 0;
        }

        .button-list-left {
            position: absolute;
            top: 18px;
            left: 0;
        }
        /*********
         公共样式设置结束
        *********/

        #contented {
            > .el-main {
                padding-top: 0;
            }

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

        .header-none {
            height: 0 !important;
        }
    }
    /*弹出框公共样式设置*/
    .el-dialog {
        text-align: left;
        margin-bottom: 10px;
        /*设置标题*/
        .dialog-title {
            margin-bottom: 0;
        }
        .el-dialog__body {
            padding-top: 10px;
            padding-bottom: 0;
            max-height: 400px;
            overflow-y: auto;
        }
        .el-form {
            width: 96%;
            .el-select {
                width: 100%;
            }
        }
    }
</style>

<template>
    <el-container id="main">
        <el-header :class="{'header-none':!headerShow}">
            <Top></Top>
        </el-header>
        <el-container id="contented" :class="{'home-bgc':showBgc}">
            <el-aside width="70px">
                <Left></Left>
            </el-aside>
            <el-main :class="{'home-bgc-none':showBgc}">
                <router-view></router-view>
            </el-main>
            <div id="plugin" v-show="!showBgc">
                <div class="phone-version" style="visibility: hidden">手机版</div>
                <div :class="['show-header',{'icon-position':!headerShow}]" @click="setHeader"
                     v-text="topSet"></div>
            </div>
        </el-container>
        <GetSelectData></GetSelectData>
    </el-container>
</template>

<script>
    import Top from "./publicModule/Top.vue"
    import Left from "./publicModule/Left.vue"
    import GetSelectData from "./publicModule/GetSelectData.vue"

    export default {
        name: "",
        created: function () {
        },
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
            Left: Left,
            GetSelectData: GetSelectData
        }
    }
</script>
