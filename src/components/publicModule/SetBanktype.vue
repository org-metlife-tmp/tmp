<style scoped lang="less" type="text/less">
    #setBanktype {

    }
</style>

<template>
    <div id="setBanktype">
        <el-input v-model="showBank" placeholder="请选择开户行" @focus="getBank" :disabled="isDisabled"></el-input>
        <!--开户行选择弹框-->
        <el-dialog :visible.sync="dialogVisible"
                   width="40%" title="选择开户行"
                   :append-to-body="true"
                   top="140px" :close-on-click-modal="false">
            <el-form :model="dialogData" size="small" :label-width="formLabelWidth">
                <el-row>
                    <el-col :span="24">
                        <el-form-item label="银行大类">
                            <el-select v-model="dialogData.bankTypeName" placeholder="请选择银行大类"
                                       clearable filterable
                                       style="width:100%"
                                       :filter-method="filterBankType"
                                       :loading="bankLongding"
                                       @change="bankIsSelect">
                                <el-option v-for="bankType in bankTypeList"
                                           :key="bankType.name"
                                           :label="bankType.display_name"
                                           :value="bankType.code">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="地区">
                            <el-select v-model="dialogData.area"
                                       filterable remote clearable
                                       style="width:100%"
                                       placeholder="请输入地区关键字"
                                       :remote-method="getAreaList"
                                       :loading="loading"
                                       @change="bankIsSelect">
                                <el-option
                                        v-for="area in areaList"
                                        :key="area.name + '-' + area.top_super"
                                        :value="area.top_super + ' - ' + area.name">
                                    <span>{{ area.name }}</span><span style="margin-left:10px;color:#bbb">{{ area.top_super }}</span>
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="开户行">
                            <el-select v-model="dialogData.bank_name" placeholder="请选择银行"
                                       clearable filterable style="width:100%"
                                       @visible-change="getBankList"
                                       @change="setCNAPS"
                                       :disabled="bankSelect">
                                <el-option v-for="bankType in bankList"
                                           :key="bankType.cnaps_code"
                                           :label="bankType.name"
                                           :value="bankType.name">
                                </el-option>
                            </el-select>
                        </el-form-item>
                    </el-col>
                    <el-col :span="24">
                        <el-form-item label="CNAPS">
                            <el-input v-model="dialogData.cnaps_code" readonly></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
            </el-form>

            <span slot="footer" class="dialog-footer" style="text-align:center">
                    <el-button type="warning" size="mini" plain @click="dialogVisible = false">取 消</el-button>
                    <el-button type="warning" size="mini" :disabled="!dialogData.bank_name"
                               @click="saveBankinfo">确 定</el-button>
                </span>
        </el-dialog>
    </div>
</template>

<script>
    /*
    * 选择开户行组件传参-fillinBankName
    * 1、接收空字符串进行清空处理
    * 2、接收开户行名称进行展示
    * 返回数据-bankInfo：包含银行大类、地区、开户行及开户行cnps号
    * */
    export default {
        name: "SetBanktype",
        created: function(){
            //银行大类
            var bankTypeList = JSON.parse(window.sessionStorage.getItem("bankTypeList"));
            if (bankTypeList) {
                this.bankAllList = bankTypeList;
            }
            var bankAllTypeList = JSON.parse(window.sessionStorage.getItem("bankAllTypeList"));
            if(bankAllTypeList){
                this.bankAllTypeList = bankAllTypeList;
                this.bankTypeList = bankAllTypeList.slice(0,200);
            }
        },
        props: ["fillinBankName","isDisabled"],
        data: function(){
            return {
                queryUrl: this.$store.state.queryUrl,
                showBank: "",
                dialogVisible: false, //选择银行弹框
                dialogData: {
                    bankTypeName: "",
                    area: "",
                    bank_name: "",
                    cnaps_code: ""
                },
                formLabelWidth: "100px",
                bankAllList: [], //银行大类
                bankAllTypeList: [],
                bankTypeList: [],
                outTime: "",
                bankLongding: false,
                areaList: [], //地区
                loading: false,
                bankSelect: true, //开户行
                bankList: [],
            }
        },
        methods: {
            //选择开户行
            getBank: function(){
                this.dialogVisible = true;
                this.bankSelect = true;
                let dialogData = this.dialogData;
                for(let k in dialogData){
                    dialogData[k] = "";
                }
            },
            //银行大类搜索筛选
            filterBankType: function (value) {
                this.bankLongding = true;
                clearTimeout(this.outTime);
                this.outTime = setTimeout(() => {
                    if (value && value.trim()) {
                        this.bankTypeList = this.bankAllList.filter(item => {
                            var chineseReg = /^[\u0391-\uFFE5]+$/; //判断是否为中文
                            var englishReg = /^[a-zA-Z]+$/; //判断是否为字母
                            var quanpinReg = /(a[io]?|ou?|e[inr]?|ang?|ng|[bmp](a[io]?|[aei]ng?|ei|ie?|ia[no]|o|u)|pou|me|m[io]u|[fw](a|[ae]ng?|ei|o|u)|fou|wai|[dt](a[io]?|an|e|[aeio]ng|ie?|ia[no]|ou|u[ino]?|uan)|dei|diu|[nl][gh]ei|[jqx](i(ao?|ang?|e|ng?|ong|u)?|u[en]?|uan)|([csz]h?|r)([ae]ng?|ao|e|i|ou|u[ino]?|uan)|[csz](ai?|ong)|[csz]h(ai?|uai|uang)|zei|[sz]hua|([cz]h|r)ong|y(ao?|[ai]ng?|e|i|ong|ou|u[en]?|uan))/; //判断是否为全拼

                            if (chineseReg.test(value)) {
                                return item.name.toLowerCase().indexOf(value.toLowerCase()) > -1;
                            } else if (englishReg.test(value)) {
                                if (quanpinReg.test(value)) {
                                    return item.pinyin.toLowerCase().indexOf(value.toLowerCase()) > -1;
                                } else {
                                    return item.jianpin.toLowerCase().indexOf(value.toLowerCase()) > -1;
                                }
                            }
                        });
                        this.bankTypeList = this.bankTypeList.filter((item,index,arr) => {
                            for(var i = index+1; i < arr.length; i++){
                                if(item.display_name == arr[i].display_name){
                                    return false;
                                }
                            }
                            return true;
                        });
                    } else {
                        this.bankTypeList = this.bankAllTypeList.slice(0,200);
                    }
                    this.bankLongding = false;
                }, 1000);
            },
            //地区数据
            getAreaList: function (query) {
                if (query && query.trim()) {
                    this.loading = true;
                    this.$axios({
                        url: this.queryUrl + "commProcess",
                        method: "post",
                        data: {
                            optype: "area_list",
                            params: {
                                query_key: query.trim()
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {

                        } else {
                            this.loading = false;
                            this.areaList = result.data.data;
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                } else {
                    this.areaList = [];
                }
            },
            //银行大类/地址变化后判断银行是否可选
            bankIsSelect: function () {
                this.bankList = [];
                this.dialogData.bank_name = "";
                this.dialogData.cnaps_code = "";
                if (this.dialogData.area && this.dialogData.bankTypeName) {
                    this.bankSelect = false;
                } else {
                    this.bankSelect = true;
                }
            },
            //获取银行列表
            getBankList: function (status) {
                if (status) {
                    var area_code = this.dialogData.area.split(" - ");
                    var bank_type = this.dialogData.bankTypeName;

                    this.$axios({
                        url: this.queryUrl + "commProcess",
                        method: "post",
                        data: {
                            optype: "bank_list",
                            params: {
                                province: area_code[0],
                                city: area_code[1],
                                bank_type: bank_type
                            }
                        }
                    }).then((result) => {
                        if (result.data.error_msg) {
                        } else {
                            this.bankList = result.data.data;
                        }
                    }).catch(function (error) {
                        console.log(error);
                    })
                }
            },
            //设置CNPAS
            setCNAPS: function(value){
                if(value){
                    var bankList = this.bankList;
                    for(var i = 0; i < bankList.length; i++){
                        if(bankList[i].name == value){
                            this.dialogData.cnaps_code = bankList[i].cnaps_code;
                            return;
                        }
                    }
                }else{
                    this.dialogData.cnaps_code = "";
                }
            },
            //保存选中的银行
            saveBankinfo: function(){
                let dialogData = this.dialogData;
                this.showBank = dialogData.bank_name;
                this.dialogVisible = false;
                this.$emit("bankInfo",dialogData);
            },
        },
        watch: {
            fillinBankName: function(val,oldVal){
                this.showBank = val;
            }
        }
    }
</script>
