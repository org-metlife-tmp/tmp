/*
* 全局公共方法
* */
export default {
    /*将数字转换成千分符并保留小数点后两位值*/
    transitSeparator: function (number) {
        var stringValue = (number * 1).toLocaleString();
        var value = stringValue.split(".");
        if (value.length == 1) {
            return value[0] + ".00";
        } else {
            if (value[1].length == 1) {
                return stringValue + "0";
            } else {
                return stringValue;
            }
        }
    },
    /*将带千分符的字符串转换成数字*/
    transitNumber: function(string){
        return typeof string == "string" ? string.split(",").join("") * 1 : string;
    },
    /*
    * 将数字转换成汉字格式
    *
    * @unitList: 计数单位
    * @textList: 数字对应的汉字
    * @numArray: 数字的小数和整数分开成数组
    * @integerArray: 整数部分
    * @numText: 转换结果
    * @numTextArr: 汉字格式的数组
    * */
    transitText: function (number) {
        /*
         * 1、将数字转换成汉字
         * 2、根据其数位添加其计数单位
         * 3、判断0在不同位置的读法
         *   3.0 个位：不转汉字 加计数单位
         *   3.1 十位 转汉字 不加计数单位
         *   3.2 百位 转汉字 不加计数单位
         *   3.3 千位 转汉字 不加计数单位
         *   3.4 万位 不转汉字 加计数单位
         *   3.5 十万位 转汉字 不加计数单位
         *   3.6 百万位 转汉字 不加计数单位
         *   3.7 千万位 转汉字 不加计数单位
         *   3.8 亿位 不转汉字 加计数单位
         *   3.9 十亿位 转汉字 不加计数单位
         * 4、去掉多余的汉字
         *   4.1 去掉重复的零
         *   4.2 去掉后面是计数单位的零
         *   4.3 去掉结尾的零
         *   4.4 判断重复的计数单位：只有零在万位时会出现此情况
         *
         *
         */
        //定义数据
        var unitList = ['', '拾', '佰', '仟', '万', '拾', '佰', '仟', '亿', '拾'];
        var textList = {0: "零", 1: "壹", 2: "贰", 3: "叁", 4: "肆", 5: "伍", 6: "陆", 7: "柒", 8: "捌", 9: "玖",};
        var numArray = (number + "").split(".");
        var integerArray = numArray[0].split("");
        var numText = "";

        //遍历进行汉字转换和添加计数单位
        for (var i = 0; i < integerArray.length; i++) {
            var indexLength = integerArray.length - 1 - i; //当前数字的数位

            if (integerArray[i] == 0) { //判断为0的情况
                if (indexLength == 0 || indexLength == 4 || indexLength == 8) { //不转汉字 加计数单位
                    numText += unitList[indexLength];
                } else { //转汉字 不加计数单位
                    numText += textList[integerArray[i]];
                }
            } else {
                numText += textList[integerArray[i]] + unitList[indexLength];
            }
        }
        /*去掉多余的汉字*/
        var numTextArr = numText.split("");
        for (var i = 0; i < numTextArr.length; i++) {
            //去掉重复的零和后面是计数单位的零
            if (numTextArr[i] == "零" && (numTextArr[i + 1] == "零" || unitList.indexOf(numTextArr[i + 1]) != -1)) {
                numTextArr.splice(i, 1);
                i--;
            }
            //判断计数单位是否重复
            if (unitList.indexOf(numTextArr[i]) == 4 && unitList.indexOf(numTextArr[i - 1]) >= 8) {
                numTextArr.splice(i, 1);
                i--;
            }
        }
        numText = numTextArr.join("");

        //去掉末尾的零
        if (numText[numText.length - 1] == "零") {
            numText = numText.slice(0, numText.length - 1);
        }

        //判断是否需要有小数位
        if (numArray.length == 1) {
            return numText + "元整";
        } else {
            var decimal = numArray[1].split(""); //小数部分
            if(decimal[0] == 0 && decimal[1] == 0){
                return numText + "元整";
            }else{
                numText += "元";
                for (var i = 0; i < decimal.length; i++) {
                    if(i == 1 && decimal[i] == 0){
                        break;
                    }
                    numText += textList[decimal[i]];
                    numText += i == 0 ? "角" : "分";
                }
                return numText;
            }
        }
    }
}
