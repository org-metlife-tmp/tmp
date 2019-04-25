<style scoped lang="less" type="text/less">
    #historyDealData {
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

        .select-way{
            text-align: left;
            padding-left: 60px;
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
    <div id="historyDealData">
        <div class="select-way" v-show="!isPending">
            <el-select v-model="inportType" size="mini">
                <el-option label="覆盖导入" value="1"></el-option>
                <el-option label="增量导入" value="2"></el-option>
            </el-select>
            <el-select v-model="uploadHeaders.pk" size="mini">
                <el-option label="自带模板" value="6"></el-option>
                <el-option label="建设银行" value="10"></el-option>
                <el-option label="中国银行" value="11"></el-option>
                <!--<el-option label="中信银行" value="12"></el-option>-->
            </el-select>
        </div>
        <div class="dataBox" v-show="!isPending">
            <div class="title-text">导入范围</div>
            <div class="title-content">截止到{{limitDate}}</div>
        </div>
        <div class="errorTip" v-show="errorTipShow">
            <div class="error-name">文档内容不符合要求</div>
            <a class="downLoad" href="javascript:;"
                @click = "downLoadExcel"
               v-text="queryUrl+'normal/excel/downExcel?object_id='+currentUpload.download_object_id"
            ></a>
        </div>
        <div class="modeUpload">
            <div class="title-text" v-show="isPending">当日交易数据文件上传</div>
            <div class="title-text" v-show="!isPending">历史交易数据文件上传</div>
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
            <el-button type="warning" size="small" plain
                       v-show="isPending || uploadHeaders.pk == '6'"
                       @click="templateDownLoad">模板下载</el-button>
            <el-button type="warning" size="small" plain>取 消</el-button>
            <el-button type="warning" size="small" @click="subConfirm">确 定</el-button>
        </div>
    </div>
</template>

<script>

    export default {
        name: "HistoryDealData",
        created: function () {
            this.currToken = this.$store.state.token;
            this.$emit("transmitTitle", "交易数据导入");
            this.uploadHeaders.Authorization = this.currToken;
            this.uploadHeaders.pk = this.isPending ? '5' : '6';
            var date = new Date();
            this.limitDate = new Date(date.setDate(date.getDate()-1)).toLocaleDateString().replace(/\//g,"-");
        },
        props: ["isPending"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                currToken:"",
                currentUpload:{},
                uploadHeaders:{
                    Authorization: "",
                    pk: ""
                },
                errorTipShow: false,
                limitDate:"",
                inportType: "1"
            }
        },
        methods: {
            //上传成功
            uploadSuccess:function(response, file, fileList){
                this.currentUpload = response;
                // this.addExcel = false;
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
                    params.pk = uploadHeaders.pk;
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
                var params = {
                    pk: this.uploadHeaders.pk
                };
                var currentUpload = this.currentUpload;
                for(var k in currentUpload){
                    params[k] = currentUpload[k];
                }
                var url = "";
                if(this.isPending){
                    url = this.queryUrl + 'normal/jyt/curTransImport';
                }else{
                    url = this.queryUrl + 'normal/jyt/hisTransImport';
                    params.import_type = this.inportType;
                }

                this.$axios({
                    url: url,
                    method: "post",
                    data:{
                        params: params
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else {
                        this.currentUpload = {} ;
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
                this.uploadHeaders.pk = val ? '5' : '6';
                this.currentUpload = {};
                this.errorTipShow = false;
            }
        }
    }
</script>
