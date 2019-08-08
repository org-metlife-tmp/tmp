<style lang="less" type="text/less" scoped>
    #lookDiagramContainer {
        width: 100%;
        height: 320px;
        position: relative;
        overflow: auto;
    }
    .static-item{
        position: absolute;
        .static-name{
            width: 63px;
            height: 24px;
            text-align: center;
            display: block;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }
    }
    .static-portrait {
        width: 63px;
        height: 63px;
        display: inline-block;
        background-image: url(../../assets/icon_common.png);
        background-repeat: no-repeat;
    }  
    .root-img {
        background-position: -10px -223px;
    }
    .end-img {
        background-position: -10px -223px;
    }
    .users-img {
        background-position: -218px -223px;
    }
    .position-img {
        background-position: -80px -292px;
    }
    .displaynone{
        visibility: hidden!important;
    }
</style>
<style lang="less" type="text/less">
    .displaynone{
        visibility: hidden!important;
    }
</style>
<template >
    <div id="lookDiagramContainer">
        <template  v-for="item in node_data">
            <div class="static-item" :id="'item_' + item.item_id" :key="item.item_id">
                <span class="static-name" :title="item.text">{{item.text}}</span>
                <span class="static-portrait"
                    :class="{'position-img':item.type=='pos','root-img':item.type=='start','users-img':item.type=='user','end-img':item.type=='end'}"></span>
            </div>  
        </template>
    </div>
</template>

<script>
    export default {
        name: "WorkFlow",
        created:function(){
            this.assembleFlowData(this.flowList);
        },
        mounted:function(){
            this.drawLine();
        },
        props:["flowList","isEmptyFlow"],
        data:function(){
            return {
                node_data:[],
                line_data:[],
                lineStyle:{
                    Anchor: ["Right", "Left"],
                    PaintStyle: {
                        strokeWidth: 1,
                        outlineWidth: 10,
                        outlineStroke: "transparent",
                        "stroke-dasharray": [3, 1],
                        stroke: '#7bb9f2'
                    },
                    HoverPaintStyle: {
                        strokeWidth: 1,
                        stroke: '#7bb9f2'
                    },
                    Endpoint: ["Dot", {cssClass: "displaynone" }],
                    Connector: ["Straight"],
                    Container: "lookDiagramContainer",
                    ConnectionOverlays: [
                        ["PlainArrow", {
                            location: 1,
                            width: 10,
                            length: 10
                        }]
                    ]
                },
                // classStyle:{
                //     "position-img":true,
                //     "users-img":true,
                //     "root-img":true
                // }
            }
        },
        methods:{
            //组装工作流数据
            assembleFlowData:function(val){
                if(!val || !val.nodes ){
                    return ;
                }
                let nodes = val.nodes;//这里不包括开始节点和结束节点
                nodes.unshift({ 
                    text:"业务提交",
                    axis_x:0,
                    axis_y:50,
                    item_id:"-1",
                    type:'start'
                });
                nodes.push({
                    text:"提交结束",
                    axis_x:690,
                    axis_y:50,
                    item_id:"-2",
                    type:'end'
                });
                let nLen = nodes.length;
                //组装点数据
                for(let i=0; i<nLen; i++){
                    if (i == 0 || i == nLen-1){
                        continue;
                    }else{
                        //判断是机构还是用户
                        let data = JSON.parse(nodes[i].data);
                        nodes[i].type = data.push_org ? 'pos' : 'user';
                        //转换用户组或机构
                        let select = nodes[i].addUsers;
                        let sLen = select.length;
                        let text = "";
                        for(let j=0; j<sLen; j++){
                            text = text + select[j].name;
                            text = (j == sLen-1) ? text : text + "|";
                        }
                        nodes[i].text = text;
                    }
                }
                this.node_data = nodes;
                this.line_data = val.lines;
            },
            //组装点的dom，画线
            drawLine:function(){
                //要等到dom渲染完之后
                this.jsplumb = jsPlumb.getInstance(this.lineStyle);
                let nlen = this.node_data.length;
                let llen = this.line_data.length;
                for (let i = 0;i<nlen;i++){
                    // var newLength = 0 ;   
                    var curId = "item_"+this.node_data[i].item_id;
                    var firstChild = document.getElementById(curId);
                    firstChild.style.top = this.node_data[i].axis_y+ "px";
                    firstChild.style.left = this.node_data[i].axis_x + "px";
                }
                for( let j=0 ; j<llen ;j++){
                    let sourceId = "item_" + this.line_data[j].d_source_id;
                    var targetId = "item_" + this.line_data[j].d_target_id;
                    this.jsplumb.connect({
                        source: sourceId,
                        target: targetId
                    })
                    // that.jsplumb.draggable(targetId);
                }
            }
        },
        watch:{
            isEmptyFlow:function(val,oldVal){
                if(val){
                    //清空工作流相关数据
                    //直接清空这个container，不知道为何下次不重新渲染dom了,必须要清除数据，
                    this.line_data = [];
                    this.node_data = [];
                    this.jsplumb.empty("lookDiagramContainer");
                }
            },
            flowList:function(val,oldVal){
                if(val.base_id){
                    this.assembleFlowData(val);
                    setTimeout(() =>{
                        this.drawLine();
                    },0)
                }
                
            }
        }
    }
</script>
