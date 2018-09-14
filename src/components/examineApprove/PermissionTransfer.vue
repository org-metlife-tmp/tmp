<style lang="less" type="text/less">
    #permissionTransfer{
        min-width: 980px;
        width: 80%;
        height: 100%;
        margin: 0 auto;
        .top-edit{
            position: relative;
            .tip-box{
                width: 24px;
                height: 24px;
                background-image: url(../../assets/icon_common.png);
                background-repeat: no-repeat;
                background-position: -190px 0;
                cursor: pointer;
                z-index: 5;
            }
            /*标题*/
            button{
                position: absolute;
                top: -40px;
                right: 0;
            }
            /*顶部下拉*/
            .head-select1{
                position: absolute;
                top: -38px;
                left: 0;
                >*{
                    float: left;
                }
            }
            .head-select2{
                position: absolute;
                top: -38px;
                right: 76px;
            }
        }
    }
</style>

<template>
    <div id="permissionTransfer">
        审批转移
    </div>
</template>

<script>
    export default {
        name: "PermissionTransfer",
        created: function(){
            
        },
        mounted:function(){
        },
        data: function () {
            return {
                
            }
        },
        methods: {
           
        }
    }
</script>

