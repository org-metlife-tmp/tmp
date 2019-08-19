package com.qhjf.cfm.web.plugins.excelup;

import com.oreilly.servlet.multipart.FilePart;
import com.oreilly.servlet.multipart.MultipartParser;
import com.oreilly.servlet.multipart.ParamPart;
import com.oreilly.servlet.multipart.Part;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.utils.RandomUtil;
import com.qhjf.cfm.web.constant.BasicTypeConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传时，文件流与其他参数的解析工具
 */
public class UploadFileScaffold {
	private static final Logger logger = LoggerFactory.getLogger(UploadFileScaffold.class);
	// 上传文件大小
	protected int size;
	// 上传文件编码
	protected String encoding;

	// 上传文件的文件名
	private String filename;
	// 上传的原始Excel文件
	private byte[] content;
	// 上传文件的byte数组的MD5 + 当前时间毫秒数 + 10000~99999之间的随机数
	private String contentMd5;
	/**
	 *  上传文件的其他参数：
	 *  1.is_encrypt（1：加密；其他不加密）
	 *  2.password（当isEncrypt=1时，password为Excel的加密密码）
	 */
	private Map<String, String> params;

	public UploadFileScaffold(int size) {
		this(size, BasicTypeConstant.DEFAULT_ENCODE);
	}
	/**
	 * @param size	上传上传的默认大小
	 * @param encoding	文件上传的编码
	 */
	public UploadFileScaffold(int size, String encoding) {
		this.size = size;
		this.encoding = encoding;
	}
	public String getFilename() {
		return filename;
	}
	public byte[] getContent() {
		return content;
	}
	public String getContentMd5() {
		return contentMd5;
	}
	public Map<String, String> getParams() {
		return params;
	}
	/**
	 * 解析文件上传的文件流与其他参数：this.content为文件流，this.filename为文件名，this.params为其他参数
	 * @param request
	 */
	public void action(HttpServletRequest request) {
		int counter = 0;
		Part part = null;
		InputStream inputStream = null;
		MultipartParser multipartParser = null;
		params = new HashMap<>();
		byte[] buffer = new byte[BasicTypeConstant.DEFAULT_BUFFER_SIZE];
		final ByteArrayOutputStream targetStream = new ByteArrayOutputStream();
		try {
			multipartParser = new MultipartParser(request, size, true, false, this.encoding);
			while ((part = multipartParser.readNextPart()) != null) {
				if (part.isFile()) {
					String fieldName = part.getName();
					FilePart filePart = (FilePart) part;
					logger.info("########### fieldName:{}", fieldName);
					logger.info("*********** fileName:{}", filePart.getFileName());
					inputStream = filePart.getInputStream();
					this.filename = filePart.getFileName();
					while ((counter = inputStream.read(buffer)) != -1) {
						if (counter < BasicTypeConstant.DEFAULT_BUFFER_SIZE)
							targetStream.write(buffer, 0, counter);
						else
							targetStream.write(buffer);
					}
					this.content = targetStream.toByteArray();
					this.contentMd5 = MD5Kit.byteArrayToMD5(content).concat(RandomUtil.currentTimeStamp())
							.concat(RandomUtil.randomNumber());
				} else if (part.isParam()) {
					ParamPart param = (ParamPart) part;
					String name = param.getName();
					params.put(name, param.getStringValue());
				}
			}
			
			//入参数放在header头里
			if (params.get("pk") == null) {
				params.put("pk", request.getHeader("pk"));
				logger.debug("从head中取pk={}",request.getHeader("pk"));
			}
		} catch (IOException e) {
			logger.error("excel上传时解析excel文件流与其他参数失败", e);
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

			if (targetStream != null) {
				try {
					targetStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}