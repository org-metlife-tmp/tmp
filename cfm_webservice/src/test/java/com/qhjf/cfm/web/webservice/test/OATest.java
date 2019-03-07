package com.qhjf.cfm.web.webservice.test;

import com.qhjf.cfm.web.webservice.oa.client.OAServer;
import com.qhjf.cfm.web.webservice.oa.client.OAServerService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;




public class OATest {
	
	private static Logger log = LoggerFactory.getLogger(OATest.class);
	
	@Test
    public void Test() throws MalformedURLException{

		//创建一个用于产生WebServiceImpl实例的工厂，WebServiceImplService类是wsimport工具生成的
		//OAServerService factory = new OAServerService("http://10.164.25.104:7080/cfm/services/OAServer");
		OAServerService factory = new OAServerService("http://10.164.26.48:7082/cfm/services/OAServer");
		//通过工厂生成一个WebServiceImpl实例，WebServiceImpl是wsimport工具生成的
		OAServer oaServer = factory.getOAServerPort();
		/*String xml = "<oa>"
				+"<flow_id>201811050002</flow_id>"
				+"<bill_no>QT201811050002</bill_no>"
				+"<biz_type>4</biz_type>"
				+"<apply_user>test_2</apply_user>"
				+"<budget_user>0005</budget_user>"
				+"<apply_date>2018-11-06</apply_date>"
				+"<recv_accno>6225880230001175</recv_accno>"
				+"<recv_accname>刘五</recv_accname>"
				+"<recv_bank>招商银行股份有限公司北京分行营</recv_bank>"
				+"<recv_province>北京</recv_province>"
				+"<recv_city>朝阳</recv_city>"
				+"<amount>6.66</amount>"

				+"<memo>我的</memo>"
				+"<apply_org>17</apply_org>"
				+"<apply_dept>dept01</apply_dept>"
				+"<pay_org_type>1</pay_org_type>"
				+"<send_count>1</send_count>"
				+"</oa>";*/
		String xml = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<oa>"
				+ "<flow_id>12223434245762</flow_id>"
				+ "<bill_no>QT2018112100263</bill_no>"
				+ "<biz_type>1</biz_type>"
				+ "<apply_user>8001320</apply_user>"
				+ "<budget_user>8001320</budget_user>"
				+ "<apply_date>20181122</apply_date>"
				+ "<recv_accno>6225880230001175</recv_accno>"
				+ "<recv_accname>刘五 </recv_accname>"
				+ "<recv_bank>招商银行重庆分行营业部</recv_bank>"
				+ "<recv_province></recv_province>"
				+ "<recv_city></recv_city>"
				+ "<recv_address></recv_address>"
				+ "<recv_banktype></recv_banktype>"
				+ "<recv_cnaps></recv_cnaps>"
				+ "<amount>7.2</amount>"
				+ "<send_count>0</send_count>"
				+ "<memo>第9笔招商上海分公司付</memo>"
				+ "<apply_org>2</apply_org>"
				+ "<apply_dept></apply_dept>"
				+ "<pay_org_type>2</pay_org_type>"
				+ "</oa>";
		System.out.println(oaServer.reciveDate(xml));

    }
	
	/*@Test
    public void queryStatusReqTest() throws Exception{
		//创建一个用于产生WebServiceImpl实例的工厂，WebServiceImplService类是wsimport工具生成的
				OAServerService factory = new OAServerService();
				//通过工厂生成一个WebServiceImpl实例，WebServiceImpl是wsimport工具生成的
				OAServer oaServer = factory.getOAServerPort();
		String xml = "<oa>"
				+"<flow_id>0000000001</flow_id>"
				+"<send_count>1</send_count>"
				+"</oa>";

		System.out.println(oaServer.queryStatus(xml));
	}*/

}
