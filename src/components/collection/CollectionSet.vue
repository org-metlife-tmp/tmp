<style scoped lang="less" type="text/less">
    #collectionSet {
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

        /*标签页*/
        .tab-content{
            width: 100%;
            height: 240px;
            border: 1px solid #e4e7ed;
            box-sizing: border-box;
            border-top: none;

            /*归集主账户选择*/
            .account-select{
                text-align: left;
                padding-left: 20px;
                padding-top: 15px;
                color: #676767;

                .el-input{
                    width: 40%;
                    margin-left: 10px;
                }
            }

            /*添加被归集账户*/
            .tab-add-collect{
                text-align: left;
                height: 30px;
                line-height: 30px;
                padding: 0 20px;
                background-color: #E9F2F9;
                margin-top: 10px;
                border-top: 1px solid #e4e7ed;
                color: #848484;

                i{
                    color:#00B3EC;
                    font-size: 22px;
                    vertical-align: middle;
                    background-color: #fff;
                    border-radius: 50%;
                    cursor: pointer;
                }

                div{
                    float: right;
                }
            }
        }
    }
</style>
<style lang="less" type="text/less">
    #collectionSet{
        .el-tabs__header{
            margin-bottom: 0;
        }
    }
</style>

<template>
    <div id="collectionSet">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="">打印</el-button>
        </div>
        <!--中间内容-->
        <section class="table-content">
            <el-form :model="collectionData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="14">
                        <el-form-item label="归集主题">
                            <el-input v-model="collectionData.acc_no" placeholder="请为本次归集主题命名以方便识别"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="14" style="text-align:left">
                        <el-form-item label="归集额度">
                            <el-radio-group v-model="collectionData.resource">
                                <el-radio label="1">全额归集</el-radio>
                                <el-radio label="2">定额归集</el-radio>
                                <el-radio label="3">留存余额归集</el-radio>
                            </el-radio-group>
                        </el-form-item>
                    </el-col>
                    <el-col :span="14" style="text-align:left">
                        <el-form-item label=" ">
                            <el-row>
                                <el-col :span="6">
                                    <el-input v-model="collectionData.acc_name" placeholder="请填写归集金额"></el-input>
                                </el-col>
                                <el-col :span="1" style="height:1px"></el-col>
                                <el-col :span="16" style="color:#676767">
                                    将归集账户内所有余额转入归集主账户
                                </el-col>
                            </el-row>
                        </el-form-item>
                    </el-col>
                    <el-col :span="20">
                        <el-form-item label="归集关系">
                            <el-tabs v-model="collectionData.activeName" type="card" closable
                                     @tab-remove="removeTab">
                                <el-tab-pane v-for="(item, index) in editableTabs"
                                             :key="item.name"
                                             :label="item.title"
                                             :name="item.name">
                                    <div class="tab-content">
                                        <div class="account-select">
                                            归集主账户<el-input v-model="collectionData.acc_name"></el-input>
                                        </div>
                                        <div class="tab-add-collect">
                                            <span>添加被归集账户</span>
                                            <i class="el-icon-circle-plus-outline"></i>
                                            <div>
                                                <span>全部清除</span>
                                                <span>被归集账户：</span>
                                                <span></span>
                                                <span>个</span>
                                            </div>
                                        </div>
                                    </div>
                                </el-tab-pane>
                            </el-tabs>
                        </el-form-item>
                    </el-col>
                    <el-col :span="14">
                        <el-form-item label="归集频率">
                            <el-input v-model="collectionData.acc_name"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
        </section>
    </div>
</template>

<script>
    export default {
        name: "CollectionSet",
        created: function () {
            this.$emit("transmitTitle", "自动归集设置");
        },
        data: function () {
            return {
                collectionData: {
                    activeName: "1"
                },
                formLabelWidth: "100px",
                editableTabs: [{
                    title: '01',
                    name: '1'
                },{
                    title: '02',
                    name: '2'
                }],
            }
        },
        methods: {
            removeTab(targetName) {
                let tabs = this.editableTabs;
                let activeName = this.collectionData.activeName;
                if (activeName === targetName) {
                    tabs.forEach((tab, index) => {
                        if (tab.name === targetName) {
                            let nextTab = tabs[index + 1] || tabs[index - 1];
                            if (nextTab) {
                                activeName = nextTab.name;
                            }
                        }
                    });
                }
                this.collectionData.activeName = activeName;
                this.editableTabs = tabs.filter(tab => tab.name !== targetName);
            }
        }
    }
</script>
