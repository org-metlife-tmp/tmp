<style scoped lang="less" type="text/less">
    #tabContent {
        width: 80%;
        height: 100%;
        margin: 0 auto;
        min-width: 800px;
        position: relative;

        //标签页按钮
        .tab-left {
            position: absolute;
            top: 8px;
            height: 30px;

            ul{
                li{
                    float: left;
                    height: 100%;
                    line-height: 30px;
                    width: 100px;
                    background-color: #fff;
                    border-radius: 3px 3px 0 0;
                    cursor: pointer;

                    i{
                        font-size: 20px;
                        vertical-align: middle;
                    }
                }
                li:nth-child(1){
                    margin-right: 4px;
                }
            }
        }

        .content {
            width: 100%;
            height: 90%;
            min-height: 500px;
            box-sizing: border-box;
            position: relative;
            padding: 20px;
            background-color: #fff;
        }
    }
</style>

<template>
    <div id="tabContent">
        <header>
            <h1 v-text="currentTitle"></h1>
        </header>
        <!--标签页按钮-->
        <div class="tab-left">
            <ul>
                <li>
                    <i class="el-icon-time"></i>
                    待处理
                </li>
                <li>
                    <i class="el-icon-circle-check-outline"></i>
                    已处理
                </li>
            </ul>
        </div>
        <section class="content">

        </section>
    </div>
</template>

<script>
    export default {
        name: "TabContent",
        data: function () {
            return {
                currentTitle: "啦啦啦"
            }
        },
        methods: {}
    }
</script>
