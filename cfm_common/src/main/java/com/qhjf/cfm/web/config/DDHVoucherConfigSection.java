package com.qhjf.cfm.web.config;

import java.util.Properties;

public class DDHVoucherConfigSection extends  AbstractConfigSection {

    private static final DDHVoucherConfigSection instance = new DDHVoucherConfigSection();

    private String path;

    protected  DDHVoucherConfigSection(){
        Properties pros = this.reader.getSection(getSectionName());
        if(pros != null && pros.size() >0 ){
            path = getAndValidateNoNullItem(pros,"path");
        }else{
            addErrMsg(SECTION_ERR_TEMP,getSectionName());
        }
    }

    public static DDHVoucherConfigSection getInstance() {
        return instance;
    }

    @Override
    protected IConfigSectionType getSectionType() {
        return IConfigSectionType.DDHConfigSectionType.VOUCHER;
    }

    public String getPath() {
        return path;
    }
}
