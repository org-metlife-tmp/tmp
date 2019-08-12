
package com.qhjf.cfm.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

public class GlobalConfigSection extends AbstractConfigSection {

    private static final Logger log = LoggerFactory.getLogger(GlobalConfigSection.class);

    private static final GlobalConfigSection instance = new GlobalConfigSection();

    private static final String SKIP_SECTION_PATTERN="([A-Z][a-zA-Z0-9]+)(\\.[a-zA-Z0-9][-a-zA-Z0-9]{0,62})+";

    private String dbURL ;

    private String dbUser ;

    private String dbPwd;

    private Map<IConfigSectionType,AbstractConfigSection> extraConfig = new HashMap();

    private GlobalConfigSection(){
        init();
        if(isValidate()){
            log.info("GlobalConfigSection loading config: "+ displayConfigSection());
        }else{
            throw new RuntimeException("GlobalConfigSection config error! error is :"+getErrMsg());
        }
    }

    private void init() {
        Properties pros = this.reader.getSection(getSectionName());
        if(pros != null){
            dbURL = getAndValidateNoNullItem(pros,"dbURL");
            dbUser = getAndValidateNoNullItem(pros,"dbUser");
            dbPwd = getAndValidateNoNullItem(pros,"dbPwd");
        }else{
            addErrMsg(SECTION_ERR_TEMP,getSectionName());
        }

        // 对其他的配置项进行解析和处理
        List<String> allSections = this.reader.getAllSectionNames();
        //把自己排除掉
        allSections.remove(getSectionName());
        for (String section : allSections) {
            if(!isSkip(section)){
                AbstractConfigSection configItem = initOtherConfigSection(section);
                if(configItem.isValidate()){
                    //校验通过则加入extraConfig
                    extraConfig.put(configItem.getSectionType(),configItem);
                }else{
                    //未校验通过加入错误信息
                    addErrMsg(configItem.getErrMsg());
                }
            }else{
                log.debug("Skip configsection "+section);
            }

        }

    }

    public static GlobalConfigSection getInstance() {
        return instance;
    }

    private AbstractConfigSection initOtherConfigSection(String name){
        try {
            Class cls = Class.forName(BASEPACKAGE+"."+name);
            return (AbstractConfigSection) cls.newInstance();
        } catch (ClassNotFoundException e) {
        	e.printStackTrace();
            addErrMsg(SECTION_ITEM_CLSNOTFOUN_ERR,name);
        } catch (IllegalAccessException e){
        	e.printStackTrace();
            addErrMsg(SECTION_ITEM_CLSINIT_ERR,name);
        } catch (InstantiationException e) {
        	e.printStackTrace();
            addErrMsg(SECTION_ITEM_CLSINIT_ERR,name);
        }
        return null;
    }


    @Override
    public String getSectionName() {
        return "DB_CONFIG";
    }

    @Override
    public IConfigSectionType getSectionType() {
        return IConfigSectionType.DefaultConfigSectionType.DB;
    }


    public String getDbURL() {
        return dbURL;
    }

    public String getDbUser() {
        return dbUser;
    }

    public String getDbPwd() {
        return dbPwd;
    }


    public <T> T getExtraConfig(IConfigSectionType type){
        if(extraConfig.get(type) != null){
            return (T) extraConfig.get(type);
        }else{
            throw new RuntimeException(type.getConfigSectionType()+" 未配置！");
        }
    }


    public boolean hasConfig(IConfigSectionType type){
        if(extraConfig.get(type) != null){
            return true;
        }
        return false;
    }


    private boolean isSkip(String configSectionName){
        return Pattern.matches(SKIP_SECTION_PATTERN,configSectionName);
    }


    private String displayConfigSection(){
        StringBuffer buffer  = new StringBuffer();
        if(extraConfig != null && extraConfig.size() > 0){
            int flag = 0;
            for (IConfigSectionType iConfigSectionType : extraConfig.keySet()) {
                if(flag > 0){
                    buffer.append(", ");
                }
                buffer.append(iConfigSectionType.getConfigSectionType());
                flag++;
            }
        }
        return buffer.toString();
    }

    /**
     * 获取数据库连接类型 ， 如:mysql , sqlserver
     * @return
     */
    public String dbType(){
        String[] dbUrlSplit = getDbURL().split(":");
        return dbUrlSplit[1];
    }


    public static void main(String[] args) {
        GlobalConfigSection global = GlobalConfigSection.getInstance();
        if(global.isValidate()){
            log.debug(global.displayConfigSection());
            log.debug(global.getDbURL());
            log.debug(global.getDbUser());
            log.debug(global.getDbPwd());

            RedisCacheConfigSection section1 = global.getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Redis);
            log.info(section1.getIp());
            log.info(section1.getPort()+"");
            log.info(section1.getCacheName());

            LdapConfigSection section2 = global.getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Ldap);
            log.info(section2.getHost());
            log.info(section2.getPort()+"");
            log.info(section2.getDomain());
            log.info(section2.getDomainFormat());
            AttachmentConfigSection attachmentConfigSection = global.getExtraConfig(IConfigSectionType.DefaultConfigSectionType.Attachment);
            System.out.println(attachmentConfigSection.getMode());
            MongoConfigSection section = (MongoConfigSection)attachmentConfigSection.getChildConfig();
            System.out.println(section.getDbName());
            System.out.println(section.getIp());
            System.out.println(section.getPort());
            System.out.println(section2.getHost());

        }else{
            log.error(global.getErrMsg());
        }
    }

}
