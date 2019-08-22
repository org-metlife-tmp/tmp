package com.qhjf.cfm.excel.bean;

/**
 * 解析结果java bean
 * 
 * 字段:
 *		suceess = true/false 解析顺利/失败
 *		content = byte[] 一个字节数组,三种情况 1 顺利返回对象的序列化结果,2 错误返回null, 3 解析失败返回被标记的excel内容
 *		message = 结果信息
 */
public class ResultBean implements java.io.Serializable {
	private static final long serialVersionUID = 9186277505490698000L;
	private boolean success;
	private String sheetName;
	private byte[] content;
	private String message;
	private String encryptObjectId;

	public boolean isSuccess() {
		return success;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSheetName() {
		return sheetName;
	}

	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}

	
	public String getEncryptObjectId() {
		return encryptObjectId;
	}

	public void setEncryptObjectId(String encryptObjectId) {
		this.encryptObjectId = encryptObjectId;
	}

	@Override
	public String toString() {
		return String.format("ResultBean [success=%s, message=%s, sheetName=%s]", success, message, sheetName);
	}
}