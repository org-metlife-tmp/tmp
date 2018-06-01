<style scoped lang="less" type="text/less">
    #channelSet {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        //顶部按钮
        .button-list-right {
            position: absolute;
            top: -60px;
            right: -18px;
        }
        /*搜索区*/
        .search-setion {
            text-align: left;
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

        /*按钮-设置状态*/
        .on-off {
            width: 22px;
            height: 22px;
            background-image: url(../../assets/icon_common.png);
            background-position: -273px -62px;
            border: none;
            padding: 0;
            vertical-align: middle;
        }
    }
</style>

<template>
    <div id="channelSet">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addChannel">新增</el-button>
        </div>
        <!--搜索区-->
        <div class="search-setion">
            <el-form :inline="true" :model="serachData" size="mini">
                <el-row>
                    <el-col :span="7">
                        <el-form-item label="渠道名称">
                            <el-select v-model="serachData.query_key" placeholder="请选择渠道"
                                       clearable filterable>
                                <el-option v-for="chiannel in channelList"
                                           :key="chiannel.code"
                                           :label="chiannel.desc"
                                           :value="chiannel.desc">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="3">
                        <el-form-item>
                            <el-button type="primary" plain @click="queryData">搜索</el-button>
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
                      border
                      size="mini">
                <el-table-column prop="code" label="渠道代码" :show-overflow-tooltip="true" width="160"></el-table-column>
                <el-table-column prop="name" label="渠道名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="memo" label="备注" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="is_activate" label="是否激活"
                                 :formatter="getStatus"
                                 width="100"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column label="操作" width="50">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="设置状态" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button size="mini"
                                       @click="setStatus(scope.row)"
                                       class="on-off"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
        </section>
        <!--分页部分-->
        <div class="botton-pag">
            <el-pagination
                    background
                    layout="prev, pager, next, jumper"
                    :page-size="pagSize"
                    :total="pagTotal"
                    @current-change="getCurrentPage"
                    :pager-count="5">
            </el-pagination>
        </div>
        <!--新增弹出框-->
        <el-dialog title="新增"
                   :visible.sync="dialogVisible"
                   width="800px" top="76px"
                   :close-on-click-modal="false">
            <el-form :model="dialogData" size="small">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="渠道代码" :label-width="formLabelWidth">
                            <el-select v-model="dialogData.code"
                                       placeholder="请选择渠道代码"
                                       clearable filterable
                                       @change="setChiannelName">
                                <el-option v-for="chiannel in channelList"
                                           :key="chiannel.code"
                                           :value="chiannel.code">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="渠道名称" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.name" auto-complete="off" :disabled="true"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="是否银企直联" :label-width="formLabelWidth">
                            <el-switch
                                    v-model="dialogData.third_party_flag"
                                    active-value="1"
                                    inactive-value="0">
                            </el-switch>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.memo" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" plain size="mini" @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="subCurrent" >确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "ChannelSet",
        created: function () {
            this.$emit("transmitTitle", "渠道设置");
            this.$emit("getTableData", this.routerMessage);

            //获取下拉框数据
            var channelList = JSON.parse(window.sessionStorage.getItem("channelList"));
            if (channelList) {
                this.channelList = channelList;
            }
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: { //本页数据获取参数
                    optype: "handlechannel_list",
                    params: {
                        page_size: 7,
                        page_num: 1
                    }
                },
                pagSize: 1,
                pagTotal: 1,
                tableList: [],
                serachData: {},
                dialogVisible:false,
                dialogData: {},
                formLabelWidth:"120px",
                channelList: []
            }
        },
        methods: {
            //状态展示格式转换
            getStatus:function(row, column, cellValue, index){
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if(constants.YesOrNo){
                    return constants.YesOrNo[cellValue];
                }else{
                    return "";
                }
            },
            //设置状态
            setStatus:function (row) {
                this.$axios({
                    url:"/cfm/adminProcess",
                    method: "post",
                    data: {
                        optype: "handlechannel_setstatus",
                        params: row
                    }
                }).then((result) => {
                    if(result.data.error_msg){
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
                        row.is_activate = result.data.data;
                        this.$message({
                            type: 'success',
                            message: '修改成功!',
                            duration: 2000
                        });
                    }
                }).catch(function(error){
                    console.log(error);
                })
            },
            //新增
            addChannel:function(){
                this.dialogData = {};
                this.dialogVisible = true;
            },
            //点击页数 获取当前页数据
            getCurrentPage: function (currPage) {
                this.routerMessage.params.page_num = currPage;
                this.$emit("getTableData", this.routerMessage);
            },
            //根据条件查询数据
            queryData: function () {
                var serachData = this.serachData;
                for (var key in serachData) {
                    this.routerMessage.params[key] = serachData[key];
                }
                this.$emit("getTableData", this.routerMessage);
            },

            /*弹出框事件*/
            //根据渠道代码设置渠道名称
            setChiannelName:function (value) {
                if(!value){
                    this.dialogData.name = "";
                }
                var channelList = JSON.parse(window.sessionStorage.getItem("channelList"));
                for(var i=0; i<channelList.length; i++){
                    var item = channelList[i];
                    if(value == item.code){
                        this.dialogData.name = item.desc;
                        return;
                    }
                }
            },
            //提交当前修改或新增
            subCurrent: function () {
                var params = this.dialogData;

                this.$axios({
                    url: "/cfm/adminProcess",
                    method: "post",
                    data: {
                        optype: "handlechannel_add",
                        params: params
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else {
                        var data = result.data.data;
                        this.tableList.push(data);
                        this.dialogVisible = false;
                        this.$message({
                            type: 'success',
                            message: "新增成功",
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                })

            },
        },
        watch: {
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.tableList = val.data;
            }
        }
    }
</script>
