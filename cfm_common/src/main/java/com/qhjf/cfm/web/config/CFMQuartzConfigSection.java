package com.qhjf.cfm.web.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class CFMQuartzConfigSection extends  AbstractConfigSection {

	private static Logger log = LoggerFactory.getLogger(CFMQuartzConfigSection.class);
    private  static final  CFMQuartzConfigSection INSTANCE = new CFMQuartzConfigSection();

    private boolean quartzOpen;
    private int maxTime ;

    protected CFMQuartzConfigSection(){
        Properties pros = this.reader.getSection(getSectionName());
        if(pros != null && pros.size() > 0){
            String maxTime_str = pros.getProperty("maxTime","10");
            try{
                maxTime = Integer.parseInt(maxTime_str);
            }catch (Exception e){
            	log.debug("定时任务失效时间配置有误,改为默认配置30分钟");
                maxTime = 10;
            }
            try{
                quartzOpen = Boolean.valueOf(pros.getProperty("quartz_open"));
            }catch (Exception e){
            	log.debug("定时任务开关配置有误,改为默认关闭");
            	quartzOpen = false;
            }
        }else{
        	quartzOpen = false;
            maxTime = 10;
        }

    }

    public static CFMQuartzConfigSection getInstance() {
        return INSTANCE;
    }

    @Override
    protected IConfigSectionType getSectionType() {
        return IConfigSectionType.DefaultConfigSectionType.Quartz;
    }


    public int getMaxTime() {
        return maxTime;
    }

	public boolean isQuartzOpen() {
		return quartzOpen;
	}


    
    
}
