package com.qhjf.cfm.web.webservice.ebs.counter.recv.tool;

import java.rmi.RemoteException;
import java.util.Map;

import org.dom4j.DocumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.utils.VelocityUtil;
import com.qhjf.cfm.utils.XmlTool;
import com.qhjf.cfm.web.config.DDHEBSConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.webservice.ebs.counter.recv.bean.PublicBean;
import cn.metlife.ebs_sit.services.FundingPlatformPayBack.FundingPlatformPayBackProxy;

public class EbsSendTool {
	private static Logger log = LoggerFactory.getLogger(EbsSendTool.class);
	
	private EbsSendTool(){}
	public static EbsSendTool getInstance(){
		return EbsSendToolInner.INSTANCE;
	}
	private static class EbsSendToolInner{
		private static final EbsSendTool INSTANCE = new EbsSendTool();
	}
	
	
	public JSONObject send(PublicBean bean) throws ReqDataException{
		try {
			Map<String, Object> data = bean.toMap();
			String req = VelocityUtil.genVelo(bean.getTemplate(), data);
			log.info("EBS柜面收url=[{}]发送参数={}", bean.getUrl(), StringKit.removeControlCharacter(req));
			
			String ebs_bean = bean.service(req);
			
			log.info("EBS柜面收回写响应参数={}", StringKit.removeControlCharacter(ebs_bean));
			JSONObject json = XmlTool.documentToJSONObject(ebs_bean);
			return json;
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (DocumentException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
