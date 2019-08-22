package com.qhjf.cfm.web.config;

public interface IChildConfigSection {

    /**
     * 获取父配置的类型
     * @return
     */
    IConfigSectionType getParentConfigSectionType();

    Class<? extends AbstractConfigSection> getParentCls();
}
