package com.qhjf.cfm.web.config;

import com.qhjf.cfm.utils.RegexUtils;

import java.util.Properties;

public class LdapConfigSection extends AbstractConfigSection {


    /**
     * ldap host
     */
    private String host;

    /**
     * ldap port
     */
    private int port;

    /**
     * ldap domain
     */
    private String domain;

    /**
     * ldap domainFormat (prefix | suffix)
     */
    private String domainFormat;


    protected LdapConfigSection(){
        Properties pros = this.reader.getSection(getSectionName());
        if(pros != null && pros.size() > 0){
            host = getAndValidateNoNullItem(pros,"host");
            if(!RegexUtils.isIPOrDoamin(host)){
                addErrMsg(SECTION_ITEM_FORMAT_ERR,getSectionName(),"host");
            }
            port = getAndValidateNoNullItemForInt(pros,"port");
            if(!RegexUtils.isValidatePort(port)){
                addErrMsg(SECTION_ITEM_FORMAT_ERR,getSectionName(),"port");
            }
            domain = getAndValidateNoNullItem(pros,"domain");
            domainFormat = getAndValidateNoNullItem(pros,"domainFormat");
            if(domainFormat != null && !domainFormat.toLowerCase().equals("prefix")
                    && !domainFormat.toLowerCase().equals("suffix")){
                addErrMsg(SECTION_ITEM_FORMAT_ERR_COMPLETE,getSectionName(),
                        "domainFormat","prefix|suffix");
            }
        }else{
            addErrMsg(SECTION_ERR_TEMP,getSectionName());
        }

    }

    @Override
    public String getSectionName() {
        return "LdapConfigSection";
    }

    @Override
    public IConfigSectionType getSectionType() {
        return IConfigSectionType.DefaultConfigSectionType.Ldap;
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDomainFormat() {
        return domainFormat;
    }

    public void setDomainFormat(String domainFormat) {
        this.domainFormat = domainFormat;
    }
}
