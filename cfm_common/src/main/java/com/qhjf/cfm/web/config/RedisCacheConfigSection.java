package com.qhjf.cfm.web.config;

import com.qhjf.cfm.utils.RegexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class RedisCacheConfigSection extends AbstractConfigSection {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheConfigSection.class);

    /**
     * redis ip
     */
    private String ip ;

    /**
     * redis port
     */
    private int port ;

    /**
     * redis cache name
     */
    private String cacheName ;


    @Override
    public String getSectionName() {
        return "RedisCacheConfigSection";
    }

    @Override
    public IConfigSectionType getSectionType() {
        return IConfigSectionType.DefaultConfigSectionType.Redis;
    }


    protected RedisCacheConfigSection(){
        Properties pros = this.reader.getSection(getSectionName());
        if(pros != null && pros.size() > 0){
            ip = getAndValidateNoNullItem(pros,"ip");
            if(!RegexUtils.isIPOrDoamin(ip)){
                addErrMsg(SECTION_ITEM_FORMAT_ERR,getSectionName(),"ip");
            }
            port = getAndValidateNoNullItemForInt(pros,"port");
            if(!RegexUtils.isValidatePort(port)){
                addErrMsg(SECTION_ITEM_FORMAT_ERR,getSectionName(),"port");
            }
            cacheName = getAndValidateNoNullItem(pros,"cacheName");
        }else{
            addErrMsg(SECTION_ERR_TEMP,getSectionName());
        }
    }


    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getCacheName() {
        return cacheName;
    }

    public static void main(String[] args)   {
        RedisCacheConfigSection section = new RedisCacheConfigSection();
        if(section.isValidate()){
            log.info(section.getIp() + "  "+
                    section.getCacheName() + "  "+
                    section.getPort());
        }else{
            log.error(section.getErrMsg());
        }
    }
}
