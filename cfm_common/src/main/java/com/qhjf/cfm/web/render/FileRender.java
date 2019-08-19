package com.qhjf.cfm.web.render;

import com.jfinal.render.Render;
import com.jfinal.render.RenderException;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

public class FileRender extends Render {

    private String fileName;
    private Workbook workbook;

    public FileRender(String fileName, Workbook workbook) {
        this.fileName = fileName;
        this.workbook = workbook;
    }

    @Override
    public void render() {
        response.addHeader("pragma", "NO-cache");
        response.addHeader("Cache-Control", "no-cache");
        response.addDateHeader("Expries", 0);
//        response.setContentType("application/octet-stream");
        response.setContentType("multipart/form-data");
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
        OutputStream out = null;
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        try {
            workbook.write(os);
            byte[] bytes = os.toByteArray();
            out = response.getOutputStream();
            out.write(bytes);
            out.flush();
            //workbook.write(out);
        } catch (Exception e) {
            throw new RenderException(e);
        } finally {
            try {
                if (null != out)
                    out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (null != workbook)
                    workbook.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
