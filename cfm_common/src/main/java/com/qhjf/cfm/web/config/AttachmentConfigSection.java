package com.qhjf.cfm.web.config;

import java.util.Properties;

public class AttachmentConfigSection extends  AbstractConfigSection {

    private IConfigSectionType.AttachmenConfigSectionType mode ; //Mongo, FileSave

    private AbstractConfigSection childConfig;

    protected  AttachmentConfigSection(){
        Properties pros = this.reader.getSection(getSectionName());
        if(pros != null && pros.size() > 0){
            String mode_str =getAndValidateNoNullItem(pros,"mode");
            if(mode_str != null && !"".equals(mode_str)){
                if(mode_str.equals(IConfigSectionType.AttachmenConfigSectionType.Mongo.name())){
                    mode = IConfigSectionType.AttachmenConfigSectionType.Mongo;
                    childConfig = new MongoConfigSection();
                }else if(mode_str.equals(IConfigSectionType.AttachmenConfigSectionType.FileSave.name())){
                    mode = IConfigSectionType.AttachmenConfigSectionType.FileSave;
                    childConfig = new FileSaveConfigSection();
                }else{
                    addErrMsg(SECTION_ITEM_FORMAT_ERR_COMPLETE,getSectionName(),"mode","Mongo|FileSave");
                }
                mergerErr();
            }

        }else{
            addErrMsg(SECTION_ERR_TEMP,getSectionName());
        }
    }

    private void mergerErr(){
        if(childConfig != null){
            if(!childConfig.isValidate()){
                addErrMsg(childConfig.getErrMsg());
            }
        }
    }


    @Override
    public IConfigSectionType getSectionType() {
        return IConfigSectionType.DefaultConfigSectionType.Attachment;
    }

    public IConfigSectionType.AttachmenConfigSectionType getMode() {
        return mode;
    }

    public AbstractConfigSection getChildConfig() {
        return childConfig;
    }
}
