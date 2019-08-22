package cfm_webservice_la;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axis2.AxisFault;
import org.apache.axis2.addressing.EndpointReference;
import org.apache.axis2.client.Options;
import org.apache.axis2.client.ServiceClient;
import org.junit.Before;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.dialect.SqlServerDialect;
import com.jfinal.plugin.druid.DruidPlugin;
import com.jfinal.template.source.ClassPathSourceFactory;
import com.qhjf.cfm.utils.XmlTool;
import com.qhjf.cfm.web.webservice.la.LaCallbackBean;
import com.qhjf.cfm.web.webservice.la.recv.LaRecvCallbackBean;
import com.qhjf.cfm.web.webservice.la.util.LaPayOMElementSkipEAIUtil2;

/**
 * 批收跳过EAI回调核心系统
 * 
 * @author CHT
 *
 */
public class TestPaySkipEAI {
	/**
	 * 测试数据
	 * @return
	 */
	public List<LaCallbackBean> getTestData(){
		List<LaCallbackBean> callbackBeans = new ArrayList<>();
		Record origin = new Record();
		origin.set("id", 21345l);
		origin.set("branch_code", "5");
		origin.set("org_code", "BJ");
		origin.set("pay_code", "Y");
		origin.set("tmp_status", 1);
		origin.set("tmp_err_message", "0::成功::交易成功");
		try {
			LaCallbackBean bean = new LaCallbackBean(origin);
			callbackBeans.add(bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return callbackBeans;
	}
	public void testCase() {
		try {
			ServiceClient sc = new ServiceClient();
			Options opts = new Options();
			EndpointReference end = new EndpointReference(
					"http://10.164.4.158:9084/OTHApplication_MLDSIT/PMTService");
			opts.setTo(end);
			opts.setAction("PMTUPD");// 方法
			sc.setOptions(opts);

			OMElement msgBodyNs = null;
			try {
				msgBodyNs = new LaPayOMElementSkipEAIUtil2().createOMElement(getTestData());//setBodyNs(fac);
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("seng:" + msgBodyNs.toString());
			// 发送请求LA服务
			OMElement res = sc.sendReceive(msgBodyNs);
			System.out.println("response:" + res.toString());
			
			JSONObject json = XmlTool.documentToJSONObject(res.toString());
			System.out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private OMElement setBodyNs() {
		OMFactory fac = OMAbstractFactory.getOMFactory();

		OMNamespace prm = fac.createOMNamespace("http://www.csc.smart/bo/schemas/PMTUPDI", "pmt");
		OMElement method = fac.createOMElement("PMTUPDI_REC", prm);

		OMNamespace mspNs = fac.createOMNamespace("http://www.csc.smart/msp/schemas/MSPContext", "msp");
		OMElement mspContextNs = fac.createOMElement("MSPContext", mspNs);

		OMElement userId = fac.createOMElement("UserId", mspNs);
		userId.setText("MSP");
		OMElement userPassword = fac.createOMElement("UserPassword", mspNs);
		userPassword.setText("123");

		OMElement requestParametersNs = fac.createOMElement("RequestParameters", mspNs);

		OMElement branch = fac.createOMElement("RequestParameter", mspNs);
		branch.addAttribute("name", "BRANCH", null);
		branch.addAttribute("value", "BJ", null);
		OMElement company = fac.createOMElement("RequestParameter", mspNs);
		company.addAttribute("name", "COMPANY", null);
		company.addAttribute("value", "5", null);
		OMElement language = fac.createOMElement("RequestParameter", mspNs);
		language.addAttribute("name", "LANGUAGE", null);
		language.addAttribute("value", "S", null);
		OMElement commitflag = fac.createOMElement("RequestParameter", mspNs);
		commitflag.addAttribute("name", "COMMIT_FLAG", null);
		commitflag.addAttribute("value", "Y", null);
		OMElement ignoredriverheld = fac.createOMElement("RequestParameter", mspNs);
		ignoredriverheld.addAttribute("name", "IGNORE_DRIVER_HELD", null);
		ignoredriverheld.addAttribute("value", "Y", null);
		OMElement xsuppressrclrsc = fac.createOMElement("RequestParameter", mspNs);
		xsuppressrclrsc.addAttribute("name", "SUPPRESS_RCLRSC", null);
		xsuppressrclrsc.addAttribute("value", "N", null);

		requestParametersNs.addChild(branch);
		requestParametersNs.addChild(company);
		requestParametersNs.addChild(language);
		requestParametersNs.addChild(commitflag);
		requestParametersNs.addChild(ignoredriverheld);
		requestParametersNs.addChild(xsuppressrclrsc);

		mspContextNs.addChild(userId);
		mspContextNs.addChild(userPassword);
		mspContextNs.addChild(requestParametersNs);

		method.addChild(mspContextNs);

		// 设置参数
		OMElement polenq2i = fac.createOMElement("ADDITIONAL_FIELDS", null);

		OMElement pmtin = fac.createOMElement("PMTIN", null);

		OMElement company1 = fac.createOMElement("COMPANY", null);
		company1.setText("5");
		OMElement branch1 = fac.createOMElement("BRANCH", null);
		branch1.setText("BJ");
		OMElement reqnno = fac.createOMElement("REQNNO", null);
		reqnno.setText("000946618");
		OMElement stflag = fac.createOMElement("STFLAG", null);
		stflag.setText("Y");
		OMElement txtline = fac.createOMElement("TXTLINE", null);
		txtline.setText("");

		pmtin.addChild(company1);
		pmtin.addChild(branch1);
		pmtin.addChild(reqnno);
		pmtin.addChild(stflag);
		pmtin.addChild(txtline);

		polenq2i.addChild(pmtin);
		method.addChild(polenq2i);

		return method;
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

	/**
	 * 按q 退出单元测试
	 */
	@org.junit.Test
	public void test123() {
		TestPaySkipEAI test = new TestPaySkipEAI();
		test.testCase();

		System.out.print("请输入值：");
		Scanner s = new Scanner(System.in);
		while (s.hasNext()) {
			String a = s.next(); // 将s.next()赋值给变量a
			if ("q".equals(a)) {
				if (s != null) {
					s.close();
				}
				break;
			} else {
				System.out.println(a);
			}
		}

		if (null != arp)
			arp.stop();
		if (null != dp)
			dp.stop();
	}
}
