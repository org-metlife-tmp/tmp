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

        .errorTip{
            width: 100%;
            display: inline-block;
            text-align: center;
            margin-top: 30px;
            .error-name{
                color: #fc6e21;
                line-height: 25px;
            }
            .downLoad{
                color: #00B4EC;
            }
        }

    }
</style>
<style lang="less">
    .upload-demo {
        .el-upload-list {
            display: none;
        }
    }
</style>


<template>
    <div id="historyData">
        <div class="dataBox" v-show="!isPending">
            <div class="title-text">导入范围</div>
            <div class="title-content">截止到{{limitDate}}，只能导入非直联账户，导入模式为覆盖。</div>
        </div>
        <div class="errorTip" v-show="errorTipShow">
            <div class="error-name">文档内容不符合要求</div>
            <a class="downLoad" href="javascript:;"
               @click = "downLoadExcel"
               v-text=" queryUrl + 'normal/excel/downExcel?object_id='+currentUpload.download_object_id"
            ></a>
        </div>
        <div class="modeUpload">
            <div class="title-text" v-show="isPending">当日余额数据文件上传</div>
            <div class="title-text" v-show="!isPending">历史余额数据文件上传</div>
            <el-input v-model="currentUpload.original_file_name" readonly>
                <template slot="append">
                    <el-upload
                            class="upload-demo"
                            :action="queryUrl + 'normal/excel/upload'"
                            :headers="uploadHeaders"
                            multiple
                            accept=".xlsx,.xls"
                            :on-success="uploadSuccess">
                        <span class="">浏览</span>
                    </el-upload>
                </template>
            </el-input>
        </div>
        <div class="btnList">
            <el-button type="warning" size="small" plain @click="templateDownLoad">模板下载</el-button>
            <el-button type="warning" size="small" plain>取 消</el-button>
            <el-button type="warning" size="small" @click="subConfirm">确 定</el-button>
        </div>
    </div>
</template>

<script>

    export default {
        name: "HistoryData",
        created: function () {
            this.currToken = this.$store.state.token;
            this.$emit("transmitTitle", "余额数据导入");
            this.uploadHeaders.Authorization = this.currToken;
            this.uploadHeaders.pk = this.isPending ? '3' : '4';
            var date = new Date();
            this.limitDate = new Date(date.setDate(date.getDate()-1)).toLocaleDateString().replace(/\//g,"-");
        },
        props: ["isPending"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                currToken:"",
                currentUpload:{},
                uploadHeaders:{},
                errorTipShow: false,
                limitDate:"",
            }
        },
        methods: {
            //上传成功
            uploadSuccess:function(response, file, fileList){
                this.currentUpload = response;
                // this.addExcel = false;
                var message = "";
                var message  = response.success ? '上传成功' : response.error_message;
                var type = response.success ? 'success' : 'warning';
                this.$message({
                    type: type,
                    message: message,
                    duration: 2000
                });
                if(!response.success && response.download_object_id){
                    this.errorTipShow = true;
                }else{
                    this.errorTipShow = false;
                }
            },
            //下载正确excel文件
            downLoadExcel:function(type){
                var params = {};
                if(type =='template'){
                    params.pk = this.isPending ? '3':'4';
                }else{
                    params.object_id = this.currentUpload.download_object_id;
                }
                this.$axios({
                    url: this.queryUrl + "normal/excel/downExcel",
                    method: "post",
                    data:{
                        params:params
                    },
                    responseType: 'blob'
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else {
                        var fileName = decodeURI(result.headers["content-disposition"]).split("=")[1];
                        //ie兼容
                        if (window.navigator && window.navigator.msSaveOrOpenBlob) {
                            window.navigator.msSaveOrOpenBlob(new Blob([result.data]), fileName);
                        } else {
                            let url = window.URL.createObjectURL(new Blob([result.data]));
                            let link = document.createElement('a');
                            link.style.display = 'none';
                            link.href = url;
                            link.setAttribute('download', fileName);
                            document.body.appendChild(link);
                            link.click();
                        }
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            subConfirm: function (){
                var url = this.isPending ?  this.queryUrl + 'normal/yet/curBlanceImport' :  this.queryUrl + 'normal/yet/hisBlanceImport';
                var currentUpload = this.currentUpload;
                this.$axios({
                    url: url,
                    method: "post",
                    data:{
                        params: currentUpload
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else {
                        this.currentUpload = {};
                        this.$message({
                            type: "success",
                            message: "确认成功",
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            templateDownLoad:function (){
                this.downLoadExcel('template');
            }
        },
        watch: {
            isPending: function (val, oldVal) {
                this.uploadHeaders.pk = val ? '3' : '4';
                this.currentUpload = {};
                this.errorTipShow = false;
            }
        }
    }
</script>

