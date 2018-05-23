<style scoped lang="less" type="text/less">
    #basicData {
        width: 100%;
        height: 100%;
        box-sizing: border-box;
        position: relative;

        /*公司-银行切换*/
        .company-bank {
            position: absolute;
            top: -20px;
            right: -48px;
            width: 28px;
            height: 140px;
            li {
                width: 88%;
                line-height: 26px;
                height: 56px;
                font-size: 14px;
                border: 1px solid #00B3ED;
                background-color: #fff;
                color: #00B3ED;
                cursor: pointer;
                margin-bottom: 6px;
                position: relative;
            }
            .current-select {
                background-color: #00B3ED;
                color: #fff;
            }
            .current-select:before {
                border: 5px solid #00B3ED;
                border-top-color: transparent;
                border-bottom-color: transparent;
                border-left: none;
            }
            li:hover {
                background-color: #00B3ED;
                color: #fff;
            }
            li:before {
                content: "";
                display: block;
                position: absolute;
                border: none;
                top: 26px;
                left: -5px;
            }
            li:hover:before {
                border: 5px solid #00B3ED;
                border-top-color: transparent;
                border-bottom-color: transparent;
                border-left: none;
            }
        }

        //顶部按钮
        .button-list-right {
            position: absolute;
            top: -60px;
            right: -18px;
        }
    }
</style>

<template>
    <div id="basicData">
        <div class="button-list-right">
            <el-button type="warning" size="mini">下载</el-button>
        </div>
        <el-table :data="tableList"
                  border
                  size="mini"
                  max-height="100%">
            <el-table-column prop="name" label="公司名称" v-if="btActive.company"></el-table-column>
            <el-table-column prop="city" label="CNAPS" v-else-if="btActive.bank"></el-table-column>
            <el-table-column prop="city" label="币种编号" v-else-if="btActive.currency"></el-table-column>
            <el-table-column prop="city" label="部门名称" v-else></el-table-column>

            <el-table-column prop="city" label="公司地址" v-if="btActive.company"></el-table-column>
            <el-table-column prop="name" label="银行名称" v-else-if="btActive.bank"></el-table-column>
            <el-table-column prop="name" label="币种名称" v-else-if="btActive.currency"></el-table-column>
            <el-table-column prop="name" label="状态" v-else></el-table-column>

            <el-table-column prop="province" label="地区（省）" v-if="btActive.company"></el-table-column>
            <el-table-column prop="province" label="币种符号" v-else-if="btActive.currency"></el-table-column>
            <el-table-column
                    label="操作"
                    width="100">
                <template slot-scope="scope" class="operationBtn">
                    <el-button type="success" icon="el-icon-plus" size="mini" @click="dialogVisible = true"></el-button>
                    <el-button type="primary" icon="el-icon-edit" size="mini"></el-button>
                    <el-button type="danger" icon="el-icon-delete" size="mini"></el-button>
                </template>
            </el-table-column>
        </el-table>
        <!-- 公司/银行 选择-->
        <div class="company-bank">
            <ul>
                <li :class="{'current-select':btActive.company}"
                    @click="isActive('company')">公司
                </li>
                <li :class="{'current-select':btActive.bank}"
                    @click="isActive('bank')">银行
                </li>
                <li :class="{'current-select':btActive.currency}"
                    @click="isActive('currency')">币种
                </li>
                <li :class="{'current-select':btActive.department}"
                    @click="isActive('department')">部门
                </li>
            </ul>
        </div>
        <!--弹出框-->
        <el-dialog title="提示"
                   :visible.sync="dialogVisible"
                   width="30%">
            <el-form :model="form" :label-width="formLabelWidth">
                <el-form-item label="公司名称">
                    <el-input v-model="form.name"></el-input>
                </el-form-item>
                <el-form-item label="公司地址">
                    <el-input v-model="form.address"></el-input>
                </el-form-item>
                <el-form-item label="公司所在地">
                    <el-select v-model="form.region" placeholder="请选择活动区域">
                        <el-option label="区域一" value="shanghai"></el-option>
                        <el-option label="区域二" value="beijing"></el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="dialogVisible = false">取 消</el-button>
                <el-button type="primary" @click="dialogVisible = false">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: "BasicData",
        props: ["tableData"],
        created: function () {
            this.$emit('transmitTitle', '基础数据维护');
            this.$emit('getTableData', this.routerMessage);
        },
        data: function () {
            return {
                routerMessage: {
                    optype: "org_list"
                },
                tableList: [],
                btActive: {
                    company: true,
                    bank: false,
                    currency: false,
                    department: false
                },
                dialogVisible: false,
                form: {
                    name: "",
                    address: "",
                    region: ""
                },
                formLabelWidth: '100px'
            }
        },
        methods: {
            //切换数据内容
            isActive: function (active) {
                var btActive = this.btActive;
                for (var k in btActive) {
                    btActive[k] = false;
                }
                //点击公司
                if (active == "company") {
                    btActive.company = true;
                    return;
                }
                //点击银行
                if (active == "bank") {
                    btActive.bank = true;
                    return;
                }
                //点击币种
                if (active == "currency") {
                    btActive.currency = true;
                    return;
                }
                //点击部门
                if (active == "department") {
                    btActive.department = true;
                    return;
                }
            }
        },
        watch: {
            //根据父组件返回的信息进行设置
            tableData: function (val, oldValue) {
                var data = val.data;
                this.tableList = data;
            }
        }
    }
</script>
