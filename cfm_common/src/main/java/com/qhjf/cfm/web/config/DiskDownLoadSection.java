package com.qhjf.cfm.web.config;

import java.util.Properties;

public class DiskDownLoadSection extends  AbstractConfigSection {

    private static final DiskDownLoadSection instance = new DiskDownLoadSection();

    private String path;

    protected  DiskDownLoadSection(){
        Properties pros = this.reader.getSection(getSectionName());
        if(pros != null && pros.size() >0 ){
            path = getAndValidateNoNullItem(pros,"path");
        }else{
            addErrMsg(SECTION_ERR_TEMP,getSectionName());
        }
    }

    public static DiskDownLoadSection getInstance() {
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
