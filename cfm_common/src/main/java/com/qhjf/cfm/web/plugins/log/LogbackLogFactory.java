package com.qhjf.cfm.web.plugins.log;

import com.jfinal.log.ILogFactory;
import com.jfinal.log.Log;

/**
 * logbackLogFactory
 */
public class LogbackLogFactory implements ILogFactory {


    @Override
    public Log getLog(Class<?> aClass) {
        return LogbackLog.getLog(aClass);
    }

    @Override
    public Log getLog(String s) {
        return LogbackLog.getLog(s);
    }
}
