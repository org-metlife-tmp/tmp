package com.qhjf.cfm.web.service;


import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.DbProcessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.ArrayUtil;
import com.qhjf.cfm.utils.CommonService;
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

public class RecvTxtDiskSendingService {
	
    private static final Logger logger = LoggerFactory.getLogger(RecvTxtDiskSendingService.class);
    ConstructTotalMapService constructTotalMapService = new ConstructTotalMapService();
    ConstructDetailMapService constructDetailMapService = new ConstructDetailMapService();
   
    TxtDiskSendingService  txtDiskSendingService = new TxtDiskSendingService();
    
	
	/**
	 * 获取代收付标志位
	 * @param document_type
	 * @return
	 */
	public String getSfFlag(int document_type) {
		return document_type == WebConstant.DocumentType.FB.getKey() ? "F" : "S";		
	}
	

	
	
	
	/**
     * @异步新线程生成txt格式文档
     * @param documentType
     * @param userInfo
     * @param fileName 
     * @param fileName
     * @param document_type 
     * @param configs_tail 
     * @throws IOException 
	 * @throws BusinessException 
     */
	public String diskDownLoadNewThread(Long pay_master_id,final Long pay_id,Integer document_moudle, final String fileName, Integer document_type, Record configs_tail) throws IOException, BusinessException {
		logger.info("==============生成txt样式的盘片");
		Record recv_batch_total_master = Db.findById("recv_batch_total_master", "id", pay_master_id);
		Record channel_setting = Db.findById("channel_setting", "id", recv_batch_total_master.get("channel_id"));
		String channel_code = channel_setting.getStr("channel_code");		
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
    	Record record = Db.findFirst(Db.getSql("recv_disk_downloading.findToatlInfo"),pay_id);
    	
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
						total_titleNames += "," + total_rec_config.getStr("field_"+i);
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
    			          .set("total_amount", record.get("recv_total_amount"))
    			          .set("total_num", record.get("recv_total_num"))
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
						datail_titleNames += "," +   configs_tail.getStr("field_"+i) ;
					}else{
						datail_titleNames = configs_tail.getStr("field_"+i);
					}
				}else{
					break ;
				}
			}
			logger.info("===========详情datail_titleNames======" + datail_titleNames);
			
			//根据 batch_id 查询 pay_batch_detail 表, 存在多条  base_id = batch_id
	        List<Record> detailRecords = Db.find(Db.getSql("recv_disk_downloading.findDatailInfo"), pay_id);	       	          
	        for (int i = 0; i < detailRecords.size(); i++) {	        	
	        	Record detailRecord = new Record();
	        	detailRecord.set("package_seq", detailRecords.get(i).get("package_seq"))
	        	            .set("legal_id", detailRecords.get(i).get("legal_id"))
	        	            .set("amount", detailRecords.get(i).get("amount"))
	        	            .set("pay_acc_no", detailRecords.get(i).get("pay_acc_no"))
	        	            .set("pay_acc_name", detailRecords.get(i).get("pay_acc_name"))
	        	            .set("pay_cert_type", detailRecords.get(i).get("pay_cert_type"))
	        	            .set("pay_cert_code", detailRecords.get(i).get("pay_cert_code"))
	        	            .set("pay_bank_name", detailRecords.get(i).get("pay_bank_name"));
	        	            
	        	detailOffers.add(detailRecord);
	        	
	        	Map<String, Object> detail_map = new HashMap<>();
	        	
	        	//详情的record同样也需要额外封装一些盘片需要的详情信息
	        	constructDetailMapService.constructDetailRecord(detail_map,detailRecords.get(i),document_moudle,channel_code,0);	        	
	        	//detail_map.put("serialnum", detailRecords.get(i).get("serialnum"));
	        	for(int j = 0; j < datail_titleNames.split(",").length; j++) {
	        		if("pay_acc_no".equals(datail_titleNames.split(",")[j])) {
	        			SymmetricEncryptUtil util = SymmetricEncryptUtil.getInstance();
	        			String str = detailRecords.get(i).getStr(datail_titleNames.split(",")[j]);
	        			String acc_no = util.decryptToStr(str);
	        			detail_map.put(datail_titleNames.split(",")[j],acc_no );	        			
	        		}else {
	        			detail_map.put(datail_titleNames.split(",")[j], detailRecords.get(i).getStr(datail_titleNames.split(",")[j]));	        			
	        		}
				}
				//B0渠道, 且银行大类为 303 的	中国光大银行 ,15/16行展示 开户证件类型/证件号 .其余B0渠道的银行都不展示
				if("B0".equalsIgnoreCase(channel_code)) {
					if(!"303".equals(TypeUtils.castToString(detail_map.get("pay_bank_type")))) {
						detail_map.remove("pay_cert_type");
						detail_map.remove("pay_cert_code");
					}
				}	        	        	
	        	datail_list.add(detail_map);
			}
	        map.put("details", datail_list);
	        final String genVelo  = VelocityUtil.genGBKVelo(filePath, map);		        
	        logger.info("===============此网盘第一次下载");
	        boolean flag = Db.tx(new IAtom() {		
	        	@Override
	        	public boolean run() throws SQLException {
	        		boolean save = Db.save("recv_offerDocument_total","id", totalOffer);
	        		if(save){
	        			logger.info("==================recv_offerDocument_total表插入成功");
	        			Integer id = totalOffer.getInt("id");
	        			for (int i = 0; i < detailOffers.size(); i++) {
	        				detailOffers.get(i).set("base_id", id);
	        			}
	        			int[] batchSave = Db.batchSave("recv_offerDocument_detail", detailOffers, 1000);
	        			boolean batchsave = ArrayUtil.checkDbResult(batchSave);
	        			logger.info("===========保存recv_offerDocument_detail表====="+batchsave);
	        			if(batchsave) {	      
	        					//往服务器上写文件
	        					try {
	        						txtDiskSendingService.diskSave(fileName, genVelo);
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
    	boolean updateDownCount = CommonService.update("recv_offerDocument_total",
    			new Record().set("download_count", offer.getInt("download_count")+1),
                new Record().set("batch_id", offer.get("batch_id")));
   	    logger.info("更新表recv_offerDocument_total下载次数结果==="+updateDownCount);
   	    logger.info("===============子批次表状态更新为已发送未回盘");
   	    boolean updateStatus = CommonService.update("recv_batch_total", new Record().set("send_on", new Date()).set("send_user_name", user_record.get("name"))
				.set("service_status", WebConstant.SftCheckBatchStatus.FSWHP.getKey()) //TODO 已发送未回盘
				, new Record().set("id", pay_id));
        
   	    return byteArray ;

	}
	
}
