package com.qhjf.cfm.web.config;

import java.util.Properties;

public class ICBCTestConfigSection extends  AbstractConfigSection {

    private  static final ICBCTestConfigSection INSTANCE = new ICBCTestConfigSection();

    private int preDay ;

    protected ICBCTestConfigSection(){
        Properties pros = this.reader.getSection(getSectionName());
        if(pros != null && pros.size() > 0){
            String str_preDay = pros.getProperty("preDay");
            try{
                preDay = Integer.parseInt(str_preDay);
            }catch (Exception e){
                preDay = 0;
            }
        }else{
            preDay = 0;
        }
    }

    public static ICBCTestConfigSection getInstance() {
        return INSTANCE;
    }
    
    
    


    @Override
    protected IConfigSectionType getSectionType() {
        return IConfigSectionType.BIConfigSectionType.ICBC;
    }

    public int getPreDay() {
        return preDay;
    }
}
