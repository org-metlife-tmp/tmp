package com.qhjf.cfm.web.utils;


public class MongoFileInfo implements java.io.Serializable {
	private static final long serialVersionUID = -8438304548055068988L;
	private String contextType;
	private String signContent;
	private String filename;
	private byte[] content;
	private long size;

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
}