<style scoped lang="less" type="text/less">
    #autoAllocationManage{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*顶部按钮*/
        .button-list-right {
            position: absolute;
            top: -60px;
            right: -18px;
        }
        /*顶部搜索框*/
        // .search-list-left{
        //     position: absolute;
        //     top: -60px;
        //     left: -20px; 
        // }

        /*搜索区*/
        .search-setion {
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
        .icon-img{
            background: url(../../assets/icon_common.png);
        }

        /*数据展示区*/
        .detail-content{
            height: 365px;
            overflow-y: auto;
            >div{
                display: flex;
                justify-content: space-between;
                flex-wrap: wrap;
            }
            .bill-box{
                width: 256px;
                height: 360px;
                float: left;
                background: #fff;
                margin-bottom: 20px;
                position: relative;
                overflow: hidden;
                box-shadow: 1px 2px 5px #CCCCCC;
                .bill-top{
                    background: white;
                    height: 28px;
                    line-height: 28px;
                    .batch-name {
                        width: 120px;
                        text-align: left;
                        padding: 0 10px;
                        overflow: hidden;
                        float: left;
                    }
                    .bill-btn{
                        float: right;
                        i{
                            width: 24px;
                            height: 28px;
                            display: inline-block;
                            cursor: pointer;
                        }
                        .active{
                            background-position: -89px -74px;
                        }
                        .pause{
                            background-position: -187px -74px;
                        }
                        .del{
                            background-position: -43px -48px;
                        }
                        .view{
                            background-position: -140px -74px;
                        }
                    }
                }
                .bill-gray{
                    height: 120px;
                    background: #F7F7F7;
                    .bill-amount{
                        padding: 30px 0 15px 0;
                        text-align: center;
                        color: #FF5800;
                        font-size: 18px;
                    }
                    .bill-type-box{
                        padding: 0 0 15px 0px;
                        text-align: center;
                        position: relative;
                        .bill-type {
                            width: 54px;
                            margin: 0 10px 0 5px;
                        }
                        .bill-num {
                            width: 74px;
                            margin: 0 10px 0 5px;
                        }
                        .bill-status{
                            position: absolute;
                            width: 56px;
                            height: 56px;
                            border: 2px solid white;
                            border-radius: 50%;
                            top: 25px;
                            left: 100px;
                            .bill-icon{
                                width: 55px;
                                height: 55px;
                                border: 1px solid #F1F1F1;
                                border-radius: 50%;
                                background: #F7F7F7;
                                .iconStyle{
                                    width: 100%;
                                    height: 100%;
                                    border-radius: 50%;
                                }
                                .icon-position{
                                    background-position: -434px -305px;
                                }
                            }
                        }
                    }
                }
                .collectWay{
                    margin-top: 45px;
                    height: 25px;
                    padding: 0 10px;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                    text-align: center;
                }
                .collectTime{
                    height: 25px;
                    text-align: center;
                    margin: 0 10px;
                    padding-bottom: 15px;
                    border-bottom: 1px solid #ccc;
                    background-color: white;
                }
                .acc-box{
                    width: 232px;
                    height: 70px;
                    padding: 15px 0 15px 8px;
                    .acc-detail{
                        height: 30px;
                        line-height: 30px;
                        font-size: 12px;
                    }
                }
            }
        }
    }
</style>
<style lang="less">
    #autoAllocationManage {
        .el-dialog__wrapper {
            .el-dialog__body {
                height: 300px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="autoAllocationManage">
        <!-- 顶部查询 -->
        <!-- <div class="search-list-left">
            <el-select v-model="searchData.bbb" placeholder="请选择下拨额度" clearable size="mini">
                <el-option v-for="bankType in bankList"
                        :key="bankType.id"
                        :label="bankType.name"
                        :value="bankType.id">
                </el-option>
            </el-select>
            <el-select v-model="searchData.aaa" placeholder="请选择下拨频率" clearable size="mini">
                <el-option v-for="bankType in bankList"
                        :key="bankType.id"
                        :label="bankType.name"
                        :value="bankType.id">
                </el-option>
            </el-select>
        </div> -->
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="">下载</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="5">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" clearable placeholder="请输入归集关键字"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="5">
                        <el-form-item>
                            <el-input v-model="searchData.query_key" clearable placeholder="请输入归集主账号关键字"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="5">
                        <el-form-item>
                            <el-select v-model="searchData.bbb" placeholder="请选择下拨额度" clearable size="mini">
                                <el-option v-for="(name,k) in poolTypeList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="5">
                        <el-form-item>
                            <el-select v-model="searchData.bbb" placeholder="请选择下拨频率" clearable size="mini">
                                <el-option v-for="(name,k) in pollFrequencyList"
                                           :key="k"
                                           :label="name"
                                           :value="k">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item style="margin-bottom:0px">
                            <el-checkbox-group v-model="searchData.service_status">
                                <el-checkbox label="3" name="type">全部</el-checkbox>
                                <el-checkbox label="12" name="type">已激活</el-checkbox>
                                <el-checkbox label="1" name="type">未激活</el-checkbox>
                            </el-checkbox-group>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section class="detail-content">
            <div>
                <div class="bill-box">
                    <div class="bill-top">
                        <div class="batch-name">test</div>
                        <div class="bill-btn">
                            <i class="icon-img active" title="激活"></i>
                            <i class="icon-img pause" title="暂停"></i>
                            <i class="icon-img del" title="作废"></i>
                            <i class="icon-img view" title="查看"></i>
                        </div>
                    </div>
                    <div class="bill-gray">
                        <div class="bill-amount">￥0.00</div>
                        <div class="bill-type-box">
                            <span class="bill-type">全额归集</span>
                            <span class="bill-num">111</span>
                            <div class="bill-status">
                                <div class="bill-icon">
                                    <div class="icon-img iconStyle icon-position"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="collectWay">每天</div>
                    <div class="collectTime">01:05</div>
                    <div class="acc-box">
                        <div class="acc-detail">
                            <span class="accNo">11087363445533344</span>
                            （子账户<span class="accChildNo">1</span>个）
                        </div>
                    </div>
                </div>
                <div class="bill-box">
                    <div class="bill-top">
                        <div class="batch-name">test</div>
                        <div class="bill-btn">
                            <i class="icon-img active" title="激活"></i>
                            <i class="icon-img pause" title="暂停"></i>
                            <i class="icon-img del" title="作废"></i>
                            <i class="icon-img view" title="查看" @click="lookDetail"></i>
                        </div>
                    </div>
                    <div class="bill-gray">
                        <div class="bill-amount">￥0.00</div>
                        <div class="bill-type-box">
                            <span class="bill-type">全额归集</span>
                            <span class="bill-num">111</span>
                            <div class="bill-status">
                                <div class="bill-icon">
                                    <div class="icon-img iconStyle icon-position"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="collectWay">每天</div>
                    <div class="collectTime">01:05</div>
                    <div class="acc-box">
                        <div class="acc-detail">
                            <span class="accNo">11087363445533344</span>
                            （子账户<span class="accChildNo">1</span>个）
                        </div>
                    </div>
                </div>
                <div class="bill-box">
                    <div class="bill-top">
                        <div class="batch-name">test</div>
                        <div class="bill-btn">
                            <i class="icon-img active" title="激活"></i>
                            <i class="icon-img pause" title="暂停"></i>
                            <i class="icon-img del" title="作废"></i>
                            <i class="icon-img view" title="查看"></i>
                        </div>
                    </div>
                    <div class="bill-gray">
                        <div class="bill-amount">￥0.00</div>
                        <div class="bill-type-box">
                            <span class="bill-type">全额归集</span>
                            <span class="bill-num">111</span>
                            <div class="bill-status">
                                <div class="bill-icon">
                                    <div class="icon-img iconStyle icon-position"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="collectWay">每天</div>
                    <div class="collectTime">01:05</div>
                    <div class="acc-box">
                        <div class="acc-detail">
                            <span class="accNo">11087363445533344</span>
                            （子账户<span class="accChildNo">1</span>个）
                        </div>
                    </div>
                </div>
                <div class="bill-box">
                    <div class="bill-top">
                        <div class="batch-name">test</div>
                        <div class="bill-btn">
                            <i class="icon-img active" title="激活"></i>
                            <i class="icon-img pause" title="暂停"></i>
                            <i class="icon-img del" title="作废"></i>
                            <i class="icon-img view" title="查看"></i>
                        </div>
                    </div>
                    <div class="bill-gray">
                        <div class="bill-amount">￥0.00</div>
                        <div class="bill-type-box">
                            <span class="bill-type">全额归集</span>
                            <span class="bill-num">111</span>
                            <div class="bill-status">
                                <div class="bill-icon">
                                    <div class="icon-img iconStyle icon-position"></div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="collectWay">每天</div>
                    <div class="collectTime">01:05</div>
                    <div class="acc-box">
                        <div class="acc-detail">
                            <span class="accNo">11087363445533344</span>
                            （子账户<span class="accChildNo">1</span>个）
                        </div>
                    </div>
                </div>
            </div>
        </section>
    </div>
</template>

<script>
    export default {
        name: "AutoAllocationManage",
        created: function () {
            this.$emit("transmitTitle", "自动下拨管理");
        },
        mounted: function () {
            //下拨频率
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            if (constants.PollingFrequency) {
                this.pollFrequencyList = constants.PollingFrequency;
            }
            //下拨额度
            if (constants.PoolingType) {
                this.poolTypeList = constants.PoolingType;
            }
            
        },
        props:["tableData"],
        data: function () {
            return {
                searchData: {
                    service_status: []
                },
                bankList:[
                    {
                        id:"1",
                        name:"定额归集"
                    }
                ],
                pollFrequencyList: [],//下拨频率
                poolTypeList: [],//下拨额度
                detailList: [],
            }
        },
        methods: {
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;

                for (var k in searchData) {
                    if (this.isPending) {
                        this.routerMessage.todo.params[k] = searchData[k];
                        this.routerMessage.todo.params.page_num = 1;
                    } else {
                        this.routerMessage.done.params[k] = searchData[k];
                        this.routerMessage.done.params.page_num = 1;
                    }
                }
            },
            //查看详情
            lookDetail: function () {
                debugger
                this.$router.push("/allot/more-bills");
            }
        },
        watch:{
            tableData: function (val, oldVal) {
                
                // this.tableList = val.data;
            }
        }
    }
</script>

