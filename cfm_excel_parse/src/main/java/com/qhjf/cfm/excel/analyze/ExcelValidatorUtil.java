package com.qhjf.cfm.excel.analyze;

import com.qhjf.cfm.excel.analyze.validator.IValidatorService;
import com.qhjf.cfm.excel.analyze.validator.ValidatorServiceFactory;
import com.qhjf.cfm.excel.bean.ResultBean;
import com.qhjf.cfm.excel.config.ExcelSheetConfig;
import com.qhjf.cfm.excel.config.load.impl.LoadSheetConfigUtil;
import com.qhjf.cfm.excel.exception.DisposeException;
import com.qhjf.cfm.excel.exception.StatisticsAssertionException;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.MD5Kit;
import com.qhjf.cfm.utils.RandomUtil;
import com.qhjf.cfm.web.constant.BasicTypeConstant;
import com.qhjf.cfm.web.plugins.excelup.UploadFileScaffold;
import com.qhjf.cfm.web.utils.comm.file.tool.FileTransToolFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 默认Excel校验工具类
 * @author CHT
 *
 */
public class ExcelValidatorUtil {
	
	private Map<String, Object> failureResultMap = new HashMap<>();
    private Map<String, Object> successResultMap = new HashMap<>();
    private UploadFileScaffold ufs;
    private String objectId;//原始Excel存入mongodb中的objectId
    
    /**
     * 
     * @param ufs	文件上传时，文件流与其他参数的解析工具
     * @return
     */
    public Map<String, Object> doExamination(UploadFileScaffold ufs){
    	this.ufs = ufs;
    	Map<String, Object> result = new HashMap<>();
    	
    	//获取Excel配置对象
		String pk = ufs.getParams().get("pk");
		if (StringUtils.isBlank(ufs.getParams().get("pk"))) {
    		result.put("success", false);
    		result.put("error_code", "INVLDEXCEL");
    		result.put("error_message", "缺少Excel模板参数pk");
    		return result;
		}
    	ExcelSheetConfig excelSheetConfig = LoadSheetConfigUtil.getExcelSheetConfigMap().get(pk);
    	if (null == excelSheetConfig) {
    		if (StringUtils.isBlank(ufs.getParams().get("pk"))) {
        		result.put("success", false);
        		result.put("error_code", "INVLDEXCEL");
        		result.put("error_message", String.format("缺少Excel配置文件：pk=%s", pk));
                return result;
    		}
		}
    	
    	//把Excel提交的数据到mongo的GridFS中
//    	this.objectId = MongoGridFSTool.getInstance().addNewFileToGridFS(MD5Kit.byteArrayToMD5(ufs.getContent()), ufs.getContent());
    	try {
			this.objectId = FileTransToolFactory.getInstance()
					.addNewFileByArray(MD5Kit.byteArrayToMD5(ufs.getContent()).concat(RandomUtil.currentTimeStamp()), ufs.getContent());
		} catch (BusinessException e1) {
			e1.printStackTrace();
			result.put("success", false);
    		result.put("error_message", String.format(BasicTypeConstant.ORIGIN_EXCEL_SAVE_FAIL, pk));
            return result;
		}
    	
    	//校验
    	IValidatorService validatorService = ValidatorServiceFactory.createValidatorService("1");
    	ResultBean bean = null;
		try {
			bean = validatorService.doValidate(ufs, excelSheetConfig);
		} catch (DisposeException e) {
			bean = new ResultBean();
			bean.setSuccess(false);
			bean.setMessage("系统错误,联系管理员");
			return onFailed(bean);
		} catch(StatisticsAssertionException e) {
			bean = new ResultBean();
			bean.setSuccess(false);
			bean.setMessage(e.getMessage());
			return onAssertion(bean);
		}
    	
    	if(!bean.isSuccess() && bean.getContent()==null) { // 文档数据格式错误
    		result = onError(bean);
		} else if(!bean.isSuccess() && bean.getContent()!=null) { // 文档内容不符合要求
			result = onFailed(bean);
		} else { // 完成解析
			result = onComplete(bean);
		}
    	return result;
    }
    
    
    
	// 错误的数据
    protected Map<String, Object> onError(ResultBean result) {
        failureResultMap.put("success", result.isSuccess());
        failureResultMap.put("error_code", "INVLDEXCEL");
        failureResultMap.put("error_message", result.getMessage());
        failureResultMap.put("original_file_name", ufs.getFilename());
        return failureResultMap;
    }

    // 根据配置发现数据不符合规定
    protected Map<String, Object> onFailed(ResultBean result) {
        failureResultMap.put("success", result.isSuccess());
        failureResultMap.put("error_code", "UNFITEXCEL");
        failureResultMap.put("error_message", result.getMessage());
        ByteArrayInputStream source = new ByteArrayInputStream(result.getContent());
        String ext = ufs.getFilename().substring(ufs.getFilename().lastIndexOf("."));
        String targetFilename = MD5Kit.byteArrayToMD5(result.getContent());
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

    protected Map<String, Object> onAssertion(ResultBean result) {
        return onError(result);
    }

    // 成功解析
    protected Map<String, Object> onComplete(ResultBean result) {
        successResultMap.put("object_id", ufs.getContentMd5());//检索excel解析结果的Key
        successResultMap.put("document_id", this.objectId);//上传的Excel的objectId
        //设置成功字段
        successResultMap.put("success", result.isSuccess());
        successResultMap.put("message", result.getMessage());
        successResultMap.put("original_file_name", ufs.getFilename());
        String ext = ufs.getFilename().substring(ufs.getFilename().lastIndexOf("."));
        successResultMap.put("original_file_ext", ext);
        
        Map<String, String> params = ufs.getParams();
    	if (params.containsKey("is_encrypt") && "1".equals(params.get("is_encrypt"))) {
    		successResultMap.put("encrypt_document_id", result.getEncryptObjectId());
    	}
        return successResultMap;
    }
}
