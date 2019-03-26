package com.qhjf.cfm.web.webservice.ebs.queue;

import cn.metlife.ebs_sit.services.FundingPlatformPayBack.FundingPlatformPayBackProxy;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.IAtom;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.BusinessException;
import com.qhjf.cfm.utils.CommonService;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.utils.XmlTool;
import com.qhjf.cfm.web.config.DDHEBSConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.constant.WebConstant.SftCallbackStatus;
import com.qhjf.cfm.web.constant.WebConstant.SftInterfaceStatus;
import com.qhjf.cfm.web.service.CheckVoucherService;
import com.qhjf.cfm.web.webservice.ebs.EbsCallbackBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

/**
 * 队列消费者
 * @author yunxw
 *
 */
public class EbsConsumerQueue implements Runnable{
	
	private static Logger log = LoggerFactory.getLogger(EbsConsumerQueue.class);
	private static DDHEBSConfigSection config = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHEBS);

	@Override
	public void run() {
		while(true){
			try{
				FundingPlatformPayBackProxy service = new FundingPlatformPayBackProxy(config.getUrl());
				EbsQueueBean queueBean = null;
				queueBean = EbsQueue.getInstance().getQueue().take();
				log.debug("EBS回写发送参数={}", StringKit.removeControlCharacter(queueBean.getParams()));
				String ebs_bean = service.saveXML(queueBean.getParams());
				log.info("EBS回写响应参数={}", StringKit.removeControlCharacter(ebs_bean));
				JSONObject json = XmlTool.documentToJSONObject(ebs_bean);
				process(json);
			}catch(Exception e){
				e.printStackTrace();
				continue;
			}
		}
		
	}
	
	private void processFail(EbsQueueBean queueBean){
		log.debug("Ebs接收回调数据失败");
		EbsCallbackBean bean = queueBean.getBeans();
		try{
			Record setRecord = new Record().set("la_callback_status", SftCallbackStatus.SFT_CALLBACK_F.getKey())
    				.set("la_callback_err_message", "EBS接收失败")
    				.set("la_callback_resp_time", new Date());
    		Record whereRecord = new Record().set("pay_code", bean.getPayNo());
    		if(CommonService.updateRows("la_origin_pay_data", setRecord, whereRecord) != 1){
    			log.error("EBS回调接口回写数据库失败:{}",StringKit.removeControlCharacter(bean.getPayNo()));
    		};
		}catch(Exception e){
			log.error("EBS回调接口回写数据库失败:{}",StringKit.removeControlCharacter(bean.getPayNo()));
			e.printStackTrace();
		}
	}
	
	private void process(final JSONObject json){
			boolean flag = Db.tx(new IAtom() {
	            @Override
	            public boolean run() throws SQLException {
	            	String processStatus = json.getString("ResultCode");
	    			String payCode = json.getString("PayNo");
	    			
	    			
	    			Record whereRecord = new Record().set("pay_code", payCode);
	    			if(processStatus.equals("SUCCESS")){
	    				Record setRecord = new Record().set("ebs_callback_status", SftCallbackStatus.SFT_CALLBACK_S.getKey())
	    						.set("ebs_callback_resp_time", new Date());
	    				if(CommonService.updateRows("ebs_origin_pay_data", setRecord, whereRecord)==1){
	    					log.debug("EBS回调接口回写数据库成功:"+payCode);
	    					Record record = Db.findFirst(Db.getSql("webservice_ebs_cfm.getStatusByPayCode"), payCode);
	    					if(record.getInt("tmp_status") == SftInterfaceStatus.SFT_INTER_PROCESS_S.getKey()){
	    						//记账
								//根据pay_mode判断是批量付还是柜面付
								String payMode = TypeUtils.castToString(record.get("pay_mode"));
								Record payLegalRecord = null;
								if(payMode.equals(WebConstant.SftDoubtPayMode.PLSF.getKeyc())){
									payLegalRecord = Db.findFirst(Db.getSql("webservice_ebs_cfm.getPayLegalByPayCode"), payCode);
								}else if(payMode.equals(WebConstant.SftDoubtPayMode.WY.getKeyc())){
									payLegalRecord = Db.findFirst(Db.getSql("webservice_ebs_cfm.getPayGmLegalByPayCode"), payCode);
								}else{
									log.info("EBS中批量付paycode为【"+payCode+"】的数据支付方式错误");
								}
								try {
									//生成凭证信息
									log.info("EBS中批量付paycode为【"+payCode+"】的数据回调成功，生成凭证---begin");
									CheckVoucherService.plfLaEbsBackCheckVoucher("EBS",
											payLegalRecord,
											CommonService.getPeriodByCurrentDay(new Date()),
											payMode
									);
									log.info("EBS中批量付paycode为【"+payCode+"】的数据回调成功，生成凭证---end");
								} catch (BusinessException e) {
									e.printStackTrace();
									return false;
								}
	    					}
	    					return true;
	    				}
	    			}else{
	    				String errMessage = json.getString("ResultMsg");
	    				Record setRecord = new Record().set("ebs_callback_status", SftCallbackStatus.SFT_CALLBACK_F.getKey())
	    						.set("ebs_callback_err_message", errMessage)
	    						.set("ebs_callback_resp_time", new Date());
	    				if(CommonService.updateRows("ebs_origin_pay_data", setRecord, whereRecord)==1){
	    					log.debug("EBS回调接口回写数据库成功:"+payCode);
	    					return true;
	    				}
	    			}
	    			log.debug("EBS回调接口回写数据库失败:{}",StringKit.removeControlCharacter(payCode));
	    			return false;
	            }
	            });	
			
			if (!flag) {
				log.error("EBS响应回写原始数据失败；response={}", StringKit.removeControlCharacter(json.toJSONString()));
			}
	}
}
