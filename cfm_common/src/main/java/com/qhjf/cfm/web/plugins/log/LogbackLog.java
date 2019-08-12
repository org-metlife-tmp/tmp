package com.qhjf.cfm.web.plugins.log;

import com.jfinal.log.Log;
import com.qhjf.cfm.utils.StringKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogbackLog extends Log {

    private Logger log;

    LogbackLog(Class<?> clazz) {
        this.log = LoggerFactory.getLogger(clazz);
    }

    LogbackLog(String name) {
        this.log = LoggerFactory.getLogger(name);
    }


    public static LogbackLog getLog(Class<?> clazz) {
        return new LogbackLog(clazz);
    }

    public static LogbackLog getLog(String name) {
        return new LogbackLog(name);
    }


    @Override
    public void debug(String s) {
        this.log.debug(StringKit.removeControlCharacter(s));
    }

    @Override
    public void debug(String s, Throwable throwable) {
        this.log.debug(StringKit.removeControlCharacter(s),throwable);
    }

    @Override
    public void info(String s) {
        this.log.info(StringKit.removeControlCharacter(s));
    }

    @Override
    public void info(String s, Throwable throwable) {
        this.log.info(StringKit.removeControlCharacter(s),throwable);
    }

    @Override
    public void warn(String s) {
        this.log.warn(StringKit.removeControlCharacter(s));
    }

    @Override
    public void warn(String s, Throwable throwable) {
        this.log.warn(StringKit.removeControlCharacter(s),throwable);
    }

    @Override
    public void error(String s) {
        this.log.error(StringKit.removeControlCharacter(s));
    }

    @Override
    public void error(String s, Throwable throwable) {
        this.log.error(StringKit.removeControlCharacter(s),throwable);
    }

    @Override
    public void fatal(String s) {
        this.log.error(StringKit.removeControlCharacter(s));

    }

    @Override
    public void fatal(String s, Throwable throwable) {
        this.log.error(StringKit.removeControlCharacter(s),throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.log.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return this.log.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return this.log.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return this.log.isErrorEnabled();
    }

    @Override
    public boolean isFatalEnabled() {
        return this.log.isErrorEnabled();
    }
}
