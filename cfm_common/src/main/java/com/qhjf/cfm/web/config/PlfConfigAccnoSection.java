package com.qhjf.cfm.web.config;

import java.util.Properties;

public class PlfConfigAccnoSection extends  AbstractConfigSection {

    private static final PlfConfigAccnoSection instance = new PlfConfigAccnoSection();

    private String accno;

    protected  PlfConfigAccnoSection(){
        Properties pros = this.reader.getSection(getSectionName());
        if(pros != null && pros.size() >0 ){
        	accno = getAndValidateNoNullItem(pros,"accno");
        }else{
            addErrMsg(SECTION_ERR_TEMP,getSectionName());
        }
    }

    public static PlfConfigAccnoSection getInstance() {
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
