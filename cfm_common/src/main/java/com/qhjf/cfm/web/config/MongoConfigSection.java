package com.qhjf.cfm.web.config;

import com.qhjf.cfm.utils.RegexUtils;

import java.util.Properties;

public class MongoConfigSection extends  AbstractConfigSection implements  IChildConfigSection {

    private String ip ;

    private int port;

    private String dbName;


    protected MongoConfigSection(){
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
            dbName = getAndValidateNoNullItem(pros,"dbName");
        }else{
            addErrMsg(SECTION_ERR_TEMP,getSectionName());
        }
    }

    @Override
    public String getSectionName() {
        return getParentCls().getSimpleName()+"."+super.getSectionName();
    }

    @Override
    public IConfigSectionType getSectionType() {
        return IConfigSectionType.AttachmenConfigSectionType.Mongo;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getDbName() {
        return dbName;
    }

    @Override
    public IConfigSectionType getParentConfigSectionType() {
        return IConfigSectionType.DefaultConfigSectionType.Attachment;
    }

    @Override
    public Class<? extends AbstractConfigSection> getParentCls() {
        return AttachmentConfigSection.class;
    }
}
