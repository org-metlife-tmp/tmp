<style scoped lang="less" type="text/less">
    #abnormalDispose {
        /*按钮样式*/
        .on-copy, .withdraw {
            width: 20px;
            height: 20px;
            background-image: url(../../assets/icon_common.png);
            border: none;
            padding: 0;
            vertical-align: middle;
        }
        /*复制按钮*/
        .on-copy {
            background-position: -24px 1px;
        }
        /*撤回按钮*/
        .withdraw {
            background-position: -48px 0;
        }
    }
</style>

<template>
    <el-container id="abnormalDispose">
        <el-header>
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
                                <el-input v-model="searchData.master_batchno" clearable
                                          placeholder="请输入主批次号"></el-input>
                            </el-form-item>
                        </el-col>
                        <el-col :span="4">
                            <el-form-item>
                                <el-select v-model="searchData.channel_id_one" placeholder="请选择通道编码"
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
                                <el-select v-model="searchData.channel_id_one" placeholder="请选择通道描述"
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
                                <el-select v-model="searchData.is_revoke" placeholder="是否回退"
                                           clearable filterable
                                           style="width:100%">
                                    <el-option label="未回退" value="0"></el-option>
                                    <el-option label="已回退" value="1"></el-option>
                                </el-select>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="clearData" size="mini">清空筛选</el-button>
                            </el-form-item>
                        </el-col>
                        <el-col :span="21">
                            <el-form-item style="margin-bottom:0px">
                                <el-checkbox-group v-model="searchData.service_status">
                                    <el-checkbox v-for="(name,k) in statusList"
                                                 :label="k" name="type" :key="k">
                                        {{ name }}
                                    </el-checkbox>
                                </el-checkbox-group>
                            </el-form-item>
                        </el-col>
                        <el-col :span="2">
                            <el-form-item>
                                <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                            </el-form-item>
                        </el-col>
                    </el-row>
                </el-form>
            </div>
            <div class="split-bar"></div>
        </el-header>
        <el-main>
            <el-table :data="tableList"
                      border size="mini" height="100%">
                <el-table-column prop="source_sys" label="来源系统" :show-overflow-tooltip="true"
                                 :formatter="transitSource"></el-table-column>
                <el-table-column prop="master_batchno" label="主批次号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="child_batchno" label="子批次号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="interactive_mode" label="交互方式" :show-overflow-tooltip="true"
                                 :formatter="transitMode"></el-table-column>
                <el-table-column prop="channel_code" label="通道编码" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="channel_desc" label="通道描述" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="send_on" label="出盘日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="total_num" label="总笔数" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="total_amount" label="总金额" :show-overflow-tooltip="true"
                                 :formatter="transitAmount"></el-table-column>
                <el-table-column prop="service_status" label="状态" :show-overflow-tooltip="true"
                                 :formatter="transitStatus"></el-table-column>
                <el-table-column prop="error_msg" label="异常原因" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="revoke_user_name" label="回退申请人" width="100px"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="revoke_date" label="申请日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="exam_position_name" label="审批人" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="exam_time" label="审批日期" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作" width="50"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="申请回退" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500"
                                    v-show="scope.row.service_status == 4">
                            <el-button type="danger" icon="el-icon-back" size="mini"
                                       @click="rejectData(scope.row)"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </el-main>
        <el-footer>
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


        </el-footer>
    </el-container>
</template>

<script>
    export default {
        name: "AbnormalDispose",
        created: function () {
            this.$emit("transmitTitle", "异常处理");
            this.$emit("getCommTable", this.routerMessage);

            /*获取常量数据*/
            var constants = JSON.parse(window.sessionStorage.getItem("constants"));
            //来源系统
            if (constants.SftOsSource) {
                this.sourceList = constants.SftOsSource;
            }
            //交互方式
            if (constants.SftInteractiveMode) {
                this.interactiveList = constants.SftInteractiveMode;
            }
            //通道编码
            this.getChannelList();
        },
        props: ["tableData"],
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
                routerMessage: {
                    optype: "sftexcept_exceptlist",
                    params: {
                        page_size: 20,
                        page_num: 1
                    }
                },
                searchData: { //搜索条件
                    source_sys: "",
                    master_batchno: "",
                    channel_id_one: "",
                    channel_id_two: "",
                    is_revoke: "",
                    service_status: []
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
                statusList: { //常量数据
                    3: "回退审批中",
                    4: "已发送未回盘",
                    5: "回盘成功",
                    6: "回盘异常",
                    8: "已回退",
                },
                interactiveList: {},
                channelList: [],
            }
        },
        methods: {
            //清空搜索条件
            clearData: function () {
                var searchData = this.searchData;
                for (var k in searchData) {
                    if (k == "service_status") {
                        searchData[k] = [];
                    } else {
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
            //展示格式转换-来源系统
            transitSource: function (row, column, cellValue, index) {
                return this.sourceList[cellValue];
            },
            //展示格式转换-状态
            transitStatus: function (row, column, cellValue, index) {
                return this.statusList[cellValue];
            },
            //展示格式转换-交互方式
            transitMode: function (row, column, cellValue, index) {
                return this.interactiveList[cellValue];
            },
            //展示格式转换-金额
            transitAmount: function (row, column, cellValue, index) {
                return this.$common.transitSeparator(cellValue);
            },
            //申请回退
            rejectData: function (row) {
                this.$prompt('请输入回退原因', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    title: "回退原因",
                    inputValidator: function (value) {
                        if (!value) {
                            return false;
                        } else {
                            return true;
                        }
                    },
                    inputErrorMessage: '请输入回退原因'
                }).then(({value}) => {
                    this.$axios({
                        url: this.queryUrl + "normalProcess",
                        method: "post",
                        data: {
                            optype: "sftexcept_revoke",
                            params: {
                                id: row.id,
                                os_source: row.source_sys,
                                persist_version: row.persist_version,
                                op_reason: value
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {
                            this.$message({
                                type: "error",
                                message: result.data.error_msg,
                                duration: 2000
                            });
                            return;
                        } else {
                            this.$message({
                                type: "success",
                                message: "回退成功",
                                duration: 2000
                            });
                            this.$emit("getCommTable", this.routerMessage);
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }).catch(() => {

                });
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
                        optype: "sftexcept_listexport",
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
