package com.qhjf.cfm.web.render;

import com.jfinal.render.Render;
import com.jfinal.render.RenderException;

import javax.servlet.ServletOutputStream;
import java.io.IOException;
import java.net.URLEncoder;

public class ByteArrayRender extends Render {

	private String fileName;
	private byte[] byteArray;

	public ByteArrayRender(String fileName, byte[] byteArray) {
		this.fileName = fileName;
		this.byteArray = byteArray;
	}

	@Override
	public void render() {
		response.addHeader("pragma", "NO-cache");
		response.addHeader("Cache-Control", "no-cache");
		response.addDateHeader("Expries", 0);
		response.setContentType("multipart/form-data");
		
		try {
			fileName = URLEncoder.encode(fileName, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		response.addHeader("Content-Disposition", "attachment;filename=" + fileName);
		ServletOutputStream out = null;
		try {
			out = response.getOutputStream();
			out.write(this.byteArray);
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
