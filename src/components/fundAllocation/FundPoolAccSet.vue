<style scoped lang="less" type="text/less">
    #fundPoolAccSet{
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

        /*数据展示区*/
        .table-content{
            height: 397px;
        }

        /*分页部分*/
        .botton-pag {
            position: absolute;
            width: 100%;
            height: 8%;
            bottom: -6px;

            .el-button {
                float: right;
                margin-top: -30px;
            }
        }
        .switch{
            position: absolute;
            left: 20px;
            bottom: 25px;
        }
    }
</style>
<style lang="less">
    #fundPoolAccSet {
        .el-dialog__wrapper {
            .el-dialog__body {
                height: 300px;
                overflow-y: auto;
            }
        }
        .el-table__expanded-cell[class*=cell] {
            padding: 20px;
        }
    }
</style>

<template>
    <div id="fundPoolAccSet">
        <!-- 顶部按钮-->
        <div class="button-list-right">
            <el-button type="warning" size="mini" @click="addAccount">新增</el-button>
        </div>
        <!--数据展示区-->
        <section class="table-content">
            <el-table :data="tableList"
                      border
                      height="100%"
                      size="mini">
                <el-table-column prop="acc_no" label="账户号" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="acc_name" label="账户名称" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column prop="bank_name" label="银行大类" :show-overflow-tooltip="true"></el-table-column>
                <el-table-column
                        label="是否默认" width="100"
                        fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <span v-show="scope.row.flag">默认</span>
                        <el-button type="warning" size="mini" v-show="!scope.row.flag"
                            @click="setDefault(scope.row)">设为默认</el-button>
                    </template>
                </el-table-column>
                <el-table-column
                            label="操作" width="80"
                            fixed="right">
                    <template slot-scope="scope" class="operationBtn">
                        <el-tooltip content="删除" placement="bottom" effect="light"
                                    :enterable="false" :open-delay="500">
                            <el-button type="danger" icon="el-icon-delete" size="mini"
                                        @click="delAcc(scope.row,scope.$index,tableList)"></el-button>
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
                    :page-sizes="[11, 50, 100, 500]"
                    :pager-count="5"
                    :current-page="pagCurrent"
                    @current-change="getCurrentPage"
                    @size-change="sizeChange">
            </el-pagination>
        </div>
        <!--待处理新增&修改弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="800px"
                   :close-on-click-modal="false"
                   top="56px">
            <h1 slot="title" v-text="dialogTitle" class="dialog-title"></h1>
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="银行大类">
                            <el-select v-model="dialogData.bankType" placeholder="请选择银行大类"
                                       clearable filterable
                                       :filter-method="filterBankType"
                                       @visible-change="clearSearch">
                                <el-option v-for="bankType in bankTypeList"
                                           :key="bankType.name"
                                           :label="bankType.name"
                                           :value="bankType.code">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span=12 style="height:51px"></el-col>
                    <el-col :span="12">
                        <el-form-item label="账户号">
                            <el-select v-model="dialogData.acc" @change="changeAccount" clearable value-key="acc_no">
                                <el-option
                                    v-for="item in accOptions"
                                    :key="item.acc_no"
                                    :label="item.acc_no"
                                    :value="item">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="账户名称">
                            <el-input v-model="dialogData.acc_name" ></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-switch
                    class="switch"
                    v-model="dialogData.value3"
                    active-text="默认">
                </el-switch>
                <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                <el-button type="warning" size="mini" @click="subAdd">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "FundPoolAccSet",
        created: function () {
            this.$emit("transmitTitle", "资金池账户设置");
            this.$emit("getCommTable", this.routerMessage);
        },
        mounted: function () {
            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
                this.bankTypeList = bankTypeList;
            }
        },
        props:["tableData"],
        data: function () {
            return {
                routerMessage: {
                    optype: "opencom_todolist",
                    params: {
                        page_size: 11,
                        page_num: 1
                    }
                },
                tableList:[],
                pagSize: 11, //分页数据
                pagTotal: 1,
                pagCurrent: 1,
                formLabelWidth: "120px",
                dialogTitle: "新增",
                dialogVisible: false,
                dialogData: {},
                bankAllList: [], //银行大类全部
                bankTypeList: [], //银行大类
                accOptions:[],//账户号下拉数据,
            }
        },
        methods: {
            //根据条件查询数据
            queryData:function(){
                var searchData = this.searchData;
                for(var k in searchData){
                    if(this.isPending){
                        this.routerMessage.todo.params[k] = searchData[k];
                    }else{
                        this.routerMessage.done.params[k] = searchData[k];
                    }
                }
                this.$emit("getCommTable", this.routerMessage);
            },
            //点击页数获取当前页数据
            getCurrentPage:function(currPage){
                if(this.isPending){
                    this.routerMessage.todo.params.page_num = currPage;
                }else{
                    this.routerMessage.done.params.page_num = currPage;
                }
                this.$emit("getCommTable", this.routerMessage);
            },
            //当前页数据条数发生变化
            sizeChange:function(val){
                this.routerMessage.todo.params = {
                    page_size: val,
                    page_num: 1
                };
                this.routerMessage.done.params = {
                    page_size: val,
                    page_num: 1
                };
                this.$emit("getCommTable", this.routerMessage);
            },
            //设为默认
            setDefault: function (row) {

            },
            //增加
            addAccount: function(){
                this.$axios({
                    url:"/cfm/normalProcess",
                    method:"post",
                    data:{
                        optype:"account_accs",
                        params:{
                            status:1,
                            acc_id:""
                        }
                    }
                }).then((result) =>{
                    this.accOptions = result.data.data;
                });
                this.dialogVisible = true;
            },
            //银行大类搜索筛选
            filterBankType: function (value) {
                if (value && value.trim()) {
                    this.bankTypeList = this.bankAllList.filter(item => {
                        var chineseReg = /^[\u0391-\uFFE5]+$/; //判断是否为中文
                        var englishReg = /^[a-zA-Z]+$/; //判断是否为字母
                        var quanpinReg = /(a[io]?|ou?|e[inr]?|ang?|ng|[bmp](a[io]?|[aei]ng?|ei|ie?|ia[no]|o|u)|pou|me|m[io]u|[fw](a|[ae]ng?|ei|o|u)|fou|wai|[dt](a[io]?|an|e|[aeio]ng|ie?|ia[no]|ou|u[ino]?|uan)|dei|diu|[nl][gh]ei|[jqx](i(ao?|ang?|e|ng?|ong|u)?|u[en]?|uan)|([csz]h?|r)([ae]ng?|ao|e|i|ou|u[ino]?|uan)|[csz](ai?|ong)|[csz]h(ai?|uai|uang)|zei|[sz]hua|([cz]h|r)ong|y(ao?|[ai]ng?|e|i|ong|ou|u[en]?|uan))/; //判断是否为全拼

                        if (chineseReg.test(value)) {
                            return item.name.toLowerCase().indexOf(value.toLowerCase()) > -1;
                        } else if (englishReg.test(value)) {
                            if (quanpinReg.test(value)) {
                                return item.pinyin.toLowerCase().indexOf(value.toLowerCase()) > -1;
                            } else {
                                return item.jianpin.toLowerCase().indexOf(value.toLowerCase()) > -1;
                            }
                        }
                    })
                } else {
                    this.bankTypeList = this.bankAllList;
                }
            },
            //银行大类展开时重置数据
            clearSearch: function () {
                if (this.bankTypeList != this.bankAllList) {
                    this.bankTypeList = this.bankAllList;
                }
            },
            //切换账户号
            changeAccount:function(cur){
                this.dialogData.acc_no = cur.acc_no;
                this.dialogData.acc_name = cur.acc_name;
            },
            //确认提交
            subAdd: function () {
                // this.dialogData;
                this.dialogVisible = false;
            },
            //删除账户
            delAcc: function (row, index, rows) {
                this.$confirm('确认删除当前账户吗?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    // var rows = this.tableList;
                    // var index = this.tableList.indexOf(row);
                    // if (this.pagCurrent < (this.pagTotal / this.pagSize)) { //存在下一页
                    //     this.$emit('getCommTable', this.routerMessage);
                    // } else {
                    //     if (rows.length == "1" && (this.routerMessage.todo.params.page_num != 1)) { //是当前页最后一条
                    //         this.routerMessage.params.page_num--;
                    //         this.$emit('getCommTable', this.routerMessage);
                    //     } else {
                    //         rows.splice(index, 1);
                    //         this.pagTotal--;
                    //     }
                    // }
                    // this.$axios({
                    //     url: "/cfm/normalProcess",
                    //     method: "post",
                    //     data: {
                    //         optype: "opencom_del",
                    //         params: {
                    //             id: row.id,
                    //             persist_version: row.persist_version
                    //         }
                    //     }
                    // }).then((result) => {
                    //     if (result.data.error_msg) {
                    //         this.$message({
                    //             type: "error",
                    //             message: result.data.error_msg,
                    //             duration: 2000
                    //         })
                    //         return;
                    //     }
                    //     this.$message({
                    //         type: "success",
                    //         message: "删除成功",
                    //         duration: 2000
                    //     })
                    //     this.$emit("getTableData", this.routerMessage);
                    // }).catch(function (error) {
                    //     console.log(error);
                    // })
                }).catch(() => {
                });
            }
        },
        watch:{
            tableData: function (val, oldVal) {
                this.pagSize = val.page_size;
                this.pagTotal = val.total_line;
                this.pagCurrent = val.page_num;
                this.tableList = val.data;
                this.tableList = [{
                    acc_no:"1111011111",
                    acc_name:"圈圈呀",
                    bank_name:"北京银行",
                    flag:true
                },
                {
                    acc_no:"223232",
                    acc_name:"圈圈2号",
                    bank_name:"上海银行",
                    flag:false
                },
                {
                    acc_no:"223232",
                    acc_name:"圈圈3号想次方便面",
                    bank_name:"上海银行",
                    flag:false
                }];
            }
        }
    }
</script>

