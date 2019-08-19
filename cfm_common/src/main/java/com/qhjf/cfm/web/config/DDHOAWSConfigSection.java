package com.qhjf.cfm.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class DDHOAWSConfigSection extends  AbstractConfigSection {

    private static final Logger log = LoggerFactory.getLogger(DDHOAWSConfigSection.class);

    private String pushWsdl;

    private String authorityWsdl;

    private String authorityUserName;

    private String authorityPassword;


    protected DDHOAWSConfigSection(){
        Properties pros = this.reader.getSection(getSectionName());
        if(pros != null && pros.size() > 0){
            pushWsdl=getAndValidateNoNullItem(pros,"pushWsdl");
            authorityWsdl=getAndValidateNoNullItem(pros,"authorityWsdl");
            authorityUserName=getAndValidateNoNullItem(pros,"authorityUserName");
            authorityPassword=getAndValidateNoNullItem(pros,"authorityPassword");
        }else{
            addErrMsg(SECTION_ERR_TEMP,getSectionName());
        }
    }


    @Override
    public String getSectionName() {
        return "DDHOAWSConfigSection";
    }

    @Override
    public IConfigSectionType getSectionType() {
        return IConfigSectionType.DDHConfigSectionType.DDHOAWS;
    }


    public String getPushWsdl() {
        return pushWsdl;
    }

    public String getAuthorityWsdl() {
        return authorityWsdl;
    }

    public String getAuthorityUserName() {
        return authorityUserName;
    }

    public String getAuthorityPassword() {
        return authorityPassword;
    }

    public static void main(String[] args) {
        DDHOAWSConfigSection section = new DDHOAWSConfigSection();
        if(section.isValidate()){
            log.info(section.getPushWsdl());
            log.info(section.getAuthorityWsdl());
            log.info(section.getAuthorityUserName());
            log.info(section.getAuthorityPassword());
        }else{
            log.error(section.getErrMsg());
        }
    }
}
