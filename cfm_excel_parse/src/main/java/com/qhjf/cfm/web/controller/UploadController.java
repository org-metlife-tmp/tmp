package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.excel.analyze.ExcelValidatorUtil;
import com.qhjf.cfm.excel.config.ExcelSheetConfig;
import com.qhjf.cfm.excel.config.load.impl.LoadSheetConfigUtil;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.web.constant.BasicTypeConstant;
import com.qhjf.cfm.web.plugins.excelup.UploadFileScaffold;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.render.ByteArrayRender;
import com.qhjf.cfm.web.utils.comm.file.info.FileInfo;
import com.qhjf.cfm.web.utils.comm.file.tool.FileTransToolFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

//@Auth(hasRoles = { "normal" })
public class UploadController extends CFMBaseController {

	private final static Log logger = LogbackLog.getLog(UploadController.class);

	/**
	 * 默认Excel上传Rest服务：会按配置文件进行默认校验，如果想自定义校验
	 */
	public void upload() {
		logger.debug("上传文件start...");
		Map<String, Object> result = new HashMap<>();
		// 1.解析文件流
		UploadFileScaffold ufs = resolveFileUpload();
		if (ufs.getContent() == null || ufs.getContent().length == 0) {
			result.put("success", false);
			result.put("error_code", "INVLDEXCEL");
			result.put("error_message", "数据内容为0");
			renderJson(result);
			return;
		}
		// 2.校验Excel文件
		result = new ExcelValidatorUtil().doExamination(ufs);

		renderJson(result);
	}

	/**
	 * 1.传入pk参数，下载excel模板 2.传入objectId，直接从mongodb下载excel
	 */
	public void downExcel() {
		Record params = getParamsToRecordStrong();
		String pk = params.getStr("pk");
		FileInfo fileContent = null;
		if (!StringUtils.isBlank(pk)) {
			logger.debug("【下载Excel模板】开始下载。。。");
			// 从Excle配置文件中获取模板的objectId
			ExcelSheetConfig excelSheetConfig = LoadSheetConfigUtil.getExcelSheetConfigMap().get(pk);
			if (null == excelSheetConfig) {
				renderJson(getErrorResult(false, String.format("excel模板不存在pk=%s", pk)));
				return;
			}

			String fileName = excelSheetConfig.getTemplateId();
			System.out.println("=================="+fileName);
			if (StringUtils.isBlank(fileName)) {
				renderJson(getErrorResult(false, String.format("pk=【%s】excel配置文件中没有配置模板【templateId】", pk)));
				return;
			}
			if (logger.isDebugEnabled()) {
				logger.debug(String.format("【下载Excel模板】templateId=%s", fileName));
			}
			//通过文件名获取mongoDB文件
			if (fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) {
				fileContent = getMongoFileByFileName(fileName);
			}else {
				try {
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("【下载Excel模板】开始下载xlsx模板，templateId=%s", fileName));
					}
					fileContent = getMongoFileByFileName(fileName.concat(BasicTypeConstant.EXCEL2007_SUFFIX));
					System.out.println("filename==================="+fileName.concat(BasicTypeConstant.EXCEL2007_SUFFIX));
				} catch (Exception e) {
					if (logger.isDebugEnabled()) {
						logger.debug(String.format("【下载Excel模板】xlsx下载失败，开始下载xls模板，templateId=%s", fileName));
					}
					try {
						fileContent = getMongoFileByFileName(fileName.concat(BasicTypeConstant.EXCEL97_SUFFIX));

					} catch (Exception e1) {
						logger.error(String.format("Excel模板%s下载失败", fileName.concat(BasicTypeConstant.EXCEL97_SUFFIX)), e1);
						renderText("Excel模板已经损坏！");
						return;
					}
				}
			}
		} else {
			String objectId = params.getStr("object_id");
			if (StringUtils.isBlank(objectId)) {
				renderJson(getErrorResult(false, "excel下载的请求参数错误！"));
				return;
			}
			fileContent = getMongoFileByObjectId(objectId);
		}

		// 把文件返回给客户端
		if (null != fileContent) {
			render(new ByteArrayRender(fileContent.getFilename(), fileContent.getContent()));
		}else {
			renderText("Excel模板已经损坏！");
		}
	}
	/**
	 * 通过objectId下载mongoDb的文件
	 * @param objectId
	 * @return
	 */
	private FileInfo getMongoFileByObjectId(String objectId) {
		FileInfo fileContent = null;
		try {
//			fileContent = MongoGridFSTool.getInstance().getFileContent(objectId);
			fileContent = FileTransToolFactory.getInstance().getFileByObjectid(objectId);
		} catch (BusinessException e) {
			logger.error(String.format("从mongoDb中下载Exce文件异常！objectId=【%s】", objectId));
			e.printStackTrace();
			renderJson(getErrorResult(false, "文件不存在，或者文件已损坏"));
		}
		return fileContent;
	}
	public FileInfo getMongoFileByFileName(String fileName){
		FileInfo fileContent = null;
		try {
//			fileContent = MongoGridFSTool.getInstance().getFileContentByFileName(fileName);
			fileContent = FileTransToolFactory.getInstance().getFileByFileName(fileName);
		} catch (BusinessException e) {
			logger.error(String.format("从mongoDb中下载Exce文件异常！fileName=【%s】", fileName));
			e.printStackTrace();
			renderJson(getErrorResult(false, "文件不存在，或者文件已损坏"));
		}
		return fileContent;
	}

	private Map<String, Object> getErrorResult(boolean success, String errorMessage){
		Map<String, Object> result = new HashMap<>();
		result.put("success", success);
		result.put("error_message", errorMessage);
		return result;
	}
}
