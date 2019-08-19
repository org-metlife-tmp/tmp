package com.qhjf.cfm.excel.analyze;

import com.qhjf.cfm.excel.analyze.validator.IValidatorService;
import com.qhjf.cfm.excel.analyze.validator.ValidatorServiceFactory;
import com.qhjf.cfm.excel.bean.ResultBean;
import com.qhjf.cfm.excel.config.ExcelSheetConfig;
import com.qhjf.cfm.excel.config.load.impl.LoadSheetConfigUtil;
import com.qhjf.cfm.excel.config.validator.ICustomValidator;
import com.qhjf.cfm.excel.exception.DisposeException;
import com.qhjf.cfm.excel.exception.ExcelConfigMergeException;
import com.qhjf.cfm.excel.exception.StatisticsAssertionException;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.web.constant.BasicTypeConstant;
import com.qhjf.cfm.web.plugins.excelup.UploadFileScaffold;
import com.qhjf.cfm.web.utils.comm.file.tool.FileTransToolFactory;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 自定义Excel校验工具类
 * 1.Excel存入mongoDb
 * 2.从内存获取Excel对应的校验规则
 * 3.将自定义校验规则  与  Excel对应的校验规则进行合并
 * 4.校验
 * 
 * @author CHT
 *
 */
public class ExcelCunstomValidatorUtil {

	/**
	 * 解析Excel
	 * @param ufs	Excel上传的参数对象
	 * @param validator	自定义Excel校验器
	 * @param pk	配置文件的索引
	 * @return
	 * @throws ReqDataException 
	 * @throws ExcelConfigMergeException 
	 */
	public Map<String, Object> parse(UploadFileScaffold ufs, ICustomValidator validator, String pk) throws ReqDataException, ExcelConfigMergeException{
		Map<String, Object> result = new HashMap<>();
		//1.存入mongoDb
		String objectId = null;
		try {
			objectId = saveMongoDb(ufs);
		} catch (BusinessException e1) {
			e1.printStackTrace();
			throw new ReqDataException(e1.getMessage());
		}
		//2.获取Excel的配置
		ExcelSheetConfig excelSheetConfig = LoadSheetConfigUtil.getExcelSheetConfigMap().get(pk);
		if (null == excelSheetConfig) {
			throw new ReqDataException(String.format("pk=[%s]没有相应的Excel配置文件", pk));
		}
		//3.合并自定义校验器
		ExcelSheetConfig configClone = null;
		try {
			configClone = (ExcelSheetConfig)excelSheetConfig.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		configClone.mergeValidator(validator);
		
		//校验
		Map<String, Object> failureResultMap = new HashMap<>();
	    Map<String, Object> successResultMap = new HashMap<>();
    	IValidatorService validatorService = ValidatorServiceFactory.createValidatorService("1");
    	ResultBean bean = null;
		try {
			bean = validatorService.doValidate(ufs, excelSheetConfig);
		} catch (DisposeException e) {
			bean = new ResultBean();
			bean.setSuccess(false);
			bean.setMessage("系统错误,联系管理员");
			result = onFailed(bean, failureResultMap, ufs);
			return result;
		} catch(StatisticsAssertionException e) {
			bean = new ResultBean();
			bean.setSuccess(false);
			bean.setMessage(e.getMessage());
			result = onAssertion(bean, failureResultMap, ufs);
			return result;
		}
    	
    	if(!bean.isSuccess() && bean.getContent()==null) { // 文档数据格式错误
    		result = onError(bean, failureResultMap, ufs);
		} else if(!bean.isSuccess() && bean.getContent()!=null) { // 文档内容不符合要求
			result = onFailed(bean, failureResultMap, ufs);
		} else { // 完成解析
			result = onComplete(bean, successResultMap, ufs, objectId);
		}
    	return result;
	}
	
	/**
	 * Excel存入mongoDb
	 * @param ufs
	 * @return
	 * @throws BusinessException 
	 */
	private String saveMongoDb(UploadFileScaffold ufs) throws BusinessException{
		//原始mongoDb存储
//		MongoGridFSTool mongo = MongoGridFSTool.getInstance();
//		return mongo.addNewFileToGridFS(MD5Kit.byteArrayToMD5(ufs.getContent()), ufs.getContent());
		return FileTransToolFactory.getInstance().addNewFileByArray(MD5Kit.byteArrayToMD5(ufs.getContent()), ufs.getContent());
	}
	
	// 错误的数据
    protected Map<String, Object> onError(ResultBean result, Map<String, Object> failureResultMap, UploadFileScaffold ufs) {
        failureResultMap.put("success", result.isSuccess());
        failureResultMap.put("error_code", "INVLDEXCEL");
        failureResultMap.put("error_message", result.getMessage());
        failureResultMap.put("original_file_name", ufs.getFilename());
        return failureResultMap;
    }

    // 根据配置发现数据不符合规定
    protected Map<String, Object> onFailed(ResultBean result, Map<String, Object> failureResultMap, UploadFileScaffold ufs) {
        failureResultMap.put("success", result.isSuccess());
        failureResultMap.put("error_code", "UNFITEXCEL");
        failureResultMap.put("error_message", result.getMessage());
        ByteArrayInputStream source = new ByteArrayInputStream(result.getContent());
        String ext = ufs.getFilename().substring(ufs.getFilename().lastIndexOf("."));
        String targetFilename = MD5Kit.byteArrayToMD5(result.getContent());
        //原始mongoDb存储方式
//        String failureObjectId = MongoGridFSTool.getInstance().addNewFileToGridFS(String.format("%s%s", targetFilename, ext), source);
        //文件系统存储 或者 mongoDb存储方式
        String failureObjectId = null;
		try {
			failureObjectId = FileTransToolFactory.getInstance().addNewFileByStream(String.format("%s%s", targetFilename, ext), source);
		} catch (BusinessException e) {
			e.printStackTrace();
			failureResultMap.put("error_message", BasicTypeConstant.ERROR_EXCEL_SAVE_FAIL);
		}
        failureResultMap.put("original_file_name", ufs.getFilename());
        failureResultMap.put("original_file_ext", ext);
        failureResultMap.put("download_object_id", failureObjectId);
        return failureResultMap;
    }

    protected Map<String, Object> onAssertion(ResultBean result, Map<String, Object> failureResultMap, UploadFileScaffold ufs) {
        return onError(result, failureResultMap, ufs);
    }

    // 成功解析
    protected Map<String, Object> onComplete(ResultBean result, Map<String, Object> successResultMap, UploadFileScaffold ufs, String objectId) {
        successResultMap.put("object_id", ufs.getContentMd5());//检索excel解析结果的Key
        successResultMap.put("document_id", objectId);//上传的Excel的objectId
        //设置成功字段
        successResultMap.put("success", result.isSuccess());
        successResultMap.put("message", result.getMessage());
        successResultMap.put("original_file_name", ufs.getFilename());
        String ext = ufs.getFilename().substring(ufs.getFilename().lastIndexOf("."));
        successResultMap.put("original_file_ext", ext);
        return successResultMap;
    }
}
