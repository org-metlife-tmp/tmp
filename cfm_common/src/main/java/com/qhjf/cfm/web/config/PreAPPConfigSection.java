package com.qhjf.cfm.web.config;

import com.qhjf.cfm.utils.RegexUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PreAPPConfigSection extends AbstractConfigSection {

    private static final Logger log = LoggerFactory.getLogger(PreAPPConfigSection.class);

    private static final String SPLIT_CHAR = "\\|";

    /**
     * listen port
     */
    private int listenPort ;

    private int loadBalanceType;

    private Map<String, Integer> preAppIPMap = new HashMap<>();


    protected  PreAPPConfigSection(){
        Properties pros = this.reader.getSection(getSectionName());
        if(pros != null && pros.size() > 0){
            listenPort = getAndValidateNoNullItemForInt(pros,"listenPort");
            if(!RegexUtils.isValidatePort(listenPort)){
                addErrMsg(SECTION_ITEM_FORMAT_ERR,getSectionName(),"listenPort");
            }
            loadBalanceType = getAndValidateNoNullItemForInt(pros,"loadBalanceType");
            if(loadBalanceType !=1){
                addErrMsg(SECTION_ITEM_FORMAT_ERR_COMPLETE,getSectionName(),"listenPort","1");
            }
            String instance_str = getAndValidateNoNullItem(pros,"instance");
            boolean flag = initInstance(instance_str);
            if(!flag){
                addErrMsg(SECTION_ITEM_FORMAT_ERR,getSectionName(),"instance");
            }
        }else{
            addErrMsg(SECTION_ERR_TEMP,getSectionName());
        }
    }

    /**
     * 校验instance_str
     * @param instance_str   eg:10.1.1.2:334|10.1.1.3:443
     * @return
     */
    private boolean initInstance(String instance_str){
        if(instance_str != null && !"".equals(instance_str)){
            String[] items = instance_str.split(SPLIT_CHAR);
            if(items != null && items.length > 0){
                for (String item : items){
                    String[] ip_port = item.split(":");
                    if(ip_port.length == 2){
                        if(RegexUtils.isIPOrDoamin(ip_port[0]) && RegexUtils.isValidatePort(ip_port[1])){
                            preAppIPMap.put(ip_port[0],Integer.getInteger(ip_port[1]));
                        }else{
                            return false;
                        }
                    }else{
                        return false;
                    }
                }
                return true;
            }else{
                return false;
            }
        }
        return false;
    }



    @Override
    public String getSectionName() {
        return "PreAPPConfigSection";
    }

    @Override
    public IConfigSectionType getSectionType() {
        return IConfigSectionType.DefaultConfigSectionType.PreApp;
    }

    public int getListenPort() {
        return listenPort;
    }

    public int getLoadBalanceType() {
        return loadBalanceType;
    }

    public Map<String, Integer> getPreAppIPMap() {
        return preAppIPMap;
    }

    public static void main(String[] args) {
        PreAPPConfigSection section = new PreAPPConfigSection();
        if(section.isValidate()){
            log.info(section.getListenPort()+"");
            if(section.getPreAppIPMap() != null && section.getPreAppIPMap().size() > 0){
                for (String s : section.getPreAppIPMap().keySet()) {
                    log.info(s);
                }
            }
        }else{
            log.error(section.getErrMsg());
        }
    }
}
