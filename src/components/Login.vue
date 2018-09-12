<style lang="less" type="text/less">
    #login {
        height: 100%;
        position: relative;
        /*头部*/
        header {
            height: 54px;
            width: 100%;
            background-color: #fff;
            box-sizing: border-box;
            padding: 0 60px;
            position: absolute;
            z-index: 10;

            .logo{
                width: 150px;
                height: 50px;
            }
        }
        .user-feedback {
            width: 200px;
            height: 100%;
            float: right;
        }
        .user-feedback li {
            float: left;
            height: 100%;
            line-height: 54px;
            margin-right: 40px;
            font-size: 16px;
            color: #333;
            cursor: pointer;
            text-align: center;
        }
        .user-feedback li:hover {
            color: #3a8ee6;
        }
        .content {
            height: 100%;
            padding-top: 54px;
            box-sizing: border-box;
            background: url(../assets/login_bg_max3.jpg) no-repeat;
            background-size: 100% 100%;
            min-width: 1060px;
            min-height: 560px;
            /*登录*/
            .login-area {
                width: 280px;
                background-color: rgba(255, 255, 255, 0.1);
                margin: 40px 0 0 120px;
                position: relative;
                padding: 20px 20px 50px 20px;
            }
            .login-area > span {
                position: absolute;
                right: 0;
                top: 0;
                width: 60px;
                height: 60px;
                cursor: pointer;
                background: url(../assets/icon_login_toggle.png);
                background-position: 0 0;
                transition: background-position 0.2s;
            }
            .login-area .toggle-pass {
                background-position: -58px -58px;
            }
        }
        /*账号密码输入区*/
        .user-message {
            h2 {
                text-align: center;
                color: #fff;
                font-weight: 400;
                font-size: 18px;
                margin-bottom: 15px;
                margin-top: 0;
            }
            .error-message {
                color: #8a6d3b;
                background-color: #fcf8e3;
                border-color: #faebcc;
                padding: 0 10px;
                white-space: nowrap;
                max-width: 200px;
                margin: auto;
                text-align: center;
            }
            .user-name, .user-pass {
                width: 100%;
                height: 38px;
                border: 1px solid transparent;
                background-color: rgba(255, 255, 255, 0.08);
                margin: 15px 0;
            }
            input {
                width: 220px;
                height: 22px;
                vertical-align: bottom;
                border: none;
                background: none;
                color: #fff;
            }
            .el-button {
                font-size: 14px;
                font-family: "Microsoft YaHei";
                color: #fff;
            }
            .login-button {
                width: 100%;
                height: 35px;
                line-height: 22px;
            }
            .text-button {
                float: right;
                margin-top: 10px;
            }
        }
        .user-name span, .user-pass span {
            display: inline-block;
            width: 18px;
            height: 18px;
            background: url(../assets/icon_nav.png);
            text-align: center;
            margin: 10px 10px 0 6px;
        }
        .user-name span {
            background-position: -80px -25px;
        }
        .user-pass span {
            background-position: -100px -26px;
        }
        /*二维码扫码区*/
        .code-login {
            text-align: center;
            color: #fff;
            > div span {
                color: #eab035;
            }
        }
        /*公司信息*/
        .footer-text {
            position: absolute;
            bottom: 10px;
            left: 50%;
            margin-left: -223px;
            color: rgba(255, 255, 255, 0.5);
            font-size: 14px;
        }
    }

</style>

<template>
    <div id="login">
        <header>
            <img class="logo" src="../assets/logo.jpg">
            <!--<ul class="user-feedback">
                <li>首页</li>
                <li>用户反馈</li>
            </ul>-->
        </header>
        <div class="content">
            <div class="login-area">
                <!--<span :class="{'toggle-pass':!isPassword}"
                      @click="isPassword=!isPassword"></span>-->

                <div v-show="isPassword" class="user-message">
                    <h2>用户登录</h2>

                    <div class="error-message" v-text="errorMessage"></div>
                    <div class="user-name">
                        <span></span>
                        <input type="text" placeholder="请输入您的登录账号" v-model="userIdentify" autofocus/>
                    </div>
                    <div class="user-pass">
                        <span></span>
                        <input type="password" v-model="passWord" @keyup.enter="submit"/>
                    </div>
                    <el-button type="primary" size="mini"
                               class="login-button"
                               @click="submit">登录
                    </el-button>
                    <!--<el-button type="text" class="text-button" size="mini">忘记密码？</el-button>-->
                </div>
                <div v-show="!isPassword" class="code-login">
                    <div>请使用<span>司库立方</span>扫码</div>
                </div>
            </div>
        </div>
        <!--Copyright &copy; 2009光大永明人寿保险有限公司 津ICP备05001011-->
        <div class="footer-text"></div>
    </div>
</template>

<script>
    //引入密码加密方式
    import security from "../js/security";

    export default {
        name: "",
        created: function () {
            var routeThis = this;
            this.$axios({
                url: "/cfm/",
                method: "get"
            }).then(function (result) {
                var data = result.data;
                routeThis.exponent = data.exponent;
                routeThis.modulus = data.modulus;
            }).catch(function (error) {
                console.log(error);
            })
        },
        data: function () {
            return {
                isPassword: true,
                errorMessage: "",
                //加密模板
                exponent: "",
                modulus: "",
                //账号密码
                userIdentify: "",
                passWord: ""
            }
        },
        methods: {
            //提交
            submit: function () {
                var userIdentify = this.userIdentify.trim();
                var passWord = this.passWord.trim();
                if(!userIdentify){
                    this.$message({
                        type:"error",
                        message: "请输入登录名",
                        duration: 2000
                    })
                    return;
                }
                if(!passWord){
                    this.$message({
                        type:"error",
                        message: "请输入密码",
                        duration: 2000
                    })
                    return;
                }

                //判断是否获取到加密信息
                if(!this.exponent){
                    this.$message({
                        type:"error",
                        message: "服务请求错误，请刷新页面或检查网络",
                        duration: 2000
                    })
                    return;
                }
                //密码加密
                var key = RSAUtils.getKeyPair(this.exponent, '', this.modulus);
                var encryptionPass = RSAUtils.encryptedString(key, this.passWord);

                var routeThis = this;
                this.$axios({
                    url: "/cfm/login",
                    method: "post",
                    data: {
                        optype: "login",
                        params: {
                            login_name: this.userIdentify,
                            password: encryptionPass
                        }
                    }
                }).then( (result) => {
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                        return;
                    }
                    var data = result.data;
                    //保存token和user信息
                    if(data.token){
                        routeThis.$store.commit("set_token",data);
                        routeThis.$router.push("/home");
                    }
                    if(data.constants){
                        window.sessionStorage.setItem("constants",JSON.stringify(data.constants));
                    }
                    if(data.uuid){
                        window.sessionStorage.setItem("uuid",JSON.stringify(data.uuid));
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            }
        },
        watch: {}
    }
</script>
