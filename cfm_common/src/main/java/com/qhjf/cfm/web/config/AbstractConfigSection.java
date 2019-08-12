package com.qhjf.cfm.web.config;

import com.qhjf.cfm.utils.CommKit;
import com.qhjf.cfm.utils.IniFileReader;

import java.io.IOException;
import java.util.Properties;

public abstract class AbstractConfigSection {


    /**
     * 配置文件名称
     */
    private final static String CONFIGNAME = "cfm_web.ini";

    /**
     * 加在实例的包路径
     */
    protected final static String BASEPACKAGE = AbstractConfigSection.class.getPackage().getName();

    protected final static IniFileReader reader ;

    protected final static String SECTION_ERR_TEMP = "[%s] 未配置或配置项为空\r\n";

    protected final static String SECTION_ITEM_INIT_ERR = "[%s.%s] 初始化错误\r\n";

    protected final static String SECTION_ITEM_SET_ERR = "[%s.%s] 未设置\r\n";

    protected final static String SECTION_ITEM_FORMAT_ERR = "[%s.%s] 格式错误\r\n";

    protected final static String SECTION_ITEM_FORMAT_ERR_COMPLETE = "[%s.%s] 格式错误,必须是 [%s]\r\n";

    protected final static String SECTION_ITEM_CLSNOTFOUN_ERR = "[%s] 未找到实现类\r\n";

    protected final static String SECTION_ITEM_CLSINIT_ERR = "[%s] 实现类初始化错误\r\n";

    private String errMsg ;

    static{
        if (AbstractConfigSection.class.getClassLoader().getResource(CONFIGNAME) != null){
            try {
                reader = new IniFileReader(AbstractConfigSection.class.getClassLoader().getResource(CONFIGNAME).getPath());
            } catch (IOException e) {
                throw new RuntimeException("配置文件" + CONFIGNAME + "读取失败！");
            }
        }else{
            throw new RuntimeException("配置文件" + CONFIGNAME + "不存在！");
        }
    }


    /**
     * 添加错误信息
     *
     * @param errMsg  错误信息
     */
    protected void addErrMsg(String errMsg) {
        StringBuffer buff = new StringBuffer(this.errMsg != null ? this.errMsg : "");
        buff.append(errMsg);
        this.errMsg = buff.toString();
    }

    /**
     * 添加错误信息
     * @param format  模板
     * @param args    参数
     */
    protected  void addErrMsg(String format, Object... args){
        addErrMsg(String.format(format,args));
    }


    /**
     * 获取配置区块的名称
     * @return
     */
    protected  String getSectionName(){
        return this.getClass().getSimpleName();
    }



    /**
     * 从Properties获取并校验属性
     * @param pros
     * @param itemName
     * @return
     */
    protected String getAndValidateNoNullItem(Properties pros , String itemName){
        if(pros != null){
            if(!CommKit.isNullOrEmpty(pros.get(itemName))){
                return pros.getProperty(itemName);
            }else{
                addErrMsg(SECTION_ITEM_SET_ERR,getSectionName(),itemName);
            }
        }
        return null;
    }


    protected Integer getAndValidateNoNullItemForInt(Properties pros, String itemName){
        Integer dest = null;
        String value = getAndValidateNoNullItem(pros,itemName);
        try{
            dest = Integer.parseInt(value);
        }catch (Exception e){
            addErrMsg(SECTION_ITEM_INIT_ERR,getSectionName(),itemName);
        }
        return dest;
    }


    protected boolean isValidate() {
        if (this.errMsg != null && !"".equals(errMsg)) {
            return false;
        }
        return true;
    }

    public String getErrMsg() {
        return errMsg;
    }


    /**
     * 获取配置类型
     * @return
     */
    protected  abstract  IConfigSectionType getSectionType();
}
