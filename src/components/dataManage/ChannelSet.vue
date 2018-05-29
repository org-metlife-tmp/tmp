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
                            <el-select v-model="serachData.region" placeholder="活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="7">
                        <el-form-item label="状态">
                            <el-select v-model="serachData.region" placeholder="活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="3">
                        <el-form-item>
                            <el-button type="primary" plain @click="">搜索</el-button>
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
                <el-table-column prop="is_activate" label="状态"
                                 :formatter="getStatus"
                                 width="100"
                                 :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="操作"
                        width="80">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="设置状态" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button size="mini"
                                       @click="setStatus(scope.row)"
                                       class="on-off"></el-button>
                        </el-tooltip>
                        <el-tooltip content="删除" placement="bottom" effect="light" :enterable="false" :open-delay="500">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                       @click=""></el-button>
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
                    @current-change=""
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
                            <el-select v-model="dialogData.region" placeholder="请选择活动区域">
                                <el-option label="区域一" value="shanghai"></el-option>
                                <el-option label="区域二" value="beijing"></el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="渠道名称" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.name" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="是否银企直联" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.name" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="备注" :label-width="formLabelWidth">
                            <el-input v-model="dialogData.name" auto-complete="off"></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false" size="mini">取 消</el-button>
                <el-button type="primary" @click="" size="mini">确 定</el-button>
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
        },
        props: ["tableData"],
        data: function () {
            return {
                routerMessage: { //本页数据获取参数
                    optype: "handlechannel_list",
                    params: {
                        page_size: 10,
                        page_num: 1
                    }
                },
                pagSize: 1,
                pagTotal: 1,
                tableList: [],
                serachData: {},
                dialogVisible:false,
                dialogData: {},
                formLabelWidth:"120px"
            }
        },
        methods: {
            //状态展示格式转换
            getStatus:function(row, column, cellValue, index){
                var constants = JSON.parse(window.sessionStorage.getItem("constants"));
                if(cellValue == "0"){
                    return "（无效）";
                }else{
                    return "（有效）";
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
            }
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
