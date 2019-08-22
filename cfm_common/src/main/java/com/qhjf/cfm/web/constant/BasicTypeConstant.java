package com.qhjf.cfm.web.constant;

/**
 * 单个的基本类型常量
 * @author CHT
 *
 */
public final class BasicTypeConstant {
	
	private BasicTypeConstant(){}
	//excel上传时默认文件上传大小
	public static final int DEFAULT_UPLOAD_FILE_SIZE = 10485760;
	//excel上传时默认缓存大小
	public static final int DEFAULT_BUFFER_SIZE = 8192;
	//数据库批量操作的条数
	public static final int BATCH_SIZE = 1000;
	
	
	//excel解析结果在内存中存活的毫秒数
	public static final long SERVIVAL_TIME = 12 * 60 * 60 * 1000;
	//timer启动后，延迟多久执行任务
	public static final long TIMER_DELAY_TIME = 12 * 60 * 1000;
	
	//excel默认编码、Excel配置文件默认编码
	public static final String DEFAULT_ENCODE = "UTF-8";
	//分割行字符串
	public static final String SPLIT_LINE = "******************************************";
	//Excel后缀名
	public static final String EXCEL97_SUFFIX = ".xls";
	public static final String EXCEL2007_SUFFIX = ".xlsx";
	//通过文件系统上传、下载文件时，给生成的文件名加的固定前缀
	public static final String FILE_NAME_PREFIX = "[QHFS]";
	public static final String ERROR_EXCEL_SAVE_FAIL = "保存校验失败的Excel时异常，请联系管理员！";
	public static final String ORIGIN_EXCEL_SAVE_FAIL = "上传的原始Excel保存失败";
	public static final String ENCRYPT_EXCEL_SAVE_FAIL = "加密后的Excel保存失败";
	public static final String ICBC_EBILL_TYPE = "ICBCEBILL";
}
