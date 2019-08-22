package com.qhjf.cfm.web.service;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.ext.kit.DateKit;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.config.DiskDownLoadSection;
import com.qhjf.cfm.web.constant.WebConstant;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ExcleDiskSendingService {
	
    private static final Logger logger = LoggerFactory.getLogger(ExcleDiskSendingService.class);

	   /**
     *   盘片下载
	 * @param pay_master_id   pay_id
	 * @param document_type 
	 * @param config_tail 
     * @throws ReqDataException 
     * @throws DbProcessException 
     * @throws IOException 
     */
    @SuppressWarnings("resource")
	public HSSFWorkbook diskDownLoad(Long pay_master_id, final Long pay_id, Integer document_moudle ,final UserInfo userInfo ,final String sheetName, Integer document_type, Record config_tail) throws ReqDataException, DbProcessException, IOException {
    	int num = 0 ;
    	//TODO   
		final Record user_record = Db.findById("user_info", "usr_id",userInfo.getUsr_id());
		if(null == user_record){
			throw new ReqDataException("当前登录人未在用户信息表内配置");
		}
    	List<Record> configs = Db.find(Db.getSql("disk_downloading.findTotalConfig"),document_type,document_moudle);
    	final HSSFWorkbook workbook = new HSSFWorkbook();   	   	
    	HSSFSheet sheet = workbook.createSheet(sheetName);
    	HSSFCell cell;
    	HSSFRow row ;
    	final Record totalOffer = new Record();
    	final List<Record> detailOffers =  new  ArrayList<>();  	
    	// TODO  pay_batch_total  根据批次号,查询批次id
    	Record record = Db.findFirst(Db.getSql("disk_downloading.findToatlInfo"),pay_id);
    	if(null != configs && configs.size() == 1 ){
    		logger.info("===========此种类型网片信息需要汇总信息");
    		Record config = configs.get(0);  
    		if(!StringUtils.isBlank(TypeUtils.castToString(config.get("title")))){
    			logger.info("===========此种类型网片信息汇总信息需要抬头");
    			//创建汇总信息表头
    			row = sheet.createRow(num++);  
    			for (int i = 0; i < TypeUtils.castToString(config.get("title")).split(",").length; i++) {
            		cell = row.createCell(i);
            		cell.setCellValue(TypeUtils.castToString(config.get("title")).split(",")[i]);
            	}   			
    		}
    		String total_titleNames = "" ;   	     		
    		   		
    		for (int i = 1; i <= config.getColumns().size() ; i++) {
    			if(!StringUtils.isBlank(config.getStr("field_"+i))){
    				total_titleNames = (total_titleNames == "" ? config.getStr("field_"+i) :total_titleNames+ ","+ config.getStr("field_"+i)) ;
    			}else {
    				break ;
    			}
			}
    		 //填充数据
            row = sheet.createRow(num++);
            for (int j = 0; j < total_titleNames.split(",").length; j++) {
                 String value = StrKit.isBlank(record.getStr(total_titleNames.split(",")[j])) ? "" : record.getStr(total_titleNames.split(",")[j]);
                 cell = row.createCell(j);
                 cell.setCellValue(value);
             }		    		
    	}
    	logger.info("===============详情部分===========");
    	//封装 offerDocument_total 表
		totalOffer.set("channel_id", record.get("channel_id"))
		          .set("batch_id", pay_id)
		          .set("file_name", sheetName)
		          .set("download_count", 1)
		          .set("create_on", new Date())
		          .set("create_by", userInfo.getUsr_id())
		          .set("total_amount", record.get("pay_total_amount"))
		          .set("total_num", record.get("pay_total_num"));
    	logger.info("========当前excel表总行数==="+num);
    	if(!StringUtils.isBlank(config_tail.getStr("title"))){
    		logger.info("============网盘详情也有表头");
    		//创建详情信息表头
			row = sheet.createRow(num++);  
			for (int i = 0; i < TypeUtils.castToString(config_tail.get("title")).split(",").length; i++) {
        		cell = row.createCell(i);
        		cell.setCellValue(TypeUtils.castToString(config_tail.get("title")).split(",")[i]);
        	}
    	}else{
    		logger.info("============网盘详情无表头");
    	}
        //填充表头数据
    	String datail_titleNames = "" ;
        for (int i = 1; i <= config_tail.getColumns().size(); i++) {
        	if(!StringUtils.isBlank(config_tail.getStr("field_"+i))){
        		datail_titleNames = (datail_titleNames == "" ? config_tail.getStr("field_"+i) : datail_titleNames+","+ config_tail.getStr("field_"+i)) ;
			}else {
				break ;
			}
		}                 
        //根据 batch_id 查询 pay_batch_detail 表, 存在多条  base_id = batch_id
        List<Record> detailRecords = Db.find(Db.getSql("disk_downloading.findDatailInfo"), pay_id);
        
        for (int i = 0; i < detailRecords.size(); i++) {
        	row = sheet.createRow(num++);
        	Record detailRecord = new Record();
        	//封装 offerDocument_detail 出盘明细信息表 
        	detailRecord.set("package_seq", detailRecords.get(i).get("package_seq")) 
        	            .set("legal_id", detailRecords.get(i).get("legal_id"))
        	            .set("amount", detailRecords.get(i).get("amount"))
        	            .set("recv_acc_no", detailRecords.get(i).get("recv_acc_no"))
        	            .set("recv_acc_name", detailRecords.get(i).get("recv_acc_name"))
        	            .set("recv_cert_type", detailRecords.get(i).get("recv_cert_type"))
        	            .set("recv_cert_code", detailRecords.get(i).get("recv_cert_code"))
        	            .set("recv_bank_name", detailRecords.get(i).get("recv_bank_name"));
        	detailOffers.add(detailRecord);
        	for (int j = 0; j < datail_titleNames.split(",").length; j++) {
        		String value = StrKit.isBlank(detailRecords.get(i).getStr(datail_titleNames.split(",")[j])) ? "" : detailRecords.get(i).getStr(datail_titleNames.split(",")[j]);
        		cell = row.createCell(j);
        		cell.setCellValue(value);
        	}			
		}
        	// 出盘的汇总表 ,出盘的详情表
        	boolean flag = Db.tx(new IAtom() {		
        		@Override
        		public boolean run() throws SQLException {
        			boolean save = Db.save("pay_offerDocument_total","id", totalOffer);
        			if(save){
        				logger.info("==================offerDocument_total表插入成功");
        				Integer id = totalOffer.getInt("id");
        				for (int i = 0; i < detailOffers.size(); i++) {
        					detailOffers.get(i).set("base_id", id);
        				}
        				int[] batchSave = Db.batchSave("pay_offerDocument_detail", detailOffers, 1000);
        				boolean detail_save = ArrayUtil.checkDbResult(batchSave);
        				logger.info("===========插入pay_offerDocument_detail结果=="+detail_save);
        				if(detail_save){
        					logger.info("=====更新子批次状态为  已下载未回盘/已发送未回盘");
        					 boolean updateStatus = CommonService.update("pay_batch_total", new Record().set("send_on", new Date()).set("send_user_name", user_record.get("name"))
        							.set("service_status", WebConstant.SftCheckBatchStatus.FSWHP.getKey()) //TODO 已发送未回盘
        							, new Record().set("id", pay_id));
        					 if(updateStatus){
        						 logger.info("=============更新子批次状态=="+updateStatus);
        						 //开始向服务器上上传文件
        						 try {
									diskSave(sheetName, workbook);
									return true ;
								} catch (IOException e) {								
									e.printStackTrace();
									logger.error("==========网盘上传服务器错误");
									return false ;
								}
        					 }
        				}
        				
        			}
        			return false;
        		}
        	});
        	if(!flag){
        		throw new DbProcessException("============网盘信息入库失败");
        	}
            return workbook ;
    }
    
    /**
     * 文件上传
     * @throws IOException 
     */
    protected void diskSave(String sheetName , Workbook workbook) throws IOException  {
        DiskDownLoadSection diskDownLoadSection = DiskDownLoadSection.getInstance();
        String path = diskDownLoadSection.getPath();
    	File f = new File(path + sheetName);
    	logger.info("路径="+path + sheetName);
    	if(!f.exists()){
    		boolean flag = f.createNewFile();
    		logger.info("===============创建文件==="+flag);
    	}
        FileOutputStream output = null;
		try {
			output = new FileOutputStream(f);
			workbook.write(output);//写入磁盘  
		} catch (Exception e) {
			logger.info("===========写入服务器失败,路径="+path + sheetName);
			e.printStackTrace();
		} finally {
			if(null != output){
				output.close();
				output.flush();
			}
           if(null != workbook){
        	   workbook.close();
			}
		} 
    }
    

    /**
     *  招行盘片文件名组装
     * @throws IOException 
     */
    public String getFileName(int channel_code, int document_type) {
    	// 年月日 + 随机数 + 渠道编码 + 盘片类型
    	String time = DateKit.toStr(new Date(), DateKit.datePattern).replaceAll("-", "");
    	String genShortSerial = RedisSericalnoGenTool.genShortSerial();
    	String filename = time +"_" + genShortSerial + "_" + channel_code + "_" + document_type +".xls" ;
    	return filename ;
    }

    /**
     * @param offer 
     * @param userInfo 
     * @param pay_id 
     * @当不是第一次下载的时候,直接从服务器上获取文件
     * @param string
     * @throws IOException 
     */
	public byte[] repeatDownload(String filename, Record offer, UserInfo userInfo, Long pay_id) throws IOException {
		Record user_record = Db.findById("user_info", "usr_id",userInfo.getUsr_id());
		DiskDownLoadSection diskDownLoadSection = DiskDownLoadSection.getInstance();
        String path = diskDownLoadSection.getPath()+filename;
        FileInputStream fis = null;
        ByteArrayOutputStream  bos  = new ByteArrayOutputStream()  ;
        byte[] byteArray = null  ;
		try {
			File file = new File(path);
			if(!file.exists()) {
				logger.error("=======文件在服务器中不存在===="+file);
			}
			fis = new FileInputStream(file);
			byte[] buf = new byte[1024];//每次读入文件数据量
			int len = -1 ;
			while ((len = (fis.read(buf))) != -1) {
				bos.write(buf, 0, len);
			}
			byteArray = bos.toByteArray();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			logger.error("获取服务器上的文件失败");
		}finally{
			if(null != fis){
				fis.close();			
			}
		}
		logger.info("===============此网盘已经多次下载");
    	boolean updateDownCount = CommonService.update("pay_offerDocument_total",
    			new Record().set("download_count", offer.getInt("download_count")+1),
                new Record().set("batch_id", offer.get("batch_id")));
   	    logger.info("更新表offerDocument_total下载次数结果==="+updateDownCount);
   	    logger.info("===============子批次表状态更新为已发送未回盘");
   	    boolean updateStatus = CommonService.update("pay_batch_total", new Record().set("send_on", new Date()).set("send_user_name", user_record.get("name"))
				.set("service_status", WebConstant.SftCheckBatchStatus.FSWHP.getKey()) //TODO 已发送未回盘
				, new Record().set("id", pay_id));
        
   	    return byteArray ;

	}
	
}
