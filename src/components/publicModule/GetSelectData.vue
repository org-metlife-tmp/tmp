<template>
    <div style="display:none"></div>
</template>

<script>
    /********
     获取公共下拉款数据
     ********/
    export default {
        name: "GetSelectData",
        created: function () {
            var isAdmin = JSON.parse(window.sessionStorage.getItem("user")).is_admin;
            if (isAdmin) {
                //机构
                this.$axios({
                    url: this.queryUrl + "adminProcess",
                    method: "post",
                    data: {
                        optype: "org_list"
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        return;
                    } else {
                        var data = result.data.data;
                        var orgList = [];
                        data.forEach(function (item) {
                            var itemNeed = {};
                            itemNeed.org_id = item.org_id;
                            itemNeed.name = item.name;
                            orgList.push(itemNeed);
                        });
                        window.sessionStorage.setItem("orgList", JSON.stringify(orgList));

                        window.sessionStorage.setItem("orgTreeList", JSON.stringify(this.setTreeData(data)));
                    }
                }).catch(function (error) {
                    console.log(error);
                })

                //部门
                this.$axios({
                    url: this.queryUrl + "adminProcess",
                    method: "post",
                    data: {
                        optype: "dept_list",
                        params: {
                            page_size: 1000,
                            page_num: 1
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        return;
                    } else {
                        var data = result.data.data;
                        window.sessionStorage.setItem("deptList", JSON.stringify(data));
                    }
                }).catch(function (error) {
                    console.log(error);
                })


                //币种
                if (!window.sessionStorage.getItem("currencyList")) {
                    this.$axios({
                        url: this.queryUrl + "adminProcess",
                        method: "post",
                        data: {
                            optype: "currency_list",
                            params: {
                                page_size: 200,
                                page_num: 1
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {
                            return;
                        } else {
                            var data = result.data.data;
                            window.sessionStorage.setItem("currencyList", JSON.stringify(data));
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }
            }
            if(!isAdmin && !window.sessionStorage.getItem("orgTreeList")){
                //普通用户用机构树
                this.$axios({
                    url: this.queryUrl + "commProcess",
                    method: "post",
                    data: {
                        optype: "org_curlist"
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
                        window.sessionStorage.setItem("orgTreeList", JSON.stringify(this.setTreeData(data)));
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            }
            //渠道名称
            if (!window.sessionStorage.getItem("channelList")) {
                this.$axios({
                    url: this.queryUrl + "extserv/catg/bankchannel",
                    method: "post",
                    data: {
                        optype: "catg_bankchannel",
                        params: {
                            page_size: 200,
                            page_num: 1
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        return;
                    } else {
                        var data = result.data.data;
                        window.sessionStorage.setItem("channelList", JSON.stringify(data));
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            }
            //银行大类
            if (!window.sessionStorage.getItem("bankTypeList")) {
                this.$axios({
                    url: this.queryUrl + "commProcess",
                    method: "post",
                    data: {
                        optype: "bank_typelist"
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        return;
                    } else {
                        var data = result.data.data;
                        window.sessionStorage.setItem("bankTypeList", JSON.stringify(data.alias));
                        window.sessionStorage.setItem("bankAllTypeList", JSON.stringify(data.standard));
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            }
            //系统常量
            if (!window.sessionStorage.getItem("catgList")) {
                this.$axios({
                    url: this.queryUrl + "commProcess",
                    method: "post",
                    data: {
                        optype: "category_listN"
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        return;
                    } else {
                        var data = result.data.data;
                        window.sessionStorage.setItem("catgList", JSON.stringify(data));
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            }
            //省/自治区 列表
            if (!window.sessionStorage.getItem("provinceList")) {
                this.$axios({
                    url: this.queryUrl + "commProcess",
                    method: "post",
                    data: {
                        optype: "area_toplevel"
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        return;
                    } else {
                        var data = result.data.data;
                        window.sessionStorage.setItem("provinceList", JSON.stringify(data));
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            }
            //币种下拉数据
            if (!window.sessionStorage.getItem("selectCurrencyList")) {
                this.$axios({
                    url: this.queryUrl + "commProcess",
                    method: "post",
                    data: {
                        optype: "currency_list",
                        params: {
                            page_size: 200,
                            page_num: 1
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        return;
                    } else {
                        var data = result.data.data;
                        window.sessionStorage.setItem("selectCurrencyList", JSON.stringify(data));
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            }
            //所属机构下拉数据
            if (!window.sessionStorage.getItem("selectOrgList")) {
                this.$axios({
                    url: this.queryUrl + "commProcess",
                    method: "post",
                    data: {
                        optype: "org_list",
                        params: {
                            page_size: 2000,
                            page_num: 1
                        }
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        return;
                    } else {
                        var data = result.data.data;
                        window.sessionStorage.setItem("selectOrgList", JSON.stringify(data));
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            }
            //电子回单类型（普通用户）
            if(!isAdmin && !window.sessionStorage.getItem("eleType")){
                this.$axios({
                    url: this.queryUrl + "normalProcess",
                    method: "post",
                    data: {
                        optype: "ele_type",
                        params:{}
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        return;
                    } else {
                        var data = result.data.data;
                        window.sessionStorage.setItem("eleType", JSON.stringify(data));
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            }
        },
        data: function () {
            return {
                queryUrl: this.$store.state.queryUrl,
            }
        },
        methods: {
            //设置树数据的转换
            setTreeData: function (data) {
                /*
                *将后台返回的数据转换为树结构数据
                *
                *  1、创建树结构的数据 并保存根节点
                *  2、递归将数据添加进根节点
                *     2.1 遍历获取当前层级的下一层数据 并根据parentId添加到其父节点下面
                *     2.2 保存当前层级数据 作为下一次遍历时的父节点
                *  3、终止条件：当前data中无数据
                *
                * */
                //设置根节点
                var treeData = {};
                var oneItem = data[0];
                for (var k in oneItem) {
                    treeData[k] = oneItem[k];
                }
                treeData.children = [];

                //遍历设置子节点
                function setTree(data, tier, currentDatas) {
                    var allDatas = []; //未使用全部数据
                    //第二级数据的设置
                    if (!currentDatas) {
                        var newCurrentDatas = []; //当前层级数据
                        for (var i = 0; i < data.length; i++) {
                            if (i > 0) {
                                var item = data[i];
                                if (item.level_num == 2) {
                                    item.children = [];
                                    treeData.children.push(item);
                                    newCurrentDatas.push(item);
                                } else {
                                    allDatas.push(item);
                                }
                            }
                        }
                        if (allDatas.length > 0) {
                            setTree(allDatas, ++tier, newCurrentDatas);
                        }
                    }
                    //第三级及以后层级数据设置
                    if (currentDatas) {
                        var newCurrentDatas = [];
                        for (var i = 0; i < data.length; i++) {
                            var item = data[i];
                            if (item.level_num == tier) {
                                var thisParentId = item.parent_id;
                                currentDatas.forEach(function (value) {
                                    if (value.org_id == thisParentId) {
                                        item.children = [];
                                        value.children.push(item);
                                        newCurrentDatas.push(item);
                                    }
                                })
                            } else {
                                allDatas.push(item);
                            }
                        }
                        if (allDatas.length > 0) {
                            setTree(allDatas, ++tier, newCurrentDatas);
                        }
                    }
                };
                setTree(data, 2);
                return treeData;
            },
        }
    }
</script>
