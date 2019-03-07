package com.qhjf.cfm.utils;

import java.math.BigDecimal;
import java.text.DecimalFormat;

public class StringKit {

    private static final int MD5LENGTH = 32;

    /**
     * 汉语中数字大写
     */
    private static final String[] CN_UPPER_NUMBER = {"零", "壹", "贰", "叁", "肆",
            "伍", "陆", "柒", "捌", "玖"};

    private static final String[] CN_UPPER_MONETRAY_UNIT = {"分", "角", "元",
            "拾", "佰", "仟", "万", "拾", "佰", "仟", "亿", "拾", "佰", "仟", "兆", "拾",
            "佰", "仟"};

    /**
     * 特殊字符：整
     */
    private static final String CN_FULL = "整";

    /**
     * 特殊字符：负
     */
    private static final String CN_NEGATIVE = "负";

    /**
     * 金额的精度，默认值为2
     */
    private static final int MONEY_PRECISION = 2;


    /**
     * 特殊字符：零元整
     */
    private static final String CN_ZEOR_FULL = "零元" + CN_FULL;


    private enum FlushDirection {
        LEFT, RIGHT;
    }

    /**
     * 判断字符串中是否包含中文
     *
     * @param str
     * @return
     */
    public static boolean isContainChina(String str) {
        if (str == null || str.length() <= 0) {
            return false;
        }
        if (str.getBytes().length == str.length()) {
            return false;
        }
        return true;
    }

    private static String flushChar(char c, long length, String content, FlushDirection direction) throws Exception {
        int strLen = content.length();
        StringBuffer sb = null;
        while (strLen < length) {
            sb = new StringBuffer();
            if (direction.equals(FlushDirection.LEFT)) {
                sb.append(c).append(content);// 左补
            } else if (direction.equals(FlushDirection.RIGHT)) {
                sb.append(content).append(c);
            } else {
                throw new Exception("请指定补充字符串方向");
            }
            content = sb.toString();
            strLen = content.length();
        }
        return content;
    }


    public static String flushZeroLeft4MD5(String content) throws Exception {
        if (content != null && !"".equals(content)) {
            if (content.length() < MD5LENGTH) {
                return flushChar('0', MD5LENGTH, content, FlushDirection.LEFT);
            }
        }
        return content;
    }


    /**
     * 将金额转换为中文大写
     * @param amount
     * @return
     */
    public static String formatAmount2CNMontrayUnit(BigDecimal amount) {
        StringBuffer sb = new StringBuffer();

        int signum = amount.signum();
        if (signum == 0) {
            return CN_ZEOR_FULL;
        }

        //这里会进行金额的四舍五入
        long number = amount.movePointRight(MONEY_PRECISION)
                .setScale(0, 4).abs().longValue();

        /**
         * 得到小数点后两位值
         */
        long scale = number % 100;
        int numUnit = 0;
        int numIndex = 0;
        boolean getZero = false;
        // 判断最后两位数，一共有四中情况：00 = 0, 01 = 1, 10, 11
        if (!(scale > 0)) {
            numIndex = 2;
            number = number / 100;
            getZero = true;
        }

        if ((scale > 0) && (!(scale % 10 > 0))) {
            numIndex = 1;
            number = number / 10;
            getZero = true;
        }

        int zeroSize = 0;
        while (true) {
            if (number <= 0) {
                break;
            }
            // 每次获取到最后一个数
            numUnit = (int) (number % 10);
            if (numUnit > 0) {
                if ((numIndex == 9) && (zeroSize >= 3)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[6]);
                }
                if ((numIndex == 13) && (zeroSize >= 3)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[10]);
                }
                sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                getZero = false;
                zeroSize = 0;
            } else {
                ++zeroSize;
                if (!(getZero)) {
                    sb.insert(0, CN_UPPER_NUMBER[numUnit]);
                }
                if (numIndex == 2) {
                    if (number > 0) {
                        sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                    }
                } else if (((numIndex - 2) % 4 == 0) && (number % 1000 > 0)) {
                    sb.insert(0, CN_UPPER_MONETRAY_UNIT[numIndex]);
                }
                getZero = true;
            }
            // 让number每次都去掉最后一个数
            number = number / 10;
            ++numIndex;
        }
        // 如果signum == -1，则说明输入的数字为负数，就在最前面追加特殊字符：负
        if (signum == -1) {
            sb.insert(0, CN_NEGATIVE);
        }
        // 输入的数字小数点后两位为"00"的情况，则要在最后追加特殊字符：整
        if (!(scale > 0)) {
            sb.append(CN_FULL);
        }
        return sb.toString();
    }

    /**
     * 格式花金额，添加千分符
     * @param amount
     * @return
     */
    public static String formatAmount(BigDecimal amount){
        DecimalFormat df=new DecimalFormat(",###,##0.00");
        return df.format(amount);
    }

    /**
     * 格式化笔数
     * @param count
     * @return
     */
    public static String formatCount(Integer count){
        DecimalFormat df=new DecimalFormat(",###,##0");
        return df.format(count);
    }


    public static void printParams(Object[] params){
        for(int i =0 ;i<params.length; i++){
            System.out.println(i+"  :  "+params[i]);
        }
    }

    /**
     * 将首字母大写
     * @param str
     * @return
     */
    public static String captureName(String str){
        if(str != null && !"".equals(str)){
            char[] cs=str.toCharArray();
            cs[0]-=32;
            return String.valueOf(cs);
        }
        return str;
    }


    /**
     * 对数字进行补零
     * @param number
     *         正整数
     * @param maxlen
     *         生成字符串最大长度
     *        如果number的长度小于maxlen, number左边补零， 否则返回number的字符串
     * @return
     */
    public static String zeroPadding(int number, int maxlen){
        String result = "";
        try {
            result =  flushChar('0',maxlen,String.valueOf(number),FlushDirection.LEFT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 去除字符串里的特殊字符
     * @param origin
     * @return
     */
    public static String removeControlCharacter(String origin){
        if(origin != null && !"".equals(origin)){
            StringBuilder sb = new StringBuilder();
            for (int i=0; i<origin.codePointCount(0, origin.length()); i++){
                int codePoint = origin.codePointAt(i);
                if(!Character.isISOControl(codePoint))
                {
                    sb.appendCodePoint(codePoint);
                }
            }
            return sb.toString();
        }else{
            return origin;
        }

    }


    public static void main(String[] args){

        StringBuffer bb = new StringBuffer("From dd ( ?,");
        bb.replace(bb.length()-1,bb.length()," ) ");
        System.out.println(bb);
        int d = 10000;
        System.out.println(formatCount(d));
        System.out.println(zeroPadding(1000,3));
        String origin = "\r\n1234@#<!234\r\n";
        System.out.println("#####################");
        System.out.println(origin);
        System.out.println("#####################");
        System.out.println();
        System.out.println("#####################");
        System.out.println(removeControlCharacter(origin));
        System.out.println("#####################");
    }

}
