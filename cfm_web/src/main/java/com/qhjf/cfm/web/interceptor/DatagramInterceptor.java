package com.qhjf.cfm.web.interceptor;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.kit.StrKit;
import com.jfinal.log.Log;
import com.qhjf.cfm.security.CryptTool;
import com.qhjf.cfm.security.OneTimekey;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import org.apache.commons.io.IOUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

public class DatagramInterceptor implements Interceptor {

    private final static Log logger = LogbackLog.getLog(DatagramInterceptor.class);


    @Override
    public void intercept(Invocation invocation) {
        getHederInfo(invocation.getController().getRequest());
        String userAgent = invocation.getController().getHeader("User-Agent");
        if (userAgent.startsWith(WebConstant.UserAgent.ANDROID_BS.getDesc())
                || userAgent.startsWith(WebConstant.UserAgent.ANDROID_CS.getDesc())
                || userAgent.startsWith(WebConstant.UserAgent.IPHONE_BS.getDesc())
                || userAgent.startsWith(WebConstant.UserAgent.IPHONE_CS.getDesc())) {
            processIOSAndAndorid(invocation.getController().getRequest());
        } else {
            processPc(invocation.getController().getRequest());
        }
        setCookieHttpOnly(invocation.getController());
        invocation.invoke();


    }

    private void getHederInfo(HttpServletRequest req) {
        logger.debug("Enter into getHederInfo(HttpServletRequest req)................ ");
        Enumeration<?> names = req.getHeaderNames();
        logger.debug("-----------------------------------------------------------");
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            logger.debug(name + " : " + req.getHeader(name));
        }
        logger.debug("-----------------------------------------------------------");
        logger.debug("Leave getHederInfo(HttpServletRequest req)! ");
    }

    private void processIOSAndAndorid(HttpServletRequest req) {
        logger.debug("Enter into processIOSAndAndorid(HttpServletRequest req)................ ");
        String key = genKey(req);
        int size = req.getContentLength();
        logger.debug("content-type is: "+req.getHeader("content-type"));
        if (StrKit.notBlank(req.getHeader("content-type")) && req.getHeader("content-type").toLowerCase().startsWith("multipart/form-data")){
            logger.debug("文件数据提交");
        } else {
            try {
                InputStream is = req.getInputStream();
                byte[] source = IOUtils.toByteArray(is, size);
                logger.debug("原始数据:" + new String(source));
                byte[] dest = CryptTool.decrypttMode(source, key);
                //IOS所有请求为UTF8编码
                logger.debug("解密数据:" + new String(dest, "utf-8"));
                JSONObject obj = JSON.parseObject(new String(dest, "utf-8"));
                req.setAttribute("_obj", obj);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void processPc(HttpServletRequest req) {
        logger.debug("Enter into processPc(HttpServletRequest req)................ ");
        if(req.getAttribute("_obj") !=null){
            return;
        }
        logger.debug("content-type is: "+req.getHeader("content-type"));
        if (StrKit.notBlank(req.getHeader("content-type")) && req.getHeader("content-type").toLowerCase().startsWith("multipart/form-data")) {
            logger.debug("文件数据提交");
            return;
        }else{
            StringBuilder json = new StringBuilder();
            BufferedReader reader = null;
            try {
                reader = req.getReader();
                String line = null;
                while ((line = reader.readLine()) != null) {
                    json.append(line);
                }
                logger.debug("_obj is:"+json.toString());
                JSONObject obj = JSONObject.parseObject(json.toString());
                req.setAttribute("_obj", obj);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String genKey(HttpServletRequest req) {
        if (req.getAttribute("_initKey") != null) {
            OneTimekey oneTimekey = new OneTimekey();
            String encrypt = oneTimekey.encryptId(req.getRequestedSessionId());
            req.removeAttribute("_initKey");
            return encrypt.substring(0, 24);
        } else {
            req.setAttribute("_initKey", "");
            return CryptTool.DEFAULTKEY;
        }
    }


    /**
     * 设置cookie httpOnly
     * @param controller
     */
    private void  setCookieHttpOnly(Controller controller){
        logger.debug("Enter into setCookieHttpOnly(Controller controller)................ ");
        if(controller != null){
            Cookie[] cookies = controller.getCookieObjects();
            if(cookies != null && cookies.length > 0){
                logger.debug("cookies length is :"+cookies.length);
                for (Cookie cookie : cookies) {
                    cookie.setHttpOnly(true);
                    cookie.setSecure(true);
                }
            }
        }
    }
}
