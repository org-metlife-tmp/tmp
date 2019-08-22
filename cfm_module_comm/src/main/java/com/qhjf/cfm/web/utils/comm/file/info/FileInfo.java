package com.qhjf.cfm.web.utils.comm.file.info;

/**
 * 上传文件的信息对象
 * 
 * @author CHT
 *
 */
public abstract class FileInfo {
	//
	protected String contextType;
	// 文件内容的MD5
	protected String signContent;
	// 文件名
	protected String filename;
	// 文件内容
	protected byte[] content;
	// 文件大小
	protected long size;

	public String getContextType() {
		return contextType;
	}

	public void setContextType(String contextType) {
		this.contextType = contextType;
	}

	public String getSignContent() {
		return signContent;
	}

	public void setSignContent(String signContent) {
		this.signContent = signContent;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

	/*@Override
	public String toString() {
		StringBuffer sb = new StringBuffer(100);
		sb.append("contextType=").append(contextType)
			.append(",signContent=").append(signContent)
			.append(",filename=").append(filename)
			.append(",size=").append(size);
		return sb.toString();
	}*/
}
