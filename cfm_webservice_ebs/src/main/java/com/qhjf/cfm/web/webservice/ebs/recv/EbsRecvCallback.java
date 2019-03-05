package com.qhjf.cfm.web.webservice.ebs.recv;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.utils.VelocityUtil;
import com.qhjf.cfm.web.config.DDHLAConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.constant.WebConstant;
import com.qhjf.cfm.web.webservice.ebs.EbsCallbackBean;
import com.qhjf.cfm.web.webservice.ebs.queue.EbsProductQueue;
import com.qhjf.cfm.web.webservice.ebs.queue.EbsQueueBean;

public class EbsRecvCallback {
	private static Logger log = LoggerFactory.getLogger(EbsRecvCallback.class);
	private static DDHLAConfigSection config = GlobalConfigSection.getInstance().getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLA);
	
	public void callBack(List<Record> records){
		if(records == null || records.size() ==0){
			log.debug("回写EBS数据为空,不进行回写");
			return;
		}
		log.debug("回写EBS,将回写数据写至队列开始。。。。。。。。。");
		int batchNum = config.getBatchNum();
		List<EbsCallbackBean> callbackBeans = new ArrayList<EbsCallbackBean>();
		for(Record origin : records){
			try {
				EbsCallbackBean bean = createBean(origin);
				callbackBeans.add(bean);
				if(callbackBeans.size() == batchNum){
					String params = createParams(callbackBeans);
					EbsQueueBean queueBean = new EbsQueueBean(callbackBeans,params);
					EbsProductQueue productQueue = new EbsProductQueue(queueBean);
					new Thread(productQueue).start();
					callbackBeans = new ArrayList<EbsCallbackBean>();
				}
			} catch (Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		if(callbackBeans.size()>0){
			String params = createParams(callbackBeans);
			EbsQueueBean queueBean = new EbsQueueBean(callbackBeans,params);
			EbsProductQueue productQueue = new EbsProductQueue(queueBean);
			new Thread(productQueue).start();
		}
		log.debug("回写EBS,将回写数据写至队列结束。。。。。。。。。");
	}
	
	private EbsCallbackBean createBean(Record origin) throws Exception{
		EbsCallbackBean bean = new EbsCallbackBean(origin);
		int updNum = Db.update(Db.getSql("webservice_ebs_cfm.updateCallbackingStatus"), WebConstant.SftCallbackStatus.SFT_CALLBACKING.getKey(),
				new Date(),origin.getLong("id"),WebConstant.SftCallbackStatus.SFT_NO_CALLBACK.getKey(),WebConstant.SftCallbackStatus.SFT_CALLBACK_F.getKey(),origin.getStr("persist_version"));
		if(updNum != 1){
			throw new Exception(origin.getLong("id")+"修改原始单据状态失败,不回写EBS");
		}
		return bean;
	}
	
	private String createParams(List<EbsCallbackBean> beans){
		if(beans == null || beans.size() == 0 || beans.size() > config.getBatchNum()){
			return null;
		}
		Map<String,Object> map = beans.get(0).toMap();
		String params = VelocityUtil.genVelo("EBSCallback.vm", map);
		return params;
	}
}
