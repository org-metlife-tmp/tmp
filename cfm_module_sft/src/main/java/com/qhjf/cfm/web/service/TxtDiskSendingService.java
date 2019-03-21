package com.qhjf.cfm.web.service;


import com.jfinal.ext.kit.DateKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.RedisSericalnoGenTool;
import com.qhjf.cfm.utils.SymmetricEncryptUtil;
import com.qhjf.cfm.utils.VelocityUtil;
import com.qhjf.cfm.web.UserInfo;
import com.qhjf.cfm.web.config.DiskDownLoadSection;
import com.qhjf.cfm.web.constant.WebConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.SQLException;
import java.util.*;

public class TxtDiskSendingService {
	
    private static final Logger logger = LoggerFactory.getLogger(TxtDiskSendingService.class);
    ConstructTotalMapService constructTotalMapService = new ConstructTotalMapService();
    ConstructDetailMapService constructDetailMapService = new ConstructDetailMapService();
   
    private  static Map<String,String>  SHH_Map  = new  HashMap<>();
    static{
    	//广银联批量  付款报盘
    	SHH_Map.put("31", "000191400100081");
    	//广银联批量  收款报盘
    	SHH_Map.put("33", "000191400100080");
    	//广银联信用卡  付款报盘
    	SHH_Map.put("41", "000191400205608");
    	//广银联信用卡  收款报盘
    	SHH_Map.put("43", "000191400204574");
    	//广银联实时  付款报盘
    	SHH_Map.put("51", "000191400204654");
    	//通联批量 付款报盘
    	SHH_Map.put("61", "200455500003685");
    	//通联批量 收款报盘
    	SHH_Map.put("63", "200455500003685");
    }

    /**
     * 生成txt格式文档
     * @param pay_master_id
     * @param pay_id
     * @param document_moudle
     * @param userInfo
     * @param fileName
     * @param document_type
     * @param configs_tail
     * @return
     * @throws IOException
     * @throws BusinessException 
     */
	public String diskDownLoad(Long pay_master_id,final Long pay_id,Integer document_moudle, final UserInfo userInfo, final String fileName, Integer document_type, Record configs_tail) throws IOException, BusinessException {
		logger.info("==============生成txt样式的盘片");
		Record pay_total = Db.findById("pay_batch_total", "id", pay_id);
		final Record user_record = Db.findById("user_info", "usr_id",userInfo.getUsr_id());
		if(null == user_record){
			throw new ReqDataException("当前登录人未在用户信息表内配置");
		}
		// .mv文件均放在common包的resource文件下
		String filePath = String.valueOf(document_moudle)+ document_type +".vm";
		String total_titleNames = "";
		String datail_titleNames = "";
		Map<String, Object> map = new HashMap<>();
		final Record totalOffer = new Record();
		final List<Record> detailOffers = new ArrayList<>();
		List<Map<String,Object>> datail_list = new ArrayList<>();

    	List<Record> configs = Db.find(Db.getSql("disk_downloading.findTotalConfig"),document_type,document_moudle);
    	// TODO  pay_batch_total  根据批次号,查询批次id
    	Record record = Db.findFirst(Db.getSql("disk_downloading.findToatlInfo"),pay_id);
    	
    	//仅仅查一张表是不符合所有渠道的盘片需要的汇总信息的字段展示,
    	//多的只需要继续向batchRecord.get(0)内封装即可,初始化的表中也相应添加field_i
    	constructTotalMapService.constructTotalRecord(map,record,document_moudle);
    	
    	if(configs == null || configs.size() == 0 ){
        	logger.info("=========此类型盘片不需要汇总信息===");	
        }else{
        	//汇总信息的第一个字段 S , F
        	map.put("sf_flag", this.getSfFlag(document_type));
        	Record total_rec_config = configs.get(0);
        	if(StringUtils.isNotBlank(total_rec_config.getStr("title"))){
        		logger.info("======抬头非空");
        		map.put("total_title", total_rec_config.getStr("title"));
        	}
        	for (int i = 1; i <= total_rec_config.getColumns().size(); i++) {
				if(StringUtils.isNotBlank(total_rec_config.getStr("field_"+i))){
				    if(total_titleNames != null && !"".equals(total_titleNames)){
                        total_titleNames += ("," + total_rec_config.getStr("field_"+i));
                    }else{
                        total_titleNames = total_rec_config.getStr("field_"+i);
                    }
				}else {
					break ;
				}
			}
        	logger.info("===========汇总total_titleNames======" + total_titleNames);
        	for (int i = 0; i < total_titleNames.split(",").length; i++) {
				map.put(total_titleNames.split(",")[i], record.get(total_titleNames.split(",")[i]));
			}
        }
    	
    	//封装 offerDocument_total 表
    			totalOffer.set("channel_id", record.get("channel_id"))
    			          .set("batch_id", pay_id)
    			          .set("file_name", fileName)
    			          .set("download_count", 1)
    			          .set("create_on", new Date())
    			          .set("create_by", userInfo.getUsr_id())
    			          .set("total_amount", record.get("pay_total_amount"))
    			          .set("total_num", record.get("pay_total_num"));
   	
        //默认是一定有这个配置存在
		if(null != configs_tail ){			
			//详情的抬头处理
			if(StringUtils.isNotBlank(configs_tail.getStr("title"))){
				map.put("detail_title", configs_tail.getStr("title"));
			}		
			//Record detail_rec_config = configs_tail.get(0);
			for (int i = 1; i <= configs_tail.getColumns().size(); i++) {
				if(StringUtils.isNotBlank(configs_tail.getStr("field_"+i))){
				    if(datail_titleNames != null && !"".equals(datail_titleNames)){
				        datail_titleNames += ("," +   configs_tail.getStr("field_"+i) );
                    }else{
                        datail_titleNames = configs_tail.getStr("field_"+i);
                    }
				}else{
					break ;
				}
			}
			logger.info("===========详情datail_titleNames======" + datail_titleNames);
			List<Record> detailRecords = null ;
			if(0 == pay_total.getInt("source_sys")) {
				logger.info("===============盘片来源系统LA");
				detailRecords = Db.find(Db.getSql("disk_downloading.findDatailInfo"), pay_id);	       	          
			}else{
				logger.info("===============盘片来源系统EBS");
				detailRecords = Db.find(Db.getSql("disk_downloading.findEBSDatailInfo"), pay_id);	       	          
			}
			
			//根据 batch_id 查询 pay_batch_detail 表, 存在多条  base_id = batch_id
	        for (int i = 0; i < detailRecords.size(); i++) {	        	
	        	Record detailRecord = new Record();
	        	detailRecord.set("package_seq", detailRecords.get(i).get("package_seq"))
	        	            .set("legal_id", detailRecords.get(i).get("legal_id"))
	        	            .set("amount", detailRecords.get(i).get("amount"))
	        	            .set("recv_acc_no", detailRecords.get(i).get("recv_acc_no"))
	        	            .set("recv_acc_name", detailRecords.get(i).get("recv_acc_name"))
	        	            .set("recv_cert_type", detailRecords.get(i).get("recv_cert_type"))
	        	            .set("recv_cert_code", detailRecords.get(i).get("recv_cert_code"))
	        	            .set("recv_bank_name", detailRecords.get(i).get("recv_bank_name"));
	        	            
	        	detailOffers.add(detailRecord);
	        	
	        	Map<String, Object> detail_map = new HashMap<>();
	        	
	        	//详情的record同样也需要额外封装一些盘片需要的详情信息
	        	constructDetailMapService.constructDetailRecord(detail_map,detailRecords.get(i),document_moudle);	        	
	        	//detail_map.put("serialnum", detailRecords.get(i).get("serialnum"));
	        	for(int j = 0; j < datail_titleNames.split(",").length; j++) {
	        		if("recv_acc_no".equals(datail_titleNames.split(",")[j])) {
	        			SymmetricEncryptUtil util = SymmetricEncryptUtil.getInstance();
	        			String str = detailRecords.get(i).getStr(datail_titleNames.split(",")[j]);
	        			String acc_no = util.decryptToStr(str);
	        			detail_map.put(datail_titleNames.split(",")[j],acc_no );	        			
	        		}else {
	        			detail_map.put(datail_titleNames.split(",")[j], detailRecords.get(i).getStr(datail_titleNames.split(",")[j]));	        			
	        		}
				}
	        	datail_list.add(detail_map);
			}
	        map.put("details", datail_list);
	        final String genVelo  = VelocityUtil.genVelo(filePath, map);		        
	        logger.info("===============此网盘第一次下载");
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
	        			boolean batchsave = ArrayUtil.checkDbResult(batchSave);
	        			logger.info("===========保存pay_offerDocument_detail表====="+batchsave);
	        			if(batchsave) {
	        				 boolean updateStatus = CommonService.update("pay_batch_total", new Record().set("send_on", new Date()).set("send_user_name", user_record.get("name"))
	        							.set("service_status", WebConstant.SftCheckBatchStatus.FSWHP.getKey()) //TODO 已发送未回盘
	        							, new Record().set("id", pay_id));
	        				if(updateStatus) {
	        					//往服务器上写文件
	        					try {
									diskSave(fileName, genVelo);
									return true ;
								} catch (IOException e) {
									logger.error("===========往服务器上上传.txt文件失败");
									e.printStackTrace();
									return false ;
								}
	        				}        				
	        			  }	        			
	        		}
	        		return false;
	        	}
	        });
	        	if(!flag){
	        		throw new DbProcessException("============Txt网盘信息入库失败");
	        	}	        	
	        	return  genVelo ;
		}else{
			throw new ReqDataException("============此通道盘片类型初始化存在问题");
		}
	}

    /**
     * 批量代付文件名
     * @param document_moudle
     * @param document_type
     * @param version
     * @return
     */
	public String getFileName(Integer document_moudle, Integer document_type, String version) {
		String fileName = "";
		//广银联/通联文件名称格式
		StringBuffer  sb = new  StringBuffer();
		//TODO 商户号
		String  shh = SHH_Map.get(String.valueOf(document_moudle)+document_type);
		// 代付 F , 代收 S
		String sfFlag = this.getSfFlag(document_type);	
		//提交日期
		String time = DateKit.toStr(new Date(), DateKit.datePattern).replaceAll("-", "");
		if( WebConstant.Channel.RP.getKey() == document_moudle
				 ||WebConstant.Channel.GP.getKey() == document_moudle
				 ||WebConstant.Channel.GS.getKey() == document_moudle
				 ||WebConstant.Channel.TP.getKey() == document_moudle
				){
			//5位的序列号
			final String seq = RedisSericalnoGenTool.genDiskFileSeqNo();
			fileName = sb.append(shh).append("_").append(sfFlag).append(version).append(time)
					             .append("_").append(seq).toString();
			logger.info("=========生成盘片的文件名==="+fileName);			
		}else if(WebConstant.Channel.GX.getKey() == document_moudle) {
			//广银联信用卡
			//3位的序列号
			final String seq = RedisSericalnoGenTool.genThreeDiskFileSeqNo();
			fileName = sb.append(shh).append("_").append(sfFlag).append(version).append(time)
		             .append("_").append("54").append(seq).toString();
            logger.info("=========生成盘片的文件名==="+fileName);
		}else{
			// 建行   CCB_F_**********_++++++++.TXT
			fileName = "CCB_"+ this.getSfFlag(document_type) + "_U9" + 
			            RedisSericalnoGenTool.genCCBCDiskFileSeqNo() + "_" + 
					    DateKit.toStr(new Date(), DateKit.datePattern).replaceAll("-", "");
			logger.info("=========生成盘片的文件名==="+fileName);
		}		
		return fileName + ".TXT" ;
	}
	
	/**
     * 批量代收文件名
     * @param document_moudle
     * @param document_type
     * @param version
     * @return
     */
	public String getSFileName(Integer document_moudle, Integer document_type, String version) {
		String fileName = "";
		//广银联/通联文件名称格式
		StringBuffer  sb = new  StringBuffer();
		//TODO 商户号
		String  shh = SHH_Map.get(String.valueOf(document_moudle)+document_type);
		// 代付 F , 代收 S
		String sfFlag = this.getSfFlag(document_type);	
		//提交日期
		String time = DateKit.toStr(new Date(), DateKit.datePattern).replaceAll("-", "");
		if(WebConstant.Channel.GP.getKey() == document_moudle
				 ||WebConstant.Channel.GS.getKey() == document_moudle
				 ||WebConstant.Channel.TP.getKey() == document_moudle
				 ||WebConstant.Channel.GX.getKey() == document_moudle
				){
			//5位的序列号
			final String seq = RedisSericalnoGenTool.genDiskFileSeqNo();
			fileName = sb.append(shh).append("_").append(sfFlag).append(version).append(time)
					             .append("_").append(seq).toString();
			logger.info("=========生成盘片的文件名==="+fileName);			
		}else if( WebConstant.Channel.RP.getKey() == document_moudle) {
			//融汇通
			//8位的序列号
			final String seq = RedisSericalnoGenTool.genCCBCDiskFileSeqNo();
			fileName = sb.append("FINGARD").append("_").append(sfFlag).append(version).append(time)
		             .append("_").append(seq).toString();
            logger.info("=========生成盘片的文件名==="+fileName);
		}else{
			// 建行   CCB_K_**********_++++++++.TXT
			fileName = "CCB_K"+ "_T9" + 
			            RedisSericalnoGenTool.genCCBCDiskFileSeqNo() + "_" + 
					    DateKit.toStr(new Date(), DateKit.datePattern).replaceAll("-", "");
			logger.info("=========生成盘片的文件名==="+fileName);
		}		
		return fileName + ".TXT" ;
	}
	
	/**
	 * 获取代收付标志位
	 * @param document_type
	 * @return
	 */
	public String getSfFlag(int document_type) {
		return document_type == WebConstant.DocumentType.FB.getKey() ? "F" : "S";		
	}
	
	/**
	 * 6位递增数
	 * @param id
	 * @param len
	 * @return
	 */
	public String getCode(int id,int len){

		String t = String.valueOf(id);

		while(t.length()<len)

		t="0"+t;

		return t;

		}

    /**
     * Txt文档上传
     * @param sheetName
     * @param genVelo
     * @throws IOException
     */
	public void diskSave(String sheetName , String genVelo) throws IOException  {
		DiskDownLoadSection diskDownLoadSection = DiskDownLoadSection.getInstance();
	    String path = diskDownLoadSection.getPath();
	    //path = "D:\\";
		File f = new File(path + sheetName);
		logger.info("==========文件路径===="+path + sheetName);
		if (!f.exists()) {
			boolean flag = f.createNewFile();
			logger.info("=============创建新文件结果==="+flag);
		}
		FileOutputStream fos = null  ;
		BufferedWriter writer = null ; 
		try {
			fos= new FileOutputStream(f);  
			writer = new BufferedWriter(new OutputStreamWriter(fos, "utf-8"));  
			writer.append(genVelo);	
		} catch (Exception e) {
			logger.error("============TXT文件长传导服务器失败");
		}finally{
			if(null != writer) {
				writer.flush();  
				writer.close();  				
			}
            if(null != fos) {
            	fos.close();  					
			}
		}
	}

    /**
     * 异步新线程生成txt格式文档
     * @param pay_master_id
     * @param pay_id
     * @param document_moudle
     * @param fileName
     * @param document_type
     * @param configs_tail
     * @return
     * @throws IOException
     * @throws BusinessException 
     */
	public String diskDownLoadNewThread(Long pay_master_id,final Long pay_id,Integer document_moudle, final String fileName, Integer document_type, Record configs_tail) throws IOException, BusinessException {
		logger.info("==============生成txt样式的盘片");
		// .mv文件均放在common包的resource文件下
		String filePath = String.valueOf(document_moudle)+ document_type +".vm";
		String total_titleNames = "";
		String datail_titleNames = "";
		Map<String, Object> map = new HashMap<>();
		final Record totalOffer = new Record();
		final List<Record> detailOffers = new ArrayList<>();
		List<Map<String,Object>> datail_list = new ArrayList<>();

    	List<Record> configs = Db.find(Db.getSql("disk_downloading.findTotalConfig"),document_type,document_moudle);
    	// TODO  pay_batch_total  根据批次号,查询批次id
    	Record record = Db.findFirst(Db.getSql("disk_downloading.findToatlInfo"),pay_id);
    	
    	//仅仅查一张表是不符合所有渠道的盘片需要的汇总信息的字段展示,
    	//多的只需要继续向batchRecord.get(0)内封装即可,初始化的表中也相应添加field_i
    	constructTotalMapService.constructTotalRecord(map,record,document_moudle);
    	
    	if(configs == null || configs.size() == 0 ){
        	logger.info("=========此类型盘片不需要汇总信息===");	
        }else{
        	//汇总信息的第一个字段 S , F
        	map.put("sf_flag", this.getSfFlag(document_type));
        	Record total_rec_config = configs.get(0);
        	if(StringUtils.isNotBlank(total_rec_config.getStr("title"))){
        		logger.info("======抬头非空");
        		map.put("total_title", total_rec_config.getStr("title"));
        	}
        	for (int i = 1; i <= total_rec_config.getColumns().size(); i++) {
				if(StringUtils.isNotBlank(total_rec_config.getStr("field_"+i))){
				    if(total_titleNames != null && !"".equals(total_titleNames)){
                        total_titleNames += ("," + total_rec_config.getStr("field_"+i));
                    }else{
                        total_titleNames = total_rec_config.getStr("field_"+i);
                    }
				}else {
					break ;
				}
			}
        	logger.info("===========汇总total_titleNames======" + total_titleNames);
        	for (int i = 0; i < total_titleNames.split(",").length; i++) {
				map.put(total_titleNames.split(",")[i], record.get(total_titleNames.split(",")[i]));
			}
        }
    	
    	//封装 offerDocument_total 表
    			totalOffer.set("channel_id", record.get("channel_id"))
    			          .set("batch_id", pay_id)
    			          .set("file_name", fileName)
    			          .set("download_count", 1)
    			          .set("create_on", new Date())
    			          .set("total_amount", record.get("pay_total_amount"))
    			          .set("total_num", record.get("pay_total_num"))
    			          .set("create_on", new Date());
   	
        //默认是一定有这个配置存在
		if(null != configs_tail){			
			//详情的抬头处理
			if(StringUtils.isNotBlank(configs_tail.getStr("title"))){
				map.put("detail_title", configs_tail.getStr("title"));
			}		
			for (int i = 1; i <= configs_tail.getColumns().size(); i++) {
				if(StringUtils.isNotBlank(configs_tail.getStr("field_"+i))){
                    if(datail_titleNames != null && !"".equals(datail_titleNames)){
                        datail_titleNames += ("," +   configs_tail.getStr("field_"+i));
                    }else{
                        datail_titleNames = configs_tail.getStr("field_"+i);
                    }
				}else{
					break ;
				}
			}
			logger.info("===========详情datail_titleNames======" + datail_titleNames);
			Record pay_total = Db.findById("pay_batch_total", "id", pay_id);
			List<Record> detailRecords = null ;
			//根据 batch_id 查询 pay_batch_detail 表, 存在多条  base_id = batch_id
			if(0 == pay_total.getInt("source_sys")) {
				logger.info("===============盘片来源系统LA");
				detailRecords = Db.find(Db.getSql("disk_downloading.findDatailInfo"), pay_id);	       	          
			}else{
				logger.info("===============盘片来源系统EBS");
				detailRecords = Db.find(Db.getSql("disk_downloading.findEBSDatailInfo"), pay_id);	       	          
			}
	        //List<Record> detailRecords = Db.find(Db.getSql("disk_downloading.findDatailInfo"), pay_id);	       	          
	        for (int i = 0; i < detailRecords.size(); i++) {	        	
	        	Record detailRecord = new Record();
	        	detailRecord.set("package_seq", detailRecords.get(i).get("package_seq"))
	        	            .set("legal_id", detailRecords.get(i).get("legal_id"))
	        	            .set("amount", detailRecords.get(i).get("amount"))
	        	            .set("recv_acc_no", detailRecords.get(i).get("recv_acc_no"))
	        	            .set("recv_acc_name", detailRecords.get(i).get("recv_acc_name"))
	        	            .set("recv_cert_type", detailRecords.get(i).get("recv_cert_type"))
	        	            .set("recv_cert_code", detailRecords.get(i).get("recv_cert_code"))
	        	            .set("recv_bank_name", detailRecords.get(i).get("recv_bank_name"));
	        	            
	        	detailOffers.add(detailRecord);
	        	
	        	Map<String, Object> detail_map = new HashMap<>();
	        	
	        	//详情的record同样也需要额外封装一些盘片需要的详情信息
	        	constructDetailMapService.constructDetailRecord(detail_map,detailRecords.get(i),document_moudle);	        	
	        	//detail_map.put("serialnum", detailRecords.get(i).get("serialnum"));
	        	for(int j = 0; j < datail_titleNames.split(",").length; j++) {
	        		if("recv_acc_no".equals(datail_titleNames.split(",")[j])) {
	        			SymmetricEncryptUtil util = SymmetricEncryptUtil.getInstance();
	        			String str = detailRecords.get(i).getStr(datail_titleNames.split(",")[j]);
	        			String acc_no = util.decryptToStr(str);
	        			detail_map.put(datail_titleNames.split(",")[j],acc_no );	        			
	        		}else {
	        			detail_map.put(datail_titleNames.split(",")[j], detailRecords.get(i).getStr(datail_titleNames.split(",")[j]));	        			
	        		}
				}
	        	datail_list.add(detail_map);
			}
	        map.put("details", datail_list);
	        final String genVelo  = VelocityUtil.genVelo(filePath, map);		        
	        logger.info("===============此网盘第一次下载");
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
	        			boolean batchsave = ArrayUtil.checkDbResult(batchSave);
	        			logger.info("===========保存pay_offerDocument_detail表====="+batchsave);
	        			if(batchsave) {	      
	        					//往服务器上写文件
	        					try {
									diskSave(fileName, genVelo);
									return true ;
								} catch (IOException e) {
									logger.error("===========往服务器上上传.txt文件失败");
									e.printStackTrace();
									return false ;
								}       				
	        			  }	        			
	        		}
	        		return false;
	        	}
	        });
	        	if(!flag){
	        		throw new DbProcessException("============Txt网盘信息入库失败");
	        	}	        	
	        	return  genVelo ;
		}else{
			throw new ReqDataException("============此通道盘片类型初始化存在问题");
		}
	}
	
}
