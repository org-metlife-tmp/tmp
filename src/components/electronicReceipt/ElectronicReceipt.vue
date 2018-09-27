<style lang="less" type="text/less">
    #electronicReceipt{
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*搜索区*/
        .search-setion {
            text-align: left;
            height: 42px;
            overflow: hidden;
            transition: height 1s;
            .line {
                text-align: center;
            }
        }
        .search-setion.show-more {
            height: 184px;
        }

        .table-content{
            height: 339px;
            transition: height 1s;
        }
        .is-small {
            height: 40%;
        }

        /*分隔栏*/
        .split-bar {
            width: 106%;
            height: 6px;
            margin-left: -20px;
            background-color: #E7E7E7;
            margin-bottom: 20px;
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -10px;
        }
        /*顶部下拉部分*/
        .btn-list-left{
            position: absolute;
            top: -58px;
            left: -20px;
        }
    }
</style>
<style lang="less" type="text/less">
    #electronicReceipt{
        .el-form-item--mini.el-form-item, .el-form-item--small.el-form-item{
            width: 90%;
        }
        .search-setion {
            .el-form {
                .el-form-item__content {
                    width: 100%;
                }
            }
            .el-select {
                width: 90%;
            }
        }
        .el-dialog__wrapper {
            .el-dialog__body {
                height: 440px;
                overflow-y: auto;
            }
        }
    }
</style>

<template>
    <div id="electronicReceipt">
        <div class="btn-list-left">
            <el-select v-model="channel_code" 
                        placeholder="渠道编码" size="mini"
                        clearable>
                <el-option v-for="channel in channelList"
                            :key="channel.channel_code"
                            :label="channel.channel_code"
                            :value="channel.channel_code">
                </el-option>
            </el-select>
            <el-select v-model="ebObj"
                        :disabled="!channel_code"
                        placeholder="银行" size="mini"
                        @change="bankIsSelect"
                        value-key="eb_type">
                <el-option v-for="type in bankList"
                            :key="type.eb_type"
                            :label="type.eb_type_desc"
                            :value="type">
                </el-option>
            </el-select>
        </div>
        <!--搜索区-->
        <div :class="['search-setion',{'show-more':showMore}]">
            <el-form :inline="true" :model="searchData" size="mini">
                <el-row>
                    <el-col :span="6">
                        <el-form-item>
                            <el-date-picker
                                    v-model="searchData.eb_date"
                                    type="date"
                                    placeholder=" 回单日期"
                                    value-format="yyyy-MM-dd"
                                    style="width: 100%;">
                            </el-date-picker>
                        </el-form-item>
                    </el-col>
                    <el-col :span="8">
                        <el-form-item>
                            <el-col :span="11">
                                <el-input v-model="searchData.min" clearable placeholder="最小回单金额"></el-input>
                            </el-col>
                            <el-col class="line" :span="2">-</el-col>
                            <el-col :span="11">
                                <el-input v-model="searchData.max" clearable placeholder="最大回单金额"></el-input>
                            </el-col>
                        </el-form-item>
                    </el-col>
                    <el-col :span="2">
                        <el-form-item>
                            <el-button type="primary" plain @click="queryData" size="mini">搜索</el-button>
                        </el-form-item>
                    </el-col>
                    <el-col :span="3" :offset="5">
                        <el-button type="primary" plain @click="showMore = !showMore" size="mini">
                            高级<i
                                :class="['el-icon--right',{'el-icon-arrow-down':!showMore},{'el-icon-arrow-right':showMore}]"></i>
                        </el-button>
                    </el-col>
                </el-row>
                <el-row>
                    <el-col :span="6">
                        <el-select v-model="searchData.addKey"
                                    placeholder="增加搜索条件" size="mini"
                                    @change="addSearch"
                                    value-key="key">
                            <el-option v-for="type in searchOptionList"
                                        :key="type.key"
                                        :label="type.value"
                                        :value="type">
                            </el-option>
                        </el-select>
                    </el-col>
                    <template v-for="item in searchList">
                        <el-col :span="6" :key="item.key">
                            <el-form-item>
                                <el-input v-model="searchData[item.key]" clearable :placeholder="item.desc" class="input-with-select">
                                    <el-button slot="append"  type="primary" @click="delSearch(item)">删除</el-button>
                                </el-input>
                            </el-form-item>
                        </el-col>
                    </template>
                </el-row>
            </el-form>
        </div>
        <!--分隔栏-->
        <div class="split-bar"></div>
        <!--数据展示区-->
        <section :class="['table-content',{'is-small':showMore}]">
            <el-table :data="tableList"
                      height="100%"
                      border size="mini">
                <el-table-column
                    prop="eb_date"
                    label="回单日期"
                    :show-overflow-tooltip="true"
                >
                </el-table-column>
                <el-table-column
                    prop="amount"
                    label="回单金额"
                    :formatter="changeThousandth"
                    :show-overflow-tooltip="true"
                >
                </el-table-column>
                <template v-for="head in tableHead">
                    <!-- 业务种类列，需要格式化数据-->
                    <el-table-column
                        v-if="head.prop=='biz_type' || head.prop=='service_status' || head.prop=='interactive_mode'"
                        :key="head.id"
                        :prop="head.prop"
                        :label="head.name"
                        :formatter="transitionStatus"
                        :show-overflow-tooltip="true"
                    >
                    </el-table-column>
                    <!-- 公用列 -->
                    <el-table-column
                        v-else
                        :key="head.prop"
                        :prop="head.prop"
                        :label="head.origin_fd_desc"
                        :show-overflow-tooltip="true"
                    >
                    </el-table-column>
                </template>
                <el-table-column
                        label="操作" width="80"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="查看" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="primary" icon="el-icon-search" size="mini"
                                    @click="viewDetail(scope.row,scope.$index)"></el-button>
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
                    :page-sizes="[7, 50, 100, 500]"
                    :pager-count="5"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange"
                    :current-page="pagCurrent">
            </el-pagination>
        </div>
        <!--查看弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="800px" title="收款回单查看"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" class="dialog-title">收款回单查看</h1>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "ElectronicReceipt",
        created: function(){
            this.$emit("transmitTitle", "电子回单");
            // this.$emit("getCommTable", this.routerMessage);
        },
        mounted:function(){
            this.channelList = [{
                channel_code: 'cmbc'
            }];
            var typeList = JSON.parse(window.sessionStorage.getItem("eleType"));
            if(typeList){
                this.bankList = typeList;
            }
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: {
                    optype: "ele_list",
                    params: {
                        page_size: 8,
                        page_num: 1,
                        channel_code: "",
                        eb_type: ""
                    }
                },
                tableList: [],//列表数据
                pagSize: 8, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                dialogVisible: false,
                dialogData: {},
                searchData: {}, //搜索条件
                showMore: false,
                searchOptionList: [],//可供增加的搜索条件的下拉列表
                searchList: [],//增加的搜索条件
                channelList: [],//渠道编码
                bankList: [],//银行
                channel_code: "",//当前渠道编码
                ebObj: {},//当前银行
                tableHead: [], //动态表格的列名称
            }
        },
        methods: {
            //换页后获取数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getCommTable", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange: function (val) {
                this.routerMessage.params = {
                    page_size: val,
                    page_num: 1
                };
                this.$emit("getCommTable", this.routerMessage);
            },
            //根据条件查询
            queryData:function (){
                var searchData = this.searchData;
                debugger
                Object.assign(this.routerMessage.params,searchData);
                this.$emit("getCommTable", this.routerMessage);
            },
            //增加搜索条件
            addSearch:function (val){
                this.searchList.push({key: val.key, desc: val.value});
            },
            delSearch: function (item){
                var index = this.searchList.indexOf(item)
                this.searchList.splice(index, 1);
                this.searchData[item.key] = "";
                if (this.searchList.length === 0) {
                    this.searchData.addKey = {};
                }
            },
            //处理表格千分位
            changeThousandth: function (row, column, cellValue, index) {
                if(cellValue){
                    return this.$common.transitSeparator(cellValue);
                }
            },
            //请求模板和列表
            bankIsSelect: function (val){
                //查询电子回单模板
                this.$axios({
                    url: "/cfm/normalProcess",
                    method: "post",
                    data: {
                        optype: "ele_template",
                        params: {
                            uuid: val.uuid
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else {
                        var uuid = this.ebObj.uuid;
                        var data = result.data.data[uuid];
                        var tabHeadList = [];
                        var sOptionlist = [];
                        if(JSON.stringify(data) == "{}"){
                            this.tableHead = [];
                            this.searchOptionList = [];
                            this.tableList = [];
                            return;
                        }
                        for(let i = 1;i<7; i++){
                            var obj = {};
                            obj.key = 'field_' + i;
                            var item = data['field_'+i];
                            item.prop = 'field_' + i;
                            obj.value = item.origin_fd_desc;
                            tabHeadList.push(item);
                            sOptionlist.push(obj);
                        }
                        //组织表格列名称数据
                        this.tableHead = tabHeadList;
                        //组织搜索条件下拉数据
                        this.searchOptionList = sOptionlist;
                        debugger
                        //查询电子回单列表
                        this.routerMessage.params.channel_code = this.channel_code;
                        this.routerMessage.params.eb_type = val.eb_type;
                        this.$emit("getCommTable", this.routerMessage);
                    }
                })
            },
            //查看详情
            viewDetail: function(row, index){
                this.dialogVisible = true;
            }
        },
        watch: {
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data;
                this.userData = val.ext;
            }
        }
    }
</script>

