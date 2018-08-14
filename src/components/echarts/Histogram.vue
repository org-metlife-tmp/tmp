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
                //提示框
                tooltip : {
                    trigger: 'axis',
                    axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                        type : 'shadow',       // 默认为直线，可选为：'line' | 'shadow'
                        shadowStyle: {
                            color: "rgba(238,250,255,0.5)"
                        }
                    }
                },
                //图表位置
                grid: {
                    left: '3%',
                    right: '4%',
                    bottom: '3%',
                    containLabel: true
                },
                //x轴
                xAxis : [
                    {
                        type : 'category',
                        data : [],
                    }
                ],
                //y轴
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
                //文字样式
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
        props: ["barData","showLegend"],
        data: function () {
            return {
                myChart:""
            }
        },
        watch: {
            barData: function(val, oldValue){
                /*设置数据*/
                var xList = []; //x轴数据
                var totalrecvList = []; //收入
                var totalpayList = []; //支出
                var totalnetrecvList = []; //净收入

                for(var i = 0; i < val.length; i++){
                    var item = val[i];
                    //设置柱状图的x轴值及样式
                    xList.push({
                        value: item.name ? item.name : ("" + (i + 1) + ""),
                        textStyle: {
                            color: "#fff"
                        }
                    });
                    //设置支出、收入、净收支数据
                    if(item.name){
                        totalrecvList.push(item.totalrecv);
                        totalpayList.push(item.totalpay);
                        totalnetrecvList.push(item.totalnetrecv);
                    }
                };

                /*将数据放进图表*/
                //设置相同项
                this.myChart.setOption({
                    //x轴线
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
                });
                //设置不同项
                if(!this.showLegend){ //不显示图例
                    //设置柱状图数据
                    this.myChart.setOption({
                        //提示框
                        tooltip : {
                            trigger: 'axis',
                            axisPointer : {            // 坐标轴指示器，坐标轴触发有效
                                type : 'shadow',       // 默认为直线，可选为：'line' | 'shadow'
                                shadowStyle: {
                                    color: "rgba(238,250,255,0.5)"
                                }
                            },
                            formatter: function (params) {
                                var data = params[0].data;
                                if(data.direction == 1){
                                    var showText = "收入：";
                                }else{
                                    var showText = "支出："
                                }
                                return showText + data.value
                            }
                        },
                        //数据
                        series : [
                            {
                                type:'bar',
                                name:'柱状图',
                                barWidth: '20px',
                                data:val
                            }
                        ]
                    });
                }else{ //显示图例
                    this.myChart.setOption({
                        //图例设置
                        legend: {
                            data:['收入','支出','净收支'],
                            left: "20px",
                            icon: 'circle',
                            itemWidth: 8, //图例的图形宽度
                            itemHeight: 8,
                            textStyle: {
                                color: '#999'
                            },
                            borderRadius: "50%", //圆角半径
                        },
                        //数据
                        series : [
                            {
                                type:'bar',
                                name:'收入',
                                barWidth: '20px',
                                data:totalrecvList
                            },
                            {
                                type:'bar',
                                name:'支出',
                                barWidth: '20px',
                                data:totalpayList
                            },
                            {
                                type:'bar',
                                name:'净收支',
                                barWidth: '20px',
                                data:totalnetrecvList,
                                label: { //图形上的文本标签
                                    show: true,
                                    position: [0,-16],
                                    color: "#25D8E5"
                                }
                            }
                        ],
                        color:['#B1E835', '#33B2F2', '#25D8E5']
                    });
                }
            }
        }
    }
</script>
