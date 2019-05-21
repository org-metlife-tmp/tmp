<style scoped lang="less" type="text/less">
    #massSingleGather {
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
    <el-container id="massSingleGather">
        <el-header>
            <div class="button-list-left">
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
            </div>
            <div class="button-list-right">
                <el-button type="warning" size="mini" @click="exportFun">导出</el-button>
            </div>
            <div class="search-setion">
                <el-form :inline="true" :model="searchData" size="mini">
                    <el-row>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.master_batchno" clearable placeholder="请输入收款机构"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.master_batchno" clearable placeholder="请输入批单号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.master_batchno" clearable placeholder="请输入投保单号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.master_batchno" clearable placeholder="请输入保单号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-input v-model="searchData.master_batchno" clearable placeholder="请输入客户号"></el-input>
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


                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.channel_id_one" placeholder="请选择收款方式"
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
                                <el-input v-model="searchData.master_batchno" clearable placeholder="请输入收款银行"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.channel_id_one" placeholder="请选择票据状态"
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
                                <el-select v-model="searchData.channel_id_one" placeholder="请选择资金用途"
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
                        <el-col :span="5">
                            <el-form-item>
                                <el-col :span="11">
                                    <el-input v-model="searchData.min" clearable placeholder="最小金额"></el-input>
                                </el-col>
                                <el-col class="line" :span="1" style="text-align:center">-</el-col>
                                <el-col :span="11">
                                    <el-input v-model="searchData.max" clearable placeholder="最大金额"></el-input>
                                </el-col>
                            </el-form-item>
                        </el-col>

                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.channel_id_one" placeholder="是否第三方缴费"
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
                        <el-col :span="20">
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
        </el-header>
        <el-main>
            <el-table :data="tableList"
                      border size="mini" height="100%">
                <el-table-column prop="source_sys" label="收款日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="master_batchno" label="批处理号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="child_batchno" label="核心系统" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="interactive_mode" label="批单号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="channel_code" label="投保单号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="channel_code" label="保单号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="channel_code" label="保单机构" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="channel_desc" label="收款方式" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="create_on" label="收款银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_total_amount" label="收款账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="pay_total_num" label="资金用途" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="status" label="票据状态" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_user_name" label="票据票号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_on" label="投保人客户号" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_on" label="投保人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_on" label="业务所属客户号" width="120px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_on" label="业务所属客户" width="110px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_on" label="客户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_on" label="客户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_on" label="金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="send_on" label="客户银行" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_on" label="客户账号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_on" label="第三方缴费" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_on" label="缴费人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_on" label="缴费编码" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_on" label="操作人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_on" label="收款机构" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_on" label="状态" :show-overflow-tooltip="true"></el-table-column>
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
        </el-main>
        <el-footer>
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
        </el-footer>
    </el-container>
</template>

<script>
    export default {
        name: "MassSingleGather",
        created: function () {
            this.$emit("transmitTitle", "团单收款");
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
                    5: "回盘成功"
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
