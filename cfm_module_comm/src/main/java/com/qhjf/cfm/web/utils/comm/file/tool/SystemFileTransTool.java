package com.qhjf.cfm.web.utils.comm.file.tool;

import com.qhjf.cfm.exceptions.AttachmentException;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.utils.RandomUtil;
import com.qhjf.cfm.web.config.FileSaveConfigSection;
import com.qhjf.cfm.web.constant.BasicTypeConstant;
import com.qhjf.cfm.web.utils.comm.file.info.FileInfo;
import com.qhjf.cfm.web.utils.comm.file.info.SystemFileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * 文件系统的文件保存、读取工具类
 * @author CHT
 *
 */
public class SystemFileTransTool implements FileTransTool {
	private static final Logger logger = LoggerFactory.getLogger(SystemFileTransTool.class);
	
	private SystemFileTransTool(){}
	private static SystemFileTransTool instance = null;
	private static String fileStoragePath = null;//保存上传文件的目录
	private static final int TIMES = 3;//最多循环生成不重复的新文件名次数
	private static final int DEL_TIMES = 5;//最多循环删除文件次数
	private static final String ORIGIN_FILE_SUFFIX = ".bin";
	
	public static SystemFileTransTool getInstance(FileSaveConfigSection childConfig){
		if (instance == null) {
			fileStoragePath = childConfig.getFileSavePath();
			instance = new SystemFileTransTool();
		}
		return instance;
	}

	/**
	 * 新增一个文件
	 * 保存成功返回保存的文件名
	 */
	@Override
	public String addNewFileByStream(String filename, InputStream source) throws BusinessException {
		//生成新文件名，与mongoDb的objectId功能类似
		File newFile = generateFileName(filename);
		//输入流写入新生成的文件
		writeFile(newFile, source);
		return newFile.getName();
	}

	@Override
	public String addNewFileByArray(String filename, byte[] source) throws BusinessException {
		// 生成新文件名，与mongoDb的objectId功能类似
		File newFile = generateFileName(filename);
		// 输入流写入新生成的文件
		writeFile(newFile, source);
		return newFile.getName();
	}

	@Override
	public String addNewFileByArray(byte[] source) throws BusinessException {
		File newFile = generateFileName(source);
		writeFile(newFile, source);
		return newFile.getName();
	}

	/**
	 * @param objectIdValue 要删除的文件名
	 * @throws AttachmentException 
	 */
	@Override
	public void deleteFileByObjectId(String objectIdValue) throws AttachmentException {
		String fileName = fileStoragePath.concat(File.separator).concat(objectIdValue);
		File file = new File(fileName);
        if (file.exists() && file.isFile()) {
        	int times = 0;
        	boolean flag = false;
            while (!flag) {
            	if (++times == DEL_TIMES) {
					break;
				}
            	flag = file.delete();
            }
            
            if (!flag) {
            	throw new AttachmentException(String.format("删除的文件[%s]失败！", objectIdValue));
			}
        } else {
            throw new AttachmentException(String.format("将要删除的文件[%s]不存在！", objectIdValue));
        }
	}
	
	@Override
	public FileInfo getFileByObjectid(String objectId) throws BusinessException {
		System.out.println("================objectId+++++="+objectId);
		String fileName = fileStoragePath.concat(File.separator).concat(objectId);
		File file = new File(fileName);
		if (!file.exists()) {
			throw new AttachmentException(String.format("将要下载的文件[%s]不存在！", objectId));
		}
		InputStream is = null;
		byte[] buffer = null;
		try {
			is = new FileInputStream(file);
			buffer = new byte[is.available()];
			is.read(buffer);
		} catch (IOException e) {
			throw new AttachmentException(String.format("将要下载的文件[%s]不存在！", objectId));
		} finally{
			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return genFileInfo(objectId, buffer);
	}
	
	@Override
	public FileInfo getFileByFileName(String fileName) throws BusinessException {
		return getFileByObjectid(fileName);
	}
	
	/**
	 * 根据原始文件名生成新文件名
	 * @param filename	原始文件名
	 * @return
	 * @throws AttachmentException
	 */
	private File generateFileName(String filename) throws AttachmentException{
		String ext = null;
		String originFileName = null;
		if (filename.indexOf(".") == -1) {
			ext = ".xlsx";
			originFileName = filename;
		}else {
			ext = filename.substring(filename.lastIndexOf("."));
			originFileName = filename.substring(0, filename.lastIndexOf("."));
		}
		
		String newFileName = BasicTypeConstant.FILE_NAME_PREFIX.concat(originFileName)
				.concat(RandomUtil.randomTimeAndNumber())
				.concat(ext);
		
		File newFile = fileRepeatCheckAndGenerate(newFileName, originFileName, ext);
		if (newFile.exists()) {
			throw new AttachmentException(String.format("保存文件[%s]时，生成新的唯一文件名失败", filename));
		}
		
		return newFile;
	}
	/**
	 * 重载generateFileName（String filename）
	 * 根据byte数组生成不重复的新文件
	 * @param source
	 * @return
	 * @throws AttachmentException
	 */
	private File generateFileName(byte[] source) throws AttachmentException{
		String filename = MD5Kit.byteArrayToMD5(source);
		String newFilename = BasicTypeConstant.FILE_NAME_PREFIX.concat(filename)
				.concat(RandomUtil.randomTimeAndNumber())
				.concat(ORIGIN_FILE_SUFFIX);
		File newFile = fileRepeatCheckAndGenerate(newFilename, filename, ORIGIN_FILE_SUFFIX);
		if (newFile.exists()) {
			throw new AttachmentException(String.format("保存文件[%s]时，生成文件名失败", filename));
		}
		
		return newFile;
	}
	/**
	 * 判断新文件名是否存在，如果存在则重新生成，如果不存在则返回File对象
	 * @param newFileName	新文件名
	 * @param originFileName	原始文件名
	 * @param ext	文件后缀
	 * @return
	 */
	private File fileRepeatCheckAndGenerate(String newFileName, String originFileName, String ext){
		File newFile = new File(fileStoragePath.concat(File.separator).concat(newFileName));
		//生成一个不存在的文件名
		int times = 0;
		while (newFile.exists()) {
			newFileName = BasicTypeConstant.FILE_NAME_PREFIX.concat(originFileName)
					.concat(RandomUtil.randomTimeAndNumber())
					.concat(ext);
			newFile = new File(fileStoragePath.concat(File.separator).concat(newFileName));
			if (++times == TIMES) {
				break;
			}
		}
		return newFile;
	}

	/**
	 * 输入流转字节数组
	 * @param is
	 * @return
	 * @throws AttachmentException
	 */
	private byte[] inputStreamToByteArr(InputStream is) throws AttachmentException{
		byte[] buffer = null;
		try {
			buffer = new byte[is.available()];
		} catch (IOException e) {
			logger.error("读取输入流的可用字节数失败！", e);
			throw new AttachmentException("读取输入流的字节数失败！", e);
		}
		try {
			is.read(buffer);
		} catch (IOException e) {
			logger.error("读取输入流中的字节数据失败！");
			throw new AttachmentException("读取输入流数据失败！", e);
		}
		return buffer;
	}
	/**
	 * 把输入流写入文件中
	 * @param fileName
	 * @param is
	 */
	private void writeFile(File fileName, InputStream is){
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(fileName);
			int byteCount = 0;
			byte[] buffer = new byte[BasicTypeConstant.DEFAULT_BUFFER_SIZE ];
			while ((byteCount = is.read(buffer)) != -1) {
				if (byteCount < BasicTypeConstant.DEFAULT_BUFFER_SIZE )
					fileOutputStream.write(buffer, 0, byteCount);
				else
					fileOutputStream.write(buffer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			if (null != fileOutputStream) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					logger.error(String.format("关闭文件【%s】失败！", fileName.getName()), e);
				}
			}
		}
	}
	/**
	 * 把字节数组写入文件
	 * @param fileName	待写入文件
	 * @param source	待写入的字节数组
	 * @throws AttachmentException 
	 */
	private void writeFile(File fileName, byte[] source) throws AttachmentException{
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream(fileName);
			fileOutputStream.write(source);
		} catch (IOException e) {
			throw new AttachmentException(e.getMessage());
		} finally {
			if (null != fileOutputStream) {
				try {
					fileOutputStream.close();
				} catch (IOException e) {
					logger.error(String.format("关闭文件【%s】失败！", fileName.getName()), e);
				}
			}
		}
	}
	
	private FileInfo genFileInfo(String fileName, byte[] content){
		FileInfo info = new SystemFileInfo();
		info.setContent(content);
		info.setSignContent(MD5Kit.byteArrayToMD5(content));
		info.setFilename(fileName);
		info.setSize(content.length);
		return info;
	}
	
}
