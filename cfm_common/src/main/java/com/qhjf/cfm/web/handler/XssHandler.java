package com.qhjf.cfm.web.handler;

import com.jfinal.handler.Handler;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

public class XssHandler extends Handler {

    // 排除的url，使用的target.startsWith匹配的
    private String exclude;

    public XssHandler(String exclude) {
        this.exclude = exclude;
    }

    @Override
    public void handle(String target, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, boolean[] isHandled) {
        //对于非静态文件，和非指定排除的url实现过滤
        if (target.indexOf(".") == -1 && !target.startsWith(exclude)) {
            httpServletRequest = new XssHttpServletRequestWrapper(httpServletRequest);
        }
        next.handle(target, httpServletRequest, httpServletResponse, isHandled);
    }


    private class XssHttpServletRequestWrapper extends HttpServletRequestWrapper {

        public XssHttpServletRequestWrapper(HttpServletRequest request) {
            super(request);
        }

        /**
         * 重写并过滤getParameter方法
         */
        @Override
        public String getParameter(String name) {
            return getBasicHtmlandimage(super.getParameter(name));
        }

        /**
         * 重写并过滤getParameterValues方法
         */
        @Override
        public String[] getParameterValues(String name) {
            String[] values = super.getParameterValues(name);
            if (null == values) {
                return null;
            }
            for (int i = 0; i < values.length; i++) {
                values[i] = getBasicHtmlandimage(values[i]);
            }
            return values;
        }

        /**
         * 重写并过滤getParameterMap方法
         */
        @Override
        public Map getParameterMap() {
            Map paraMap = super.getParameterMap();
            Map<Object, String[]> map = new HashMap<Object, String[]>();
            map.putAll(paraMap);
            // 对于paraMap为空的直接return
            if (null == paraMap || paraMap.isEmpty()) {
                return paraMap;
            }
            for (Object key : paraMap.keySet()) {
                String[] values = (String[]) paraMap.get(key);
                if (null == values) {
                    continue;
                }
                String[] newValues = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                    newValues[i] = getBasicHtmlandimage(values[i]);
                }
                map.put(key, newValues);
            }
            return map;
        }
    }

    public static String getBasicHtmlandimage(String html) {
        if (html == null)
            return null;
        return Jsoup.clean(html, Whitelist.basicWithImages());
    }
}
