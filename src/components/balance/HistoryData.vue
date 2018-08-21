<style scoped lang="less" type="text/less">
    #historyData {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;
        .modeUpload{
            position: absolute;
            width: 100%;
            top: 200px;
        }
        .title-text{
            font-size: 14px;
            float: left;
            width: 200px;
            text-align: right;
            padding-right: 15px;
            line-height: 40px;
        }
        .btnList{
            position: absolute;
            top: 300px;
            right: 55px;
        }
        .el-input-group {
            width: 68%;
            float: left;
        }
        .dataBox{
            position: absolute;
            top: 140px;
            line-height: 40px;
            height: 40px;
        }
        .title-content{
            float: left;
        }
    }
</style>

<template>
    <div id="historyData">
        <div class="dataBox" v-show="!isPending">
            <div class="title-text">导入范围</div>
            <div class="title-content">截止到2018年8月19号</div>
        </div>
        <div class="modeUpload">
            <div class="title-text">当日余额数据文件上传</div>
            <el-input v-model="input">
                <template slot="append">
                    <el-upload
                            class="upload-demo"
                            action="cfm/comm/attachment/upload"
                            multiple
                            :on-success="uploadSuccess"
                            :before-upload="beforeUpload">
                        <span class="">浏览</span>
                    </el-upload>
                </template>
            </el-input>
        </div>
        <div class="btnList">
            <el-button type="warning" size="small" plain >模板下载</el-button>
            <el-button type="warning" size="small" plain>取 消</el-button>
            <el-button type="warning" size="small" >确 定</el-button>
        </div>
    </div>
</template>

<script>

    export default {
        name: "HistoryData",
        created: function () {

            this.$emit('transmitTitle', '余额数据导入');
            // this.$emit('getTableData', this.routerMessage);
        },
        props: ["isPending","tableData"],
        data: function () {
            return {
                input:""
            }
        },
        methods: {
            uploadSuccess:function(){

            },
            beforeUpload:function(){

            }
        },
        watch: {
            //设置数据
            tableData: function (val, oldValue) {
            }
        }
    }
</script>
