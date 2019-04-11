<style scoped lang="less" type="text/less">
    #payDiskSending {
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
        .button-list-left {
            position: absolute;
            top: -56px;
            left: -21px;
        }

        /*搜索区*/
        .search-setion {
            text-align: left;

            /*时间控件*/
            .el-date-editor {
                width: 100%;
                max-width: 210px;
            }
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
        .table-content {
            height: 66%;
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

        /*按钮样式-发送*/
        .send,.export {
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            background-position: -440px -62px;
            vertical-align: middle;
        }

        /*导出*/
        .export {
            background-position: -535px -62px;
        }
    }
</style>

<template>
    <div id="payDiskSending">
        <!-- 顶部按钮-->
        <div class="button-list-left">
            <el-select v-model="searchData.source_sys"
                       clearable filterable size="mini"
                       placeholder="请选择业务系统">
                <el-option v-for="(item,key) in sourceList"
                           :key="key"
                           :label="item"
                           :value="key">
                </el-option>
            </el-select>
        </div>
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="exportFun">导出</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="5">
                        <el-form-item>
                            <el-date-picker
                                    v-model="dateValue"
                                    type="daterange"
                                    range-separator="至"
                                    start-placeholder="开始日期"
                                    end-placeholder="结束日期"
                                    value-format="yyyy-MM-dd"
                                    size="mini" clearable
                                    unlink-panels
                                    :picker-options="pickerOptions">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.channel_id" placeholder="请选择通道编码"
                                       clearable filterable
                                       style="width:100%">
                                <el-option v-for="channel in channelList"
                                           :key="channel.channel_id"
                                           :label="channel.channel_code"
                                           :value="channel.channel_id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-select v-model="searchData.channel_id" placeholder="请选择通道描述"
                                       clearable filterable
                                       style="width:100%">
                                <el-option v-for="channel in channelList"
                                           :key="channel.channel_id"
                                           :label="channel.channel_desc"
                                           :value="channel.channel_id">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="4">
                        <el-form-item>
                            <el-input v-model="searchData.master_batchno" clearable placeholder="请输入主批次号"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="3">
                        <el-form-item>
                            <el-select v-model="searchData.interactive_mode" placeholder="请选择交互方式"
                                       clearable filterable
                                       style="width:100%">
                                <el-option v-for="(interact,key) in interactiveList"
                                           :key="key"
                                           :label="interact"
                                           :value="key">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <el-button type="primary" plain @click="clearData" size="mini">清空筛选</el-button>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item style="margin-bottom:0px">
                            <el-checkbox-group v-model="searchData.status">
                                <el-checkbox v-for="(name,k) in statusList"
                                             :label="k" name="name" :key="k">
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
            <el-table :data="tableList"
                      border size="mini" height="100%">
                <el-table-column prop="source_sys" label="来源系统" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="master_batchno" label="主批次号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="child_batchno" label="子批次号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="interactive_mode" label="交互方式" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="channel_code" label="通道编码" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="channel_desc" label="通道描述" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="create_on" label="组批日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_total_amount" label="总金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="pay_total_num" label="总笔数" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="status" label="状态" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_user_name" label="操作人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_on" label="发送日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="80"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="下载" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.interactive_mode=='报盘' && ((scope.row.status=='已审批未发送' && scope.row.file_name) || scope.row.status=='已发送未回盘' || scope.row.status=='已回退')">
                            <el-button type="info" icon="el-icon-download" size="mini"
                                       @click="downData(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="发送" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-if="scope.row.interactive_mode=='直连' && (scope.row.status=='已审批未发送' || scope.row.status=='已回退')">
                            <el-button class="send" size="mini"
                                       @click="sendData(scope.row)"></el-button>
                        </el-tooltip>
                        <el-tooltip content="导出" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button class="export" size="mini"
                                       @click="exportData(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </section>
        <!--分页部分-->
        <div class="botton-pag">
            <el-pagination
                    background
                    layout="sizes, prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    :page-sizes="[20, 50, 100, 500]"
                    :pager-count="5"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange"
                    :current-page="pagCurrent">
            </el-pagination>
        </div>
    </div>
</template>

<script>
    export default {
        name: "PayDiskSending",
        created: function () {
            this.$emit("transmitTitle", "盘片发送");
            this.$emit("getCommTable", this.routerMessage);

            /*获取常量数据*/
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            //交互方式
            if (constants.SftInteractiveMode) {
                this.interactiveList = constants.SftInteractiveMode;
            }
            //来源系统
            if (constants.SftOsSource) {
                this.sourceList = constants.SftOsSource;
            }
            //通道编码
            this.getChannelList();
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "disksending_list",
                    params: {
                        page_size: 20,
                        page_num: 1
                    }
                },
                searchData: { //搜索条件
                    source_sys: "",
                    master_batchno: "",
                    channel_id: "",
                    channel_desc: "",
                    interactive_mode: "",
                    status: []
                },
                dateValue: "", //时间控件
                pickerOptions: {
                    disabledDate(time) {
                        return time.getTime() > Date.now();
                    }
                },
                tableList: [], //列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                sourceList: {}, //常量数据
                interactiveList: {},
                statusList: {
                    1: "审批中",
                    2: "已审批未发送",
                    3: "回退审批中",
                    4: "已发送未回盘",
                    5: "回盘成功",
                    6: "回盘异常",
                    7: "审批拒绝",
                    8: "已回退"
                },
                channelList: [],
            }
        },
        methods: {
            //清空搜索条件
            clearData: function(){
                var searchData = this.searchData;
                for (var k in searchData) {
                    if(k == "status"){
                        searchData[k] = [];
                    }else{
                        searchData[k] = "";
                    }
                }
            },
            //根据条件查询数据
            queryData: function () {
                var searchData = this.searchData;
                for (var k in searchData) {
                    this.routerMessage.params[k] = searchData[k];
                }
                var val = this.dateValue;
                this.routerMessage.params.start_date = val ? val[0] : "";
                this.routerMessage.params.end_date = val ? val[1] : "";
                this.routerMessage.params.page_num = 1;
                this.$emit("getCommTable", this.routerMessage);
            },
            //换页后获取数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getCommTable", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.params.page_size = val;
                this.routerMessage.params.page_num = 1;
                this.$emit("getCommTable", this.routerMessage);
            },
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //获取通道编码
            getChannelList: function () {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "sftchannel_getallchannel",
                        params: {
                            pay_attr: 1
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        });
                    } else {
                        var data = result.data.data;
                        this.channelList = data;
                    }

                }).catch(function (error) {
                    console.log(error);
                });
            },
            //展示格式转换-来源系统
            transitSource: function (row, column, cellValue, index) {
                return this.sourceList[cellValue];
            },
            //下载
            downData: function (row) {
                var params = {
                    optype: "disksending_diskdownload",
                    params: {
                        pay_master_id: row.pay_master_id,
                        channel_code: row.channel_code,
                        pay_id: row.pay_id
                    }
                }
                this.$axios({
                    url:this.queryUrl + "normalProcess",
                    method: "post",
                    data: params,
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
                            this.$emit("getCommTable", this.routerMessage);
                        } else {
                            let url = window.URL.createObjectURL(new Blob([result.data]));
                            let link = document.createElement('a');
                            link.style.display = 'none';
                            link.href = url;
                            link.setAttribute('download', fileName);
                            document.body.appendChild(link);
                            link.click();
                            this.$emit("getCommTable", this.routerMessage);
                        }
                    }
                }).catch(function(error){
                    console.log(error);
                })
            },
            //发送
            sendData: function (row) {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "disksending_sendbank",
                        params: {
                            id: row.pay_id,
                            persist_version: row.persist_version
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        });
                    } else {
                        this.$message({
                            type: "success",
                            message: "发送成功",
                            duration: 2000
                        });
                        this.$emit("getCommTable", this.routerMessage);
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            },
            //单条导出
            exportData: function (row) {
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "disksending_detaillistexport",
                        params: {
                            child_batchno: row.child_batchno,
                            type: 1
                        }
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
                })
            },
            //导出
            exportFun: function () {
                if (!this.tableList.length) {
                    this.$message({
                        type: "warning",
                        message: "当前数据为空",
                        duration: 2000
                    });
                    return;
                }
                var params = this.routerMessage.params;
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "disksending_listexport",
                        params: params
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
                })
            }
        },
        watch: {
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.tableList = val.data;
                this.pagCurrent = val.page_num;
            }
        }
    }
</script>











