<style lang="less" type="text/less" scoped>
    /*业务追踪*/
    .busTrackContainer{
        margin-top: 20px;
        padding-bottom: 20px;
        min-height: 160px;
        /*详情弹出框区域分割样式*/
        .form-small-title {
            // font-weight: bold;
            border-bottom: 1px solid #e3e3e3;
            padding-bottom: 8px;
            margin-bottom: 15px;
            position: relative;
            span:first-child {
                display: inline-block;
                width: 4px;
                height: 16px;
                background-color: orange;
                margin-right: 6px;
                vertical-align: middle;
            }
            .seeMore{
                position: absolute;
                right: 0;
                top: -4px;
            }
        }

        .nodeBox{
            margin-bottom: 15px;
            .node-item{
                text-align: center;
                position: relative;
                .pointCon{
                    background-color: #fff;
                    .pointer{
                        width: 6px;
                        height: 6px;
                        border: 2px solid #e0e0e0;
                        border-radius: 6px;
                        display: inline-block;
                        margin: 0 2px;
                    }
                }
                >span{
                    display: inline-block;
                    width: 100%;
                    overflow: hidden;
                    text-overflow: ellipsis;
                    white-space: nowrap;
                }
                >span.name{
                    width: calc(100% - 40px);
                    padding: 0 20px;
                }
                .pointCon{
                    width: 14px;
                }
                .time{
                    font-size: 12px;
                    color: #777;
                }
                .parse {
                    background-color: #6bbdf8;
                    border-color: #6bbdf8!important;
                }

            }
        }

        .rightContainer{
            width: 420px;
            height: 604px;
            position: fixed;
            height: 100%;
            background-color: #fff;
            top: 52px;
            z-index: 1000;
            padding: 10px;
            box-shadow: 0px 5px 5px rgba(153, 153, 153, 0.5);
            transition: right 0.8s;
            .rightContent{
                height: 100%;
                overflow-y: auto;
            }
            .flowlinesVTitle {
                border-bottom: 1px solid #e3e3e3;
                height: 30px;
                >.title{
                    float: left;
                    line-height: 30px;
                }
                .titleText {
                    color: #ccc;
                }
                i{
                    position: absolute;
                    top: 11px;
                    color: #333;
                    right: 14px;
                    cursor: pointer;
                    font-size: 18px;
                }
            }
            .flowlinesVContainer{
                padding-bottom: 100px;
                position: relative;
                .node-item{
                    padding: 10px 0;
                    height: 120px;
                    .time{
                        width: 80px;
                        text-align: right;
                        display: block;
                        float: left;
                        font-size: 12px;
                        color: #777;
                        margin-top: 15px;
                    }
                    .parse {
                        background-color: #6bbdf8;
                        border-color: #6bbdf8!important;
                    }
                    .node{
                        width: 30px;
                        margin-top: 15px;
                        float: left;
                        text-align: center;
                    }
                    .pointCon{
                        background-color: #fff;
                        width: 14px;
                        .pointer{
                            width: 6px;
                            height: 6px;
                            border: 2px solid #e0e0e0;
                            border-radius: 6px;
                            display: inline-block;
                            margin: 0 2px;
                        }
                    }
                    .personImg{
                        margin-right: 10px;
                        float: left;
                        .img-box{
                            display: inline-block;
                            width: 50px;
                            height: 50px;
                            border-radius: 30px;
                            border: 1px solid #eee;
                            padding: 2px;
                            box-shadow: 0px 1px 1px rgba(153,153,153,0.3);
                        }
                        .flowlinesV-img{
                            width: 51px;
                            height: 51px;
                            display: inline-block;
                            background-image: url(../../assets/yz_ic_user.png);
                            background-repeat: no-repeat;
                        }
                        .img1{
                            background-position: -15px -11px;
                        }
                    }
                    .main{
                        width: 200px;
                        overflow: hidden;
                        text-overflow: ellipsis;
                        white-space: nowrap;
                        float: left;
                        .name{
                            font-size: 14px;
                            margin-right: 10px;
                            color: #000;
                            height: 22px;
                            line-height: 22px;
                            display: inline-block;
                            width: 100%;
                            text-align: left;
                        }
                        .node-name{
                            display: inline-block;
                            padding: 6px 0;
                            margin-right:5px;
                            .el-tag--small {
                                height: 26px;
                                line-height: 24px;
                            }
                            .el-tag{
                                border-radius: 14px;
                            }
                        }
                        .message{
                            color: #888;
                            width: 100%;
                            display: block;
                            text-align: left;
                            text-overflow: ellipsis;
                            overflow: hidden;
                            white-space: nowrap;
                        }
                    }
                }
            }
            .rightsliders{
                position: absolute;
                left: 94px;
                width: 2px;
                top: 38px;
                z-index: -1;
                .sliders{
                    position: absolute;
                    background: #eee;
                    width: 2px;
                }
                .fills{
                    position: absolute;
                    background-color: #6bbdf8;
                    width: 2px;
                }
            }
        }
        .right500{
            right: -500px;
        }
        .right0{
            right:0px;
        }
    }
</style>
<style lang="less" type="text/less">
    /*进度条样式*/
    .slidersContainer{
        top: 32px;
        position: relative;
        .el-progress-bar{
            padding: 0;
            margin: 0;
        }
    }
</style>
<template >
    <div class="busTrackContainer">
        <div v-if="showRowline">
            <el-row>
                <el-col :span="24"  class="form-small-title">
                    <span></span>
                    <span>业务状态跟踪</span>
                    <el-button class="seeMore" size="mini" round plain @click="showRight">查看更多<i class="el-icon-arrow-right"></i></el-button>
                </el-col>
            </el-row>
            <div class="slidersContainer">
                <el-progress :style="{width: generalLength + '%', left: leftDistance + '%'}" :percentage="percentage" :show-text="false" :stroke-width="2"></el-progress>
            </div>
            <el-row class="nodeBox">
                <el-col :span="divideCol" class="node-item" v-show="submiter.show">
                    <span class="name">{{submiter.init_user_name}}</span>
                    <span class="pointCon"><em class="pointer parse"></em></span>
                    <span class="node-name"><el-tag type="info" size="mini">提交</el-tag></span>
                    <span class="time">{{submiter.lastSubmit_time}}</span>
                </el-col>
                <el-col :span="divideCol" class="node-item" v-show="history.show">
                    <span class="name">{{history.assignee}}</span>
                    <span class="pointCon"><em class="pointer parse"></em></span>
                    <span class="node-name"><el-tag type="info" size="mini">自定义节点</el-tag></span>
                    <span class="time">{{history.end_time}}</span>
                </el-col>
                <el-col :span="divideCol" class="node-item" v-show="current.show">
                    <span class="name" :title="current.name">{{current.name}}</span>
                    <span class="pointCon"><em class="pointer"></em></span>
                    <!-- <span class="node-name"><el-tag type="info" size="mini">当前节点</el-tag></span> -->
                    <!-- <span class="time">{{current.start_time}}</span> -->
                </el-col>
                <el-col :span="divideCol" class="node-item" v-show="future.show">
                    <span class="name" :title="future.name">{{future.name}}</span>
                    <span class="pointCon"><em class="pointer"></em></span>
                    <!-- <span class="node-name"><el-tag type="info" size="mini">未来节点</el-tag></span> -->
                    <!-- <span class="time">{{future.start_time}}</span> -->
                </el-col>
            </el-row>
        </div>
        <div :class="[businessParams.type?'right0':'right500','rightContainer']">
            <div class="flowlinesVTitle">
                <span class="title">审批记录</span>
                <!-- <span class="titleText">[ ITP201801230000001 ]</span> -->
                <i class="el-icon-close" @click="closeRight"></i>
            </div>
            <div class="rightContent">
                <div class="flowlinesVContainer">
                    <div class="rightsliders" :style="{height: rightLinesHeight + 'px'}">
                        <div class="sliders" :style="{height: rightLinesHeight + 'px'}"></div>
                        <div class="fills" :style="{height: rightFillsHeight + 'px'}"></div>
                    </div>
                    <el-row>
                        <el-col :span="24" class="node-item" v-show="submiter.show">
                            <span class="time">{{submiter.initSubmit_time}}</span>
                            <div class="node"><span class="pointCon"><em class="pointer parse"></em></span></div>
                            <div class="personImg">
                                <span class="img-box">
                                    <em class="flowlinesV-img img1"></em>
                                </span>
                            </div>
                            <div class="main">
                                <span class="name">{{submiter.init_user_name}}</span>
                                <div>
                                    <span class="node-name"><el-tag size="small">发起</el-tag></span>
                                    <span>提交</span>
                                </div>
                            </div>
                        </el-col>
                        <template v-for="history in historyList">
                            <el-col :span="24" class="node-item" :key="history.id">
                                <span class="time">{{history.end_time}}</span>
                                <div class="node"><span class="pointCon"><em class="pointer parse"></em></span></div>
                                <div class="personImg">
                                    <span class="img-box">
                                        <em class="flowlinesV-img img1"></em>
                                    </span>
                                </div>
                                <div class="main">
                                    <span class="name">{{history.assignee}}</span>
                                    <div v-show="history.assignee_type">
                                        <span class="node-name"><el-tag size="small">{{history.assignee_name}}</el-tag></span>
                                        <span>自定义节点</span>
                                    </div>
                                    <span class="message" :title="history.assignee_memo">{{history.assignee_memo}}</span>
                                </div>
                            </el-col>
                        </template>
                        <el-col :span="24" class="node-item" v-show="current.show">
                            <span class="time"></span>
                            <div class="node"><span class="pointCon"><em class="pointer"></em></span></div>
                            <div class="personImg">
                                <span class="img-box">
                                    <em class="flowlinesV-img img1"></em>
                                </span>
                            </div>
                            <div class="main">
                                <span class="name">{{current.name}}</span>
                            </div>
                        </el-col>
                        <!-- <template v-for="future in futureList"> -->
                        <el-col :span="24" class="node-item" v-show="future.show">
                            <span class="time"></span>
                            <div class="node"><span class="pointCon"><em class="pointer"></em></span></div>
                            <div class="personImg">
                                <span class="img-box">
                                    <em class="flowlinesV-img img1"></em>
                                </span>
                            </div>
                            <div class="main">
                                <span class="name" :title="future.name">{{future.name}}</span>
                            </div>
                        </el-col>
                        <!-- </template> -->
                    </el-row>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        name: "BusinessTracking",
        created:function(){
            if(this.businessParams.id){
                if(this.businessParams.type){
                    this.showRowline = false;
                }
                this.getBusinessData(this.businessParams);
            }
            this.assigneeTypeList ={
                1:"同意",
                2:"拒绝",
                3:"加签"
            }
        },
        mounted:function(){
        },
        props:["businessParams"],
        data:function(){
            return {
                queryUrl: this.$store.state.queryUrl,
                submiter:{},
                history:{},
                historyList:[],
                current:{},
                future:{},
                futureList:[],
                divideCol:0 ,//横线每个节点所占宽度
                generalLength:0,//横线灰线的长度
                leftDistance:0,//横线的marginLeft
                percentage:0,//横线的蓝线长度
                rightLinesHeight:0,//竖线的灰线长度
                rightFillsHeight:0,//竖线的蓝线长度
                assigneeTypeList:{},
                showRowline:true
            }
        },
        methods:{
            //获取数据
            getBusinessData:function(param){
                //清空数据
                this.submiter = {};
                this.history = {};
                this.current = {};
                this.future = {};
                //加载业务跟踪状态数据
                this.$axios({
                    url:this.queryUrl + "commProcess",
                    method:"post",
                    data:{
                        optype:"wfquery_approvedetail",
                        params:{
                            id:param.id,
                            biz_type:param.biz_type
                        }
                    }
                }).then((result) =>{
                    if (result.data.error_msg) {
                        this.$message({
                            type: "error",
                            message: result.data.error_msg,
                            duration: 2000
                        })
                    }else{
                        let i = 4;
                        let data = result.data.data;
                        this.submiter = data.submiter[0];
                        this.submiter.show = this.submiter ? true : false;
                        let cL = data.current.length;
                        this.current = cL > 0 ? data.current[0] : {};
                        this.current.show = cL > 0 ? true : false;
                        if(cL > 0){
                            this.current.name = this.rename(data.current,cL);
                        }
                        //current,future的名字要显示所有的
                        //history,future取记录最后一条
                        let future = data.future;
                        let fL = future.length;
                        this.future = fL > 0 ? data.future[fL-1] : {} ;
                        this.future.show = fL > 0 ? true : false;
                        if(fL > 0){
                            this.future.name = this.rename(data.future,fL);
                        }
                        let hL = data.history.length;
                        if(hL){
                            data.history.forEach(element => {
                                element.assignee_name =  this.transitionType(element.assignee_type);
                            });
                        }
                        let history = data.history;
                        this.history = hL > 0 ? data.history[hL-1] : {} ;
                        this.history.show = hL > 0 ? true : false;


                        if(!this.businessParams.type){
                            if(!this.current.show) i --;
                            if(!this.future.show ) i --;
                            if(!this.history.show){
                                i --;
                                // 100/2(i-1)
                                this.percentage = 50 / (i - 1);
                            } else{
                                if(i === 4)
                                    this.percentage = 50;
                                if(i === 3)
                                    this.percentage = 75;
                                if(i === 2)
                                    this.percentage = 100;
                            }

                            //处理线条样式
                            this.divideCol = 24 / i;
                            this.generalLength = 100 / i * (i - 1);
                            this.leftDistance = 100 / (i * 2);
                            //本想打算让这个请求结束在让弹框显示，但是如果该弹框不显示，子组件都不会调用
                            // this.$emit("showDialog");
                        }

                        //组装右侧数据
                        this.historyList = history;
                        // this.futureList = future;
                        cL = this.current.show ? 1 : 0;
                        fL = this.future.show ? 1 : 0;
                        this.rightLinesHeight = (1 + cL + hL + fL -1) * 120;
                        if(cL === 0 && fL === 0)
                            this.rightFillsHeight = hL * 120;
                        else
                            this.rightFillsHeight = hL * 120 + 60;

                    }
                })
            },
            showRight:function(){
                document.getElementsByClassName("rightContainer")[0].style.right = "0";
            },
            closeRight:function(event){
                if(this.businessParams.type){
                    this.$emit("closeRightDialog");
                }else{
                    event.currentTarget.parentNode.parentNode.style.right = "-500px";
                }
            },
            transitionType:function(id){
                return this.assigneeTypeList[id];
            },
            //current,future重新赋值名字
            rename: function(list, len){
                var new_name = "";
                list.forEach((element,index) =>{
                    if(element.name){
                        new_name = index===len-1 ? new_name + element.name : new_name + element.name + "|";
                    }
                })
                return new_name;
            }
        },
        watch:{
            businessParams:function(val, oldVal){
                if(val.id){
                    this.getBusinessData(val);
                }
                if(val.type){
                    this.showRowline = false;
                }
            }
        }
    }
</script>
