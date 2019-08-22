package com.qhjf.cfm.utils;

/**
 * 序列号生成配置项
 */
interface SerialnoGenConfig {

    /**
     * 序列号分割符
     * @return
     */
    String getSplitString();

    /**
     * 序列号初始值
     * @return
     */
    int getInitial();

    /**
     * 序列号前缀
     * @return
     */
    String getPrefix();

    /**
     * 序列号滚动间隔
     * @return
     */
    int getRollingInterval();

    /**
     * 填充序列的最大长度
     * @return
     */
    int getPadding();
}
