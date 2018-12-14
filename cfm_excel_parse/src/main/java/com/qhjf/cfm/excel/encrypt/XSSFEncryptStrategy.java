package com.qhjf.cfm.excel.encrypt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OLE2NotOfficeXmlFileException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.crypt.EncryptionMode;
import org.apache.poi.poifs.crypt.Encryptor;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * EXCEL2007加密策略
 * 
 * @author CHT
 *
 */
public class XSSFEncryptStrategy extends ExcelEncryptStrategy {
	private static final Logger logger = LoggerFactory.getLogger(XSSFEncryptStrategy.class);

	@Override
	public byte[] encryptExcel(InputStream is, String password) {
		byte[] result = null;
		if (logger.isDebugEnabled()) {
			logger.debug(String.format("Excel开始加密，密码：[%s]", password));
		}
		POIFSFileSystem pOIFSFileSystem = new POIFSFileSystem();
		EncryptionInfo encryptionInfo = new EncryptionInfo(EncryptionMode.standard);
		Encryptor encryptor = encryptionInfo.getEncryptor();

		// 设置Excel密码
		encryptor.confirmPassword(password);

		// 加密Excel
		OutputStream os = null;
		OPCPackage oPCPackage = null;
		try {
			os = encryptor.getDataStream(pOIFSFileSystem);
			oPCPackage = OPCPackage.open(is);
			oPCPackage.save(os);
			os = encryptor.getDataStream(pOIFSFileSystem);
		} catch (InvalidFormatException e) {
			logger.error("Excel格式化异常！", e);
		} catch (OLE2NotOfficeXmlFileException e1) {
			logger.error("Excel版本为xls，Excel必须为xlsx！", e1);
		} catch (IOException e) {
			logger.error("IO异常！", e);
			e.printStackTrace();
		} catch (GeneralSecurityException e) {
			logger.error("Excel加密失败！", e);
		} finally {
			try {
				if (null != oPCPackage) {
					oPCPackage.close();
				}
			} catch (IOException e) {
				logger.error("关闭OPCPackage异常！", e);
			}
			try {
				if (null != os) {
					os.close();
				}
			} catch (IOException e) {
				logger.error("关闭输出流异常！", os);
			}
		}

		// save the file back to the filesystem
		ByteArrayOutputStream bos = null;
		try {
			bos = new ByteArrayOutputStream();
			pOIFSFileSystem.writeFilesystem(bos);
			result = bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != bos) {
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

}
