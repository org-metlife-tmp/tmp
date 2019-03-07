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
import com.qhjf.cfm.web.service.RecvDiskSendingService;
import com.qhjf.cfm.web.service.RecvTxtDiskSendingService;
import com.qhjf.cfm.web.service.TxtDiskSendingService;

import java.util.List;


/**
 * @批收盘片发送页面
 *
 * @author lwb
 * @date 2018年9月18日
 */
public class RecvDiskSendingController extends CFMBaseController {

    private final static Log logger = LogbackLog.getLog(RecvDiskSendingController.class);
    private RecvDiskSendingService service = new  RecvDiskSendingService();
    private TxtDiskSendingService txtservice = new TxtDiskSendingService();
	private RecvTxtDiskSendingService recvTxtDiskSendingService = new RecvTxtDiskSendingService();
      
    
    /**
     * @throws ReqDataException 
     * @盘片发送列表
     */
    @Auth(hasForces = {"RecvBatchSend"})
    public  void  list() throws ReqDataException {
    	Record record = getRecordByParamsStrong();
    	int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);   		
    	Page<Record> page = service.list(pageNum,pageSize,record,getCurUodp());
    	renderOkPage(page);
    }

    /**
     * 发送银行
     */
    public  void  sendbank() throws ReqDataException {
    	Record record = getRecordByParamsStrong();
    	service.sendbank(record);
		renderOk(null);
    }
   
    
    /**
     * 批付盘片下载列表导出
     */
    @Auth(hasForces = {"RecvBatchSend"})
    public void listexport() {
        doExport();
    }
    
    /**
     * @盘片下载
     */
    //@Auth(hasForces = {"PayBatchSend"})
    @SuppressWarnings("unused")
	public void diskdownload() {
    	try {		
    		Record record = getParamsToRecordStrong();
    		UserInfo userInfo = getUserInfo();
    		Long pay_master_id = record.getLong("recv_master_id"); //主批次id
    		Long pay_id = record.getLong("recv_id"); //子批次id
    		//通道编码
        	String channel_code = record.getStr("channel_code");
        	List<Record> find = Db.find(Db.getSql("channel_setting.getChannelCode"), channel_code);
        	if(null == find || find.size() == 0 ){
        		logger.error("============通道编码已过期"+record.get("channel_code"));
        		throw new ReqDataException("此条通道编码已过期,请刷新页面");
        	}
        	Integer detail_id = find.get(0).getInt("document_moudle"); //报盘模板
        	Integer pay_attr = find.get(0).getInt("pay_attr");  //收付属性 0--收，1--付
        	Integer document_type = pay_attr == 0 ? WebConstant.DocumentType.SB.getKey() : WebConstant.DocumentType.FB.getKey();
        	logger.info("============报盘模板详情id==="+detail_id);
        	//默认详情配置是一定有的..如果没有说明此渠道未配置
        	//List<Record> configs_tail = Db.find(Db.getSql("disk_downloading.findDatailConfig"),document_type,document_moudle);
        	Record configs_tail = Db.findById("document_detail_config", "id", detail_id);
        	int document_moudle = Integer.valueOf(configs_tail.getStr("document_moudle")) ;

        	String document_version = configs_tail.getStr("document_version");
        	if(configs_tail == null ){
            	logger.error("==========此渠道模板尚未初始化");
            	throw new ReqDataException("此渠道模板尚未配置,请联系管理员");
            }
        	
        	List<Record> offerDocument = Db.find(Db.getSql("recv_disk_downloading.findOfferDocument"), pay_id);
            boolean is_download = false ;
            if(offerDocument != null && offerDocument.size() == 1){
            	logger.info("========已经是多次下载");
            	is_download = true ;
            }
        	//
        	logger.info("=========网盘是TXT格式的文件");
        	if(!is_download){
        		String fileName = txtservice.getFileName(document_moudle,document_type,document_version);
        		String diskDownLoad = recvTxtDiskSendingService.diskDownLoadNewThread(pay_master_id,pay_id,document_moudle,fileName,document_type,configs_tail);
        		render(new ByteArrayRender(fileName,diskDownLoad.getBytes()));
        	}else{
        		//直接去服务器上获取文件
        		String file_name = offerDocument.get(0).getStr("file_name");
        	    byte[] file_string = recvTxtDiskSendingService.repeatDownload(file_name,offerDocument.get(0),userInfo,pay_id);
        		render(new ByteArrayRender(file_name,file_string));
        	}
		} catch (Exception e) {
			logger.error("============盘片下载异常");
			e.printStackTrace();
		}
    }
    
}
