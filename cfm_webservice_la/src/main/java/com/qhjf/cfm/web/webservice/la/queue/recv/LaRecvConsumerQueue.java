package com.qhjf.cfm.web.webservice.la.queue.recv;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.CommKit;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.utils.XmlTool;
import com.qhjf.cfm.web.config.DDHLARecvConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.service.CheckVoucherService;
import com.qhjf.cfm.web.webservice.la.logger.recv.BatchRecvLogger;
import com.qhjf.cfm.web.webservice.la.recv.LaRecvCallbackBean;
import org.apache.axiom.om.OMElement;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;


/**
 * @author cht
 *
 */
public class LaRecvConsumerQueue implements Runnable{
	
	private static Logger track = LoggerFactory.getLogger(BatchRecvLogger.class);
	private static Logger log = LoggerFactory.getLogger(LaRecvConsumerQueue.class);
	private static DDHLARecvConfigSection config = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLaRecv);
	private static final String LA_ORIGIN = "la_origin_recv_data";

	@Override
	public void run() {
		while(true){
			
			try{
				log.debug("LA pishou queue size = {}", LaRecvQueue.getInstance().getQueue().size());
				LaRecvQueueBean queueBean = null;
				queueBean = LaRecvQueue.getInstance().getQueue().take();
				log.debug("LA批收回调核心系统webservice url="+config.getUrl());
				String send = StringKit.removeControlCharacter(queueBean.getoMElement() != null ? queueBean.getoMElement().toString() : "请求报文为空");
				track.debug("LA批收send={}",send);
				
				ServiceClient sc = new ServiceClient();
//				sendSkipEAI(sc);
				sendEAI(sc);
				
				OMElement res = sc.sendReceive(queueBean.getoMElement());
				CommKit.debugPrint(track,"LA批收回调response={}",res.toString());


				JSONObject json = XmlTool.documentToJSONObject(res.toString());
				
//				String status = getStatusSkipEAI(json);
				String status = getStatusEAI(json);
				
				if(status.equals("0")){
					processSuccess(json, queueBean);
				}else{
					processFail(queueBean);
				}
			}catch(Exception e){
				e.printStackTrace();
				log.error(e.getMessage());
				continue;
			}
		}
		
	}
	
	private void sendEAI(ServiceClient sc) throws Exception{
		Options opts = new Options();
		EndpointReference end = new EndpointReference(config.getUrl());
		opts.setTo(end); 
		opts.setAction("http://eai.metlife.com/ESBWebEntry/ProcessMessage");
		sc.setOptions(opts);
	}
	private void sendSkipEAI(ServiceClient sc) throws Exception {
		Options opts = new Options();   
		EndpointReference end = new EndpointReference(config.getUrl());   
		opts.setTo(end); 
		opts.setAction("DRNADD");
		sc.setOptions(opts);
	}
	private String getStatusEAI(JSONObject json) {
		try {
			JSONObject rec = json.getJSONArray("ESBEnvelopeResult").getJSONObject(0)
					.getJSONArray("MsgBody").getJSONObject(0)
					.getJSONArray("DRNADDO_REC").getJSONObject(0);
			return rec.getString("STATUS");
		} catch (Exception e) {
			CommKit.errorPrint(log,"解析LA批收回调响应状态失败(ESBEnvelopeResult-MsgBody-DRNADDO_REC-STATUS)={}",json.toJSONString());
		}
		return "";
	}
	private String getStatusSkipEAI(JSONObject json){
		return json.getString("STATUS");
	}
	private JSONArray getRecordListEAI(JSONObject json){
		return json.getJSONArray("ESBEnvelopeResult").getJSONObject(0)
				.getJSONArray("MsgBody").getJSONObject(0)
				.getJSONArray("DRNADDO_REC").getJSONObject(0)
				.getJSONArray("ADDITIONAL_FIELDS").getJSONObject(0)
				.getJSONArray("DRNOUTRECS");
	}
	private JSONArray getRecordListSkipEAI(JSONObject json){
		return json.getJSONArray("ADDITIONAL_FIELDS").getJSONObject(0).getJSONArray("DRNOUTRECS");
	}

	
	private void processFail(LaRecvQueueBean queueBean){
		log.debug("LA批收推送核心系统，核心系统接收失败！");
		List<LaRecvCallbackBean> beans = queueBean.getBeans();
		for(LaRecvCallbackBean bean : beans){
			log.debug("更新原始数据pay_code={}为回调失败！", bean.getDdderef());
			try{
				Record setRecord = new Record()
						.set("la_callback_status", WebConstant.SftCallbackStatus.SFT_CALLBACK_F.getKey())
	    				.set("la_callback_err_message", "LA接收失败")
	    				.set("la_callback_resp_time", new Date());
	    		Record whereRecord = new Record()
	    				.set("pay_code", bean.getDdderef());
	    		if(CommonService.updateRows(LA_ORIGIN, setRecord, whereRecord) != 1){
	    			log.error("LA批收回调接口回写数据库失败:"+bean.getDdderef());
	    			continue;
	    		};
			}catch(Exception e){
				log.error("LA批收回调接口回写数据库失败:"+bean.getDdderef());
				e.printStackTrace();
				continue;
			}
			
		}
	}
	
	private void processSuccess(JSONObject resp, LaRecvQueueBean queueBean){
		log.debug("LA批收推送核心系统，核心系统接收成功！");
		
//		JSONArray array = getRecordListSkipEAI(resp);
		JSONArray array = getRecordListEAI(resp);
		
		for(int i = 0;i<array.size();i++){
			
			final JSONObject json = array.getJSONObject(i);
			boolean flag = Db.tx(new IAtom() {
	            @Override
	            public boolean run() throws SQLException {
	            	String payCode = json.getString("DDDEREF");
	            	String processStatus = json.getString("STATUS");
	    			String receipt = json.getString("RECEIPT");
	    			
	    			if (StringUtils.isBlank(processStatus)) {
	    				return true;
					}
	    			
	    			if (StringUtils.isBlank(payCode)) {
	    				log.error("LA批收回调报文，传回的DDDEREF字段为空！跳过该笔处理");
						return true;
					}
	    			
	    			Record whereRecord = new Record().set("pay_code", payCode);
	    			
	    			if(processStatus.equalsIgnoreCase("SC")){
	    				Record setRecord = new Record()
	    						.set("la_callback_status", WebConstant.SftCallbackStatus.SFT_CALLBACK_S.getKey())
	    						.set("la_callback_resp_time", new Date())
	    						.set("receipt", receipt);
	    				
	    				log.debug("LA批收回调，回写原始数据表，pay_code={}", payCode);
	    				if(CommonService.updateRows(LA_ORIGIN, setRecord, whereRecord)==1){
	    					Record record = Db.findFirst(Db.getSql("webservice_la_recv_cfm.getStatusByPayCode"), payCode);
	    					if(record.getInt("tmp_status") == WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_S.getKey()){
								Record payLegalRecord = Db.findFirst(Db.getSql("webservice_la_recv_cfm.getPayLegalByPayCode"), payCode);
								try {
									log.info("LA中批量收paycode为【"+payCode+"】的数据回调成功，生成凭证---begin");
									CheckVoucherService.plsLaBackCheckVoucher("LA",
											payLegalRecord
											);
									log.info("LA中批量收paycode为【"+payCode+"】的数据回调成功，生成凭证---end");
								} catch (BusinessException e) {
									log.error("LA批收响应回写原始数据，LA接收成功，生成凭证失败！响应数据节点DRNOUTRECS={}", json);
									e.printStackTrace();
									return false;
								}
	    					}else {
								log.debug("LA响应回写原始数据，支付失败，不生成凭证；payCode={}", payCode);
							}
	    					return true;
	    				}else {
							log.error("LA响应回写原始数据，更新la_origin_recv_data失败！payCode={}", payCode);
						}
	    			}else{
	    				String errField = json.getString("FLDNAM");
	    				String errCode = json.getString("ERRCODE");
	    				String errMessage = json.getString("ERRMESS");
	    				Record setRecord = new Record()
	    						.set("la_callback_status", WebConstant.SftCallbackStatus.SFT_CALLBACK_F.getKey())
	    						.set("la_callback_err_field", errField)
	    						.set("la_callback_err_code", errCode)
	    						.set("la_callback_err_message", errMessage)
	    						.set("la_callback_resp_time", new Date());
	    				if(CommonService.updateRows(LA_ORIGIN, setRecord, whereRecord)==1){
	    					return true;
	    				}
	    			}
	    			return false;
	            }
	            });
			
			if (!flag) {
				log.error("LA批收响应回写原始数据失败；响应数据节点<DRNOUTRECS>=", json);
			}
		}
	}
}
