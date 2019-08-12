package com.qhjf.cfm.web.config;

import java.util.Properties;

public class FileSaveConfigSection extends  AbstractConfigSection implements IChildConfigSection {

    private String fileSavePath;   //文件存储路径


    protected FileSaveConfigSection(){
        Properties pros = this.reader.getSection(getSectionName());
        if(pros != null && pros.size() >0 ){
            fileSavePath = getAndValidateNoNullItem(pros,"fileSavePath");
        }else{
            addErrMsg(SECTION_ERR_TEMP,getSectionName());
        }
    }

    @Override
    protected String getSectionName() {
        return getParentCls().getSimpleName()+"."+super.getSectionName();
    }

    @Override
    public IConfigSectionType getSectionType() {
        return IConfigSectionType.AttachmenConfigSectionType.FileSave;
    }

    public String getFileSavePath() {
        return fileSavePath;
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
