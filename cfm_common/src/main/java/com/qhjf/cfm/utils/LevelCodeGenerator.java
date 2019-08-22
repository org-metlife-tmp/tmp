package com.qhjf.cfm.utils;

import com.jfinal.log.Log;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.plugins.log.LogbackLog;

import java.text.NumberFormat;
import java.util.HashMap;

public class LevelCodeGenerator {

    private static final Log log = LogbackLog.getLog(LevelCodeGenerator.class);

    private int preLevelLength;  //每一层级的最大长度

    public LevelCodeGenerator(String levelCode, int levelNum) throws BusinessException {
        if (levelCode.length() % levelNum == 0) {
            this.preLevelLength = levelCode.length() / levelNum;
        } else {
            throw new ReqDataException( "无法生成LevelCode");
        }
    }

    public LevelCodeGenerator(int preLevelLength) {
        this.preLevelLength = preLevelLength;
    }

    public long getPreLevelLength() {
        return preLevelLength;
    }


    public String genInitCode() {
        log.info("Enter into genInitCode");
        return getNumStr(1);
    }

    public String genChildCodeByParent(String parentCode, String maxParent) throws BusinessException {
        if (parentCode != null && parentCode.length() % this.preLevelLength == 0) {
            if (maxParent != null && !"".equals(maxParent)) {
                if (maxParent.length() - parentCode.length() == preLevelLength) {
                    String changePart = maxParent.substring(parentCode.length());
                    int nextNum = Integer.parseInt(changePart) + 1;
                    String nextCode = getNumStr(nextNum);
                    log.debug("nextCode is:" + nextCode);

                    return parentCode + nextCode;

                } else {
                    throw new ReqDataException("每级长度为" + this.preLevelLength + ",无法处理的级别编码:" + maxParent);
                }
            } else {
                log.info("maxParent is null ,generate init code");
                String initCode = genInitCode();
                return parentCode + initCode;
            }

        } else {
            throw new ReqDataException("每级长度为" + this.preLevelLength + ",无法处理的级别编码:" + parentCode);
        }
    }

    public HashMap<Integer, String> getAncestorCodeByChild(String curCode, int curLevel) throws BusinessException {
        HashMap<Integer, String> result = null;
        if (curCode != null && !"".equals(curCode)
                && curCode.length() % this.preLevelLength == 0
                && curCode.length() / this.preLevelLength == curLevel) {
            result = new HashMap<Integer, String>();
//			result.put(new Integer(curLevel), curCode);
            for (int preLevel = curLevel - 1; preLevel > 0; preLevel--) {
                result.put(new Integer(preLevel), curCode.substring(0, preLevel * preLevelLength));
            }
            return result;

        } else {
            throw new ReqDataException("每级长度为" + this.preLevelLength + ",无法处理的级别编码:" + curCode + ",级别层次为：" + curLevel);
        }

    }


    private String getNumStr(int num) {
        NumberFormat nf = NumberFormat.getInstance();
        // 设置是否使用分组  
        nf.setGroupingUsed(false);
        // 设置最大整数位数  
        nf.setMaximumIntegerDigits(this.preLevelLength);
        // 设置最小整数位数  
        nf.setMinimumIntegerDigits(this.preLevelLength);
        return nf.format(num);
    }


    public static void main(String[] args) {
        try {
			/*LevelCodeGenerator gen = new LevelCodeGenerator(5);
			//System.out.println(gen.genChildCodeByParent("00001", "000019103"));
			System.out.println(gen.genInitCode());
			System.out.println(gen.genChildCodeByParent("00001", "0000100103"));
			System.out.println(gen.genChildCodeByParent("00001", "0000109103"));
			
			
			System.out.println("################");*/
            LevelCodeGenerator ogen = new LevelCodeGenerator("00001000010000100001", 4);
            System.out.println(ogen.getAncestorCodeByChild("000010000100001", 3));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
