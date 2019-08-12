package com.qhjf.cfm.web.config;

import java.util.Properties;

public class GmfConfigAccnoSection extends  AbstractConfigSection {

    private static final GmfConfigAccnoSection instance = new GmfConfigAccnoSection();

    private String accno;

    protected  GmfConfigAccnoSection(){
        Properties pros = this.reader.getSection(getSectionName());
        if(pros != null && pros.size() >0 ){
        	accno = getAndValidateNoNullItem(pros,"accno");
        }else{
            addErrMsg(SECTION_ERR_TEMP,getSectionName());
        }
    }

    public static GmfConfigAccnoSection getInstance() {
        return instance;
    }


    public String getAccno() {
        return accno;
    }

    @Override
    protected IConfigSectionType getSectionType() {
        return IConfigSectionType.DDHConfigSectionType.VOUCHER;
    }
}
