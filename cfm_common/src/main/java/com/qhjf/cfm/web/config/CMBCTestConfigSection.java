package com.qhjf.cfm.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class CMBCTestConfigSection extends  AbstractConfigSection {
	private static final Logger log = LoggerFactory.getLogger(CMBCTestConfigSection.class);

    private  static final CMBCTestConfigSection INSTANCE = new CMBCTestConfigSection();

    private int preDay ;

    protected CMBCTestConfigSection(){
        Properties pros = this.reader.getSection(getSectionName());
        if(pros != null && pros.size() > 0){
            String str_preDay = pros.getProperty("preDay");
            log.debug("招行读取配置文件中的配置preDay={}", str_preDay);
            str_preDay = str_preDay == null ? "0" : str_preDay.trim();
            try{
                preDay = Integer.parseInt(str_preDay);
            }catch (Exception e){
                preDay = 0;
            }
        }else{
            preDay = 0;
        }
    }

    public static CMBCTestConfigSection getInstance() {
        return INSTANCE;
    }
    
    
    


    @Override
    protected IConfigSectionType getSectionType() {
        return IConfigSectionType.BIConfigSectionType.CMBC;
    }

    public int getPreDay() {
        return preDay;
    }
}
