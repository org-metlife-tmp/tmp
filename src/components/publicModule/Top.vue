<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
    #titleTop {
        height: 52px;
        width: 100%;
        background: url("../../assets/header_bg.jpg") no-repeat;
        background-color: #649cc3;
        position: relative;
    }

    .company-name, .user-message {
        position: absolute;
        height: 100%;
        line-height: 52px;
        color: #fff;
    }

    .company-name {
        right: 220px;
        width: 200px;
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
        width: 80%;
        margin: 0;
        height: 5px;
        top: 29px;
        left: 25px;
        border-width: 0 1px 1px;
        border-style: solid;
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

    .indexButton{
        position: absolute;
        top: 0;
        left: 0;
        width: 160px;
        height: 52px;
        background-color: rgba(0,0,0,0);
    }
</style>
<template>
    <div id="titleTop">
        <router-link :to="{name:'Home'}" class="indexButton"></router-link>
        <p class="company-name" v-text="companyName"></p>
        <el-dropdown class="user-message"  @command="handleCommand">
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
    </div>
</template>

<script>
    import axios from "axios";
    export default {
        name: 'Top',
        created:function(){
            this.userName = this.$store.state.user.name;
        },
        data: function () {
            return {
                companyName: "集团公司",
                userName: "用户名",
                userOperation: [
                    {content:"个人设置",id:"1"},
                    {content:"修改登录密码",id:"2"},
                    {content:"帮助中心",id:"3"},
                    {content:"退出",id:"4"}
                ],
                userNeed: false
            }
        },
        methods: {
            //用户操作
            handleCommand:function(command){
                //个人设置
                if(command == "1"){

                };
                //修改登录密码
                if(command == "2"){

                };
                //帮助中心
                if(command == "3"){

                };
                //退出
                if(command == "4"){
                    this.$store.commit("del_token");
                    this.$router.push({name:"Login"});
                }
            }
        }
    }
</script>

