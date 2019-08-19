package com.qhjf.cfm.web.handler;

import com.jfinal.handler.Handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CFSHandler extends Handler {

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        response.addHeader("x-frame-options","SAMEORIGIN");
        next.handle(target, request, response, isHandled);
    }
}
