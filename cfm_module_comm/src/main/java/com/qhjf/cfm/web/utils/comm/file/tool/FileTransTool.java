package com.qhjf.cfm.web.utils.comm.file.tool;

import com.qhjf.cfm.exceptions.AttachmentException;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.utils.comm.file.info.FileInfo;

import java.io.InputStream;

/**
 * 文件保存、读取工具接口
 * @author CHT
 *
 */
public interface FileTransTool {
	/**
	 * 新增文件
	 * @param filename
	 * @param source
	 * @return
	 */
	public String addNewFileByStream(String filename, InputStream source) throws BusinessException;
	/**
	 * 新增文件
	 * @param filename
	 * @param source
	 * @return
	 */
	public String addNewFileByArray(String filename,byte[] source) throws BusinessException;
	/**
	 * 新增文件
	 * @param source
	 * @return
	 */
	public String addNewFileByArray(byte[] source) throws BusinessException;
	/**
	 * 删除文件
	 * @param objectIdValue
	 */
	public void deleteFileByObjectId(String objectIdValue) throws AttachmentException;
	/**
	 * 获取文件
	 * @param objectId
	 * @return
	 * @throws BusinessException
	 */
	public FileInfo getFileByObjectid(String objectId) throws BusinessException;
	/**
	 * 获取文件
	 * @param fileName
	 * @return
	 * @throws BusinessException
	 */
	public FileInfo getFileByFileName(String fileName) throws BusinessException;
}
