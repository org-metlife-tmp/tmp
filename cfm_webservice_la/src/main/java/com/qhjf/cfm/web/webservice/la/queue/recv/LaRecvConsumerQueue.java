package com.qhjf.cfm.web.webservice.la.queue.recv;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.XmlTool;
import com.qhjf.cfm.web.config.DDHLARecvConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.service.CheckVoucherService;
import com.qhjf.cfm.web.webservice.la.recv.LaRecvCallbackBean;
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
 * @author cht
 *
 */
public class LaRecvConsumerQueue implements Runnable{
	
	private static Logger log = LoggerFactory.getLogger(LaRecvConsumerQueue.class);
	private static DDHLARecvConfigSection config = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLaRecv);

	@Override
	public void run() {
		while(true){
			try{
				LaRecvQueueBean queueBean = null;
				queueBean = LaRecvQueue.getInstance().getQueue().take();
				log.debug("LA批收回调核心系统webservice url="+config.getUrl());
				
				ServiceClient sc = new ServiceClient();
				Options opts = new Options();
				EndpointReference end = new EndpointReference(config.getUrl());
//				EndpointReference end = new EndpointReference("http://10.164.26.43/esbwebentry/ESBWebEntry.asmx?wsdl");
				opts.setTo(end); 
				opts.setAction("http://eai.metlife.com/ESBWebEntry/ProcessMessage");
				sc.setOptions(opts);
				OMElement res = sc.sendReceive(queueBean.getoMElement());
				
				log.debug("response="+res.toString().replaceAll("\\r|\\n", ""));
				JSONObject json = XmlTool.documentToJSONObject(res.toString());
				
				JSONObject rec = json.getJSONArray("ESBEnvelopeResult").getJSONObject(0)
						.getJSONArray("MsgBody").getJSONObject(0)
						.getJSONArray("DRNADDO_REC").getJSONObject(0);
				String status = rec.getString("STATUS");
				if(status.equals("0")){
					processSuccess(json, queueBean);
				}else{
					processFail(queueBean);
				}
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
		}
		
	}

	
	private void processFail(LaRecvQueueBean queueBean){
		log.debug("LA批收推送核心系统，核心系统接收失败！");
		List<LaRecvCallbackBean> beans = queueBean.getBeans();
		for(LaRecvCallbackBean bean : beans){
			try{
				Record setRecord = new Record()
						.set("la_callback_status", WebConstant.SftCallbackStatus.SFT_CALLBACK_F.getKey())
	    				.set("la_callback_err_message", "LA接收失败")
	    				.set("la_callback_resp_time", new Date());
	    		Record whereRecord = new Record()
	    				.set("pay_code", bean.getDdderef());
	    		if(CommonService.updateRows("la_origin_recv_data", setRecord, whereRecord) != 1){
	    			log.debug("LA批收回调接口回写数据库失败:"+bean.getDdderef());
	    			continue;
	    		};
			}catch(Exception e){
				log.debug("LA批收回调接口回写数据库失败:"+bean.getDdderef());
				e.printStackTrace();
				continue;
			}
			
		}
	}
	
	private void processSuccess(JSONObject resp, LaRecvQueueBean queueBean){
		log.debug("LA批收推送核心系统，核心系统接收成功！");
		
		List<LaRecvCallbackBean> beans = queueBean.getBeans();
		
		JSONArray array = resp.getJSONArray("ESBEnvelopeResult").getJSONObject(0)
				.getJSONArray("MsgBody").getJSONObject(0)
				.getJSONArray("DRNADDO_REC").getJSONObject(0)
				.getJSONArray("ADDITIONAL_FIELDS").getJSONObject(0)
				.getJSONArray("DRNOUTRECS");
		
		for(int i = 0;i<array.size();i++){
			final LaRecvCallbackBean bean = beans.get(i);
			final String payCode = bean.getDdderef();
			
			final JSONObject json = array.getJSONObject(i);
			boolean flag = Db.tx(new IAtom() {
	            @Override
	            public boolean run() throws SQLException {
	            	String processStatus = json.getString("STATUS");
	    			String receipt = json.getString("RECEIPT");
	    			
	    			/*if (null == payCode || "".equals(payCode.trim())) {
	    				log.debug("LA批收响应回写原始数据，payCode为空，响应数据节点<DRNOUTRECS>=", json);
						return true;
					}*/
	    			if (null == processStatus || "".equals(processStatus.trim())) {
	    				if (null == receipt || "".equals(receipt.trim())) {
							return true;
						}
					}
	    			
	    			Record whereRecord = new Record().set("pay_code", payCode);
	    			
	    			if(processStatus.equalsIgnoreCase("SC")){
	    				Record setRecord = new Record()
	    						.set("la_callback_status", WebConstant.SftCallbackStatus.SFT_CALLBACK_S.getKey())
	    						.set("la_callback_resp_time", new Date())
	    						.set("receipt", receipt);
	    				
	    				if(CommonService.updateRows("la_origin_recv_data", setRecord, whereRecord)==1){
	    					Record record = Db.findFirst(Db.getSql("webservice_la_recv_cfm.getStatusByPayCode"), payCode);
	    					if(record.getInt("tmp_status") == WebConstant.SftInterfaceStatus.SFT_INTER_PROCESS_S.getKey()){
								Record payLegalRecord = Db.findFirst(Db.getSql("webservice_la_recv_cfm.getPayLegalByPayCode"), payCode);
								try {
									//TODO：
									CheckVoucherService.plsLaBackCheckVoucher("LA",
											payLegalRecord,
											CommonService.getPeriodByCurrentDay(new Date())
											);
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
	    				if(CommonService.updateRows("la_origin_recv_data", setRecord, whereRecord)==1){
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
