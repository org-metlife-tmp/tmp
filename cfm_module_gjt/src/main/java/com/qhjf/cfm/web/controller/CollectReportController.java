package com.qhjf.cfm.web.controller;

import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.log.Log;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.web.plugins.jwt.Auth;
import com.qhjf.cfm.web.plugins.log.LogbackLog;
import com.qhjf.cfm.web.service.CollectReportService;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CollectReportController extends CFMBaseController {

    private static final Log log = LogbackLog.getLog(CollectReportController.class);

    private CollectReportService service = new CollectReportService();
    
    /**
      *  
            *  查看归集通报表列表
     * @throws ReqDataException 
      * 
      */
    @Auth(hasForces = {"GJReport"})
    public void  reports() throws ReqDataException {
    	log.info("进入归集通报表列表查询============");
    	Record record = getRecordByParamsStrong();
        int pageNum = getPageNum(record);
        int pageSize = getPageSize(record);
        try {
        	Page<Record> page = service.findList(pageNum,pageSize,record);
        	//获取金额统计
        	Record sum = service.collectSum(record);
        	renderOkPage(page, sum);       	
        }catch(BusinessException e){
        	 log.error("获取归集通报表列表失败！", e);
             renderFail(e);
        }
    }
    
    
    /**
     *  
           *  查看归集通报表列表_图表
    * @throws ReqDataException 
     * 
     */
   @Auth(hasForces = {"GJReport"})
   public void  reportsChart() throws ReqDataException {
   	log.info("进入归集通报表列表查询============");
   	Record record = getRecordByParamsStrong();
    try {
       List<Record> page = service.findChartList(record);
       	//获取金额统计
       	Record sum = service.collectSum(record);
       	BigDecimal totcalAmount = TypeUtils.castToBigDecimal(sum.get("total_amount"));     	
       	if(page != null && page.size() > 0){
       		for (Record rec : page) {
       			BigDecimal amount = TypeUtils.castToBigDecimal(rec.get("collect_amount"));
       			if(BigDecimal.ZERO.compareTo(totcalAmount) == 0){
       				rec.set("amount_ration", "0");
       				rec.set("amount_ration_else", "0");
       				rec.set("collect_amount_else", 0);
       			}else {   
       				BigDecimal amount_else = totcalAmount.subtract(amount);
       			    String amount_ration = String.valueOf(amount.divide(totcalAmount, 2, BigDecimal.ROUND_HALF_UP).
       				                              multiply(new BigDecimal(100)).
       				                              setScale(2, BigDecimal.ROUND_HALF_UP))+"%";
       			    String amount_ration_else = String.valueOf(amount_else.divide(totcalAmount, 2, BigDecimal.ROUND_HALF_UP).
                            multiply(new BigDecimal(100)).
                            setScale(2, BigDecimal.ROUND_HALF_UP))+"%";
       				rec.set("amount_ration", amount_ration);
       				rec.set("amount_ration_else", amount_ration_else);
       				rec.set("collect_amount_else", amount_else);
       			}
			}
       	}
       	//添加比例	
       	sum.set("list", page);
       	renderOk(sum);       	
       }catch(BusinessException e){
       	 log.error("获取归集通报表列表失败！", e);
         renderFail(e);
       }
   }
   

    
    
    @Override
    protected void renderOkPage(Page page, Record ext) {

        Map<String, Object> root = new HashMap<>();
        root.put("optype", getOptype());
        root.put("total_line", page.getTotalRow());
        root.put("total_page", page.getTotalPage());

        root.put(PAGE_SIZE, page.getPageSize());
        root.put(PAGE_NUM, page.getPageNumber());
        root.put("ext", ext == null ? new Record() : ext);
        //TODO  merger dbt and jyyet
        //总条数
        root.put("total_num", ext == null ? 0 : TypeUtils.castToInt(ext.get("total_num") == null ? 0 : ext.get("total_num")));
        //总金额
        root.put("total_amount", ext == null ? 0 : TypeUtils.castToBigDecimal(ext.get("total_amount") == null ? 0 : ext.get("total_amount")));
        //成功金额
        root.put("success_amount", ext == null ? 0 : TypeUtils.castToBigDecimal(ext.get("success_amount") == null ? 0 : ext.get("success_amount")));

        
        //开始封装金额占比
        List<Record> list = page.getList();
        if(null != list && list.size() > 0){
        	for (Record record : list) {
        		BigDecimal sumAmount = TypeUtils.castToBigDecimal(root.get("total_amount"));
        		BigDecimal collect_amount = TypeUtils.castToBigDecimal(record.get("collect_amount"));
        		if(sumAmount.compareTo(new BigDecimal("0")) == 0){
        			record.set("amount_ration", "0");
        		}else {        			
        			BigDecimal divide = collect_amount.divide(sumAmount, 4, BigDecimal.ROUND_HALF_UP);
        			String ration = String.valueOf(divide.multiply(new BigDecimal(100)).setScale(2, BigDecimal.ROUND_HALF_UP))+"%";
        			record.set("amount_ration", ration);
        		}
			}
        }
        root.put("data", page.getList());
        
        renderJson(root);
    }
}
