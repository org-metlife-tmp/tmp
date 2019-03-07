package com.qhjf.cfm.web.render;


import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import com.qhjf.cfm.web.utils.comm.file.info.FileInfo;
import org.apache.commons.codec.binary.Base64;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class AttachmentRender extends Render {

    private FileInfo mfi;

    public AttachmentRender(FileInfo mfi) {
        this.mfi = mfi;
    }


    @Override
    public void render() {
        response.addHeader("pragma", "NO-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.addDateHeader("Expries", 0);
        response.setContentType("multipart/form-data");
        String fileName = mfi.getFilename();
        try {

            String agent = request.getHeader("User-Agent");
            //判断是否是IE11
            Boolean flag = agent.indexOf("like Gecko") > 0;
            if (agent.toUpperCase().indexOf("MSIE") > 0 || flag) {
                fileName = URLEncoder.encode(fileName, "UTF-8");
            } else if (agent.toLowerCase().indexOf("firefox") > 0) {
                fileName = "=?UTF-8?B?" + (new String(Base64.encodeBase64(fileName.getBytes("UTF-8")))) + "?=";
            } else {
                fileName = new String(fileName.getBytes("UTF-8"), "ISO8859-1");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);

        OutputStream out = null;
        try {
            out = response.getOutputStream();
            out.write(mfi.getContent());
            out.flush();
        } catch (Exception e) {
            throw new RenderException(e);
        } finally {
            try {
                if (null != out)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
