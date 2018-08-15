<style>
    #line-chart {
        width: 100%;
        height: 44%;
        margin: 0 auto;
        text-align: left;
    }
</style>

<template>
    <div id="line-chart"></div>
</template>

<script>
    export default {
        name: "LineChart",
        mounted: function () {
            //创建饼图
            this.myChart = this.$echarts.init(document.getElementById("line-chart"));
            this.myChart.showLoading();
            var myChartDom = this.myChart;
            window.onresize = function(){
                var setSize = setTimeout(() => {
                    window.clearTimeout(setSize);
                    myChartDom.resize();
                },200)
            }
            this.$myChart = this.myChart;
        },
        props:["lineData"],
        data: function () {
            return {
                myChart:"",
            }
        },
        methods: {},
        watch:{
            //为折线设置数据
            lineData: function (val,oldValue) {
                this.myChart.clear();
                this.myChart.setOption(
                    {
                        title: {
                            x: 'center'
                        },
                        tooltip: {
                            trigger: 'axis',
                            axisPointer: {
                                lineStyle: {
                                    type: 'dashed',
                                    color: '#00ffde'
                                }
                            }
                        },
                        xAxis: {
                            type: 'category',
                            boundaryGap: false,
                            axisLine: {
                                show: false,
                                lineStyle: {
                                    color: "#e8eaed"
                                }
                            },
                            splitLine: {
                                lineStyle: {
                                    color: '#e8eaed'
                                }
                            },
                            axisLabel: {
                                textStyle: {
                                    color: '#999'
                                }
                            },
                            data: val.x
                        },
                        yAxis: {
                            type: 'value',
                            axisLine: {
                                show: false,
                                lineStyle: {
                                    color: "#e8eaed"
                                }
                            },
                            splitLine: {
                                lineStyle: {
                                    color: '#e8eaed'
                                }
                            },
                            axisLabel: {
                                formatter: '{value}',
                                textStyle: {
                                    color: '#999'
                                }
                            },
                            scale: true
                        },
                        series: 
                            {
                                name: "余额",
                                type: 'line',
                                data: val.y,
                                smooth: true,
                                areaStyle: {
                                    normal: {
                                        opacity: 0.3
                                    }
                                },
                                lineStyle: {
                                    normal: {
                                        color: "#B6A2DE"
                                    }
                                },
                                itemStyle: {
                                    normal: {
                                        color: "#B6A2DE"
                                    }
                                }
                            }
                        ,
                        custom: "wave"
                    }
                );
                this.$myChart.hideLoading();
            }
        }
    }
</script>
