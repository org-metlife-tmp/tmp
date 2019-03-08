package com.qhjf.cfm.web.webservice.la.queue;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.utils.XmlTool;
import com.qhjf.cfm.web.config.DDHLAConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.service.CheckVoucherService;
import com.qhjf.cfm.web.webservice.la.LaCallbackBean;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;


/**
 * 闂冪喎鍨☉鍫ｅ瀭閼帮拷
 * @author yunxw
 *
 */
public class LaConsumerQueue implements Runnable{
	
	private static Logger log = LoggerFactory.getLogger(LaConsumerQueue.class);
	private static DDHLAConfigSection config = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLA);

	@Override
	public void run() {
		while(true){
			try{
				LaQueueBean queueBean = null;
				queueBean = LaQueue.getInstance().getQueue().take();
				log.debug("LA批付回调核心系统web url={}", StringKit.removeControlCharacter(config.getUrl()));
				
				ServiceClient sc = new ServiceClient();
				Options opts = new Options();   
				EndpointReference end = new EndpointReference(config.getUrl());   
				opts.setTo(end); 
				opts.setAction("http://eai.metlife.com/ESBWebEntry/ProcessMessage");
				sc.setOptions(opts);
				OMElement res = sc.sendReceive(queueBean.getoMElement());
				String laBean = StringKit.removeControlCharacter(res.toString());
				log.debug("response={}",laBean);
				JSONObject json = XmlTool.documentToJSONObject(res.toString());
				JSONObject rec = json.getJSONArray("ESBEnvelopeResult").getJSONObject(0).getJSONArray("MsgBody").getJSONObject(0).getJSONArray("PMTUPDO_REC").getJSONObject(0);
				String status = rec.getString("STATUS");
				if(status.equals("0")){
					processSuccess(json);
				}else{
					processFail(queueBean);
				}
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
		}
		
	}

	
	private void processFail(LaQueueBean queueBean){
		log.debug("LA接收回调数据失败");
		List<LaCallbackBean> beans = queueBean.getBeans();
		for(LaCallbackBean bean : beans){
			try{
				Record setRecord = new Record().set("la_callback_status", WebConstant.SftCallbackStatus.SFT_CALLBACK_F.getKey())
	    				.set("la_callback_err_message", "LA接收失败")
	    				.set("la_callback_resp_time", new Date());
	    		Record whereRecord = new Record().set("pay_code", bean.getReqnno());
	    		if(CommonService.updateRows("la_origin_pay_data", setRecord, whereRecord) != 1){
	    			log.debug("LA回调接口回写数据库失败:{}",StringKit.removeControlCharacter(bean.getReqnno()));
	    			continue;
	    		};
			}catch(Exception e){
				log.debug("LA回调接口回写数据库失败:{}",StringKit.removeControlCharacter(bean.getReqnno()));
				e.printStackTrace();
				continue;
			}
			
		}
	}
	
	private void processSuccess(JSONObject resp){
		JSONArray array = resp.getJSONArray("ESBEnvelopeResult").getJSONObject(0)
				.getJSONArray("MsgBody").getJSONObject(0).getJSONArray("PMTUPDO_REC").getJSONObject(0)
				.getJSONArray("ADDITIONAL_FIELDS").getJSONObject(0).getJSONArray("PMTOUT");
		
		for(int i = 0;i<array.size();i++){
			final JSONObject json = array.getJSONObject(i);
			boolean flag = Db.tx(new IAtom() {
	            @Override
	            public boolean run() throws SQLException {
	            	String processStatus = json.getString("STATUS");
	    			String payCode = json.getString("REQNNO");
	    			if (null == payCode || "".equals(payCode.trim())) {
	    				log.debug("LA响应回写原始数据，payCode为空，节点<PMTOUT>={}", StringKit.removeControlCharacter(json.toJSONString()));
						return true;
					}
	    			
	    			Record whereRecord = new Record().set("pay_code", payCode);
	    			if(processStatus.equals("SC")){
	    				Record setRecord = new Record().set("la_callback_status", WebConstant.SftCallbackStatus.SFT_CALLBACK_S.getKey())
	    						.set("la_callback_resp_time", new Date());
	    				if(CommonService.updateRows("la_origin_pay_data", setRecord, whereRecord)==1){
	    					Record record = Db.findFirst(Db.getSql("webservice_la_cfm.getStatusByPayCode"), payCode);
	    					if(record.getInt("tmp_status") == WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_S.getKey()){
								Record payLegalRecord = Db.findFirst(Db.getSql("webservice_la_cfm.getPayLegalByPayCode"), payCode);
								try {
									CheckVoucherService.plfLaEbsBackCheckVoucher("LA",
											payLegalRecord,
											CommonService.getPeriodByCurrentDay(new Date())
											);
								} catch (BusinessException e) {
									log.error("LA响应回写原始数据，LA接收成功，生成凭证失败！PMTOUT={}", StringKit.removeControlCharacter(json.toJSONString()));
									e.printStackTrace();
									return false;
								}
	    					}else {
								log.debug("LA响应回写原始数据，支付失败，不生成凭证；payCode={}", StringKit.removeControlCharacter(payCode));
							}
	    					return true;
	    				}else {
							log.error("LA响应回写原始数据，更新la_origin_pay_data失败！payCode={}",  StringKit.removeControlCharacter(payCode));
						}
	    			}else{
	    				String errField = json.getString("FLDNAM");
	    				String errCode = json.getString("ERRCODE");
	    				String errMessage = json.getString("ERRMESS");
	    				Record setRecord = new Record().set("la_callback_status", WebConstant.SftCallbackStatus.SFT_CALLBACK_F.getKey())
	    						.set("la_callback_err_field", errField)
	    						.set("la_callback_err_code", errCode)
	    						.set("la_callback_err_message", errMessage)
	    						.set("la_callback_resp_time", new Date());
	    				if(CommonService.updateRows("la_origin_pay_data", setRecord, whereRecord)==1){
	    					return true;
	    				}
	    			}
	    			return false;
	            }
	            });
			
			if (!flag) {
				log.error("LA响应回写原始数据失败；节点<PMTOUT>={}",  StringKit.removeControlCharacter(json.toJSONString()));
			}
		}
		

		
	}

	

	
	
}
