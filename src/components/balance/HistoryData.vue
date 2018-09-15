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
<style lang="less" type="text/less">
    #historyData{
        .upload-demo {
            .el-upload-list {
                display: none;
            }
        }
    }
</style>

<template>
    <div id="historyData">
        <div class="dataBox" v-show="!isPending">
            <div class="title-text">导入范围</div>
            <div class="title-content">截止到{{ getCurrDate }}</div>
        </div>
        <div class="modeUpload">
            <div class="title-text">当日余额数据文件上传</div>
            <el-input size="small" readonly v-model="input" v-if="isPending">
                <template slot="append">
                    <el-upload
                            class="upload-demo"
                            action="/cfm/normal/excel/upload"
                            :headers="{pk:'3',Authorization:currToken}"
                            :on-success="uploadSuccess"
                            multiple>
                        <span class="">浏览</span>
                    </el-upload>
                </template>
            </el-input>
            <el-input size="small" readonly v-model="input" v-if="!isPending">
                <template slot="append">
                    <el-upload
                            class="upload-demo"
                            action="/cfm/normal/excel/upload"
                            :headers="{pk:'4',Authorization:currToken}"
                            :on-success="uploadSuccess"
                            multiple>
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

            //设置token数据
            this.currToken = this.$store.state.token;
        },
        props: ["isPending","tableData"],
        data: function () {
            return {
                input:"",
                currToken: ""
            }
        },
        methods: {
            //上传成功
            uploadSuccess: function (response, file, fileList) {
                if(response.error_message){
                    this.$message({
                        type: "error",
                        message: response.error_message,
                        duration: 2000
                    });
                    return;
                }else{
                    this.$message({
                        type: "success",
                        message: "上传成功",
                        duration: 2000
                    });
                }
            },
        },
        computed: {
            getCurrDate: function(){
                var currDate = new Date();
                return currDate.getFullYear() + "年" + (currDate.getMonth()+1) + "月" + (currDate.getDate()-1);
            }
        },
        watch: {
            //设置数据
            tableData: function (val, oldValue) {
            }
        }
    }
</script>
