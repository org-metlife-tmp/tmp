package com.qhjf.cfm.web.config;

import java.util.Properties;

public class DiskUpLoadSection extends  AbstractConfigSection {

    private static final DiskUpLoadSection instance = new DiskUpLoadSection();

    private String path;

    protected  DiskUpLoadSection(){
        Properties pros = this.reader.getSection(getSectionName());
        if(pros != null && pros.size() >0 ){
            path = getAndValidateNoNullItem(pros,"path");
        }else{
            addErrMsg(SECTION_ERR_TEMP,getSectionName());
        }
    }

    public static DiskUpLoadSection getInstance() {
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
