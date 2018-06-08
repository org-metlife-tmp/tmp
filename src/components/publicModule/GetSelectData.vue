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
            if(isAdmin){
                //机构
                if (!window.sessionStorage.getItem("orgList")) {
                    this.$axios({
                        url: "/cfm/adminProcess",
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

                            //保存机构树
                            //设置根节点
                            var treeData = {};
                            var oneItem = data[0];
                            for (var k in oneItem) {
                                treeData[k] = oneItem[k];
                            }
                            treeData.children = [];

                            //遍历设置子节点
                            function setTreeData(data, tier, currentDatas) {
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
                                        setTreeData(allDatas, ++tier, newCurrentDatas);
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
                                        setTreeData(allDatas, ++tier, newCurrentDatas);
                                    }
                                }
                            };
                            setTreeData(data, 2);
                            window.sessionStorage.setItem("orgTreeList", JSON.stringify(treeData));
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }
                //部门
                if (!window.sessionStorage.getItem("deptList")){
                    this.$axios({
                        url: "/cfm/adminProcess",
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
                            var deptList = [];
                            data.forEach(function (item) {
                                var itemNeed = {};
                                for (var k in item) {
                                    itemNeed[k] = item[k];
                                }
                                deptList.push(itemNeed);
                            });
                            window.sessionStorage.setItem("deptList", JSON.stringify(deptList));
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }
                //用户组
                if (!window.sessionStorage.getItem("usrgroupList")){
                    this.$axios({
                        url: "/cfm/adminProcess",
                        method: "post",
                        data: {
                            optype: "usrgroup_list",
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
                            var usrgroupList = [];
                            data.forEach(function (item) {
                                var itemNeed = {};
                                for (var k in item) {
                                    itemNeed[k] = item[k];
                                }
                                usrgroupList.push(itemNeed);
                            });
                            window.sessionStorage.setItem("usrgroupList", JSON.stringify(usrgroupList));
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }
            }
            //银行大类
            if (!window.sessionStorage.getItem("bankTypeList")) {
                this.$axios({
                    url: "/cfm/commProcess",
                    method: "post",
                    data: {
                        optype: "bank_typelist"
                    }
                }).then((result) => {
                    if (result.data.error_msg) {
                        return;
                    } else {
                        var data = result.data.data;
                        var bankTypeList = [];
                        data.forEach(function (item) {
                            var itemNeed = {};
                            for (var k in item) {
                                itemNeed[k] = item[k];
                            }
                            bankTypeList.push(itemNeed);
                        });
                        window.sessionStorage.setItem("bankTypeList", JSON.stringify(bankTypeList));
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            }
            //币种
            if (!window.sessionStorage.getItem("currencyList")) {
                this.$axios({
                    url: "/cfm/adminProcess",
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
                        var currencyList = [];
                        data.forEach(function (item) {
                            var itemNeed = {};
                            for (var k in item) {
                                itemNeed[k] = item[k];
                            }
                            currencyList.push(itemNeed);
                        });
                        window.sessionStorage.setItem("currencyList", JSON.stringify(currencyList));
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            }
            //渠道名称
            if (!window.sessionStorage.getItem("channelList")) {
                this.$axios({
                    url: "/cfm/extserv/catg/bankchannel",
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
                        var channelList = [];
                        data.forEach(function (item) {
                            var itemNeed = {};
                            for (var k in item) {
                                itemNeed[k] = item[k];
                            }
                            channelList.push(itemNeed);
                        });
                        window.sessionStorage.setItem("channelList", JSON.stringify(channelList));
                    }
                }).catch(function (error) {
                    console.log(error);
                })
            }
            //系统常量
            if (!window.sessionStorage.getItem("catgList")){
                this.$axios({
                    url: "/cfm/extserv/catg/list",
                    method: "post",
                    data: {
                        optype: "catg_list"
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
        },
        data: function () {
            return {}
        },
        methods: {}
    }
</script>
