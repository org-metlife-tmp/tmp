package cfm_webservice_la;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.apache.axiom.om.OMElement;
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
import com.qhjf.cfm.web.webservice.la.recv.LaRecvCallbackBean;
import com.qhjf.cfm.web.webservice.la.util.LaRecvOMElementUtil;

/**
 * 批收 回调核心系统测试
 * 
 * @author CHT
 *
 */
public class LaRecvCallbackTest {
	/**
	 * 测试数据
	 * @return
	 */
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
			
			

			OMElement msgBodyNs = null;
			try {
				msgBodyNs = new LaRecvOMElementUtil().createOMElement(getTestData());
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("send:"+msgBodyNs);
			//发送请求LA服务
			OMElement res = sc.sendReceive(msgBodyNs);
			System.out.println("response:"+res);
			
			JSONObject json = XmlTool.documentToJSONObject(res.toString());
			System.out.println(json);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
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
		LaRecvCallbackTest test =new LaRecvCallbackTest();
		test.testCase();
		test.testCase();
		System.out.print("请输入值：");        
		Scanner s = new Scanner(System.in);        
		while(s.hasNext()){           
			String a = s.next();  //将s.next()赋值给变量a           
			if("q".equals(a)){   
				if (s != null) {
					s.close();
				}
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
