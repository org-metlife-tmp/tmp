<style>
    #cake-picture {
        width: 100%;
        height: 44%;
        margin: 0 auto;
        text-align: left;
    }
</style>

<template>
    <div id="cake-picture"></div>
</template>

<script>
    export default {
        name: "CakePicture",
        mounted: function () {
            //创建饼图
            this.myChart = this.$echarts.init(document.getElementById("cake-picture"));
            this.myChart.setOption({
                //提示框组件
                tooltip: {
                    //触发类型
                    trigger: "item", //数据项图形触发
                    formatter: function (params) {
                        if(params.data.code){
                            var showData = "账户:" + params.name + "<br/>" +
                                    "账号" + params.data.code + "<br/>" +
                                    "余额:" + params.value + "<br/>" +
                                    "占比:" + params.percent;
                        }else{
                            var showData = "账户:" + params.name + "<br/>" +
                                    "余额:" + params.value + "<br/>" +
                                    "占比:" + params.percent;
                        }
                        return showData;
                    }
                },
                //图例组件
                legend: {
                    orient: 'vertical',
                    x: 'right',
                    icon: 'circle',
                    left: "56%", //图例距离容器左侧距离
                    align: "left",
                    top: 'middle',
                    itemGap: 4, //图例间隔
                    itemWidth: 8, //图例的图形宽度
                    itemHeight: 8,
                    textStyle: {
                        color: '#999'
                    },
                    borderRadius: "50%", //圆角半径
                    formatter: function (name) {
                        if (name == "其它") {
                            return name;
                        }
                        /*for (var i = 0; i < currentData.length; i++) {
                            var entity = currentData[i]
                            if (entity.name == name) {
                                return name + "[" + entity.code + "]";
                            }
                        }*/
                    }
                },
                //列表-图表
                series: [{
                    type: "pie",
                    name: "饼图", //用于tooltip的显示，legend图例的筛选
                    avoidLabelOverlap: false, //防止标签重叠
                    //图形中心的文本标签
                    label: {
                        //正常情况下的文字
                        normal: {
                            show: false,
                            position: "center"
                        },
                        //鼠标经过时的文字
                        emphasis: {
                            show: true,
                            formatter: "{d}%", //文字格式模板
                            textStyle: {
                                fontSize: "20",
                                color: "#333"
                            }
                        }
                    },
                    //视觉引导线
                    labelLine: {
                        show: false
                    },
                    itemStyle: {
                        emphasis: {
                            borderColor: '#dbdee4'
                        },
                        normal:{
                            label:{
                                show: true,
                                formatter: '{b} : {c} ({d}%)'
                            },
                            labelLine :{show:true}
                        }
                    },
                    center: ['36%', '50%'], //饼图的中心坐标
                    radius: ['60%', '90%'], //饼图的半径  内/外
                    data: []
                }],
                color: ['#62AAFF', '#40B9FE', '#61CBF2', '#8CD7FF', '#6CDF39', '#8BE851', '#C0E74E', '#FED45B', '#F4BB47', '#EBEBED']
            });

            //设置图标能根据页面大小变化
            var myChartDom = this.myChart;
            window.onresize = function(){
                var setSize = setTimeout(() => {
                    window.clearTimeout(setSize);
                    myChartDom.resize();
                },200)
            };
        },
        props:["pieData"],
        data: function () {
            return {
                myChart:""
            }
        },
        methods: {
            //带图例和中心内容的饼图
            setNormal: function(val){
                this.myChart.setOption({
                    legend: {
                        formatter: function (name) {
                            if (name == "其它") {
                                return name;
                            }
                            for (var i = 0; i < val.length; i++) {
                                var entity = val[i]
                                if (entity.name == name) {
                                    if(entity.code){
                                        return name + "[" + entity.code + "]";
                                    }else{
                                        return name;
                                    }
                                }
                            }
                        }
                    },
                    series: [{
                        name: "饼图", //用于tooltip的显示，legend图例的筛选
                        data: val
                    }],
                });
            },
            //没有图例和中心内容的饼图
            setNoEles: function(val){
                this.myChart.setOption({
                    legend: {
                        show: false
                    },
                    series: [{
                        name: "饼图", //用于tooltip的显示，legend图例的筛选
                        data: val,
                        label: {
                            emphasis:{
                                show: false
                            }
                        },
                        center: ['50%', '50%'], //饼图的中心坐标
                    }],
                    tooltip: {
                        //触发类型
                        trigger: "item", //数据项图形触发
                        formatter: function (params) {
                            var showData = "";
                            if(params.data.value){
                                var type = params.data.type ? '下拨' : '归集';
                                var showData = params.name + "<br/>" + type +
                                        "金额：" + params.value + "(" + params.data.code + ")";
                            }
                            return showData;
                        }
                    },
                });
            }
        },
        watch:{
            //为饼图设置数据
            pieData: function (val,oldValue) {
                // console.log(val);
                if(val.$noElse){
                    this.setNoEles(val.pieData);
                }else {
                    this.setNormal(val);
                }
                this.myChart.dispatchAction({type: 'highlight',seriesIndex: 0,dataIndex: 0});
            }
        }
    }
</script>
