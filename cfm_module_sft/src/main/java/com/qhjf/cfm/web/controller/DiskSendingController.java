package com.qhjf.cfm.web.controller;

import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.render.ByteArrayRender;
import com.qhjf.cfm.web.render.FileRender;
import com.qhjf.cfm.web.service.DiskSendingService;
import com.qhjf.cfm.web.service.ExcleDiskSendingService;
import com.qhjf.cfm.web.service.TxtDiskSendingService;
import java.util.List;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


/**
 * @通道设置
 *
 * @author lwb
 * @date 2018年9月18日
 */
public class DiskSendingController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(DiskSendingController.class);
    private ExcleDiskSendingService excleservice = new ExcleDiskSendingService();
    private TxtDiskSendingService txtservice = new TxtDiskSendingService();
    private DiskSendingService service = new  DiskSendingService();

    /**
     * @盘片下载
     */
    @Auth(hasForces = {"PayBatchSend"})
    public void diskdownload() {
    	try {		
    		Record record = getParamsToRecordStrong();
    		UserInfo userInfo = getUserInfo();
    		Long pay_master_id = record.getLong("pay_master_id"); //主批次id
    		Long pay_id = record.getLong("pay_id"); //子批次id
    		Record pey_total = Db.findById("pay_batch_total", "id", pay_id);
    		if(pey_total.getInt("service_status") == WebConstant.SftCheckBatchStatus.SPZ.getKey()){
    			throw new ReqDataException("此盘片仍处于审批中,暂不支持下载");
    		}
    		//通道编码
        	String channel_code = record.getStr("channel_code");
        	List<Record> find = Db.find(Db.getSql("channel_setting.getChannelCode"), channel_code);
        	if(null == find || find.size() == 0 ){
        		logger.error("============通道编码已过期"+record.get("channel_code"));
        		throw new ReqDataException("此条通道编码已过期,请刷新页面");
        	}
        	Integer document_moudle = find.get(0).getInt("document_moudle"); //报盘模板
        	Integer pay_attr = find.get(0).getInt("pay_attr");  //收付属性 0--收，1--付
        	Integer document_type = pay_attr == 0 ? WebConstant.DocumentType.SB.getKey() : WebConstant.DocumentType.FB.getKey();
        	logger.info("============报盘模板类型==="+document_moudle);
        	//默认详情配置是一定有的..如果没有说明此渠道未配置
        	List<Record> configs_tail = Db.find(Db.getSql("disk_downloading.findDatailConfig"),document_type,document_moudle);
            if(configs_tail == null || configs_tail.size() == 0){
            	logger.error("==========此渠道模板尚未初始化");
            	throw new ReqDataException("此渠道模板尚未配置,请联系管理员");
            }
        	
        	List<Record> offerDocument = Db.find(Db.getSql("disk_downloading.findOfferDocument"), pay_id);
            boolean is_download = false ;
            if(offerDocument != null && offerDocument.size() == 1){
            	logger.info("========已经是多次下载");
            	is_download = true ;
            }
        	//
        	if(WebConstant.Channel.ZP.getKey() == document_moudle){
        		logger.info("=========网盘是Excel格式的文件");
        		if(!is_download){
        			String fileName = excleservice.getFileName(document_moudle,document_type);
        			HSSFWorkbook workbook = excleservice.diskDownLoad(pay_master_id,pay_id,document_moudle,userInfo,fileName,document_type,configs_tail);
        			render(new FileRender(fileName, workbook));        		       			
        		}else{
        			//直接去服务器上获取文件
        			String file_name = offerDocument.get(0).getStr("file_name");
        			byte[] file_string = excleservice.repeatDownload(file_name,offerDocument.get(0),userInfo,pay_id);      	
        			render(new ByteArrayRender(file_name,file_string));
        		}
        	}else{
        		logger.info("=========网盘是TXT格式的文件");
        		if(!is_download){
        		    String fileName = txtservice.getFileName(document_moudle,document_type);
        		    String diskDownLoad = txtservice.diskDownLoad(pay_master_id,pay_id,document_moudle,userInfo,fileName,document_type,configs_tail);
        		    render(new ByteArrayRender(fileName,diskDownLoad.getBytes()));
        		}else{
        			//直接去服务器上获取文件
        			String file_name = offerDocument.get(0).getStr("file_name");
        			byte[] file_string = excleservice.repeatDownload(file_name,offerDocument.get(0),userInfo,pay_id);
        			render(new ByteArrayRender(file_name,file_string));
        		}
        	}
		} catch (Exception e) {
			logger.error("============盘片下载异常");
			e.printStackTrace();
		}
    }
    
    
    /**
     * @throws ReqDataException 
     * @盘片发送列表
     */
    @Auth(hasForces = {"PayBatchSend"})
    public  void  list() throws ReqDataException {
    	Record record = getRecordByParamsStrong();
    	int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);   		
    	Page<Record> page = service.list(pageNum,pageSize,record,getCurUodp());
    	renderOkPage(page);
    }


    /**
     * @throws ReqDataException
     * @发送盘片
     */
    @Auth(hasForces = {"PayBatchSend"})
    public  void  sendbank() throws ReqDataException {
    	Record record = getRecordByParamsStrong();
    	service.sendbank(record);
		renderOk(null);
    }
    
    /**
     * 核对组批导出
     */
    @Auth(hasForces = {"PayBatchSend"})
    public void listexport() {
        doExport();
    }
}
