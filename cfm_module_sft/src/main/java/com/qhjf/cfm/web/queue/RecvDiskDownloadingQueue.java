package com.qhjf.cfm.web.queue;

import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.service.RecvTxtDiskSendingService;
import com.qhjf.cfm.web.service.TxtDiskSendingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * 收付费审批流结束后,hookpass内开启一个异步的线程下载盘片
 * @author pc_liweibing
 *
 */
public class RecvDiskDownloadingQueue implements  Runnable {

	private static Logger log = LoggerFactory.getLogger(RecvDiskDownloadingQueue.class);

	private RecvTxtDiskSendingService recvTxtDiskSendingService = new RecvTxtDiskSendingService();
	
    private TxtDiskSendingService txtservice = new TxtDiskSendingService(); 

	
	private Record main_record ;
	
	
	public Record getMain_record() {
		return main_record;
	}


	public void setMain_record(Record main_record) {
		this.main_record = main_record;
	}


	@SuppressWarnings("unused")
	@Override
	public void run() {
		long startMillis = System.currentTimeMillis();
		log.info("======开启异步线程下载盘片,盘片的主批次id==="+main_record.getStr("id"));
		String master_batchno = main_record.getStr("master_batchno");
		Long channel_id = main_record.getLong("channel_id");
		Record channel = Db.findById("channel_setting", "id", channel_id);
		List<Record> find = Db.find(Db.getSql("recv_disk_downloading.findTotalByMainBatchNo"),master_batchno);
		Integer detail_id = channel.getInt("document_moudle"); //报盘模板
    	Integer pay_attr = channel.getInt("pay_attr");  //收付属性 0--收，1--付
    	String channel_code = channel.getStr("channel_code");
    	Integer document_type = pay_attr == 0 ? WebConstant.DocumentType.SB.getKey() : WebConstant.DocumentType.FB.getKey();
    	log.info("============报盘模板详情id==="+detail_id);
    	Record configs_tail = Db.findById("document_detail_config", "id", detail_id);
    	int document_moudle = Integer.valueOf(configs_tail.getStr("document_moudle")) ;
    	String document_version = configs_tail.getStr("document_version");
    	//默认详情配置是一定有的..如果没有说明此渠道未配置
    	//List<Record> configs_tail = Db.find(Db.getSql("disk_downloading.findDatailConfig"),document_type,document_moudle);
        if(configs_tail == null){
        	log.error("==========此渠道模板尚未初始化");
        	return ;
        }   	    	
		if(null != find  && find.size()> 0){
			//工行的递增的随机数
			String uuid = RedisSericalnoGenTool.genCCBCDiskFileSeqNo(channel_code);
			String date = DateKit.toStr(new Date(), DateKit.datePattern).replaceAll("-", "");
			for (int i = 0 ; i < find.size() ; i++ ){				
	        		log.info("=========网盘是TXT格式的文件");
	        		Record record = find.get(i);
	        		String fileName = txtservice.getSFileName(document_moudle,document_type,document_version,channel_code,i+1 ,uuid,date);
	        		try {
	        			recvTxtDiskSendingService.diskDownLoadNewThread(main_record.getLong("id"),record.getLong("id"),document_moudle,fileName,document_type,configs_tail);
					} catch (Exception e) {
						log.error("===========txt文件下载失败,子批次id==="+record.getLong("id"));
						e.printStackTrace();
						continue ;
					} 
			}
		}else {
			log.error("======未根据主批次号查询到子批次信息==="+master_batchno);
		}
		log.info("===========盘片文件生成结束,总耗时=="+(System.currentTimeMillis()-startMillis));
	}
}
