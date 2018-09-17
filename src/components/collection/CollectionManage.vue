<style scoped lang="less" type="text/less">
    #collectionManage{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion{
            text-align: left;
        }

        /*分隔栏*/
        .split-bar {
            width: 106%;
            height: 6px;
            margin-left: -20px;
            background-color: #E7E7E7;
            margin-bottom: 20px;
        }

        /*数据展示区*/
        .table-content{
            text-align: left;
            height: 76%;
            overflow-y: auto;

            .el-card{
                width: 92%;
                margin-bottom: 20px;

                /*顶部按钮*/
                .title-btn{
                    float: right;
                    margin-top: -4px;

                    .icon-img{
                        width: 24px;
                        height: 28px;
                        display: inline-block;
                        cursor: pointer;
                        background: url(../../assets/icon_common.png);
                    }
                    i:nth-child(1){
                        background-position: -89px -74px;
                    }
                    i:nth-child(2){
                        background-position: -187px -74px;
                    }
                    i:nth-child(3){
                        background-position: -43px -48px;
                    }
                    i:nth-child(4){
                        background-position: -140px -74px;
                    }
                }

                /*卡片内容*/
                .card-content{
                    text-align: center;

                    .content-top{
                        height: 110px;
                        background-color: #F7F7F7;

                        .collect-money{
                            padding: 20px 0 15px 0;
                            color: #FF5800;
                            font-size: 18px;
                        }

                        .collect-status{
                            width: 56px;
                            height: 56px;
                            margin: 10px auto;
                            border-radius: 50%;
                            background-color: #fff;
                            padding-top: 2px;

                            div{
                                width: 52px;
                                height: 52px;
                                box-sizing: border-box;
                                border: 1px solid #F1F1F1;
                                border-radius: 50%;
                                background: #F7F7F7;
                                margin: 0 auto;

                                .status-icon{
                                    display: inline-block;
                                    width: 52px;
                                    height: 52px;
                                    background: url(../../assets/icon_common.png);
                                    background-position: -388px -308px;
                                }
                            }
                        }
                    }

                    .content-center{
                        height: 60px;
                        width: 90%;
                        margin: 50px auto 10px;
                        border-bottom: 1px solid #ccc;

                        span{
                            display: block;
                            margin-top: 6px;
                        }
                    }

                    .content-bottom{
                        width: 90%;
                        margin: 0 auto;
                        text-align: left;
                        padding-bottom: 60px;
                    }
                }
            }
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

    }
</style>
<style lang="less" type="text/less">
    #collectionManage{
        .el-card{
            .el-card__header{
                padding: 8px 14px;
            }
            .el-card__body{
                padding: 0;
            }
        }
    }
</style>

<template>
    <div id="collectionManage">
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" clearable placeholder="归集额度"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" clearable placeholder="归集频率"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" clearable placeholder="请输入归集关键字"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" clearable placeholder="请输入归集主账号关键字"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <el-button type="primary" plain @click="" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item style="margin-bottom:0px">
                            <el-checkbox-group v-model="searchData.service_status">
                                <el-checkbox v-for="(name,k) in statusList"
                                             :label="k" name="type" :key="k">
                                    {{ name }}
                                </el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section class="table-content">
            <el-row>
                <el-col :span="6" v-for="card in tableList" :key="card.id">
                    <el-card class="box-card">
                        <div slot="header">
                            <span>{{ card.name }}</span>
                            <div class="title-btn">
                                <i class="icon-img" title="激活"></i>
                                <i class="icon-img" title="暂停"></i>
                                <i class="icon-img" title="作废"></i>
                                <i class="icon-img" title="查看"></i>
                            </div>
                        </div>
                        <div class="card-content">
                            <div class="content-top">
                                <div class="collect-money">￥0.00</div>
                                <div class="collect-way">全额归集</div>
                                <div class="collect-status">
                                    <div>
                                        <i class="status-icon"></i>
                                    </div>
                                </div>
                            </div>
                            <div class="content-center">
                                <span>每天</span>
                                <span>01:04</span>
                            </div>
                            <div class="content-bottom">
                                11087363445533344 (子账户 1个)
                            </div>
                        </div>
                    </el-card>
                </el-col>
            </el-row>
        </section>
    </div>
</template>

<script>
    export default {
        name: "CollectionManage",
        created: function () {
            this.$emit("transmitTitle", "自动归集管理");
        },
        data:function(){
            return {
                searchData:{ //搜索条件

                },
                statusList: {
                    1: "已激活",
                    0: "未激活"
                },
                tableList: [
                    {id:1,name:"lala"},
                    {id:2,name:"lala"},
                    {id:3,name:"lala"},
                    {id:4,name:"lala"},
                    {id:5,name:"lala"},
                    {id:6,name:"lala"},
                    {id:7,name:"lala"},
                    {id:8,name:"lala"},
                    {id:9,name:"lala"},
                    {id:0,name:"lala"},
                ], //列表数据
            }
        },
        methods: {
            //换页后获取数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getCommTable", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.params = {
                    page_size: val,
                    page_num: 1
                };
                this.$emit("getCommTable", this.routerMessage);
            },
        }
    }
</script>

