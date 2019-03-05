package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.UodpInfo;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.config.DiskDownLoadSection;
import com.qhjf.cfm.web.config.DiskUpLoadSection;
import com.qhjf.cfm.web.plugins.excelup.UploadFileScaffold;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.DiskBackingService;
import com.qhjf.cfm.web.service.RecvDiskBackingService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * @批收回盘
 *
 * @author lwb
 * @date 2018年9月18日
 */
public class RecvDiskBackingController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(RecvDiskBackingController.class);
    private RecvDiskBackingService service = new  RecvDiskBackingService();

     
    /**
     * @throws ReqDataException 
     * @ 批收盘片回盘列表
     */
    //@Auth(hasForces = {"PayBatchResp"})
    public void list() throws ReqDataException{
    	logger.info("=========进入回盘列表页面");
    	Record record = getRecordByParamsStrong();
    	UodpInfo curUodp = getCurUodp();
    	int pageNum = getPageNum(record);
    	int pageSize = getPageSize(record);
    	Page<Record> page = service.list(pageNum,pageSize,record,curUodp);
    	renderOkPage(page);
    }
    
    
    
    
    
    /**
      * @接收并解析回盘文件
      * @throws IOException 
     * @throws ReqDataException 
      */
    public void upload() throws IOException, ReqDataException {
    	String recv_master_id = getHeader("recv_master_id");
    	String recv_id = getHeader("recv_id");
    	String channel_id = getHeader("channel_id");
    	String user_id = getHeader("user_id");
    	logger.info("=============开始接收回盘文件");
    	UploadFileScaffold ufs = resolveFileUpload();
    	Map<String, Object> result = new HashMap<>();
		if (ufs.getContent() == null || ufs.getContent().length == 0) {
			result.put("success", false);
			result.put("error_code", "NULLFILE");
			result.put("error_message", "数据内容为0");
			renderJson(result);
			return;
		}
		result = service.validateAndWriteToUrl(user_id,ufs,result,recv_master_id,recv_id,channel_id);
		renderJson(result);
    }   
    
    /**
     * 回盘列表导出
     */
    //@Auth(hasForces = {"PayBatchResp"})
    public void listexport() {
        doExport();
    }

}
