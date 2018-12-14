package com.qhjf.cfm.utils;

import java.util.HashMap;
import java.util.Map;

public class Vigenere {

    private static char[] wordTable = new char[]{
            'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't',
            'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
            'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', '0', '-', '_','@','#','$','&','.'
    };

    private static Map<Character,Integer> keyIndex ;

    private static String key = "yzth-qhjf_one";

    static{
        keyIndex = new HashMap<>();
        for (int i = 0 ; i<wordTable.length; i++){
            keyIndex.put(wordTable[i], i);
        }
    }


    /**
     * 校验输入字符是否在可用字符中
     * @param input
     * @return
     */
    private static boolean validateInput(String input){
        if(input != null && !"".equals(input)){
            char[] chars = input.toCharArray();
            for (char aChar : chars) {
                if(!keyIndex.containsKey(aChar)){
                    return false;
                }
            }
            return true;
        }
        return false;
    }



    /**
     * 密文 =（明文+密钥）Mod length
     * @param plain
     * @return
     */
    public  static String encrypt (String plain){
        String result = null;
        if(validateInput(plain)){
            char[] chars = plain.toCharArray();
            char[] cipher = new char[chars.length];
            for (int i =0 ; i<chars.length ; i++){
                char cur_char = chars[i];
                char cur_key = key.charAt(i % key.length());
                int cipher_index = (keyIndex.get(cur_char)+keyIndex.get(cur_key)) % wordTable.length;
                cipher[i] = wordTable[cipher_index];
            }
            result = String.valueOf(cipher);
        } else {
            result = plain;
        }
        return result;
    }


    /**
     * 如果密文字符为值 小于密钥字符位置 ，则加上所有密钥的长度,
     * 即明文=[ lenth +（密文-密钥）]
     * 否则
     * 明文 = [ 密文-密钥]
     * @param cipher
     * @return
     */
    public static String decrypt(String cipher){
        String result = null;
        if(validateInput(cipher)){
            char[] chars = cipher.toCharArray();
            char[] plain = new char[chars.length];
            for (int i =0 ; i<chars.length ; i++){
                char cur_char = chars[i];
                char cur_key = key.charAt(i % key.length());
                int plain_index = keyIndex.get(cur_char) - keyIndex.get(cur_key);
                if(plain_index < 0){
                    plain_index += wordTable.length;
                }
                plain[i] = wordTable[plain_index];
            }
            result = String.valueOf(plain);
        }else {
            result = cipher;
        }
        return result;
    }


    /**
     *  密文=（明文+密钥）Mod legnth-1
     *  明文=[ length +（密文-密钥）]Mod legnth +1
     *
     * @param args
     */
    public static void main(String[] args) {
        String cipher = "ikf-Yf#abV&&8moj$7a9_9Zdd_ukm81k8-&X$jrSKE";
        System.out.println("cipher:"+cipher);

        String decrypt = Vigenere.decrypt(cipher);
        System.out.println("cipher:"+decrypt);
    }
}
