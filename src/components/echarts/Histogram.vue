<style scoped>
    #histogram{
        width: 100%;
        height: 44%;
        margin: 0 auto;
        text-align: left;
    }
</style>

<template>
    <div id="histogram"></div>
</template>

<script>
    export default {
        name: "Histogram",
        mounted: function () {
            //创建图表
            this.myChart = this.$echarts.init(document.getElementById("histogram"));
            this.myChart.setOption({
                tooltip : {
                    trigger: 'axis',
                    formatter: function (params) {
                        var data = params[0].data;
                        if(data.direction == 1){
                            var showText = "收入：";
                        }else{
                            var showText = "支出："
                        }
                        return showText + data.value
                    },
                    axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                        type : 'shadow',       // 默认为直线，可选为：'line' | 'shadow'
                        shadowStyle: {
                            color: "rgba(238,250,255,0.5)"
                        }
                    }
                },
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                xAxis : [
                    {
                        type : 'category',
                        data : [],
                    }
                ],
                yAxis : [
                    {
                        type : 'value',
                        axisLine: { //坐标轴轴线
                            show: false
                        },
                        axisTick: { //坐标轴刻度
                            show: false
                        }
                    }
                ],
                series : [
                    {
                        name:'交易信息',
                        type:'bar',
                        barWidth: '20px',
                        data:[]
                    }
                ],
                textStyle: {
                    color: "#aaa"
                }
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
        props: ["barData"],
        data: function () {
            return {
                myChart:""
            }
        },
        watch: {
            barData: function(val, oldValue){
                var xList = [];
                //设置柱状图的x轴值及样式
                for(var i = 0; i < val.length; i++){
                    xList.push({
                        value: "" + (i + 1) + "",
                        textStyle: {
                            color: "#fff"
                        }
                    });
                };
                //设置柱状图数据
                this.myChart.setOption({
                    xAxis : [
                        {
                            type : 'category',
                            data : xList, //坐标轴数据
                            axisTick: { //坐标轴刻度
                                show: false
                            },
                            axisLine: { //坐标轴轴线
                                lineStyle: {
                                    color: "#aaa",
                                }
                            }
                        }
                    ],
                    series : [
                        {
                            type:'bar',
                            name:'柱状图',
                            barWidth: '20px',
                            data:val
                        }
                    ]
                });
            }
        }
    }
</script>
