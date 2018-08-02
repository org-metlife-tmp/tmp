<style lang="less" type="text/less">
    #upload {
        .upload-icon {
            width: 40px;
            height: 40px;
            border: 1px solid rgb(204, 204, 204);
            font-size: 38px;
            color: #00B3EC;
        }
        .upload-demo {
            display: inline-block;

            .el-upload {
                width: 40px;
                height: 40px;
                line-height: 40px;
                border: none;
            }
            .el-upload-list{
                display: none;
            }
        }

        .file-list{
            float: left;

            li{
                float: left;
                position: relative;
                cursor: pointer;
                margin-right: 10px;
                margin-bottom: 10px;

                span{
                    width: 38px;
                    height: 42px;
                    display: inline-block;
                    background: url("../../assets/icon_common.png") no-repeat;
                    background-position: -85px -103px;
                    vertical-align: middle;
                }
                .txt{
                    background-position: -85px -103px;
                }
                .doc,.docx{
                    background-position: 0px -103px;
                }
                .xls,xlsx{
                    background-position: -44px -103px;
                }
                .png,.jpg{
                    background-position: -132px -103px;
                }

                i{
                    position: absolute;
                    width: 21px;
                    height: 21px;
                    background: url("../../assets/icon_common.png") no-repeat;
                    background-position: -248px 0;
                    top: -5px;
                    left: 26px;
                    display: none;
                }
            }

            li:hover{
                i{
                    display: inline-block;
                }
            }
        }
    }
</style>
<template>
    <div id="upload">
        <ul class="file-list">
            <li v-for="file in fileList">
                <span :class="file.file_extension_suffix" title="点击下载"
                      @click="downloadUpFile(file.object_id)"></span>
                <i title="点击删除" @click="delUpFile(file.id)" v-show="isPending"></i>
                {{ file.original_file_name }}
            </li>
            <el-upload
                    class="upload-demo"
                    action="cfm/comm/attachment/upload"
                    :headers="{Authorization:currToken}"
                    multiple
                    :on-success="uploadSuccess"
                    :before-upload="beforeUpload"
                    v-show="isPending">
                <i class="el-icon-plus upload-icon"></i>
            </el-upload>
        </ul>
    </div>
</template>

<script>
    export default {
        name: "Upload",
        created:function(){
            this.currToken = this.$store.state.token;
            if(this.fileMessage.bill_id){
                this.getFileList();
            }
        },
        props:["emptyFileList","fileMessage","isPending","triggerFile"],
        data:function(){
            return {
                currToken: "",
                fileList: []
            }
        },
        methods:{
            //上传限制
            beforeUpload:function(file){
                const isLt10M = file.size / 1024 / 1024 < 10;
                if (!isLt10M) {
                    this.$message.error('上传文件大小不能超过 10MB!');
                }
                return isLt10M;
            },
            //上传成功
            uploadSuccess: function(response, file, fileList){
                this.fileList.push(response.files[0]);
                this.$emit("currentFielList",this.fileList);
            },
            //删除
            delUpFile: function(fileId){
                var fileList = this.fileList;
                for(var i = 0; i < fileList.length; i++){
                    if(fileList[i].id == fileId){
                        fileList.splice(i,1);
                        break;
                    }
                }
                this.$emit("currentFielList",this.fileList);
            },
            //下载
            downloadUpFile:function(objectId){
                this.$axios({
                    url:"/cfm/comm/attachment/download",
                    method: "post",
                    data: {
                        optype:"download",
                        params:{
                            object_id: objectId
                        }
                    },
                    responseType: 'blob'
                }).then((result) => {
                    if(result.error_msg){
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
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
                }).catch(function(error){
                    console.log(error);
                })
            },
            //获取当前项相关附件
            getFileList: function(){
                var params = this.fileMessage;
                this.$axios({
                    url:"/cfm/commProcess",
                    method: "post",
                    data:{
                        optype: "attachment_list",
                        params: params
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
                        var data = result.data;
                        this.fileList = data.files;
                        this.$emit("currentFielList",this.fileList);
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            }
        },
        watch:{
            emptyFileList:function(val,oldVal){
                this.fileList = val;
                this.$emit("currentFielList",this.fileList);
            },
            triggerFile: function(val,oldVal){
                this.getFileList();
            }
        }
    }
</script>
