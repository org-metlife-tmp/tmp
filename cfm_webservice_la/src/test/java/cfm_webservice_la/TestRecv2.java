package cfm_webservice_la;


import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.web.webservice.la.queue.recv.LaRecvQueuePlugin;
import com.qhjf.cfm.web.webservice.la.recv.LaRecvCallbackBean;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.junit.Before;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
/**
 * 批付测试类
 * @author CHT
 *
 */
public class TestRecv2 {
	private static Map<String, String> headKv = new LinkedHashMap<>();
	static{
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = formatter.format(currentTime);
		headKv.put("SrvDate", dateString);
		
		SimpleDateFormat formatter1 = new SimpleDateFormat("HH:mm:ss");
		String dateString1 = formatter1.format(currentTime);
		headKv.put("SrvTime", dateString1);
		
		headKv.put("SenderID", "TMP");
		headKv.put("ReceiverID", "LA");
		headKv.put("SrvOpName", "DRNService");
		headKv.put("SrvOpVer", "20120606_1.1");
		headKv.put("MsgID", UUID.randomUUID().toString());
		headKv.put("CorrID", UUID.randomUUID().toString());
		headKv.put("ESBRspCode", "0");
		headKv.put("ESBRspDec", "Success");
		headKv.put("ResField1", null);
		headKv.put("ResField2", null);
		headKv.put("ResField3", null);
		headKv.put("ResField4", null);
		headKv.put("ResField5", null);
	}
	
	public List<LaRecvCallbackBean> getTestData(){
		List<LaRecvCallbackBean> callbackBeans = new ArrayList<>();
		Record origin = new Record();
		origin.set("id", 21345l);
		origin.set("branch_code", "1");
		origin.set("org_code", "SH");
		origin.set("fee_mode", "1");
		origin.set("recv_date", "2019-11-05");
		origin.set("amount", "72.20");
		origin.set("pay_acc_no", "0x00B84F0E73331A29DBEB513B4EFB192101000000757C6D2EEC44E9E01B07C70E524C2940C74EC2BF52952B41EA8AC087093D6D19B6825B1E13B94E769F4A3502A2D52E8A");
		origin.set("pay_bank_name", null);
		origin.set("insure_bill_no", "01285168");
		origin.set("pay_code", "20");
		origin.set("pay_acc_name", "张亮星");
		origin.set("insure_bill_no", "01285168");
		origin.set("job_no", "5");
		origin.set("trans_code", "B521");
		origin.set("sacscode", "LP");
		origin.set("sacstyp", "S");
		origin.set("tmp_status", 1);
		origin.set("tmp_err_message", "0::成功::交易成功");
		origin.set("bankcode", "LU");
		/*Record origin1 = new Record();
		origin1.set("id", 21346l);
		origin1.set("branch_code", "1");
		origin1.set("org_code", "SH");
		origin1.set("fee_mode", "3");
		origin1.set("recv_date", "3");
		origin1.set("amount", "3");
		origin1.set("pay_acc_no", "0x00B84F0E73331A29DBEB513B4EFB192101000000757C6D2EEC44E9E01B07C70E524C2940C74EC2BF52952B41EA8AC087093D6D19B6825B1E13B94E769F4A3502A2D52E8A");
		origin1.set("pay_bank_name", "3");
		origin1.set("insure_bill_no", "3");
		origin1.set("pay_code", "3");
		origin1.set("pay_acc_name", "3");
		origin1.set("insure_bill_no", "3");
		origin1.set("job_no", "3");
		origin1.set("trans_code", "3");
		origin1.set("sacscode", "3");
		origin1.set("sacstyp", "3");
		origin1.set("tmp_status", 2);
		origin1.set("tmp_err_message", "测试错误信息3");
		origin1.set("bankcode", "LU");*/
		try {
			LaRecvCallbackBean bean = new LaRecvCallbackBean(origin);
			callbackBeans.add(bean);
			/*LaRecvCallbackBean bean1 = new LaRecvCallbackBean(origin1);
			callbackBeans.add(bean1);*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return callbackBeans;
	}
		
	public void testCase() {
		try {
			ServiceClient sc = new ServiceClient();
			Options opts = new Options();   
			EndpointReference end = new EndpointReference("http://10.164.26.43/esbwebentry/ESBWebEntry.asmx?wsdl");   
			opts.setTo(end); 
			opts.setAction("http://eai.metlife.com/ESBWebEntry/ProcessMessage");
			sc.setOptions(opts);
			
			OMFactory fac = OMAbstractFactory.getOMFactory();

			OMNamespace processNs = fac.createOMNamespace("http://eai.metlife.com/","");
			OMElement processMessage = fac.createOMElement("ProcessMessage", processNs);    
			
			OMNamespace soapenvNs = fac.createOMNamespace("http://MetLifeEAI.EAISchema","");  
			OMElement envelopeNs = fac.createOMElement("ESBEnvelope",soapenvNs);
			
			OMElement headerNs = setHeaderNs(fac);
			OMElement msgBodyNs = setBodyNs(fac, getTestData());
			envelopeNs.addChild(headerNs);
			envelopeNs.addChild(msgBodyNs);
			
			processMessage.addChild(envelopeNs);
			System.out.println(processMessage);
			//发送请求LA服务
			OMElement res = sc.sendReceive(processMessage);
			System.out.println(res);
		} catch (AxisFault e) {
			e.printStackTrace();
		} 
		
	}
	
	/**
	 * 请求头部参数设置
	 * @param fac xml节点构造工厂
	 * @return
	 */
	private OMElement setHeaderNs(OMFactory fac) {
		OMElement headerNs = fac.createOMElement("ESBHeader", null);
		addChildList(fac, headerNs, headKv);
		return headerNs;
	}

	/**
	 *  消息体参数设设置
	 * @param fac xml节点构造工厂
	 * @param callbackBeans	LA批收回调报文业务字段bean
	 * @return
	 */
	private OMElement setBodyNs(OMFactory fac, List<LaRecvCallbackBean> callbackBeans) {
		//1.1.2 MsgBody
		OMElement msgBody = fac.createOMElement("MsgBody", null);
		
		//1.1.2.1 要调用的接口方法名称
		OMNamespace drn = fac.createOMNamespace("http://www.csc.smart/bo/schemas/DRNADDI", "drn");
		OMElement pmtupdiRec = fac.createOMElement("DRNADDI_REC", drn);

		// 1.1.2.1.1 msp:MSPContext	第一个方法参数
		OMElement mspContext = setMSPContext(fac, callbackBeans);
		pmtupdiRec.addChild(mspContext);

		// 1.1.2.1.2 ADDITIONAL_FIELDS	第二个方法参数
		OMElement additionalFields = setAdditionalFields(fac, callbackBeans);
		pmtupdiRec.addChild(additionalFields);

		msgBody.addChild(pmtupdiRec);
		return msgBody;
	}
	
	private OMElement setMSPContext(OMFactory fac, List<LaRecvCallbackBean> callbackBeans){
		OMNamespace msp = fac.createOMNamespace("http://www.csc.smart/msp/schemas/MSPContext", "msp");
		OMElement mspContextNs = fac.createOMElement("MSPContext", msp);
		
		OMElement userid = fac.createOMElement("UserId", msp);
		userid.setText("MSP");
		OMElement userpassword = fac.createOMElement("UserPassword", msp);
		userpassword.setText("");
		
		
		
		OMElement requestparameters = fac.createOMElement("RequestParameters", msp);

		OMElement branch = fac.createOMElement("RequestParameter", msp);
		branch.addAttribute("name", "BRANCH", null);
		branch.addAttribute("value", callbackBeans.get(0).getBranch(), null);
		OMElement company = fac.createOMElement("RequestParameter", msp);
		company.addAttribute("name", "COMPANY", null);
		company.addAttribute("value", callbackBeans.get(0).getCompany(), null);
		OMElement language = fac.createOMElement("RequestParameter", msp);
		language.addAttribute("name", "LANGUAGE", null);
		language.addAttribute("value", "S", null);
		OMElement commitflag = fac.createOMElement("RequestParameter", msp);
		commitflag.addAttribute("name", "COMMIT_FLAG", null);
		commitflag.addAttribute("value", "Y", null);
		OMElement ignoredriverheld = fac.createOMElement("RequestParameter", msp);
		ignoredriverheld.addAttribute("name", "IGNORE_DRIVER_HELD", null);
		ignoredriverheld.addAttribute("value", "Y", null);
		OMElement xsuppressrclrsc = fac.createOMElement("RequestParameter", msp);
		xsuppressrclrsc.addAttribute("name", "SUPPRESS_RCLRSC", null);
		xsuppressrclrsc.addAttribute("value", "N", null);
		requestparameters.addChild(branch);
		requestparameters.addChild(company);
		requestparameters.addChild(language);
		requestparameters.addChild(commitflag);
		requestparameters.addChild(ignoredriverheld);
		requestparameters.addChild(xsuppressrclrsc);
		
		mspContextNs.addChild(userid);
		mspContextNs.addChild(userpassword);
		mspContextNs.addChild(requestparameters);
		return mspContextNs;
	}
	/**
	 * 设置 ADDITIONAL_FIELDS xml节点
	 * @param fac
	 * @param callbackBeans
	 * @return
	 */
	private OMElement setAdditionalFields(OMFactory fac, List<LaRecvCallbackBean> callbackBeans){
		OMElement additionalFields = fac.createOMElement("ADDITIONAL_FIELDS", null);
		
		for(LaRecvCallbackBean callbackBean : callbackBeans){
			OMElement drninrecs = fac.createOMElement("DRNINRECS", null);
			Map<String, String> kvs = callbackBean.toMap();
			addChildList(fac, drninrecs, kvs);
			additionalFields.addChild(drninrecs);
		}
		
		return additionalFields;
	}
	
	private void addChildList(OMFactory fac, OMElement parent, Map<String, String> kvs){
		if (kvs.size() > 0) {
			Set<Entry<String,String>> entrySet = kvs.entrySet();
			for (Entry<String, String> kv : entrySet) {
				addChild(fac, parent, kv.getKey(), kv.getValue());
			}
		}
	}
	/**
	 * 
	 * @param fac
	 * @param parent	父标签
	 * @param key	xml标签名
	 * @param value	xml标签文本
	 */
	private void addChild(OMFactory fac, OMElement parent, String key, String value){
		OMElement omElement = fac.createOMElement(key, null);
		omElement.setText(value);
		parent.addChild(omElement);
	}
	
	DruidPlugin dp = null;
	ActiveRecordPlugin arp = null;
	@Before
	public void before() {
		dp = new DruidPlugin("jdbc:sqlserver://10.164.26.24:1433;DatabaseName=TreasureDB", "tmpadmin", "User123$");
		arp = new ActiveRecordPlugin(dp);
		arp.setDevMode(true);
		arp.setDialect(new SqlServerDialect());
		arp.setShowSql(true);
		arp.setBaseSqlTemplatePath(null);
		arp.getEngine().setSourceFactory(new ClassPathSourceFactory());
		arp.addSqlTemplate("/sql/sqlserver/webservice_la_recv_cfm.sql");
		arp.addSqlTemplate("/sql/sqlserver/webservice_la_cfm.sql");
		
		dp.start();
		arp.start();
	}
	@org.junit.Test
	public void test123() {
		TestRecv2 test =new TestRecv2();
		test.testCase();
		
		System.out.print("请输入值：");        
		Scanner s = new Scanner(System.in);        
		while(s.hasNext()){           
			String a = s.next();  //将s.next()赋值给变量a           
			if("quit".equals(a)){               
				break;           
			}else{               
				System.out.println(a);           
			}        	
		}
		
		if (null != arp)
			arp.stop();
		if (null != dp)
			dp.stop();
	}
}
