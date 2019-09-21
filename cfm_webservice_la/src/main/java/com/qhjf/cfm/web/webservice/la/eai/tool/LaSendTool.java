package com.qhjf.cfm.web.webservice.la.eai.tool;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alibaba.fastjson.util.TypeUtils;
import com.jfinal.plugin.activerecord.Record;
import com.qhjf.cfm.exceptions.ReqDataException;
import com.qhjf.cfm.utils.StringKit;
import com.qhjf.cfm.web.config.DDHLAConfigSection;
import com.qhjf.cfm.web.config.GlobalConfigSection;
import com.qhjf.cfm.web.config.IConfigSectionType;
import com.qhjf.cfm.web.webservice.la.eai.bean.ESBHeaderBean;
import com.qhjf.cfm.web.webservice.la.eai.bean.RequestParametersBean;
import com.qhjf.cfm.web.webservice.la.eai.bean.ServiceEnum;
import com.qhjf.cfm.web.webservice.la.logger.recv.BatchRecvLogger;

/**
 * 通过EAI调用核心系统
 * 
 * @author CHT
 *
 */
public class LaSendTool {
	private static Logger log = LoggerFactory.getLogger(BatchRecvLogger.class);
	private static DDHLAConfigSection config = GlobalConfigSection.getInstance()
			.getExtraConfig(IConfigSectionType.DDHConfigSectionType.DDHLA);

	private OMFactory fac;
	private List<Record> dataList;
	private String srvOpName;
	private String branch;
	private String company;
	private long currTime;
	/**
	 * 
	 * @param r
	 * @param srvOpName webservice服务器接口名
	 * @param branch 机构
	 * @param company 分公司编码
	 */
	public LaSendTool(List<Record> r, String srvOpName, String branch, String company) {
		currTime = System.currentTimeMillis();
		
		this.fac = OMAbstractFactory.getOMFactory();
		if (r != null && r.size() < config.getBatchNum()) {
			this.dataList = r;
		}
		this.srvOpName = srvOpName;
		this.branch = branch;
		this.company = company;
	}

	public OMElement sendEAI() throws ReqDataException {
		try {
			ServiceClient sc = new ServiceClient();
			Options opts = new Options();
			EndpointReference end = new EndpointReference(config.getUrl());
			opts.setTo(end);
			opts.setAction("http://eai.metlife.com/ESBWebEntry/ProcessMessage");
			sc.setOptions(opts);

			OMElement processMessage = createProcessMessage();
			log.info("{}:la柜面收 {}，URL={}发送报文：{}", currTime, srvOpName, config.getUrl(),
					StringKit.removeControlCharacter(processMessage != null ? processMessage.toString() : "请求报文为空"));
			OMElement res = sc.sendReceive(processMessage);
			log.info("{}:la柜面收 {}响应报文：{}", currTime, srvOpName,
					StringKit.removeControlCharacter(res != null ? res.toString() : "返回数据为空"));
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof ReqDataException) {
				throw (ReqDataException)e;
			}
		}
		return null;
	}

	private OMElement createProcessMessage() throws ReqDataException {
		OMNamespace processNs = this.fac.createOMNamespace("http://eai.metlife.com/", "");
		OMElement processMessage = this.fac.createOMElement("ProcessMessage", processNs);

		processMessage.addChild(createESBEnvelope());
		return processMessage;
	}

	private OMElement createESBEnvelope() throws ReqDataException {
		OMNamespace soapenvNs = this.fac.createOMNamespace("http://MetLifeEAI.EAISchema", "");
		OMElement esbEnvelope = this.fac.createOMElement("ESBEnvelope", soapenvNs);

		esbEnvelope.addChild(createESBHeader());
		esbEnvelope.addChild(createMsgBody());
		return esbEnvelope;
	}

	/**
	 * 构建ESBHeader xml节点
	 * 
	 * @param fac
	 * @param r
	 * @return
	 * @throws ReqDataException 
	 */
	private OMElement createESBHeader() throws ReqDataException {
		OMElement esbHeader = this.fac.createOMElement("ESBHeader", null);

		ServiceEnum enum1 = ServiceEnum.getEnum(this.srvOpName);
		Map<String, Object> map = new ESBHeaderBean(enum1.getServiceName()).toMap();
		addChildList(this.fac, esbHeader, map);
		return esbHeader;
	}

	/**
	 * 构建MsgBody xml节点
	 * 
	 * @param fac
	 * @param r
	 * @return
	 * @throws ReqDataException 
	 */
	private OMElement createMsgBody() throws ReqDataException {
		OMElement msgBody = this.fac.createOMElement("MsgBody", null);

		OMElement msgBodyChild = createMsgBodyChild();
		msgBody.addChild(msgBodyChild);
		return msgBody;
	}

	private OMElement createMsgBodyChild() throws ReqDataException {
		ServiceEnum serviceEnum = ServiceEnum.getEnum(srvOpName);
		
		OMNamespace ns = this.fac.createOMNamespace(serviceEnum.getNodeNameSpaceUrl(), serviceEnum.getNodeNameSpace());
		OMElement pmtupdiRec = this.fac.createOMElement(serviceEnum.getNodeName(), ns);

		OMElement mspContext = createMSPContext();
		pmtupdiRec.addChild(mspContext);

		OMElement additionalFields = createADDITIONAL_FIELDS(this.fac, this.dataList, serviceEnum);
		pmtupdiRec.addChild(additionalFields);

		return pmtupdiRec;
	}

	private OMElement createMSPContext() {
		OMNamespace mspNameSpace = this.fac.createOMNamespace("http://www.csc.smart/msp/schemas/MSPContext", "msp");
		OMElement mspContext = this.fac.createOMElement("MSPContext", mspNameSpace);

		OMElement userId = this.fac.createOMElement("UserId", mspNameSpace);
		userId.setText(config.getUserId());
		mspContext.addChild(userId);

		OMElement userPassword = this.fac.createOMElement("UserPassword", mspNameSpace);
		userPassword.setText(config.getUserPassword());
		mspContext.addChild(userPassword);

		OMElement requestParameters = createRequestParameters(mspNameSpace);
		mspContext.addChild(requestParameters);

		return mspContext;
	}

	private OMElement createRequestParameters(OMNamespace mspNameSpace) {
		OMElement requestParameters = this.fac.createOMElement("RequestParameters", mspNameSpace);

		RequestParametersBean bean = new RequestParametersBean(this.branch, this.company);
		List<Map<String, String>> attrsList = bean.getAttributes();
		for (Map<String, String> attrs : attrsList) {
			addChildAttr(this.fac, requestParameters, mspNameSpace, bean.getNodeName(), null, attrs);
		}
		return requestParameters;
	}

	// 跳过EAI 或 直接调用核心可以复用
	public static OMElement createADDITIONAL_FIELDS(OMFactory omFactory, List<Record> list, ServiceEnum serviceEnum) {
		OMElement additionalFields = omFactory.createOMElement("ADDITIONAL_FIELDS", null);

		if (serviceEnum.isAdditionalFieldsSonIsArray()) {
			for (Record r : list) {
				OMElement pmtin = createAdditionalFieldsSon(omFactory, r, serviceEnum.getAdditionalFieldsSonTagName());
				additionalFields.addChild(pmtin);
			}
		}else {
			addChildList(omFactory, additionalFields, list.get(0).getColumns());
		}
		return additionalFields;
	}

	// 跳过EAI 或 直接调用核心可以复用
	public static OMElement createAdditionalFieldsSon(OMFactory omFactory, Record r, String tagName) {
		OMElement additionalFieldsSon = omFactory.createOMElement(tagName, null);
		Map<String, Object> columns = r.getColumns();
		addChildList(omFactory, additionalFieldsSon, columns);
		return additionalFieldsSon;
	}

	public static void addChildList(OMFactory omFactory, OMElement parent, Map<String, Object> kvs) {
		if (null != kvs && kvs.size() > 0) {
			Set<Entry<String, Object>> entrySet = kvs.entrySet();
			for (Entry<String, Object> kv : entrySet) {
				addChild(omFactory, parent, kv.getKey(), TypeUtils.castToString(kv.getValue()));
			}
		}
	}

	public static void addChild(OMFactory omFactory, OMElement parent, String key, String value) {
		addChildAttr(omFactory, parent, null, key, value, null);
	}

	public static void addChildAttr(OMFactory omFactory, OMElement parent, OMNamespace ns, String key, String value,
			Map<String, String> attrs) {
		OMElement omElement = omFactory.createOMElement(key, ns);
		if (null != value) {
			omElement.setText(value);
		}

		addAttrs(omElement, attrs);
		parent.addChild(omElement);
	}

	public static void addAttrs(OMElement ome, Map<String, String> attrs) {
		if (null != attrs && attrs.size() > 0) {
			Set<Entry<String, String>> entrySet = attrs.entrySet();
			for (Entry<String, String> kv : entrySet) {
				ome.addAttribute(kv.getKey(), kv.getValue(), null);
			}
		}
	}
}