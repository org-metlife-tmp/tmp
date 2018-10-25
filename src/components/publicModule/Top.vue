<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped lang="less" type="text/less">
    #titleTop {
        height: 52px;
        width: 100%;
        background: url("../../assets/header_bg.jpg") no-repeat;
        background-color: #649cc3;
        position: relative;

        .company-name, .user-message {
            position: absolute;
            height: 100%;
            line-height: 52px;
            color: #fff;
        }

        .company-name {
            right: 220px;
            width: 260px;
            text-align: center;
            border-right: 1px solid rgba(255, 255, 255, 0.1);
            cursor: default;
        }

        .company-name:before {
            content: " ";
            position: absolute;
            background-image: url("../../assets/icon_nav.png");
            background-position: -80px 0;
            width: 25px;
            height: 25px;
            top: 13px;
            left: 0;
        }

        .company-name:after {
            content: " ";
            position: absolute;
            width: 84%;
            margin: 0;
            height: 5px;
            top: 29px;
            left: 25px;
            border-width: 0 1px 1px;
            border-style: solid;
        }
    }

    .user-message {
        right: 30px;
        width: 180px;
        cursor: pointer;
    }

    .user-message:before {
        content: " ";
        background-image: url("../../assets/icon_nav.png");
        background-position: -80px -25px;
        width: 22px;
        height: 25px;
        position: absolute;
        left: 20px;
        top: 16px;
    }

    .user-operation {
        position: absolute;
        background-color: #fff;
        right: 30px;
        top: 40px !important;
        width: 180px;
        border: 2px solid #3FB0D8;
        text-align: center;
        border-top: none;
    }

    .user-operation li {
        padding: 4px 0;
        cursor: pointer;
        color: #178fc8;
    }

    .user-operation li:hover {
        background-color: #6bc8e8;
        color: #fff;
    }

    .indexButton {
        position: absolute;
        top: 0;
        left: 0;
        width: 160px;
        height: 52px;
        background-color: rgba(0, 0, 0, 0);
    }
</style>
<template>
    <div id="titleTop">
        <router-link :to="{name:'Home'}" class="indexButton"></router-link>
        <p class="company-name" v-text="companyName" v-show="showCompany"></p>
        <el-dropdown class="user-message" @command="handleCommand">
            <p v-text="userName"></p>
            <el-dropdown-menu class="user-operation"
                              v-show="userNeed"
                              slot="dropdown">
                <el-dropdown-item v-for="operation in userOperation"
                                  v-text="operation.content"
                                  :key="operation.id"
                                  :command="operation.id"
                                  @click=""></el-dropdown-item>
            </el-dropdown-menu>
        </el-dropdown>
        <!--个人设置 弹出框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="800px" top="76px" title="个人设置"
                   :append-to-body="true"
                   :close-on-click-modal="false">
            <el-form :model="dialogData" size="small"
                     :label-width="formLabelWidth"
                     :rules="rules" ref="dialogForm">
                <el-row>
                    <el-col :span="12">
                        <el-form-item label="姓名">
                            <el-input v-model="dialogData.name" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="登录名称">
                            <el-input v-model="dialogData.login_name" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="邮箱" prop="email">
                            <el-input v-model="dialogData.email"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12">
                        <el-form-item label="手机号" prop="phone">
                            <el-input v-model="dialogData.phone"></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <div class="split-form">
                            <h4>所属机构部门</h4>
                        </div>
                    </el-col>
                    <el-col :span="24" style="margin-bottom:24px">
                        <el-table :data="dialogData.udops"
                                  border size="mini"
                                  style="width:96%;float:right"
                                  highlight-current-row
                                  @current-change="udopsCurrentChange">
                            <el-table-column label="当前所属" width="70">
                                <template slot-scope="scope">
                                    <el-radio v-model="scope.row.$current" label="1">{{emptyData}}</el-radio>
                                </template>
                            </el-table-column>
                            <el-table-column prop="org_name" label="所属机构"
                                             :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column prop="dept_name" label="所属部门"
                                             :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column prop="pos_name" label="所属职位"
                                             :show-overflow-tooltip="true"></el-table-column>
                            <el-table-column prop="is_default" label="默认" width="60"
                                             :formatter="transitDefault"></el-table-column>
                        </el-table>
                    </el-col>
                </el-row>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button type="warning" size="mini" @click="subCurrent">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    import axios from "axios";

    export default {
        name: 'Top',
        created: function () {
            var user = this.$store.state.user;
            this.userName = user.name;
            if (user.is_admin) {
                this.showCompany = false;
            } else {
                var uodps = user.uodp;
                for (var i = 0; i < uodps.length; i++) {
                    if (uodps[i].is_default == "1") {
                        this.companyName = uodps[i].org_name;
                        break;
                    }
                }
            }
        },
        data: function () {
            return {
                companyName: "",
                userName: "用户名",
                showCompany: true,
                userOperation: [
                    {content: "个人设置", id: "1"},
                    // {content:"修改登录密码",id:"2"},
                    {content: "退出", id: "3"}
                ],
                userNeed: false,
                dialogVisible: false, //个人设置弹框
                dialogData: {
                    name: "",
                    email: "",
                    login_name: "",
                    phone: "",
                    udops: []
                },
                udopsList: [],
                formLabelWidth: "120px",
                emptyData: "", //为了展示数据为空
                //校验规则设置
                rules: {
                    email: {
                        type: "email",
                        message: "请输入正确的邮箱格式",
                        trigger: "blur"
                    },
                    phone: {
                        validator: function (rule, value, callback, source, options) {
                            if (!value) {
                                callback();
                            }
                            var reg = /^[1][0-9]{10}$/;
                            if (reg.test(value)) {
                                callback();
                            } else {
                                var errors = [];
                                callback(new Error("请输入正确的手机号"));
                            }
                        },
                        trigger: "blur"
                    }
                },
            }
        },
        methods: {
            //用户操作
            handleCommand: function (command) {
                //个人设置
                if (command == "1") {
                    this.dialogVisible = true;
                    this.setUserInfo();
                }
                //修改登录密码
                if (command == "2") {

                }
                //帮助中心
                if (command == "3") {

                }
                //退出
                if (command == "3") {
                    this.$store.commit("del_token");
                    this.$router.push({name: "Login"});
                }
            },
            //个人设置数据操作
            setUserInfo: function () {
                //清空数据
                var dialogData = this.dialogData;
                for(var k in dialogData){
                    if(k == "udops"){
                        dialogData[k] = [];
                    }else{
                        dialogData[k] = "";
                    }
                }

                //获取新数据
                this.$axios({
                    url: "/cfm/commProcess",
                    method: "post",
                    data: {
                        optype: "user_userinfo",
                        params: {}
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
                        data.udops.forEach((item) => {
                            if (item.id == data.cur_uodp_id) {
                                item.$current = "1";
                            } else {
                                item.$current = "0";
                            }
                        })
                        var dialogData = this.dialogData;
                        for (var k in data) {
                            dialogData[k] = data[k];
                        }
                    }
                }).catch(function (error) {
                    console.log(error);
                });
            },
            //展示格式转换-是否默认
            transitDefault: function (row, column, cellValue, index) {
                if (cellValue == 1) {
                    return "是"
                } else {
                    return "";
                }
            },
            //修改用户当前所属
            udopsCurrentChange: function (val) {
                if (val) {
                    this.dialogData.udops.forEach((item) => {
                        item.$current = "0";
                    })
                    val.$current = "1";
                }
            },
            //保存个人设置信息
            subCurrent: function () {
                this.$refs.dialogForm.validate((valid, object) => {
                    if (valid) {
                        var dialogData = this.dialogData;
                        var udops = dialogData.udops;
                        var setCurrent = false;
                        for (var i = 0; i < udops.length; i++) {
                            var item = udops[i];
                            if (item.$current == "1" && item.id != dialogData.cur_uodp_id) {
                                setCurrent = true;
                                this.$axios({
                                    url: "/cfm/commProcess",
                                    method: "post",
                                    data: {
                                        optype: "user_switchuodp",
                                        params: {
                                            uodp_id: item.id
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
                                        this.saveBaseInfo();
                                    }
                                }).catch(function (error) {
                                    console.log(error);
                                });
                                break;
                            }
                        }
                        if(!setCurrent){
                            this.saveBaseInfo();
                        }
                    } else {
                        return false;
                    }
                })
            },
            //保存基本信息
            saveBaseInfo: function(){
                this.$axios({
                    url: "/cfm/commProcess",
                    method: "post",
                    data: {
                        optype: "user_chg",
                        params: this.dialogData
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    } else {
                        var data = result.data.data;
                        this.dialogVisible = false;
                        //保存用户信息并返回首页
                        var userData = {
                            token: this.$store.state.token,
                            user: data.menu_info,
                        }
                        this.$store.commit("set_token",userData);
                        this.$router.push("/");
                        this.$router.push({
                            name:"Home",
                            params:{
                                refreshUser: true
                            }
                        });
                        this.$message({
                            type: 'success',
                            message: "修改成功",
                            duration: 2000
                        });
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            }
        }
    }
</script>

